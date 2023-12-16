package pricerComaprison;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class EvansScrapper extends Thread {
    
    public void run() {

        try {
            for (int j = 1; j < 8; j++) {
                Document doc = Jsoup.connect("https://www.evanscycles.com/searchresults?descriptionfilter=bike#dcp=" + j + "&dppp=60&OrderBy=rank").get();
                //  Document doc = Jsoup.connect(s).get();

                //section where bikes are layed out
                Elements allSection = doc.select(".s-productthumbbox");

                //Display the page number
                System.out.println("Page ." + j);
                System.out.println("");

                int size = allSection.size();
                for (int i = 0; i < size; i++) {

                    //div for the individual product
                    Elements eachSection = allSection.get(i).select(".s-productthumbbox");

                    //brand name
                    Elements brandNameElem = eachSection.select(".brandWrapTitle");
                    String brandName = brandNameElem.text();

                    //product name
                    Elements productNameElem = eachSection.select(".productdescriptionname");
                    String bikeName = productNameElem.text();

                    //product Image
                    Elements imgElem = eachSection.select("img");
                    String imgUrl = imgElem.attr("src");

                    //product price
                    Elements productPrice = eachSection.select(".s-largered");
                    String priceInString = productPrice.text().replaceAll("[^0-9]","");
                    int price = Integer.parseInt(priceInString)/100;

                    //Navigating to next page for more data
                    Elements productLinkElem = eachSection.select("a");
                    String modelLink = "https://www.evanscycles.com" + productLinkElem.attr("href");

                    Document modelPageDoc = Jsoup.connect(modelLink).get();
                    //Description
                    String description = modelPageDoc.select("span.infoPageDescription:not(#DisplayAttributes)").text();

                    //Add EvansScrapper website's cycles to DB using Hibernate
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");
                    hibernate.addBike(bikeName, description, brandName, imgUrl, price, modelLink);
                    hibernate.shutDown();
                }

                try{
                    sleep(1000);
                }catch(InterruptedException ex){
                    System.out.println("Error: "+ex.getMessage());
                }
            }
        } catch (IOException err) {
            System.out.println("Error: " + err.getMessage());
            System.out.println("Couldn't get the data");
        }
    }
}


