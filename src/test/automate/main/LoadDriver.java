package test.automate.main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoadDriver {

	static WebDriver load(DriverType driverType) {
		if (driverType == DriverType.Chrome) {
			System.setProperty("webdriver.chrome.driver",
					"/Users/shababu/eclipse-workspace/MobiAutomate/drivers/chromedriver");
			return new ChromeDriver();
		}

		if (driverType == DriverType.Firfox) {
			System.setProperty("webdriver.gecko.driver",
					"/Users/shababu/eclipse-workspace/MobiAutomate/drivers/geckodriver");
			return new FirefoxDriver();
		}
		return null;
	}

	enum DriverType {
		Chrome,Firfox;
	}
}
