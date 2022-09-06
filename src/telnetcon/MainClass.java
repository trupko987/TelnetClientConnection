package telnetcon;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class MainClass {

	public static void main(String[] args) {

		int port = 4042;

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("1", "Milos");
		map.put("2", "Ivan");
		map.put("3", "Mirko");
		map.put("4", "WeeRRttghhaaaasssiiiii");

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");

				ServerClass s = new ServerClass(socket, map);
				s.start();
			}
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}