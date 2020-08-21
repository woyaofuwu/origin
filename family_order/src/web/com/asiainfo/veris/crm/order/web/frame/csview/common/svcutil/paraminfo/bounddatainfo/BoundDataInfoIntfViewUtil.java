
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bounddatainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bounddatainfo.BoundDataInfoIntf;

public class BoundDataInfoIntfViewUtil
{

    /**
     * 通过paramCode 查询TD_S_BOUND_DATA表 BBOSS下拉数据源配置表
     * 
     * @param bc
     * @param paramCode
     * @return
     * @throws Exception
     */
    public static IDataset qryBoundDataInfosByParamCode(IBizCommon bc, String paramCode) throws Exception
    {
        IDataset infosDataset = BoundDataInfoIntf.qryBoundDataInfosByParamCode(bc, paramCode);
        return infosDataset;
    }

}
