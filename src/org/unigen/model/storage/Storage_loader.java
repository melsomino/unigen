package org.unigen.model.storage;


import org.json.simple.JSONObject;
import org.unigen.Parser;
import org.unigen.Unigen_exception;
import org.unigen.generator.Generator;
import org.unigen.model.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage_loader extends Loader {

	public static Storage load(JSONObject def) throws Exception {
		if (def == null) {
			return null;
		}
		String module = get_string(".name", def);
		List<Record_type> record_types = new ArrayList<>();
		Storage_table[] tables = create_default_items(def, Storage_table[]::new, (name, source) -> load_table(name, (JSONObject) source, record_types));
		Map<String, Storage_table> table_by_name = new HashMap<>();
		for (Storage_table table : tables) {
			table_by_name.put(table.name, table);
		}
		Storage_query[] queries = create_default_items(get_object(".queries", def), Storage_query[]::new, (name, source) -> load_query(module, name, source, table_by_name, record_types));
		return new Storage(module, get_integer(".version", 1, def), record_types.toArray(new Record_type[record_types.size()]), tables, queries);
	}


	private static Storage_table load_table(String name, JSONObject def, List<Record_type> record_types) throws Exception {
		List<Table_field> primary_key = new ArrayList<>();
		Table_field[] fields = create_default_items(def, Table_field[]::new, (field_name, field_def) -> load_field(field_name, field_def, primary_key));
		List<Table_index> indexes = new ArrayList<>();
		if (!primary_key.isEmpty()) {
			indexes.add(new Table_index("", primary_key.toArray(new Table_field[primary_key.size()]), true));
		}
		Record_type record_type = new Record_type(name + "Record", fields, true);
		record_types.add(record_type);
		return new Storage_table(name, fields, indexes.toArray(new Table_index[indexes.size()]), record_type);
	}


	private static Table_field load_field(String name, Object def, List<Table_field> primary_key) {
		Field_parameters parameters = Field_parameters.parse((String) def);
		Table_field field = new Table_field(name, parameters.type, parameters.included_in_primary_key);
		if (field.is_key) {
			primary_key.add(field);
		}
		return field;
	}


	private static class Select_section {
		final Storage_table table;
		final Table_field[] fields;
		private final Record_type record_type;

		private Select_section(Storage_table table, Table_field[] fields, Record_type record_type) {
			this.table = table;
			this.fields = fields;
			this.record_type = record_type;
		}
	}


	private static Storage_query load_query(String module_name, String name, Object def, Map<String, Storage_table> table_by_name, List<Record_type> record_types) throws Exception {
		Select_section select;
		if (def instanceof String) {
			select = parser_select_section(module_name, name, (String) def, table_by_name, "Query \"" + name + "'\"", record_types);
			return new Storage_query(Storage_query.Returns.Iterator, name, null, new Query_param[0], select.table, select.fields, null, select.record_type);
		}

		Query_param[] params;
		JSONObject def_object = (JSONObject) def;
		params = create_default_items(def_object, Query_param[]::new, Storage_loader::load_query_param);
		String select_def = get_string(".select", def_object);
		if (select_def == null || select_def.isEmpty()) {
			throw new Unigen_exception("Query \"" + name + "\" does not specify required attribute \".select\"");
		}

		select = parser_select_section(module_name, name, select_def, table_by_name, "Query \"" + name + "'\"", record_types);
		return new Storage_query(Storage_query.Returns.parse(get_string(".returns", def_object)), name, get_string(".generate_record_type", def_object), params, select.table, select.fields, get_string(".where", def_object), select.record_type);
	}


	private static Select_section parser_select_section(String module_name, String query_name, String select, Map<String, Storage_table> table_by_name, String referrer, List<Record_type> record_types) throws Unigen_exception {
		Parser parser = new Parser(select, referrer + " table", Parser.Whitespaces.Pass);
		String table_name = parser.expect_name();

		Storage_table table = table_by_name.get(table_name);
		if (table == null) {
			throw new Unigen_exception(referrer + " reference missing table \"" + table_name + "\"");
		}

		Table_field[] fields;
		Record_type record_type;
		if (parser.pass("(")) {
			List<Table_field> fields_builder = new ArrayList<>();
			while (!parser.eof()) {
				String name = parser.expect_name();
				Table_field field = table.find_field(name);
				if (field == null) {
					throw new Unigen_exception(referrer + " reference missing field \"" + name + "\" in table \"" + table.name + "\"");
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

		return new Select_section(table, fields, record_type);
	}


	private static Query_param load_query_param(String name, Object def) {
		Field_parameters parameters = Field_parameters.parse((String) def);
		return new Query_param(name, parameters.type);
	}


}
