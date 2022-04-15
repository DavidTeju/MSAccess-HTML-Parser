import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

public class HTMLParser {
	private static final String SECTION =
			"""
								<section>
									<h2>$sectionName$</h2>
					$sectionContent$            </section>
					""";
	private static final String FOLDER = "Webpages" + File.separator;
	private static final String html =
			"""
					<!DOCTYPE html>
					<html lang="en">
					$head$
					$body$
					</html>
					""";
	private static final String head =
			"""
						<head>
							<title>$name$</title>
							<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
							<link href="../../src/styling.css" rel="stylesheet" />
						</head>
					""";
	private static final String body =
			"""
						<body>
							<main>
								<h1>$collegeName$</h1>
					$admissions$
					$finaid$
					$studlife$
					$other$
					$contact$
					$aid$
							</main>
						</body>
					""";
	
	/**
	 *
	 */
	public static void main(String[] args) {
		int numColleges = 0;
		
		try (var sc = new Scanner(Objects.requireNonNull(HTMLParser.class.getResourceAsStream("data.txt"))).useDelimiter(";")) {
			new File("Webpages").mkdirs();
			while (sc.hasNext()) {
				nextCollege(sc);
				numColleges++;
			}
		}
		
		// confirmation
		System.out.println("finished");
		System.out.println(numColleges + " colleges processed");
	}
	
	/**
	 *
	 */
	private static void nextCollege(Scanner sc) {
		String domain = sc.next().trim();
		String collegeName = sc.next().trim();
		// create folder for each University after domain
		new File(FOLDER + domain).mkdirs();
		try (PrintWriter file = new PrintWriter(String.format("%s%s%s%s.html", FOLDER, domain, File.separator, collegeName))) {
			//All Universities that redirect will have their name written as Example University(r)
			//The admission field will be used for the URL and all other fields will be skipped
			if (collegeName.contains("(r)")) { //if the name contains our identifier
				redirect(collegeName.replace("(r)", ""), sc.next(), file);
				for (int i = 0; i < 4; i++)//skips the rest of the fields
					sc.next();
				sc.nextLine();
			} else //for the pages that don't redirect(most pages), we build a page using its content
				file.print(html
						.replace("$head$", head
								.replace("$name$", collegeName))
						.replace("$body$\n", body
								.replace("$collegeName$", collegeName)
								.replace("$admissions$", analyzeSection(sc, "Admissions Information"))
								.replace("$finaid$", analyzeSection(sc, "Financial Aid Policies"))
								.replace("$studlife$", analyzeSection(sc, "Student Life"))
								.replace("$other$", merger(sc))
								.replace("$contact$", special(sc))
								.replace("$aid$", aid(sc))
						).trim());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void redirect(String collegeName, String url, PrintWriter file) {
		String temp =
				html
						.replace("$head$",
								"""
										<head>
											<title>Presidents Alliance Policy Database</title>
											<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
											<meta http-equiv="refresh" content="0; url='$url$'" />
											<link href="../../src/styling.css" rel="stylesheet" />
										</head>
										""")
						.replace("$body$\n",
								"""
										<body style="
											background: #191A19;
											display: flex;\s
											align-items: center;\s
											width: 80vw;\s
											height: 100vh;
											justify-content: center;\s
											margin: auto;
											position: relative;
											top: -1em">
											<span style="
												color: #4E9F3D;\s
												text-align: center;\s
												font-size: 5em">
												You are being redirected to $collegeName$'s website
											</span>
											<p> Please follow <a href="$url$">this link</a> if you are not redirected immediately.
										</body>
										""")
						.replace("$collegeName$", collegeName)
						.replace("$url$", url);
		file.print(temp);
	}
	
	private static String appendField(Scanner sectionScanner) {
		StringBuilder toAppend = new StringBuilder();
		sectionScanner.useDelimiter("\n").tokens()
				.map(line -> analyzeLine(line.trim()))
				.forEach(toAppend::append);
		
		return String.valueOf(toAppend);
	}
	
	private static String analyzeLine(String line) {
		StringBuilder analyzedLine = new StringBuilder();
		int holdPosition = 0;
		if (line.length() < 1) return "";
		
		if (line.contains("ul>") || line.contains("li>"))//this covers starting and closing tags
			return "\t\t\t\t" + line + "\n";
		
		if (line.contains("?"))
			for (int i = 0; i < line.length(); i++)
				if (line.charAt(i) == '?') {
					analyzedLine.append(question(line.substring(holdPosition, i + 1)));
					holdPosition = i + 1;
				}
		
		if (holdPosition < line.length())//If there's anything left in the line after extracting questions
			analyzedLine.append(answer(line.substring(holdPosition)));
		
		return String.valueOf(analyzedLine);
	}
	
	private static String analyzeSection(Scanner sc, String sectionName) {
		StringBuilder onHold = new StringBuilder();
		Scanner sectionScanner = new Scanner(listify(sc.next())).useDelimiter("\n");
		//take the next Section and turn it into its own scanner
		//listify here before we put into Scanner and Stream in individual lines
		
		if (sectionScanner.hasNext())
			onHold.append(SECTION
					.replace("$sectionName$", sectionName)
					.replace("$sectionContent$", appendField(sectionScanner))
			);
		
		sectionScanner.close();
		return String.valueOf(onHold);
	}
	
	private static String merger(Scanner sc) {
		String sectionString = sc.next().trim();
		if (sectionString.length() > 2)
			return
					SECTION
							.replace("$sectionName$", "More Information")
							.replace("$sectionContent$", String.format("\t\t\t\t\t<p>%s</p>\n", sectionString)
							);
		return "";
	}
	
	private static String aid(Scanner sc) {
		if (sc.nextLine().trim().equalsIgnoreCase("0"))
			return "\t\t\t<p class=\"aid\">Does not give need-based aid</p>";
		
		return "\t\t\t<p class=\"aid\">Gives need-based aid</p>";
	}
	
	private static String listify(String wordToListify) {
		StringBuilder onHold = new StringBuilder();
		boolean currentlyInList = false;
		for (String wordInCheck : wordToListify.trim().split("\n")) {
			if (wordInCheck.length() < 2) continue;
			if (currentlyInList)
				if (wordInCheck.startsWith("•"))
					onHold.append("<li>").append(wordInCheck.replace("•", "")).append("</li>\n");
				else {
					onHold.append("</ul>\n").append(wordInCheck).append("\n");
					currentlyInList = false;
				}
			else if (wordInCheck.startsWith("•")) {
				onHold.append("<ul>\n<li>").append(wordInCheck.replace("•", "")).append("</li>\n");
				currentlyInList = true;
			} else
				onHold.append(wordInCheck).append("\n");
		}
		
		if (currentlyInList) onHold.append("</ul>\n");
		
		return String.valueOf(onHold);
	}
	
	private static String special(Scanner sc) {
		String sectionString = sc.next().trim();
		if (sectionString.length() > 2)
			return
					(SECTION
							.replace("$sectionName$", "Who to Contact")
							.replace("$sectionContent$", String.format("\t\t\t\t\t<p>%s</p>\n", sectionString)
							));
		return "";
	}
	
	private static String answer(String wordsToPass) {
		return String.format("\t\t\t\t<p class =\"answer\">%s</p>\n", wordsToPass);
	}
	
	private static String question(String wordsToPass) {
		return String.format("\t\t\t\t<h3 class=\"question\">%s</h3>\n", wordsToPass);
	}
}