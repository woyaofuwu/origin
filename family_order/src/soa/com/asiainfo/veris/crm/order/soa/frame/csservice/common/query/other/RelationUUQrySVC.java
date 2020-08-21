
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RelationUUQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public static IDataset getUserRelationByBBOSSUserIdA(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", input.getString("USER_ID_A"));
        IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AUU", param);
        return userRelationInfos;
    }

}
