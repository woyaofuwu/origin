
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.twodimension;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TwoDimensionCodeQry extends CSBizBean
{

    /**
     * 根据前台查询参数查询二维码基本信息 TD_B_BARCODE
     * 
     * @param reqId
     * @return
     * @throws Exception
     */
    public static IDataset queryBaseTwoDimensionCode(IData inparams, Pagination pagination) throws Exception
    {

        IDataset result = Dao.qryByCode("TD_B_BARCODE", "SEL_BY_WEB_QUERY", inparams, pagination);

        return result;
    }

    /**
     * 根据二维码id查询配置信息 TD_B_BARCODE_CONFIG
     * 
     * @param reqId
     * @return
     * @throws Exception
     */
    public static IDataset queryDetailTwoDimensionCode(IData inparams) throws Exception
    {

        IDataset result = Dao.qryByCode("TD_B_BARCODE", "SEL_BARCODE_DTAIL", inparams);

        return result;
    }

}
