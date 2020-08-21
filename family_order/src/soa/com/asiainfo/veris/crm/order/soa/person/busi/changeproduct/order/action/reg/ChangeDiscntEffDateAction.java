package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
//import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
/**
 * 校验用户办理的优惠是否是赠送流量包，如果是的话按照入参指定的开始，结束时间订购资费
 * wuhao5
 */
public class ChangeDiscntEffDateAction implements ITradeAction{

	private static final Logger logger = Logger.getLogger(ChangeDiscntEffDateAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		if(logger.isDebugEnabled())
		{
			logger.debug("----ChangeDiscntEffDateAction------in31="+btd);
		}
		
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
		IData param = btd.getRD().getPageRequestData();
		String giftTag = param.getString("QUICK_GIFT_TAG","0");
		if(logger.isDebugEnabled())
		{
			logger.debug("----ChangeDiscntEffDateAction------in37="+param);
		} 
		//判断是否是"快装快修,超时送流量"活动
		if("1".equals(giftTag)){
			//判断是否存在优惠子台帐
			if (discntTrades != null && discntTrades.size() > 0)
	        {
				String discntCode = "";
				IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "8001", "BROAD_DISCNT", CSBizBean.getVisit().getStaffEparchyCode());
		    	if (DataUtils.isNotEmpty(commparaSet))
				{
		    		discntCode = commparaSet.getData(0).getString("PARA_CODE1");
				}
		    	else
		    	{
		    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有配置赠送的流量包编码!");
		    	}
				//循环优惠
				for (DiscntTradeData discntTrade : discntTrades)
	            {
					//判断优惠是否是新增
					if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
	                {
						//判断是否是赠送流量优惠
						if (discntCode.equals(discntTrade.getElementId()))
						{
							
							UcaData uca=btd.getRD().getUca();
							String userId=uca.getUserId();
							String serialNumber = uca.getSerialNumber();
							
							//开始时间为当前时间,结束时间为开始时间加上24小时
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");						
							String startDate = df.format(new Date());
							String endDate = SysDateMgr.addSecond(startDate, 86400);
			
						    //用户当前有生效的该优惠,按照该优惠最大生效时间顺延24小时
							IDataset discnts = TradeDiscntInfoQry.queryCountByUidDiscntCode(userId, discntCode, "0");
							
							if(IDataUtil.isNotEmpty(discnts) && discnts.size() > 0){
								DataHelper.sort(discnts, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
								IData lastDiscnt = discnts.getData(0);
								String lastDiscntDate = lastDiscnt.getString("END_DATE");
								startDate = SysDateMgr.addSecond(lastDiscntDate, 1);
								endDate = SysDateMgr.addSecond(startDate, 86400);
							}else{
								IDataset dataset = UserDiscntInfoQry.getUserByDiscntCode(userId, discntCode);
								if(IDataUtil.isNotEmpty(dataset) && dataset.size() > 0){
									DataHelper.sort(dataset, "END_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
									IData data = dataset.getData(0);
									String lastDiscntDate = data.getString("END_DATE");
									startDate = SysDateMgr.addSecond(lastDiscntDate, 1);
									endDate = SysDateMgr.addSecond(startDate, 86400);
								}
							}
				
							discntTrade.setStartDate(startDate);
							discntTrade.setEndDate(endDate);
							if(logger.isDebugEnabled())
							{
								logger.debug("----ChangeDiscntEffDateAction------in115= "+discntTrade);
							}
						}
	                }
	            }
	        }
		}
	}
}
