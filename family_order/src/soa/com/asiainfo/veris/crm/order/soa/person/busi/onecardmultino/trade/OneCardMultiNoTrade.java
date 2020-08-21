package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.trade;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;

public class OneCardMultiNoTrade extends BaseTrade implements ITrade {
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String flag=oneCardMultiNoReqData.getFlag();
		btd.getMainTradeData().setRsrvStr1(oneCardMultiNoReqData.getChannel_id());
		if("1".equals(flag)){
			oneCardMultiNoReqData.setRemark("营业厅：取消单个副号码");
			this.cancelOneForCRM(btd);
		}
		else if("2".equals(flag)){
			/**
			 * 计件薪酬功能，将IBOSS返回的工号MS_OPCODE保存到预留字段RsrvStr2
			 * by MengQX 2018-10-16
			 */
			btd.getMainTradeData().setRsrvStr2(oneCardMultiNoReqData.getMsOpCode());

			oneCardMultiNoReqData.setRemark("一卡多号平台：申请添加副号码");
			this.applyOneForEC(btd);
		}
		else if("3".equals(flag)){
			oneCardMultiNoReqData.setRemark("一卡多号平台：取消单个副号码");
			this.cancelOneForEC(btd);
		}
		else if("4".equals(flag)){
			oneCardMultiNoReqData.setRemark("二次短信确认：接收二次短信确认后取消号码，根据参数确定是取消单个还是取消全部");
			this.cancelForSMS(btd);
		}
		else if("6".equals(flag)){
			oneCardMultiNoReqData.setRemark("0000查询及退订和多号：取消单个副号码");
			this.cancelOneForCRM(btd);
		}
//		else if("5".equals(flag)){
//			oneCardMultiNoReqData.setRemark("营业厅：添加单个副号码");
//			this.applyOneForCRM(btd);
//		}
	}
	/**
	 * 营业厅取消关系，生成台帐取消关系
	 */
	public IData cancelOneForCRM(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		IData resultData = new DataMap();
		String serial_number=oneCardMultiNoReqData.getSerialNumber(); // 主号
		String orderno=oneCardMultiNoReqData.getOrderno(); //副号序号
		String serial_number_b=oneCardMultiNoReqData.getSerial_number_b();
		System.out.println("serial_number_b======" + serial_number_b);
		IData inputParam = new DataMap();
		inputParam.put("SERIAL_NUMBER", serial_number);
		IDataset relationList = new DatasetList();
		if("".equals(orderno) || "undefined".equals(orderno)){
			orderno = null;
		}
		relationList=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b,orderno);
		if(IDataUtil.isEmpty(relationList)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到相应的订购关系！");
		}
		IData relation=relationList.getData(0);
		serial_number_b=relation.getString("SERIAL_NUMBER_B");
		// 取消 台帐
		inputParam.put("ORDERNO", relation.getString("ORDERNO"));
		inputParam.put("SERIAL_NUMBER_B", serial_number_b);
		inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
		OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
		
//		inputParam.put("OPER_CODE", PlatConstants.OPER_USER_DATA_MODIFY);
//		inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_OK);
//		inputParam.put("RSRV_STR8", OneCardMultiNoBean.OPER_CODE_CANCEL);
		
		inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
		inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
		inputParam.put("RSRV_STR8", null);
		
		inputParam.put("RELATE_INST_ID", relationList.getData(0).getString("RSRV_STR5"));
		// 最后一个取消，则删除优惠、退订服务 
		IDataset relationListAll=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
		if (null!=relationListAll && 1 == relationListAll.size()) {
			OneCardMultiNoTradeUtil.cancelTradeDiscntForMonth(btd,inputParam);
//			inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
//			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
//			inputParam.put("RSRV_STR8", null);
		}
		//inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
		inputParam.put("IS_NEED_PF", "YES");
		OneCardMultiNoTradeUtil.cancelTradePlatSVC(btd,inputParam);
		
		OneCardMultiNoTradeUtil.cancelTradeDiscnt(btd,inputParam);
		
//		// 调用网状网接口同步解除关系
//		inputParam.put("OPR_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL);
//		if (null!=relationList && 1 == relationList.size()) {
//			inputParam.put("OPR_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL_ALL);
//		}
//		inputParam.put("REC_NUM", "1");
//		IDataset follow_infoList = new DatasetList();
//		IData follow_info = new DataMap();
//		follow_info.put("SERIAL_NUM", orderno);
//		follow_info.put("FOLLOW_MSISDN", serial_number_b);
//		follow_infoList.add(follow_info);
//		inputParam.put("FOLLOW_INFOLIST", follow_infoList);
//		OneCardMultiNoTradeUtil.updateRelationsCallIBoss(inputParam);
		return resultData;
	}
	
//	/**
//	 * 营业厅办理一卡多号业务
//	 */
//	public IData applyOneForCRM(BusiTradeData btd) throws Exception {
//		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
//		IData resultData = new DataMap();
//		String serial_number=oneCardMultiNoReqData.getSerialNumber(); // 主号
//		String orderno=oneCardMultiNoReqData.getOrderno(); //副号序号
//		String serial_number_b=oneCardMultiNoReqData.getSerial_number_b();
//		IData inputParam = new DataMap();
//		inputParam.put("SERIAL_NUMBER", serial_number);
//		
//		// 调用网状网接口同步申请办理一卡多号
//		inputParam.put("OPR_CODE", OneCardMultiNoBean.OPER_CODE_APPLY);
//		
//		inputParam.put("REC_NUM", "1");
//		IDataset follow_infoList = new DatasetList();
//		IData follow_info = new DataMap();
//		follow_info.put("SERIAL_NUM", orderno);
//		follow_info.put("FOLLOW_MSISDN", serial_number_b);
//		follow_infoList.add(follow_info);
//		inputParam.put("FOLLOW_INFOLIST", follow_infoList);
//		OneCardMultiNoTradeUtil.updateRelationsCallIBoss(inputParam);
//		return resultData;
//	}
	
	
	/**
	 * 一卡多号号码申请 台帐
	 */
	public IData applyOneForEC(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		IData resultData = new DataMap();
		String serial_number=oneCardMultiNoReqData.getSerialNumber(); // 主号
		String orderno=oneCardMultiNoReqData.getOrderno(); //副号序号
		String serial_number_b=oneCardMultiNoReqData.getSerial_number_b(); // 副号码
		String user_id = btd.getRD().getUca().getUserId();
		
		IData inparams = new DataMap();
		inparams.put("SUBSYS_CODE", "CSM");
		inparams.put("PARAM_ATTR", "9999");
		inparams.put("PARAM_CODE", "CHECK_QUALIFICATIONS");
		
		IDataset Qulification = Dao.qryByCode("TD_S_COMMPARA", "SEL_PARAM_BY_CODE", inparams);
		
		for(int i = 0;i<Qulification.size();i++){
			String biz_code = Qulification.getData(i).getString("PARA_CODE2");
			String sp_code = Qulification.getData(i).getString("PARA_CODE1");
			if(biz_code.equals(oneCardMultiNoReqData.getBiz_Code())&&sp_code.equals(oneCardMultiNoReqData.getSp_code())){
				IData inparam = new DataMap();
				inparam.put("USER_ID", user_id);
				inparam.put("REMOVE_TAG", "0");
				IDataset UserInfos = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", inparam);
				if(IDataUtil.isNotEmpty(UserInfos)){
					String open_date = UserInfos.getData(0).getString("OPEN_DATE");
					String sysdate = SysDateMgr.getSysTime();
					IData input = new DataMap();
					input.put("DATA1", sysdate);
					input.put("DATA2", open_date);
					//如果开户时间超过30天，不允许办理
					String DayCount = compareTo(input).getData(0).getString("OUTSTR");
					
					String[] Value = DayCount.split("\\.");
					int dayCount = Integer.parseInt(Value[0]);
					
					if(dayCount > 30){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "老用户不允许办理此业务！");
					}
				}
				break;
			}
		}
		
		IData inputParam = new DataMap();
		
		String relate_inst_id = SeqMgr.getInstId();
		
		inputParam.put("RELATE_INST_ID", relate_inst_id);
		inputParam.put("SERIAL_NUMBER", serial_number);
		
		//查询对应BIZ_CODE和SP_CODE的优惠信息
		IData input= new DataMap();
		input.put("BIZ_CODE", oneCardMultiNoReqData.getBiz_Code());
		input.put("SP_CODE",oneCardMultiNoReqData.getSp_code());
		IDataset ServiceList = PlatInfoQry.getBizInfo(input);
		if(IDataUtil.isEmpty(ServiceList)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "对应的平台服务不存在，请先进行配置！");
		}
		inputParam.put("BIZ_CODE", oneCardMultiNoReqData.getBiz_Code());
		inputParam.put("SPID", oneCardMultiNoReqData.getSp_code());
		String discnt_code = ServiceList.getData(0).getString("RSRV_STR2");
		
		//如果是免费，设置优惠标志DISCNT_FLAG为true，将免费月份值放入FREE_MOTH。
		if(discnt_code!=null && !("".equals(discnt_code))){		
			inputParam.put("UU_END_FLAG", "ture");
		}
		
		//查询是否已经在UU关系中绑定对应的副号码
		IDataset relationListAll=OneCardMultiNoQry.qryRelationList( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
		IDataset relationList=OneCardMultiNoQry.qryRelationListNew( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b);
		
		//如果有两条或者两条以上的，不在重复订购
		if(2<=relationList.size()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + serial_number + "】存在副号为序号：【"+ orderno + "】订购关系，重复订购！");
		}
		//如果有且只有一条有效的，认为是续订
		else if(1==relationList.size()){
			
			//续订必须传入EFFECTIVE_TIME，否则报错。
			if("".equals(oneCardMultiNoReqData.getEffective_time())){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "续订时必须传入生效时间");
			}
			
			//设置续订标识为true
			inputParam.put("RE_ORDER_FLAG", "true");
			inputParam.put("RELATE_INST_ID", relationList.getData(0).getString("RSRV_STR5"));
			
			//按照接口传来的时间格式将日期取出来
			SimpleDateFormat origFormater = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = origFormater.parse(oneCardMultiNoReqData.getEffective_time());
			//将取出来的日期转换成需要的格式
			SimpleDateFormat targetFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String effective_time= targetFormater.format(date);
			
			inputParam.put("EFFECTIVE_TIME", effective_time);
			
			//号码申请 台帐
			inputParam.put("ORDERNO", orderno);
			inputParam.put("SERIAL_NUMBER_B", serial_number_b);
			inputParam.put("MODIFY_TAG",BofConst.MODIFY_TAG_ADD);
			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_OK);
			
			//将当前生效的UU关系的end_date设置为EFFECTIVE_TIME
			OneCardMultiNoTradeUtil.updateTradeRelation(btd,inputParam);
			
			//将Platsvc当前生效的记录的end_date设置为EFFECTIVE_TIME
			OneCardMultiNoTradeUtil.updateTradePlatSVC(btd,inputParam);
			
			//将Discnt当前生效的记录的end_date设置为EFFECTIVE_TIME
			OneCardMultiNoTradeUtil.updateTradeDiscnt(btd, inputParam);
			
			//新增订购关系之前，生成一个新的RELATE_INST_ID，将之前的值覆盖
			relate_inst_id = SeqMgr.getInstId();
			inputParam.put("RELATE_INST_ID", relate_inst_id);
			
			//新增一条start_date为EFFECTIVE_TIME的UU关系
			OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
			
			//新增一条start_date为EFFECTIVE_TIME的记录
			OneCardMultiNoTradeUtil.registerTradePlatSVC(btd,inputParam);
				
			//如果discnt_code有值，增加一条减免月租优惠
			if(discnt_code!=null && !("".equals(discnt_code))){	
				inputParam.put("DISCNT_CODE", discnt_code);
				OneCardMultiNoTradeUtil.registerTradeDiscnt(btd, inputParam);	
			}			
		}
		else{
			//号码申请 台帐
			inputParam.put("ORDERNO", orderno);
			inputParam.put("SERIAL_NUMBER_B", serial_number_b);
			inputParam.put("MODIFY_TAG",BofConst.MODIFY_TAG_ADD);
			
			//登记UU信息表
			OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
			
			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_OK);
			
			// 没有有效的副号码则添加 月租优惠、服务
			if(IDataUtil.isEmpty(relationListAll)){
				OneCardMultiNoTradeUtil.registerTradeDiscntForMonth(btd, inputParam);
			}
			
			OneCardMultiNoTradeUtil.registerTradePlatSVC(btd,inputParam);
			
			//如果discnt_code有值，增加一条减免月租优惠
			if(discnt_code!=null && !("".equals(discnt_code))){	
				inputParam.put("DISCNT_CODE", discnt_code);
				OneCardMultiNoTradeUtil.registerTradeDiscnt(btd, inputParam);	
			}
		}
		resultData.put("X_RESULT_CODE", "0");
		return resultData;
	}
	/**
	 * 一卡多号副号码取消 台帐 无需调用IBOSS同步
	 */
	public IData cancelOneForEC(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		IData resultData = new DataMap();
		String serial_number=oneCardMultiNoReqData.getSerialNumber(); // 主号
		String orderno=oneCardMultiNoReqData.getOrderno(); //副号序号
		String serial_number_b=oneCardMultiNoReqData.getSerial_number_b(); // 副号码
		
		IData inputParam = new DataMap();
		inputParam.put("SERIAL_NUMBER", serial_number);
		IDataset relationList=OneCardMultiNoQry.qryRelationList( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
		IDataset relations=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b,orderno);
		if(null==relations||relations.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + serial_number + "】不存在副号为序号：【"+ orderno + "】订购关系，不能业务办理！");
		}
		serial_number_b=relations.getData(0).getString("SERIAL_NUMBER_B");
		inputParam.put("RELATE_INST_ID", relations.getData(0).getString("RSRV_STR5"));
		// 取消 台帐
		inputParam.put("ORDERNO", orderno);
		inputParam.put("SERIAL_NUMBER_B", serial_number_b);
		inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
		OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
//		inputParam.put("OPER_CODE", PlatConstants.OPER_USER_DATA_MODIFY);
//		inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_OK);
//		inputParam.put("RSRV_STR8", OneCardMultiNoBean.OPER_CODE_CANCEL);
		
		inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
		inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
		inputParam.put("RSRV_STR8", null);
		
		// 最后一个取消，则删除优惠、退订服务 
		if (null!=relations && 1 == relationList.size()) {
			OneCardMultiNoTradeUtil.cancelTradeDiscntForMonth(btd,inputParam);
//			inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
//			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
//			inputParam.put("RSRV_STR8", null);
		}
		
		OneCardMultiNoTradeUtil.cancelTradePlatSVC(btd,inputParam);
		
		inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
		OneCardMultiNoTradeUtil.cancelTradeDiscnt(btd,inputParam);
		
		resultData.put("X_RESULT_CODE", "0");
		resultData.put("X_RESULT_INFO", "业务取消成功");
		return resultData;
	}
	
	
	/**
	 * 一卡多号变更接口：提供短厅，接收到二次短信确认后，生成台帐取消关系，需调用IBOSS同步
	 */
	public IData cancelForSMS(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		IData resultData = new DataMap();
		String serial_number=oneCardMultiNoReqData.getSerialNumber(); // 主号
		String orderno=oneCardMultiNoReqData.getOrderno(); //副号序号
		String serial_number_b=oneCardMultiNoReqData.getSerial_number_b(); // 副号码
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
	        if (IDataUtil.isEmpty(userInfo))
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_112);
	        }

	    String userId = userInfo.getString("USER_ID");
		IData inputParam = new DataMap();
		inputParam.put("SERIAL_NUMBER", serial_number);
		
		//二次短信确认校验
		IDataset twoSmsCheckList = OneCardMultiNoQry.qryTwoSmsCheck(serial_number,oneCardMultiNoReqData.getForce_object().substring(8));
		if(null==twoSmsCheckList||twoSmsCheckList.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"不存在短信二次确认的信息或短信二次确认信息已经失效！");
		}
	
		//首先判断是否是一卡多号号码
		IDataset relationList=OneCardMultiNoQry.qryRelationList( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
		if(null==relationList||relationList.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + serial_number + "】不是一卡多号用户，不能进行办理！");
			/*resultData.put("X_RESULTCODE", "1001");
			resultData.put("X_RESULTINFO", "主号【" + serial_number + "】不是一卡多号用户，不能进行办理！");
			return resultData;*/
		}
		StringBuilder SMSInfo = new StringBuilder();
		SMSInfo.append("尊敬的用户，您已成功取消副号,");
		//取消全部处理
		if("4".equals(orderno)){
			inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
			for (int index = 0; index < relationList.size(); index++) {
				inputParam.put("ORDERNO", relationList.getData(index).getString("ORDERNO"));
				inputParam.put("SERIAL_NUMBER_B", relationList.getData(index).getString("SERIAL_NUMBER_B"));
				
				inputParam.put("RELATE_INST_ID", relationList.getData(index).getString("RSRV_STR5"));
				
				OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
				
				OneCardMultiNoTradeUtil.cancelTradeDiscnt(btd,inputParam);
				inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
				inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
				inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
				inputParam.put("IS_NEED_PF", "YES");			
				OneCardMultiNoTradeUtil.cancelTradePlatSVC(btd,inputParam);
			}
			OneCardMultiNoTradeUtil.cancelTradeDiscntForMonth(btd,inputParam);
			
			//同步平台
			inputParam.put("OPR_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL_ALL);
			inputParam.put("REC_NUM", relationList.size());
			IDataset follow_infoList = new DatasetList();
			IData follow_info = new DataMap();
			
			for (int index = 0; index < relationList.size(); index++) {
				follow_info.put("FOLLOW_MSISDN", relationList.getData(index).getString("SERIAL_NUMBER_B"));
				follow_info.put("SERIAL_NUM", relationList.getData(index).getString("ORDERNO"));
				SMSInfo.append(relationList.getData(index).getString("ORDERNO"));
				SMSInfo.append("、");
				SMSInfo.append(relationList.getData(index).getString("SERIAL_NUMBER_B"));
				SMSInfo.append(";");
				follow_infoList.add(follow_info);
			}
			inputParam.put("FOLLOW_INFOLIST",follow_infoList);
			resultData.put("FOLLOW_INFOLIST",follow_infoList);
		}
		// 取消单个
		else {
			IDataset relations=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b,orderno);
			if(null==relations||relations.isEmpty()){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + serial_number + "】不存在副号为序号：【"+ orderno + "】订购关系，不能业务办理！");
			}
			serial_number_b=relations.getData(0).getString("SERIAL_NUMBER_B");
			inputParam.put("RELATE_INST_ID", relations.getData(0).getString("RSRV_STR5"));
			inputParam.put("ORDERNO", orderno);
			inputParam.put("SERIAL_NUMBER_B", serial_number_b);
			inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
			OneCardMultiNoTradeUtil.registerTradeRelation(btd,inputParam);
//			inputParam.put("OPER_CODE", PlatConstants.OPER_USER_DATA_MODIFY);
//			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_OK);
//			inputParam.put("RSRV_STR8", OneCardMultiNoBean.OPER_CODE_CANCEL);
			
			inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
			inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
			inputParam.put("RSRV_STR8", null);
			
			// 最后一个取消，则删除优惠、退订服务
			if (null!=relationList&&1 == relationList.size()) {
				OneCardMultiNoTradeUtil.cancelTradeDiscntForMonth(btd,inputParam);
				inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
				inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
				inputParam.put("RSRV_STR8", null);
			}
			inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
			inputParam.put("IS_NEED_PF", "YES");
			OneCardMultiNoTradeUtil.registerTradePlatSVC(btd,inputParam);
			
			OneCardMultiNoTradeUtil.cancelTradeDiscnt(btd,inputParam);
			
//			// 调用网状网接口同步解除关系
//			inputParam.put("OPER_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL);
//			if (null!=relationList&&1 == relationList.size()) {
//				inputParam.put("OPER_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL_ALL);
//			}
//			inputParam.put("REC_NUM", "1");
//			IDataset follow_infoList = new DatasetList();
//			IData follow_info = new DataMap();
//			follow_info.put("SERIAL_NUM", orderno);
//			follow_info.put("FOLLOW_MSISDN", serial_number_b);
//			SMSInfo.append(orderno);
//			SMSInfo.append("、");
//			SMSInfo.append(serial_number_b);
//			follow_infoList.add(follow_info);
//			inputParam.put("FOLLOW_INFOLIST", follow_infoList);
//			resultData.put("FOLLOW_INFOLIST",follow_infoList);
		}
		//同步平台
//		OneCardMultiNoTradeUtil.updateRelationsCallIBoss(inputParam);
		//给用户发成功办理短信
		IData SMSinfo = new DataMap();
		String sysDate = SysDateMgr.getSysTime();
		String sms_notice_id=SeqMgr.getSmsSendId();
		SMSinfo.put("SMS_NOTICE_ID",sms_notice_id);
		SMSinfo.put("PARTITION_ID", sms_notice_id.substring(sms_notice_id.length() - 4));
		SMSinfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		SMSinfo.put("BRAND_CODE", "");
		SMSinfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		SMSinfo.put("CHAN_ID",OneCardMultiNoBean.BIZ_TYPE_CODE);//短信渠道编码:平台编码
		SMSinfo.put("SMS_NET_TAG","0");
		SMSinfo.put("SEND_OBJECT_CODE","6");	
		SMSinfo.put("SEND_COUNT_CODE","1");
		SMSinfo.put("SEND_TIME_CODE","1");
		SMSinfo.put("RECV_OBJECT_TYPE","00");//被叫对象类型:00－手机号码
		SMSinfo.put("RECV_OBJECT",serial_number);//被叫对象:传手机号码
		SMSinfo.put("RECV_ID",userId);//被叫对象标识:传用户标识
		SMSinfo.put("SMS_TYPE_CODE","20");//短信类型:20-业务通知
		SMSinfo.put("SMS_KIND_CODE","02");//短信种类:02－短信通知
		SMSinfo.put("NOTICE_CONTENT_TYPE","0");//短信内容类型:0－指定内容发送
		SMSinfo.put("REFERED_COUNT","0");
		SMSinfo.put("FORCE_REFER_COUNT","1");//指定发送次数
		SMSinfo.put("FORCE_OBJECT", oneCardMultiNoReqData.getForce_object());
		SMSinfo.put("FORCE_START_TIME",sysDate);//指定发送次数
		SMSinfo.put("FORCE_END_TIME", "");
		SMSinfo.put("SMS_PRIORITY",1000);//短信优先级
		SMSinfo.put("REFER_TIME",sysDate);//提交时间
		SMSinfo.put("REFER_STAFF_ID",CSBizBean.getVisit().getStaffId());//提交员工
		SMSinfo.put("REFER_DEPART_ID",CSBizBean.getVisit().getDepartId());//提交部门
		SMSinfo.put("DEAL_TIME",sysDate);//处理时间
		SMSinfo.put("DEAL_STAFFID", CSBizBean.getVisit().getStaffId());
		SMSinfo.put("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId());
		SMSinfo.put("DEAL_STATE","15");//处理状态:0－未处理
		SMSinfo.put("REMARK", "一卡多号业务取消二次短信下发");
		SMSinfo.put("MONTH",Integer.parseInt(sysDate.substring(5, 7)));
		SMSinfo.put("DAY",Integer.parseInt(sysDate.substring(8, 10)));
		
		SMSInfo.append("，感谢您的使用。中国移动");
		SMSinfo.put("NOTICE_CONTENT", SMSInfo);// 短信内容类型:0－指定内容发送
		
		SmsSend.insSms(SMSinfo);
		
		//短信二次确认完成后，更新短信二次确认表的状态信息
		IData inparam = new DataMap();
		inparam.put("TRADE_ID", 		twoSmsCheckList.getData(0).getString("TRADE_ID"));
		inparam.put("SERIAL_NUMBER", 	twoSmsCheckList.getData(0).getString("SERIAL_NUMBER"));
		StringBuilder parser = new StringBuilder();
		parser.append(" UPDATE TF_B_TWO_CHECK ");
		parser.append("    SET REVERT_DATE        = SYSDATE, ");
		parser.append("        EXEC_FLAG			= '1'" );
		parser.append("  WHERE TRADE_ID = :TRADE_ID  ");
		parser.append("    AND SERIAL_NUMBER = :SERIAL_NUMBER ");  
        Dao.executeUpdate(parser, inparam);
        
		resultData.put("X_RESULTCODE", "0");
		resultData.put("X_RESULTINFO", "Trade OK!");
		return resultData;
	}
	
	/**
	 * 比较两个日期大小，返回日期1和日期2相差的天数
	 * 
	 */
	public  IDataset compareTo(IData input) throws Exception
	{
		String date1 = input.getString("DATA1");
		String date2 = input.getString("DATA2");
		String sql_ref = " SELECT TO_CHAR(TO_DATE('" + date1 + "','yyyy-mm-dd hh24:mi:ss')-TO_DATE('" + date2 + "','yyyy-mm-dd hh24:mi:ss')) OUTSTR FROM DUAL ";
		return Dao.qryBySql(new StringBuilder(sql_ref),input);
	}
}