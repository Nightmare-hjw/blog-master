package com.jax.blog.controller.admin;

import com.jax.blog.controller.BaseController;
import com.jax.blog.model.Article;
import com.jax.blog.service.URLMapper;
import com.jax.blog.service.article.ArticleService;
import com.jax.blog.utils.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName BlogAdminController
 * @Description 博客文章信息相关接口
 * @Author huangjw
 * @Date 2018/9/5 16:40
 * @Version 1.0
 **/
@Controller
public class ArticleController extends BaseController {
    @Autowired
    ArticleService articleService;

    @RequestMapping(value = URLMapper.ADMIN_ARTICLE_QUERYALL, method = RequestMethod.POST)
    @ResponseBody
    public List<Article> queryArticleList() throws Exception {
        return articleService.queryArticleList();
    }

    @RequestMapping(value = URLMapper.ADMIN_ARTICLE_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public Article queryArticle(@RequestParam("id") Integer aid) throws Exception {
        return articleService.queryArticleById(aid);
    }

    @RequestMapping(value = URLMapper.ADMIN_ARTICLE_MODIFY, method = RequestMethod.POST)
    @ResponseBody
    public APIResponse updateArticle(Article article) throws Exception {
        return APIResponse.fail("更新文章操作失败");
    }

    @RequestMapping(value = URLMapper.ADMIN_ARTICLE_DELETE, method = RequestMethod.POST)
    @ResponseBody
    public APIResponse deleteArticle(@RequestParam("aid") Integer aid) throws Exception {
        return APIResponse.fail("删除文章操作失败");
    }

    @RequestMapping(value = URLMapper.ADMIN_ARTICLE_BATCHDELETE, method = RequestMethod.POST)
    @ResponseBody
    public APIResponse batchDeleteArticle(List<Integer> list) throws Exception {
        return APIResponse.fail("批量删除文章操作失败");
    }
}
