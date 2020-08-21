package com.asiainfo.veris.crm.order.soa.person.busi.customerclub;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;


public class CustomerClubBean extends CSBizBean{
	
	
	/**
	 * 查询会员信息
	 * @param input
	 */
	public IDataset queryClubInfo(IData input) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		
		return Dao.qryByCode("TF_O_CUSTOMERCLUB", "SEL_BY_SERIAL", param, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 退会记录日志
	 * chenxy3
	 * */
	public String retreatClub(IData input) throws Exception {
		String tradeId=input.getString("TRADE_ID");
		IData param = new DataMap();
		param.put("SEQ_ID", SeqMgr.getInstId());
		param.put("TRADE_ID", tradeId);  // 发送流水
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER")); 
		param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()); // 受理渠道编码
		param.put("REASON", input.getString("REASON")); // 退会原因  
		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());  // 受理员工 
		param.put("CLUB_TYPE", input.getString("CLUB_TYPE")); // 退会俱乐部  
		 
		Dao.executeUpdateByCodeCode("TF_O_CUSTOMERCLUB", "INS_RETREAT_CLUB_INFO", param, Route.CONN_CRM_CEN);
		return tradeId;
	}

	public void insertClub(IData input) throws Exception {
		IData param = new DataMap();
		param.put("SEQ_ID", SeqMgr.getInstId());
		param.put("TRADE_ID", input.getString("TRADE_ID"));  // 发送流水
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("CLUB_TYPE", input.getString("CLUB_TYPE")); // 会员类型
		param.put("AGREEMENT_TAG", input.getString("AGREEMENT_TAG")); // 是否签署协议
		param.put("ENTRY_TIME", SysDateMgr.getSysTime()); // 入会时间
		param.put("IN_MODE_CODE", input.getString("IN_MODE_CODE")); // 受理渠道编码
		param.put("RELATED_ACTIVITY_ID", input.getString("RELATED_ACTIVITY_ID")); // 关联活动ID
		param.put("ACCEPT_TAG", "0"); // 受理标记：0-入会，1-退会
		param.put("SINGNING_TIME", SysDateMgr.getSysTime()); // 协议时间
		param.put("CUST_NAME", input.getString("CUST_NAME")); // 客户名
		param.put("UPDATE_TIME", SysDateMgr.getSysTime()); // 受理时间
		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());  // 受理员工
		param.put("START_DATE", SysDateMgr.getSysTime());  //开始时间
		param.put("END_DATE", SysDateMgr.END_DATE_FOREVER); //结束时间
		
		Dao.insert("TF_O_CUSTOMERCLUB", param, Route.CONN_CRM_CEN);
		
	}

	public IDataset getSeq(IData input) throws Exception {
		IDataset results = new DatasetList();
		IData result = new DataMap();
		String seq_id = SeqMgr.getAcctId();
		result.put("SEQ_ID", seq_id);
		results.add(result);
		return results;
	}

	
	public void updateInsertClub(IData input) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("CLUB_TYPE", input.getString("CLUB_TYPE"));
		Dao.executeUpdateByCodeCode("TF_O_CUSTOMERCLUB", "UPDATE_BY_SERIAL", param, Route.CONN_CRM_CEN);
		
	}
	
	public void updateRetreatClub(IData input) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("CLUB_TYPE", input.getString("CLUB_TYPE"));
		param.put("RETREAT_TRADE_ID", input.getString("RETREAT_TRADE_ID"));
		Dao.executeUpdateByCodeCode("TF_O_CUSTOMERCLUB", "UPDATEDATE_BY_SERIAL", param, Route.CONN_CRM_CEN);
		
	}
	
	/**
	 * @param 入会校验
	 * @param td
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData clubInfoIboss(IData input) throws Exception {
		String kind_id =input.getString("KIND_ID");// 交易唯一标识
		String routetype = "00";//路由类型  00-省代码，01-手机号
		String routevalue = "000";
		
		String trade_depart_passwd = "";//渠道接入密码
		
		String accountnum ="";
		String phonenum = input.getString("SERIAL_NUMBER");
		
		// 调用一级BOSS接口
		IData result = (IData) this.ibossCallSmsManagement(input,kind_id, routetype,
				routevalue,trade_depart_passwd,accountnum,phonenum);
		return result;
	}
	
	
	
	
	/**
	 * IBOSSCALL类中 调用一级BOSS接口 查询DM历史配置信息
	 * @param inData
	 * @param kind_id
	 * @param routetype
	 * @param routevalue
	 * @param trade_depart_passwd
	 * @param accountnum
	 * @param phonenum
	 * @return
	 * @throws Exception
	 */
    public IData ibossCallSmsManagement(IData inData,String kind_id, String routetype,
			String routevalue,String trade_depart_passwd,String accountnum,
			String phonenum) throws Exception {
		inData.put("KIND_ID", kind_id);
		inData.put("ROUTETYPE", routetype);
		inData.put("ROUTEVALUE", routevalue);
		inData.put("TRADE_DEPART_PASSWD", trade_depart_passwd);//渠道接入密码
		inData.put("ACCOUNTNUM", accountnum);
		inData.put("PHONENUM", phonenum);//手机号码

		return IBossCall.dealInvokeUrl(kind_id, "IBOSS", inData).first();
	}

	


}
