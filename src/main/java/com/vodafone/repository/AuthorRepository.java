package com.vodafone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vodafone.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer>
{
    
}
