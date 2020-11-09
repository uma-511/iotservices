package com.lgmn.iotserver.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.fastjson.JSONObject;
import com.lgmn.iotserver.model.PrintPojo;
import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelUtils {
    public static String exportLabelExcel(String templatePath, PrintPojo printPojo) {

        File templateFile=new File(templatePath);
        if(!templateFile.exists()){
            System.out.println("找不到模板文件");
            try {
                throw new FileNotFoundException("找不到模板文件");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 加载模板
        TemplateExportParams params = new TemplateExportParams(templatePath);
        // 生成workbook 并导出
        Workbook workbook = null;
        try {
            workbook = ExcelExportUtil.exportExcel(params, objectToMap(printPojo));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String savePath="H:\\UMa\\LabelRecord\\";
        File savefile = new File(savePath);
        if (!savefile.exists()) {
            boolean result = savefile.mkdirs();
            System.out.println("目录不存在,进行创建,创建" + (result ? "成功!" : "失败！"));
        }
        String filePath = savePath + printPojo.getLabelNum() + ".xls";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    /**
     * 对象转换为Map<String, Object>的工具类
     *
     * @param obj
     *            要转换的对象
     * @return map
     * @throws IllegalAccessException
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>(16);
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            /*
             * Returns the value of the field represented by this {@code Field}, on the
             * specified object. The value is automatically wrapped in an object if it
             * has a primitive type.
             * 注:返回对象该该属性的属性值，如果该属性的基本类型，那么自动转换为包装类
             */
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }

    public static Map<String, String> objectToMap2(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>(16);
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            /*
             * Returns the value of the field represented by this {@code Field}, on the
             * specified object. The value is automatically wrapped in an object if it
             * has a primitive type.
             * 注:返回对象该该属性的属性值，如果该属性的基本类型，那么自动转换为包装类
             */
            String value = String.valueOf(field.get(obj));
            map.put(fieldName, value);
        }
        return map;
    }

    public static Map<String,String> objectToMap3(Object obj){
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
        Map<String,String> data = new HashMap<>();

        Iterator it =jsonObject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            data.put(entry.getKey(), entry.getValue().toString());
        }

        return data;
    }
}