
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.unit;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatDealBBossUnit
{

    /**
     * @param
     * @desciption 限制批量办理时间只能是早上7点到晚上20
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:59
     */
    public static String getBatDealDate() throws Exception
    {
        String todayDateTime07 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 7); // 早上7点
        String todayDateTime20 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 20); // 晚上20

        String nowDatetime = SysDateMgr.getSysTime();

        if (nowDatetime.compareTo(todayDateTime07) < 0)
        {
            return nowDatetime;
        }
        else if (nowDatetime.compareTo(todayDateTime07) > 0 && nowDatetime.compareTo(todayDateTime20) < 0)
        {
            return todayDateTime20;
        }
        else
        {
            return nowDatetime;
        }
    }

    /**
     * @param
     * @desciption 生成集团规范要求的附件名称
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:22
     */
    public static String getMemAttachFileName() throws Exception
    {
        return "A" + ProvinceUtil.getProvinceCodeGrpCorp() + SysDateMgr.getSysDate("yyyyMMddHHmmss") + "." + "000";
    }

    /**
     * @param
     * @desciption 根据BatchID、SerialNumber更新未启动批量详情表,更新deal_state/deal_result
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:50
     */
    public static void updateBatDealByBatchIdSn(IData data) throws Exception
    {
        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
        sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
        sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
        sql.append(" where 1=1");
        sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        sql.append(" and a.deal_state = '0'");
        sql.append(" and a.serial_number = :SERIAL_NUMBER");

        Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param
     * @desciption 更新批量详情表
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:33
     */
    public static void updateBatDealStateByBatchId(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
        sql.append(" where 1=1");
        sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        sql.append(" and a.deal_state = '0'");

        Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param
     * @desciption 更新批量任务表信息
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:27:32
     */
    public static void updateBatTaskByBatchTaskId(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder();

        sql.append("update tf_b_trade_bat_task t ");
        sql.append("set t.remark = :REMARK ");
        sql.append("where t.batch_task_id = :BATCH_TASK_ID ");
        sql.append("AND t.accept_month = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");

        Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param
     * @desciption 更新批量状态batdeal
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:40
     */
    public static void updateYDZFBatState(String batch_id) throws Exception
    {

        IData data = new DataMap();
        // 默认为1
        String deal_state = "1";
        // 更改TF_B_TRADE_BAT 的表的状态标志为激活
        data.put("DEAL_STATE", deal_state);
        data.put("BATCH_ID", batch_id);
        data.put("ACTIVE_FLAG", "1");
        data.put("ACTIVE_TIME", SysDateMgr.getSysTime());
        Dao.save("TF_B_TRADE_BAT", data, new String[]{ "BATCH_ID" }, Route.getJourDb(Route.CONN_CRM_CG));

        // 更改TF_B_TRADE_BATDEAL的状态标志为成功 tf_b_trade_batdeal
        IData result = new DataMap();
        result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_9);
        result.put("BATCH_ID", batch_id);
        updateBatDealStateByBatchId(result);
    }
}
