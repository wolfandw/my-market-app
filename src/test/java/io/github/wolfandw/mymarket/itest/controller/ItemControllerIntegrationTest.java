package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.MyMarketUtils;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.hamcrest.beans.HasProperty;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты контроллера товаров.
 */
public class ItemControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getItemsTest() throws Exception {
        mockMvc.perform(get("/items")
                        .param(MyMarketUtils.PARAMETER_SEARCH, "")
                        .param(MyMarketUtils.PARAMETER_SORT, "")
                        .param(MyMarketUtils.PARAMETER_PAGE_NUMBER, "")
                        .param(MyMarketUtils.PARAMETER_PAGE_SIZE, ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(2)))
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_SEARCH))
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_SORT))
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_PAGING))
                .andExpect(view().name(MyMarketUtils.TEMPLATE_ITEMS));
    }

    @Test
    void getItemTest() throws Exception {
        mockMvc.perform(get("/items/2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_ITEM))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, IsNull.notNullValue(ItemDto.class)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasProperty.hasProperty(MyMarketUtils.ATTRIBUTE_TITLE)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(MyMarketUtils.ATTRIBUTE_TITLE, equalTo("Item 08"))))
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_NEW_ITEM))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_NEW_ITEM, equalTo(false)))
                .andExpect(view().name(MyMarketUtils.TEMPLATE_ITEM));
    }

    @Test
    @Transactional
    void changeItemCountOnItemsTest() throws Exception {
        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

        String redirectUrl = MyMarketUtils.buildRedirectUrlToItems(searchParamValue, sortParamValue, pageNumberParamValue, pageSizeParamValue);

        mockMvc.perform(post("/items")
                        .param(MyMarketUtils.PARAMETER_ID, Long.toString(itemId))
                        .param(MyMarketUtils.PARAMETER_ACTION, MyMarketUtils.ACTION_PLUS)
                        .param(MyMarketUtils.PARAMETER_SEARCH, searchParamValue)
                        .param(MyMarketUtils.PARAMETER_SORT, sortParamValue)
                        .param(MyMarketUtils.PARAMETER_PAGE_NUMBER, pageNumberParamValue.toString())
                        .param(MyMarketUtils.PARAMETER_PAGE_SIZE, pageSizeParamValue.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));
    }

    @Test
    @Transactional
    void changeItemCountOnItemTest() throws Exception {
        mockMvc.perform(post("/items/1")
                        .param(MyMarketUtils.PARAMETER_ACTION, MyMarketUtils.ACTION_PLUS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_ITEM))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, IsNull.notNullValue(ItemDto.class)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasProperty.hasProperty(MyMarketUtils.ATTRIBUTE_TITLE)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(MyMarketUtils.ATTRIBUTE_TITLE, equalTo("Item 07 SearchTag"))))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasProperty.hasProperty(MyMarketUtils.ATTRIBUTE_COUNT)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(MyMarketUtils.ATTRIBUTE_COUNT, equalTo(1))))

                .andExpect(view().name(MyMarketUtils.TEMPLATE_ITEM));
    }

    @Test
    @Transactional
    void addNewItemTest() throws Exception {
        mockMvc.perform(get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(MyMarketUtils.TEMPLATE_ITEM_NEW));
    }

    @Test
    @Transactional
    void saveNewItemTest() throws Exception {
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;

        String imageName = "14.jpg";
        byte[] expectedImageData = fileStorageService.readFile(imageName);
        MockMultipartFile imageFileParamValue = new MockMultipartFile(MyMarketUtils.PARAMETER_IMAGE_FILE,
                imageName,
                MediaType.IMAGE_JPEG_VALUE,
                expectedImageData);

        String redirectUrl = MyMarketUtils.buildRedirectUrlToNewItem(itemId);

        mockMvc.perform(multipart("/items/new")
                        .param(MyMarketUtils.PARAMETER_TITLE, titleParamValue)
                        .param(MyMarketUtils.PARAMETER_DESCRIPTION, descriptionParamValue)
                        .param(MyMarketUtils.PARAMETER_PRICE, Long.toString(priceParamValue))
                        .file(imageFileParamValue)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));
    }
}
