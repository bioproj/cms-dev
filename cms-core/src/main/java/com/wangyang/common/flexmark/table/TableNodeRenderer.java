package com.wangyang.common.flexmark.table;

import com.vladsch.flexmark.ext.tables.*;
//import com.vladsch.flexmark.ext.tables.internal.TableParserOptions;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;



public class TableNodeRenderer implements NodeRenderer {
    class TableParserOptions {
        public final int maxHeaderRows;
        public final int minHeaderRows;
        public final int minSeparatorDashes;
        public final boolean appendMissingColumns;
        public final boolean discardExtraColumns;
        public final boolean columnSpans;
        public final boolean trimCellWhitespace;
        public final boolean headerSeparatorColumnMatch;
        public final String className;
        public final boolean withCaption;

        TableParserOptions(DataHolder options) {
            this.maxHeaderRows = (Integer) TablesExtension.MAX_HEADER_ROWS.get(options);
            this.minHeaderRows = (Integer)TablesExtension.MIN_HEADER_ROWS.get(options);
            this.minSeparatorDashes = (Integer)TablesExtension.MIN_SEPARATOR_DASHES.get(options);
            this.appendMissingColumns = (Boolean)TablesExtension.APPEND_MISSING_COLUMNS.get(options);
            this.discardExtraColumns = (Boolean)TablesExtension.DISCARD_EXTRA_COLUMNS.get(options);
            this.columnSpans = (Boolean)TablesExtension.COLUMN_SPANS.get(options);
            this.trimCellWhitespace = (Boolean)TablesExtension.TRIM_CELL_WHITESPACE.get(options);
            this.headerSeparatorColumnMatch = (Boolean)TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH.get(options);
            this.className = (String)TablesExtension.CLASS_NAME.get(options);
            this.withCaption = (Boolean)TablesExtension.WITH_CAPTION.get(options);
        }
    }


    private final TableParserOptions options;

    public TableNodeRenderer(DataHolder options) {
        this.options = new TableParserOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return new HashSet<>(Arrays.asList(
                new NodeRenderingHandler<>(TableBlock.class, this::render),
                new NodeRenderingHandler<>(TableHead.class, this::render),
                new NodeRenderingHandler<>(TableSeparator.class, this::render),
                new NodeRenderingHandler<>(TableBody.class, this::render),
                new NodeRenderingHandler<>(TableRow.class, this::render),
                new NodeRenderingHandler<>(TableCell.class, this::render),
                new NodeRenderingHandler<>(TableCaption.class, this::render)
        ));
    }


//    private void render(Node node, @NotNull NodeRendererContext nodeRendererContext, @NotNull HtmlWriter lineInfos) {
//    }

    private void render(TableBlock node, NodeRendererContext context, HtmlWriter html) {
        if (!this.options.className.isEmpty()) {
            html.attr("class", this.options.className);
        }
        html.withAttr().attr("class","tab-warp").tag("div");
        ((HtmlWriter)html.srcPosWithEOL(node.getChars()).withAttr().tagLineIndent("table", () -> {
            context.renderChildren(node);
        })).line();
        html.tag("/div");
    }

    private void render(TableHead node, NodeRendererContext context, HtmlWriter html) {
        ((HtmlWriter)html.withAttr().withCondIndent()).tagLine("thead", () -> {
            context.renderChildren(node);
        });
    }

    private void render(TableSeparator tableSeparator, NodeRendererContext context, HtmlWriter html) {
    }

    private void render(TableBody node, NodeRendererContext context, HtmlWriter html) {
        ((HtmlWriter)html.withAttr().withCondIndent()).tagLine("tbody", () -> {
            context.renderChildren(node);
        });
    }

    private void render(TableRow node, NodeRendererContext context, HtmlWriter html) {
        html.srcPos((BasedSequence)node.getChars().trimStart()).withAttr().tagLine("tr", () -> {
            context.renderChildren(node);
        });
    }

    private void render(TableCaption node, NodeRendererContext context, HtmlWriter html) {
        html.srcPos((BasedSequence)node.getChars().trimStart()).withAttr().tagLine("caption", () -> {
            context.renderChildren(node);
        });
    }

    private void render(TableCell node, NodeRendererContext context, HtmlWriter html) {
        String tag = node.isHeader() ? "th" : "td";
        if (node.getAlignment() != null) {
            html.attr("align", getAlignValue(node.getAlignment()));
        }

        if (this.options.columnSpans && node.getSpan() > 1) {
            html.attr("colspan", String.valueOf(node.getSpan()));
        }

        html.srcPos(node.getText()).withAttr().tag(tag);
        context.renderChildren(node);
        html.tag("/" + tag);
    }

    private static String getAlignValue(TableCell.Alignment alignment) {
        switch (alignment) {
            case LEFT:
                return "left";
            case CENTER:
                return "center";
            case RIGHT:
                return "right";
            default:
                throw new IllegalStateException("Unknown alignment: " + alignment);
        }
    }

    public static class Factory implements NodeRendererFactory {
        public Factory() {
        }

        public @NotNull NodeRenderer apply(@NotNull DataHolder options) {
            return new TableNodeRenderer(options);
        }
    }
}

