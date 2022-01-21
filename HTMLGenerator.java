import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class HTMLGenerator {
    private static final String ANSWER = "\t\t<p> \n\t\t\t%s \n\t\t</p>\n";
    private static final String SECTION = "\t\t<div class=\"section\"><h2>";
    private static final String SOURCE_FILE = "src" + File.separator + "read.txt";
    private static final String FOLDER = "Webpages"+File.separator;

    /**
     *
     */
    public static void main(String[] args)  {

        Scanner sc = null;
        try {
            sc = new Scanner(new File(SOURCE_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sc.useDelimiter(";");

        new File("Webpages").mkdirs();
        int numColleges = 0;
        while (sc.hasNext()) {
            try {
                nextCollege(sc);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            numColleges++;
        }
        sc.close();

        // confirmation
        System.out.println("finished");
        System.out.println(numColleges + " colleges processed");
    }

    /**
     *
     */
    private static void nextCollege(Scanner sc) throws FileNotFoundException {
        String domain = domain(sc);
        String collegeName = collegeName(sc);
        // create folder for each University after domain
        new File(FOLDER + domain).mkdirs();
        PrintWriter file = new PrintWriter(String.format("%s%s%s%s.html", FOLDER, domain, File.separator, collegeName));
        //All Universities that redirect will have their name written as Example University(r)
        //The admission field will be used for the URL and all other fields will be skipped
        if (collegeName.contains("(r)")) { //if the name contains our identifier
            redirect(collegeName.replace("(r)", ""), sc.next(), file);
            for (int i = 0; i < 4; i++)
                sc.next();
            sc.nextLine();
        }
        else {//for the pages that don't redirect(most pages), we build a page using its content
            file.print("<!DOCTYPE html>\n<html lang=\"en\">\n"
                    + "\t<head>"
                    + String.format("%n\t\t<title>%s</title>%n", collegeName)
                    + """
                <meta http-equiv=Content-Type content="text/html; charset=UTF-8">
                <link href="../../src/styling.css" rel="stylesheet" />
            </head>
                
            <body>
        """ + "\t\t<div class=\"collegeName\">" + collegeName + "</div>\n");
            printSection(sc, file, "Admissions Information");
            printSection(sc, file, "Financial Aid");
            printSection(sc, file, "Student Life");
            merger(sc, file);
            special(sc, file);
            aid(sc, file);
            file.print("\n\t</body>\n</html>");
        }
        file.close();
    }

    public static void redirect (String collegeName, String url, PrintWriter file){
        file.print("""
    <html lang="en">
    <head>
    """ + "\t\t<meta http-equiv=\"refresh\" content=\"5;url=" + url + "/\">\n" +
                """
                <title>
                Presidents Alliance Policy Database
                </title>
                </head>
                <body>
                <div style="text-align:center;">
                <div style="width:90%;
                height: 100px;
                display:inline-block;">
                <h1><span style="color: black;">""" + "You are being redirected to " + collegeName + "'s website</span></h1>\n"
                + """
    </div>
    </div>
    </body>
    </html>
    """);
    }

    private static String domain(Scanner sc) {
        return sc.next().trim();
    }

    private static String collegeName(Scanner sc) {
        return sc.next().trim();
    }

    private static void printLine(PrintWriter file, Scanner scan) {
        String wordsToPass;
        int holdPosition;
        while (scan.hasNext()) {
            wordsToPass = scan.nextLine().trim();
            holdPosition = 0;
            if (wordsToPass.length() > 1){
                if (wordsToPass.contains("?")) {
                    flushListOnHold(file);
                    for (int i = 0; i < wordsToPass.length(); i++)
                        if (wordsToPass.charAt(i) == '?') {
                            question(wordsToPass.substring(holdPosition, i + 1), file);
                            holdPosition = i + 1;
                        }
                }
                if (holdPosition<wordsToPass.length())
                    answer(wordsToPass.substring(holdPosition), file);

            }
        }
    }

    private static void printSection(Scanner sc, PrintWriter file, String sectionName) {
        flushListOnHold(file);
        String field = sc.next();
        Scanner scan = new Scanner(field);
        if (scan.hasNext()){
            file.print(SECTION + sectionName + "</h2></div>\n");
            printLine(file, scan);
        }
        if (listOnHold!=null){
            file.printf(ANSWER, listOnHold + "\n\t\t\t</ul>");
            listOnHold = null;
        }
        scan.close();
    }

    private static void merger(Scanner sc, PrintWriter file) {
        flushListOnHold(file);
        String field = sc.next();
        if (field.length() > 2){
            file.printf(SECTION + "More Information</h2></div>%n");
            for (String line: Arrays.stream(field.split("\n")).map(String::trim).toArray(String[]::new))
                if (!line.equals(""))
                    answer(line, file);
        }
    }

    private static void aid(Scanner sc, PrintWriter file) {
        if (sc.nextLine().equalsIgnoreCase("0"))
            file.print("\t\t<div class=\"aid\">Does not give need based aid</div>");
        else
            file.print("\t\t<div class=\"aid\">Gives need-based aid</div>");
    }

    private static void special(Scanner sc, PrintWriter file) {
        String field = sc.next();
        if (field.length() > 2){
            file.print(SECTION + "Who To Contact</h2></div>\n");
            file.printf(ANSWER, field);
        }
    }

    static String listOnHold = null;
    private static void answer(String wordsToPass, PrintWriter file) {
        if (wordsToPass.contains("•"))
            listify(wordsToPass.replace("•", "").trim());
//         Print answer to file
        else {
            flushListOnHold(file);
            file.printf(ANSWER, wordsToPass);
        }
    }

    private static void flushListOnHold(PrintWriter file) {
        if (listOnHold != null) {
            file.printf(ANSWER, listOnHold + "\n\t\t\t</ul>");
            listOnHold = null;
        }
    }

    private static void listify(String wordToListify){
        if (listOnHold == null)
            listOnHold = String.format("<ul>\n\t\t\t\t<li>%s</li>", wordToListify);
        else
            listOnHold += String.format("\n\t\t\t\t<li>%s</li>", wordToListify);
    }

    private static void question(String wordsToPass, PrintWriter file) {
        // Print question to file as Header 3
        file.printf("\t\t\t<div class=\"question\"><h3>%s</h3></div>%n", wordsToPass);
    }
}