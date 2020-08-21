
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class WidenetModifyCustInfoSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getWidenetInfo(IData input) throws Exception
    {
        IDataset wideInfos = WidenetInfoQry.getUserWidenetInfo(input.getString("USER_ID"));
        return wideInfos;
    }

}
