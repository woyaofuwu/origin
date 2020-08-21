
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DetailItemInfoQrySVC extends CSBizService
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 根据上级银行查询银行信息
     * 
     * @author fengsl
     * @date 2013-03-18 *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGroupUserPayRelation(IData input) throws Exception
    {
        String ITEM_ID = input.getString("ITEM_ID");
        IDataset output = DetailItemInfoQry.queryGroupUserPayRelation(ITEM_ID, this.getPagination());
        return output;
    }
}
