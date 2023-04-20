package Recipes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

public class PCOS {
	@Test(priority = 2)
	private void LoadAZRecipes() throws InterruptedException, IOException {
		WebElement btnAZ = Common_Code.chromeDriver.findElement(By.xpath("//a[@title='Recipea A to Z']"));
		btnAZ.click();
		//LoadRecipes("Y", Common_Code.PCOSEliminate);
		LoadRecipes("U", Common_Code.PCOSEliminate, "PCOS");
		LoadRecipes("U", Common_Code.HyperTensionEliminate, "HTension");
	}
	
	private void LoadRecipes(String letter, String[] eliminate, String strMorbidity) 
					throws InterruptedException, IOException {
		WebElement btnAZ = Common_Code.chromeDriver.findElement(By.xpath("//a[text()='" + letter + "']"));
		btnAZ.click();
		List<WebElement> pages = Common_Code.chromeDriver.findElements
				//(By.xpath("//*[@class='respglink']"));
				(By.xpath("//*[contains(text(),'Goto')]/a"));
		int nCount = pages.size()/2;
		System.out.println("no of pages : " + nCount);
		int i=0;
		for(i=1;i<=nCount;i++) {
			System.out.println("page no. = " + i);
			WebElement page = Common_Code.chromeDriver.findElement
					//(By.xpath("//*[@class='respglink'][" + i + "]"));
					(By.xpath("//*[text()='" + i + "']"));
			page.click();
			ScanThruRecipes1(letter,eliminate, strMorbidity);
		}
	}
	
	private void ScanThruRecipes1(String foodCategory, String[] eliminate, String strMorbidity) 
					throws InterruptedException, IOException {
		Thread.sleep(2000);
		List<WebElement> recipeList = Common_Code.chromeDriver.findElements
					(By.xpath("//*[@class='rcc_recipecard']"));
		
		int i=0;
		int nRecipes = recipeList.size();
		
		XSSFRow row;
		XSSFCell cell;		
		String xPathName="",xPathID="", name="", pTime="", cTime="", id="";
		String  xPreparationMethod="", xNValues="", xIngredients="";
		String ingredients="", nutrientValues="", preparationMethod="", url="";
		System.out.println("No. of recipes : " + nRecipes);
		for(i=1;i<=nRecipes;i++) {
			boolean bFound = false;
	    	List<String> list = Arrays.asList(eliminate);
			
			xPathName = "//div[@class='rcc_recipecard'][" + i + "]/div[3]/span/a";
			xPathID = "//*[@class='rcc_recipecard'][" + i + "]/div[2]/span";
			
			xIngredients = "//*[@id='rcpinglist']";
			xPreparationMethod = "//*[@id='recipe_small_steps']";
			xNValues = "//*[@id='rcpnutrients']";
			name = Common_Code.chromeDriver.findElement(By.xpath(xPathName)).getText();
			System.out.println("name : " + name);
			for (String word: list ) {
			        bFound = name.contains(word);
			       if (bFound) {
			    	   System.out.println("Found in name!");
			    	   break;
			    	   }
			   }
			if (bFound){
				continue;
			}
			bFound = false;
			
			id = Common_Code.chromeDriver.findElement(By.xpath(xPathID)).getText();		
			Common_Code.chromeDriver.findElement(By.xpath(xPathName)).click();
			
			ingredients = Common_Code.chromeDriver.findElement(
					By.xpath(xIngredients)).getText();
			for (String word: list ) {
		        bFound = ingredients.contains(word);
		        if (bFound) {
			    	System.out.println("Found in ingredients!");
		        	break;
		    	   	}
			}
			if (bFound){
				Common_Code.chromeDriver.navigate().back();// switchTo().window(mainWindowHandle);
				continue;
			}
			bFound = false;

			pTime = Common_Code.chromeDriver.findElement(
						By.xpath("//div[@class='tags']/../p[2]/time[1]")).getText();
			cTime = Common_Code.chromeDriver.findElement(
						By.xpath("//div[@class='tags']/../p[2]/time[2]")).getText();
			//calories = chromeDriver.findElement(By.xpath(xPathCalories)).getText();
			preparationMethod = Common_Code.chromeDriver.findElement(By.xpath(xPreparationMethod)).getText();
			List<WebElement> listNV = Common_Code.chromeDriver.findElements(By.xpath(xNValues));
			if (listNV.size()>0) {
				nutrientValues = Common_Code.chromeDriver.findElement(By.xpath(xNValues)).getText();
			}
			else {
				nutrientValues = "no data";
			}
			url = Common_Code.chromeDriver.getCurrentUrl();
	
			if(!bFound) {
			Path p = Paths.get(Common_Code.strPCOSExcel);
			boolean bFileExists = Files.exists(p);
		    System.out.println("File exists : " + bFileExists); 
		    XSSFWorkbook wb;
			if(bFileExists) {
				FileInputStream myxls = new FileInputStream(Common_Code.strPCOSExcel);
			    wb = new XSSFWorkbook(myxls);
			    XSSFSheet sheet = wb.getSheetAt(0);
			    int lastRow=sheet.getLastRowNum();
			    System.out.println("last row : " + lastRow); 
			    row = sheet.createRow(++lastRow);
				}else {
					wb = new XSSFWorkbook();
					CreationHelper ch = wb.getCreationHelper();
					XSSFSheet sheet = wb.createSheet("Recipes");
					XSSFRow header = sheet.createRow(0);			
					for(int k=0;k<Common_Code.strHeading.length;k++) {
						CellStyle cs = wb.createCellStyle();
						XSSFFont font = wb.createFont();
						font.setColor(IndexedColors.BLUE.getIndex());
						font.setBold(true);
						cs.setFont(font);
						cs.setWrapText(true);
						cell = header.createCell(k);
						cell.setCellStyle(cs);
						cell.setCellValue(Common_Code.strHeading[k]);
					}
				    System.out.println("new file"); 
					row=sheet.createRow(1);
				}
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
			    
				FileOutputStream fileOut = new FileOutputStream(Common_Code.strPCOSExcel);
				wb.write(fileOut);
				fileOut.close();
				wb.close();
			}
		Common_Code.chromeDriver.navigate().back();// switchTo().window(mainWindowHandle);
		Thread.sleep(2000);
		}
	}
}
