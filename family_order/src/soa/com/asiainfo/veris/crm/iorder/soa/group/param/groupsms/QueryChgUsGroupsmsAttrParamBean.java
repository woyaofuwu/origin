package com.asiainfo.veris.crm.iorder.soa.group.param.groupsms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class QueryChgUsGroupsmsAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryGroupsmsAttrForChgInit(IData param) throws Exception
    {
        
        IData result = new DataMap();
        /*String offerCode = param.getString("PRODUCT_ID");*/
        /*String custId = param.getString("CUST_ID");*/
        String userId = param.getString("USER_ID");
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("REMOVE_TAG", "0");
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset dataset = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserId", inparam);
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData userInfo = dataset.getData(0);
            IData managerPhone=new DataMap();
            IData info=new DataMap();
            IData linkPhone=new DataMap();
            managerPhone.put("DATA_VAL", userInfo.getString("RSRV_STR8", ""));
            info.put("DATA_VAL", userInfo.getString("RSRV_STR7", ""));
            linkPhone.put("DATA_VAL", userInfo.getString("RSRV_STR9", ""));
            result.put("DETMANAGERPHONE", managerPhone);
            result.put("DETMANAGERINFO", info);
            result.put("DETLINKPHONE", linkPhone);
        }
        
        return result;
       
    }
    
}
