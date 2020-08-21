
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class LogBean
{
    /**
     * 记录客户经理操作日志
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static boolean insertOperLog(IData inparam) throws Exception
    {
        IData param = new DataMap();
        param.put("OPER_ID", SeqMgr.getOperId());
        param.put("OPER_MOD", inparam.getString("OPER_MOD")); // 操作模块
        param.put("OPER_TYPE", inparam.getString("OPER_TYPE")); // 操作类型

        // 去掉$开头的参数
        String oper_desc = inparam.getString("OPER_DESC");
        String oper_desc_in = oper_desc.replaceAll("\\\"+\\$+(\\w*|\\W*)+\"", "");
        if (oper_desc_in.length() > 2000)
        {
            oper_desc_in = oper_desc_in.substring(0, 1900);
        }

        String staffId = inparam.getString("STAFF_ID", CSBizBean.getVisit().getStaffId());
        String deptId = inparam.getString("DEPART_ID", CSBizBean.getVisit().getDepartId());

        param.put("STAFF_ID", staffId); // 操作员工
        param.put("OPER_DESC", oper_desc_in); // 操作描述
        param.put("RSRV_STR1", deptId); // 操作员工归属部门
        param.put("RSRV_STR2", inparam.getString("CITY_ID", CSBizBean.getVisit().getCityCode())); // CITY_CODE归属业务区
        param.put("RSRV_STR3", inparam.getString("IP_ADDR", CSBizBean.getVisit().getRemoteAddr())); // 记录登录员工IP
        param.put("OPER_TIME", SysDateMgr.getSysTime()); // 操作时间
        param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 当前月份
        param.put("DAY", SysDateMgr.getCurDay()); // 当前日

        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", deptId);
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());

        return Dao.insert("TL_B_CRM_OPERLOG", param, Route.CONN_LOG);
    }
}
