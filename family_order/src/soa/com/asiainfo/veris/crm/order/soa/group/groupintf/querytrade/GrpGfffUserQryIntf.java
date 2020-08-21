
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;

public class GrpGfffUserQryIntf
{

    /**
     * 自由充成员查询列表
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpGfffMebOrderInfo(IData inParam, Pagination pg) throws Exception
    {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpGfffMebOrderInfo(inParam, pg);
    }

    /**
     * 定额统付集团查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpGfffMebDiscntCountBySn(IData inParam) throws Exception
    {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpGfffMebDiscntCountByUserId(inParam);
    }
    
    /**
     * 根据GroupId查询集团管理员手机号
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMgrSerialNumberByGroupId(IData inParam) throws Exception
    {
        UserOrderQryBean bean = new UserOrderQryBean();

        return bean.qryGrpMgrSerialNumberByGroupId(inParam);
    }
    
    
}
