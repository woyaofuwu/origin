
package com.asiainfo.veris.crm.order.web.group.vpmn;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class DelSubGrpVpmn extends GroupBasePage
{

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        // 业务受理前的校验
        onSubmitBaseTradeCheck(cycle);

        IData condData = getData();

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("PARENT_SERIAL_NUMBER", condData.getString("PARENT_SERIAL_NUMBER"));
        svcData.put("SERIAL_NUMBER", condData.getString("SUB_SERIAL_NUMBER"));
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DelSubGrpVpmnSVC.crtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);

    }

    /**
     * 业务提交前校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTradeCheck(IRequestCycle cycle) throws Exception
    {
        // 1. 未完工工单查询
        IData condData = getData();

        String parentSerialNumber = condData.getString("PARENT_SERIAL_NUMBER");

        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", parentSerialNumber);
        svcData.put("TRADE_TYPE_CODE", "3581");
        svcData.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

        IDataset tradeList = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeBySnEparchyCode", svcData);

        if (IDataUtil.isNotEmpty(tradeList))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_0, parentSerialNumber);
        }
    }

    /**
     * 子VPMN信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void qrySubVpmn(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("SERIAL_NUMBER");

        // VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);

        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_1, serialNumber);
        }

        // VPMN用户校验
        if (!"8000".equals(userData.getString("PRODUCT_ID")) || !"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_12, serialNumber);
        }

        String custId = userData.getString("CUST_ID");
        String userId = userData.getString("USER_ID");

        // 查询VPMN客户信息
        IData custGrpData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_2, serialNumber);
        }

        // 判断VPMN编码是否是子VPMN编码
        IData relaData = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfoByUserIdBAndRelationTypeCode(this, userId, "40", false);

        if (IDataUtil.isEmpty(relaData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_12, serialNumber);
        }
        // 查询母VPMN用户信息
        String parentSerialNumber = relaData.getString("SERIAL_NUMBER_A");

        IData parentUserData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, parentSerialNumber, false);

        if (IDataUtil.isEmpty(parentUserData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_1, parentSerialNumber);
        }

        // 查询母VPMN客户信息
        String parentCustId = parentUserData.getString("CUST_ID");

        IData parentCustData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, parentCustId, false);

        if (IDataUtil.isEmpty(parentCustData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_2, parentSerialNumber);
        }

        // 设置返回数据
        IData subVpmnData = new DataMap();
        subVpmnData.put("SUB_SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        subVpmnData.put("SUB_USER_ID", userData.getString("USER_ID"));
        subVpmnData.put("SUB_CUST_ID", userData.getString("CUST_ID"));
        subVpmnData.put("SUB_CUST_NAME", custGrpData.getString("CUST_NAME"));
        subVpmnData.put("SUB_PRODUCT_NAME", userData.getString("PRODUCT_NAME"));
        subVpmnData.put("SUB_GROUP_CONTACT_PHONE", custGrpData.getString("GROUP_CONTACT_PHONE"));

        IData parentVpmnData = new DataMap();
        parentVpmnData.put("PARENT_SERIAL_NUMBER", parentUserData.getString("SERIAL_NUMBER"));
        parentVpmnData.put("PARENT_USER_ID", parentUserData.getString("USER_ID"));
        parentVpmnData.put("PARENT_CUST_ID", parentUserData.getString("CUST_ID"));
        parentVpmnData.put("PARENT_CUST_NAME", parentCustData.getString("CUST_NAME"));
        parentVpmnData.put("PARENT_PRODUCT_NAME", parentUserData.getString("PRODUCT_NAME"));
        parentVpmnData.put("SUB_GROUP_CONTACT_PHONE", parentCustData.getString("GROUP_CONTACT_PHONE"));

        setSubVpmn(subVpmnData);
        setParentVpmn(parentVpmnData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setParentVpmn(IData parentVpmn);

    public abstract void setSubVpmn(IData subVpmn);

}
