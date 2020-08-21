
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class DestroyCentrexSuperTeleGroupUserSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    public final IDataset crtOrder(IData map) throws Exception
    {
        // 调用OrderBaseBean,生成集团台账和成员台账
        DestroyCentrexSuperTeleGroupUserBean bean = new DestroyCentrexSuperTeleGroupUserBean();

        map.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        map.put("RULE_BIZ_KIND_CODE", "GrpUserDestory");

        return bean.crtOrder(map);
    }
}
