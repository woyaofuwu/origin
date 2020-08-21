
package com.asiainfo.veris.crm.order.soa.person.busi.plat.officedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SpInfoCSQry;

public class SpInfoCSBean extends CSBizBean
{
    public IDataset deleteSpInfoCS(IData params) throws Exception
    {
        params.put("PROVINCE_CODE", params.getString("PROV_CODE"));
        params.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        params.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        params.put("UPDATE_TIME", SysDateMgr.getSysTime());
//        params.put("RECORD_DATE", SysDateMgr.getSysTime());

        params.put("RECORD_STATUS", "03");// 删除，逻辑删除，致标志位
        //产商品接口需要将where条件中的值拼入CONDITION中，用":"分隔
        String condition = "PROVINCE_CODE="+params.getString("PROV_CODE")+";"+"BIZ_CODE="+params.getString("BIZ_CODE","")+";"+"SP_CODE="+params.getString("SP_CODE","");
        params.put("CONDITION", condition);

//        Dao.executeUpdateByCodeCode("TD_M_SPINFO_CS", "UPD_RECORD_STATUS_BY_PK", params, Route.CONN_CRM_CEN);
        UpcCall.updSpInfoCs(params);
        IDataset res = new DatasetList();
        res.add(params);
        return res;
    }

    public IDataset insertSpInfoCS(IData params) throws Exception
    {
        params.put("PROVINCE_CODE", params.getString("PROV_CODE"));
        params.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        params.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        params.put("UPDATE_TIME", SysDateMgr.getSysTime());
        params.put("RECORD_DATE", SysDateMgr.getSysTime());

        params.put("RECORD_STATUS", "01");// 新增
        params.put("REMARK", "");
        params.put("RSRV_STR1", "");
        params.put("RSRV_STR2", "");
        params.put("RSRV_STR3", "");
        params.put("RSRV_STR4", "");
        params.put("RSRV_STR5", "");
        params.put("RSRV_STR6", "");
        params.put("RSRV_STR7", "");
        params.put("RSRV_STR8", "");
        params.put("RSRV_STR9", "");
        params.put("RSRV_STR10", "");

//        Dao.executeUpdateByCodeCode("TD_M_SPINFO_CS", "INSERT_ALL", params, Route.CONN_CRM_CEN);
        IDataset paramsList = new DatasetList();
        paramsList.add(params);
        IData param = new DataMap();
        param.put("INDATA", paramsList);
        UpcCall.saveSpInfoCs(param);
        IDataset res = new DatasetList();
        res.add(params);
        return res;
    }

    public IDataset querySpInfoCS(IData input, Pagination page) throws Exception
    {
        String provinceCode = input.getString("PROV_CODE");
        String bizCode = input.getString("BIZ_CODE");
        String bizName = input.getString("BIZ_NAME");
        String spCode = input.getString("SP_CODE");
        String spName = input.getString("SP_NAME");
        String spType = input.getString("SP_TYPE");
        String spAttr = input.getString("SP_ATTR");
        String recordStatus = input.getString("RECORD_STATUS");
        IDataset res = SpInfoCSQry.querySpInfoCS(provinceCode, bizCode, bizName, spCode, spName, spType, spAttr, recordStatus, page);
        return res;
    }

    public IDataset querySpInfoCSByPkRecordStatus(IData params) throws Exception
    {
        String provinceCode = params.getString("PROV_CODE");
        String bizCode = params.getString("BIZ_CODE");
        String spCode = params.getString("SP_CODE");
        return SpInfoCSQry.querySpInfoCSByPkRecordStatus(provinceCode, bizCode, spCode);
    }

    public IDataset querySpInfoCSByRowID(IData params) throws Exception
    {
        String recordRowID = params.getString("RECORDROWID");
        return SpInfoCSQry.querySpInfoCSByRowID(recordRowID);
    }

    public IDataset updateSpInfoCS(IData params) throws Exception
    {
        params.put("PROVINCE_CODE", params.getString("PROV_CODE"));
        params.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        params.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        params.put("UPDATE_TIME", SysDateMgr.getSysTime());
        params.put("RECORD_DATE", SysDateMgr.getSysTime());

        params.put("RECORD_STATUS", "02");// 修改
        params.put("REMARK", "");
        params.put("RSRV_STR1", "");
        params.put("RSRV_STR2", "");
        params.put("RSRV_STR3", "");
        params.put("RSRV_STR4", "");
        params.put("RSRV_STR5", "");
        params.put("RSRV_STR6", "");
        params.put("RSRV_STR7", "");
        params.put("RSRV_STR8", "");
        params.put("RSRV_STR9", "");
        params.put("RSRV_STR10", "");

       // Dao.executeUpdateByCodeCode("TD_M_SPINFO_CS", "UPD_BY_PK", params, Route.CONN_CRM_CEN);
        //产商品接口需要将where条件中的值拼入CONDITION中，用":"分隔
        String condition = "PROVINCE_CODE="+params.getString("PROV_CODE")+";"+"BIZ_CODE="+params.getString("BIZ_CODE","")+";"+"SP_CODE="+params.getString("SP_CODE","");
        params.put("CONDITION", condition);
        UpcCall.updSpInfoCs(params);
        IDataset res = new DatasetList();
        res.add(params);
        return res;
    }

}
