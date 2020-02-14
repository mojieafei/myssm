package com.ymt.mybatis;

import org.apache.commons.collections4.ListUtils;
import org.assertj.core.util.Lists;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyBatisGeneratorTool {
    public static String curModule = "persistence";

    public MyBatisGeneratorTool() {
    }

    public static void main(String[] args) throws Exception {
        generate("resource_machine_idc", "");
    }

    public static void generate(String tableNames, String dbName) throws Exception {
        Arrays.asList(tableNames.split(",")).forEach((tableName) -> {
            byte var3 = -1;
            dbName.hashCode();
            switch(var3) {
                default:
                    try {
                        generate(tableName, "classpath:MybatisGenConfig.xml", "com.alibaba.ais.tools.mybatis.entity", "com.alibaba.ais.tools.mybatis.dao");
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }

            }
        });
    }

    public static void generate(String tableName, String mybatisGenConfig, String entityPackage, String daoPackage) throws Exception {
        ClassPathResource classpathRoot = new ClassPathResource(".");
        System.out.println(classpathRoot.getFile().getAbsolutePath());
        String targetModulePath = classpathRoot.getFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();
        System.out.println(targetModulePath);
        String targetPath = targetModulePath + File.separator + curModule + File.separator + "src" + File.separator + "main" + File.separator + "java";
        File genConf = ResourceUtils.getFile(mybatisGenConfig);
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("mybatis-gen-param.properties"));
        List<String> warnings = Lists.newArrayList();
        ConfigurationParser configurationParser = new ConfigurationParser(properties, warnings);
        Configuration configuration = configurationParser.parseConfiguration(genConf);
        Context context = configuration.getContext("MysqlContext");
        PluginConfiguration customPlugin = new PluginConfiguration();
        customPlugin.setConfigurationType(CustomMybatisPlugin.class.getName());
        context.addPluginConfiguration(customPlugin);
        PluginConfiguration batchInsertPlugin = new PluginConfiguration();
        batchInsertPlugin.setConfigurationType(MybatisAddBatchInsertPlugin.class.getName());
        context.addPluginConfiguration(batchInsertPlugin);
        PluginConfiguration serializablePluginConf = new PluginConfiguration();
        serializablePluginConf.setConfigurationType(SerializablePlugin.class.getName());
        context.addPluginConfiguration(serializablePluginConf);
        JavaModelGeneratorConfiguration modelGenConf = context.getJavaModelGeneratorConfiguration();
        modelGenConf.setTargetProject(targetPath);
        modelGenConf.setTargetPackage(entityPackage);
        JavaClientGeneratorConfiguration clientGenConf = context.getJavaClientGeneratorConfiguration();
        clientGenConf.setTargetProject(targetPath);
        clientGenConf.setConfigurationType("XMLMAPPER");
        clientGenConf.setTargetPackage(daoPackage);
        SqlMapGeneratorConfiguration sqlMapGenConf = context.getSqlMapGeneratorConfiguration();
        sqlMapGenConf.setTargetProject(targetPath);
        sqlMapGenConf.setTargetPackage(daoPackage);
        Iterator var19 = context.getTableConfigurations().iterator();

        while(var19.hasNext()) {
            TableConfiguration tableConfiguration = (TableConfiguration)var19.next();
            tableConfiguration.setTableName(tableName);
            tableConfiguration.setCountByExampleStatementEnabled(true);
            tableConfiguration.setSelectByPrimaryKeyStatementEnabled(true);
            tableConfiguration.setDeleteByExampleStatementEnabled(true);
            tableConfiguration.setSelectByExampleStatementEnabled(true);
            tableConfiguration.setUpdateByExampleStatementEnabled(true);
            tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(true);
        }

        DefaultShellCallback defaultShellCallback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, defaultShellCallback, warnings);
        myBatisGenerator.generate((ProgressCallback)null);
        System.out.println("warnings: ");
        List var10000 = ListUtils.emptyIfNull(warnings);
        PrintStream var10001 = System.out;
        var10000.forEach(var10001::println);
    }
}
