package com.wangyang.common.flexmark.link;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.*;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LinkReader extends CoreNodeRenderer {

    public LinkReader(DataHolder options) {
        super(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();

//        set.add(new NodeRenderingHandler<>(Link.class,LinkReader.this::render));
        return set;
    }

//
//
//    void render(Link node, NodeRendererContext context, HtmlWriter html) {
//        super.render(node,context,html);
//        if (!context.isDoNotRenderLinks() && !isSuppressedLinkPrefix(node.getUrl(), context)) {
//            ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, node.getUrl().unescape(), (Attributes)null, (Boolean)null);
//            html.attr("href", resolvedLink.getUrl());
//            if (node.getTitle().isNotNull()) {
//                resolvedLink = resolvedLink.withTitle(node.getTitle().unescape());
//            }
//
//            html.attr(resolvedLink.getNonNullAttributes());
//            html.srcPos(node.getChars()).withAttr(resolvedLink).tag("a");
//            this.renderChildrenSourceLineWrapped(node, node.getText(), context, html);
//            html.tag("/a");
//        } else {
//            context.renderChildren(node);
//        }
//
//    }
}
