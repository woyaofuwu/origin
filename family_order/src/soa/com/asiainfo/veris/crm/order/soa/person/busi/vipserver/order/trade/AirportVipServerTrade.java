
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.trade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata.AirportVipServerReqData;

public class AirportVipServerTrade extends BaseTrade implements ITrade
{

    /**
     * 生成俱乐部易登机服务表数据
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
    public String createAirportService(BusiTradeData btd) throws Exception
    {

        IData data = new DataMap();
        AirportVipServerReqData reqData = (AirportVipServerReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();

        //将不能在IPrintFinishAction中取到的值都存入都主台账的预留字段10中
        data.put("SERVICE_ID", btd.getTradeId());
        data.put("AIRDROME_ID", reqData.getAirDromeId());
        data.put("AIRDROME_NAME", reqData.getAirDromeName());
        data.put("VIP_ID", reqData.getVipId()); // 全球通商旅非VIP客户 VIP_ID为0
        data.put("VIP_TYPE_CODE", reqData.getVipTypeCode()); // 全球通商旅非VIP客户 VIP_TYPE_CODE为S
        data.put("CLASS_ID", reqData.getVipClassId()); // 全球通商旅非VIP客户 VIP_CLASS_ID为S
        data.put("VIP_NO", reqData.getVipNo());
        data.put("RESERVICE_ID", btd.getTradeId());
        data.put("PLANE_LINE", reqData.getPlanLine());
        data.put("FOLLOW_NUM", reqData.getFollowNuber());
        data.put("OLD_SCORE_VALUE", reqData.getScore());
        data.put("CONSUME_SCORE", reqData.getConsumeScore()); // 消耗积分
        data.put("SERVICE_TYPE", reqData.getServiceType());
        data.put("HANDING_CHARGE", reqData.getHandingChange()); // 手续费
        data.put("SERVICE_CHARGE", reqData.getServiceChange()); // 服务费
        data.put("SERVICE_STAFF", getVisit().getStaffId());
        data.put("SERVICE_DATE", reqData.getAcceptTime());
        data.put("RSRV_STR2", reqData.getThisRsrvStr2()); // 当次受理使用的免费次数
        data.put("RSRV_STR3", reqData.getRestCount()); // 剩余免费次数
//        data.put("SERIAL_NUMBER", ucaData.getSerialNumber());
//        data.put("CUST_NAME", ucaData.getCustomer().getCustName());
//        data.put("BEGIN_CITY", "");
//        data.put("ARRIVE_CITY", "");
//        data.put("SERVICE_CONTENT", reqData.getServiceContent());
//        data.put("STATE", "0"); // 已受理
//        data.put("RESERVICE_ID", btd.getTradeId());
//        data.put("RETURN_EXPLAIN", "");
//        data.put("RETURN_DATE", "");
//        data.put("RETURN_STAFF", "");
//        data.put("REMARK", "VIP机场易登机服务");
//        data.put("RSRV_TAG1", "1");
//        data.put("INDIV_INFO", reqData.getIndivInfo());
//        data.put("FEEDBACK", reqData.getFeeBack());
//        data.put("INNOVATION", reqData.getInnovation());
//        data.put("ADVICES", reqData.getAdvices());
//        data.put("OTHERS", reqData.getOthers());
        return IDataUtil.isNotEmpty(data)?data.toString():"";
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        AirportVipServerReqData reqData = (AirportVipServerReqData) btd.getRD();

        createTradeMain(btd);

        createTradeScore(btd);

        createTradeOther(btd);

        //将在登记时插资料表的动作，搬到AirportVipServerAction中
//        createAirportService(btd);

    }

    public void createTradeMain(BusiTradeData btd) throws Exception
    {
        AirportVipServerReqData reqData = (AirportVipServerReqData) btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        //
        int consumeScore = Math.abs(Integer.parseInt(reqData.getConsumeScore())); // 折合应扣总积分
        // String valueChange = commInfo.getString("VALUE_CHANGED", "0"); //折合应扣金额

        String serviceType = reqData.getServiceType();
        IDataset paramset = ParamInfoQry.getCommparaByCode("CSM", "989", serviceType, CSBizBean.getTradeEparchyCode());

        if (paramset != null || paramset.size() > 0)
        {
            IData paraminfo = (IData) paramset.get(0);
            if ("1".equals(paraminfo.getString("PARA_CODE11", "0")))
            {
                String noticeContent = "";
                String cur_date = reqData.getAcceptTime();
                int rest_free = reqData.getRestCount();
                int consume_score = Integer.parseInt(reqData.getConsumeScore());
                if (rest_free >= 0)
                {
                    noticeContent = "您好！您于" + cur_date + "享受中国移动海南公司机场易登机服务，尚余" + rest_free + "次免费享受服务次数，如需查询更多服务信息请致电08981255518联系您的专属客户经理！";
                }
                else
                {
                    noticeContent = "您好！您于" + cur_date + "享受中国移动海南公司机场易登机服务，您的免费享受服务次数已用完，本次扣减积分" + consume_score + "分。如需查询更多服务信息请致电08981255518联系您的专属客户经理！";
                }
            }
        }
        mainTradeData.setRsrvStr1(serviceType);// 服务类型
        mainTradeData.setRsrvStr2(consumeScore + "");// 主台帐RSRV_STR2字段记录扣减积分
        mainTradeData.setRsrvStr3(reqData.getValueChange());
        mainTradeData.setRsrvStr4(reqData.getHandingChange());// 金额
        mainTradeData.setRsrvStr5(reqData.getServiceChange());
        //将在登记时插资料表的动作，搬到AirportVipServerAction中，reqData的数据存在主台账预留字段8、9、10中
        //将上次附加信息放入预留字段8中
        IData data = new DataMap();
        data.put("INDIV_INFO", reqData.getIndivInfo());
        data.put("FEEDBACK", reqData.getFeeBack());
        data.put("INNOVATION", reqData.getInnovation());
        data.put("ADVICES", reqData.getAdvices());
        data.put("OTHERS", reqData.getOthers());
        if(data.toString().length()>500){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "上次附加信息内容超长!");
        }
        mainTradeData.setRsrvStr8(data.toString());
        if(reqData.getServiceContent().length()>500){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务内容信息超长!");
        }
        mainTradeData.setRsrvStr9(reqData.getServiceContent());
        
        mainTradeData.setRsrvStr10(createAirportService(btd));

    }

    /**
     * 拼其它台帐
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void createTradeOther(BusiTradeData btd) throws Exception
    {
        AirportVipServerReqData reqData = (AirportVipServerReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();

        int total_freecount = reqData.getTotalFreeCount(); // 客户可使用的免费次数
        int freecount = reqData.getFreeCount(); // 已使用的免费服务次数
        // 免费次数使用完时不用登记其他台帐
        if (total_freecount <= freecount)
        {
            return; // 免费次数使用完时的处理
        }
        String staticParams = StaticUtil.getStaticValue("VIP_IDENTITY_EXP_DATE", "VIP_DATE");

        String vip_end_date = "";
        String time = SysDateMgr.getSysDate("yyyy");
        if ("02".equals(staticParams.substring(0, 2)))
        {
            time = time + "-03-01";
            time = SysDateMgr.getLastSecond(time);// 取前一秒的时间

        }
        else
        {
            time = time + "-" + staticParams;
        }
        String now = reqData.getAcceptTime();
        if (now.compareTo(time) > 0)
        {
            vip_end_date = SysDateMgr.getAddMonthsLastDay(12, time);
        }
        else
        {
            vip_end_date = time;
        }
        IDataset freeCountInfos = UserOtherInfoQry.getUserOther(ucaData.getUserId(), "AREM");

        OtherTradeData otherTradeData = new OtherTradeData();

        if (IDataUtil.isNotEmpty(freeCountInfos))
        {
            OtherTradeData delotherTradeData = new OtherTradeData(freeCountInfos.getData(0));
            delotherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delotherTradeData.setEndDate(reqData.getAcceptTime());
            btd.add(ucaData.getSerialNumber(), delotherTradeData);

            otherTradeData = new OtherTradeData(freeCountInfos.getData(0));
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setRsrvDate10(reqData.getOldStartDate());
        }
        else
        {
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setRsrvValue("AREM");
            otherTradeData.setRsrvValueCode("AREM");
            otherTradeData.setUserId(ucaData.getUserId());
        }
        otherTradeData.setStartDate(now);
        otherTradeData.setEndDate(vip_end_date); // 修改大客户截止时间
        otherTradeData.setRsrvStr1(String.valueOf(reqData.getThisRsrvStr1()));// 本次业务完成后已使用的免费次数
        otherTradeData.setRemark("VIP机场易登机服务");

        btd.add(ucaData.getSerialNumber(), otherTradeData);
    }

    /**
     * 拼积分台帐
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
    public void createTradeScore(BusiTradeData btd) throws Exception
    {

        AirportVipServerReqData reqData = (AirportVipServerReqData) btd.getRD();
        int consumeScore = Math.abs(Integer.parseInt(reqData.getConsumeScore()));// 折合应扣总积分
        String consumeValue = reqData.getValueChange();// 折合应扣总金额
        ScoreTradeData scoreTradeData = new ScoreTradeData();
        UcaData ucaData = reqData.getUca();
        // int handChange = Integer.parseInt(reqData.getHandingChange());
        // int serviceChange = Integer.parseInt(reqData.getServiceChange());
        // int valueChange = handChange + serviceChange;

        scoreTradeData.setUserId(ucaData.getUserId());
        scoreTradeData.setSerialNumber(ucaData.getSerialNumber());
        scoreTradeData.setIdType("0"); // 用户积分
        scoreTradeData.setScoreTypeCode("ZZ"); // 用户消费积分td_s_scoretype
        scoreTradeData.setYearId("ZZZZ");
        scoreTradeData.setStartCycleId("-1");
        scoreTradeData.setEndCycleId("-1");
        scoreTradeData.setScore(reqData.getScore()); // 原积分
        scoreTradeData.setScoreChanged("-" + consumeScore); // 应扣积分（积分异动）
        if(StringUtils.isBlank(consumeValue)){
            consumeValue = "0";
        }
        scoreTradeData.setValueChanged("-" + consumeValue); // 总金额
        scoreTradeData.setScoreTag("1");
        scoreTradeData.setCancelTag("0");
        scoreTradeData.setRemark("VIP机场易登机服务");

        btd.add(ucaData.getSerialNumber(), scoreTradeData);

    }

}
