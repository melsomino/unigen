package org.unified.module.cloud;

import org.unified.Unified_error;

public interface Cloud_type {
	String get_to_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unified_error;
	String get_from_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unified_error;
	String get_record_schema_name();
}
