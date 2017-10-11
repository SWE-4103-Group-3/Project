package com.unb.tracker.repository;


import com.unb.tracker.model.Course;
import com.unb.tracker.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Long> {
    public User findByUserName(String userName);
}
