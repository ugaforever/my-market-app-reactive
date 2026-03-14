package ru.ugaforever.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import ru.ugaforever.market.config.DatabaseInitializer;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(DatabaseInitializer.class)
@AutoConfigureMockMvc
class CartControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItemsCart_ShouldReturnEmptyCart_WhenNoItems() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/cart/items")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("items", hasSize(0)))
                .andExpect(model().attribute("total", is(0.0)));
    }

    @Test
    void TestHandleItemActionById_WithPlusAction_ShouldAddItemToCart() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        String id = "1";
        String search = "1";
        String sort = "NO";
        int pageNumber = 1;
        int pageSize = 5;

        // Act
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("action", "PLUS")
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items?search=" + search +
                        "&sort=" + sort +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize));


        // Assert - проверяем через GET запрос
        mockMvc.perform(get("/cart/items")
                        .param("id", id)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("items", hasSize(1)))
                .andExpect(model().attribute("total", 333.0));
    }

    @Test
    void TestHandleItemActionById_WithMinusAction_ShouldRemoveItemToCart() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        String id = "1";
        String search = "1";
        String sort = "NO";
        int pageNumber = 1;
        int pageSize = 5;

        // Act
        // Добавляем в корзину
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("action", "PLUS")
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items?search=" + search +
                        "&sort=" + sort +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize));

        // Удаляем из корзины
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("action", "MINUS")
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items?search=" + search +
                        "&sort=" + sort +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize));

        // Assert - проверяем через GET запрос
        mockMvc.perform(get("/cart/items")
                        .param("id", id)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", hasSize(0)))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("total", 0.0));
    }
}
