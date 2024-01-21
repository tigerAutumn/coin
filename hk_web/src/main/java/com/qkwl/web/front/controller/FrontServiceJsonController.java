package com.qkwl.web.front.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.common.PageHelper;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.article.ArticleService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class FrontServiceJsonController extends JsonBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(FrontServiceJsonController.class);

    @Autowired
    private RedisHelper redisHelper;
    
    @Autowired
    private ArticleService articleService;

    /**
     * 通知列表
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/index_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult ourService(
            @RequestParam(required = false, defaultValue = "5") Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {// 12,5,5

        FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
        //System.out.println(LuangeHelper.getLan(request));
        if (systemLan == null) {
            return ReturnResult.FAILUER(""); 
        }

        FArticleType farticletype = redisHelper.getArticleType(id, systemLan.getFid());
        if (farticletype == null) {

            return ReturnResult.FAILUER(""); 
        }
        List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid() , 2, farticletype.getFid(), currentPage,
                WebConstant.BCAgentId);

        int total = redisHelper.getArticlesPageCont(systemLan.getFid() ,2, farticletype.getFid(), WebConstant.BCAgentId);
        String pagin = PageHelper.generatePagin(total / 10 + (total % 10 == 0 ? 0 : 1), currentPage,
                "/notice/index?id=" + id + "&");  //$NON-NLS-2$

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pagin", pagin); 
        jsonObject.put("farticles", farticles); 
        jsonObject.put("id", id); 
        return ReturnResult.SUCCESS(jsonObject);
    }

    
    /**
     * 帮助中心
     * 
     * source 1.pc端 2.app端
     * 
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/help/index",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult helpIndex(@RequestParam(required = false, defaultValue = "1") Integer source){
    	try {
            FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
            if (systemLan == null) {
                return ReturnResult.FAILUER(""); 
            }
            List<FArticleType> articleTypeList = redisHelper.getArticleTypeList(systemLan.getFid());
            if (articleTypeList == null) {
                return ReturnResult.FAILUER(""); 
            }
            JSONArray jsonArray = new JSONArray();
            for (FArticleType fArticleType : articleTypeList) { 
            	JSONObject jsonObject = new JSONObject();
            	jsonObject.put("typeId", fArticleType.getFtypeid()); 
            	jsonObject.put("title", fArticleType.getFname()); 
            	jsonObject.put("logo", fArticleType.getImgUrl()); 
            	List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid() , source, fArticleType.getFid(), 1, WebConstant.BCAgentId);
            	if(farticles != null) {
            		JSONArray ja = new JSONArray();
            		for (FArticle fArticle : farticles) {
            		JSONObject jo = new JSONObject();
            		jo.put("articleId", fArticle.getFid()); 
            		jo.put("childrenTitle", fArticle.getFtitle()); 
            		ja.add(jo);
    				}
            		jsonObject.put("list", ja); 
            	}
            	jsonArray.add(jsonObject);
    		}
            return ReturnResult.SUCCESS(jsonArray);
		} catch (Exception e) {
			logger.error("访问 /notice/help/index 异常",e); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
    }
    
    /**
     * 通过文章类型获取文章列表
     * 
     * source 1.pc端 2.app端
     * type   新闻类型
     * currentPage 当前页
     * 
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/help/type",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult helpType(
    		@RequestParam(required = false, defaultValue = "1") Integer source,
    		@RequestParam(required = false, defaultValue = "5") Integer type,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage
    		){
    	try {
            FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
            if (systemLan == null) {
                return ReturnResult.FAILUER(""); 
            }
            int articlesPageCont = redisHelper.getArticlesPageCont(systemLan.getFid(), source, type, WebConstant.BCAgentId);
            List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid() , source, type, currentPage, WebConstant.BCAgentId);
            JSONObject jo = new JSONObject();
            jo.put("total", articlesPageCont); 
            jo.put("page", currentPage); 
            jo.put("list", farticles); 
            return ReturnResult.SUCCESS(jo);
		} catch (Exception e) {
			logger.error("访问 /notice/help/type 异常",e); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
    }
    
    /**
     * 通过文章id获取文章
     * 
     * source 1.pc端 2.app端
     * type   新闻类型
     * currentPage 当前页
     * 
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/help/detail",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult helpDetail(@RequestParam(required = true) Integer id) throws Exception {
    	try {
    		FArticle articleById = redisHelper.getArticleById(id);
            if(articleById == null) {
            	return ReturnResult.FAILUER(""); 
            }
            return ReturnResult.SUCCESS(articleById);
		} catch (Exception e) {
			logger.error("访问 /notice/help/detail 异常",e); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
    }
    
    /**
     * 搜索文章
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/search_article_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult searchArticle(
    		@RequestParam(required = true)String keyword ,
    		@RequestParam(required = false,defaultValue = "1")Integer page
    		) throws Exception {
    	try {
    		PageInfo<FArticle> searchArticle = articleService.searchArticle(keyword, page, Constant.webPageSize);
            return ReturnResult.SUCCESS(searchArticle);
		} catch (Exception e) {
			logger.error("访问 /notice/help/detail 异常",e); 
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
    }
    
    /**
     * 通知详情
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/notice/detail_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult article(Integer id) throws Exception {
        FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
        FArticle farticle = redisHelper.getArticleById(systemLan.getFid() ,2, id, WebConstant.BCAgentId);
        if (farticle == null) {
            return ReturnResult.FAILUER(""); 

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticle", farticle); 
        return ReturnResult.SUCCESS(jsonObject);
    }
    


    /**
     * app页面
     *
     * @return
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/service/appnews_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult AppNews(
            @RequestParam(required = false, defaultValue = "2") Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
        FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
        int pagesize = 10;
        FArticleType farticletype = redisHelper.getArticleType(id, 1);
        List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid() , 1, farticletype.getFid(), currentPage, WebConstant.BCAgentId);
        int total = redisHelper.getArticlesPageCont(systemLan.getFid(),1, farticletype.getFid(), WebConstant.BCAgentId);
        int totalpage = total / pagesize + (total % pagesize == 0 ? 0 : 1);
        int nextpage = 0;
        if (currentPage <= totalpage - 1) {
            nextpage = currentPage + 1;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticles", farticles); 
        jsonObject.put("nextpage", nextpage); 
        jsonObject.put("id", id); 
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * app页面详情
     *
     * @return
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/service/appnew_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult AppNew(int id) throws Exception {
        FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
        FArticle farticle = redisHelper.getArticleById(systemLan.getFid() ,1, id, WebConstant.BCAgentId);
        if (farticle == null) {
            return ReturnResult.FAILUER(""); 
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticle", farticle); 
        return ReturnResult.SUCCESS(jsonObject);
    }


}
