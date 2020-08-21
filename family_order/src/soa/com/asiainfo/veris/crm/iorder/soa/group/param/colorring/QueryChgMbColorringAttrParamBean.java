package com.asiainfo.veris.crm.iorder.soa.group.param.colorring;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class QueryChgMbColorringAttrParamBean  extends QueryAttrParamBean
{
   
    public static IData queryColorringAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        //IData transData = new DataMap();
        IData parainfo = new DataMap();
        String ecUserId = param.getString("EC_USER_ID");
        String userId = param.getString("USER_ID");
       // String offerCode = param.getString("OFFER_CODE");
        String eparchyCode = param.getString("EPARCHY_CODE");
        //IData results = initChgUs(userId,offerCode);//取老代码父类
        IDataset dataset =  UserAttrInfoQry.getUserProductAttrByUserIdAndUserIdA(userId, ecUserId, "P");
        
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
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");

        }
        
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond("6200", "P", "1", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
        
        //IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(ecUserId);
       
        IData userInfoParam = new DataMap();
        userInfoParam.put("USER_ID", userId);
        userInfoParam.put("RSRV_VALUE_CODE", "DLMR");
        userInfoParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset userresinfo = CSAppCall.call("CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", userInfoParam);

        IData data1 = new DataMap();
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            data1.put("CANCEL_LING", "1");
        }
        else
        {
            data1.put("CANCEL_LING", "0");
        }
        
        IData userattritem = IDataUtil.iDataA2iDataB(data1, "ATTR_VALUE");

        transComboBoxValue(userattritem,attrItemA);
     
        if (IDataUtil.isNotEmpty(userattritem))
        {
            Set<String> propNames = userattritem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userattritem.getData(key);
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
