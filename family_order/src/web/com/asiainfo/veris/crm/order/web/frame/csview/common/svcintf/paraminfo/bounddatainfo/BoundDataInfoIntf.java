
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bounddatainfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class BoundDataInfoIntf
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
        IData inparam = new DataMap();
        inparam.put("PARAMCODE", paramCode);
        return CSViewCall.call(bc, "CS.BoundDataQrySVC.qryBoundDataByParamcode", inparam);
    }
}
