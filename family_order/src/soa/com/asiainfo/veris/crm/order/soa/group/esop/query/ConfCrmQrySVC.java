package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ConfCrmQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

    /**查询ibsysid
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryIbsysid(IData param) throws Exception
    {
	    String productId = param.getString("PRODUCT_ID");
	    String groupId = param.getString("GROUP_ID");
	    String bpmTempletId = param.getString("BPM_TEMPLET_ID");
	    return ConfCrmQry.qryIbsysid(productId,groupId,bpmTempletId);
    }
    
    /**查询专线实例号
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryLineNo(IData param) throws Exception
    {
	    return ConfCrmQry.qryLineNo(param);
    }
    
    public static IDataset qrySubscribe(IData param) throws Exception
    {
	    return ConfCrmQry.qrySubscribe(param);
    }
    
	public static IDataset qryStateByRelIbsysidPoolValue(IData param) throws Exception
    {
		String relIbsysid = param.getString("REL_IBSYSID");
		String poolValue = param.getString("POOL_VALUE");
        IDataset subPoolInfos = ConfCrmQry.qryStateByRelIbsysidPoolValue(relIbsysid,poolValue);
		return subPoolInfos;
    }
	
	public static IDataset qrySubscribePool(IData param) throws Exception
	{
		IDataset subPoolInfos = ConfCrmQry.qrySubscribePool(param);
		return subPoolInfos;
	}
}
