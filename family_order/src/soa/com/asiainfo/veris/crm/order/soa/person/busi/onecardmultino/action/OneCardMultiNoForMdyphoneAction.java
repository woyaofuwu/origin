package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;



import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;
/**
 * 一卡多号 配置143改号业务的参数
 */
@SuppressWarnings("unchecked")
public class OneCardMultiNoForMdyphoneAction implements ITradeAction {
	public void executeAction(BusiTradeData btd) throws Exception {
		ModifyPhoneCodeReqData modifyPhoneCodeReqData = (ModifyPhoneCodeReqData) btd.getRD();
		String serial_number=modifyPhoneCodeReqData.getNewSerialNumber();
		String inherit=modifyPhoneCodeReqData.getInherit();


    String userId = btd.getRD().getUca().getUserId();
  
		//判断是否是继承一卡多号，不继承全部取消
		if("1".equals(inherit)){
			//首先判断是否是一卡多号号码主号码，同步状态
			IDataset relationList=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
			if(null!=relationList&&!relationList.isEmpty()){
				IData inputParam = new DataMap();
				  inputParam.put("USER_ID", userId);
				inputParam.put("SERIAL_NUMBER", serial_number);
				inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				for (int index = 0; index < relationList.size(); index++) {
					inputParam.put("ORDERNO", relationList.getData(index).getString("ORDERNO"));
					String serial_number_b=relationList.getData(index).getString("SERIAL_NUMBER_B");
					String orderno=relationList.getData(index).getString("ORDERNO");
					inputParam.put("SERIAL_NUMBER_B", relationList.getData(index).getString("SERIAL_NUMBER_B"));
					registerTradeRelation(btd,inputParam);
				}
				registerTradeDiscnt(btd,inputParam);
				inputParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
				inputParam.put("BIZ_STATE_CODE", PlatConstants.STATE_CANCEL);
				inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
				inputParam.put("ALL_TAG", "01");
				inputParam.put("INHERIT","1" );
				registerTradePlatSVC(btd,inputParam);
			}
		}
		else{
			//继承一卡多号服务的预留字段修改为13，服务开通取值预留字段8
			List<PlatSvcTradeData> platSvcTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
			for (int index = 0; index < platSvcTradeDataList.size(); index++) {
				String org_domain= platSvcTradeDataList.get(index).getRsrvStr9();
				if("MOSP".equals(org_domain)){
					platSvcTradeDataList.get(index).setRsrvStr8("13");
					break;
				}
			}
		
		
			//首先判断是否是一卡多号号码主号码，同步状态
			IDataset relationList=OneCardMultiNoQry.qryRelationList(btd.getRD().getUca().getUserId(),OneCardMultiNoBean.RELATION_TYPE_CODE,null,null);
			if(null!=relationList&&!relationList.isEmpty()){
			IData inputParam = new DataMap();
			inputParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			inputParam.put("SERIAL_NUMBER", serial_number);
			inputParam.put("OPER_CODE", PlatConstants.OPER_USER_DATA_MODIFY);
			inputParam.put("RSRV_STR8", "13");
			inputParam.put("INHERIT","0" );
			inputParam.put("ALL_TAG", "02");
			registerTradePlatSVC(btd,inputParam);
			for (int index = 0; index < relationList.size(); index++) {
				inputParam.put("ORDERNO", relationList.getData(index).getString("ORDERNO"));
					inputParam.put("SERIAL_NUMBER_B", relationList.getData(index).getString("SERIAL_NUMBER_B"));
				registerTradeRelation(btd,inputParam);
				}
		
			}
		}
		}
	
	
	/**
	 * 拼装优惠表子台帐 TF_B_TRADE_DISCNT
	 */
	public   void registerTradeDiscnt(BusiTradeData btd,IData inputParam) throws Exception {
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
		discntTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		discntTradeData.setUserId(user_id);
		discntTradeData.setInstId(inst_id);	
		discntTradeData.setStartDate(start_date);
		discntTradeData.setEndDate((BofConst.MODIFY_TAG_DEL.equals(modify_tag))?SysDateMgr.getSysTime():SysDateMgr.getEndCycle20501231());
		discntTradeData.setUserIdA("-1");
		discntTradeData.setSpecTag("2"); // 特殊优惠标记
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");
		
		discntTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		discntTradeData.setElementId(discnt_code);
		discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
//		discntTradeData.setCampnId(discntInfo.getString("CAMPN_ID"));
		btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);		
	}
	
	
	/**
	 * 拼装服务表子台帐 TF_B_TRADE_PLATSVC
	 */
	public   void registerTradePlatSVC(BusiTradeData btd,IData inputParam) throws Exception {
		OneCardMultiNoReqData rd = (OneCardMultiNoReqData) btd.getRD();
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String start_date=SysDateMgr.getSysTime();
		String first_date=SysDateMgr.getSysTime();
		String first_date_mon=SysDateMgr.getSysTime();
		//取服务局数据
		IData inParam = new DataMap();
		inParam.put("ORG_DOMAIN", OneCardMultiNoBean.ORG_DOMAIN);
		inParam.put("SP_CODE", rd.getSp_code());
		inParam.put("BIZ_CODE", rd.getBiz_Code());
		inParam.put("BIZ_TYPE_CODE", OneCardMultiNoBean.BIZ_TYPE_CODE);
		IDataset platSvcInfos =OneCardMultiNoQry.getPlatSVCBySPInfo( inParam);
		if(null==platSvcInfos||platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"国内一卡多号服务局数据不存在，请先进行配置！");
		}
		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
		String service_id=platSvcInfos.getData(0).getString("SERVICE_ID");
		
		if(!BofConst.MODIFY_TAG_ADD.equals(modify_tag)){
			IDataset platSVCList=OneCardMultiNoQry.getPlatSVCByUserId(user_id, service_id, OneCardMultiNoBean.ORG_DOMAIN, rd.getSp_code(), rd.getBiz_Code(), OneCardMultiNoBean.BIZ_TYPE_CODE);
			if(null==platSVCList||platSVCList.isEmpty()){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"一卡多号服务数据错误");
			}
			start_date=platSVCList.getData(0).getString("START_DATE");
			inst_id=platSVCList.getData(0).getString("INST_ID");
			first_date=platSVCList.getData(0).getString("FIRST_DATE");
			first_date_mon=platSVCList.getData(0).getString("FIRST_DATE_MON");
		}
		
		PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData();
		platSvcTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		platSvcTradeData.setUserId(user_id);
		platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
		platSvcTradeData.setStartDate(start_date);
		platSvcTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		platSvcTradeData.setBizStateCode(inputParam.getString("BIZ_STATE_CODE",PlatConstants.STATE_OK)); //A-正常，N-暂停，E-退订，L-挂失
		platSvcTradeData.setOperCode(inputParam.getString("OPER_CODE",PlatConstants.OPER_USER_DATA_MODIFY)); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
		platSvcTradeData.setOprSource(inputParam.getString("OPR_SOURCE","08"));
		platSvcTradeData.setFirstDate(first_date);
		platSvcTradeData.setFirstDateMon(first_date_mon);
		platSvcTradeData.setIntfTradeId(inputParam.getString("INTF_TRADE_ID","0"));
//		platSvcTradeData.setp("PRICE","0");
		
		platSvcTradeData.setPackageId(OneCardMultiNoBean.PACKAGE_ID);
		platSvcTradeData.setProductId(OneCardMultiNoBean.PRODUCT_ID);
		platSvcTradeData.setElementId(service_id);
		platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
		platSvcTradeData.setAllTag(inputParam.getString("ALL_TAG")); // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关
		platSvcTradeData.setActiveTag("1");// 主被动标记  1-主动，2-被动
		platSvcTradeData.setRsrvStr10(rd.getSp_code());
		platSvcTradeData.setRsrvStr9(OneCardMultiNoBean.ORG_DOMAIN);
		platSvcTradeData.setRsrvStr7(rd.getBiz_Code());
		platSvcTradeData.setRsrvStr6(OneCardMultiNoBean.BIZ_TYPE_CODE);
		platSvcTradeData.setRsrvStr5(bill_type); //0-免费 1-按次 2-包月
		platSvcTradeData.setInstId(inst_id);	
		
		platSvcTradeData.setRsrvStr2(btd.getRD().getUca().getSerialNumber());
		platSvcTradeData.setRsrvStr1(inputParam.getString("INHERIT"));
		platSvcTradeData.setRsrvStr8(inputParam.getString("RSRV_STR8"));
		platSvcTradeData.setIsNeedPf("1");
		btd.add(btd.getRD().getUca().getSerialNumber(), platSvcTradeData);	
	}	
	
	
	/**
	 * 拼装关系表子台帐 TF_B_TRADE_RELATION
	 */
	public   void registerTradeRelation(BusiTradeData btd,IData inputParam) throws Exception {
		String modify_tag=inputParam.getString("MODIFY_TAG");
		String user_id=inputParam.getString("USER_ID",btd.getRD().getUca().getUserId());
		String serial_number=inputParam.getString("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
		String serial_number_b=inputParam.getString("SERIAL_NUMBER_B");
		String orderno=inputParam.getString("ORDERNO");
		String start_date=SysDateMgr.getSysTime();
		String inst_id = inputParam.getString("INST_ID", SeqMgr.getInstId());
		String category="1";
		
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
		}
		
		RelationTradeData relationTradeData = new RelationTradeData();
		relationTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		relationTradeData.setSerialNumberA(serial_number);
		relationTradeData.setUserIdB(serial_number_b+category);
		relationTradeData.setSerialNumberB(serial_number_b);
		relationTradeData.setOrderno(orderno);
		relationTradeData.setUserIdA(user_id);
		relationTradeData.setInstId(inst_id);
		relationTradeData.setStartDate(start_date);
		relationTradeData.setEndDate((BofConst.MODIFY_TAG_DEL.equals(modify_tag))?SysDateMgr.getSysTime():SysDateMgr.getEndCycle20501231());
		relationTradeData.setRoleTypeCode("0");
		relationTradeData.setRoleCodeA("0");
		relationTradeData.setRoleCodeB("1");
		relationTradeData.setRsrvStr4("898");
		relationTradeData.setRelationTypeCode(OneCardMultiNoBean.RELATION_TYPE_CODE);
		if(null!=serial_number&&!"".equals(serial_number)){
			relationTradeData.setRsrvStr2( btd.getRD().getUca().getSerialNumber());
		}
		relationTradeData.setRsrvStr1(inputParam.getString("INHERIT"));
		btd.add(btd.getRD().getUca().getSerialNumber(), relationTradeData);
	}
}
		
	