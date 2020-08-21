package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfferInfoQueryBean extends CSBizBean {
	private static final Logger logger = LoggerFactory.getLogger(OfferInfoQueryBean.class);
	public IData offerInfoQuery(IData inParams) throws Exception{
		if( logger.isDebugEnabled()) {
			logger.debug("=====入参======"+inParams);
		}
		String offerId = IDataUtil.chkParam(inParams, "offerID"); //入参应该死OFFER_CODE
//		IDataset offerList = UpcCallIntf.queryOfferByOfferId(offerId);
		IDataset offerList = UpcCallIntf.queryOfferByOfferId("P",offerId,"");
		if ( CollectionUtils.isEmpty(offerList) ) {
            CSAppException.apperr(GrpException.CRM_GRP_713,"根据offerId："+offerId+"查询商品信息不存在！");
		}
		IData returnInfo = new DataMap();
		IData offerInfo = offerList.first();
		returnInfo.put("offerID", offerInfo.getString("OFFER_CODE"));
		returnInfo.put("offerName", offerInfo.getString("OFFER_NAME"));
		returnInfo.put("offerType", offerInfo.getString("OFFER_TYPE"));
		int status = offerInfo.getInt("STATUS");
		returnInfo.put("offerStatus", dealStatus(status) );
		return returnInfo;
	} 
	private String dealStatus ( int status) throws Exception{
		String offerStatus ;
		switch (status) {
			case 1:
				offerStatus="草稿";
				break;
			case 2:
				offerStatus="测试";
				break;
			case 3:
				offerStatus="发布";
				break;
			case 4:
				offerStatus="下架";
				break;
			default:
				offerStatus="正常在用";
				break;
		}
		return offerStatus;
	}

}
