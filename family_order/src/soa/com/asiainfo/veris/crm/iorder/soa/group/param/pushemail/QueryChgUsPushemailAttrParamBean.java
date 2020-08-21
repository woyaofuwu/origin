package com.asiainfo.veris.crm.iorder.soa.group.param.pushemail;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgUsPushemailAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryPushemailAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        /*String custId = param.getString("CUST_ID");*/
        String userId = param.getString("USER_ID");
        
        IDataset dataset = UserAttrInfoQry.getUserProductAttrByUTForGrp(userId, "P", null);
        IData attrItemA = new DataMap();
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData user_attrida = (IData) dataset.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataset.set(i, user_attrida);
                }

            }
        }
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
        
       
        IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userVpnData =new DataMap();
        if(IDataUtil.isNotEmpty(userInfo)){
            userVpnData = userInfo.getData(0);
        }

        IData userAttrItem = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        
        
        /*IDataset otherInfoList = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, "MUTISUPERTEL");
        IDataset superInfoList = new DatasetList();

        if (IDataUtil.isNotEmpty(otherInfoList))
        {
            for (int i = 0, row = otherInfoList.size(); i < row; i++)
            {
                IData otherData = otherInfoList.getData(i);

                IData tempOther = new DataMap();
                IData userInfos =  UserInfoQry.getMebUserInfoBySN(otherData.getString("RSRV_VALUE", ""));
                if(IDataUtil.isNotEmpty(userInfos)){
                    tempOther.put("E_EPARCHY_NAME", userInfos.getString("EPARCHY_NAME"));  
                }
                tempOther.put("EXCHANGETELE_SN", otherData.getString("RSRV_VALUE", ""));
                tempOther.put("E_CUST_NAME", otherData.getString("RSRV_STR1", ""));
                tempOther.put("E_CUST_ID", otherData.getString("RSRV_STR5", ""));
                tempOther.put("E_USER_ID", otherData.getString("RSRV_STR4", ""));
                tempOther.put("E_BRAND_CODE", otherData.getString("RSRV_STR2", ""));
                tempOther.put("E_EPARCHY_CODE", otherData.getString("RSRV_STR3", ""));
                tempOther.put("MAXWAITINGLENGTH", otherData.getString("RSRV_STR6", ""));
                tempOther.put("CALLCENTERTYPE", otherData.getString("RSRV_STR7", ""));
                tempOther.put("CALLCENTERSHOW", otherData.getString("RSRV_STR8", ""));
                tempOther.put("CORP_REGCODE", otherData.getString("RSRV_STR9", ""));
                tempOther.put("CORP_DEREGCODE", otherData.getString("RSRV_STR10", ""));

                superInfoList.add(tempOther);
            }
        }
        IData tempMap = new DataMap();
        tempMap.put("ATTR_VALUE", JSONArray.fromObject(superInfoList).toString().replaceAll("\"", "\'"));
        userAttrItem.put("SUPERNUMBER", tempMap);*/
        
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                if(IDataUtil.isEmpty(attrCodeInfo))
                  continue;
                if ("VISION_NUMBER".equals(key))
                {
                    IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    if(StringUtils.isBlank(attrItemValue))
                       continue;
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
