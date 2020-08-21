
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RelaMMInfoQry
{

    /*
     * 查询所有SERIAL_NUMBER_B
     */
    public static IDataset getAllSnB() throws Exception
    {
        IData inparams = new DataMap();

        return Dao.qryByCode("TF_F_RELATION_MM", "SEL_ALL_SERIAL_NUMBER_B", inparams);
    }

    public static IDataset getInfoBySnA(String SERIAL_NUMBER_A) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER_A", SERIAL_NUMBER_A);

        return Dao.qryByCode("TF_F_RELATION_MM", "SEL_BY_SERIAL_NUMBER_A", inparams);
    }

    public static IDataset getInfoBySnB(String SERIAL_NUMBER_B) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER_B", SERIAL_NUMBER_B);

        return Dao.qryByCode("TF_F_RELATION_MM", "SEL_BY_SERIAL_NUMBER_B", inparams);
    }
}
