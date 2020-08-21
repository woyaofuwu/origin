package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformEomsDetailSVC extends CSBizService
{

    public static IDataset qryEomsDetailByIbsysidAndRecordNum(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
        return WorkformEomsDetailBean.qryEomsDetailByIbsysidAndRecordNum(ibsysid,recordNum);
    }
    
    public static IDataset qryEomsDetailByIbsysidAndProductNo(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String productNo = param.getString("PRODUCT_NO");
        return WorkformEomsDetailBean.qryEomsDetailByIbsysidAndProductNo(ibsysid,productNo);
    }
    
    public static void updEomsDetailByPk(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String productNo = param.getString("PRODUCT_NO");
    	String recordNum = param.getString("RECORD_NUM");
    	String tradeId = param.getString("TRADE_ID");
    	String busiState = param.getString("BUSI_STATE");
        WorkformEomsDetailBean.updEomsDetailByPk(ibsysid, recordNum ,tradeId, productNo, busiState);
    }
    
    public static IDataset qryEomsDetailByIbsysid(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
        return WorkformEomsDetailBean.qryEomsDetailByIbsysid(ibsysid);
    }
    
    public static void updByIbsysidRecordNum(IData param) throws Exception
    {
    	String ibsysid = param.getString("IBSYSID");
    	String recordNum = param.getString("RECORD_NUM");
    	String busiState = param.getString("BUSI_STATE");
    	WorkformEomsDetailBean.updEomsDetailByIbsysidRecordNum(ibsysid, recordNum, busiState);
    }
    
    public static IDataset qryEmosDetailInfoByIdNo(IData param) throws Exception
    {
    	return WorkformEomsDetailBean.qryEmosDetailInfoByIdNo(param);
    }
    
    
    /**
     * 根据IBSYSID,ProductNo更新所有DETAIL表状态
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset updateDetailInfo(IData inparams) throws Exception
    {
    	IDataset results = new DatasetList();
    	IData result = new DataMap();
    	try
    	{
    		WorkformEomsDetailBean.updateDetailInfo(inparams);
    		result.put("X_RESULTCODE", "0");
    		result.put("X_RESULTINFO", "更新正常");
    		result.put("X_RSPCODE", "0");
    		
    	}catch (Exception e)
    	{
    		result.put("X_RESULTCODE", "-1");
    		result.put("X_RESULTINFO", e.getMessage());
    		result.put("X_RSPCODE", "-1");
    	}
    	results.add(result);
    	return results;
    }
    
    	
}
