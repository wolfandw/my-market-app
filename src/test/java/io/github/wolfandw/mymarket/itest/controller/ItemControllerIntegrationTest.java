package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.DtoConstants;
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
                        .param(DtoConstants.PARAMETER_SEARCH, "")
                        .param(DtoConstants.PARAMETER_SORT, "")
                        .param(DtoConstants.PARAMETER_PAGE_NUMBER, "")
                        .param(DtoConstants.PARAMETER_PAGE_SIZE, ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(2)))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_SEARCH))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_SORT))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_PAGING))
                .andExpect(view().name(DtoConstants.TEMPLATE_ITEMS));
    }

    @Test
    void getItemTest() throws Exception {
        mockMvc.perform(get("/items/2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ITEM))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, IsNull.notNullValue(ItemDto.class)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasProperty.hasProperty(DtoConstants.ATTRIBUTE_TITLE)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(DtoConstants.ATTRIBUTE_TITLE, equalTo("Item 08"))))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_NEW_ITEM))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_NEW_ITEM, equalTo(false)))
                .andExpect(view().name(DtoConstants.TEMPLATE_ITEM));
    }

    @Test
    @Transactional
    void changeItemCountOnItemsTest() throws Exception {
        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

        String redirectUrl = RedirectUrlFactory.createUrlToItems(searchParamValue, sortParamValue, pageNumberParamValue, pageSizeParamValue);

        mockMvc.perform(post("/items")
                        .param(DtoConstants.PARAMETER_ID, Long.toString(itemId))
                        .param(DtoConstants.PARAMETER_ACTION, DtoConstants.ACTION_PLUS)
                        .param(DtoConstants.PARAMETER_SEARCH, searchParamValue)
                        .param(DtoConstants.PARAMETER_SORT, sortParamValue)
                        .param(DtoConstants.PARAMETER_PAGE_NUMBER, pageNumberParamValue.toString())
                        .param(DtoConstants.PARAMETER_PAGE_SIZE, pageSizeParamValue.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));
    }

    @Test
    @Transactional
    void changeItemCountOnItemTest() throws Exception {
        mockMvc.perform(post("/items/1")
                        .param(DtoConstants.PARAMETER_ACTION, DtoConstants.ACTION_PLUS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ITEM))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, IsNull.notNullValue(ItemDto.class)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasProperty.hasProperty(DtoConstants.ATTRIBUTE_TITLE)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(DtoConstants.ATTRIBUTE_TITLE, equalTo("Item 07 SearchTag"))))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasProperty.hasProperty(DtoConstants.ATTRIBUTE_COUNT)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEM, HasPropertyWithValue.hasProperty(DtoConstants.ATTRIBUTE_COUNT, equalTo(1))))

                .andExpect(view().name(DtoConstants.TEMPLATE_ITEM));
    }

    @Test
    @Transactional
    void addNewItemTest() throws Exception {
        mockMvc.perform(get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(DtoConstants.TEMPLATE_ITEM_NEW));
    }

    @Test
    @Transactional
    void saveNewItemTest() throws Exception {
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;

        String imageName = "15.jpg";
        byte[] expectedImageData = fileStorageService.readFile(imageName);
        MockMultipartFile imageFileParamValue = new MockMultipartFile(DtoConstants.PARAMETER_IMAGE_FILE,
                imageName,
                MediaType.IMAGE_JPEG_VALUE,
                expectedImageData);

        String redirectUrl = RedirectUrlFactory.createUrlToNewItem(itemId);

        mockMvc.perform(multipart("/items/new")
                        .param(DtoConstants.PARAMETER_TITLE, titleParamValue)
                        .param(DtoConstants.PARAMETER_DESCRIPTION, descriptionParamValue)
                        .param(DtoConstants.PARAMETER_PRICE, Long.toString(priceParamValue))
                        .file(imageFileParamValue)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

        fileStorageService.deleteFile(imageName);
    }
}
