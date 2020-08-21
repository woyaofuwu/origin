
package com.asiainfo.veris.crm.order.web.group.vpmn;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class DelVpmnOutNet extends CSBasePage
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
        svcData.put("OUT_SHORT_CODE", condData.getString("OUT_SHORT_CODE"));
        svcData.put("REMARK", condData.getString("REMARK"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DelVpmnOutNetSVC.crtTrade", svcData);

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

        // 1. 判断网外号码是否属于VPMN集团
        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER_B", outSerialNumber);
        svcData.put("RELATION_TYPE_CODE", "41");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset relaDataset = RelationUUInfoIntfViewUtil.qryRelaUUInfosBySerialNumberBAndRelationTypeCode(this, outSerialNumber, "41", getTradeEparchyCode());
        if (IDataUtil.isEmpty(relaDataset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_8, outSerialNumber, serialNumber);
        }
        boolean isNotHas = true;
        for (int i = 0; i < relaDataset.size(); i++)
        {
            IData relaData = relaDataset.getData(i);
            if (serialNumber.equals(relaData.getString("SERIAL_NUMBER_A")))
            {
                isNotHas = false;
                break;
            }
        }
        if (isNotHas)
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
    public void qryVpmnInfo(IRequestCycle cycle) throws Exception
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
        if (!"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }
        if ("8050".equals(userData.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_16, serialNumber);
        }
        String custId = userData.getString("CUST_ID");

        // 查询客户信息
        IData custGrpData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_2, serialNumber);
        }

        // 查询UU关系
        IDataset relaList = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userData.getString("USER_ID"), "41");

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
        parentVpmnData.put("PSPT_ID", custGrpData.getString("PSPT_ID"));
        parentVpmnData.put("PSPT_TYPE", custGrpData.getString("PSPT_ID"));
        setParentVpmn(parentVpmnData);

        setCondition(getData());
    }

    public abstract void setCondition(IData condition);

    public abstract void setParentVpmn(IData parentVpmn);

}
