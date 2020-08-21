
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.Date;

import com.ailk.common.data.IData; 
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean; 

public class GroupCycleUtil extends CSBizBean
{ 

    /**
     * 根据偏移量计算账期时间
     * 
     * @param date
     *            传入时间
     * @param acctOffset
     *            偏移量
     * @param acctDay
     *            本账期结账日
     * @param firstDate
     *            首次结账日
     * @param bookAcctDay
     *            预约账期结账日
     * @param bookFirstDate
     *            预约账期首次结账日
     * @return
     * @throws Exception
     */
    public static String addAcctDayByOffset(String date, int acctOffset, String acctDay, String firstDate, String bookAcctDay, String bookFirstDate) throws Exception
    {
        if (acctOffset == 0)
        {
            return date;
        }

        // 无预约账期
        if (StringUtils.isEmpty(bookAcctDay))
        {
            bookAcctDay = acctDay;
            bookFirstDate = firstDate;
        }

        boolean isNext = false;

        String nowDate = SysDateMgr.getSysDate();

        String nowAcctLastDay = SysDateMgr.getLastDayThisAcct(nowDate, acctDay, firstDate);

        if (date.compareTo(nowAcctLastDay) > 0)
        {
            isNext = true;
        }

        if (isNext)
        {
            // 传过来的日期已经处于下帐期内
            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = SysDateMgr.getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                return firstDateNextAcctDay;
            }
            else
            {
                // 偏移值大于1
                String firstDateNextAcctDay = SysDateMgr.getFirstDayNextAcct(date, bookAcctDay, bookFirstDate);
                Date firstDateNextAcctDayDate = SysDateMgr.string2Date(firstDateNextAcctDay, "yyyy-MM-dd");
                return SysDateMgr.date2String(DateUtils.addMonths(firstDateNextAcctDayDate, acctOffset - 1), "yyyy-MM-dd");

            }
        }
        else
        {
            // 传递过来的日期仍然在本帐期内
            if (acctOffset == 1)
            {
                String firstDateNextAcctDay = SysDateMgr.getFirstDayNextAcct(date, acctDay, firstDate);
                return firstDateNextAcctDay;
            }
            else
            {
                // 偏移值大于1
                String firstDateNextAcctDay = SysDateMgr.getFirstDayNextAcct(date, acctDay, firstDate);

                String nextNextAcctDay = SysDateMgr.getFirstDayNextAcct(firstDateNextAcctDay, bookAcctDay, bookFirstDate);

                Date nextNextAcctDayDate = SysDateMgr.string2Date(nextNextAcctDay, "yyyy-MM-dd");

                return SysDateMgr.date2String(DateUtils.addMonths(nextNextAcctDayDate, acctOffset - 2), "yyyy-MM-dd");
            }
        }
    }

    /**
     * 处理账期方法, 由账期六位变为八位
     * 
     * @param map
     * @throws Exception
     */
    public static void dealPayRelaCycle(IData map) throws Exception
    { 
        String startCycleId = map.getString("START_CYCLE_ID", "");
        String endCycleId = map.getString("END_CYCLE_ID", "");

        String nowDate = SysDateMgr.getSysDate();
        String nowCycleId = StringUtils.replaceChars(nowDate, "-", "").substring(0, 6);

        // 获取账期信息
        AcctTimeEnv acctTimeEnv = AcctTimeEnvManager.getAcctTimeEnv();

        String acctDay = acctTimeEnv.getAcctDay();// 本账期结账日
        String firstDate = acctTimeEnv.getFirstDate();// 首次出账时间
        String startDate = acctTimeEnv.getStartDate();// 本账期开始时间

        String nextAcctDay = acctTimeEnv.getNextAcctDay();// 下账期结账日
        String nextFirstDate = acctTimeEnv.getNextFirstDate();// 下账期出账时间

        int months = 0;

        // 处理开始账期
        if (startCycleId.length() == 6)
        {
            months = SysDateMgr.monthIntervalYYYYMM(nowCycleId, startCycleId);

            if (months < 0)// 如果开始账期startCycleId在本月前则不再推算对应的新账期, 直接扩展+"01"
            {
                startCycleId = startCycleId + "01";
            }
            else if (months == 0)// 如果开始账期startCycleId为本月, 则需要替换为当前账期
            {
                startCycleId = SysDateMgr.getFirstCycleDayThisAcct(nowDate, acctDay, firstDate, startDate);
            }
            else
            {
                startCycleId = addAcctDayByOffset(nowDate, months, acctDay, firstDate, nextAcctDay, nextFirstDate);
            }

            startCycleId = StringUtils.replaceChars(startCycleId, "-", "");
        }

        // 处理结束账期
        if (endCycleId.length() == 6)
        {

            months = SysDateMgr.monthIntervalYYYYMM(nowCycleId, endCycleId);

            if (endCycleId.compareTo("203301") >= 0)
            {
                endCycleId = SysDateMgr.END_TIME_FOREVER;
            }
            else if (months < -1)// 如果账期结束时间点endCycleId在一个月前则不再推算对应的新账期，直接扩展+‘31’
            {
                endCycleId = SysDateMgr.addDays(endCycleId + acctDay, -1);
            }
            else if (months == -1)// 如果账期结束时间点为上个月, 则需要替换为前一个账期的结束时间
            {
                String firstDayThisAcct = SysDateMgr.getFirstCycleDayThisAcct(nowDate, acctDay, firstDate, startDate);

                endCycleId = SysDateMgr.addDays(firstDayThisAcct, -1);
            }
            else
            {
                String tempCycleId = addAcctDayByOffset(nowDate, months + 1, acctDay, firstDate, nextAcctDay, nextFirstDate);
                endCycleId = SysDateMgr.addDays(tempCycleId, -1);
            }

            endCycleId = StringUtils.replaceChars(endCycleId, "-", "");
        }
        String payitemcodeStr = map.getString("PAYITEM_CODE", "");
 //对学护卡反向订购账期做特殊处理，相关问题： ORA-01722: 无效数字     START_CYCLE_ID = [20160813 00:00:00]
        if("41000".equals(payitemcodeStr) )    	map.put("START_CYCLE_ID", startCycleId.substring(0, 8));        
        else                                       map.put("START_CYCLE_ID", startCycleId); // 起始帐期         
        map.put("END_CYCLE_ID", endCycleId); // 终止帐期 
         
        
    }
}
