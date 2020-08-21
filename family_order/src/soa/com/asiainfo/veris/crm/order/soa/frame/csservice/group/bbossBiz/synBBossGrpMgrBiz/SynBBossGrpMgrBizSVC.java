
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossGrpMgrBiz;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 业务流程管理接口
 * 
 * @author weixb3
 * @date 2013-8-19
 */
public class SynBBossGrpMgrBizSVC extends CSBizService
{
    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    public static final IDataset dealCommonData(IData data) throws Exception
    {
        SynBBossGrpMgrBiz biz = new SynBBossGrpMgrBiz();
        return new DatasetList(biz.dealCommonData(data));

    }

    /**
     * @description 管理节点回复
     * @author xunyl
     * @date 2014-08-28
     */
    public static final IDataset RspBBossManage(IData data) throws Exception
    {
        RspBBossGrpMgrBiz.RspBBossManage(data);
        return new DatasetList();
    }
}
