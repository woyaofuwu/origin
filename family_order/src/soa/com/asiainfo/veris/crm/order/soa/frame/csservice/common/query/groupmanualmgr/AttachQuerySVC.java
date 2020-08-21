
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.groupmanualmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AttachQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset queryFileInfobyName(IData data) throws Exception
    {
        String condGroupID = data.getString("GROUP_ID");
        String condFileName = data.getString("FILE_NAME");
        String condPoSpecNumber = data.getString("POSPECNUMBER");
        String condProductSpecNumber = data.getString("PRODUCTSPECNUMBER");
        String condStartDate = data.getString("START_DATE");
        String condEndDate = data.getString("END_DATE");

        return AttachQueryBean.queryFileInfobyName(condGroupID, condFileName, condPoSpecNumber, condProductSpecNumber, condStartDate, condEndDate, getPagination());
    }

}
