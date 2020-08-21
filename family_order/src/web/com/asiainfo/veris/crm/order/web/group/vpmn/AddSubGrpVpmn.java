
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

public abstract class AddSubGrpVpmn extends GroupBasePage
{

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        // 业务提交前校验
        onSubmitBaseTradeCheck(cycle);

        //子母集团的短号校验
        isExistSameShortNumber(cycle);
        
        IData condData = getData();

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("PARENT_SERIAL_NUMBER", condData.getString("PARENT_SERIAL_NUMBER"));
        svcData.put("SERIAL_NUMBER", condData.getString("SUB_SERIAL_NUMBER"));
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.AddSubGrpVpmnSVC.crtTrade", svcData);

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

        String serialNumber = condData.getString("PARENT_SERIAL_NUMBER");

        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("TRADE_TYPE_CODE", "3580");
        svcData.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

        IDataset tradeList = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeBySnEparchyCode", svcData);

        if (IDataUtil.isNotEmpty(tradeList))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_0, serialNumber);
        }
    }

    /**
     * 子母集团的短号校验
     * @param cycle
     * @throws Exception
     */
    private void isExistSameShortNumber(IRequestCycle cycle) throws Exception {
             
        IData condData = getData();
        
        //校验新增子集团与母集团下子集团的成员短号码 
        IData param = new DataMap();
        param.put("USER_ID_B", condData.get("SUB_USER_ID"));
        param.put("USER_ID_A", condData.getString("PARENT_USER_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

        IDataset shortList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getSubShortCodeExistByUserIdAB", param);
        if(IDataUtil.isNotEmpty(shortList)){
            CSViewException.apperr(VpmnUserException.VPMN_USER_224);
        }
        
        //校验新增子集团与母集团的成员短号码
        IData inparam = new DataMap();
        inparam.put("USER_ID_B", condData.get("SUB_USER_ID"));
        inparam.put("USER_ID_A", condData.getString("PARENT_USER_ID"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

        IDataset shortParentList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getParentShortCodeExistByUserIdAB", inparam);
        if(IDataUtil.isNotEmpty(shortParentList)){
            CSViewException.apperr(VpmnUserException.VPMN_USER_225);
        }
        
    }
    
    /**
     * 查询母VPMN信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryParentVpmn(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("SERIAL_NUMBER");

        // 查询母VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);

        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_1, serialNumber);
        }

        // 母VPMN用户校验
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

        // 设置返回值
        IData parentVpmnData = new DataMap();
        parentVpmnData.put("PARENT_USER_ID", userData.getString("USER_ID"));
        parentVpmnData.put("PARENT_SERIAL_NUMBER", serialNumber);
        parentVpmnData.put("PARENT_CUST_ID", custGrpData.getString("CUST_ID"));
        parentVpmnData.put("PARENT_CUST_NAME", custGrpData.getString("CUST_NAME"));
        parentVpmnData.put("PARENT_PRODUCT_NAME", userData.getString("PRODUCT_NAME"));
        parentVpmnData.put("PARENT_GROUP_CONTACT_PHONE", custGrpData.getString("GROUP_CONTACT_PHONE"));
        setParentVpmn(parentVpmnData);

        setCondition(getData());
    }

    /**
     * 查询子VPMN信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qrySubVpmn(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        String parentSerialNumber = condData.getString("PARENT_SERIAL_NUMBER");
        String subSerialNumber = condData.getString("SUB_SERIAL_NUMBER");

        // 查询子VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, subSerialNumber, false);

        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_1, subSerialNumber);
        }

        // 子VPMN用户信息校验
        if (!"8000".equals(userData.getString("PRODUCT_ID")) || !"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_12, subSerialNumber);
        }

        // 查询子VPMN客户信息
        String custId = userData.getString("CUST_ID");
        String userId = userData.getString("USER_ID");

        IData custGrpData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_2, subSerialNumber);
        }

        // 判断子VPMN是否已经是母VPMN的子VPMN
        IData svcData = new DataMap();
        svcData.put("USER_ID_B", userId);
        svcData.put("RELATION_TYPE_CODE", "40");
        IData relaData = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfoByUserIdBAndRelationTypeCode(this, userId, "40", false);

        if (IDataUtil.isNotEmpty(relaData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_13, subSerialNumber, relaData.getString("SERIAL_NUMBER_A"));
        }

        // 判断子网下面是否存在802服务
        IDataset userSvcList = qryUserSvcList(userId, "802");

        // 如果子VPMN存在802服务, 则判断母VPMN下面的的子VPMN用户是否已经订购802服务
        if (IDataUtil.isNotEmpty(userSvcList))
        {
            // 查询母VPMN用户信息
            IData parentUserData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, parentSerialNumber, false);

            if (IDataUtil.isEmpty(parentUserData))
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_1, parentSerialNumber);
            }

            // 查询母VPMN下面的子VPMN用户信息
            IDataset subRelaList = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, parentUserData.getString("USER_ID"), "40");

            if (IDataUtil.isNotEmpty(subRelaList))
            {
                for (int i = 0, row = subRelaList.size(); i < row; i++)
                {
                    IData subRelaData = subRelaList.getData(i);

                    IDataset subUserSvcList = qryUserSvcList(subRelaData.getString("USER_ID_B", ""), "802");

                    if (IDataUtil.isEmpty(subUserSvcList))
                    {
                        CSViewException.apperr(VpmnUserException.VPMN_USER_14, subRelaData.getString("SERIAL_NUMBER_B"));
                        break;
                    }
                }
            }
        }

        // 设置子VPMN用户信息
        IData subVpmnData = new DataMap();
        subVpmnData.put("SUB_SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        subVpmnData.put("SUB_USER_ID", userData.getString("USER_ID"));
        subVpmnData.put("SUB_CUST_ID", userData.getString("CUST_ID"));
        subVpmnData.put("SUB_CUST_NAME", custGrpData.getString("CUST_NAME"));
        subVpmnData.put("SUB_PRODUCT_NAME", userData.getString("PRODUCT_NAME"));
        subVpmnData.put("SUB_GROUP_CONTACT_PHONE", userData.getString("GROUP_CONTACT_PHONE"));

        setSubVpmn(subVpmnData);
    }

    /**
     * 查询用户服务信息
     * 
     * @param userId
     *            用户id
     * @param serviceId
     *            服务id
     * @return
     * @throws Exception
     */
    public IDataset qryUserSvcList(String userId, String serviceId) throws Exception
    {
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put("SERVICE_ID", "802");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset userSvcList = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId", svcData);

        return userSvcList;
    }

    public abstract void setCondition(IData condition);

    public abstract void setParentVpmn(IData parentVpmn);

    public abstract void setSubVpmn(IData subVpmn);

}
