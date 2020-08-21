package com.asiainfo.veris.crm.iorder.soa.group.param.sms;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

import net.sf.json.JSONArray;

public class QueryChgUsSmsAttrParamBean extends QueryAttrParamBean
{
   
    public static IData querySmsAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
        String userId = param.getString("USER_ID");
        
        
        
        IData resultA = new DataMap();
        IDataset datasA = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IDataset dataChaSpec = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "2", null);
        IData attrIteA = new DataMap();
        IData attrIteB = new DataMap();
        if (IDataUtil.isNotEmpty(datasA))
        {
        	attrIteA = IDataUtil.hTable2STable(datasA, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        if (IDataUtil.isNotEmpty(dataChaSpec))
        {
        	attrIteB = IDataUtil.hTable2STable(dataChaSpec, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        
        if (IDataUtil.isNotEmpty(attrIteA))
        {
            Set<String> propNames = attrIteA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrIteA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    resultA.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    resultA.put(key, attrItem);
                }
                
            }
        }
        if (IDataUtil.isNotEmpty(attrIteB))
        {
            Set<String> propNames = attrIteB.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrIteB.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                attrItem.put("DATA_VAL", workTypeCodeInfo);
                resultA.put(key, attrItem);
            }
        }
        
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
        
       
        /*IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userVpnData =new DataMap();
        if(IDataUtil.isNotEmpty(userInfo)){
            userVpnData = userInfo.getData(0);
        }*/
        IData userattr = new DataMap();
        IData datatemp = new DataMap();
        datatemp.put("USER_ID", userId);
        // 调用后台服务，获取用户平台服务信息
        IDataset datasetA = CSAppCall.call("CS.UserGrpPlatSvcInfoQrySVC.getLxtGrpPlatSvcByUserId", datatemp);
        if (IDataUtil.isNotEmpty(datasetA))
        {
            userattr = datasetA.getData(0);
        }
        
        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        IData userInfo =CSAppCall.callOne("CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", userParam);

        IData uinfos = new DataMap();
        uinfos.put("RSRV_STR6", userInfo.getString("RSRV_STR6"));
        uinfos.put("RSRV_STR7", userInfo.getString("RSRV_STR7"));
        uinfos.put("RSRV_STR8", userInfo.getString("RSRV_STR8"));

        userattr.putAll(uinfos);
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(userattr, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                if(IDataUtil.isEmpty(attrCodeInfo))
                  continue;
                String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                if(StringUtils.isBlank(attrItemValue))
                   continue;
                attrItem.put("DATA_VAL", attrItemValue);
                result.put(key, attrItem);
               
            }
        }
        IData userattritem = IDataUtil.iDataA2iDataB(userattr, "ATTR_VALUE");
        IData attrItemData =new DataMap();
        IData attrItemSetData =new DataMap();
        attrItemData.put("DATA_VAL", JSONArray.fromObject(userattritem).toString().replaceAll("\"", "\'"));
        attrItemSetData.put("DATA_VAL", JSONArray.fromObject(IDataUtil.iData2iDataset(userattr, "ATTR_CODE", "ATTR_VALUE")).toString().replaceAll("\"", "\'"));
        result.put("ATTRITEM", attrItemData); // 页面上属性值，取是的userattritem里面的，而不是父类attrtiem中的
        result.put("ATTRITEMSET", attrItemSetData);
        
        
        result.put("OPER_STATE", resultA.getData("OPER_STATE"));
        result.put("TD_M_CHARGETYPE", resultA.getData("TD_M_CHARGETYPE"));
        result.put("TD_M_BIZSTATUS", resultA.getData("TD_M_BIZSTATUS"));
        result.put("TD_M_BIZBLACKWHITE", resultA.getData("TD_M_BIZBLACKWHITE"));
        result.put("TD_M_BIZDETAILCLASS", resultA.getData("TD_M_BIZDETAILCLASS"));
        result.put("TD_M_PRECHARGETAG", resultA.getData("TD_M_PRECHARGETAG"));
        result.put("TD_M_DEFAULTECGNLANG", resultA.getData("TD_M_DEFAULTECGNLANG"));
        result.put("PRODUCT_TYPE", resultA.getData("PRODUCT_TYPE"));
        
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
