
package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeMfcStateSVC extends GroupOrderService
{

    private static final long serialVersionUID = 6337637316099786690L;

    public IDataset tradeReg(IData param) throws Exception
    {
    	IDataset ret=new DatasetList();
    	IData returnInfo=new DataMap();
    	returnInfo.put("RSP_CODE", "00");
    	returnInfo.put("RSP_DESC", "操作成功！");
    	IDataUtil.chkParam(param, Route.ROUTE_EPARCHY_CODE);
    	IDataUtil.chkParam(param, Route.USER_EPARCHY_CODE);
    	IDataUtil.chkParam(param, "TRADE_TYPE_CODE");
    	param.put("USER_ID", IDataUtil.chkParam(param, "USER_ID_A"));
    	param.put("SERIAL_NUMBER", IDataUtil.chkParam(param, "SERIAL_NUMBER_A"));  
    	ChangeMfcStateBean bean = new ChangeMfcStateBean();      
        bean.crtTrade(param);
        ret.add(returnInfo);
        return ret;
    }

}
