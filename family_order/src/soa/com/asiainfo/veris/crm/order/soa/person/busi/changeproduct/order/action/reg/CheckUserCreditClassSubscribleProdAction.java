package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 核对用户的星级是否能够办理业务
 *
 */
public class CheckUserCreditClassSubscribleProdAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		
		String tradeTypeCode = btd.getTradeTypeCode();
		UcaData uca = btd.getRD().getUca();
		
		//获取用户的星级
		String strCreditClass = null;
		if("310".equals(tradeTypeCode))
		{	//如果是复机
			strCreditClass = "-1";	//用户销户的时候，星级已经全部清空
		}
		else
		{	
			strCreditClass = uca.getUserCreditClass();
		}
		
		int iCreditClass = Integer.parseInt(strCreditClass);
		
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		if(CollectionUtils.isNotEmpty(userDiscnts) && userDiscnts.size() > 0)
		{
			for (DiscntTradeData userDiscnt : userDiscnts)
            {
                String modifyTag = userDiscnt.getModifyTag();
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                	String discntCode = userDiscnt.getDiscntCode();
                	
                	//获取验证的数据
                	IDataset strCompara9966 = CommparaInfoQry.getCommparaInfoByCode("CSM", "9966", "D", discntCode, "0898");
                	List<SvcTradeData> userVolte = uca.getUserSvcBySvcId("190");
                	if(IDataUtil.isNotEmpty(strCompara9966) && CollectionUtils.isNotEmpty(userVolte))
                	{
                		int nDays = SysDateMgr.getDayIntervalNoAbs(uca.getUser().getOpenDate(), SysDateMgr.getSysTime());
            			if(nDays < 365)
            			{
            				CSAppException.apperr(CrmCommException.CRM_COMM_103, "VOLTE用户在网时长不满一年以上不具备订购优惠" + discntCode);
            			}
            			
            			String strIsRealName = uca.getCustomer().getIsRealName();
            			if(!"1".equals(strIsRealName))
            			{
            				CSAppException.apperr(CrmCommException.CRM_COMM_103, "VOLTE用户不是实名制不具备订购优惠" + discntCode);
            			}
            			
            			boolean isBlackSn = false;
            			String psptId = uca.getCustomer().getPsptId();
            			String strSn = uca.getSerialNumber();
            			IData input = new DataMap();
            			input.put("PSPT_ID", psptId);
            			input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            	    	IDataset callSets = AcctCall.qryBlackListByPsptId(input);//调账务接口查黑名单
            	    	if(IDataUtil.isNotEmpty(callSets))
            	    	{
            	    		for (int i = 0; i < callSets.size(); i++) 
            	    		{
            	    			IData callSet = callSets.getData(i);
            	    			String strBlackSn = callSet.getString("SERIAL_NUMBER", "");
            	    			if(strSn.equals(strBlackSn))
            	    			{
            	    				isBlackSn = true;
            	    				break;
            	    			}
            				}
            	    	}
            	    	
            	    	if(!isBlackSn)
            	    	{
            	    		return;
            	    	}
            	    	else
            	    	{
            	    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "VOLTE用户是黑名单不具备订购优惠" + discntCode);
            	    	}
            			
                	}
                	else
                	{
                		
                		//获取验证的数据
                    	IDataset configRules = CommparaInfoQry.getCommparaInfoByCode("CSM", "1415", "D", discntCode, "0898");
                    	
                    	if(IDataUtil.isNotEmpty(configRules))
                    	{
                    		boolean isValid = checkCreditValid(iCreditClass, configRules);
                        	if(!isValid)
                        	{
                        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户的星级不具备订购优惠" + discntCode);
                        	}
                    	}
                    	
                	}
                }
            }
		}
	}
	
	
	public boolean checkCreditValid(int userCredit, IDataset configRules)throws Exception{
		boolean isValid=false;
		
		for(int i=0,size=configRules.size();i<size;i++){
    		String minClass=configRules.getData(i).getString("PARA_CODE2","");
    		String maxClass=configRules.getData(i).getString("PARA_CODE3","");
    		
    		if(!minClass.equals("")&&!maxClass.equals("")){
    			int minClassI=Integer.parseInt(minClass);
    			int maxClassI=Integer.parseInt(maxClass);
    			
    			if(userCredit>=minClassI&&userCredit<=maxClassI){
    				isValid=true;
    			}
    		}else if(!minClass.equals("")){
    			int minClassI=Integer.parseInt(minClass);
    			
    			if(userCredit>=minClassI){
    				isValid=true;
    			}
    		}else if(!maxClass.equals("")){
    			int maxClassI=Integer.parseInt(maxClass);
    			
    			if(userCredit<=maxClassI){
    				isValid=true;
    			}
    		}
    		
    	}
		
		return isValid;
	}
}
