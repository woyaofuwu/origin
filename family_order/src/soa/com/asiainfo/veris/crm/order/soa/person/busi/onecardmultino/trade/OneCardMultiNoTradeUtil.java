package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.trade;



import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;

@SuppressWarnings("unchecked")
public class OneCardMultiNoTradeUtil{
	/**
	 * 拼装关系表子台帐 TF_B_TRADE_RELATION
	 */
	public static  void registerTradeRelation(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String serial_number_b=inputParam.getString("SERIAL_NUMBER_B");
		String orderno=inputParam.getString("ORDERNO");
		String start_date=SysDateMgr.getSysTime();
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		String category=oneCardMultiNoReqData.getCategory(); //副号类型 0  1
		String new_serial_number =inputParam.getString("NEW_SERIAL_NUMBER","");//新号码
		
		//TODO huanghua 此处老逻辑无用
		String serviceId = "";
		//一卡多号业务增加副号码服务标识取值  added ouyang begin
//		String sp_code = inputParam.getString("SPID");
//		String biz_code = inputParam.getString("BIZ_CODE");
//		IDataset bizInfo = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(biz_code, sp_code);
//		
//		if (null != bizInfo && bizInfo.size() > 0){
//			serviceId = bizInfo.getData(0).getString("SERVICE_ID");
//		}
		//一卡多号业务增加副号码服务标识取值  added ouyang end
		
		if(!BofConst.MODIFY_TAG_ADD.equals(modify_tag)){
			IDataset relationList=OneCardMultiNoQry.qryRelationList( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b,orderno);
			if(null==relationList||relationList.isEmpty()){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"一卡多号订购数据错误");
			}
			IData relation=relationList.getData(0);
			start_date=relation.getString("START_DATE");
			orderno=relation.getString("ORDERNO");
			category=relation.getString("USER_ID_B").substring(relation.getString("USER_ID_B").length()-1);
			inst_id=relation.getString("INST_ID");
			serviceId = relation.getString("RSRV_STR3");
		}
		
		RelationTradeData relationTradeData = new RelationTradeData();
		relationTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
		relationTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		relationTradeData.setSerialNumberA(serial_number);
		relationTradeData.setUserIdB(serial_number_b+category);
		relationTradeData.setSerialNumberB(serial_number_b);
		relationTradeData.setOrderno(orderno);
		relationTradeData.setUserIdA(user_id);
		relationTradeData.setInstId(inst_id);
		relationTradeData.setStartDate(start_date);		
		
		//如果是续订，将开始时间设置成EFFECTIVE_TIME
		if("true".equals(inputParam.getString("RE_ORDER_FLAG"))){
			relationTradeData.setStartDate(inputParam.getString("EFFECTIVE_TIME"));
		}
		
		relationTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);	
	
		if(BofConst.MODIFY_TAG_ADD.equals(modify_tag)){
			relationTradeData.setRsrvStr5(inputParam.getString("RELATE_INST_ID"));
		}
		if(!BofConst.MODIFY_TAG_ADD.equals(modify_tag)){
			
			relationTradeData.setEndDate(SysDateMgr.getSysTime());
		}
	
		relationTradeData.setRoleTypeCode("0");
		relationTradeData.setRoleCodeA("0");
		relationTradeData.setRoleCodeB("1");
		String tag = StaticUtil.getStaticValue("HDH_SEQ_SWICH", "OFF");
		if(StringUtils.isBlank(tag))
		{
			relationTradeData.setRsrvStr4(oneCardMultiNoReqData.getSeqId());//898这个字段意义不大，改为seq，现在要求各个接口使用统一seq
		}
		relationTradeData.setRsrvStr5(inputParam.getString("RELATE_INST_ID"));
		relationTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		relationTradeData.setRsrvStr3(serviceId);
		if(null!=new_serial_number&&!"".equals(new_serial_number)){
			relationTradeData.setSerialNumberA(new_serial_number);
			relationTradeData.setRsrvStr2(serial_number);
		}
		relationTradeData.setRsrvStr1(oneCardMultiNoReqData.getInherit());
		btd.add(serial_number, relationTradeData);
	}
	
	
	/**
	 * 拼装关系表子台帐 TF_B_TRADE_RELATION,将原来的uu关系的结束时间设置为EFFECTIVE_TIME
	 */
	public static  void updateTradeRelation(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();

		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String serial_number_b=inputParam.getString("SERIAL_NUMBER_B");
		String orderno=inputParam.getString("ORDERNO");
		
		String new_serial_number =inputParam.getString("NEW_SERIAL_NUMBER","");//新号码
		
		//查询当前生效的UU关系，并取出
		IDataset relationList=OneCardMultiNoQry.qryRelationListNew( btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,serial_number_b);
		IData relation=relationList.getData(0);

		RelationTradeData relationTradeData = new RelationTradeData(relation);
		relationTradeData.setEndDate(inputParam.getString("EFFECTIVE_TIME",""));
		relationTradeData.setModifyTag("2");//0:新增,1:删除,2:修改
//
//		relationTradeData.setRemark(relation.getString("REMARK",""));
//		relationTradeData.setModifyTag("2");//0:新增,1:删除,2:修改
//		relationTradeData.setSerialNumberA(relation.getString("SERIAL_NUMBER_A",""));
//		relationTradeData.setUserIdB(relation.getString("USER_ID_B",""));
//		relationTradeData.setSerialNumberB(relation.getString("SERIAL_NUMBER_B",""));
//		relationTradeData.setOrderno(relation.getString("ORDER_NO",""));
//		relationTradeData.setUserIdA(relation.getString("USER_ID_A",""));
//		relationTradeData.setInstId(relation.getString("INST_ID",""));
//		relationTradeData.setStartDate(relation.getString("START_DATE",""));	
//		
//		//end_date设置为EFFECTIVE_TIME
//		relationTradeData.setEndDate(inputParam.getString("EFFECTIVE_TIME",""));
//		
//		relationTradeData.setRoleTypeCode(relation.getString("ROLE_TYPE_CODE",""));
//		relationTradeData.setRoleCodeA(relation.getString("ROLE_CODE_A",""));
//		relationTradeData.setRoleCodeB(relation.getString("ROLE_CODE_B",""));
//		relationTradeData.setRsrvStr4(relation.getString("RSRV_STR4",""));
//		relationTradeData.setRsrvStr5(relation.getString("RSRV_STR5",""));
//		relationTradeData.setRelationTypeCode(relation.getString("RELATION_TYPE_CODE",""));
//		relationTradeData.setRsrvStr3(relation.getString("RSRV_STR3",""));
//		relationTradeData.setSerialNumberA(relation.getString("SERIAL_NUMBER_A",""));
//		relationTradeData.setRsrvStr2(relation.getString("RSRV_STR2",""));
//		relationTradeData.setRsrvStr1(relation.getString("RSRV_STR1",""));
		String tag = StaticUtil.getStaticValue("HDH_SEQ_SWICH", "OFF");
		if(StringUtils.isBlank(tag))
		{
			relationTradeData.setRsrvStr4(oneCardMultiNoReqData.getSeqId());//898这个字段意义不大，改为seq，现在要求各个接口使用统一seq
		}
		btd.add(serial_number, relationTradeData);
	}
	
	/**
	 * 拼装优惠表子台帐 TF_B_TRADE_DISCNT
	 */
	public static  void registerTradeDiscnt(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());

		String discnt_code=inputParam.getString("DISCNT_CODE");
		
		DiscntTradeData discntTradeData = new DiscntTradeData();
		discntTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
		discntTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		discntTradeData.setUserId(user_id);
		discntTradeData.setInstId(inst_id);	
		discntTradeData.setStartDate(start_date);
		
		//如果是续订，将开始时间设置成EFFECTIVE_TIME
		if("true".equals(inputParam.getString("RE_ORDER_FLAG"))){
			discntTradeData.setStartDate(inputParam.getString("EFFECTIVE_TIME"));
		}
		
		discntTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		
		discntTradeData.setUserIdA("-1");
		discntTradeData.setSpecTag("2"); // 特殊优惠标记
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");
		discntTradeData.setRsrvStr4(inputParam.getString("RELATE_INST_ID"));
		discntTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		discntTradeData.setElementId(discnt_code);
		discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
//		discntTradeData.setCampnId(discntInfo.getString("CAMPN_ID"));
		btd.add(serial_number, discntTradeData);		
	}
	
	/**
	 * 拼装优惠表子台帐 TF_B_TRADE_DISCNT，将原有优惠的end_date设置为EFFECTIVE_TIME
	 */
	public static  void updateTradeDiscnt(BusiTradeData btd,IData inputParam) throws Exception {

		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER");
		
		//查询当前 优惠
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		String relate=inputParam.getString("RELATE_INST_ID");
		
		IDataset discntList=OneCardMultiNoQry.getUserDiscntRelate(user_id, "M2", relate);
		
		if(IDataUtil.isNotEmpty(discntList)){
			
			IData discnt = discntList.getData(0);
			DiscntTradeData dtd = new DiscntTradeData(discnt);
			dtd.setEndDate(inputParam.getString("EFFECTIVE_TIME"));
			dtd.setModifyTag("2");
			//DiscntTradeData dtd = new DiscntTradeData();
//			dtd.setRemark(discnt.getString("REMARK",""));
//			dtd.setModifyTag("2");
//			dtd.setUserId(discnt.getString("USER_ID",""));
//			dtd.setInstId(discnt.getString("INST_ID",""));
//			dtd.setStartDate(discnt.getString("START_DATE",""));			
//		    dtd.setEndDate(inputParam.getString("EFFECTIVE_TIME"));			
//			dtd.setUserIdA(discnt.getString("USER_ID_A",""));
//			dtd.setSpecTag("2");
//			dtd.setPackageId("-1");
//			dtd.setProductId("-1");
//			dtd.setRelationTypeCode("M2");
//			dtd.setElementId(discnt.getString("DISCNT_CODE",""));
//			dtd.setRsrvStr4(discnt.getString("RSRV_STR4",""));
//			dtd.setRsrvStr5(discnt.getString("RSRV_STR5",""));
			
			btd.add(btd.getRD().getUca().getSerialNumber(), dtd);
		}	
	}
	
	public static void cancelTradeDiscnt(BusiTradeData btd,IData inputParam) throws Exception{
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER");
		String sysDate=SysDateMgr.getSysTime();
		String start_date=sysDate;
		String discnt_code="";
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		
		//查询当前 优惠
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		String relate=inputParam.getString("RELATE_INST_ID");
		
		IDataset discntList=OneCardMultiNoQry.getUserDiscntRelate(user_id, OneCardMultiNoBean.RELATION_TYPE_CODE, relate);
		
		if(IDataUtil.isNotEmpty(discntList)){
			discnt_code=discntList.getData(0).getString("DISCNT_CODE"); 
			start_date=discntList.getData(0).getString("START_DATE");
			inst_id=discntList.getData(0).getString("INST_ID");
			DiscntTradeData dtd = new DiscntTradeData();
			dtd.setRemark(inputParam.getString("REMARK","一卡多号业务"));
			dtd.setModifyTag(modify_tag);
			dtd.setUserId(user_id);
			dtd.setInstId(inst_id);
			dtd.setStartDate(start_date);			
		    dtd.setEndDate(SysDateMgr.getSysTime());			
			dtd.setUserIdA("-1");
			dtd.setSpecTag("2");
			dtd.setPackageId("-1");
			dtd.setProductId("-1");
			dtd.setRelationTypeCode("M2");
			dtd.setElementId(discnt_code);
			
			btd.add(btd.getRD().getUca().getSerialNumber(), dtd);
		}
		
	}
	
	public static  void cancelTradeDiscntForMonth(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		//查询优惠配置
		IDataset discnt_codeParamInfos = CommparaInfoQry.getCommpara("CSM","9999","DISCNT_CODE",btd.getRD().getUca().getUserEparchyCode());
		if(null==discnt_codeParamInfos||discnt_codeParamInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号优惠配置不存在，请先进行配置！");
		}

		String discnt_code=discnt_codeParamInfos.getData(0).getString("PARA_CODE1");

		IDataset discntList=OneCardMultiNoQry.getDiscntByUserId(user_id, discnt_code, OneCardMultiNoBean.RELATION_TYPE_CODE);
		if(null==discntList||discntList.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"一卡多号优惠数据错误");
		}
		start_date=discntList.getData(0).getString("START_DATE");
		inst_id=discntList.getData(0).getString("INST_ID");

		DiscntTradeData discntTradeData = new DiscntTradeData();
		discntTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
		discntTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		discntTradeData.setUserId(user_id);
		discntTradeData.setInstId(inst_id);	
		discntTradeData.setStartDate(start_date);
		discntTradeData.setEndDate(SysDateMgr.getSysTime());
  
		discntTradeData.setUserIdA("-1");
		discntTradeData.setSpecTag("2"); // 特殊优惠标记
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");

		discntTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		discntTradeData.setElementId(discnt_code);
		discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
		btd.add(serial_number, discntTradeData);		
	}
	/**
	 * 拼装服务表子台帐 TF_B_TRADE_PLATSVC
	 */
	public static  void cancelTradePlatSVC(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String first_date=SysDateMgr.getSysTime();
		String first_date_mon=SysDateMgr.getSysTime();
		String new_serial_number =inputParam.getString("NEW_SERIAL_NUMBER");//新号码
		
//		//使用传入的sp_code和biz_code查询对应的serviceid
//		IDataset platSvcInfos = OneCardMultiNoQry.queryPlatSVCInfoBySPInfo(oneCardMultiNoReqData.getSp_code(), oneCardMultiNoReqData.getBiz_Code());
//		if(IDataUtil.isEmpty(platSvcInfos)){
//			CSAppException.apperr(CrmCommException.CRM_COMM_103, "国内一卡多号局数据不存在，请先进行配置！");
//		}
//		
//		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
//		String service_id=platSvcInfos.getData(0).getString("SERVICE_ID");
		
		//查询当前 服务
		IData input = new DataMap();
		input.put("USER_ID", user_id);
		input.put("RSRV_STR4", inputParam.getString("RELATE_INST_ID"));
		IDataset platSVCList=OneCardMultiNoQry.getPlatInfoByRelateInstId(input);
		
		if(IDataUtil.isNotEmpty(platSVCList)){
			start_date=platSVCList.getData(0).getString("START_DATE");
			inst_id = platSVCList.getData(0).getString("INST_ID");
			first_date=platSVCList.getData(0).getString("FIRST_DATE");
			first_date_mon=platSVCList.getData(0).getString("FIRST_DATE_MON");
			String service_id=platSVCList.getData(0).getString("SERVICE_ID");
			String bill_type=platSVCList.getData(0).getString("RSRV_STR5");
			
			
			PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData();
			platSvcTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
			platSvcTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
			platSvcTradeData.setUserId(user_id);
			platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
			platSvcTradeData.setStartDate(start_date);
			//platSvcTradeData.setEndDate(start_date);
			platSvcTradeData.setEndDate(SysDateMgr.getSysTime());//结束时间取当前时间
			
			platSvcTradeData.setBizStateCode(inputParam.getString("BIZ_STATE_CODE","E")); //A-正常，N-暂停，E-退订，L-挂失
			platSvcTradeData.setOperCode(inputParam.getString("OPER_CODE",PlatConstants.OPER_USER_DATA_MODIFY)); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
			platSvcTradeData.setOprSource(inputParam.getString("OPR_SOURCE","08"));
			platSvcTradeData.setFirstDate(first_date);
			platSvcTradeData.setFirstDateMon(first_date_mon);
			platSvcTradeData.setIntfTradeId(inputParam.getString("INTF_TRADE_ID","0"));
			
			platSvcTradeData.setPackageId(OneCardMultiNoBean.PACKAGE_ID);
			platSvcTradeData.setProductId(OneCardMultiNoBean.PRODUCT_ID);
			platSvcTradeData.setElementId(service_id);
			platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
			platSvcTradeData.setAllTag("02"); // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关
			platSvcTradeData.setActiveTag("1");// 主被动标记  1-主动，2-被动
			platSvcTradeData.setRsrvStr10(oneCardMultiNoReqData.getSp_code());
			platSvcTradeData.setRsrvStr9(OneCardMultiNoBean.ORG_DOMAIN);
			platSvcTradeData.setRsrvStr7(oneCardMultiNoReqData.getBiz_Code());
			platSvcTradeData.setRsrvStr6(OneCardMultiNoBean.BIZ_TYPE_CODE);
			platSvcTradeData.setRsrvStr5(bill_type); //0-免费 1-按次 2-包月
			platSvcTradeData.setInstId(inst_id);	

			platSvcTradeData.setRsrvStr2((null!=new_serial_number&&!"".equals(new_serial_number))?serial_number:null);
			platSvcTradeData.setRsrvStr1(oneCardMultiNoReqData.getInherit());
			platSvcTradeData.setRsrvStr8(inputParam.getString("RSRV_STR8"));
			platSvcTradeData.setIsNeedPf(("YES".equals(inputParam.getString("IS_NEED_PF")))?BofConst.IS_NEED_PF_YES:BofConst.IS_NEED_PF_NO);
			btd.add(serial_number, platSvcTradeData);
		}
	}	

	
	
	public static  void registerTradePlatSVC(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String first_date=SysDateMgr.getSysTime();
		String first_date_mon=SysDateMgr.getSysTime();
		String new_serial_number =inputParam.getString("NEW_SERIAL_NUMBER");//新号码
		//取服务局数据
//		IData inParam = new DataMap();
//		inParam.put("ORG_DOMAIN", OneCardMultiNoBean.ORG_DOMAIN);
//		inParam.put("SP_CODE", oneCardMultiNoReqData.getSp_code());
//		inParam.put("BIZ_CODE", oneCardMultiNoReqData.getBiz_Code());
//		inParam.put("BIZ_TYPE_CODE", OneCardMultiNoBean.BIZ_TYPE_CODE);
		String spCode = oneCardMultiNoReqData.getSp_code();
		String bizCode = oneCardMultiNoReqData.getBiz_Code();
		//TODO huanghua 34 与产商品解耦---已解决
		IDataset platSvcInfos =UpcCall.qryOffersBySpCond(spCode, bizCode, OneCardMultiNoBean.BIZ_TYPE_CODE);
		if(null==platSvcInfos||platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号服务局数据不存在，请先进行配置！");
		}
		String bill_type = "";
		String service_id=platSvcInfos.getData(0).getString("OFFER_CODE");
		IDataset services = UpcCall.qrySpServiceSpInfo(service_id,platSvcInfos.getData(0).getString("OFFER_TYPE"));
		if(null!=platSvcInfos||IDataUtil.isNotEmpty(services)){
			bill_type = services.getData(0).getString("BILL_TYPE");
		}
		
		PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData();
		platSvcTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
		platSvcTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		platSvcTradeData.setUserId(user_id);
		platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
		platSvcTradeData.setStartDate(start_date);
		
		//如果是续订，将开始时间设置成EFFECTIVE_TIME
		if("true".equals(inputParam.getString("RE_ORDER_FLAG"))){
			platSvcTradeData.setStartDate(inputParam.getString("EFFECTIVE_TIME"));
		}
		
		platSvcTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		platSvcTradeData.setBizStateCode(inputParam.getString("BIZ_STATE_CODE",PlatConstants.STATE_OK)); //A-正常，N-暂停，E-退订，L-挂失
		//platSvcTradeData.setOperCode(inputParam.getString("OPER_CODE",PlatConstants.OPER_USER_DATA_MODIFY)); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
		platSvcTradeData.setOperCode(inputParam.getString("OPER_CODE",PlatConstants.OPER_ORDER)); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
		platSvcTradeData.setOprSource(inputParam.getString("OPR_SOURCE","08"));
		platSvcTradeData.setFirstDate(first_date);
		platSvcTradeData.setFirstDateMon(first_date_mon);
		platSvcTradeData.setIntfTradeId(inputParam.getString("INTF_TRADE_ID","0"));
//		platSvcTradeData.setp("PRICE","0");
		
		platSvcTradeData.setPackageId(OneCardMultiNoBean.PACKAGE_ID);
		platSvcTradeData.setProductId(OneCardMultiNoBean.PRODUCT_ID);
		platSvcTradeData.setElementId(service_id);
		platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
		platSvcTradeData.setAllTag("02"); // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关
		platSvcTradeData.setActiveTag("1");// 主被动标记  1-主动，2-被动
		platSvcTradeData.setRsrvStr10(oneCardMultiNoReqData.getSp_code());
		platSvcTradeData.setRsrvStr9(OneCardMultiNoBean.ORG_DOMAIN);
		platSvcTradeData.setRsrvStr7(oneCardMultiNoReqData.getBiz_Code());
		platSvcTradeData.setRsrvStr6(OneCardMultiNoBean.BIZ_TYPE_CODE);
		platSvcTradeData.setRsrvStr5(bill_type); //0-免费 1-按次 2-包月
		platSvcTradeData.setInstId(inst_id);
		platSvcTradeData.setRsrvStr4(inputParam.getString("RELATE_INST_ID"));
		platSvcTradeData.setRsrvStr2((null!=new_serial_number&&!"".equals(new_serial_number))?serial_number:null);
		platSvcTradeData.setRsrvStr1(oneCardMultiNoReqData.getInherit());
		platSvcTradeData.setRsrvStr8(inputParam.getString("RSRV_STR8"));
		platSvcTradeData.setIsNeedPf(("YES".equals(inputParam.getString("IS_NEED_PF")))?BofConst.IS_NEED_PF_YES:BofConst.IS_NEED_PF_NO);
		btd.add(serial_number, platSvcTradeData);	
	}
	
	public static  void updateTradePlatSVC(BusiTradeData btd,IData inputParam) throws Exception {

		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();

		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String new_serial_number =inputParam.getString("NEW_SERIAL_NUMBER");//新号码
		
		//查询当前 服务
		IData input = new DataMap();
		input.put("USER_ID", user_id);
		input.put("RSRV_STR4", inputParam.getString("RELATE_INST_ID"));
		IDataset platSVCList=OneCardMultiNoQry.getPlatInfoByRelateInstId(input);

		if(IDataUtil.isNotEmpty(platSVCList)){
			IData platsvc = platSVCList.getData(0);
			
			PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData(platsvc);
			platSvcTradeData.setEndDate(inputParam.getString("EFFECTIVE_TIME",""));
			platSvcTradeData.setModifyTag("2");//0:新增,1:删除,2:修改
			platSvcTradeData.setIsNeedPf("0");
			platSvcTradeData.setOperCode(inputParam.getString("OPER_CODE","08")); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
			platSvcTradeData.setOprSource(inputParam.getString("OPER_SOURCE","08"));
			platSvcTradeData.setIntfTradeId(inputParam.getString("INTF_TRADE_ID","0"));
//			platSvcTradeData.setRemark(platsvc.getString("REMARK",""));
//			platSvcTradeData.setModifyTag("2");//0:新增,1:删除,2:修改
//			platSvcTradeData.setUserId(platsvc.getString("USER_ID",""));
//			platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
//			platSvcTradeData.setStartDate(platsvc.getString("START_DATE",""));
//			//将end_date设置为EFFECTIVE_TIME
//			platSvcTradeData.setEndDate(inputParam.getString("EFFECTIVE_TIME",""));
//			platSvcTradeData.setBizStateCode(platsvc.getString("BIZ_STATE_CODE","")); //A-正常，N-暂停，E-退订，L-挂失
//			platSvcTradeData.setOperCode(platsvc.getString("OPER_CODE","")); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
//			platSvcTradeData.setOprSource(platsvc.getString("OPER_SOURCE",""));
//			platSvcTradeData.setFirstDate(platsvc.getString("FIRST_DATE",""));
//			platSvcTradeData.setFirstDateMon(platsvc.getString("FIRST_DATE_MON",""));
//			platSvcTradeData.setIntfTradeId(platsvc.getString("INTF_TRADE_ID",""));
//			
//			platSvcTradeData.setPackageId(OneCardMultiNoBean.PACKAGE_ID);
//			platSvcTradeData.setProductId(OneCardMultiNoBean.PRODUCT_ID);
//			platSvcTradeData.setElementId(platsvc.getString("SERVICE_ID",""));
//			platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
//			platSvcTradeData.setAllTag("02"); // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关
//			platSvcTradeData.setActiveTag("1");// 主被动标记  1-主动，2-被动
//			platSvcTradeData.setRsrvStr10(platsvc.getString("RSRV_STR10",""));
//			platSvcTradeData.setRsrvStr9(platsvc.getString("RSRV_STR9",""));
//			platSvcTradeData.setRsrvStr7(platsvc.getString("RSRV_STR7",""));
//			platSvcTradeData.setRsrvStr6(platsvc.getString("RSRV_STR6",""));
//			platSvcTradeData.setRsrvStr5(platsvc.getString("RSRV_STR5","")); //0-免费 1-按次 2-包月
//			platSvcTradeData.setInstId(platsvc.getString("INST_ID",""));	
//			platSvcTradeData.setRsrvStr2(platsvc.getString("RSRV_STR2",""));
//			platSvcTradeData.setRsrvStr1(platsvc.getString("RSRV_STR1",""));
//			platSvcTradeData.setRsrvStr8(platsvc.getString("RSRV_STR8",""));
//			platSvcTradeData.setIsNeedPf(("YES".equals(inputParam.getString("IS_NEED_PF")))?BofConst.IS_NEED_PF_YES:BofConst.IS_NEED_PF_NO);
			btd.add(serial_number, platSvcTradeData);
		}	
	}
	
	public static  IDataset updateRelationsCallIBoss(IData inputParam) throws Exception {
		//取服务局数据
		IData inParam = new DataMap(); 
		
		//接口重新约定
		inParam.put("PKG_SEQ", inputParam.getString("PKG_SEQ"));//PkgSeq 交易包流水 发起省代码＋YYYYMMDD＋6位定长流水，序号从000001开始，增量步长为1
		OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
		inParam.put("PKG_SEQ", bean.getSeqID("BIP5A012"));//PkgSeq
		
		inParam.put("REC_NUM", inputParam.getString("REC_NUM","1"));//本交易包中的记录条数
		inParam.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));//IDV 主号码标识值 手机号码，换号时填写新号码
		inParam.put("OLD_SERIAL_NUMBER", inputParam.getString("OLD_SERIAL_NUMBER",""));//旧主号码标识值 换号时填写
		inParam.put("OPER_CODE", inputParam.getString("OPR_CODE"));//Opr 主号码的操作代码
		
		String service_id = inputParam.getString("SERVICE_ID","99941710");//如果service_id没有，默认为一期的service_id99941710
		inParam.put("SERVICE_ID", service_id);//Opr 主号码的操作代码
		
		//TODO huanghua 33 与产商品解耦---已解决
		IDataset platSvcInfos =OneCardMultiNoQry.getPlatSVCBillTypeByPK(inParam);
		
		if(null==platSvcInfos||platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号服务局数据不存在，请先进行配置！");
		}
		String sp_code=platSvcInfos.getData(0).getString("SP_CODE");
		String biz_Code=platSvcInfos.getData(0).getString("BIZ_CODE");
		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
		inParam.put("BILL_TYPE", bill_type);//ChrgType 计费类型
		
		inParam.put("MOSP_OPRT", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//OprT BOSS操作时间
		inParam.put("BIZ_TYPE_CODE", "74");//BizType 业务类型代码
		inParam.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//EffetiTime 业务生效时间
		inParam.put("MOSP_CHANNEL", "08");//Channel 受理渠道 02-网上营业厅， 07-10086语音，08-营业厅
		inParam.put("SP_CODE", sp_code);//SPID SP企业代码
		inParam.put("SP_SERVID", biz_Code);//SPServID SP业务代码
		inParam.put("INHERIT", inputParam.getString("INHERIT","0"));//0：继承；1：不继承  用户状态变为过户、换号时使用
				
		inParam.put("KIND_ID", "BIP5A012_T5101011_0_0");// 接口标识

		IDataset follow_infolist=inputParam.getDataset("FOLLOW_INFOLIST");
		int rec_num=inputParam.getInt("REC_NUM",1);
		IDataset user_dataList = new DatasetList();
		for (int i = 0; i < rec_num; i++) {
			IData follow_info = follow_infolist.getData(i);
			IData user_data = new DataMap();
			user_data.put("MOSP_FOLLOWMSISDN", follow_info.getString("FOLLOW_MSISDN"));//FMInfo 副号码
			//user_data.put("MOSP_SERIALNUM", follow_info.getString("SERIAL_NUM"));//SerialNum 表示副号码序列号
			user_data.put("MOSP_CATEGORY", (null != follow_info.getString("FOLLOW_MSISDN") && !"".equals(follow_info.getString("FOLLOW_MSISDN"))) ? "1":"0");//Category 副号码类型
			user_dataList.add(user_data);
		}
		
		inParam.put("USER_DATA", user_dataList);
		/**
		 * 
		 * 端午节活动
		 * @author zhuoyingzhi
		 * @date 20170519
		 */
		inParam.put("MOSP_OLDIDV", inputParam.getString("MOSP_OLDIDV",""));
		IDataset IBossResult=new DatasetList();
		IBossResult =IBossCall.callHttpIBOSS("IBOSS", inParam);
		if(IDataUtil.isEmpty(IBossResult))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP5A012_T5101011_0_0！");
        }
        IData tmpData = IBossResult.getData(0);
        if (!StringUtils.equals("0", tmpData.getString("X_RESULTCODE"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给一卡多号平台信息出错，错误编码【" + tmpData.getString("RESULT")+ "】!");
        }
        //记录日志信息
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
        params.put("SERIAL_NUMBER_B", inputParam.getDataset("FOLLOW_INFOLIST").getData(0).getString("FOLLOW_MSISDN"));
        params.put("SERIAL_NUMBER_B_TYPE",user_dataList.getData(0).getString("MOSP_CATEGORY"));
        params.put("REMARK", "记录和多号发起交易！");
        writeOneCardMultiNoLog(params);
		return IBossResult;
	}	
	
	
	public static  IDataset updateRelationsCallIBossNew(IData inputParam) throws Exception {
		//取服务局数据
		IData inParam = new DataMap(); 
		
		//接口重新约定
		OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
		inParam.put("PKG_SEQ", bean.getSeqID("BIP5A012"));//PkgSeq
		
		inParam.put("REC_NUM", inputParam.getString("REC_NUM","1"));//本交易包中的记录条数
		
		String service_id = inputParam.getString("SERVICE_ID","99941710");//如果service_id没有，默认为一期的service_id99941710
		//TODO huanghua 33 与产商品解耦---已解决
		IDataset platSvcInfos =OneCardMultiNoQry.getPlatSVCBillTypeByPK(inParam);
		
		if(null==platSvcInfos||platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号服务局数据不存在，请先进行配置！");
		}
		String sp_code=platSvcInfos.getData(0).getString("SP_CODE");
		String biz_Code=platSvcInfos.getData(0).getString("BIZ_CODE");
		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
		inParam.put("INHERIT", inputParam.getString("INHERIT","0"));//0：继承；1：不继承  用户状态变为过户、换号时使用
				
		inParam.put("KIND_ID", "BIP5A012_T5101011_0_0");// 接口标识

		IDataset follow_infolist=inputParam.getDataset("FOLLOW_INFOLIST");
		IDataset user_dataList = new DatasetList();
		for (int i = 0; i < follow_infolist.size(); i++) {
			IData follow_info = follow_infolist.getData(i);
			IData user_data = new DataMap();
			user_data.put("MOSP_FOLLOWMSISDN", follow_info.getString("FOLLOW_MSISDN"));//FMInfo 副号码
			user_data.put("MOSP_SERIALNUM", "");//SerialNum 表示副号码序列号
			user_data.put("MOSP_CATEGORY", inputParam.getString("CATEGORY"));//Category 副号码类型
			user_data.put("MS_OPCODE", CSBizBean.getVisit().getStaffId());
			user_data.put("SEQ", inputParam.getString("SEQ",bean.getSeqID("BIP5A012")));
			user_data.put("SERVICE_ID", service_id);
			
			user_data.put("SP_SERVID", "100000");
			user_data.put("SP_CODE", "698034");
			user_data.put("MOSP_CHANNEL", "08");
			user_data.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			user_data.put("BIZ_TYPE_CODE", "74");
			user_data.put("MOSP_OPRT", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			user_data.put("BILL_TYPE", bill_type);
			user_data.put("OPER_CODE", inputParam.getString("OPER_CODE"));
			user_data.put("OLD_SERIAL_NUMBER", inputParam.getString("OLD_SERIAL_NUMBER",""));
			user_data.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
			if(StringUtils.equals("0", inputParam.getString("CATEGORY")) && StringUtils.equals("06", inputParam.getString("OPER_CODE")))
			{
				user_data.put("NUMBER_LEVER", follow_info.getString("NUMBER_LEVER","6"));
			}
			else
			{
				user_data.put("NUMBER_LEVER", "");
			}
			
			user_dataList.add(user_data);
		}
		
		inParam.put("UD_DATAS", user_dataList);
		/**
		 * 
		 * 端午节活动
		 * @author zhuoyingzhi
		 * @date 20170519
		 */
		inParam.put("MOSP_OLDIDV", inputParam.getString("MOSP_OLDIDV",""));
		IDataset IBossResult=new DatasetList();
		IBossResult =IBossCall.callHttpIBOSS("IBOSS", inParam);
		if(IDataUtil.isEmpty(IBossResult))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP5A012_T5101011_0_0！");
        }
        IData tmpData = IBossResult.getData(0);
        if (!StringUtils.equals("0", tmpData.getString("X_RESULTCODE"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给一卡多号平台信息出错，错误编码【" + tmpData.getString("RESULT")+ "】!");
        }
        //记录日志信息
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
        params.put("SERIAL_NUMBER_B", inputParam.getDataset("FOLLOW_INFOLIST").getData(0).getString("FOLLOW_MSISDN"));
        params.put("SERIAL_NUMBER_B_TYPE",user_dataList.getData(0).getString("MOSP_CATEGORY"));
        params.put("REMARK", "记录和多号发起交易！");
        writeOneCardMultiNoLog(params);
		return IBossResult;
	}	
	
	
	public static  IDataset updateRelationsCallIBossNew1(IData inputParam) throws Exception {
		//取服务局数据
		IData inParam = new DataMap(); 
		
		//接口重新约定
		OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
		//inParam.put("PKG_SEQ", bean.getSeqID("BIP5A012"));//PkgSeq
		inParam.put("SEQ", bean.getSeqID("BIP5A012"));//PkgSeq
		//inParam.put("REC_NUM", inputParam.getString("REC_NUM","1"));//本交易包中的记录条数
		inParam.put("MSISDN", inputParam.getString("SERIAL_NUMBER"));//IDV 主号码标识值 手机号码，换号时填写新号码
		inParam.put("OPER_CODE", inputParam.getString("OPER_CODE"));//Opr 主号码的操作代码
		inParam.put("CATEGORY",inputParam.getString("CATEGORY")); //Category 副号码类型
		inParam.put("MS_OPCODE",CSBizBean.getVisit().getStaffId());// 员工号 
		inParam.put("FPROVINCE", CSBizBean.getVisit().getProvinceCode());
				
		
		String service_id = inputParam.getString("SERVICE_ID","99941710");//如果service_id没有，默认为一期的service_id99941710
		inParam.put("SERVICE_ID", service_id);
		//TODO huanghua 33 与产商品解耦---已解决
		IDataset platSvcInfos =OneCardMultiNoQry.getPlatSVCBillTypeByPK(inParam);
		
		if(null==platSvcInfos||platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号服务局数据不存在，请先进行配置！");
		}
		String sp_code=platSvcInfos.getData(0).getString("SP_CODE");
		String biz_Code=platSvcInfos.getData(0).getString("BIZ_CODE");
		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
		//inParam.put("INHERIT", inputParam.getString("INHERIT","0"));//0：继承；1：不继承  用户状态变为过户、换号时使用
				
		// 接口标识
		inParam.put("KIND_ID", "BIP5A011_T5000011_0_0");
		//BizType 业务类型代码
		inParam.put("BIZ_TYPE", "74");
		//ChrgType 计费类型
		inParam.put("CHRG_TYPE", bill_type);
		//EffetiTime 业务生效时间
		inParam.put("EFFECTIVE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//SPID SP企业代码
		inParam.put("SPID", sp_code);
		//SPServID SP业务代码
		inParam.put("BIZ_CODE", biz_Code);
		
		IData follow_info = inputParam.getDataset("FOLLOW_INFOLIST").getData(0);
		//FMInfo 副号码
		inParam.put("FOLLOW_MSISDN", follow_info.getString("FOLLOW_MSISDN"));
		//SerialNum 表示副号码序列号
		inParam.put("SERIAL_NUM", follow_info.getString("SERIAL_NUM"));
		
		String mode_code = CSBizBean.getVisit().getInModeCode();
		//普通营业厅
		if("0".equals(mode_code)){
			inParam.put("CHANNEL_ID", "08");
		//10086，客服
		}else if("1".equals(mode_code)){
			inParam.put("CHANNEL_ID", "07");
		// 网上营业厅
		}else if("3".equals(mode_code)){
			inParam.put("CHANNEL_ID", "02");
		// 短厅
		}else if("5".equals(mode_code) || "c".equals(mode_code)){
			inParam.put("CHANNEL_ID", "04");
		//WEB
		}else if("a".equals(mode_code)){
			inParam.put("CHANNEL_ID", "01");
		//WAP
		}else if("b".equals(mode_code)){
			inParam.put("CHANNEL_ID", "03");
		//掌上营业厅
		}else if("K".equals(mode_code)){
			inParam.put("CHANNEL_ID", "09");
		}
		
		/**
		 * 
		 * 端午节活动
		 * @author zhuoyingzhi
		 * @date 20170519
		 */
		inParam.put("MOSP_OLDIDV", inputParam.getString("MOSP_OLDIDV",""));
		
		/**
		 * 入台账
		 */
		//插入台账时参数的设置，共设置了参数：serialNumber serial_number_b sp_code biz_code flag orderno
		IData input = new DataMap();
		String serialNumber = inParam.getString("MSISDN");
		// call服务必须要这个参数
		input.put("SERIAL_NUMBER", serialNumber);
		// requestData里面的 serialNumber
		input.put("SERIALNUMBER", serialNumber);
		input.put("SERIAL_NUMBER_B", inParam.getString("FOLLOW_MSISDN"));
		input.put("BIZ_CODE", inParam.getString("BIZ_CODE"));
		input.put("SPID", inParam.getString("SPID"));
		input.put("FLAG", "2");
		//input.put("SEQ", inParam.getString("SEQ"));
		input.put("CATEGORY", inputParam.getString("CATEGORY"));
		//查询userId，为查询uu关系表做准备
		IData inparam1 = new DataMap();
		inparam1.put("SERIAL_NUMBER", serialNumber);  //主号码
		inparam1.put("REMOVE_TAG", "0");     //完工标志
		IDataset UserInfos = Dao.qryByCode("TF_F_USER", "SEL_BY_SERIAL_NUMBER", inparam1);
		String userId = null;
		if (!UserInfos.isEmpty()) {
			 IData userData = (IData) UserInfos.get(0);
			 userId = userData.getString("USER_ID");
		}
		//查uu关系表
		IDataset relationTradeDatas = OneCardMultiNoQry.qryRelationList(userId,OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
		//设置orderno
		String orderno = null;
		if(relationTradeDatas!=null && !IDataUtil.isEmpty(relationTradeDatas)){
			//过滤查出的记录，决定orderno的值
			IDataset relaTradeFilterDatas = DataHelper.filter(relationTradeDatas, "ORDERNO=" + 1);
			if(IDataUtil.isEmpty(relaTradeFilterDatas)){
				orderno = "1";
			} else {
				relaTradeFilterDatas = DataHelper.filter(relationTradeDatas, "ORDERNO=" + 2);
				if(IDataUtil.isEmpty(relaTradeFilterDatas)){
					orderno = "2";
				} else {
					orderno = "3";
				}
			}
		}else{
			orderno="1";
		}
		input.put("ORDERNO", orderno);   //input作为插入的台账数据，传入下面的服务接口
		input.put("SEQ", inParam.getString("SEQ",""));
		input.put("FPROVINCE", inParam.getString("FPROVINCE",""));
		input.put("PSPT_ID", inputParam.getString("PSPT_ID"));
		input.put("CUST_NAME", inputParam.getString("CUST_NAME"));
		input.put("ADDRESS", inputParam.getString("ADDRESS"));
		input.put("OPR_CODE", inParam.getString("OPER_CODE","06"));
		IDataset resultDataset = new DatasetList();
//		try {
			//插入台账数据     SS.OneCardMultiNoRegSVC.tradeReg  ----
			resultDataset = CSAppCall.call("SS.OneCardMultiNoRegSVC.tradeReg", input);
//		} catch (Exception e) {
//			System.out.println("插入台账数据时出错："+e.getMessage());
//		}
		
		/*IDataset IBossResult=new DatasetList();
		IBossResult =IBossCall.callHttpIBOSS("IBOSS", inParam);
		if(IDataUtil.isEmpty(IBossResult))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP5A012_T5101011_0_0！");
        }
        IData tmpData = IBossResult.getData(0);
      //  if (!StringUtils.equals("0", tmpData.getString("X_RESULTCODE"))) {
        if (!StringUtils.equals("0", tmpData.getString("X_RSPTYPE")) &&
        		!StringUtils.equals("0000",tmpData.getString("X_RSPCODE"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给一卡多号平台信息出错，错误编码【" + tmpData.getString("RESULT")+ "】!");
        }*/
        //记录日志信息
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", inputParam.getString("SERIAL_NUMBER"));
        params.put("SERIAL_NUMBER_B", inputParam.getDataset("FOLLOW_INFOLIST").getData(0).getString("FOLLOW_MSISDN"));
    //    params.put("SERIAL_NUMBER_B_TYPE",user_dataList.getData(0).getString("MOSP_CATEGORY"));
        params.put("REMARK", "记录和多号发起交易！");
        writeOneCardMultiNoLog(params);
		return resultDataset;
	}	
	
	
	 /**
     * 记录一卡多号发起的交易
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IData writeOneCardMultiNoLog(IData params) throws Exception
    {
    	
    	  UcaData ucadata = UcaDataFactory.getNormalUca(params.getString("SERIAL_NUMBER"));

          String eparchyCode = CSBizBean.getTradeEparchyCode();
          String systime = SysDateMgr.getSysTime();
          String tradeId = SeqMgr.getTradeId();
          IData inparam = new DataMap();
          String netTypeCode = ucadata.getUser().getNetTypeCode();
          if (StringUtils.isBlank(netTypeCode))
              netTypeCode = "00";

          inparam.put("TRADE_ID", tradeId);// 业务流水号
          inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
          inparam.put("BATCH_ID", "0");
          inparam.put("ORDER_ID", SeqMgr.getOrderId());
          inparam.put("CAMPN_ID", "0");
          inparam.put("TRADE_TYPE_CODE", "2102");// 业务类型编码：见参数表TD_S_TRADETYPE
          inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
          inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
          inparam.put("SUBSCRIBE_STATE", "0");
          inparam.put("NEXT_DEAL_TAG", "0");
          inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
          inparam.put("CUST_ID", ucadata.getCustId());
          inparam.put("CUST_NAME", ucadata.getCustomer().getCustName());
          inparam.put("USER_ID", ucadata.getUserId());
          inparam.put("ACCT_ID", ucadata.getAcctId());
          inparam.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
          inparam.put("NET_TYPE_CODE", netTypeCode);
          inparam.put("EPARCHY_CODE", eparchyCode);
          inparam.put("CITY_CODE", ucadata.getUser().getCityCode());
          inparam.put("PRODUCT_ID", ucadata.getProductId());
          inparam.put("BRAND_CODE", ucadata.getBrandCode());
          inparam.put("ACCEPT_DATE", systime);
          inparam.put("UPDATE_TIME", systime);
          inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
          inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
          inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
          inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
          inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
          inparam.put("TRADE_EPARCHY_CODE", eparchyCode);
          inparam.put("OPER_FEE", "0");
          inparam.put("FOREGIFT", "0");
          inparam.put("ADVANCE_PAY", "0");
          inparam.put("PROCESS_TAG_SET", "0");
          inparam.put("OLCOM_TAG", "0");
          inparam.put("FEE_STATE", "0");
          inparam.put("FINISH_DATE", systime);
          inparam.put("EXEC_TIME", systime);
          inparam.put("CANCEL_TAG", "0");
          inparam.put("PF_WAIT", "0");// 是否发开通
          inparam.put("RSRV_STR1", params.getString("SERIAL_NUMBER_B"));//副号
          inparam.put("RSRV_STR2", params.getString("SERIAL_NUMBER_B_TYPE"));//副号类型
          inparam.put("REMARK", params.get("REMARK"));
          Dao.insert("TF_BH_TRADE", inparam, Route.getJourDbDefault());
          return inparam;
    }

	/**
	 * 拼装优惠表子台帐 TF_B_TRADE_DISCNT,月租优惠、服务
	 */
	public static  void registerTradeDiscntForMonth(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		//查询优惠配置
		IDataset discnt_codeParamInfos = CommparaInfoQry.getCommpara("CSM","9999","DISCNT_CODE",btd.getRD().getUca().getUserEparchyCode());
		if(null==discnt_codeParamInfos||discnt_codeParamInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号优惠配置不存在，请先进行配置！");
		}
		String discnt_code=discnt_codeParamInfos.getData(0).getString("PARA_CODE1");
		
		if(!BofConst.MODIFY_TAG_ADD.equals(modify_tag)){
			IDataset discntList=OneCardMultiNoQry.getDiscntByUserId(user_id, discnt_code, OneCardMultiNoBean.RELATION_TYPE_CODE);
			if(null==discntList||discntList.isEmpty()){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"一卡多号优惠数据错误");
			}
			start_date=discntList.getData(0).getString("START_DATE");
			inst_id=discntList.getData(0).getString("INST_ID");
		}
		
		DiscntTradeData discntTradeData = new DiscntTradeData();
		discntTradeData.setRemark(inputParam.getString("REMARK",oneCardMultiNoReqData.getRemark()));
		discntTradeData.setModifyTag("0");//0:新增,1:删除,2:修改
		discntTradeData.setUserId(user_id);
		discntTradeData.setInstId(inst_id);	
		discntTradeData.setStartDate(start_date);
		discntTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
		discntTradeData.setUserIdA("-1");
		discntTradeData.setSpecTag("2"); // 特殊优惠标记
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");
		
		discntTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		discntTradeData.setElementId(discnt_code);
		discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
		btd.add(serial_number, discntTradeData);		
	}
}