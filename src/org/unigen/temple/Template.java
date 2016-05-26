package org.unigen.temple;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class Template {


	Template(Object generator, Class... arg_types) throws Exception {
		this.generator = generator;
		this.method = generator.getClass().getMethod("generate", arg_types);
	}


	public void generate(PrintStream writer, Object... args) throws Exception {
		Object[] method_args = new Object[1 + args.length];
		method_args[0] = writer;
		System.arraycopy(args, 0, method_args, 1, args.length);
		method.invoke(generator, method_args);
	}


	public void generate_to_file(Path file_path, Object... args) throws Exception {
		PrintStream file = new PrintStream(file_path.toFile(), "utf8");
		generate(file, args);
		file.close();
	}

	private final Object generator;
	private final Method method;
}
