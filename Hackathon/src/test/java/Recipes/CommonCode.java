package Recipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class CommonCode {
	public static WebDriver chromeDriver;
	public static String strFilteredRecipes =  System.getProperty("user.dir")
				+ "\\src\\test\\resources\\ExcelFiles\\FilteredRecipes.xlsx";
	
	
	public static String[] strHeading = {"Recipe ID",
			"Recipe Name: ",
			"Recipe Category",
			"Food Category", 
			"Ingredients",
			"Preparation Time",
			"Cooking Time",
			"Preparation Method",
			"Nutrient Values",
			"Targetted Morbid Conditions",
			"Recipe URL",
			"Allergies : "
			};
	public static List<String> lstHypothyroidism = new ArrayList<String>();
	public static List<String> lstDiabetes = new ArrayList<String>();
	public static List<String> lstHypertension = new ArrayList<String>();
	public static List<String> lstPCOS = new ArrayList<String>();
	
	public static List<String> lstHDiabetes = new ArrayList<String>();
	public static List<String> lstHHThyroidism = new ArrayList<String>();
	public static List<String> lstHHTension = new ArrayList<String>();
	public static List<String> lstHPCOS = new ArrayList<String>();
	
	public static List<String> lstAllergies = new ArrayList<String>();
	
	//load home page of website of tarladalal.com
	@Test(priority = 1)
	private void LoadPage() throws InterruptedException, IOException {		
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.setAcceptInsecureCerts(true);
		optChrome.addArguments("--remote-allow-origins=*");
		optChrome.setImplicitWaitTimeout(Duration.ofSeconds(10));
		ReadDataFromExcel();
		chromeDriver = new ChromeDriver(optChrome);
		//chromeDriver = new FirefoxDriver();
		chromeDriver.get("https://tarladalal.com/");	
		//chromeDriver.manage().window().maximize();
	}

	//Read the list of eliminated ingredients from excel sheet and store it in the list/array
	public void ReadDataFromExcel() throws IOException
	{		
		//String path = System.getProperty("user.dir")+"\\IngredientsAndComorbidities-ScrapperHackathon.xlsx";
		String path =System.getProperty("user.dir") + 
				"\\src/test\\resources\\ExcelFiles\\IngredientsAndComorbidities-ScrapperHackathon.xlsx";
		//System.out.println(path);
		File excelfile = new File(path);
		FileInputStream fis = new FileInputStream(excelfile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);//("EliminatedItems");
		int lastRow = sheet.getLastRowNum();
	    //System.out.println("last row: " + lastRow);

		int i=0;
		XSSFRow row;
		XSSFCell cell;
		String strItem;
		
		for (i=2;i<lastRow;i++) {
			row = sheet.getRow(i);
		    cell=row.getCell(0);
		    if (cell == null) {
		    	break;
		    }		    		    
		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstDiabetes.add(strItem.strip());
		    }
		}

		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(2);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHypothyroidism.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(4);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHypertension.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(6);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstPCOS.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(1);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHDiabetes.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(3);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHHThyroidism.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(5);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHHTension.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(7);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstHPCOS.add(strItem.strip());
		    }
		}
		for (i=2;i<lastRow;i++) {
			row=sheet.getRow(i);
		    cell=row.getCell(8);

		    strItem = cell.getStringCellValue();
		    if (!strItem.isBlank()){
		    	lstAllergies.add(strItem.strip());
		    }
		}
	    workbook.close();	
	  }
	
	public static void CheckForHealthyItems(List<String> healthyItems, int sheetNo) throws IOException {
		XSSFRow row;
		XSSFCell cell;		
		boolean bFound=false;
		int i;
		String ingredient="";
		Path p = Paths.get(CommonCode.strFilteredRecipes);
		boolean bFileExists = Files.exists(p);
		
		XSSFWorkbook wb;

		//if file already exists, check for the last row number
		if(bFileExists) {
			FileInputStream myxls = new FileInputStream(CommonCode.strFilteredRecipes);
			wb = new XSSFWorkbook(myxls);
		    XSSFSheet sheet = wb.getSheetAt(sheetNo);
			int lastRow=sheet.getLastRowNum();			
		    for(i=0;i<lastRow;i++){
		    	row = sheet.getRow(i);		    	
		    //	cell = row.getCell(9);
	//	    	if(cell.getStringCellValue().equals("Diabetes")){
	    		cell = row.getCell(4);
	    		ingredient = cell.getStringCellValue().toLowerCase();

	    		for (String item: healthyItems ) {
			        bFound = ingredient.contains(item.toLowerCase());
			        if (bFound) {
			        	XSSFFont font = wb.createFont();
			        	CellStyle cs = wb.createCellStyle();
			        	font.setColor(IndexedColors.GREEN.getIndex());
						font.setBold(true);
						//font.setColor(new XSSFFont().COLOR_RED);
						cs.setFont(font);
						for(int x=0;x<11;x++)
						{
							cell = row.getCell(x);
							cell.setCellStyle(cs);
						}
						System.out.println(row.getRowNum() + " : " + item);
			        	break;
			    	   	}
		//		   	}
		    		if(!bFound) {
		    			continue;
		    		}
		    		bFound = false;	
		    	}
		    }
			FileOutputStream fileOut = new FileOutputStream(CommonCode.strFilteredRecipes);
			wb.write(fileOut);
			fileOut.close();
		    wb.close();
		}
	}
	
	public static void CheckForAllergy(String allergy, int sheetNo) throws IOException {
		System.out.println(allergy);
		XSSFRow row;
		XSSFCell cell;		
		boolean bFound=false;
		int i;
		String ingredient="";
		Path p = Paths.get(CommonCode.strFilteredRecipes);
		boolean bFileExists = Files.exists(p);
		
		XSSFWorkbook wb;

		//if file already exists, check for the last row number
		if(bFileExists) {
			FileInputStream myxls = new FileInputStream(CommonCode.strFilteredRecipes);
			wb = new XSSFWorkbook(myxls);
		    XSSFSheet sheet = wb.getSheetAt(sheetNo);
			int lastRow=sheet.getLastRowNum();	
			
			System.out.println("sheet no : " + sheetNo);
			System.out.println("last row : " + lastRow);
			
		    for(i=1;i<lastRow;i++){
		    	row = sheet.getRow(i);		    	
	    		cell = row.getCell(4);
	    		ingredient = cell.getStringCellValue().toLowerCase();	
		        bFound = ingredient.contains(allergy.toLowerCase());
		        if (bFound) {
	//	        	XSSFFont font = wb.createFont();
		//        	CellStyle cs = wb.createCellStyle();
		  //      	font.setColor(IndexedColors.RED.getIndex());
			//		font.setBold(true);
				//	cs.setFont(font);
				//	System.out.println(" B last cell no : " + row.getLastCellNum());

					/*if(row.getLastCellNum()<=10) {
						System.out.println("last cell no : " + row.getLastCellNum());
						cell = row.createCell(11);
						System.out.println("Added cell");
						cell.setCellValue("Allergies - ");
					}*/
				//	System.out.println("A last cell no : " + row.getLastCellNum());
					
					cell = row.getCell(11);
					String strPrevVal = cell.getStringCellValue();
					System.out.println("prev value : " + strPrevVal);

					//cell.setCellStyle(cs);
					cell.setCellValue(strPrevVal + ", " + allergy);
					System.out.println(row.getRowNum());
		        }
		    }
			FileOutputStream fileOut = new FileOutputStream(CommonCode.strFilteredRecipes);
			wb.write(fileOut);
			fileOut.close();
		    wb.close();
		}
	}
}
