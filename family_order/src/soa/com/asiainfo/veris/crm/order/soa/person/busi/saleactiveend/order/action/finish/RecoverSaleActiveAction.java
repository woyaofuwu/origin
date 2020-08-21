package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwnchangeproduct.CancelWNChangeProductBean;

/**
 * 恢复办理功能，吉祥号码用户突然不打算携转但已缴纳违约金，恢复吉祥号码营销活动的结束时间；
 * @author mqx
 */
public class RecoverSaleActiveAction implements ITradeFinishAction{
	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
		System.out.println(">>>>>>>>>>>RecoverSaleActiveAction>>>>>>>>>>mainTrade:"+mainTrade);
		if("237".equals(mainTrade.getString("TRADE_TYPE_CODE",""))&&"2".equals(mainTrade.getString("CANCEL_TAG","")))
		{
			String oldSaleactivetradeId = mainTrade.getString("TRADE_ID", "");
			System.out.println(">>>>>>>>>>RecoverSaleActiveAction>>>>>>>>>>>oldSaleactivetradeId:"+oldSaleactivetradeId);

			CancelWNChangeProductBean.recoverActiveEndDateByBak(oldSaleactivetradeId);
		}
    }
}
