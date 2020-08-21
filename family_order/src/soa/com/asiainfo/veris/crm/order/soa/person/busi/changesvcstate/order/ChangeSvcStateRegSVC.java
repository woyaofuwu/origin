
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcStateRegSVC.java
 * @Description: 用户服务状态变更业务登记类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-7 上午11:30:19
 */
public class ChangeSvcStateRegSVC extends OrderService
{

    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        // 停开机不再做后向校验了
    }

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals("7301", tradeTypeCode) || StringUtils.equals("7303", tradeTypeCode)// 开机类
                || StringUtils.equals("7304", tradeTypeCode) || StringUtils.equals("7313", tradeTypeCode) || StringUtils.equals("7317", tradeTypeCode) || StringUtils.equals("7801", tradeTypeCode)
                || StringUtils.equals("7904", tradeTypeCode) || StringUtils.equals("460", tradeTypeCode))
        {// 信控开机
            orderData.setSubscribeType("201");
        }
        else if (StringUtils.equals("7314", tradeTypeCode) || StringUtils.equals("7311", tradeTypeCode) || StringUtils.equals("7316", tradeTypeCode) || StringUtils.equals("7210", tradeTypeCode) || StringUtils.equals("7101", tradeTypeCode)
                || StringUtils.equals("7110", tradeTypeCode) || StringUtils.equals("7220", tradeTypeCode) || StringUtils.equals("7122", tradeTypeCode) || StringUtils.equals("7312", tradeTypeCode) || StringUtils.equals("7305", tradeTypeCode)
                || StringUtils.equals("7315", tradeTypeCode) || StringUtils.equals("7121", tradeTypeCode) || StringUtils.equals("7802", tradeTypeCode) || StringUtils.equals("7803", tradeTypeCode) || StringUtils.equals("45", tradeTypeCode)
                || StringUtils.equals("7901", tradeTypeCode)|| StringUtils.equals("7902", tradeTypeCode)|| StringUtils.equals("7903", tradeTypeCode) || StringUtils.equals("459", tradeTypeCode))
        {
            orderData.setSubscribeType("200");
        }

        if (StringUtils.equals("44", tradeTypeCode) || StringUtils.equals("46", tradeTypeCode))
        {
            orderData.setExecTime(SysDateMgr.END_DATE_FOREVER);
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            orderData.setRemark(tradeTypeName);
            orderData.setSubscribeType("200");
        }
        else if (StringUtils.equals("43", tradeTypeCode))
        {
            IDataset commparaDataset = CommparaInfoQry.getCommpara("CSM", "2013", "HAIN", btd.getMainTradeData().getEparchyCode());
            if (IDataUtil.isEmpty(commparaDataset))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "查询COMMPARA参数表PARAM_ATTR=2013异常！");
            }
            String paraCode1 = commparaDataset.getData(0).getString("PARA_CODE1");
            if (StringUtils.isEmpty(paraCode1))
            {
                paraCode1 = "18000";// 默认为5小时后执行
            }
            int delayHours = Integer.parseInt(paraCode1) / 3600;
            String dealayTime = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), delayHours);
            orderData.setExecTime(dealayTime);
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            orderData.setRemark(tradeTypeName);
            orderData.setSubscribeType("200");
        }
        
        //防止信控同一秒内发起两笔信控工单造成完工错乱，如果存在执行时间一样的信控工单存在，则后来的信控工单执行时间加一秒
        if("200".equals(orderData.getSubscribeType())||"201".equals(orderData.getSubscribeType())){
        	String nowExecTime = orderData.getExecTime();
//        	nowExecTime = "2016-11-10 10:56:57";
        	String userId = btd.getMainTradeData().getUserId();
        	IDataset results = TradeInfoQry.qryCreditOrderInfo(userId, nowExecTime);
        	if(IDataUtil.isNotEmpty(results)&&results.size()>0){
        		String execTime =SysDateMgr.getNextSecond(nowExecTime);
        		orderData.setExecTime(execTime);
        		orderData.setRemark(orderData.getRemark() + "【执行时间加1秒】");
        	}
        }
        
    }
    
    /**
		* 确认业务类型编码
		*/
    public IDataset confirmTradeTypeCode(IData param)throws Exception{
    	ChangeSvcStateRegBean bean = new ChangeSvcStateRegBean();
    	return bean.getUserSvcStateInfo(param);
    }
}
