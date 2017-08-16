package org.team4u.diff.render;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * @author Jay Wu
 */
public class SimpleHtmlRender {

    private SimpleMarkdownRender render;

    public SimpleHtmlRender(SimpleMarkdownRender render) {
        this.render = render;
    }

    public String render() {
        String md = render.render();

        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}