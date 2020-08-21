package com.asiainfo.veris.crm.order.soa.group.grplocalmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GrpOfCityCodeMgrBean extends GroupBean {
	
   	private static String custId = "";
   	private static String changeCityCode = "";
   	private static String synTag = "";

    @Override
    public void actTradeSub() throws Exception {
        super.actTradeSub();
        
        modifyTradeUser();
        modifyTradeCustomer();
        modifyTradeAccount();
    }
   
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        //哪笔工单来做同步集团客户资料
        if(StringUtils.isNotBlank(synTag) && StringUtils.equals("1", synTag))
    	{
        	IData map = bizData.getTrade();
        	map.put("RSRV_STR1", "1");
        	map.put("RSRV_STR2", changeCityCode);
    	}
    }
    
    protected void modifyTradeUser() throws Exception
    {
        UserTradeData userTradeData = reqData.getUca().getUser();
        // 用户
        if (userTradeData != null)
        {
        	String userId = userTradeData.getUserId();
        	IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        	userInfo.put("CITY_CODE", changeCityCode);
        	userInfo.put("MODIFY_TAG",TRADE_MODIFY_TAG.MODI.getValue());
        	super.addTradeUser(userInfo);
        }
    }
    
    /**
     * 同步customer表数据
     * @throws Exception
     */
    protected void modifyTradeCustomer() throws Exception
    {
    	if(StringUtils.isNotBlank(synTag) && StringUtils.equals("1", synTag))
    	{
    		if(StringUtils.isNotBlank(custId))
    		{
    			IDataset custInfos = CustomerInfoQry.getCustomerByCustID(custId);
    			if(IDataUtil.isNotEmpty(custInfos))
    			{
    				for(int i=0;i<custInfos.size();i++)
    				{
    					IData acctInfo = custInfos.getData(i);
    					acctInfo.put("CITY_CODE", changeCityCode);
    					acctInfo.put("MODIFY_TAG",TRADE_MODIFY_TAG.MODI.getValue());
    				}
    				super.addTradeCustomer(custInfos);
    			}
    		}
    	}
    }
    
    /**
     * 同步客户的账户资料
     * @throws Exception
     */
    protected void modifyTradeAccount() throws Exception
    {
    	if(StringUtils.isNotBlank(synTag) && StringUtils.equals("1", synTag))
    	{
    		if(StringUtils.isNotBlank(custId))
    		{
    			IDataset acctInfos = AcctInfoQry.qryAcctInfoByCustId(custId);
    			if(IDataUtil.isNotEmpty(acctInfos))
    			{
    				for(int i=0;i<acctInfos.size();i++)
    				{
    					IData acctInfo = acctInfos.getData(i);
    					acctInfo.put("CITY_CODE", changeCityCode);
    					acctInfo.put("MODIFY_TAG",TRADE_MODIFY_TAG.MODI.getValue());
    				}
    				super.addTradeAccount(acctInfos);
    			}
    		}
    	}
    }
    
    
    @Override
    protected void initReqData() throws Exception {
        super.initReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);
        custId = map.getString("CUST_ID","");
        changeCityCode = map.getString("CHANGE_CITY_CODE","");
        synTag = map.getString("SYN_TAG","");
    }

    @Override
    protected final void makUca(IData map) throws Exception {
        makUcaForGrpNormal(map);
    }
    
    @Override
    protected String setTradeTypeCode() throws Exception {
        return "3927";
    }
    
}
