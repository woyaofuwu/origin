package com.asiainfo.veris.crm.iorder.soa.group.param.workphone;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

import net.sf.json.JSONArray;

public class QueryCrtMbWorkPhoneAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryWorkPhoneAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("6013", "P", "1", null);
        
        IData attrItemA = new DataMap();
        
        
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        
        IData workPhoneMebData=new DataMap();
        String mebUserId = param.getString("USER_ID");
        IData userDiscntParam=new DataMap();
        userDiscntParam.put("MEB_USER_ID", mebUserId);
        IDataset otherDataset = CSAppCall.call("CS.UserDiscntInfoQrySVC.queryUserDiscntToCommparaByUID", userDiscntParam);
        boolean userParamFlag=false;
        if (IDataUtil.isNotEmpty(otherDataset))
        {
            IDataset datasetA= new DatasetList();
            for (int i = 0; i < otherDataset.size(); i++)
            {
                IData otherInfo = otherDataset.getData(i);
                IData otherData = new DataMap();
                otherData.put("pam_DISCNT_CODE", otherInfo.getString("DISCNT_CODE", ""));
                otherData.put("pam_DISCNT_NAME", otherInfo.getString("DISCNT_NAME", ""));
                otherData.put("pam_CDISCNT_CODE", otherInfo.getString("PARA_CODE1", ""));
                otherData.put("pam_START_DATE", otherInfo.getString("START_DATE", ""));
                otherData.put("pam_END_DATE", otherInfo.getString("END_DATE", ""));
                datasetA.add(otherData);
            }
            workPhoneMebData.put("userParamInfos", datasetA);
            userParamFlag=true;
        }
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(workPhoneMebData, "DATA_VAL");
        
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
        if(userParamFlag) {
        	IData infosData =new DataMap();
        	infosData.put("DATA_VAL", JSONArray.fromObject(result.getData("userParamInfos").getDataset("DATA_VAL")).toString().replaceAll("\"", "\'"));
            result.put("userParamInfos", infosData);
        }
        
        return result;
       
    }
    
}

