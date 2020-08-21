
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossmange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BbossManageSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 管理节点总接口 chenyi 2014-3-4
     */

    public IDataset dealManageInfo(IData param) throws Exception
    {
        BbossManage.dealManageInfo(param);
        return null;
    }

}
