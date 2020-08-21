package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.parse.TextToken;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.ComponentFactory;

public class CommonTools 
{
	/**
     * 根据regex分割字符串
     * 
     * @param src
     *            String
     * @param regex
     *            String
     * @return String[]
     */
    public static String[] split(String src, String regex)
    {
        if (src == null || src.length() == 0)
        {
            return new String[0];
        }
        if (src.indexOf(regex) == -1)
        {
            return new String[]
            { src };
        }

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(src, regex);
        String str = null;
        while (st.hasMoreTokens())
        {
            str = st.nextToken();
            list.add(str);
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
	 * 拼字符传
	 * 
	 * @param data
	 * @param value
	 * @return
	 */
	public static String spellStr(IData data, String... value) 
	{
		StringBuilder sbd = new StringBuilder();
		for (int i = 0; i < value.length; i++) 
		{
			String tempStr = data.getString(value[i], "");
			if (StringUtils.isNotEmpty(tempStr)) 
			{
				sbd.append(tempStr);
			} else 
			{
				break;
			}
		}

		return sbd.toString();
	}

	
	public static IComponent anyComponents(IPage page, ILocation location, IComponent container, INamespace namespace) throws Exception
    {
        IData part = new DataMap();
        part.put("jwcid", "@Any");
        part.put("element", "div");
        IComponent part_comp = ComponentFactory.builderComponent(page, location, container, namespace, "@Any", part);
        
        return part_comp;
    }
	
	public static TextToken dealTextToken(StringBuilder sbf,ILocation location) throws Exception
    {
        char []ch = new char[sbf.toString().length()];   
        ch = sbf.toString().toCharArray();
        TextToken tt = new TextToken(ch,0,ch.length-1,location);
        
        return tt;
    }
	
	/**
     * 表达式特殊处理
     * @param str
     * @param pointTwo
     * @return
     * @throws Exception
     */
    public static String dealStr(String str, String pointTwo , boolean flag) throws Exception
    {
        if(str.indexOf(":") != -1 && str.indexOf("ognl") == -1)
        {
            String resultStr = LayoutConstants.STATIC_ONE;
            String[] strStr = str.split(":");
            if(strStr.length == 2)
            {
            	String startStr = strStr[0];
            	if(!LayoutConstants.STR_START.contains(startStr) || startStr.length() != 1)
            	{
            		return str;
            	}
            	
                String[] dealStr = strStr[1].split(",");
                for(int i = 0 ; i < dealStr.length ; i ++)
                {
                    if(pointTwo.equals(dealStr[i]))
                    {
                        resultStr = LayoutConstants.STATIC_ZERO;
                        break;
                    }
                }
            }
            else
            {
                resultStr = LayoutConstants.STATIC_ONE;
            }
                
            if(flag)
            {
               return resultStr;
            }
            else
            {
                if(LayoutConstants.STATIC_ONE.equals(resultStr))
                {
                    resultStr = LayoutConstants.STATIC_ZERO;
                }
                else if(LayoutConstants.STATIC_ZERO.equals(resultStr))
                {
                    resultStr = LayoutConstants.STATIC_ONE;
                }
                return resultStr;
            }
            
        }
        else
        {
            return str;
        }
    }
}
