package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
/**
 * B改H，办理活动判断是否有特殊套餐，修改活动开始时间
 * @author xuyt
 *
 */
public class ChangeSaleActiveTimeAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		List<SaleActiveTradeData> saleActiveTrades=btd.get("TF_B_TRADE_SALE_ACTIVE");
		boolean flag = false;
		String activeFlag = "";
		String time_start_date = "";
		String time_end_date="";
		String discnt_code = "";
		String discnt_end_date = "";
		//2.判断用户是否要办理宽带1+或者宽带包年
		if(saleActiveTrades!=null&&saleActiveTrades.size()>0){
			for(int i=0,size=saleActiveTrades.size();i<size;i++){
				SaleActiveTradeData saleActive=saleActiveTrades.get(i);
				String productId=saleActive.getProductId();	
				
				String sn = "KD_" + saleActive.getSerialNumber();
				IData snmap = UcaInfoQry.qryUserInfoBySn(sn);
				int mon = 0;
				String timeflag = "";

				if(IDataUtil.isNotEmpty(snmap))
				{
					String userID = snmap.getString("USER_ID");
					String start_date = saleActive.getStartDate();  //活动开始时间
					IDataset dismap = UserDiscntInfoQry.getUserCOMDiscnt(userID, "1510", "SALEACTIVETIME", start_date, "0898");
					if(IDataUtil.isNotEmpty(dismap))
					{
						timeflag = dismap.getData(0).getString("TIMEFLAG","");
					}
					
					//语句判断套餐的结束时间是否大于活动的开始时间，是则标识1
					if(StringUtils.isNotBlank(timeflag) && "1".equals(timeflag))
					{
						flag = true;
						//为了后续日志查找，remark记录套餐编码和套餐结束时间
						discnt_code = dismap.getData(0).getString("DISCNT_CODE","");
						discnt_end_date = dismap.getData(0).getString("END_DATE","");
						//获取套餐结束时间加1秒的时间
						time_start_date = dismap.getData(0).getString("TIME_START_DATE",saleActive.getStartDate());
						//包年需要修改结束时间
						time_end_date   = dismap.getData(0).getString("TIME_END_DATE",SysDateMgr.addMonths(discnt_end_date, 12));
						
					}
				}
				
				//宽带包年
				if(flag && saleActive.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) && "67220428".equals(productId))
				{
					saleActive.setStartDate(time_start_date);
					saleActive.setEndDate(time_end_date);
					saleActive.setRemark("优惠"+ discnt_code + "结束时间" + discnt_end_date + "特殊修改活动时间" );
					activeFlag = "1";
					
				}
				//宽带1+
				if(flag &&saleActive.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)&& "69908001".equals(productId))
				{
					saleActive.setStartDate(time_start_date);
					saleActive.setRemark("优惠"+ discnt_code + "结束时间" + discnt_end_date + "特殊修改活动时间" );
					activeFlag = "2";
				}	
			}
			
			List<SaleDepositTradeData> saledepositTrades=btd.get("TF_B_TRADE_SALE_DEPOSIT");
			List<OfferRelTradeData> offerrelTrades=btd.get("TF_B_TRADE_OFFER_REL");
			List<DiscntTradeData> discntTrades=btd.get("TF_B_TRADE_DISCNT");
			
			if(!"".equals(activeFlag))
			{
				if(saledepositTrades!=null&&saledepositTrades.size()>0){
					for(int i=0,size=saledepositTrades.size();i<size;i++){
						SaleDepositTradeData saledepositTrade=saledepositTrades.get(i);
						saledepositTrade.setStartDate(time_start_date);
						if("1".equals(activeFlag))
						{
							saledepositTrade.setEndDate(time_end_date);
						}
						saledepositTrade.setRemark("优惠"+ discnt_code + "结束时间" + discnt_end_date + "特殊修改活动时间" );
					}
				}
				
				if(offerrelTrades!=null&&offerrelTrades.size()>0){
					for(int i=0,size=offerrelTrades.size();i<size;i++){
						OfferRelTradeData offerrelTrade=offerrelTrades.get(i);
						offerrelTrade.setStartDate(time_start_date);
						if("1".equals(activeFlag))
						{
							offerrelTrade.setEndDate(time_end_date);
						}
						offerrelTrade.setRemark("优惠"+ discnt_code + "结束时间" + discnt_end_date + "特殊修改活动时间" );
					}
				}
				
				if(discntTrades!=null&&discntTrades.size()>0){
					for(int i=0,size=discntTrades.size();i<size;i++){
						DiscntTradeData discntTrade=discntTrades.get(i);
						discntTrade.setStartDate(time_start_date);
						if("1".equals(activeFlag))
						{
							discntTrade.setEndDate(time_end_date);
						}
						discntTrade.setRemark("优惠"+ discnt_code + "结束时间" + discnt_end_date + "特殊修改活动时间" );
					}
				}
			}
		}
	}
}
