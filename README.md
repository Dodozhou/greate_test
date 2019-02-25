实现原理
----
1、功能描述：一个用户对同一文章只能点赞一次，第二次就是取消赞
2、建立一个点赞表great，字段有文章ID（aid），点赞用户ID（uid）
3、当有用户进行点赞行为时，使用aid和uid搜索点赞表。

 - 若有该记录，则表示用户已经点过赞，本次点击是取消点赞行为，故删除great表中的该条记录，同时将该文章的点赞数减1。
 - 若无该记录，则表示用户是要点赞，故在great表中添加该记录，同时该文章的点赞数加1。

核心代码分析
------
核心控制器BaseController：

```
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

	//添加事务支持
    @Transactional
    @RequestMapping("/great")
    public String great(@Param("aid") int aid, @Param("uid") int uid, Model model){
        //查询是否有该用户对该文章的点赞记录
        List<Great> list=greatRepository.findByAidAndUid(aid,uid);
        if (list!=null && list.size()>0){
            //如果找到了这条记录，则删除该记录，同时文章的点赞数减1
            Great great=list.get(0);
            //删除记录
            greatRepository.delete(great.getId());
            //文章点赞数减1，查询时使用Mysql行级锁解决并发覆盖问题
            Article article=articleRepository.findByIdForUpdate(aid);
            article.setGreatNum(article.getGreatNum()-1);
            articleRepository.saveAndFlush(article);
        }else {
            //如果没有找到这条记录，则添加这条记录，同时文章点赞数加1
            Great great=new Great();
            great.setAid(aid);
            great.setUid(uid);
            //添加记录
            greatRepository.saveAndFlush(great);
            //文章点赞数加1，查询时使用Mysql行级锁解决并发覆盖问题
            Article article=articleRepository.findByIdForUpdate(aid);
            article.setGreatNum(article.getGreatNum()+1);
            articleRepository.saveAndFlush(article);
        }
        model.addAttribute("details",articleRepository.findAll());
        return "detail";
    }

}

```
Aritcle实体的持久化仓库ArticleRepository
```
@Repository
public interface ArticleRepository extends JpaRepository<Article,Integer>{
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Article findByIdForUpdate(Integer id);
}
```
注意其中使用了@Lock注解以及 LockModeType.PESSIMISTIC_WRITE来让JPA使用数据库层的行级锁，在事务中，该行级锁能解决对同一条记录的并发修改问题。

代码中已经附有详细注解

完整实例的相关信息
---------
为了突出重点，项目前端较为简陋，功能已经通过测试。
项目采用的框架：
1、容器框架：SpringBoot
2、持久层框架：Spring Data JPA
3、渲染框架：Thymeleaf
4、版本控制：Git
5、依赖：Maven
6、数据库：Mysql
数据库建表文件Schema.sql：

```
DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `num` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1,1),(2,0);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `great`
--

DROP TABLE IF EXISTS `great`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `great` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `great`
--

LOCK TABLES `great` WRITE;
/*!40000 ALTER TABLE `great` DISABLE KEYS */;
INSERT INTO `great` VALUES (5,1,1);
/*!40000 ALTER TABLE `great` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
```



项目已经上传到Github，欢迎大家克隆学习。
项目地址：[https://github.com/Dodozhou/greate_test](https://github.com/Dodozhou/greate_test)
（若没有Github账户的同学，可以在评论区留言你的邮箱，我会将zip压缩包发到你的邮箱）（喜欢的请别忘了点赞哟，这是对我莫大的鼓励:-D）

特别说明：本文章的目的只是单纯向大家说明点赞这个功能的实现思路。为了保证逻辑尽量清晰和简单，因而并没有涉及到性能优化和高并发访问。示例代码中的锁机制能保证并发访问下的安全性，但会对系统并发性能产生一定的影响，但在一般系统中，由于大量用户同时对同一文章集中点赞的情况并不常见，因此性能损耗扔在可以接受的范围内。
如果大家在使用过程中确实有高并发的需要，那么可以考虑使用Redis这类缓存数据库来替代mysql。Redis是高性能的内存KV数据库，且是单线程运行的，因此性能和安全性问题都能得到完美的解决。
