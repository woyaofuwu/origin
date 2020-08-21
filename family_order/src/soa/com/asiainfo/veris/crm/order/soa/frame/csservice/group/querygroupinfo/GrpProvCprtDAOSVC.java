
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.GrpProvCprtDAO;

public class GrpProvCprtDAOSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据SYNC_SEQUENCE 更改 IF_ANS 是否反馈,和 保存RSRV_STR1 字段为 下一个节点的序列号
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset updatePoTradeState(IData idata) throws Exception
    {

        GrpProvCprtDAO.updatePoTradeState(idata.getString("IF_ANS"), idata.getString("NEXT_SYNC_SEQUENCE"), idata.getString("SYNC_SEQUENCE"));
        return null;
    }
}
