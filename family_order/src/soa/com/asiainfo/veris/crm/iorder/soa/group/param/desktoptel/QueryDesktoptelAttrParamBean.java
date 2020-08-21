package com.asiainfo.veris.crm.iorder.soa.group.param.desktoptel;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryDesktoptelAttrParamBean extends QueryAttrParamBean
{

    public static IData queryDesktoptelAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
        // String eparchyCode = param.getString("EPARCHY_CODE");

        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IData attrItemA = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        //IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfos = UserVpnInfoQry.getUserDesktopVPNInfoByCstId(custId, null);
        
        if (IDataUtil.isNotEmpty(uservpninfos))
        {
            IDataset vpnInfo = new DatasetList();
            IData vpnInfos = new DataMap();
            IData vpnInfoss = new DataMap();
            IData userInfo = uservpninfos.getData(0);
            vpnInfos.put("DATA_NAME", userInfo.getString("VPN_NO"));
            vpnInfos.put("DATA_ID", userInfo.getString("VPN_NO"));
            vpnInfo.add(vpnInfos);
            vpnInfoss.put("DATA_VAL", vpnInfo);
            vpnInfoss.put("ATTR_VALUE", userInfo.getString("VPN_NO"));
            result.put("VPN_NO", vpnInfoss);
        }
        if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
               
                String attrItemValue = attrCodeInfo.getString("ATTR_INIT_VALUE");
                attrItem.put("DATA_VAL", attrItemValue);
                result.put(key, attrItem);
            }
        }
       
        return result;

    }

}
