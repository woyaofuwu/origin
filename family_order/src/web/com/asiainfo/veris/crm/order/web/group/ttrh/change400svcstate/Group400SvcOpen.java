
package com.asiainfo.veris.crm.order.web.group.ttrh.change400svcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class Group400SvcOpen extends GroupBasePage
{
    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setConsign(IData consign);

    public abstract void setConditions(IDataset condition);

    public abstract void setCondition(IData condition);

    public abstract void setOpera(IData opera);

    public void initial(IRequestCycle cycle) throws Exception
    {
        String product_Id = getParameter("PRODUCT_ID");
        String menu_userStateCodeset = getParameter("USER_STATE_CODESET");// 看是报停还是报开
        IDataset result = null;

        if (StringUtils.isNotEmpty(product_Id))
        {// 如果没有得到产品参数则显示所有产品
            result = new DatasetList();
            IData condition = new DataMap();
            condition.put("DATA_ID", product_Id);
            String dataName = StaticUtil.getStaticValue("TTRH_PRODUCT_TYPE", product_Id);
            condition.put("DATA_NAME", dataName);
            condition.put("USER_STATE_CODESET", menu_userStateCodeset);
            result.add(condition);

            setCondition(condition);
        }
        else
        {// 菜单必选配置传productId
            // result = StaticUtil.getStaticList("TTRH_PRODUCT_TYPE");
            CSViewException.apperr(GrpException.CRM_GRP_664);
        }
        setConditions(result);
    }

    /**
     * 根据GROUPID查询集团专网产品开户相关信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getZXInfoByGroupId(IRequestCycle cycle) throws Throwable
    {
        String groupId = this.getParameter("GROUP_ID");
        String product_Id = this.getParameter("PRODUCT_ID");

        if (StringUtils.isNotEmpty(product_Id))
            productId = product_Id;

        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId, false);

        if (IDataUtil.isEmpty(result)) // 未找到对应的集团
            CSViewException.apperr(GrpException.CRM_GRP_472, groupId);

        if (!"1".equals(result.getString("RSRV_NUM3")))
            CSViewException.apperr(GrpException.CRM_GRP_663, groupId);

        String custId = result.getString("CUST_ID");

        if (StringUtils.isEmpty(custId))
            return;

        IData params = new DataMap();
        params.put("CUST_ID", custId);

        params.put("PRODUCT_ID", product_Id);

        IDataset zxproducts = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, product_Id, false);
        IData resultdata = new DataMap();

        if (IDataUtil.isEmpty(zxproducts))
        {
            resultdata.put("RESULT_CODE", "0");
            resultdata.put("PRODUCT_ID", product_Id);
        }
        if (zxproducts.size() == 1)
        {
            String userId = zxproducts.getData(0).getString("USER_ID", "");
            resultdata.put("RESULT_CODE", "0");
            resultdata.put("RESULT_NUM", zxproducts.size());
            resultdata.put("GROUP_ID", groupId);
            resultdata.put("PRODUCT_ID", product_Id);
            resultdata.put("USER_ID", userId);
        }
        else
        {
            resultdata.put("RESULT_CODE", "0");
            resultdata.put("RESULT_NUM", zxproducts.size());
            resultdata.put("PRODUCT_ID", product_Id);
            resultdata.put("CUST_ID", custId);
        }

        this.setAjax(resultdata);
    }

    public void getZXInfoBySerialNumber(IRequestCycle cycle) throws Exception
    {
        String productId = getParameter("PRODUCT_ID", "");

        String sn = getParameter("SERIAL_NUMBER", "");
        if (StringUtils.isEmpty(sn))
            CSViewException.apperr(CrmUserException.CRM_USER_115);

        IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, sn);
        if (IDataUtil.isEmpty(userinfo))
            CSViewException.apperr(CrmUserException.CRM_USER_554);
        if (!productId.equals(userinfo.getString("PRODUCT_ID")))
            CSViewException.apperr(CrmUserException.CRM_USER_1085);

        String custId = userinfo.getString("CUST_ID", "");

        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);

        setGroupInfo(result);

        queryGroupNetInfo(cycle, userinfo);
    }

    public void getZXInfoByUserId(IRequestCycle cycle) throws Exception
    {
        String productId = getParameter("PRODUCT_ID", "");
        String userid = getParameter("USER_ID", "");
        if (StringUtils.isEmpty(userid))
            CSViewException.apperr(CrmUserException.CRM_USER_583);

        IData params = new DataMap();

        params.put("USER_ID", userid);

        IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userid);

        if (IDataUtil.isEmpty(userinfo))
            CSViewException.apperr(CrmUserException.CRM_USER_554);

        if (!productId.equals(userinfo.getString("PRODUCT_ID")))
            CSViewException.apperr(CrmUserException.CRM_USER_1085);

        String custId = userinfo.getString("CUST_ID", "");

        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);

        setGroupInfo(result);

        queryGroupNetInfo(cycle, userinfo);

    }

    /**
     * 根据USER_ID查询集团专网产品开户相关信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryGroupNetInfo(IRequestCycle cycle, IData userInfo) throws Exception
    {
        String userStateCodeset = getParameter("USER_STATE_CODESET");// 正常
        IData param = new DataMap();

        String userstatecode = userInfo.getString("USER_STATE_CODESET", "");
        String state_name = UpcViewCall.queryStateNameBySvcId(this, "0", userstatecode);
        if ("0".equals(userStateCodeset) && "0".equals(userstatecode))// 正常用户不能在报开页面办理
            CSViewException.apperr(CrmUserException.CRM_USER_137, state_name);

        if (StringUtils.isBlank(userStateCodeset) && !"0".equals(userstatecode))// 销户用户不能在报停页面办理
            CSViewException.apperr(CrmUserException.CRM_USER_137, state_name);

        IData Info = new DataMap();

        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("INST_TYPE", "P");
        /*
         * IDataset userattrset = dao.queryListByCodeCodeParser("TF_F_USER_ATTR", "SEL_BY_P_USERID_TYPE_CODE", param);
         * IData userattrinfo = GroupUtil.hTable2STable(userattrset,"ATTR_CODE","ATTR_VALUE","ATTR_VALUE");
         * Info.putAll(userattrinfo);
         */

        IData acctInfo = UCAInfoIntfViewUtil.qryGrpDefAcctInfoByUserId(this, userInfo.getString("USER_ID"));

        Info.put("PAY_NAME", acctInfo.getString("PAY_NAME", ""));
        String pay_mode = StaticUtil.getStaticValue(getVisit(), "TD_S_PAYMODE", "PAY_MODE_CODE", "PAY_MODE", acctInfo.getString("PAY_MODE_CODE", ""));
        Info.put("PAY_MODE", pay_mode);
        String bank = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", acctInfo.getString("BANK_CODE", ""));
        Info.put("BANK", bank);
        Info.put("BANK_ACCT_NO", acctInfo.getString("BANK_ACCT_NO", ""));

        setConsign(Info);

        // 填充操作类型,报停还是报开
        IData opera = new DataMap();
        opera.put("USER_ID", userInfo.getString("USER_ID"));
        opera.put("OPERA_ID", userstatecode);
        opera.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        this.setAjax(opera);
    }

    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {

        IData idata = getData();

        IData data = new DataMap();
        String operaType = idata.getString("cond_OPERA_TYPE");
        String userStateCodeset = "";// 用户状态
        String stateCode = "";// 服务状态
        if ("1".equals(operaType))
        {// 报开
            stateCode = "1";
            userStateCodeset = "0";
        }
        else if ("0".equals(operaType))
        {// 报停
            stateCode = "0";
            userStateCodeset = "1";
        }
        String userId = idata.getString("USER_ID");
        if (StringUtils.isEmpty(userId))
            CSViewException.apperr(CrmUserException.CRM_USER_583);

        data.put("USER_ID", userId);
        data.put("USER_STATE_CODESET", userStateCodeset);
        data.put("STATE_CODE", stateCode);
        data.put("PRODUCT_ID", idata.getString("cond_PRODUCT_ID"));
        data.put(Route.USER_EPARCHY_CODE, idata.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.ChangeTTRHSvcElementSVC.crtTrade", data);

        setAjax(dataset);
    }

}
