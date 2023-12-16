package pricerComaprison;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * @author barun
 */
public class CyclesUkScrapper extends Thread {

    //showStatus method to check if the connection is secured or not
    public void showStatus() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection to the database is successful.");
                connection.close(); // Close the connection when done
            } else {
                System.out.println("Failed to make a connection to the database.");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions appropriately
            System.out.println("Connection to the database failed. Error caused due to: " + e.getMessage());
        }
    }

    public void run() {

        // j is going to represent pageNumber
        for (int j = 1; j < 20; j++) {

            try {

                String URL = "https://www.cyclesuk.com/shop/bikes/?page=" + j;

                //connecting with the website and fetching Html content with get() method
                Document doc = Jsoup.connect(URL).get();

                //Section where the bikes are layed out
                Elements mainSection = doc.select(".product-card-custom");

                //Display the page number
                System.out.println("Page ." + j);
                System.out.println("");

                int size = mainSection.size();
                for (int i = 0; i < size; i++) {

                    //Data needed: productName, description, price,
                    //Individual section
                    Elements eachSection = mainSection.get(i).select(".product-card-custom");

                    //Product name
                    Elements productName = eachSection.select(".product-title");
                    String nameStr = productName.text();
                    String[] arrName = nameStr.split(" ");
                    String[] newArry = new String[arrName.length - 2];

                    String brandName = arrName[1];

                    for (int m = 0, n = 2; n < arrName.length; n++, m++) {
                        newArry[m] = arrName[n];
                    }

                    StringBuilder nameBuild = new StringBuilder();

                    for (String element : newArry) {
                        nameBuild.append(element).append(" ");
                    }

                    String name = nameBuild.toString().trim();

                 // product price
                  //In this website the prices are wrapped with discount so just extracting the current price
                    Elements priceSection = eachSection.select(".product-price");
                    String[] prices = priceSection.text().split(" ");

                    //in allmost all the price sections the first word is the price if not we use condotion
                    String stringPrice = prices[0];

                    if (stringPrice.equals("From")) {
                        stringPrice = prices[1];
                    }

                    // Removing non-numeric characters
                    String numString = stringPrice.replaceAll("[^0-9]", "");
                    // Converting string to a BigDecimal
                    int price = Integer.parseInt(numString) / 100;

                    //img element
                    Elements imgElement = eachSection.select("img");
                    //img url
                    String imgUrl = imgElement.attr("src");


                    //This is the Url that takes to the page where you get other data like description size color etc
                    Elements nextPageUrlElement = eachSection.select("a");
                    String nextPageUrl = "  https://www.cyclesuk.com" + nextPageUrlElement.attr("href");

                    Document nextPageDoc = Jsoup.connect(nextPageUrl).get();


                    Elements descriptionContainer = nextPageDoc.select("#product-description");
                    Elements paragraph = descriptionContainer.select("p");
                    //there are list of paragraph so we just take the paragraph we need by index
                    String description = paragraph.get(1).text();

                    //Add CyclesUK website's cycles to DB using Hibernate
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");
                    hibernate.addBike(name, description, brandName, imgUrl, price, nextPageUrl);
                    hibernate.shutDown();



                }//End of for loop over the page's list items

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Error:" + e.getMessage());
                }

            } catch (IOException e) {
                System.out.println("Exception caused: " + e.getMessage());
            }
        } //End of all available pages (page number: 69)
    }
}
