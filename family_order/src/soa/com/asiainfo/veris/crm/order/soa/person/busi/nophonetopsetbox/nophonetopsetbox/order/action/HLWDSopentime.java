
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

/**
 * 互联网开户，修改台帐表开始时间
 */
public class HLWDSopentime implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String finishDate = ""; 
        String CheckStr = "0";
        //MODIFY_TAG为1的，不做处理
        if ("4910".equals(tradeTypeCode))
        {
        	finishDate = SysDateMgr.getSysTime(); 
           //finishDate = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()); 
           
            String execDate = "";
            String tagDate = "25";
            String sysDay = "";
       
            sysDay = finishDate.substring(8, 10);
            // 默认下月开通
            execDate = SysDateMgr.getDateNextMonthFirstDay(finishDate);
            // 如大于标识日期，则下下月开通
            if (sysDay.compareTo(tagDate) >= 0)
            {
                execDate = SysDateMgr.getDateNextMonthFirstDay(execDate);
            }

             //SysDateMgr.END_DATE_FOREVER  2050年
            //TradeDiscntInfoQry.updateStartDate(tradeId, execDate);
            IDataset discntInfos = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
            for (int i = 0; i < discntInfos.size(); i++){
            	IData discntInfo_check = discntInfos.getData(i); 
            	String MODIFY_TAG = discntInfo_check.getString("MODIFY_TAG");
            	if ("1".equals(MODIFY_TAG)) {
            		CheckStr = MODIFY_TAG;
            	}
            }
            if("1".equals(CheckStr)) { 
            	return ;
            } 
            
            for (int i = 0; i < discntInfos.size(); i++)
            {
                IData discntInfo = discntInfos.getData(i); 
                int DISCNT_CODE = discntInfo.getInt("DISCNT_CODE",0); 
                //String DISCNT_CODE = discntInfo.getString("DISCNT_CODE","0"); 
                String INST_ID = discntInfo.getString("INST_ID");// 取出优惠编码 
        		IDataset commpara3700List = CommparaInfoQry.getTradeDiscntbyDiscntcode("CSM", "3700", String.valueOf(DISCNT_CODE)); 
        		if (!IDataUtil.isEmpty(commpara3700List)) 
        		 {
                     TradeDiscntInfoQry.updateStartEndDate(tradeId, discntInfo.getString("INST_ID"), execDate,discntInfo.getString("END_DATE","2050-12-31 23:59:59"));
        	     } 
            } 
        }
    }
}
