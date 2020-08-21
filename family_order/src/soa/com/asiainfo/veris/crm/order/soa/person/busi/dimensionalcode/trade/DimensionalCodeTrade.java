package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.DimensionalCodeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode.requestdata.DimensionalCodeReqData;

@SuppressWarnings("unchecked")
public class DimensionalCodeTrade extends BaseTrade implements ITrade {
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		this.registerTradePlatSVC(btd);
		//同步平台
		DimensionalCodeReqData dimensionalCodeReqData = (DimensionalCodeReqData) btd.getRD();
		IData inputParam=new DataMap();
		inputParam.put("PKG_SEQ", dimensionalCodeReqData.getPkg_seq());
		inputParam.put("REC_NUM", "1");
		inputParam.put("SEQ", dimensionalCodeReqData.getSeq());
		IDataset recordList = new DatasetList();
		IData record = new DataMap();
		record.put("ID_VALUE", dimensionalCodeReqData.getId_type()); // 用户ID标识值 手机号码
		record.put("ID_VALUE", dimensionalCodeReqData.getId_value()); // 用户ID标识值 手机号码
		record.put("OPR_CODE", dimensionalCodeReqData.getOpr_code());// 操作类型
		record.put("OPR_TIME", dimensionalCodeReqData.getOpr_time());// 操作时间
		record.put("CHANNEL", dimensionalCodeReqData.getChannel());// 02-网上营业厅， 07-10086语音，08-营业厅
		recordList.add(record);
		inputParam.put("recordList",recordList);
		this.dimensionalCodeOperateCallIBoss(inputParam);
	}
	/**
	 * 拼装服务表子台帐 TF_B_TRADE_PLATSVC
	 * 01：下发-新增 02：补发（已经生成的下发）-无需 03：重置（重新生成下发）-无需  04：冻结-暂停  05：解冻-恢复  06：启用消费密码-无需 07：关闭消费密码-无需
	 */
	private void registerTradePlatSVC(BusiTradeData btd) throws Exception {
		DimensionalCodeReqData dimensionalCodeReqData = (DimensionalCodeReqData) btd.getRD();
		String cur_status=dimensionalCodeReqData.getStatus();
		String cur_opr_code=dimensionalCodeReqData.getOpr_code();
		String start_date=SysDateMgr.getSysTime();
		String first_date=SysDateMgr.getSysTime();
		String first_date_mon=SysDateMgr.getSysTime();
		String user_id=btd.getRD().getUca().getUserId();
		String intfTradeId="0";
		String inst_id =SeqMgr.getInstId();	
		
		//取服务局数据
		IData inParam = new DataMap();
		inParam.put("ORG_DOMAIN", DimensionalCodeBean.ORG_DOMAIN);
		inParam.put("SP_CODE", DimensionalCodeBean.SPID);
		inParam.put("BIZ_CODE", DimensionalCodeBean.BIZ_CODE);
		inParam.put("BIZ_TYPE_CODE", DimensionalCodeBean.BIZ_TYPE_CODE);
		IDataset platSvcInfos =SpInfoQry.getPlatSVCBySPInfo(inParam);
		if(null==platSvcInfos || platSvcInfos.isEmpty()){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"二维码服务局数据不存在，请先进行配置！");
		}
		String bill_type=platSvcInfos.getData(0).getString("BILL_TYPE");
		String service_id=platSvcInfos.getData(0).getString("SERVICE_ID");
		// 默认下发处理
		String modify_tag=BofConst.MODIFY_TAG_ADD;
		String oper_code=PlatConstants.OPER_ORDER;
		String biz_state_code=PlatConstants.STATE_OK;
		// 解冻处理
		if(DimensionalCodeBean.STATUS_REISSUE.equals(cur_opr_code)){
			modify_tag=BofConst.MODIFY_TAG_UPD;
			biz_state_code=PlatConstants.STATE_PAUSE;
			oper_code=PlatConstants.OPER_PAUSE;
		}
		// 解冻处理
		else if(DimensionalCodeBean.STATUS_UNFREEZE.equals(cur_opr_code)){
			modify_tag=BofConst.MODIFY_TAG_UPD;
			biz_state_code=PlatConstants.STATE_OK;
			oper_code=PlatConstants.OPER_RESTORE;
		}
		// 补发 重置 启用 关闭处理
		else {
			modify_tag=BofConst.MODIFY_TAG_UPD;
			biz_state_code=PlatConstants.STATE_OK;
			oper_code=PlatConstants.OPER_USER_DATA_MODIFY;
		}
		PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData();
		platSvcTradeData.setModifyTag(modify_tag);//0:新增,1:删除,2:修改
		platSvcTradeData.setBizStateCode(biz_state_code); //A-正常，N-暂停，E-退订，L-挂失
		platSvcTradeData.setOperCode(oper_code); //08-资料变更，07-退订，06-订购，04-暂停，05-恢复
		platSvcTradeData.setRemark(dimensionalCodeReqData.getRemark());
		platSvcTradeData.setIsNeedPf(BofConst.IS_NEED_PF_NO);	
		platSvcTradeData.setOprSource("08");
		
		platSvcTradeData.setStartDate(start_date);
		platSvcTradeData.setEndDate(SysDateMgr.getEndCycle20501231());
		platSvcTradeData.setOperTime(SysDateMgr.getSysTime());
		platSvcTradeData.setFirstDate(first_date);
		platSvcTradeData.setFirstDateMon(first_date_mon);
		
		platSvcTradeData.setUserId(user_id);
		platSvcTradeData.setInstId(inst_id);
		platSvcTradeData.setIntfTradeId(intfTradeId);
		platSvcTradeData.setPackageId(DimensionalCodeBean.PACKAGE_ID);
		platSvcTradeData.setProductId(DimensionalCodeBean.PRODUCT_ID);
		platSvcTradeData.setElementId(service_id);
		platSvcTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
		
		platSvcTradeData.setAllTag("02"); // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关
		platSvcTradeData.setActiveTag("1");// 主被动标记  1-主动，2-被动
		platSvcTradeData.setRsrvStr1(cur_status);
		platSvcTradeData.setRsrvStr2(cur_opr_code);
		platSvcTradeData.setRsrvStr9(DimensionalCodeBean.ORG_DOMAIN);
		platSvcTradeData.setRsrvStr8(DimensionalCodeBean.SPID);
		platSvcTradeData.setRsrvStr7(DimensionalCodeBean.BIZ_CODE);
		platSvcTradeData.setRsrvStr6(DimensionalCodeBean.BIZ_TYPE_CODE);
		platSvcTradeData.setRsrvStr5(bill_type); //0-免费 1-按次 2-包月
		btd.add(btd.getRD().getUca().getSerialNumber(), platSvcTradeData);	
	}
	/**
	 * 调用网状网 取消业务
	 */
	public IData dimensionalCodeOperateCallIBoss(IData inputParam) throws Exception {
		IData inIBossParam = new DataMap();
		inIBossParam.put("KIND_ID", "BIP2B334_T5101013_0_0");// 接口标识
		DimensionalCodeBean bean = (DimensionalCodeBean) BeanManager.createBean(DimensionalCodeBean.class);
		inIBossParam.put("PKG_SEQ", inputParam.getString("PKG_SEQ",bean.getSeqID("BIP2B334")));// 交易包流水号 发起省代码＋YYYYMMDD＋6位定长流水，序号从000001开始，增量步长为1
		inIBossParam.put("REC_NUM", inputParam.getString("REC_NUM", "1"));// 本交易包中的记录条数
		IDataset recordList = inputParam.getDataset("recordList");
		IDataset user_dataList = new DatasetList();
		for (int i = 0; i < recordList.size(); i++) {
			IData record = recordList.getData(i); // 手机号码，换号时填写新号码
			IData user_data = new DataMap(); // 手机号码，换号时填写新号码
			user_data.put("ID_TYPE", "01");//01-手机号码
			user_data.put("ID_VALUE", record.getString("ID_VALUE")); // 用户ID标识值 手机号码
			user_data.put("OPR_CODE", record.getString("OPR_CODE"));// 操作类型
			user_data.put("OPR_TIME", record.getString("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss")));
			user_data.put("CHANNEL", record.getString("CHANNEL","07"));// 02-网上营业厅， 07-10086语音，08-营业厅
			user_data.put("SPID", DimensionalCodeBean.SPID);
			user_data.put("BIZCODE", DimensionalCodeBean.BIZ_CODE);
			user_data.put("SEQ", inputParam.getString("SEQ",bean.getSeqID("BIP2B334")));
			user_dataList.add(user_data);
		}
		inIBossParam.put("USER_DATA", user_dataList);
		IData IBossResult = new DataMap();
		IBossResult = IBossCall.dealInvokeUrl("BIP2B334_T5101013_0_0", "IBOSS", inIBossParam).first();
		if ("0".equals(IBossResult.getString("X_RESULT_CODE", "-1"))) {
			IBossResult.put("X_RESULT_CODE", "0");
			IBossResult.put("X_RESULT_INFO", "调用平台接口同步成功！");
			return IBossResult;
		}
		return IBossResult;
	}
}