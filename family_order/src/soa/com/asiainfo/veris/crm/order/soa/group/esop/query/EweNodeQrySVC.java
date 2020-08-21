package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EweNodeQrySVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static IDataset qryEweNodeByIbsysid(IData param) throws Exception
	{
		String bi_sn = param.getString("BI_SN");
		
		return EweNodeQry.qryEweNodeByIbsysid(bi_sn);
	}

	public IDataset countWorkflowByAcceptStaffId(IData param) throws Exception
	{
	    return EweNodeQry.countWorkflowByAcceptStaffId(param.getString("STAFF_ID"));
	}
	
	public IDataset qryEweByibsysIdRecordnum(IData param) throws Exception
	{
	    return EweNodeQry.qryEweByibsysIdRecordnum(param);
	}
	
	public IDataset qryEweByBusiFormNodeId(IData param) throws Exception
	{
		String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
	    return EweNodeQry.qryEweByBusiFormNodeId(busiformNodeId);
	}
	
	public IDataset qryBySubBusiformId(IData param) throws Exception{
		String busiformNodeId = param.getString("BUSIFORM_ID");
		return EweNodeQry.qryBySubBusiformId(busiformNodeId);
	}	

    public IDataset qryByBusiformNodeId(IData param) throws Exception {
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        return EweNodeQry.qryByBusiformNodeId(busiformNodeId);
    }
    public IDataset qryEweNodeByBusiformIdState(IData param) throws Exception{
		String busiformId = param.getString("BUSIFORM_ID");
		String state  = param.getString("STATE");
		return EweNodeQry.qryEweNodeByBusiformIdState(busiformId,state);
	}

    public IDataset qryEweNodeByBusiformIdNodeId(IData param) throws Exception {
        return EweNodeQry.qryByBusiformIdAndNodeId(param);
    }
    
    public IDataset qryEweHByIbsysid(IData param) throws Exception {
    	String ibsysId = param.getString("IBSYSID");
    	String templateType = param.getString("TEMPLET_TYPE");
        return EweNodeQry.qryEweHByIbsysid(ibsysId,templateType);
    }
    public IDataset qryEweByIbsysid(IData param) throws Exception {
    	String ibsysId = param.getString("IBSYSID");
    	String templateType = param.getString("TEMPLET_TYPE");
        return EweNodeQry.qryEweByIbsysid(ibsysId,templateType);
    }
    
    public IDataset qryAuditEweByBpmAndIbsysId(IData param) throws Exception {
        return EweNodeQry.qryAuditEweByBpmAndIbsysId(param);
    }
    
    public void updEweNodeAutoTimeByPk(IData param) throws Exception {
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        String execTime = param.getString("AUTO_TIME");
        EweNodeQry.updEweNodeByPk(busiformNodeId, execTime);
    }
    
    public IDataset qryEweHNodeByBusiformIdState(IData param) throws Exception {
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        return EweNodeQry.qryEweHNodeByBusiformIdState(busiformNodeId);
    }
    public IDataset qryAuditInfoEweByBpmAndIbsysId(IData param) throws Exception {
        return EweNodeQry.qryAuditInfoEweByBpmAndIbsysId(param);
    }
    
    
}