
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 查询BBOSSTASK_GRP表
 * 
 * @author chenkh 2014-7-25
 */
public class BbossTaskGrpChargeAgainstSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public final static IDataset queryBbossTaskGrp(IData data) throws Exception
    {

        return BbossTaskChargeAgainstGrp.queryBbossTaskGrp();
    }
}
