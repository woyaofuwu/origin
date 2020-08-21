
package com.asiainfo.veris.crm.order.web.group.grpactpaymgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class AcctPayOrderMgr extends CSBasePage
{

    /**
     * @description 进行排序操作
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData params = getData();
        String strAcctInfo = params.getString("pam_relay", "");
        IDataset acctInfoSet = new DatasetList(strAcctInfo);

        IData inData = new DataMap();
        inData.put("DATA_SET", acctInfoSet);
        inData.put("RELATION_TYPE_CODE", "98");
        inData.put("STATE", "MODI");
        inData.put("TRADE_TYPE_CODE", "4610");// 代付
        String eparchyCode = getTradeEparchyCode();
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("USER_EPARCHY_CODE", eparchyCode);
        inData.put("CUST_ID", params.getString("CUST_ID"));
        inData.put("ACCT_ID", acctInfoSet.getData(0).getString("ACCT_ID_A"));// 支付账户
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.PayrelaAdvChgSVC.createRelaTrade", inData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * @description:根据手机号码查询其默认付费账户信息
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public IDataset getAccountInfoBySN(String serialNumber) throws Exception
    {
        // 一、查询用户信息
        IData userInfo = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serialNumber, false);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_394);
        }

        IData payParam = new DataMap();
        payParam.put("USER_ID", userInfo.getString("USER_ID"));
        payParam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        // 二、查询默认付费账户
        IData defPayRelaInfos = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryDefaultPayRelaByUserId", payParam);
        if (null == defPayRelaInfos)
        {
            defPayRelaInfos = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryLastPayRelaByUserId", payParam);
            if (defPayRelaInfos.size() == 0)
            {
                CSViewException.apperr(GrpException.CRM_GRP_117);
            }
        }
        // 三、查询账户信息
        String acctId = defPayRelaInfos.getString("ACCT_ID");
        IData acctParam = new DataMap();
        acctParam.put("ACCT_ID", acctId);
        acctParam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        IDataset acctInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryAcctInfoByAcctId", acctParam);
        return acctInfos;
    }

    /**
     * @description 通过group_id来查询账户信息
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public void getAcctInfoByGrpId(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData();
        // 一、查询用户信息
        IData param = new DataMap();
        param.put("CUST_ID", initdata.getString("CUST_ID", ""));
        IDataset acctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByCustIDForGrp", param);
        setAcctInfos(acctInfos);
    }

    /**
     * @description 通过手机号码获取账户信息
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public void getAcctInfoBySn(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData("cond", true);
        String serialNumber = initdata.getString("SERIAL_NUMBER", "");
        IDataset acctInfos = getAccountInfoBySN(serialNumber);
        setAcctInfos(acctInfos);
    }

    /**
     * @description 获取AA关系，只查询代付关系：relation_type_code = 98
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public void getRelationAA(IRequestCycle cycle) throws Exception
    {
        IData params = getData("cond", true);

        IData inparams = new DataMap();
        String userType = params.getString("QUERY_TYPE", "");
        inparams.put("ACCT_ID_A", params.getString("ACCT_ID_A", ""));
        inparams.put("RELATION_TYPE_CODE", "98");// 代付关系
        inparams.put("ACT_TAG", "1");// 有效
        IDataset relationAAs = new DatasetList();
        // 如果是个人用户，查询地州库
        if ("1".equals(userType))
        {
            inparams.put(Route.ROUTE_EPARCHY_CODE, params.getString("EPARCHY_CODE"));
            relationAAs = CSViewCall.call(this, "CS.AcctInfoQrySVC.getRelAAByActIdAReltypecode", inparams);
        }
        else
        {// 集团用户查询cg库
            relationAAs = CSViewCall.call(this, "CS.AcctInfoQrySVC.getRelationAAByActIdATagForCg", inparams);
        }
        setPayInfos(relationAAs);

        // 绑定变更前数据
        IData relaypamData = new DataMap();
        relaypamData.put("pam_relay", relationAAs);
        relaypamData.put("pam_oldrelay", relationAAs);// 保存优先级修改前数据
        setRelaypam(relaypamData);
    }

    /**
     * @description 初始化页面信息
     * @author liuzz
     * @date 2014-04-02
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData("cond", true);
        initdata.put("cond_QUERY_TYPE", "1");// 默认为集团用户
        setCondition(initdata);
    }

    public abstract void setAcctInfo(IData info);

    public abstract void setAcctInfos(IDataset infos);

    public abstract void setCondition(IData condition);

    public abstract void setPayInfos(IDataset infos);

    public abstract void setRelaypam(IData relaypam);
}
