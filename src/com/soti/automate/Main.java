package com.soti.automate;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.http.util.TextUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.soti.automate.LoadDriver.DriverType;

public class Main {

	private static WebDriver webDriver;

	private static void waitUntilProfileLoaded() {
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//TD[@class='text-ellipsis'][text()='WasteQABuild_Price_hardcoded']")));
		WebElement element = webDriver
				.findElement(By.xpath("//TD[@class='text-ellipsis'][text()='WasteQABuild_Price_hardcoded']"));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	private static boolean isProfileShowingNow() {
		try {
			((JavascriptExecutor)webDriver).executeScript("arguments[0].scrollIntoView(true);", webDriver.findElement(By.xpath(Config.PROFILE_XPATH)));
			return true;
		} catch (Exception exc) {
			return false;
		}
	}

	private static void waitForProfileToLoad() {
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Config.PROFILE_XPATH)));
		WebElement element = webDriver.findElement(By.xpath(Config.PROFILE_XPATH));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	private static void rightClickOnDesiredProfile() {
		WebElement element = webDriver.findElement(By.xpath(Config.PROFILE_XPATH));
		Actions action = new Actions(webDriver);
		action.moveToElement(element);
		action.contextClick(element).build().perform(); /* this will perform right click */
		WebElement elementAssign = webDriver .findElement(By.linkText("Assign Profile")); /* This will select menu after right click */
		elementAssign.click();
	}

	private static void waitForStoreListToOpen() {
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//SPAN[@unselectable='on'][text()='2001 PITTODRIE EXPRESS']")));
		WebElement element = webDriver.findElement(By.xpath(Config.PROFILE_XPATH));
		wait.until(ExpectedConditions.visibilityOf(element));
		try { Thread.sleep(2000); } catch (Exception exc) { }
	}

	private static void startAssignment() {
		try {
			File f = new File(Config.EXCEL_FILE_TO_BE_DEPLOYED);
			Workbook wb = WorkbookFactory.create(f);
			Sheet mySheet = wb.getSheetAt(0);
			TreeSet<String> storeSet = new TreeSet<>();
			for (Iterator<Row> rowIterator = mySheet.rowIterator(); rowIterator.hasNext();) {
				for (Iterator<Cell> cellIterator = ((Row) rowIterator.next()).cellIterator(); cellIterator.hasNext();) {
					String row = cellIterator.next().toString();
					if (row.length() < 4)
						continue;
					storeSet.add(row.substring(0, 4));
				}
			}

			// Assignment starts
			for (String storeNumber : storeSet) {
				String storeDetails = getStoreDetails(storeNumber);
				if (!TextUtils.isEmpty(storeDetails)) {
					WebElement element = null;
					try {
						element = webDriver.findElement(By.xpath("//div[@*[name()='ext:tree-node-id']=\"\\\\TESCO PLC\\1. UK Production\\Store Stock\\" + storeDetails + "\"]/img[3]"));
						if (Config.IS_ASSIGN) {
							if (element.getAttribute("class").contains("checkbox-0")) { // If checkbox is unchecked, then check
								((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
								try { Thread.sleep(500); } catch (Exception exc) { }
								element.click();
								System.out.println(storeDetails);
							} else {
								System.out.println(storeDetails + " is already checked"); // Log error: If checkbox is already checked
							}
						} else {
							if (!element.getAttribute("class").contains("checkbox-0")) { // If checkbox is checked, then un-check
								((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);",
										element);
								try {
									Thread.sleep(500);
								} catch (Exception exc) {
								}
								element.click();
								System.out.println(storeDetails);
							} else {
								System.out.println(storeDetails + " is already un-checked"); // Log error: If checkbox is already unchecked
							}
						}
					} catch (org.openqa.selenium.WebDriverException e) {
						try {
							if (Config.IS_ASSIGN) {
								if (element.getAttribute("class").contains("checkbox-0")) { // If checkbox is unchecked, then check
									try { Thread.sleep(500); } catch (Exception exc) { }
									element.click();
									System.out.println(storeDetails);
								} else {
									System.out.println(storeDetails + " is already checked"); // Log error: If checkbox is already checked
								}
							} else {
								if (!element.getAttribute("class").contains("checkbox-0")) { // If checkbox is checked, then un-check
									try { Thread.sleep(500); } catch (Exception exc) { }
									element.click();
									System.out.println(storeDetails);
								} else {
									System.out.println(storeDetails + " is already un-checked"); // Log error: If checkbox is already unchecked
								}
							}
						} catch (Exception ex) {
							System.out.println("Assignment Error : " + storeDetails);
						}
					}
					try { Thread.sleep(100); } catch (Exception exc) { }
				} else {
					System.out.println("Assignment Error : " + storeNumber);
				}
			}
		} catch (Exception e) {
			System.out.println("exception while assigning: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static String getStoreDetails(String storeNumber) {
		String storeDetails = "";
		try {
			File f = new File("/Users/shababu/eclipse-workspace/MobiAutomate/files/stores_list.xls");
			Workbook wb = WorkbookFactory.create(f);
			Sheet mySheet = wb.getSheetAt(0);
			for (Iterator<Row> rowIterator = mySheet.rowIterator(); rowIterator.hasNext();) {
				for (Iterator<Cell> cellIterator = ((Row) rowIterator.next()).cellIterator(); cellIterator.hasNext();) {
					String temp = ((Cell) cellIterator.next()).toString();
					if (storeNumber.equals(temp.substring(0, 4))) {
						return temp;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
			e.printStackTrace();
		}
		return storeDetails;
	}

	public static void main(String[] args) {
		WebElement element = null;
		webDriver = LoadDriver.load(DriverType.Chrome);
		webDriver.get("https://mdmmgmt.global.tesco.org/MobiControl/WebConsole/Home");
		webDriver.findElement(By.id("userName")).sendKeys(Config.LOGIN_USERNAME);
		webDriver.findElement(By.id("password")).sendKeys(Config.LOGIN_PASSWORD);
		webDriver.findElement(By.id("btn-login")).click();

		System.out.println("Login is in progress...");
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ext-gen746")));
		element = webDriver.findElement(By.id("ext-gen746"));
		wait.until(ExpectedConditions.visibilityOf(element));

		// Click on profile tab
		/*
		 * try { Thread.sleep(1000); } catch (Exception exc) { } element =
		 * webDriver.findElement(By.
		 * xpath("//SPAN[@class='x-tab-strip-text i-tab i-tab-profiles'][text()='Profiles']"
		 * )); element.click();
		 */
		System.out.println("Dashboard is loaded and profiles are getting loaded...");

		waitUntilProfileLoaded();
		System.out.println("Profiles are loaded");

		if (!isProfileShowingNow()) {
			String keyWord = Config.PROFILE_XPATH.substring(Config.PROFILE_XPATH.lastIndexOf('-') + 1, Config.PROFILE_XPATH.length());
			keyWord = keyWord.substring(0, (keyWord.contains("v") ? keyWord.lastIndexOf("v") : keyWord.lastIndexOf("V")) - 1);
			webDriver.findElement(By.id("ext-comp-1198")).sendKeys(keyWord);
			System.out.println("Desired profile not visible and is getting loaded...");
			waitForProfileToLoad();
		}
		System.out.println("Desired profile is loaded.");

		rightClickOnDesiredProfile();
		System.out.println("Assignment popup is open.");

		waitForStoreListToOpen();
		System.out.println("Store list expanded for assignment...");

		try { Thread.sleep(1000); } catch (Exception exc) { }

		System.out.println("Assignment is started...\n");
		startAssignment();
		
		System.out.println("Assignment completed.");
		// webDriver.close();
	}
}