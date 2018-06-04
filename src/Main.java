import javafx.util.Pair;
import org.unified.dev.generator.Module_generator;
import org.unified.dev.server.Dev_server;
import org.unified.module.Module;
import org.unified.module.Module_loader;

import java.nio.file.Paths;
import java.util.Arrays;


public class Main {


	private static Pair<String, String> parse_command_value(String arg) {
		arg = arg.trim();
		if (!arg.startsWith("-")) {
			return new Pair<>(null, arg);
		}
		arg = arg.substring(1);
		int separator_pos = arg.indexOf('=');
		if (separator_pos < 0) {
			separator_pos = arg.indexOf(':');
		}
		if (separator_pos < 0) {
			return new Pair<>(arg.toLowerCase(), null);
		}
		return new Pair<>(arg.substring(0, separator_pos).toLowerCase(), arg.substring(separator_pos + 1));
	}



	private static void generate_modules(Dev_server dev_server) {
		Arrays.stream(dev_server.configuration.sources).filter(source -> source.is_module).forEach((source) -> {
			try {
				System.out.println("Generate: " + source.name);
				Module module = Module_loader.load(source.path);
				Module_generator.generate(module, source.ios_out, source.android_out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}



	public static void main(String[] args) {
		try {
			Dev_server dev_server = new Dev_server();
			String configuration_file = null;
			String dev_web_path = null;
			boolean generate = false;
			boolean run = false;


			for (String source_arg : args) {
				Pair<String, String> arg = parse_command_value(source_arg);
				String command = arg.getKey();
				String value = arg.getValue();
				if (command == null) {
					configuration_file = value;
				} else if (command.equals("dev_web_path")) {
					dev_web_path = value;
				} else if (command.equals("generate")) {
					generate = true;
				} else if (command.equals("run")) {
					run = true;
				}
			}
			if (configuration_file != null) {
				dev_server.configure(Paths.get(configuration_file));
			}
			if (dev_web_path != null) {
				dev_server.dev_web_path = Paths.get(dev_web_path);
			}
			if (generate) {
				generate_modules(dev_server);
			}
			if (run) {
				dev_server.run();
			}
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
