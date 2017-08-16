package org.team4u.test.example;

import com.xiaoleilu.hutool.io.FileUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.javers.core.diff.Diff;
import org.team4u.diff.definiton.DefinitionBuilder;
import org.team4u.diff.definiton.DefinitionModel;
import org.team4u.diff.render.BeanRender;
import org.team4u.diff.render.ChangeValues;
import org.team4u.diff.render.ChangeValuesRender;
import org.team4u.diff.render.ValueFormatterRegistry;
import org.team4u.kit.core.util.ValueUtil;
import org.team4u.test.TestUtil;
import org.team4u.test.formatter.MyValueFormatter;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class PersonDiffHtmlRender {

    private Map<String, DefinitionModel> definitionMap =
            new DefinitionBuilder("org.team4u.test.model").build();

    private ChangeValuesRender render;

    private Object oldBean;
    private Object newBean;

    public PersonDiffHtmlRender(Object oldBean, Object newBean) {
        this.newBean = newBean;
        this.oldBean = oldBean;
        render = new ChangeValuesRender(definitionMap, oldBean, newBean);

        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);
    }

    public static void main(String[] args) {
        Diff diff = TestUtil.createDiff();
        String md = (new PersonDiffHtmlRender(TestUtil.createPerson1(), TestUtil.createPerson2()).render());
        System.out.println(md);

        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        html = FileUtil.readUtf8String("person.html").replace("${body}", html);
        FileUtil.writeUtf8String(html, "../result.html");
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

    public String render() {
        Map<String, ?> map = render.renderToPathMap();
        StringBuilder builder = new StringBuilder();
        toMarkdown(map, builder, null);
        return builder.toString();
    }

    private void toMarkdown(ChangeValues.Value value, StringBuilder builder) {
        if (value.getOwner() == null) {
            builder.append(" ");

            if (value.getNewValue() == null) {
                builder.append("删除 ").append(BeanRender.renderWholeValue(value.getOldValue()));
            } else {
                builder.append("新增 ").append(BeanRender.renderWholeValue(value.getNewValue()));
            }
        } else {
            builder.append(" 变更前值：").append(ValueUtil.defaultIfNull(value.getOldValue(), "无"))
                    .append(", 变更后值：").append(ValueUtil.defaultIfNull(value.getNewValue(), "无"));
        }

        builder.append("\n");
    }
}