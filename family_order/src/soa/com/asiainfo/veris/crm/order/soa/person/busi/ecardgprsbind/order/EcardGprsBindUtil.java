
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.requestdata.ECardGprsBindReqData;

public class EcardGprsBindUtil
{

    /************************************************************************************************
     * 如果业务中2个号码均是分散用户，起始时间继续向后偏移一个账期 注意：将2个号码中账期日较大的下账期传入
     * 
     * @param firstDayNextAcct
     * @return
     * @throws Exception
     */
    private String bothDiversityStartDate(String firstDayNextAcct) throws Exception
    {
        // 将YYYMMDD转换为YYYY-MM-DD格式
        if (firstDayNextAcct.length() == 8)
            firstDayNextAcct = firstDayNextAcct.substring(0, 4) + "-" + firstDayNextAcct.substring(4, 6) + "-" + firstDayNextAcct.substring(6, 8);

        String startDate = null;
        // 如果不是1号 则需要进行变更，然后获取下账期初
        String chgAcctDay = "1";
        String sysDate = SysDateMgr.getSysTime();
        // 如果当前时间小于下账期初，即还在本账期内
        if (firstDayNextAcct.compareTo(sysDate) > 0)
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(firstDayNextAcct, Integer.parseInt(chgAcctDay));
        else
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(sysDate, Integer.parseInt(chgAcctDay));
        // 获取首次结账日 likai3 获取下一面的时间为开始时间未完成
        // startDate = DiversifyAcctUtil.get
        // DiversifyAcctUtil.getDateAddSec(startDate);

        return startDate.substring(0, 10).replaceAll("-", "");
    }

    public IData checkAfterNewSnInfo(BusiTradeData btd, String userIdA) throws Exception
    {
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        IData returnInfo = new DataMap();

        // 1.根据USERID获取用户账期相关信息
        IData userAcctDayinfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(ucaDta.getUserId());
        // 提示信息
        StringBuilder warnInfo = null;
        // 1.1 获取账期数据出错
        if (userAcctDayinfo == null)
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取号码" + reqData.getE_serial_number() + "账期无数据！");

        // 1.2存在预约账期
        if (!"".equals(userAcctDayinfo.getString("NEXT_ACCT_DAY", "")))
        {
            warnInfo = new StringBuilder(68);
            warnInfo.append("号码").append(reqData.getE_serial_number()).append("存在预约的帐期，").append("账期生效时间为").append(userAcctDayinfo.getString("NEXT_FIRST_DATE")).append("，账期生效后才能办理随E行捆绑业务！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, warnInfo.toString());
        }

        String targetAcctDay = userAcctDayinfo.getString("ACCT_DAY");

        // 判断目标号码和操作号码是不是自然月;若是自然月赋值为false，不用进行提示
        boolean targetFlag = "1".equals(targetAcctDay) ? false : true;
        boolean operFlag = "1".equals(ucaDta.getAcctDay()) ? false : true;

        // 2.如果2个号码结账日不相同，发生了被动账期变更，那么开始时间为下下个月月初
        String startDate = getDiversityStartDate(targetAcctDay, userIdA);

        if (ucaDta.getNextAcctDayStartDate() != null && Integer.parseInt(startDate) < Integer.parseInt(ucaDta.getNextAcctDayStartDate()))
        {
            startDate = ucaDta.getNextAcctDayStartDate();
        }
        // 3.如果2个号码均是分散号码，开始时间需要重新处理
        if (!"1".equals(ucaDta.getAcctDay()) && !"1".equals(targetAcctDay))
            startDate = bothDiversityStartDate(startDate);
        /*
         * 此行代码不能写在上面条件判断里，否则在结账日不同的副卡校验时，提示信息将被覆盖; 如：主号码为1号，先测试了5号的号码，此时startDate为下下个月月初，如果此时换成1号号码了， 那么开始时间应该为本月初；
         */

        // 2. 展示提示信息
        // 2.1 如果2个号码都是非自然月
        if (targetFlag && operFlag)
        {
            warnInfo = new StringBuilder(115).append(" 号码");
            warnInfo.append(ucaDta.getSerialNumber()).append("结账日为：").append(ucaDta.getAcctDay()).append("号；号码").append(reqData.getE_serial_number()).append("结账日为：").append(targetAcctDay).append("号；");
            // 2.2 如果操作号码不是自然月账期
        }
        else if (operFlag)
        {
            warnInfo = new StringBuilder(95).append(" 号码").append(ucaDta.getSerialNumber()).append("结账日为：").append(ucaDta.getAcctDay()).append("号；");
            // 2.3 如果目标合账号码不是自然月账期
        }
        else if (targetFlag)
        {
            warnInfo = new StringBuilder(95).append(" 号码").append(reqData.getE_serial_number()).append("结账日为：").append(targetAcctDay).append("号；");
        }

        // 如果2个号码中存在号码不为自然月账期
        if (warnInfo != null)
        {
            warnInfo.append("办理随E行捆绑业务后结账日会改为1号，").append(startDate).append("开始生效，是否继续？");
            returnInfo.put("WARN_MSG", warnInfo.toString());// 提示警告信息
        }
        returnInfo.put("startDate", startDate);

        return returnInfo;
    }

    private String getDiversityStartDate(String acctDay, String userId) throws Exception
    {
        String startDate = null;
        // 如果是1号，立即生效[本账期初]
        if ("1".equals(acctDay))
        {

            startDate = AcctDayDateUtil.getFirstDayThisAcct(userId);
            // 分散用户需要进行账期变更，默认为下账期生效
        }
        else
        {
            startDate = DiversifyAcctUtil.getFirstDayNextAcct(userId);
        }
        return startDate.replaceAll("-", "");
    }

}
