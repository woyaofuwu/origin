
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/***
 * 
 */
public class ResNumInfoQry
{

    public static IDataset queryResNum(String paraAttr, String paraCode1, String paraCode2, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_ATTR", paraAttr);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PARA_CODE1", paraCode1);
        data.put("PARA_CODE2", paraCode2);

        return Dao.qryByCode("TF_M_RES_NUM", "SEL_BY_PRIMARY", data);
    }

    public static IDataset queryResNumInfo(String paraAttr, String paraCode1, String paraCode2, String validTag) throws Exception
    {
        IData data = new DataMap();
        IDataset results = new DatasetList();
        data.put("PARA_ATTR", paraAttr);
        data.put("VALID_TAG", validTag);
        data.put("PARA_CODE1", paraCode1);
        data.put("PARA_CODE2", paraCode2);

        IData result = Dao.qryByPK("TF_M_RES_NUM", data, new String[]
        { "PARA_ATTR", "PARA_CODE1", "PARA_CODE2", "VALID_TAG" });

        results.add(result);

        return results;
    }
}
