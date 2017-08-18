package com.star.repository;


import com.star.domain.Great;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hp on 2017/8/18.
 */
@Repository
public interface GreatRepository extends JpaRepository<Great,Integer>{
    List<Great> findByAidAndUid(int aid, int uid);
}
