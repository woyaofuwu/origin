package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.filter;

import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;

public class IntegralPlanAddInFilter implements IFilterIn {

	@Override
	public void transferDataInput(IData input) throws Exception {
		// TODO Auto-generated method stub
		
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "INTEGRAL_PLAN_ID");
		IDataUtil.chkParam(input, "START_DATE");
		IDataUtil.chkParam(input, "END_DATE");
		
		IData userinfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
		if(IDataUtil.isEmpty(userinfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验用户信息错误！");
		}
		
		//校验用户积分账户信息
		IDataset acctinfo = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(userinfo.getString("USER_ID"), "10A", BizService.getVisit().getLoginEparchyCode());
		if (IDataUtil.isEmpty(acctinfo)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户积分账户资料无数据！");
        }
	}

}
