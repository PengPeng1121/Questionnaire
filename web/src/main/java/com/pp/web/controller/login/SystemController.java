package com.pp.web.controller.login;

import com.pp.web.controller.until.PoiUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaopeng on 2017/3/14.
 */
@Controller
@RequestMapping("/web")
public class SystemController {

    /**
     * 获取系统时间
     *
     * @return
     */
    @RequestMapping(value = "/getTime", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> getUserPrivilege(){
        Map<String,Object> map = new HashMap<>();
        Long timeStamp = System.currentTimeMillis();;
        map.put("timeStamp",timeStamp);
        return map;
    }

    /**
     * 导出课程模板
     */
    @RequestMapping(value = "/exportLessonTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportLessonTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入课程模板（含导入教师与课程关系）_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"课程编码", "课程名称", "课程类型代码(0:实践课;1:理论课)","授课教师名称","学期","授课班级","是否必修(是:否)"};
            ServletOutputStream outputStream = response.getOutputStream();
            Object[][] data = null;
            PoiUtils.export(name, title, columnName, data,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }

    /**
     * 导出模板
     */
    @RequestMapping(value = "/exportStudentTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportStudentTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入学生模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"学号", "姓名", "年级","班级","是否毕业（0或1）"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }

    /**
     * 导出问题模板
     */
    @RequestMapping(value = "/exportQuestionTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportQuestionTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入问卷问题模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"问题类型（简答题或选择题）", "问题内容", "是否必答（是或否）", "选项组","问题权重"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }

    /**
     * 导出模板
     */
    @RequestMapping(value = "/exportStudentAndLessonTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportStudentAndLessonTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入课程与学生关系模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"课程编码", "课程名称", "学生学号","学生姓名","学期","教师名称"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }

    /**
     * 导出模板
     */
    @RequestMapping(value = "/exportTeacherTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportTeacherTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入教师模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"教师编码", "教师名称"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("教师模板导出失败!" + e.getMessage());
        }
    }

    /**
     * 导出模板
     */
    @RequestMapping(value = "/exportAnswerGroupTemplate", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportAnswerGroupTemplate(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入选择组模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"选项组编号", "选项组名称", "答案","答案值","答案得分"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导出选择组模板导出失败!" + e.getMessage());
        }
    }
}
