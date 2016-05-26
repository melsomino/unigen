package org.unigen.model.cloud;

import org.unigen.Unigen_exception;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface Cloud_type {
	String get_to_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unigen_exception;
	String get_from_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unigen_exception;
	String get_record_schema_name();
}
