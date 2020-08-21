
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.seqinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class SeqMgrInfoIntf
{

    /**
     * 查询Molist序列
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMoListSeqInfos(IBizCommon bc) throws Exception
    {
        IData inparam = new DataMap();

        return CSViewCall.call(bc, "CS.SeqMgrSVC.getGrpMolist", inparam);
    }
    
    /**
     * @description 根据用户地州编号获取批次号
     * @author xunyl
     * @date 2016-01-17
     */
    public static IDataset qryBatchInfos(IBizCommon bc,String userEparchCode)throws Exception
    {
        IData inparam = new DataMap();
        inparam.put(Route.USER_EPARCHY_CODE, userEparchCode);
        return CSViewCall.call(bc, "CS.SeqMgrSVC.getBatchId", inparam);

    }
}
