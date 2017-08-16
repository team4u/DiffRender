package org.team4u.test.example;

import com.xiaoleilu.hutool.io.FileUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.team4u.diff.render.SimpleMarkdownRender;
import org.team4u.diff.render.ValueFormatterRegistry;
import org.team4u.test.TestUtil;
import org.team4u.test.formatter.MyValueFormatter;

/**
 * @author Jay Wu
 */
public class PersonDiffHtmlRender {

    public static void main(String[] args) {
        ValueFormatterRegistry.INSTANCE.registerTemplateFunction(MyValueFormatter.class);

        SimpleMarkdownRender render = new SimpleMarkdownRender(
                "org.team4u.test.model",
                TestUtil.createPerson1(), TestUtil.createPerson2());

        String md = render.render();
        System.out.println(md);

        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        html = FileUtil.readUtf8String("person.html").replace("${body}", html);
        FileUtil.writeUtf8String(html, "../result.html");
    }
}