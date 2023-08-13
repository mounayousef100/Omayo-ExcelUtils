package com.omayo.utility;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	public static String path = System.getProperty("user.dir") + "\\src\\test\\resources\\TestData\\TestOmayo.xlsx";

//	public  String path;
    private FileInputStream fis = null;
    private FileOutputStream fileOut = null;
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;
    private XSSFRow row = null;
    private XSSFCell cell = null;
    private int rowsAdded = 0;


    public ExcelUtils() {
        this.path = path;
        try {
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRandomDataIfRowIsEmpty() {
        Random random = new Random();

        int rowCount = sheet.getLastRowNum() + 1;
        int columnCount = getColumnCount();

        int emptyRowCount = 0;
        int targetEmptyRowCount = 5; // Set the desired number of empty rows to add data to

        // Add random data to the desired number of empty rows
        for (int i = 1; i <= rowCount; i++) {
            XSSFRow currentRow = sheet.getRow(i);

            if (currentRow == null) {
                currentRow = sheet.createRow(i);
            }

            boolean isRowEmpty = true;
            for (int j = 0; j < columnCount; j++) {
                XSSFCell currentCell = currentRow.getCell(j);
                if (currentCell != null && currentCell.getCellType() != CellType.BLANK) {
                    isRowEmpty = false;
                    break;
                }
            }

            if (isRowEmpty) {
                emptyRowCount++;
                if (emptyRowCount <= targetEmptyRowCount) { // Add random data only to the desired number of empty rows
                    for (int j = 0; j < columnCount; j++) {
                        XSSFCell currentCell = currentRow.createCell(j);
                        int dataType = random.nextInt(3); // Random data type: 0 - String, 1 - Numeric, 2 - Boolean

                        switch (dataType) {
                            case 0: // String
                                currentCell.setCellValue("RandomString" + random.nextInt(1000));
                                break;
                            case 1: // Numeric
                                currentCell.setCellValue(random.nextDouble() * 100);
                                break;
                            case 2: // Boolean
                                currentCell.setCellValue(random.nextBoolean());
                                break;
                        }
                    }
                    System.out.println("Successfully entered value");
                } else {
                    break; // Stop adding rows if we have already added the desired number of empty rows
                }
            }
        }

        try {
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();

            // Close the previous workbook and create a new one after updating the data
            workbook.close();
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Helper method to get the total number of columns in the sheet
    private int getColumnCount() {
        row = sheet.getRow(0);
        if (row == null)
            return -1;
        return row.getLastCellNum();
    }

    public void readDataFromCells() {
        int rowCount = sheet.getLastRowNum() + 1;
        int columnCount = getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                XSSFRow currentRow = sheet.getRow(i);
                if (currentRow != null) {
                    XSSFCell currentCell = currentRow.getCell(j);
                    if (currentCell != null) {
                        String cellData = "";
                        if (currentCell.getCellType() == CellType.STRING) {
                            cellData = currentCell.getStringCellValue();
                        } else if (currentCell.getCellType() == CellType.NUMERIC || currentCell.getCellType() == CellType.FORMULA) {
                            double numericValue = currentCell.getNumericCellValue();

                            if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(HSSFDateUtil.getJavaDate(numericValue));
                                cellData = (cal.get(Calendar.DAY_OF_MONTH)) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.YEAR) % 100);
                            } else {
                                cellData = String.valueOf(numericValue);
                            }
                        } else if (currentCell.getCellType() == CellType.BOOLEAN) {
                            cellData = String.valueOf(currentCell.getBooleanCellValue());
                        }

                        System.out.println("Test Case: " + (i + 1) + ", Column: " + (j + 1) + ", Value: " + cellData);
                    }
                }
            }
        }
        
    }
	public String getCellData(String sheetName, String colName, int rowNum) {
	    try {
	    	Thread.sleep(2000);
	        if (rowNum <= 0)
	            return "";

	        int index = workbook.getSheetIndex(sheetName);
	        int col_Num = -1;
	        if (index == -1)
	            return "";

	        sheet = workbook.getSheetAt(index);
	        row = sheet.getRow(0);
	        for (int i = 0; i < row.getLastCellNum(); i++) {
	            if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
	                col_Num = i;
	        }
	        if (col_Num == -1)
	            return "";

	        sheet = workbook.getSheetAt(index);
	        row = sheet.getRow(rowNum - 1);
	        if (row == null || row.getCell(col_Num) == null || row.getCell(col_Num).getCellType() == CellType.BLANK)
	            return "";

	        cell = row.getCell(col_Num);

	        if (cell.getCellType().name().equals("STRING"))
	            return cell.getStringCellValue();
	        else if (cell.getCellType().name().equals("NUMERIC") || cell.getCellType().name().equals("FORMULA")) {

	            String cellText = String.valueOf(cell.getNumericCellValue());
	            if (HSSFDateUtil.isCellDateFormatted(cell)) {
	                // format in form of M/D/YY
	                double d = cell.getNumericCellValue();

	                Calendar cal = Calendar.getInstance();
	                cal.setTime(HSSFDateUtil.getJavaDate(d));
	                cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
	                cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;
	            }

	            return cellText;
	        } else
	            return String.valueOf(cell.getBooleanCellValue());

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "row " + rowNum + " or column " + colName + " does not exist in xls";
	    }
	}
	

	// returns the data from a cell
	public String getCellData(String sheetName, int colNum, int rowNum) {
		try {
			Thread.sleep(2000);
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);

			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(colNum);
			if (cell == null)
				return "";

			if (cell.getCellType().name().equals("STRING"))
				return cell.getStringCellValue();
			else if (cell.getCellType().name().equals("NUMERIC") || cell.getCellType().name().equals("FORMULA")) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(HSSFDateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cellText;

					// System.out.println(cellText);

				}

				return cellText;
			} else if (cell.getCellType().name().equals("BLANK"))
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e) {

			e.printStackTrace();
			return "row " + rowNum + " or column " + colNum + " does not exist  in xls";
		}
	}


	// returns true if column is created successfully
	public boolean addColumn(String sheetName, String colName) {
		// System.out.println("**************addColumn*********************");

		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1)
				return false;

			XSSFCellStyle style = workbook.createCellStyle();
			// style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			// style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);

			// cell = row.getCell();
			// if (cell == null)
			// System.out.println(row.getLastCellNum());
			if (row.getLastCellNum() == -1)
				cell = row.createCell(0);
			else
				cell = row.createCell(row.getLastCellNum());

			cell.setCellValue(colName);
			cell.setCellStyle(style);

			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}


	// find whether sheets exists
	public boolean isSheetExist(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1) {
			index = workbook.getSheetIndex(sheetName.toUpperCase());
			if (index == -1)
				return false;
			else
				return true;
		} else
			return true;
	}

	// returns number of columns in a sheet
	public int getColumnCount(String sheetName) {
		// check if sheet exists
		if (!isSheetExist(sheetName))
			return -1;

		sheet = workbook.getSheet(sheetName);
		row = sheet.getRow(0);

		if (row == null)
			return -1;

		return row.getLastCellNum();

	}

	public int getCellRowNum(String sheetName, String colName, String cellValue) throws InterruptedException {

		for (int i = 2; i <= getRowCount(sheetName); i++) {
			if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
				return i;
			}
		}
		return -1;

	}

	public int getRowCount(String sheetName) throws InterruptedException {
	
	    int index = workbook.getSheetIndex(sheetName);
	    if (index == -1)
	        return 0;
	    else {
	        sheet = workbook.getSheetAt(index);
	        int lastRowNum = sheet.getLastRowNum();
	        int count = 0;

	        for (int i = 0; i <= lastRowNum; i++) {
	            if (sheet.getRow(i) != null && sheet.getRow(i).getPhysicalNumberOfCells() > 0)
	                count++;
	        }

	        return count;
	    }
	}

}