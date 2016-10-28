import org.unified.dev.Dev_configuration;
import org.unified.dev.generator.Module_generator;
import org.unified.dev.server.Dev_server;
import org.unified.module.Module;
import org.unified.module.Module_loader;

import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		try {
			Dev_server dev_server = new Dev_server();
			dev_server.dev_web_path = Paths.get("/Users/mu.vlasov/projects/unigen/src/org/unified/dev/server/web");
			dev_server.configure(Paths.get(args[0]));

			Arrays.stream(dev_server.configuration.sources).filter(source -> source.is_module).forEach((source) -> {
				try {
					System.out.println("Generate: " + source.name);
					Module module = Module_loader.load(source.path);
					Module_generator.generate(module, source.ios_out, source.android_out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

//			dev_server.run();
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
