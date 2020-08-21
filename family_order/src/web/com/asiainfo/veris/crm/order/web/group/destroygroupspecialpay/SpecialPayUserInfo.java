
package com.asiainfo.veris.crm.order.web.group.destroygroupspecialpay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SpecialPayUserInfo extends GroupBasePage
{

    /**
     * 查询统付产品成员信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryProductUserInfo(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String groupId = condData.getString("GROUP_ID");

        IData grpCustData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        IData svcData = new DataMap();

        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("CUST_ID", grpCustData.getString("CUST_ID"));
        svcData.put("ACCT_ID", condData.getString("ACCT_ID"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataOutput output = CSViewCall.callPage(this, "CS.PayRelaInfoQrySVC.qryGrpSpecialPayByCustIdAcctIdProductId", svcData, getPagination(""));

        setInfoList(output.getData());

        setInfoCount(output.getDataCount());

        setGroupInfo(grpCustData);

        setCondition(condData);
    }

    /**
     * 注销统一付费关系
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        String checkValue = getData().getString("CHECKVALUE_LIST");
        String serialNumber = getData().getString("SERIAL_NUMBER");
        String actionFlag = getData().getString("ACTION_FLAG");
        String smsFlag = getData().getString("SMS_FLAG");
        // 服务数据
        IData svcData = new DataMap();
        svcData.put("CHECKVALUE_LIST", checkValue);
        svcData.put("ACTION_FLAG", actionFlag);
        svcData.put("SMS_FLAG", smsFlag);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DestroyGroupSpecialPaySVC.crtBat", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    public abstract void setMessage(String message);

    public abstract void setInfoCount(long infoCount);

    public abstract void setCondition(IData condData);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfoList(IDataset productList);
}
