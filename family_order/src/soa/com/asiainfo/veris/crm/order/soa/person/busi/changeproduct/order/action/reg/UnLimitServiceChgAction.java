
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * Copyright: Copyright (c) 2016
 * 
 * @ClassName: UnLimitServiceChgAction
 * @Title: REQ201604130016 关于关闭流量欺诈客户上网功能的需求
 * @Description: 通过该界面或批量操作关闭上网功能后，无法通过其它渠道方式恢复，只能通过该界面或操作才能恢复客户的上网功能。
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2016-05-11
 * 
 */
public class UnLimitServiceChgAction implements ITradeAction
{

	protected static final Logger log = Logger.getLogger(UnLimitServiceChgAction.class);
	
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String strBatChId = btd.getRD().getBatchId();
    	String strBatchOperType = btd.getRD().getPageRequestData().getString("BATCH_OPER_TYPE");
    	String strSn = btd.getMainTradeData().getSerialNumber();
    	String strUserId = btd.getMainTradeData().getUserId();
    	if(!"".equals(strBatChId) && "SERVICECHGSPEC".equals(strBatchOperType))
    	{
    		List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            if(CollectionUtils.isNotEmpty(tradeSvcs))
            {
            	IData svcInfo = new DataMap();
            	boolean bService22 = false;
            	int size = tradeSvcs.size();
                for (int i = 0; i < size; i++)
                {
                	SvcTradeData tradeSvc = tradeSvcs.get(i);
                	if("22".equals(tradeSvc.getElementId()) &&  
                		BofConst.MODIFY_TAG_DEL.equals(tradeSvc.getModifyTag()) && 
                	   "S".equals(tradeSvc.getElementType()))
                	{
                		bService22 = true;
                		svcInfo.put("SERIAL_NUMBER", strSn);
                		svcInfo.put("USER_ID", strUserId);
                		svcInfo.put("RSRV_STR1", tradeSvc.getInstId());
                		svcInfo.put("INST_ID", SeqMgr.getInstId());
                		svcInfo.put("START_DATE", SysDateMgr.getSysTime());
                		svcInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                		svcInfo.put("ModifyTag", "0");
                		break;
                	}
                }
                
                if(bService22)
                {
                	createOtherTradeData(btd, svcInfo);
                }
                
        	}
            
    	}
    	else
    	{
    		
    		List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            if(CollectionUtils.isNotEmpty(tradeSvcs))
            {
            	boolean bModifyTag = false;
            	for (int i = 0; i < tradeSvcs.size(); i++) 
            	{
            		SvcTradeData tradeSvc = tradeSvcs.get(i);
            		if("22".equals(tradeSvc.getElementId()) &&  
                		BofConst.MODIFY_TAG_ADD.equals(tradeSvc.getModifyTag()) && 
                	   "S".equals(tradeSvc.getElementType()))
            		{
            	        
            			IDataset svcInfos =UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(strUserId, "UnLimitService");
            			//IDataset svcInfos = UserSvcInfoQry.getSvcIdByUserIdOrder(uca.getUserId(), "22");
            			if(IDataUtil.isNotEmpty(svcInfos))
            			{
            				IData svcInfo = svcInfos.getData(0);
            				svcInfo.put("SERIAL_NUMBER", strSn);
                    		svcInfo.put("END_DATE", SysDateMgr.getSysTime());
                    		svcInfo.put("ModifyTag", "1");
            				createOtherTradeData(btd, svcInfo);
            				bModifyTag = true;
                			break;
            			}
            		}
            		
            		if(bModifyTag)
        			{
            			break;
        			}
            		
				}
            	
            	boolean isUnLimitServicePriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "UnLimitService");
            	if(bModifyTag && !isUnLimitServicePriv)
            	{
            		IDataset compare3998 = CommparaInfoQry.getCommparaInfoByCode("CSM", "3998", "UnLimitService", "22", "0898");
            		if(IDataUtil.isNotEmpty(compare3998))
                    {
            			IData infoData = compare3998.getData(0);
            			String strInfo = infoData.getString("PARAM_NAME", "配置错误");
            			CSAppException.apperr(CrmCommException.CRM_COMM_888, strInfo);
            			
                    }else{
                    	//该号码为GPRS服务受限号码，代理渠道网点无法办理该业务，请前往自办营业网点办理。
                		CSAppException.apperr(CrmCommException.CRM_COMM_888, "该号码为GPRS服务受限号码，代理渠道网点无法办理该业务，请前往自办营业网点办理。");
                    }
            		
            	}
            	
            }
    		
    	}
        
    }
    
    public void createOtherTradeData(BusiTradeData btd, IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER"); 
        String strUserid = input.getString("USER_ID");
        String strInstId = input.getString("RSRV_STR1");
    	String strModifyTag = input.getString("ModifyTag");
    	if("0".equals(strModifyTag))
    	{
    		OtherTradeData otherTD = new OtherTradeData();
            otherTD.setUserId(strUserid);
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setRsrvValueCode("UnLimitService");
            otherTD.setRsrvValue("22");
            otherTD.setRemark("REQ201604130016 关于关闭流量欺诈客户上网功能的需求");
            otherTD.setStartDate(SysDateMgr.getSysTime());
            otherTD.setEndDate(SysDateMgr.getTheLastTime());
            
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            
            otherTD.setRsrvStr1(strInstId);
            otherTD.setRsrvStr2(SysDateMgr.getSysTime());
            otherTD.setRsrvStr3(CSBizBean.getVisit().getStaffId());
            otherTD.setRsrvStr4(CSBizBean.getVisit().getStaffName());
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setRsrvStr5(serialNumber);
            btd.add(serialNumber, otherTD);
            
    	}
    	else if("1".equals(strModifyTag))
    	{
    		OtherTradeData otherTD = new OtherTradeData(input);
    		otherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    		otherTD.setEndDate(SysDateMgr.getSysTime());
    		btd.add(serialNumber, otherTD);
    		
    	}
        
    }

}
