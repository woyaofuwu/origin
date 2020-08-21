package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BusiFlowReleSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	
	public IDataset qryBusiTypeByProductId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String operType = input.getString("OPER_TYPE");
        String areaId = input.getString("AREA_ID");
        String inModeCode = input.getString("IN_MODE_CODE");
        return BusiFlowReleInfoQuery.qryBusiTypeByProductId(operType, productId, areaId, inModeCode);
    }
	
	public IDataset qryBusiNameByTempletId(IData input) throws Exception
    {
        return BusiFlowReleInfoQuery.qryBusiNameByTempletId(input);
    }
	
	public IDataset getOperTypeByTempletId(IData input) throws Exception
    {
        return BusiFlowReleInfoQuery.getOperTypeByTempletId(input);
    }
	
    public IDataset getOperTypeByBusiCode(IData param) throws Exception {
        return BusiFlowReleInfoQuery.getOperTypeByBusiCode(param);
    }
    
    public static IDataset getBusiCodeByBusitype(IData idata) throws Exception {
		String busiType = idata.getString("BUSI_TYPE");
		String validTag = idata.getString("VALID_TAG");
		return BusiFlowReleInfoQuery.getBusiCodeByBusitype(busiType,validTag);
	}
    
    public static IDataset qryInfoByBpmtempletid(IData idata) throws Exception {
		String bpmTempletID = idata.getString("BPM_TEMPLET_ID");
		String busiCode = idata.getString("BUSI_CODE");
		return BusiFlowReleInfoQuery.qryInfoByBpmtempletid(bpmTempletID,busiCode);
	}

}