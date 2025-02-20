package org.example.repository;

import org.example.entity.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends CrudRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByUserId(String UserId);
    List<ExpenseEntity> findByUserIdAndDateBetween(String UserId, Timestamp from, Timestamp to);
    Optional<ExpenseEntity> findByUserIdAndExternalId(String userId, String externalId);
}
