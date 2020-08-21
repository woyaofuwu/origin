
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 修复预约取消流量用户关停产品立即失效问题
 * 
 * @author 
 *
 */
public class ModifyYuYueDiscntAPNAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        if(!"PWLW".equals(uca.getBrandCode()))
		{
            return;
        }
        List<DiscntTradeData> lsDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if(CollectionUtils.isNotEmpty(lsDiscnt))
        {
        	int size = lsDiscnt.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dtDiscnt = lsDiscnt.get(i);
            	String discntCode = dtDiscnt.getDiscntCode();
            	String modifyTag = dtDiscnt.getModifyTag();
            	String stadateNew = dtDiscnt.getStartDate();
            	//如果取消预约的流量用户关停产品，结束时间改为立即结束
            	if(modifyTag.equals(BofConst.MODIFY_TAG_DEL) && stadateNew.compareTo(SysDateMgr.getSysTime()) > 0){
            		if("20171113".equals(discntCode)){
            			dtDiscnt.setEndDate(SysDateMgr.getSysTime());
            		}
        		}
            }
    	} 
    }
}
