package com.ymt.util;

import com.ymt.mybatis.CustomMybatisPlugin;
import com.ymt.mybatis.MyBatisGeneratorTool;

import java.util.Arrays;

public class MyBatisGenerator {

    public static void main(String[] args) throws Exception {
        CustomMybatisPlugin.BASE_DAO_CLASS_NAME = "BaseDao";
        MyBatisGeneratorTool.curModule = "com/ymt";
        // generate("ems_verify_stage_notification_config", "");
        generate( "user" , "local");
    }

    public static void generate(String tableNames, String dbName) throws Exception {
        Arrays.asList(tableNames.split(",")).forEach(tableName -> {
            switch (dbName) {
                default:
                    try {
                        MyBatisGeneratorTool.generate(tableName, "classpath:MybatisGenConfig.xml",
                                                      "my_dao.model",
                                                      "my_dao.mapper");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
    }
}
