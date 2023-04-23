package Recipes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class FilteredRecipes {
	@Test(priority = 2)
	private void LoadAZRecipes() throws InterruptedException, IOException {
		WebElement btnAZ = CommonCode.chromeDriver.findElement(By.xpath("//a[@title='Recipea A to Z']"));
		btnAZ.click();
		LoadRecipes("O", CommonCode.lstDiabetes, "Diabetes", 0);
		LoadRecipes("O", CommonCode.lstHypothyroidism, "Hypothyroidism", 1);
		LoadRecipes("O", CommonCode.lstHypertension, "Hypertension", 2);
		LoadRecipes("O", CommonCode.lstPCOS, "PCOS", 3);
		CommonCode.CheckForHealthyItems(CommonCode.lstHDiabetes, 0);
		CommonCode.CheckForHealthyItems(CommonCode.lstHHThyroidism, 1);
		CommonCode.CheckForHealthyItems(CommonCode.lstHHTension, 2);
		CommonCode.CheckForHealthyItems(CommonCode.lstHPCOS, 3);
		
		for(int j=0;j<CommonCode.lstAllergies.size();j++) {
			for(int i=0;i<4;i++) {
				CommonCode.CheckForAllergy(CommonCode.lstAllergies.get(j), i);
			}
		}
	}
	
	//load the recipes according to a letter and for a particular morbidity
	private void LoadRecipes(String letter, List<String> eliminate, String strMorbidity, int sheetNo) 
					throws InterruptedException, IOException {
		WebElement btnAZ = CommonCode.chromeDriver.findElement(By.xpath("//a[text()='" + letter + "']"));
		btnAZ.click();
		List<WebElement> pages = CommonCode.chromeDriver.findElements
				(By.xpath("//*[contains(text(),'Goto')]/a"));
		int nCount = pages.size()/2;
		System.out.println("no of pages : " + nCount);
		int i=0;
		nCount = 2;
		//loop through the pages and check each recipe
		for(i=1;i<=nCount;i++) {
			System.out.println("page no. = " + i);
			WebElement page = CommonCode.chromeDriver.findElement
					(By.xpath("//*[text()='" + i + "']"));
			page.click();
			ScanThruRecipes1(letter,eliminate, strMorbidity, sheetNo);
		}
	}
	
	//scan through each recipe and find whether the recipe has any ingredients from the eliminated ingredients
	//if the recipe name or ingredients do not have any eliminated ingredient, the recipe details are saved in the excel
	private void ScanThruRecipes1(String foodCategory, List<String> list, String strMorbidity, int sheetNo) 
					throws InterruptedException, IOException {
		Thread.sleep(2000);
		//find the number of recipes on the page
		List<WebElement> recipeList = CommonCode.chromeDriver.findElements
					(By.xpath("//*[@class='rcc_recipecard']"));	
		int i=0;
		int nRecipes = recipeList.size();
		XSSFRow row;
		XSSFCell cell;		
		String xPathName="",xPathID="", name="", pTime="", cTime="", id="";
		String  xPreparationMethod="", xNValues="", xIngredients="";
		String ingredients="", nutrientValues="", preparationMethod="", url="";
		//System.out.println("No. of recipes : " + nRecipes);
		//nRecipes=2;
		//Scan through all the recipes on the page
		for(i=1;i<=nRecipes;i++) {
			//bFound is a flag to check if the recipe has any eliminated ingredient
			boolean bFound = false;
	 
			//List<String> list = Arrays.asList(eliminate);	
			xPathName = "//div[@class='rcc_recipecard'][" + i + "]/div[3]/span/a";
			xPathID = "//*[@class='rcc_recipecard'][" + i + "]/div[2]/span";
			
			xIngredients = "//*[@id='rcpinglist']";
			xPreparationMethod = "//*[@id='recipe_small_steps']";
			xNValues = "//*[@id='rcpnutrients']";
			//id = CommonCode.chromeDriver.findElement(By.xpath(xPathID)).getText();		
			List<WebElement> ids = CommonCode.chromeDriver.findElements(By.xpath(xPathID));
			if(ids.size()>0) {
				id = ids.get(0).getText();
			}
			//System.out.println("id done...");
			
			name = CommonCode.chromeDriver.findElement(By.xpath(xPathName)).getText();
			//System.out.println("name : " + name);
			//check if the name of the recipe has an eliminated ingredient
			//if yes, set the bFound to true
			for (String word: list ) {
		        bFound = name.toLowerCase().contains(word.toLowerCase());
		        if (bFound) {
		        	System.out.println("Found in name! - " + word);
		        	break;
		    	   	}
		   	}
			
			//if eliminated ingredients found in the name, continue through the loop, 
			//don't go ahead for saving in the excel
			if (bFound){
				continue;
			}
			bFound = false;
			
			//if eliminated ingredients not found in the name, open the recipe details page 
			CommonCode.chromeDriver.findElement(By.xpath(xPathName)).click();	
			Thread.sleep(2000);
			ingredients = CommonCode.chromeDriver.findElement(
					By.xpath(xIngredients)).getText();
			for (String word: list ) {
		        bFound = ingredients.toLowerCase().contains(word.toLowerCase());
		        if (bFound) {
			    	System.out.println("Found in ingredients! - " + word);
		        	break;
		    	   	}
			}
			
			//if eliminated ingredients found in the ingredients of the recipe, continue through the loop
			//don't go ahead for saving in the excel
			if (bFound){
				CommonCode.chromeDriver.navigate().back();// switchTo().window(mainWindowHandle);
				continue;
			}
			bFound = false;
			
			//You have reached here, means the recipe does not contain any eliminated ingredients
			//scrape the recipe details
			
			List<WebElement> lstPTime1 = CommonCode.chromeDriver.findElements(
					By.xpath("//div[@class='tags']/../p[2]/time[1]"));
			List<WebElement> lstPTime2 = CommonCode.chromeDriver.findElements(
					By.xpath("//*[@itemprop='prepTime']"));
			
			if(lstPTime1.size()>0) {
				pTime = lstPTime1.get(0).getText();
			}
			else if(lstPTime2.size()>0){
				pTime = lstPTime2.get(0).getText();

			}
			
			List<WebElement> lstCTime1 = CommonCode.chromeDriver.findElements(
					By.xpath("//div[@class='tags']/../p[2]/time[2]"));
			List<WebElement> lstCTime2 = CommonCode.chromeDriver.findElements(
					By.xpath("//*[@itemprop='cookTime']"));
			
			if(lstCTime1.size()>0) {
				cTime = lstCTime1.get(0).getText();
			}
			else if(lstCTime2.size()>0){
				cTime = lstCTime2.get(0).getText();
			}
			
			//calories = chromeDriver.findElement(By.xpath(xPathCalories)).getText();
			preparationMethod = CommonCode.chromeDriver.findElement(By.xpath(xPreparationMethod)).getText();
			
			//some recipes do not have nutrition value chart, so check if it's there
			List<WebElement> listNV = CommonCode.chromeDriver.findElements(By.xpath(xNValues));
			if (listNV.size()>0) {
				nutrientValues = CommonCode.chromeDriver.findElement(By.xpath(xNValues)).getText();
			}
			else {
				nutrientValues = "no data";
			}
			url = CommonCode.chromeDriver.getCurrentUrl();
			
			//Now it's tome to save the recipe in the excel
			if(!bFound) {
			Path p = Paths.get(CommonCode.strFilteredRecipes);
			boolean bFileExists = Files.exists(p);
			//System.out.println("path: " + p.toString());
		    //System.out.println("File exists : " + bFileExists); 
		    XSSFWorkbook wb;
		    //if file already exists, check for the last row number
			if(bFileExists) {
				FileInputStream myxls = new FileInputStream(CommonCode.strFilteredRecipes);
				wb = new XSSFWorkbook(myxls);
				
			    XSSFSheet sheet = wb.getSheetAt(sheetNo);
			    int lastRow=sheet.getLastRowNum();
			    //System.out.println("last row : " + lastRow); 
			    row = sheet.createRow(++lastRow);
				}
			
			//if file doesn't exist, create a new file and add headers as the first row
			else {
				wb = new XSSFWorkbook();
				CreationHelper ch = wb.getCreationHelper();
				
				if (wb.getNumberOfSheets()<=1) {
					wb.createSheet("Diabetes");
					wb.createSheet("Hypothyroidism");
					wb.createSheet("Hypertension");
					wb.createSheet("PCOS");					
				}
				for(int z = 0; z<4; z++) {
					XSSFSheet sheet = wb.getSheetAt(z);
					XSSFRow header = sheet.createRow(0);			
					for(int k=0;k<CommonCode.strHeading.length;k++) {
						CellStyle cs = wb.createCellStyle();
						XSSFFont font = wb.createFont();
						font.setColor(IndexedColors.BLUE.getIndex());
						font.setBold(true);
						cs.setFont(font);
						cs.setWrapText(true);
						cell = header.createCell(k);
						cell.setCellStyle(cs);
						cell.setCellValue(CommonCode.strHeading[k]);
					}
				}
				XSSFSheet sheet = wb.getSheet(strMorbidity);

			    //System.out.println("new file"); 
				row=sheet.createRow(1);
			}
			
			//add recipe details to the newly added row
			cell=row.createCell(0);
			cell.setCellValue(id);
			cell=row.createCell(1);
			cell.setCellValue(name);
			cell=row.createCell(2);
			cell.setCellValue("Vegetarian");
			cell=row.createCell(3);
			cell.setCellValue(foodCategory);
			cell=row.createCell(4);
			cell.setCellValue(ingredients);
			cell=row.createCell(5);
			cell.setCellValue(pTime);
			cell=row.createCell(6);
			cell.setCellValue(cTime);
			cell=row.createCell(7);
			cell.setCellValue(preparationMethod);
			cell=row.createCell(8);
			cell.setCellValue(nutrientValues);
			cell=row.createCell(9);
			cell.setCellValue(strMorbidity);
			cell=row.createCell(10);
			cell.setCellValue(url);
			cell=row.createCell(11);
			cell.setCellValue("");
			//commit the newly added row to the excel and save the excel file
			FileOutputStream fileOut = new FileOutputStream(CommonCode.strFilteredRecipes);
			wb.write(fileOut);
			fileOut.close();
			wb.close();
		}
			
		//move back from the recipe details page to the recipe list page 
		CommonCode.chromeDriver.navigate().back();// switchTo().window(mainWindowHandle);
		Thread.sleep(2000);
		}
	}
}
