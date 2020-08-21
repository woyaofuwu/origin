
package com.asiainfo.veris.crm.order.soa.group.grplocalmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpOfCityCodeMgrSvc extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
    	GrpOfCityCodeMgrBean bean = new GrpOfCityCodeMgrBean();

        return bean.crtTrade(inParam);
    }
    
    /**
     * 根据cust_id查询标准集团下的产品
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryProductInfoByCustId(IData inParam) throws Exception
    {
    	GrpOfCityCodeBean bean = new GrpOfCityCodeBean();
        return bean.queryProductInfoByCustId(inParam);
    }
    
    
}
