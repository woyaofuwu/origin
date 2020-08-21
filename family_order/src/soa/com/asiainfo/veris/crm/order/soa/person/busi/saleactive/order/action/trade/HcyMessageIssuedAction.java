package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;

/**
 * 手机终端购机时，下发和彩云推荐短信
 * 
 * @author liwei9 2020-5-6
 */
public class HcyMessageIssuedAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
	
		  String preType = btd.getRD().getPageRequestData().getString("PRE_TYPE","");
		  if(BofConst.PRE_TYPE_CHECK.equals(preType)){
			  return;
		  }

		List<SaleGoodsTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
		
		if (list != null && list.size() > 0 && !list.isEmpty()) {

			SaleGoodsTradeData saleGoods = list.get(0);
			
			String resType = saleGoods.getResTypeCode();
           //判断资源类型为4
			if("4".equals(resType)){
        	   
			String rescode = saleGoods.getResCode();

			if (StringUtils.isNotEmpty(rescode) && !"-1".equals(rescode)) {
				//效验终端类型是否匹配串号，如果返回1为匹配，如果返回0则不匹配 
				IDataset idset = HwTerminalCall.checkIsResRightType(rescode,"rsclM.01");
				
				String retVal = idset.getData(0).getString("retVal");
				//判断资源接口返回是否为1
				if ("1".equals(retVal)) {
					// 下发短信,构造短信台账
					SendSMS(btd);

				}
			}
		}
	 }
  }
	private void SendSMS(BusiTradeData btd) throws Exception
	    {
	        SmsTradeData std = new SmsTradeData();
	        
	        std.setSmsNoticeId(SeqMgr.getSmsSendId());
	        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
	        std.setBrandCode(btd.getRD().getUca().getBrandCode());
	        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
	        std.setSmsNetTag("0");
	        std.setChanId("11");
	        std.setSendObjectCode("6");
	        std.setSendTimeCode("1");
	        std.setSendCountCode("1");
	        std.setRecvObjectType("00");

	        std.setRecvId(btd.getRD().getUca().getUserId());
	        std.setSmsTypeCode("20");
	        std.setSmsKindCode("02");
	        std.setNoticeContentType("0");
	        std.setReferedCount("0");
	        std.setForceReferCount("1");
	        std.setForceObject("");
	        std.setForceStartTime("");
	        std.setForceEndTime("");
	        std.setSmsPriority("50");
	        std.setReferTime(SysDateMgr.getSysTime());
	        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
	        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
	        std.setDealTime(SysDateMgr.getSysTime());
	        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
	        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
	        std.setDealState("0");// 处理状态，0：未处理
	        std.setRemark("和彩云APP下发提示");
	        std.setRevc1("");
	        std.setRevc2("");
	        std.setRevc3("");
	        std.setRevc4("");
	        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
	        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
	        std.setCancelTag("0");
	        // 短信截取
	        String strContent = "尊敬的客户：您好！购买新手机后，"
					+ "通讯录、相册等资料如何安全转到新手机上呢？"
					+ "和彩云云盘APP可以帮您一键备份老手机上的通讯录、"
					+ "相册等资料到云盘，再从云盘恢复到新手机上。"
					+ "现在下载和彩云APP即可免费使用16G空间，"
					+ "还可享特惠价5元500G空间（原价15元），专属优惠链接：http://dx.10086.cn/YUTI。【中国移动】"; 
	        std.setNoticeContent(strContent);

	        std.setRecvObject(btd.getRD().getUca().getSerialNumber());// 发送号码
	        
	        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
	    }

}
