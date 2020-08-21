
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakActReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakCancelReqData;

public class VipSimBakCancelTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        createTradeMain(btd);
//        updateVipSimBak(btd);////将登记时插资料表的处理移到afterPrintAction中

    }

    /**
     * 设置台帐主表额外的数据
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void createTradeMain(BusiTradeData btd) throws Exception
    {
        VipSimBakCancelReqData reqData = (VipSimBakCancelReqData) btd.getRD();

        List<MainTradeData> mainList = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);

        mainList.get(0).setRsrvStr2("0");
        mainList.get(0).setRsrvStr10(reqData.getNewSimCardNo());

        if ("1".equals(reqData.getNormalUserSimbak()) && "0".equals(reqData.getVipEmptyTag()))
        {
            mainList.get(0).setRsrvStr2("1");
        }
        
        // 资源接口调不通
        ResCall.backupCardCancel(reqData.getNewSimCardNo());

    }

    /**
     * 功能说明：生成资源台帐 最后决定 取消不写资源台帐
     * 
     * @param btd
     * @throws Exception
     */
    protected void geneResTrade(BusiTradeData btd) throws Exception
    {

        VipSimBakCancelReqData reqData = (VipSimBakCancelReqData) btd.getRD();

        ResTradeData resTdDel = new ResTradeData();

        resTdDel.setUserId(reqData.getUca().getUser().getUserId());
        resTdDel.setUserIdA("-1");
        resTdDel.setResTypeCode("1");

        resTdDel.setResCode(reqData.getNewSimCardNo());
        resTdDel.setImsi(reqData.getNewIMSI());
        resTdDel.setInstId(reqData.getTradeId());

        resTdDel.setStartDate(SysDateMgr.getSysDate());
        resTdDel.setEndDate(SysDateMgr.getSysDate());

        resTdDel.setModifyTag("1");

        resTdDel.setRemark(reqData.getRemark());

        btd.add(btd.getRD().getUca().getSerialNumber(), resTdDel);

    }

}
