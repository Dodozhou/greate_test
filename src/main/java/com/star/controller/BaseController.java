package com.star.controller;

import com.star.domain.Article;
import com.star.domain.Great;
import com.star.repository.ArticleRepository;
import com.star.repository.GreatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by hp on 2017/8/18.
 */
@Controller
public class BaseController {
    private final GreatRepository greatRepository;
    private final ArticleRepository articleRepository;
    //Spring团队推荐的做法是用构造器来引入依赖，而不是直接使用@Autowired引入
    @Autowired
    public BaseController(GreatRepository greatRepository,
                          ArticleRepository articleRepository) {
        this.greatRepository = greatRepository;
        this.articleRepository=articleRepository;
    }

    @RequestMapping({"/","/index"})
    public String index(){
        return "index";
    }


    @RequestMapping("/great")
    public String great(@Param("aid") int aid, @Param("uid") int uid, Model model){
        //查询是否有该用户对该文章的点赞记录
        List<Great> list=greatRepository.findByAidAndUid(aid,uid);
        if (list!=null && list.size()>0){
            //如果找到了这条记录，则删除该记录，同时文章的点赞数减1
            Great great=list.get(0);
            //删除记录
            greatRepository.delete(great.getId());
            //文章点赞数减1
            Article article=articleRepository.findOne(aid);
            article.setGreatNum(article.getGreatNum()-1);
            articleRepository.saveAndFlush(article);
        }else {
            //如果没有找到这条记录，则添加这条记录，同时文章点赞数加1
            Great great=new Great();
            great.setAid(aid);
            great.setUid(uid);
            //添加记录
            greatRepository.saveAndFlush(great);
            //文章点赞数加1
            Article article=articleRepository.findOne(aid);
            article.setGreatNum(article.getGreatNum()+1);
            articleRepository.saveAndFlush(article);
        }
        model.addAttribute("details",articleRepository.findAll());
        return "detail";
    }

}
