package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class EChannelContractAction implements ITradeAction 
{
	@SuppressWarnings("unchecked")
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		
		UcaData uca = btd.getRD().getUca();
		String strUserID = uca.getUserId();
		String strProduct = "69900101";
		IDataset CommparaParaP9981 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "9891", "4GOnlineSaleActive", "P", "0898");
		if(IDataUtil.isNotEmpty(CommparaParaP9981))
    	{
			strProduct = CommparaParaP9981.first().getString("PARA_CODE1", "69900101");
    	}
		
		List<SaleActiveTradeData> ls4GOnline = uca.getUserSaleActiveByProductId(strProduct);
		
		if(CollectionUtils.isNotEmpty(ls4GOnline))
		{
			SaleActiveTradeData tdSA = ls4GOnline.get(0);
			String strProductName = tdSA.getProductName();
			
			List<ProductTradeData> lsProductTrade = btd.get("TF_B_TRADE_PRODUCT");
			if(CollectionUtils.isNotEmpty(lsProductTrade))
			{
				String strError = String.format("%s合约期内的用户，不能更换主产品套餐，请重新选择。", strProductName);
				CSAppException.apperr(CrmCommException.CRM_COMM_888, strError);
			}
			
			int nAddCount = 0;
			int nDelCount = 0;
			List<DiscntTradeData> lsDiscntTrade = btd.get("TF_B_TRADE_DISCNT");
			if(CollectionUtils.isNotEmpty(lsDiscntTrade))
			{
				for (int i = 0; i < lsDiscntTrade.size(); i++) 
				{
					DiscntTradeData dtDiscnt = lsDiscntTrade.get(i);
					String strElementType = dtDiscnt.getElementType();
					String strElementId = dtDiscnt.getElementId();
					String strModifyTag = dtDiscnt.getModifyTag();
					IDataset CommparaParaD9981 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "9891", "4GOnlineUserProduct", strElementId, "D", "0898");
					if("D".equals(strElementType) && BofConst.MODIFY_TAG_ADD.equals(strModifyTag) && IDataUtil.isNotEmpty(CommparaParaD9981))
					{
						String strAddPrice = this.getPrice(BofConst.ELEMENT_TYPE_CODE_DISCNT, strElementId);//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strElementId);
						int m = Integer.valueOf(strAddPrice);
						nAddCount += m;
					}
					else if("D".equals(strElementType) && BofConst.MODIFY_TAG_DEL.equals(strModifyTag) && IDataUtil.isNotEmpty(CommparaParaD9981))
					{
						String strDelPrice = this.getPrice(BofConst.ELEMENT_TYPE_CODE_DISCNT, strElementId);//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strElementId);
						int m = Integer.valueOf(strDelPrice);
						nDelCount += m;
					}
				}
				int nUserCount = 0;
				//查询用户已订购的飞享套餐优惠
				IDataset idsUserDiscnt = UserDiscntInfoQry.getFXDiscntByUserIdA(strUserID);
				if(IDataUtil.isNotEmpty(idsUserDiscnt))
				{
					for (int i = 0; i < idsUserDiscnt.size(); i++) 
					{
						IData idUserDiscnt = idsUserDiscnt.getData(i);
						String strUserDC = idUserDiscnt.getString("DISCNT_CODE");
						String strUserPrice = this.getPrice(BofConst.ELEMENT_TYPE_CODE_DISCNT, strUserDC);//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strUserDC);
						nUserCount += Integer.valueOf(strUserPrice);
					}
					int nNewCount = nUserCount - nDelCount + nAddCount;
					if(nNewCount < nUserCount)
					{
						String strError = String.format("%s合约期内的用户，不能更换低档次套餐，请重新选择。", strProductName);
						CSAppException.apperr(CrmCommException.CRM_COMM_888, strError);
					}
				}
			}
		}
	}
	
	private String getPrice(String elementType, String elementId) throws Exception{
		IDataset prices = UpcCall.queryOfferPriceRelPriceByOfferId(elementType,elementId);
		if(IDataUtil.isEmpty(prices)){
			return "0";
		}
		IData price = prices.getData(0);
		return price.getString("FEE","0");
	}
}
