package com.baizhi.controller;

import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/poetry")
public class PoetryController {

    @RequestMapping("/find")
    public String find(String text, String type, HttpSession session, Integer page, Integer pageSize) throws IOException, InvalidTokenOffsetsException, ParseException {
        //指定路径
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\lucene\\index\\poetry"));
        //创建索引读入器
        DirectoryReader directoryReader = DirectoryReader.open(fsDirectory);
        //创建索引检索器对象
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        //创建解析器对象  默认查询内容域
        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());

        Query query = null;
        String ss = type + ":" + text;
        query = queryParser.parse(ss);
        /**
         * 分页
         */
        TopDocs topDocsss = indexSearcher.search(query, pageSize);
        //符合条件的总条数
        int totalHits = topDocsss.totalHits;
        session.setAttribute("count", totalHits);
        int pages = (totalHits % pageSize == 0) ? (totalHits / pageSize) : (totalHits / pageSize + 1);
        if (page < 1) {
            page = 1;
        }
        if (page > pages) {
            page = pages;
        }
        //点击上一页 下一页时 需要传递四个数据 这里现将 text 和type存入session中
        //session.setAttribute("pages",pages);
        session.setAttribute("text", text);
        session.setAttribute("type", type);

        session.setAttribute("page", page);
        session.setAttribute("pageSize", pageSize);
        //--------------------------------------------------------------------

        TopDocs topDocs = null;
        if (page <= 1) {
            topDocs = indexSearcher.search(query, pageSize);
        } else if (page > 1) {
            TopDocs topDocss = indexSearcher.search(query, (page - 1) * pageSize);
            //获取到当前页的左右一条数据
            ScoreDoc[] scoreDocs = topDocss.scoreDocs;
            ScoreDoc scoreDoc = scoreDocs[scoreDocs.length - 1];
            topDocs = indexSearcher.searchAfter(scoreDoc, query, pageSize);
        }
        // 指定高亮格式
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        //创建高亮器对象 参数：1.分数器对象
        //创建分数器 对象 QueryScorer
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Poetry> list = new ArrayList<Poetry>();

        for (ScoreDoc scoreDoc : scoreDocs) {
            //分数
            float score = scoreDoc.score;
            //查询到数据在索引库所对应的编号
            int doc = scoreDoc.doc;
            //根据编号  获取数据
            Document document = directoryReader.document(doc);
            /**
             * 高亮内容
             */
            String bestFragmentcontent = highlighter.getBestFragment(new IKAnalyzer(), "content", document.get("content"));
            if (bestFragmentcontent == null) {
                bestFragmentcontent = document.get("content");
            }

            /**
             * 高亮诗名
             */
            String bestFragmenttitle = highlighter.getBestFragment(new IKAnalyzer(), "title", document.get("title"));
            if (bestFragmenttitle == null) {
                bestFragmenttitle = document.get("title");
            }

            /**
             * 高亮作者
             */
            String bestFragmentauthor = highlighter.getBestFragment(new IKAnalyzer(), "author", document.get("author"));
            if (bestFragmentauthor == null) {
                bestFragmentauthor = document.get("author");
            }

            /**
             * 将数据存入对象  并将对象存入集合
             */
            Poetry poetry = new Poetry();
            poetry.setTitle(bestFragmenttitle);
            poetry.setPoet(new Poet(null, bestFragmentauthor));
            poetry.setContent(bestFragmentcontent);
            list.add(poetry);
        }
        session.setAttribute("list", list);
        return "redirect:/poetrylist.jsp";
    }

}



