package com.asiainfo.veris.crm.order.pub.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class SignUtil {
	public static Logger logger = Logger.getLogger(SignUtil.class);
	
    public static String getSDKSign(Map<String, String> map, String privateKey) {
    	logger.debug("========SignUtil.SignUtil    map:"+map);
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())&&!"sign".equals(entry.getKey())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "-");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.substring(0, sb.length()-1);
        String sign =   HMACUtil.encryptHMAC(result, privateKey);
        return sign;
    }
    
    public static Map<String, String> obj2Map(Object obj) {
  		Map<String, String> map = new HashMap<String, String>();
  		// 获取f对象对应类中的所有属性域
  		Field[] fields = obj.getClass().getDeclaredFields();
  		for (int i = 0, len = fields.length; i < len; i++) {
  			String varName = fields[i].getName();
  			////将key置为小写，默认为对象的属性
  			try {
  				// 获取原来的访问控制权限
  				boolean accessFlag = fields[i].isAccessible();
  				// 修改访问控制权限
  				fields[i].setAccessible(true);
  				// 获取在对象f中属性fields[i]对应的对象中的变量
  				Object o = fields[i].get(obj);
  				if (o != null){
  					map.put(varName, String.valueOf(o));
  				}
  				// 恢复访问控制权限
  				fields[i].setAccessible(accessFlag);
  			} catch (IllegalArgumentException ex) {
  				ex.printStackTrace();
  			} catch (IllegalAccessException ex) {
  				ex.printStackTrace();
  			}
  		}
  		return map;
  	}
    

}
