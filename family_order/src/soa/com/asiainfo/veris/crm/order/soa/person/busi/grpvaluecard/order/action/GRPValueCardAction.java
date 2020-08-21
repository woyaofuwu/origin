/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.action;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;

/**
 * @CREATED by gongp@2014-8-1 修改历史 Revision 2014-8-1 上午09:55:57
 */
public class GRPValueCardAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(GRPValueCardAction.class);
    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DeviceTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE);
        for (int i = 0; i < list.size(); i++)
            {
                DeviceTradeData data = list.get(i);
                int sum = Integer.parseInt(data.getDeviceNum());
                for (int y = 0; y < sum ; y++)
                {
                	BigInteger card_num = new BigInteger(data.getDeviceNoS());
                	String num = String.valueOf(y);
                	BigInteger x = new BigInteger(num);
        			card_num = card_num.add(x);
                	
                	IData param = new DataMap();
                    param.put("TRADE_ID", btd.getTradeId());
                    param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getTradeId()));
                    param.put("TRADE_TYPE_CODE", btd.getTradeTypeCode());
                    param.put("KIND_NAME", data.getRsrvStr3());
                    param.put("CARD_NUMBER", card_num.toString());
                    param.put("DEVICE_PRICE", data.getDevicePrice());
                    param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                    param.put("SALE_PRICE", data.getRsrvStr4());
                    param.put("GROUP_ID", data.getRsrvStr5());
                    param.put("CUST_ID", data.getRsrvStr6());
                    param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
                    param.put("DEPART_CODE", CSBizBean.getVisit().getDepartCode());
                    param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    param.put("STATE_NAME", "销售");
                    param.put("STATE_CODE", "2");

                    param.put("RSRV_STR3", data.getRsrvStr7());//销售方式
                    param.put("RSRV_STR4", data.getRsrvStr8());//打折 销售价格
                    param.put("RSRV_STR5", data.getRsrvStr10());//打折 折扣率
                    
                    Dao.executeUpdateByCodeCode("TL_B_VALUECARD_DETAILED", "INS_VALUECARD_AUDIT", param);
                }
            }
        }

}
