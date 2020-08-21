
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.greencell;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class QueryGreenCellInfoSVC extends CSBizService
{
    /**
     * 执行绿色田野卡基站信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGreenCellInfo(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        result = SundryQry.queryGreenCellInfo(input, this.getPagination());
        return result;
    }

    /**
     * 绿色田野卡基础信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryInitInfo(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        result.add(SundryQry.queryGreenCards(null, null));
        result.add(SundryQry.initCityArea(false));
        return result;
    }

}
