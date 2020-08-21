package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


/**
 * 物联网国漫套餐只允许11位号码办理
 * @author yanwu
 *
 */
public class CheckWlwGlobalAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		// TODO Auto-generated method stub
		if(!"PWLW".equals(btd.getRD().getUca().getBrandCode()))
		{
            return;
        }
		String strSerialNumber = btd.getRD().getUca().getSerialNumber();
		List<SvcTradeData> lsTradesvc  = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if(CollectionUtils.isNotEmpty(lsTradesvc))
		{
			IDataset wlwSvclist = CommparaInfoQry.getCommByParaAttr("CSM", "9014", "0898");
			for (int i = 0; i < lsTradesvc.size(); i++) 
			{
				SvcTradeData stTradesvc = lsTradesvc.get(i);
				String strSvcID = stTradesvc.getElementId();
				IDataset commdisnts = DataHelper.filter(wlwSvclist, "PARA_CODE2=I00010100420,PARAM_CODE="+strSvcID);
				if(IDataUtil.isNotEmpty(commdisnts))
				{
					int nlength = strSerialNumber.length();
					if(nlength != 11)
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"物联网国漫套餐只允许11位号码办理！");
					}
				}
			}
		}
		List<DiscntTradeData> lsTradeDiscnt  = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if(CollectionUtils.isNotEmpty(lsTradeDiscnt))
		{
			IDataset wlwDiscntlist = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
			for (int i = 0; i < lsTradeDiscnt.size(); i++) 
			{
				DiscntTradeData dtTradeDiscnt = lsTradeDiscnt.get(i);
				String strDiscntID = dtTradeDiscnt.getElementId();
				IDataset commdisnts1 = DataHelper.filter(wlwDiscntlist, "PARA_CODE2=I00010100400,PARAM_CODE="+strDiscntID);
				IDataset commdisnts2 = DataHelper.filter(wlwDiscntlist, "PARA_CODE2=I00010100410,PARAM_CODE="+strDiscntID);
				IDataset commdisnts3 = DataHelper.filter(wlwDiscntlist, "PARA_CODE2=I00010100430,PARAM_CODE="+strDiscntID);
				if(IDataUtil.isNotEmpty(commdisnts1) || IDataUtil.isNotEmpty(commdisnts2) || IDataUtil.isNotEmpty(commdisnts3))
				{
					int nlength = strSerialNumber.length();
					if(nlength != 11)
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"物联网国漫套餐只允许11位号码办理！");
					}
				}
			}
		}
	}
}
