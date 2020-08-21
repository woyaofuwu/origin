
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeCentrexSuperTeleUserElementSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    public final IDataset crtOrder(IData map) throws Exception
    {
        // 调用bean封装成和商品数据结构一致的数据包
        ChangeCentrexSuperTeleGroupUserBean bean = new ChangeCentrexSuperTeleGroupUserBean();

        // 规则校验参数
        map.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        map.put("RULE_BIZ_KIND_CODE", "GrpUserChg");

        return bean.crtOrder(map);

    }

}
