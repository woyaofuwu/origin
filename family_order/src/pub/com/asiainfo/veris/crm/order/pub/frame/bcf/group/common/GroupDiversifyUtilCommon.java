
package com.asiainfo.veris.crm.order.pub.frame.bcf.group.common;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class GroupDiversifyUtilCommon
{

    /**
     * 根据用户结账期信息获取用户的账期分布标志 true 出账日为目标出账日 false-true 下账期出账日为目标出账日 true-false 当前账期的出账日为目标出账日
     * false-false用户的出账日非目标出账日，并且存在未生效的账期变更 false 用户的出账日非目标出账日
     * 
     * @param userAcctDay
     * @param inDay
     * @return
     */

    public static String getUserAcctDayDistribute(IData userAcctDay, String inDay)
    {
        // 判断用户账期信息和指定的结账日是否有值
        if (IDataUtil.isEmpty(userAcctDay) || StringUtils.isBlank(inDay))
            return "";

        // 本账期结账日
        String acctDay = userAcctDay.getString("ACCT_DAY");

        // 下账期结账日,如果下账期结账日不存在,则下账期结账日为本账期结账日
        String nextAcctDay = userAcctDay.getString("NEXT_ACCT_DAY");
        if (StringUtils.isBlank(nextAcctDay))
            nextAcctDay = acctDay;

        if (inDay.equals(acctDay) && inDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.TRUE.getValue();
        }

        if (!inDay.equals(acctDay) && inDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue();
        }

        if (inDay.equals(acctDay) && !inDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.TRUE_FALSE.getValue();
        }

        if (!inDay.equals(acctDay) && !inDay.equals(nextAcctDay) && !acctDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE_FALSE.getValue();
        }

        if (!inDay.equals(acctDay) && !inDay.equals(nextAcctDay) && acctDay.equals(nextAcctDay))
        {
            return GroupBaseConst.UserDaysDistribute.FALSE.getValue();
        }

        return "";
    }
}
