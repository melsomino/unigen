import org.unified.dev.server.Dev_server;

import java.nio.file.Paths;


public class Main {

	public static void main(String[] args) {
		try {
			Dev_server dev_server = new Dev_server();
			dev_server.configure(Paths.get(args[0]));
			dev_server.run();
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
