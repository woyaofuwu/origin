
package com.asiainfo.veris.crm.order.soa.person.busi.plat.upay;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class UpayReconQuerySVC extends CSBizService
{
	private static final long serialVersionUID = 4829236996987004886L;
	
	/**
     * 查询和包电子券对账总体结果
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUpayReconTotal(IData param) throws Exception
    {
        return UpayReconQueryBean.queryUpayReconTotal(param, this.getPagination());
    }
    
    /**
     * 查询和包电子券对账结果
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUpayRecon(IData param) throws Exception
    {
        return UpayReconQueryBean.queryUpayRecon(param, this.getPagination());
    }
    
    /**
     * 更新返销结果
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int updateUpayRecon(IData param) throws Exception
    {
        return UpayReconQueryBean.updateUpayRecon(param);
    }
}
