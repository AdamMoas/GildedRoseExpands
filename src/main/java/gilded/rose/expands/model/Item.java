package gilded.rose.expands.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "item")
public class Item implements Serializable {

    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private int price;

    public Item() {
    }

    public Item(String name, String description, int price) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Item) {
            Item that = (Item) object;
            return Objects.equals(name, that.name)
                    && Objects.equals(description, that.description)
                    && price == that.price;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Item [name='%s', description='%s', price=%d]",
                name, description, price);
    }
}
