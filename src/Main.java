
import org.unified.dev.Dev_configuration;
import org.unified.dev.generator.Module_generator;
import org.unified.dev.server.Dev_server;
import org.unified.module.Module;
import org.unified.module.Module_loader;

import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		try {
			Dev_server dev_server = new Dev_server();
			dev_server.dev_web_path = Paths.get("/Users/mu.vlasov/projects/unigen/src/org/unified/dev/server/web");
			dev_server.configure(Paths.get(args[0]));

			Dev_configuration.Source source = dev_server.configuration.find_source_with_path(Paths.get("/Users/mu.vlasov/projects/sbis/ios/rc-0.2.0/ios-notification/SbisNotifications/SbisNotifications/Code/Tasks.uni"));
			Module module = Module_loader.load(source.path);
			Module_generator.generate(module, source.ios_out, source.android_out);
			System.out.println("Generated");

//			dev_server.run();
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
