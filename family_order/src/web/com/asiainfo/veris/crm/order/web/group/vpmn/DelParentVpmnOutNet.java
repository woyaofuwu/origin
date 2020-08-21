
package com.asiainfo.veris.crm.order.web.group.vpmn;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class DelParentVpmnOutNet extends CSBasePage
{
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        // 业务受理前校验
        onSubmitBaseTradeCheck(cycle);

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put("OUT_SERIAL_NUMBER", condData.getString("OUT_SERIAL_NUMBER"));
        svcData.put("REMARK", condData.getString("REMARK"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DelParentVpmnOutNetSVC.crtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 业务受理前校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTradeCheck(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        String serialNumber = condData.getString("SERIAL_NUMBER");
        String outSerialNumber = condData.getString("OUT_SERIAL_NUMBER");

        // 1. 未完工工单限制
        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("TRADE_TYPE_CODE", "3583");
        svcData.put(Route.ROUTE_EPARCHY_CODE, Route.getJourDb(getTradeEparchyCode()));

        IDataset tradeList = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeBySnEparchyCode", svcData);

        if (IDataUtil.isNotEmpty(tradeList))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_0, serialNumber);
        }

        // 1.5. 网外号码验证
        if (outSerialNumber.length() < 5 || outSerialNumber.length() > 15)
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_52);
        }

        // 2. 判断网外号码是否属于VPMN集团

        IDataset relaDataset = RelationUUInfoIntfViewUtil.qryRelaUUInfosBySerialNumberBAndRelationTypeCode(this, outSerialNumber, "41", getTradeEparchyCode());
        if (IDataUtil.isEmpty(relaDataset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_8, outSerialNumber, serialNumber);
        }
        IData relaData = relaDataset.getData(0);
        if (!serialNumber.equals(relaData.getString("SERIAL_NUMBER_A")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_8, outSerialNumber, serialNumber);
        }

    }

    /**
     * 查询VPMN用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryParentVpmn(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("SERIAL_NUMBER");

        // 查询用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);

        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_1, serialNumber);
        }

        // VPMN用户校验
        if (!"8050".equals(userData.getString("PRODUCT_ID")) || !"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_3, serialNumber);
        }

        String custId = userData.getString("CUST_ID");

        // 查询客户信息
        IData custGrpData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_2, serialNumber);
        }

        // 查询客户核心资料信息
        IData customerData = UCAInfoIntfViewUtil.qryGrpCustomerInfoByCustId(this, custId);

        // 查询UU关系
        IDataset relaList = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, userData.getString("USER_ID"), "41");

        if (IDataUtil.isEmpty(relaList))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_4, serialNumber);
        }

        // 设置返回值
        IData parentVpmnData = new DataMap();
        parentVpmnData.put("USER_ID", userData.getString("USER_ID"));
        parentVpmnData.put("SERIAL_NUMBER", serialNumber);
        parentVpmnData.put("CUST_NAME", custGrpData.getString("CUST_NAME"));
        parentVpmnData.put("GROUP_CONTACT_PHONE", custGrpData.getString("GROUP_CONTACT_PHONE"));
        parentVpmnData.put("GROUP_ADDR", custGrpData.getString("GROUP_ADDR"));
        parentVpmnData.put("JURISTIC_NAME", custGrpData.getString("JURISTIC_NAME"));
        parentVpmnData.put("POST_CODE", custGrpData.getString("POST_CODE"));
        parentVpmnData.put("PSPT_ID", customerData.getString("PSPT_ID"));
        parentVpmnData.put("PSPT_TYPE_NAME", customerData.getString("PSPT_TYPE_NAME"));
        setParentVpmn(parentVpmnData);

        setCondition(getData());
    }

    public abstract void setCondition(IData condition);

    public abstract void setParentVpmn(IData parentVpmn);

}
