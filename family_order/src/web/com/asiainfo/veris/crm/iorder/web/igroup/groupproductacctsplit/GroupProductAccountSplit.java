package com.asiainfo.veris.crm.iorder.web.igroup.groupproductacctsplit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PayRelationTradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.ptypeproductinfo.PTypeProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class GroupProductAccountSplit extends GroupBasePage {

    public void getGrpUCAInfoBySn(IRequestCycle cycle) throws Exception
    {

        String grpsn = getData().getString("cond_GROUP_SERIAL_NUMBER");
        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);
        IData userInfo = result.getData("GRP_USER_INFO");
        userInfo.put("CHECKED", "true");
        String productId = userInfo.getString("PRODUCT_ID", "");

        if (!PTypeProductInfoIntfViewUtil.ifGrpProductTypeBooByProductId(this, productId))
        {// 非集团产品树下的产品编码不允许办理业务
            CSViewException.apperr(GrpException.CRM_GRP_745, ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId));
            return;
        }

        IData productInfoData = GroupProductUtilView.getProductExplainInfo(this, productId);

        result.put("PRODUCT_DESC_INFO", productInfoData);
        
        this.setAjax(result);

    }
    
    /**
     * 查询账户信息
     * @param cycle
     * @throws Throwable
     */
    public void queryAcctInfos(IRequestCycle cycle) throws Throwable{
        
        String custId = this.getParameter("CUST_ID");
        String curacctId = this.getParameter("ACCT_ID"); 
        
        if (StringUtils.isEmpty(custId))
            return;

        IData inParam = new DataMap();
        inParam.put("CUST_ID", custId);
        inParam.put("ACCT_ID", curacctId);
        inParam.put("SERIAL_NUMBER", this.getParameter("SERIAL_NUMBER"));

        // 查询集团账户信息
        IDataset curAcctInfos  = CSViewCall.call(this, "SS.SplitGroupProductFromAcct.queryDefPayRelationByCustIdAndAcctId", inParam);
        if(curAcctInfos != null && curAcctInfos.size() ==1)
        {
            CSViewException.apperr(PayRelationTradeException.CRM_PAYRELATION_26,this.getParameter("SERIAL_NUMBER"),curacctId);
        }
        //查询当前账户下的集团产品
        IDataset acctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", inParam);
        
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
    }
    
    
    /**
     * 根据集团服务号码查询集团账户信息
     * @param inpara
     * @throws Exception
     */
    public void getAcctByGrpSn(IData inpara) throws Exception
    {

        String grpSn = inpara.getString("SERIAL_NUMBER", "");

        if (StringUtils.isEmpty(grpSn))
            return;
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, grpSn);
        IDataset defPayRelaInfos = UCAInfoIntfViewUtil.qryGrpValidPayRelaInfoByUserId(this, userInfo.getString("USER_ID"));
        IDataset acctInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(defPayRelaInfos))
        {
            for (int i = 0; i < defPayRelaInfos.size(); i++)
            {
                String acctId = defPayRelaInfos.getData(i).getString("ACCT_ID");
                IData acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId, false);
                acctInfos.add(acctInfo);
            }
        }
        
        setAcctInfos(acctInfos);

    }
    
    /**
     * 获取集团可用账户
     * @param inpara
     * @throws Exception
     */
    public void getAcctByCustId(IRequestCycle cycle) throws Exception
    {

    	String custId = getData().getString("CUST_ID");
		if (StringUtils.isNotBlank(custId))
		{
		  IDataset acctInfos = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
		  for(int i=0; i<acctInfos.size(); i++){
			  IData acctInfo = acctInfos.getData(i);
			  acctInfo.put("ROWINDEX", i);
		  }
		  setAcctInfos(acctInfos);

		}
    }
    
    /**
     * 作用： 根据账户ID查询账户信息
     */
    public void getAcctByActId(IRequestCycle cycle) throws Exception
    {

        IData inpara = getData();
        String acctId = inpara.getString("ACCT_ID");
        if (StringUtils.isEmpty(acctId))
            return;
        IData acctInfo = new DataMap();
        if (StringUtils.isNotBlank(acctId))
        {
            acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        }
        setAcctDetailInfo(acctInfo);
        this.setAjax(acctInfo);

    }
    
    /**
     * 作用：根据AcctInfo查询账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        //String isTTGRP = getParameter("IS_TTGRP"); // 铁通
        //setIsTTGrp(isTTGRP);

        IData param = new DataMap();
        param.put("ACCT_ID", getData().getString("ACCT_ID"));
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
/*                String superBankCode = idAcctConsInfo.getString("SUPER_BANK_CODE", "");
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
                
                setBankList(bankList);*/
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
        setEditAcctInfo(acctInfo);
        
        IData bankInfoData = initSelectList();
        setInfo(bankInfoData);
        this.setAjax(acctInfo);
    }
    
    

	private IData initSelectList() throws Exception {
		IData bankData = new DataMap();
		bankData.put("ACCT_TYPE_SEL_LIST", getAcctTypeSelList());
		bankData.put("SUPER_BANK_CODE_SEL_LIST", getSuperBankSelList());
		
		return bankData;
	}

	private static IDataset getAcctTypeSelList() throws Exception {
		IDataset result = StaticUtil.getStaticList("PAY_MODE");
		if (result == null) {
			result = new DatasetList();
		}
		return result;
	}

	private IDataset getSuperBankSelList() throws Exception {
		IDataset datas = CSViewCall.call(this,"CS.BankInfoQrySVC.queryBankList", new DataMap());

		if (datas == null) {
			datas = new DatasetList();
		}
		return datas;
	}
	
	/**
	 * 查询银行信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryBankInfo(IRequestCycle cycle) throws Exception {
		String superBank = getData().getString("SUPER_BANK");
		IData info = new DataMap();

		if (StringUtils.isNotBlank(superBank)) {
			IDataset result = getBankSelList(superBank,getVisit().getLoginEparchyCode());

			info.put("BANK_CODE_SEL_LIST", result);
		}
		
		setInfo(info);

	}
	private IDataset getBankSelList(String superBank,String mgmtDistrict) throws Exception {
        IData info = new DataMap();
        info.put("SUPER_BANK_CODE", superBank);
        info.put("EPARCHY_CODE", mgmtDistrict);
        IDataset datas  = CSViewCall.call(this,"CS.BankInfoQrySVC.queryBackCode", info);

		if (datas == null) {
			datas = new DatasetList();
		}

		return datas;
	}
	
    /**
     * 修改账户信息提交方法
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData idata = getData();
        CSViewCall.call(this, "SS.CreateGroupAcctSVC.checkTradeAcct", idata); // 检查是否有未完工工单
        String paytag = idata.getString("cond_PAY_MODE_CODE","");
        String state = idata.getString("state","");
        //删除、修改托收账户时,校验账户是否欠费
        if((StringUtils.equals(state, "2") || StringUtils.equals(state, "3")) 
        		&& StringUtils.equals(paytag, "1")){
            IData accData = new DataMap();
            accData.put("ACCT_ID", idata.getString("cond_ACCT_ID"));
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
                        CSViewException.apperr(CrmCommException.CRM_COMM_103,"该账户已欠费,请缴清费用,再做托收业务!最早欠费月份:" + ownDay);
                    }
                }
            }
        }
        confirm(cycle);
    }
    
    /**
     * 新增账户提交方法
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTradeForAdd(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        CSViewCall.call(this, "SS.CreateGroupAcctSVC.checkTradeAcct", param); // 检查是否有未完工工单
        IData dataInput = new DataMap();
        IData acctInfo = getData("add");
        String productId = getData().getString("productId");
        String custId = getData().getString("CUST_ID_HIDE");
        //String grpSn = getData().getString("GrpSn");

        String state = this.getParameter("state");// 账户操作标记 1新增账户,2修改,3删除
        dataInput.put("STATE", state);
        if(StringUtils.isNotBlank(productId) && productId.equals("9898"))
        {
            dataInput.put("isFreePay", true);
        }
        else if(StringUtils.isNotBlank(productId) && productId.equals("7345")){//集团统一付费产品
            dataInput.put("isUnifyPay", true);
        }
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
        
        //if (StringUtils.isNotEmpty(grpSn))
            //dataInput.put("SERIAL_NUMBER", grpSn);
        dataInput.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        boolean hasChgPayModePriv = this.getData().getBoolean("HAS_PAYMODE_CHGPRIV", false);
        dataInput.put("HAS_PAYMODE_CHGPRIV", hasChgPayModePriv);
        IDataset dataset = CSViewCall.call(this, "SS.CreateGroupAcctSVC.crtTrade", dataInput);
        if(hasChgPayModePriv && IDataUtil.isNotEmpty(dataset)){
            dataset.getData(0).put("HAS_PAYMODE_CHGPRIV", hasChgPayModePriv);
        }
        setAjax(dataset);
        
    }
    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData dataInput = new DataMap();
        IData acctInfo = getData("cond");
        IData param = getData();
        String productId = getData().getString("productId");
        String custId = getData().getString("CUST_ID_HIDE");
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
    
    /**
     * 拆分提交
     * @param cycle
     * @throws Exception
     */
    public void onSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.SplitGroupProductFromAcct.crtTrade", data);

        setAjax(dataset);
    }
    
    /**
     * 新增账户初始化
     * @param cycle
     * @throws Exception
     */
    public void initAdd(IRequestCycle cycle) throws Exception
    {	
    	String custId = getData().getString("CUST_ID");
    	IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this,custId);
    	String custName = custInfo.getString("CUST_NAME");
        IData bankInfoData = initSelectList();
        setInfo(bankInfoData);
        
        this.setAjax("CUST_NAME", custName);
    }
    
    public abstract void setGroupCustInfo(IData groupCustInfo);
   
    public abstract void setAcctInfos(IDataset acctInfos);
    
    public abstract void setAcctDetailInfo(IData acctDetailInfo);
    
    public abstract void setEditAcctInfo(IData editAcctInfo);   
    
    public abstract void setIsTTGrp(String acctDetailInfo);
    
    public abstract void setConsign(IData consign);

    public abstract void setInfo(IData info);


    
}
