package routing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import routing.control.ApplicationException;
import routing.control.entities.Session;

public class SessionUtil {

	public static Vector<Session> fromString(String value)
			throws ApplicationException {
		Vector<Session> retVal = new Vector<Session>();

		BufferedReader stringReader = new BufferedReader(
				new StringReader(value));
		String line = null;
		Boolean error = false;
		Boolean end = false;

		do {
			try {
				line = stringReader.readLine();
				end = line == null || line.trim().equals("");

				if (!error && !end) {
					retVal.add(parseLine(line));
				}
			} catch (IOException e) {
				error = true;
			}

		} while (!error && !end);

		if (error) {
			throw new ApplicationException("IncorrectFileFormat", "Error");
		}

		return retVal;
	}

	private static Session parseLine(String line) throws IOException{
		Boolean endLine = false;
		String word = null;
		List<String> words = new ArrayList<String>();
		int chr = 0;

		BufferedReader lineReader = new BufferedReader(new StringReader(line));

		do {
			word = "";
			do {
				chr = lineReader.read();

				if (chr == -1) {
					endLine = true;
				} else if (chr != (int) (' ')) {
					word += (char) chr;
				}
			} while (chr != (int) (' ') && !endLine);

			words.add(word);
		} while (!endLine);

		if (words.get(0).equals("s")) {
			return parseSession(words);
		}
		return null;
	}

	private static Session parseSession(List<String> line) {
		Session retVal = new Session();
		
		retVal.id = Integer.parseInt(line.get(1));
		retVal.weight = Integer.parseInt(line.get(2));
		retVal.batchCount = Integer.parseInt(line.get(3));
		retVal.sourceId = Integer.parseInt(line.get(4));
		int i = 5;
		
		while(!line.get(i).equals("n")) {
			retVal.destinationIds.add(Integer.parseInt(line.get(i++)));
		}
		
		if (line.get(i).equals("n")) {
			if (line.get(line.size() - 1).equals("ne")) {
				++i;
				String label = "";
				String word = "";
				do {
					word = line.get(i);
					++i;

					if (word.charAt(0) == '{') {
						word = word.substring(1);
					}

					if (word.charAt(word.length() - 1) == '}') {
						word = word.substring(0, word.length() - 1);
					}

					word = word.replace("\\{", "{");
					word = word.replace("\\}", "}");

					if (i < line.size()) {
						label += " " + word;
					}
				} while (i != line.size());

				retVal.name = label.trim();
			}
		} else {
			// Error?
		}
		
		return retVal;
	}

	public static String fromObject(Vector<Session> value) {
		StringBuilder sb = new StringBuilder();

		for (Session s : value) {
			sb.append("s ");
			sb.append(s.id);
			sb.append(" ");
			sb.append(s.weight);
			sb.append(" ");
			sb.append(s.batchCount);
			sb.append(" ");
			sb.append(s.sourceId);
			sb.append(" ");
			for (int d : s.destinationIds) {
				sb.append(d);
				sb.append(" ");
			}
			sb.append("n");
			String lbl = (s.name == null || s.name.trim().equals("") ? ""
					: s.name.trim().replace("{", "\\{").replace("}", "\\}"));

			if (lbl.indexOf(" ") != -1) {
				lbl = "{" + lbl + "}";
			}

			if (!lbl.equals("")) {
				lbl = " " + lbl + " ne";
			}
			sb.append(lbl);
			sb.append("\n");
		}

		return sb.toString();
	}

}
