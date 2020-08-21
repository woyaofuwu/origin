package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class FuzzyPsptUtil {
	 private final static String character = "*";
	 public static String fuzzyPsptId(String psptType, String pstpId) throws Exception
	    {
	        if (StringUtils.isBlank(pstpId))
	        {
	            return "";
	        }

	        pstpId = pstpId.trim();

	        StringBuilder mask = new StringBuilder(20);

	        if (("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType)))
	        {
	            if (pstpId.length() == 18)
	            {
	                // 出生年月日用*代替，最后一位用*代替
	                String ss = pstpId.substring(0, 6);
	                String sm = StringUtils.repeat(character, 8);
	                String se = pstpId.substring(14, pstpId.length() - 1);

	                mask.append(ss).append(sm).append(se).append(character);
	            }
	            else if (pstpId.length() == 15)
	            {
	                // 出生年月日用*代替，最后一位用*代替
	                String ss = pstpId.substring(0, 6);
	                String sm = StringUtils.repeat(character, 6);
	                String se = pstpId.substring(12, pstpId.length() - 1);

	                mask.append(ss).append(sm).append(se).append(character);
	            } else {
	            	mask.append(pstpId);
	            }
	        }
	        else if (pstpId.length() >= 4)
	        {
	            // 末4位用*代替，
	            String ss = pstpId.substring(0, pstpId.length() - 4);
	            String se = StringUtils.repeat(character, 4);

	            mask.append(ss).append(se);
	        }
	        else
	        {
	            mask.append(pstpId);
	        }

	        return mask.toString();
	    }
	//保留第一个姓，其余为*
	 public static String fuzzyName(String name) throws Exception
	    {
	        if (StringUtils.isBlank(name))
	        {
	            return "";
	        }
	
	        name = name.trim();
	
	        StringBuilder mask = new StringBuilder(20);
	
	        if (name.length() == 1)
	        {
	            return name;
	        }
	        else if (name.length() >= 2)
	        {
	            // 张*  李**  广**** 
	            mask.append(name.charAt(0));
	
	            for (int i = 1, iSize = name.length(); i < iSize; i++)
	            {
	                mask.append(character);
	            }
	
	            
	        }
	
	        return mask.toString();
	    }
}
