
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class GrpMebInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询groupmember中定位服务号码归属的集团
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGroupInfoByMember(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());
        IDataset data = GrpMebInfoQry.getGroupInfoByMember(user_id, eparchyCode, null);

        return data;
    }

    public IDataset getGrpMenberInfos(IData input) throws Exception
    {
        IDataset data = GrpMebInfoQry.getGrpMenberInfos(input);
        return data;
    }

    /**
     * 集团成员预约业务查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpPreInfoByGrpId(IData input) throws Exception
    {
        String group_id = input.getString("GROUP_ID");
        IDataset data = GrpMebInfoQry.getGrpPreInfoByGrpId(group_id, getPagination());

        return data;
    }

    public IDataset qryGrpMebProductInfo(IData input) throws Exception
    {
        IDataset data = GrpMebInfoQry.qryGrpMebProductInfo(input, getPagination());
        return data;
    }

    /**
     * 根据服务号码查询成员归属集团信息 查BB表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryRelaBBInfoTheDb(IData input) throws Exception
    {
        IDataset data = RelaBBInfoQry.qryRelaBBInfoTheDb(input, getPagination());
        return data;
    }

    /**
     * 根据服务号码查询成员归属集团信息
     * 
     * @author fengsl
     * @date 2013-03-22
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryRelaUUInfoTheDb(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.qryRelaUUInfoTheDb(input, getPagination());
        return data;
    }

    /**
     * 根据服务号码查询成员归属集团信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGrpMebBySN(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        IDataset data = GrpMebInfoQry.queryGrpMebBySN(serial_number);
        return data;
    }

}
