/**   
* @Title: CreateGroupAcct.java 
* @Package com.asiainfo.veris.crm.iorder.web.igroup.creategroupacct 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2018年4月12日 上午9:44:36 
* @version V1.0   
*/
package com.asiainfo.veris.crm.iorder.web.igroup.creategroupacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bankinfo.BankInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/** 
 * @ClassName: CreateGroupAcct 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author A18ccms a18ccms_gmail_com 
 * @date 2018年4月12日 上午9:44:36 
 *  
 */
public abstract class CreateGroupAcct extends GroupBasePage{


    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData dataInput = new DataMap();
        IData acctInfo = getData("acctInfo");
        String productId = getData().getString("productId");
        String custId = this.getParameter("CUST_ID_HIDE");
        String grpSn = getData().getString("GrpSn");

        String state = this.getParameter("state");// 账户操作标记 1新增账户,2修改,3删除
        dataInput.put("STATE", state);
        if(StringUtils.isNotBlank(productId) && productId.equals("9898"))
        {
            dataInput.put("isFreePay", true);
        }
        else if(StringUtils.isNotBlank(productId) && productId.equals("7345")){//集团统一付费产品
            dataInput.put("isUnifyPay", true);
        }
        else if(StringUtils.isNotBlank(productId) && productId.equals("7349")){//集团客户预缴款(虚拟)
            dataInput.put("isPrepay", true);
        }
        
        //EC 特殊标志
        acctInfo.put("CHECK", getData().getString("CHECK"));
        acctInfo.put("GROUPCUSTCODE", getData().getString("GROUPCUSTCODE"));

        dataInput.put("ACCT_INFO", acctInfo);
        dataInput.put("CUST_ID", custId);
        dataInput.put("ACCT_ID", acctInfo.getString("ACCT_ID", ""));
        dataInput.put("PAY_NAME_ISCHANGED", acctInfo.getString("PAY_NAME_ISCHANGED", "false"));
        if ("".equals(acctInfo.getString("ACCT_ID", "")))
        {
            dataInput.put("ACCT_IS_ADD", true); // 新增账户
        }
        else
        {
            dataInput.put("ACCT_IS_ADD", false);
        }

        if (StringUtils.isNotEmpty(grpSn))
            dataInput.put("SERIAL_NUMBER", grpSn);
        dataInput.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        boolean hasChgPayModePriv = this.getData().getBoolean("HAS_PAYMODE_CHGPRIV", false);
        dataInput.put("HAS_PAYMODE_CHGPRIV", hasChgPayModePriv);
        IDataset dataset = CSViewCall.call(this, "SS.CreateGroupAcctSVC.crtTrade", dataInput);
        if(hasChgPayModePriv && IDataUtil.isNotEmpty(dataset)){
            dataset.getData(0).put("HAS_PAYMODE_CHGPRIV", hasChgPayModePriv);
        }
        setAjax(dataset);
    }

//    /**
//     * 作用：根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
//     * 
//     * @param cycle
//     * @throws Throwable
//     */
//    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
//    {
//        IData result = qryGrpInfoByGroupAcct(cycle);
//        String custId = result.getString("CUST_ID");
//        queryAcctInfoList(custId);
//        // setGroupInfo(result);
//        this.setAjax(result);
//    }

    public void initGroupCustInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = qryGrpInfoByGroupAcct(cycle);
        String productId = getData().getString("PRODUCT_ID","");
        setProductId(productId);
        String custId = result.getString("CUST_ID");
       // queryAcctInfoList(custId);
        setGroupInfo(result);
        if(IDataUtil.isNotEmpty(result)){
            String isTTGrpString= result.getString("RSRV_NUM3","");
            if(isTTGrpString.equals("1")){
                setIsTTGrp("true");
            }
        }

        // 查询账户信息
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", data);

        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset acctFlag = CSViewCall.call(this, "SS.CreateGroupAcctSVC.checkHasMoreAcct", param); // 检查是否支持多个账户

        if (IDataUtil.isEmpty(acctFlag))
        {
            return;
        }
        IData flag = acctFlag.getData(0);
        String attrValue = flag.getString("ATTR_VALUE");

        IData paramFlag = new DataMap();
        if (attrValue.equals("0") || idata.size() < 1)
        {
            paramFlag.put("FLAG", "true");

        }
        if (!attrValue.equals("0") && idata.size() > 1)
        {
            paramFlag.put("FLAG", "false");

        }

        setCondition(paramFlag);

    }

    public IData qryGrpInfoByGroupAcct(IRequestCycle cycle) throws Exception
    {

        IData conParams = getData("cond", true);
        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");
        IData result = null;

        String ttGrp = getData().getString("IS_TTGRP", "false");
        if (ttGrp.equals("true"))
        {

            if (StringUtils.isNotEmpty(custId))
                result = UCAInfoIntfViewUtil.qryTTGrpCustInfoByGrpCustId(this, custId, true);
            else
                result = UCAInfoIntfViewUtil.qryTTGrpCustInfoByGrpId(this, groupId, true);

        }
        else
        {

            if (StringUtils.isNotEmpty(custId))

                result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            else
                result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            //add by chenzg@20170410 REQ201703130016申请限制账户资料变更集团账户资料变更界面，仅允许移动用户在移动BOSS界面办理
            if(IDataUtil.isNotEmpty(result)){
            	String rsrvNum3 = result.getString("RSRV_NUM3", "");
                if("1".equals(rsrvNum3)){
                	CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询集团客户资料为铁通集团,不允许办理业务!");
                }
            }
        }

        return result;
    }

    public abstract IData getGroupInfo();

    /**
     * 作用：新增账户信息编辑页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPageEdit(IRequestCycle cycle) throws Exception
    {
        String isTTGRP = getParameter("IS_TTGRP"); // 铁通
        setIsTTGrp(isTTGRP);

        IData param = new DataMap();
        param.put("PAY_NAME", getData().getString("CUST_NAME"));
        setCondition(param);
    }

    /**
     * 作用：覆盖父类方法，提交
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData idata = getData();
        CSViewCall.call(this, "SS.CreateGroupAcctSVC.checkTradeAcct", idata); // 检查是否有未完工工单
        String paytag = idata.getString("acctInfo_PAY_MODE_CODE","");
        String state = idata.getString("state","");
        //删除、修改托收账户时,校验账户是否欠费
        if((StringUtils.equals(state, "2") || StringUtils.equals(state, "3")) 
        		&& (StringUtils.equals(paytag, "0") || StringUtils.equals(paytag, "1"))){
            IData accData = new DataMap();
            accData.put("ACCT_ID", idata.getString("acctInfo_ACCT_ID"));
            IDataset returnData = CSViewCall.call(this, "AM_CRM_GetAccountOwefee", accData);
            if(IDataUtil.isNotEmpty(returnData.getData(0))){
                String ownDay = returnData.getData(0).getString("MIN_OWE_MONTH");
                String sysDate = SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(),"yyyyMM");
                int monthCount = SysDateMgr.monthIntervalYYYYMM(ownDay, sysDate);
                if(monthCount > 1){
                    //判断操作工号是否有权限：“特殊变更为托收账户” ，如果没有则按原来的提示信息提示欠费不能办理业务
                    if(this.hasPriv("ACCT_PAYMODE_CHGPRIV", "1")){
                        idata.put("HAS_PAYMODE_CHGPRIV", true);     //标识有权限做特殊变更为托收账户
                    }else{
                        CSViewException.apperr(CrmCommException.CRM_COMM_103,"该账户已欠费,请缴清费用,再做账户变更业务!最早欠费月份:" + ownDay);
                    }
                }
            }
        }
        confirm(cycle);
    }

    /**
     * 作用：根据AcctInfo查询账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        String isTTGRP = getParameter("IS_TTGRP"); // 铁通
        setIsTTGrp(isTTGRP);

        IData param = new DataMap();
        param.put("ACCT_ID", getParameter("ACCT_ID"));
        param.put(StrUtil.getNotFuzzyKey(), true);

        IData acctInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryAcctInfoByAcctIdForGrp", param);
        if (IDataUtil.isEmpty(acctInfo))
            return;

        String strPayModeCode = acctInfo.getString("PAY_MODE_CODE", "0");
        // 如果不是现金帐户，即托收帐户，才查托收表
        if (!"0".equals(strPayModeCode))
        {
            IData idParam = new DataMap();
            idParam.put("ACCT_ID", getParameter("ACCT_ID"));
            idParam.put(StrUtil.getNotFuzzyKey(), true);
            IDataset idsAcctConsInfo = CSViewCall.call(this, "CS.AcctConsignInfoQrySVC.getConsignInfoByAcctIdForGrp", idParam);
            IData idAcctConsInfo = (idsAcctConsInfo.size() > 0 ? idsAcctConsInfo.getData(0) : null);
            if (idAcctConsInfo != null)
            {
                String superBankCode = idAcctConsInfo.getString("SUPER_BANK_CODE", "");
                String eparchyCode = getVisit().getStaffEparchyCode();
                
                IDataset bankList = null;
                if (StringUtils.equals(isTTGRP, "true"))
                {
                    bankList = BankInfoIntfViewUtil.qryBankCCTInfosBySuperBankCodeAndEparchyCode(this, superBankCode, eparchyCode);
                }
                else
                {
                    bankList = BankInfoIntfViewUtil.qryBankInfosBySuperBankCodeAndEparchyCode(this,superBankCode, eparchyCode);

                }
                
                
                for(int i=0,iSize=bankList.size();i<iSize;i++){
                    IData bankInfo = bankList.getData(i);
                    String bankCode = bankInfo.getString("BANK_CODE");
                    String bank = bankInfo.getString("BANK");
                    bankInfo.put("BANK_INFO", bankCode+"|"+bank);
                }
                
                setBankList(bankList);
                // 托收限额,把db中的分 转化为元 展示到页面上
                String tmpConAmount = idAcctConsInfo.getString("RSRV_STR7");
                if (null != tmpConAmount && tmpConAmount.length() > 0)
                {
                    Float conAmount = Float.valueOf(tmpConAmount) / 100;
                    idAcctConsInfo.put("RSRV_STR7", conAmount);
                }
                setConsign(idAcctConsInfo);
            }
        }

        // 判断是否有删除账户权限
        boolean isDeleteAcct = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "GROUP_ACCT_DEL_CLASS_PRV");

        if (isDeleteAcct)
        {
            acctInfo.put("GROUP_ACCT_DEL_CLASS_PRV", "true");
        }
        else
        {
            acctInfo.put("GROUP_ACCT_DEL_CLASS_PRV", "false");
        }

        //如果不是全网用户，
        if (!StringUtils.equals(acctInfo.getString("RSRV_STR6"), "TOBBOSSACCT")){
            acctInfo.put("RSRV_STR6", "0");
            String open_date = acctInfo.getString("OPEN_DATE");
            if (open_date.compareTo("2018-11-13 23:59:59") < 0){//在存量同步时间之前
//                是全网
                String CUST_ID = acctInfo.getString("CUST_ID","");
                param.put("CUST_ID", CUST_ID);
                IDataset groupinfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
                if(IDataUtil.isNotEmpty(groupinfo)){
                    String mpGroupCustCode = groupinfo.getData(0).getString("MP_GROUP_CUST_CODE");
                    if(StringUtils.isNotBlank(mpGroupCustCode)){
                        acctInfo.put("RSRV_STR6", "TOBBOSSACCT");
                    }
                }
            }
        }

        //上级银行列表信息
        
        IDataset superBankList=pageutil.getList("TD_S_SUPERBANK","SUPER_BANK_CODE","SUPER_BANK"); 
        setSuperBankList(superBankList);
        setAjax(acctInfo);
        setCondition(acctInfo);

    }

    /**
     * 作用：查询账户信息
     * 
     * @author luojh
     */
    public void queryAcctInfoList(IRequestCycle cycle) throws Throwable
    {
    	IData data = new DataMap();
        data.put("CUST_ID", getData().getString("CUST_ID"));
        String productId = getData().getString("PRODUCT_ID","");
        setProductId(productId);
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", data);
        setInfos(idata);
    }

    /**
     * 作用：根据CUST_ID查询信息账户
     * 
     * @author luojh
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfos(IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_ID", getData().getString("CUST_ID"));
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", params);
        setInfos(idata);
    }

    // 初始化
    public void initial(IRequestCycle cycle) throws Exception
    {

        // "true": 铁通集团,"false":移动集团
        String isTTGrp = this.getParameter("IS_TTGRP", "false");

        setIsTTGrp(isTTGrp);

    }

    /**
     * 作用： 根据上级银行获取银行数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBank(IRequestCycle cycle) throws Exception
    {
        IData params = getData("acctInfo", true);

        String isTTGrp = this.getParameter("IS_TTGRP");

        IDataset bankList = null;
        
        if (StringUtils.equals(isTTGrp, "true"))
        {
            bankList = BankInfoIntfViewUtil.qryBankCCTInfosBySuperBankCodeAndEparchyCode(this, params.getString("SUPER_BANK_CODE", ""), getVisit().getStaffEparchyCode());
        }
        else
        {
            bankList = BankInfoIntfViewUtil.qryBankInfosBySuperBankCodeAndEparchyCode(this, params.getString("SUPER_BANK_CODE", ""), getVisit().getStaffEparchyCode());

        }
        
        for(int i=0,iSize=bankList.size();i<iSize;i++){
            IData bankInfo = bankList.getData(i);
            String bankCode = bankInfo.getString("BANK_CODE");
            String bank = bankInfo.getString("BANK");
            bankInfo.put("BANK_INFO", bankCode+"|"+bank);
        }

        setBankList(bankList);
    }
    
    public void queryPersonBySerial(IRequestCycle cycle) throws Throwable
    {

        IData params = getData("cond", true);
        IDataset results = new DatasetList();
        IData userInfo = new DataMap();
        IDataset tempMoffice = CSViewCall.call(this, "CS.RouteInfoQrySVC.getEparchyCodeBySn", params);

        if (tempMoffice.size() < 1)
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询服务号码归属地州无信息！");
            return;
        }
        String eparchyCode = (String) tempMoffice.get(0, "EPARCHY_CODE", "");

        params.put("REMOVE_TAG", "0");
        params.put("NET_TYPE_CODE", "00");
        params.put("EPARCHY_CODE", eparchyCode);

        IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySN", params);
        if (userInfos.size() > 0)
        {
            userInfo = (IData) userInfos.get(0);
        }
        else
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询服务号码无用户信息！");
            return;
        }
        // String custID = userInfo.getString("CUST_ID","");

        // getVisit().setRouteEparchyCode(eparchyCode);
        //
        IDataset tempPerson = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryPerInfoByCustId", userInfo);

        if (tempPerson.size() > 0)
        {
            userInfo.putAll((IData) (tempPerson.get(0)));
        }
        else
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询服务号码无客户信息！");
        }

        results.add(userInfo);
        setInfos(results);
        setCondition(params);
    }

    //判断是否为全网集团
    public void queryIfNetGroup(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("FLAG", "0");//默认为0
        IData param = getData();
//        String group_id = param.getString("GROUP_ID","");
        String CUST_ID = param.getString("CUST_ID","");
//        param.clear();
//        param.put("GROUP_ID", group_id);
        IDataset groupinfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
//        IDataset groupinfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByGrpId", param);
        if(IDataUtil.isNotEmpty(groupinfo)){
            String mpGroupCustCode = groupinfo.getData(0).getString("MP_GROUP_CUST_CODE");
            if(StringUtils.isNotBlank(mpGroupCustCode)){
                data.put("FLAG", "1");//不为空则为全网集团
                data.put("GROUPCUSTCODE", mpGroupCustCode);//全网集团编码也要传到后台
            }
        }

        setAjax(data);
    }



    /**
     * 根据银行名称或编码模糊查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBankInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData temp = getData("comminfo", true);
        data.putAll(temp);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CreateGroupAcctSVC.queryBurBankInfo", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
        	setBankListInfos(dataset);
        }
    }
    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setBankList(IDataset bankList);

    public abstract void setCondition(IData condition);

    public abstract void setConsign(IData consign);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setIsTTGrp(String isTTGrp);
    
    public abstract void setSuperBankList(IDataset  superBankList);
    
    public abstract void setBankListInfos(IDataset bankListInfos);

}
