package com.baizhi;

import com.baizhi.dao.PoetryDao;
import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import com.baizhi.service.PoetryService;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TssApplicationTests {

    @Autowired
    private PoetryService poetrys;

  /*  @Test
    public void contextLoads() {
        //List<Poetry> poetries = poetryDao.showAll();
        for (Poetry poetry : poetries) {
            System.out.println(poetry);
        }
    }*/


    @Test
    public void addIndex() throws IOException {
        //指定路径
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\lucene\\index\\poetry"));
        // 创建写入器
        IndexWriter indexWriter = new IndexWriter(fsDirectory, new IndexWriterConfig(new IKAnalyzer()));

        List<Poetry> all = poetrys.findAll();
        Document document = null;
        for (Poetry poetry : all) {
            document = new Document();
            document.add(new IntField("id",poetry.getId(), Field.Store.YES));
            document.add(new StringField("author",poetry.getPoet().getName(), Field.Store.YES));
            document.add(new StringField("title",poetry.getTitle(), Field.Store.YES));
            document.add(new TextField("content",poetry.getContent(), Field.Store.YES));
        indexWriter.addDocument(document);
        }

        indexWriter.commit();
        indexWriter.close();
    }

    /**
     *
     * @throws IOException
     * @throws ParseException
     * @throws InvalidTokenOffsetsException
     */
    @Test
    public void Testss() throws IOException, ParseException, InvalidTokenOffsetsException {
        //指定路径
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\lucene\\index\\poetry"));
        //创建索引读入器
        DirectoryReader directoryReader = DirectoryReader.open(fsDirectory);
        //创建索引检索器对象
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        //创建解析器对象  默认查询内容域
        QueryParser queryParser = new QueryParser("content",new IKAnalyzer());

        Query query = null;

        query = queryParser.parse("title:赠内");

        //声明请求参数信息
        int nowPage = 1;
        int pageSize = 10000;

        TopDocs topDocs = null;

        if(nowPage <= 1){
            topDocs = indexSearcher.search(query,pageSize);
        }else if(nowPage > 1){
            TopDocs topDocss = indexSearcher.search(query, (nowPage - 1) * pageSize);
            ScoreDoc[] scoreDocs = topDocss.scoreDocs;
            ScoreDoc ss = scoreDocs[scoreDocs.length - 1];
            topDocs = indexSearcher.searchAfter(ss, query, pageSize);
        }
        ScoreDoc[] scoreDocs1 = topDocs.scoreDocs;
        int totalHits = topDocs.totalHits;
        System.out.println("符合条件的总记录数:"+totalHits);

        // 指定高亮格式
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");

        //创建高亮器对象 参数：1.分数器对象
        //创建分数器 对象 QueryScorer
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter,scorer);



        List<Poetry> list = new ArrayList<Poetry>();

        for (ScoreDoc scoreDoc : scoreDocs1) {
            //查询到数据在索引库所对应的编号
            int doc = scoreDoc.doc;
            //根据编号  获取数据
            Document document = directoryReader.document(doc);
            String bestFragmentcontent = highlighter.getBestFragment(new IKAnalyzer(), "content", document.get("content"));
            if(bestFragmentcontent == null){
                bestFragmentcontent = document.get("content");
            }
            String bestFragmenttitle = highlighter.getBestFragment(new IKAnalyzer(), "title", document.get("title"));
            if(bestFragmenttitle == null){
                bestFragmenttitle =document.get("title");
            }
            String bestFragmentauthor = highlighter.getBestFragment(new IKAnalyzer(), "author", document.get("author"));
            if(bestFragmentauthor == null){
                bestFragmentauthor =document.get("author");
            }
            Poetry poetry = new Poetry();
            poetry.setTitle(bestFragmenttitle);
            poetry.setPoet(new Poet(null,bestFragmentauthor));
            poetry.setContent(bestFragmentcontent);
            list.add(poetry);

        }
        for (Poetry poetry : list) {
            System.out.println("+++"+poetry.getTitle()+"--"+poetry.getPoet().getName()+"-"+poetry.getContent());
        }

    }
}

