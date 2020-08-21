package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;


import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class DealInfoBean extends CSBizBean {

	public IData dealInfo(IData in) throws Exception{
		IData res = new DataMap();
		res.put("tradeId", IDataUtil.chkParam(in, "tradeId" ));
		try {
			CSAppCall.call("SS.WorkformNodeDealSVC.dealInfo", this.transferInparams(in) );
			res.put(GroupStandardConstans.RES_BIZ_DESC, "");
			res.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_SUCCESS);
			return res ;
		}catch (Exception e) {
			res.put(GroupStandardConstans.RES_BIZ_DESC, e.getMessage());
			res.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED);
			return res;
		}
	}
	private IData transferInparams(IData in) throws Exception{
		String tradeId = IDataUtil.chkParam(in, "tradeId" ); //受理单编号（唯一标识） 也就是 BI_SN或者IBSYSID
		boolean isNumTradeId = StringUtils.isNumeric( tradeId );
		String mgmtDistrict = IDataUtil.chkParam(in, "mgmtDistrict" ); //地市编号
		boolean isMgmtDistrict = StringUtils.isNumeric( mgmtDistrict );
		if( !isNumTradeId ||  !isMgmtDistrict){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "入参[tradeId]或者[mgmtDistrict]必须是数字类型！");
		}
		String receiveStaffId = IDataUtil.chkParam(in, "receiveStaffId"); //接收人工号
		String dispatchStaffId = IDataUtil.chkParam(in, "dispatchStaffId"); //派发人工号
		String dealTime = IDataUtil.chkParam(in, "dealTime"); //处理时间 时间格式转换
		
		dealTime = SysDateMgr.getDateForSTANDYYYYMMDDHHMMSS(dealTime); //传入时间入表，则EOS-FUSE工程需要修改
		
		IData out = new DataMap();
		out.put("STAFF_ID", dispatchStaffId);
		out.put("EOS_ROLE_ID", receiveStaffId);
		out.put("BUSIFORM_NODE_ID", tradeId);
		out.put("EPARCHY_CODE", mgmtDistrict);
		out.put("DEPART_ID", getVisit().getDepartId());
		out.put("UPDATE_TIME", dealTime );
		
		return out;
	}
}
