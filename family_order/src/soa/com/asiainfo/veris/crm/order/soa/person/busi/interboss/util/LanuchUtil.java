
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;

public class LanuchUtil extends CSBizBean
{

    public String changeIdType(String IdType)
    {
        String iBossTdType = null;
        if (IdType == null)
            IdType = "";
        IdType = IdType.trim();

        if ("身份证".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("VIP卡".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("护照".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("军官证".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("警官证".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    /**
     * 大客户卡类型转换大客户等级
     * 
     * @param cycle
     * @throws Exception
     */
    private String codeTransfer(String value)
    {
        String vip_id = "";
        if ("1".equals(value))// 钻卡
        {
            vip_id = "4";
        }
        else if ("2".equals(value))// 金卡
        {
            vip_id = "3";

        }
        else if ("3".equals(value))// 银卡
        {
            vip_id = "2";
        }
        else
        {
            vip_id = "0";
        }
        return vip_id;
    }

    public void createBhStaffTrade(String tradeId) throws Exception
    {
        TradeStaffInfoQry.insertStaffTradeInfo(tradeId);
    }

    public String dealHBTrade(IData idata) throws Exception
    {
        // getRouteEparchyCode 是作为用户资料。
        // getTradeEparchyCode 是作为操作员资料。
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysDate();
        String trade_id = idata.getString("TRADE_ID");
        String order_id = idata.getString("ORDER_ID");
        IData inparam = new DataMap();
        inparam.putAll(idata);
        String trade_type_code;
        if (strIsNull(idata.getString("TRADE_TYPE_CODE")))
        {
            trade_type_code = "9999";
        }
        else
        {
            trade_type_code = idata.getString("TRADE_TYPE_CODE");
        }

        String net_type_code;
        if (strIsNull(idata.getString("NET_TYPE_CODE")))
        {
            net_type_code = "00";
        }
        else
        {
            net_type_code = idata.getString("NET_TYPE_CODE");
        }

        inparam.put("TRADE_ID", trade_id);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", order_id);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", idata.getString("CUST_ID", ""));
        inparam.put("CUST_NAME", idata.getString("CUST_NAME", ""));
        inparam.put("USER_ID", idata.getString("USER_ID", ""));
        inparam.put("ACCT_ID", idata.getString("ACCT_ID", ""));
        inparam.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", net_type_code);
        inparam.put("EPARCHY_CODE", eparchy_code);
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", idata.getString("BRAND_CODE", ""));
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", idata.getString("OPER_FEE") == null ? "0" : idata.getString("OPER_FEE","0"));
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", BofConst.PROCESS_TAG_SET);
        inparam.put("OLCOM_TAG", "0");
        String operFee = idata.getString("OPER_FEE");
        if (null != operFee && !operFee.equals("0"))
        {
            inparam.put("FEE_STATE", "1");
        }
        else
        {
            inparam.put("FEE_STATE", "0");
        }
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", idata.getString("REMARK"));

        inparam.put("RSRV_STR1", idata.getString("RSRV_STR1"));
        inparam.put("RSRV_STR2", idata.getString("RSRV_STR2"));
        inparam.put("RSRV_STR3", idata.getString("RSRV_STR3"));
        inparam.put("RSRV_STR4", idata.getString("RSRV_STR4"));
        inparam.put("RSRV_STR5", idata.getString("RSRV_STR5"));
        inparam.put("RSRV_STR6", idata.getString("RSRV_STR6"));
        inparam.put("RSRV_STR7", idata.getString("RSRV_STR7"));
        inparam.put("RSRV_STR8", idata.getString("RSRV_STR8"));
        inparam.put("RSRV_STR9", idata.getString("RSRV_STR9"));
        inparam.put("RSRV_STR10", idata.getString("RSRV_STR10"));

        Dao.insert("TF_BH_TRADE", inparam,Route.getJourDb(CSBizBean.getTradeEparchyCode()));//modify by duhj 2017/03/13  订单表改为jour用户
        return trade_id + "," + order_id;
    }

    public String decodeCustClassType(String classId)
    {
        String custClassType = null;

        if ("100".equals(classId))
        {
            custClassType = "普通客户";
        }
        else if ("200".equals(classId))
        {
            custClassType = "重要客户";
        }
        else if ("201".equals(classId))
        {
            custClassType = "党政机关客户";
        }
        else if ("202".equals(classId))
        {
            custClassType = "军、警、安全机关客户";
        }
        else if ("203".equals(classId))
        {
            custClassType = "联通合作伙伴客户";
        }
        else if ("204".equals(classId))
        {
            custClassType = "英雄、模范、名星类客户";
        }
        else if ("300".equals(classId))
        {
            custClassType = "普通大客户";
        }
        else if ("301".equals(classId))
        {
            custClassType = "钻石卡大客户";
        }
        else if ("302".equals(classId))
        {
            custClassType = "金卡大客户";
        }
        else if ("303".equals(classId))
        {
            custClassType = "银卡大客户";
        }
        else if ("304".equals(classId))
        {
            custClassType = "贵宾卡大客户";
        }

        return custClassType;
    }

    /**
     * @param IdType
     * @return
     * @author wangww3
     */
    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }
    
    public static String decodeIdType2(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("2".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    public String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    /**
     * 拼参数数据
     * 
     * @param pd
     * @param td
     * @param idata
     * @throws Exception
     */
    public IData getParamData(IData idata) throws Exception
    {

        String sysdate = SysDateMgr.getSysTime();
        IData userInfo = idata.getData("USER_INFO");
        IData iparam = new DataMap();
        String trade_id = SeqMgr.getTradeId();
        String order_id = SeqMgr.getOrderId();

        String scoreTypeCode = idata.getString("SCORE_TYPE_CODE");
        if ("G010".endsWith(scoreTypeCode))
        {
            iparam.put("SCORE_TYPE_CODE", "b"); // 动感地带
        }
        else if ("G001".endsWith(scoreTypeCode))
        {
            iparam.put("SCORE_TYPE_CODE", "e"); // 全球通
        }
        iparam.put("MOBILENUM", idata.getString("MOBILENUM"));// 跨区号码
        iparam.put("SCORE_CHANGED", idata.getString("SCORE_VALUE", "0"));// 积分变动值
        iparam.put("SCORE_VALUE", idata.getString("SCORE_VALUE", "0"));// 积分额
        iparam.put("RSRV_STR1", "1");// 调账务修改积分接口时用 是否调整总消费积分 0 =不调整 1=调整
        iparam.put("RSRV_STR2", idata.getString("SCORE_TYPE_CODE"));// 积分类型 0－全球通积分；1－动感地带
        iparam.put("RSRV_STR3", idata.getString("CLASS_LEVEL"));// 客户级别 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
        iparam.put("VIP_TYPE_CODE", "0");// 固定为个人大客户类型
        iparam.put("VIP_CLASS_ID", codeTransfer(idata.getString("CLASS_LEVEL")));// 大客户级别

        iparam.put("YEAR_ID", "ZZZZ");// 修改积分用 参数
        iparam.put("CHANNEL_ID", "0");// 修改积分用 参数
        iparam.put("RULE_ID", "");// 修改积分用 参数
        iparam.put("OPERATE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 修改积分用 参数
        iparam.put("OPERATE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 修改积分用 参数
        iparam.put("OPERATE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 修改积分用 参数
        iparam.put("OPERATE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 修改积分用 参数
        iparam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE", ""));
        iparam.put("TRADE_ID", trade_id);
        iparam.put("BATCH_ID", "0");
        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        iparam.put("ORDER_ID", order_id);
        iparam.put("PROD_ORDER_ID", "");
        iparam.put("BPM_ID", "");
        iparam.put("CAMPN_ID", "");
        iparam.put("TRADE_TYPE_CODE", "361");
        iparam.put("PRIORITY", "0");
        iparam.put("SUBSCRIBE_TYPE", "0");
        iparam.put("SUBSCRIBE_STATE", "9");
        iparam.put("NEXT_DEAL_TAG", "0");
        iparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        iparam.put("USER_ID", idata.getString("USER_ID"));
        iparam.put("CUST_NAME", idata.getString("CUST_NAME"));
        iparam.put("ACCT_ID", idata.getString("ACCT_ID"));
        iparam.put("CUST_ID", idata.getString("CUST_ID"));
        iparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        String net_type_code;
        if (strIsNull(idata.getString("NET_TYPE_CODE")))
        {
            net_type_code = "00";
        }
        else
        {
            net_type_code = idata.getString("NET_TYPE_CODE");
        }
        iparam.put("NET_TYPE_CODE", net_type_code);
        iparam.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE", ""));
        iparam.put("CITY_CODE", userInfo.getString("CITY_CODE", ""));
        iparam.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        iparam.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        iparam.put("CUST_ID_B", "");
        iparam.put("ACCT_ID_B", "");
        iparam.put("USER_ID_B", "");
        iparam.put("SERIAL_NUMBER_B", "");
        iparam.put("CUST_CONTACT_ID", "");
        iparam.put("SERV_REQ_ID", "");
        iparam.put("INTF_ID", "");
        iparam.put("ACCEPT_DATE", sysdate);
        iparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        iparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        iparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        iparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        iparam.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        iparam.put("OPER_FEE", "0");
        iparam.put("FOREGIFT", "0");
        iparam.put("ADVANCE_PAY", "0");
        iparam.put("INVOICE_NO", "");
        iparam.put("FEE_STATE", "0");
        iparam.put("FEE_TIME", "");
        iparam.put("FEE_STAFF_ID", "");
        String processTag = "";
        String checkMode = idata.getString("CHECK_MODE", "");// 验证方式 0 =本人证件号码 1=用户密码
        if (checkMode.equals("0"))// 证件校验方式
        {
            iparam.put("CHECK_MONTH", "证件校验");
            processTag = "0000000000000000000E";
        }
        else
        // 密码校验
        {
            iparam.put("CHECK_MONTH", "密码校验");
            processTag = "0000000000000000000B";
        }
        iparam.put("PROCESS_TAG_SET", processTag);
        iparam.put("OLCOM_TAG", "0");
        iparam.put("FINISH_DATE", sysdate);
        iparam.put("EXEC_TIME", sysdate);
        iparam.put("EXEC_ACTION", "0");
        iparam.put("EXEC_RESULT", "");
        iparam.put("EXEC_DESC", "");
        iparam.put("CANCEL_TAG", "0");
        iparam.put("CANCEL_DATE", "");
        iparam.put("CANCEL_STAFF_ID", "");
        iparam.put("CANCEL_DEPART_ID", "");
        iparam.put("CANCEL_CITY_CODE", "");
        iparam.put("CANCEL_EPARCHY_CODE", "");
        iparam.put("UPDATE_TIME", sysdate);
        iparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        iparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        String remark1 = idata.getString("REMARK", "");
        String remark2 = "跨区入网资料同步";
        String remark = !"".equals(remark1) ? remark1 : remark2;
        iparam.put("REMARK", remark);// 备注

        iparam.put("RSRV_STR4", "");
        iparam.put("RSRV_STR5", "");
        iparam.put("RSRV_STR6", "");
        iparam.put("RSRV_STR7", "");
        iparam.put("RSRV_STR8", "");
        iparam.put("RSRV_STR9", "");
        iparam.put("RSRV_STR10", "");

        return iparam;
    }

    public void insertRailWayLog(IData input) throws Exception
    {
        IData inparam = new DataMap();
        String sysDate = SysDateMgr.getSysTime();
        String userPass = input.getString("USER_PASSWD", ""); // 客服密码
        String inpsptId = input.getString("IDCARDNUM", ""); // 证件号码

        String enterTime = input.getString("START_DATE", ""); // 进入时间
        String leaveTime = input.getString("END_DATE", ""); // 离开时间
        String weatherFlag = input.getString("WEATHER_FLAG", ""); // 是否查询天气预报

        if (!"".equals(enterTime) && !"".equals(leaveTime))
        {
            enterTime = enterTime.substring(0, 4) + "-" + enterTime.substring(4, 6) + "-" + enterTime.substring(6, 8) + " " + enterTime.substring(8, 10) + ":" + enterTime.substring(10, 12) + ":" + enterTime.substring(12, 14);
            leaveTime = leaveTime.substring(0, 4) + "-" + leaveTime.substring(4, 6) + "-" + leaveTime.substring(6, 8) + " " + leaveTime.substring(8, 10) + ":" + leaveTime.substring(10, 12) + ":" + leaveTime.substring(12, 14);
        }
        else
        {
            enterTime = sysDate;
            leaveTime = SysDateMgr.END_DATE_FOREVER;
        }

        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
        inparam.put("ACCEPT_MONTH", sysDate.substring(5, 7));
        inparam.put("CUST_NAME", input.getString("CUST_NAME"));
        inparam.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        if (inpsptId.length() >= 0)
        {
            inparam.put("IDENTITY_CHECK_TAG", "0");
            inparam.put("INFO_CONTENT", "证件号码+证件类型");
        }
        else if (userPass.length() >= 0)
        {
            inparam.put("IDENTITY_CHECK_TAG", "1");
            inparam.put("INFO_CONTENT", "证件号码+用户密码");
        }
        inparam.put("START_DATE", enterTime); // 进入时间
        inparam.put("END_DATE", leaveTime); // 离开时间
        inparam.put("RSRV_STR1", input.getString("RSRV_STR1", "")); // 业务流水，发起方提供给一级BOSS使用的
        inparam.put("RSRV_STR2", input.getString("DESC", input.getString("X_RSPTYPE", ""))); // 返回结果
        inparam.put("RSRV_STR3", input.getString("X_RSPDESC")); // 返回结果描述
        inparam.put("RSRV_STR4", weatherFlag); // 查询天气预报标志
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("UPDATE_TIME", sysDate);
        inparam.put("REMARK", input.getString("REMARK"));

        Dao.insert("TL_B_IDENTITYAUTH_LOG", inparam);
    }

    /**
     * @param str
     * @return
     * @author wangww3
     */
    public boolean strIsNull(String str)
    {
        return ("".equals(str) || (str == null));
    }

    /**
     * @param pd
     * @param td
     * @param idata
     * @return
     * @throws Exception
     * @author wangww3
     */
    /*
     * public String writeLanuchLog(IData idata) throws Exception { // getRouteEparchyCode 是作为用户资料。 //
     * getTradeEparchyCode 是作为操作员资料。 //String eparchy_code = td.getRouteEparchyCode(); String eparchy_code =
     * CSBizBean.getTradeEparchyCode(); // String systime = DualMgr.getSysDate(pd); String systime =
     * SysDateMgr.getSysTime(); // if (td.getTradeId() == null || "".equals(td.getTradeId())) { //
     * td.setTradeId(DualMgr.getSeqId(pd, "seq_trade_id")); // } String trade_id = SeqMgr.getTradeId(); IData inparam =
     * new DataMap(); inparam.putAll(idata); String trade_type_code; // if (strIsNull(td.getTradeTypeCode())) { //
     * trade_type_code = "9999"; // } else { // trade_type_code = td.getTradeTypeCode(); // } trade_type_code = "9999";
     * UcaData ucaData = UcaDataFactory.getNormalUca(idata.getString("SERIAL_NUMBER")); String net_type_code; // if
     * (strIsNull(td.getNetTypeCode())) { // net_type_code = "00"; // } else { // net_type_code = td.getNetTypeCode();
     * // } net_type_code = "00"; //inparam.put("TRADE_ID", btd.getTradeId());// 业务流水号 inparam.put("TRADE_ID",
     * trade_id);// 业务流水号 inparam.put("ACCEPT_MONTH", 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
     * inparam.put("BATCH_ID", "0"); // inparam.put("ORDER_ID", DualMgr.getSeqId(pd, "seq_order_id"));
     * inparam.put("ORDER_ID", SeqMgr.getOrdReqId()); inparam.put("PROD_ORDER_ID", ""); inparam.put("BPM_ID", "");
     * inparam.put("CAMPN_ID", "0"); inparam.put("TRADE_TYPE_CODE", trade_type_code);// 业务类型编码：见参数表TD_S_TRADETYPE
     * inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准） inparam.put("SUBSCRIBE_TYPE", "0");//
     * 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行 inparam.put("SUBSCRIBE_STATE", "0");
     * inparam.put("NEXT_DEAL_TAG", "0"); //inparam.put("IN_MODE_CODE", td.getInModeCode()); inparam.put("IN_MODE_CODE",
     * getVisit().getInModeCode()); //inparam.put("CUST_ID", td.getCustId()); inparam.put("CUST_ID",
     * ucaData.getCustId()); //inparam.put("CUST_NAME", td.getCustInfo().getString("CUST_NAME"));
     * inparam.put("CUST_NAME", ucaData.getCustomer().getCustName()); //inparam.put("USER_ID", td.getUserId());
     * inparam.put("USER_ID", ucaData.getUserId()); //inparam.put("ACCT_ID", td.getAcctId()); inparam.put("ACCT_ID",
     * ucaData.getAcctId()); inparam.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
     * inparam.put("NET_TYPE_CODE", net_type_code); inparam.put("EPARCHY_CODE", eparchy_code); inparam.put("CITY_CODE",
     * ""); inparam.put("PRODUCT_ID", ""); //inparam.put("BRAND_CODE", td.getUserInfo().getString("BRAND_CODE"));
     * inparam.put("BRAND_CODE", ucaData.getBrandCode()); inparam.put("ACCEPT_DATE", systime);
     * //inparam.put("TRADE_STAFF_ID", td.getString("TRADE_STAFF_ID")); inparam.put("TRADE_STAFF_ID",
     * getVisit().getStaffId()); //inparam.put("TRADE_DEPART_ID", td.getString("TRADE_DEPART_ID"));
     * inparam.put("TRADE_DEPART_ID", getVisit().getDepartId()); //inparam.put("TRADE_CITY_CODE",
     * td.getString("TRADE_CITY_CODE")); inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
     * //inparam.put("TRADE_EPARCHY_CODE", td.getTradeEparchyCode()); inparam.put("TRADE_EPARCHY_CODE",
     * CSBizBean.getTradeEparchyCode()); inparam.put("OPER_FEE", idata.getString("OPER_FEE") == null ? "0" :
     * idata.getString("OPER_FEE")); inparam.put("FOREGIFT", "0"); inparam.put("ADVANCE_PAY", "0");
     * inparam.put("PROCESS_TAG_SET", "                    "); inparam.put("OLCOM_TAG", "0"); String operFee =
     * idata.getString("OPER_FEE"); if (null != operFee && !operFee.equals("0")) { inparam.put("FEE_STATE", "1"); } else
     * { inparam.put("FEE_STATE", "0"); } inparam.put("FINISH_DATE", systime); inparam.put("EXEC_TIME", systime);
     * inparam.put("CANCEL_TAG", "0"); inparam.put("REMARK", "未处理请求！"); if (idata.containsKey("SCORE_VALUE") &&
     * idata.containsKey("SCORE_TYPE_CODE") && idata.containsKey("CLASS_LEVEL")) { inparam.put("USER_ID", "99999999");//
     * 跨区入网标志 inparam.put("RSRV_STR1", idata.getString("SCORE_VALUE"));// 积分额 inparam.put("RSRV_STR2",
     * idata.getString("SCORE_TYPE_CODE"));// 积分类型 // 0－全球通积分；1－动感地带 inparam.put("RSRV_STR3",
     * idata.getString("CLASS_LEVEL"));// 客户级别 // 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡 inparam.put("RSRV_STR4", "0");// 0
     * :发起方 } if (idata.containsKey("RSRV_STR20") && idata.containsKey("RSRV_STR2")) { inparam.put("USER_ID",
     * "88888888");// 机场VIP标志 inparam.put("RSRV_STR1", idata.getString("RSRV_STR20"));// 机场VIP应扣免费次数 int score; try {
     * score = Integer.parseInt(idata.getString("RSRV_STR2")); } catch (NumberFormatException e) { score = 0; }
     * inparam.put("RSRV_STR3", score / 20);// 积分600分，30元/人次;积分1000分，50元/人次;积分1500分，75元/人次;积分2500分，125元/人次
     * inparam.put("RSRV_STR4", "0");// 0 :发起方 } if (idata.containsKey("NEWSIMNUM")) { inparam.put("RSRV_STR1",
     * idata.getString("NEWSIMNUM"));// 备卡卡号 inparam.put("RSRV_STR4", "0");// 0 :发起方 } Dao.insert("TF_BH_TRADE",
     * inparam); //return td.getTradeId(); return trade_id; }
     */
    /**
     * @param pd
     * @param trade_id
     * @param status_code
     * @param status_name
     * @return
     * @throws Exception
     * @author wangww3
     */
    public int updateLanuchLog(String trade_id, String status_code, String status_name) throws Exception
    {
        if (status_name != null && status_name.length() > 48)
            status_name = status_name.substring(0, 48);

        IData param = new DataMap();
        param.put("REMARK", status_code + ":" + status_name);
        param.put("TRADE_ID", trade_id);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));

        return Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPDATE_TRADE_REMARK", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 登记 TF_BH_TRADE表 异地号码 不通过我们查询 idata 需要传入三户部分信息
     * 
     * @data 2013-8-20
     * @param idata
     * @return trade_id + order_id 字符串
     * @throws Exception
     */
    public String writeLanuchLog(IData idata) throws Exception
    {
        // getRouteEparchyCode 是作为用户资料。
        // getTradeEparchyCode 是作为操作员资料。
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysTime();
        String trade_id = SeqMgr.getTradeId();
        String order_id = SeqMgr.getOrderId();
        IData inparam = new DataMap();
        inparam.putAll(idata);
        String trade_type_code;
        if (strIsNull(idata.getString("TRADE_TYPE_CODE")))
        {
            trade_type_code = "9999";
        }
        else
        {
            trade_type_code = idata.getString("TRADE_TYPE_CODE");
        }

        String net_type_code;
        if (strIsNull(idata.getString("NET_TYPE_CODE")))
        {
            net_type_code = "00";
        }
        else
        {
            net_type_code = idata.getString("NET_TYPE_CODE");
        }

        inparam.put("TRADE_ID", trade_id);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "");
        inparam.put("ORDER_ID", order_id);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", idata.getString("CUST_ID", ""));
        inparam.put("CUST_NAME", idata.getString("CUST_NAME", ""));
        inparam.put("USER_ID", idata.getString("USER_ID", ""));
        inparam.put("ACCT_ID", idata.getString("ACCT_ID", ""));
        inparam.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", net_type_code);
        inparam.put("EPARCHY_CODE", eparchy_code);
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", idata.getString("BRAND_CODE", ""));
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", idata.getString("OPER_FEE","") == "" ? "0" : idata.getString("OPER_FEE","0"));
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "         ");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("INTF_ID", idata.getString("INTF_ID", ""));
        inparam.put("UPDATE_TIME", SysDateMgr.getSysTime());
        inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        String operFee = idata.getString("OPER_FEE","");
        if ("" != operFee && !operFee.equals("0"))
        {
            inparam.put("FEE_STATE", "1");
        }
        else
        {
            inparam.put("FEE_STATE", "0");
        }
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", "未处理请求！");

        if (idata.containsKey("SCORE_VALUE") && idata.containsKey("SCORE_TYPE_CODE") && idata.containsKey("CLASS_LEVEL"))
        {
            inparam.put("USER_ID", "99999999");// 跨区入网标志
            inparam.put("RSRV_STR1", idata.getString("SCORE_VALUE"));// 积分额
            inparam.put("RSRV_STR2", idata.getString("SCORE_TYPE_CODE"));// 积分类型 0－全球通积分；1－动感地带
            inparam.put("RSRV_STR3", idata.getString("CLASS_LEVEL"));// 客户级别 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (idata.containsKey("RSRV_STR20") && idata.containsKey("RSRV_STR2"))
        {
            inparam.put("USER_ID", "88888888");// 机场VIP标志
            inparam.put("RSRV_STR1", idata.getString("RSRV_STR20"));// 机场VIP应扣免费次数
            int score;
            try
            {
                score = Integer.parseInt(idata.getString("RSRV_STR2"));
            }
            catch (NumberFormatException e)
            {
                score = 0;
            }
            inparam.put("RSRV_STR3", score / 20);// 积分600分，30元/人次;积分1000分，50元/人次;积分1500分，75元/人次;积分2500分，125元/人次
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (idata.containsKey("NEWSIMNUM"))
        {
            inparam.put("RSRV_STR1", idata.getString("NEWSIMNUM"));// 备卡卡号
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }

        if ("417".equals(idata.getString("TRADE_TYPE_CODE")))
        {
            inparam.put("RSRV_STR1", idata.getString("RSRV_STR1", "")); // 服务类别
            inparam.put("RSRV_STR2", idata.getString("RSRV_STR2", "")); // 折合应扣总积分
            inparam.put("RSRV_STR3", idata.getString("RSRV_STR3", "")); // 折合应扣总金额
            inparam.put("RSRV_STR4", "0"); // 发起方
            inparam.put("RSRV_STR5", idata.getString("RSRV_STR5", "")); // 应扣免费次数
            inparam.put("RSRV_STR6", idata.getString("RSRV_STR6", "")); // 随员人数
            inparam.put("RSRV_STR7", idata.getString("RSRV_STR7", "")); // 机场名称
            inparam.put("RSRV_STR8", idata.getString("RSRV_STR8", "")); // 航班名称
            inparam.put("RSRV_STR9", idata.getString("RSRV_STR9", "")); // 客户总积分
            inparam.put("RSRV_STR10", idata.getString("RSRV_STR10", "")); // 剩余免费次数（未扣减之前）
        }
        if ("141".equals(idata.getString("TRADE_TYPE_CODE")))
        {
            inparam.put("RSRV_STR4", idata.getString("EMPTY_CARD_ID", "")); // 异地空卡号记录
        }

        Dao.insert("TF_BH_TRADE", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return trade_id + "," + order_id;
    }
    public void writeRealNameLog4RemoteWriteCard(IData input) throws Exception
    {
        IData param = new DataMap();
        String reqSeq = input.getString("ReqSeq");
        String tradeId = input.getString("TRADE_ID");
        param.put("WRITECARD_SEQ",reqSeq);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        param.put("REALNAME_SEQ",input.getString("REALNAME_SEQ"));
        param.put("TRADE_ID", tradeId);// 业务流水号
        param.put("CREATE_TIME", SysDateMgr.getSysTime());// 业务流水号
        param.put("HOME_PROV", input.getString("HOME_PROV"));// 归属省代码        
        param.put("PIC_NAME_T",input.getString("PIC_NAME_T"));
        param.put("PIC_NAME_Z",input.getString("PIC_NAME_Z"));
        param.put("PIC_NAME_F",input.getString("PIC_NAME_F"));
        param.put("STATE",0);
        Dao.insert("TF_F_REALNAME_RWCARD_F", param,Route.CONN_CRM_CEN);
    }
    
    /**
     * 获取异地写卡(单号卡)拼接数据 财务费用接口日志表 插表 TF_B_TRADE_BFAS_IN
     * 
     * @author YYZ
     * @param pd
     * @param td
     * @throws Exception
     */
    /*
     * public void insertTradeBfasIn(String tradeId,IData input) throws Exception{ String
     * sysDate=SysDateMgr.getSysDate(); String paymoneyCode="0"; //收款方式 TF_B_TRADEFEE_PAYMONEY表获取pay_money_code默认现金0
     * String checkCode=""; //支票/转账号 IData accdataparam = new DataMap(); accdataparam.put("TRADE_ID", tradeId);
     * accdataparam.put("CANCEL_TAG", "0"); IDataset paymodeList = Dao.queryListByCodeCode("TF_B_TRADEFEE_PAYMONEY",
     * "SEL_BY_TRADE", accdataparam); if(paymodeList != null && paymodeList.size() > 0){ paymoneyCode =
     * paymodeList.getData(0).getString("PAY_MONEY_CODE"); } // 收款方式为支票或转账时从TF_B_TRADEFEE_CHECK获取支票/转账号
     * if("Z".equals(paymoneyCode) || "3".equals(paymoneyCode)){ IDataset checkList =
     * dao.queryListByCodeCode("TF_B_TRADEFEE_CHECK", "SEL_BY_TRADE", accdataparam); if(checkList != null &&
     * checkList.size() > 0){ checkCode = checkList.getData(0).getString("CHECK_CARD_NO"); } } //账户类别
     * 根据tf_b_trade_accout表获取PAY_MODE_CODE String paymodeCode = td.getAcctInfo().getString("PAY_MODE_CODE");
     * if(!"0".equals(paymodeCode) || !"1".equals(paymodeCode)){ paymodeCode=""; } String resTypeCode="1"; //资源大类编码1
     * sim卡 6 白卡 String simTypeCode= "";//资源小类编码 String capacityTypeCode="";//面值/容量
     * if(!td.getTradeTypeCode().equals("360")){
     *//***** 查询SIM卡类资源 先查询TF_R_SIMCARD_IDLE 再查询TF_R_SIMCARD_USE ********/
    /*
     * IData imsiidleparam=new DataMap(); IData imsiuseparam=new DataMap(); IData emptycardparam=new DataMap();
     * imsiidleparam.put("IMSI", conParams.getString("IMSI")); IData simidledata = dao.queryByPK("TF_R_SIMCARD_IDLE",
     * imsiidleparam); String emptyCardId="";
     *//****************** add yangyz3 2013530 start **************************/
    /*
     * IData simData = new DataMap(pd.getData().getString("SimCardData")); if(simData!=null&&simData.size()>0){
     * emptyCardId = simData.getString("RSRV_STR1"); }else{ emptyCardId = pd.getData().getString("emptyCardNo"); }
     *//****************** add yangyz3 2013530 end **************************/
    /*
     * boolean flag=true; if(simidledata!=null && simidledata.size()>0){ flag=false;
     * emptyCardId=simidledata.getString("EMPTY_CARD_ID","");
     * simTypeCode=simidledata.getString("SIM_TYPE_CODE","");////资源小类
     * capacityTypeCode=simidledata.getString("CAPACITY_TYPE_CODE","");//容量 }else{ imsiuseparam.put("IMSI",
     * conParams.getString("IMSI")); IData simusedata = dao.queryByPK("TF_R_SIMCARD_USE", imsiuseparam);
     * if(simusedata!=null && simusedata.size()>0){ flag=false; emptyCardId=simusedata.getString("EMPTY_CARD_ID","");
     * simTypeCode=simusedata.getString("SIM_TYPE_CODE","");////资源小类
     * capacityTypeCode=simusedata.getString("CAPACITY_TYPE_CODE","");//容量 } } if("".equals(emptyCardId)){
     * common.error("异地写卡 卡号不能为空！"); } if(!"".equals(emptyCardId)){//白卡 resTypeCode="6";
     * emptycardparam.put("EMPTY_CARD_ID", emptyCardId); IData emptycarddata = dao.queryByPK("TF_R_EMPTYCARD",
     * emptycardparam); simTypeCode=emptycarddata.getString("SIM_TYPE_CODE","");//资源小类
     * capacityTypeCode=emptycarddata.getString("CAPACITY_TYPE_CODE","");//容量 }
     *//********************* delete yangyz3 2013530 start *********************/
    /*
     * if(flag){ common.error("异地写卡 卡号不能为空！"); }
     *//********************* delete yangyz3 2013530 end *********************/
    /*
     * } String present_fee=""; //促销 String formFee=""; //代收手续费/佣金
     *//******* 会计日期 *******/
    /*
     * datas.getData(0).getString("ACCEPT_DATE"); //pd.setData(datas.getData(0));//主台账数据放入pd IDataset listdata = new
     * DatasetList(); IData tempdata = new DataMap(); // IDataset feeList = td.getFeeList(); IDataset feeList =
     * dao.queryListByCodeCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_ID", accdataparam); if (feeList != null &&
     * feeList.size() > 0){ for (int i = 0; i < feeList.size(); i++){ tempdata = feeList.getData(i); String
     * feeMode=tempdata.getString("FEE_MODE","");//费用类型 String
     * feeItemTypeCode=tempdata.getString("FEE_TYPE_CODE","");//费用明细类型 (TF_B_TRADEFEE_SUB) String feeTypeCode=""; //费用类型
     * String oldFee=tempdata.getString("OLDFEE","0");//应收金额 String fee=tempdata.getString("FEE","0");//实收金额 String
     * saleTypeCode="1" ; //销售类型，默认为正常销售 1 打折销售 2 赠送3 present_fee=""; formFee=""; if("0".equals(feeMode))//营业费，卡费 {
     * feeTypeCode = "10";//费用类型 (营业费用) } if("".equals(feeTypeCode)){ continue; } if(Integer.parseInt(oldFee)>0) {
     * if(Integer.parseInt(oldFee)>Integer.parseInt(fee) && Integer.parseInt(fee)!= 0) { //打折销售 saleTypeCode = "2"; }
     * else if(Integer.parseInt(fee)==0) { //赠送 saleTypeCode = "3"; } } if(saleTypeCode != "1") { int itempFee =
     * Integer.parseInt(oldFee) - Integer.parseInt(fee); present_fee = String.valueOf(itempFee); formFee = "0"; } IData
     * param = new DataMap(); param.put("SALE_TYPE_CODE", saleTypeCode); //销售类型 param.put("PAY_MONEY_CODE",
     * paymoneyCode); //收款方式 param.put("FEE_TYPE_CODE", feeTypeCode); //费用类型 param.put("FEE_ITEM_TYPE_CODE",
     * feeItemTypeCode); //费用明细类型 param.put("PAY_MODE_CODE", paymodeCode); //账户类别 param.put("BRAND_CODE",
     * td.getUserInfo().getString("BRAND_CODE"));//用户品牌 param.put("PRODUCT_ID",
     * td.getUserInfo().getString("PRODUCT_ID"));//用户产品 param.put("USER_TYPE_CODE",
     * td.getUserInfo().getString("USER_TYPE_CODE")); //用户类型 param.put("RES_TYPE_CODE", resTypeCode);//资源类型
     * param.put("RES_KIND_CODE", simTypeCode);//资源种类 param.put("CAPACITY_TYPE_CODE", capacityTypeCode);//面值/容量
     * param.put("RECE_FEE", oldFee);//应收金额 param.put("FEE", fee);//实收金额 param.put("PRESENT_FEE", present_fee);//促销赠送金额
     * param.put("FORM_FEE", formFee); //代收手续费/佣金 param.put("ACC_DATE", acceptData);//会计日期 param.put("OPER_DATE",
     * acceptData);//交易日期 param.put("CANCEL_TAG", "0");//返销标记 0 正业务 1返销业务 param.put("PROC_TAG", "0"); //0 未处理 1正在拆分
     * 3拆分成功 4错单 param.put("RSRV_NUM1", td.getUserId()); //填写用户的USER_ID param.put("CHECK_NUMBER", checkCode);//支票/转账号
     * //公共参数bfas param.put("TRADE_ID", td.getTradeId()); param.put("EPARCHY_CODE",
     * td.getString("TRADE_EPARCHY_CODE","0898"));//地市编码 param.put("CITY_CODE", td.getString("TRADE_CITY_CODE"));//业务区编码
     * param.put("DEPART_ID", td.getString("TRADE_DEPART_ID"));//部门编码 param.put("OPER_STAFF_ID",
     * td.getString("TRADE_STAFF_ID"));//员工编码 param.put("OPER_TYPE_CODE", td.getString("TRADE_TYPE_CODE"));//系统业务类型
     * param.put("IN_MODE_CODE", td.getString("IN_MODE_CODE"));//接入方式td.getInModeCode() param.put("NET_TYPE_CODE",
     * "00");//网别 //add by yuezy 20131227 if("true".equals(VatUtils.getJudeYGZTag(pd))){ IData rateInfo = new DataMap();
     * String nowDate = DualMgr.getSysDate(pd); String tradeEparchyCode = td.getTradeEparchyCode();
     * rateInfo.put("TRADE_TYPE_CODE","2108"); rateInfo.put("NOW_DATE", nowDate); rateInfo.put("EPARCHY_CODE",
     * tradeEparchyCode); IDataset taxInfo = LanuchUtil.getTaxInfo(pd,rateInfo); if(taxInfo.size() == 0 || taxInfo ==
     * null || taxInfo.size()>1) { common.error("900099","税率配置错误，请检查"); }else{ String rate =
     * taxInfo.getData(0).getString("RATE"); String type = taxInfo.getData(0).getString("TYPE"); IDataset infos = new
     * DatasetList(); IData info = new DataMap(); info.put("RATE",rate); info.put("TYPE",type); info.put("FEE",oldFee);
     * info.put("FACT_PAY_FEE",fee); infos.add(info); IDataset vatInfos = VatUtils.getTradeFeeTaxForCalculate(infos);
     * if(vatInfos!=null&&vatInfos.size()>0){ param.put("NO_TAX_FEE",vatInfos.getData(0).getString("FEE1"));//不含税价
     * param.put("TAX_FEE",vatInfos.getData(0).getString("FEE2"));//不含税价
     * param.put("RATE",vatInfos.getData(0).getString("RATE"));//不含税价
     * param.put("RSRV_NUM5",vatInfos.getData(0).getString("FEE3"));//不含税价 param.put("RSRV_STR3",type);//不含税价 } } }
     * listdata.add(param); } }else{ IData param = new DataMap(); param.put("SALE_TYPE_CODE", "1"); //销售类型
     * param.put("PAY_MONEY_CODE", paymoneyCode); //收款方式 param.put("FEE_TYPE_CODE", "10"); //费用类型
     * param.put("FEE_ITEM_TYPE_CODE", "10"); //费用明细类型 param.put("PAY_MODE_CODE", paymodeCode); //账户类别
     * param.put("BRAND_CODE", td.getUserInfo().getString("BRAND_CODE"));//用户品牌 param.put("PRODUCT_ID",
     * td.getUserInfo().getString("PRODUCT_ID"));//用户产品 param.put("USER_TYPE_CODE",
     * td.getUserInfo().getString("USER_TYPE_CODE")); //用户类型 param.put("RES_TYPE_CODE", resTypeCode);//资源类型
     * param.put("RES_KIND_CODE", simTypeCode);//资源种类 param.put("CAPACITY_TYPE_CODE", capacityTypeCode);//面值/容量
     * param.put("RECE_FEE", "0");//应收金额 param.put("FEE", "0");//实收金额 param.put("PRESENT_FEE", present_fee);//促销赠送金额
     * param.put("FORM_FEE", formFee); //代收手续费/佣金 param.put("ACC_DATE", acceptData);//会计日期 param.put("OPER_DATE",
     * acceptData);//交易日期 param.put("CANCEL_TAG", "0");//返销标记 0 正业务 1返销业务 param.put("PROC_TAG", "0"); //0 未处理 1正在拆分
     * 3拆分成功 4错单 param.put("RSRV_NUM1", td.getUserId()); //填写用户的USER_ID param.put("CHECK_NUMBER", checkCode);//支票/转账号
     * //公共参数bfas param.put("TRADE_ID", td.getTradeId()); param.put("EPARCHY_CODE",
     * td.getString("TRADE_EPARCHY_CODE","0898"));//地市编码 param.put("CITY_CODE", td.getString("TRADE_CITY_CODE"));//业务区编码
     * param.put("DEPART_ID", td.getString("TRADE_DEPART_ID"));//部门编码 param.put("OPER_STAFF_ID",
     * td.getString("TRADE_STAFF_ID"));//员工编码 param.put("OPER_TYPE_CODE", td.getString("TRADE_TYPE_CODE"));//系统业务类型
     * param.put("IN_MODE_CODE", td.getString("IN_MODE_CODE"));//接入方式td.getInModeCode() param.put("NET_TYPE_CODE",
     * "00");//网别 if("true".equals(VatUtils.getJudeYGZTag(pd))){ IData rateInfo = new DataMap(); String nowDate =
     * DualMgr.getSysDate(pd); String tradeEparchyCode = td.getTradeEparchyCode();
     * rateInfo.put("TRADE_TYPE_CODE","2108"); rateInfo.put("NOW_DATE", nowDate); rateInfo.put("EPARCHY_CODE",
     * tradeEparchyCode); IDataset taxInfo = LanuchUtil.getTaxInfo(pd,rateInfo); if(taxInfo.size() == 0 || taxInfo ==
     * null || taxInfo.size()>1) { common.error("900099","税率配置错误，请检查"); }else{ String type =
     * taxInfo.getData(0).getString("TYPE"); param.put("RSRV_STR3",type);//不含税价 } } listdata.add(param); }
     * FinancialFeeUtil.insertTradeBfasIn(pd,listdata); }
     */

}
