
package com.asiainfo.veris.crm.order.soa.person.busi.plat.platsyn;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatOrderRelationQry;

public class PlatOrderRelationBean extends CSBizBean
{   
    /**
     * 平台业务订购关系同步批量导入
     * 
     * @param fileName
     * @param dataset
     * @param operSource
     * @throws Exception
     */
    public static void importPlatOrderRelation(String fileName, IDataset dataset) throws Exception
    {
        //String[] inParam = { "v_resultcode", "v_resultinfo" };
        String importId = SeqMgr.getSpbureImportId();
        String importTime = SysDateMgr.getSysTime();
        IData data = new DataMap();

        data.put("IMPORT_ID", importId);
        data.put("DATA_TYPE", 0);
        data.put("IMPORT_TIME", importTime);
        data.put("IMPORT_STAFF_ID", getVisit().getStaffId());
        data.put("IMPORT_DEPART_ID", getVisit().getDepartId());
        data.put("DEAL_FLAG", "0");
        data.put("REMARK", "平台业务订购关系同步前台导入");

        Dao.insert("TI_B_PLATDATA_BAT", data, Route.CONN_CRM_CG);
        IDataset batdtls = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData element = (IData) dataset.get(i);
            IData batdtl = new DataMap();
            batdtl.put("IMPORT_ID", importId);
            batdtl.put("SERIAL_NUMBER", element.getString("SERIAL_NUMBER"));
            batdtl.put("SP_CODE", element.getString("SP_CODE"));
            batdtl.put("BIZ_CODE", element.getString("BIZ_CODE"));
            batdtl.put("BIZ_TYPE_CODE", element.getString("BIZ_TYPE_CODE"));
            batdtl.put("IMPORT_TIME", importTime);
            batdtl.put("DEAL_STATE", "0");
            batdtl.put("DEAL_RESULT", "");
            batdtl.put("DEAL_INFO", "");
            batdtl.put("DEAL_TIME", "");
            batdtl.put("TRADE_ID", "");
            batdtl.put("REMARK", "平台业务订购关系同步前台导入");
            batdtl.put("RSRT_STR1", element.getString("RSRT_STR1"));
            batdtl.put("RSRT_STR2", element.getString("RSRT_STR2"));
            batdtl.put("RSRT_STR3", element.getString("RSRT_STR3"));
            batdtl.put("RSRT_STR4", element.getString("RSRT_STR4"));
            batdtl.put("RSRT_STR5", element.getString("RSRT_STR5"));

            batdtls.add(batdtl);
        }

        Dao.insert("TI_B_PLATDATA_BATDEAL", batdtls, Route.CONN_CRM_CG);
        // 异步处理
        //Dao.callProc("PKG_CMS_BUREDATA.p_bat_buredata_deal", inParam, data, Route.CONN_CRM_CG);      
    }
    
    /**
     * 平台业务订购关系同步批量启动
     * 
     * @param data
     * @throws Exception
     */
    public static void startBatDeal(IData data) throws Exception
    {
    	//更新批次
        Dao.executeUpdateByCodeCode("TI_B_PLATDATA_BAT", "UPD_DEALFLAG_BY_IMPORTID", data, Route.CONN_CRM_CG);

        //更新批次记录
        Dao.executeUpdateByCodeCode("TI_B_PLATDATA_BATDEAL", "UPD_DEALRESULT_BY_IMPORTID", data, Route.CONN_CRM_CG);
    	
    	// 异步处理
        String[] inParam = { "V_CHANNELID", "V_RESULTCODE", "V_RESULTERRINFO" };
    	Dao.callProc("PKG_CS_SYN_PLAT.P_CS_SYN_PLATDATA_BAT", inParam, data, Route.CONN_CRM_CG);      
    }

    /**
     * 查询平台业务订购关系同步批量信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatOrderRelationBat(IData param, Pagination pagination) throws Exception
    {
        return PlatOrderRelationQry.queryPlatOrderRelationBat(param, pagination);
    }
    
    /**
     * 查询平台业务订购关系同步导入详情
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatOrderRelationBatDtl(IData param, Pagination pagination) throws Exception
    {
        return PlatOrderRelationQry.queryPlatOrderRelationBatDtl(param, pagination);
    }
    
    /**
     * 根据主键查询批次信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData qryPlatDataBatByPK(IData data) throws Exception
    {
        return PlatOrderRelationQry.qryPlatDataBatByPK(data);
    }
}
