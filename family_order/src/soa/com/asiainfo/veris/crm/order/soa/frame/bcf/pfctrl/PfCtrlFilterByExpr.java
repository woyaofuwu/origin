
package com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.DynamicDecisionTable;

public final class PfCtrlFilterByExpr
{

    /**
     * 根据MVEL表达式过滤TD_S_PF_DETAIL
     * 
     * @param dataset
     * @param tableDataset
     * @return
     * @throws Exception
     */
    public static IDataset filterPfCtrlInfos(IDataset dataset, IDataset tableDataset) throws Exception
    {
        DynamicDecisionTable dynamic = new DynamicDecisionTable("FIELD_EXPRE");
        IDataset result = dynamic.decide(dataset, tableDataset);
        return result;
    }

}
