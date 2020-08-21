
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SpInfoCSQry
{
    public static IDataset querySpInfoCS(String provinceCode, String bizCode, String bizName, String spCode, String spName, String spType, String spAttr, String recordStatus, Pagination page) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("PROVINCE_CODE", provinceCode);
        inparams.put("BIZ_CODE", bizCode);
        inparams.put("BIZ_NAME", bizName);
        inparams.put("SP_CODE", spCode);
        inparams.put("SP_NAME", spName);
        inparams.put("SP_TYPE", spType);
        inparams.put("SP_ATTR", spAttr);
        inparams.put("RECORD_STATUS", recordStatus);

//        return Dao.qryByCode("TD_M_SPINFO_CS", "SEL_BY_MORE_COND", inparams, page, Route.CONN_CRM_CEN);
        return UpcCall.qrySpInfoCs(inparams);
    }

    public static IDataset querySpInfoCSByPkRecordStatus(String provinceCode, String bizCode, String spCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PROVINCE_CODE", provinceCode);
        params.put("BIZ_CODE", bizCode);
        params.put("SP_CODE", spCode);
        IDataset result = new DatasetList();
        IDataset spInfos = UpcCall.qrySpInfoCs(params);
        if(IDataUtil.isNotEmpty(spInfos)){
        	for(Object obj : spInfos){
        		IData spInfo = (IData) obj;
        		if("01".equals(spInfo.getString("RECORD_STATUS","")) || "02".equals(spInfo.getString("RECORD_STATUS",""))){
        			result.add(spInfo);
        		}
        	}
        }
        return result;
//        return Dao.qryByCode("TD_M_SPINFO_CS", "SEL_BY_PK_RECORD_STATUS", params, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpInfoCSByRowID(String recordRowID) throws Exception
    {
        IData params = new DataMap();
        params.put("RECORDROWID", recordRowID);
        return UpcCall.qrySpInfoCs(params);
//        return Dao.qryByCode("TD_M_SPINFO_CS", "SEL_BY_ROWID", params, Route.CONN_CRM_CEN);
    }
}
