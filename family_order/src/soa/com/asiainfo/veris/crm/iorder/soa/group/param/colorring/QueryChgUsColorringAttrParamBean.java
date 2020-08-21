package com.asiainfo.veris.crm.iorder.soa.group.param.colorring;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class QueryChgUsColorringAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryColorringAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
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
        
        IData userattr = new DataMap();
        IData uinfos = new DataMap();
        if (attrItemA == null || attrItemA.isEmpty())
        {
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

            
            uinfos.put("PASSWORD", userInfo.getString("RSRV_STR4", ""));
            uinfos.put("MANAGER_NAME", userInfo.getString("RSRV_STR6", ""));
            uinfos.put("MANAGER_PHONE", userInfo.getString("RSRV_STR7", ""));
            uinfos.put("MANAGER_INFO", userInfo.getString("RSRV_STR8", ""));
        }
        
        String staffId=getVisit().getStaffId();
        String dataPriv = "GRP_COLORRING_FEE";
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, dataPriv);
        uinfos.put("HAS_FEE_PRIV", bool);
        uinfos.put("SERIAL_NUMBER", param.getString("GRP_SN", ""));
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
