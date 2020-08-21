
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class DealPwlwDiscntAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
	public void executeAction(BusiTradeData btd) throws Exception
    {
		if (!"PWLW".equals(btd.getMainTradeData().getBrandCode()))
        {
            return;
        }
        IDataset idsCommpara1551 = CommparaInfoQry.getOnlyByAttr("CSM", "1551", btd.getRD().getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(idsCommpara1551))
        {
            return;
        }
        List<DiscntTradeData> lsDiscntTrade = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if(CollectionUtils.isEmpty(lsDiscntTrade))
        {
        	return;
        }
        //for (DiscntTradeData dtDiscntTrade: lsDiscntTrade)
        String FstrStartDate = "";
        boolean bIsExist = false;
        for (int i = 0; i < lsDiscntTrade.size(); i++)
        {
        	DiscntTradeData dtDiscntTrade = lsDiscntTrade.get(i);
        	String strModifyTag = dtDiscntTrade.getModifyTag();
        	String strElementId = dtDiscntTrade.getElementId();
        	String strEndDate = dtDiscntTrade.getEndDate();
            //if (!bIsExist){
            if (BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
            {
                for (int ii = 0; ii < idsCommpara1551.size(); ii++)
                {
                    // xiekl修改 PARA_CODE改为PARAM_CODE
                	IData idCommpara1551 = idsCommpara1551.getData(ii);
                    String strParamCode = idCommpara1551.getString("PARAM_CODE");
                    if (strElementId.equals(strParamCode))
                    {
                    	//物联网正式套餐开始时间是测试套餐的结束时间下个月的第一天
                    	FstrStartDate = SysDateMgr.getDateNextMonthFirstDay(strEndDate);
                    	bIsExist = true;
                        break;
                    }
                }
            }

            if (bIsExist)
            {
                break;
            }
        }
        if (bIsExist && StringUtils.isNotBlank(FstrStartDate))
        {
            List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);// 只能循环 sunxin
            if(CollectionUtils.isNotEmpty(userTradeDatas))
            {
            	for (UserTradeData userTradeData : userTradeDatas)
            	{
                    userTradeData.setUserTypeCode("E");
                }
            }
            
            //for (DiscntTradeData discntTradeData: lsDiscntTrade)
            for (int i = 0; i < lsDiscntTrade.size(); i++)
            {
            	DiscntTradeData dtDiscntTrade = lsDiscntTrade.get(i);
            	String strModifyTag = dtDiscntTrade.getModifyTag();
            	String strElementId = dtDiscntTrade.getElementId();
            	//String strStartDate = dtDiscntTrade.getStartDate();
            	if (BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
            	{
            		boolean isModfy = true;
                    for (int ii = 0; ii < idsCommpara1551.size(); ii++)
                    {
                    	IData idCommpara1551 = idsCommpara1551.getData(ii);
                        String strParamCode = idCommpara1551.getString("PARAM_CODE");
                        //String strParamCode = idsCommpara1551.getData(ii).getString("PARAM_CODE");
                        if (strElementId.equals(strParamCode))
                        {
                        	isModfy = false;
                        	break;
                        }
                    }
                    if(isModfy)
                    {
                    	//shenhai 物联网正是产品优惠开始时间往后延两年 
                    	//String startdate= SysDateMgr.addYearsNature(SysDateMgr.getSysDate(), 2);
                    	dtDiscntTrade.setStartDate(FstrStartDate);
                    	//shenhai end
                    }
                }
            }
        }
    }
}
