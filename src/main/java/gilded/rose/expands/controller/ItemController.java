package gilded.rose.expands.controller;


import gilded.rose.expands.exceptions.ItemOutOfStockException;
import gilded.rose.expands.interfaces.ItemApi;
import gilded.rose.expands.data.BuyItem;
import gilded.rose.expands.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import gilded.rose.expands.service.ItemService;

import java.util.List;

@RestController
public class ItemController implements ItemApi {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    @RequestMapping(value = ItemApi.ITEMS_PATH, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public @ResponseBody
    List<Item> getAllItemsTest() {
        return itemService.getAllItems();
    }

    @Override
    @RequestMapping(value = ItemApi.ITEM_DESCRIPTION_PATH, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public @ResponseBody
    Item viewItem(String name) {
        return itemService.viewItem(name);
    }

    @Override
    @RequestMapping(value = ItemApi.ITEM_BUY_PATH, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public @ResponseBody
    BuyItem buyItem(@RequestParam(ID_PARAM) String name) {
        try {
            if (itemService.buyItem(name)) {
                return new BuyItem("Purchase successful");
            } else {
                return new BuyItem("Item not found");
            }
        } catch (ItemOutOfStockException e) {
            return new BuyItem("Item out of Stock");
        }
    }

}
