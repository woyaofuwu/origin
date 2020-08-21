
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ProductTroopMemberQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 3, 2014 5:46:10 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Sep 3, 2014 maoke v1.0.0 修改原因
 */
public class ProductTroopMemberQry
{
    /**
     * @Description: 该函数的功能描述
     * @param custCode
     * @param troopId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 3, 2014 5:48:30 PM
     */
    public static IDataset qryTroopMemberByCodeId(String custCode, String troopId) throws Exception
    {
        IData param = new DataMap();

        param.put("CUST_CODE", custCode);
        param.put("TROOP_ID", troopId);

        return Dao.qryByCodeParser("TF_PRODUCT_TROOP_MEMBER", "SEL_GET_PRODUCT_USER", param);
    }
}
