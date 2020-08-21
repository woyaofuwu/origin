package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class CheckDreamNetAction implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{


		String tradeTypeCode=btd.getTradeTypeCode();
	
		
        if( "3700".equals(tradeTypeCode) )
        {
    		
        	List<PlatSvcTradeData> listTradePlatSvc = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        	
        	if (listTradePlatSvc == null || listTradePlatSvc.size() <= 0)
            {
                return;
            }
        	for (int i = 0; i < listTradePlatSvc.size(); i++)
            {
        		PlatSvcTradeData pstd = listTradePlatSvc.get(i);
        		String modify_tag=pstd.getModifyTag();
        		String oper_code=pstd.getOperCode();
        		String serviceId = pstd.getElementId();
        		if("0".equals(modify_tag)&&"01_06".indexOf(oper_code)>-1){
        			
    	        	IDataset data = PlatInfoQry.getPlatSvcBySpCode(serviceId);
    	        
    	        	if(IDataUtil.isNotEmpty(data)){
    	        		
    	        		IData statuData = data.getData(0);
        	        	
        	            String statu = statuData.getString("STATUS");
    	            	String endDate = statuData.getString("END_DATE");
    	            	//endDate大返回-1 相等返回0
    	            	boolean ist = SysDateMgr.decodeTimestamp(endDate, "yyyy-MM-dd") .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), "yyyy-MM-dd"))>0;
        	            //int sysDate = SysDateMgr.getSysDate().compareTo(endDate);
        	           
                		if (statu.equals("0") && ist)
                    	{
                			
                    		CSAppException.apperr(CrmUserException.CRM_USER_783,"暂停订购状态，不允许办理该业务！");
                    	}
        	            
    	        		
    	        	}
    	        	
    		        	
        		}
        		
				
            }
        }
	}
	

	




}
