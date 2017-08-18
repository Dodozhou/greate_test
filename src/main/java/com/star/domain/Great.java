package com.star.domain;

import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by hp on 2017/8/18.
 */
@Entity
@Table(name = "great")
public class Great {
    @Id
    private int id; //记录的id
    @Column
    private int aid; //article id 文章的ID
    @Column
    private int uid; //user id 点赞用户的ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
