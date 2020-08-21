
package com.asiainfo.veris.crm.order.soa.group.destroyonekey;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DestroyOneKeySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtBat(IData inParam) throws Exception
    {
        DestroyOneKeyBean bean = new DestroyOneKeyBean();

        return bean.crtBat(inParam);
    }
}
