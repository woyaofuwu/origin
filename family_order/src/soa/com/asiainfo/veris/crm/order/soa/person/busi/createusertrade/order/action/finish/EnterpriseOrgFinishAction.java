package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action.finish;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.EnterpriseOrgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * Copyright: Copyright 2016 Asiainfo
 * 
 * @ClassName:
 * @Description:
 * @version: v1.0.0
 * @author:
 * @date:
 */
public class EnterpriseOrgFinishAction implements ITradeFinishAction {
    private static Logger logger = Logger.getLogger(EnterpriseOrgFinishAction.class);

	public void executeAction(IData mainTrade) throws Exception {
		IDataset staticInfo = null;
		String isVerify = "";
		String serialNumber = mainTrade.getString("SERIAL_NUMBER", "").trim();
		String strTradeId = mainTrade.getString("TRADE_ID");
		String remark = mainTrade.getString("REMARK","").trim();//用来区别，以便不要再次进行变更
		if (remark==null || (remark.indexOf("根据工商联系统及公安系统校验证件结果进行覆盖")==-1 && !remark.startsWith("资料因物联网卡"))) {
		logger.error("EnterpriseOrgFinishActionxxxxxxxxxx32 " + mainTrade);
		IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(strTradeId);
		logger.error("EnterpriseOrgFinishActionxxxxxxxxxx36 " + customerDs);

		if (IDataUtil.isNotEmpty(customerDs)) {
			// this.updateForFinishLock(); //add by chenzg@20180525 上锁
			IData customer = customerDs.getData(0);
			String name = customer.getString("CUST_NAME", "").trim();
			String psptId = customer.getString("PSPT_ID", "").trim();
			String psptTypeCode = customer.getString("PSPT_TYPE_CODE", "").trim();
			logger.error("EnterpriseOrgFinishActionxxxxxxxxxx49 " + customer);

			if (psptTypeCode.equals("E")) {// 营业执照

				staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ENTERPRISE");
				isVerify = "";
				if (IDataUtil.isNotEmpty(staticInfo)) {
					isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,返回1为验证,0不验证
				}
				
					if (isVerify.equals("1")) {// 1是设置为需要验证
					IDataset ds = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(strTradeId, "ENTERPRISE");
					logger.error("EnterpriseOrgFinishActionxxxxxxxxxx60 " + ds);

					if (IDataUtil.isNotEmpty(ds)) {
						IData data = ds.getData(0);
						String RSRV_STR1 = data.getString("RSRV_STR1", "").trim();// 法人
						String RSRV_STR2 = data.getString("RSRV_STR2", "").trim();// 成立日期
						String RSRV_STR3 = data.getString("RSRV_STR3", "").trim();// 营业开始日期
						String RSRV_STR4 = data.getString("RSRV_STR4", "").trim();// 营业结束日期
						logger.error("EnterpriseOrgFinishActionxxxxxxxxxx68 " + data);

						if (RSRV_STR1.length() > 0 && RSRV_STR2.length() > 0 && RSRV_STR3.length() > 0 && RSRV_STR4.length() > 0) {
							
							// 查询tf_f_enterprise，看是否已存在记录，如存在，则update，不存在则insert
							IDataset enterpriseDs = EnterpriseOrgInfoQry.getEnterpriseByPsptID(psptId);
							logger.error("EnterpriseOrgFinishActionxxxxxxxxxx74 " + enterpriseDs);

							IData enterprise = new DataMap();
								
								if (IDataUtil.isNotEmpty(enterpriseDs)) {
//							if (1==0) {//测试用
									enterprise = enterpriseDs.getData(0);
									if (!enterprise.getString("ENTERPRISENAME", "").trim().equals(name) || !enterprise.getString("LEGALPERSON", "").trim().equals(RSRV_STR1) || !enterprise.getString("STARTDATE", "").trim().equals(RSRV_STR2) || !enterprise.getString("TERMSTARTDATE", "").trim().equals(RSRV_STR3) || !enterprise.getString("TERMENDDATE", "").trim().equals(RSRV_STR4)) {
										enterprise.put("REGITNO", psptId);
										enterprise.put("ENTERPRISENAME", name);
										enterprise.put("LEGALPERSON", RSRV_STR1);
										enterprise.put("STARTDATE", RSRV_STR2);
										enterprise.put("TERMSTARTDATE", RSRV_STR3);
										enterprise.put("TERMENDDATE", RSRV_STR4);
										enterprise.put("RSRV_STR5", SysDateMgr.getSysDate());
										IDataset enterInfos = EnterpriseOrgInfoQry.getEnterpriseByPsptIDOther(enterprise);
										if (IDataUtil.isEmpty(enterInfos)) {
											Dao.update("TF_F_ENTERPRISE", enterprise, new String[] { "REGITNO" });
										}
									}
								} else {
							    
								enterprise.put("REGITNO", psptId);
								enterprise.put("ENTERPRISENAME", name);
								enterprise.put("LEGALPERSON", RSRV_STR1);
								enterprise.put("STARTDATE", RSRV_STR2);
								enterprise.put("TERMSTARTDATE", RSRV_STR3);
								enterprise.put("TERMENDDATE", RSRV_STR4);
								enterprise.put("RSRV_STR1", "");
								enterprise.put("RSRV_STR2", "");
								enterprise.put("RSRV_STR3", "");
								enterprise.put("RSRV_STR4", "");
								enterprise.put("RSRV_STR5", SysDateMgr.getSysTime());
								enterprise.put("RSRV_DATE1", "");
								enterprise.put("RSRV_DATE2", "");
								enterprise.put("RSRV_DATE3", "");
								enterprise.put("RSRV_DATE4", "");
								enterprise.put("RSRV_DATE5", "");
								enterprise.put("RSRV_NUM1", "");
								enterprise.put("RSRV_NUM2", "");
								enterprise.put("RSRV_NUM3", "");
								enterprise.put("RSRV_NUM4", "");
								enterprise.put("RSRV_NUM5", "");
								enterprise.put("RSRV_TAG1", "");
								enterprise.put("RSRV_TAG2", "");
								enterprise.put("RSRV_TAG3", "");
								enterprise.put("RSRV_TAG4", "");
								enterprise.put("RSRV_TAG5", "");
								logger.error("EnterpriseOrgFinishActionxxxxxxxxxx119 " + enterprise);

								try {
									Dao.insert("TF_F_ENTERPRISE", enterprise);
									
								} catch (Exception e) {
									logger.error("EnterpriseOrgFinishActionxxxxxxxxxx125 " + e);
									/*enterprise.put("REGITNO", strTradeId);//为避免重复，该字段保持tradeid
									enterprise.put("RSRV_STR1", psptId);
									enterprise.put("RSRV_STR2", serialNumber);
									enterprise.put("RSRV_STR4", "插入表异常，可能是证件号码重复。本条记录的REGITNO代表tradeid,RSRV_STR1代表证件号码,RSRV_STR2代表证件对应的手机号码,RSRV_TAG1=1代表异常记录");
									enterprise.put("RSRV_TAG1", "1");
									logger.error("EnterpriseOrgFinishActionxxxxxxxxxx128 " + enterprise);
									
									Dao.insert("TF_F_ENTERPRISE", enterprise);*/
									
								}
							}
						}
					}
					
					// REQ201804090009 新增物联网开户、变更界面单位证件开户一证多名限制需求
					// 如果是物联网号码，则需要调用客户资料变更接口，对所有物联网用户的该号码的营业执照，进行更新为正确合法的
//						qryData.put("PSPT_TYPE_CODE", "E");

							operTF_F_PSPT(serialNumber, customer, name, psptId, psptTypeCode);

				}
			} else if (psptTypeCode.equals("M")) {// 组织机构代码证
				staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "1085", "VERIFY_ORG");
				isVerify = "";
				if (IDataUtil.isNotEmpty(staticInfo)) {
					isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,返回1为验证,0不验证
				}

					if (isVerify.equals("1")) {// 1是设置为需要验证
			    

					IDataset ds = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(strTradeId, "ORG");
					logger.error("EnterpriseOrgFinishActionxxxxxxxxxx180 " + ds);

					if (IDataUtil.isNotEmpty(ds)) {
						IData data = ds.getData(0);
						String RSRV_STR1 = data.getString("RSRV_STR1", "").trim();// 机构类型
						String RSRV_STR2 = data.getString("RSRV_STR2", "").trim();// 有效日期
						String RSRV_STR3 = data.getString("RSRV_STR3", "").trim();// 失效日期
						if (RSRV_STR1.length() > 0 && RSRV_STR2.length() > 0 && RSRV_STR3.length() > 0) {
							// 查询tf_f_org，看是否已存在记录，如存在，则update，不存在则insert
							IDataset orgDs = EnterpriseOrgInfoQry.getOrgByPsptID(psptId);
							
							IData org = new DataMap();
							if (IDataUtil.isNotEmpty(orgDs)) {
								org = orgDs.getData(0);
								if (!org.getString("ORGNAME", "").trim().equals(name) || !org.getString("ORGTYPE", "").trim().equals(RSRV_STR1)
										|| !org.getString("EFFECTIVEDATE", "").trim().equals(RSRV_STR2) || !org.getString("EXPIRATIONDATE", "").trim().equals(RSRV_STR3)) {
									org.put("ORGCODE", psptId);
									org.put("ORGNAME", name);
									org.put("ORGTYPE", RSRV_STR1);
									org.put("EFFECTIVEDATE", RSRV_STR2);
									org.put("EXPIRATIONDATE", RSRV_STR3);
									org.put("RSRV_STR5", SysDateMgr.getSysTime());
									Dao.update("TF_F_ORG", org, new String[] { "ORGCODE" });
								}
							} else {
								org.put("ORGCODE", psptId);
								org.put("ORGNAME", name);
								org.put("ORGTYPE", RSRV_STR1);
								org.put("EFFECTIVEDATE", RSRV_STR2);
								org.put("EXPIRATIONDATE", RSRV_STR3);
								org.put("RSRV_STR1", "");
								org.put("RSRV_STR2", "");
								org.put("RSRV_STR3", "");
								org.put("RSRV_STR4", "");
								org.put("RSRV_STR5", SysDateMgr.getSysTime());
								org.put("RSRV_DATE1", "");
								org.put("RSRV_DATE2", "");
								org.put("RSRV_DATE3", "");
								org.put("RSRV_DATE4", "");
								org.put("RSRV_DATE5", "");
								org.put("RSRV_NUM1", "");
								org.put("RSRV_NUM2", "");
								org.put("RSRV_NUM3", "");
								org.put("RSRV_NUM4", "");
								org.put("RSRV_NUM5", "");
								org.put("RSRV_TAG1", "");
								org.put("RSRV_TAG2", "");
								org.put("RSRV_TAG3", "");
								org.put("RSRV_TAG4", "");
								org.put("RSRV_TAG5", "");
								
								try {
									Dao.insert("TF_F_ORG", org);
									
								} catch (Exception e) {
									logger.error("EnterpriseOrgFinishActionxxxxxxxxxx216 " + e);
									/*org.put("ORGCODE", strTradeId);//为避免重复，该字段保持tradeid
									org.put("RSRV_STR1", psptId);	
									org.put("RSRV_STR2", serialNumber);
									org.put("RSRV_STR4", "插入表异常，可能是证件号码重复。本条记录的ORGCODE代表tradeid,RSRV_STR1代表证件号码,RSRV_STR2代表证件对应的手机号码,RSRV_TAG1=1代表异常记录");
									org.put("RSRV_TAG1", "1");
									logger.error("EnterpriseOrgFinishActionxxxxxxxxxx221 " + org);
									
									Dao.insert("TF_F_ORG", org);*/
									
								}
							}
						}
					}

					// REQ201804090009 新增物联网开户、变更界面单位证件开户一证多名限制需求
					// 如果是物联网号码，则需要调用客户资料变更接口，对所有物联网用户的该号码的组织机构代码，进行更新为正确合法的
//						qryData.put("PSPT_TYPE_CODE", "M");

							operTF_F_PSPT(serialNumber, customer, name, psptId, psptTypeCode);
							
					}
				}
			}
		}
	}
	
/*
	private void modifycustinfo(String name, String psptId, String psptTypeCode,String serialNumber) throws Exception {

		// 查询该证件号下的物联网用户记录，对其证件进行变更
		IDataset qryCustomerDs = CustomerInfoQry.getCustIdByPspt(psptId);
		List<String> alreadyModUserList = new ArrayList<String>();
		logger.error("EnterpriseOrgFinishActionxxxxxxxx220 " + qryCustomerDs);

		if (qryCustomerDs != null && qryCustomerDs.size() > 0) {
			for (int i = 0; i < qryCustomerDs.size(); i++) {
				String custName = qryCustomerDs.getData(i).getString("CUST_NAME", "").trim();
				String PSPT_TYPE_CODE = qryCustomerDs.getData(i).getString("PSPT_TYPE_CODE", "").trim();

				if (!name.equals(custName) && PSPT_TYPE_CODE.equals(psptTypeCode)) {
					String custid = qryCustomerDs.getData(i).getString("CUST_ID", "").trim();
					IDataset qryUserDs = UserInfoQry.getAllNormalUserInfoByCustId_2(custid);
					logger.error("EnterpriseOrgFinishActionxxxxxxxx230 " + qryUserDs);

					if (qryUserDs != null && qryUserDs.size() > 0) {
						for (int j = 0; j < qryUserDs.size(); j++) {
							
							if(!qryUserDs.getData(j).getString("MAIN_TAG", "").trim().equals("1")){
								continue;
							}
							String brandCode = qryUserDs.getData(j).getString("BRAND_CODE", "").trim();
							if (!brandCode.equals("PWLW")) {
								continue;
							}							
							
							String other_serialNumber = qryUserDs.getData(j).getString("SERIAL_NUMBER", "").trim();
							String other_userid = qryUserDs.getData(j).getString("USER_ID", "").trim();
							String other_custid = qryUserDs.getData(j).getString("CUST_ID", "").trim();
							if (alreadyModUserList.contains(other_serialNumber)) {
								continue;
							}
							logger.error("EnterpriseOrgFinishActionxxxxxxxx253 " + alreadyModUserList);
							IData data1 = new DataMap();
							data1.put("SERIAL_NUMBER", other_serialNumber);
							data1.put("USER_ID", other_userid);
							data1.put("CUST_ID", other_custid);

							IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", data1);
							IData params = custInfos.getData(0).getData("CUST_INFO");
							logger.error("EnterpriseOrgFinishActionxxxxxxxx129 CUST_INFO " + data1);

							params.put("TRADE_TYPE_CODE", "60");
							params.put("IS_NEED_SMS", false);// 不发送短信
							params.putAll(data1);
							params.put("REMARK", "资料因物联网卡"+serialNumber+"根据工商联系统及公安系统校验证件结果进行覆盖");// 需要修改
							params.put("SKIP_RULE", "TRUE");
							params.put("CUST_NAME", name);
							logger.error("EnterpriseOrgFinishActionxxxxxxxx259 " + params);
							alreadyModUserList.add(other_serialNumber);
							CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
							
						}
*/
	private void operTF_F_PSPT(String serialNumber, IData customer, String name, String psptId, String psptTypeCode) throws Exception {
		String remark = "资料因物联网卡" + serialNumber + "根据工商联系统及公安系统校验证件结果进行覆盖";
		IData qryData = new DataMap();
		qryData.put("SERIAL_NUMBER", serialNumber);
		IDataset ispwlwds = CSAppCall.call("SS.CreatePersonUserSVC.isPwlwOper", qryData);
		logger.error("EnterpriseOrgFinishActionxxxxxxxxxx263 " + ispwlwds);
		if (ispwlwds != null && ispwlwds.size() > 0) {
			if (ispwlwds.first().getBoolean("FLAG")) {// 是物联网号码
				logger.error("EnterpriseOrgFinishActionxxxxxxxxxx266 " + customer);
				IData cond = new DataMap();
				cond.put("PSPT_ID", psptId);
				cond.put("PSPT_TYPE_CODE", psptTypeCode);
				cond.put("STATUS", "0");// 0 未被获取处理； 1 已被获取处理；
				IDataset ds2 = Dao.qryByCode("TF_F_PSPT", "SEL_BY_PSPTID_2", cond);
				IData psptData = new DataMap();
				if (ds2 != null && ds2.size() > 0) {
				
					IData data = ds2.first();
					logger.error("EnterpriseOrgFinishActionxxxxxxxxxx275 " + data);
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
				} else {
					logger.error("EnterpriseOrgFinishActionxxxxxxxxxx296 " + psptData);
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
			}
		}
	}

	/**
     * 修改预留字段，只是为了加锁(解决并发问题：IMS成员开户批量任务执行时会有并发问题，经常报组织机构主键冲突)
     * 事务提交后锁自动释放
     * @throws Exception
     * @author chenzg
     * @date 2018-5-25
     */
    private void updateForFinishLock() throws Exception
    {
    	IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_F_USER_GRP_GFSP T SET T.TAG_STR = 'U',T.UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.TAG_ID = 'IMSBATFINISH' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        Dao.executeUpdate(parser);
    }
}
