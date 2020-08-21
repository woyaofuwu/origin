
package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class GrpCautionFeeMgrBean extends CSBizBean
{
	
    /**
     * 集团客户保证金管理分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryCautionFeeList(IData param, Pagination pagination) 
    	throws Exception
    {
        IDataset infos = GrpCautionFeeMgrQry.queryCautionFeeList(param, pagination);
        return infos;
    }

    /**
     * 根据userId查询集团客户保证金
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryCautionFeeByUserId(IData param) 
    	throws Exception
    {
        IDataset infos = GrpCautionFeeMgrQry.queryCautionFeeByUserId(param);
        return infos;
    }
    
    
    /**
     * 根据userId查询集团客户保证金
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryOneCautionFeeByUserId(IData param) 
    	throws Exception
    {
        IDataset infos = GrpCautionFeeMgrQry.queryOneCautionFeeByUserId(param);
        return infos;
    }

}
