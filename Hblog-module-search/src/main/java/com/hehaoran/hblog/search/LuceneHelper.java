package com.hehaoran.hblog.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hehaoran.hblog.search.index.ArticleIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description: Lucene 索引工具类
 **/
@Component
@Slf4j
public class LuceneHelper {

    /**
     * 创建索引（全量重建：先清空目录再写入）
     *
     * @param indexDir  索引存放的目录
     * @param documents 文档
     */
    public void createIndex(String indexDir, List<Document> documents) {
        try {
            File dir = new File(indexDir);

            // 判断索引目录是否存在
            if (dir.exists()) {
                // 删除目录中的内容
                FileUtils.cleanDirectory(dir);
            } else {
                // 若不存在，则创建目录
                FileUtils.forceMkdir(dir);
            }

            doWrite(indexDir, writer -> {
                for (Document document : documents) {
                    writer.addDocument(document);
                }
            });
        } catch (Exception e) {
            log.error("创建 Lucene 索引失败: ", e);
        }
    }

    /**
     * 新增文档
     *
     * @param indexDir 索引目录
     * @param document 文档
     */
    public void addDocument(String indexDir, Document document) {
        try {
            ensureIndexDir(indexDir);
            doWrite(indexDir, writer -> writer.addDocument(document));
            log.info("==> 新增 Lucene 文档成功, indexDir: {}", indexDir);
        } catch (Exception e) {
            log.error("新增 Lucene 文档失败: ", e);
        }
    }

    /**
     * 按条件更新文档（底层先删后加）
     *
     * @param indexDir  索引目录
     * @param condition 条件字段名，如 id
     * @param conditionValue 条件值
     * @param document  新文档
     */
    public void updateDocument(String indexDir, String condition, String conditionValue, Document document) {
        try {
            ensureIndexDir(indexDir);
            doWrite(indexDir, writer -> writer.updateDocument(new Term(condition, conditionValue), document));
            log.info("==> 更新 Lucene 文档成功, {}={}", condition, conditionValue);
        } catch (Exception e) {
            log.error("更新 Lucene 文档失败, {}={}: ", condition, conditionValue, e);
        }
    }

    /**
     * 按条件删除文档
     *
     * @param indexDir  索引目录
     * @param condition 条件字段名，如 id
     * @param conditionValue 条件值
     */
    public void deleteDocument(String indexDir, String condition, String conditionValue) {
        try {
            File dir = new File(indexDir);
            if (!dir.exists()) {
                log.warn("==> 索引目录不存在，跳过删除, indexDir: {}", indexDir);
                return;
            }
            doWrite(indexDir, writer -> {
                writer.deleteDocuments(new Term(condition, conditionValue));
                // 强制合并删除段，使删除立即生效
                writer.forceMergeDeletes();
            });
            log.info("==> 删除 Lucene 文档成功, {}={}", condition, conditionValue);
        } catch (Exception e) {
            log.error("删除 Lucene 文档失败, {}={}: ", condition, conditionValue, e);
        }
    }

    /**
     * 关键词搜索, 查询总数据量
     *
     * @param indexDir 索引目录
     * @param word     查询关键词
     * @param columns  需要搜索的字段
     * @return 命中总数
     */
    public long searchTotal(String indexDir, String word, String[] columns) {
        try {
            // 打开索引目录
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // 中文分析器
            Analyzer analyzer = new SmartChineseAnalyzer();
            // 查询解析器
            QueryParser parser = new MultiFieldQueryParser(columns, analyzer);
            // 解析查询关键字
            Query query = parser.parse(word);

            // 搜索文档
            TopDocs totalDocs = searcher.search(query, Integer.MAX_VALUE);
            reader.close();
            // 返回文档数
            return totalDocs.totalHits.value;
        } catch (Exception e) {
            log.error("查询 Lucene 错误: ", e);
            return 0;
        }
    }

    /**
     * 关键词搜索
     *
     * @param indexDir 索引目录
     * @param word     查询关键词
     * @param columns  被搜索的字段
     * @param current  当前页
     * @param size     每页数据量
     * @return 文档字段 Map 列表（不向外暴露 Lucene Document）
     */
    public List<Map<String, String>> search(String indexDir, String word, String[] columns, int current, int size) {
        try {
            // 打开索引目录
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);

            // 中文分析器
            Analyzer analyzer = new SmartChineseAnalyzer();
            // 查询解析器
            QueryParser parser = new MultiFieldQueryParser(columns, analyzer);
            // 解析查询关键字
            Query query = parser.parse(word);

            // 执行搜索，获取匹配查询的前 limit 条结果。
            int limit = current * size;
            TopDocs topDocs = searcher.search(query, limit); // 搜索前 limit 条结果

            // 匹配的文档数组
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // 计算分页的起始 - 结束位置
            int start = (current - 1) * size;
            int end = Math.min(start + size, scoreDocs.length);

            // 标题高亮
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color: #f73131\">", "</span>");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));

            // 返回指定页码的文档字段
            List<Map<String, String>> documents = Lists.newArrayList();
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(scoreDocs[i].doc);
                Map<String, String> row = Maps.newHashMap();
                doc.getFields().forEach(field -> row.put(field.name(), field.stringValue()));

                String title = row.getOrDefault(ArticleIndex.COLUMN_TITLE, "");
                try {
                    TokenStream tokenStream = analyzer.tokenStream(ArticleIndex.COLUMN_TITLE, new StringReader(title));
                    String titleFragment = highlighter.getBestFragment(tokenStream, title);
                    if (StringUtils.isNotBlank(titleFragment)) {
                        row.put(ArticleIndex.COLUMN_TITLE, titleFragment);
                    }
                } catch (Exception highlightEx) {
                    log.warn("标题高亮失败, title={}", title, highlightEx);
                }
                documents.add(row);
            }

            // 释放资源
            reader.close();
            return documents;
        } catch (Exception e) {
            log.error("查询 Lucene 错误: ", e);
            return Collections.emptyList();
        }
    }

    private void ensureIndexDir(String indexDir) throws IOException {
        File dir = new File(indexDir);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
    }

    private void doWrite(String indexDir, IndexWriteAction action) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory, config);
            action.write(writer);
            writer.commit();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @FunctionalInterface
    private interface IndexWriteAction {
        void write(IndexWriter writer) throws IOException;
    }
}
