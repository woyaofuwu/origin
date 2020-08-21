package com.asiainfo.veris.crm.order.soa.group.modifylinepayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ModifyLinePayRelationSVC extends GroupOrderService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final IDataset crtOrder(IData map) throws Exception {
        ModifyLinePayRelationBean modifyLinePayRelation = new ModifyLinePayRelationBean();

        map.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        map.put("RULE_BIZ_KIND_CODE", "GrpUserOpen");
        return modifyLinePayRelation.crtOrder(map);
    }

}
