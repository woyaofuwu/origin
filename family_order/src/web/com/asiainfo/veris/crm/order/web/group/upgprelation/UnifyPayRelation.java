
package com.asiainfo.veris.crm.order.web.group.upgprelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee.UserFeeIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class UnifyPayRelation extends GroupBasePage
{
    
    /**
     * 初始化
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", "7345");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, 
                "CS.ParamInfoQrySVC.getCommparaByParamattr", 
                iparam);
        
        if (IDataUtil.isNotEmpty(resultSet))
        {
            param.put("MEB_FILE_SHOW","true");
        }
        
        setCondition(param);
    }
    
    /**
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = queryGroupCustInfo(cycle);
        String custId = result.getString("CUST_ID");

        if (StringUtils.isEmpty(custId))
        {
            return;
        }

        IData param = new DataMap();
        param.put("CUST_ID", custId);
        //查询集团统一付费的产品
        IDataset prodoctInfos = CSViewCall.call(this, 
                "CS.UserProductInfoQrySVC.getGrpProductByCustIdForUPGP",
                param);
        
        if(IDataUtil.isNotEmpty(prodoctInfos))
        {
            IData productInfo = prodoctInfos.getData(0);
            
            if(IDataUtil.isNotEmpty(productInfo))
            {
                
                String userId = productInfo.getString("USER_ID","");
                String serialNumber = productInfo.getString("SERIAL_NUMBER","");
                param.clear();
                param.put("USER_ID", userId);
                
                //欠费情况的判断
                IData oweFeeData = UserFeeIntfViewUtil.qryGrpUserOweFeeInfo(this, userId);
                if (IDataUtil.isEmpty(oweFeeData))
                {
                    CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_145, serialNumber);
                }
                else
                {
                    double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE","0"));// 实时欠费
                    if (acctBalance < 0)
                    {
                        CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_146, serialNumber);
                    }
                }
                
                IDataset acctInfos = CSViewCall.call(this, 
                        "CS.AcctInfoQrySVC.getAcctInfoByUserIdFroUPGP", 
                        param);
                
                if(IDataUtil.isNotEmpty(acctInfos))
                {
                    for (int i = 0, len = acctInfos.size(); i < len; i++)
                    {
                        IData acctInfoData = acctInfos.getData(i);
                        String acctId = acctInfoData.getString("ACCT_ID", "");
                        String payName = acctInfoData.getString("PAY_NAME", "");
                        String productId = acctInfoData.getString("PRODUCT_ID", "");
                        String productName = acctInfoData.getString("PRODUCT_NAME", "");

                        String payModeCode = acctInfoData.getString("PAY_MODE_CODE", "");
                        String tempUserId = acctInfoData.getString("USER_ID", "");
                        String epachyCode = acctInfoData.getString("EPARCHY_CODE", "");
                        StringBuffer sb = new StringBuffer();
                        sb.append(acctId);
                        sb.append("|");
                        sb.append(payName);
                        sb.append("|");
                        sb.append(productId);
                        sb.append("|");
                        sb.append(productName);
                        sb.append("|");
                        sb.append(tempUserId);
                        sb.append("|");
                        sb.append(epachyCode);
                        sb.append("|");
                        sb.append(payModeCode);
                        acctInfoData.put("DISPLAY_NAME", sb.toString());
                    }
                }
                
                setAcctInfos(acctInfos);
            }
        }

        // 设置综合账目列表
        IData itemData = new DataMap();
        IDataset ItemList = CSViewCall.call(this, 
                "CS.NoteItemInfoQrySVC.queryNoteItems", 
                itemData); // 综合帐目列表

        setNoteItemList(ItemList);

        setGroupInfo(result);
    }
    
    /**
     * 查询手机用户、集团产品用户
     * @param cycle
     * @throws Exception
     */
    public void getAdvChgNewSnInfo(IRequestCycle cycle) throws Exception
    {
        String memSn = this.getParameter("MEM_SERIAL_NUMBER");
        // 查询成员用户信息是否存在
        
        IData condition = new DataMap();
        IData ajaxData = new DataMap();
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", memSn);
        
        //查询用户信息，并且可以判断是否是集团产品用户
        IDataset memInfos = CSViewCall.call(this, 
                "CS.UserInfoQrySVC.getUserInfoBySN", param);
        if(IDataUtil.isEmpty(memInfos))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_117, memSn);
        }
        
        IData memInfo = memInfos.getData(0);
        String isGrpSn = memInfo.getString("IsGrpSn");
        String eparchyCode = memInfo.getString("EPARCHY_CODE");
        String userId = memInfo.getString("USER_ID", "");
        if(StringUtils.isNotBlank(isGrpSn) 
                && StringUtils.equals("Yes", isGrpSn))
        {//集团产品用户
            
            ajaxData.put("IsGrpNum", "true");
            //获取集团产品用户三户信息
            IData ucaData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, memSn, false);
            
            IData idCustInfo = ucaData.getData("GRP_CUST_INFO");
            IData idUserInfo = ucaData.getData("GRP_USER_INFO");
            IData idAcctInfo = ucaData.getData("GRP_ACCT_INFO");
            
            IData idAcctDayInfo = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this,
                    userId,eparchyCode,false);
            if(IDataUtil.isEmpty(idAcctDayInfo))
            {
                CSViewException.apperr(AcctDayException.CRM_ACCTDAY_17, userId);
            }
            
            condition.put("USER_ID", idUserInfo.getString("USER_ID"));
            condition.put("CUST_NAME", idCustInfo.getString("CUST_NAME"));
            condition.put("EPARCHY_CODE", idUserInfo.get("EPARCHY_CODE"));
            condition.put("ACCT_ID", idAcctInfo.get("ACCT_ID"));
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
            
            ajaxData.put("FIRST_DAY_NEXTACCT", idAcctDayInfo.getString("FIRST_DAY_NEXTACCT"));
            ajaxData.put("USER_ACCTDAY_DISTRIBUTION", idAcctDayInfo.getString("USER_ACCTDAY_DISTRIBUTION"));
            
        } else { //手机号码
            
            ajaxData.put("IsGrpNum", "false");
            IData relaParam = new DataMap();
            relaParam.put("USER_ID_B", memInfo.getString("USER_ID", ""));
            relaParam.put("RELATION_TYPE_CODE", "56");
            relaParam.put("ROLE_CODE_B", "2");

            relaParam.put(Route.ROUTE_EPARCHY_CODE, memInfo.getString("EPARCHY_CODE"));
            IDataset relaDatas = CSViewCall.call(this, 
                    "CS.RelationUUQrySVC.getRelaFKByUserIdB", relaParam);

            if (IDataUtil.isNotEmpty(relaDatas))
            {
                CSViewException.apperr(UUException.CRM_UU_101, memSn);
            }
            
            // 获取成员用户三户信息
            IData ucaData = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this, memSn);

            IData idCustInfo = ucaData.getData("MEB_CUST_INFO");
            IData idUserInfo = ucaData.getData("MEB_USER_INFO");
            IData idAcctInfo = ucaData.getData("MEB_ACCT_INFO");
            IData idAcctDayInfo = ucaData.getData("MEB_ACCTDAY_INFO");

            condition.put("USER_ID", idUserInfo.getString("USER_ID"));
            condition.put("CUST_NAME", idCustInfo.getString("CUST_NAME"));
            condition.put("EPARCHY_CODE", idUserInfo.get("EPARCHY_CODE"));
            condition.put("ACCT_ID", idAcctInfo.get("ACCT_ID"));
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
            
            ajaxData.put("FIRST_DAY_NEXTACCT", idAcctDayInfo.getString("FIRST_DAY_NEXTACCT"));
            ajaxData.put("USER_ACCTDAY_DISTRIBUTION", idAcctDayInfo.getString("USER_ACCTDAY_DISTRIBUTION"));
        }
        
        this.setAjax(ajaxData);
    }

    /**
     * 根据acct_id查询账户的信息
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        String acctId = getParameter("ACCT_ID");
        
        IData idAcctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        String strPayModeCode = idAcctInfo.getString("PAY_MODE_CODE", "0");
        //如果不是现金帐户，即托收帐户，才查托收表
        if (!"0".equals(strPayModeCode))
        {
            IData idParam = new DataMap();
            idParam.put("ACCT_ID", getParameter("ACCT_ID"));
            IDataset idsAcctConsInfo = CSViewCall.call(this, 
                    "CS.AcctConsignInfoQrySVC.getConsignInfoByAcctIdForGrp",
                    idParam);

            if (IDataUtil.isEmpty(idsAcctConsInfo))
            {
                return;
            }

            IData idAcctConsInfo = idsAcctConsInfo.getData(0);

            String bankCode = idAcctConsInfo.getString("BANK_CODE");
            String superBankCode = idAcctConsInfo.getString("SUPER_BANK_CODE");

            String bankName = StaticUtil.getStaticValue(getVisit(), 
                    "TD_B_BANK", "BANK_CODE", "BANK",
                    bankCode);
            String superBankName = StaticUtil.getStaticValue(getVisit(), 
                    "TD_S_SUPERBANK", "SUPER_BANK_CODE", "SUPER_BANK",
                    superBankCode);

            idAcctInfo.put("SUPER_BANK_CODE", superBankName);
            idAcctInfo.put("BANK_CODE", bankName);
        }

        setCondition(idAcctInfo);
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

        IDataset iDataset = CSViewCall.call(this, 
                "CS.NoteItemInfoQrySVC.filterNoteItems", data);

        setNoteItemList(iDataset);
    }
    
    /**
     * 提交
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        confirm(cycle);
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData idata = new DataMap();
        IData data = getData();
        IData acctInfo = getData("acctInfo"); // 集团
        String sn = getData("cond").getString("SERIAL_NUMBER");

        idata.put("SERIAL_NUMBER", sn);

        idata.put("USER_ID", acctInfo.getString("USER_ID"));

        idata.put("START_CYCLE_ID", data.getString("newSnInfo_START_CYCLE_ID"));
        idata.put("END_CYCLE_ID", data.getString("newSnInfo_END_CYCLE_ID"));

        idata.put("LIMIT_TYPE", data.getString("LIMIT_TYPE"));
        idata.put("LIMIT", StringUtils.isEmpty(data.getString("LIMIT")) ? 0 : data.getString("LIMIT"));
        idata.put("NEW_LIMIT", StringUtils.isEmpty(data.getString("NEW_LIMIT")) ? 0 : data.getString("NEW_LIMIT"));
        
        idata.put("COMPLEMENT_TAG", data.getString("COMPLEMENT_TAG"));
        
        idata.put("IsGrpNum", data.getString("IsGrpNum"));
        
        idata.put("FEE_TYPE", data.getString("FEE_TYPE"));
        // 账目编码
        idata.put("PAYITEM_CODE", data.getString("selectItemcodes"));
        idata.put("newSnInfo_CheckAll", data.getString("newSnInfo_CheckAll"));

        idata.put("crmSmsOrder", data.getString("crmSmsOrder"));// 订购短信提醒
        idata.put("acctSmsOrder", data.getString("acctSmsOrder"));// 订购短信提醒

        String mebFileShow = data.getString("MEB_FILE_SHOW");
        if(StringUtils.isNotBlank(mebFileShow) 
                && StringUtils.equals("true", mebFileShow) )
        {
            String fileList =  data.getString("MEB_FILE_LIST");
            if(StringUtils.isNotBlank(fileList))
            {
                idata.put("MEB_FILE_SHOW", data.getString("MEB_FILE_SHOW"));
                idata.put("MEB_FILE_LIST", data.getString("MEB_FILE_LIST"));
            }
        }
        
        //add by chenzg@2018075--REQ201804280001集团合同管理界面优化需求--凭证上传附件
        idata.put("MEB_VOUCHER_FILE_LIST", data.getString("MEB_VOUCHER_FILE_LIST", ""));
        idata.put("AUDIT_STAFF_ID", data.getString("AUDIT_STAFF_ID", ""));
        
        IDataset dataset = CSViewCall.call(this, "SS.UnifyPayRelationSVC.crtTrade", idata);

        setAjax(dataset);
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
