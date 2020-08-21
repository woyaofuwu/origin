package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformEomsSVC extends CSBizService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IDataset qryNewWorkSheetByIbsysid(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM","");
        return WorkformEomsBean.qryNewWorkSheetByIbsysid(ibsysid,recordNum);
    }
    
    public static IDataset qryworkformEOMSBySerialNo(IData param) throws Exception
    {
    	String serialNo = param.getString("SERIALNO");
        return WorkformEomsBean.qryworkformEOMSBySerialNo(serialNo);
    }
    
    public static IDataset qryworkformEOMSByIbsysidRecordNum(IData param) throws Exception{
    	
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
    	return WorkformEomsBean.qryworkformEOMSByIbsysidRecordNum(ibsysid, recordNum);
    }
    
    public static IDataset qryworkformEOMSByIbsysidRecordNumSheettype(IData param) throws Exception{
    	
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
    	String sheettype = param.getString("SHEETTYPE");
    	return WorkformEomsBean.qryworkformEOMSByIbsysidRecordNumSheettype(ibsysid, recordNum,sheettype);
    }
    
    public static IDataset qryFinishEOMSByIbsysidRecordNumSheettype(IData param) throws Exception{
    	
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
    	String sheettype = param.getString("SHEETTYPE");
    	return WorkformEomsBean.qryFinishEOMSByIbsysidRecordNumSheettype(ibsysid, recordNum,sheettype);
    }
    
	public static IDataset getEomsDatasetByIbsysidRecordNum(IData param) throws Exception
	{
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
    	return WorkformEomsBean.getEomsDatasetByIbsysidRecordNum(ibsysid, recordNum);
    }
	
	public static IDataset qryEomsByIbsysIdOperType(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
    	String recordNum = param.getString("RECORD_NUM", ""); 
    	String operType = param.getString("OPER_TYPE", "");
    	
    	return WorkformEomsBean.qryEomsByIbsysIdOperType(ibsysid, recordNum, operType);
	}
	
	public static IDataset qryEomsByIbsysid(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
    	
    	return WorkformEomsBean.qryEomsByIbsysid(ibsysid);
	}
    
	public static IDataset qryHisEomsByIbsysIdOperTypeGroupSeq(IData param) throws Exception
	{
    	return WorkformEomsBean.qryHisEomsByIbsysIdOperTypeGroupSeq(param);
	}
	
	public static IDataset qryBulletinReply(IData param) throws Exception
	{
		String replyState = param.getString("REPLY_STATE", "");
		String sysTag = param.getString("SYS_TAG", "");
		String beginDate = param.getString("BEGIN_DATE", "");
		String endDate = param.getString("END_DATE", "");
		
		if("YES".equals(replyState))
		{
			return WorkformEomsBean.qryBulletinReplyYes(sysTag, beginDate, endDate);
		}
		else
		{
			return WorkformEomsBean.qryBulletinReplyNo(sysTag, beginDate, endDate);
		}
	}
	
	public static IDataset qryEomsByIbsysIdOperTypeGroupSeq(IData param) throws Exception
	{
    	return WorkformEomsBean.qryEomsByIbsysIdOperTypeGroupSeq(param);
	}
	public static IDataset getHEomsByIbsysidRecordNum(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
		String recordNum = param.getString("RECORD_NUM", "");
        IDataset workformEmos = WorkformEomsHBean.getHEomsDatasetByIbsysidRecordNum(ibsysid, recordNum);
        return workformEmos;
	}
}
