
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import java.util.Random;
import java.util.Vector;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecLotteryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class UECLotteryGeneralSVC extends CSBizService
{

    private byte[] lock = new byte[0];

    /**
     * 录入抽奖记录
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    private boolean addLottery(IData data)
    {
        // TODO Auto-generated method stub
        try
        {
            Dao.insert("TM_O_UECLOTTERYTIME", data);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            // 更新失败，说明已经有一条记录存在了
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 电子渠道抽奖权限校验(通用)
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkLottryRight(IData data) throws Exception
    {
        IData paramMap = getParamMap(data);
        IDataUtil.chkParam(data, "ACTIVITY_NUMBER");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "IN_MODE_CODE");

        supplyUserInfo(data);

        String startTime = paramMap.getString("START_TIME", "2011-01-01");
        String endTime = paramMap.getString("END_TIME", "2011-12-31");

        // 活动未开始
        
        if (SysDateMgr.getTimeDiff(startTime, SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD) < 0)
        {
            data.put("X_RESULTCODE", "100002");
            data.put("X_RESULTINFO", "活动还未开始");
            data.put("X_RSPTYPE", "2");
            data.put("X_RSPCODE", "2998");
            return data;
        }
        if (SysDateMgr.getTimeDiff(endTime, SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD) > 0)
        {
            data.put("X_RESULTCODE", "100003");
            data.put("X_RESULTINFO", "活动已经结束");
            data.put("X_RSPTYPE", "2");
            data.put("X_RSPCODE", "2998");
            return data;
        }

        if (StringUtils.isNotEmpty(paramMap.getString("CHECKRIGHT_PROC_NAME", "")))
        {
            data = checkLottryRightByProc(data);
        }
        else if (StringUtils.isNotEmpty(paramMap.getString("CHECKRIGHT_INTERFACE_NAME", "")))
        {
            data = checkLottryRightByInterface(data);
        }
        else
        {
            data.put("X_RESULTCODE", "0");
            data.put("X_RESULTINFO", "此活动不需要进行资格校验");
        }

        return data;
    }

    private IData checkLottryRightByInterface(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData paramMap = getParamMap(data);
        String interfaceName = paramMap.getString("CHECKRIGHT_INTERFACE_NAME", "");

        if (StringUtils.isEmpty(interfaceName))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未设置活动资格校验接口");
        }
        // 修改td_s_static DATA_ID=CHECKRIGHT_INTERFACE_NAME 配置 !@#

        IData result = CSAppCall.call(interfaceName.trim(), data).getData(0);
        String tmpXResultCode = result.getString("X_RESULTCODE", "0");
        String tmpRemainNum = result.getString("REMAIN_NUM", "");
        data.put("REMAIN_NUM", tmpRemainNum);
        data.put("X_RESULTCODE", tmpXResultCode);
        data.put("X_RESULTINFO", "资格校验");
        // 接口返回操作码为0的情况下，还需要判断可用抽奖次数是否为0
        if ("".equals(tmpXResultCode) || "0".equals(tmpXResultCode))
        {
            if ("".equals(tmpRemainNum) || "0".equals(tmpRemainNum))
            {
                data.put("X_RESULTCODE", "800100");
                data.put("X_RESULTINFO", "没有抽奖机会");
                data.put("X_RSPTYPE", "2");
                data.put("X_RSPCODE", "2998");
                data.put("REMAIN_NUM", "0");
            }
        }
        data.put("CHECK_RESULT", result);
        return data;
    }

    private IData checkLottryRightByProc(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData paramMap = getParamMap(data);
        String procName = paramMap.getString("CHECKRIGHT_PROC_NAME", "");
        if (StringUtils.isEmpty(procName))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未设置活动资格校验存储过程");
        }

        IData param = new DataMap();
        Vector<String> ve = new Vector<String>();
        param.put("in_v_acitiy_number", data.get("ACTIVITY_NUMBER"));
        ve.add("in_v_acitiy_number");
        param.put("in_v_user_id", data.get("USER_ID"));
        ve.add("in_v_user_id");
        if (StringUtils.isNotEmpty(data.getString("REVC1")))
        {
            param.put("in_v_revc1", data.getString("REVC1", ""));
            ve.add("in_v_revc1");
        }
        if (StringUtils.isNotEmpty(data.getString("REVC2")))
        {
            param.put("in_v_revc2", data.getString("REVC2", ""));
            ve.add("in_v_revc2");
        }
        if (StringUtils.isNotEmpty(data.getString("REVC3")))
        {
            param.put("in_v_revc3", data.getString("REVC3", ""));
            ve.add("in_v_revc3");
        }
        if (StringUtils.isNotEmpty(data.getString("REVC4")))
        {
            param.put("in_v_revc4", data.getString("REVC4", ""));
            ve.add("in_v_revc4");
        }
        if (StringUtils.isNotEmpty(data.getString("REVC5")))
        {
            param.put("in_v_revc5", data.getString("REVC5", ""));
            ve.add("in_v_revc5");
        }
        ve.add("ov_v_resultcode");
        ve.add("ov_v_resultinfo");
        ve.add("ov_v_remainnum");
        String inParam[] = ve.toArray(new String[]
        {});
        Dao.callProc(procName, inParam, param);
        data.put("X_RESULTCODE", param.get("ov_v_resultcode"));
        data.put("X_RESULTINFO", param.get("ov_v_resultinfo"));
        data.put("REMAIN_NUM", param.get("ov_v_remainnum"));
        data.put("CHECK_RESULT", param);
        return data;
    }

    private IData dealPreBusi(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData paramMap = getParamMap(data);
        String interfaceName = paramMap.getString("PREBUSI_INTERFACE_NAME", "");
        IData result = new DataMap();
        if ("".equals(interfaceName))
        {
            data.put("X_RESULTCODE", "0");
            data.put("X_RESULTINFO", "此活动不需要进行前置业务办理");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "此活动不需要进行前置业务办理");
            data.put("PREBUSI_RESULT", result);
            return data;
        }

        IDataset results = CSAppCall.call(interfaceName, data);

        if (IDataUtil.isEmpty(results))
        {
            IData ida = results.getData(0);

            data.put("X_RESULTCODE", "800400");
            data.put("X_RESULTINFO", "办理前置业务失败");
            data.put("X_RSPTYPE", "2");
            data.put("X_RSPCODE", "2998");
            result.put("X_RESULTCODE", "800400");
            result.put("X_RESULTINFO", "办理前置业务失败");

            data.put("PREBUSI_RESULT", ida);
        }

        return data;
    }

    /**
     * 删除抽奖记录
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    private void delLottery(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder(1000);
        sql.append("DELETE FROM TM_O_UECLOTTERYTIME WHERE ACTIVITY_NUMBER = :ACTIVITY_NUMBER AND SERIAL_NUMBER = :SERIAL_NUMBER");
        Dao.executeUpdate(sql, data);
    }

    private IData getParamMap(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData paramMap = new DataMap();
        String activity_number = data.getString("ACTIVITY_NUMBER");

        IDataset ids = StaticUtil.getStaticListByParent("UECLOTTERY_PARAM" + activity_number, activity_number);
        for (int i = 0, s = ids.size(); i < s; i++)
        {
            IData result = ids.getData(i);
            paramMap.put(result.getString("DATA_ID"), result.getString("DATA_NAME"));
        }

        return paramMap;
    }

    /**
     * 电子渠道抽奖办理(通用)
     * 
     * @throws Exception
     */
    public IData lottery(IData data) throws Exception
    {
        IData result = new DataMap();
        IData paramMap = getParamMap(data);

        String inModeCode = data.getString("IN_MODE_CODE");// 防止接口调用过程中删除INMODECODE信息
        // 抽奖资格校验
        IData checkResult = checkLottryRight(data);
        if (!"0".equals(checkResult.getString("X_RESULTCODE")))
        {
            return checkResult;
        }
        else
        {
            result.put("CHECK_RESULT", data.getData("CHECK_RESULT"));
        }

        // 抽奖需办理前置业务
        IData beforeLotteryResult = dealPreBusi(data);
        if (!"0".equals(beforeLotteryResult.getString("X_RESULTCODE")))
        {
            return beforeLotteryResult;
        }
        else
        {
            result.put("PREBUSI_RESULT", data.getData("PREBUSI_RESULT"));
        }
        data.put("IN_MODE_CODE", inModeCode);

        // 增加抽奖记录
        if (!addLottery(data))
        {
            data.put("X_RESULTCODE", "800800");
            data.put("X_RESULTINFO", "正在参与活动，稍后再试");
            data.put("X_RSPTYPE", "2");
            data.put("X_RSPCODE", "2998");
            return data;
        }
        String detailTime = UecLotteryInfoQry.getSysDateAndMonth(paramMap.getString("TIME_LIMIT"));
        data.put("DETAIL_TIME", detailTime.substring(0, 14));
        data.put("ACCEPT_DATE", detailTime.substring(0, 14));
        IDataset results = UecLotteryInfoQry.qryLotteryPrizelist(data.getString("ACTIVITY_NUMBER"), detailTime, data.getString("CITY_CODE"));

        // 可抽中的奖项一数量
        int prizeNumber1 = Integer.parseInt(results.getData(0).getString("PRIZE_1", "0"));
        // 可抽中的奖项二数量
        int prizeNumber2 = Integer.parseInt(results.getData(0).getString("PRIZE_2", "0"));
        // 可抽中的奖项三数量
        int prizeNumber3 = Integer.parseInt(results.getData(0).getString("PRIZE_3", "0"));
        // 可抽中的奖项四数量
        int prizeNumber4 = Integer.parseInt(results.getData(0).getString("PRIZE_4", "0"));
        // 可抽中的奖项五数量
        int prizeNumber5 = Integer.parseInt(results.getData(0).getString("PRIZE_5", "0"));
        // 可抽中的奖项六数量
        int prizeNumber6 = Integer.parseInt(results.getData(0).getString("PRIZE_6", "0"));

        boolean winFlag = false;

        // 抽奖方式 SEQUENCE：顺序取数方式 RANDOM：随机数方式
        String lotteryType = paramMap.getString("LOTTERY_TYPE", "RANDOM");

        // 抽奖
        if (prizeNumber1 != 0 || prizeNumber2 != 0 || prizeNumber3 != 0 || prizeNumber4 != 0 || prizeNumber5 != 0 || prizeNumber6 != 0)
        {
            // 在最后时间内不考虑中奖概率，先到先得
            if ("0".equals(detailTime.substring(24, 25)))
            {
                if (!winFlag && prizeNumber1 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "1");
                    result.put("X_RESULTINFO", "抽中奖项一");
                    result.put("LEVEL", "1");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber2 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "2");
                    result.put("X_RESULTINFO", "抽中奖项二");
                    result.put("LEVEL", "2");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber3 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "3");
                    result.put("X_RESULTINFO", "抽中奖项三");
                    result.put("LEVEL", "3");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber4 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "4");
                    result.put("X_RESULTINFO", "抽中奖项四");
                    result.put("LEVEL", "4");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber5 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "5");
                    result.put("X_RESULTINFO", "抽中奖项五");
                    result.put("LEVEL", "5");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber6 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "6");
                    result.put("X_RESULTINFO", "抽中奖项六");
                    result.put("LEVEL", "6");
                    winFlag = true;
                }
            }
            else
            {
                // 根据抽奖方式进行抽奖
                if ("RANDOM".equals(lotteryType))
                {
                    int radix = Integer.parseInt(paramMap.getString("RADIX", "1000000"));
                    int prizeOdds1 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_1", "0"));
                    int prizeOdds2 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_2", "0"));
                    int prizeOdds3 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_3", "0"));
                    int prizeOdds4 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_4", "0"));
                    int prizeOdds5 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_5", "0"));
                    int prizeOdds6 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_6", "0"));

                    Random random = new Random();
                    int usrRandom = random.nextInt(radix) + 1;
                    data.put("RANDOM_NUM", usrRandom);

                    if (!winFlag && usrRandom <= prizeOdds1 && prizeNumber1 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "1");
                        result.put("X_RESULTINFO", "抽中奖项一");
                        result.put("LEVEL", "1");
                        winFlag = true;
                    }
                    else if (!winFlag && usrRandom <= prizeOdds2 && prizeNumber2 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "2");
                        result.put("X_RESULTINFO", "抽中奖项二");
                        result.put("LEVEL", "2");
                        winFlag = true;
                    }
                    else if (!winFlag && usrRandom <= prizeOdds3 && prizeNumber3 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "3");
                        result.put("X_RESULTINFO", "抽中奖项三");
                        result.put("LEVEL", "3");
                        winFlag = true;
                    }
                    else if (!winFlag && usrRandom <= prizeOdds4 && prizeNumber4 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "4");
                        result.put("X_RESULTINFO", "抽中奖项四");
                        result.put("LEVEL", "4");
                        winFlag = true;
                    }
                    else if (!winFlag && usrRandom <= prizeOdds5 && prizeNumber5 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "5");
                        result.put("X_RESULTINFO", "抽中奖项五");
                        result.put("LEVEL", "5");
                        winFlag = true;
                    }
                    else if (!winFlag && usrRandom <= prizeOdds6 && prizeNumber6 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "6");
                        result.put("X_RESULTINFO", "抽中奖项六");
                        result.put("LEVEL", "6");
                        winFlag = true;
                    }
                }
                else if ("SEQUENCE".equals(lotteryType))
                {
                    String radix = paramMap.getString("RADIX", "1000000");
                    String prizeOdds1 = paramMap.getString("PRIZE_ODDS_1", "N");
                    String prizeOdds2 = paramMap.getString("PRIZE_ODDS_2", "N");
                    String prizeOdds3 = paramMap.getString("PRIZE_ODDS_3", "N");
                    String prizeOdds4 = paramMap.getString("PRIZE_ODDS_4", "N");
                    String prizeOdds5 = paramMap.getString("PRIZE_ODDS_5", "N");
                    String prizeOdds6 = paramMap.getString("PRIZE_ODDS_6", "N");

                    String usrSequence = SeqMgr.getLotterySeq();
                    data.put("RANDOM_NUM", usrSequence);

                    if (!winFlag && usrSequence.endsWith(prizeOdds1))
                    {
                        data.put("PRIZE_TYPE_CODE", "1");
                        result.put("X_RESULTINFO", "抽中奖项一");
                        result.put("LEVEL", "1");
                        winFlag = true;
                    }
                    else if (!winFlag && usrSequence.endsWith(prizeOdds2))
                    {
                        data.put("PRIZE_TYPE_CODE", "2");
                        result.put("X_RESULTINFO", "抽中奖项二");
                        result.put("LEVEL", "2");
                        winFlag = true;
                    }
                    else if (!winFlag && usrSequence.endsWith(prizeOdds3))
                    {
                        data.put("PRIZE_TYPE_CODE", "3");
                        result.put("X_RESULTINFO", "抽中奖项三");
                        result.put("LEVEL", "3");
                        winFlag = true;
                    }
                    else if (!winFlag && usrSequence.endsWith(prizeOdds4))
                    {
                        data.put("PRIZE_TYPE_CODE", "4");
                        result.put("X_RESULTINFO", "抽中奖项四");
                        result.put("LEVEL", "4");
                        winFlag = true;
                    }
                    else if (!winFlag && usrSequence.endsWith(prizeOdds5))
                    {
                        data.put("PRIZE_TYPE_CODE", "5");
                        result.put("X_RESULTINFO", "抽中奖项五");
                        result.put("LEVEL", "5");
                        winFlag = true;
                    }
                    else if (!winFlag && usrSequence.endsWith(prizeOdds6))
                    {
                        data.put("PRIZE_TYPE_CODE", "6");
                        result.put("X_RESULTINFO", "抽中奖项六");
                        result.put("LEVEL", "6");
                        winFlag = true;
                    }

                }
                else
                {
                    // TODO:其他抽奖方式
                }
            }
        }

        if (!winFlag)
        {
            data.put("PRIZE_TYPE_CODE", "0");
            data.put("DEAL_FLAG", "0");
            result.put("X_RESULTCODE", "800200");
            result.put("X_RESULTINFO", "没有中奖");
            result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
        }
        else
        {
            data.put("DEAL_FLAG", "1");
            data.put("EXEC_FLAG", "0");
            result.put("X_RESULTCODE", "0");
        }
        synchronized (lock)
        {
            if (winFlag)
            {
                // 更新奖项设置
                if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET", data) == 0)
                {
                    // 如果更新1等奖数量失败，就更新2等奖数量，并重置中奖信息，以此类推
                    int startPrizeCode = Integer.parseInt(data.getString("PRIZE_TYPE_CODE", "6")) + 1;
                    boolean updateFlag = false;
                    for (int i = startPrizeCode; i <= 6; i++)
                    {
                        data.put("PRIZE_TYPE_CODE", i);
                        result.put("X_RESULTINFO", "抽中奖项" + i);
                        result.put("LEVEL", i);
                        if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET", data) > 0)
                        {
                            updateFlag = true;
                            break;
                        }
                    }

                    if (!updateFlag)
                    {
                        data.put("EXEC_FLAG", "");
                        data.put("DEAL_FLAG", "0");
                        data.put("PRIZE_TYPE_CODE", "0");

                        result.put("X_RESULTCODE", "800200");
                        result.put("X_RESULTINFO", "没有中奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                        result.remove("LEVEL");
                    }
                }
            }
            // 特殊处理用户抽奖序列号
            if ("SEQUENCE".equals(lotteryType))
            {
                String radix = paramMap.getString("RADIX", "1000000");
                String prizeOdds1 = paramMap.getString("PRIZE_ODDS_1", "N");
                String prizeOdds2 = paramMap.getString("PRIZE_ODDS_2", "N");
                String prizeOdds3 = paramMap.getString("PRIZE_ODDS_3", "N");
                String prizeOdds4 = paramMap.getString("PRIZE_ODDS_4", "N");
                String prizeOdds5 = paramMap.getString("PRIZE_ODDS_5", "N");
                String prizeOdds6 = paramMap.getString("PRIZE_ODDS_6", "N");

                String usrSequence = data.getString("RANDOM_NUM", "");

                if (!"N".equals(result.getString("LEVEL", "N")))
                {
                    // 用户中奖但是没有序列号的情况(最后时刻百分之百中奖)
                    // 用户中奖，奖项等级与序列号不符合。
                    String level = data.getString("LEVEL", "0");

                    if ("1".equals(level))
                    {
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds1))
                        {
                            usrSequence = "500800" + prizeOdds1;
                        }
                    }
                    else if ("2".equals(level))
                    {
                        usrSequence = "500800" + prizeOdds2;
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds2))
                        {
                            usrSequence = "500800" + prizeOdds2;
                        }
                    }
                    else if ("3".equals(level))
                    {
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds3))
                        {
                            usrSequence = "500800" + prizeOdds3;
                        }
                    }
                    else if ("4".equals(level))
                    {
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds4))
                        {
                            usrSequence = "500800" + prizeOdds4;
                        }
                    }
                    else if ("5".equals(level))
                    {
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds5))
                        {
                            usrSequence = "500800" + prizeOdds5;
                        }
                    }
                    else if ("6".equals(level))
                    {
                        if ("".equals(usrSequence) || !usrSequence.endsWith(prizeOdds5))
                        {
                            usrSequence = "500800" + prizeOdds6;
                        }
                    }
                    else
                    {
                    }
                }
                else
                {
                    // 用户尾数中奖，但是因为没有奖品了，重新设置为未中奖,需要重置抽奖数
                    if (StringUtils.isEmpty(usrSequence))
                    {
                        usrSequence = SeqMgr.getLotterySeq();
                    }
                    while (true)
                    {
                        if (usrSequence.endsWith(prizeOdds1) || usrSequence.endsWith(prizeOdds2) || usrSequence.endsWith(prizeOdds3) || usrSequence.endsWith(prizeOdds4) || usrSequence.endsWith(prizeOdds5) || usrSequence.endsWith(prizeOdds6))
                        {
                            usrSequence = SeqMgr.getLotterySeq();
                        }
                        else
                        {
                            data.put("RANDOM_NUM", usrSequence);
                            break;
                        }
                    }
                }
            }
            result.put("LOTTERY_NUM", data.getString("RANDOM_NUM"));
            // 对特定奖项进行充值
            boolean rechargeFlag = false;
            String rechargeLvStr = paramMap.getString("RECHARGE_LV", "");
            String[] rechargeLv = rechargeLvStr.split(",");

            for (int i = 0, t = rechargeLv.length; i < t; i++)
            {
                if (rechargeLv[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
                {
                    data.put("EXEC_FLAG", "1");
                    data.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));
                    rechargeFlag = true;
                }
            }
            // 对特定奖项发送电子礼品
            boolean eGiftFlag = false;
            String eGiftLvStr = paramMap.getString("EGIFT_LV", "");
            String[] eGiftLv = eGiftLvStr.split(",");
            for (int i = 0; i < eGiftLv.length; i++)
            {
                if (eGiftLv[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
                {
                    data.put("EXEC_FLAG", "1");
                    data.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));
                    eGiftFlag = true;
                }
            }
            // 对特定奖项绑定营销活动
            boolean bindPackageFlag = false;
            String packageLvStr = paramMap.getString("PACKAGE_BIND_LV", "");
            String[] packageLv = packageLvStr.split(",");
            for (int i = 0; i < packageLv.length; i++)
            {
                if (packageLv[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
                {
                    data.put("EXEC_FLAG", "1");
                    data.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));
                    bindPackageFlag = true;
                }
            } 
            String tradeId = SeqMgr.getTradeId();
            data.put("TRADE_ID", tradeId);
            data.put("TRADE_TYPE_CODE", paramMap.getString("TRADE_TYPE_CODE", "305"));
            // 记录子台账
            recordSubTrade(data);
            // 记录台账
            recordHisMainTrade(data);
            // 是否充值
            if (rechargeFlag)
            {
                payFeeSubmit(data);
            }
            // 是否发送电子礼品
            if (eGiftFlag)
            {
                sendGift(data);
            }
            // 是否绑定营销活动
            if (bindPackageFlag)
            {
                bindPackage(data);
            }
            // 处理用户可抽奖次数
            int remainNumber = data.getInt("REMAIN_NUM", 0);
            if (remainNumber > 0)
            {
                remainNumber = remainNumber - 1;
            }
            else
            {
                remainNumber = 0;
            }
            result.put("REMAIN_NUM", remainNumber);

        }
        // 删除抽奖记录
        delLottery(data);

        return result;
    }

    public IDataset payFeeSubmit(IData data) throws Exception
    {

        IData paramMap = getParamMap(data);

        String rechargeLvStr = paramMap.getString("RECHARGE_LV", "");
        String rechargeConfigStr = paramMap.getString("RECHARGE_CONFIG", "").trim();
        String[] rechargeLvs = rechargeLvStr.split(",");
        String[] rechargeConfigs = rechargeConfigStr.split(",");
        if (rechargeLvs.length != rechargeConfigs.length)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数配置有误:RECHARGE_LV与RECHARGE_CONFIG不匹配");
        }
        String rechargeConfig = "";
        for (int i = 0, t = rechargeLvs.length; i < t; i++)
        {
            if (rechargeLvs[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
            {
                rechargeConfig = rechargeConfigs[i];
            }
        }
        String[] config = rechargeConfig.split("#");
        if (config == null || config.length != 4)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "充值参数配置有误");
        }

        /*
         * IData param = new DataMap(); IDataset ids = new DatasetList(); param.put("PROVINCE_CODE",
         * data.getString("PROVINCE_CODE", "")); param.put("TRADE_DEPART_ID", getVisit().getDepartId());
         * param.put("TRADE_STAFF_ID", getVisit().getStaffId()); param.put("TRADE_CITY_CODE", getVisit().getCityCode());
         * param.put("TRADE_EPARCHY_CODE", getTradeEparchyCode()); param.put("IN_MODE_CODE",
         * data.getString("IN_MODE_CODE", "")); param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));// 服务号码
         * param.put("WRITEOFF_MODE", "1");//'2'-按用户 '1'-按帐户 param.put("RECOVER_TAG", "0"); //复机标志
         * param.put("TRADE_FEE", config[0]); //收费金额 param.put("PAYMENT_ID", config[1]); //费用来源编码
         * param.put("PAY_FEE_MODE_CODE", config[2]); //收费方式 param.put("DEPOSIT_CODE", config[3]);
         * param.put("REMOVE_TAG", "0"); //销号标志 param.put("CHANNEL_ID", data.getString("CHANNEL_ID", "150004"));
         */

        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String tradeId = data.getString("TRADE_ID");
        String tradeFee = config[0];
        String channelId = data.getString("CHANNEL_ID", "150004");
        String paymentId = config[1];
        String payFeeModeCode = config[2];
        String remark = data.getString("REMARK", "");

        // ids = TuxedoHelper.callTuxSvc(pd, "TAM_RECV_FEE", param) 账务缴费接口 !@#
        IDataset ids = AcctCall.recvFee(serialNumber, tradeId, tradeFee, channelId, paymentId, payFeeModeCode, remark);
        if (ids.size() < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "缴费异常");
        }
        return ids;
    }

    private void recordHisMainTrade(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData iparam = new DataMap();

        iparam.put("TRADE_ID", data.getString("TRADE_ID", ""));

        iparam.put("BATCH_ID", "");

        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID", "")));

        iparam.put("ORDER_ID", "");

        iparam.put("PROD_ORDER_ID", "");

        iparam.put("BPM_ID", "");

        iparam.put("CAMPN_ID", "");

        iparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", ""));

        iparam.put("PRIORITY", "0");

        iparam.put("SUBSCRIBE_TYPE", "0");

        iparam.put("SUBSCRIBE_STATE", "9");

        iparam.put("NEXT_DEAL_TAG", "0");

        iparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));

        iparam.put("USER_ID", data.getString("USER_ID", ""));

        iparam.put("CUST_NAME", data.getString("CUST_NAME", ""));

        iparam.put("ACCT_ID", data.getString("ACCT_ID", ""));

        iparam.put("CUST_ID", data.getString("CUST_ID", ""));

        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));

        iparam.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));

        iparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", "0898"));

        iparam.put("CITY_CODE", data.getString("CITY_CODE", ""));

        iparam.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));

        iparam.put("BRAND_CODE", data.getString("BRAND_CODE", ""));

        iparam.put("CUST_ID_B", "");

        iparam.put("ACCT_ID_B", "");

        iparam.put("USER_ID_B", "");

        iparam.put("SERIAL_NUMBER_B", "");

        iparam.put("CUST_CONTACT_ID", "");

        iparam.put("SERV_REQ_ID", "");

        iparam.put("INTF_ID", "");

        iparam.put("ACCEPT_DATE", data.getString("DETAIL_TIME", ""));

        iparam.put("TRADE_STAFF_ID", getVisit().getStaffId());

        iparam.put("TRADE_DEPART_ID", getVisit().getDepartId());

        iparam.put("TRADE_CITY_CODE", getVisit().getCityCode());

        iparam.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());

        iparam.put("TERM_IP", getVisit().getLoginIP());

        iparam.put("OPER_FEE", "0");

        iparam.put("FOREGIFT", "0");

        iparam.put("ADVANCE_PAY", "0");

        iparam.put("INVOICE_NO", "");

        iparam.put("FEE_STATE", "0");

        iparam.put("FEE_TIME", "");

        iparam.put("FEE_STAFF_ID", "");

        iparam.put("PROCESS_TAG_SET", "0");

        iparam.put("OLCOM_TAG", "0");

        iparam.put("FINISH_DATE", data.getString("DETAIL_TIME", ""));

        iparam.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));

        iparam.put("EXEC_ACTION", "0");

        iparam.put("EXEC_RESULT", "");

        iparam.put("EXEC_DESC", data.getString("EXEC_DESC", ""));

        iparam.put("CANCEL_TAG", "0");

        iparam.put("CANCEL_DATE", "");

        iparam.put("CANCEL_STAFF_ID", "");

        iparam.put("CANCEL_DEPART_ID", "");

        iparam.put("CANCEL_CITY_CODE", "");

        iparam.put("CANCEL_EPARCHY_CODE", "");

        iparam.put("UPDATE_TIME", data.getString("DETAIL_TIME", ""));

        iparam.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));

        iparam.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", ""));

        iparam.put("REMARK", ""); // 总数

        iparam.put("RSRV_STR1", ""); // 劲爆奖中奖区间

        iparam.put("RSRV_STR2", ""); // 惊喜奖中奖区间

        iparam.put("RSRV_STR3", ""); // 幸运奖中奖区间

        iparam.put("RSRV_STR4", data.getString("RANDOM_NUM", "")); // 随机数

        iparam.put("RSRV_STR5", ""); // 最后时间
        iparam.put("RSRV_STR6", data.getString("EXEC_FLAG", "")); // 领奖标识
        iparam.put("RSRV_STR7", data.getString("PRIZE_TYPE_CODE", "")); // 中奖类型

        iparam.put("RSRV_STR8", data.getString("EXEC_TIME", "")); // 领奖时间

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public void recordSubTrade(IData data) throws Exception
    {

        IData paramMap = getParamMap(data);
        // 子台帐
        IData subTrade = new DataMap();
        subTrade.put("TRADE_ID", data.getString("TRADE_ID", ""));
        subTrade.put("ACTIVITY_NUMBER", data.getString("ACTIVITY_NUMBER"));
        subTrade.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("DETAIL_TIME")));
        subTrade.put("USER_ID", data.getString("USER_ID", ""));
        subTrade.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        subTrade.put("CITY_CODE", data.getString("CITY_CODE", ""));
        subTrade.put("DEAL_FLAG", data.getString("DEAL_FLAG", "0"));
        subTrade.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", ""));
        subTrade.put("PRIZE_TYPE_CODE", data.getString("PRIZE_TYPE_CODE", "0"));
        subTrade.put("EXEC_FLAG", data.getString("EXEC_FLAG", "0"));
        subTrade.put("EXEC_TIME", data.getString("EXEC_TIME", ""));
        subTrade.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        subTrade.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        subTrade.put("RANDOM_NUM", data.getString("RANDOM_NUM", ""));

        subTrade.put("RADIX", paramMap.getString("RADIX", ""));
        subTrade.put("PRIZE_ODDS_1", paramMap.getString("PRIZE_ODDS_1", ""));
        subTrade.put("PRIZE_ODDS_2", paramMap.getString("PRIZE_ODDS_2", ""));
        subTrade.put("PRIZE_ODDS_3", paramMap.getString("PRIZE_ODDS_3", ""));
        subTrade.put("PRIZE_ODDS_4", paramMap.getString("PRIZE_ODDS_4", ""));
        subTrade.put("PRIZE_ODDS_5", paramMap.getString("PRIZE_ODDS_5", ""));

        subTrade.put("PRIZE_ODDS_6", paramMap.getString("PRIZE_ODDS_6", ""));
        subTrade.put("REMARK", data.getString("REMARK", ""));
        subTrade.put("REVC1", data.getString("REVC1", ""));
        subTrade.put("REVC2", data.getString("REVC2", ""));
        subTrade.put("REVC3", data.getString("REVC3", ""));
        subTrade.put("REVC4", data.getString("REVC4", ""));
        subTrade.put("REVC5", data.getString("REVC5", ""));

        Dao.executeUpdateByCodeCode("SMS", "INS_UEC_LOTTERY_TRADE", subTrade,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    private void sendGift(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData paramMap = getParamMap(data);
        String eGiftLvStr = paramMap.getString("EGIFT_LV", "");
        String eGiftConfigStr = paramMap.getString("EGIFT_CONFIG", "").trim();
        String[] eGiftLvs = eGiftLvStr.split(",");
        String[] eGiftConfigs = eGiftConfigStr.split(",");

        if (eGiftLvs.length != eGiftConfigs.length)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数配置有误:EGIFT_LV与EGIFT_CONFIG不匹配");
        }
        String eGiftConfig = "";
        for (int i = 0, t = eGiftLvs.length; i < t; i++)
        {
            if (eGiftLvs[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
            {
                eGiftConfig = eGiftConfigs[i];
            }
        }

        String[] config = eGiftConfig.split("#");
        if (config == null || config.length != 2)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电子礼品参数配置有误");
        }
        data.put("GIFT_TYPE_CODE", config[0]);
        data.put("GIFT_ID", config[1]);
        data.put("X_RULECHECK", "1");

        // 大客户礼品兑换接口 !@#
        CSAppCall.call("SS.VipExchangeRegSVC.tradeReg", data);
    }

    private void supplyUserInfo(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        // IData userinfo = UserInfoQry.qryUsrInfo(data.getString("SERIAL_NUMBER"));
        IData userinfo = UserInfoQry.getUserInfoBySN(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到用户信息！");
        }

        data.put("USER_ID", userinfo.getString("USER_ID"));
        data.put("CITY_CODE", userinfo.getString("CITY_CODE"));
    }
    
    private void bindPackage(IData data) throws Exception
    {
        // TODO Auto-generated method stub 
        IData paramMap = getParamMap(data);
        String packageLvStr = paramMap.getString("PACKAGE_BIND_LV", "");
        String packageConfigStr = paramMap.getString("PACKAGE_CONFIG", "").trim();
        String[] packageLvs = packageLvStr.split(",");
        String[] packageConfigs = packageConfigStr.split(",");

        if (packageLvs.length != packageConfigs.length)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数配置有误:PACKAGE_BIND_LV与PACKAGE_CONFIG不匹配");
        }
        String packageConfig = "";
        for (int i = 0, t = packageLvs.length; i < t; i++)
        {
            if (packageLvs[i].equals(data.getString("PRIZE_TYPE_CODE", "0")))
            {
            	packageConfig = packageConfigs[i];
            }
        }

        String[] config = packageConfig.split("#");
        if (config == null || config.length != 2)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "营销活动参数配置有误");
        }
        //TODO 调用营销活动办理接口
        IData bindData = new DataMap();
        bindData.put("SERIAL_NUMBER", data.get("SERIAL_NUMBER"));
        bindData.put("PRODUCT_ID", config[0]);
        bindData.put("PACKAGE_ID", config[1]);


        // 大客户礼品兑换接口 !@#
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", bindData);
    }
}
