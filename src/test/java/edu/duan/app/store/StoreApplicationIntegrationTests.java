package edu.duan.app.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.duan.app.store.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StoreApplicationIntegrationTests {
    @Autowired
    private WebApplicationContext webContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    @Test
    public void shouldReturnOrderByIdTest() throws Exception {
        performGet("/orders/{id}", "1")
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void shouldReturnNotFoundIfOrderNotFoundTest() throws Exception {
        mockMvc.perform(get("/orders/{id}", "7").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOrdersByLoginTest() throws Exception {
        performGet("/orders/for/user?userLogin={userLogin}", "test@user.com")
                .andExpect(jsonPath("$.[0].id").value("1"))
                .andExpect(jsonPath("$.[0].user.login").value("test@user.com"))
                .andExpect(jsonPath("$.[0].orderState").value(OrderState.NEW.name()))
                .andExpect(jsonPath("$.[1].id").value("2"))
                .andExpect(jsonPath("$.[1].user.login").value("test@user.com"))
                .andExpect(jsonPath("$.[1].orderState").value(OrderState.COMPLETED.name()));
    }

    @Test
    public void shouldReturnOrdersByLoginAndStateTest() throws Exception {
        performGet("/orders/for/user/by/state?userLogin={userLogin}&state={state}", "test@user.com", OrderState.COMPLETED.name())
                .andExpect(jsonPath("$.[0].id").value("2"))
                .andExpect(jsonPath("$.[0].user.login").value("test@user.com"))
                .andExpect(jsonPath("$.[0].orderState").value(OrderState.COMPLETED.name()));
    }

    @Test
    public void shouldPlaceOrderForExistingUserTest() throws Exception {
        performPost("/orders", buildOrderRequest("test@user.com", 1, 1))
                .andExpect(content().string(asJsonString(OrderState.NEW.name())));
        performGet("/orders/for/user?userLogin={userLogin}", "test@user.com")
                .andExpect(jsonPath("$.[2].id").value("3"))
                .andExpect(jsonPath("$.[2].user.login").value("test@user.com"))
                .andExpect(jsonPath("$.[2].orderState").value(OrderState.NEW.name()));
    }

    @Test
    public void shouldPlaceOrderForNewUserTest() throws Exception {
        performPost("/orders", buildOrderRequest("new@user.com", 1, 1))
                .andExpect(content().string(asJsonString(OrderState.NEW.name())));
        performGet("/orders/for/user?userLogin={userLogin}", "new@user.com")
                .andExpect(jsonPath("$.[0].id").value("3"))
                .andExpect(jsonPath("$.[0].user.login").value("new@user.com"))
                .andExpect(jsonPath("$.[0].orderState").value(OrderState.NEW.name()));
    }

    @Test
    public void shouldBeCanceledIfStockIsEmptyTest() throws Exception {
        performPost("/orders", buildOrderRequest("test1@user.com", 1, 100))
                .andExpect(content().string(asJsonString(OrderState.CANCELLED.name())));
    }

    @Test
    public void shouldBeNotFoundIfItemNotInWarehouseIfNotExistingItemTest() throws Exception {
        // Actually item exist but not added to warehouse
        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(buildOrderRequest("test1@user.com", 4, 100)))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundIfNotExistingItemTest() throws Exception {
        // Actually item does not exist
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(buildOrderRequest("test1@user.com", 7, 1)))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldProcessNewOrderToStatusCompleteTest() throws Exception {
        performPut("/orders", buildProcessRequest(1, OrderState.COMPLETED, "", ""))
                .andExpect(content().string(asJsonString(OrderState.COMPLETED.name())));
        performGet("/orders/{id}", "1")
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.login").value("test@user.com"))
                .andExpect(jsonPath("$.orderState").value(OrderState.COMPLETED.name()));
    }

    @Test
    public void shouldProcessNewOrderToStatusCompleteAndSetNotesTest() throws Exception {
        performPut("/orders", buildProcessRequest(1, OrderState.COMPLETED, "set notes", ""))
                .andExpect(content().string(asJsonString(OrderState.COMPLETED.name())));
        performGet("/orders/{id}", "1")
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.login").value("test@user.com"))
                .andExpect(jsonPath("$.orderState").value(OrderState.COMPLETED.name()))
                .andExpect(jsonPath("$.notes").value("set notes"));
    }

    @Test
    public void shouldProcessNewOrderToStatusCompleteAndSkipFulfillmentNotesTest() throws Exception {
        performPut("/orders", buildProcessRequest(1, OrderState.COMPLETED, "set notes", "some fulfillment"))
                .andExpect(content().string(asJsonString(OrderState.COMPLETED.name())));
        performGet("/orders/{id}", "1")
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.login").value("test@user.com"))
                .andExpect(jsonPath("$.orderState").value(OrderState.COMPLETED.name()))
                .andExpect(jsonPath("$.fulfillmentNotes").isEmpty());
    }

    @Test
    public void shouldProcessNewOrderToStatusFulfilledTest() throws Exception {
        performPut("/orders", buildProcessRequest(1, OrderState.FULFILLED, "", ""))
                .andExpect(content().string(asJsonString(OrderState.FULFILLED.name())));
        performGet("/orders/{id}", "1")
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.login").value("test@user.com"))
                .andExpect(jsonPath("$.orderState").value(OrderState.FULFILLED.name()))
                .andExpect(jsonPath("$.fulfillmentNotes").isEmpty());
    }

    @Test
    public void shouldProcessNewOrderToStatusFulfilledAndSetFulfillmentNotesTest() throws Exception {
        performPut("/orders", buildProcessRequest(1, OrderState.FULFILLED, "", "NovaPost tracking number"))
                .andExpect(content().string(asJsonString(OrderState.FULFILLED.name())));
        performGet("/orders/{id}", "1")
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.login").value("test@user.com"))
                .andExpect(jsonPath("$.fulfillmentNotes").value("NovaPost tracking number"));
    }


    private ResultActions performGet(String path, Object... uriVariables) throws Exception {
        return mockMvc.perform(get(path, uriVariables).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPost(String path, Object content, Object... uriVariables) throws Exception {
        return mockMvc.perform(
                post(path, uriVariables)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(content))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPut(String path, Object content, Object... uriVariables) throws Exception {
        return mockMvc.perform(
                        put(path, uriVariables)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(content))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private OrderRequest buildOrderRequest(String login, Integer itemId, Integer count) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItemId(itemId);
        orderRequest.setCount(count);
        orderRequest.setLogin(login);
        return orderRequest;
    }
    private ProcessingOrder buildProcessRequest(Integer orderId, OrderState state, String notes, String fulfillmentNotes) {
        ProcessingOrder processingOrder = new ProcessingOrder();
        processingOrder.setId(orderId);
        processingOrder.setOrderState(state);
        processingOrder.setNotes(notes);
        processingOrder.setFulfillmentNotes(fulfillmentNotes);
        return processingOrder;
    }
}
