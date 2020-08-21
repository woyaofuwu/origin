package com.asiainfo.veris.crm.order.soa.group.esop.query;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class WorkformOtherSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IDataset qryLastInfoByIbsysidAndAttrCode(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID"); 		
		String attrCode = param.getString("ATTR_CODE");  
		IDataset dataset = WorkformOtherBean.qryLastInfoByIbsysidAndAttrCode(ibsysid,attrCode);
		return dataset;
	}
	
    public IDataset qryByIbsysidNodeId(IData param) throws Exception {
	    String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        return WorkformOtherBean.qryByIbsysidNodeId(ibsysid, nodeId);
    }

    public IDataset qryHOtherByIbsysidNodeId(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        return WorkformOtherHBean.qryHOtherByIbsysidNodeId(ibsysid, nodeId);
    }

	public IDataset qryOtherBySubIbsysidRecordNum(IData param) throws Exception
	{
		String subIbsysid = param.getString("SUB_IBSYSID"); 		
		String recordNum = param.getString("RECORD_NUM");  
		IDataset dataset = WorkformOtherBean.qryOtherBySubIbsysidRecordNum(subIbsysid, recordNum);
		return dataset;
	}
	
	public IDataset qryFinishBySubIbsysidRecordNum(IData param) throws Exception
	{
		String subIbsysid = param.getString("SUB_IBSYSID"); 		
		String recordNum = param.getString("RECORD_NUM");  
		IDataset dataset = WorkformOtherHBean.qryOtherBySubIbsysidRecordNum(subIbsysid, recordNum);
		return dataset;
	}
	
	public IDataset qryOtherByIbsysidAttrCode(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID"); 		
		String attrCode = param.getString("ATTR_CODE");  
		IDataset dataset = WorkformOtherHBean.qryOtherByIbsysidAttrCode(ibsysid, attrCode);
		return dataset;
	}
	public IDataset insertHotherInfo(IData param) throws Exception
	{
		IDataset params = param.getDataset("OTHER_INFO");
		String subIbsysId = SeqMgr.getSubIbsysId();
		for(int i = 0;i<params.size();i++){
			IData paramda = params.getData(i);
			paramda.put("SUB_IBSYSID", subIbsysId);
			paramda.put("SEQ", i);
			paramda.put("GROUP_SEQ", 0);
			paramda.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			paramda.put("UPDATE_TIME", SysDateMgr.getSysTime());
			paramda.put("RECORD_NUM", paramda.getString("RECORD_NUM","0"));
		}
		WorkformOtherHBean.insertWorkformOtherH(params);
		IDataset paramBusis =  new DatasetList();
		return paramBusis;
	}
	public IDataset qryOtherByIbsysidAttrCodeRecordNum(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID"); 		
		String attrCode = param.getString("ATTR_CODE");  
		String recordNum = param.getString("RECORD_NUM");  
		IDataset dataset = WorkformOtherHBean.qryOtherByIbsysidAttrCodeRecordNum(ibsysid, attrCode,recordNum);
		return dataset;
	}
	
    public static IData insertOtherToList(IData param) throws Exception
    {
    	IDataset attrList=param.getDataset("otherList");
		for (int i = 0; i < attrList.size(); i++) {
            IData workformAttr = attrList.getData(i);
            workformAttr.put("SEQ", i);
		}
		WorkformOtherBean.insertWorkformOther(attrList);
        return null;

    }
	
}