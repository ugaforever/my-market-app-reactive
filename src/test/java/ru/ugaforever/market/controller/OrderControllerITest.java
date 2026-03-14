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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@Import(DatabaseInitializer.class)
@AutoConfigureMockMvc
class OrderControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateOrderFromCart_ShouldRedirect() throws Exception {
        // Arrange
        long id = 1L;
        MockHttpSession session = new MockHttpSession();

        // Act
        // Добавляем +1 единицу товара id в корзину
        mockMvc.perform(post( "/items/{id}", id)
                        .contentType(MediaType.TEXT_HTML)
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().isOk());


        // Act
        // Создаем  заказ
        mockMvc.perform(post("/buy")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/" + id + "?newOrder=true"));

        // Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attribute("orders", hasSize(1)));
    }
}