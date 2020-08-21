
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserEncryptGeneInfoQry
{
    public static IDataset getEncryptGeneBySn(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_ENCRYPT_GENE", "SEL_BY_USERID", param);
    }

    public static int updataPswByUserid(String userId, String encryptGene) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ENCRYPT_GENE", encryptGene);
        Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "DEL_BY_USERID", param);

        return Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "INS_ENCRYPT", param);
    }

}
