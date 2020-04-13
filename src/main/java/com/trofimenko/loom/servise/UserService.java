package com.trofimenko.loom.servise;

import com.trofimenko.loom.domain.Role;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public boolean addUser(User user){
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if(userFromDB != null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER)); //тут у user создается set с одним единственным значением
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        if(!StringUtils.isEmpty(user.getEmail())){   //проверка что поле не пустое, если не пусто - отправляем
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Loom. Please, visit next link: http://localhost:8080/activate/%s",  //url можно вынести в проперти и использовать разные проперти для продакшн и девелоп
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(),"Activation code", message);
        }
        return true;
    }

    public boolean activeUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if(user == null){
            return false;
        }

        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())  //переводим enum в set ролей
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        /*
        добавляем роли пользователю, тоесть сравниваем списов ВСЕХ ролей и ролей которые
        заполнил пользователь через форму
        Если роль из формы соответствует роли в базовом списке то добавляем эту роль юзеру
         */
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
    }
}
