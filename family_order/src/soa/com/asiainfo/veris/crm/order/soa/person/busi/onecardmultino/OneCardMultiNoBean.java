package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;




import net.sf.json.JSONArray;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.trade.OneCardMultiNoTradeUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;

public class OneCardMultiNoBean extends CSBizBean {
	Logger logger = Logger.getLogger(OneCardMultiNoBean.class);
	
	public static final String TRADE_TYPE_CODE="3798";// 业务类型代码 74-国内一卡多号业务
	public static final String PROV_CODE="898";//省份代码：海南898 湖南731 青海971 陕西029 天津220 新疆991 云南871
	public static final String BIZ_TYPE_CODE="74";// 业务类型代码 74-国内一卡多号业务
	public static final String RELATION_TYPE_CODE="M2"; //关系类型
	public static final String ORG_DOMAIN="MOSP";//平台编码
	public static final String PACKAGE_ID="50000000";
	public static final String PRODUCT_ID="50000000";
	// 主号码的操作代码07 – 取消副号码  04 – 用户暂停 05 – 用户恢复  12 – 用户退订且销号 	13 – 换号保留
	public static final String OPER_CODE_PAUSE="04";
	public static final String OPER_CODE_RESUME="05";
	public static final String OPER_CODE_APPLY="06";
	public static final String OPER_CODE_CANCEL="07";
	public static final String OPER_CODE_CANCEL_ALL="12";
	public static final String OPER_CODE_CHANGE_NO="13";
	/**
	 * 获取交易包流水号
	 */
	public String getSeqID(String bip_code) throws Exception {
		String dateString =SysDateMgr.getSysDate("yyyyMMddHHmmss");
		String seqID = String.format("%06d", (int)(Math.random()*1000000));
		return PROV_CODE+bip_code+dateString+seqID;
	}
	/**
	 * 一卡多号查询接口（提供给外围接口）
	 */
	public IData qryRelationListForEC(IData input) throws Exception {
		IData resultData = new DataMap();
		String serial_number=input.getString("SERIAL_NUMBER");
		//首先判断是否是一卡多号号码
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		if(null==user||user.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String user_id=user.getString("USER_ID");
		IDataset relationList=OneCardMultiNoQry.qryRelationList(user_id,RELATION_TYPE_CODE,null,null);
		if(null==relationList||relationList.isEmpty()){
			resultData.put("X_RESULTCODE", "1001");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】没有副号码！");
			return resultData;
		}
		IDataset follow_info=new DatasetList();
		int relationCount = relationList.size();
		for (int i = 0; i < relationCount; i++) {
			IData follow_infoData=new DataMap();
			follow_infoData.put("FOLLOW_MSISDN", relationList.getData(i).getString("SERIAL_NUMBER_B"));
			follow_infoData.put("SERIAL_NUM",  relationList.getData(i).getString("ORDERNO"));
			IData relation = relationList.getData(i);
			String category=relation.getString("USER_ID_B").substring(relation.getString("USER_ID_B").length()-1);
			follow_infoData.put("MOSP_CATEGORY", category);
			String serviceId = relationList.getData(i).getString("RSRV_STR3");
			relation.put("SERVICE_ID", serviceId);
			follow_infoData.put("SERVICE_ID", serviceId);
			
			if(serviceId.length()==0){
				follow_infoData.put("SERVICE_NAME", "");
			}
			else{
				//TODO huanghua 需要产商品解耦解耦---已解决
				IDataset serviceList=PlatInfoQry.getServiceInfo(relation);
				if(IDataUtil.isNotEmpty(serviceList)){
					follow_infoData.put("SERVICE_NAME", serviceList.getData(0).getString("SERVICE_NAME",""));
				}
				else{
					follow_infoData.put("SERVICE_NAME", "");
				}
			}
			
			follow_info.add(follow_infoData);
		}
		resultData.put("X_RESULTCODE", "0");
		resultData.put("X_RESULTINFO", "Trade OK!");
		resultData.put("SERIAL_NUMBER", serial_number);
		resultData.put("FOLLOW_INFO", follow_info);
		resultData.put("X_RESULT_NUM", relationCount);
		
		
		return resultData;
	}
	/**
	 * 一卡多号查询（提供给营业厅）
	 */
	public IDataset qryRelationListForCRM(IData input) throws Exception {
		String serial_number = "";
		if("0".equals(input.getString("NUMBERTYPE"))){
			IDataset number_idataset = OneCardMultiNoQry.queryNumberAbyB(input.getString("SERIAL_NUMBER"));
			if(null==number_idataset||number_idataset.isEmpty()){
				CSAppException.apperr(CrmUserException.CRM_USER_615);
			}
			serial_number = number_idataset.getData(0).getString("SERIAL_NUMBER_A");
		}else{
			serial_number = input.getString("SERIAL_NUMBER");
		}
		
		//String serial_number=input.getString("SERIAL_NUMBER");
		//首先判断是否是一卡多号号码
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		if(null==user||user.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String user_id=user.getString("USER_ID");
		IDataset relationListTmp=OneCardMultiNoQry.qryRelationList(user_id,RELATION_TYPE_CODE,null,null);
		IDataset relationList = new DatasetList();
		for(int i = 0;i < relationListTmp.size();i++)
		{
			IData relation = relationListTmp.getData(i);
			String category=relation.getString("USER_ID_B").substring(relation.getString("USER_ID_B").length()-1);
			relation.put("MOSP_CATEGORY", category);
			String serviceId = relationListTmp.getData(i).getString("RSRV_STR3","");
			relation.put("SERVICE_ID", serviceId);
			if(serviceId.length()==0){
				relation.put("SERVICE_NAME", "");
			}
			else{
				//TODO huanghua 20 需与产商品解耦---已解决
				IDataset serviceList=PlatInfoQry.getServiceInfo(relation);
				if(IDataUtil.isNotEmpty(serviceList)){
					relation.put("SERVICE_NAME", serviceList.getData(0).getString("SERVICE_NAME",""));
				}
				else{
					relation.put("SERVICE_NAME", "");
				}
			}
			
			relationList.add(relation);
		}
		return relationList;
	}
	/**
	 * 一卡多号取消前接口：提供给渠道，发送二次短信确认
	 */
	public IData sendSMSForCancel(IData input) throws Exception {
		IData resultData = new DataMap();
		String serial_number = input.getString("SERIAL_NUMBER");// 主卡号码
		//首先判断是否是一卡多号号码
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		if(null==user||user.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String user_id=user.getString("USER_ID");
		IDataset relationList=OneCardMultiNoQry.qryRelationList(user_id,RELATION_TYPE_CODE,null,null);
		if(null==relationList||relationList.isEmpty()){
			resultData.put("X_RESULTCODE", "1001");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】不是一卡多号用户，不能进行办理！");
			return resultData;
			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + serial_number + "】没有可以取消的副号码，不能进行办理！");
		}
		//二次短信确认校验
		IDataset paramForCheckTwo = CommparaInfoQry.getCommpara("CSM","9999","CHECK_TWO_MOSP",getTradeEparchyCode());
		if(null==paramForCheckTwo || paramForCheckTwo.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"短信下发端口配置错误，请检查配置！");
		}
		String xSequenceid = String.format("%06d", (int)(Math.random()*1000000));
		String forceObject = "10086"+paramForCheckTwo.getData(0).getString("PARA_CODE1")+xSequenceid;
		input.put("OUTER_TRADE_ID", xSequenceid);
		IDataset twoSmsCheckList = OneCardMultiNoQry.qryTwoSmsCheck(serial_number,xSequenceid);
		if(null!=twoSmsCheckList&&!twoSmsCheckList.isEmpty()){
			resultData.put("X_RESULTCODE", "1002");
			resultData.put("X_RESULTINFO", "存在短信二次确认记录");
		}
		else{
			StringBuilder SMSInfo = new StringBuilder();
			SMSInfo.append("尊敬的客户，一卡多号副号取消操作不可逆，请回复以下号码前序号，确认取消一卡多号副号,");
			for (int i = 0; i < relationList.size(); i++) {
				SMSInfo.append(relationList.getData(i).getString("ORDERNO"));
				SMSInfo.append("、");
				SMSInfo.append(relationList.getData(i).getString("SERIAL_NUMBER_B"));
				SMSInfo.append(";");
			}
			SMSInfo.append("4、全部取消。30分钟之内回复有效。温馨提示：虚拟副号取消之后将无法找回。中国移动");
			input.put("NOTICE_CONTENT", SMSInfo);// 短信内容类型:0－指定内容发送
			
			//二次短信确认表
			String seqId = SeqMgr.getTradeId();
			IData twoCheckInfo = new DataMap();
			twoCheckInfo.put("TRADE_ID", 		seqId);
			twoCheckInfo.put("OUTER_TRADE_ID", 	input.getString("OUTER_TRADE_ID"));
			twoCheckInfo.put("SERIAL_NUMBER", 	serial_number);
			twoCheckInfo.put("REVERT_DATE",     SysDateMgr.getOtherSecondsOfSysDate(1800));   //后移动半小时
			twoCheckInfo.put("NOTICE_CONTENT", 	input.getString("NOTICE_CONTENT"));
			twoCheckInfo.put("REMARK", 			"取消一卡多号业务二次短信确认记录");
			twoCheckInfo.put("RSRV_STR1", 		"");
			twoCheckInfo.put("RSRV_STR2", 		"");
			twoCheckInfo.put("RSRV_STR3", 		"");
			twoCheckInfo.put("RSRV_STR4", 		"");
			twoCheckInfo.put("RSRV_STR5", 		"");
			twoCheckInfo.put("EXEC_FLAG", 		"");
			SmsSend.twoCheck(twoCheckInfo);
		
			//给用户发短信
			String sysDate =SysDateMgr.getSysTime();
			String sms_notice_id=SeqMgr.getSmsSendId();
			input.put("SMS_NOTICE_ID",sms_notice_id);
			input.put("PARTITION_ID", sms_notice_id.substring(sms_notice_id.length() - 4));
			input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			input.put("BRAND_CODE", "");
			input.put("IN_MODE_CODE", getVisit().getInModeCode());
			input.put("CHAN_ID",BIZ_TYPE_CODE);//短信渠道编码:平台编码
			input.put("SMS_NET_TAG","0");
			input.put("SEND_OBJECT_CODE","6");	
			input.put("SEND_COUNT_CODE","1");
			input.put("SEND_TIME_CODE","1");
			input.put("RECV_OBJECT_TYPE","00");//被叫对象类型:00－手机号码
			input.put("RECV_OBJECT",input.getString("SERIAL_NUMBER"));//被叫对象:传手机号码
			input.put("RECV_ID",input.getString("USER_ID"));//被叫对象标识:传用户标识
			input.put("SMS_TYPE_CODE","20");//短信类型:20-业务通知
			input.put("SMS_KIND_CODE","02");//短信种类:02－短信通知
			input.put("NOTICE_CONTENT_TYPE","0");//短信内容类型:0－指定内容发送
			input.put("REFERED_COUNT","0");
			input.put("FORCE_REFER_COUNT","1");//指定发送次数
			input.put("FORCE_OBJECT", forceObject);
			input.put("FORCE_START_TIME",sysDate);//指定发送次数
			input.put("FORCE_END_TIME", "");
			input.put("SMS_PRIORITY",1000);//短信优先级
			input.put("REFER_TIME",sysDate);//提交时间
			input.put("REFER_STAFF_ID",getVisit().getStaffId());//提交员工
			input.put("REFER_DEPART_ID",getVisit().getDepartId());//提交部门
			input.put("DEAL_TIME",sysDate);//处理时间
			input.put("DEAL_STAFFID", getVisit().getStaffId());
			input.put("DEAL_DEPARTID", getVisit().getDepartId());
			input.put("DEAL_STATE","15");//处理状态:0－未处理
			input.put("REMARK", "一卡多号业务取消二次短信下发");
			input.put("MONTH",Integer.parseInt(sysDate.substring(5, 7)));
			input.put("DAY",Integer.parseInt(sysDate.substring(8, 10)));
			SmsSend.insSms(input);
			resultData.put("X_RESULTCODE", "0");
			resultData.put("X_RESULTINFO", "Trade OK!");
		}
		return resultData;
	}
	/**
	 * 客户资料 实名制 校验
	 */
	public IData isRealNameUser(IData inputParam) throws Exception {
		IData resultData = new DataMap();
		
		String serial_number=inputParam.getString("SERIAL_NUMBER");
		IDataset custInfoList=OneCardMultiNoQry.getCustInfoBySn(serial_number);
		if (null == custInfoList || custInfoList.isEmpty()) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE","4005");
			resultData.put("X_RESULTCODE", "4005");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】客户资料不存在，不能进行办理！");
			return resultData;
		}
		String is_real_name = custInfoList.getData(0).getString("IS_REAL_NAME");
		if(!"1".equals(is_real_name)&&!"5".equals(is_real_name)){
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE","2031");
			resultData.put("X_RESULTCODE", "2031");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】客户不是实名制用户，不能进行该业务受理！");
			return resultData;
		}
		resultData.put("X_RESULTCODE", "0");
		return resultData;
	}
	/**
	 * 三户资料 主体服务状态校验
	 */
	public IData isNormalMainService(IData input) throws Exception {
		IData resultData = new DataMap();
		
		String serial_number=input.getString("SERIAL_NUMBER");
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
	    if (IDataUtil.isEmpty(user)){
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE","4005");
	    	resultData.put("X_RESULTCODE", "4005");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】用户资料不存在，不能进行办理！");
			return resultData;
	    }
		IData inParam=new DataMap();
		inParam.put("SERIAL_NUMBER", serial_number);
		inParam.put("REMOVE_TAG", "0");
		IData userInfoList = UserInfoQry.getUserInfoBySn2(inParam);
		if (null == userInfoList || userInfoList.isEmpty()) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE","4005");
			resultData.put("X_RESULTCODE", "4005");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】用户资料不存在，不能进行办理！");
			return resultData;
		}
		if (!"0".equals(userInfoList.getString("USER_STATE_CODESET"))) {
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE","2009");
			resultData.put("X_RESULTCODE", "2009");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】用户状态非正常，不能进行办理！");
			return resultData;
		}
		input.put("USER_ID", userInfoList.getString("USER_ID"));
		
		/**
		 * REQ201608190012_关于取消内部员工办理和多号业务限制的需求
		 * 20160912
		 * @author zhuoyingzhi
		 * 		  //-----移动员工不能办理----------
        boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUserNew(userInfoList.getString("USER_ID"));
        if(isCmccStaffUser){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
        }
		 */

        
		resultData.put("X_RESULTCODE", "0");
		return resultData;
	}
	/**
	 * 待销户虚拟副号码同步接口 ITF_CRM_MSISDNSYNC
	 */
	public void syncFollowMsisdnDestroy(IData inputParam)throws Exception {
		String followMsisdnsStr = inputParam.getString("MSISDN");
		//单条记录，组装参数
		IDataset followMsisdnList = new DatasetList();
		if(!followMsisdnsStr.startsWith("[")){ 
			IData followMsisdn= new DataMap();
			followMsisdn.put("MSISDN", inputParam.getString("MSISDN"));
			followMsisdnList.add(followMsisdn);
		}
		//多条记录，组装参数
		else{
			JSONArray followMsisdnJson = JSONArray.fromObject(inputParam.getString("MSISDN"));
			for (int index = 0; index < followMsisdnJson.size(); index++) {
				IData followMsisdn= new DataMap();
				followMsisdn.put("MSISDN", followMsisdnJson.get(index).toString());
				followMsisdnList.add(followMsisdn);
			}
		}
		for (int i = 0; i < followMsisdnList.size(); i++) {
			String serial_number_b = followMsisdnList.getData(i).getString("MSISDN");
			inputParam.put("SERIAL_NUMBER", serial_number_b);
			// 查询用户资料
			IData userInfoParam = new DataMap();
			userInfoParam.put("REMOVE_TAG", "0");
			userInfoParam.put("SERIAL_NUMBER", serial_number_b);
			IData userInfoList = UserInfoQry.getUserInfoBySn2(userInfoParam);
			IData inparam = new DataMap();
			if (null==userInfoList||userInfoList.isEmpty()) {
				inparam.put("USER_STATUS", "90");
			} 
			// 检查用户状态
			else {
				String user_state_codeset = userInfoList.getString("USER_STATE_CODESET");
				if ("0".equals(user_state_codeset)) {
					inparam.put("USER_STATUS", "00");
				} else if ("8".equals(user_state_codeset)) {
					inparam.put("USER_STATUS", "03");
				} else if ("6".equals(user_state_codeset) || "9".equals(user_state_codeset)) {
					inparam.put("USER_STATUS", "04");
				} else {
					inparam.put("USER_STATUS", "02");
				}
			}
			// 查询一卡多号关系
			IDataset relation = OneCardMultiNoQry.qryRelationList(null,"M2",serial_number_b,null);
			if (null==relation||relation.isEmpty()) {
				inparam.put("RESULT_CODE", "0");// 成功
			} else {
				inparam.put("RESULT_CODE", "1");// 失败
			}
			//状态同步表插入记录
//			Dao.insert("TI_B_MOSP_FOLLOWMSISDN", inparam,Route.CONN_CRM_CEN);
			
			IData inputInfo = new DataMap();
			inputInfo.put("SERIAL_NUMBER", serial_number_b); // 虚拟副号码
			inputInfo.put("IMSI", "");// IMSI
			inputInfo.put("AREA_CODE", getVisit().getProvinceCode()); // 省编码
			inputInfo.put("EPARCHY_CODE", getTradeEparchyCode()); // 地州编码
			inputInfo.put("EXEC_STATUS", "1");// 号码状态 0-开户 1-销户
			inputInfo.put("OPERTYPE", "1"); // 同步类型 0-批量开销户 1-待销户批量
			inputInfo.put("SPRING_RESULT_CODE", inparam.getString("RESULT_CODE")); // 处理结果
			inputInfo.put("USER_STATUS", inparam.getString("USER_STATUS")); // 用户状态
			inputInfo.put("DEAL_STATUS", "0"); // 同步状态 0-未上传 1-已上传
			Dao.executeUpdateByCodeCode("TF_F_USER_RES","INS_MOSP_FOLLOWMSISDN",inputInfo,Route.CONN_CRM_CEN);
		}
	}
	
	/**
	 * 查询可订购的服务
	 */
	public IData qryServiceList(IData input) throws Exception {
		// TODO Auto-generated method stub
		IDataset ServiceList = PlatInfoQry.getBizInfo(input);
        if (IDataUtil.isEmpty(ServiceList))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74,"未查询到可订购的服务。");
        }
		return null;
	}
	
	/**
	 * 一卡多号业务办理（提供给营业厅）
	 */
	public IDataset applyOneForCRM(IData input) throws Exception {
		String in_mode_code = this.getVisit().getInModeCode();
    	//副号码校验 --->注意防止调用资源两次
    	if("0".equals(input.getString("SERIAL_TYPE",input.getString("CATEGORY", "0")))){
    		if(!"0".equals(in_mode_code)){       //前台界面只有自动获取和平台分配两种，自动获取界面上已经调用了资源了，平台分配不需要调用资源   add by tanjl 20180508
        		if("".equals(input.getString("SERIAL_NUMBER_B",""))){
    				IDataset returnData = ResCall.getSubPhone(getTradeEparchyCode());
    				input.put("SERIAL_NUMBER_B",returnData.getData(0).getString("SERIAL_NUMBER"));
    			} else {
					ResCall.checkIsSubNumber(getTradeEparchyCode(), input.getString("SERIAL_NUMBER_B", ""));
    			}
        	}
    	}
		String serial_number=input.getString("SERIAL_NUMBER"); // 主号
		String serial_number_b=input.getString("SERIAL_NUMBER_B");
		
		/**
		 * 20191008 mengqx
		 * 校验副号是否是和多号副号
		 * 查询号码套餐是国内一卡多号主套餐
		 */
		if("0".equals(in_mode_code)) {
			IDataset userInfos = UserInfoQry.getUserInfoBySn(serial_number_b, "0");
			if(IDataUtil.isNotEmpty(userInfos)){
				String userID = userInfos.getData(0).getString("USER_ID");
				IDataset serial_number_b_product = UserProductInfoQry.queryUserMainProduct(userID);

				if (IDataUtil.isNotEmpty(serial_number_b_product) && null != serial_number_b_product && !serial_number_b_product.isEmpty()) {
					if (!"MOSP".equals(serial_number_b_product.getData(0).getString("BRAND_CODE"))) {
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "副号不是和多号副号！");
					}
				}
			}
		}
		//end
		
		String service_id=input.getString("SERVICE_ID","");
		String userId = "";
		if("".equals(service_id)){
			String sp_code=input.getString("SPID","");
			String biz_code=input.getString("BIZ_CODE","");
			if("".equals(sp_code)||"".equals("biz_code")){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"SPID,BIZ_CODE不能为空！");
			}
			IData inparams = new DataMap();
			inparams.put("SP_CODE", sp_code);
			inparams.put("BIZ_CODE", biz_code);
			
//			IDataset serviceInfo = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_CODE", inparams);
			IDataset serviceInfo = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(inparams.getString("SP_CODE",""), inparams.getString("BIZ_CODE",""));
			if(IDataUtil.isEmpty(serviceInfo)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到相应SPID,BIZ_CODE所对应的服务！");
			}
			service_id = serviceInfo.getData(0).getString("SERVICE_ID");
				
		}

		IData inputParam = new DataMap();
		inputParam.put("SERIAL_NUMBER", serial_number);
		
		IData inparams = new DataMap();
		inparams.put("SUBSYS_CODE", "CSM");
		inparams.put("PARAM_ATTR", "9999");
		inparams.put("PARAM_CODE", "CHECK_QUALIFICATIONS");
		
		IDataset Qulification = Dao.qryByCode("TD_S_COMMPARA", "SEL_PARAM_BY_CODE", inparams);
		IData dataParam = new DataMap();
		dataParam.put("SERIAL_NUMBER", serial_number);
		dataParam.put("REMOVE_TAG", "0");
		/**
		 * REQ201608190012_关于取消内部员工办理和多号业务限制的需求
		 * 由于不需要限制移动员工办理,所以注释掉代码
		 * 	/*IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", dataParam);
			if(IDataUtil.isNotEmpty(userInfos)){
				userId = userInfos.getData(0).getString("USER_ID");
				 boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUserNew(userId);
			        if(isCmccStaffUser){
			        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
			        }
			}
		*/
		for(int i = 0;i<Qulification.size();i++)
		{
			String service_id_param = Qulification.getData(i).getString("PARA_CODE3");
			
			if(service_id_param.equals(service_id))
			{	
				/**
				 * 关于和多号和平台业务办理优化的需求 
				 * @author huangzailong
				 * @date 20170921
				 */
				//REQ201710310012关于BOSS侧进行和多号业务受理支撑系统改造的通知，要求取消限制
//				IData inparam1 = new DataMap();
//				inparam1.put("SERIAL_NUMBER", serial_number);
//				inparam1.put("REMOVE_TAG", "0");
//				IDataset UserInfos = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", inparam1);
//				IData inparam = new DataMap();
//				inparam.put("USER_ID_A", UserInfos.getData(0).getString("USER_ID"));
//				inparam.put("SERIAL_NUMBER_A", serial_number);
//				inparam.put("RELATION_TYPE_CODE", RELATION_TYPE_CODE);
//				//获取三个月内办理和多号的数量
//				IDataset count = Dao.qryByCode("TF_F_RELATION_UU", "SEL_COUNT_BY_NUMAANDRTC", inparam);
//				if(IDataUtil.isNotEmpty(count))//判断三个月内有办理该业务
//				{	
//					String num = count.getData(0).getString("NUM");
//					
//					if(!"0".equals(num)){
//						CSAppException.apperr(CrmCommException.CRM_COMM_103, "非和多号新用户不允许办理此业务！");
//					}
//				}
				break;
			}
			
		}
		// 调用网状网接口同步申请办理一卡多号
		inputParam.put("OPER_CODE", OPER_CODE_APPLY);
		
		inputParam.put("REC_NUM", "1");
		IDataset follow_infoList = new DatasetList();
		IData follow_info = new DataMap();
		follow_info.put("SERIAL_NUM", serial_number);
		follow_info.put("FOLLOW_MSISDN", serial_number_b);
		follow_info.put("SERVICE_ID", service_id);
		if(StringUtils.equals("0", input.getString("SERIAL_TYPE","0")))
		{
        	follow_info.put("NUMBER_LEVER",input.getString("MOSP_NUMBER_LEVEL","6"));
		}
		follow_infoList.add(follow_info);
		inputParam.put("FOLLOW_INFOLIST", follow_infoList);
		inputParam.put("SERVICE_ID", service_id);
		inputParam.put("CATEGORY", input.getString("SERIAL_TYPE","0"));
		String category = input.getString("SERIAL_TYPE","0");
		/**
		 * 端午节活动
		 * @author zhuoyingzhi
		 * @date 20170519
		 */
		inputParam.put("MOSP_OLDIDV", input.getString("OLDIDV",""));
		IData callResult =  isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未实名制!");
        }
        // 绑定虚拟副号码,如果为申请虚拟副号码 // 主号码就需要走一证五号查验
		if ("0".equals(category)) {
			input.put("MSISDN", serial_number);
			idCheck(input);
		}
		inputParam.put("PSPT_ID", input.getString("PSPT_ID"));
		inputParam.put("CUST_NAME", input.getString("CUST_NAME"));
		inputParam.put("ADDRESS", input.getString("PSPT_ADDR"));
		IDataset resultData = null;
		if("0".equals(category)){//虚拟副号码
			if(StringUtils.isBlank(serial_number_b)){
				//旧的
				 resultData = OneCardMultiNoTradeUtil.updateRelationsCallIBossNew(inputParam);
			}else{
				//新的
				resultData = OneCardMultiNoTradeUtil.updateRelationsCallIBossNew1(inputParam);		
			}
		}else { //实体副号码
			resultData = OneCardMultiNoTradeUtil.updateRelationsCallIBossNew(inputParam);
		}
		return resultData;
	}
	
	 /**
		 * 取吉祥号码等级
		 * @param snb
		 * @return
		 * @throws Exception
		 */
	public static String getNumberLevel(String snb) throws Exception{
			IDataset resDatas =  ResCall.getMphonecodeInfo(snb);
			String numLevel = "";
			if(IDataUtil.isNotEmpty(resDatas))
			{
				String beautiTag = resDatas.getData(0).getString("BEAUTIFUL_TAG");

				if(StringUtils.equals("1", beautiTag))
				{
					numLevel = "1";//本地不做吉祥号码区分，任意取个值表示吉祥号码，吉祥号码等级由平台做区分。
				}
			}

			return numLevel;
	}
	
	public void idCheck(IData input) throws Exception {
		String serialNumber=input.getString("MSISDN"); // 主号
		logger.debug("----------idCheck----serialNumber"+serialNumber);
		NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		logger.debug("----------userInfo----userInfo"+userInfo);
		String userId = userInfo.getString("USER_ID");
		String custId = userInfo.getString("CUST_ID");
		String userTypeCode = userInfo.getString("USER_TYPE_CODE");
		IData checkData = new DataMap();
		checkData.put("USER_ID", userId);
		checkData.put("USER_TYPE_CODE", userTypeCode);	
		checkData.put("SERIAL_NUMBER", serialNumber);
		if(bean.isIgnoreCall()){//是否需要走一证五号
			return;
		}		
		
	    IData customerData = UcaInfoQry.qryCustomerInfoByCustId(custId);
	    String psptTypeCode = customerData.getString("PSPT_TYPE_CODE");
	    
	    // 根据证件类型查找全网开户限制数
		IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptTypeCode, "ZZZZ");
		if (openLimitResult.isEmpty())
		{// 如果本地配置没有该证件类型的限制数量配置，则直接返回
			return ;
		}
	    
	    IData param =new DataMap();		
	    param.put("ID_CARD_TYPE", psptTypeCode);
	    param.put("IDCARD_TYPE", psptTypeCode);
	    /**
	     * 全国一证五号校验 NationalOpenLimitBean.java  里面有转换
	     */
//	    IDataset checkIdTypeResults = bean.checkPspt(psptTypeCode); //转换证件类型 
//	    if(IDataUtil.isEmpty(checkIdTypeResults)){
//	    	return;
//	    }
//	    param.put("ID_CARD_TYPE", checkIdTypeResults.first().getString("PARA_CODE1"));
//	    param.put("IDCARD_TYPE", checkIdTypeResults.first().getString("PARA_CODE1"));
		param.put("CUSTOMER_NAME", customerData.getString("CUST_NAME"));		
		param.put("ID_CARD_NUM", customerData.getString("PSPT_ID"));
		
		param.put("IDCARD_NUM", customerData.getString("PSPT_ID"));
		
		param.put("SEQ", input.getString("SEQ"));
		IDataset openNumInfos = bean.idCheck(param); //一证五号校验
		if(IDataUtil.isNotEmpty(openNumInfos)){
			if ("0".equals(openNumInfos.getData(0).getString("X_RESULTCODE")))
			{
				int openNum = openNumInfos.getData(0).getInt("TOTAL", 0);
				int untrustresult = openNumInfos.getData(0).getInt("UN_TRUST_RESULT", 0);

				if (openNum >= 0)
				{
					if (untrustresult > 0)
					{
						String xRspDesc = "开户人有不良信息，不满足开户条件，禁止开户";
						CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
					}
					if (IDataUtil.isNotEmpty(openLimitResult))
					{
						//start-REQ201907010012关于和多号没有进行本地一证五号校验问题优化-wangsc
						int rCount = UserInfoQry.getRealNameUserCountByPspt2(customerData.getString("CUST_NAME"), customerData.getString("PSPT_ID"));
						rCount += UserInfoQry.getRealNameUserCountByUsePspt(customerData.getString("CUST_NAME"), customerData.getString("PSPT_ID"), null);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
						int rLimit = UserInfoQry.getRealNameUserLimitByPspt(customerData.getString("CUST_NAME"), customerData.getString("PSPT_ID"));
						if (rCount >= rLimit)
						{
							String xRspDesc = "【本省一证多号】校验: 证件号码【" + customerData.getString("PSPT_ID") + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！";
				    		CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
						}
						//end
						
						int openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 0);

						if (openNum >= openLimitNum)
						{
							String xRspDesc = "【全网一证多号】校验: 证件号码【" + customerData.getString("PSPT_ID") + "】实名制开户的数量已达到最大值【" + openLimitNum + "个】，请更换其它证件！";
				    		CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
						}
					}
				}
			} else if ("2998".equals(openNumInfos.getData(0).getString("X_RESULTCODE"))){
//			if (!("0".equals(openNumInfos.getData(0).getString("X_RSPTYPE", "")))|| !"0000".equals(openNumInfos.getData(0).getString("X_RSPCODE"))) {
				/**
				 * REQ201709250007_全网一证多名返回优化
				 * <br/>
				 * 二级错误
				 * <br/>
				 * 调用集团一证五号平台，若返回码为：23039  提示：同一证件号码下存在多个用户姓名，不限制用户办理业务
				 * @author zhuoyingzhi
				 * @date 20171129
				 */
				if ("ns1:23039".equals(openNumInfos.getData(0).getString(
						"X_RSPCODE", ""))
						|| "23039".equals(openNumInfos.getData(0)
								.getString("X_RSPCODE", ""))) {
					//不限制业务办理
					logger.debug("若返回码为：23039  提示：同一证件号码下存在多个用户姓名，不限制用户办理业务");
				}else{
		    		String xRspDesc = "全国一证五号校验失败:";
		    		xRspDesc = xRspDesc + openNumInfos.getData(0).getString("X_RESULTINFO", "");
		    		CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
				}
			}else if(StringUtils.isBlank(openNumInfos.getData(0).getString("X_RESULTCODE"))
							&&"2998".equals(openNumInfos.getData(0).getString("X_RSPCODE"))){
				//QR-20200316-04一证超五号问题核查
				//IBOSS调用一证五号平台校验,若调用平台超时,返回的错误编码字段没有X_RESULTCODE,但会返回X_RSPCODE=2998
				//因此这种场景会进这里,防止调用超时,且平台已超号,此处若超时场景进行拦截处理
				String xRspDesc = "全国一证五号校验失败:";
				xRspDesc = xRspDesc + openNumInfos.getData(0).getString("X_RSPDESC", "");
				CSAppException.apperr(CrmCommException.CRM_COMM_103,xRspDesc);
			}
		}
	}
	
	/**
	 * 比较两个日期大小，返回日期1和日期2相差的天数
	 * 
	 */
	public  IDataset compareTo(IData input) throws Exception
	{
		String date1 = input.getString("DATA1");
		String date2 = input.getString("DATA2");
		String sql_ref = " SELECT TRUNC(TO_CHAR(TO_DATE('" + date1 + "','yyyy-mm-dd hh24:mi:ss')-TO_DATE('" + date2 + "','yyyy-mm-dd hh24:mi:ss'))) OUTSTR FROM DUAL ";
		return Dao.qryBySql(new StringBuilder(sql_ref),input);
	}
	
	/**
	 * 端午节活动
	 * <br/>
	 * 判断主号码,是否办理和多号业务
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20170516
	 */
	public IDataset checkIsOneCardMultiNo(IData input) throws Exception {
		String serial_number=input.getString("SERIAL_NUMBER");
		//首先判断是否是一卡多号号码
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		if(null==user||user.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String user_id=user.getString("USER_ID");
		IDataset relationListTmp=OneCardMultiNoQry.qryRelationList(user_id,RELATION_TYPE_CODE,null,null);
		return relationListTmp;
	}
	
	
	public IDataset getViceAsynInfo(String sna,String snb)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TP_F_HDH_SYNINFO WHERE SERIAL_NUMBER=:SERIAL_NUMBER AND SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
		sql.append(" AND END_DATE >SYSDATE ");
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sna);
		param.put("SERIAL_NUMBER_B", snb);
		
		return Dao.qryBySql(sql, param,Route.CONN_CRM_CEN);
	}
	
	public int updateViceAsynInfo(IData param)throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TP_F_HDH_SYNINFO ");
		sql.append("SET ADDRESS =:ADDRESS, ");
		sql.append("F_PICNAME_Z =:F_PICNAME_Z, ");
		sql.append("F_PICNAME_T =:F_PICNAME_T, ");
		sql.append("F_PICNAME_F =:F_PICNAME_F ");
		sql.append(" WHERE    end_date >sysdate and  SERIAL_NUMBER =:SERIAL_NUMBER");
		sql.append("SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
		
		return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
	}

	public void picInfoSyn(IData input) throws Exception {
		// TODO Auto-generated method stub
		/*IDataUtil.chkParam(input, "F_PICNAME_T");// 用户头像图片文件名称
    	IDataUtil.chkParam(input, "F_PICNAME_Z");// 身份证正面图片文件名称
    	IDataUtil.chkParam(input, "F_PICNAME_F");// 身份证反面图片文件名称  	
    	String resultstate = "";//结果处理状态。0失败，1成功。空为未处理
    	//匹配其他台账表中的6.7结果和信息补录的接口插入的其他台账表相关图片信息，找到对应的图片文件名，进行比对，将比对的结果，更新至TL_B_HDH_PICSYN图片信息同步表,将结果处理状态字段进行修改
		IDataset synInfo = Dao.qryByCode("TP_F_HDH_SYNINFO", "SEL_BY_PICNAME", input);
		if(null == synInfo  || synInfo.isEmpty()){
			resultstate = "0";
		}else{			
			resultstate = "1";
		}
		input.put("RESULT_STATE", resultstate);
		ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
		bean.picInfoSyn(input);*/
		
		String picT = input.getString("F_PICNAME_T");// 用户头像图片文件名称
    	String picZ = input.getString("F_PICNAME_Z");// 身份证正面图片文件名称
    	String picF = input.getString("F_PICNAME_F");// 身份证反面图片文件名称  	
		String seq = input.getString("PLAT_SEQ_ID");//交易包流水号
		String sn = input.getString("SERIAL_NUMBER");//交易包流水号
		String snb = input.getString("SERIAL_NUMBER_B");//交易包流水号
		String address = input.getString("ADDRESS");
		//String eparchycode = input.getString("EPARCHY_CODE");//地州编码
    	/*String resultstate = "";//结果处理状态。0失败，1成功。空为未处理
    	//匹配其他台账表中的6.7结果和信息补录的接口插入的其他台账表相关图片信息，找到对应的图片文件名，进行比对，将比对的结果，更新至TL_B_HDH_PICSYN图片信息同步表,将结果处理状态字段进行修改
		IDataset synInfo = Dao.qryByCode("TP_F_HDH_SYNINFO", "SEL_BY_PICNAME", input);
		if(null == synInfo  || synInfo.isEmpty()){
			resultstate = "0";
		}else{			
			IData picinfo = synInfo.getData(0);
			if(picT.equals(picinfo.getString("F_PICNAME_T")) && picZ.equals(picinfo.getString("F_PICNAME_Z")) && picF.equals(picinfo.getString("F_PICNAME_F"))){
				resultstate = "1";
			}else{
				resultstate = "0";
			}			
		}
		IData indata =new DataMap();
		indata.put("SEQ_ID", seq);
		indata.put("RESULT_STATE", resultstate);
		Dao.update("TL_B_HDH_PICSYN", indata, new String[]
                { "SEQ_ID", "RESULT_STATE"}, Route.CONN_CRM_CEN);*/
		
		ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
		
		IDataset synInfo = bean.qryHdhPicSynInfo(seq);
		
		if(IDataUtil.isNotEmpty(synInfo))
		{
			IData dc = synInfo.getData(0);
			String sPicT = dc.getString("F_PICNAME_T");
			String sPicZ = dc.getString("F_PICNAME_Z");
			String sPicF = dc.getString("F_PICNAME_F");
			
			String state ="0";
			if(StringUtils.isNotBlank(sPicT) && StringUtils.isNotBlank(sPicZ) && StringUtils.isNotBlank(sPicF))
			{
				state ="1";
				if(StringUtils.equals(picT, sPicT) && StringUtils.equals(picZ, sPicZ) && StringUtils.equals(picF, sPicF))
				{
					
				}
				else
				{
					IData param = new DataMap();
					param.put("SERIAL_NUMBER", sn);
					param.put("SERIAL_NUMBER_B", snb);
					param.put("F_PICNAME_T", sPicT);
					param.put("F_PICNAME_Z", sPicZ);
					param.put("F_PICNAME_F", sPicF);
					param.put("ADDRESS", address);
					param.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
					param.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
					bean.updateViceAsynInfo(param);
				}
			}
			else
			{
				state ="0";
			}
			IData indata =new DataMap();
			indata.put("SEQ_ID", seq);
			indata.put("RESULT_STATE", state);
			Dao.update("TL_B_HDH_PICSYN", indata, new String[]
	                { "SEQ_ID", "RESULT_STATE"}, Route.CONN_CRM_CEN);
		}
	}
	
	public IData CheckMainRealInfo(IData inputParam) throws Exception{
		IData resultData = new DataMap();
		IData input = inputParam.getDataset("MOS_CHECK").getData(0);
		String serialNumber = input.getString("MSISDN");
        IDataUtil.chkParam(input, "MSISDN");// 主号码
        IDataUtil.chkParam(input, "SEQ");// 本次操作的流水号
        IDataUtil.chkParam(input, "BIZ_TYPE");// 业务类型代码 74-国内一卡多号业务
        IDataUtil.chkParam(input, "CHANNEL");// 受理渠道 01-WEB， 03-WAP，04-SMS，70-客户端
		
        resultData.put("SEQ", input.getString("SEQ"));
        resultData.put("X_RESULTCODE", "0000");
        resultData.put("X_RESULTINFO", "主号实名信息不完整");
        resultData.put("INTEGRITY", "1");
        
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String psptAddress = uca.getCustPerson().getPsptAddr();
		String custName = uca.getCustPerson().getCustName();
		String psptId = uca.getCustomer().getPsptId();
		String psptType = uca.getCustomer().getPsptTypeCode();
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PSPT_ID", psptId);
		
		if("1".equals(uca.getCustomer().getIsRealName()) && StringUtils.isNotBlank(psptAddress) 
				&& StringUtils.isNotBlank(custName) && StringUtils.isNotBlank(psptId)&& StringUtils.isNotBlank(psptType)){
			resultData.put("X_RESULTCODE", "0000");
			resultData.put("X_RESULTINFO", "验证通过");
			resultData.put("INTEGRITY", "0");
		}
		return resultData;
	}
}