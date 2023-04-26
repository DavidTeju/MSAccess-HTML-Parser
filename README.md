# MSAccess-HTML-Excel Parser

MSAccess-HTML-Excel Parser is a set of two Java programs designed to process and organize loosely typed questions and answers from educational institutions into properly formatted semantic HTML and Excel (xlsx) files. This project was created in response to the challenges I faced while gathering information from over 1000 educational institutions regarding their policies towards a specific sub-group of students. The initial email template was not strictly followed, resulting in inconsistent and incomplete replies. These programs provide a workaround to identify questions, answers, and other relevant information and sort them into a more structured and accessible format.

## Features

- Sorts and organizes loosely typed questions and answers into structured data.
- Generates semantic HTML files for each institution, ensuring consistent formatting and structure.
- Creates an Excel file (xlsx) to easily visualize and analyze the gathered information.
- Handle redirects and special cases, ensuring all relevant information is included.

## Note

This repo is provided for portfolio purposes and is not an open source project. This main goal of publishing this repo is to act as a benchmark for my progress since early 2022, and to demonstrate my ability to create effective solutions for handling unstructured and inconsistent data in a real-world scenario. While I could do better today if I took up this task, I'm proud of the work I did here with the limited experience I had.

## Programs

1. **ExcelParser.java**: Reads the input data, organizes it into a structured format, and generates an Excel file (xlsx) containing the organized information for each institution.
2. **HTMLParser.java**: Processes the input data and creates well-structured semantic HTML files for each institution, ensuring consistent formatting and presentation.

## Getting Started

To use the MSAccess-HTML-Excel Parser, follow these steps:

1. Ensure you have Java installed on your machine.
2. Clone this repository to your local machine.
3. Add the input data file `data.txt` containing the information from educational institutions, following the format provided in the example file.
4. Compile and run the `ExcelParser.java` and `HTMLParser.java` programs.
5. The generated HTML files will be stored in the `Webpages` directory, and the Excel file will be saved as `CollegeDataTable.xlsx`.
