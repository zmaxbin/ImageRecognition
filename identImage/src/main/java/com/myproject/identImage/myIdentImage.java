package com.myproject.identImage;

/**
 * @author 郑志彬
 * @time 2017/5/6
 * @Version 2.0
 */

import com.myproject.identImage.sortMap;

import java.awt.Color;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;  
  
import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.methods.GetMethod;  
import org.apache.commons.io.IOUtils;

public class myIdentImage {

  // 下载图片的函数
  public void DownloadImage(int i){
      HttpClient httpClient = new HttpClient();  
      GetMethod getMethod = new GetMethod("http://www.hxee.com.cn/ValidateCode.aspx?rd=0.4402198938492241"); 
      try{
	      int statusCode = httpClient.executeMethod(getMethod);  
	      if (statusCode != HttpStatus.SC_OK) {  
	          System.err.println("Method failed: " + getMethod.getStatusLine());  
	          return ;  
	      }  
	      String picName = "D://testImage/";  
	      File filepic=new File(picName);  
	      if(!filepic.exists())  
	          filepic.mkdir();
	      File filepicF=new File(picName+String.valueOf(i)+".jpg");  
	      InputStream inputStream = getMethod.getResponseBodyAsStream();  
	      OutputStream outStream = new FileOutputStream(filepicF);  
	      IOUtils.copy(inputStream, outStream);  
	      outStream.close();
      }catch(Exception e){
    	  System.out.println(e);
      }
  }

  // 去除图片的干扰
  public BufferedImage CleanImpurity(int l){
	  File file = new File("D://testImage/"+String.valueOf(l)+".jpg");
      BufferedImage BI = null;
      try{
          BI = ImageIO.read(file);
      }catch(Exception e){
          e.printStackTrace();
      }
      int width = BI.getWidth();
      int height = BI.getHeight();
      int minx = BI.getMinX();
      int miny = BI.getMinY();
      int[] rgb = new int[3];
      for (int i = minx; i < width; i++) {
          for (int j = miny; j < height; j++) {
              int pixel = BI.getRGB(i, j);//获得像素值
              rgb[0] = (pixel & 0xff0000) >> 16; //获得RGB值
              rgb[1] = (pixel & 0xff00) >> 8;
              rgb[2] = (pixel & 0xff);
              if(rgb[1]+rgb[2]>80){
            	  if(rgb[0]<rgb[1]+rgb[2]+60){
            		  BI.setRGB(i, j, Color.WHITE.getRGB());
            	  }
            	  else{
            		  BI.setRGB(i, j, Color.BLACK.getRGB());
            	  }
              }
              else if(rgb[0]<2*(rgb[1]+rgb[2])){
                  BI.setRGB(i, j, Color.WHITE.getRGB());
              }
              else{
            	  BI.setRGB(i, j, Color.BLACK.getRGB());
              }
          }
      }
      return BI;
  }
  
  //对图像进行分割，这里要求字符之间没有粘连，但是实际中处理完偶尔还是有粘连
  public  List<BufferedImage> splitImage(BufferedImage img){

      int width = img.getWidth();
      int height = img.getHeight();
      int minx = img.getMinX();
      int miny = img.getMinY();
      Color myBlack = new Color(0,0,0);
      int brgb = myBlack.getRGB();
      int[] splitLineArr = new int[8];
      int m = 0;
      boolean flag = true;
      try{
	      for (int i = minx; i < width; i++) {
	    	  int cnt = 0;
	    	  for (int j = miny; j < height; j++) {
	    		  int pixel = img.getRGB(i, j);
	    		  if(pixel == brgb){
	    			  cnt++;
	    		  }
	    	  }
	    	  if(cnt > 0 && flag == true){
	    		  splitLineArr[m] = i;
	    		  flag = false;
	    		  m++;
	    	  }
	    	  else if(cnt == 0 && flag == false){
	    		  splitLineArr[m] = i;
	    		  flag = true;
	    		  m++;
	    	  }
	      }
      }catch(Exception e){
    	  System.out.println(e);
    	  return null;
      }
      List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
      try{
	      subImgs.add(img.getSubimage(splitLineArr[0], 6, splitLineArr[1]-splitLineArr[0], 12));  
	      subImgs.add(img.getSubimage(splitLineArr[2], 6, splitLineArr[3]-splitLineArr[2], 12));  
	      subImgs.add(img.getSubimage(splitLineArr[4], 6, splitLineArr[5]-splitLineArr[4], 12));  
	      subImgs.add(img.getSubimage(splitLineArr[6], 6, splitLineArr[7]-splitLineArr[6], 12));
      }catch(Exception e){
    	  System.out.println(e);
    	  return null;
      }
      return subImgs;
      
  }
  
  public void getNum(BufferedImage img){
	  if(img == null){
		  return ;
	  }else{
		  String []name_list = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		  Map<String, Integer> map = new HashMap<String, Integer>();
		  int width;
		  int height;
		  int o_width = img.getWidth();
		  int o_height = img.getHeight();
		  for(int m=0;m<36;m++){
			  for(int k=0;k<9;k++){
				  int cnt = 0;
				  File file = new File("D:/JavaCode/ImageRecognition/ImageRecognition/Trainimg/"+name_list[m]+"_"+String.valueOf(k)+".jpg");
			      BufferedImage BI = null;
			      try{
			          BI = ImageIO.read(file);
			      }catch(Exception e){
			    	  System.out.println("D:/JavaCode/ImageRecognition/ImageRecognition/Trainimg/"+name_list[m]+"_"+String.valueOf(k)+".jpg");
			          e.printStackTrace();
			      }
			      int r_width = BI.getWidth();
			      int r_height = BI.getHeight();
			      if(-2<=(r_width-o_width) && (r_width-o_width)<=2){
				      if(o_width<=r_width){
				    	  width = o_width;
				      }else{
				    	  width = r_width;
				      }
				      if(o_height<=r_height){
				    	  height = o_height;
				      }else{
				    	  height = r_height;
				      }
				      for(int i=0;i<r_width;i++){
				    	  for(int j=0;j<r_height;j++){
				    		  int pixel = BI.getRGB(i, j);
				    		  if(pixel>-14000000){
				    			  BI.setRGB(i, j, Color.WHITE.getRGB());
				    		  }else{
				    			  BI.setRGB(i, j, Color.BLACK.getRGB());
				    		  }
				    	  }
				      }
				      for(int i=0;i<width;i++){
				    	  for(int j=0;j<height;j++){
				    		  int o_pixel = img.getRGB(i, j);
				    		  int r_pixel = BI.getRGB(i, j);
				    		  if(o_pixel == r_pixel){
				    			  cnt ++;
				    		  }
				    	  }
				      }
				      // key值为训练样本的编号，value值为像素一样的数量
				      map.put(name_list[m]+"_"+String.valueOf(k), cnt);
			      }
			  }
		  }
		  sortMymap(map);
	  }
  }
  
  //对每张对比图片的相似值进行排序
  //对排完序后使用KNN算法，取排完之后属于同一类图片数量最多的，但是发现i取1最好
  public void sortMymap(Map<String, Integer> mymap){
	  String[] strArray = new String[1];
	  Map<String, Integer> map = new HashMap<String, Integer>();
	  List<Map.Entry<String,Integer>> list = sortMap.sort_map(mymap);
	  int cnt = 0;
	  for(Map.Entry<String,Integer> mapping:list){
		  if(cnt<1){
			  strArray[cnt] = mapping.getKey();
			  cnt++;
		  }
	  }
	  for(int i=0;i<1;i++){
		  if(map.containsKey(strArray[i])){
			  int temp = map.get(strArray[i]);
			  map.put(strArray[i],temp+1);
		  }else{
			  map.put(strArray[i],1);
		  }
	  }
	  Iterator<String> iter = map.keySet().iterator();
	  int max = 0;
	  String fkey="start";
      while(iter.hasNext()){
          String key=iter.next();
          int value = map.get(key);
          if(value > max){
        	  max = value;
        	  fkey = key;
          }
      }
      System.out.print(fkey.split("_")[0]);  
  }
  
  
  public static void main(String args[]) {
	  for(int i=0;i<10;i++){
		  myIdentImage idcode = new myIdentImage();
//		  idcode.DownloadImage(i);
		  BufferedImage image = idcode.CleanImpurity(i);
		  List<BufferedImage> subImages = idcode.splitImage(image);
		  if(subImages != null){
			  for(BufferedImage BI : subImages){
				  idcode.getNum(BI);
			  }
		  }
		  System.out.println("");
	  }
  }
}  
 


