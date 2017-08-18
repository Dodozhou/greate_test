package com.star.repository;


import com.star.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by hp on 2017/8/18.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article,Integer>{

}
