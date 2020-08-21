/**
 * 
 */
package com.asiainfo.veris.crm.order.pub.util;

import java.util.TreeMap;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * @author tanzheng
 *
 */
public class StringUtils {
	private StringUtils() {
		
		throw new RuntimeException("can't instantiate for me!");
	}
	/**
	 * 
	 * @Description：判断是否为整数
	 * @param:@param str
	 * @param:@return 是整数返回true,否则返回false
	 * @return boolean
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-8-6下午04:08:54
	 */
	public static boolean isInteger(String str) {
		if(com.ailk.org.apache.commons.lang3.StringUtils.isBlank(str)){
			return false;
		}
		
		for(int i = str.length();--i>=0;){
			int chr = str.charAt(i);
			if(chr<48||chr>57)
				return false;
		}
		return true;
	}
	/***
	 * 下划线命名转为驼峰命名
	 * 
	 * @param para
	 *        下划线命名的字符串
	 */
	 
		public static String UnderlineToHump(String para){
			StringBuilder result=new StringBuilder();
			String a[]=para.split("_");
			for(String s:a){
				if (!para.contains("_")) {
	                result.append(s);
	                continue;
	            }
				if(result.length()==0){
					result.append(s.toLowerCase());
				}else{
					result.append(s.substring(0, 1).toUpperCase());
					result.append(s.substring(1).toLowerCase());
				}
			}
			return result.toString();
		}



	/***
	* 驼峰命名转为下划线命名
	 * 
	 * @param para
	 *        驼峰命名的字符串
	 */
	 
		public static String HumpToUnderline(String para){
	        StringBuilder sb=new StringBuilder(para);
	        int temp=0;//定位
	        if (!para.contains("_")) {
	            for(int i=0;i<para.length();i++){
	                if(Character.isUpperCase(para.charAt(i))){
	                    sb.insert(i+temp, "_");
	                    temp+=1;
	                }
	            }
	        }
	        return sb.toString().toUpperCase();
	    }
		/**
		 * 
		 * @description 将dataMap中的数据
		 * @param @param param
		 * @param @return
		 * @return IData
		 * @author tanzheng
		 * @date 2019年5月21日
		 * @param param
		 * @return
		 */
		public static IData HumpToUnderline(IData param){
			IData result = new DataMap();
			if(IDataUtil.isEmpty(param)){
				return result;
			}
			
			for (String key : param.keySet()) {
				result.put(HumpToUnderline(key), param.getString(key));
				  }
			return result;
			
		}
		/**
		 * @description 将map转成String key1value1key2value2……
		 * @param @param data
		 * @param @return
		 * @return String
		 * @author tanzheng
		 * @date 2019年6月21日
		 * @param data
		 * @return
		 */
		public static String mapToString(TreeMap<String, Object> data) {
			StringBuilder sBuilder = new StringBuilder();
			for (String key : data.keySet()) {
				sBuilder.append(key).append(data.get(key));
			}
			return sBuilder.toString();
			
		}

	/**
	 * 模糊化名字
	 * 例：王小二---->xx二
	 * @param custName
	 * @return
	 */
	public static String fuzzyNameForWidenet(String custName) {
		int length = custName.length();
		String suffix = custName.substring(length-1,length);
		String result = "";
		for(int i=0 ; i<length-1;i++){
			result += "x";
		}
		result += suffix;
		return  result;
	}
	public static void main(String[] args){
		System.out.println(StringUtils.fuzzyNameForWidenet("王二"));
	}

}
