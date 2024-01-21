package com.qkwl.web.front.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.QuestionIsAnswerEnum;
import com.qkwl.common.dto.Enum.QuestionStatusEnum;
import com.qkwl.common.dto.Enum.QuestionTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FQuestion;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.rpc.user.IQuestionService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class FrontQuestionJsonController extends JsonBaseController {

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private IUserService userService;

    /**
     * 提交问题
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/online_help/help_submit",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult submitQuestion(
            @RequestParam(required = true) int questiontype,
            @RequestParam(required = true) String questiondesc) {
        String fquestionType = QuestionTypeEnum.getValueByCode(questiontype);
        questiondesc = HtmlUtils.htmlEscape(questiondesc);
        if (questiondesc.trim().equals("") && fquestionType == null && questiondesc.trim().length() < 10) { 
            return ReturnResult.FAILUER(I18NUtils.getString("com.activity.error.10006")); 
        }
        try {
            String ip = HttpRequestUtils.getIPAddress();
            FUser fuser = getCurrentUserInfoByToken();
            fuser = userService.selectUserById(fuser.getFid());
            FQuestion question = new FQuestion();
            question.setFtype(questiontype);
            question.setFdesc(questiondesc);
            question.setFuid(fuser.getFid());
            question.setFstatus(QuestionStatusEnum.NOT_SOLVED.getCode());
            question.setFcreatetime(Utils.getTimestamp());
            question.setFisanswer(QuestionIsAnswerEnum.NOT);
            question.setVersion(0);
            question.setFname(fuser.getFrealname());
            question.setFtelephone(fuser.getFtelephone());
            questionService.insertQuestion(question, 0, ip);
        } catch (Exception e) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
        }
        return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
    }

    /**
     * 删除提问
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/online_help/help_delete",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult delQuestion(@RequestParam(required = false, defaultValue = "0") Integer fid) {
        FUser fuser = getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());
        if (fid == 0 || null == fuser || fuser.getFid() <= 0) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
        }
        if (!questionService.deleteQuestionById(fid, true, fuser.getFid())) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.activity.error.10007")); 
        }
        return ReturnResult.SUCCESS();
    }

    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/online_help/index_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult question()  {
        Map<Integer, Object> fquestiontypes = new LinkedHashMap<Integer, Object>();
        for (QuestionTypeEnum questionTypeEnum : QuestionTypeEnum.values()) {
            fquestiontypes.put(questionTypeEnum.getCode(), I18NUtils.getString("question.type.enum" + questionTypeEnum.getCode())); 
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fquestiontypes", fquestiontypes); 
        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/online_help/help_list_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult questionColumn(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage
    ) {
        FUser fuser = getCurrentUserInfoByToken();
        JSONObject jsonObject = new JSONObject();
        if (fuser != null) {
            Pagination<FQuestion> fquestion = new Pagination<FQuestion>(currentPage, Constant.QuestionRecordPerPage, "/online_help/help_list.html?"); 
            FQuestion operation = new FQuestion();
            operation.setFuid(fuser.getFid());
            fquestion = questionService.selectPageQuestionList(fquestion, operation);
            jsonObject.put("currentPage", currentPage); 
            jsonObject.put("list", fquestion); 
        }
        return ReturnResult.SUCCESS(jsonObject);
    }


}
