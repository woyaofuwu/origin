package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public class CreateGroupUserPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();
        
        IData result = new DataMap();
        
        IData custInfo = getEcCustomer();
        result.put("CUST_ID", custInfo.getString("CUST_ID"));
        result.put("SERIAL_NUMBER", getSerialNumber());
        result.put("EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        result.put("USER_EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        result.put("USER_INFO", getEcSubscriber());
        
        String productId = getProductId();
        result.put("PRODUCT_ID", productId);
        
        IDataset offerInfos = getOfferList();
        if(DataUtils.isEmpty(offerInfos))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到商品数据结构！");
        }
        
        IDataset offerInfoList = transformOfferList(offerInfos);
        result.put("ELEMENT_INFO", offerInfoList);
        
        IDataset compOfferChaList = getOfferChaList();
        if(DataUtils.isNotEmpty(compOfferChaList))
        {
            result.put("PRODUCT_PARAM_INFO", transformOfferChaList(productId, compOfferChaList));
        }
        
/*        IDataset ecPackageList = transformEcPackageList(offerInfos);
        if(DataUtils.isNotEmpty(ecPackageList))
        {//集团定制
            result.put("GRP_PACKAGE_INFO", ecPackageList);
        }*/
        
        if(DataUtils.isNotEmpty(offerInfos.first().getDataset("POWER100_PRODUCT_INFO")))
        {//动力100子商品
            IDataset ori_infos = offerInfos.first().getDataset("POWER100_PRODUCT_INFO");
            IDataset POWER100_PRODUCT_INFO = new DatasetList();
            for (int i = 0; i < ori_infos.size()/2; i++) {
                IData info = new DataMap();
                info.put("POWER100_PACKAGE_ID",offerInfos.first().getString("OFFER_CODE"));
                info.put("IS_CANCHOICE",ori_infos.getData(i).getString("IS_SHOW_SET_TAG").equals("true"));
                info.put("USER_ID",ori_infos.getData((ori_infos.size()/2)+i).getString("USER_ID"));
                info.put("PRODUCT_ID_B",ori_infos.getData(i).getString("OFFER_CODE"));
                info.put("PRODUCT_NAME",ori_infos.getData(i).getString("OFFER_NAME"));
                POWER100_PRODUCT_INFO.add(info);
            }
//            IData POWER100_INFO = new DataMap();
//            POWER100_INFO.put("BRAND_CODE","DLBG");
//            POWER100_INFO.put("POWER100_PRODUCT_INFO",POWER100_PRODUCT_INFO);
//            POWER100_INFO.put("POWER100_PRODUCT_SIZE",POWER100_PRODUCT_INFO.size());
            //result.put("POWER100_INFO", POWER100_INFO);
            result.put("POWER100_PRODUCT_INFO", POWER100_PRODUCT_INFO);
        }
        if(DataUtils.isNotEmpty(offerInfos.first().getDataset("GRP_PACKAGE_INFO")))
        {//定制
            result.put("GRP_PACKAGE_INFO", offerInfos.first().getDataset("GRP_PACKAGE_INFO"));
        }
        IData accountInfo = getEcAccount();
        
        IData acctInfo = transformAcctInfo(accountInfo);
        if(DataUtils.isNotEmpty(acctInfo))
        {
        	acctInfo.put("EPARCHY_CODE", result.getString("EPARCHY_CODE"));
        	acctInfo.put("USER_EPARCHY_CODE", result.getString("EPARCHY_CODE"));
            result.put("ACCT_INFO", acctInfo);
            
            if(StringUtils.isNotBlank(acctInfo.getString("ACCT_ID")))
            {
                result.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
            }
            else
            {
                result.put("ACCT_IS_ADD", true);
            }
        }
        
        IData commonInfo = getCommonData();
        
        IDataset resInfoList = transformResInfo(commonInfo);
        if(DataUtils.isNotEmpty(resInfoList))
        {
            result.put("RES_INFO", resInfoList);
        }
        
        IDataset planInfo = transformPlanInfo(commonInfo);
        if(DataUtils.isNotEmpty(planInfo))
        {
            result.put("PLAN_INFO", planInfo);
        }

        IData contractInfo = commonInfo.getData("CONTRACT_INFO");
        if(DataUtils.isNotEmpty(contractInfo))
        {
            result.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID", ""));
        }
        
        result.put("REMARK", commonInfo.getString("REMARK", ""));
        
        result.put("POST_INFO", commonInfo.getDataset("POST_INFO"));
        result.put("ASKPRINT_INFO", transAskPostInfoList(commonInfo));

        IData eosInfo = commonInfo.getData("ESOP_INFO");
        if(DataUtils.isNotEmpty(eosInfo))
        {
            result.put("EOS_INFO", eosInfo);
        }
        return result;
    }
    
    public void setServiceName() throws Exception
    {
    	String productId = getProductId();
    	String brandCode = getBrandCode();
    	if("BOSG".equals(brandCode)){
    		 setSvcName("CS.CreateBBossUserSVC.crtOrder");
    	}else if ("6130".equals(productId))
        {// 融合总机
            setSvcName("SS.CreateCentrexSuperTeleGroupUserSVC.crtOrder");
        }else if ("6100".equals(productId))
        {// 移动总机
            setSvcName("SS.CreateSuperTeleGroupUserSVC.crtOrder");
        } /** VOIP专线（专网专线） */
        if ("7010".equals(productId))
        {
        	setSvcName("SS.CreateVoipGroupUserSVC.crtOrder");
        }
        /** 互联网专线接入（专网专线） */
        if ("7011".equals(productId))
        {
        	setSvcName("SS.CreateInternetGroupUserSVC.crtOrder");
            
        }
        /** 数据专线（专网专线） */
        if ("7012".equals(productId))
        {
        	setSvcName("SS.CreateDatalineGroupUserSVC.crtOrder");
          
        }else{
            setSvcName(EcConstants.EC_OPER_SERVICE.CREATE_ENTERPRISE_SUBSCRIBER.getValue());
        }
    }
    
    private IData transformAcctInfo(IData accountInfo) throws Exception
    {
        IData acctInfo = new DataMap();
        if(DataUtils.isEmpty(accountInfo))
        {
            return acctInfo;
        }
        String acctId = accountInfo.getString("ACCT_ID");
        if(StringUtils.isNotBlank(acctId))
        {
            acctInfo.put("ACCT_ID", acctId);
        }
        else
        {
            acctInfo.putAll(accountInfo);
        }
        
        acctInfo.put("PAY_NAME", accountInfo.getString("ACCT_NAME","0"));
        acctInfo.put("PAY_MODE_CODE", accountInfo.getString("ACCT_TYPE","0"));
        
        //现金直接返回
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("0")){
        	return acctInfo;
        }
        
        //托收
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("1"))
        	acctInfo.put("PAYMENT_ID", "4");
        
        acctInfo.put("START_CYCLE_ID", SysDateMgr.getSysDate().replace("-", "").substring(0, 6));
        acctInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
        acctInfo.put("BANK_ACCT_NAME", acctInfo.getString("BANK_NAME"));
        acctInfo.put("CONSIGN_MODE", "1");
        acctInfo.put("ACT_TAG", "1");
        acctInfo.put("RSRV_STR1", "1");
        acctInfo.put("MODIFY_TAG", "0");
        	
        return acctInfo;
    }
    
    private IDataset transformPlanInfo(IData commonInfo) throws Exception
    {
    	IDataset payPlanInfo = new DatasetList();
        
        String payPlanType = commonInfo.getString("PAY_PLAN_INFO","P");
        
        String[] payPlans = payPlanType.split(",");
        
        for(int i=0;i<payPlans.length;i++){
        	IData data = new DataMap();
        	data.put("MODIFY_TAG", "0");
        	data.put("PLAN_TYPE_CODE", payPlans[i]);
        	payPlanInfo.add(data);
        }
        
        return payPlanInfo;
    }
    
}
