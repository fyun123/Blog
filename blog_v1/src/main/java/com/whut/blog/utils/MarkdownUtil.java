package com.whut.blog.utils;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributes;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.image.attributes.internal.ImageAttributesAttributeProvider;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.lang.reflect.Array;
import java.util.*;

public class MarkdownUtil {

    /**
     * markdown格式转成HTML格式
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * 增加扩展（标题锚点，表格生成）
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown){
        //标题id生成
        Set<Extension> headingAnchorExtension = Collections.singleton(HeadingAnchorExtension.create());
        //转换Table的HTML
        List<Extension> tableExtensions = Arrays.asList(TablesExtension.create());

        Set<Extension>  imageExtension = Collections.singleton(ImageAttributesExtension.create());

        Parser parser = Parser.builder()
                .extensions(tableExtensions)
                .build();
        Node document = parser.parse(markdown);
//        Node document1 = parser.parse("![text](/asserts/images/9b34922c-3bad-4def-bb1f-4c189945b84f_image.png)");

        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtension)
                .extensions(tableExtensions)
                .extensions(imageExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    @Override
                    public AttributeProvider create(AttributeProviderContext attributeProviderContext) {
                        return new CustomAttributeProvider();
                    }
                })
                .build();
//        System.out.println("图片" + renderer.render(document1));
        return renderer.render(document);
    }
    //处理标签的属性
    static class CustomAttributeProvider implements AttributeProvider {

        @Override
        public void setAttributes(Node node, String s, Map<String, String> map) {
            if (node instanceof Link) {
                map.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                map.put("class", "ui celled table");
            }
        }
    }
}
