package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement.AgreementInfoBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreateAgreementListBean extends CSBizBean {
	protected static final Logger logger = Logger.getLogger(CreateAgreementListBean.class);

	private static final String AGREEMENT_TYPE_BODY = "AGCONTENT";

	public IData createAgreementList( IData inParams ) throws Exception{
		if( logger.isDebugEnabled() ) {
			logger.debug("入参打印====="+inParams);
		}
		String tradeId = IDataUtil.chkParam(inParams, "tradeId");
		String mgmtDistrict = IDataUtil.chkParam(inParams, "mgmtDistrict");
		IData input = new DataMap();
		input.put("BI_SN", tradeId );
		input.put("EPARCHY_CODE", mgmtDistrict );
		IData retDs = new DataMap();
		retDs.put(GroupStandardConstans.GROUP_TRADE_ID, tradeId );
		retDs.put("fileName", "" );

		IDataset eweSet =  Dao.qryByCode("TF_B_EWE", "SEL_BY_BISN_EPARCHYCODE", input);
		if( CollectionUtils.isEmpty(eweSet)) {
			retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED );
			retDs.put(GroupStandardConstans.RES_BIZ_DESC, "通过tradeId:" + tradeId +"和mgmtDistrict"+
					mgmtDistrict+"找不到TF_B_EWE表的记录！" );

			return retDs;
		}
		//判断产品列表
		String proId ="";
		IDataset goodsInfoList = inParams.getDataset("goodsInfoList", new DatasetList());
		if( IDataUtil.isNotEmpty( goodsInfoList ) ) {
			proId = goodsInfoList.first().getString("PRODUCT_ID");
		}else {
			//根据 tradeId 找到产品信息
			retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED );
			retDs.put(GroupStandardConstans.RES_BIZ_DESC, "根据产品编码PRODUCT_ID"+
					proId+ "找不到模板合同信息记录！");
			return retDs;
		}
		input.put("PRODUCT_ID", proId);
		input.put("AGREEMENT_TYPE", AGREEMENT_TYPE_BODY );
		IDataset agreementSet =  AgreementInfoBean.queryAgreementDefInfo(input);
		if (IDataUtil.isEmpty( agreementSet )) {
			retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED );
			retDs.put(GroupStandardConstans.RES_BIZ_DESC, "根据产品编码PRODUCT_ID"+
					proId+ "找不到模板合同信息记录！");
		}else{
			retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_SUCCESS );
			retDs.put(GroupStandardConstans.GROUP_TRADE_ID, tradeId );
			retDs.put("fileName", agreementSet.first().getString("AGREEMENT_NAME") );
		}
		return retDs;
	}
}
