package com.asiainfo.veris.crm.iorder.soa.group.param.vpmn;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class QueryCrtMbVpmnAttrParamBean  extends QueryAttrParamBean
{
   
    public static IData queryVpnParamAttrForCrtInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        IData paraInfo = new DataMap();
        String ecUserId = param.getString("EC_USER_ID");
        String userId = param.getString("USER_ID");
        //String eparchyCode = param.getString("EPARCHY_CODE");
        
        IDataset dataSet =  UserAttrInfoQry.getUserProductAttrByUserIdAndUserIdA(userId, ecUserId, "P");
        if (IDataUtil.isNotEmpty(dataSet))
        {
            for (int i = 0; i < dataSet.size(); i++)
            {
                IData user_attrida = (IData) dataSet.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataSet.set(i, user_attrida);
                }
            }
        }
        
        if (IDataUtil.isNotEmpty(dataSet))
        {
            attrItemA = IDataUtil.hTable2STable(dataSet, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        
//        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond("8000", "P", "1", null);
//        if(IDataUtil.isNotEmpty(pzAttrItems))
//        {
//            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
//            // 方便前台取下拉框选项值
//            transComboBoxValue(attrItemA, pzAttrItem);
//        }
        
        IDataset attrInfos = UserOtherInfoQry.getUserOtherInfoByAll(ecUserId, "VPMN_GRPCLIP");
        if (IDataUtil.isNotEmpty(attrInfos))
        {
        	IData attrInfo = attrInfos.getData(0);
        	if(IDataUtil.isNotEmpty(attrInfos))
     	   	{
        		String grpClipType = attrInfo.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
        		String grpUserClipType = attrInfo.getString("RSRV_STR2","");//GRP_USER_CLIP_TYPE 选择号显方式
        		String grpUserMod = attrInfo.getString("RSRV_STR3","");//GRP_USER_MOD 成员修改号显方式
        		paraInfo.put("GRP_CLIP_TYPE", grpClipType);
        		paraInfo.put("GRP_USER_CLIP_TYPE", grpUserClipType);
        		paraInfo.put("GRP_USER_MOD", grpUserMod);
     	   	}
        }
        
        
        IData userAttrInfo  = new DataMap();
        userAttrInfo.putAll(paraInfo);
        IData userAttrItem = IDataUtil.iDataA2iDataB(userAttrInfo, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
     
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
              
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                }
            }
        }
        return result;

    }
    
    
    public static void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
    {
        if (IDataUtil.isEmpty(pzAttrItem))
        {
            return;
        }
        else if (IDataUtil.isEmpty(userAttrItem)) 
        {
            userAttrItem.putAll(pzAttrItem);
        }
        else 
        {
            for (Iterator iterator = pzAttrItem.keySet().iterator(); iterator.hasNext();)
            {
                String datakey = (String) iterator.next();
                IData tempData = userAttrItem.getData(datakey);
                IData tempData2 = pzAttrItem.getData(datakey);
                if (IDataUtil.isEmpty(tempData))
                {
                    tempData = tempData2;
                    userAttrItem.put(datakey, tempData);
                }
                else if (IDataUtil.isNotEmpty(tempData2))
                {
                    tempData.put("DATA_VAL", tempData2.getDataset("DATA_VAL"));
                }
            }
        }
    }
    
}
