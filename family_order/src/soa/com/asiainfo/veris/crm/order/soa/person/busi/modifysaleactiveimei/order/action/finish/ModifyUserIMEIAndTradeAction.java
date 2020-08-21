
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.action.finish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

public class ModifyUserIMEIAndTradeAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String relationTradeId = mainTrade.getString("RSRV_STR3");
        String newIMEI = mainTrade.getString("RSRV_STR1");
        String oldIMEI = mainTrade.getString("RSRV_STR2");
        String userId = mainTrade.getString("USER_ID");
        
        //处理USER_IMEI======================
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("END_DATE", SysDateMgr.getSysTime());
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_USERIMEI_ENDDATE", cond);
        
        cond.clear();
        cond.put("USER_ID", userId);
        cond.put("IMEI", oldIMEI);
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "DEL_USERIMEI_BY_ENDDATE", cond);

        cond.clear();
        cond.put("USER_ID", userId);
        cond.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        cond.put("IMEI", newIMEI);
        cond.put("START_DATE", SysDateMgr.getSysTime());
        cond.put("END_DATE", SysDateMgr.getTheLastTime());
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "INS_ALL", cond);
        //======================
        
        //=========处理台账
        cond.clear();
        cond.put("TRADE_ID", relationTradeId);
        cond.put("NEW_IMEI", newIMEI);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_SALE_GOODS", "UPD_BY_TRADEID", cond, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        //===============
    }
}
