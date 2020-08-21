package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformProductSubSVC extends CSBizService {

	private static final long serialVersionUID = 1L;
	
	public IData qryProductByPk(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String recordNum = param.getString("RECORD_NUM");
		return WorkformProductSubBean.qryProductByPk(ibsysid, recordNum);
	}
	
	public IData qryProductHByIbsysidRecordNum(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String recordNum = param.getString("RECORD_NUM");
		return WorkformProductSubHBean.qryProductByIbsysidRecordNum(ibsysid, recordNum);
	}

	public IDataset qryProductByIbsysid(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		return WorkformProductSubBean.qryProductByIbsysid(ibsysid);
	}

    public IDataset qryProductHByIbsysid(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        return WorkformProductSubBean.qryProductHByIbsysid(ibsysid);
    }
    
    public IDataset queryHisEopProInfoByuserId(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String userId = param.getString("USER_ID");
		return WorkformProductSubBean.queryHisEopProInfoByuserId(ibsysid,userId);
	}
}
