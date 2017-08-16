package org.team4u.diff.render;

import org.team4u.diff.definiton.DefinitionBuilder;
import org.team4u.diff.definiton.DefinitionModel;
import org.team4u.kit.core.util.ValueUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class SimpleMarkdownRender {

    private ChangeValuesRender render;

    public SimpleMarkdownRender(String packageName, Object oldBean, Object newBean) {
        Map<String, DefinitionModel> definitionMap = new DefinitionBuilder(packageName).build();
        render = new ChangeValuesRender(definitionMap, oldBean, newBean);
    }

    public String render() {
        Map<String, ?> map = render.renderToPathMap();
        StringBuilder builder = new StringBuilder();
        toMarkdown(map, builder, null);
        return builder.toString();
    }

    // A/[B/[B1/[B1X,B1Y],B2+],C]
    @SuppressWarnings("unchecked")
    private void toMarkdown(Map<String, ?> data, StringBuilder builder, String retract) {
        retract = ValueUtil.defaultIfNull(retract, "");

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            builder.append(retract).append("* ").append("【").append(entry.getKey()).append("】");
            if (entry.getValue() instanceof Map) {
                builder.append("\n");
                toMarkdown((Map<String, ?>) entry.getValue(), builder, retract + "  ");
            } else if (entry.getValue() instanceof List) {
                builder.append("\n");
                toMarkdown((List<?>) entry.getValue(), builder, retract + "  ");
            } else if (entry.getValue() instanceof ChangeValues.Value) {
                toMarkdown((ChangeValues.Value) entry.getValue(), builder);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void toMarkdown(List<?> data, StringBuilder builder, String retract) {
        for (Object e : data) {
            builder.append(retract).append("* ");

            if (e instanceof Map) {
                toMarkdown((Map<String, ?>) e, builder, null);
            } else if (e instanceof ChangeValues.Value) {
                toMarkdown((ChangeValues.Value) e, builder);
            }
        }
    }

    private void toMarkdown(ChangeValues.Value value, StringBuilder builder) {
        if (value.getOwner() == null) {
            builder.append(" ");

            if (value.getNewValue() == null) {
                builder.append("<删除> <u>").append(BeanRender.renderWholeValue(value.getOldValue())).append("</u>");
            } else {
                builder.append("<新增> <u>").append(BeanRender.renderWholeValue(value.getNewValue())).append("</u>");
            }
        } else {
            builder.append(" 原始值：<u>")
                    .append(ValueUtil.defaultIfNull(value.getOldValue(), "无")).append("</u>")
                    .append("， 变更值：<u>")
                    .append(ValueUtil.defaultIfNull(value.getNewValue(), "无")).append("</u>");
        }

        builder.append("\n");
    }
}