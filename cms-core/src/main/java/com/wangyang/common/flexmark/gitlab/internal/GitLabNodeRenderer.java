package com.wangyang.common.flexmark.gitlab.internal;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.ReferenceRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlRendererOptions;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attribute;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.wangyang.common.flexmark.gitlab.GitLabBlockQuote;
import com.wangyang.common.flexmark.gitlab.GitLabDel;
import com.wangyang.common.flexmark.gitlab.GitLabInlineMath;
import com.wangyang.common.flexmark.gitlab.GitLabIns;
import com.wangyang.common.utils.CMSUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitLabNodeRenderer implements NodeRenderer
        // , PhasedNodeRenderer
{
    final public static AttributablePart VIDEO = new AttributablePart("VIDEO");
    final public static AttributablePart VIDEO_LINK = new AttributablePart("VIDEO_LINK");
    final static Pattern pImgWH = Pattern.compile("html=\\((.*)\\)");
    final GitLabOptions options;
    final private boolean codeContentBlock;
    final private ReferenceRepository referenceRepository;
    final private boolean recheckUndefinedReferences;

    public GitLabNodeRenderer(DataHolder options) {
        this.options = new GitLabOptions(options);
        this.codeContentBlock = Parser.FENCED_CODE_CONTENT_BLOCK.get(options);
        this.referenceRepository = Parser.REFERENCES.get(options);
        this.recheckUndefinedReferences = HtmlRenderer.RECHECK_UNDEFINED_REFERENCES.get(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        // @formatter:off
        set.add(new NodeRenderingHandler<>(GitLabIns.class, GitLabNodeRenderer.this::render));
        set.add(new NodeRenderingHandler<>(GitLabDel.class, GitLabNodeRenderer.this::render));
        set.add(new NodeRenderingHandler<>(GitLabInlineMath.class, GitLabNodeRenderer.this::render));
        set.add(new NodeRenderingHandler<>(GitLabBlockQuote.class, GitLabNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
        set.add(new NodeRenderingHandler<>(Link.class, GitLabNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
//        set.add(new NodeRenderingHandler<>(Link.class, CoreNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
        if (options.renderBlockMath || options.renderBlockMermaid) {
            set.add(new NodeRenderingHandler<>(FencedCodeBlock.class, GitLabNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
        }
        if (options.renderVideoImages) {
            set.add(new NodeRenderingHandler<>(Image.class, GitLabNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
            set.add(new NodeRenderingHandler<>(ImageRef.class, GitLabNodeRenderer.this::render));// ,// zzzoptionszzz(CUSTOM_NODE)
        }

        // @formatter:on
        return set;
    }


    private void render(@NotNull Link node, @NotNull NodeRendererContext context, @NotNull HtmlWriter html) {
        if (!context.isDoNotRenderLinks() && !CoreNodeRenderer.isSuppressedLinkPrefix(node.getUrl(), context)) {
            ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, node.getUrl().unescape(), (Attributes)null, (Boolean)null);
            String url = resolvedLink.getUrl();
            if(url.startsWith("zotero://select/library/items")){
                html.attr("href", url.replace("zotero://select/library/items","/literature")+".html");
            }
//            else if (url.startsWith("zotero://open-pdf/library/items")) {
//                html.attr("href", url.replace("zotero://open-pdf/library/items","/library/items"));
//            }
            else {
                html.attr("href", url);
            }

//            html.attr("href", resolvedLink.getUrl());
            if (node.getTitle().isNotNull()) {
                resolvedLink = resolvedLink.withTitle(node.getTitle().unescape());
            }

            html.attr(resolvedLink.getNonNullAttributes());
            html.srcPos(node.getChars()).withAttr(resolvedLink).tag("a").text(node.getText());
            html.tag("/a");
        } else {
            context.renderChildren(node);
        }
    }

    private void render(GitLabIns node, NodeRendererContext context, HtmlWriter html) {
        html.withAttr().tag("ins", false, false, () -> context.renderChildren(node));
    }

    private void render(GitLabDel node, NodeRendererContext context, HtmlWriter html) {
        html.withAttr().tag("del", false, false, () -> context.renderChildren(node));
    }

    /**
     * 行内的公式渲染
     * @param node
     * @param context
     * @param html
     */
    private void render(GitLabInlineMath node, NodeRendererContext context, HtmlWriter html) {
        html.withAttr().attr(Attribute.CLASS_ATTR, options.inlineMathClass).withAttr().tag("span");
        html.text(node.getText());
        html.tag("/span");
    }

    private void render(GitLabBlockQuote node, NodeRendererContext context, HtmlWriter html) {
        html.withAttr().tagLineIndent("section", () -> context.renderChildren(node));
    }
    //行间公式渲染
    private void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
        HtmlRendererOptions htmlOptions = context.getHtmlOptions();
        BasedSequence language = node.getInfoDelimitedByAny(htmlOptions.languageDelimiterSet);

        if (this.options.renderBlockMath && language.isIn(options.mathLanguages)) {
            html.line();
            html.srcPosWithTrailingEOL(node.getChars()).attr(Attribute.CLASS_ATTR, this.options.blockMathClass).withAttr().tag("div").line().openPre();
            if (codeContentBlock) {
                context.renderChildren(node);
            } else {
                html.text(node.getContentChars().normalizeEOL());
            }
            html.closePre().tag("/div");

            html.lineIf(htmlOptions.htmlBlockCloseTagEol);
        } else if (this.options.renderBlockMermaid && language.isIn(options.mermaidLanguages)) {
            html.line();
            html.srcPosWithTrailingEOL(node.getChars()).attr(Attribute.CLASS_ATTR, this.options.blockMermaidClass).withAttr().tag("div").line().openPre();
            if (codeContentBlock) {
                context.renderChildren(node);
            } else {
                html.text(node.getContentChars().normalizeEOL());
            }
            html.closePre().tag("/div");

            html.lineIf(htmlOptions.htmlBlockCloseTagEol);
        } else if (language.startsWith("btn-")) {
            BasedSequence sequence = language.replace("btn-", "");
            html.line();
            html = html.tag("div");
            html.withAttr().attr("href","javascript:;").attr(Attribute.CLASS_ATTR,"markdown-btn").withAttr().tag("a").text(sequence).tag("/a");
            html.srcPosWithTrailingEOL(node.getChars()).attr(Attribute.CLASS_ATTR, "markdown-btn-content").withAttr().tag("div").line().openPre();
            html.tag("pre");
            context.renderChildren(node);
//            if (codeContentBlock) {
//
//            } else {
//                html.text(node.getContentChars().normalizeEOL());
//            }
            html.tag("/pre");
            html.closePre().tag("/div");
            html.tag("/div");
            html.lineIf(htmlOptions.htmlBlockCloseTagEol);
        } else {
            context.delegateRender();
        }
    }

    private boolean renderVideoImage(Node srcNode, String url, String altText, Attributes attributes, HtmlWriter html,ResolvedLink resolvedLink) {
        String bareUrl = url;
        int pos = url.indexOf('?');
        if (pos != -1) {
            bareUrl = url.substring(0, pos);
        }
        bareUrl=bareUrl.replace(" ","" );
        if(bareUrl.startsWith("http://player.bilibili.com") || bareUrl.startsWith("//player.bilibili.com")){
            html.withAttr().attr("class","meta-media").withAttr().tag("div");
//            html.attr("class","lazy");
//                    html.attr("data-original", url);
//            String altTextImg =altText;
//            html.attr(options.imageSrcTag, url);

//            html.attr("alt", altTextImg);
            html.attr(resolvedLink.getNonNullAttributes());
            html.withAttr()
                    .attr("src",url)
                    .attr("class","video")
                    .attr("class","iframe-video")
                    .attr("autoplay","autoplay")
                    .attr("allowfullscreen","true")
                    .attr("framespacing","0")
                    .attr("frameborder","no")
                    .attr("border","0")
                    .attr("scrolling","no")
                    .withAttr().tag("iframe");
//            html.srcPos(srcNode.getChars()).withAttr(resolvedLink).tagVoid("iframe");
            html.tag("/iframe");
            html.tag("/div");
            return true;
        }
        pos = bareUrl.lastIndexOf('.');
        if (pos != -1) {
            String extension = bareUrl.substring(pos + 1);
            if (options.videoImageExtensionSet.contains(extension)) {
                //<div class="video-container">
                //<video src="video.mp4" width="400" controls="true"></video>
                //<p><a href="video.mp4" target="_blank" rel="noopener noreferrer" title="Download 'Sample Video'">Sample Video</a></p>
                //</div>
                html.attr(Attribute.CLASS_ATTR, options.videoImageClass).attr(attributes).withAttr().tagLineIndent("div", () -> {
                    // need to take attributes for reference definition, then overlay them with ours
                    html.srcPos(srcNode.getChars())
                            .attr("src", url)
                            .attr("width", "400")
                            .attr("controls", "true")
                            .withAttr(VIDEO)
                            .tag("video")
                            .tag("/video")
                            .line();

                    if (options.renderVideoLink) {
                        html.tag("p")
                                .attr("href", url)
                                .attr("target", "_blank")
                                .attr("rel", "noopener noreferrer")
                                .attr("title", String.format(options.videoImageLinkTextFormat, altText))
                                .withAttr(VIDEO_LINK)
                                .tag("a")
                                .text(altText)
                                .tag("/a")
                                .tag("/p")
                                .line();
                    }
                });

                return true;
            }else  if(options.imageExtensionSet.contains(extension)){
//                <p><img class="lazy" data-original="img/markdown_logo.png" alt="alt text1" title="Title Text" /></p>
                if(altText.startsWith("@")){
                    html.tag("picture");
                    html.attr("src", url);
                    html.attr("alt", altText);
                    html.attr(resolvedLink.getNonNullAttributes());
                    html.srcPos(srcNode.getChars()).withAttr(resolvedLink).tagVoid("img");
                    html.tag("/picture");
                }else {

                    String id = "ID"+ CMSUtils.randomViewName();
                    html.withAttr().attr("class","pic-warp").withAttr().tag("div");
                    if(altText.contains("carousel")){
                        html.withAttr().attr("class", " slide ");
                        html.withAttr().attr("id",id);
                    }else {
                        html.withAttr().attr("class", options.imageSrcTag + " row ");
                    }

                    String altTextImg =altText;
                    Matcher matcher = pImgWH.matcher(altTextImg);
                    if (matcher.find()){
                        String group = matcher.group(1);
                        altTextImg = matcher.replaceAll("");
                        String[] groups = group.split(",");
                        for (int i=0;i<groups.length;i++){
                            String[] items = groups[i].split("=");
                            html.attr(items[0], items[1]);

                        }
                    }

                    html.withAttr().tag("div");
                    if(url.contains("|")){
                        String[] urls = url.split("\\|");
                        if(altText.contains("carousel")){
                            html.withAttr().attr("class","carousel-inner").withAttr().tag("div");

                        }
                        Boolean flagFirst=true;
                        for (String item : urls){

                            if(altText.contains("carousel")){
                                html.attr("class","carousel-item");
                            }else {
                                html.attr("class","col");
                            }
                            if(flagFirst){
                                html.attr("class","active");
                                flagFirst=false;
                            }
                            html.withAttr().tag("div");


                            if(altText.contains("carousel")){
                                html.attr("class","carousel-lazy");

                            }else {
                                html.attr("class","lazy");
                            }

                            html.attr(options.imageSrcTag, item);


                            html.attr("alt", altTextImg);
                            html.attr(resolvedLink.getNonNullAttributes());
                            html.srcPos(srcNode.getChars()).withAttr(resolvedLink).tagVoid("img");
                            html.tag("/div");
                        }
                        if(altText.contains("carousel")){
                            html.tag("/div");
                        }

                        if(altText.contains("carousel")){
                            html.raw(" <button class=\"carousel-control-prev\" type=\"button\" data-target=\"#"+id+"\" data-slide=\"prev\">\n" +
                                    "    <span class=\"carousel-control-prev-icon\" aria-hidden=\"true\"></span>\n" +
                                    "    <span class=\"sr-only\">Previous</span>\n" +
                                    "  </button>\n" +
                                    "  <button class=\"carousel-control-next\" type=\"button\" data-target=\"#"+id+"\" data-slide=\"next\">\n" +
                                    "    <span class=\"carousel-control-next-icon\" aria-hidden=\"true\"></span>\n" +
                                    "    <span class=\"sr-only\">Next</span>\n" +
                                    "  </button>");
                        }

                    }else {
                        html.attr("class","lazy");
                        html.attr(options.imageSrcTag, url);
//                        Matcher matcher = pImgWH.matcher(altTextImg);
//                        if (matcher.find()){
//                            String group = matcher.group(1);
//                            altTextImg = matcher.replaceAll("");
//                            String[] groups = group.split(",");
//                            for (int i=0;i<groups.length;i++){
//                                String[] items = groups[i].split("=");
////                            System.out.println(items[0]);
//                                html.attr(items[0], items[1]);
//                            }
//                        }
                        html.attr("alt", altTextImg);
                        html.attr(resolvedLink.getNonNullAttributes());
                        html.srcPos(srcNode.getChars()).withAttr(resolvedLink).tagVoid("img");

                    }


                    html.tag("/div");
                    if(!altTextImg.equals("") && !altTextImg.equals("生信小木屋")){
                        html.withAttr().attr("class","img-caption").withAttr().tag("p")
                                .text(altTextImg)
                                .tag("/p");
                    }
                    html.tag("/div");
                }

//                 we have a title part, use that
//                if (srcNode.getTitle().isNotNull()) {
//                    resolvedLink = resolvedLink.withTitle(node.getTitle().unescape());
//                }
//                html.tag("picture")
//                        .attr("src", url)
//                        .attr("target", "_blank")
//                        .attr("alt", altText)
//                        .attr("title", String.format(options.videoImageLinkTextFormat, altText))
//                        .withAttr(VIDEO_LINK)
//                        .tag("img")
//                        .text(altText)
//                        .tag("/img")
//                        .tag("/picture")
//                        .line();

                return true;
            }
        }
        return false;
    }

    private void render(Image node, NodeRendererContext context, HtmlWriter html) {
        if (!(context.isDoNotRenderLinks() || CoreNodeRenderer.isSuppressedLinkPrefix(node.getUrl(), context))) {
            String altText = new TextCollectingVisitor().collectAndGetText(node);
            ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, node.getUrl().unescape(), null, null);
            String url = resolvedLink.getUrl();

            if (node.getTitle().isNotNull()) {
                resolvedLink = resolvedLink.withTitle(node.getTitle().unescape());
            }
            if (node.getUrlContent().isEmpty()) {
                Attributes attributes = resolvedLink.getNonNullAttributes();

                // need to take attributes for reference definition, then overlay them with ours
                attributes = context.extendRenderingNodeAttributes(node, AttributablePart.NODE, attributes);

                if (renderVideoImage(node, url, altText, attributes, html,resolvedLink)) {
                    return;
                }
            }

            context.delegateRender();
        }
    }

    private void render(ImageRef node, NodeRendererContext context, HtmlWriter html) {
        ResolvedLink resolvedLink = null;
        boolean isSuppressed = false;

        if (!node.isDefined() && recheckUndefinedReferences) {
            if (node.getReferenceNode(referenceRepository) != null) {
                node.setDefined(true);
            }
        }

        Reference reference = null;

        if (node.isDefined()) {
            reference = node.getReferenceNode(referenceRepository);
            String url = reference.getUrl().unescape();
            isSuppressed = CoreNodeRenderer.isSuppressedLinkPrefix(url, context);

            resolvedLink = context.resolveLink(LinkType.IMAGE, url, null, null);
            if (reference.getTitle().isNotNull()) {
                resolvedLink = resolvedLink.withTitle(reference.getTitle().unescape());
            }
        } else {
            // see if have reference resolver and this is resolved
            String normalizeRef = referenceRepository.normalizeKey(node.getReference());
            resolvedLink = context.resolveLink(LinkType.IMAGE_REF, normalizeRef, null, null);
            if (resolvedLink.getStatus() == LinkStatus.UNKNOWN) {
                resolvedLink = null;
            }
        }


        if (resolvedLink != null) {
            if (!(context.isDoNotRenderLinks() || isSuppressed)) {
                String altText = new TextCollectingVisitor().collectAndGetText(node);
                String url = resolvedLink.getUrl();
                Attributes attributes = resolvedLink.getNonNullAttributes();
                if (renderVideoImage(node, url, altText, attributes, html,resolvedLink)) {
                    return;
                }
            }
        }

        context.delegateRender();
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new GitLabNodeRenderer(options);
        }
    }
}
