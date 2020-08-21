
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * @author 
 *
 */
public class CheckPeriodCycleDiscntAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> lsDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if(CollectionUtils.isNotEmpty(lsDiscnt))
        {
        	int size = lsDiscnt.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dtDiscnt = lsDiscnt.get(i);
            	String strElementId = dtDiscnt.getElementId();

            	IDataset idsDiscnt = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9109", "PERIODCYCLEDISCNT", strElementId);
            	if(IDataUtil.isNotEmpty(idsDiscnt))
            	{
					CSAppException.apperr(CrmCommException.CRM_COMM_888,  "存在长周期套餐不允许销户，请先取消长周期套餐。");

				}
            }
    	} 
    }
}
