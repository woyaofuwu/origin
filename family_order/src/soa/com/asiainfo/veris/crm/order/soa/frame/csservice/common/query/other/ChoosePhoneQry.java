
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ChoosePhoneQry
{
    public static IDataset getChoosePhoneInfo(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER", "SEL_NETCHOOSE_PHONE", params, pagination);
    }

}
