package gilded.rose.expands.interfaces;

import gilded.rose.expands.data.BuyItem;
import gilded.rose.expands.model.Item;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface ItemApi {

    String ITEMS_PATH = "/items";
    String ID_PARAM = "name";
    String ITEM_DESCRIPTION_PATH = ITEMS_PATH + "/view";
    String ITEM_BUY_PATH = ITEMS_PATH + "/buy";

    @GET(ITEMS_PATH)
    List<Item> getAllItemsTest();

    @GET(ITEM_BUY_PATH)
    BuyItem buyItem(@Query(ID_PARAM) String name);

    @GET(ITEM_DESCRIPTION_PATH)
    Item viewItem(@Query(ID_PARAM) String name);

}
