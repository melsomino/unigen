package org.unified.templates;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class Template {

	public class Error extends Exception {
		public Error(Throwable error) {
			super(format_message(generated_class_name, generated_code, error), error);
		}
	}





	private static String format_message(String generated_class_name, String generated_code, Throwable error) {
		StringBuilder message = new StringBuilder();
		for (StackTraceElement stack : error.getStackTrace()) {
			if (stack.getClassName().equalsIgnoreCase(generated_class_name)) {
				int line_number = stack.getLineNumber();
				if (line_number < 0) {
					message.append(generated_code);
				} else {
					String[] code_lines = generated_code.split("\\r?\\n");
					for (int i = Math.max(0, line_number - 10); i <= line_number; ++i) {
						message.append(code_lines[i]).append('\n');
					}
				}
				message.append("^^^\n").append(error.getMessage()).append('\n');
			}
		}
		return message.toString();
	}





	Template(String generated_class_name, String generated_code, Object generator, Class... arg_types) throws Exception {
		this.generated_class_name = generated_class_name;
		this.generated_code = generated_code;
		this.generator = generator;
		this.method = generator.getClass().getMethod("generate", arg_types);
	}





	public void generate(PrintWriter writer, Object... args) throws Exception {
		Object[] method_args = new Object[1 + args.length];
		method_args[0] = writer;
		System.arraycopy(args, 0, method_args, 1, args.length);
		try {
			method.invoke(generator, method_args);
		} catch (InvocationTargetException invocation_error) {
			throw new Error(invocation_error.getTargetException());
		}
	}





	public void generate_to_file(Path file_path, Object... args) throws Exception {
		PrintWriter file = new PrintWriter(file_path.toFile(), "utf8");
		generate(file, args);
		file.close();
	}





	private final String generated_class_name;
	private final String generated_code;
	private final Object generator;
	private final Method method;
}
