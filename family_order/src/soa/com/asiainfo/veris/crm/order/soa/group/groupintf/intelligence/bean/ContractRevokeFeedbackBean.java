package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.workform.WorkFormBean;

public class ContractRevokeFeedbackBean extends CSBizBean {
	protected static final Logger logger = Logger.getLogger(ContractRevokeFeedbackBean.class);

	public IData contractRevokeFeedback( IData input ) throws Exception{
		IData returnDatas = new DataMap();
		try{
			String workOrderID = IDataUtil.chkParam(input, "workOrderID"); //BUSIFORM_ID
			String workOrderNode = IDataUtil.chkParam(input, "workOrderNode"); //NODE_ID
			String feedback = IDataUtil.chkParam(input, "feedback","M");
			IDataUtil.chkParam(input, "feedbackTime");
			String submitTime = IDataUtil.chkParam(input, "submitTime");
			IDataUtil.chkParam(input, "staffName");
			String staffNO = IDataUtil.chkParam(input, "staffNO");
			IData param = new DataMap();
			param.put("BUSIFORM_ID", workOrderID);
			param.put("NODE_ID", workOrderNode);
			IDataset eweNode = Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID_NODE_ID", param, Route.getJourDb(Route.CONN_CRM_CG));
			if(  CollectionUtils.isEmpty( eweNode ) ) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据工单编号:" + workOrderID + ""
						+ "和处理环节"+workOrderNode+",未找到流程信息！");
			}
			param = new DataMap();
			param.put("BUSIFORM_ID", workOrderID);
			param.put("BUSIFORM_NODE_ID", eweNode.first().getString("BUSIFORM_NODE_ID"));
			param.put("UPDATE_STAFF_ID", staffNO);
			param.put("STATE", feedback);
			param.put("UPDATE_TIME", SysDateMgr.getDateForSTANDYYYYMMDDHHMMSS( submitTime ) );
			WorkFormBean.updateEweStepByState( param );
		}catch (Exception e) {
			returnDatas.put( GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_FAILED);
			returnDatas.put(GroupStandardConstans.RES_BIZ_DESC, e.getMessage());
			return returnDatas;
		}
		returnDatas.put( GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_SUCCESS);
		returnDatas.put(GroupStandardConstans.RES_BIZ_DESC, "");
		return returnDatas;
	}
}
