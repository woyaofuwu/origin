package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CheckWlwLongDiscntNotDelAction implements ITradeAction{
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		BaseReqData baseReq = btd.getRD();
		
		UcaData uca = baseReq.getUca();
		if(!"PWLW".equals(uca.getBrandCode())){
            return;
        }
		String productId = uca.getProductId();
		
		
		if(StringUtils.isNotBlank(productId) && ("20120706".equals(productId) || "84006837".equals(productId)))
		{
			List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			
			if (discntTrades != null && discntTrades.size() > 0) 
			{
				for (DiscntTradeData discntTrade : discntTrades) 
				{
					//String productId = discntTrade.getProductId();
					String modifyTag = discntTrade.getModifyTag();
					String packageId = discntTrade.getPackageId();
					String discntCode = discntTrade.getDiscntCode();
					if("1".equals(modifyTag) && ("20120706".equals(productId) || "84006837".equals(productId)))
					{
						if("70000012".equals(packageId) || "70000013".equals(packageId))
						{
							IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "4005", 
		        					"D",discntCode, "0898");
							if(IDataUtil.isNotEmpty(commInfos))
							{
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "长期通用、定向流量产品包下的优惠不能取消!");
							}
						}
					}
				}
			}
			
			//islongcycle(btd);
		}
	}
	
	private void islongcycle(BusiTradeData btd) throws Exception
	{
		// 所有长期通用包
		IDataset discntInfo1 = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", "I00010101005", "ZZZZ", null);
		// 长期定向包
		IDataset discntInfo2 = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", "I00010101006", "ZZZZ", null);
		
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		
		if (discntTrades != null && discntTrades.size() > 0) 
		{
			for (DiscntTradeData discntTrade : discntTrades) 
			{
				//String productId = discntTrade.getProductId();
				String modifyTag = discntTrade.getModifyTag();
				String packageId = discntTrade.getPackageId();
				String discntCode = discntTrade.getDiscntCode();
				if("1".equals(modifyTag) && 
						("70000012".equals(packageId) || "70000013".equals(packageId)))
				{
					String filter = "PARAM_CODE=" + discntCode;
					IDataset commInfos1 = DataHelper.filter(discntInfo1, filter);
					if(IDataUtil.isNotEmpty(commInfos1))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "长期通用、定向流量产品包下的优惠不能取消!");
					}
					IDataset commInfos2 = DataHelper.filter(discntInfo2, filter);
					if(IDataUtil.isNotEmpty(commInfos2))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "长期通用、定向流量产品包下的优惠不能取消!");
					}
				}
			}
		}
		
	}
}
