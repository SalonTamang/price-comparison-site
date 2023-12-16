package pricerComaprison;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {

    SessionFactory sessionFactory;

    /**
     * ScraperHandler Bean
     *
     * @return scraperHandler
     */
    @Bean
    public ScraperHandler scraperHandler() {
        ScraperHandler scraperHandler = new ScraperHandler();

        List<Thread> scraperList = new ArrayList();
        scraperList.add(scraper1());
        scraperList.add(scraper2());
        scraperList.add(scraper3());
        scraperList.add(scraper4());
        scraperList.add(scraper5());
        ScraperHandler.setScraperList(scraperList);

        // Return Scraper Handler object
        return scraperHandler;
    }

    /**
     * CyckesUkScrapper Bean
     *
     * @return scraper1
     */
    @Bean
    public CyclesUkScrapper scraper1() {
        CyclesUkScrapper scraper1 = new CyclesUkScrapper();
        return scraper1;
    }

    /**
     * EvansScrapper Bean
     *
     * @return scraper2
     */
    @Bean
    public EvansScrapper scraper2() {
        EvansScrapper scraper2 = new EvansScrapper();
        return scraper2;
    }

    /**
     * JuliesCycles Bean
     *
     * @return scraper3
     */
    @Bean
    public JuliesCycles scraper3() {
        JuliesCycles scraper3 = new JuliesCycles();
        return scraper3;
    }

    /**
     * NevisCycles Bean
     *
     * @return scraper4
     */
    @Bean
    public NevisCyclesScraper scraper4() {
        NevisCyclesScraper scraper4 = new NevisCyclesScraper();
        return scraper4;
    }

    /**
     * TisoBikes Bean
     *
     * @return scraper5
     */
    @Bean
    public TisoBikes scraper5() {
        TisoBikes scraper5 = new TisoBikes();
        return scraper5;
    }

   /**
    * Hibernate Bean
    *
    * @return hibernate
    */
   @Bean
   public Hibernate hibernate() {
       Hibernate Hibernate = new Hibernate();
       Hibernate.setSessionFactory(sessionFactory());
       return Hibernate;
    }

    /**
    * SessionFactory Bean
    *
    * @return sessionFactory
    */
    @Bean
    public SessionFactory sessionFactory() {
        if (sessionFactory == null) {//Build sessionFatory once only
            try {
                //Create a builder for the standard service registry
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

                //Load configuration from hibernate configuration file.
                //Here we are using a configuration file that specifies Java annotations.
                standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

                //Create the registry that will be used to build the session factory
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
                try {
                    //Create the session factory - this is the goal of the init method.
                    sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
                } catch (Exception e) {
                    /* The registry would be destroyed by the SessionFactory,
                            but we had trouble building the SessionFactory, so destroy it manually */
                    System.err.println("Session Factory build failed.");
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                //Ouput result
                System.out.println("Session factory built.");
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("SessionFactory creation failed." + ex);
            }
        }
        return sessionFactory;
    }
}