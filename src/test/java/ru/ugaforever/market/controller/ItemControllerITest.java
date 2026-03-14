package ru.ugaforever.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.ugaforever.market.config.DatabaseInitializer;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.service.ItemService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@Import(DatabaseInitializer.class)
@AutoConfigureMockMvc
class ItemControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Test
    void testGetItems_ShouldReturnItems() throws Exception {
        // Arrange
        String search ="1";
        String pageNumber = "1";
        String pageSize = "20";

        Item item1 = itemService.findById(1L);
        Item item10 = itemService.findById(10L);
        Item item11 = itemService.findById(11L);

        // Act & Assert
        mockMvc.perform(get("/items")
                        .contentType(MediaType.TEXT_HTML)
                        .param("search", search)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("items", instanceOf(List.class)))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attribute("items", hasSize(4)))

                .andExpect(model().attribute("items",
                hasItem(
                        contains(
                                allOf(
                                        hasProperty("id", is(item1.getId())),
                                        hasProperty("title", is(item1.getTitle())),
                                        hasProperty("price", is(item1.getPrice()))),
                                allOf(
                                        hasProperty("id", is(item10.getId())),
                                        hasProperty("title", is(item10.getTitle())),
                                        hasProperty("price", is(item10.getPrice()))),
                                allOf(
                                        hasProperty("id", is(item11.getId())),
                                        hasProperty("title", is(item11.getTitle())),
                                        hasProperty("price", is(item11.getPrice())))
                        )
                )));
    }

    @Test
    void testGetItemById_ShouldReturnItem() throws Exception {
        // Arrange
        Item item = itemService.findById(1L);

        // Act & Assert
        mockMvc.perform(get( "/items/{id}", item.getId())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", hasProperty("id", is(item.getId()))))
                .andExpect(model().attribute("item", hasProperty("title", is(item.getTitle()))))
                .andExpect(model().attribute("item", hasProperty("description", is(item.getDescription()))))
                .andExpect(model().attribute("item", hasProperty("price", is(item.getPrice()))))
                .andExpect(model().attribute("item", hasProperty("imgPath", is(item.getImgPath()))));
    }

    @Test
    void testHandleItemAction_ShouldRedirect() throws Exception {
       // Arrange
        String id = "1";
        String search = "1";
        String sort = "NO";
        int pageNumber = 1;
        int pageSize = 5;

        // Act & Assert
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("action", "PLUS")
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testHandleItemActionById_WithPlus_ShouldReturnItem() throws Exception {
        // Arrange
        long id = 1L;
        MockHttpSession session = new MockHttpSession();

        // Act
        // Добавляем +1 единицу товара id
        mockMvc.perform(post( "/items/{id}", id)
                        .contentType(MediaType.TEXT_HTML)
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().isOk());

        // Повторно запрашиваем товар id (в той же сессии)
        MvcResult mvcResult = mockMvc.perform(get( "/items/{id}", id)
                        .contentType(MediaType.TEXT_HTML)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Item result = (Item) mvcResult.getModelAndView().getModel().get("item");
        assertThat(result.getCount()).isEqualTo(1);
    }

    @Test
    void testHandleItemActionById_WithMinus_ShouldReturnItem() throws Exception {
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

        // Удалем -1 единицу товара id из корзины
        mockMvc.perform(post( "/items/{id}", id)
                        .contentType(MediaType.TEXT_HTML)
                        .param("action", "MINUS")
                        .session(session))
                .andExpect(status().isOk());

        // Повторно запрашиваем товар id (в той же сессии)
        MvcResult mvcResult = mockMvc.perform(get( "/items/{id}", id)
                        .contentType(MediaType.TEXT_HTML)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        Item result = (Item) mvcResult.getModelAndView().getModel().get("item");
        assertThat(result.getCount()).isEqualTo(0);
    }
}
