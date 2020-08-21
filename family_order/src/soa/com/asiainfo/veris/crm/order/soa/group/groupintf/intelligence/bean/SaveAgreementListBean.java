package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaveAgreementListBean extends CSBizBean {
	protected static final Logger logger = Logger.getLogger(SaveAgreementListBean.class);

	public IData saveAgreementList( IData inParams ) throws Exception{
		if( logger.isDebugEnabled() ) {
			logger.debug("入参打印====="+inParams);
		}
		String tradeId = IDataUtil.chkParam(inParams, "tradeId");
		String mgmtDistrict = IDataUtil.chkParam(inParams, "mgmtDistrict");
		String signType = IDataUtil.chkParam(inParams, "signType");
		IData retDs = new DataMap();
		retDs.put(GroupStandardConstans.GROUP_TRADE_ID, tradeId );
		if(StringUtils.equals("0", signType)) {
			//线下签约直接文件上传 文件入表
			String photoAppendixName = inParams.getString("photoAppendixName");
			if(StringUtils.isBlank(photoAppendixName)) {
				retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED );
				retDs.put(GroupStandardConstans.RES_BIZ_DESC, "线下签约时，拍照附件名称不能为空！" );
				return retDs;
			}
			
		}else {
			//线上签约
		}
		IData input = new DataMap();
		input.put("BI_SN", tradeId );
		input.put("EPARCHY_CODE", mgmtDistrict );

		IDataset eweSet =  Dao.qryByCode("TF_B_EWE", "SEL_BY_BISN_EPARCHYCODE", input);
		if( CollectionUtils.isEmpty(eweSet)) {
			retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED );
			retDs.put(GroupStandardConstans.RES_BIZ_DESC, "通过tradeId:" + tradeId +"和mgmtDistrict"+
					mgmtDistrict+"找不到TF_B_EWE表的记录！" );

			return retDs;
		}
		return retDs;
	}
}
