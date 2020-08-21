package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;


import org.omg.CORBA.PUBLIC_MEMBER;

import oracle.net.aso.i;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.FamilyOperPreBean;

public class InterRoamDayBreak extends BreBase implements IBREScript{

	 private static final long serialVersionUID = 1L;
	
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag)) { //提交时校验
			
			IData reqData = databus.getData("REQDATA");// 请求的数据
			
			UcaData uca = (UcaData) databus.get("UCADATA");
			
			
			//只是变优惠
			if(StringUtils.isBlank(reqData.getString("NEW_PRODUCT_ID"))){
			 	IDataset produtIds = CommparaInfoQry.getCommparaAllCol("CSM", "2070", uca.getProductId(), "0898");
				if (IDataUtil.isNotEmpty(produtIds)) {
					String UserId = uca.getUserId();
					
					//调账务接口查询当月流量是否达到100G(国内+港澳台);
					IDataset QryTraffic = AcctCall.QryTraffic(UserId);
					if (IDataUtil.isNotEmpty(QryTraffic)) {
						
						IData trafficInfo = QryTraffic.getData(0);
						String gmtraffic = trafficInfo.getString("18030_USED_VALUE");
						String gattraffic = trafficInfo.getString("18051_USED_VALUE");
						//国内流量
						double traffic1 =Double.parseDouble(gmtraffic)/(1024*1024*1024);
						//港澳台流量
						double traffic2 =Double.parseDouble(gattraffic)/(1024*1024*1024);
						//总和
						double sum = traffic2+traffic1;
						
						//国内+港澳台流量>=100G
						if (sum>=100.00) {
							String errorMsg = "当月流量达到或超过100G后不再允许订购流量提速包";
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"20190703", errorMsg);
							return true;
						}else if(!("1".equals(produtIds.getData(0).getString("PARA_CODE1")))){//1不校验，0校验
							//取优惠
							IDataset selectedElements=new DatasetList(reqData.getString("SELECTED_ELEMENTS"));

							if (IDataUtil.isNotEmpty(selectedElements)) {
								for (int k = 0; k < selectedElements.size(); k++) {
									
									IData elements = selectedElements.getData(k);
									String elementID = elements.getString("ELEMENT_ID");
									String elementTypeCode = elements.getString("ELEMENT_TYPE_CODE");
									String modifyTag = elements.getString("MODIFY_TAG");
									if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode)) {
										if ("84009939".equals(elementID)) {//当月【84009938】优惠存在，才能办理【84009939】优惠
											IDataset pramas = FamilyOperPreBean.getDiscntCode(UserId);
											for (int j = 0; j < pramas.size(); j++) {
												if ("84009938".equals(pramas.getData(j).getString("DISCNT_CODE"))) {
													return false;
												}
											}
											
											String errorMsg = "当月提速包[84009938]不存在，不允许直接办理["+elementID+"]";
											BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"20190703", errorMsg);
											return true;
										}
									}
								}
							}

						}
						
					}
					
				}
				
			}
			
		}
		return false;
	}
}
