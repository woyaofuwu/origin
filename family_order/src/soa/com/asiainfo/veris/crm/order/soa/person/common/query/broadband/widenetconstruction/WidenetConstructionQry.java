
package com.asiainfo.veris.crm.order.soa.person.common.query.broadband.widenetconstruction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WidenetConstructionQry
{
	public static IDataset queryWidenetConstsInfo(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_CONSTRUCTION_ADDR", "SEL_WIDENET_CONSTS_BY_ALL", inparams, pagination);
    }

}
