
package com.asiainfo.veris.crm.order.soa.group.grplocalmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class GrpOfCityCodeBean extends CSBizBean
{
    
    /**
     * 根据cust_id查询标准集团下的产品
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryProductInfoByCustId(IData data) throws Exception
    {
        IDataset infos = GrpOfCityCodeQry.queryProductInfoByCustId(data);
        return infos;
    }
    
}
