import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TakeScreenShot {

    private final Logger LOGGER = Logger.getLogger(TakeScreenShot.class);
    WebDriver driver = new ChromeDriver();
    File srcFile;
    List<File> fileList = new ArrayList<File>();


    @BeforeMethod
    public void setUp(){
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.get("https://www.onliner.by/");
    }

    @AfterMethod
    public void tearDown(){
        driver.close();
    }

    @Test
    public void takeScreenShot() throws IOException {

        int counterIMG = 0;
        Long pScrollHeight = totalPageSize();
        Long pTotalHeight = pageTotalHeight();
        Long winInnerHeight = winInnerHeight();

        while (pTotalHeight < pScrollHeight) {
            fileList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
            srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile,
                    new File("d:\\Project\\TakeShot\\src\\test\\resources\\screenShots\\" + counterIMG + ".png"));
            counterIMG++;
            scrollPageDown();
            //pressPageDown();
            pTotalHeight = pageTotalHeight();
            pScrollHeight = totalPageSize();
            System.out.println(fileList.toString());
            System.out.println("scroll height - " + (pScrollHeight - 1));
            System.out.println("total height - " + pTotalHeight);
            System.out.println("winInnerHeigth -" + winInnerHeight);
        }


        BufferedImage joinedImage = joinImagesWithURL(fileList);

        File final_Image = new File("d:\\Project\\TakeShot\\src\\test\\resources\\screenShots\\finale.png");
        ImageIO.write(joinedImage, "png", final_Image);


        //       onceMoreScroll();

    }

    Long totalPageSize() {
       return  (Long)((JavascriptExecutor)driver).executeScript("return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight)");
    }

    Long pageTotalHeight() {
        return  (Long)((JavascriptExecutor)driver).executeScript("return window.scrollY + window.innerHeight");
    }

    Long winInnerHeight() {
        return (Long)((JavascriptExecutor)driver).executeScript("return window.innerHeight");
    }

    void pressPageDown(){
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
    }

    void scrollPageDown(){
        ((JavascriptExecutor)driver).executeScript("window.scrollBy(window.scrollY, window.innerHeight)");
    }

    void onceMoreScroll() throws IOException {
        Long pageSize = totalPageSize();
        Long windowSize = winInnerHeight();
        long fullScrollCount = pageSize / windowSize;
        Long pieceOfScroll = pageSize % windowSize;
        for (int i = 0; i <= fullScrollCount - 1; i++) {
            fileList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
            srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(srcFile,
                        new File("d:\\Project\\TakeShot\\src\\test\\resources\\screenShots\\" + i + ".png"));
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(window.scrollY, window.innerHeight)");
        }
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(window.scrollY, arguments[0])", pieceOfScroll);
        fileList.add(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
        srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    }



    public BufferedImage joinImagesWithURL(List<File> fList) throws IOException {

        List<BufferedImage> buffImgList = new ArrayList<BufferedImage>();

        for(File bImage: fList) buffImgList.add(ImageIO.read(bImage));

        int width = buffImgList.get(0).getWidth();
        int height = buffImgList.get(0).getWidth() * buffImgList.size();
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = newImage.createGraphics();
        g2.drawImage(urlToImage(driver.getCurrentUrl(), buffImgList), null, 0,0);
        g2.drawImage(buffImgList.get(0), null, 0, newImage.getHeight());
        for(int i = 1; i <=buffImgList.size(); i++){
            g2.drawImage(buffImgList.get(i), null, 0, newImage.getHeight());
        }
        g2.dispose();
        return newImage;
    }


    BufferedImage urlToImage(String url, List<BufferedImage>  bufferedImages) throws IOException {
        BufferedImage urlImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = urlImg.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        //int width = fm.stringWidth(url);
        int width = bufferedImages.get(0).getWidth();
        int height = fm.getHeight();
        g2d.dispose();

        urlImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = urlImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(url, 0, fm.getAscent());
        g2d.dispose();

       return urlImg;
    }

}
