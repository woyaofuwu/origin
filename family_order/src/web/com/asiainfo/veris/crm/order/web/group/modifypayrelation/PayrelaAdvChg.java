
package com.asiainfo.veris.crm.order.web.group.modifypayrelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class PayrelaAdvChg extends GroupBasePage
{

    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData idata = new DataMap();
        IData data = getData();
        IData acctInfo = getData("acctInfo"); // 集团
        IData memAcctInfo = getData("acctInfoDesc");
        String sn = getData("cond").getString("SERIAL_NUMBER");

        idata.put("SERIAL_NUMBER", sn);

        idata.put("USER_ID", acctInfo.getString("USER_ID"));

        idata.put("START_CYCLE_ID", data.getString("newSnInfo_START_CYCLE_ID"));
        idata.put("END_CYCLE_ID", data.getString("newSnInfo_END_CYCLE_ID"));

        idata.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
        idata.put("LIMIT", StringUtils.isEmpty(data.getString("LIMIT")) ? 0 : data.getString("LIMIT"));
        
        /**
         * 20160526
         *   删除页面上的"是否补足"
         *   是否补足 默认为0（不补足）
         */
        idata.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));
        //idata.put("COMPLEMENT_TAG", "0");
        
        idata.put("FEE_TYPE", data.getString("FEE_TYPE"));
        // 账目编码
        idata.put("PAYITEM_CODE", data.getString("selectItemcodes"));
        idata.put("newSnInfo_CheckAll", data.getString("newSnInfo_CheckAll"));

        idata.put("crmSmsOrder", data.getString("crmSmsOrder"));// 订购短信提醒
        idata.put("acctSmsOrder", data.getString("acctSmsOrder"));// 订购短信提醒
        
        //add by chenzg@2018075--REQ201804280001集团合同管理界面优化需求--凭证上传附件
        idata.put("MEB_VOUCHER_FILE_LIST", data.getString("MEB_VOUCHER_FILE_LIST", ""));
        idata.put("AUDIT_STAFF_ID", data.getString("AUDIT_STAFF_ID", ""));

        IDataset dataset = CSViewCall.call(this, "SS.PayrelaAdvChgSVC.crtTrade", idata);

        setAjax(dataset);
    }

    /**
     * 过滤综合帐目
     * 
     * @param cycle
     * @throws Exception
     */
    public void filterNoteItems(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", getParameter("NOTE_ITEM"));

        IDataset iDataset = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.filterNoteItems", data);

        setNoteItemList(iDataset);
    }

    // 查询成员账户信息
    public void getAdvChgNewSnInfo(IRequestCycle cycle) throws Exception
    {
        String memSn = this.getParameter("MEM_SERIAL_NUMBER");
        // 查询成员用户信息是否存在
        IData memInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, memSn, true, false); //改成由规则判断用户状态

        IData relaParam = new DataMap();
        relaParam.put("USER_ID_B", memInfo.getString("USER_ID", ""));
        relaParam.put("RELATION_TYPE_CODE", "56");
        relaParam.put("ROLE_CODE_B", "2");

        relaParam.put(Route.ROUTE_EPARCHY_CODE, memInfo.getString("EPARCHY_CODE"));
        IDataset relaDatas = CSViewCall.call(this, "CS.RelationUUQrySVC.getRelaFKByUserIdB", relaParam);

        if (IDataUtil.isNotEmpty(relaDatas))
        {
            CSViewException.apperr(UUException.CRM_UU_101, memSn);
        }

        // 获取成员用户三户信息
        IData ucaData = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this, memSn, true, false);

        IData idCustInfo = ucaData.getData("MEB_CUST_INFO");
        IData idUserInfo = ucaData.getData("MEB_USER_INFO");
        IData idAcctInfo = ucaData.getData("MEB_ACCT_INFO");
        IData idAcctDayInfo = ucaData.getData("MEB_ACCTDAY_INFO");

        IData condition = new DataMap();

        condition.put("USER_ID", idUserInfo.getString("USER_ID"));
        condition.put("CUST_NAME", idCustInfo.getString("CUST_NAME"));
        condition.put("EPARCHY_CODE", idUserInfo.get("EPARCHY_CODE"));
        condition.put("ACCT_ID", idAcctInfo.get("ACCT_ID"));
        condition.put("PAY_NAME", idAcctInfo.get("PAY_NAME"));
        condition.put("PAY_MODE_CODE", idAcctInfo.get("PAY_MODE_CODE"));
        condition.put("SUPER_BANK_CODE", idAcctInfo.get("SUPER_BANK_CODE"));
        condition.put("BANK_CODE", idAcctInfo.get("BANK_CODE"));
        condition.put("BANK_ACCT_NO", idAcctInfo.get("BANK_ACCT_NO"));
        condition.put("CONTRACT_NO", idAcctInfo.get("CONTRACT_NO"));

        condition.put("USER_ACCT_DAY", idAcctDayInfo);

        String startCycleId = "";
        String endCycleId = "";
        String ifBooking = "";

        // 获取用户账期分布
        String userAcctDayDistribution = idAcctDayInfo.getString("USER_ACCTDAY_DISTRIBUTION");

        // 自然月账期
        if (GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayDistribution))
        {
            // startCycleId为本账期第一天
            startCycleId = idAcctDayInfo.getString("FIRST_DAY_THISACCT");
            endCycleId = SysDateMgr.getEndCycle20501231();

        }
        else if (GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue().equals(userAcctDayDistribution))
        {
            // 预约账期变更的,startCycleId 为下账期第一天
            startCycleId = idAcctDayInfo.getString("FIRST_DAY_NEXTACCT");
            endCycleId = SysDateMgr.getEndCycle20501231();
            ifBooking = "true";
        }

        condition.put("IF_BOOKING", ifBooking);
        condition.put("START_CYCLE_ID", startCycleId);
        condition.put("END_CYCLE_ID", endCycleId);

        setAcctInfoDesc(condition);

        IData ajaxData = new DataMap();

        ajaxData.put("FIRST_DAY_NEXTACCT", idAcctDayInfo.getString("FIRST_DAY_NEXTACCT"));
        ajaxData.put("USER_ACCTDAY_DISTRIBUTION", idAcctDayInfo.getString("USER_ACCTDAY_DISTRIBUTION"));
        
        //增加集团与成员归属业务区校验
        String grpCityCode = this.getParameter("GROUP_ACCT_CITY_CODE");
        String userAcctCityCode = idUserInfo.getString("CITY_CODE","");
        if("HNSJ".equals(grpCityCode) || "HNHN".equals(grpCityCode))
        {
            if(!grpCityCode.equals(userAcctCityCode))
            {
                ajaxData.put("TIP_MSG", "成员号码与集团的归属业务区不一致，此业务不能继续办理，请更换成员号码!");
            }
        }
        else
        {
            if("HNSJ".equals(userAcctCityCode) || "HNHN".equals(userAcctCityCode))
            {
                ajaxData.put("TIP_MSG", "成员号码的业务区编码不能为HNSJ或HNHN!");
            }
        }
        this.setAjax(ajaxData);
    }

    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = queryGroupCustInfo(cycle);
        String custId = result.getString("CUST_ID");

        if (StringUtils.isEmpty(custId))
            return;

        // 查询集团账户信息
        IDataset acctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", result);

        if (IDataUtil.isNotEmpty(acctInfos))
        {
            for (int i = 0, len = acctInfos.size(); i < len; i++)
            {
                IData acctInfoData = acctInfos.getData(i);
                String acctId = acctInfoData.getString("ACCT_ID", "");
                String payName = acctInfoData.getString("PAY_NAME", "");
                String productId = acctInfoData.getString("PRODUCT_ID", "");
                String productName = acctInfoData.getString("PRODUCT_NAME", "");

                String payModeCode = acctInfoData.getString("PAY_MODE_CODE", "");
                String userId = acctInfoData.getString("USER_ID", "");
                String epachyCode = acctInfoData.getString("EPARCHY_CODE", "");
                acctInfoData.put("DISPLAY_NAME", acctId + "|" + payName + "|" + productId + "|" + productName + "|" + userId + "|" + epachyCode + "|" + payModeCode);
            }
        }

        setAcctInfos(acctInfos);

        // 设置综合账目列表
        IData itemData = new DataMap();

        IDataset ItemList = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.queryNoteItems", itemData); // 综合帐目列表

        setNoteItemList(ItemList);

        setGroupInfo(result);
    }

    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {

        IData idata = getData();
        // CSViewCall.call(this, "SS.CreateGroupAcctSVC.checkTradeAcct", idata); // 检查是否有未完工工单

        confirm(cycle);
    }

    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        String acctId = getParameter("ACCT_ID");
        
        IData idAcctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        String strPayModeCode = idAcctInfo.getString("PAY_MODE_CODE", "0");
        // 如果不是现金帐户，即托收帐户，才查托收表
        if (!"0".equals(strPayModeCode))
        {
            IData idParam = new DataMap();
            idParam.put("ACCT_ID", getParameter("ACCT_ID"));
            IDataset idsAcctConsInfo = CSViewCall.call(this, "CS.AcctConsignInfoQrySVC.getConsignInfoByAcctIdForGrp", idParam);

            if (IDataUtil.isEmpty(idsAcctConsInfo))
            {
                return;
            }

            IData idAcctConsInfo = idsAcctConsInfo.getData(0);

            String bank_code = idAcctConsInfo.getString("BANK_CODE");
            String super_bank_code = idAcctConsInfo.getString("SUPER_BANK_CODE");

            String bank_name = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", bank_code);
            String super_bank_name = StaticUtil.getStaticValue(getVisit(), "TD_S_SUPERBANK", "SUPER_BANK_CODE", "SUPER_BANK", super_bank_code);

            idAcctInfo.put("SUPER_BANK_CODE", super_bank_name);
            idAcctInfo.put("BANK_CODE", bank_name);
        }

        setCondition(idAcctInfo);
    }

    public abstract void setAcctInfo(IData idata);

    public abstract void setAcctInfoDesc(IData acctInfoDesc);

    public abstract IData getGroupInfo();

    public abstract void setAcctInfos(IDataset idataset);

    public abstract void setCondition(IData idAcctInfo);

    public abstract void setConsignDesc(IData consignDesc);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setMemacctinfo(IData idAcctConsInfo);

    public abstract void setNoteItem(IData noteItem);

    public abstract void setNoteItemList(IDataset noteItemList);
}
