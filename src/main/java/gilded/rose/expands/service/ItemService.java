package gilded.rose.expands.service;

import gilded.rose.expands.data.MockItemDatabase;
import gilded.rose.expands.utils.SurgePriceUtil;
import gilded.rose.expands.exceptions.ItemOutOfStockException;
import gilded.rose.expands.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ItemService {

    private Map<String, Item> items = MockItemDatabase.createItemDatabase();
    private SurgePriceUtil surgePrice;
    private int itemsAvailable = 5;

    @Autowired
    public ItemService(SurgePriceUtil surgePrice) {
        this.surgePrice = surgePrice;
    }

    public List<Item> getAllItems() {
        int surgePriceMultiplier = surgePrice.calculateSurgeMultiplierPercent();
        List<Item> itemResources = new ArrayList<>();
        for (Item item : items.values()) {
            itemResources.add(new Item(item.getName(), item.getDescription(), SurgePriceUtil.applySurgeMultiplier(item.getPrice(), surgePriceMultiplier)));
        }
        return itemResources;
    }

    public Item viewItem(String name) {
        int surgePriceMultiplier = surgePrice.calculateSurgeMultiplierPercent();
        Item item = items.get(name);
        return new Item(item.getName(), item.getDescription(), SurgePriceUtil.applySurgeMultiplier(item.getPrice(), surgePriceMultiplier));
    }

    public boolean buyItem(String name) throws ItemOutOfStockException {
        Optional<Item> item = Optional.ofNullable(items.get(name));
        if (item.isPresent() && itemsAvailable > 0) {
            itemsAvailable--;
            return true;
        } else {
            throw new ItemOutOfStockException(name);
        }
    }

}
