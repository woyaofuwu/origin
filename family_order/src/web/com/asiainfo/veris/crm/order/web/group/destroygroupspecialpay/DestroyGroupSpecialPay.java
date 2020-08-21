
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

public abstract class DestroyGroupSpecialPay extends GroupBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData condData = new DataMap();
        condData.put("cond_QueryType", "1");

        setCondition(condData);
        setMessage("请输入查询条件~~");
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
        //add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
        svcData.put("MEB_VOUCHER_FILE_LIST", this.getData().getString("MEB_VOUCHER_FILE_LIST", ""));
        svcData.put("AUDIT_STAFF_ID", this.getData().getString("AUDIT_STAFF_ID", ""));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DestroyGroupSpecialPaySVC.crtBat", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 查询信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryInfoList(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String queryType = condData.getString("QueryType");

        IData svcData = new DataMap();
        String svcName = "";

        if ("0".equals(queryType)) // 按集团客户编码查询
        {
            IData grpCustData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, condData.getString("GROUP_ID"));

            svcData.put("CUST_ID", grpCustData.getString("CUST_ID"));
            svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

            svcName = "CS.PayRelaInfoQrySVC.qryGrpSpecialPayByCustId";
        }
        else if ("1".equals(queryType)) // 按成员号码查询
        {
            svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
            svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

            svcName = "CS.PayRelaInfoQrySVC.qryGrpSpecialPayBySerialNumber";
        }

        IDataOutput output = CSViewCall.callPage(this, svcName, svcData, getPagination("pageNavInfo"));

        // 设置返回值
        setInfoList(output.getData());
        setInfoCount(output.getDataCount());

        setCondition(getData());
        setMessage("查询结果~~");
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfoList(IDataset infoList);

    public abstract void setMessage(String message);
}
