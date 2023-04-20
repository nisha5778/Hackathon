package Recipes;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class Common_Code {
	public static WebDriver chromeDriver;
	
	@Test(priority = 1)
	private void LoadPage() throws InterruptedException, IOException {
		
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.setAcceptInsecureCerts(true);
		optChrome.addArguments("--remote-allow-origins=*");
		chromeDriver = new ChromeDriver(optChrome);
		//chromeDriver = new FirefoxDriver();
		chromeDriver.get("https://tarladalal.com/");		
		//chromeDriver.manage().window().maximize();
	}
}
