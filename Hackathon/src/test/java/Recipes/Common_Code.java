package Recipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class Common_Code {
	public static WebDriver chromeDriver;
	public static String strPCOSExcel = "C:/NumpyNinja_Projects/Hackathon/PCOS.xlsx";
	public static String[] PCOSEliminate = {"Cake", "Pastries","White bread", "Fried", "Pizza", "Burger","Carbonated beverages",
			 "sweet", "icecream","soda","juice","Red meat", "Processed meat", "Dairy",
			 "Soy", "Gluten" ,"Pasta", "White rice", "Doughnut", "Fries", "Coffee", "Seed oils",
			"vegetable oil", "soybean oil", "canola oil", "rapeseed oil", "sunflower oil", "safflower oil"};
	
	public static String[] HyperTensionEliminate = {"chips","pretzels","crackers", "coffee", "tea", "soft drink", 
			"Alcohol", "Frozen food", "meat", "bacon", "ham", "Pickles", "Processed", "canned",
			"Fried", "Sauce", "mayonnaise", "sausage","White rice","white bread", "pav"};

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
			"Recipe URL"
			};
	@Test(priority = 1)
	private void LoadPage() throws InterruptedException, IOException {		
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.setAcceptInsecureCerts(true);
		optChrome.addArguments("--remote-allow-origins=*");
		ReadEliminationsFromExcel();
		chromeDriver = new ChromeDriver(optChrome);
		//chromeDriver = new FirefoxDriver();
		chromeDriver.get("https://tarladalal.com/");	
		//chromeDriver.manage().window().maximize();
	}
	
	//@Test
	public void ReadEliminationsFromExcel() throws IOException
	{
		System.out.print(System.getProperty("user.dir"));
		
		String path = System.getProperty("user.dir")+"\\IngredientsAndComorbidities-ScrapperHackathon.xlsx";
		File excelfile = new File(path);
		FileInputStream fis = new FileInputStream(excelfile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("EliminatedItems");
	    XSSFRow row=sheet.getRow(0);
	    XSSFCell cell=row.getCell(0);
	    String strItem = cell.getStringCellValue();
	    System.out.println(strItem);
	    workbook.close();		
	}

}
