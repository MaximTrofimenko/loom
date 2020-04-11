package com.trofimenko.loom.servise;

import com.trofimenko.loom.domain.Role;
import com.trofimenko.loom.domain.User;
import com.trofimenko.loom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

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
        //тут у user создается set с одним единственным значением
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);

        return true;
    }
}
