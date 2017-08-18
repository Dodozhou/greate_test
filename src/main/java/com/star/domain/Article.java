package com.star.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by hp on 2017/8/18.
 */
@Entity
@Table(name = "article")
public class Article {
    @Id
    private int id;

    @Column(name = "num") //给此文章点赞的数量
    private int greatNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGreatNum() {
        return greatNum;
    }

    public void setGreatNum(int greatNum) {
        this.greatNum = greatNum;
    }
}
