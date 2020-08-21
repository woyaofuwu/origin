package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;



public class GroupUserForOweInfoQrySVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUserOweInfo(IData inparam) throws Exception
    {
    	GroupUserForOweInfoQryBean bean = new GroupUserForOweInfoQryBean();
        return bean.qryGrpUserOweInfo(inparam, this.getPagination());
    }

  
    /**
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData getGrpUserOweInfoByUserId(IData inparam) throws Exception
    {
    	String userId = inparam.getString("USER_ID","");
    	IData infoSet = new DataMap();
    	if(userId != null && userId != "")
    	{
    		infoSet = AcctCall.getGrpUserOweInfoByUserId(userId);
    	}
    	return infoSet;
    }
    
}