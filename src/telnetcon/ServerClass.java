package telnetcon;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class ServerClass extends Thread {
	private Socket socket;
	private HashMap<String, String> map;

	public ServerClass(Socket socket, HashMap<String, String> map) {
		this.socket = socket;
		this.map = map;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
			writer.println("Welcome! Press 'enter' to start program!");

			String cmd;

			do {
				cmd = reader.readLine();
				
				String[] in;
				if (cmd == null) {
					break;
				}
				in = cmd.split("\\s+");

				String param = null;
				String result = null;

				if ("help".equals(in[0])) {
					writer.println("help - ispisuje listu komandi i sintaksu\r\n"
							+ "find all - izlistava sve studente i njihove indekse\r\n"
							+ "find [ime] - pronalazi broj indeksa studenta sa imenom npr.Pera (primer komande: find Pera).Pretraga je case sensitive.\r\n"
							+ "push [index] [ime] - cuva novog studenta u lokalnoj memoriji\r\n"
							+ "update [index] [ime] - menja ime za navedeni index\r\n"
							+ "pop [index] - brise studenta\r\n"
							+ "count [index] - vraca slova i broj koliko puta se pojavljuje u imenu (npr: za studenta {123,Mirko} komanda: count 123 vraca rezultat: V:1, L:1, A:1, D:1,I:2,M:1,R:1)\r\n"
							+ "quit - prekida konekciju sa tim klijentom.\r\n" + "");
				}

				if (cmd.startsWith("find")) {
					param = cmd.substring("find ".length(), cmd.length());
				}

				if (param != null) {
					result = find(map, param);

					writer.println("Result: " + result);
					continue;
				}

				if ("push".equals(in[0])) {
					result = push(map, in[1], in[2]);

					writer.println("New person: " + in[1] + " " + in[2]);
					continue;
				}

				if ("update".equals(in[0])) {
					result = update(map, in[1], in[2]);

					writer.println("Updated: " + result);
					continue;
				}

				if (cmd.startsWith("pop")) {
					param = cmd.substring("pop ".length(), cmd.length());
				}

				if (param != null) {
					result = pop(map, param);

					writer.println("Deleted person: " + result);
					continue;
				}

				if (cmd.startsWith("count")) {
					param = cmd.substring("count ".length(), cmd.length());
				}

				if (param != null) {
					result = count(map, param);

					writer.println("Counted: " + result);
					continue;
				}

			} while (!cmd.equals("quit"));

			socket.close();

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private static String find(HashMap<String, String> map, String value) {

		String result = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if ("all".equals(value)) {
				result = map.toString();
			} else if (entry.getValue().equals(value)) {
				result = entry.getKey();
				break;
			} else {
				result = String.format("Can not find name %s", value);
			}
		}

		return result;
	}

	private static String push(HashMap<String, String> map, String key, String value) {

		String result = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().equals(key)) {
				result = String.format("The key %s is occupied!", key);
				break;
			} else if (!entry.getKey().equals(key)) {
				map.put(key, value);
				break;
			} else {
				result = String.format("Can not add %s %s!", key, value);
			}
		}

		return result;
	}

	private static String update(HashMap<String, String> map, String key, String value) {

		String result = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().equals(key)) {
				result = map.replace(entry.getKey(), value);
				break;
			} else {
				result = String.format("Cant find value for key %s", key);
			}
		}

		return result;
	}

	private static String pop(HashMap<String, String> map, String value) {

		String result = map.get(value);
		if (result == null) {
			result = String.format("Cant find value for key %s", value);
		} else {
			map.remove(value);
		}

		return result;
	}

	private static String count(HashMap<String, String> map, String value) {

		String result = null;
		String counted = "";
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().equals(value)) {
				result = entry.getValue();
				result = result.toLowerCase();

				int[] counts = new int[result.length()];
				for (int i = 0; i < result.length(); i++) {
					for (int j = 0; j < result.length(); j++) {
						if (result.charAt(i) == result.charAt(j)) {
							counts[j]++;
							break;
						}
					}
				}

				for (int k = 0; k < result.length(); k++) {
					result = result.toUpperCase();
					if (counts[k] == 0) {

					} else {
						counted = counted + result.charAt(k) + ":" + counts[k] + ", ";
					}
				}
			} else {
				result = String.format("Cant find value for key %s", value);
			}
		}

		return counted;
	}
}