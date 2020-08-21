
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata.ServiceOperReqData;

public class ServiceOperLogAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<SvcStateTradeData> stateList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
        ServiceOperReqData req = (ServiceOperReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();

        if (stateList != null && stateList.size() > 0)
        {
            IDataset insertDatas = new DatasetList();
            for (SvcStateTradeData stateData : stateList)
            {
                IData map = new DataMap();
                map.put("LOG_ID", SeqMgr.getLogIdForCrm());
                map.put("TRADE_ID", btd.getTradeId());
                map.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getTradeId()));
                map.put("USER_ID", uca.getUserId());
                map.put("SERIAL_NUMBER", uca.getSerialNumber());
                map.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
                map.put("ELEMENT_ID", stateData.getServiceId());
                map.put("OPER_CODE", req.getOperCode());
                map.put("OPER_TYPE", "GP");
                if ("04".equals(req.getOperCode()))
                {
                    map.put("STATE", "N");
                }
                else
                {
                    map.put("STATE", "A");
                }
                map.put("OPER_TIME", req.getAcceptTime());
                map.put("UPDATE_TIME", req.getAcceptTime());
                map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                insertDatas.add(map);
            }
            Dao.insert("TL_B_USER_ELMTSTATE", insertDatas);
        }
    }

}
