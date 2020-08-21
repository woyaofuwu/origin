package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.action;
import java.util.List;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

public class changeSpeedAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
       
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
        	 
        	
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            String discntCode = discntTradeData.getDiscntCode();
            String modify_tag=discntTradeData.getModifyTag();
            if("84013441".equals(discntCode) || "84013442".equals(discntCode) ||"84013443".equals(discntCode) ||"84013444".equals(discntCode)){
	            if("1".equals(modify_tag)||("U".equals(modify_tag))){//删除与变更时
	            	 discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            	 discntTradeData.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            }else if ("0".equals(modify_tag)){
	            	 if("84013441".equals(discntCode)){ //基础优惠 
	            		 discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            		 discntTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);

	            	 }else if("84013442".equals(discntCode)){//3个月的最后一天
	            		 discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            		 discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(4, SysDateMgr.getDateForSTANDYYYYMMDD(SysDateMgr.getSysDateYYYYMMDD())));
	            	 
	            	 }else if("84013443".equals(discntCode)){//6个月的最后一天
	            		 discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            		 discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(7, SysDateMgr.getDateForSTANDYYYYMMDD(SysDateMgr.getSysDateYYYYMMDD())));

	            	 }else if("84013444".equals(discntCode)){//12个月的最后一天
	            		 discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            		 discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(13, SysDateMgr.getDateForSTANDYYYYMMDD(SysDateMgr.getSysDateYYYYMMDD())));

				     }
	            }
            
            }

            

        }
        
    }

}
