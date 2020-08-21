package com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGiveClassInfoQry;

public class GiveUserClassBean extends CSBizBean {
	private static Logger logger = Logger.getLogger(GiveUserClassBean.class);

	public IDataset queryUserClassBySn(IData data) throws Exception {
		IDataset userclassdata = UserGiveClassInfoQry.queryUserClassBySn(data.getString("SERIAL_NUMBER", ""));
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx23 " + userclassdata);
		return userclassdata;
	}

	public IDataset queryUserClassDetailBySn(IData data) throws Exception {

		IDataset userclassdetaildata = UserGiveClassInfoQry.queryUserClassDetailBySn(data.getString("SERIAL_NUMBER", ""));
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx37 " + userclassdetaildata);
		return userclassdetaildata;
	}

	public IDataset checkAddMeb(IData data) throws Exception {
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx42 " + data);

		// 主号码
		IDataset rds = new DatasetList();
		String mainSn = data.getString("SERIAL_NUMBER");
		// 副号码
		String addsn = data.getString("SERIAL_NUMBER_B");

		// 新增的成员号码不能和主卡号码一致,请确认！
		if (mainSn.equals(addsn)) {
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "新增成员号码不能与主卡号码一致！");
			rds.add(data);
			return rds;
		}

		IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
		if (IDataUtil.isEmpty(mainUser)) {
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "该服务号码" + mainSn + "用户信息不存在！");
			rds.add(data);
			return rds;
		}

		// String userId = mainUser.getString("USER_ID");
		// 查询成员信息
		IData adduser = UcaInfoQry.qryUserInfoBySn(addsn);
		if (IDataUtil.isEmpty(adduser)) {
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "该服务号" + addsn + "用户信息不存在！");
			rds.add(data);
			return rds;
		}

		// 判断是否未激活
		if (!"0".equals(adduser.getString("ACCT_TAG"))) {
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "未激活用户不能参与该业务！");
			rds.add(data);
			return rds;
		}

		IDataset userclassdetaildata = UserGiveClassInfoQry.queryUserClassDetailByGiveSn(addsn);
		if (IDataUtil.isNotEmpty(userclassdetaildata)) {
			data.put("X_RESULTCODE", "-1");
			// 1个副号在明细表里只能有1条有效记录，只能被一个主号赠送；不能是多个主号赠送同一个副号
			String deatailsn = userclassdetaildata.getData(0).getString("SERIAL_NUMBER", "").trim();
			if (!mainSn.equals(deatailsn)) {
				// 说明已被其他主号赠送过。不能再次赠送
				data.put("X_RESULTINFO", "号码【" + addsn + "】已被其他号码【" + deatailsn + "】赠送过，不能再次赠送!");
			} else {
				data.put("X_RESULTINFO", "号码【" + addsn + "】已被赠送过，不能再次赠送!");
			}

			rds.add(data);
			return rds;
		}

		IData input = new DataMap();
		input.put("SERIAL_NUMBER", addsn);
		IData userclassdata = UserClassInfoQry.queryUserClassBySN(input);
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx103 " + userclassdata);

		if (IDataUtil.isNotEmpty(userclassdata)) {
			int olduserclass = userclassdata.getInt("USER_CLASS");
			int giveuserclass = data.getInt("USER_CLASS");
			logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx108 " + giveuserclass+"\t"+olduserclass);

			/*
			 * USER_INFO_CLASS6:全球通体验用户, USER_INFO_CLASS5:终身全球通用户,
			 * USER_INFO_CLASS4:全球通钻石卡（非终身全球通用户）, USER_INFO_CLASS3:全球通白金卡,
			 * USER_INFO_CLASS2:全球通金卡, USER_INFO_CLASS1:全球通银卡,
			 * 
			 * 6最小，54321从大到小。
			 */
			if (olduserclass != giveuserclass) {
				if (olduserclass == 6 || giveuserclass > olduserclass) {

				} else {
					logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx121 " + giveuserclass+"\t"+olduserclass);
					data.put("X_RESULTCODE", "-1");
					data.put("X_RESULTINFO", "号码【" + addsn + "】已有高于当前赠送的全球通标识，不须赠送!");
					rds.add(data);
					return rds;
				}
			}
		}

		rds.add(data);
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx131 " + rds);

		return rds;
	}

	public IDataset submitData(IData data) throws Exception {
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx100 " + data);
		// [2019-07-11 16:45:31,709] [pool-1-app-6] (GiveUserClassBean.java:105)
		// DEBUG SUPERUSR GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx100
		// {"p":"giveuserclass.GiveUserClass","SERIAL_NUMBER":"13976108478","ROUTE_EPARCHY_CODE":"0898","LOGIN_LOG_ID":"",
		// "MEB_LIST":"[{\"SERIAL_NUMBER\":\"14798851104\",\"TAG\":\"0\"},{\"SERIAL_NUMBER\":\"18808904403\",\"TAG\":\"0\"}]",
		// "service":"ajax","listener":"submitData","page":"giveuserclass.GiveUserClass","TRADE_EPARCHY_CODE":"0898","m":"crm9369","REMARK":"使对方的身份的"}

		// 主号码
		IDataset rds = new DatasetList();
		// IData cond = new DataMap();
		String mainSn = data.getString("SERIAL_NUMBER", "").trim();
		IData redata = new DataMap();
		redata.put("X_RESULTCODE", "0000");
		redata.put("X_RESULTINFO", "信息处理成功！");

		IDataset userclassdatas = UserGiveClassInfoQry.queryUserClassBySn(mainSn);
		IData maindata = null;
		String tradeidmainsn = "";
		if (IDataUtil.isEmpty(userclassdatas)) {
			redata.put("X_RESULTCODE", "2999");
			redata.put("X_RESULTINFO", "号码：" + mainSn + "不能办理赠送全球通权益！");
			rds.add(redata);
			return rds;
		} else {
			maindata = userclassdatas.getData(0);
			tradeidmainsn = userclassdatas.getData(0).getString("TRADEID", "").trim();
			 
		}

		// 本次操作前的数量
		IDataset beforeuserclassdetaildata = UserGiveClassInfoQry.queryUserClassDetailBySn(mainSn);
		int beforecount = beforeuserclassdetaildata.size();

		int opercount = 0;

		IDataset mebList = new DatasetList(data.getString("MEB_LIST", "[]"));
		List<String> list = new ArrayList<String>();

		if (IDataUtil.isNotEmpty(mebList)) {
			for (int i = 0, size = mebList.size(); i < size; i++) {
				IData meb = mebList.getData(i);
				// 副号码
				String addsn = meb.getString("SERIAL_NUMBER", "").trim();
				// 一次不能操作相同号码
				if (list.contains(addsn)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "不能提交重复的号码！");
				} else {
					list.add(addsn);
				}

				String TAG = meb.getString("TAG");

				// 新增的成员号码不能和主卡号码一致,请确认！
				if (mainSn.equals(addsn)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "新增号码不能与主号码【" + mainSn + "】相同！");

					/*
					 * redata.put("X_RESULTCODE", "-1");
					 * redata.put("X_RESULTINFO", "新增成员号码不能与主卡号码一致！");
					 * rds.add(redata); return rds;
					 */
				}

				IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
				if (IDataUtil.isEmpty(mainUser)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该服务号码" + mainSn + "用户信息不存在！");

					/*
					 * redata.put("X_RESULTCODE", "-1");
					 * redata.put("X_RESULTINFO", ); rds.add(redata); return
					 * rds;
					 */
				}

				// String userId = mainUser.getString("USER_ID");
				// 查询成员信息
				IData adduser = UcaInfoQry.qryUserInfoBySn(addsn);
				if (IDataUtil.isEmpty(adduser)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该服务号" + addsn + "用户信息不存在！");

					/*
					 * redata.put("X_RESULTCODE", "-1");
					 * redata.put("X_RESULTINFO", "该服务号" + addsn + "用户信息不存在！");
					 * rds.add(redata); return rds;
					 */
				}

				// 判断是否未激活
				if (!"0".equals(adduser.getString("ACCT_TAG"))) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "未激活用户不能参与该业务！");

					/*
					 * redata.put("X_RESULTCODE", "-1");
					 * redata.put("X_RESULTINFO", "未激活用户不能参与该业务！");
					 * rds.add(redata); return rds;
					 */
				}

				UcaData mebUca = UcaDataFactory.getNormalUca(addsn);

				if ("0".equals(TAG)) {

					IDataset userclassdetaildata = UserGiveClassInfoQry.queryUserClassDetailByGiveSn(addsn);
					if (IDataUtil.isNotEmpty(userclassdetaildata)) {
						String deatailsn = userclassdetaildata.getData(0).getString("SERIAL_NUMBER", "").trim();
						if (!mainSn.equals(deatailsn)) {
							// 说明已被其他主号赠送过。不能再次赠送
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【" + addsn + "】已被其他号码【" + deatailsn + "】赠送过，不能再次赠送!");
						} else {
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【" + addsn + "】已被赠送过，不能再次赠送!");
						}

					} else {


						
						// 新插入记录 tl_b_user_give_class_detail 和
						// tf_f_user_info_class
						IData insertparam = new DataMap();
						insertparam.put("FROMTRADEID", tradeidmainsn);
						insertparam.put("USER_ID", maindata.getString("USER_ID"));
						insertparam.put("SERIAL_NUMBER", maindata.getString("SERIAL_NUMBER"));
						insertparam.put("GIVE_USER_ID", mebUca.getUserId());
						insertparam.put("GIVE_SERIAL_NUMBER", mebUca.getSerialNumber());
						insertparam.put("USER_CLASS", maindata.getString("USER_CLASS"));
						insertparam.put("END_DATE", maindata.getString("END_DATE"));
						// insertparam.put("REMARK", data.getString("REMARK"));
						insert_TL_B_USER_GIVE_CLASS_DETAIL(insertparam);
						opercount = opercount + 1;

						insertparam.put("REMARK", addsn+"赠送全球通标识");
						insertparam.put("START_DATE", SysDateMgr.getSysTime());
						insertparam.put("RSRV_STR1", "4");
						dealAddUserClassForAll(insertparam);
					}
				} else if ("1".equals(TAG)) {
					// 删除
					IDataset userclassdetaildata = UserGiveClassInfoQry.queryUserClassDetailByGiveSn(addsn);
					if (IDataUtil.isNotEmpty(userclassdetaildata)) {
						// 1个副号在明细表里只能有1条有效记录，只能被一个主号赠送；不能是多个主号赠送同一个副号
						String deatailsn = userclassdetaildata.getData(0).getString("SERIAL_NUMBER", "").trim();
						if (!mainSn.equals(deatailsn)) {
							// 说明已被其他主号赠送过。不能再次赠送
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【" + addsn + "】被其他号码【" + deatailsn + "】赠送，不能删除!");
						} else {
							enddate_TL_B_USER_GIVE_CLASS_DETAIL(mebUca.getSerialNumber(), SysDateMgr.getSysTime());
							enddate_TF_F_USER_INFO_CLASS(mebUca.getUserId(), mebUca.getSerialNumber(), SysDateMgr.getSysTime());
							opercount = opercount - 1;
						}
					}
				}
			}
		}

		int maxnum = maindata.getInt("TOTAL_NUM", 0);
		if ((beforecount + opercount) > maxnum) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务处理失败！赠送数量已超过最大数量！");
		} else {
			// 更新已用量和可用量
			IData insertparam = new DataMap();
			insertparam.put("SERIAL_NUMBER", mainSn);
			insertparam.put("USED_NUM", (beforecount + opercount));
			insertparam.put("UNUSED_NUM", String.valueOf(maxnum - (beforecount + opercount)));
			logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx253 " + insertparam);
			Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS", "UPD_NUM", insertparam);
		}

		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx257 " + rds);

		rds.add(redata);
		return rds;
	}

	private void insert_TL_B_USER_GIVE_CLASS_DETAIL(IData param) throws Exception {

		String fromtradeId = param.getString("FROMTRADEID");
		String userId = param.getString("USER_ID", "");
		String serialNum = param.getString("SERIAL_NUMBER", "");
		String giveUserId = param.getString("GIVE_USER_ID", "");
		String giveSerialNum = param.getString("GIVE_SERIAL_NUMBER", "");
		String userclass = param.getString("USER_CLASS");
		String endDate = param.getString("END_DATE");
		String remark = param.getString("REMARK");

		if (1 == 1) {
			// 判断被赠送号码是否已存在全球通标识，如存在，并且本次即将赠送的等级高于原等级，则截止原记录，新插记录；如小于等于原等级，则跳过不处理。
			IData input = new DataMap();
			input.put("SERIAL_NUMBER", giveSerialNum);
			IData userclassdata = UserClassInfoQry.queryUserClassBySN(input);
			if (IDataUtil.isNotEmpty(userclassdata)) {
				int olduserclass = userclassdata.getInt("USER_CLASS");
				int giveuserclass = Integer.parseInt(userclass);

				/*
				 * USER_INFO_CLASS6:全球通体验用户,
				 * USER_INFO_CLASS5:终身全球通用户,
				 * USER_INFO_CLASS4:全球通钻石卡（非终身全球通用户）,
				 * USER_INFO_CLASS3:全球通白金卡,
				 * USER_INFO_CLASS2:全球通金卡,
				 * USER_INFO_CLASS1:全球通银卡,
				 *
				 * 6最小，54321从大到小。
				 */
//								2、转赠给已是钻石卡权益的客户，接口也返回添加成功，并且BOSS可以查到添加记录。
				if (olduserclass != giveuserclass) {
					if (olduserclass == 6 || giveuserclass > olduserclass) {
						enddate_TF_F_USER_INFO_CLASS(giveUserId, giveSerialNum, endDate);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【" + giveSerialNum + "】已有高于当前赠送的全球通标识，不须赠送!");

					}
				}else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码已有当前赠送的全球通标识，不须赠送!");
				}
			}
		}

		IData inData = new DataMap();
		inData.put("TRADE_ID", "");
		inData.put("USER_CLASS", userclass);
		inData.put("USER_ID", userId);
		inData.put("SERIAL_NUMBER", serialNum);
		inData.put("GIVE_USER_ID", giveUserId);
		inData.put("GIVE_SERIAL_NUMBER", giveSerialNum);
		inData.put("FROMTRADEID", fromtradeId);
		inData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		inData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		inData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		inData.put("UPDATE_CITY_CODE", getVisit().getCityCode());
		inData.put("START_DATE", SysDateMgr.getSysTime());
		inData.put("END_DATE", endDate);
		inData.put("REMARK", remark);
		logger.debug("GiveUserClassBean277xxxxxxxxxxxxxxxxxxxxxxxx " + inData);

		Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS_DETAIL", "INSERT_GIVE_CLASS_DETAIL_ALL", inData);
	}

	private void insert_TF_F_USER_INFO_CLASS(IData param) throws Exception {
		/*
		 * USER_CLASS 1 全球通银卡 2 全球通金卡 3 全球通白金卡 4 全球通钻石卡（非终身全球通用户） 5 终身全球通用户 6
		 * 全球通体验用户
		 */
		String giveUserId = param.getString("GIVE_USER_ID", "");
		String giveSerialNum = param.getString("GIVE_SERIAL_NUMBER", "");
		String userclass = param.getString("USER_CLASS");
		String endDate = param.getString("END_DATE");

		IData inData = new DataMap();
		inData.put("USER_ID", giveUserId);
		inData.put("SERIAL_NUMBER", giveSerialNum);
		IDataset custInfos = CustomerInfoQry.getCustInfoPsptBySn(giveSerialNum);
		String birthday = new String();
		if(IDataUtil.isNotEmpty(custInfos)){
			IData custInfo = custInfos.getData(0);
			String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE");
			if ("0".equals(psptTypeCode) || "1".equals(psptTypeCode) || "2".equals(psptTypeCode)) {
				String idCard = custInfo.getString("PSPT_ID");
				birthday = idCard.substring(6, 14);
			}
		}
		inData.put("USER_CLASS", userclass);
		inData.put("BIRTHDAY", birthday);
		inData.put("IN_DATE", SysDateMgr.getSysTime());
		inData.put("START_DATE", param.getString("START_DATE"));
		inData.put("END_DATE", endDate);
        inData.put("RSRV_STR1", param.getString("RSRV_STR1"));
		inData.put("RSRV_STR3", param.getString("RSRV_STR3"));
		inData.put("REMARK", param.getString("REMARK"));
		logger.debug("GiveUserClassBeanxxxxxxxxxxxxxxxxxxxxxxxx320 " + inData);

		Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "INSERT_INFO_CLASS_ALL", inData);
	}

	private void enddate_TL_B_USER_GIVE_CLASS_DETAIL(String giveserialNum, String endDate) throws Exception {
		IData input = new DataMap();
		input.put("GIVE_SERIAL_NUMBER", giveserialNum);
		input.put("END_DATE", endDate);// 
		logger.debug("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx329 " + input);
		Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS_DETAIL", "UPD_END_DATE", input);
	}

	private void enddate_TF_F_USER_INFO_CLASS(String userId, String serialNum, String endDate) throws Exception {
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", serialNum);
		IData userclassdata = UserClassInfoQry.queryUserClassBySN(input);
		logger.debug("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx337 " + userclassdata);

		if (IDataUtil.isNotEmpty(userclassdata)) {
			input.clear();
			input.put("SERIAL_NUMBER", serialNum);
			input.put("USER_ID", userId);
			input.put("END_DATE", endDate);// 已存在全球通标识的截止时间为新套餐生效时间
			logger.debug("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx344 " + input);

			Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPD_END_DATE_1", input);
		}
	}

	/**
	 * 给客户新增全球通标识。
	 * 如果满足条件，就新增全球通标识，同时终止老的全球通标识，将老的全球通标识保存至预留字段RSRV_DATE1中。
	 * @tanzheng
	 * @20190826
	 */
	public void dealAddUserClassForAll(IData inParam) throws Exception {
		logger.debug("dealAddUserClassForAll inParam:"+inParam.toString());
		String serialNum = inParam.getString("GIVE_SERIAL_NUMBER");
		String userId = inParam.getString("GIVE_USER_ID");
		int classLevel = inParam.getInt("USER_CLASS");//需要新增的用户等级
		int ownLevel = 0;//用户现有等级
		IData input = new DataMap();
		input.put("USER_ID", userId);
		IDataset dataset = UserClassInfoQry.queryUserClass(input);
		IData userclassdata = new DataMap();
		if(IDataUtil.isNotEmpty(dataset)){
			userclassdata = dataset.first();
			ownLevel = userclassdata.getInt("USER_CLASS");
			userclassdata.put("END_DATE",inParam.getString("START_DATE"));
			userclassdata.put("REMARK",inParam.getString("REMARK")+"，终止老的全球通标识");
		}
		if(ownLevel==6 || classLevel >= ownLevel){
			//1、终止用户老的全球通权益。
			if(IDataUtil.isNotEmpty(userclassdata)){
				Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPDATE_USER_CLASS_END_BAK", userclassdata);
			}
			//2、新增新的全球通权益。
			insert_TF_F_USER_INFO_CLASS(inParam);
		}


	}

	/**
	 * 新增用户级别
	 * @param userId
	 * @param serialNum
	 * @param startDate
	 * @param endDate
	 * @param userClass
	 * @param rsrvStr3
	 * @param rsrvStr1

	 * @throws Exception
	 */
	public void addClassInfo(String userId, String serialNum, String startDate, String endDate, String userClass, String rsrvStr3, String rsrvStr1) throws Exception {
		IData param = new DataMap();
		param.put("GIVE_USER_ID", userId);
		param.put("GIVE_SERIAL_NUMBER", serialNum);
		param.put("USER_CLASS", userClass);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("REMARK", "办理【"+rsrvStr3+"】，获赠全球通权益");
		param.put("RSRV_STR1", rsrvStr1);//办理套餐赠送
		param.put("RSRV_STR3", rsrvStr3);//办理套餐赠送
		dealAddUserClassForAll(param);
	}

	public void delClassInfo(String userId, String serialNum,
							 String endDate, String userClass,  String rsrvStr1, String remark) throws Exception {
		IData input = new DataMap();
		input.put("USER_ID", userId);
		input.put("RSRV_STR1", rsrvStr1);
		IDataset dataset = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_RSRVSTR1", input);

		if (IDataUtil.isNotEmpty(dataset) ) {// 如果是全球通标识客户
				// 当前截止，截止时间是新产品生效时间startDate
				IData inData = new DataMap();
				inData.put("USER_ID", userId);
				inData.put("SERIAL_NUMBER", serialNum);
				inData.put("END_DATE", endDate==null?SysDateMgr.getSysTime():endDate);// 已存在全球通标识的截止时间为新套餐生效时间
				inData.put("REMARK", remark+",终止用户等级");
				inData.put("USER_CLASS", userClass);
				inData.put("RSRV_STR1", rsrvStr1);
				Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPD_END_DATE_NEW", inData);

				input.put("USER_ID", userId);
				IDataset invaildUserClass = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_NEW", input);
				if(IDataUtil.isNotEmpty(invaildUserClass) && invaildUserClass.size()>0)
				{
					IData updateData = invaildUserClass.first();
					updateData.put("REMARK", remark+",恢复用户等级");
					Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPDATE_BAK_TO_VAILD", updateData);
				}

		}
	}
}
