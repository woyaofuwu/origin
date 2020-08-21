package com.asiainfo.veris.crm.iorder.soa.group.param.yht;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class QueryCrtMbYhtAttrParamBean extends QueryAttrParamBean
{

    public static IData queryYhtParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        String mebUserId = param.getString("USER_ID");
//        String ecUserId = param.getString("EC_USER_ID");
//        String custId = param.getString("CUST_ID");
//        String eparchyCode = param.getString("EPARCHY_CODE");
        boolean ctFlag = false;
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("8016", "P", "1", null);
        
        IData attrItemA = new DataMap();

        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        attrItemA.put("IF_SHORT_CODE", "yes");
        
        // 获取成员手机号
        String memsn = ""; // 集团成员手机号
        IData mebUserInfoData = UcaInfoQry.qryUserInfoByUserId(mebUserId);
        memsn = mebUserInfoData.getString("SERIAL_NUMBER");
        attrItemA.put("METHOD_NAME", "CrtUs");
        attrItemA.put("MEMSN", memsn);
      
        // 判断是否可以拨打国内长途 14
        boolean outDataset = RouteInfoQry.isChinaMobile(memsn);
       
        if (outDataset)
        {
            IDataset ds = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(mebUserId, "14");

            if (IDataUtil.isNotEmpty(ds))
            {
                ctFlag = true;
            }
        }
        else
        { // 为固定电话时只产生了虚拟三户资料，是没有14服务，暂时不判断
            ctFlag = true;
        }

        
        attrItemA.put("PRODUCT_ID", "8016");
        
        if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                String attrCodeInfo = attrItemA.getString(key);
                IData attrItem = new DataMap();
               
                    attrItem.put("ATTR_VALUE", attrCodeInfo);
                    result.put(key, attrItem);
                }
           
        }
        
    
        return result;
       
    }
    
    

}
