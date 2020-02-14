package com.ymt.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.springframework.util.StringUtils;

public class CommentGenerator extends DefaultCommentGenerator {
    public CommentGenerator() {
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuilder comment = new StringBuilder();
        field.addJavaDocLine("/**");
        if (StringUtils.hasText(introspectedColumn.getRemarks())) {
            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        }

        comment.append(" * 表字段 ： ");
        comment.append(introspectedTable.getFullyQualifiedTable());
        comment.append(" . ");
        comment.append(introspectedColumn.getActualColumnName());
        field.addJavaDocLine(comment.toString());
        this.addJavadocTag(field, false);
        field.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        super.addClassComment(innerClass, introspectedTable);
    }
}
