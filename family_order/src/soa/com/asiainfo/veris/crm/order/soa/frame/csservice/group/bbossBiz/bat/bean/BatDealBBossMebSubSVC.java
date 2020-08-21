package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BatDealBBossMebSubSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @description 处理BBOSS成员批量明细信息
     * @author xunyl
     * @date 2015-12-17
     */
    public static final IDataset dealBBossMebSub(IData inparam)throws Exception{
        String batchId = inparam.getString("BATCH_ID");
        String batchOperType = inparam.getString("BATCH_OPER_TYPE");
        BatDealBBossMebSubBean.dealBBossMebSub(batchId,batchOperType);
        return new DatasetList();
    }

}
