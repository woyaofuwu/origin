
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wlan;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class WlanOperLogAction implements IProductModuleAction
{

    /**
     * WLAN封顶暂停暂停的操作日志
     */
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        String transCode = CSBizBean.getVisit().getXTransCode();
        if (("04_05".indexOf(pstd.getOperCode()) >= 0) && transCode.equals("SS.WlanCapSuspendAndResume.tradeReg"))
        {
            IData map = new DataMap();
            map.put("LOG_ID", SeqMgr.getLogIdForCrm());
            map.put("TRADE_ID", btd.getTradeId());
            map.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getTradeId()));
            map.put("USER_ID", uca.getUserId());
            map.put("SERIAL_NUMBER", uca.getSerialNumber());
            map.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PLATSVC);
            map.put("ELEMENT_ID", pstd.getElementId());
            map.put("OPER_CODE", pstd.getOperCode());
            map.put("OPER_TYPE", "GP");
            if ("04".equals(pstd.getOperCode()))
            {
                map.put("STATE", "N");
            }
            else
            {
                map.put("STATE", "A");
            }
            map.put("OPER_TIME", btd.getRD().getAcceptTime());
            map.put("UPDATE_TIME", btd.getRD().getAcceptTime());
            map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            Dao.insert("TL_B_USER_ELMTSTATE", map, BizRoute.getRouteId());
        }

    }

}
