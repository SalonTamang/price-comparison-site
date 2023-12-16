package pricerComaprison.Tables;

import javax.persistence.*;

/**
 * *Class representing prices
 */

@Entity
@Table(name = "prices")
public class prices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "bike_id")
    private int bike_id;

    @Column(name = "price")
    private int price;

    @Column(name = "url")
    private String url;

    public void setBike_id(int id) {
        this.bike_id = id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public int getBike_id() {
        return bike_id;
    }

    public int getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString(){
        return "Price: "+ price+ " Url: "+url+ " bike_id: "+bike_id;
    }

}
