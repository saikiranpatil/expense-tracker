package org.example.repository;

import org.example.entity.UserInfoDto;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserInfoDto, Long> {
    UserInfoDto findByUserId(String userId);
}
