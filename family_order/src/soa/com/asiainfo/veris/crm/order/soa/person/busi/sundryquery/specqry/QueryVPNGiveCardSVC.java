
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.specqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class QueryVPNGiveCardSVC extends CSBizService
{
    /**
     * 初始化信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryInitInfo(IData input) throws Exception
    {

        return UAreaInfoQry.qryAreaByParentAreaCode(input.getString("PARENT_AREA_CODE"));
    }

    /**
     * 执行绿色田野卡基站信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryVPNGiveCard(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        input.put("PARA_CODE2", input.getString("PARA_CODE2", "").replace("-", ""));

        result = SundryQry.getVPNGiveCardInfo(input, this.getPagination());

        return result;
    }

}
