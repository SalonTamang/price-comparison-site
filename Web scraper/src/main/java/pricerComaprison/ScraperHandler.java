package pricerComaprison;

import java.util.List;
public class ScraperHandler {
    public static List<Thread> scraperList;

    //Empty Constructor
    ScraperHandler(){
    }

    //Getter
    public static List<Thread> getScraperList(){
        return scraperList;
    }

    //Setter
    public static void setScraperList(List<Thread> slist){
        scraperList = slist;
    }

    //start bikes thread
    public void startThreads(){
        for(Thread th: scraperList){
            th.start();
        }
    }

    //Join the bike threads
    public void joinThreads() {
        for (Thread th : scraperList) {

            try {
                th.join();
            }catch(InterruptedException exc){
                System.out.println("Error: " + exc.getMessage());
            }
        }
    }
}
