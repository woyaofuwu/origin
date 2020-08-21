
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.hint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class HintInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * ngboss外框展示的所有提示信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAllHintInfos(IData input) throws Exception
    {
        return HintInfoQry.getAllHintInfos(input);
    }

    /**
     * 提示信息查询（小栏框）
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getHintInfo(IData input) throws Exception
    {
        return HintInfoQry.getHintInfo(input);
    }
}
