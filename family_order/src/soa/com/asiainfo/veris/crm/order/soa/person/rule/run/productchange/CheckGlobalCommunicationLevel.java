package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common.CheckHdhkActiveLimit;

import freemarker.template.utility.DateUtil;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckGlobalCommunicationLevel
 * @Description: 
 *               用户必须有全球通银卡才能办理5G直通包，全球通金卡才能办理5G直通包-8折，全球通白金卡才能办理5G直通包-5折，全球通钻卡（含尊享套餐
 *               ）才能办理5G直通包 -3 折，即用户只需满足全球通钻卡或尊享套餐其中一个条件就可办理5G直通包-3 折。。
 * @version: v1.0.0
 * @author: fusr
 * @date: 20200107
 */
public class CheckGlobalCommunicationLevel extends BreBase implements
		IBREScript {
	private static final long serialVersionUID = -2792489391805861229L;

	private static Logger logger = Logger
			.getLogger(CheckGlobalCommunicationLevel.class);

	@SuppressWarnings("null")
	@Override
	public boolean run(IData databus, BreRuleParam param) throws Exception {
		// UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
		 IData reqData = databus.getData("REQDATA");// 请求的数据
		//IDataset TradeDiscntset = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset TradeDiscntset = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		String userId = databus.getString("USER_ID");
		String serial_number = databus.getString("SERIAL_NUMBER");
		boolean flag = false;
		if (StringUtils.isBlank(xChoiceTag)
				|| StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
		{
			if (TradeDiscntset != null && TradeDiscntset.size() > 0) {
				// 场景一
				for (int i = 0; i < TradeDiscntset.size(); i++) {
					IData discnt = TradeDiscntset.getData(i);
					/*String discnt_code = discnt.getString("DISCNT_CODE", "");
					String modfty_tag=discnt.getString("MODIFY_TAG");*/
					String discnt_code = discnt.getString("ELEMENT_ID", "");
					String modfty_tag=discnt.getString("MODIFY_TAG");
					if(BofConst.MODIFY_TAG_ADD.equals(modfty_tag)){
						IDataset commpara7710 = CommparaInfoQry
								.getCommparaInfoByCode("CSM", "7710", "GLOBAL",
										discnt_code, "0898");

						// 当7710存在的时候
						if (IDataUtil.isNotEmpty(commpara7710)) {
							IData data7710 = commpara7710.getData(0);
							String checklevel = data7710
									.getString("PARA_CODE2", "");
							String checkelemenid = data7710.getString("PARA_CODE3",
									"");
							String vipClassId = "";
							String[] checklevel_items = checklevel.split("#");
							IData in = new DataMap();
							in.put("SERIAL_NUMBER", serial_number);
							IData custVipInfos = UserClassInfoQry
									.queryUserClassBySN(in);
							if (IDataUtil.isNotEmpty(custVipInfos)) {
								vipClassId = custVipInfos.getString(
										"USER_CLASS", "");// 全球通等级

							}

							// 判断全球通等级与套餐是否匹配
							for (int b = 0; b < checklevel_items.length; b++) {

								if (!vipClassId.equals(checklevel_items[b])) {//
									flag = true;
								}
							}

							// 判断是否尊享用户
							if (flag && !"".equals(checkelemenid)) {
								IDataset userdiscntinfo = UserDiscntInfoQry
										.getAllValidDiscntByUserId(userId);
								if (IDataUtil.isNotEmpty(userdiscntinfo)) {
									for (int d = 0; d < userdiscntinfo.size(); d++) {
										String user_discntcode = userdiscntinfo
												.getData(d).getString(
														"DISCNT_CODE", "");
										String[] checkelementType_items = checkelemenid
												.split("#");
										// 用户正在办理的优惠中包含尊享套餐且办理时间与5G直通包—3折优惠的办理时间不相同时进行拦截
										for (int k = 0; k < checkelementType_items.length; k++) {
											if (user_discntcode
													.equals(checkelementType_items[k])) {
												flag = false;
												break;
											}
										}
										
										if(!flag){
											break;
										}
									}
								}
							}
						}
					}
				}
				
				

				if (flag) {
					// 场景2
					for (int i = 0; i < TradeDiscntset.size(); i++) {
						IData discnt = TradeDiscntset.getData(i);
						String discnt_code = discnt.getString("ELEMENT_ID", "");
						String modfty_tag=discnt.getString("MODIFY_TAG");
						String start_date = discnt.getString("START_DATE", "");				
						if(BofConst.MODIFY_TAG_ADD.equals(modfty_tag)){
							
							
							
							IDataset commpara7710 = CommparaInfoQry
									.getCommparaInfoByCode("CSM", "7710", "GLOBAL",
											discnt_code, "0898");

							// 当7710存在的时候
							if (IDataUtil.isNotEmpty(commpara7710)) {
								IData data7710 = commpara7710.getData(0);
								String checkelemenid = data7710.getString("PARA_CODE3",
										"");
								
								
								if(!"".equals(checkelemenid)){
									String[] checkelementType_items = checkelemenid.split("#");
									for(int z = 0;z<checkelementType_items.length;z++){
										String element = checkelementType_items[z];
										
										for (int j = 0; j < TradeDiscntset.size(); j++) {
											IData discnt2 = TradeDiscntset.getData(j);
											String discnt_code2 = discnt2.getString("ELEMENT_ID", "");
											String modfty_tag2=discnt2.getString("MODIFY_TAG");
											String start_date2 = discnt2.getString("START_DATE", "");	
											
											if(discnt_code.equals(discnt_code2)){
												continue;
											}
											if(BofConst.MODIFY_TAG_ADD.equals(modfty_tag2)){
												if(discnt_code2.equals(element)){
													if(start_date.length()>=10&&start_date2.length()>=10){
														String start_date_times1 =start_date.substring(0,10) ;//df.format(sdf.parse(start_date));
							    			    		String start_date_times2 =start_date2.substring(0,10) ;//df.format(sdf.parse(start_date2));
					                    				if(start_date_times1.equals(start_date_times2)){
					                    					flag=false;	
					                    					break;
					                    				}
													}
						    			    	
				                    			}
											}
											
										}
									}
								
									
									
								}
								
								
							}
						}
						
						if(!flag){
							break;
						}
						
					}
				}

			}

		}

		return flag;
	}
}
