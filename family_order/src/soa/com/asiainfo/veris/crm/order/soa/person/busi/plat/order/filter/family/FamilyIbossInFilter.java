
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.family;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 家庭开放平台业务受理接口一级boss入参转换
 * 
 * @author lihb3 20170223
 */
public class FamilyIbossInFilter implements IFilterIn
{
	Logger log = Logger.getLogger(FamilyIbossInFilter.class);

	@Override
	public void transferDataInput(IData input) throws Exception
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
                    String attrCode = dealAttrCodeName(infoCodes.get(i).toString());
                    attrParam.put("ATTR_CODE", attrCode);
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
                String attrCode = dealAttrCodeName(infoCode.toString());
                attrParam.put("ATTR_CODE", attrCode);
                attrParam.put("ATTR_VALUE", input.getString("INFO_VALUE"));
                IDataset attrParams = new DatasetList();
                attrParams.add(attrParam);
                input.put("ATTR_PARAM", attrParams);
            }
        }
	}
	
	//将平台传来的属性名转为CRM魔百盒、宽带业务定义的
	private String dealAttrCodeName(String attrCode){
		if("MAC_ID".equals(attrCode)){
        	attrCode = "STDBID";   
        }else if("MAC_TYPE".equals(attrCode)){
        	attrCode = "MACTYPE"; 
        }else if("BROADBAND_ACCOUNT".equals(attrCode)){
        	attrCode = "BROADBANDID";
        }
        return attrCode;
	}

}
