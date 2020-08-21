package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformSubscribeSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	
	public static IData qryWorkformSubscribeByIbsysid(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID","");
		
		IDataset workformDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		
		if(DataUtils.isEmpty(workformDataset)){
			return null;
		}
		return workformDataset.getData(0);
	}
	
	
	public static IData qryWorkformSubscribeByRsrvstr5(IData param) throws Exception{
		String rsrvStr5 = param.getString("RSRV_STR5");
		IDataset workformDataset = WorkformSubscribeBean.qryWorkformSubscribeByRsrvstr5(rsrvStr5);
		if(DataUtils.isEmpty(workformDataset)){
			return null;
		}
		return workformDataset.getData(0);
	}
	
	
	public static IDataset getProductLineInfo(IData param) throws Exception{
		
		IDataset subscribeInfos = WorkformSubscribeHBean.qrySubScribeInfoByProductNo(param);
		
		return subscribeInfos;
	}
	
	public IDataset qryWorkformSubscribeInfo(IData param) throws Exception{
		return WorkformSubscribeBean.qryWorkformSubscribeInfo(param,getPagination());
	}
	
	public static void updateWorkformSubscribeByIBSYSID(IData param) throws Exception{
		WorkformSubscribeBean.updateWorkformSubscribeByIBSYSID(param);
		WorkformAttrBean.updateByIbsysid(param);
		SubscribePoolBean.updByRelIbsysidAndState(param);
	}

    public IDataset queryDealSubscribe(IData param) throws Exception {
        return WorkformSubscribeBean.queryDealSubscribe(param, getPagination());
    }
    
    public IData qryWorkformHSubscribeByIbsysid(IData param) throws Exception {
    	String ibsysid = param.getString("IBSYSID");
        IDataset subInfos = WorkformSubscribeBean.qryWorkformHSubscribeByIbsysid(ibsysid);
        return subInfos.first();
    }

    public IDataset qryScribeHInfoByIbsysidForOpen(IData param) throws Exception {
        return WorkformSubscribeHBean.qryScribeHInfoByIbsysidForOpen(param);
    }

    public IDataset qryScribeInfoByIbsysidForOpen(IData param) throws Exception {
        return WorkformSubscribeBean.qryScribeInfoByIbsysidForOpen(param);
    }

    public IDataset qryAllScribeInfoByGroupIdForOpen(IData param) throws Exception {
        return WorkformSubscribeBean.qryAllScribeInfoByGroupIdForOpen(param);
    }
    
    
    public IDataset qrySubScribeInfoByProductNo(IData param) throws Exception {
        return WorkformSubscribeHBean.qrySubScribeInfoByProductNo(param);
    }
    
    public IDataset queryWorkform(IData param) throws Exception {
    	IDataset subInfos = WorkformSubscribeBean.queryWorkform(param);
        return subInfos;
    }
    
    public IDataset getSubScribeInfoByProductNo(IData param) throws Exception{
    	IDataset subInfos = WorkformSubscribeHBean.getSubScribeInfoByProductNo(param);
    	return subInfos;
    }

    public IDataset qrySubScribeInfoByIbsysidOrGroupId(IData param) throws Exception{
		return WorkformSubscribeBean.qrySubScribeInfoByIbsysidOrGroupId(param,getPagination());
	}
}