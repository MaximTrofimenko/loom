package com.trofimenko.loom.repository;

import com.trofimenko.loom.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message,Long> {
}
