package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 多人约消用户办理拦截
 * @author Cnaic
 *
 */
public class CheckDRProductAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String brandCode = btd.getRD().getUca().getBrandCode();
		String sn = btd.getRD().getUca().getSerialNumber();
		String discntCode = "";
		
		List<DiscntTradeData> discntTrades=btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		for(DiscntTradeData discnt:discntTrades)
		{
			discntCode = discnt.getDiscntCode();
			IDataset drProductList = CommparaInfoQry.getCommparaInfoBy5("CSM", "8383", "DR_DEPLOY_PRODUCT", discntCode ,CSBizBean.getTradeEparchyCode(),null);
			boolean isTagid = false;
			if (IDataUtil.isNotEmpty(drProductList)) {
				isTagid=true;
			}
		
			System.out.println(">>>>>discntCode>>>>>>discntCode="+discntCode);
			System.out.println(">>>>>drProductList>>>>>>drProductList="+drProductList);
			
			IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(sn,"61");
			//String  roleCodeB=snRelationSet.getData(0).getString("ROLE_CODE_B");
			if (IDataUtil.isNotEmpty(snRelationSet)) 
			{
				for (int i = 0; i < drProductList.size(); i++) 
				{
					if (isTagid) 
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：多人约消用户不允许办理企业尊享套餐");
						return;
					}
				}
				
				IDataset drbCodeList = CommparaInfoQry.getCommparaInfoBy5("CSM", "8383", "DR_DEPLOY_BRANDCODE", brandCode ,CSBizBean.getTradeEparchyCode(),null);
				if (IDataUtil.isNotEmpty(drbCodeList)) {
					isTagid=true;
				}
				System.out.println(">>>>>drbCodeList>>>>>>drbCodeList="+drbCodeList);
				
				for (int i = 0; i < drbCodeList.size(); i++) 
				{
					if (isTagid) 
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：多人约消用户不允许办理TD固话、移动公话、随e行等品牌");
						return;
					}
				}
			}
		}
	}

}
