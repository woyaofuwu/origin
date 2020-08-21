package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;


public class SingleNumMultiDeviceStatusChangeBean extends CSBizBean {
	 private static transient Logger logger = Logger.getLogger(SingleNumMultiDeviceStatusChangeBean.class);

	
	/**
	 * 参数校验
	 */
	public static String chkParam(IData data, String strColName) throws Exception {
		String strParam = data.getString(strColName);
		if (strParam == null || strParam.trim().equals("")) {
			StringBuilder strError = new StringBuilder("接口参数检查: 输入参数[");
			strError.append(strColName).append("]不存在或者参数值为空");
			CSAppException.appError("-1", strError.toString());
		}
		return strParam;
	}
    /**
     * @Function: queryUserOweFee
     * @Description: 查询用户是否欠费
     * @param:user_id,EPARCHY_CODE,ID_TYPE,ROUTE_EPARCHY_CODE,GET_MODE
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-3-15 下午1:16:12 Modification History: Date Author Version Description
     * ---------------------------------------------------------* 2013-3-15 lijm3 v1.0.0 修改原因
     */
    public boolean queryUserOweFee(IData param) throws Exception {
        IData oweFee = null;
        String userId = param.getString("USER_ID");
        oweFee = AcctCall.getOweFeeByUserId(userId);//调账务接口

        if (oweFee == null) {
            CSAppException.apperr(UUException.CRM_UU_58);
        }
        String pt_i_OweFee3 = oweFee.getString("ACCT_BALANCE", "");

        if (Integer.parseInt(pt_i_OweFee3) < 0) {
            return true;
        }
        return false;
    }

    public static IDataset qryMDRPRealSync(IData param) throws Exception {
        return Dao.qryByCodeParser("TI_B_MDRP_REALSYNC", "SEL_BY_SN_AUXMSISDN", param, Route.CONN_CRM_CEN);
    }
    public static int UpdateMDRPRealSync(IData param) throws Exception {
        return Dao.executeUpdateByCodeCode("TI_B_MDRP_REALSYNC", "UPD_MDRP_RSPTIME_BY_PK", param, Route.CONN_CRM_CEN);
    }
    /**
     * 一号多终端业务信息同步
     *
     * @param input
     * @throws Exception
     */
    public void insertMDRPRealSyncAction(IData input) throws Exception {
        IData realSyncData = new DataMap();

        String sysDateString = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

        int randomNum = (int) (Math.random() * 10000);

        String seq = sysDateString + randomNum;

        String oprCode = input.getString("OPER_CODE");

        //操作流水
        realSyncData.put("SEQ", seq);

        //对账日期
        realSyncData.put("REQ_DAY", SysDateMgr.getSysDateYYYYMMDD());

        //主号码
        realSyncData.put("PRI_MSISDN", input.getString("PRI_SERIAL_NUMBER"));

        //操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
        realSyncData.put("OPR_CODE", input.getString("OPER_CODE"));

		//03--回退
		if ("01".equals(oprCode) || "03".equals(oprCode)) {
			//添加结果：OPR_CODE为01时必填 0--添加失败 1--添加成功
			realSyncData.put("ADD_RESULT", "0");
			if("03".equals(oprCode)){    //回退流程
				if("0000".equals(input.getString("ERROR_CODE"))){
					realSyncData.put("ADD_RESULT", "1");
				}
				realSyncData.put("RSRV_STR2",input.getString("PROFILE_STATUS"));
			}
		} else if ("02".equals(oprCode)) {
            //删除结果：OPR_CODE为02时必填 0--删除失败 1--删除成功
            realSyncData.put("DEL_RESULT", "0");
        }

        //副号码类型：1-eSIM卡，2-实体卡
        realSyncData.put("AUX_TYPE", input.getString("AUX_TYPE", ""));

        //副号MSISDN
        realSyncData.put("AUX_MSISDN", input.getString("AUX_MSISDN", ""));

        //副卡ICCID
        realSyncData.put("AUX_ICCID", input.getString("AUX_ICCID", ""));

        //副卡EID：AUX_TYPE为1时必填
        realSyncData.put("EID", input.getString("EID", ""));

        //副设备IMEI：AUX_TYPE为1时必填
        realSyncData.put("IMEI", input.getString("AUX_IMEI", ""));

        //副设备昵称：ADD_RESULT为1时必填
        realSyncData.put("AUX_NICK_NAME", input.getString("AUX_NICK_NAME", ""));

        //创建时间
        realSyncData.put("CTEATE_TIME", sysDateString);

        //处理结果编码
        realSyncData.put("BIZ_ORDER_RESULT", input.getString("ERROR_CODE", ""));
        //反馈时间
        realSyncData.put("RSP_TIME", sysDateString);
        //反馈结果
        realSyncData.put("RESULT_DESC", input.getString("ERROR_INFO"));
        //反馈次数
        realSyncData.put("RSP_COUNT", "0");
        Dao.insert("TI_B_MDRP_REALSYNC", realSyncData, Route.CONN_CRM_CEN);

    }

	/**
	 * 当前有效的副设备列表查询供展示
	 */
	public IDataset queryAuxDevInfos(IData input) throws Exception 
	{
		String oprFlag = input.getString("OPR_FLAG","");
		String serial_number=input.getString("SERIAL_NUMBER");
		
		//首先判断是否是一卡多号号码
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		
		if(IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String user_id=user.getString("USER_ID");
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("RELATION_TYPE_CODE", OneNoMultiTerminalConstants.RELATION_TYPE_CODE);	
		
		IDataset relationListTmp=RelaUUInfoQry.qryRelationList(inParam);
		
		IDataset relationList = new DatasetList();
		
		for(int i = 0;i < relationListTmp.size();i++)
		{
			IData relation = relationListTmp.getData(i);
			String oprCode = relation.getString("RSRV_STR1","");//操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
			String channlCode = relation.getString("RSRV_NUM1","");//业务暂停方式：1--业务主动发起的业务暂停操作
			if("1".equals(oprFlag))
			{	
				//业务暂停逻辑		
				if((oprCode.equals(OneNoMultiTerminalConstants.OPER_CODE_ADD) || oprCode.equals(OneNoMultiTerminalConstants.OPER_CODE_RESUME)) && "".equals(channlCode))
				{
					relationList.add(relation);
				}
			}
			else
			{
				//业务恢复逻辑
				if(oprCode.equals(OneNoMultiTerminalConstants.OPER_CODE_SUSPEND) && "1".equals(channlCode))
				{
					relationList.add(relation);
				}
			}
		}
		logger.debug("====queryAuxDevInfos relationList==="+relationList);
		return relationList;
	}

	/**
	 * 主号码校验
	 *
	 * @param input
	 * @return
	 * @throws Exception
     */
	public IDataset checkSingleAddAuxDev(IData input) throws Exception{
		IDataset resultList = new DatasetList();
		IDataset verifyList = new DatasetList();
		IData resultData = new DataMap();
		IData verifyData = new DataMap();
		IDataUtil.chkParamNoStr(input, "PRI_SERIAL_NUMBER");//用户主号码
		String serialNumber = input.getString("PRI_SERIAL_NUMBER");
		String isverify="0";//通过
		String xResultInfo = "";

		UcaData uca = null;
		try{
			uca = UcaDataFactory.getNormalUca(serialNumber);
		} catch (Exception ex){
			IDataset userInfos = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);
			if(IDataUtil.isNotEmpty(userInfos)){
				IData userInfo = userInfos.getData(0);
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				if("1".equals(userInfo.getString("REMOVE_TAG")) || "3".equals(userInfo.getString("REMOVE_TAG"))){
					resultData.put("VERIFY_RESULT", "0");//1:校验通过 0：校验不通过
					verifyData.put("VERIFY_TYPE", "009");
					verifyData.put("VERIFY_REASON", "2006");
					verifyData.put("RESULT_COMM", "用户预销户！");
					verifyList.add(verifyData);
					resultData.put("VERIFY_RESULTINFO", "2006|用户预销户");
					resultData.put("VERIFY_REASON_LIST", verifyList);
					resultList.add(resultData);
					return resultList;
				} else {
					resultData.put("VERIFY_RESULT", "0");//1:校验通过 0：校验不通过
					verifyData.put("VERIFY_TYPE", "009");
					verifyData.put("VERIFY_REASON", "2007");
					verifyData.put("RESULT_COMM", "用户已销户！");
					verifyList.add(verifyData);
					resultData.put("VERIFY_RESULTINFO", "2007|用户已销户");
					resultData.put("VERIFY_REASON_LIST", verifyList);
					resultList.add(resultData);
					return resultList;
				}
			} else {
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				resultData.put("VERIFY_RESULT", "0");//1:校验通过 0：校验不通过
				resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				verifyData.put("VERIFY_TYPE", "009");
				verifyData.put("VERIFY_REASON", "4005");
				verifyData.put("RESULT_COMM", "非移动手机号码或者号码不存在！");
				verifyList.add(verifyData);
				resultData.put("VERIFY_RESULTINFO", "4005|非移动手机号码或者号码不存在");
				resultData.put("VERIFY_REASON_LIST", verifyList);
				resultList.add(resultData);
				return resultList;
			}
		}

		String priuserid = uca.getUserId();
		IDataset svcstateLists = UserSvcStateInfoQry.getUserMainState(priuserid);
		if (svcstateLists == null || svcstateLists.size() <= 0) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "2099|主号其他原因！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "009");
			verifyData.put("VERIFY_REASON", "2099");
			verifyData.put("RESULT_COMM", "主号其他原因！");
			verifyList.add(verifyData);
		} else {
			if("0".equals(svcstateLists.getData(0).getString("SERVICE_ID"))){
				String stateCode = svcstateLists.getData(0).getString("STATE_CODE");
				if(!"0".equals(stateCode)){
					if("1".equals(stateCode)){
						isverify = "1";
						xResultInfo = xResultInfo(xResultInfo, "2004|用户已单向停机！");
						verifyData = new DataMap();
						verifyData.put("VERIFY_TYPE", "009");
						verifyData.put("VERIFY_REASON", "2004");
						verifyData.put("RESULT_COMM", "用户已单向停机！");
						verifyList.add(verifyData);
					}else if("5".equals(stateCode)){
						isverify = "1";
						xResultInfo = xResultInfo(xResultInfo, "2005|用户已欠费停机！");
						verifyData = new DataMap();
						verifyData.put("VERIFY_TYPE", "009");
						verifyData.put("VERIFY_REASON", "2005");
						verifyData.put("RESULT_COMM", "用户已欠费停机！");
						verifyList.add(verifyData);
					} else {
						isverify = "1";
						xResultInfo = xResultInfo(xResultInfo, "2009|用户处于非单停、停机、预销、销户的其它状态！");
						verifyData = new DataMap();
						verifyData.put("VERIFY_TYPE", "009");
						verifyData.put("VERIFY_REASON", "2009");
						verifyData.put("RESULT_COMM", "用户处于非单停、停机、预销、销户的其它状态！");
						verifyList.add(verifyData);
					}
				}
			} else {
				isverify = "1";
				xResultInfo = xResultInfo(xResultInfo, "2099|主号其他原因！");
				verifyData = new DataMap();
				verifyData.put("VERIFY_TYPE", "009");
				verifyData.put("VERIFY_REASON", "2099");
				verifyData.put("RESULT_COMM", "主号其他原因！");
				verifyList.add(verifyData);
			}
		}

		String psptTypeCode = uca.getCustomer().getPsptTypeCode();
		String psptid = uca.getCustomer().getPsptId();
		//集团客户CUST_PERSON表中不存在数据
		if(uca.getCustPerson()!=null){
			psptTypeCode = uca.getCustPerson().getPsptTypeCode();
			psptid = uca.getCustPerson().getPsptId();
		}
		IDataset blackInfos = UCustBlackInfoQry.qryBlackCustInfo(psptTypeCode, psptid);
		if (IDataUtil.isNotEmpty(blackInfos)) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "3000|主号属于黑名单！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "009");
			verifyData.put("VERIFY_REASON", "3000");
			verifyData.put("RESULT_COMM", "主号属于黑名单！");
			verifyList.add(verifyData);
		}

		//集团客户不允许办理
		String custType = uca.getCustomer().getCustType();
		if("1".equals(custType)){
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "3015|用户为集团客户，不允许受理该业务！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "009");
			verifyData.put("VERIFY_REASON", "3015");
			verifyData.put("RESULT_COMM", "用户为集团客户，不允许受理该业务！");
			verifyList.add(verifyData);
		}

		IDataset serviceLists = UserSvcInfoQry.getSvcUserId(priuserid, "190");
		if (serviceLists == null || serviceLists.size() <= 0) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "2054|主号不是VoLTE用户！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "001");
			verifyData.put("VERIFY_REASON", "2054");
			verifyData.put("RESULT_COMM", "主号不是VoLTE用户！");
			verifyList.add(verifyData);
		} else {
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "001");
			verifyData.put("VERIFY_REASON", "0000");
			verifyData.put("RESULT_COMM", "主号是VoLTE用户！");
			verifyList.add(verifyData);
		}

		IData feeData = AcctCall.getOweFeeByUserId(priuserid);//调账务接口
		String acctBalance = feeData.getString("ACCT_BALANCE","0");
		if (Float.valueOf(acctBalance) < 0) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "3003|主号欠费！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "002");
			verifyData.put("VERIFY_REASON", "3003");
			verifyData.put("RESULT_COMM", "主号欠费！");
			verifyList.add(verifyData);
		} else {
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "002");
			verifyData.put("VERIFY_REASON", "0000");
			verifyData.put("RESULT_COMM", "主号未欠费！");
			verifyList.add(verifyData);

			if(Float.valueOf(acctBalance)/100 < 10){
				isverify = "1";
				xResultInfo = xResultInfo(xResultInfo, "3002|主号余额不足！");
				verifyData = new DataMap();
				verifyData.put("VERIFY_TYPE", "002");
				verifyData.put("VERIFY_REASON", "3002");
				verifyData.put("RESULT_COMM", "主号余额不足！");
				verifyList.add(verifyData);
			}
		}

        // REQ202001020007  关于做好一号双终端业务相关问题优化改造的补充通知
        /**
         * 取消携号转网限制。根据“携号转网”相关工作要求（《关于携号转网全网型增值业务系统改造需求的函》，市函 [2019] 281 号），
         * 为保证携入用户正常使用现网增值业务产品，对于eSIM一号双终端（副号为非实体卡）不再进行携号转网用户校验。
         */
//		IDataset idsNpUser = UserNpInfoQry.qryUserNpInfosByUserId(priuserid);
//        if(IDataUtil.isNotEmpty(idsNpUser)) {
//            IData idNpUser = idsNpUser.first();
//            String strNpTag = idNpUser.getString("NP_TAG", "");
//            if ("1".equals(strNpTag)) {
//                isverify = "1";
//                xResultInfo = xResultInfo(xResultInfo, "2055|主号是携号转网用户！");
//                verifyData = new DataMap();
//                verifyData.put("VERIFY_TYPE", "003");
//                verifyData.put("VERIFY_REASON", "2055");
//                verifyData.put("RESULT_COMM", "主号是携号转网用户！");
//                verifyList.add(verifyData);
//            } else {
//                verifyData = new DataMap();
//                verifyData.put("VERIFY_TYPE", "003");
//                verifyData.put("VERIFY_REASON", "0000");
//                verifyData.put("RESULT_COMM", "主号不是携号转网用户！");
//                verifyList.add(verifyData);
//            }
//        } else {
//            verifyData = new DataMap();
//            verifyData.put("VERIFY_TYPE", "003");
//            verifyData.put("VERIFY_REASON", "0000");
//            verifyData.put("RESULT_COMM", "主号不是携号转网用户！");
//            verifyList.add(verifyData);
//        }

		//校验用户主号码是否归属于一号双终端试点城市
		String pilotCity = uca.getUserEparchyCode();
		IDataset mdrpPilotCity = CommparaInfoQry.getCommparaByCodeCode1("CSM", "8621", "MDRP_PILOT_CITY", pilotCity);
		if(com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil.isEmpty(mdrpPilotCity)){
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "5010|主号归属城市未开通一号双终端业务！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "004");
			verifyData.put("VERIFY_REASON", "5010");
			verifyData.put("RESULT_COMM", "主号归属城市未开通一号双终端业务！");
			verifyList.add(verifyData);
		} else {
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "004");
			verifyData.put("VERIFY_REASON", "0000");
			verifyData.put("RESULT_COMM", "主号归属城市已开通一号双终端业务！");
			verifyList.add(verifyData);
		}

		// REQ202001020007  关于做好一号双终端业务相关问题优化改造的补充通知
        /**
         * 取消副号一证五号限制。根据工信部最新管理要求，一号双终端业务副号码（非实体卡）不再计入用户一证五号管理范围，
         * 故需取消BOSS侧业务办理的一证五号限制。各省公司在为用户办理业务时，对于eSIM一号双终端（副号为非实体卡）不再向一证五号发送校验信息。
         */
//		verifyData = new DataMap();
//		IData priuser = new DataMap();
//		priuser.put("EPARCHY_CODE",uca.getUserEparchyCode());
//		priuser.put("CUST_ID", uca.getCustId());
//		String auxType = input.getString("AUX_TYPE");
//        if(!"".equals(auxType) && auxType != null){
//        	priuser.put("AUX_TYPE", auxType);
//        }
//		verifyData = NationalOpenLimitCheck(priuser, verifyData);
//		if (IDataUtil.isNotEmpty(verifyData)) {
//			if (!"0000".equals(verifyData.getString("VERIFY_REASON"))) {
//				isverify = "1";
//				xResultInfo = xResultInfo(xResultInfo, verifyData.getString("VERIFY_REASON") +"|"+ verifyData.getString("RESULT_COMM"));
//			}
//			verifyList.add(verifyData);
//		}

		String isRealName = uca.getCustomer().getIsRealName();
		if(!"1".equals(isRealName)){
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "2031|主号是非实名认证用户！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "006");
			verifyData.put("VERIFY_REASON", "2031");
			verifyData.put("RESULT_COMM", "主号是非实名认证用户！");
			verifyList.add(verifyData);
		} else {
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "006");
			verifyData.put("VERIFY_REASON", "0000");
			verifyData.put("RESULT_COMM", "主号是实名认证用户！");
			verifyList.add(verifyData);
		}

		IDataset relationUULists = RelaUUInfoQry.check_byuserida_idbzm_A(priuserid, "OM", null, uca.getUserEparchyCode());
		IDataset auxnum = StaticInfoQry.getStaticValueByTypeId("ONENO_MULTITERMIANL");
		int nums = 0;
		if (auxnum == null || auxnum.size() <= 0) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "2099|主号其他原因！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "009");
			verifyData.put("VERIFY_REASON", "2099");
			verifyData.put("RESULT_COMM", "主号其他原因！");
			verifyList.add(verifyData);
		} else {
			nums = auxnum.getData(0).getInt("DATA_ID");
		}
		if (relationUULists != null && relationUULists.size() > 0) {
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "2000|主号已开通过一号双终端业务！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "007");
			verifyData.put("VERIFY_REASON", "2000");
			verifyData.put("RESULT_COMM", "主号已开通过一号双终端业务！");
			verifyList.add(verifyData);
			if (relationUULists.size() >= nums) {
				isverify = "1";
				xResultInfo = xResultInfo(xResultInfo, "3026|主号超出绑定设备上限！");
				verifyData = new DataMap();
				verifyData.put("VERIFY_TYPE", "009");
				verifyData.put("VERIFY_REASON", "3026");
				verifyData.put("RESULT_COMM", "主号超出绑定设备上限！");
				verifyList.add(verifyData);
			}
		} else {
			//校验当前号码已经作为副设备进行开通了。
			relationUULists = RelaUUInfoQry.check_byuserida_idbzm(priuserid, "OM", null, uca.getUserEparchyCode());
			if(IDataUtil.isNotEmpty(relationUULists)){
				isverify = "1";
				xResultInfo = xResultInfo(xResultInfo, "2000|主号已开通过一号双终端业务！");
				verifyData = new DataMap();
				verifyData.put("VERIFY_TYPE", "007");
				verifyData.put("VERIFY_REASON", "2000");
				verifyData.put("RESULT_COMM", "主号已开通过一号双终端业务！");
				verifyList.add(verifyData);
			} else {
				verifyData = new DataMap();
				verifyData.put("VERIFY_TYPE", "007");
				verifyData.put("VERIFY_REASON", "0000");
				verifyData.put("RESULT_COMM", "主号未开通过一号双终端业务！");
				verifyList.add(verifyData);
			}
		}

		//主号套餐与一号双终端业务冲突校验
		IDataset mainOfferData = CommparaInfoQry.getCommpara("CSM", "8621", uca.getProductId(), uca.getUserEparchyCode());
		if(IDataUtil.isNotEmpty(mainOfferData)){
			isverify = "1";
			xResultInfo = xResultInfo(xResultInfo, "3004|主号套餐与一号双终端业务冲突校验！");
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "008");
			verifyData.put("VERIFY_REASON", "3004");
			verifyData.put("RESULT_COMM", "主号套餐与一号双终端业务冲突校验！");
			verifyList.add(verifyData);
		} else {
			verifyData = new DataMap();
			verifyData.put("VERIFY_TYPE", "008");
			verifyData.put("VERIFY_REASON", "0000");
			verifyData.put("RESULT_COMM", "主号套餐与一号双终端业务不冲突！");
			verifyList.add(verifyData);
		}

		if (!"1".equals(isverify)) {
			resultData.put("PRI_SERIAL_NUMBER", serialNumber);
			resultData.put("VERIFY_RESULT", "1");//1:校验通过 0：校验不通过
			resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			resultData.put("VERIFY_RESULTINFO", "ok");
			resultData.put("VERIFY_REASON_LIST", verifyList);
			resultList.add(resultData);
		} else {
			resultData.put("PRI_SERIAL_NUMBER", serialNumber);
			resultData.put("VERIFY_RESULT", "0");//1:校验通过 0：校验不通过
			resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			resultData.put("VERIFY_RESULTINFO", xResultInfo);
			resultData.put("VERIFY_REASON_LIST", verifyList);
			resultList.add(resultData);
		}

		return resultList;
	}

	/**
	 * 处理校验错误信息拼串
	 *
	 * @author tanjl
	 * @param xResultInfo
	 * @param errorInfo
	 * @return
	 * @throws Exception
	 */
	public String xResultInfo(String xResultInfo, String errorInfo) throws Exception {
		if ("".equals(xResultInfo)) {
			xResultInfo += errorInfo;
		} else {
			xResultInfo += "@" + errorInfo;
		}
		return xResultInfo;
	}

	/**
	 * 身份证号码校验
	 *
	 * @param input
	 * @return IDateset
	 * @throws Exception
	 */
	public IData NationalOpenLimitCheck(IData input,IData resultData) throws Exception {
		IData custdata = new DataMap();

		IDataset custinfos = CustPersonInfoQry.getPerInfoByCustId(input.getString("CUST_ID"));
		if(custinfos==null||custinfos.size()<=0){
			return resultData;
		}
		custdata.put("CUSTOMER_NAME", custinfos.getData(0).getString("CUST_NAME"));
		custdata.put("ID_CARD_TYPE",  custinfos.getData(0).getString("PSPT_TYPE_CODE"));
		custdata.put("ID_CARD_NUM", custinfos.getData(0).getString("PSPT_ID"));
		custdata.put("ROUTE_EPARCHY_CODE", input.getString("EPARCHY_CODE"));

        //关于做好一号双终端业务相关问题优化改造的通知 第二个改造点：eSIM一号双终端(副号为非实体卡)不进行一证五号校验
        String auxType = input.getString("AUX_TYPE");
        if("1".equals(auxType)){//副设备卡类型  1：eSIM卡  2：实体卡
    	    resultData = new DataMap();
            resultData.put("VERIFY_TYPE", "005");
            resultData.put("VERIFY_REASON", "0000");
            resultData.put("RESULT_COMM", "主号身份证件未到达“一证五号”限制！");
            return resultData;
        }
		//证件类型转换
//  		IDataset checkPsptResults = CSAppCall.call("SS.NationalOpenLimitSVC.checkPspt", custdata);
		//服务层直接使用方法调用，减少性能的损耗  add tanjl 20180814
		NationalOpenLimitBean nationalOpenLimitBean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		IDataset checkPsptResults = nationalOpenLimitBean.checkPspt(custinfos.getData(0).getString("PSPT_TYPE_CODE"));
		if(IDataUtil.isNotEmpty(checkPsptResults)){
			input.put("IDCARD_TYPE", checkPsptResults.getData(0).getString("PARA_CODE1"));
			input.put("CUSTOMER_NAME", custinfos.getData(0).getString("CUST_NAME"));
			input.put("IDCARD_NUM", custinfos.getData(0).getString("PSPT_ID"));
//			IDataset openNumInfos = CSAppCall.call("SS.NationalOpenLimitSVC.idCheck", input);
			//服务层直接使用方法调用，减少性能的损耗  add tanjl 20180814
			try {
				IDataset openNumInfos = nationalOpenLimitBean.idCheck(input);
				if (com.ailk.bizcommon.util.IDataUtil.isNotEmpty(openNumInfos)) {
					int total = openNumInfos.getData(0).getInt("TOTAL");
					if (total >= 5) {
						resultData = new DataMap();
						resultData.put("VERIFY_TYPE", "005");
						resultData.put("VERIFY_REASON", "3042");
						resultData.put("RESULT_COMM", "主号身份证件已到达“一证五号”限制校验！");
						return resultData;
					} else {
						resultData = new DataMap();
						resultData.put("VERIFY_TYPE", "005");
						resultData.put("VERIFY_REASON", "0000");
						resultData.put("RESULT_COMM", "主号身份证件未到达“一证五号”限制！");
						return resultData;
					}
				} else {
					resultData = new DataMap();
					resultData.put("VERIFY_TYPE", "005");
					resultData.put("VERIFY_REASON", "0000");
					resultData.put("RESULT_COMM", "主号身份证件未到达“一证五号”限制！");
					return resultData;
				}
			} catch (Exception ex){
				resultData = new DataMap();
				resultData.put("VERIFY_TYPE", "005");
				resultData.put("VERIFY_REASON", "0000");
				resultData.put("RESULT_COMM", "主号身份证件未到达“一证五号”限制！");
				return resultData;
			}
		}

		return resultData;
	}


	/**
	 * 用户身份信息校验
	 *
	 * @param newinput
	 * @return
	 * @throws Exception
	 */
	public IData checkMsisdnAuthInfo(IData newinput) throws Exception {
		IData pwdData = new DataMap();
		IData resultData = new DataMap();
		IDataset adrresultList = new DatasetList();

		String serialNumber = newinput.getString("PRI_SERIAL_NUMBER", "");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		pwdData.put("SERIAL_NUMBER", serialNumber);
		pwdData.put("USER_ID", uca.getUser().getUserId());
		pwdData.put("USER_PASSWD", newinput.getString("CUST_PWD"));
		pwdData.put("EPARCHY_CODE", uca.getUser().getEparchyCode());
		String custname = uca.getCustomer().getCustName();
		String psptid = uca.getCustomer().getPsptId();
		//集团客户CUST_PERSON表中不存在数据
		if(uca.getCustPerson()!=null){
			custname = uca.getCustPerson().getCustName();
			psptid = uca.getCustPerson().getPsptId();
		}

		/*IDataset idents = UserIdentInfoQry.queryIdentInfoByCode(psptid, serialNumber);
		if (IDataUtil.isEmpty(idents)) {
			resultData.put("PRI_SERIAL_NUMBER", serialNumber);
			resultData.put("VERIFY_RESULT", "0");
			resultData.put("ERROR_CODE", "3018");
			resultData.put("ERROR_INFO", "用户身份/预占凭证已失效");
			resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			return resultData;
		} else {
			if (StringUtils.equals(idents.getData(0).getString("TAG", ""), "EXPIRE")) {
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				resultData.put("VERIFY_RESULT", "0");
				resultData.put("ERROR_CODE", "3018");
				resultData.put("ERROR_INFO", "用户身份/预占凭证已失效");
				resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				return resultData;
			}
		}*/

		if (custname.trim().equals(newinput.getString("CUST_NAME").trim().trim()) && psptid.trim().equals(newinput.getString("CUST_ID_NUM").trim())) {
			IData pwdresult = checkUserPWD(pwdData);
			if ("1".equals(pwdresult.getString("VERIFY_RESULT", ""))) {
				resultData.put("VERIFY_RESULT", "1");
				resultData.put("ERROR_CODE", "0000");
				resultData.put("ERROR_INFO", "成功");
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				IData adrData = new DataMap();
				adrData.put("ADDRESS", uca.getCustPerson().getPsptAddr().replaceAll("\\d+", "")+"1");
				adrData.put("ADDRESS_CODE", "1");
				adrresultList.add(adrData);
				IData adrData2 = new DataMap();
				adrData2.put("ADDRESS", uca.getCustPerson().getPsptAddr().replaceAll("\\d+", "")+"2");
				adrData2.put("ADDRESS_CODE", "2");
				adrresultList.add(adrData2);
				IData adrData3 = new DataMap();
				adrData3.put("ADDRESS", uca.getCustPerson().getPsptAddr().replaceAll("\\d+", "")+"3");
				adrData3.put("ADDRESS_CODE", "3");
				adrresultList.add(adrData3);
				IData adrData4 = new DataMap();
				adrData4.put("ADDRESS", uca.getCustPerson().getPsptAddr().trim());
				adrData4.put("ADDRESS_CODE", "4");
				adrresultList.add(adrData4);
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				resultData.put("ADDRESS_OPT", adrresultList);
				resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			} else {
				resultData.putAll(pwdresult);
				resultData.put("PRI_SERIAL_NUMBER", serialNumber);
				resultData.put("VERIFY_RESULT", "0");
				resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			}
		} else {
			resultData.put("PRI_SERIAL_NUMBER", serialNumber);
			resultData.put("VERIFY_RESULT", "0");
			resultData.put("ERROR_CODE", "2043");
			resultData.put("ERROR_INFO", "身份信息错误");
			resultData.put("RSP_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		}
		return resultData;
	}

	/**
	 * 校验用户密码
	 *
	 * @param data
	 * @return
	 * @throws Exception
     */
	public IData checkUserPWD(IData data) throws Exception {
		IData result = new DataMap();
		IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));

		String passwd = data.getString("USER_PASSWD");
		if (StringUtils.isNotBlank(passwd)) {
			String password = PasswdMgr.encryptPassWD(passwd, userInfo.getString("USER_ID"));
			String inpass = userInfo.getString("USER_PASSWD", "");
			if (!password.equals(inpass)) {
				result.put("VERIFY_RESULT", "0");
				result.put("ERROR_CODE", "2036");
				result.put("ERROR_INFO", "用户密码不正确！");
				return result;
			} else {
				result.put("VERIFY_RESULT", "1");
				result.put("ERROR_CODE", "0000");
				result.put("ERROR_INFO", "成功");
			}
		}
		return result;
	}

	/**
	 * 操作副设备失败后的处理
	 *
	 * @param devInfoData
	 * @param relaUUInfo
	 * @return
	 * @throws Exception
     */
	public IData returnAuxInfos(IData devInfoData, IData relaUUInfo) throws Exception {
		IData resultData = new DataMap();
		devInfoData.put("ERROR_CODE", "2998");
		//一号多终端业务信息同步
		String operCode = devInfoData.getString("OPER_CODE","");
		if("02".equals(operCode)) {
			if (IDataUtil.isNotEmpty(relaUUInfo)) {
				devInfoData.put("AUX_TYPE", relaUUInfo.getString("RSRV_TAG1"));
				devInfoData.put("AUX_ICCID", relaUUInfo.getString("RSRV_STR2"));
				devInfoData.put("EID", relaUUInfo.getString("RSRV_STR3"));
				devInfoData.put("AUX_IMEI", relaUUInfo.getString("RSRV_STR4"));
				devInfoData.put("AUX_NICK_NAME", relaUUInfo.getString("RSRV_STR5"));
			} else {
				devInfoData.put("AUX_TYPE", "1");
			}
		}
		sendErrorMessage(devInfoData);
		//一号多终端业务信息同步
		insertMDRPRealSyncAction(devInfoData);

		resultData.put("X_RSPTYPE", "2");
		resultData.put("X_RSPCODE", "2998");
		resultData.put("X_RESULTCODE", "-1");
		resultData.put("X_RESULTINFO", devInfoData.getString("ERROR_INFO"));
		return resultData;
	}

//

	/**
	 *  业务处理失败短信操作
	 *
	 * @param input
	 * @throws Exception
     */
	public void sendErrorMessage(IData input) throws Exception {
		String oprCode = input.getString("OPER_CODE");
		if ("01".equals(oprCode)) {
			//添加结果：OPR_CODE为01时必填 0--添加失败 1--添加成功
//			realSyncData.put("ADD_RESULT", "0");
			IData smsadd = new DataMap();
			smsadd.put("SERIAL_NUMBER", input.getString("PRI_SERIAL_NUMBER"));
			smsadd.put("NOTICE_CONTENT", "尊敬的客户：抱歉，因系统原因，您的一号双终端业务办理失败。建议稍后再试。【中国移动 一号双终端】");
			sendMessage(smsadd);
		} else if ("02".equals(oprCode)) {
			//删除结果：OPR_CODE为02时必填 0--删除失败 1--删除成功
//			realSyncData.put("DEL_RESULT", "0");
			IData smsdel = new DataMap();
			smsdel.put("SERIAL_NUMBER", input.getString("PRI_SERIAL_NUMBER"));
			smsdel.put("NOTICE_CONTENT", "尊敬的客户：抱歉，因系统原因，您的一号双终端业务取消操作失败。建议稍后重新申请。【中国移动 一号双终端】");
			sendMessage(smsdel);
		}

	}

	/**
	 * 发送短信
	 * @param param
	 * @return
	 * @throws Exception
     */
	public IDataset sendMessage(IData param) throws Exception {
		IData sms = new DataMap();
		String seq = SeqMgr.getSmsSendId();
		long seq_id = Long.parseLong(seq);
		String content = param.getString("NOTICE_CONTENT");
		sms.put("SMS_NOTICE_ID", seq);
		sms.put("PARTITION_ID", seq_id % 1000);
		sms.put("SEND_COUNT_CODE", "1");
		sms.put("REFERED_COUNT", "0");
		sms.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", "ZZZZ"));
		sms.put("IN_MODE_CODE", "0");
		sms.put("CHAN_ID", "11");// 短信渠道编码:客户服务
		sms.put("RECV_OBJECT_TYPE", "00");// 被叫对象类型:00－手机号码
		sms.put("FORCE_OBJECT", "10086");
		sms.put("RECV_OBJECT", param.getString("SERIAL_NUMBER")); // 被叫对象:传手机号码
		sms.put("RECV_ID", param.getString("SERIAL_NUMBER")); // 被叫对象标识
		sms.put("SMS_TYPE_CODE", "20"); // 短信类型:20-业务通知
		sms.put("SMS_KIND_CODE", "02"); // 短信种类:02－短信通知
		sms.put("NOTICE_CONTENT_TYPE", "0");// 短信内容类型:0－指定内容发送
		if (content.length() > 500)
			content = content.substring(0, 500);
		sms.put("NOTICE_CONTENT", content);// 短信内容类型:0－指定内容发送
		sms.put("FORCE_REFER_COUNT", "1");// 指定发送次数
		sms.put("SMS_PRIORITY", 1000);// 短信优先级
		sms.put("REFER_TIME", SysDateMgr.getSysTime()); // 提交时间
		sms.put("REFER_STAFF_ID", getVisit().getStaffId());// 提交员工
		sms.put("REFER_DEPART_ID", getVisit().getDepartId());// 提交部门
		sms.put("DEAL_TIME", SysDateMgr.getSysTime()); // 处理时间
		sms.put("DEAL_STATE", "0");// 处理状态:0－未处理

		sms.put("REMARK", "一卡多终端办理失败");
		sms.put("SEND_TIME_CODE", "1");
		sms.put("SEND_OBJECT_CODE", "0");
		sms.put("SMS_NET_TAG", "0");
		sms.put("MONTH", Integer.parseInt(SysDateMgr.getCurMonth()));
		sms.put("DAY", Integer.parseInt(SysDateMgr.getCurDay()));

		// Dao.insert("TI_O_SMS", sms);
		SmsSend.insSms(sms);

		IData data = new DataMap();
		data.put("RESULT_INFO", "短信已成功发送！");
		IDataset res = new DatasetList();
		res.add(data);
		return res;
	}

	/**
	 * 处理一号双终端副号码开户资源预占返销
	 *
	 * @param tradeinfos
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData execOpenRes(IDataset tradeinfos, IData input) throws Exception {
		IDataset traderesinfos = TradeResInfoQry.queryAllTradeResByTradeId(tradeinfos.getData(0).getString("TRADE_ID"));
		String sim = "";
		String serialnumber = "";
		String imsi = "";
		IData inparam = new DataMap();
		for (int i = 0; i < traderesinfos.size(); i++) {
			String restypecode = traderesinfos.getData(i).getString("RES_TYPE_CODE");
			if ("0".equals(restypecode)) {
				serialnumber = traderesinfos.getData(i).getString("RES_CODE");
				ResCall.releaseMdrpRes(serialnumber, restypecode);
			}
			if ("1".equals(restypecode)) {
				sim = traderesinfos.getData(i).getString("RES_CODE");
				ResCall.releaseMdrpRes(sim, restypecode);
				imsi = traderesinfos.getData(i).getString("IMSI");
			}
		}
		inparam.put("AUX_ICCID", sim);
		inparam.put("AUX_SERIAL_NUMBER", serialnumber);
		inparam.put("AUX_IMSI", imsi);//副号的imsi
		inparam.putAll(input);
		//搬除副设备开户的工单
		TradeInfoQry.moveBhTrade(tradeinfos.getData(0));
		returnCallIboss(inparam);
		return inparam;
	}

	/**
	 * 回退流程
	 *
	 *
	 * @param input
	 * @throws Exception
	 */
	public IDataset returnCallIboss(IData input) throws Exception {

		IData iboss = new DataMap();
		IData outdate = new DataMap();
		IDataset ibossList = new DatasetList();
		String serialNumber = input.getString("PRI_SERIAL_NUMBER");//主号码
		String priIMSI = "";
		outdate.put("REQ_TYPE", "0");
		outdate.put("PRI_SERIAL_NUMBER", serialNumber);
		outdate.put("AUX_TYPE", input.getString("AUX_TYPE"));
		outdate.put("AUX_ICCID", input.getString("AUX_ICCID"));
		outdate.put("AUX_SERIAL_NUMBER", input.getString("AUX_SERIAL_NUMBER"));
		outdate.put("CTEATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		ibossList.add(outdate);
		iboss.put("SEQ", getSeqID(""));
		iboss.put("REQ_DAY", SysDateMgr.getSysDateYYYYMMDD());
		iboss.put("ADD_DEVN_LIST", ibossList);
		iboss.put("KIND_ID", "BIP3B526_T3000526_0_0");
		
		//关于做好一号双终端业务相关问题优化改造的通知 第一个改造点：增加PriIMSI和AuxIMSI两个必填字段
		//主号的根据号码查找user_res查
		IDataset userResList = UserResInfoQry.getUserResBySelbySerialnremove(serialNumber,"1");//RES_TYPE_CODE="1"为sim卡
		if (userResList != null && userResList.size() > 0) {
			 priIMSI = userResList.getData(0).getString("IMSI");
		}
		iboss.put("PRI_IMSI", priIMSI);//主号imsi
		iboss.put("AUX_IMSI", input.getString("AUX_IMSI"));//副号imsi
		return IBossCall.dealInvokeUrl("BIP3B526_T3000526_0_0", "IBOSS2", iboss);
	}

	/**
	 * 获取交易包流水号
	 */
	public static String getSeqID(String bipCode) throws Exception {
		String dataString=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		String seqID = String.format("%06d", (int)(Math.random()*1000000));
		return "898"+bipCode+dataString+seqID;
	}

	/**
	 * 回退操作副设备销户处理
	 *
	 * @param opentradeinfos
	 * @return
	 * @throws Exception
	 */
	public IData destroyAuxSingle(IDataset opentradeinfos) throws Exception {
		IData inparam = new DataMap();
		inparam.put("ERROR_CODE", "0000");
		inparam.put("ERROR_INFO", "回退成功");
		IDataset traderesinfos = TradeResInfoQry.queryAllTradeResByTradeId(opentradeinfos.getData(0).getString("TRADE_ID"));
		if (IDataUtil.isNotEmpty(traderesinfos)) {
			for (int i = 0; i < traderesinfos.size(); i++) {
				String restypecode = traderesinfos.getData(i).getString("RES_TYPE_CODE");
				if ("0".equals(restypecode)) {
					inparam.put("AUX_MSISDN", traderesinfos.getData(i).getString("RES_CODE"));
				}
				if ("1".equals(restypecode)) {
					inparam.put("AUX_ICCID", traderesinfos.getData(i).getString("RES_CODE"));
				}
			}
		}
		IDataset auxresult = new DatasetList();
		IData resultMap = new DataMap();
		// 调用手机销户登记流程
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", opentradeinfos.getData(0).getString("SERIAL_NUMBER"));
		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		data.put("ORDER_TYPE_CODE", "192");
		data.put("TRADE_TYPE_CODE", "192");
		try {
			auxresult = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", data);
			if (IDataUtil.isNotEmpty(auxresult)) {
				resultMap = auxresult.getData(0);
			}
			inparam.putAll(resultMap);
		} catch (Exception ex) {
			inparam.put("ERROR_CODE", "2998");
			inparam.put("ERROR_INFO", ex.getMessage());
		}

		return inparam;
	}
}