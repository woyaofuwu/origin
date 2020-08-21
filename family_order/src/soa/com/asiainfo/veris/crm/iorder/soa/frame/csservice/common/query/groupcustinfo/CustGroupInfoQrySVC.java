package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.groupcustinfo;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CustGroupInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public IDataset qryGroupByCustID(IData input) throws Exception
    {    	   	    	
    	return CustGroupInfoQry.qryGroupByCustID(input);
    }
    
    /**
     * 根据集团客户下用户费用情况
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpAccountDepositByCustId(IData input) throws Exception
    {    
        String cust_id = input.getString("CUST_ID");
    	IDataset offerListDataset=UserInfoQry.getUserInfoByCstIdForGrpHasPriv(cust_id, "false", getPagination());
    	if(IDataUtil.isEmpty(offerListDataset)){
    		return null;
    	}
        for(int i=0,sizeI=offerListDataset.size();i<sizeI;i++){
        
        	String userid=offerListDataset.getData(i).getString("USER_ID");
        	try{
        		IData feeData= AcctCall.getOweFeeByUserId(userid);
        		if(StringUtils.isNotEmpty(feeData.getString("ACCT_BALANCE"))){
        			offerListDataset.getData(i).put("DEPOSIT_BALANCE",Float.parseFloat(feeData.getString("ACCT_BALANCE")) / 100+"");
        		}
        	}catch(Exception e){
        		offerListDataset.getData(i).put("DEPOSIT_BALANCE","0");
        	}
        }
    	return offerListDataset;
    }
    
    /**
     * 根据集团合同到期
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpContactInfoByCustId(IData input) throws Exception
    {    
        String cust_id = input.getString("CUST_ID");
    	IDataset allContractInfo=CustGroupInfoQry.qryValidContractByCustId(cust_id);
    	if(IDataUtil.isEmpty(allContractInfo)){
    		return new DatasetList();
    	}
        for (int i = 0, size = allContractInfo.size(); i < size; i++)
            {
                IData contractData = allContractInfo.getData(i);
                String contractId = contractData.getString("CONTRACT_ID");
                String contractInDate= contractData.getString("CONTRACT_END_DATE");
                int month=SysDateMgr.monthIntervalYYYYMM(contractInDate, SysDateMgr.getSysDate());
                if(month<=0){
                	contractData.put("VALID", false);
                }else{
                	contractData.put("VALID", true);
                }
                if (StringUtils.isNotEmpty(contractId))
                {
                	IDataset productInfos = CustContractProductInfoQry.qryContractProductByContId(contractId);
                	if(productInfos != null && productInfos.size() > 0){
                		contractData.put("PRODUCT_ID",productInfos.getData(0).getString("PRODUCT_ID"));
                		contractData.put("PRODUCT_NAME",productInfos.getData(0).getString("PRODUCT_NAME"));
                	}
                }
            }
        return allContractInfo;
    }
    
}