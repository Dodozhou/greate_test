package com.star.repository;


import com.star.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;


/**
 * Created by hp on 2017/8/18.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article,Integer>{
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Article findByIdForUpdate(Integer id);
}
