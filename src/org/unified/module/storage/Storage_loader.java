package org.unified.module.storage;


import org.unified.Parser;
import org.unified.Unified_error;
import org.unified.declaration.Declaration_attribute;
import org.unified.declaration.Declaration_element;
import org.unified.declaration.Declaration_error;
import org.unified.dev.generator.Generator;
import org.unified.module.Module_loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage_loader {

	public static Storage load_from(Declaration_element storage_element, String module_name) throws Exception {
		List<Record_type> record_types = new ArrayList<>();
		Map<String, Storage_table> table_by_name = new HashMap<>();

		Storage_table[] tables = Module_loader.load_default_items(storage_element, null, Storage_table[]::new, table_element -> {
			Storage_table table = load_table_from(table_element, record_types);
			table_by_name.put(table.name, table);
			return table;
		});

		Storage_query[] queries = Module_loader.load_default_items(storage_element, "+queries", Storage_query[]::new,
			query_element -> load_query_from(query_element, module_name, table_by_name, record_types));

		Storage_folder[] folders = Module_loader.load_default_items(storage_element, "+folders", Storage_folder[]::new,
			folder_element -> new Storage_folder(folder_element.name(), folder_element.value()));


		return new Storage(module_name, storage_element.get_int_attribute("version", 1), folders, record_types.toArray(new Record_type[record_types.size()]), tables, queries);
	}





	private static Storage_table load_table_from(Declaration_element table_element, List<Record_type> record_types) throws Exception {
		List<Table_field> primary_key = new ArrayList<>();
		Table_field[] fields = Module_loader.load_default_items(table_element, null, Table_field[]::new, (field_element) -> load_field_from(field_element, primary_key));
		List<Table_index> indexes = new ArrayList<>();
		if (!primary_key.isEmpty()) {
			indexes.add(new Table_index("", primary_key.toArray(new Table_field[primary_key.size()]), true));
		}
		String name = table_element.name();
		Record_type record_type = new Record_type(name + "Record", fields, true);
		record_types.add(record_type);
		return new Storage_table(name, fields, indexes.toArray(new Table_index[indexes.size()]), record_type);
	}





	private static Table_field load_field_from(Declaration_element field_element, List<Table_field> primary_key) throws Declaration_error {
		Table_field field = Table_field.from(field_element);
		if (field.included_in_primary_key) {
			primary_key.add(field);
		}
		return field;
	}





	private static class From_section {
		final Storage_table table;
		final Table_field[] fields;
		private final Record_type record_type;





		private From_section(Storage_table table, Table_field[] fields, Record_type record_type) {
			this.table = table;
			this.fields = fields;
			this.record_type = record_type;
		}
	}





	private static Storage_query load_query_from(Declaration_element query_element, String module_name, Map<String, Storage_table> table_by_name, List<Record_type> record_types) throws Exception {
		String name = query_element.name();
		Query_param[] params = Module_loader.load_default_items(query_element, null, Query_param[]::new, Storage_loader::load_query_param);

		From_section from = null;
		Storage_query.Returns returns = Storage_query.Returns.Iterator;
		String alias = null;
		String where = null;
		String order_by = null;


		for (Declaration_attribute attribute : query_element.attributes) {
			String lowercase_name = attribute.name.toLowerCase();
			switch (lowercase_name) {
				case "record":
					returns = Storage_query.Returns.Record;
					break;
				case "array":
					returns = Storage_query.Returns.Array;
					break;
				case "iterator":
					returns = Storage_query.Returns.Iterator;
					break;
				case "as":
					alias = attribute.getString();
					break;
				case "where":
					where = attribute.getString();
					break;
				case "order-by":
				case "orderby":
					order_by = attribute.getString();
					break;
				case "from":
					from = parser_from_section(module_name, name, attribute.getString(), table_by_name, "Query \"" + name + "'\"", record_types);
			}
		}

		if (from == null) {
			throw new Unified_error("Query \"" + name + "\" does not specify required attribute \"from\"");
		}

		return new Storage_query(returns, name, alias, params, from.table, from.fields, where, order_by, from.record_type);
	}





	private static From_section parser_from_section(String module_name, String query_name, String from, Map<String, Storage_table> table_by_name, String referrer,
		List<Record_type> record_types) throws Unified_error {

		Parser parser = new Parser(from, referrer + " table", Parser.Whitespaces.Pass);
		String table_name = parser.expect_name();

		Storage_table table = table_by_name.get(table_name);
		if (table == null) {
			throw new Unified_error(referrer + " reference missing table \"" + table_name + "\"");
		}

		Table_field[] fields;
		Record_type record_type;
		if (parser.pass("(")) {
			List<Table_field> fields_builder = new ArrayList<>();
			while (!parser.eof()) {
				String name = parser.expect_name();
				Table_field field = table.find_field(name);
				if (field == null) {
					throw new Unified_error(referrer + " reference missing field \"" + name + "\" in table \"" + table.name + "\"");
				}
				fields_builder.add(field);
				if (!parser.pass(",")) {
					break;
				}
			}
			parser.expect(")");
			fields = fields_builder.toArray(new Table_field[fields_builder.size()]);
			record_type = new Record_type(module_name + "Storage" + Generator.uppercase_first_letter(query_name) + "Record", fields, false);
			record_types.add(record_type);
		} else {
			fields = table.fields;
			record_type = table.record_type;
		}

		return new From_section(table, fields, record_type);
	}





	private static Query_param load_query_param(Declaration_element element) throws Declaration_error {
		Table_field field = Table_field.from(element);
		return new Query_param(field.name, field.type);
	}


}
