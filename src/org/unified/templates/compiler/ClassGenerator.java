package org.unified.templates.compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * Build a class from code
 *
 * @author Henry Huang <a href="mailto:h1886@outlook.com">h1886@outlook.com</a>
 * @version 8:52:50 PM Nov 25, 2016
 *
 */
public class ClassGenerator {

	private String classRootDir;

	public ClassGenerator() {
		this(Paths.get(System.getProperty("java.io.tmpdir"), "unserved").toString());
	}

	/**
	 * @param classRootDir
	 *            class root dir
	 */
	public ClassGenerator(String classRootDir) {
		this.classRootDir = classRootDir;
	}

	/**
	 *
	 * @param classFullName the class's full name
	 * @param code the java source code that you want to product class
	 * @return the class
	 * @throws MalformedURLException see {@link URL#toURI()}
	 * @throws ClassNotFoundException see {@link Class#forName(String, boolean, ClassLoader)}
	 */
	public Class<?> generate(String classFullName, String code) throws MalformedURLException, ClassNotFoundException {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileObject fileObject = new JavaSourceFromString(classFullName, code);

		if (!new File(classRootDir).exists()) {
			new File(classRootDir).mkdirs();
		}

		Iterable<String> options = Arrays.asList("-d", classRootDir);
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(fileObject);
		CompilationTask task = compiler.getTask(null, null, null, options, null, compilationUnits);

		// generate class
		boolean success = task.call();
		if (success) {
			File root = new File(classRootDir);
			URL[] urls;
			urls = new URL[] { root.toURI().toURL() };
			URLClassLoader classLoader = URLClassLoader.newInstance(urls);
			Class<?> clazz = Class.forName(classFullName, true, classLoader);
			return clazz;
		}

		return null;
	}

	public class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		/**
		 * @param name the class's full name
		 * @param code
		 */
		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}

	}
}