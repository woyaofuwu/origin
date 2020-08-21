package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BbossDestroyInfoQrySVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @description 获取需要注销的用户数据
     * @author xunyl
     * @date 2015-01-21
     */
    public IDataset getUserInfoList(IData inpram)throws Exception{
        return BbossDestroyInfoQry.getUserInfoList();
    }
}
