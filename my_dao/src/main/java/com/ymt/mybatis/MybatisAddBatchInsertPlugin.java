package com.ymt.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.stream.Collectors;

public class MybatisAddBatchInsertPlugin extends PluginAdapter {
    public MybatisAddBatchInsertPlugin() {
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.createBatchInsertMethod("batchInsert", interfaze, introspectedTable);
        this.createBatchInsertMethod("batchUpdateByPrimaryKey", interfaze, introspectedTable);
        this.createBatchInsertMethod("batchUpdateSelectiveByPrimaryKey", interfaze, introspectedTable);
        return true;
    }

    private void createBatchInsertMethod(String methodName, Interface interfaze, IntrospectedTable introspectedTable) {
        Method batchInsertMethod = new Method(methodName);
        batchInsertMethod.setVisibility(JavaVisibility.PUBLIC);
        batchInsertMethod.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + introspectedTable.getBaseRecordType() + ">"), "list"));
        batchInsertMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        this.context.getCommentGenerator().addGeneralMethodComment(batchInsertMethod, introspectedTable);
        interfaze.addMethod(batchInsertMethod);
        interfaze.addImportedType(FullyQualifiedJavaType.getIntInstance());
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        XmlElement rootElement = document.getRootElement();
        this.createBatchInsert(rootElement, tableName, introspectedTable);
        this.createBatchUpdate(rootElement, tableName, introspectedTable, false);
        this.createBatchUpdate(rootElement, tableName, introspectedTable, true);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void createBatchInsert(XmlElement rootElement, String tableName, IntrospectedTable introspectedTable) {
        XmlElement batchInsert = new XmlElement("insert");
        batchInsert.addAttribute(new Attribute("id", "batchInsert"));
        batchInsert.addAttribute(new Attribute("parameterType", "java.util.List"));
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns != null && primaryKeyColumns.size() == 1) {
            IntrospectedColumn pk = (IntrospectedColumn)primaryKeyColumns.get(0);
            if (pk.isAutoIncrement()) {
                batchInsert.addAttribute(new Attribute("useGeneratedKeys", "true"));
                batchInsert.addAttribute(new Attribute("keyProperty", pk.getJavaProperty()));
            }
        }

        StringBuilder foreach = new StringBuilder("(");
        String cols = (String)introspectedTable.getBaseColumns().stream().peek((col) -> {
            foreach.append("#{item.").append(col.getJavaProperty()).append("},");
        }).map(IntrospectedColumn::getActualColumnName).collect(Collectors.joining(","));
        foreach.replace(foreach.length() - 1, foreach.length(), ")");
        this.context.getCommentGenerator().addComment(batchInsert);
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + tableName + "(" + cols + ")");
        OutputUtilities.newLine(sb);
        sb.append("values ");
        batchInsert.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addElement(new TextElement(foreach.toString()));
        batchInsert.addElement(foreachElement);
        rootElement.addElement(batchInsert);
    }

    private void createBatchUpdate(XmlElement rootElement, String tableName, IntrospectedTable introspectedTable, boolean isSelective) {
        XmlElement batchInsert = new XmlElement("insert");
        batchInsert.addAttribute(new Attribute("id", isSelective ? "batchUpdateSelectiveByPrimaryKey" : "batchUpdateByPrimaryKey"));
        batchInsert.addAttribute(new Attribute("parameterType", "java.util.List"));
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String colKey = null;
        String javaKey = null;
        if (primaryKeyColumns != null && primaryKeyColumns.size() == 1) {
            IntrospectedColumn pk = (IntrospectedColumn)primaryKeyColumns.get(0);
            if (pk.isAutoIncrement()) {
                javaKey = pk.getJavaProperty();
                colKey = pk.getActualColumnName();
            }
        }

        this.context.getCommentGenerator().addComment(batchInsert);
        StringBuilder sb = new StringBuilder();
        sb.append("update " + tableName + " set ");
        XmlElement setElement = new XmlElement("set");
        OutputUtilities.newLine(sb);
        String cols = (String)introspectedTable.getBaseColumns().stream().peek((col) -> {
            XmlElement ifEl = new XmlElement("if");
            ifEl.addAttribute(new Attribute("test", "item." + col.getJavaProperty() + " != null"));
            ifEl.addElement(new TextElement(col.getActualColumnName() + "=#{item." + col.getJavaProperty() + "},"));
            setElement.addElement(ifEl);
            OutputUtilities.xmlIndent(sb, 4);
            sb.append(col.getActualColumnName()).append("=").append("#{item.").append(col.getJavaProperty()).append("},");
            OutputUtilities.newLine(sb);
        }).map(IntrospectedColumn::getActualColumnName).collect(Collectors.joining(","));
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
        sb.append("where ").append(colKey).append("=").append("#{item." + javaKey + "}");
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("separator", ";"));
        foreachElement.addAttribute(new Attribute("close", ";"));
        if (isSelective) {
            foreachElement.addElement(new TextElement("update " + tableName + " "));
            foreachElement.addElement(setElement);
            foreachElement.addElement(new TextElement(" where " + colKey + "=#{item." + javaKey + "}"));
        } else {
            foreachElement.addElement(new TextElement(sb.toString()));
        }

        batchInsert.addElement(foreachElement);
        rootElement.addElement(batchInsert);
    }
}
