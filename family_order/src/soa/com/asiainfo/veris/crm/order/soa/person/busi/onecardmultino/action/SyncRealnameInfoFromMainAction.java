package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.ViceRealInfoReRegBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;


/**
 * 和多号绑定虚拟副卡时，当主号实名信息完整，将主卡照片信息同步给副号
 * @author
 *
 */
public class SyncRealnameInfoFromMainAction implements ITradeAction{
	Logger logger = Logger.getLogger(SyncRealnameInfoFromMainAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("------进入SyncRealnameInfoFromMainAction------");
		OneCardMultiNoReqData rd = (OneCardMultiNoReqData) btd.getRD();
		String category = rd.getCategory();
		String picNameT = rd.getPictureT();
		String picNameF = rd.getPictureF();
		String picNameZ = rd.getPictureZ();
		if(StringUtils.isEmpty(picNameZ)){
			picNameT = "";
			picNameF = "";
			picNameZ = "";
		}
		String serialNumber = rd.getSerialNumber();
		String serialNumberb = rd.getSerial_number_b();
		String operCode = rd.getOprCode();
		UcaData ucaA = UcaDataFactory.getNormalUca(serialNumber);
//		UcaData ucaB = UcaDataFactory.getNormalUca(serialNumberb);
		String psptAddress = ucaA.getCustPerson().getPsptAddr();
		String custName = ucaA.getCustPerson().getCustName();
		String psptId = ucaA.getCustomer().getPsptId();
		String psptType = ucaA.getCustomer().getPsptTypeCode();
		if("1".equals(ucaA.getCustomer().getIsRealName()) && StringUtils.isNotBlank(psptAddress) 
				&& StringUtils.isNotBlank(custName) && StringUtils.isNotBlank(psptId)&& StringUtils.isNotBlank(psptType) && "0".equals(category) && "06".equals(operCode)){
			if("0".equals(btd.getMainTradeData().getInModeCode())){
				String psptIdB = btd.getRD().getPageRequestData().getString("PSPT_ID","");
				String custNameB = btd.getRD().getPageRequestData().getString("CUST_NAME","");
				if(psptIdB.equals(ucaA.getCustomer().getPsptId()) && custNameB.equals(ucaA.getCustomer().getCustName())){
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "主副号实名信息不一致！");
				}
			}
			//插入主副信息同步表
	        List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
	        RelationTradeData relationTradeData = null;
	        if(DataUtils.isNotEmpty(relationTradeDatas)){
	        	for (int i = 0,size = relationTradeDatas.size(); i < size; i++)
				{
					relationTradeData = relationTradeDatas.get(i);
					if (BofConst.MODIFY_TAG_ADD.equals(relationTradeData.getModifyTag())
							&& "M2".equals(relationTradeData.getRelationTypeCode())
							&& serialNumber.equals(relationTradeData.getSerialNumberA())
							&& serialNumberb.equals(relationTradeData.getSerialNumberB()))
					{
						
						ViceRealInfoReRegBean vBean = BeanManager.createBean(ViceRealInfoReRegBean.class);
						IData inputparam = new DataMap();
						inputparam.put("SERIAL_NUMBER_B", serialNumberb);
						IDataset bindInfo = vBean.qryHdhSynInfo(inputparam);
						
						IData custInfoChargeSys = new DataMap();
						custInfoChargeSys.put("CUST_NAME", ucaA.getCustomer().getCustName());
						custInfoChargeSys.put("PSPT_TYPE_CODE", ucaA.getCustomer().getPsptTypeCode());
						custInfoChargeSys.put("PSPT_ID", ucaA.getCustomer().getPsptId());
						custInfoChargeSys.put("ADDRESS", ucaA.getCustPerson().getPsptAddr());
						custInfoChargeSys.put("F_PICNAME_T", picNameT);
						custInfoChargeSys.put("F_PICNAME_Z", picNameF);
						custInfoChargeSys.put("F_PICNAME_F", picNameZ);
						custInfoChargeSys.put("SERIAL_NUMBER", serialNumber);
						custInfoChargeSys.put("SERIAL_NUMBER_B", serialNumberb);
						if(IDataUtil.isNotEmpty(bindInfo))
						{
							logger.debug("更新主副信息同步表");
							vBean.updateViceAsynInfo(custInfoChargeSys);
						}
						else
						{
							logger.debug("插入一条新纪录主副信息同步表");
							custInfoChargeSys.put("SEQ_ID",SeqMgr.getOperId());
							custInfoChargeSys.put("USER_ID",ucaA.getUserId());
							custInfoChargeSys.put("SERIAL_NUMBER",ucaA.getSerialNumber());
							custInfoChargeSys.put("OPR_CODE","08");
							custInfoChargeSys.put("CUST_NAME",ucaA.getCustomer().getCustName());
							custInfoChargeSys.put("PROVINCE_CODE","898");
							custInfoChargeSys.put("CUST_TYPE","0");
							custInfoChargeSys.put("CATEGORY","0");
							custInfoChargeSys.put("PSPT_TYPE_CODE",ucaA.getCustomer().getPsptTypeCode());
							custInfoChargeSys.put("PSPT_ID",ucaA.getCustomer().getPsptId());
							custInfoChargeSys.put("BRAND_CODE",ucaA.getBrandCode());
							custInfoChargeSys.put("F_PROVINCE_CODE","898");
							custInfoChargeSys.put("U_PARTITION_ID",StrUtil.getPartition4ById(serialNumberb+"0"));
							custInfoChargeSys.put("USER_ID_B",serialNumberb+"0");
							custInfoChargeSys.put("SERIAL_NUMBER_B",serialNumberb);
							custInfoChargeSys.put("RELATION_TYPE_CODE","M2");
							custInfoChargeSys.put("ROLE_TYPE_CODE","0");
							custInfoChargeSys.put("ROLE_CODE_A","1");
							custInfoChargeSys.put("ROLE_CODE_B","2");
							custInfoChargeSys.put("ORDERNO",relationTradeData.getOrderno());
							custInfoChargeSys.put("SHORT_CODE","");
							custInfoChargeSys.put("INST_ID",relationTradeData.getInstId());
							custInfoChargeSys.put("U_START_DATE",relationTradeData.getStartDate());
							custInfoChargeSys.put("U_END_DATE",relationTradeData.getEndDate());
							custInfoChargeSys.put("START_DATE",relationTradeData.getStartDate());
							custInfoChargeSys.put("END_DATE",relationTradeData.getEndDate());
							custInfoChargeSys.put("IN_STAFF_ID",CSBizBean.getVisit().getStaffId());
							custInfoChargeSys.put("IN_DEPART_ID",CSBizBean.getVisit().getDepartId());
							custInfoChargeSys.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
							custInfoChargeSys.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
							custInfoChargeSys.put("PLAT_SEQ_ID",rd.getSeqId());
							
							vBean.addViceAsynInfo(custInfoChargeSys);
						}
						break;
					}
				}
	        }
		}
	}
}
