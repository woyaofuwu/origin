
package com.asiainfo.veris.crm.order.soa.group.imsmanage.closegrpmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CloseGrpMgrSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getRelaData(IData inparam) throws Exception
    {
        String user_id_a = inparam.getString("USER_ID_A");
        String user_id_b = inparam.getString("USER_ID_B");
        String relation_type = inparam.getString("RELATION_TYPE");

        return RelaUUInfoQry.qryUU(user_id_a, user_id_b, relation_type, getPagination());
    }

}
