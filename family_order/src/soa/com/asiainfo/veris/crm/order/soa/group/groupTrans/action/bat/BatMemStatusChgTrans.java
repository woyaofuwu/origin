
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatMemStatusChgTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("DEAL_FLAG", IDataUtil.getMandaData(condData, "DEAL_FLAG"));
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        svcData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
    }

}
