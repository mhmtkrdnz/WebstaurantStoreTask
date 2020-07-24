package com.automation.tests;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.sql.Driver;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchProductTest {

    @Test
    public void search() throws InterruptedException {
        //setting up chrome driver
        WebDriverManager.chromedriver().setup();

        //creating driver object
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        //navigate to website
        driver.get("https://www.webstaurantstore.com/");

        //locate searchbox
        WebElement searchBox = driver.findElement(By.id("searchval"));

        //search for product name
        searchBox.sendKeys("stainless work table");

        //find search button and click
        driver.findElement(By.xpath("//button[text()='Search']")).click();

        int pageNumber =1;
        SoftAssert softAssert = new SoftAssert();

        while(true){
                //find all items and store in List
                List<WebElement> items = driver.findElements(By.xpath("//a[@data-testid='itemDescription']"));

                //verify that each item has 'table' in the title
                for (int i = 0; i < items.size(); i++) {

                    softAssert.assertTrue(items.get(i).getText().contains("Table"),"Table not found page "+pageNumber+" itemNumber "+(i+1));
                }
                //find next page right arrow
                WebElement nextPage = driver.findElement(By.xpath("//i[@class='icon-right-open']"));
                //scroll to that next page element
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",nextPage );
                //click next page
                nextPage.click();

                Thread.sleep(1000);
                pageNumber++;

                //check next arrow has class attribute disabled to find there is no more page available
                Boolean disabledNextPage = driver.findElements(By.xpath("//i[@class='icon-right-open']/../../../li[@class='active']/following-sibling::li[@class='disabled']")).size()>0;
                    if (disabledNextPage){

                        List<WebElement> itemsLastPage = driver.findElements(By.xpath("//a[@data-testid='itemDescription']"));

                        //verify that each item has 'table' in the title
                        for (int i = 0; i < items.size(); i++) {
                            //I used soft assertion to check all pages and all items
                            softAssert.assertTrue(itemsLastPage.get(i).getText().contains("Table"),"Table not found page "+pageNumber+" itemNumber "+(i+1));

                        }

                        System.out.println("no more page");
                        break;
                    }


            }

        //find last product Add to Cart button
        List<WebElement> addCartButtons = driver.findElements(By.xpath("//input[@data-testid='itemAddCart']"));
        //save the total number of item in the last page
        int lastItem = addCartButtons.size();
        //save last item
        WebElement lastButton = addCartButtons.get(lastItem-1);
        //scroll to element
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",lastButton );
        //click last add to cart
        lastButton.click();

        //scroll to view cart button
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",driver.findElement(By.xpath("//a[.='View Cart']")) );

        //click view card
        driver.findElement(By.xpath("//a[.='View Cart']")).click();

        //click empty cart
        driver.findElement(By.xpath("//a[.='Empty Cart']")).click();

        Thread.sleep(2000);

        driver.findElement(By.xpath("//button[.='Empty Cart']")).click();

        Thread.sleep(1000);


        String emptyMessage = driver.findElement(By.xpath("//div[@class='empty-cart__text']/p[1]")).getText();
        //verify cart is empty
        Assert.assertEquals(emptyMessage,"Your cart is empty.");


        //print soft assert messages
         softAssert.assertAll();

         //close browser
       driver.quit();
    }

}
