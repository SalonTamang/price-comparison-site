package pricerComaprison;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pricerComaprison.Tables.bikes;
import pricerComaprison.Tables.prices;

import java.util.ArrayList;
import java.util.List;

public class Hibernate {

    private SessionFactory sessionFactory;

    Hibernate() {

    }


    public ArrayList<Integer> addBike(String name, String description, String brand, String image, int price, String url) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            if (!checkBikeDuplicates("brand", brand, "name", name)) {
                bikes bike = new bikes();
                prices prices = new prices();

                bike.setName(name);
                bike.setDescription(description);
                bike.setBrand(brand);
                bike.setImage(image);

                session.save(bike);

                prices.setBike_id(bike.getId());
                prices.setPrice(price);
                prices.setUrl(url);

                session.save(prices);
                session.getTransaction().commit();

                ArrayList<Integer> ids = new ArrayList<>();
                ids.add(bike.getId());
                ids.add(prices.getId());

                System.out.println("Bike added to database with ID: " + bike.getId());
                return ids;

            }  else if (checkBikeDuplicates("description", description, "image", image)) {
                session.close();
                System.out.println("Bike is already in bikes table");
                return new ArrayList<>();
            }else if (checkBikePriceUrl("price", price, "url", url)) {
                //if the url and price is present already then close and move to another
                session.close();
                System.out.println("Price and url is already present is already present");
                return new ArrayList<>();
            } else {
                bikes existingBike = matchBike("name", name);

                prices prices = new prices();

                prices.setBike_id(existingBike.getId());
                prices.setPrice(price);
                prices.setUrl(url);

                session.save(prices);
                session.getTransaction().commit();

                session.close();
                System.out.println("New Price added to database with ID: " + prices.getId());

            }

        } catch (Exception e) {
            // Handle exception
            if (session != null) {
                try {
                    if (session.getTransaction() != null && session.getTransaction().isActive()) {
                        // Rollback the transaction if it's still active
                        session.getTransaction().rollback();
                    }
                } finally {
                    // Close the session in the finally block to ensure it's closed even if an exception occurs during rollback
                    session.close();
                }
            }

            e.printStackTrace(); // Print the exception stack trace for debugging

            System.out.println("Error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean checkBikeDuplicates(String column1, String data1, String column2, String data2) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        String hql = "from bikes where " + column1 + " = :data1 AND " + column2 + " = :data2";
        List<bikes> bikeList = session.createQuery(hql)
                .setParameter("data1", data1)
                .setParameter("data2", data2 )
                .getResultList();

        session.close();
        return bikeList.size() > 0;
    }

    public bikes matchBike(String column1, String data1) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        String hql = "from bikes where " + column1 + " = :data1";
        List<bikes> bikeList = session.createQuery(hql)
                .setParameter("data1", data1 )
                .getResultList();

        session.close();

        if(bikeList.size()>1){
            return bikeList.get(1);
        }
        return bikeList.get(0);
    }

    public boolean checkBikePriceUrl(String column1, int data1, String column2, String data2) {

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        String hql = "from prices where " + column1 + " = :data1 AND " + column2 + " = :data2";
        List<bikes> priceList = session.createQuery(hql)
                .setParameter("data1", data1)
                .setParameter("data2", data2)
                .getResultList();
        session.close();
        return priceList.size() > 0;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
