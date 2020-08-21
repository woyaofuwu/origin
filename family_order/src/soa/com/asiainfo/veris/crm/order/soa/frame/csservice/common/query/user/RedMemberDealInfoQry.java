
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RedMemberDealInfoQry
{
    /**
     * @Function: getBlackWhitedata
     * @Description: 根据user_id查询成员黑白名单信息 PROCESS_TAG = '1'类型为新增的用户
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:14:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getBlackUserdata(String user_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        return Dao.qryByCode("TL_B_BLACKUSER", "SEL_BY_NUMBER_IN", idata, Route.CONN_CRM_CEN);
    }

    public static int InsertBlackUser(IData data) throws Exception
    {
        int info = Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "INS_BLACK_USER", data, Route.CONN_CRM_CEN);

        return info;
    }

    public static int updateExitBlackUser(IData data) throws Exception
    {
        int info = Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "UPD_BLACK_EXIT", data, Route.CONN_CRM_CEN);

        return info;
    }
}
