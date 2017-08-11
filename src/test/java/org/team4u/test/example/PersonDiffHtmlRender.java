package org.team4u.test.example;

import com.xiaoleilu.hutool.io.FileUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.javers.core.diff.Diff;
import org.team4u.diff.definiton.PropertyDefinition;
import org.team4u.diff.definiton.PropertyDefinitionBuilder;
import org.team4u.diff.render.ChangeValues;
import org.team4u.diff.render.ChangeValuesRender;
import org.team4u.diff.render.ValueFormatterRegistry;
import org.team4u.kit.core.util.CollectionExUtil;
import org.team4u.kit.core.util.ValueUtil;
import org.team4u.test.TestUtil;
import org.team4u.test.formatter.MyValueFormatter;

import java.util.List;
import java.util.Map;

/**
 * @author Jay Wu
 */
public class PersonDiffHtmlRender {

    private Map<String, PropertyDefinition> definitionMap =
            new PropertyDefinitionBuilder("org.team4u.test.model").build();

    public PersonDiffHtmlRender() {
        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);
    }

    public static void main(String[] args) {
        Diff diff = TestUtil.createDiff();
        String md = (new PersonDiffHtmlRender().render(diff));
        System.out.println(md);

        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        html = FileUtil.readUtf8String("person.html").replace("${body}", html);
        FileUtil.writeUtf8String(html, "../result.html");

    }

    public String render(Diff diff) {
        Map<String, ?> map = ChangeValuesRender.renderToPathMap(definitionMap, diff);
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
        switch (CollectionExUtil.getLast(value.getPropertyIdFragments())) {
            case "+":
                builder.append(" ").append(value.getNewValue())
                        .append("\n");
                break;

            case "-":
                builder.append(" ").append(value.getOldValue())
                        .append("\n");
                break;

            default:
                builder.append(" 变更前值：").append(value.getOldValue())
                        .append(" 变更后值：").append(value.getNewValue())
                        .append("\n");
        }
    }
}