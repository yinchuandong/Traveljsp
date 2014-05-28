package Util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.lang.model.element.VariableElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchUtil {
	
	private Directory directory = null;
	private IndexReader reader = null;
	private IndexSearcher searcher = null;
	
	public SearchUtil(){
		
	}

	/**
	 * 切分词语，结果返回到arraylist中
	 * @param word
	 * @return
	 */
	private ArrayList<String> cut(String word){
		ArrayList<String> list = new ArrayList<String>();
		if (word == null || word.equals("")) {
			return list;
		}
	    StringReader reader = new StringReader(word); 
	    IKSegmenter ik = new IKSegmenter(reader,false);//当为true时，分词器进行最大词长切分 
	    Lexeme lexeme = null; 
	    
	    try {
			while((lexeme = ik.next())!=null) {
				list.add(lexeme.getLexemeText());
				System.out.println(lexeme.getLexemeText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return list;
	}
	
	/**
	 * 在索引中进行检索
	 * @param word
	 * @return
	 */
	public String retrieve(String word){
		JSONObject resultJson = JSONObject.fromObject("{}");
		if (word == null || word.equals("")) {
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("关键词不能为空"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		}
		
		//-------打开索引--------
		ArrayList<String> list = this.cut(word);
		try {
			directory = new SimpleFSDirectory(new File("E:\\traveldata\\index"));
			reader = IndexReader.open(directory);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			e.printStackTrace();
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("索引打开失败"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		}
		
		BooleanQuery booleanQuery = new BooleanQuery();
		
		for (String key : list) {
			Query query = new TermQuery(new Term("ambiguity_sname", key));
			booleanQuery.add(query, BooleanClause.Occur.MUST);
		}
		ArrayList<Scenery> dataList = new ArrayList<Scenery>();
		try {
			TopDocs topDocs = searcher.search(booleanQuery, 10);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document targetDoc = searcher.doc(scoreDoc.doc);
				String sid = targetDoc.get("sid");
				String ambiguitySname = AppUtil.toUnicode(targetDoc.get("ambiguity_sname"));
				String surl = targetDoc.get("surl");
				dataList.add(new Scenery(sid, ambiguitySname, surl));
			}
		} catch (IOException e) {
			e.printStackTrace();
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("搜索失败"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		} finally{
			try {
				reader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		resultJson.put("data", JSONArray.fromObject(dataList));
		resultJson.put("info", AppUtil.toUnicode("返回成功"));
		resultJson.put("status", "1");
		return resultJson.toString().replaceAll("\\\\u", "\\u");
	}
	
	/**
	 * 外部调用的静态方法
	 * @param word
	 * @return
	 */
	public static String search(String word){
		SearchUtil searchUtil = new SearchUtil();
		String result = searchUtil.retrieve(word);
		return result;
	}
	
	public class Scenery{
		public String sid = "";
		public String ambiguitySname = "";
		public String surl = "";
		
		public Scenery(String sid, String ambiguitySname, String surl){
			this.sid = sid;
			this.ambiguitySname = ambiguitySname;
			this.surl = surl;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getAmbiguitySname() {
			return ambiguitySname;
		}

		public void setAmbiguitySname(String ambiguitySname) {
			this.ambiguitySname = ambiguitySname;
		}

		public String getSurl() {
			return surl;
		}

		public void setSurl(String surl) {
			this.surl = surl;
		}
		
		
	}
	
	
	
	public static void main(String[] args){
		System.out.println("====================");
		SearchUtil searchUtil = new SearchUtil();
		String result = searchUtil.retrieve("广");
		System.out.println(result);
	}
	
}
