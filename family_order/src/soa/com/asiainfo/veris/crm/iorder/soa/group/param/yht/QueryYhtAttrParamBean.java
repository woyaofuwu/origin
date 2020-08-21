package com.asiainfo.veris.crm.iorder.soa.group.param.yht;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryYhtAttrParamBean extends QueryAttrParamBean
{

    public static IData queryYhtParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        // String eparchyCode = param.getString("EPARCHY_CODE");
        String custId = param.getString("CUST_ID");
      
        IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null); 
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
        return result;

    }

}
