package pricerComaprison;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Contains main method for application
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScraperHandler handler = (ScraperHandler) context.getBean("scraperHandler");

        handler.startThreads();
        handler.joinThreads();
    }
}