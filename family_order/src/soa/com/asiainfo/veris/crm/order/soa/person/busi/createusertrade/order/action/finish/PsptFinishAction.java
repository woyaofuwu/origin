package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;

import java.util.ArrayList;
import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class PsptFinishAction implements ITradeFinishAction {

	public void executeAction(IData mainTrade) throws Exception {
		IDataset staticInfo = null;

		String serialNumber = mainTrade.getString("SERIAL_NUMBER", "").trim();
		String strTradeId = mainTrade.getString("TRADE_ID");
		//System.out.println("PsptFinishActionxxxxxxxxxx27 " + mainTrade);
		IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(strTradeId);
		//System.out.println("PsptFinishActionxxxxxxxxxx29 " + customerDs);
		String remark = mainTrade.getString("REMARK","").trim();//用来区别，以便不要再次进行变更
		if (remark==null || (remark.indexOf("根据工商联系统及公安系统校验证件结果进行覆盖")==-1 && !remark.startsWith("资料因物联网卡"))) {
			if (IDataUtil.isNotEmpty(customerDs)) {
				IData customer = customerDs.getData(0);
				String name = customer.getString("CUST_NAME", "").trim();
				String psptId = customer.getString("PSPT_ID", "").trim();
				String psptTypeCode = customer.getString("PSPT_TYPE_CODE", "").trim();
				String psptAddr = customer.getString("PSPT_ADDR", "").trim();
				if (psptTypeCode.equals("0") || psptTypeCode.equals("1") || psptTypeCode.equals("2") || psptTypeCode.equals("3")) {// 个人证件

					staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_IDCARD");
					
					boolean isVerify = IDataUtil.isNotEmpty(staticInfo) && staticInfo.getData(0).getBoolean("PARA_CODE1");
					
					if (isVerify) {// 只有经过验证
						
						// REQ201804090009 新增物联网开户、变更界面单位证件开户一证多名限制需求
						// 如果是物联网号码，则需要调用客户资料变更接口，对所有物联网用户的该号码的营业执照，进行更新为正确合法的
						operTF_F_PSPT(serialNumber, customer, name, psptId, psptTypeCode);
					}
				}
			}
		}
	}
	
	/*
	private void modifycustinfo(String name, String psptId, String psptTypeCode,String serialNumber) throws Exception {
		
		IDataset qryCustomerDs = CustomerInfoQry.getCustIdByPspt(psptId);
		List<String> alreadyModUserList = new ArrayList<String>();
		//System.out.println("PsptFinishActionxxxxxxxxxx110 " + qryCustomerDs);

		if (qryCustomerDs != null && qryCustomerDs.size() > 0) {
			for (int i = 0; i < qryCustomerDs.size(); i++) {
				String custName = qryCustomerDs.getData(i).getString("CUST_NAME", "").trim();
				String PSPT_TYPE_CODE = qryCustomerDs.getData(i).getString("PSPT_TYPE_CODE", "").trim();

				if (!name.equals(custName) && PSPT_TYPE_CODE.equals(psptTypeCode)) {
					String custid = qryCustomerDs.getData(i).getString("CUST_ID", "").trim();
					IDataset qryUserDs = UserInfoQry.getAllNormalUserInfoByCustId_2(custid);
					//System.out.println("PsptFinishActionxxxxxxxxxx116  " + qryUserDs);
					
					if (qryUserDs != null && qryUserDs.size() > 0) {
						for (int j = 0; j < qryUserDs.size(); j++) {
							
							
							 * if(!qryUserDs.getData(j).getString("MAIN_TAG",
							 * "").trim().equals("1")){ continue; }
							 
							
							 * String brandCode =
							 * qryUserDs.getData(j).getString("BRAND_CODE",
							 * "").trim(); if (!brandCode.equals("PWLW")) {
							 * continue; }
							 
							String other_serialNumber = qryUserDs.getData(j).getString("SERIAL_NUMBER", "").trim();
							String other_userid = qryUserDs.getData(j).getString("USER_ID", "").trim();
							String other_custid = qryUserDs.getData(j).getString("CUST_ID", "").trim();
							if (alreadyModUserList.contains(other_serialNumber)) {
								continue;
							}
							IData data1 = new DataMap();
							data1.put("SERIAL_NUMBER", other_serialNumber);
							data1.put("USER_ID", other_userid);
							data1.put("CUST_ID", other_custid);
							
							IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", data1);
							IData params = custInfos.getData(0).getData("CUST_INFO");
							//System.out.println("PsptFinishActionxxxxxxxxxx136 CUST_INFO " + data1);

							params.put("TRADE_TYPE_CODE", "60");
							params.put("IS_NEED_SMS", false);// 不发送短信
							params.putAll(data1);
							params.put("REMARK", "资料因物联网卡"+serialNumber+"根据工商联系统及公安系统校验证件结果进行覆盖");
							params.put("SKIP_RULE", "TRUE");
							params.put("CUST_NAME", name);
							//System.out.println("PsptFinishActionxxxxxxxxxx138 " + params);
							alreadyModUserList.add(other_serialNumber);
							CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
						}
					}
				}
			}
		}
	}*/
    
	private void operTF_F_PSPT(String serialNumber, IData customer, String name, String psptId, String psptTypeCode) throws Exception {
		String remark = "资料因物联网卡" + serialNumber + "根据工商联系统及公安系统校验证件结果进行覆盖";

		IData qryData = new DataMap();
		qryData.put("SERIAL_NUMBER", serialNumber);

		IDataset ispwlwds = CSAppCall.call("SS.CreatePersonUserSVC.isPwlwOper", qryData);
		//System.out.println("PsptFinishActionxxxxxxxxxx54 " + ispwlwds);

		if (ispwlwds != null && ispwlwds.size() > 0) {
			if (ispwlwds.first().getBoolean("FLAG")) {// 是物联网号码
				// if(1==1){
				//System.out.println("PsptFinishActionxxxxxxxxxx60 " + customer);

				// 先查询再更新
				IData cond = new DataMap();
				cond.put("PSPT_ID", psptId);
				cond.put("PSPT_TYPE_CODE", psptTypeCode);
				cond.put("STATUS", "0");// 0 未被获取处理； 1 已被获取处理；
				IDataset ds = Dao.qryByCode("TF_F_PSPT", "SEL_BY_PSPTID_2", cond);
				IData psptData = new DataMap();
				if (ds != null && ds.size() > 0) {
					IData data = ds.first();
					//System.out.println("PsptFinishActionxxxxxxxxxx69 " + data);

					// if (!name.equals(data.getString("CUST_NAME", "").trim())
					// || !psptAddr.equals(data.getString("PSPT_ADDR",
					// "").trim())) {
					psptData.put("SERIAL_NUMBER", serialNumber);
					psptData.put("CUST_NAME", name);
					psptData.put("PSPT_TYPE_CODE", psptTypeCode);
					psptData.put("PSPT_ID", psptId);
					psptData.put("STATUS", "0");// 0 未被获取处理； 1 已被获取处理；
					psptData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					psptData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					psptData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					psptData.put("REMARK", remark);
					psptData.put("RSRV_STR1", "");
					psptData.put("RSRV_STR2", "");
					psptData.put("RSRV_STR3", "");
					psptData.put("RSRV_STR4", "");
					psptData.put("RSRV_STR5", "");
					psptData.put("RSRV_STR6", "");
					psptData.put("RSRV_STR7", "");
					psptData.put("RSRV_STR8", "");
					psptData.put("RSRV_STR9", "");
					Dao.update("TF_F_PSPT", psptData, new String[] { "PSPT_ID", "PSPT_TYPE_CODE" });
					// }
				} else {
					//System.out.println("PsptFinishActionxxxxxxxxxx82 " + psptData);
					psptData.put("SERIAL_NUMBER", serialNumber);
					psptData.put("CUST_NAME", name);
					psptData.put("PSPT_TYPE_CODE", psptTypeCode);
					psptData.put("PSPT_ID", psptId);
					psptData.put("STATUS", "0");// 0 未被获取处理； 1 已被获取处理；
					psptData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					psptData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					psptData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					psptData.put("REMARK", remark);
					psptData.put("RSRV_STR1", "");
					psptData.put("RSRV_STR2", "");
					psptData.put("RSRV_STR3", "");
					psptData.put("RSRV_STR4", "");
					psptData.put("RSRV_STR5", "");
					psptData.put("RSRV_STR6", "");
					psptData.put("RSRV_STR7", "");
					psptData.put("RSRV_STR8", "");
					psptData.put("RSRV_STR9", "");
					Dao.insert("TF_F_PSPT", psptData);
				}

				// modifycustinfo(name, psptId, psptTypeCode,serialNumber);
			}

		}
	}
	
	/**
	 * 修改预留字段，只是为了加锁(解决并发问题：IMS成员开户批量任务执行时会有并发问题，经常报组织机构主键冲突) 事务提交后锁自动释放
	 * 
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-5-25
	 */
	/*
	 * private void updateForFinishLock() throws Exception { IData param = new
	 * DataMap(); SQLParser parser = new SQLParser(param);parser.addSQL(
	 * " UPDATE TF_F_USER_GRP_GFSP T SET T.TAG_STR = 'U',T.UPDATE_TIME = SYSDATE "
	 * ); parser.addSQL(" WHERE 1 = 1 ");
	 * parser.addSQL(" AND T.TAG_ID = 'IMSBATFINISH' ");
	 * parser.addSQL(" AND T.END_DATE > SYSDATE "); Dao.executeUpdate(parser); }
	 */
}
