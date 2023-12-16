package pricerComaprison;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class  AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void sessionFactoryTest() {
        Hibernate hibernate = new Hibernate();
        AppConfig app = new AppConfig();

        try {
            hibernate.setSessionFactory(app.sessionFactory());
        } catch (Exception ex) {
            fail("Error: Can't Set Session Factory: " + ex.getMessage());
        }

        assertNotNull(hibernate.getSessionFactory());
        System.out.println("Test 1 has been completed");
    }

}
