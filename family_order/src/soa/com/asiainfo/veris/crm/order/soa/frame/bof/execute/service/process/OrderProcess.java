
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class OrderProcess
{

    public static MainOrderData createOrderData(IData input, BusiTradeData btd) throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();
        MainOrderData mainOrderData = new MainOrderData();
        IData resultSet = null;
        UcaData uca = btd.getRD().getUca();

        // 查找tradeTypeInfo
        TradeTypeData tradeType = null;
        resultSet = UTradeTypeInfoQry.getTradeType(dataBus.getOrderTypeCode(), uca.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(resultSet))
        {
            tradeType = new TradeTypeData(resultSet);
        }

        String inModeCode = CSBizBean.getVisit().getInModeCode();

        mainOrderData.setOrderTypeCode(dataBus.getOrderTypeCode());
        mainOrderData.setOrderId(dataBus.getOrderId());
        mainOrderData.setTradeTypeCode(dataBus.getOrderTypeCode());
        mainOrderData.setOrderState("0");
        mainOrderData.setPriority(tradeType.getPriority());
        mainOrderData.setNextDealTag("0");
        mainOrderData.setInModeCode(inModeCode);
        mainOrderData.setTradeStaffId(CSBizBean.getVisit().getStaffId());
        mainOrderData.setTradeDepartId(CSBizBean.getVisit().getDepartId());
        mainOrderData.setTradeCityCode(CSBizBean.getVisit().getCityCode());
        mainOrderData.setTradeEparchyCode(CSBizBean.getTradeEparchyCode());

        mainOrderData.setOperFee(dataBus.getOperFee());
        mainOrderData.setForegift(dataBus.getForeGift());
        mainOrderData.setAdvancePay(dataBus.getAdvanceFee());
        mainOrderData.setExecTime(dataBus.getAcceptTime());
        mainOrderData.setCancelTag("0");
        mainOrderData.setBatchId(dataBus.getBatchId());

        mainOrderData.setCustId(uca.getCustId());
        mainOrderData.setCustName(uca.getCustomer().getCustName());
        mainOrderData.setPsptTypeCode(uca.getCustomer().getPsptTypeCode());
        mainOrderData.setPsptId(uca.getCustomer().getPsptId());
        mainOrderData.setEparchyCode(uca.getUser().getEparchyCode());
        mainOrderData.setCityCode(uca.getUser().getCityCode());
        mainOrderData.setOrderId(dataBus.getOrderId());

        if (Integer.parseInt(dataBus.getOperFee()) > 0 || Integer.parseInt(dataBus.getForeGift()) > 0 || Integer.parseInt(dataBus.getAdvanceFee()) > 0)
        {
            mainOrderData.setFeeState(BofConst.FEE_STATE_YES);
            mainOrderData.setFeeStaffId(CSBizBean.getVisit().getStaffId());
            mainOrderData.setFeeTime(dataBus.getAcceptTime());
        }
        else
        {
            mainOrderData.setFeeState(BofConst.FEE_STATE_NO);
        }

        if (StringUtils.isNotBlank(btd.getRD().getBatchId()))
        {
            if ("9".equals(btd.getRD().getBatchDealType()))
            {
                mainOrderData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_BATCH_PF_FILE);
            }
            else
            {
                mainOrderData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_BATCH_NOW);
            }
        }
        else
        {
            mainOrderData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);
        }
        String inModecodeStr = StaticUtil.getStaticValue("NEED_PAY_CHANNELS", "0");
        //如果有待支付,待打印 TRADE,则改order状态为 X或者Y,
        if(StringUtils.indexOf(inModecodeStr,"|"+inModeCode+"|") != -1){
        	List<BusiTradeData> btds = dataBus.getBtds();
            if(ArrayUtil.isNotEmpty(btds))
            {
            	for(BusiTradeData temp:btds)
            	{
            		String subscribeState = temp.getMainTradeData().getSubscribeState();
            		if(StringUtils.equals("X", subscribeState))
            		{
            			if(!StringUtils.equals("X", mainOrderData.getOrderState()))
            			{
            				mainOrderData.setOrderState("X");
            			}
            		}
            		else if(StringUtils.equals("Y", subscribeState))
            		{
            			if(!StringUtils.equals("X", mainOrderData.getOrderState()))
            			{
            				mainOrderData.setOrderState("Y");
            			}
            		}
            	}
            }
        }

        return mainOrderData;
    }
}
