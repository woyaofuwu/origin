package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;

public class GprsResumeExpireAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	MainTradeData mtd = btd.getMainTradeData();
//    	String transCode = CSBizBean.getVisit().getXTransCode();
        if (PlatConstants.OPER_PAUSE.equals(mtd.getRsrvStr7()) && "FD".equals(mtd.getRemark()))
        {
        	String userId = btd.getRD().getUca().getUserId();
            IData acceptData = new DataMap();
            acceptData.put("OPER_CODE", PlatConstants.OPER_RESTORE);
            acceptData.put("ELEMENT_ID", mtd.getRsrvStr5());
            acceptData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
            acceptData.put("USER_ID", userId);
            acceptData.put("SERV_TYPE", mtd.getRsrvStr8());
            acceptData.put("OPEN_CODE", "1");
            acceptData.put("EXPIRE_ACTIVE", "1");
            

            IData insertData = new DataMap();
            insertData.put("DEAL_ID", btd.getTradeId());
            insertData.put("USER_ID", userId);
            insertData.put("PARTITION", userId.substring(userId.length() - 4, userId.length()));
            insertData.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
            insertData.put("EPARCHY_CODE", btd.getRD().getUca().getUserEparchyCode());
            String exec_time = SysDateMgr.getFirstDayOfNextMonth();
            if(StringUtils.isNotBlank(mtd.getRsrvStr9()) && mtd.getRsrvStr9().equals("3")){
            	exec_time = SysDateMgr.getTomorrowDate();
            }
            insertData.put("EXEC_TIME", exec_time);// 先统一默认为下月1号，有特殊情况再处理
            insertData.put("EXEC_MONTH", SysDateMgr.getTheMonth(exec_time));
            insertData.put("IN_TIME", SysDateMgr.getSysTime());
            insertData.put("DEAL_STATE", "0");
            insertData.put("DEAL_COND", acceptData.toString());
            insertData.put("DEAL_TYPE", "GPRS");
            insertData.put("REMARK", "GPRS流量封顶暂停恢复");
            if(mtd.getRsrvStr5().equals("220")){
            	insertData.put("DEAL_TYPE", "CPE");
            	insertData.put("REMARK", "CPE封顶暂停恢复");
            }
            
            insertData.put("TRADE_ID", btd.getTradeId());

            Dao.insert("TF_F_EXPIRE_DEAL", insertData);
        	
        }
    	
    	
        }
    }

