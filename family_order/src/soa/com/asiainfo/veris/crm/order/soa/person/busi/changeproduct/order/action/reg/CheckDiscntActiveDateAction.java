package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * 校验用户办理的优惠是否在【办理有效期】内，注意：这里的办理有效期是独立的，不是优惠的有效期。
 * songlm 20150612 REQ201506020003 假日流量套餐开发需求
 */
public class CheckDiscntActiveDateAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		UcaData uca = btd.getRD().getUca();
		ChangeProductReqData request = (ChangeProductReqData) btd.getRD();
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
		
		//判断是否存在优惠子台帐
		if (discntTrades != null && discntTrades.size() > 0)
        {
			//循环优惠
			for (DiscntTradeData discntTrade : discntTrades)
            {
				//判断优惠是否是新增
				if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
                {
					//判断是否是6600元素
					if ("6600".equals(discntTrade.getElementId()))
					{
						//如果是新增6600元素，同时还是主产品变更，报错，不允许
						if (request.getNewMainProduct() != null)
				        {
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"主产品变更时不能同时办理假日流量套餐，请在主产品变更完成后单独办理假日流量套餐");
				        }
						
						//如果是新增6600元素，不能预约办理
						if(request.isBookingTag())
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"不能选择[预约时间]办理假日流量套餐");
						}
					}
					
					//查看该优惠是否存在需要校验办理有效期的规则配置
					IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "335", discntTrade.getElementId(), uca.getUserEparchyCode());
					
					//如果存在配置，则进行校验
					if(IDataUtil.isNotEmpty(commparaInfos))
					{
						//判断优惠是否在有效期内
						IDataset isInActiveDate = CommparaInfoQry.getCommparaIsInActiveDate("CSM", "335", discntTrade.getElementId());
						
						//如果结果集是空，代表不在有效期内
						if(IDataUtil.isEmpty(isInActiveDate))
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_71);
						}
					}
                }
            }
        }
	}
}
