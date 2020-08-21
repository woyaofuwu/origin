package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.List;

import com.ailk.bizcommon.util.StrUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.ViceRealInfoReRegBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;

public class AsynUserViceInfoAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		List<RelationTradeData> tradeRels = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
		OneCardMultiNoReqData reqData = (OneCardMultiNoReqData) btd.getRD();
		String seqId = reqData.getSeqId();
		ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
		if(tradeRels != null && tradeRels.size() >0 )
		{
			for(int i=0,size = tradeRels.size();i<size;i++)
			{
				RelationTradeData tradeRela = tradeRels.get(i);
				String fmsisdn = tradeRela.getSerialNumberB();
				
				IData routeInfo = UcaInfoQry.qryUserInfoBySn(fmsisdn);
				String action = tradeRela.getModifyTag();
				String relaTypeCode = tradeRela.getRelationTypeCode();
				String custType = "0";
				String oprCode ="06";
				if(StringUtils.equals("M3", relaTypeCode)){custType="1";}
				
				if(IDataUtil.isEmpty(routeInfo))//外省号码
				{
					if(BofConst.MODIFY_TAG_ADD.equals(action))
					{
						IData param = new DataMap();
						param.put("SEQ_ID",SeqMgr.getOperId());
						param.put("USER_ID",reqData.getUca().getUserId());
						param.put("SERIAL_NUMBER",reqData.getUca().getSerialNumber());
						param.put("OPR_CODE",oprCode);
						param.put("CUST_NAME",reqData.getUca().getCustomer().getCustName());
						param.put("PROVINCE_CODE","898");
						param.put("CUST_TYPE",custType);
						param.put("CATEGORY",reqData.getCategory());
						param.put("PSPT_TYPE_CODE",reqData.getUca().getCustomer().getPsptTypeCode());
						param.put("PSPT_ID",reqData.getUca().getCustomer().getPsptId());
						param.put("ADDRESS",reqData.getAddress());
						param.put("F_PICNAME_T",reqData.getPictureT());
						param.put("F_PICNAME_Z",reqData.getPictureZ());
						param.put("F_PICNAME_F",reqData.getPictureF());
						param.put("BRAND_CODE",reqData.getUca().getBrandCode());
						param.put("F_PROVINCE_CODE","");
						param.put("U_PARTITION_ID",StrUtil.getPartition4ById(tradeRela.getUserIdB()));
						param.put("USER_ID_B",tradeRela.getUserIdB());
						param.put("SERIAL_NUMBER_B",tradeRela.getSerialNumberB());
						param.put("RELATION_TYPE_CODE",tradeRela.getRelationTypeCode());
						param.put("ROLE_TYPE_CODE",tradeRela.getRoleTypeCode());
						param.put("ROLE_CODE_A",tradeRela.getRoleCodeA());
						param.put("ROLE_CODE_B",tradeRela.getRoleCodeB());
						param.put("ORDERNO",tradeRela.getOrderno());
						param.put("SHORT_CODE",tradeRela.getShortCode());
						param.put("INST_ID",tradeRela.getInstId());
						param.put("U_START_DATE",tradeRela.getStartDate());
						param.put("U_END_DATE",tradeRela.getEndDate());
						param.put("START_DATE",tradeRela.getStartDate());
						param.put("END_DATE",tradeRela.getEndDate());
						param.put("IN_STAFF_ID",CSBizBean.getVisit().getStaffId());
						param.put("IN_DEPART_ID",CSBizBean.getVisit().getDepartId());
						param.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
						param.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
						param.put("PLAT_SEQ_ID",reqData.getSeqId());
						bean.addViceAsynInfo(param);
					}
					else if(BofConst.MODIFY_TAG_DEL.equals(action))
					{
						oprCode ="07";
						IData param = new DataMap();
						param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
						param.put("SERIAL_NUMBER_B", fmsisdn);
						param.put("U_END_DATE", tradeRela.getEndDate());
						bean.endViceAsynInfo(param);
					}
					this.mainInfoSync(reqData,oprCode,custType);
				}
				else
				{
					if(BofConst.MODIFY_TAG_ADD.equals(action))
					{
						IData param = new DataMap();
						param.put("SEQ_ID",SeqMgr.getOperId());
						param.put("USER_ID",reqData.getUca().getUserId());
						param.put("SERIAL_NUMBER",reqData.getUca().getSerialNumber());
						param.put("OPR_CODE",oprCode);
						param.put("CUST_NAME",reqData.getUca().getCustomer().getCustName());
						param.put("PROVINCE_CODE","898");
						param.put("CUST_TYPE",custType);
						param.put("CATEGORY",reqData.getCategory());
						param.put("PSPT_TYPE_CODE",reqData.getUca().getCustomer().getPsptTypeCode());
						param.put("PSPT_ID",reqData.getUca().getCustomer().getPsptId());
						param.put("ADDRESS",reqData.getAddress());
						param.put("F_PICNAME_T",reqData.getPictureT());
						param.put("F_PICNAME_Z",reqData.getPictureZ());
						param.put("F_PICNAME_F",reqData.getPictureF());
						param.put("BRAND_CODE",reqData.getUca().getBrandCode());
						param.put("F_PROVINCE_CODE","898");
						param.put("U_PARTITION_ID",StrUtil.getPartition4ById(tradeRela.getUserIdB()));
						param.put("USER_ID_B",tradeRela.getUserIdB());
						param.put("SERIAL_NUMBER_B",tradeRela.getSerialNumberB());
						param.put("RELATION_TYPE_CODE",tradeRela.getRelationTypeCode());
						param.put("ROLE_TYPE_CODE",tradeRela.getRoleTypeCode());
						param.put("ROLE_CODE_A",tradeRela.getRoleCodeA());
						param.put("ROLE_CODE_B",tradeRela.getRoleCodeB());
						param.put("ORDERNO",tradeRela.getOrderno());
						param.put("SHORT_CODE",tradeRela.getShortCode());
						param.put("INST_ID",tradeRela.getInstId());
						param.put("U_START_DATE",tradeRela.getStartDate());
						param.put("U_END_DATE",tradeRela.getEndDate());
						param.put("START_DATE",tradeRela.getStartDate());
						param.put("END_DATE",tradeRela.getEndDate());
						param.put("IN_STAFF_ID",CSBizBean.getVisit().getStaffId());
						param.put("IN_DEPART_ID",CSBizBean.getVisit().getDepartId());
						param.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
						param.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
						param.put("PLAT_SEQ_ID",reqData.getSeqId());
						if(StringUtils.equals("0", reqData.getCategory()))
						{
							UcaData fUca = UcaDataFactory.getNormalUca(tradeRela.getSerialNumberB());
							String oldCustName = fUca.getCustPerson().getCustName();
							String oldCustPsId = fUca.getCustPerson().getPsptId();
							String oldCustPsType = 	fUca.getCustPerson().getPsptTypeCode();
							param.put("RSRV_STR1", oldCustName+"|"+oldCustPsType+"|"+oldCustPsId);
						}
						bean.addViceAsynInfo(param);
						
						if(StringUtils.equals("0", reqData.getCategory()))
						{
							bean.changeViceRealInfo(fmsisdn, reqData.getAddress(), reqData.getUca().getCustomer().getCustName(), reqData.getUca().getCustomer().getPsptTypeCode(), reqData.getUca().getCustomer().getPsptId());
						}
					}
					else if(BofConst.MODIFY_TAG_DEL.equals(action))
					{
						oprCode ="07";
						IData param = new DataMap();
						param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
						param.put("SERIAL_NUMBER_B", fmsisdn);
						
						if(StringUtils.equals("0", reqData.getCategory()))
						{
							String fCustName = "和多号虚拟副号";
							String fCustType = "Z";
							String fCustpsId= "100000000000000000";
							//一卡多号一证五号校验，取消和多号的时，同步表查询出来的信息不正确，把这一段注释掉by huangmx
							/*IDataset datas = bean.qryHdhSynInfo(reqData.getUca().getSerialNumber(),fmsisdn);
							if(IDataUtil.isNotEmpty(datas))
							{
								IData temp = datas.getData(0);
								String rsrvStr1 = temp.getString("RSRV_STR1");
								if(StringUtils.isNotBlank(rsrvStr1))
								{
									String[] custInfos = rsrvStr1.split("\\|");
									if(custInfos.length >2)
									{
										fCustName = custInfos[0];
										fCustType = custInfos[1];
										fCustpsId= custInfos[2];
									}
								}
							}*/
							bean.chgBackViceRealInfo(btd,fmsisdn, fCustName, fCustType, fCustpsId);
						}
						
						param.put("U_END_DATE", tradeRela.getEndDate());
						bean.endViceAsynInfo(param);
					}
				}
			}
		}
	}
	
	public void mainInfoSync(OneCardMultiNoReqData reqData,String oprCode,String custType)throws Exception
	{
		IData callIbossReq = new DataMap(); 
		String routeValue = "898";
		IData routeData = MsisdnInfoQry.getCrmMsisonBySerialnumber(reqData.getSerial_number_b());
		if(IDataUtil.isNotEmpty(routeData))
		{
			routeValue = routeData.getString("PROV_CODE");
		}
		callIbossReq.put("MSISDN", reqData.getUca().getSerialNumber());
		callIbossReq.put("FOLLOW_MSISDN", reqData.getSerial_number_b());
		callIbossReq.put("ROUTEVALUE",reqData.getSerial_number_b());
		callIbossReq.put("ROUTETYPE", "01");
		callIbossReq.put("OPR_CODE", oprCode);	
		callIbossReq.put("MPROVINCE", "898");
		callIbossReq.put("CUST_TYPE", custType);
		callIbossReq.put("CATEGORY", reqData.getCategory());
		callIbossReq.put("ADDRESS", reqData.getAddress());
		callIbossReq.put("SYNTIME", reqData.getSynTime());
		
		if("06".equals(oprCode))
		{
			callIbossReq.put("MSISDN_NAME", reqData.getUca().getCustomer().getCustName());
			callIbossReq.put("IDCARD_TYPE", LanuchUtil.decodeIdType2(reqData.getUca().getCustomer().getPsptTypeCode()));
			callIbossReq.put("IDCARD_NUM", reqData.getUca().getCustomer().getPsptId());
			callIbossReq.put("PIC_NAMET",reqData.getPictureT());
			callIbossReq.put("PIC_NAMEZ", reqData.getPictureZ());
			callIbossReq.put("PIC_NAMEF", reqData.getPictureF());
		}
		else
		{
			callIbossReq.put("MSISDN_NAME", "");
			callIbossReq.put("IDCARD_TYPE", "");
			callIbossReq.put("IDCARD_NUM", "");
			callIbossReq.put("PIC_NAMET","");
			callIbossReq.put("PIC_NAMEZ", "");
			callIbossReq.put("PIC_NAMEF", "");
		}
		
		
		callIbossReq.put("REC_NUM", "1");
		callIbossReq.put("PKG_SEQ", "898"+SysDateMgr.getSysDateYYYYMMDD()+SeqMgr.getLogId().substring(0, 6));
		//callIbossReq.put("PKG_SEQ", reqData.getSeqId());
		callIbossReq.put("SEQ", reqData.getSeqId());
		callIbossReq.put("KIND_ID", "BIP5A013_T5101023_0_0");// 接口标识
		IDataset results = IBossCall.callHttpIBOSS7("IBOSS", callIbossReq);
	}

}
