package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.FtpUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.ChangePhonePreRegisterBean;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class RemoteDestroyUserBean extends CSBizBean {

	/**
	 * 漫游省发起查询卡类型
	 * 
	 * @param input
	 * @throws Exception
	 */
	public IDataset queryCardType(IData input) throws Exception {
		input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
		input.put("ID_TYPE", "01");
		input.put("BIZ_TYPE", input.getString("BIZ_TYPE"));// 跨区密码重置
		// 补充操作流水号
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		input.put("OPR_NUMB", "COP" + "898" + date + seqRealId);
		input.put("BIZ_VERSION", "1.0.0");
		IData result = new DataMap();
		IData iResult = IBossCall.queryCardTypeInfo(input);
		if (IDataUtil.isNotEmpty(iResult)) {
			if ("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))) {
				result.putAll(iResult.getDataset("CARD_INFO").getData(0));
				result.put("BIZ_ORDER_RESULT", iResult.getString("BIZ_ORDER_RESULT"));
				result.put("RESULT_DESC", iResult.getString("RESULT_DESC", ""));
			} else {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,
						"查询卡类型失败：" + iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC")));
			}
		} else {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询卡类型出错，归属省未返回原因！");
		}
		IDataset results = new DatasetList();
		results.add(result);
		return results;
	}

	/**
	 * 鉴权发起
	 * 
	 * @param input
	 * @throws Exception
	 */
	public IDataset openResultAuthF(IData input) throws Exception {
		IData param = new DataMap();
		// 补充操作流水号
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		param.put("OPR_NUMB", "COP" + "898" + date + seqRealId);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", input.getString("MOBILENUM"));
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));// 跨区密码重置
		param.put("ID_CARD_TYPE", "00");// 身份证
		param.put("ID_CARD_NUM", input.getString("PSPT_ID"));
		param.put("USER_NAME", input.getString("CUST_NAME"));
		param.put("BIZ_VERSION", "1.0.2");
		param.put("NUMBER_CHECK", input.getString("NUMBER_CHECK", ""));
		param.put("CCPASSWD", input.getString("PASSWORD", ""));
		IData result = new DataMap();
		result.put("RESULT", "0");
		// iboss
		IData iResult = IBossCall.userAuth(param);
		if (IDataUtil.isNotEmpty(iResult)) {
			if ("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))) {
				String identCode = iResult.getString("IDENT_CODE");
				result.put("RESULT", "1");
				IData userInfo = iResult.getDataset("USER_INFO").getData(0);
				String custName = userInfo.getString("USER_NAME", "");
				String level = userInfo.getString("LEVEL", "");
				String brand = userInfo.getString("BRAND", "");
				String userState = userInfo.getString("USER_STATUS", "");
				result.put("CUST_NAME", custName);
				result.put("USER_STATE", userState);
				result.put("BRAND_CODE", brand);
				result.put("LEVEL", level);
				result.put("PSPT_TYPE_CODE", input.getString("IDCARDTYPE", ""));
				result.put("IDCARDNUM", input.getString("IDCARDNUM", ""));
				result.put("IDENT_CODE", identCode);
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if (IDataUtil.isNotEmpty(blResultInfos)) {
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					String reason = blResultInfo.getString("REASON");
					result.put("BUS_STATE", busState);
					result.put("REASON", reason);
				} else {
					result.put("BUS_STATE", "0");
					result.put("REASON",
							"鉴权失败，归属省未按规范正常返回！" + iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", "")));
				}
			} else {
				result.put("RESULT", "0");
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if (IDataUtil.isNotEmpty(blResultInfos)) {
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					String reason = blResultInfo.getString("REASON");
					if (StringUtils.isBlank(reason)) {
						reason = iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", ""));
					}
					result.put("BUS_STATE", busState);
					result.put("REASON", reason);
				} else {
					result.put("BUS_STATE", "0");
					result.put("REASON",
							"鉴权失败，归属省返回：" + iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", "")));
				}
			}
		} else {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "鉴权出错！归属省未返回");
		}
		IDataset results = new DatasetList();
		results.add(result);
		return results;
	}

	/**
	 * 前台提交销户申请 保存到派单表中 并生成一笔主台账
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset applySubmitCancel(IData input) throws Exception {
		IDataset retnDataSet = new DatasetList();
		IData param = new DataMap();
		param.put("REMOTE_ORDER_ID", input.getString("REMOTE_ORDER_ID"));
		param.put("TRADE_ID", input.getString("TRADE_ID"));
		param.put("REMOTE_SERIAL_NUMBER", input.getString("MOBILENUM"));
		param.put("REMOTE_CUST_NAME", input.getString("CUST_NAME"));
		param.put("GIFT_SERIAL_NUMBER", input.getString("GIFT_SERIAL_NUMBER"));
		param.put("GIFT_CUST_NAME", input.getString("GIFT_CUST_NAME"));
		param.put("GIFT_SERIAL_NUMBER_B", input.getString("GIFT_SERIAL_NUMBER_B"));
		param.put("GIFT_CUST_NAME_B", input.getString("GIFT_CUST_NAME_B"));
		param.put("CONTACT_PHONE", input.getString("CONTACT_PHONE"));
		param.put("CONTACT_NAME", input.getString("CONTACT_NAME"));
		String sysDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String oprNumb = input.getString("REMOTE_ORDER_ID");
		String homeProv = input.getString("HOME_PROV");
		param.put("IDEN_HEAD", "BOSS898_PICTURE_" + sysDate + "_" + oprNumb + "_BOSS" + homeProv + "_Z.jpg");
		param.put("IDEN_BACK", "BOSS898_PICTURE_" + sysDate + "_" + oprNumb + "_BOSS" + homeProv + "_F.jpg");
		param.put("PIC_NAME_R", "BOSS898_PICTURE_" + sysDate + "_" + oprNumb + "_BOSS" + homeProv + "_R.jpg");
		param.put("PIC_CNOTE", "BOSS898_elctricOrder_" + sysDate + "_" + oprNumb + "_BOSS" + homeProv + ".pdf");
		param.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("CREATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		param.put("CREATE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		param.put("CREATE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		param.put("CREATE_PHONE", input.getString("CREATE_PHONE"));
		param.put("CREATE_CONTACT", input.getString("CREATE_CONTACT"));
		param.put("CREATE_ORG_NAME", input.getString("CREATE_ORG_NAME"));
		param.put("CREATE_DATE", SysDateMgr.getSysTime());
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		param.put("DEAL_TAG", "0");
		param.put("DEAL_INFO", "未派单");
		param.put("RSRV_STR2", input.getString("IDENT_CODE"));
		// param.put("TRANSACTION_ID",
		// input.getString("IDENT_CODE"));//派单时实际取的是RSRV_STR2
		param.put("REMARKS", input.getString("REMARKS"));
		boolean ins = RemoteDestroyUserDao.insertApplyDestroyUserTrade(param);
		return retnDataSet;
	}

	/**
	 * k3 插入身份证正反面
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset insPsptFrontBack(IData input) throws Exception {
		IDataset retnDataSet = new DatasetList();
		String tradeId = input.getString("TRADE_ID");
		String acceptMonth = tradeId.substring(4, 6);
		int intMonth = Integer.parseInt(acceptMonth);
		IData param = new DataMap();
		param.put("TRADE_ID", input.getString("TRADE_ID"));
		param.put("SERIAL_NUMBER", input.getString("MOBILENUM"));
		param.put("UPDATE_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		param.put("ACCEPT_MONTH", intMonth);
		param.put("HEAD_BASE64", input.getString("HEADBASE64"));
		param.put("BACK_BASE64", input.getString("BACKBASE64"));
		param.put("FRONT_BASE64", input.getString("FRONTBASE64"));
		param.put("CUST_NAME", input.getString("CUST_NAME"));
		param.put("PSPT", input.getString("IDCARDNUM"));
		param.put("SEX", input.getString("CARD_SEX"));
		param.put("ADDRESS", input.getString("CARD_ADDRESS"));
		param.put("BORN", input.getString("CARD_BORN"));
		param.put("EFF_DATE", input.getString("EFF_DATE"));
		param.put("ISSUED", input.getString("CARD_ISSUED"));
		param.put("REMARK", "身份证正反面");
		boolean ins = RemoteDestroyUserDao.insertPsptPic(param);
		return retnDataSet;
	}

	/**
	 * 查询待派单工单
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryDestroyOrder(IData input) throws Exception {
		input.put("REMOTE_SERIAL_NUMBER", IDataUtil.chkParam(input, "REMOTE_SERIAL_NUMBER"));
		// input.put("DEAL_TAG", "0");
		IDataset destroyOrders = RemoteDestroyUserDao.queryApplyDestroyUserTrade(input);
		// if(IDataUtil.isEmpty(destroyOrders)){
		// CSAppException.apperr(CrmCommException.CRM_COMM_103, "无数据！");
		// }
		return destroyOrders;
	}

	/**
	 * 查询接收到的工单
	 */
	public IDataset queryReceiptOrder(IData input) throws Exception {
		IDataset paramSet = CommparaInfoQry.getCommNetInfo("CSM", "2001", "KQXH_FTP_DIR");
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		inparam.put("ORDER_ID", input.getString("ORDER_ID"));
		inparam.put("DEAL_TAG", input.getString("DEAL_TAG"));

		// IData destroyOrder =
		// RemoteDestroyUserDao.queryReceiveDestroyUserTrade(inparam).first();
		IDataset destroyOrders = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(inparam);
		if (IDataUtil.isEmpty(destroyOrders)) {
			return null;
		}
		IData destroyOrder = destroyOrders.first();
		String lateTime = destroyOrder.getString("RSRV_STR6");
		if(StringUtils.isNotBlank(lateTime)){
			 lateTime = SysDateMgr.decodeTimestamp(lateTime, SysDateMgr.PATTERN_STAND_YYYYMMDD);
			 destroyOrder.put("LATE_TIME", lateTime);
		}else{
			destroyOrder.put("LATE_TIME", "");
		}
		
		String staffName="";
		String staffTel="";
		//查询受理员工名称
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TD_M_STAFF T WHERE T.STAFF_ID=:STAFF_ID ");
		IData staffParam = new DataMap();
		staffParam.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		IDataset staffDatas=Dao.qryBySql(sql, staffParam,Route.CONN_SYS); 
		if(staffDatas!=null&&staffDatas.size()>0){
			staffName=staffDatas.getData(0).getString("STAFF_NAME");
			staffTel=staffDatas.getData(0).getString("SERIAL_NUMBER");
		}
		destroyOrder.put("DESTROY_DEAL_USER",staffName);
		destroyOrder.put("DESTROY_DEALUSER_TEL", staffTel);
		// 查询图片
		IData path4Photo = new DataMap();
		path4Photo.put("FTP_SITE", paramSet.getData(0).getString("PARA_CODE1"));
		path4Photo.put("FTP_SUB_FOLDER", paramSet.getData(0).getString("PARA_CODE2"));

		/*-------------取图片 start------------*/
		/**
		 * 根据文件名将图片base64串存入result
		 */
		try {
			IData picMap = new DataMap();
			picMap.put("IDEN_HEAD", destroyOrder.getString("IDEN_HEAD"));
			picMap.put("IDEN_BACK", destroyOrder.getString("IDEN_BACK"));
			picMap.put("PIC_NAME_R", destroyOrder.getString("PIC_NAME_R"));
			picMap.put("PIC_CNOTE", destroyOrder.getString("PIC_CNOTE"));

			IData picResult = showImage(picMap, path4Photo);

			destroyOrder.put("IDEN_HEAD_BASE", picResult.getString("IDEN_HEAD_BASE"));
			destroyOrder.put("IDEN_BACK_BASE", picResult.getString("IDEN_BACK_BASE"));
			destroyOrder.put("PIC_NAME_R_BASE", picResult.getString("PIC_NAME_R_BASE"));
			destroyOrder.put("PIC_CNOTE_BASE", picResult.getString("PIC_CNOTE_BASE"));
			destroyOrder.put("PIC_CODE", "0");
		} catch (Exception e) {
			destroyOrder.put("PIC_CODE", "-1");
		}
		/*-------------取图片 end ------------*/
		IDataset resultDatas = new DatasetList();
		resultDatas.add(destroyOrder);

		if (IDataUtil.isEmpty(resultDatas)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_301, "无数据！");
		}

		return resultDatas;
	}

	public IDataset updateReceiveDestroyUserTrade(IData input) throws Exception {
		IDataset resultSet = new DatasetList();
		IData resultData = new DataMap();
		boolean result = RemoteDestroyUserDao.updateReceiveDestroyUserTrade(input);
		if (result) {
			resultData.put("FLAG", "1");
		} else {
			resultData.put("FLAG", "0");
		}
		resultSet.add(resultData);
		return resultSet;
	}

	/**
	 * 漫游省发起接口：派单
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset applyCancelAccount(IData input) throws Exception {
		
		IData inparam = new DataMap();
		inparam.put("ID_TYPE", "01");
		inparam.put("ID_VALUE", input.getString("REMOTE_SERIAL_NUMBER"));
		inparam.put("OPR_CODE", "29");
		inparam.put("BIZ_VERSION", "1.0.0");
		inparam.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		IData param = new DataMap();
		param.put("REMOTE_ORDER_ID", input.getString("REMOTE_ORDER_ID"));
		IDataset cancelOrder = RemoteDestroyUserDao.queryApplyDestroyUserTradeByOrderId(param);
		if (IDataUtil.isEmpty(cancelOrder)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,
					"查询单号" + input.getString("REMOTE_ORDER_ID") + "无待派单销户工单数据！");
		}
		if (!"0".equals(cancelOrder.getData(0).getString("DEAL_TAG"))
				&& !"2".equals(cancelOrder.getData(0).getString("DEAL_TAG"))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "此工单已经发起过派单，请不要重复派单！");
		}
		String remoteOrderId = cancelOrder.getData(0).getString("REMOTE_ORDER_ID");
		inparam.put("ID_VALUE_NAME", cancelOrder.getData(0).getString("REMOTE_CUST_NAME"));
		inparam.put("OPR_NUMB", remoteOrderId);
		inparam.put("IDENT_CODE", cancelOrder.getData(0).getString("RSRV_STR2"));
		inparam.put("OR_OPR_NUMB", remoteOrderId);
		String lateTime = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(7), SysDateMgr.PATTERN_TIME_YYYYMMDD);
		inparam.put("LATE_TIME", lateTime);// 销户最迟完成时间
		inparam.put("XH_MARK", "1");
		if ("2".equals(cancelOrder.getData(0).getString("DEAL_TAG"))) {
			inparam.put("XH_MARK", "2");
			addOprNumb(inparam);
		}
		// 拼UD1参数
		IData userDestroy = new DataMap();
		userDestroy.put("JD_HALL", cancelOrder.getData(0).getString("CREATE_ORG_NAME"));// 建单营业厅名称
		userDestroy.put("HALL_NAME", cancelOrder.getData(0).getString("CREATE_CONTACT"));// 建单人姓名
		userDestroy.put("HALL_TEL", cancelOrder.getData(0).getString("CREATE_PHONE"));// 建单人联系方式
		userDestroy.put("CONTACT_NAME", cancelOrder.getData(0).getString("CONTACT_NAME"));// 跨区销户联系人
		userDestroy.put("CONTACT_TEL", cancelOrder.getData(0).getString("CONTACT_PHONE"));// 跨区销户联系号码
		userDestroy.put("MONEY_TEL", cancelOrder.getData(0).getString("GIFT_SERIAL_NUMBER"));// 跨区销户转账用户号码（现金）
		userDestroy.put("MONEY_TEL_NAME", cancelOrder.getData(0).getString("GIFT_CUST_NAME"));
		userDestroy.put("PAY_TEL", cancelOrder.getData(0).getString("GIFT_SERIAL_NUMBER_B"));
		userDestroy.put("PAY_TEL_NAME", cancelOrder.getData(0).getString("GIFT_CUST_NAME_B"));
		userDestroy.put("CREDIT_PICTURE", cancelOrder.getData(0).getString("IDEN_HEAD"));
		userDestroy.put("LIST_PICTURE", cancelOrder.getData(0).getString("IDEN_BACK"));
		userDestroy.put("PICTURE_T", cancelOrder.getData(0).getString("PIC_NAME_R"));
		userDestroy.put("ELCTRIC_ORDER", cancelOrder.getData(0).getString("PIC_CNOTE"));
		userDestroy.put("CHANNEL", "01");
		userDestroy.put("TIP_INFO", cancelOrder.getData(0).getString("REMARKS", ""));
		IDataset userDataset = new DatasetList();
		userDataset.add(userDestroy);
		inparam.put("UD1", userDataset);
		IDataset retnDataSet = new DatasetList();
		IData res = new DataMap();
		// 拼销户请求参数
		IDataset cardTypeResults = IBossCall.applyCancelAccount(inparam);
		if (IDataUtil.isEmpty(cardTypeResults)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调IBOSS返回为空！");
		} else {
			IData cardTypeResult = cardTypeResults.getData(0);
			if (IDataUtil.isNotEmpty(cardTypeResult)) {

				if ("0000".equals(cardTypeResult.getString("BIZ_ORDER_RESULT", ""))
						|| "0000".equals(cardTypeResult.getString("X_RSPCODE", ""))) {
					// 更新派单表状态为已派单
					IData data = new DataMap();
					data.put("REMOTE_SERIAL_NUMBER", input.getString("REMOTE_SERIAL_NUMBER"));
					data.put("REMOTE_ORDER_ID", cancelOrder.getData(0).getString("REMOTE_ORDER_ID"));
					data.put("DEAL_TAG", "1");
					data.put("DEAL_INFO", "已派单");
					//data.put("RSRV_STR1", cardTypeResult.getString("FINAL_TIME", lateTime));// 归属省销户最迟完成时间
					data.put("RSRV_STR1", lateTime);//销户最迟完成时间
					data.put("FINISH_TIME", SysDateMgr.getSysTime());
					data.put("UPDATE_TIME", SysDateMgr.getSysTime());
					RemoteDestroyUserDao.updateApplyDestroyUserTrade(data);
					res.put("IS_SUCCESS", "1");
					retnDataSet.add(res);
				} else {
					
					res.put("IS_SUCCESS", "0");
					res.put("RESULT_DESC",
							cardTypeResult.getString("RESULT_DESC", cardTypeResult.getString("X_RSPDESC")));
					retnDataSet.add(res);
					// 更新派单表状态为已派单
					/*
					 * IData data = new DataMap();
					 * data.put("REMOTE_SERIAL_NUMBER",
					 * input.getString("REMOTE_SERIAL_NUMBER"));
					 * data.put("REMOTE_ORDER_ID",
					 * cancelOrder.getData(0).getString("REMOTE_ORDER_ID"));
					 * data.put("DEAL_TAG", "6"); data.put("DEAL_INFO",
					 * cardTypeResult.getString("RESULT_DESC",
					 * cardTypeResult.getString("X_RSPDESC")));
					 * data.put("FINISH_TIME", SysDateMgr.getSysTime());
					 * data.put("UPDATE_TIME", SysDateMgr.getSysTime());
					 * RemoteDestroyUserDao.updateApplyDestroyUserTrade(data);
					 */
					CSAppException.apperr(CrmCommException.CRM_COMM_103,
							"派单失败：" + cardTypeResult.getString("RESULT_DESC", cardTypeResult.getString("X_RSPDESC")));
				}
			} else {
				res.put("IS_SUCCESS", "0");
				res.put("RESULT_DESC", "未返回任何信息！");
				retnDataSet.add(res);
				// 更新派单表状态为已派单
				/*
				 * IData data = new DataMap(); data.put("REMOTE_SERIAL_NUMBER",
				 * input.getString("REMOTE_SERIAL_NUMBER"));
				 * data.put("REMOTE_ORDER_ID",
				 * cancelOrder.getData(0).getString("REMOTE_ORDER_ID"));
				 * data.put("DEAL_TAG", "6"); data.put("DEAL_INFO", "派单异常");
				 * data.put("FINISH_TIME", SysDateMgr.getSysTime());
				 * data.put("UPDATE_TIME", SysDateMgr.getSysTime());
				 * RemoteDestroyUserDao.updateApplyDestroyUserTrade(data);
				 */
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "派单失败，归属省未返回任何信息");
			}
		}

		return retnDataSet;
	}

	/**
	 * 销户请求落地接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset cancelAccount(IData input) throws Exception {
		IDataset retnDataSet = new DatasetList();
		IData result = new DataMap();
		result.put("BIZ_ORDER_RESULT", "0000");
		result.put("X_RSPCODE", "0000");
		result.put("X_RSPTYPE", "0");
		result.put("X_RSPDESC", "ok");
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "ok");
		result.put("OPR_NUMB", input.getString("OPR_NUMB"));
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		String serialNO = "";
		String XHMark = "";// 1：首次过程工单 2：非首次过程工单
		String orOprNumb = "";//用户销户唯一性标识
		String inLateTime = "";//漫游省要求的最迟销户时间
		try {
			serialNO = IDataUtil.chkParam(input, "ID_VALUE");
			XHMark = IDataUtil.chkParam(input, "XH_MARK");
			orOprNumb = IDataUtil.chkParam(input, "OR_OPR_NUMB");
			inLateTime = IDataUtil.chkParam(input, "LATE_TIME");
		} catch (Exception e) {
			result.put("BIZ_ORDER_RESULT", "3029");
			result.put("RESULT_DESC", e.getMessage());
			result.put("X_RESULTCODE", "3029");
			result.put("X_RESULTINFO", e.getMessage());
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "3029");
			result.put("X_RSPDESC", e.getMessage());
			retnDataSet.add(result);
			return retnDataSet;
		}
		IDataset userList = input.getDataset("UD_1");
		IData user = userList.getData(0);
		String contact_tel = user.getString("CONTACT_TEL");
		String money_tel = user.getString("MONEY_TEL");
		String pay_tel = user.getString("PAY_TEL");
		if (StringUtils.equals(serialNO, contact_tel)) {
			result.put("BIZ_ORDER_RESULT", "4050");
			result.put("RESULT_DESC", "联系人电话不能与销户号码相同");
			result.put("X_RESULTCODE", "4050");
			result.put("X_RESULTINFO", "联系人电话不能与销户号码相同");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "4050");
			result.put("X_RSPDESC", "联系人电话不能与销户号码相同");
			retnDataSet.add(result);
			return retnDataSet;
		} else if (StringUtils.equals(serialNO, money_tel) || StringUtils.equals(serialNO, pay_tel)) {
			result.put("BIZ_ORDER_RESULT", "4049");
			result.put("RESULT_DESC", "转费号码不能与销户号码相同");
			result.put("X_RESULTCODE", "4049");
			result.put("X_RESULTINFO", "转费号码不能与销户号码相同");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "4049");
			result.put("X_RSPDESC", "转费号码不能与销户号码相同");
			retnDataSet.add(result);
			return retnDataSet;
		}
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNO);
		inparam.put("CUST_NAME", input.getString("ID_VALUE_NAME"));
		if (!"2".equals(XHMark)) {
			IDataset userAuth = UserIdentInfoQry.queryIdentInfoByCode(input.getString("IDENT_CODE"),
					input.getString("ID_VALUE"));
			boolean valid = false;
			if (IDataUtil.isNotEmpty(userAuth)) {
				if ("VALID".equals(userAuth.getData(0).getString("TAG"))) {
					valid = true;
				}
			} else {
				result.put("BIZ_ORDER_RESULT", "3018");
				result.put("RESULT_DESC", "用户身份凭证错误");
				result.put("X_RESULTCODE", "3018");
				result.put("X_RESULTINFO", "用户身份凭证错误");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "3018");
				result.put("X_RSPDESC", "用户身份凭证错误");
				retnDataSet.add(result);
				return retnDataSet;
			}
			if (!valid) {
				result.put("X_RESULTCODE", "3018");
				result.put("X_RESULTINFO", "用户身份凭证已过期/失效");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户身份凭证已过期/失效");
				result.put("BIZ_ORDER_RESULT", "3018");
				result.put("RESULT_DESC", "用户身份凭证已过期/失效");
				retnDataSet.add(result);
				return retnDataSet;
			}
		} else {

			IData xHMarkParam = new DataMap();
			xHMarkParam.put("SERIAL_NUMBER", serialNO);
			xHMarkParam.put("ORDER_ID", orOprNumb);
			xHMarkParam.put("DEAL_TAG", "3");
			IDataset destroyOrders = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(xHMarkParam);
			if (IDataUtil.isEmpty(destroyOrders)) {
				result.put("BIZ_ORDER_RESULT", "3029");
				result.put("RESULT_DESC", "用户销户唯一性标识或销户号码错误");
				result.put("X_RESULTCODE", "3029");
				result.put("X_RESULTINFO", "用户销户唯一性标识或销户号码错误");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "3029");
				result.put("X_RSPDESC", "用户销户唯一性标识或销户号码错误");
			} else {
				IData xHMarkParam2 = new DataMap();
				xHMarkParam2.put("ORDER_ID", orOprNumb);
				xHMarkParam2.put("DEAL_TAG", "0");
				xHMarkParam2.put("DEAL_INFO", "补充材料");
				xHMarkParam2.put("UPDATE_TIME", SysDateMgr.getSysTime());
				xHMarkParam2.put("RSRV_STR1", "补充材料");
				xHMarkParam2.put("RSRV_STR6", inLateTime);//漫游省要求销户最迟完成时间
				updateReceiveDestroyUserTrade(xHMarkParam2);
				String lateTime2 = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(7), SysDateMgr.PATTERN_CHINA_DATE);
				String lateTimeInfo="您的手机号码满足归属省相关销户条件要求，归属省预计在"+lateTime2+"前完成销户，如您手机账户中还有剩余的预存款，也会按您填写的要求进行处理";
				result.put("FINAL_TIME", lateTimeInfo);// 销户最迟完成时间
			}
			retnDataSet.add(result);
			return retnDataSet;
		}

		// 插入接单表
		String sysTime = SysDateMgr.getSysTime();

		inparam.put("ORDER_ID", input.getString("OPR_NUMB"));
		inparam.put("GIFT_SERIAL_NUMBER", user.getString("MONEY_TEL"));
		inparam.put("GIFT_CUST_NAME", user.getString("MONEY_TEL_NAME"));
		inparam.put("GIFT_SERIAL_NUMBER_B", user.getString("PAY_TEL"));
		inparam.put("GIFT_CUST_NAME_B", user.getString("PAY_TEL_NAME"));
		inparam.put("CONTACT_PHONE", user.getString("CONTACT_TEL"));
		inparam.put("CONTACT_NAME", user.getString("CONTACT_NAME"));
		inparam.put("CREATE_DATE", sysTime);
		inparam.put("CREATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		inparam.put("CREATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
		inparam.put("CREATE_EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE"));
		inparam.put("CREATE_CITY_CODE", input.getString("TRADE_CITY_CODE"));
		inparam.put("CREATE_PHONE", user.getString("HALL_TEL"));
		inparam.put("CREATE_CONTACT", user.getString("JD_HALL"));
		inparam.put("DEAL_TAG", "0");// 未处理
		inparam.put("DEAL_INFO", "未处理");
		inparam.put("ACCOUNT_TAG", "0");// 未处理
		inparam.put("FINISH_TIME", sysTime);
		inparam.put("UPDATE_TIME", sysTime);
		inparam.put("IDEN_HEAD", user.getString("CREDIT_PICTURE"));
		inparam.put("IDEN_BACK", user.getString("LIST_PICTURE"));
		inparam.put("PIC_NAME_R", user.getString("PICTURE_T"));
		inparam.put("PIC_CNOTE", user.getString("ELCTRIC_ORDER"));
		inparam.put("RSRV_STR1", user.getString("TIP_INFO"));
		inparam.put("RSRV_STR6", inLateTime);//漫游省要求销户最迟完成时间
		if (input.getString("USERPARTYID").startsWith("COP") && input.getString("USERPARTYID").length() == 7) {
			String temp = input.getString("USERPARTYID").substring(3);// 去前三位
			temp = temp.substring(0, temp.length() - 1);// 去最后一位
			inparam.put("RSRV_STR2", temp);// 省代码
		} else {
			result.put("BIZ_ORDER_RESULT", "3006");
			result.put("RESULT_DESC", "USERPARTYID格式不合规范！");
			result.put("X_RSPCODE", "2998");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPDESC", "失败");
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "USERPARTYID格式不合规范！");
			retnDataSet.add(result);
			return retnDataSet;
		}

		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", "192");// 立即销户
		data.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
		data.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

		boolean isSuc = false;
		try {
			IDataset dataset = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", data);
			if (IDataUtil.isNotEmpty(dataset)) {
				IData temp = dataset.getData(0);
				if (IDataUtil.isNotEmpty(data) && StringUtils.isNotEmpty(temp.getString("TRADE_ID"))) {
					isSuc = true;
				}
			}
			if (isSuc) {
				result.put("BIZ_ORDER_RESULT", "0000");
				result.put("X_RSPCODE", "0000");
				result.put("X_RSPTYPE", "0");
				result.put("X_RSPDESC", "ok");
				result.put("X_RESULTCODE", "0");
				result.put("X_RESULTINFO", "ok");
				String lateTime2 = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(7), SysDateMgr.PATTERN_CHINA_DATE);
				String lateTimeInfo="您的手机号码满足归属省相关销户条件要求，归属省预计在"+lateTime2+"前完成销户，如您手机账户中还有剩余的预存款，也会按您填写的要求进行处理";
				result.put("FINAL_TIME", lateTimeInfo);// 销户最迟完成时间
				inparam.put("DEAL_INFO", "归属省鉴权成功");
				IData qryUserInfoBySn = UcaInfoQry.qryUserInfoBySn(contact_tel);
				if(IDataUtil.isNotEmpty(qryUserInfoBySn)){
					IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "REMOTE_DESTROY_REPLACE_SMS", "0898");
					IData templateConfig = config.getData(0);
					String templateId=templateConfig.getString("PARA_CODE1");//销户成功受理短信模板
					//根据模板ID获取短信
					String content="";
					IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
					if(IDataUtil.isNotEmpty(smsTemplateData)){
						content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
					}
					content=content.replaceAll("code1", serialNO);
					IData smsParam = new DataMap();
					smsParam.put("SERIAL_NUMBER", contact_tel);
					smsParam.put("NOTICE_CONTENT", content);
					sendSms(smsParam);
				}else{//联系号码为外省号码时 调iboss下发短信
					IData remoteParam = new DataMap();
					remoteParam.put("CONTACT_TEL", contact_tel);
					remoteParam.put("BIZ_TYPE", "1013");
					remoteParam.put("MESS_TYPE", "1");
					IDataset messChanges = new DatasetList();
					IData messChange = new DataMap();
					messChange.put("INFO_CODE", "code1");
					messChange.put("INFO_VALUE", serialNO);//销户号码
					messChanges.add(messChange);
					remoteParam.put("MESS_CHANGE", messChanges);
					simpleCardNotice(remoteParam);
					
				}
			} else {
				result.put("BIZ_ORDER_RESULT", "3006");
				result.put("RESULT_DESC", "号码存在销号业务办理条件的限制，不允许销户！");
				result.put("X_RSPCODE", "3006");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPDESC", "失败");
				result.put("X_RESULTCODE", "3006");
				result.put("X_RESULTINFO", "号码存在销号业务办理条件的限制，不允许销户！");
				inparam.put("DEAL_TAG", "2");// 处理失败
				inparam.put("DEAL_INFO", "号码存在销号业务办理条件的限制，不允许销户");
			}
		} catch (Exception e) {
			result.put("BIZ_ORDER_RESULT", "3006");
			result.put("RESULT_DESC", e.getMessage());
			result.put("X_RSPCODE", "3006");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPDESC", "失败");
			result.put("X_RESULTCODE", "3006");
			result.put("X_RESULTINFO", e.getMessage());
			inparam.put("DEAL_TAG", "2");// 处理失败
			String errorInfo = "";
			if (e.getMessage().length() > 3000) {
				errorInfo = e.getMessage().substring(0, 2999);
			} else {
				errorInfo = e.getMessage();
			}
			inparam.put("DEAL_INFO", "接单失败：" + errorInfo);
		} finally {
			boolean ins = RemoteDestroyUserDao.insertReceiveDestroyUserTrade(inparam);
			retnDataSet.add(result);
			return retnDataSet;
		}
	}

	/**
	 * 归属省销户结果同步落地
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset cancelAccountSyn(IData input) throws Exception {
		IDataset retnDataSet = new DatasetList();
		IData result = new DataMap();
		System.out.println("============销户结果同步落地入参=====input======" + input.toString());
		try {
			String bizOrderResult = IDataUtil.chkParam(input, "BIZ_ORDER_RESULT");
			String checkResult = IDataUtil.chkParam(input, "CHECK_RESULT");// 工单处理结果
																			// 0：销户成功
																			// 1：用户原因销户失败
																			// 2：销户请求报文填写错误
																			// 3：销户附件缺失
																			// 4：销户附件不可用
			String XHGDstatus = IDataUtil.chkParam(input, "XHGD_STATUS");// 销户工单状态
																			// 0：完结
																			// 1：在途
			String tipInfo = IDataUtil.chkParam(input, "TIP_INFO");
			IData inparam = new DataMap();
			inparam.put("REMOTE_SERIAL_NUMBER", input.getString("ID_VALUE"));
			inparam.put("REMOTE_ORDER_ID", input.getString("OPR_NUMB"));
			inparam.put("RSRV_STR3", checkResult);// 工单处理结果 0：销户成功 1：用户原因销户失败
													// 2：销户请求报文填写错误 3：销户附件缺失
													// 4：销户附件不可用
			inparam.put("RSRV_STR4", XHGDstatus);// 销户工单状态 0：完结 1：在途
			inparam.put("DEAL_INFO", tipInfo);
			inparam.put("UPDATE_TIME", SysDateMgr.getSysTime());
			if ("0".equals(XHGDstatus)) {
				inparam.put("DEAL_TAG", "9");// 归档
			} else {
				String returnTime = IDataUtil.chkParam(input, "RETURN_TIME");
				inparam.put("DEAL_TAG", "2");// 在途
				inparam.put("RSRV_STR5", returnTime);// 补传材料时间
			}
			if ("0000".equals(bizOrderResult) && "0".equals(checkResult)) {
				IDataset cancelUsers = input.getDataset("UD_1");
				IData ud1Info = cancelUsers.getData(0);
				String XHperson = ud1Info.getString("XH_PERSON");// 归属省的销户处理人
				String XHtel = ud1Info.getString("XH_TEL");// 归属省的销户处理人联系方式
				inparam.put("RSRV_STR6", XHperson);// 归属省的销户处理人
				inparam.put("RSRV_STR7", XHtel);// 归属省的销户处理人联系方式
			} else if ("3".equals(checkResult) || "4".equals(checkResult)) {
				inparam.put("RSRV_STR10", "A");
			}

			result.put("OPR_NUMB", input.getString("OPR_NUMB"));
			result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

			RemoteDestroyUserDao.updateApplyDestroyUserTrade(inparam);
			result.put("BIZ_ORDER_RESULT", "0000");
			result.put("X_RSPCODE", "0000");
			result.put("X_RSPTYPE", "0");
			result.put("X_RSPDESC", "ok");
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "ok");
			retnDataSet.add(result);
			return retnDataSet;

		} catch (Exception e) {
			result.put("BIZ_ORDER_RESULT", "3006");
			result.put("RESULT_DESC", e.getMessage());
			result.put("X_RSPCODE", "3006");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPDESC", e.getMessage());
			result.put("X_RESULTCODE", "3006");
			result.put("X_RESULTINFO", e.getMessage());
			retnDataSet.add(result);
			return retnDataSet;
		}
	}

	/**
	 * 销户结果同步归属省发起
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset applyCancelAccountSyn(IData input) throws Exception {
		IDataset retnDataSet = new DatasetList();
		String bizOrderResult = input.getString("BIZ_ORDER_RESULT");
		IDataset cancelOrder = input.getDataset("CANCEL_ORDER");
		String serialNumber = cancelOrder.getData(0).getString("SERIAL_NUMBER");
		String contactPhone = cancelOrder.getData(0).getString("CONTACT_PHONE");
		String auditResult = input.getString("AUDIT_RESULT");
		String checkResult = input.getString("CHECK_RESULT");
		String transMoney = input.getString("TRANS_MONEY");
		String transPay = input.getString("TRANS_PAY");
		// 单位由元（小数点后保留角分）转为分，去掉小数点
		int strSize = transMoney.length();
		transMoney = transMoney.substring(0, strSize - 3) + transMoney.substring(strSize - 2);
		strSize = transPay.length();
		transPay = transPay.substring(0, strSize - 3) + transPay.substring(strSize - 2);
		IData result = new DataMap();
		result.put("ID_TYPE", "01");
		result.put("ID_VALUE", serialNumber);
		result.put("OPR_NUMB", cancelOrder.getData(0).getString("ORDER_ID"));
		result.put("BIZ_VERSION", "1.0.0");
		result.put("BIZ_ORDER_RESULT", bizOrderResult);
		result.put("TIP_INFO", input.getString("TIP_INFO"));
		result.put("CHECK_RESULT", input.getString("CHECK_RESULT"));
		if ("2".equals(checkResult) || "3".equals(checkResult) || "4".equals(checkResult)) {
			result.put("XHGD_STATUS", "1");// 销户工单状态 0:完结 1:在途
			String returnTime = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(2), SysDateMgr.PATTERN_STAND_SHORT);
			result.put("RETURN_TIME", returnTime);
		}
		IDataset CancelUser = new DatasetList();
		IData userDestroy = new DataMap();
		if ("1".equals(auditResult)) {
			// 审核成功时拼UD1参数
			userDestroy.put("ID_TYPE", "01");
			userDestroy.put("ID_VALUE", serialNumber);
			userDestroy.put("CANCEL_EF_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 销户生效时间
			userDestroy.put("CANCEL_OPR", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 销户操作时间
			userDestroy.put("TRANS_MONEY", Integer.parseInt(transMoney) * 10);// IBOSS接口要求单位为厘
			userDestroy.put("TRANS_PAY", Integer.parseInt(transPay) * 10);
			userDestroy.put("MONEY_TEL", cancelOrder.getData(0).getString("GIFT_SERIAL_NUMBER"));
			userDestroy.put("MONEY_TEL_NAME", cancelOrder.getData(0).getString("GIFT_CUST_NAME"));
			userDestroy.put("PAY_TEL", cancelOrder.getData(0).getString("GIFT_SERIAL_NUMBER_B"));
			userDestroy.put("PAY_TEL_NAME", cancelOrder.getData(0).getString("GIFT_CUST_NAME_B"));
			userDestroy.put("XH_PERSON", input.getString("XH_PERSON"));
			userDestroy.put("XH_TEL", input.getString("XH_TEL"));
			if(StringUtils.isBlank(input.getString("XH_PERSON"))||StringUtils.isBlank(input.getString("XH_TEL"))){
				IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "DESTROY_DEALUSER_INFO", "0898");
				IData dealInfo = config.getData(0);
				String dealName = dealInfo.getString("PARA_CODE1");
				String dealTel = dealInfo.getString("PARA_CODE2");
				userDestroy.put("XH_PERSON", dealName);
				userDestroy.put("XH_TEL", dealTel);
			}
			
			CancelUser.add(userDestroy);
			result.put("UD1", CancelUser);
			result.put("CHECK_RESULT", "0");// 工单处理结果 0：销户成功 1：用户原因销户失败
											// 2：销户请求报文填写错误 3：销户附件缺失 4：销户附件不可用
			result.put("XHGD_STATUS", "0");// 销户工单状态 0:完结 1:在途
		} else if ("1".equals(checkResult)) {
			result.put("XHGD_STATUS", "0");// 销户工单状态 0:完结 1:在途
		}
		result.put("ROUTEVALUE", cancelOrder.getData(0).getString("RSRV_STR2"));// 省代码
		// 向IBOSS同步
		IDataset ibossResults = IBossCall.applyCancelAccountSyn(result);
		if (IDataUtil.isEmpty(ibossResults)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调IBOSS返回为空！");
		} else {
			IData ibossResult = ibossResults.getData(0);
			if (!"0000".equals(ibossResult.getString("X_RSPCODE", ""))) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "调IBOSS失败！" + ibossResult.getString("X_RSPDESC"));
			}
		}
		if ("0".equals(auditResult) && "1".equals(checkResult)) {
			if (StringUtils.isNotBlank(contactPhone)){
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
				if(IDataUtil.isNotEmpty(userInfo)){
					IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "REMOTE_DESTROY_REPLACE_SMS", "0898");
					IData templateConfig = config.getData(0);
					String templateId=templateConfig.getString("PARA_CODE2");//用户原因销户失败短信模板
					//根据模板ID获取短信
					String content="";
					IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
					if(IDataUtil.isNotEmpty(smsTemplateData)){
						content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
					}
					content=content.replaceAll("code1", serialNumber);
					IData contactsms = new DataMap();
					contactsms.put("SERIAL_NUMBER", contactPhone);
					contactsms.put("NOTICE_CONTENT",content);
					sendSms(contactsms);
				}else{//联系人是外省号码时 调IBOSS下发短信
					IData remoteParam = new DataMap();
					remoteParam.put("CONTACT_TEL", contactPhone);
					remoteParam.put("BIZ_TYPE", "1013");
					remoteParam.put("MESS_TYPE", "2");
					IDataset messChanges = new DatasetList();
					IData messChange = new DataMap();
					messChange.put("INFO_CODE", "code1");
					messChange.put("INFO_VALUE", serialNumber);//销户号码
					messChanges.add(messChange);
					remoteParam.put("MESS_CHANGE", messChanges);
					simpleCardNotice(remoteParam);
				}
				
			}
		}
		result.put("TRANS_MONEY_FEN", transMoney);
		result.put("TRANS_PAY_FEN", transPay);
		retnDataSet.add(result);
		return retnDataSet;
	}

	public IDataset queryTransferInfo(IData input) throws Exception {
		String serialNumber = input.getString("SERIAL_NUMBER");
		String outerTradeId = input.getString("OUTER_TRADE_ID");
		return AcctCall.queryTransferInfo(serialNumber, outerTradeId);
	}

	public IDataset checkSerialNumberProv(IData input) throws Exception {
		IDataset resultSet = new DatasetList();
		IData resultData = new DataMap();
		ChangePhonePreRegisterBean bean = BeanManager.createBean(ChangePhonePreRegisterBean.class);
		bean.getSnRoute(input, input);
		String flag = input.getString("FLAG");
		resultData.put("FLAG", flag);
		if ("C".equals(flag)) {// 如果为外省号码，则调用卡类型查询接口
			input.put("BIZ_TYPE", "1013");// 现金费用转移
			IData cardTypeInfo = queryCardType(input).getData(0);
			String BizOrderResult = cardTypeInfo.getString("BIZ_ORDER_RESULT");
			if (!"0000".equals(BizOrderResult)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, cardTypeInfo.getString("RESULT_DESC"));
			}
			String name = input.getString("NAME");
			String lastCharOfName = name.substring(name.length() - 1);
			String userName = cardTypeInfo.getString("USER_NAME");
			String lastCharOfUserName = userName.substring(userName.length() - 1);
			if (!StringUtils.equals(lastCharOfName, lastCharOfUserName)) {// IBOSS只提供姓名的最后一个字
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "所填姓名与号码认证姓名不符！");
			}
			String homeProv = cardTypeInfo.getString("HOME_PROV");
			IData homeProvData = StaticInfoQry.getStaticInfoByTypeIdDataId("PROV_CODE", homeProv);
			String homeProvName = homeProvData.getString("DATA_NAME");
			resultData.put("HOME_PROV_NAME", homeProvName);
		} else {// 如果为本省号码，则查询用户信息
			IData inparam = new DataMap();
			inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
			IData custInfo = CustomerInfoQry.getCustInfoByAllSn(inparam, null).first();
			String custName = custInfo.getString("CUST_NAME");
			String name = input.getString("NAME");
			if (!StringUtils.equals(name, custName)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "所填姓名与号码认证姓名不符！");
			}
		}
		resultSet.add(resultData);
		return resultSet;
	}

	public IDataset queryReceiveDestroyOrderHis(IData input, Pagination pagination) throws Exception {
		if ((!(input.getString("START_TIME").isEmpty()) && input.getString("END_TIME").isEmpty())
				|| (input.getString("START_TIME").isEmpty() && !(input.getString("END_TIME").isEmpty()))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_301, "起始时间和结束时间必须同为空或同为非空！");
		}
		if (!(input.getString("START_TIME").isEmpty()) && !input.getString("END_TIME").isEmpty()) {
			IDataset paramset = ParamInfoQry.getCommparaByCode("CSM", "2001", "KQXH", "ZZZZ");

			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTime = input.getString("START_TIME");
			String endTime = input.getString("END_TIME");

			// 计算天数
			long start = simpleFormat.parse(startTime).getTime();
			long end = simpleFormat.parse(endTime).getTime();
			int days = (int) ((end - start) / (1000 * 60 * 60 * 24));

			int limit = Integer.parseInt(paramset.getData(0).getString("PARA_CODE1"));

			// 限制查询时长
			if (days > limit) {
				CSAppException.apperr(CrmCommException.CRM_COMM_301, "查询时长不能超过" + limit + "天！");
			}
		}
		String deal_tag = input.getString("DEAL_TAG", "");
		if (StringUtils.isNotBlank(input.getString("cond_SERIAL_NUMBER", ""))
				&& ("0".equals(input.getString("DEAL_TAG", "")) || "1".equals(input.getString("DEAL_TAG", "")))) {
			input.put("DEAL_TAG", "9");
			IDataset destroyOrder = RemoteDestroyUserDao.queryReceiveDestroyOrderHis(input, pagination);
			if (IDataUtil.isNotEmpty(destroyOrder)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已销户！");
			} else {
				input.put("DEAL_TAG", deal_tag);
			}
		}
		IDataset destroyOrders = RemoteDestroyUserDao.queryReceiveDestroyOrderHis(input, pagination);
		if (IDataUtil.isEmpty(destroyOrders)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "无数据！");
		}
		return destroyOrders;
	}

	/**
	 * 获取图片
	 *
	 * @param picMap
	 * @return
	 * @throws Exception
	 */
	public IData showImage(IData picMap, IData path4Photo) throws Exception {
		if (IDataUtil.isEmpty(picMap)) {
			return new DataMap();
		}

		// 等现场进行路径配置
		String ftpSite = path4Photo.getString("FTP_SITE");

		/**
		 * 子路径为root_path后的路径,注意格式,等现场确认具体文件夹路径配置后,拆分文件名或其他数据进行配置
		 * 建议将一笔业务的图片放在一个子文件夹内
		 */
		String ftpSubFolder = path4Photo.getString("FTP_SUB_FOLDER");

		IData imgBase64 = readFtpImage(picMap, ftpSubFolder, ftpSite);
		return imgBase64;
	}

	public IData readFtpImage(IData picMap, String ftpSubFolder, String ftpSite) throws Exception {
		FtpFileAction ftpFileAction = new FtpFileAction();

		IData config = ftpFileAction.getFtpConfig(ftpSite, getVisit());
		String ibossIP = config.getString("FTP_URL");
		String ibossUser = config.getString("ACCT_USR");
		String ibossPassword = config.getString("ACCT_PWD");
		String rootPath = config.getString("ROOT_PATH");

		// jar包里的util没有completePendingCommand方法,无法一次读取多个文件,所以用自己配置的,注意引用路径
		FtpUtil ftpUtil = new FtpUtil(ibossIP, ibossUser, ibossPassword, rootPath);
		ftpUtil.setFileType(ftpUtil.FILE_TYPE_BINARY);
		try {
			ftpUtil.changeDirectory(rootPath);
			ftpUtil.changeDirectory(ftpSubFolder);
		} catch (Exception e) {
			// 获取图片服务器存储路径失败
		}

		IData result = new DataMap();
		List<String> listFiles = ftpUtil.getFileList(".");
		if (!listFiles.isEmpty()) {
			String[] picNames = picMap.getNames();
			for (int i = 0; i < picMap.size(); i++) {
				String picName = picNames[i];
				String imgName = picMap.getString(picName);
				for (String fileName : listFiles) {
					if (fileName.equals(imgName)) {
						InputStream file = ftpUtil.getFileStream(fileName);
						if (file != null) {
							String imgBase64Src = "";
							imgBase64Src = getfileBase64(file);
							String imgBase64 = imgBase64Src;
							result.put(picName + "_BASE", imgBase64);
							file.close();
							ftpUtil.completePendingCommand();
						}
						break;
					}
				}
			}
		}
		return result;
	}

	public String getfileBase64(InputStream input) throws Exception {
		byte[] bytes = null;
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];// buff用于存放循环读取的临时数据
			int rc = 0;
			while ((rc = input.read(buff, 0, 1024)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			bytes = swapStream.toByteArray();
			swapStream.close();
			input.close();
			input = null;
			swapStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (swapStream != null) {
				try {
					swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String imgStr = new BASE64Encoder().encode(bytes);
		return imgStr;
	}

	/**
	 * 短信发送
	 *
	 * @param input
	 * @throws Exception
	 */
	private void sendSms(IData input) throws Exception {
		IData sendInfo = new DataMap();
		sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(input.getString("SERIAL_NUMBER")));
		sendInfo.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
		sendInfo.put("RECV_ID", input.getString("SERIAL_NUMBER"));
		sendInfo.put("SMS_PRIORITY", "50");
		sendInfo.put("NOTICE_CONTENT", input.getString("NOTICE_CONTENT"));
		sendInfo.put("REMARK", "跨区销户归属省受理");
		sendInfo.put("FORCE_OBJECT", "10086");
		SmsSend.insSms(sendInfo, RouteInfoQry.getEparchyCodeBySn(input.getString("SERIAL_NUMBER")));
	}

	/**
	 * K3 查询已审核的工单
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryAuditedOrder(IData input) throws Exception {
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		inparam.put("ORDER_ID", input.getString("ORDER_ID"));
		inparam.put("DEAL_TAG", input.getString("DEAL_TAG"));
		IDataset destroyOrders = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(inparam);
		if (IDataUtil.isEmpty(destroyOrders)) {
			return null;
		}
		return destroyOrders;
	}

	/**
	 * k3 查询当天收到的工单
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset qryNowDayOrder(IData param) throws Exception {

		IDataset NowDayOrder = RemoteDestroyUserDao.qryNowDayOrder(param);
		if (IDataUtil.isNotEmpty(NowDayOrder)) {
			return NowDayOrder;
		}
		return null;
	}

	/**
	 * k3 自动销户失败更新接单表
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset updateDestroyFail(IData param) throws Exception {
		IDataset resultSet = new DatasetList();
		IData resultData = new DataMap();
		boolean result = RemoteDestroyUserDao.updateDestroyFail(param);
		if (result) {
			resultData.put("FLAG", "1");
		} else {
			resultData.put("FLAG", "0");
		}
		resultSet.add(resultData);
		return resultSet;
	}

	public IDataset queryApplyDestroyUserTradeByOrderId(IData param) throws Exception {
		return RemoteDestroyUserDao.queryApplyDestroyUserTradeByOrderId(param);
	}

	/**
	 * 销户前校验 发起
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset destroyCheck(IData input) throws Exception {
		IData param = new DataMap();
		// 补充操作流水号
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqRealId = SeqMgr.getRealId();
		param.put("OPR_NUMB", "COP" + "898" + date + seqRealId);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", input.getString("MOBILENUM"));
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));// 跨区销户校验
		param.put("BIZ_VERSION", "1.0.2");
		IData result = new DataMap();
		result.put("RESULT", "0");
		// iboss
		IData iResult = IBossCall.userAuth(param);
		if (IDataUtil.isNotEmpty(iResult)) {
			if ("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))) {
				String identCode = iResult.getString("IDENT_CODE");
				result.put("RESULT", "1");
				if(IDataUtil.isNotEmpty(iResult.getDataset("USER_INFO"))){
					IData userInfo = iResult.getDataset("USER_INFO").getData(0);
					String userState = userInfo.getString("USER_STATUS", "");
					result.put("USER_STATE", userState);
				}
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if (IDataUtil.isNotEmpty(blResultInfos)) {
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					String reason = blResultInfo.getString("REASON");
					result.put("BUS_STATE", busState);
					result.put("REASON", reason);
				} else {
					result.put("BUS_STATE", "0");
					result.put("REASON", "销户前校验失败，归属省未按规范正常返回！"
							+ iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", "")));
				}
			} else {
				result.put("RESULT", "0");
				IDataset blResultInfos = iResult.getDataset("BL_RESULT_INFO");
				if (IDataUtil.isNotEmpty(blResultInfos)) {
					IData blResultInfo = blResultInfos.getData(0);
					String busState = blResultInfo.getString("BUS_STATE");
					String reason = blResultInfo.getString("REASON");
					if (StringUtils.isBlank(reason)) {
						reason = iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", ""));
					}
					result.put("BUS_STATE", busState);
					result.put("REASON", reason);
				} else {
					result.put("BUS_STATE", "0");
					result.put("REASON",
							"销户前校验失败，归属省返回：" + iResult.getString("RESULT_DESC", iResult.getString("X_RSPDESC", "")));
				}
			}
		} else {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "销户前校验出错！归属省未返回");
		}
		IDataset results = new DatasetList();
		results.add(result);
		return results;
	}
	public IData addOprNumb(IData inparam) throws Exception
	 {
	     //补充操作流水号
		 String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		 String seqRealId = SeqMgr.getRealId();
		 inparam.put("OPR_NUMB", "COP"+"898"+ date + seqRealId);
	     return inparam;
	 }
	/**
	 * 跨区销户短信代发k3
	 * @param data
	 * @param staffEparchyCode
	 * @return 
	 * @throws Exception 
	 */
	public IDataset simpleCardNotice(IData remoteParam) throws Exception{
		IDataset rtn=new DatasetList();
		IData param = new DataMap();
		addOprNumb(param);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", remoteParam.getString("CONTACT_TEL"));//联系人手机号
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", remoteParam.getString("BIZ_TYPE"));//跨区销户1013
		param.put("SEND_MARK", "1");
		param.put("MESS_TYPE", remoteParam.getString("MESS_TYPE"));
		param.put("MESS_CHANGE", remoteParam.getDataset("MESS_CHANGE"));
		param.put("BIZ_VERSION", "1.0.0");
		IData result = new DataMap();
		result.put("RESULT", "0");
		//iboss 
		IData iResult = IBossCall.remoteSendSms(param);
		if(IDataUtil.isNotEmpty(iResult)){
			String rtnDesc = iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC"));
			if("0000".equals(iResult.getString("BIZ_ORDER_RESULT"))){
				result.put("RESULT", "1");
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区销户短信代发失败："+rtnDesc);
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区销户短信代发失败！");
		}
		rtn.add(result);
		return rtn;
	}
	/**
	 * 催单落地接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset busRemind(IData input)throws Exception{
		System.out.println("============销户催单落地入参=====input======" + input.toString());
		IDataset rtnList = new DatasetList();
		IData rtnData = new DataMap();
		try {
			IDataUtil.chkParam(input, "BIZ_VERSION");
			IDataUtil.chkParam(input, "BIZ_TYPE");
			IDataUtil.chkParam(input, "OPR_TIME");
			IDataUtil.chkParam(input, "ID_TYPE");
			String oprNumb = IDataUtil.chkParam(input, "OPR_NUMB");
			String serialNo = IDataUtil.chkParam(input, "ID_VALUE");
			String oprType = IDataUtil.chkParam(input, "OPR_TYPE");//1催单   2查询
			rtnData.put("BIZ_ORDER_RESULT", "0000");
			rtnData.put("X_RSPCODE", "0000");
			rtnData.put("X_RSPTYPE", "0");
			rtnData.put("X_RSPDESC", "ok");
			rtnData.put("X_RESULTCODE", "0000");
			rtnData.put("X_RESULTINFO", "ok");
			rtnData.put("RESULT_DESC", "成功");
			rtnData.put("OPR_NUMB", oprNumb);
			rtnData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			IDataset CDInfos = new DatasetList();
			IData CDInfo = new DataMap();
			IData qryParam = new DataMap();
			IData commpara = new DataMap();
			IDataset config = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "DESTROY_DEALUSER_INFO", "0898");
			IData dealInfo = config.getData(0);
			String dealName = dealInfo.getString("PARA_CODE1");
			String dealTel = dealInfo.getString("PARA_CODE2");
			if("2".equals(oprType)){//查询
				IDataset CXinfos = input.getDataset("CX_INFO");
				if(IDataUtil.isEmpty(CXinfos)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"CX_INFO不能为空！");
				}
				IData CXinfo = CXinfos.getData(0);
				String orderId = IDataUtil.chkParam(CXinfo, "OR_OPR_NUMB");
				qryParam.put("ORDER_ID",orderId);
				qryParam.put("SERIAL_NUMBER",serialNo);
				IDataset receiveDestroyInfo = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(qryParam);
				if(IDataUtil.isNotEmpty(receiveDestroyInfo)){
					IData destroyInfo = receiveDestroyInfo.getData(0);
					String dealTag = destroyInfo.getString("DEAL_TAG");
					CDInfo.put("CD_NAME", dealName);//销户处理人姓名
					CDInfo.put("CD_TEL", dealTel);//销户处理人电话
					if("0".equals(dealTag)){
						CDInfo.put("XH_STATUS", "待处理");
					}else{
						CDInfo.put("XH_STATUS", "已处理");
					}
					CDInfos.add(CDInfo);
					rtnData.put("CD_INFO", CDInfos);
				}else{
					rtnData.put("BIZ_ORDER_RESULT", "3063");
					rtnData.put("X_RSPCODE", "3063");
					rtnData.put("X_RSPTYPE", "2");
					rtnData.put("X_RSPDESC", "无数据");
					rtnData.put("X_RESULTCODE", "3063");
					rtnData.put("X_RESULTINFO", "无数据");
					rtnData.put("RESULT_DESC", "无数据");
				}
			}else if("1".equals(oprType)){//催单
				String cdreason = IDataUtil.chkParam(input, "CD_REASON");//催单原因
				IDataset XHInfos = input.getDataset("XH_INFO");
				if(IDataUtil.isEmpty(XHInfos)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"XH_INFO不能为空！");
				}
				IData XHInfo = XHInfos.getData(0);
				String ContactTel = XHInfo.getString("CONTACT_TEL");
				IData isMobile = MsisdnInfoQry.getMsisonBySerialnumberAsp(ContactTel,"1");
				if(IDataUtil.isEmpty(isMobile)){
		        	CSAppException.apperr(CrmUserException.CRM_USER_783, "联系人电话不是移动号码！");
				}
				String orderId = IDataUtil.chkParam(XHInfo, "OR_OPR_NUMB");
				qryParam.put("ORDER_ID",orderId);
				qryParam.put("SERIAL_NUMBER",serialNo);
				qryParam.put("DEAL_TAG","0");
				IDataset receiveDestroyInfo = RemoteDestroyUserDao.queryReceiveDestroyUserTrade(qryParam);
				if(IDataUtil.isNotEmpty(receiveDestroyInfo)){
					IData destroyInfo = receiveDestroyInfo.getData(0);
					String remindCount = destroyInfo.getString("RSRV_STR7");//催单次数
					IData updateParam = new DataMap();
					updateParam.put("ORDER_ID", orderId);
					updateParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
					updateParam.put("RSRV_STR4", cdreason);
					if(StringUtils.isBlank(remindCount)){
						updateParam.put("RSRV_STR7", "1");//催单次数
					}else{
						int intremindCount = Integer.parseInt(remindCount)+1;
						updateParam.put("RSRV_STR7", intremindCount+"");
					}
					updateReceiveDestroyUserTrade(updateParam);
					//给销户处理人发短信
					IDataset configList = CommparaInfoQry.getCommparaAllCol("CSM", "2020", "REMOTE_DESTROY_REPLACE_SMS", "0898");
					IData templateConfig = configList.getData(0);
					String templateId=templateConfig.getString("PARA_CODE4");//销户成功受理短信模板
					//根据模板ID获取短信
					String content="";
					IData smsTemplateData = TemplateQry.qryTemplateContentByTempateId(templateId);
					if(IDataUtil.isNotEmpty(smsTemplateData)){
						content = smsTemplateData.getString("TEMPLATE_CONTENT1","");
					}
					content=content.replaceAll("code1", serialNo);
					content=content.replaceAll("code2", orderId);
					IData smsParam = new DataMap();
					smsParam.put("SERIAL_NUMBER", dealTel);
					smsParam.put("NOTICE_CONTENT", content);
					sendSms(smsParam);
					CDInfo.put("CD_NAME", dealName);//销户处理人姓名
					CDInfo.put("CD_TEL", dealTel);//销户处理人电话
					CDInfo.put("XH_STATUS", "待处理");
					CDInfos.add(CDInfo);
					rtnData.put("CD_INFO", CDInfos);
					
				}else{
					rtnData.put("BIZ_ORDER_RESULT", "3063");
					rtnData.put("X_RSPCODE", "3063");
					rtnData.put("X_RSPTYPE", "2");
					rtnData.put("X_RSPDESC", "未查到催单数据");
					rtnData.put("X_RESULTCODE", "3063");
					rtnData.put("X_RESULTINFO", "未查到催单数据");
					rtnData.put("RESULT_DESC", "未查到催单数据");
				}
				
			}
			
		} catch (Exception e) {
			rtnData.put("BIZ_ORDER_RESULT", "3063");
			rtnData.put("X_RSPCODE", "3063");
			rtnData.put("X_RSPTYPE", "2");
			rtnData.put("X_RSPDESC", e.getMessage());
			rtnData.put("X_RESULTCODE", "3063");
			rtnData.put("X_RESULTINFO", e.getMessage());
			rtnData.put("RESULT_DESC", e.getMessage());
		}
		rtnList.add(rtnData);
		return rtnList;
	}

	/**
	 * 调iboss 催单与查询 发起
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryRemindOrder(IData input) throws Exception{
		IDataset rtnDataset=new DatasetList();
		IData rtnData = new DataMap();
		IData param = new DataMap();
		addOprNumb(param);
		param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		param.put("ID_VALUE", input.getString("REMOTE_SERIAL_NUMBER"));
		param.put("ID_TYPE", "01");
		param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));//跨区销户1013
		param.put("OPR_TYPE", input.getString("OPR_TYPE"));
		param.put("BIZ_VERSION", "1.0.0");
		IDataset CXAndXHInfos=new DatasetList();
		if("2".equals(input.getString("OPR_TYPE"))){
			IData CXInfo = new DataMap();
			CXInfo.put("OR_OPR_NUMB", input.getString("REMOTE_ORDER_ID"));
			CXInfo.put("GC_OPR_NUMB", input.getString("REMOTE_ORDER_ID"));
			CXInfo.put("ID_VALUE_NAME", input.getString("REMOTE_CUST_NAME"));
			CXAndXHInfos.add(CXInfo);
			param.put("CX_INFO", CXAndXHInfos);
		}else if("1".equals(input.getString("OPR_TYPE"))){
			IData qryparam = new DataMap();
			qryparam.put("REMOTE_ORDER_ID", input.getString("REMOTE_ORDER_ID"));
			IDataset cancelOrder = RemoteDestroyUserDao.queryApplyDestroyUserTradeByOrderId(qryparam);
			if (IDataUtil.isEmpty(cancelOrder)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,
						"催单单号" + input.getString("REMOTE_ORDER_ID") + "无催单销户工单数据！");
			}
			IData urgeInfo = cancelOrder.getData(0);
			if (!"1".equals(urgeInfo.getString("DEAL_TAG"))) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,
						"催单单号" + input.getString("REMOTE_ORDER_ID") + "无催单数据！");
			}
			IData XHInfo = new DataMap();
			XHInfo.put("OR_OPR_NUMB", input.getString("REMOTE_ORDER_ID"));
			XHInfo.put("GC_OPR_NUMB", input.getString("REMOTE_ORDER_ID"));
			XHInfo.put("ID_VALUE_NAME", urgeInfo.getString("REMOTE_CUST_NAME"));
			XHInfo.put("HALL_TEL", urgeInfo.getString("CREATE_PHONE"));
			String jdTime = SysDateMgr.decodeTimestamp(urgeInfo.getString("CREATE_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
			XHInfo.put("JD_TIME", jdTime);
			XHInfo.put("CONTACT_NAME", urgeInfo.getString("CONTACT_NAME"));
			XHInfo.put("CONTACT_TEL", urgeInfo.getString("CONTACT_PHONE"));
			String destroyLateTime = urgeInfo.getString("RSRV_STR1");
			if(StringUtils.isNotBlank(destroyLateTime)){
				destroyLateTime=SysDateMgr.decodeTimestamp(destroyLateTime, SysDateMgr.PATTERN_TIME_YYYYMMDD);
			}else{
				String createCate7 = SysDateMgr.addDays(urgeInfo.getString("UPDATE_TIME",urgeInfo.getString("CREATE_DATE")), 7);
				destroyLateTime=SysDateMgr.decodeTimestamp(createCate7, SysDateMgr.PATTERN_TIME_YYYYMMDD);
			}
			XHInfo.put("LATE_TIME", destroyLateTime);
			CXAndXHInfos.add(XHInfo);
			param.put("CD_REASON", input.getString("CD_REASON"));
			param.put("XH_INFO", CXAndXHInfos);
		}
		//iboss 
		IData iResult = IBossCall.queryRemindOrder(param);
		if("0000".equals(iResult.getString("BIZ_ORDER_RESULT",""))){
			IDataset ibossInfos = iResult.getDataset("CD_INFO");
			if(IDataUtil.isEmpty(ibossInfos)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"操作失败：归属省未返回CD_INFO字段");
			}
			IData ibossInfo = ibossInfos.getData(0);
			rtnData.put("BIZ_ORDER_RESULT", "0000");
			rtnData.put("CD_NAME", ibossInfo.getString("CD_NAME"));//工单当前环节处理人姓名
			rtnData.put("CD_TEL", ibossInfo.getString("CD_TEL"));//工单当前环节处理人联系方式
			rtnData.put("XH_STATUS", ibossInfo.getString("XH_STATUS"));//销户工单当前环节
			if("1".equals(input.getString("OPR_TYPE"))){
				IData updateData = new DataMap();
				updateData.put("REMOTE_SERIAL_NUMBER", input.getString("REMOTE_SERIAL_NUMBER"));
				updateData.put("REMOTE_ORDER_ID", input.getString("REMOTE_ORDER_ID"));
				updateData.put("DEAL_TAG", "1");
				updateData.put("RSRV_STR6", ibossInfo.getString("CD_NAME"));
				updateData.put("RSRV_STR7", ibossInfo.getString("CD_TEL"));
				updateData.put("RSRV_STR8", "1");//催单标志    1：已催单
				updateData.put("UPDATE_TIME", SysDateMgr.getSysTime());
				RemoteDestroyUserDao.updateApplyDestroyUserTrade(updateData);
			}
		}else{
			rtnData.put("BIZ_ORDER_RESULT", "2999");
			rtnData.put("RESULT_DESC", iResult.getString("RESULT_DESC",iResult.getString("X_RSPDESC")));
		}
		if(IDataUtil.isEmpty(iResult)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"操作失败：归属省未返回任何信息");
		}
		rtnDataset.add(rtnData);
		return rtnDataset;
	}

	public IDataset queryDestroyLocalOrder(IData input,Pagination pagination)throws Exception {
		
		return RemoteDestroyUserDao.queryDestroyLocalOrder(input,pagination);
	}

	public IDataset queryDestroyReceiOrder(IData input, Pagination pagination) throws Exception{
		
		return RemoteDestroyUserDao.queryDestroyReceiOrder(input,pagination);
	}
	
	
	/**
	 * 调iboss工具接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset callIbossTool(IData input) throws Exception{
		IDataset rtnDataset=new DatasetList();
		//iboss 
		IData iResult = IBossCall.callIbossTool(input);
		
		rtnDataset.add(iResult);
		return rtnDataset;
	}
}
