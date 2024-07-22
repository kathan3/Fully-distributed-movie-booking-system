package com.microservice.user.Repository;

import com.microservice.user.Models.UserDB;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface UserDBRepository extends Repository <UserDB,Integer> {
    List<UserDB> findAll();
    UserDB save(UserDB user);
    UserDB findById(Integer id);
    UserDB findByEmail(String email);
    Integer deleteById(Integer id);
}
