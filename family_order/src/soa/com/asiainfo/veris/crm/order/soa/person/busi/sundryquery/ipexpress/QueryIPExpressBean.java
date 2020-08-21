
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ipexpress;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryIPExpressQry;

public class QueryIPExpressBean extends CSBizBean
{
    public IDataset getUserinfo(String routeEparchyCode, String userIdB) throws Exception
    {
        IDataset infos = QueryIPExpressQry.getUserinfo(routeEparchyCode, userIdB);
        return infos;
    }

    /**
     * 功能：IP直通车查询 作者：GongGuang
     */
    public IDataset getUserInfoBySN(String serialNumber, String routeEparchyCode) throws Exception
    {
        IDataset infos = QueryIPExpressQry.getUserInfoBySN(serialNumber, routeEparchyCode);
        return infos;
    }

    public IDataset getUserMainSvc(String routeEparchyCode, String userId) throws Exception
    {
        IDataset infos = QueryIPExpressQry.getUserMainSvc(routeEparchyCode, userId);
        return infos;
    }

    public IDataset getUserMainSvcLast(String routeEparchyCode, String userId) throws Exception
    {
        IDataset infos = QueryIPExpressQry.getUserMainSvcLast(routeEparchyCode, userId);
        return infos;
    }

    public IDataset getUserMainSvcState(String routeEparchyCode, String serviceId) throws Exception
    {

        IDataset infos = QueryIPExpressQry.getUserMainSvcState(routeEparchyCode, serviceId);
        return infos;
    }

    public IDataset getUserStateCodeset(String routeEparchyCode, String stateCodeset) throws Exception
    {

        IDataset infos = USvcStateInfoQry.qryStateNameBySvcIdStateCode(routeEparchyCode, stateCodeset);
        return infos;
    }

    public IDataset getUUByIDB(String routeEparchyCode, String userId, String relationTypeCode) throws Exception
    {
        IDataset uurelainfosB = QueryIPExpressQry.getUUByIDB(routeEparchyCode, userId, relationTypeCode);
        return uurelainfosB;
    }

    public IDataset getUUUserinfo(String routeEparchyCode, String userIdA, String relationTypeCode) throws Exception
    {
        IDataset infos = QueryIPExpressQry.getUUUserinfo(routeEparchyCode, userIdA, relationTypeCode);
        return infos;
    }

}
