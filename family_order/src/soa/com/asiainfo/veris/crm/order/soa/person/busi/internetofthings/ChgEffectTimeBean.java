package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

public class ChgEffectTimeBean {
	public IDataset getDiscntByUser(String userId, String discntCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("DISCNT_CODE", discntCode);

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USER", cond);
    }


}
