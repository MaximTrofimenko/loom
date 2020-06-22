package com.trofimenko.loom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("maxim")//имя пользователя под которым мы хотим выполнять данные тесты, можно приметять и на классе и на методе
/*
Аннотация TestPropertySource работает только для файлов .properties или .xml.
 */
@TestPropertySource("/application-test.yml") //это чтобы указать какие проперти использовать при тестах
@Sql(value = {"/create-user-before.sql","/messages-list-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)//скрипты которые нужно выполнить перед тестами, можно приметять и на классе и на методе
@Sql(value = {"/messages-list-after.sql","/create-user-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)//скрипты которые нужно выполнить перед тестами, можно приметять и на классе и на методе

public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
     /*
    проверка на аутентификацию
     */
    @Test
    public void mainPageTest() throws Exception{
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())  //то что пользователь корректно аутенц, используется в месте с @WithUserDetails
                .andExpect(xpath("//*[@id='navbarSupportedContent']/div").string("maxim")); //ищем атрибут id вот с таким именем. нехера не понял
    }

    /*
    корректное отображение списка сообщений
    ожидаем что на странице будет какое то кол-во элементов.В идеале для теста нужно своя база данных - поэтому создадим новую бд
    Например loomtest. Затем в тестах создаем свой файл application-test.yml.
     */
    @Test
    public void messageListTest() throws Exception{
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(4));//проверяем что по этому адресу таких элементов 4 штуки
    }

    /*
    тест на наличие конкретных сообщений с определенным кодом
     */
    @Test
    public void filterMessageTest() throws Exception{
        this.mockMvc.perform(get("/main").param("filter","my-tag"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(2))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id=1]").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id=3]").exists());
    }


    /*
    тест на наличие добавление конкретного сообщения
     */
    @Test
    public void addMessageToListTest() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/main")
                .file("file", "123".getBytes())
                .param("text", "fifth")
                .param("tag", "new one")
                .with(csrf());

        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(5))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/span").string("fifth"))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/i").string("#new one"));
    }
}
