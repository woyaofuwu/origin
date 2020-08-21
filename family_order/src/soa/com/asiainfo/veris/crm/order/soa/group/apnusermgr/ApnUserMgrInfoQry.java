
package com.asiainfo.veris.crm.order.soa.group.apnusermgr;

import com.ailk.common.data.IData;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ApnUserMgrInfoQry
{
	/**
	 * 更新批量导入明细表
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
    public static boolean updateImportDataApnUserInfo(IData inParam) throws Exception
    {
        String importId = inParam.getString("IMPORT_ID");
        String rsrv_str1 = inParam.getString("RSRV_STR1");
        return Dao.update("TF_F_CMS_IMPORT_DATA", inParam, new String[]
        { "IMPORT_ID", "RSRV_STR1"}, new String[]
        { importId, rsrv_str1}, Route.CONN_CRM_CG);
    }
    
    /**
     * 更新APN用户批量导入临时表
     * @param inparam
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static boolean updateImportBatApnUserInfo(IData inparam, String eparchyCode) throws Exception
    {
        inparam.put("DEAL_TIME", SysDateMgr.getSysTime());
        inparam.put("DEAL_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("DEAL_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("UPDATE TF_F_CMS_IMPORT_BAT  SET ");
        parser.addSQL(" DEAL_STATE = :DEAL_STATE, ");
        parser.addSQL(" DEAL_TIME = TO_DATE(:DEAL_TIME, 'yyyy-mm-dd hh24:mi:ss'), ");
        parser.addSQL(" DEAL_DEPART_ID = :DEAL_DEPART_ID, ");
        parser.addSQL(" DEAL_STAFF_ID = :DEAL_STAFF_ID, ");
        parser.addSQL(" CITY_CODE = :CITY_CODE, ");
        parser.addSQL(" EPARCHY_CODE = :EPARCHY_CODE, ");
        parser.addSQL(" RSRV_STR10 = :RSRV_STR10 ");
        parser.addSQL(" WHERE IMPORT_ID = :IMPORT_ID ");

        return Dao.executeUpdate(parser, Route.CONN_CRM_CG) != 0 ? true : false;
    }
    
    /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int insOtherInfoForApnUser(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_OTHER_APN", param,Route.CONN_CRM_CG);
    }
    
    
}
