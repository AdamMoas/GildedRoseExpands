import gilded.rose.expands.Main;
import gilded.rose.expands.data.MockItemDatabase;
import gilded.rose.expands.interfaces.ItemApi;
import gilded.rose.expands.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@WebAppConfiguration
public class MainTests {

    private static final String ITEM_NAME = "Book";

    private static final String[] ITEM_NAMES = {"Plant", "Car", "Book", "Food"};

    private MockMvc mockMvc;

    private Map<String, Item> mockItemDatabase;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private FilterChainProxy filterChainProxy;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .addFilter(filterChainProxy).build();

        this.mockItemDatabase = MockItemDatabase.createItemDatabase();
    }

    @Test
    public void itemOutOfStockUnauthorizedTest() throws Exception {
        mockMvc.perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "=" + ITEM_NAME)
                .contentType(contentType))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Test
    public void buyItemUnauthorizedTest() throws Exception {
        mockMvc.perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "="
                + mockItemDatabase.get(ITEM_NAME).getName()).secure(true))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Test
    public void viewAllItemsTest() throws Exception {
        viewAllItems(4);
    }

    private void viewAllItems(int numOfItems) throws Exception {
        int lastItemIndex = numOfItems - 1;
        for (int i = 0; i <= lastItemIndex; i++) {
            mockMvc.perform(get(ItemApi.ITEMS_PATH).secure(true))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$", hasSize(numOfItems)))
                    .andExpect(jsonPath("$[" + lastItemIndex + "].name",
                            is(mockItemDatabase.get(ITEM_NAMES[lastItemIndex]).getName())))
                    .andExpect(jsonPath("$[" + lastItemIndex + "].description",
                            is(mockItemDatabase.get(ITEM_NAMES[lastItemIndex]).getDescription())))
                    .andExpect(jsonPath("$[" + lastItemIndex + "].price",
                            is(mockItemDatabase.get(ITEM_NAMES[lastItemIndex]).getPrice())));
        }
    }

    @Test
    public void buyItemAuthorizedTest() throws Exception {
        mockMvc.perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "="
                + mockItemDatabase.get(ITEM_NAME).getName())
                .header("Authorization", "Bearer " + obtainAccessToken()).secure(true))
                .andExpect(status().isOk());
    }

    @Test
    public void incorrectAuthenticationDetailsTest() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .params(getTokenParameters("incorrect", "incorrect123"))
                .with(httpBasic("authorized", "authorizedpassword"))
                .accept(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_grant")))
                .andExpect(jsonPath("$.error_description", is("Bad credentials")));
    }

    private String obtainAccessToken() throws Exception {
        ResultActions result = mockMvc
                .perform(post("/oauth/token")
                        .params(getTokenParameters("adam", "qazqaz123"))
                        .with(httpBasic("authorized", "authorizedpassword"))
                        .accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
                .andExpect(jsonPath("$.refresh_token", is(notNullValue())))
                .andExpect(jsonPath("$.expires_in", is(lessThan(240))))
                .andExpect(jsonPath("$.scope", is(equalTo("BUY"))));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private MultiValueMap<String, String> getTokenParameters(String username, String password) {
        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "password");
        tokenParams.add("client_id", "authorized");
        tokenParams.add("username", username);
        tokenParams.add("password", password);
        return tokenParams;
    }

}
