package pricerComaprison;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class JuliesCycles extends Thread {
    public void run() {

        for (int i = 1; i < 8; i++) {//This loop interates over page

            try {
                Document doc = Jsoup.connect("https://www.juliescycles.co.uk/shop/search/?s=bike&page=" + i).userAgent("Google website study for personal projet.").get();
                Elements mainSection = doc.select(".product-card-custom");
                int size = mainSection.size();

                //Display the page number
                System.out.println("Page ." + i);
                System.out.println("");


                for (int j = 0; j < size; j++) {//This loop interates over the items in a single page
                    Elements eachSection = mainSection.get(j).select(".product-card-custom");

                    //Image
                    Elements imgElem = eachSection.select("img");
                    String image = imgElem.attr("src");

                    //Bike brand
                    Elements brandElem = eachSection.select(".product-title a span");
                    String brandName = brandElem.text();

                    //Bike name
                    Elements nameDiv = eachSection.select(".product-title a");
                    String[] arr = nameDiv.text().split(" ");
                    String[] nameArry = new String[arr.length - 1];
                    String nameString = "";
                    for (int n = 0; n < nameArry.length; n++) {
                        nameArry[n] = arr[n];
                        nameString += nameArry[n] + " ";
                    }
                    String name = nameString.trim();

                    //Price
                    Elements priceElem = eachSection.select(".product-price");
                    String[] arrPrice = priceElem.text().split(" ");
                    String stringPrice = arrPrice[0].replaceAll("[^0-9]","");
                    int price = Integer.parseInt(stringPrice)/100;

                    //URL
                    Elements nextPageElem = eachSection.select("a");
                    String Url = "https://www.juliescycles.co.uk" + nextPageElem.attr("href");

                    Document nextPage = Jsoup.connect(Url).get();

                    //Description
                    Elements descriptionElem = nextPage.select("#product-description");
                    String description = descriptionElem.text();

                    //Add Julies website's cycles to DB using Hibernate
                    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                    Hibernate hibernate = (Hibernate) context.getBean("hibernate");
                    hibernate.addBike(name, description, brandName, image, price, Url);
                    hibernate.shutDown();

                }
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ec){
                    System.out.println("Error: "+ ec.getMessage());
                }


            } catch (IOException EXC) {
                System.out.println("Error: " + EXC.getMessage());
            }
        }
    }
}
