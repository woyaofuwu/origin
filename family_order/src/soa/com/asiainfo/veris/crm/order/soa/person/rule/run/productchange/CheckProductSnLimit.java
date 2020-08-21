package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 
 * REQ201902260008关于协助开发高危客户保有活动的需求
 * 
 * @author xurf3 指定的人才能办理指定的产品
 * 
 */
public class CheckProductSnLimit extends BreBase implements IBREScript {
	static transient final Logger logger = Logger
			.getLogger(CheckProductSnLimit.class);

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
	  String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
	  boolean bResult = false;
	  if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
	  {
			String tradetypecode = databus.getString("TRADE_TYPE_CODE", "");
			IDataset tradeDiscntSet = databus.getDataset("TF_B_TRADE_DISCNT");
			if ("110".equals(tradetypecode) || "150".equals(tradetypecode)) {
				this.getPsptFromDataBus(tradetypecode,databus);
				String serialNumber = databus.getString("SERIAL_NUMBER");
				String psptId = databus.getString("PSPT_ID");
				String discntCode = "";
				if (IDataUtil.isNotEmpty(tradeDiscntSet) && tradeDiscntSet.size() > 0) {
					for (int i = 0; i < tradeDiscntSet.size(); i++) {
						if ("0".equals(((IData) tradeDiscntSet.get(i)).getString("MODIFY_TAG"))) {
							discntCode = ((IData) tradeDiscntSet.get(i)).getString("DISCNT_CODE");
							logger.debug("优惠id:" + discntCode);
							if (!"".equals(discntCode)) {
								IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 3256, discntCode, "ZZZZ");
								if (IDataUtil.isNotEmpty(comparas)) {

									String troopId = ((IData) comparas.get(0)).getString("PARA_CODE1");
									int max = ((IData) comparas.get(0)).getInt("PARA_CODE2");
									//IDataset result = TroopMemberInfoQry.queryCountByTroopIdAndPspt(troopId, psptId);
									IDataset result = TroopMemberInfoQry.queryCountByTroopIdAndPspt(troopId, serialNumber);
									if (IDataUtil.isNotEmpty(result)&&result.size()>0) {
											int total = 0;
										 	//IDataset custInfoPspt = CustomerInfoQry.getCustIdByPspt(psptId);
											IData param = new DataMap();
					                		param.put("USER_ID", databus.getString("USER_ID"));
					                		param.put("DISCNT_CODE", discntCode);
					                		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_PLATDISCNT", param);
					                		if(IDataUtil.isNotEmpty(userDiscnts)&&userDiscnts.size()>0){
					                			 total =userDiscnts.size();
					                		}
									        if(total>=max){
												BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 201790012,
														"您已经办理"+total+"个["+discntCode+"]套餐，已达到最大限制数量，不能再办理！");
												bResult = true;
											}
									}else{
										BreTipsHelp.addNorTipsInfo(databus,	BreFactory.TIPS_TYPE_ERROR, 201790013,"您不是目标客户不能办理【"+discntCode+"】套餐");
										bResult = true;
									}
								}
							}
						}
					}
				
				}

			}
	  }
		return bResult;
	}

	private void getPsptFromDataBus(String tradetypecode, IData databus) throws Exception {
		//String psptId ="";
		String serialNumber ="";
		String userId = databus.getString("USER_ID");
		if("150".equals(tradetypecode)||"110".equals(tradetypecode)){
			UcaData uca = UcaDataFactory.getUcaByUserId(userId);
			//psptId = uca.getCustomer().getPsptId();
			serialNumber = uca.getSerialNumber();
		}
		//databus.put("PSPT_ID", psptId);
		databus.put("SERIAL_NUMBER", serialNumber);
	}
}
