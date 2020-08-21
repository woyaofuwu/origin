
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserBlackInfoQry
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

    /**
     * @Function: getBlackUserInfo
     * @Description: 根据user_id查询成员黑白名单信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-25 下午9:14:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-25 updata v1.0.0 修改原因
     */
    public static IDataset getBlackUserInfo(String user_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        return Dao.qryByCode("TL_B_BLACKUSER", "SEL_ALL_BY_NUMBER", idata, Route.CONN_CRM_CEN);
    }

    /**
     * 查询白名单用户存在
     * 
     * @param data
     * @throws Exception
     * @author zhuyu
     * @date 2014-6-17
     */
    public static IDataset getRedmemberUserdata(String serialNumber) throws Exception
    {

        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("HN_SMS_REDMEMBER", "SEL_BY_SN1", idata);
    }

}
