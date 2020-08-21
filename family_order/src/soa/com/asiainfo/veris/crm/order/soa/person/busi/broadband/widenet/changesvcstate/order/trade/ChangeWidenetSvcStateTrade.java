
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changesvcstate.order.trade;

import java.util.Date;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;

public class ChangeWidenetSvcStateTrade extends BaseTrade implements ITrade
{

    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {

        btd.getMainTradeData().setSubscribeType("300");
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        String tradeTypeCode = btd.getTradeTypeCode();
        
        //宽带主动报停时，手机不需要连带宽带紧急开机。
        if ("639".equals(tradeTypeCode) || "637".equals(tradeTypeCode) || "638".equals(tradeTypeCode) || "641".equals(tradeTypeCode))
        {
        	checkConnectOpen(btd);
        }
        //宽带局方停机时，手机报开、局方开机、信控开机业务，不允许关联宽带开机
        if("7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode)|| "7309".equals(tradeTypeCode)
        		|| "639".equals(tradeTypeCode) || "637".equals(tradeTypeCode) || "638".equals(tradeTypeCode) || "641".equals(tradeTypeCode)){
        	checkConnectOpenForOffice(btd,"1");
        }
        //手机报停、手机信控停机（高额半停、高额停机、欠费停机）业务，不允许关联修改宽带状态
        if("617".equals(tradeTypeCode) || "618".equals(tradeTypeCode) || "632".equals(tradeTypeCode) || "603".equals(tradeTypeCode) || "7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode)
                || "7224".equals(tradeTypeCode)){
        	checkConnectOpenForOffice(btd,"2");
        }
        
        ChangeSvcStateComm changeSvcStateComm = new ChangeSvcStateComm();
        changeSvcStateComm.getSvcStateChangeTrade(btd); // 获取用户服务状态变更订单
        // 宽带报开，有预约报停特殊处理
        if ("683".equals(tradeTypeCode) || "684".equals(tradeTypeCode) || "619".equals(tradeTypeCode) || "621".equals(tradeTypeCode) || "633".equals(tradeTypeCode) || "604".equals(tradeTypeCode) || "7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode)
                || "7309".equals(tradeTypeCode) || "639".equals(tradeTypeCode) || "637".equals(tradeTypeCode) || "638".equals(tradeTypeCode) || "641".equals(tradeTypeCode))
        {

            changeSvcStateComm.getWidenetSvcStateChangeTrade(btd);
        }
        List<SvcStateTradeData> tradesSvcDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);

        if ("617".equals(tradeTypeCode) || "618".equals(tradeTypeCode) || "632".equals(tradeTypeCode) || "603".equals(tradeTypeCode) || "7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode)
                || "7224".equals(tradeTypeCode))
        {

            for (int j = 0, size = tradesSvcDataList.size(); j < size; j++)
            {
                SvcStateTradeData svcStateData = tradesSvcDataList.get(j);
                if ("1".equals(svcStateData.getModifyTag()))
                {
                    svcStateData.setEndDate(SysDateMgr.getLastDateThisMonth());
                }
                else if ("0".equals(svcStateData.getModifyTag()))
                {
                    svcStateData.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
                }
            }
        }
        //局方停机、开机，立即生效
        if("671".equals(tradeTypeCode) || "672".equals(tradeTypeCode)){
        	String strExecTime = btd.getRD().getAcceptTime();
        	
        	 for (int j = 0, size = tradesSvcDataList.size(); j < size; j++)
             {
                 SvcStateTradeData svcStateData = tradesSvcDataList.get(j);
                 if ("1".equals(svcStateData.getModifyTag()))
                 {
                     svcStateData.setEndDate(strExecTime);
                 }
                 else if ("0".equals(svcStateData.getModifyTag()))
                 {
                     svcStateData.setStartDate(SysDateMgr.getNextSecond(strExecTime));
                 }
             }
        }
        changeSvcStateComm.modifyMainSvcStateByUserId(btd);// 修改用户主体服务状态和最后停机时间

        appendTradeMainData(btd);

    }
    
    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     * @author wukw3
     */
    private void checkConnectOpen(BusiTradeData<BaseTradeData> btd) throws Exception
    {

    	// 获取用户服务状态
        List<SvcStateTradeData> userSvcStateList = btd.getRD().getUca().getUserSvcsState();// 从uca取提高效率
        for (int j = 0, sizeSvcStateList = userSvcStateList.size(); j < sizeSvcStateList; j++)
        {
            SvcStateTradeData userSvcTradeData = userSvcStateList.get(j);
            String userSvcState = userSvcTradeData.getStateCode();
            String endDate = userSvcTradeData.getEndDate();
            
            Date d1, d2;
            // 将输入数据 String 转为 Date 类型以便下面作日期比较
            d1 = SysDateMgr.string2Date(SysDateMgr.END_TIME_FOREVER_2030, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s2); //开始时间
            d2 = SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s3); //结束时间

            if ("1".equals(userSvcState) && d2.compareTo(d1) > 0)
            {
            	String str = "宽带用户属于主动报停状态，不给予连带开机，需要主动报开！";
                CSAppException.apperr(CrmUserException.CRM_USER_783, str);
            }
        }
    }
    /**
     * 宽带局方停机状态，手机业务连带宽带状态限制
     * 
     * @param btd
     * @throws Exception
     * @author lijun17
     */
    private void checkConnectOpenForOffice(BusiTradeData<BaseTradeData> btd,String operType) throws Exception
    {
    	// 获取用户服务状态
        List<SvcStateTradeData> userSvcStateList = btd.getRD().getUca().getUserSvcsState();// 从uca取提高效率
        for (int j = 0, sizeSvcStateList = userSvcStateList.size(); j < sizeSvcStateList; j++)
        {
            SvcStateTradeData userSvcTradeData = userSvcStateList.get(j);
            String userSvcState = userSvcTradeData.getStateCode();
            String endDate = userSvcTradeData.getEndDate();
            
            Date d1, d2;
            // 将输入数据 String 转为 Date 类型以便下面作日期比较
            d1 = SysDateMgr.string2Date(SysDateMgr.END_TIME_FOREVER_2030, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s2); //开始时间
            d2 = SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s3); //结束时间

            if ("4".equals(userSvcState) && d2.compareTo(d1) > 0)
            {
            	String str = "";
            	if("1".equals(operType)){
            		str = "宽带用户属于局方停机状态，不给予连带开机，需要局方开机！";
            	}else if("2".equals(operType)){
            		str = "宽带用户属于局方停机状态，不给予连带修改宽带状态，需要局方开机！";
            	}
                CSAppException.apperr(CrmUserException.CRM_USER_783, str);
            }
        }
    }
}
