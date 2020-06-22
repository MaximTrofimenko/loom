package com.trofimenko.loom;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest             //spring
@AutoConfigureMockMvc       //spring
/*
Аннотация TestPropertySource работает только для файлов .properties или .xml.
 */
@TestPropertySource("/application-test.yml") //это чтобы указать какие проперти использовать при тестах
public class LoginTest {
    /*
     создает структуру классов которая автоматически подменяет слой mvc, все будет происходить в фейковом окружении
     как результат мы теперь не должы создавать рест темплэйт - мы просто используем мокнутую версию нашего mvc слоя
     */
    @Autowired
    private MockMvc mockMvc;

    /*
    тест делает http запроси и сравнивает ожидание и полученное
     */
    @Test
    public void contextLoads() throws Exception {
            this.mockMvc.perform(get("/")) //делаем гет запрос на главную страницу
                    .andDo(print())                     //выводим результат в консоль
                    .andExpect(status().isOk())         //ожидаем что запрос вернет статус 200
                    .andExpect(content().string(containsString("Hello, guest")))  //вернется контент и мы результат сравниваем как строку и содержит подстроку
                    .andExpect(content().string(containsString("Please, login")));
    }

    /*
    проверка на запрос авторизации
     */
    @Test
    public void accessDeniedTest() throws Exception{
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())                             //статус 302 - система будет перенаправлять на страницу логина
                .andExpect(redirectedUrl("http://localhost/login"));      //проверка что система подкинет конкретный адрес
    }

    /*
    проверка авторизации пользователя
    тут пользователь берется из той базы которая  сейчас крутится
     */
    @Test
    @Sql(value = {"/create-user-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)//скрипты которые нужно выполнить перед тестами, можно приметять и на классе и на методе
    @Sql(value = {"/create-user-after.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)//скрипты после
    public void correctLoginTest() throws Exception{
        this.mockMvc.perform(formLogin().user("maxim").password("123"))//смотрит как мы в контексте определили логин пэйдж И ВЫЗЫВАЕТ ОБРАЩЕНИЕ К ЭТОЙ СТРАНИЧКЕ
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    /*
    то что отрабатывает отбивка на неправильные данные пользователя
     */
    @Test
    public void badCredentials() throws Exception{
        this.mockMvc.perform(post("/login").param("name","Zalypa"))
                .andDo(print())
                .andExpect(status().isForbidden()); //403 «403 Forbidden» (ошибка «запрещено»)
    }
}
