package com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.OpenElectrombileUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.thingsinternet.order.requestdata.OpenElectrombileUserReqData;

public class OpenElectrombileUserTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		// TODO Auto-generated method stub
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)bd.getRD();
		String operType = reqData.getOperType();
		UcaData uca = reqData.getUca();
		
		bd.getMainTradeData().setRsrvStr1(operType);
		bd.getMainTradeData().setRsrvStr2("0");
		bd.getMainTradeData().setRsrvStr3("0");
		bd.getMainTradeData().setRsrvStr4("0");
		bd.getMainTradeData().setRsrvStr5("0");
		
		if(reqData.getMainUca() != null)
		{
			bd.getMainTradeData().setRsrvStr6(reqData.getMainUca().getSerialNumber());
		}
		else
		{
			if("2".equals(operType) || "3".equals(operType) || "4".equals(operType))
			{
				IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
				if(IDataUtil.isEmpty(result))
				{
					CSAppException.apperr(UUException.CRM_UU_103, uca.getUserId(), "88");
				}
				
				String mainSn = result.getData(0).getString("SERIAL_NUMBER_A").substring(2);
				bd.getMainTradeData().setRsrvStr6(mainSn);
			}
		}
		
		if("1".equals(operType))//订购
		{
			genTradeRelaInfo(bd);
			genePlatTrade(bd);
			geneTradeCustPerson(bd);
			if("1".equals(reqData.getIsPayFee()))
			{
				geneTradePayrelation(bd);
			}
			geneTradeDiscntAdd(bd);
		}
		else if("2".equals(operType))//退订
		{
			geneTradePayrelation(bd);
			genePlatTradeEdit(bd);
  			geneTradeDiscntDel(bd);
  			genTradeRelaInfo(bd);
		}
		else if("3".equals(operType))//暂停
		{
			genePlatTradeEdit(bd);
  			geneTradeDiscntDel(bd);
		}
		else if("4".equals(operType))//恢复
		{
			genePlatTradeEdit(bd);
  			geneTradeDiscntAdd(bd);
		}
		else if("5".equals(operType))//变更
		{
			geneTradeCustPerson(bd);
  			genTradeRelaInfo(bd);
			geneTradePayrelation(bd);
		}
	}

	private void genTradeRelaInfo(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		String operType = reqData.getOperType();
		UcaData uca = reqData.getUca();
		
		if("1".equals(operType))
		{
			UcaData mainUca = reqData.getMainUca();
			String virtualUserId = SeqMgr.getUserId();
			
			RelationTradeData mainUUTD = new RelationTradeData();
			mainUUTD.setUserIdA(virtualUserId);
			mainUUTD.setSerialNumberA("XC" + mainUca.getSerialNumber());
			mainUUTD.setUserIdB(mainUca.getUserId());
			mainUUTD.setSerialNumberB(mainUca.getSerialNumber());
			mainUUTD.setRelationTypeCode("88");
			mainUUTD.setRoleTypeCode("0");
			mainUUTD.setRoleCodeA("0");
			mainUUTD.setRoleCodeB("1");
			mainUUTD.setOrderno("0");
			mainUUTD.setStartDate(reqData.getAcceptTime());
			mainUUTD.setEndDate(SysDateMgr.getTheLastTime());
			mainUUTD.setInstId(SeqMgr.getInstId());
			mainUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);			
			btd.add(uca.getSerialNumber(), mainUUTD);
			
			RelationTradeData uuTD = new RelationTradeData();
			uuTD.setUserIdA(virtualUserId);
			uuTD.setSerialNumberA("XC" + mainUca.getSerialNumber());
			uuTD.setUserIdB(uca.getUserId());
			uuTD.setSerialNumberB(uca.getSerialNumber());
			uuTD.setRelationTypeCode("88");
			uuTD.setRoleTypeCode("0");
			uuTD.setRoleCodeA("0");
			uuTD.setRoleCodeB("2");
			uuTD.setOrderno("0");
			uuTD.setStartDate(reqData.getAcceptTime());
			uuTD.setEndDate(SysDateMgr.getTheLastTime());
			uuTD.setInstId(SeqMgr.getInstId());
			uuTD.setModifyTag(BofConst.MODIFY_TAG_ADD);			
			btd.add(uca.getSerialNumber(), uuTD);
		}
		else if("2".equals(operType))
		{
			IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
			if(IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(UUException.CRM_UU_103, uca.getUserId(), "88");
			}
			
			String userIdA = result.getData(0).getString("USER_ID_A");
			IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "88");
			for(int i = 0, size = mebList.size(); i < size; i++)
			{
				IData meb = mebList.getData(i);
				RelationTradeData uuTD = new RelationTradeData(meb);
				uuTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
				uuTD.setEndDate(reqData.getAcceptTime());
				btd.add(uca.getSerialNumber(), uuTD);
			}
		}
		else if("5".equals(operType))
		{
			UcaData mainUca = reqData.getMainUca();
			IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
			if(IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(UUException.CRM_UU_103, uca.getUserId(), "88");
			}
			
			String userIdA = result.getData(0).getString("USER_ID_A");
			IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "88");
			for(int i = 0, size = mebList.size(); i < size; i++)
			{
				IData meb = mebList.getData(i);
				if("1".equals(meb.getString("ROLE_CODE_B")))//主号
				{
					RelationTradeData uuTD = new RelationTradeData(meb);
					uuTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
					uuTD.setEndDate(reqData.getAcceptTime());
					btd.add(uca.getSerialNumber(), uuTD);
					btd.getMainTradeData().setRsrvStr9(meb.getString("SERIAL_NUMBER_A").substring(2));
				}
				else//副号
				{
					RelationTradeData uuTD = new RelationTradeData(meb);
					uuTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
					uuTD.setSerialNumberA("XC" + mainUca.getSerialNumber());
					btd.add(uca.getSerialNumber(), uuTD);
				}
			}
				
			//新增主号UU关系
			RelationTradeData mainUUTD = new RelationTradeData();
			mainUUTD.setUserIdA(userIdA);
			mainUUTD.setSerialNumberA("XC" + mainUca.getSerialNumber());
			mainUUTD.setUserIdB(mainUca.getUserId());
			mainUUTD.setSerialNumberB(mainUca.getSerialNumber());
			mainUUTD.setRelationTypeCode("88");
			mainUUTD.setRoleTypeCode("0");
			mainUUTD.setRoleCodeA("0");
			mainUUTD.setRoleCodeB("1");
			mainUUTD.setOrderno("0");
			mainUUTD.setStartDate(reqData.getAcceptTime());
			mainUUTD.setEndDate(SysDateMgr.getTheLastTime());
			mainUUTD.setInstId(SeqMgr.getInstId());
			mainUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);			
			btd.add(uca.getSerialNumber(), mainUUTD);			
		}
	}
	
	private void genePlatTrade(BusiTradeData btd) throws Exception
	{
		btd.getMainTradeData().setRsrvStr3("1");
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		String operType = reqData.getOperType();
		UcaData uca = reqData.getUca();
		String serviceId = reqData.getServiceId();
		
		IDataset result = CommparaInfoQry.getCommByParaAttr("CSM", "5002", "ZZZZ");
		if(IDataUtil.isEmpty(result))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "COMMPARA没有配置5002参数");
		}
		
		IData platData = new DataMap();
		platData.put("SERVICE_ID", serviceId);
		platData.put("OPER_CODE", "06");
		PlatSvcData platSvcData = new PlatSvcData(platData);
		List<ProductModuleData> productModuleDatas = new ArrayList<ProductModuleData>();
		productModuleDatas.add(platSvcData);
		ProductModuleCreator.createProductModuleTradeData(productModuleDatas, btd);
		
		btd.getMainTradeData().setRsrvStr8(serviceId);
	}
	
	public void geneTradeCustPerson(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		UcaData uca = reqData.getUca();
		
		btd.getMainTradeData().setRsrvStr2("1");
		btd.getMainTradeData().setRsrvStr10(reqData.getMainUca().getSerialNumber());
		
		CustPersonTradeData custPersonTD = uca.getCustPerson().clone();
		custPersonTD.setPhone(reqData.getMainUca().getSerialNumber());
		custPersonTD.setContactPhone(reqData.getMainUca().getSerialNumber());
		custPersonTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		custPersonTD.setRemark("行车卫士联系人修改");
		btd.add(uca.getSerialNumber(), custPersonTD);
	}
	
	public void geneTradePayrelation(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		UcaData uca = reqData.getUca();
		String operType = reqData.getOperType();
		String ispayFee = reqData.getIsPayFee();
		
		btd.getMainTradeData().setRsrvStr4("1");
		if("1".equals(operType))
		{
			if("1".equals(ispayFee))
			{
				UcaData mainUca = reqData.getMainUca();
				PayRelationTradeData payRelationTD = new PayRelationTradeData();
				payRelationTD.setUserId(uca.getUserId());
				payRelationTD.setAcctId(mainUca.getAcctId());
				payRelationTD.setPayitemCode("-1");
				payRelationTD.setAcctPriority("0");
				payRelationTD.setUserPriority("0");
				payRelationTD.setBindType("1");
				payRelationTD.setStartCycleId(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
				payRelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
				payRelationTD.setActTag("1");
				payRelationTD.setDefaultTag("0");
				payRelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
				payRelationTD.setLimitType("0");
				payRelationTD.setLimit("0");
				payRelationTD.setComplementTag("0");
				payRelationTD.setAddupMethod("0");
				payRelationTD.setAddupMonths("0");
				payRelationTD.setInstId(SeqMgr.getInstId());
				btd.add(uca.getSerialNumber(), payRelationTD);
			}
		}
		else if("2".equals(operType))
		{
			String mainUserId = null;
			IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
			if(IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(UUException.CRM_UU_103, uca.getUserId(), "88");
			}
			
			String userIdA = result.getData(0).getString("USER_ID_A");
			IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "88");
			for(int i = 0, size = mebList.size(); i < size; i++)
			{
				IData meb = mebList.getData(i);
				if("1".equals(meb.getString("ROLE_CODE_B")))//主号
				{
					mainUserId = meb.getString("USER_ID_B");
					break;
				}
			}
			if(StringUtils.isBlank(mainUserId))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到主号码");
			}
			IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(mainUserId);
			if(IDataUtil.isEmpty(acctInfo))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID[" + mainUserId +"]找不到账户资料");
			}
			
			IDataset payRelaList = PayRelaInfoQry.getMemberPayRelaxc(uca.getUserId(), acctInfo.getString("ACCT_ID"), "-1");
			if(IDataUtil.isNotEmpty(payRelaList))
			{
				PayRelationTradeData payRelationTD = new PayRelationTradeData(payRelaList.getData(0));
				payRelationTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
				payRelationTD.setRemark("删除行车卫士付费关系");
				payRelationTD.setEndCycleId(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
				btd.add(uca.getSerialNumber(), payRelationTD);
			}
		}
		else if("5".equals(operType))
		{
			if("1".equals(ispayFee))
			{
				UcaData mainUca = reqData.getMainUca();
				PayRelationTradeData payRelationTD = new PayRelationTradeData();
				payRelationTD.setUserId(uca.getUserId());
				payRelationTD.setAcctId(mainUca.getAcctId());
				payRelationTD.setPayitemCode("-1");
				payRelationTD.setAcctPriority("0");
				payRelationTD.setUserPriority("0");
				payRelationTD.setBindType("1");
				payRelationTD.setStartCycleId(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
				payRelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
				payRelationTD.setActTag("1");
				payRelationTD.setDefaultTag("0");
				payRelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
				payRelationTD.setLimitType("0");
				payRelationTD.setLimit("0");
				payRelationTD.setComplementTag("0");
				payRelationTD.setAddupMethod("0");
				payRelationTD.setAddupMonths("0");
				payRelationTD.setInstId(SeqMgr.getInstId());
				btd.add(uca.getSerialNumber(), payRelationTD);
			}
			
			//删除原来的付费关系
			String mainUserId = null;
			IDataset result = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(uca.getUserId(), "88", "2");
			if(IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(UUException.CRM_UU_103, uca.getUserId(), "88");
			}
			
			String userIdA = result.getData(0).getString("USER_ID_A");
			IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "88");
			for(int i = 0, size = mebList.size(); i < size; i++)
			{
				IData meb = mebList.getData(i);
				if("1".equals(meb.getString("ROLE_CODE_B")))//主号
				{
					mainUserId = meb.getString("USER_ID_B");
					break;
				}
			}
			if(StringUtils.isBlank(mainUserId))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到主号码");
			}
			IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(mainUserId);
			if(IDataUtil.isEmpty(acctInfo))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID[" + mainUserId +"]找不到账户资料");
			}
			
			IDataset payRelaList = PayRelaInfoQry.getMemberPayRelaxc(uca.getUserId(), acctInfo.getString("ACCT_ID"), "-1");
			if(IDataUtil.isNotEmpty(payRelaList))
			{
				PayRelationTradeData payRelationTD = new PayRelationTradeData(payRelaList.getData(0));
				payRelationTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
				payRelationTD.setRemark("删除行车卫士付费关系");
				payRelationTD.setEndCycleId(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
				btd.add(uca.getSerialNumber(), payRelationTD);
			}
		}
	}
	
	public void geneTradeDiscntAdd(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		UcaData uca = reqData.getUca();
		
		btd.getMainTradeData().setRsrvStr5("1");
		
		DiscntTradeData discntTD = new DiscntTradeData();
		discntTD.setUserId(uca.getUserId());
		discntTD.setProductId("50000000");
		discntTD.setPackageId("50000000");
		discntTD.setElementId(reqData.getDiscntCode());
		discntTD.setSpecTag("0");
		discntTD.setInstId(SeqMgr.getInstId());
		discntTD.setStartDate(reqData.getAcceptTime());
		discntTD.setEndDate(SysDateMgr.getTheLastTime());
		discntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		btd.add(uca.getSerialNumber(), discntTD);
	}
	
	public void geneTradeDiscntDel(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		UcaData uca = reqData.getUca();
		
		btd.getMainTradeData().setRsrvStr5("1");
		
		DiscntTradeData  userDiscnt = OpenElectrombileUserBean.getUserDiscnt(uca);
		if(userDiscnt == null)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到用户和车宝的优惠资料");
		}
		
		DiscntTradeData discntTD = userDiscnt.clone();
		discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
		discntTD.setEndDate(reqData.getAcceptTime());
		btd.add(uca.getSerialNumber(), discntTD);
	}
	
	public void genePlatTradeEdit(BusiTradeData btd) throws Exception
	{
		OpenElectrombileUserReqData reqData = (OpenElectrombileUserReqData)btd.getRD();
		UcaData uca = reqData.getUca();
		String operType = reqData.getOperType();
		
		btd.getMainTradeData().setRsrvStr3("1");
		
		IDataset result = CommparaInfoQry.getCommByParaAttr("CSM", "5002", "ZZZZ");
		if(IDataUtil.isEmpty(result))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "COMMPARA没有5002的参数配置");
		}
		
		boolean flag = false;
		
		PlatSvcTradeData userPlatSvc = OpenElectrombileUserBean.getUserPlatSvc(uca);
		if(userPlatSvc == null)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到用户的和车宝平台服务资料");
		}
		
		IData platData = new DataMap();
		platData.put("SERVICE_ID", userPlatSvc.getElementId());
		
		if("2".equals(operType))
		{
			platData.put("OPER_CODE", "07");
		}
		else if("3".equals(operType))
		{
			platData.put("OPER_CODE", "04");
		}
		else if("4".equals(operType))
		{
			platData.put("OPER_CODE", "05");
		}
		PlatSvcData platSvcData = new PlatSvcData(platData);
		
		List<ProductModuleData> productModuleDatas = new ArrayList<ProductModuleData>();
		productModuleDatas.add(platSvcData);
		ProductModuleCreator.createProductModuleTradeData(productModuleDatas, btd);
		
		btd.getMainTradeData().setRsrvStr8(userPlatSvc.getElementId());
	}
}
