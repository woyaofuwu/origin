package com.asiainfo.veris.crm.iorder.soa.group.param.m2m;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryCrtMbM2mAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryM2mAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("7050", "P", "1", null);
        
        IData attrItemA = new DataMap();
        
        
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        
        IData parainfoParam = new DataMap();
        parainfoParam.put("SUBSYS_CODE", "CSM");
        parainfoParam.put("PARAM_ATTR", "9980");
        parainfoParam.put("PARAM_CODE", "1");
        parainfoParam.put("EPARCHY_CODE", "0898");
        IDataset applyTypeas = CSAppCall.call("CS.CommparaInfoQrySVC.getCommpara", parainfoParam);
        
        
        
        
        IData WlwMebData=new DataMap();
        WlwMebData.put("APPLY_TYPE_A_LIST", applyTypeas);
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(WlwMebData, "DATA_VAL");
        
        attrItemA.putAll(userAttrItem);
        
        if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
           
            }
        }
        return result;
       
    }
    
}

