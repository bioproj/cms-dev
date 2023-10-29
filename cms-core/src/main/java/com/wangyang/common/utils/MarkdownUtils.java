package com.wangyang.common.utils;


//import com.vladsch.flexmark.ext.tables.TablesExtension;
//import com.vladsch.flexmark.ext.toc.TocExtension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vladsch.flexmark.ext.admonition.AdmonitionExtension;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.enumerated.reference.EnumeratedReferenceExtension;
import com.vladsch.flexmark.ext.macros.MacrosExtension;
import com.vladsch.flexmark.ext.media.tags.MediaTagsExtension;

import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
//import com.wangyang.common.flexmark.gitlab.GitLabExtension;
//import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;

import com.wangyang.common.flexmark.footnotes.FootnoteExtension;
import com.wangyang.common.flexmark.gitlab.GitLabExtension;
//import com.wangyang.common.flexmark.imgattr.AttributeExtension;
import com.wangyang.common.flexmark.table.TablesExtension;
import com.wangyang.pojo.dto.Toc;
import com.wangyang.pojo.entity.base.Content;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//import com.wangyang.cms.gitlab.GitLabExtension;
//import com.wangyang.cms.media.tags.MediaTagsExtension;


public class MarkdownUtils {

    /**
     * 图片懒加载
     */
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS,Arrays.asList(
                    EmojiExtension.create(),
                    TablesExtension.create(),
                    GitLabExtension.create(),
                    TocExtension.create(),
                    MediaTagsExtension.create(),
                    FootnoteExtension.create(),
                    AdmonitionExtension.create(),
                    MacrosExtension.create(),
                    AttributesExtension.create(),
                    EnumeratedReferenceExtension.create()



            )) .set(GitLabExtension.IMAGE_SRC_TAG,"data-original")
            .set(HtmlRenderer.SOFT_BREAK, "<br/>")
//            .set(Parser.HARD_LINE_BREAK_LIMIT,true)
            .set(TocExtension.LEVELS, -1)
            .set(TocExtension.LIST_CLASS,"thisToc")
            .set(TocExtension.IS_NUMBERED,false)
            .set(Parser.INDENTED_CODE_BLOCK_PARSER,false)

            .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.EMOJI_CHEAT_SHEET)
                .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_ONLY);



    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    public static String renderHtml(String content) {
        Node document = PARSER.parse(content);
        String render = RENDERER.render(document);
        return  render;

    }

    public static Content renderHtml(Content article) {
        if(StringUtils.isBlank(article.getOriginalContent())){
            return null;
        }
        Node document = PARSER.parse("[TOC]\n @@@ \n\n"+article.getOriginalContent());

        String render = RENDERER.render(document);
        String[] split = render.split("<p>@@@</p>");
        article.setFormatContent(split[1]);
        article.setToc(split[0]);
        List<Toc> tocList = getToc(split[0]);
        String tocJSON = JSON.toJSON(tocList).toString();
        article.setTocJSON(tocJSON);

        return article;
    }

    public static List<Toc> getToc(String tocStr){
        Document doc = Jsoup.parse(tocStr);
        Element searchInfo= doc.getElementsByClass("thisToc").first();
        if(searchInfo!=null){
            Elements elements = searchInfo.select(" > li");
//            JSONArray jsonArray = new JSONArray();
            List<Toc> tocList = new ArrayList<>();
            getTocList(elements,tocList);
            return tocList;
//            String tocJSON = JSON.toJSON(tocLost).toString();
//            String tocJSON = jsonArray.toJSONString();

        }
        return null;
    }

    private static void getTocList( Elements elements, List<Toc> tocList){
        for (Element element : elements) {
            Elements as = element.getElementsByTag("a");
//            JSONArray newArray = new JSONArray();
            List<Toc> newToc = new ArrayList<>();

            if(as.size()>0){
                Element a = as.get(0);
                String href = a.attr("href");
                String html = a.html();
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("title",html);
//                jsonObject.put("linkPath",href);
//                jsonObject.put("children",newArray);
                Toc toc = new Toc();
                toc.setTitle(html);
                toc.setLinkPath(href);
                toc.setChildren(newToc);
                tocList.add(toc);

//                jsonArray.add(jsonObject);
            }

//            jsonArray.add(JSONObject.parse("{title:" + html + ",children:[]}"));
            Elements ul = element.getElementsByTag("ul");

            if(ul.size()>0){
                Elements lis = ul.get(0).select(" > li");
                getTocList(lis,newToc);
            }

        }

    }

    /**
     * 打印PDF
     */
    private static final DataHolder OPTIONS_OUTPUT = new MutableDataSet()
            .set(Parser.EXTENSIONS,Arrays.asList(
                    EmojiExtension.create(),
                    TablesExtension.create(),
                    GitLabExtension.create(),
                    TocExtension.create(),
                    MediaTagsExtension.create(),
                    FootnoteExtension.create(),
                    AdmonitionExtension.create(),
                    MacrosExtension.create(),
                    AttributesExtension.create(),

                    EnumeratedReferenceExtension.create()


            )).set(GitLabExtension.IMAGE_SRC_TAG,"src")
            .set(HtmlRenderer.SOFT_BREAK, "<br/>")
            .set(Parser.INDENTED_CODE_BLOCK_PARSER,false)
//            .set(Parser.HARD_LINE_BREAK_LIMIT,true)
            .set(TocExtension.LEVELS, -1)
            .set(TocExtension.LIST_CLASS,"thisToc")
//            .set(TocExtension.C,"thisToc")

            .set(TocExtension.IS_NUMBERED,false)
            .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.EMOJI_CHEAT_SHEET)
            .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_ONLY);
    private static final Parser PARSER_OUTPUT = Parser.builder(OPTIONS_OUTPUT).build();
    private static final HtmlRenderer RENDERER_OUTPUT = HtmlRenderer.builder(OPTIONS_OUTPUT).build();

    /**
     * 不使用图片懒加载
     * @param markdown
     * @return
     */
    public static String renderHtmlOutput(String markdown) {
        if(StringUtils.isBlank(markdown)){
            return null;
        }
        Node document = PARSER_OUTPUT.parse("[TOC]\n "+markdown);
        String render = RENDERER_OUTPUT.render(document);
        return render;
    }

    public  static String getText(String content){
        if(content==null){
            return StringUtils.EMPTY;
        }
        String txtcontent = content.replaceAll("</?[^>]+>", ""); //剔出<html>的标签  
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n|</a>", "");//去除字符串中的空格,回车,换行符,制表符



        return txtcontent;
    }

}
