package pricerComaprison;

import java.io.IOException;
import java.lang.Integer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class TisoBikes extends Thread {

    public void run() {
        try {

            for (int i = 1; i < 8; i++) {  //loops over page

                Document doc = Jsoup.connect("https://www.tiso.com/cycling/bikes/page" + i + ".html")
                        .userAgent("Chrome, My computer Science project").get();
                Elements mainSection = doc.select(".productlist_list");

                Elements eachSectionCollection = mainSection.select("article");

                //Display the page number
                System.out.println("Page ." + i);
                System.out.println("");

                //This loop is for the all the available bikes in a single page
                int size = eachSectionCollection.size();
                for (int j = 0; j < size; j++) {
                    Element eachSection = eachSectionCollection.get(j);

                    //Brand name
                    Elements brandElem = eachSection.select(".brand");
                    String brandName = brandElem.text();

                    //Bike name
                    Elements nameElem = eachSection.select("h2 span:nth-child(2)");
                    String bikeNameStr = nameElem.text();
                    String[] newArry = bikeNameStr.split("-");
                    String bikeName = newArry[0];

                    //extracting price
                    Elements priceElem = eachSection.select(".product-info-holder>span:nth-child(2)");
                    String priceValue = priceElem.text();
                    String stringPrice = priceValue.replaceAll("[^0-9]","");
                    int price = Integer.parseInt(stringPrice)/100;

                    //extracting image
                    Elements imgElem = eachSection.select(".product-img-box .img-aurora");
                    String imgUrl = imgElem.attr("data-src");

                    //Moving inside the page from here
                    Elements nextPageUrl = eachSection.select("a");
                    String nextUrl = "https://www.tiso.com" + nextPageUrl.attr("href");

                    Document innerDoc = Jsoup.connect(nextUrl).get();

                    //Description
                    Elements descriptionElem = innerDoc.select(".product-copy-wrap");
                    String description = descriptionElem.text();

                    //Add TisoBikes website's cycles to DB using Hibernate
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");
                    hibernate.addBike(bikeName, description, brandName, imgUrl, price, nextUrl);
                    hibernate.shutDown();

                }

                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }
}

