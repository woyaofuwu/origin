
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.countryreceipt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CountryReceiptQry extends CSBizBean
{

    /**
     * 获取已打印记录
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getPrintRecords(IData inparam) throws Exception
    {
        /*
         * 打印记录如何存储还未定,先造数据
         */
        IDataset records = new DatasetList();
        IData record = new DataMap();
        record.put("FEE_MODE", "0");
        record.put("TRADE_ID", "1");
        records.add(record);

        return records;
    }

    /**
     * 获取可打印的流水记录
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getPrintTrade(IData inparams) throws Exception
    {
        return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_PRINT_TRADE", inparams, new Pagination());
    }
}
