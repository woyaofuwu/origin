
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata.ServiceOperReqData;

public class ServiceOperExpireAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<SvcStateTradeData> stateList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
        ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        if (stateList != null && stateList.size() > 0)
        {
            String serviceIds = "";
            for (SvcStateTradeData stateData : stateList)
            {
                if ("0".equals(stateData.getModifyTag()))
                {
                    serviceIds += stateData.getServiceId() + "|";
                }
            }
            if (StringUtils.isNotBlank(serviceIds))
            {
                serviceIds = serviceIds.substring(0, serviceIds.length() - 1);
                String operCode = null;
                if ("04".equals(req.getOperCode()))
                {
                    operCode = "05";
                    IData acceptData = new DataMap();
                    acceptData.put("OPER_CODE", operCode);
                    acceptData.put("ELEMENT_ID", serviceIds);
                    acceptData.put("IS_ACTIVE", "false");

                    IData insertData = new DataMap();
                    insertData.put("EXP_ID", SeqMgr.getOrderId());
                    insertData.put("EXEC_MONTH", StrUtil.getAcceptMonthById(btd.getTradeId()));
                    insertData.put("EXP_TYPE", "GPRS");
                    insertData.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth());
                    insertData.put("TRADE_TYPE_CODE", req.getTradeType().getTradeTypeCode());
                    insertData.put("SRC_TRADE_ID", btd.getTradeId());
                    insertData.put("ACCEPT_DATE", req.getAcceptTime());
                    insertData.put("SERIAL_NUMBER", uca.getSerialNumber());
                    insertData.put("DEAL_STATE", "0");
                    insertData.put("SVC_NAME", "SS.ServiceOperRegSVC.tradeReg");
                    insertData.put("ACCEPT_DATA1", acceptData.toString());
                    insertData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    insertData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    insertData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    insertData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    Dao.insert("TF_B_ORDER_EXPIRE", insertData, Route.CONN_CRM_CEN);
                }
            }
        }
    }
}
