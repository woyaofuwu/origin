package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;
//emos撤单，勘查归档撤单、重派撤单、oss撤单
public class WorkformEomsCancelSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	public void cancel(IData inparam) throws Exception
	{		
		String ibsysid = inparam.getString("IBSYSID");
		String busiformId = inparam.getString("BUSIFORM_ID");
		
		//根据ibsysid查询tf_b_eop_eoms_state表
		IDataset eomsStateDataset = WorkformEomsStateBean.qryEomsStateByIbsysid(ibsysid);
		if (IDataUtil.isEmpty(eomsStateDataset)) {
			return;
		}
		
		boolean isAllCancel = true;//是否全部专线都为撤单
        for(int i=0,size=eomsStateDataset.size();i<size;i++){
        	String busiState = eomsStateDataset.getData(i).getString("BUSI_STATE","");
        	
        	if(!busiState.equals("C")){
        		isAllCancel = false;
        	}
        }
        
      //判断是否全部专线都为撤单，全部撤单则结束流程
        if(isAllCancel){
        	IData tempSub = new DataMap();
    		tempSub.put("STATE", "4");
    		tempSub.put("BUSIFORM_ID", busiformId);
    		CSAppCall.call("SS.WorkformMoveHisSVC.history", tempSub);
        }
		
	}
	private void saveWorkformEoms(IData workformEoms) throws Exception
	{
		workformEoms.put("MONTH", SysDateMgr.getCurMonth());
		workformEoms.put("GROUP_SEQ", workformEoms.getInt("GROUP_SEQ", 0)+1);//+1
		workformEoms.put("TRADE_DRIECT", "0");
		workformEoms.put("OPER_TYPE", "cancelWorkSheet");
		workformEoms.put("DEAL_STATE",  "2");
		workformEoms.put("INSERT_TIME", SysDateMgr.getSysTime());
		workformEoms.put("UPDATE_TIME", SysDateMgr.getSysTime());
		workformEoms.put("SHEETTYPE",  workformEoms.getString("SHEETTYPE"));
		workformEoms.put("SERVICETYPE",  workformEoms.getString("SERVICETYPE"));
		workformEoms.put("OPPERSON",  CSBizBean.getVisit().getStaffName());
		workformEoms.put("OPCORP",  CSBizBean.getVisit().getCityName());
		workformEoms.put("OPDEPART",  CSBizBean.getVisit().getDepartId());
		workformEoms.put("OPCONTACT",  CSBizBean.getVisit().getSerialNumber());
		workformEoms.put("OPTIME",  SysDateMgr.getSysTime());
		workformEoms.put("OPDETAIL",  new DatasetList().toString());
		workformEoms.put("ATTACHREF",  workformEoms.getString("ATTACHREF"));

		WorkformEomsBean.insertWorkformEoms(workformEoms);
	}

	private void saveWorkformAttr(IDataset workformAttrs,String ibsysid,String subIbsysid,String groupSeq,String nodeId,String recordNum) throws Exception
	{
		if(DataUtils.isEmpty(workformAttrs))
		{
			return;
		}
		IDataset attrs = new DatasetList();
		for(int i = 0 ; i < workformAttrs.size() ; i ++)
		{
			IData workformAttr = workformAttrs.getData(i);
			IData attr = new DataMap();
			attr.put("RECORD_NUM", recordNum);
			attr.put("ATTR_CODE", workformAttr.getString("ATTR_CODE"));
			attr.put("ATTR_NAME", workformAttr.getString("ATTR_NAME"));
			attr.put("ATTR_VALUE", workformAttr.getString("ATTR_VALUE"));
			attr.put("SUB_IBSYSID", subIbsysid);
			attr.put("IBSYSID", ibsysid);
			attr.put("SEQ", SeqMgr.getAttrSeq());
			attr.put("GROUP_SEQ", groupSeq);
            attr.put("NODE_ID", nodeId);
            attr.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			attr.put("UPDATE_TIME", SysDateMgr.getSysTime());
			attrs.add(attr);
		}
		
		WorkformAttrBean.insertWorkformAttr(attrs);
	}

}
