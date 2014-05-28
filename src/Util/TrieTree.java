package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javax.print.DocFlavor.STRING;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.lang.WordUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


import Util.AppUtil;

/**
 * 测试trie树，用于搜索的提示模块
 * @author yinchuandong
 *
 */
public class TrieTree {

	private TrieNode root = null;
	private HashMap<String, Integer> sentenceMap = null;
	
	public TrieTree(){
		root = new TrieNode();
		sentenceMap = new HashMap<String, Integer>();
	}
	
	/**
	 * 加入一个词到树中
	 * @param word 如：长隆欢乐世界
	 * @param viewCount 如：36555
	 */
	public void add(String word, int viewCount){
		//根节点为空
		TrieNode node = root;
		word = word.trim();
		sentenceMap.put(word, viewCount);
		
		for(int i=0; i<word.length(); i++){
			String key = word.substring(i, i+1);
			if (!node.getChildren().containsKey(key)) {
				TrieNode sub = new TrieNode();
				sub.setWord(key);
				sub.setCount(viewCount);
				node.getChildren().put(key, sub);
			}
			node.setTerminal(false);
			node = node.getChildren().get(key);
		}
		node.setTerminal(true);
		node.setSentence(true);
		
	}
	
	/**
	 * 查找指定的前缀
	 * @param word
	 * @return
	 */
	public ArrayList<Sentence> find(String word){
		ArrayList<Sentence> result = new ArrayList<Sentence>();
		 
		TrieNode node = root;
		word = word.trim();
		String prefix = "";
		for(int i=0; i<word.length(); i++){
			String key = word.substring(i, i+1);
			if (node.getChildren().containsKey(key)) {
				node = node.getChildren().get(key);
				prefix += node.getWord();
			}else{
				return result;
			}
		}
		
		//节点栈，用来保存访问过的节点
		Stack<TrieNode> nodeStack = new Stack<TrieNode>();
		//字符栈，用来保存访问过的路径的字符
		Stack<String> strStack = new Stack<String>();
		
		//初始化堆栈
		Iterator<String> iterator = node.getChildren().keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			nodeStack.push(node.getChildren().get(key));
			strStack.push(prefix);
		}
		
		String tmpStr = "";
		while(!nodeStack.empty()){
			TrieNode tmpNode = nodeStack.pop();
			tmpStr = strStack.pop() + tmpNode.word;
			
			if (tmpNode.isTerminal()) {//如果是终端词，则构成一个句子，加入到结果列表中
				Sentence sentence = new Sentence(tmpStr, tmpNode.getCount());
				result.add(sentence);
				tmpStr = "";
			}else{
				//如果不是终端词，则将该词的children压栈，等待访问
				Iterator<String> iterChild = tmpNode.getChildren().keySet().iterator();
				while (iterChild.hasNext()) {
					String key = iterChild.next();
					nodeStack.push(tmpNode.getChildren().get(key));
					strStack.push(tmpStr);
				}
			}
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	/**
	 * 搜索文档
	 * @param keyWord
	 * @return
	 */
	public static String doSearch(String keyWord, String dirpath){
		JSONObject resultObj = JSONObject.fromObject("{}");;
		if (keyWord == null || keyWord.length() <1) {
			resultObj.put("info", AppUtil.toUnicode("关键字不合法"));
			resultObj.put("status", "0");
			resultObj.put("data", JSONArray.fromObject("[]"));
			return resultObj.toString().replaceAll("\\\\u", "\\u");
		}
		
		keyWord = keyWord.trim();
		String fileName = keyWord.substring(0,1) + ".txt";
		String filePath = dirpath + "/" + fileName;
		
		TrieTree tree = new TrieTree();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] arr = line.split(" ");
				String word = arr[0];
				int viewCount = Integer.parseInt(arr[1]);
				tree.add(word, viewCount);
			}
		} catch (IOException e) {
			e.printStackTrace();
			try{
				if (reader != null) {
					reader.close();
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
			resultObj.put("info", AppUtil.toUnicode("服务器错误"));
			resultObj.put("status", "0");
			resultObj.put("data", JSONArray.fromObject("[]"));
			return resultObj.toString().replaceAll("\\\\u", "\\u");
		}
		
		ArrayList<Sentence> list = tree.find(keyWord);
		ArrayList<Sentence> keyList = new ArrayList<Sentence>();
		int len = (list.size() > 10) ? 10 : list.size();
		for (int i=0; i<len; i++){
			Sentence sentence = list.get(i);
			sentence.setWord(AppUtil.toUnicode(sentence.getWord()));
			keyList.add(sentence);
		}
		
		JSONArray jsonData = JSONArray.fromObject(keyList);
		resultObj.put("info", AppUtil.toUnicode("返回成功"));
		resultObj.put("status", "1");
		resultObj.put("data", jsonData);
		return resultObj.toString().replaceAll("\\\\u", "\\u");
	}
	
	public static void main(String[] args) throws IOException{
		System.out.println("-----------------");
		long begin = System.currentTimeMillis();
		
		String result = TrieTree.doSearch("广州科技", "E:\\traveldata\\keyword");
		System.out.println(result);
		
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - begin));
	}
	
	
	
	/**
	 * 搜索的结果对象
	 * @author yinchuandong
	 *
	 */
	public class Sentence implements Comparable<Sentence>{
		/**
		 * 句子，如:广州白云山
		 */
		private String word = null;
		/**
		 * 访问量，如：36555
		 */
		private int viewCount = 0;
		
		public Sentence(String word, int viewCount){
			this.word = word;
			this.viewCount = viewCount;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}

		@Override
		public int compareTo(Sentence o) {
			if (this.getViewCount() > o.getViewCount()) {
				return -1;
			}else{
				return 1;
			}
		}
		
		
	}
	
	/**
	 * 字典树对象
	 * @author yinchuandong
	 *
	 */
	public class TrieNode{
		
		private String word = null;
		private HashMap<String, TrieNode> children = null;
		private int count = 0;
		private boolean isSentence = false;
		private boolean isTerminal = false;
		
		public TrieNode(){
			children = new HashMap<String, TrieNode>();
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}
		
		public HashMap<String, TrieNode> getChildren() {
			return children;
		}

		public void setChildren(HashMap<String, TrieNode> children) {
			this.children = children;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isSentence() {
			return isSentence;
		}

		public void setSentence(boolean isSentence) {
			this.isSentence = isSentence;
		}

		public boolean isTerminal() {
			return isTerminal;
		}

		public void setTerminal(boolean isTerminal) {
			this.isTerminal = isTerminal;
		}

		
		
	}
}
