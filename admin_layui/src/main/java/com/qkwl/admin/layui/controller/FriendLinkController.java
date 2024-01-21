package com.qkwl.admin.layui.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.LinkTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.dto.web.FFriendLinkDTO;
import com.qkwl.common.dto.web.SaveLinkDTO;
import com.qkwl.common.dto.web.UpdateLinkDTO;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

/**
 * 友情链接
 * Created by wangchen on 2017-04-07.
 */
@Controller
public class FriendLinkController extends WebBaseController {
    @Autowired
    private IAdminSettingService adminSettingService;

    @RequestMapping("/friendLink/linkList")
    public ModelAndView linkList(
            @RequestParam(value = "pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value = "keywords", required=false) String keywords,
            @RequestParam(value = "orderField", required=false,defaultValue="fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", required=false,defaultValue="desc") String orderDirection
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("friendLink/linkList");

        Pagination<FFriendLinkDTO> page=new Pagination<FFriendLinkDTO>(currentPage, Constant.adminPageSize);
        FFriendLink friendLink=new FFriendLink();
        if (keywords != null && keywords.trim().length() > 0) {
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        page=adminSettingService.selectLinkPageList(page,friendLink);

        modelAndView.addObject("linkList", page);
        return modelAndView;
    }

    @RequestMapping("friendLink/goLinkJSP")
    public ModelAndView goLinkJSP(
            @RequestParam(value = "url", required=false,defaultValue="") String url,
            @RequestParam(value = "fid", required=false,defaultValue="0") Integer fid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (fid != null && fid > 0) {
          FFriendLinkDTO friendlink = this.adminSettingService.goLinkJSP(fid);
            modelAndView.addObject("friendlink", friendlink);
        }
        Map<Integer,String> map = new HashMap<Integer,String>();
        map.put(LinkTypeEnum.LINK_VALUE, LinkTypeEnum.getEnumString(LinkTypeEnum.LINK_VALUE));
        modelAndView.addObject("linkTypeMap", map);
        return modelAndView;
    }

    @RequestMapping(value="friendLink/saveLink",method=RequestMethod.POST)
    @ResponseBody
    public ReturnResult saveLink(SaveLinkDTO dto) {

        if(dto.getForder()==null){
            return ReturnResult.FAILUER("请设置序号！");
        }
        this.adminSettingService.saveLink(dto);
        return ReturnResult.SUCCESS("新增成功");
    }

    @RequestMapping("friendLink/deleteLink")
    @ResponseBody
    public ReturnResult deleteLink(@RequestParam(required=false,defaultValue="1") Integer fid) {
        this.adminSettingService.deleteLink(fid);
        return ReturnResult.SUCCESS("删除成功");
    }

    @RequestMapping(value="friendLink/updateLink",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult updateLink(UpdateLinkDTO dto)  {

        if(dto.getForder()==null){
            return ReturnResult.FAILUER("请设置序号！");
        }

        adminSettingService.updateLink(dto);

        return ReturnResult.SUCCESS("修改成功");
    }
}
