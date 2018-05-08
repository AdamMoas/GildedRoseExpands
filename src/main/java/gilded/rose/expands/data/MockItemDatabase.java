package gilded.rose.expands.data;

import gilded.rose.expands.model.Item;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockItemDatabase {

    public static Map<String, Item> createItemDatabase() {
        Map<String, Item> items = new HashMap<>();

        addItem(items, new Item("Book", "A book is a series of pages assembled for easy portability and reading.", 10));
        addItem(items, new Item("Plant", "Plants are mainly multi-cellular, predominantly photosynthetic eukaryotes of the kingdom Plantae.", 1));
        addItem(items, new Item("Food", "Provides nutritional support for an organism", 250));
        addItem(items, new Item("Car", "Ferrari 458", 120000));

        return items;
    }

    private static void addItem(Map<String, Item> map, Item item) {
        map.put(item.getName(), item);
    }
}
