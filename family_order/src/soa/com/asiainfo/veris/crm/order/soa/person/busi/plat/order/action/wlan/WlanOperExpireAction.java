
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wlan;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UserSertrackLogInfoQry;

public class WlanOperExpireAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {

        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        String transCode = CSBizBean.getVisit().getXTransCode();
        if (PlatConstants.OPER_PAUSE.equals(pstd.getOperCode()) && transCode.equals("SS.WlanCapSuspendAndResume.tradeReg"))
        {
            String userId = uca.getUserId();
            IData acceptData = new DataMap();
            acceptData.put("OPER_CODE", PlatConstants.OPER_RESTORE);
            acceptData.put("ELEMENT_ID", pstd.getElementId());
            acceptData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
            acceptData.put("USER_ID", userId);
            acceptData.put("SVC_NAME", "SS.WlanCapSuspendAndResume.tradeReg");
            acceptData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            acceptData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            acceptData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            acceptData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            acceptData.put("TRADE_TYPE_CODE", "3700");

            IData insertData = new DataMap();
            insertData.put("DEAL_ID", btd.getTradeId());
            insertData.put("USER_ID", userId);
            insertData.put("PARTITION", userId.substring(userId.length() - 4, userId.length()));
            insertData.put("SERIAL_NUMBER", uca.getSerialNumber());
            insertData.put("EPARCHY_CODE", btd.getRD().getUca().getUserEparchyCode());
            insertData.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth());// 先统一默认为下月1号，有特殊情况再处理
            insertData.put("EXEC_MONTH", SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(1, SysDateMgr.getSysTime()), "MM"));
            insertData.put("IN_TIME", SysDateMgr.getSysTime());
            insertData.put("DEAL_STATE", "0");
            insertData.put("DEAL_COND", acceptData.toString());
            insertData.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_WLANRESUME);

            Dao.insert("TF_F_EXPIRE_DEAL", insertData);
        }
        
        //记录业务轨迹日志  
        if(transCode.equals("SS.WlanCapSuspendAndResume.tradeReg")){
        	boolean flag = true; 
	        if (PlatConstants.OPER_RESTORE.equals(pstd.getOperCode()))
	        {
	            if (UserSertrackLogInfoQry.queryByUserId(uca.getUserId()).size() == 0)
	            {
	                flag = false;
	            }
	        }
	        if (flag)
	        {
	            addUserSerTrackLog(btd,pstd);
	        }
        }
    }

    public void addUserSerTrackLog(BusiTradeData btd,PlatSvcTradeData pstd) throws Exception
    {
        IData param = new DataMap();
//        PlatReqData req = (PlatReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String serviceId = pstd.getElementId();
        String operCode = pstd.getOperCode();
        String tradeId = btd.getTradeId();
        String accptMonth = StrUtil.getAcceptMonthById(tradeId);
        String remark = "";
        String stateCode = "";

        if ("04".equals(operCode))
        {
            remark = "WLAN套餐封顶";
            stateCode = "0";
        }
        else
        {
            remark = "WLAN套餐封顶主动恢复";
            stateCode = "1";
        }

        param.put("USER_ID", uca.getUserId());
        param.put("ACCEPT_MONTH", accptMonth);
        param.put("SERIAL_NUMBER", uca.getSerialNumber());
        param.put("REMARK", remark);
        param.put("TRADE_ID", tradeId);
        param.put("STATE_CODE", stateCode);

        StringBuilder strSql = new StringBuilder();
        StringBuilder strSqlDel = new StringBuilder();
        StringBuilder strSqlIns = new StringBuilder();

        IData cond = new DataMap();

        // 插入MODIFY_TAG=2通知账务修改
        strSqlDel.append(" INSERT INTO TL_F_USER_SERTRACK_LOG(TRADE_ID,USER_ID,ACCEPT_MONTH,SERIAL_NUMBER,");
        strSqlDel.append(" SERV_TYPE,STATE_CODE,START_DATE,END_DATE,UPDATE_DATE,REMARK,MODIFY_TAG )");
        strSqlDel.append(" select :TRADE_ID ,USER_ID,ACCEPT_MONTH,SERIAL_NUMBER,");
        strSqlDel.append("  SERV_TYPE,STATE_CODE,START_DATE,sysdate,sysdate,REMARK,'2'");
        strSqlDel.append("  from TL_F_USER_SERTRACK_LOG");
        strSqlDel.append("  WHERE USER_ID = :USER_ID");
        strSqlDel.append("   AND MODIFY_TAG = '0'");
        strSqlDel.append("   AND SYSDATE BETWEEN START_DATE AND END_DATE");
        Dao.executeUpdate(strSqlDel, param);

        // 修改状态使轨迹表中用户其他记录失效
        strSql.append(" UPDATE TL_F_USER_SERTRACK_LOG   SET END_DATE=SYSDATE,MODIFY_TAG ='1' ");
        strSql.append(" WHERE USER_ID = :USER_ID AND SYSDATE BETWEEN START_DATE AND END_DATE  AND MODIFY_TAG = '0'");
        ;
        int r = Dao.executeUpdate(strSql, param);

        // 插入MODIFY_TAG=0通知账务新增

        cond.putAll(param);
        cond.put("SERV_TYPE", "1");
        cond.put("START_DATE", pstd.getOperTime());
        cond.put("END_DATE", SysDateMgr.getTheLastTime());
        cond.put("UPDATE_DATE", pstd.getOperTime());
        cond.put("MODIFY_TAG", "0");
        Dao.insert("TL_F_USER_SERTRACK_LOG", cond);

        // Dao.executeUpdate(strSqlIns, param);
    }
}
