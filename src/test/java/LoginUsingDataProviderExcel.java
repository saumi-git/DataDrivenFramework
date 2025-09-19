import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

public class LoginUsingDataProviderExcel {
    WebDriver driver;

    @BeforeMethod
    public void setUp() throws InterruptedException {
        driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

    }

    @DataProvider(name="LoginData")
    public String[][] getExcelData() throws IOException {
        FileInputStream file=new FileInputStream(System.getProperty("user.dir")+"\\testData\\Book1.xlsx");
        XSSFWorkbook workbook=new XSSFWorkbook(file);
        XSSFSheet sheet1=workbook.getSheet("Sheet1");
        int totalRows=sheet1.getLastRowNum();
        int totalColumns=sheet1.getRow(0).getLastCellNum();
        System.out.println("Total row count : "+totalRows);
        System.out.println("Total coulmn count : "+totalColumns);

        String[][] testData=new String[totalRows][totalColumns];
        for (int r=1;r<=totalRows;r++){
            for (int c=0;c<totalColumns;c++){
                testData[r-1][c]=sheet1.getRow(r).getCell(c).toString();
            }
        }
        workbook.close();
        file.close();
        return testData;
    }

    @Test(dataProvider = "LoginData")
    public void loginWithUsernamePassword(String uname,String pass,String expValidation) throws InterruptedException {
        WebElement username=driver.findElement(By.name("username"));
        username.sendKeys(uname);

        WebElement password=driver.findElement(By.name("password"));
        password.sendKeys(pass);

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(3000);

     //   WebElement dashboardText=driver.findElement(By.linkText("Dashboard"));
//        WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(3));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Dashboard")));


        boolean urlVerification=driver.getCurrentUrl().contains("dashboard");
        if (expValidation.equals("valid")){
            Assert.assertTrue(urlVerification,"Expected login success,but not navigated to dashboard");
        }else {
            Assert.assertFalse(urlVerification,"Expected login failed,but navigated to dashboard");
        }

    }

    @AfterMethod
    public void closeBrowser(){
        driver.quit();
    }

}
