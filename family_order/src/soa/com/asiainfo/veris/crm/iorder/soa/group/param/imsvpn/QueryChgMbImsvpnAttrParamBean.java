package com.asiainfo.veris.crm.iorder.soa.group.param.imsvpn;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgMbImsvpnAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryImsvpnParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        IData parainfo = new DataMap();
        String ecUserId = param.getString("EC_USER_ID");
        String userId = param.getString("USER_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
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
        
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond("8001", "P", "1", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
        
        IData userattrinfo = UserVpnInfoQry.getMemberVpnByUserId(userId, ecUserId, eparchyCode);

        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。
        IDataset userresinfo = UserResInfoQry.getUserResByUserIdA(userId, ecUserId);
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            IData userres = userresinfo.getData(0);
            userattrinfo.put("SHORT_CODE", userres.getString("RES_CODE", ""));

        }
       
        IData userattritem = IDataUtil.iDataA2iDataB(userattrinfo, "ATTR_VALUE");

        IData mebUserInfoData = UcaInfoQry.qryUserInfoByUserId(userId);

        String userStateCodeSet = mebUserInfoData.getString("USER_STATE_CODESET", "");
        
        if (userStateCodeSet.equals("0") || userStateCodeSet.equals("N") || userStateCodeSet.equals("00"))
        {
            String netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");

            IData net = new DataMap();
            net.put("ATTR_VALUE", netTypeCode);
            userattritem.put("NET_TYPE_CODE", net);

        }
        transComboBoxValue(userattritem,attrItemA);
        
        IDataset defaultDiscntset =CommparaInfoQry.getOnlyByAttr("CGM", "8001", eparchyCode);
        if (IDataUtil.isEmpty(defaultDiscntset))
        {
            CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }
        String IMS_VPN_DISCNT_00 = "";
        String IMS_VPN_DISCNT_05 = "";
        for (int i = 0, size = defaultDiscntset.size(); i < size; i++)
        {
            IData defaultDiscnt = defaultDiscntset.getData(i);
            String discntinfo = defaultDiscnt.getString("PARA_CODE1", "");
            String nettypecode = defaultDiscnt.getString("PARAM_CODE", "");
            if ("00".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_00))
                    IMS_VPN_DISCNT_00 = discntinfo;
                else
                    IMS_VPN_DISCNT_00 = IMS_VPN_DISCNT_00 + "," + discntinfo;
            }
            else if ("05".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_05))
                    IMS_VPN_DISCNT_05 = discntinfo;
                else
                    IMS_VPN_DISCNT_05 = IMS_VPN_DISCNT_05 + "," + discntinfo;
            }
        }
        IData dis00 = new DataMap();
        dis00.put("ATTR_VALUE", IMS_VPN_DISCNT_00);
        userattritem.put("IMS_VPN_DISCNT_00", dis00);

        IData dis05 = new DataMap();
        dis05.put("ATTR_VALUE", IMS_VPN_DISCNT_05);
        userattritem.put("IMS_VPN_DISCNT_05", dis05);
        
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

