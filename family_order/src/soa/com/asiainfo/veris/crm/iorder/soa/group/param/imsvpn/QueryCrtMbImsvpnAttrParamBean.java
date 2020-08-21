package com.asiainfo.veris.crm.iorder.soa.group.param.imsvpn;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;

public class QueryCrtMbImsvpnAttrParamBean extends QueryAttrParamBean
{
    public static IData queryImsvpnParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
       
        String mebUserId = param.getString("USER_ID");
        //String ecUserId = param.getString("EC_USER_ID");
        String custId = param.getString("CUST_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
        
        
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("8001", "P", "1", null);
       
        IData attrItemA = new DataMap();
        
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        // 2、查成员用户信息
        IData mebUserInfoData = UcaInfoQry.qryUserInfoByUserId(mebUserId);
        
        String netTypeCode ="";
        
        if(IDataUtil.isNotEmpty(mebUserInfoData)){
            
            netTypeCode = mebUserInfoData.getString("NET_TYPE_CODE");
        }

        String userType = "0";// =0 (SIP终端): IMS SIP-UE 用户
     
        IDataset impuInfos = UserImpuInfoQry.queryUserImpuInfo(mebUserId);
        
        if (IDataUtil.isNotEmpty(impuInfos))
        {
            IData datatmp = impuInfos.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }

        if (!"05".equals(netTypeCode))
        {
            userType = "4";// =4 : 传统移动用户
        }

     
        boolean crtFlag = GroupImsUtil.getCreateMebFlag(custId, mebUserId);// 判断成员是否已经订购其他ims产品，如果没有订购则返回true
        
        if (!crtFlag) // 如果短号已经存在，则获取impu表中的短号
        {
            String shortcode = GroupImsUtil.getImpuStr4(mebUserId, userType, 1);
            if (StringUtils.isNotBlank(shortcode))
            {
                IData res = new DataMap();
                res.put("ATTR_VALUE", shortcode);
                attrItemA.put("SHORT_CODE", res);
            }
        }

        IDataset defaultDiscntset = CommparaInfoQry.getOnlyByAttr("CGM", "8001", eparchyCode);
        if (IDataUtil.isNotEmpty(defaultDiscntset))
        {
          
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
            attrItemA.put("IMS_VPN_DISCNT_00", dis00);
            
            IData dis05 = new DataMap();
            dis05.put("ATTR_VALUE", IMS_VPN_DISCNT_05);
            attrItemA.put("IMS_VPN_DISCNT_05", dis05);
            
        }
        IData net = new DataMap();
        net.put("ATTR_VALUE", netTypeCode);
        attrItemA.put("NET_TYPE_CODE", net);

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
  
}
