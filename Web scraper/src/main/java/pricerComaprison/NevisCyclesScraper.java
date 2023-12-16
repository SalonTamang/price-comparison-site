package pricerComaprison;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.io.IOException;

public class NevisCyclesScraper extends Thread {

    public void run() {
        try {

            Document doc = Jsoup.connect("https://www.neviscycles.com/shop/products?sort=relevance-score&q=bikes&categories%5B%5D=bikes&showing=360").get();
            Elements mainSection = doc.select("#product-list .thumbnail-product");
            int size = mainSection.size();

            for (int i = 0; i < 355; i++) {//In this website all 360 bikes are in one page so just looping over each
                Elements eachSection = mainSection.get(i).select(".thumbnail-product");

                //BrandName
                Elements brandNameElem = eachSection.select("a:not(.thumbnail) strong");
                String brandName = brandNameElem.text();

                //Bike name
                Elements bikeNameElem = eachSection.select("a:not(.thumbnail)");
                String bikeName = bikeNameElem.text();

                //Bike price
                Elements priceElem = eachSection.select("p.lead-price");
                String stringPriceElem = priceElem.text();
                String stringPrice = stringPriceElem.replaceAll("[^0-9]", "");
                int price = Integer.parseInt(stringPrice) / 100;

                //Next page URL
                Elements nextPageElem = eachSection.select("a");
                String url = nextPageElem.attr("href");

                //nextPage element give the link to next page

                //Image section
                Document nextPage = Jsoup.connect(url).get();
                Elements imgElem = nextPage.select(".thumbnail-product img");
                String imgUrl = imgElem.attr("src");

                //Description section
                Elements descriptionElem = nextPage.select(".text-user");
                String description = descriptionElem.text();

                //Add NevisCycles website's cycles to DB using Hibernate
                ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
                Hibernate hibernate = (Hibernate) context.getBean("hibernate");
                hibernate.addBike(bikeName, description, brandName, imgUrl, price, url);
                hibernate.shutDown();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Error:" + e.getMessage());
                }
            }

        } catch (IOException Exc) {
            System.out.println("Error: " + Exc.getMessage());
        }

    }
}

