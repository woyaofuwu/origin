package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class TradeRuleCheckBean extends CSBizBean {

	@SuppressWarnings("unused")
	public IData  checkTrade(IData params) throws Exception{
		String offerId =IDataUtil.chkParam(params, "offerId");
		String action = IDataUtil.chkParam(params, "action");
		String tradeId = IDataUtil.chkParam(params, "tradeId");
		String mgmtDistrict = IDataUtil.chkParam(params, "mgmtDistrict");
		//TODO 查询依赖互斥 目前只能做到的是用户(需要明确用户是集团用户还是成员)原来的主产品和新订购的是不是同一个
		// 根据 tradeId 查询到用户 信息
        IDataset eopSubscribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(tradeId);
        if (CollectionUtils.isEmpty(eopSubscribeDataset)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID"+tradeId+"查询TF_B_EOP_SUBSCRIBE为空");
        }
        String custId = eopSubscribeDataset.first().getString("GROUP_ID");
		GrpInfoQry.queryGroupCustInfoByGroupId( custId );
		return null;
	}
}
