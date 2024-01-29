package com.rxvlvxr.repositories;

import com.rxvlvxr.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    // ищем строку по параметру name
    Person findPersonByName(String name);
}
