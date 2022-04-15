import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelParser {
	public static void main(String[] args) {
		int numColleges = 0;
		List<String[]> table = new ArrayList<>();
		HashMap<String, Integer> dictionary = fillDictionary(table);
		
		
		try (Scanner sc = new Scanner(Objects.requireNonNull(ExcelParser.class.getResourceAsStream("data.txt"))).useDelimiter(";")) {
			while (sc.hasNext()) {
				//account for redirects
				parseCollege(dictionary, table, sc);
				numColleges++;
			}
		}
		System.out.println(numColleges + " colleges parsed");
		
		try (var outFile = new FileOutputStream("src/CollegeDataTable.xlsx")) {
			tableToExcel(table).write(outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static HashMap<String, Integer> fillDictionary(List<String[]> table) {
		HashMap<String, Integer> dictionary = new HashMap<>();
		
		try (Scanner scanner = new Scanner(Objects.requireNonNull(ExcelParser.class.getResourceAsStream("questions.txt"))).useDelimiter("\n")) {
			String[] questions = scanner
					.tokens()
					.toArray(String[]::new);
			for (int i = 0; i < questions.length; i++)
				dictionary.put(questions[i].trim(), i);
			table.add(Arrays.stream(questions)
					.map(line -> switch (line) {
						case "$domain$" -> "Domain";
						case "$name$" -> "College Name";
						case "$0$" -> "Other Admissions Information";
						case "$1$" -> "Other Financial Aid Information";
						case "$2$" -> "Other Student Life Information";
						case "$other$" -> "Other Information";
						case "$contact$" -> "Special Contact";
						case "$aid$" -> "Gives aid?";
						default -> line;
					})
					.toArray(String[]::new));
		}
		
		return dictionary;
	}
	
	private static XSSFWorkbook tableToExcel(List<String[]> table) {
		XSSFWorkbook excelBook = new XSSFWorkbook();
		XSSFSheet excelSheet = excelBook.createSheet("College Data Table");
		XSSFRow row;
		int rowId = 0;
		for (String[] college : table) {
			row = excelSheet.createRow(rowId++);
			int cellId = 0;
			for (String cellData : college) {
				Cell cell = row.createCell(cellId++);
				cell.setCellValue(cellData);
			}
		}
		return excelBook;
	}
	
	private static void parseCollege(HashMap<String, Integer> dictionary, List<String[]> table, Scanner sc) {
		String[] currentArray = new String[table.get(0).length];
		//domain
		currentArray[0] = sc.next().trim();
		//college name
		currentArray[1] = sc.next().trim();
		//for the next three sections
		for (int i = 0; i < 3; i++) {
			String section = sc.next();
			parseSection(dictionary, currentArray, i, section);
		}
		currentArray[dictionary.get("$other$")] = sc.next().trim();
		currentArray[dictionary.get("$contact$")] = sc.next().trim();
		currentArray[dictionary.get("$aid$")] = sc.nextLine().trim();
		
		table.add(currentArray);
	}
	
	private static void parseSection(HashMap<String, Integer> dictionary, String[] currentArray, int i, String section) {
		StringBuilder answer = new StringBuilder();
		int index = dictionary.get("$" + i + "$"); //the other info in each section is named according to the index order
		String[] lines = section.split("\n");
		
		for (String line : lines) {
			if (line.contains("?")) {
				if (!answer.isEmpty())
					currentArray[index] = answer.toString().trim();
				answer.setLength(0);
				Integer tempIndex = dictionary.get(line);
				index = tempIndex == null
						? dictionary.get("$" + i + "$")
						: tempIndex;
			} else
				answer.append("\n").append(line);
		}
		if (!answer.isEmpty())
			currentArray[index] = answer.toString().trim();
		answer.setLength(0);
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	