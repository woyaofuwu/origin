package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * 校验用户办理的优惠是否是生日权益包，如果是的话按照入参指定的开始，结束时间订购资费
 * dengyi5
 */
public class callTransSerialnumberCheckAction implements ITradeAction{

	private static final Logger logger = Logger.getLogger(callTransSerialnumberCheckAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
	
		if (logger.isDebugEnabled()) {
			// logger.debug("callTransSerialnumberCheckActionxxxxxxxxxxxx25 "+btd);
		}
//		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
		IData param = btd.getRD().getPageRequestData();
		if (logger.isDebugEnabled()) {
			//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx31 " + param);
		}				 
		

		IDataset elementds = new DatasetList(param.getString("SELECTED_ELEMENTS",""));
		//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx35 " + elementds);

		if (elementds != null && elementds.size() > 0) {
			for (int i = 0; i < elementds.size(); i++) {
				IData eledata = elementds.getData(i);
				//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx40 " + eledata);

				// \"ELEMENT_ID\":\"12\",\"ELEMENT_TYPE_CODE\":\"S\",\"PRODUCT_ID\":\"84001306\",\"PACKAGE_ID\":\"10004472\",\"ATTR_PARAM\
				String elementId = eledata.getString("ELEMENT_ID", "").trim();
				String elementTypeCode = eledata.getString("ELEMENT_TYPE_CODE", "").trim();
				//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx45 " + elementId + "\t" + elementTypeCode);

				if(elementId.equals("12")&&elementTypeCode.equals("S")){//无条件转移
					String modifytag = eledata.getString("MODIFY_TAG", "").trim();
					if (modifytag.equals("0") || modifytag.equals("2")) {// 如果是新增或修改
					// String productId = eledata.getString("PRODUCT_ID","").trim();
					// String productId = eledata.getString("PACKAGE_ID","").trim();
//					IDataset attrParamDs = eledata.getDataset("ATTR_PARAM");
					IDataset attrParamDs = new DatasetList(eledata.getString("ATTR_PARAM",""));

						//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx51 " + attrParamDs);

					if (attrParamDs != null && attrParamDs.size() > 0) {
						for (int j = 0; j < attrParamDs.size(); j++) {
							IData attrdata = attrParamDs.getData(j);
								//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx56 " + attrdata);

							//"ATTR_VALUE\":\"13976108478\",\"ATTR_CODE\":\"V12V1\"
							String attrvalue = attrdata.getString("ATTR_VALUE","").trim();
							String attrcode = attrdata.getString("ATTR_CODE","").trim();
								//System.out.println("callTransSerialnumberCheckActionxxxxxxxxxxxx61 " + attrvalue + "\t" + attrcode);

							if(attrcode.equals("V12V1")){//无条件转移
								if(attrvalue.startsWith("0898")){
										CSAppException.apperr(CrmCommException.CRM_COMM_103, "无条件转移的号码，不能以0898开头。");
								}
							}
						}
					}					
				}
			}
		}		
	}		
	}
		 
}
