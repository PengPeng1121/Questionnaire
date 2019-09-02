/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.AnswerGroup;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.AnswerGroupService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选项组表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/answergroup")
public class AnswerGroupController extends BaseController {

    Logger log = LoggerFactory.getLogger(AnswerGroupController.class.getName());

    @Autowired
    AnswerGroupService answerGroupService;

    /**
     * 逻辑删除数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> delete(Long id) {
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }
        int rows = this.answerGroupService.delete(id, account.getUserCode());
        if (rows == 1) {
            map.put("status",200);
            return map;
        }
        map.put("msg","删除失败");
        return map;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(AnswerGroup answerGroupQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        // 设置合理的参数
        if (size < 1) {
            size = 20;
        } else if (size > 100) {
            size = 100;
        }
        // 开始页码
        int pageIndex = page - 1;
        // 排序
        Sort sort = null;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.desc(sortName);
        } else {
            sort = Sort.asc(sortName);
        }
        // 创建分页对象
        Page<AnswerGroup> groupPage = new Page<AnswerGroup>(pageIndex, size, sort);
        // 执行查询
        groupPage = this.answerGroupService.selectPage(answerGroupQuery, groupPage);
        // 返回查询结果
        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> returnMap = new HashMap<String, Object>();
        if (groupPage != null) {
            map.put("data", groupPage.getContent());
            map.put("count", groupPage.getTotalElements());
            map.put("page", groupPage.getPageIndex() + 1);
            map.put("limit", groupPage.getPageSize());
            returnMap.put("data", map);
            returnMap.put("status", 200);
        } else {
            returnMap.put("data", map);
            returnMap.put("msg", "没有查询到数据");
            returnMap.put("status", 300);
        }
        return returnMap;
    }

    @RequestMapping(value = "/importAnswerGroup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importAnswerGroup(@RequestParam("fileUpload") MultipartFile file) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status", 302);
        int rows = 0;// 实际导入行数
        // 最大导入条数
        Integer importNum = 5000;
        String fileName = file.getOriginalFilename();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        int fileNameLength = fileName.length();
        String prefix = fileName.substring(lastIndexOfDot + 1, fileNameLength);
        Account account = AccountUtils.getCurrentAccount();
        try {
            if (prefix.toLowerCase().equals("xlsx") || prefix.toLowerCase().equals("xls")) {
                InputStream in = file.getInputStream();
                HSSFWorkbook wb = new HSSFWorkbook(in);
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条！！");
                    return map;
                }
                List<AnswerGroup> answerGroups = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        AnswerGroup answerGroup = new AnswerGroup();
                       if(checkIsEmpty(row,i,answerGroup)){
                           answerGroups.add(answerGroup);
                       }else {
                           throw new IllegalArgumentException("导入数据不满足格式，导入失败");
                       }
                    }
                }
                if(CollectionUtils.isEmpty(answerGroups)){
                    throw new IllegalArgumentException("导入数据为空，导入失败");
                }
                try {
                    this.answerGroupService.insert(answerGroups,account.getUserCode());
                } catch (Exception r) {
                    throw new RuntimeException("写入失败：" + r.getMessage());
                }
                map.put("status", 200);
                map.put("msg", "数据导入全部成功！");
            } else {
                map.put("msg", "导入失败说明：文件格式不正确，仅支持xlsx和xls文件！");
            }
        } catch (Exception e) {
            log.error("导入失败说明：数据导入异常！msg:"+e.getMessage());
            map.put("msg", "导入失败说明：数据导入异常！msg:"+e.getMessage());
            return map;
        }
        return map;
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private Boolean checkIsEmpty( HSSFRow row, int i, AnswerGroup answerGroup) {
        StringBuilder reason = new StringBuilder("");
        reason.append("第").append(i + 1).append("行的");
        Boolean flag = true;
        try {
            if (row.getCell(0) == null || row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("选项组编号;");
                flag = false;
            }
            if (row.getCell(1) == null || row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("选项组名称;");
                flag = false;
            }
            if (row.getCell(2) == null || row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("答案;");
                flag = false;
            }
            if (row.getCell(3) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("答案值;");
                flag = false;
            }
            if (row.getCell(4) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("答案得分;");
                flag = false;
            }
            reason.append("不能为空");
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            return false;
        }

        //20190902
        //去掉结尾.0
        if(row.getCell(0).toString().endsWith(".0")) {
            answerGroup.setGroupCode(row.getCell(0).toString().substring(0,row.getCell(0).toString().indexOf(".")));
        }else {
            answerGroup.setGroupCode(row.getCell(0).toString().trim());
        }

        if(row.getCell(1).toString().endsWith(".0")) {
            answerGroup.setGroupName(row.getCell(1).toString().substring(0,row.getCell(1).toString().indexOf(".")));
        }else {
            answerGroup.setGroupName(row.getCell(1).toString().trim());
        }

        if(row.getCell(2).toString().endsWith(".0")) {
            answerGroup.setAnswer(row.getCell(2).toString().substring(0,row.getCell(2).toString().indexOf(".")));
        }else {
            answerGroup.setAnswer(row.getCell(2).toString().trim());
        }

        if(row.getCell(3).toString().endsWith(".0")) {
            answerGroup.setAnswerValue(row.getCell(3).toString().substring(0,row.getCell(3).toString().indexOf(".")));
        }else {
            answerGroup.setAnswerValue(row.getCell(3).toString().trim());
        }

        try{

            if(row.getCell(4).toString().endsWith(".0")) {
                answerGroup.setAnswerScore(Integer.parseInt(row.getCell(4).toString().substring(0,row.getCell(4).toString().indexOf("."))));
            }else {
                answerGroup.setAnswerScore(Integer.parseInt(row.getCell(4).toString().trim()));
            }

        }catch (Exception e){
            log.error("答案得分输入的应该是数字，导入失败！！msg:"+e.getMessage());
            throw new IllegalArgumentException("答案得分输入的应该是数字，导入失败！");
        }
        return true;
    }
}