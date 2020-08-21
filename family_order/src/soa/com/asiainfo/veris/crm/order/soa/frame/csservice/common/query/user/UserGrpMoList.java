
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMoList
{

    /**
     * todo getVisit().setRouteEparchyCode(getVisit().getRouteEparchyCode()); 怎么处理
     * 
     * @Function: getGrpMolist
     * @Description: 根据userId查询用户所有上行业务指令 UserDom::UserGrpMolist::TF_F_USER_GRP_MOLIST::SEL_BY_USERID
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:47:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpMolist(IData params) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", params.getString("USER_ID"));

        return Dao.qryByCode("TF_F_USER_GRP_MOLIST", "SEL_BY_USERID", idata);
    }

    /**
     * @Function: getuserMolistbyserverid
     * @Description: 从集团库 根据user_id,svcid查询用户订购服务的 molist用户上行指令 @param userId 用户编码
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:56:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getuserMolistbyserverid(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);

        IDataset molistset = Dao.qryByCode("TF_F_USER_GRP_MOLIST", "SEL_BY_USERID_SVCID", param, Route.CONN_CRM_CG);
        return molistset;
    }

}
