package com.asiainfo.veris.crm.iorder.soa.group.param.imsvpn;

import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryImsvpnAttrParamBean extends QueryAttrParamBean
{

    public static IData queryImsvpnParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");

        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IData attrItemA = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
     // 调用后台服务，查vpn信息
        //IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfos = UserVpnInfoQry.getUserDesktopVPNInfoByCstId(custId, null);
        IDataset data = DataHelper.distinct(uservpninfos, "VPN_NO", "");
        if (IDataUtil.isNotEmpty(data))
        {
            IDataset vpnInfo = new DatasetList();
            IData vpnInfoss = new DataMap();
            for (Object object : data)
            {
                IData vpnInfos = new DataMap();
                IData userInfo = (IData) object;
                vpnInfos.put("DATA_NAME", userInfo.getString("VPN_NO"));
                vpnInfos.put("DATA_ID", userInfo.getString("VPN_NO"));
                vpnInfo.add(vpnInfos);
            }
            vpnInfoss.put("DATA_VAL", vpnInfo);
            result.put("VPN_NO", vpnInfoss);
        }
        
        //
        IData vpnInfos = new DataMap();
        vpnInfos.put("DATA_VAL", data);
        result.put("VPN_INFOS", vpnInfos);

        
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
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                }
                
            }
        }
      
        return result;

    }

}
