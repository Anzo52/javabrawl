package main.java;

// Java program to crawl a website using chrome driver and return links and objects
// Initial URL is given as command line argument
// Output is written to a file and/or console, depending on the command line arguments
// Page is crawled using breadth first search


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class JavaBrawl {

    // method to launch headless chrome driver
    public static WebDriver launchChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    // method to load webpage, access DOM and find anchors and files (.jpg, .doc, .html, etc), 
    // returns href values of components
    public static List<String> getLinks(WebDriver driver, String url) {
        // use try-catch block to handle exceptions: if exception is thrown, store and return page error from server
        try {
            driver.get(url);
            List<WebElement> links = driver.findElements(By.tagName("a"));
            List<String> hrefs = new ArrayList<String>();
            for (WebElement link : links) {
                hrefs.add(link.getAttribute("href"));
            }
            return hrefs;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    // method to filter links: removes links to same page, links to non-html files, and external links
    public static List<String> filterLinks(List<String> links) {
        List<String> filteredLinks = new ArrayList<String>();
        for (String link : links) {
            if (link.contains(".html") && !link.contains("#") && !link.contains("mailto")) {
                filteredLinks.add(link);
            }
        }
        return filteredLinks;
    }

    // main
    public static void main(String[] args) {

        // check if command line arguments are valid
        if (args.length != 2) {
            System.out.println("Usage: JavaBrawl <url> <output file>");
            System.exit(1);
        }

        // start chrome driver
        WebDriver driver = launchChromeDriver();

        // add url to list of links to crawl
        List<String> links = new ArrayList<String>();
        links.add(args[0]);

        // add url to list of visited links
        List<String> visitedLinks = new ArrayList<String>();

        // load webpage
        List<String> hrefs = getLinks(driver, args[0]);

        // get links from webpage
        if (hrefs != null) {
            links.addAll(hrefs);
        }

        // filter links
        links = filterLinks(links);

        // add filtered links to list of links to crawl
        for (String link : links) {
            if (!visitedLinks.contains(link)) {
                visitedLinks.add(link);
            }
        }

        // remove url from list of links to crawl
        links.remove(args[0]);

        // repeat until list of links to crawl is empty
        while (!links.isEmpty()) {

            // get links from webpage
            hrefs = getLinks(driver, links.get(0));

            // get links from webpage
            if (hrefs != null) {
                links.addAll(hrefs);
            }

            // filter links
            links = filterLinks(links);

            // add filtered links to list of links to crawl
            for (String link : links) {
                if (!visitedLinks.contains(link)) {
                    visitedLinks.add(link);
                }
            }

            // remove url from list of links to crawl
            links.remove(links.get(0));
        }

        // write links to file and/or console
        try {
            FileWriter writer = new FileWriter(new File(args[1]));
            for (String link : visitedLinks) {
                writer.write(link + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // close driver
        driver.close();
    }
}