
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class AttrTrans
{
    /**
     * 获取属性值
     * 
     * @param input
     * @param attrCode
     * @return
     */
    public static String getInfoValue(IData input, String attrCode)
    {
        String value = input.getString(attrCode);
        if (StringUtils.isNotBlank(value))
        {
            return value;
        }
        IDataset attrParams = input.getDataset("ATTR_PARAM");
        if (IDataUtil.isNotEmpty(attrParams))
        {
            int size = attrParams.size();
            for (int i = 0; i < size; i++)
            {
                IData attrParam = attrParams.getData(i);
                if (attrParam.getString("ATTR_CODE").equals(attrCode))
                {
                    return attrParam.getString("ATTR_VALUE");
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    public static void main(String[] args)
    {
        IDataset d = new DatasetList();
        d.add("s");
        d.add("a");
        String s = "[\"301\",\"BLBY_SERV_TYPE\"]";
        IData b = new DataMap();
        b.put("INFO_CODE", s);
        Object c = b.get("INFO_CODE");
        IDataset a = new DatasetList(s);
    }

    /**
     * 获取属性值
     * 
     * @param input
     * @param attrCode
     * @return
     */
    public static boolean setInfoValue(IData input, String attrCode, String attrValue)
    {

        IDataset attrParams = input.getDataset("ATTR_PARAM");
        if (IDataUtil.isNotEmpty(attrParams))
        {
            int size = attrParams.size();
            for (int i = 0; i < size; i++)
            {
                IData attrParam = attrParams.getData(i);
                if (attrParam.getString("ATTR_CODE").equals(attrCode))
                {
                    attrParam.put("ATTR_VALUE", attrValue);
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    public static void trans(IData input)
    {
        String infoCode = input.getString("INFO_CODE", "");
        if (infoCode.startsWith("["))
        {
            JSONArray infoCodes = JSONArray.fromObject(input.getString("INFO_CODE"));
            JSONArray infoValues = JSONArray.fromObject(input.getString("INFO_VALUE"));
            if (infoCodes != null && infoCodes.size() > 0)
            {
                IDataset attrParams = new DatasetList();
                int size = infoCodes.size();
                for (int i = 0; i < size; i++)
                {
                    IData attrParam = new DataMap();
                    attrParam.put("ATTR_CODE", infoCodes.get(i).toString());
                    attrParam.put("ATTR_VALUE", infoValues.get(i).toString());
                    attrParams.add(attrParam);
                }
                input.put("ATTR_PARAM", attrParams);
            }
        }
        else
        {
            if (StringUtils.isNotBlank(infoCode.toString()))
            {
                IData attrParam = new DataMap();
                attrParam.put("ATTR_CODE", infoCode.toString());
                attrParam.put("ATTR_VALUE", input.getString("INFO_VALUE"));
                IDataset attrParams = new DatasetList();
                attrParams.add(attrParam);
                input.put("ATTR_PARAM", attrParams);
            }
        }
    }
    
    public static void transExtendInfo(IData input)
    {
    	String extendInfoStr = input.getString("EXTEND_INFO");
    	
    	if (StringUtils.isEmpty(extendInfoStr)) {
    		return;
    	}
    	
    	IDataset extendInfo = input.getDataset("EXTEND_INFO");
        IDataset attrParams = new DatasetList();
        for (int i = 0;DataUtils.isNotEmpty(extendInfo) && i < extendInfo.size(); i++)
        {
            String infoCode = extendInfo.getData(i).getString("INFO_CODE");
            String infoValue = extendInfo.getData(i).getString("INFO_VALUE");
            if (StringUtils.isNotBlank(infoCode)) {
            	IData attrParam = new DataMap();
                
                attrParam.put("ATTR_CODE", infoCode);
                attrParam.put("ATTR_VALUE",infoValue);
                attrParams.add(attrParam);
            }

        }
        input.put("ATTR_PARAM", attrParams);
    }
}
