package gilded.rose.expands.exceptions;

public class ItemOutOfStockException extends RuntimeException {

    public ItemOutOfStockException(String name) {
        super("Sorry, the item: " + name + " is out of stock");
    }

}
