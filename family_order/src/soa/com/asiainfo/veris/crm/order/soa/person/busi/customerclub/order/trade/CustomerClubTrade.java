
package com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order.trade;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order.requsetdata.CustomerClubReqData;


/**
 * REQ201708300021+俱乐部会员页面增加入会协议需求
 * 客户俱乐部业务入会台账
 * @author mengqx
 * 
 */
public class CustomerClubTrade extends BaseTrade implements ITrade
{
	@Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	
    	
		CustomerClubReqData rd = (CustomerClubReqData) btd.getRD();
		
		//往预留字段中存入协议，为了打印单据做判断条件
		MainTradeData mainTradeData = btd.getMainTradeData();	
		mainTradeData.setRsrvStr6(rd.getClubType());
		
		String serialNumber = rd.getUca().getUser().getSerialNumber();//info.getString("AUTH_SERIAL_NUMBER", ""); 
		//获取in_mode_code与渠道比对关系
		String inModeCode = getVisit().getInModeCode();
		IData input=new DataMap();
		input.put("IN_MODE_CODE", inModeCode);
		//IDataset chans = CSViewCall.call(this, "SS.CustomerClubSVC.getInModeCodeChange", input); 
//		String channel="12";
//		if(chans!=null && chans.size()>0){
//			channel=chans.getData(0).getString("CHANNEL","");
//		}
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CHANNEL", "");
		
		IDataset tradeIds = CSAppCall.call("SS.CustomerClubSVC.getSeq", param);//SeqMgr.getAcctId();CS.SeqMgrSVC.getTradeId
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
			param.put("TRADE_ID", date + sqlId);
		}
		IData param1 = new DataMap();
		param1.put("SERIAL_NUMBER", serialNumber);
		/*IDataset bandCodes = CSViewCall.call(this,"SS.CustomerClubSVC.getBrandInfo", param1); // 获取品牌信息
		String bandid = bandCodes.getData(0).getString("BRAND_CODE");
		if ("G001".equals(bandid)) {
			param.put("CLUB_TYPE", "G");
		} else if ("G010".equals(bandid)) {
			param.put("CLUB_TYPE", "M");
		} else {
			param.put("CLUB_TYPE", "B");
		}*/
		param.put("CLUB_TYPE", rd.getClubType());
		param.put("AGREEMENT", "Y");
		param.put("SIGNING_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("ENTRY_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("RELATE_ACTIVITY", "0000");
		param.put("CUST_NAME", rd.getCustName());
		param.put("RESERVE", "");
		param.put("KIND_ID","BIP5A061_T5000061_0_0");//会员入会受理请求
		IData temp=new DataMap();
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CLUB_TYPE", param.getString("CLUB_TYPE"));
		temp.put("AGREEMENT", param.getString("AGREEMENT"));
		temp.put("ENTRY_TIME", param.getString("ENTRY_TIME"));
		temp.put("CHANNEL", "");
		temp.put("RELATE_ACTIVITY", param.getString("RELATE_ACTIVITY"));
		temp.put("SIGNING_TIME", SysDateMgr.getSysTime());
		temp.put("RESERVE", "");
		IDataset temps=new DatasetList();
		temps.add(temp);
		param.put("MemberInfo", temps);
		
		IDataset insertInfos = CSAppCall.call("SS.CustomerClubSVC.insertClubIboss", param); // 会员入会受理请求
		
		//REQ201708300021+俱乐部会员页面增加入会协议需求 by mnegqx 20180711
		//测试构造数据
//		IData data = new DataMap();
//		data.put("RES_CODE", "00");
//		data.put("CLUB_TYPE", "G");//G,M,B
//		data.put("G_FIRST_IN", "Y");
//		data.put("G_ENTRANCE_TIME", TimeUtil.getDefaultSysTime());
//		IDataset insertInfos = new DatasetList();
//		insertInfos.add(data);
		//构造结束
		
		
		if (null != insertInfos && !insertInfos.isEmpty()) {
			if (!"00".equals(insertInfos.getData(0).getString("RES_CODE"))) {
				String errInfo=insertInfos.getData(0).getString("RES_DESC");
				if(errInfo==null || "".equals(errInfo)){
					errInfo=insertInfos.getData(0).getString("X_RSPDESC");
				}
				CSAppException.apperr(CrmCommException.CRM_COMM_103,errInfo);
			}
		}
		
		param.put("IN_MODE_CODE", inModeCode);
		param.put("AGREEMENT_TAG", "Y");//签署
		param.put("RELATED_ACTIVITY_ID", "0000");
		CSAppCall.call( "SS.CustomerClubSVC.insertClub", param); // 会员入会
		CSAppCall.call( "SS.CustomerClubSVC.updateInsertClub", param); // 将原先退会结束时间改为当先时间
		

    } 

}
