
package com.asiainfo.veris.crm.order.soa.person.busi.villagework;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VillageWorkBean extends CSBizBean
{

    public void insertUserOtherServInfo(IDataset data) throws Exception
    {
        Dao.insert("TF_F_USER_OTHERSERV", data);
    }

}
