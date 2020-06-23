package com.trofimenko.loom.servise;

import com.trofimenko.loom.domain.Role;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailSender mailSender;

    /*
    проверка на создание юзера добавления его а также почта, код активации и пр.
     */
    @Test
    void addUser() {
        User user = new User();
        user.setEmail("basic@yandex.ru");

        boolean isUserCreated = userService.addUser(user);

        assertTrue(isUserCreated);  //проверка что юзер создался
        assertNotNull(user.getActivationCode());  //проверка что установился кот активации
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));  //АССЕРТ НА ИСТИНОЕ ЗНАЧЕНИЕ  , проверка на роль юзера
        Mockito.verify(userRepository, Mockito.times(1)).save(user);//проверка что юзер был вызван из репы один раз и метод был сэйв с аргументом юзер
        Mockito.verify(mailSender, Mockito.times(1))//проверка на то что пользователю отправлено сообщение на почту
                .send(  //сообщение состоит из..(это все соответсвует методу send)
                        ArgumentMatchers.eq(user.getEmail()),//email
                        ArgumentMatchers.anyString(),//subject
                        ArgumentMatchers.anyString()//message
                );
    }

    //проверка что вернется фолс если юзер с таким именем существует
    @Test
    void addUserFailTest(){
        User user = new User();
        user.setUsername("Israel");

        Mockito.doReturn(new User()) //возвращаем нового пользователя
                .when(userRepository) //когда в репозитории вызываем
                .findByUsername("Israel");//данный метод с аргументом "Israel"

        boolean isUserCreated = userService.addUser(user);
        assertFalse(isUserCreated);//должен вернуть false
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));//проверка что юзер никакой юзер не сохранялся
        Mockito.verify(mailSender, Mockito.times(0))//проверка на то что пользователю отправлено сообщение на почту
                .send(  //сообщение состоит из..(это все соответсвует методу send)
                        ArgumentMatchers.anyString(),//проверяем что ничего не отправлялось, какие бы аргументы не передавались
                        ArgumentMatchers.anyString(),//
                        ArgumentMatchers.anyString()//
                );
    }

    /*
    тест на коды активации
     */
    @Test
    public void activateUser() {
        User user = new User();

        user.setActivationCode("bingo!");//устанавливаем юзеру код

        Mockito.doReturn(user)
                .when(userRepository)
                .findByActivationCode("activate");//возвращаем созданово ранее юзера когда обратимся к репозиторию с кодом "activate"

        boolean isUserActivated = userService.activeUser("activate"); //обращаемся к сервису(внутри он обращается к репозитории см. выше)

        assertTrue(isUserActivated); //проверяем и оказывается там есть код "bingo!" поэтому true
        assertNull(user.getActivationCode()); // а тут кода не будет потому что он по логике должен стираться

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    /*
    тест на неудачную активацию
     */
    @Test
    public void activateUserFailTest() {
        boolean isUserActivated = userService.activeUser("activate me");

        assertFalse(isUserActivated);

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}