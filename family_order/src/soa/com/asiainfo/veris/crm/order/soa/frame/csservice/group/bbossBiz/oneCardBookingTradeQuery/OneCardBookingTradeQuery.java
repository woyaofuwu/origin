
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardBookingTradeQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class OneCardBookingTradeQuery
{

    /*
     * @description bboss瞬时报文处理
     * @author zhangcheng
     * @date 2013-08-15
     */
    public static IDataset ecBizInfoReqSelect(IData map) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", IDataUtil.getMandaData(map, "GROUP_ID"));
        param.put("POSPECNUMBER", map.getString("POSPECNUMBER", ""));
        param.put("PRODUCTSPECNUMBER", IDataUtil.getMandaData(map, "PRODUCTSPECNUMBER"));

        // 海南没有这个逻辑，后续使用时再加
        // return D****ByCode("TF_F_QDGROUP", "SEL_BY_ONECARD_BOOKING", param, Route.CONN_CRM_CG);
        // IDataset idsRet = MerchInfoQry.bookingGrpBBossQuery(param);
        // return idsRet;
        return new DatasetList();
    }

}
