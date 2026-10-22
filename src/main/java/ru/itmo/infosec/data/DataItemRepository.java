package ru.itmo.infosec.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DataItemRepository extends JpaRepository<DataItem, Long> {
    List<DataItem> findAllByOwnerUsernameOrderByCreatedAtDesc(String ownerUsername);
}

