package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class FileUtil {

	
	/**
	 * ¶ÁÈ¡ÎÄ¼þ
	 * @param file
	 * @return
	 */
	public static String readFile(File file){
		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while((tmp = reader.readLine()) != null){
				result += tmp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public static void main(String[] args){
		File dir = new File("E:\\web");
		File[] files = dir.listFiles();
		for (int i = 0; i < 1; i++) {
			File file = files[i];
			String content = FileUtil.readFile(file);
			System.out.println(content);
		}
		
//		File dir2 = new File("E:\\ailu-1.json");
//		String content = FileUtil.readFile(dir2);
//		System.out.println(content);
	}
}
