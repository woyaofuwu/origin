package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakAppReqData;

public class VipSimBakAppTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        VipSimBakAppReqData reqData = (VipSimBakAppReqData) btd.getRD();

        createTradeMain(btd);

        // infoRegTradeRes(btd); //备卡申请不插res 

        // 资源接口调用不通
        ResCall.resEngrossForSim("3", reqData.getBakCardNo(), reqData.getUca().getSerialNumber());// 预占

//        insertVipSimBak(btd);//将登记时插资料表的处理移到afterPrintAction中
        
        if("NO".equals(reqData.getCreditFeeTag()))
        {
        	insertUserOtherServ(btd);
        }
        
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
        VipSimBakAppReqData reqData = (VipSimBakAppReqData) btd.getRD();

        List<MainTradeData> mainList = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);

        mainList.get(0).setPriority("240");
        mainList.get(0).setRsrvStr3(reqData.getBakCardNo());
        mainList.get(0).setRsrvStr4(reqData.getBakCardNo());
        mainList.get(0).setRsrvStr9(insertVipSimBak(btd));
        mainList.get(0).setRsrvStr10(reqData.getBakCardNo());
        
        if(!StringUtils.equals("0", reqData.getSaleFeeTag())){
            mainList.get(0).setRsrvStr7(reqData.getSaleMoney());
        }
        
        IDataset resDataSet = ResCall.getSimCardInfo("0", reqData.getBakCardNo(), "", "");
        if(IDataUtil.isNotEmpty(resDataSet)){
            IData resInfo = resDataSet.getData(0);
            mainList.get(0).setRsrvStr5(resInfo.getString("RES_TYPE_CODE"));
            mainList.get(0).setRsrvStr6(resInfo.getString("RES_KIND_CODE"));
        }
        
        if ("1".equals(reqData.getUserTag()))
        {

            mainList.get(0).setRsrvStr1(reqData.getUca().getUser().getUserId());
            mainList.get(0).setRsrvStr2("1");

        }
        else if ("2".equals(reqData.getUserTag()))
        {

            mainList.get(0).setRsrvStr1(reqData.getVipId());
            mainList.get(0).setRsrvStr2("0");

        }
        else
        {
            // common.error("用户类型获取出错！");
        }

    }

    /**
     * 组织台帐资源子表数据
     * 
     * @param btd
     * @throws Exception
     */
    protected void infoRegTradeRes(BusiTradeData btd) throws Exception
    {

        VipSimBakAppReqData reqData = (VipSimBakAppReqData) btd.getRD();

        ResTradeData resTD = new ResTradeData();

        resTD.setResTypeCode("1");
        resTD.setResCode(reqData.getBakCardNo());
        resTD.setInstId(reqData.getTradeId());

        resTD.setImsi(reqData.getIMSI()); // 待定
        resTD.setRsrvStr1(reqData.getSimTypeCode()); // 待定

        resTD.setRemark(reqData.getRemark());

        resTD.setUserId(reqData.getUca().getUser().getUserId());
        resTD.setUserIdA("-1");
        resTD.setModifyTag("0");
        resTD.setStartDate(reqData.getAcceptTime());
        resTD.setEndDate(SysDateMgr.END_DATE_FOREVER);

        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }

    private String insertVipSimBak(BusiTradeData btd) throws Exception
    {

        VipSimBakAppReqData reqData = (VipSimBakAppReqData) btd.getRD();

        IData inparam = new DataMap();
        IDataset dataset = CustVipInfoQry.getVipSimbakInfos(reqData.getBakCardNo());
        if (IDataUtil.isNotEmpty(dataset))
        {
            CSAppException.apperr(ResException.CRM_RES_21, reqData.getBakCardNo());
        }

        inparam.put("SIM_TYPE_CODE", reqData.getSimTypeCode());
        inparam.put("IMSI", reqData.getIMSI());
        inparam.put("SEND_DATE", reqData.getAcceptTime());

        if ("1".equals(reqData.getUserTag()))
        {
            inparam.put("VIP_ID", reqData.getUca().getUser().getUserId());
        }
        else if ("2".equals(reqData.getUserTag()))
        {
            inparam.put("VIP_ID", reqData.getVipId());
        }
        inparam.put("CLIENT_INFO2", reqData.getKI());
        inparam.put("CLIENT_INFO3", reqData.getRsrvStr4());
        return IDataUtil.isNotEmpty(inparam)?inparam.toString():"";
    }

    private void insertUserOtherServ(BusiTradeData btd) throws Exception
    {

        VipSimBakAppReqData reqData = (VipSimBakAppReqData) btd.getRD();

        IData inparam = new DataMap();
        String strUserId = btd.getMainTradeData().getUserId();
        
        inparam.put("PARTITION_ID", Long.parseLong(strUserId) % 10000);
        inparam.put("INST_ID", SeqMgr.getInstId());
        inparam.put("USER_ID", strUserId);
        inparam.put("SERVICE_MODE", "05");
        inparam.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
        inparam.put("PROCESS_INFO", "星级客户免费办理备卡记入补换卡次数");
        inparam.put("RSRV_NUM1", "0");
        inparam.put("RSRV_NUM2", "0");
        inparam.put("RSRV_NUM3", "0");
        inparam.put("RSRV_STR1", reqData.getIMSI());
        inparam.put("RSRV_STR2", reqData.getKI());
        inparam.put("RSRV_STR3", reqData.getSimTypeCode());
        inparam.put("RSRV_STR4", reqData.getBakCardNo());
        if ("1".equals(reqData.getUserTag()))
        {
            inparam.put("RSRV_STR5", reqData.getUca().getUser().getUserId());
        }
        else if ("2".equals(reqData.getUserTag()))
        {
            inparam.put("RSRV_STR5", reqData.getVipId());
        }        
        inparam.put("RSRV_STR6", btd.getMainTradeData().getOrderId());
        inparam.put("RSRV_STR7", "");
        inparam.put("RSRV_STR8", "");
        inparam.put("RSRV_STR9", "");
        inparam.put("RSRV_STR10", "");
        inparam.put("RSRV_DATE1", "");
        inparam.put("RSRV_DATE2", "");
        inparam.put("RSRV_DATE3", "");
        inparam.put("PROCESS_TAG", "0");
        inparam.put("STAFF_ID",  CSBizBean.getVisit().getStaffId());
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("START_DATE", SysDateMgr.getSysTime());
        inparam.put("END_DATE", SysDateMgr.getTheLastTime());
        inparam.put("REMARK", "REQ201604220007 关于大客户备卡的优化 ");
       

        Dao.insert("TF_F_USER_OTHERSERV", inparam);
    }

}
