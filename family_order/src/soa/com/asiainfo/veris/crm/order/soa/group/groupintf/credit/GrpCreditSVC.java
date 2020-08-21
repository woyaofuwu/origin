
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class GrpCreditSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 集团用户信控暂停恢复成员处理接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset creatCreditStopTask(IData inParam) throws Exception
    {
        IDataset result = new DatasetList();
        IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("DEAL_FLAG", inParam.getString("DEAL_FLAG"));
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        result = CSAppCall.call("SS.MemberStateChgSVC.crtBat", param);
        return result;
    }

    /**
     * 集团用户一键注销（信控）接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset creatDestoryTask(IData inParam) throws Exception
    {
        IDataset result = new DatasetList();
        IData param = new DataMap();
        String userId = inParam.getString("USER_ID");
        // 查询集团用户信息
        IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }

        // 查询集团客户信息
        IData custInfo = UcaInfoQry.qryGrpInfoByCustId(userinfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_35);
        }

        param.put("GROUP_ID", custInfo.getString("GROUP_ID"));
        param.put("PRODUCT_ID", userinfo.getString("PRODUCT_ID"));
        param.put("USER_ID", userId);
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        result = CSAppCall.call("SS.DestroyOneKeySVC.crtBat", param);
        return result;
    }

    /**
     * 代付关系批量暂停恢复（信控）接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset payRelationChgTask(IData inParam) throws Exception
    {
        IDataset result = new DatasetList();
        IData param = new DataMap();
        param.putAll(inParam);
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("TRADE_TYPE_CODE", inParam.getString("TRADE_TYPE_CODE"));
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        result = CSAppCall.call("SS.MebPayrelationChgSVC.crtBat", param);
        return result;
    }

    /**
     * 集团用户BBoss状态变更信控接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset bbossCreditReg(IData inParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("STATE_CODE", inParam.getString("STATE_CODE"));
        param.put("PRODUCT_ID", inParam.getString("PRODUCT_ID"));
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        param.put("BRAND_CODE", inParam.getString("BRAND_CODE"));

        BBossUserStatusChangIntf bean = new BBossUserStatusChangIntf();

        return bean.bbossCreditReg(param);
    }

    /**
     * 集团成员注销（供个人使用）
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset destroyByPerson(IData data) throws Exception
    {

        GrpCreditBaseBean bean = new GrpCreditBaseBean();
        return bean.destroybyperson(data);

    }

    /**
     * BBOSS成员暂停恢复
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset bbossMemberStopOrBack(IData data) throws Exception
    {

        GrpCreditBaseBean bean = new GrpCreditBaseBean();
        return bean.bbossMemberStopOrBack(data);

    }

    /**
     * 集团成员注销
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset destroyGrpMemInfo(IData data) throws Exception
    {

        GrpCreditBaseBean bean = new GrpCreditBaseBean();
        return bean.destroyGrpMemInfo(data);
    }

    /**
     * 对TradeImpu成员资料的处理
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset CreateGrpTradeImpu(IData data) throws Exception
    {

        GrpCreditBaseBean bean = new GrpCreditBaseBean();
        return bean.CreateGrpTradeImpu(data);
    }

    /**
     * 处理集团信控
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset grpCreditTradeReg(IData data) throws Exception
    {

        CreditGrpTradeReg bean = new CreditGrpTradeReg();
        return bean.grpCreditTradeReg(data);
    }
}
