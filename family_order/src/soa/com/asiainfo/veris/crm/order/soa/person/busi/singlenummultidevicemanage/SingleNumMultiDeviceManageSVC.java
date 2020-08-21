
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage;

import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservice.LockUserPwdNewBean;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.SingleNumMultiDeviceStatusChangeBean;

public class SingleNumMultiDeviceManageSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;

	private static transient final Logger logger = Logger.getLogger(SingleNumMultiDeviceManageSVC.class);


	public IData loadChildInfo(IData input) throws Exception
	{
		IData verData = new DataMap();
		verData.put("PRI_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

		verData = checkSingleAddAuxDev(verData, false).getData(0);

		//1-校验通过；0-校验不通过
		if (!"1".equals(verData.getString("VERIFY_RESULT")))
		{
			CSAppException.appError("-1", verData.getString("VERIFY_RESULTINFO"));
		}

		//三户信息里面取的会模糊化，重新取一次
		IDataset personCustInfo = CustomerInfoQry.getNormalCustInfoBySN(input.getString("SERIAL_NUMBER"));

		return personCustInfo.getData(0);
	}

	/**
	 * 添加副设备资格校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkAddAuxDev(IData input) throws Exception {
		IData checkResultData = new DataMap();
		IDataset resultList = new DatasetList();

		IDataset verList = input.getDataset("VER_LIST");
		if (IDataUtil.isEmpty(verList)) {
			checkResultData.put("X_RSPCODE", "2998");
			checkResultData.put("X_RSPTYPE", "2");
			checkResultData.put("X_RESULTCODE", "-1");
			checkResultData.put("X_RESULTINFO", "VER_LIST请求为空！");
			return checkResultData;
		}
		boolean isCheckPass = false;   //校验是否通过
		for (int i = 0; i < verList.size(); i++) {
			IData verData = verList.getData(i);
			//单个设备校验
			IDataset checkresults  = checkSingleAddAuxDev(verData, false);
			//校验不通过的情况进行标记处理
			if(IDataUtil.isNotEmpty(checkresults) && "0".equals(checkresults.getData(0).getString("VERIFY_RESULT"))){
				isCheckPass = true;
			}
			resultList.add(checkresults.getData(0));
		}

		if(isCheckPass){
			checkResultData.put("X_RSPCODE", "2998");
			checkResultData.put("X_RSPDESC", "校验不通过");
			checkResultData.put("X_RSPTYPE", "2");
			checkResultData.put("X_RESULTCODE", "-1");
			checkResultData.put("X_RESULTINFO", "校验不通过！");
		}
		checkResultData.put("VER_RSP_LIST", resultList);

		return checkResultData;
	}

	/**
	 * 身份校验
	 *
	 * @param input
	 * @return
	 * @throws Exception
     */
	public IData checkMsisdnAuthInfo(IData input) throws Exception {
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();
		IData result = new DataMap();
		IDataUtil.chkParamNoStr(input, "VER_LIST");
		IDataset inpputs=input.getDataset("VER_LIST");
		if(inpputs==null||inpputs.size()<=0){
			resultData.put("X_RSPCODE","2998");
			resultData.put("X_RSPDESC", "校验不通过");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "VER_LIST请求为空！");
			return resultData;
		}
		for(int i=0;i<inpputs.size();i++){
			IData newinput = inpputs.getData(i);
			IDataUtil.chkParamNoStr(newinput, "PRI_SERIAL_NUMBER");
			IDataUtil.chkParamNoStr(newinput, "CUST_NAME");
			IDataUtil.chkParamNoStr(newinput, "CUST_ID_NUM");
			IDataUtil.chkParamNoStr(newinput, "CUST_PWD");
			IDataUtil.chkParamNoStr(newinput, "REQ_TIME");

			SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
			resultData = bean.checkMsisdnAuthInfo(newinput);
			if("0".equals(resultData.getString("VERIFY_RESULT", ""))){
				resultData.put("X_RSPCODE","2998");
				resultData.put("X_RSPDESC", "校验不通过");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RESULTCODE", resultData.getString("ERROR_CODE","-1"));
				resultData.put("X_RESULTINFO", resultData.getString("ERROR_INFO","受理失败"));
			}
			resultList.add(resultData);
		}

		result.put("VER_RSP_LIST", resultList);
		if(resultList!=null&&resultList.size()>0){
			result.put("X_RSPTYPE", resultList.getData(0).getString("X_RSPTYPE", ""));
			result.put("RSP_CODE", resultList.getData(0).getString("X_RESULTCODE", "0"));
			result.put("RSP_DESC", resultList.getData(0).getString("X_RESULTINFO", "OK"));
			result.put("X_RESULTCODE", resultList.getData(0).getString("X_RESULTCODE","0"));
			result.put("X_RESULTINFO", resultList.getData(0).getString("X_RESULTINFO","OK"));
			result.put("X_RSPCODE", resultList.getData(0).getString("X_RSPCODE", "0000"));
			result.put("X_RSPDESC", resultList.getData(0).getString("X_RSPDESC", "OK"));
		}
		return result;
	}

	/**
	 * 地址校验
	 *
	 * @param input
	 * @return
	 * @throws Exception
     */
	public IData checkMsisdnAddressInfo(IData input) throws Exception {
		IDataset resultList = new DatasetList();

		IData resultData = new DataMap();
		IData result = new DataMap();
		IDataUtil.chkParamNoStr(input, "VER_LIST");
		IDataset inpputs=input.getDataset("VER_LIST");
		if(inpputs==null||inpputs.size()<=0){
			resultData.put("X_RSPCODE","2998");
			resultData.put("X_RSPDESC", "校验不通过");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "VER_LIST请求为空！");
			return resultData;
		}
		for(int i=0;i<inpputs.size();i++){
			IData newinput=inpputs.getData(i);
			IDataUtil.chkParamNoStr(newinput, "PRI_SERIAL_NUMBER");
			IDataUtil.chkParamNoStr(newinput, "ADDRESS");
			IDataUtil.chkParamNoStr(newinput, "REQ_TIME");

			String serialNumber = newinput.getString("PRI_SERIAL_NUMBER", "");
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

			String addr = uca.getCustPerson().getPsptAddr().trim();
			if (addr.equals(newinput.getString("ADDRESS").trim())) {
				resultData.put("VERIFY_RESULT", "1");
				resultData.put("ERROR_CODE", "0000");
				resultData.put("ERROR_INFO", "成功");
			} else {
				resultData.put("VERIFY_RESULT", "0");
				resultData.put("ERROR_CODE", "2056");
				resultData.put("ERROR_INFO", "主号码登记地址错误！");
			}
			resultData.put("PRI_SERIAL_NUMBER", serialNumber);
			resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			if("0".equals(resultData.getString("VERIFY_RESULT", ""))){
				resultData.put("X_RSPCODE","2998");
				resultData.put("X_RSPDESC", "校验不通过");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RESULTCODE", resultData.getString("ERROR_CODE","-1"));
				resultData.put("X_RESULTINFO", resultData.getString("ERROR_INFO","受理失败"));
			}
			resultList.add(resultData);
		}

		result.put("VER_RSP_LIST", resultList);
		if(resultList!=null&&resultList.size()>0){
			result.put("X_RSPTYPE", resultList.getData(0).getString("X_RSPTYPE", ""));
			result.put("RSP_CODE", resultList.getData(0).getString("X_RESULTCODE", "0"));
			result.put("RSP_DESC", resultList.getData(0).getString("X_RESULTINFO", "OK"));
			result.put("X_RESULTCODE", resultList.getData(0).getString("X_RESULTCODE","0"));
			result.put("X_RESULTINFO", resultList.getData(0).getString("X_RESULTINFO","OK"));
			result.put("X_RSPCODE", resultList.getData(0).getString("X_RSPCODE", "0000"));
			result.put("X_RSPDESC", resultList.getData(0).getString("X_RSPDESC", "OK"));
		}
		return result;







	}

	/**
	 * 单个副设备资格校验
	 * @param verData
	 * @return
	 * @return
	 * @throws Exception
	 */
	public IDataset checkSingleAddAuxDev(IData verData, boolean isCheck) throws Exception
	{
		SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
		return bean.checkSingleAddAuxDev(verData);
	}

	/**
	 * 添加副设备接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData addAuxDevForSnmd(IData input) throws Exception {
		SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
		IData resultData = new DataMap();
		IDataset addDevList = input.getDataset("ADD_DEV_LIST");
		if (IDataUtil.isEmpty(addDevList)) {
			CSAppException.appError("-1", "添加副设备接口入参[ADD_DEV_LIST]为空！");
		}

		for (int i = 0; i < addDevList.size(); i++) {
			IData addDevData = addDevList.getData(i);
			if ("0".equals(addDevData.getString("REQ_TYPE"))) {    //添加副设备取消请求
				resultData = returnSingleTradeReg(addDevData);
			} else {
				//单个设备校验
				IData checkSingleAuxDev = checkSingleAddAuxDev(addDevData, true).getData(0);
				//1-校验通过；0-校验不通过
				if ("1".equals(checkSingleAuxDev.getString("VERIFY_RESULT"))) {
					//随机获取sim卡与号码并选占(一号多终端接口开户用)
					boolean resResultStatus = false;
					IDataset oNMTInfo = new DatasetList();
					try {
						oNMTInfo = ResCall.getONMTInfo();
					}catch (Exception ex) {
						resResultStatus = true;
					}
					if (resResultStatus || (oNMTInfo == null || oNMTInfo.size() <= 0)) {
						addDevData.put("OPER_CODE", "01");
						addDevData.put("ERROR_INFO", "5008|该号段数据量不足！");
						resultData = bean.returnAuxInfos(addDevData, null);
						resultData = new DataMap();
						resultData.put("X_RESULTCODE", "0000");
						resultData.put("X_RESULTINFO", "OK！");
						resultData.put("X_RSPCODE", "0000");
						resultData.put("X_RSPINFO", "OK！");
						return resultData;
					}
					if (IDataUtil.isNotEmpty(oNMTInfo)) {
						IData busiInputData = new DataMap();

						busiInputData.put("EID", IDataUtil.chkParam(addDevData, "EID"));
						busiInputData.put("AUX_IMEI", IDataUtil.chkParam(addDevData, "AUX_IMEI"));
						busiInputData.put("AUX_TYPE", IDataUtil.chkParam(addDevData, "AUX_TYPE", "1"));
						String custName = IDataUtil.chkParam(addDevData, "CUST_NAME");
						String psptId = IDataUtil.chkParam(addDevData, "CUST_ID_NUMBER");
						busiInputData.put("CUST_NAME", custName);
						busiInputData.put("PSPT_TYPE_CODE", "0");
						busiInputData.put("PSPT_ID", psptId);
						String priSerialNumber = IDataUtil.chkParam(addDevData, "PRI_SERIAL_NUMBER");
						busiInputData.put("PHONE", priSerialNumber);
						String simplyFlag = IDataUtil.chkParam(addDevData, "SIMPLY_FLAG");
						if ("0".equals(simplyFlag)) {
							//实名认证，直接用接口过来的身份信息资料
							busiInputData.put("PSPT_ADDR", IDataUtil.chkParam(addDevData, "CUST_CERT_ADDR"));
							busiInputData.put("SEX", IDataUtil.chkParam(addDevData, "GENDER"));
							busiInputData.put("FOLK_CODE", IDataUtil.chkParam(addDevData, "NATION"));
							busiInputData.put("BIRTHDAY", IDataUtil.chkParam(addDevData, "BIRTHDAY"));
							busiInputData.put("PSPT_END_DATE", IDataUtil.chkParam(addDevData, "CERT_EXP_DATE"));
							busiInputData.put("FRONTBASE64", IDataUtil.chkParam(addDevData, "ID_IMAGE_FRONT"));
							busiInputData.put("BACKBASE64", IDataUtil.chkParam(addDevData, "ID_IMAGE_BACK"));
						} else {
							//非实名认证，查主号码的身份信息资料
							IData userInfo = UcaInfoQry.qryUserInfoBySn(priSerialNumber);
							IData custPerson = UcaInfoQry.qryPerInfoByCustId(userInfo.getString("CUST_ID"));
							if (custName.equals(custPerson.getString("CUST_NAME", "")) && psptId.equals(custPerson.getString("PSPT_ID", ""))) {
								busiInputData.put("PSPT_ADDR", custPerson.getString("PSPT_ADDR", ""));
								busiInputData.put("SEX", custPerson.getString("SEX", ""));
								busiInputData.put("FOLK_CODE", custPerson.getString("FOLK_CODE", ""));
								busiInputData.put("BIRTHDAY", custPerson.getString("BIRTHDAY", ""));
								busiInputData.put("PSPT_END_DATE", custPerson.getString("PSPT_END_DATE", ""));
							} else {
								addDevData.put("OPER_CODE", "01");
								addDevData.put("ERROR_INFO", "2043|用户身份信息错误！");
								resultData = bean.returnAuxInfos(addDevData, null);
								resultData = new DataMap();
								resultData.put("X_RESULTCODE", "0000");
								resultData.put("X_RESULTINFO", "OK！");
								resultData.put("X_RSPCODE", "0000");
								resultData.put("X_RSPINFO", "OK！");
								return resultData;
							}
						}

						//号码信息
						IData resPhoneData = oNMTInfo.getData(0).getData("PHONE");
						//卡信息
						IData resSimData = oNMTInfo.getData(0).getData("SIM");
						addDevData.put("AUX_MSISDN", resPhoneData.getString("SERIAL_NUMBER"));
						addDevData.put("AUX_ICCID", resSimData.getString("SIM_CARD_NO"));

						resPhoneData.put("SERIAL_NUMBER_B", resPhoneData.getString("SERIAL_NUMBER"));
						resSimData.put("SIM_CARD_NO_B", resSimData.getString("SIM_CARD_NO"));
						busiInputData.putAll(resPhoneData);
						busiInputData.putAll(resSimData);
						busiInputData.put("SERIAL_NUMBER", addDevData.getString("PRI_SERIAL_NUMBER"));
						busiInputData.put("ORDER_TYPE_CODE", "396");
						busiInputData.put("TRADE_TYPE_CODE", "396");
						// 操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
						busiInputData.put("OPER_CODE", "01");

						try {
							IDataset resultList = CSAppCall.call("SS.SingleNumMultiDeviceManageRegSVC.tradeReg", busiInputData);
							if(IDataUtil.isNotEmpty(resultList)){
								resultData = resultList.getData(0);
							}
						} catch (Exception e) {
							addDevData.put("OPER_CODE", "01");
							addDevData.put("ERROR_INFO", e.getMessage());
							resultData = bean.returnAuxInfos(addDevData, null);
						}
					}
				} else {
					addDevData.put("OPER_CODE", "01");
					addDevData.put("ERROR_INFO", checkSingleAuxDev.getString("VERIFY_RESULTINFO"));
					resultData = bean.returnAuxInfos(addDevData, null);
				}
			}
		}
		resultData = new DataMap();
		resultData.put("X_RESULTCODE", "0000");
		resultData.put("X_RESULTINFO", "OK！");
		resultData.put("X_RSPCODE", "0000");
		resultData.put("X_RSPINFO", "OK！");
		return resultData;
	}

	/**
	 * 删除副设备接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData delAuxDevForSnmd(IData input) throws Exception {
		SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
		IData resultData = new DataMap();

		IDataset delDevList = input.getDataset("DEL_DEV_LIST");

		if (IDataUtil.isEmpty(delDevList)) {
			CSAppException.appError("-1", "删除副设备接口入参[DEL_DEV_LIST]为空！");
		}

		for (int i = 0; i < delDevList.size(); i++) {
			IData delDevData = delDevList.getData(i);
			delDevData.put("AUX_MSISDN", input.getString("AUX_SERIAL_NUMBER"));

			IData busiInputData = new DataMap();
			busiInputData.put("SERIAL_NUMBER", delDevData.getString("PRI_SERIAL_NUMBER"));
			busiInputData.put("SERIAL_NUMBER_B", delDevData.getString("AUX_SERIAL_NUMBER"));
			busiInputData.put("ORDER_TYPE_CODE", "396");
			busiInputData.put("TRADE_TYPE_CODE", "396");

			// 操作类型：01-业务添加 02-业务删除  03-添加回退 04-业务暂停 05-业务恢复
			busiInputData.put("OPER_CODE", "02");

			IDataset relaUUInfos = RelaUUInfoQry.qryRelaUUBySerNumAndSerNumB(delDevData.getString("PRI_SERIAL_NUMBER"), delDevData.getString("AUX_SERIAL_NUMBER"), "OM");
			if (IDataUtil.isNotEmpty(relaUUInfos)) {
				try {
					CSAppCall.call("SS.SingleNumMultiDeviceManageRegSVC.tradeReg", busiInputData);
				} catch (Exception e) {
					delDevData.put("OPER_CODE", "02");
					delDevData.put("ERROR_INFO", e.getMessage());
					resultData = bean.returnAuxInfos(delDevData, relaUUInfos.getData(0));
				}
			} else {
				delDevData.put("OPER_CODE", "02");
				delDevData.put("ERROR_INFO", "该主号码跟副号码不存在有效的一号多终端关系!");
				resultData = bean.returnAuxInfos(delDevData, null);
			}
		}

		return resultData;
	}

	/**
	 * 回退流程
	 *
	 *
	 * @param input
	 * @throws Exception
	 */
	public IData returnSingleTradeReg(IData input) throws Exception {
		SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
		IDataUtil.chkParamNoStr(input, "PRI_SERIAL_NUMBER");//用户主号码
		IDataUtil.chkParamNoStr(input, "EID");//副设备EID
		IDataUtil.chkParamNoStr(input, "AUX_IMEI");//副设备IMEI
		IDataUtil.chkParamNoStr(input, "AUX_TYPE");//副设备卡类型
		IDataUtil.chkParamNoStr(input, "PROFILE_STATUS");//副设备卡类型
		input.put("PRI_MSISDN", input.getString("PRI_SERIAL_NUMBER"));

		IData mdrpdata = new DataMap();
		IData resultData = new DataMap();
		String errorCode = "0000";
		String errorInfo = "回退成功";
		IData priuser = UcaInfoQry.qryUserInfoBySn(input.getString("PRI_SERIAL_NUMBER"));
		if (IDataUtil.isEmpty(priuser)) {
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RSPTYPE", "2");//1:失败 0：成功
			resultData.put("RSP_CODE", "0");
			resultData.put("RSP_DESC", "主号码资料异常，失败！");
			resultData.put("X_RESULTINFO", "主号码资料异常，失败！");
			return resultData;
		}

		IDataset dataset =  bean.qryMDRPRealSync(input);
		IDataset resultList = new DatasetList();
		boolean doReturn = true;
		if(IDataUtil.isNotEmpty(dataset)){
			IData param = dataset.getData(0);
			String rspcount=dataset.getData(0).getString("RSP_COUNT");
			String seq=dataset.getData(0).getString("SEQ");
			String oprCode = dataset.getData(0).getString("OPR_CODE");
			String addResult = dataset.getData(0).getString("ADD_RESULT");
			if ("01".equals(oprCode)) {
				if ("0".equals(addResult)) {   //绑定副设备失败的情况，直接返回回退成功即可
					doReturn = false;
				} else {     //绑定副设备成功的情况，判断反馈的值的状态
					if (!"F".equals(rspcount) && !"S".equals(rspcount)) {
						IData mdrpInfo = new DataMap();
						mdrpInfo.put("RESULT_DESC", "回退取消绑定操作");
						mdrpInfo.put("RSP_COUNT", "S");
						mdrpInfo.put("SEQ", seq);
						bean.UpdateMDRPRealSync(mdrpInfo);    //执行update操作
					}
				}
			} else if ("03".equals(oprCode)) {
				if ("1".equals(addResult)) {     ////回退成功的情况直接返回成功即可,失败的情况下，再重新执行一次回退流程
					resultData.put("X_RESULTCODE", "0");
					resultData.put("X_RSPTYPE", "0");//1:失败 0：成功
					resultData.put("X_RSPCODE", "0000");//1:失败 0：成功
					resultData.put("RSP_CODE", "0");
					resultData.put("RSP_DESC", "成功！");
					resultData.put("X_RESULTINFO", "OK！");
					return resultData;//中止流程
				}
			}
			input.put("AUX_SERIAL_NUMBER", param.getString("AUX_MSISDN",""));
			input.put("AUX_ICCID", param.getString("AUX_ICCID", ""));
			input.put("AUX_IMEI", param.getString("IMEI", ""));
			input.put("AUX_NICK_NAME", param.getString("AUX_NICK_NAME", ""));
		}
		if(doReturn) {
			IDataset relationUULists = RelaUUInfoQry.check_byuserida_idbzm_A(priuser.getString("USER_ID"), "OM", null, priuser.getString("EPARCHY_CODE"));
			if (IDataUtil.isEmpty(relationUULists)) {
				IDataset tradeinfos = TradeInfoQry.queryTradeInfoByCodeAndSn(input.getString("PRI_SERIAL_NUMBER"), "396");
				if (IDataUtil.isEmpty(tradeinfos)) {
					resultData.put("X_RESULTCODE", "-1");
					resultData.put("X_RSPTYPE", "1");//1:失败 0：成功
					resultData.put("X_RSPCODE", "2998");//1:失败 0：成功
					resultData.put("RSP_CODE", "0");
					resultData.put("RSP_DESC", "主号未办理绑定业务，失败！");
					resultData.put("X_RESULTINFO", "主号未办理绑定业务，失败！");
					return resultData;//中止流程
				} else {//搬台帐调资源返销，调IBOSS接口
					IData tradeinfo = tradeinfos.getData(0);
					String orderId = tradeinfo.getString("ORDER_ID");
					String routeId = tradeinfo.getString("EPARCHY_CODE");
					IData orderInfo = UOrderInfoQry.qryOrderByOrderId(orderId, routeId);

					TradeInfoQry.moveBhTrade(tradeinfo);
					IDataset opentradeinfos = TradeInfoQry.queryTradeInfoByOidAndCodeAndTag(tradeinfo.getString("ORDER_ID"), "10", "0");
					if (IDataUtil.isNotEmpty(opentradeinfos)) {
						input.put("AUX_SERIAL_NUMBER", opentradeinfos.getData(0).getString("SERIAL_NUMBER"));
						input.putAll(bean.execOpenRes(opentradeinfos, input));
					} else {
						opentradeinfos = TradeInfoQry.getHisMainTradeByOrderId(tradeinfo.getString("ORDER_ID"), "0", routeId);
						if (IDataUtil.isNotEmpty(opentradeinfos)) {
							opentradeinfos = DataHelper.filter(opentradeinfos, "TRADE_TYPE_CODE=10");
							if (IDataUtil.isNotEmpty(opentradeinfos)) {
								IData resultInfo = bean.destroyAuxSingle(opentradeinfos);
								errorCode = resultInfo.getString("ERROR_CODE");
								errorInfo = resultInfo.getString("ERROR_INFO");
								input.put("AUX_SERIAL_NUMBER", resultInfo.getString("AUX_MSISDN", ""));
								input.put("AUX_ICCID", resultInfo.getString("AUX_ICCID", ""));
							}
						}
					}
					if (IDataUtil.isNotEmpty(orderInfo)) {
						TradeInfoQry.moveBhOrder(orderInfo);
					}
					input.put("AUX_SERIAL_NUMBER", opentradeinfos.getData(0).getString("SERIAL_NUMBER"));
				}
			} else {
				input.put("AUX_SERIAL_NUMBER", relationUULists.getData(0).getString("SERIAL_NUMBER_B"));
				input.put("CREATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				//判断10工单是否完工了，如果没有完工，搬走开户工单   add by tanjl
				IDataset tradeinfos = TradeInfoQry.queryTradeInfoByCodeAndSn(input.getString("AUX_SERIAL_NUMBER"), "10");
				if (IDataUtil.isNotEmpty(tradeinfos)) {
					IData tradeinfo = tradeinfos.getData(0);
					String orderId = tradeinfo.getString("ORDER_ID");
					String routeId = tradeinfo.getString("EPARCHY_CODE");

					//处理台账信息和资源信息
					input.putAll(bean.execOpenRes(tradeinfos, input));
					//搬订单信息

					IData orderInfo = UOrderInfoQry.qryOrderByOrderId(orderId, routeId);
					TradeInfoQry.moveBhOrder(orderInfo);
					//设置标记，代表是回退的，10工单未完工，所以不需要调用销户的操作。
					input.put("EXEC_TYPE", "BACK");
				}
				resultList = delSingleTradeReg(input);
				if (IDataUtil.isNotEmpty(resultList)) {
					resultData = resultList.getData(0);
					if ("-1".equals(resultData.getString("X_RESULTCODE"))) {
						errorCode = "2998";
						errorInfo = resultData.getString("X_RESULTINFO");
					}
				}
			}
		}
		mdrpdata.put("OPER_CODE", "03");
		mdrpdata.put("PRI_SERIAL_NUMBER", input.getString("PRI_SERIAL_NUMBER"));
		mdrpdata.put("AUX_TYPE", input.getString("AUX_TYPE"));
		mdrpdata.put("AUX_MSISDN", input.getString("AUX_SERIAL_NUMBER"));
		mdrpdata.put("AUX_ICCID",input.getString("AUX_ICCID",""));
		mdrpdata.put("CREATE_TIME", input.getString("CREATE_TIME"));
		mdrpdata.put("EID", input.getString("EID"));
		mdrpdata.put("AUX_IMEI", input.getString("AUX_IMEI", ""));
		mdrpdata.put("AUX_NICK_NAME", input.getString("AUX_NICK_NAME", ""));
		mdrpdata.put("PROFILE_STATUS", input.getString("PROFILE_STATUS"));
		mdrpdata.put("ERROR_CODE", errorCode);
		mdrpdata.put("ERROR_INFO", errorInfo);
		bean.insertMDRPRealSyncAction(mdrpdata);
		return resultData;
	}

	/**
	 * 副卡撤销接口
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset delSingleTradeReg(IData input) throws Exception {
		IDataset resultList = new DatasetList();
		IData resultData = new DataMap();

		IDataUtil.chkParamNoStr(input, "PRI_SERIAL_NUMBER");//用户主号码
		IDataUtil.chkParamNoStr(input, "AUX_SERIAL_NUMBER");//用户副号码
		IDataUtil.chkParamNoStr(input, "CREATE_TIME");
		String serial_number=input.getString("PRI_SERIAL_NUMBER");
		//只有回退的情况才会存在REQ_TYPE,传入该标记，避免回退时，插入取消的对账记录。 add by tanjl
		resultData.put("REQ_TYPE", input.getString("REQ_TYPE",""));

		IData priuser = UcaInfoQry.qryUserInfoBySn(serial_number);
		if (IDataUtil.isEmpty(priuser)){
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "主号码用户资料异常！");
			resultList.add(resultData);
			return resultList;
		}
		serial_number=input.getString("AUX_SERIAL_NUMBER");

		if ("BACK".equals(input.getString("EXEC_TYPE", ""))) {
			IData auxuser = UcaInfoQry.qryUserInfoBySn(serial_number);
			if (IDataUtil.isEmpty(auxuser)) {
				resultData.put("X_RESULTCODE", "-1");
				resultData.put("X_RESULTINFO", "副号码用户资料异常！");
				resultList.add(resultData);
				return resultList;
			}
		}
		String priuserid=priuser.getString("USER_ID");
		IDataset relationUULists=	RelaUUInfoQry.qryRelaUUBySerNumAndSerNumB(input.getString("PRI_SERIAL_NUMBER"), input.getString("AUX_SERIAL_NUMBER"), "OM");
		if(relationUULists==null||relationUULists.size()<=0){
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "主号码和副号码未绑定！");
			resultList.add(resultData);
			return resultList;
		}
		IDataset discntLists=	UserDiscntInfoQry.getAllDiscntByUser(priuser.getString("USER_ID"), "20170998");
		if(discntLists==null||discntLists.size()<=0){
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "主号码主资费未绑定！");
			resultList.add(resultData);
			return resultList;
		}
		IData auxinput=new DataMap();
		auxinput.putAll(input);
		input.putAll(priuser);
		input.put("USER_ID_NEW", relationUULists.getData(0).getString("USER_ID_B"));
		input.put("SERIAL_NUMBER", input.getString("PRI_SERIAL_NUMBER"));
		input.put("SERIAL_NUMBER_B", input.getString("AUX_SERIAL_NUMBER"));
		input.put("ORDRENO",relationUULists.getData(0).getString("ORDRENO",""));
		input.put("OPER_TAG","DEL");//删除
		input.put("OPER_CODE", "02");//调用删除操作

		try
		{
			resultList = CSAppCall.call("SS.SingleNumMultiDeviceManageRegSVC.tradeReg",input);
		}	catch (Exception e)
		{
			resultData.put("ERROR_INFO", e.getMessage());
			resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RSPTYPE", "1");//1:失败 0：成功
			resultData.put("RSP_CODE", "1");
			resultData.put("RSP_DESC", "添加副设备失败！");
			resultData.put("X_RESULTINFO", "添加副设备失败！");
			resultList.add(resultData);
			return resultList;
		}
		return resultList;
	}
}
