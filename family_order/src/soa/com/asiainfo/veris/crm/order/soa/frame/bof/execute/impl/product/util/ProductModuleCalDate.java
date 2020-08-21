
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

public class ProductModuleCalDate
{
    public static String calCancelDate(ProductModuleData elementData, ProductTimeEnv env) throws Exception
    {
        String cancelTag = elementData.getCancelTag();
        String cancelAbsoluteDate = elementData.getCancelAbsoluteDate();
        String cancelOffset = elementData.getCancelOffSet();
        String cancelUnit = elementData.getCancelUnit();
        
        if("6".equals(cancelTag))
        {
        	 return SysDateMgr.getAddMonthsLastDay(-1,SysDateMgr.getSysDate());
        	
        }
        // 海南特殊处理CANCEL_TAG=5 当预约情况且当绝对开始时间所在DAY与用户本账期DAY相等时候 为所在绝对时间的上账期末 否则 结束时间为绝对开始时间所在账期末
        if (StringUtils.isNotBlank(cancelTag) && "5".equals(cancelTag))
        {
            String firstDayThisMonth = SysDateMgr.getFirstDayOfNextMonth();

            if (env != null && StringUtils.isNotBlank(env.getBasicAbsoluteStartDate()))
            {
                if (isBookingChange(env.getBasicAbsoluteStartDate()) && SysDateMgr.getIntDayByDate(firstDayThisMonth) == SysDateMgr.getIntDayByDate(env.getBasicAbsoluteStartDate()))
                {
                    return SysDateMgr.getAddMonthsLastDay(-1, env.getBasicAbsoluteStartDate()).substring(0, 10) + SysDateMgr.getEndTime235959();
                }
                // 非预约情况 或者 为预约且预约DAY与结账日DAY不相等
                else
                {
                    return SysDateMgr.getAddMonthsLastDay(1, env.getBasicAbsoluteStartDate());
                }
            }
            // 相当于CANCEL_TAG=3的情况
            else
            {
                return SysDateMgr.getLastDateThisMonth();
            }
        }
        if (StringUtils.isNotBlank(cancelTag) && "4".equals(cancelTag))//时间还是返回原来元素的结束时间 相当于什么都不做
        {
            return elementData.getEndDate().substring(0, 10) + SysDateMgr.END_DATE;
        }
        if (env != null && !StringUtils.isBlank(env.getBasicAbsoluteCancelDate()))
        {
            return env.getBasicAbsoluteCancelDate();
        }
        if (StringUtils.isBlank(cancelTag))
        {
            return SysDateMgr.getLastDateThisMonth();
        }
        return SysDateMgr.cancelDate(cancelTag, cancelAbsoluteDate, cancelOffset, cancelUnit);
    }

    public static String calEndDate(ProductModuleData elementData, String startDate) throws Exception
    {
        String endDate = elementData.getEndDate();
        if (StringUtils.isNotBlank(endDate))
        {
            return endDate;
        }
        String endEnableTag = elementData.getEndEnableTag();
        String endAbsoluteEndDate = elementData.getEndAbsoluteDate();
        String endOffset = elementData.getEndOffSet();
        String endUnit = elementData.getEndUnit();
        //REQ201603170019  流量快餐业务开发需求 新增END_UNIT=6类型的失效偏移单位，为了不影响原有逻辑，新增if判断 by songlm 20160323
        if("6".equals(endUnit))
        {
        	return SysDateMgr.getAddHoursDate(startDate,Integer.parseInt(endOffset));
        }else
        {
        	return SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteEndDate, endOffset, endUnit);
        }
        //end
    }

    public static String calStartDate(ProductModuleData elementData, ProductTimeEnv env) throws Exception
    {
        String startDate = elementData.getStartDate();
        if (StringUtils.isNotBlank(startDate))
        {
            return startDate;
        }
        String enableTag = elementData.getEnableTag();
        String absoluteStartDate = elementData.getStartAbsoluteDate();
        String startOffset = elementData.getStartOffset();
        String startUnit = elementData.getStartUnit();
        if ("5".equals(enableTag) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementData.getElementType()))
        {
            return SysDateMgr.startDateOffset(SysDateMgr.getFirstDayOfNextMonth(), startOffset, startUnit);
        }
        else if ("6".equals(enableTag) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementData.getElementType()))
        {
        	return SysDateMgr.getFirstDayOfThisMonth();
        	
        }
        if (env != null && !StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
        {
            return env.getBasicAbsoluteStartDate();
        }
        else
        {
            if (env != null && !StringUtils.isBlank(env.getBasicCalStartDate()))
            {
                startDate = SysDateMgr.startDateBook(env.getBasicCalStartDate(), enableTag, absoluteStartDate, startOffset, startUnit);
            }
            else
            {
                startDate = SysDateMgr.startDate(enableTag, absoluteStartDate, startOffset, startUnit);
            }
            return startDate;
        }
    }

    /**
     * 是否是预约变更 【是预约：true】
     * 
     * @param bookingDate
     * @return
     * @throws Exception
     */
    public static boolean isBookingChange(String bookingDate) throws Exception
    {
        if (StringUtils.isNotBlank(bookingDate) && SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND_YYYYMMDD).compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD)) > 0)
        {
            return true;
        }

        return false;
    }
}
