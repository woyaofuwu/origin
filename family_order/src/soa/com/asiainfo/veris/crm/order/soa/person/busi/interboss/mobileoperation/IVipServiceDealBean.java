
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;

public class IVipServiceDealBean extends CSBizBean
{

    protected static final transient Logger log = Logger.getLogger(IVipServiceDealBean.class);

    public IDataset airportServiceCharge(IData param) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        IData inparam = new DataMap();
        inparam.putAll(getDealCommpara(param));

        inparam.put("KIND_ID", "BIP2B005_T2101005_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));// 手机号码
        inparam.put("USER_PASSWD", param.getString("cond_USER_PASSWD"));// 客服密码
        inparam.put("IDCARDTYPE", logutil.decodeIdType(param.getString("cond_IDCARDTYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", param.getString("cond_IDCARDNUM"));// 证件号码

        inparam.put("START_DATE", SysDateMgr.getSysDate("yyyyMMddHHmmssS"));// 进入时间 YYYYMMDDhhmmss
        inparam.put("END_DATE", SysDateMgr.getSysDate("yyyyMMddHHmmssS"));// 离开时间 YYYYMMDDhhmmss

        inparam.put("SVC_LEVEL", param.getString("cond_SVCLEVEL"));// 服务级别

        String para_code1 = param.getString("SVCCODE_ALL");
        if (para_code1.startsWith(","))
        {
            para_code1 = para_code1.substring(2, para_code1.length());
        }
        String para_code2 = param.getString("SVCDISC_ALL");
        if (para_code2.startsWith(","))
        {
            para_code2 = para_code2.substring(2, para_code2.length());
        }
        String para_code3 = param.getString("ITEMID");
        if (para_code3.startsWith(","))
        {
            para_code3 = para_code3.substring(2, para_code3.length());
        }
        String para_code4 = param.getString("ITEMVALUE");
        if (para_code4.startsWith(","))
        {
            para_code4 = para_code4.substring(2, para_code4.length());
        }

        inparam.put("PARA_CODE1", para_code1.substring(1, para_code1.length() - 1));// 统计项目编码
        inparam.put("PARA_CODE2", para_code2.substring(1, para_code2.length() - 1));// 统计项目内容
        inparam.put("PARA_CODE3", para_code3.substring(1, para_code3.length() - 1));// 二级项目编码
        inparam.put("PARA_CODE4", para_code4.substring(1, para_code4.length() - 1));// 二级项目值

        int score = 0;
        if ("1".equals(param.getString("cond_SVCLEVEL")))
            score = 4000;
        if ("2".equals(param.getString("cond_SVCLEVEL")))
            score = 8000;
        if ("3".equals(param.getString("cond_SVCLEVEL")))
            score = 8000;
        if ("4".equals(param.getString("cond_SVCLEVEL")))
            score = 8000;
        int num = 0;
        try
        {
            num = Integer.parseInt(param.getString("cond_ATTENDANTS")) + 1;
        }
        catch (NumberFormatException e)
        {
            num = 1;
        }
        int freeTimes = Integer.parseInt(param.getString("FREE_TIMES", "0"));

        int totalScore, actNum;

        int thisFreeTimes = freeTimes - num;

        if (thisFreeTimes >= 0)
        {
            totalScore = 0;
        }
        else
        {
            totalScore = -1 * (score * thisFreeTimes);
        }

        actNum = num;

        inparam.put("PARA_CODE5", "0"); // 统计项目金额（不包括随员情况）
        inparam.put("PARA_CODE6", "0"); // 统计项目积分（不包括随员情况）
        inparam.put("RSRV_STR1", param.getString("AMOUNT", "0"));// 折合应扣总金额
        inparam.put("RSRV_STR2", totalScore); // 折合应扣总积分
        inparam.put("RSRV_STR20", thisFreeTimes); // 应扣的免费次数

        IDataset dataset = new DatasetList();

        String trade_id = "";
        String order_id = "";
        String remark = "VIP机场服务登记！";
        try
        {
            IData input = getLaunchLogParams(param);

            String operId = logutil.writeLanuchLog(input);
            trade_id = operId.split(",")[0];
            order_id = operId.split(",")[1];

            dataset = IBossCall.callHttpIBOSS("IBOSS", inparam);

            // dataset.clear(); IData tmp = new DataMap(); tmp.put("REGISTER_NAME","IBOSS无法返回，测试数据");
            // tmp.put("SEX_NAME","男"); tmp.put("AGE","10000"); tmp.put("USERSCORE","50000"); tmp.put("X_RSPCODE",
            // "0000");
            // tmp.put("X_RSPDESC", "查询成功"); tmp.put("X_RSPTYPE", "0");dataset.add(tmp);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // 写tf_bh_trade记录日志

            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark += xRspCode + ":" + xRspDesc;
            }

            logutil.updateLanuchLog(trade_id, dataset.getData(0).getString("X_RSPCODE"), dataset.getData(0).getString("X_RSPDESC"));

            dataset.getData(0).put("ORDER_ID", order_id);

        }
        return dataset;

    }

    public IDataset checkAirPortRight(IData param) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        IData inparam = new DataMap();
        inparam.putAll(getDealCommpara(param));
        String trade_id = SeqMgr.getTradeId();

        inparam.put("KIND_ID", "BIP2B005_T2000009_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        if ("01".equals(param.getString("cond_IDTYPE")))
        {
            inparam.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));// 手机号码
        }
        inparam.put("USER_PASSWD", param.getString("cond_USER_PASSWD"));// 客服密码

        inparam.put("IDCARDTYPE", logutil.decodeIdType(param.getString("cond_IDCARDTYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", param.getString("cond_IDCARDNUM"));// 证件号码

        inparam.put("SVC_LEVEL", param.getString("cond_SVCLEVEL"));// 服务级别
        inparam.put("ATTENDANTS", param.getString("cond_ATTENDANTS"));// 随员人数
        inparam.put("TRADE_ID", trade_id); // 业务类型，鉴权和记账都需要的

        param.put("TRADE_ID", trade_id);
        param.put("RSRV_STR1", trade_id);

        IDataset dataset = new DatasetList();

        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", inparam);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // dataset.clear(); IData tmp = new DataMap(); tmp.put("REGISTER_NAME","IBOSS无法返回，测试数据");
            // tmp.put("SEX_NAME","男"); tmp.put("AGE","10000"); tmp.put("USERSCORE","50000"); tmp.put("X_RSPCODE",
            // "0000");
            // tmp.put("X_RSPDESC", "查询成功"); tmp.put("X_RSPTYPE", "0");
            // tmp.put("CLASS_ID", "100");tmp.put("LEVEL", "1");tmp.put("USER_STATE_CODESET", "01");
            // tmp.put("CUST_NAME", "100");tmp.put("ALL_BALANCE", "1");tmp.put("IDCARDTYPE", "1");
            // tmp.put("CUST_NAME", "100");tmp.put("ALL_BALANCE", "1");tmp.put("IDCARDTYPE", "1");
            // tmp.put("USERRANK", "100");tmp.put("USERSCORE", "1222");tmp.put("ENTERTIME", SysDateMgr.getSysTime());
            // tmp.put("BRAND_CODE", "01");tmp.put("PUK", "1222");dataset.add(tmp);

            // 写tf_bh_trade记录日志
            String remark = "VIP机场服务鉴权！";
            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark += xRspCode + ":" + xRspDesc;
            }
            param.put("REMARK", remark);
            param.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));
            param.put("TRADE_TYPE_CODE", "417");
            String order_id = insTradeHis(param);

            logutil.insertRailWayLog(param);

            dataset.getData(0).put("ORDER_ID", order_id);

        }
        return dataset;
    }

    public IDataset checkRalwayRight(IData param) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        IData inparam = new DataMap();
        String trade_id = SeqMgr.getTradeId();

        inparam.putAll(getDealCommpara(param));

        inparam.put("KIND_ID", "BIP2B025_T2000029_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        if ("01".equals(param.getString("cond_IDTYPE")))
        {
            inparam.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));// 手机号码
        }
        inparam.put("USER_PASSWD", param.getString("cond_USER_PASSWD"));// 客服密码

        inparam.put("IDCARDTYPE", logutil.decodeIdType(param.getString("cond_IDCARDTYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", param.getString("cond_IDCARDNUM"));// 证件号码

        inparam.put("SVC_LEVEL", param.getString("cond_SVCLEVEL"));// 服务级别
        inparam.put("ATTENDANTS", param.getString("cond_ATTENDANTS"));// 随员人数

        inparam.put("TRADE_ID", trade_id); // 业务类型，鉴权和记账都需要的

        param.put("TRADE_ID", trade_id);
        param.put("RSRV_STR1", trade_id);

        IDataset dataset = new DatasetList();

        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", inparam);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // dataset.clear(); IData tmp = new DataMap(); tmp.put("REGISTER_NAME","IBOSS无法返回，测试数据");
            // tmp.put("SEX_NAME","男"); tmp.put("AGE","10000"); tmp.put("USERSCORE","50000"); tmp.put("X_RSPCODE",
            // "0000");
            // tmp.put("X_RSPDESC", "查询成功"); tmp.put("X_RSPTYPE", "0");
            // tmp.put("CLASS_ID", "100");tmp.put("LEVEL", "1");tmp.put("USER_STATE_CODESET", "01");
            // tmp.put("CUST_NAME", "100");tmp.put("ALL_BALANCE", "1");tmp.put("IDCARDTYPE", "1");
            // tmp.put("CUST_NAME", "100");tmp.put("ALL_BALANCE", "1");tmp.put("IDCARDTYPE", "1");
            // tmp.put("USERRANK", "100");tmp.put("USERSCORE", "1222");tmp.put("ENTERTIME", SysDateMgr.getSysTime());
            // tmp.put("BRAND_CODE", "01");tmp.put("PUK", "1222");dataset.add(tmp);

            // 写tf_bh_trade记录日志
            String remark = "VIP火车站服务鉴权！";
            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark += xRspCode + ":" + xRspDesc;
            }
            param.put("REMARK", remark);
            param.put("SERIAL_NUMBER", param.getString("cond_IDVALUE", ""));
            param.put("TRADE_TYPE_CODE", "364");

            logutil.insertRailWayLog(param);

            String order_id = insTradeHis(param);
            dataset.getData(0).put("ORDER_ID", order_id);

        }

        IDataset ret = new DatasetList();
        IData vipInfo = transCommpara(dataset.getData(0));
        ret.add(vipInfo);
        return ret;

    }

    public IData getDealCommpara(IData inparam) throws Exception
    {
        IData commonparam = new DataMap();
        commonparam.put("PROVINCE_CODE", inparam.getString("cond_PROVINCE_CODE"));// 省别编码
        commonparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        // 接入方式0 营业厅 1 客服(callcenter)2 网上客服 3 网上营业厅 4 银行 5 短信平台 6 一级BOSS 7 手机支付 8 统一帐户服务系统(uasp)
        // 9 短信营销/短信营业厅/短信代办 A 触摸屏 B 自助打印机 C 多媒体 D 自助营业厅 E 个人代扣/银行代扣 F 电话开通 G 168点播信息
        // H 空中充值 I 积分平台 J 彩铃接口 K 梦网接口 L WAP接口 M 大客户接口 N 电信卡余额 O 家校通 P 缴费卡缴费 Q 手机钱包 R POS机缴费

        commonparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 交易地州编码
        commonparam.put("TRADE_CITY_CODE", getVisit().getCityCode());// 交易城市代码
        commonparam.put("TRADE_DEPART_ID", getVisit().getDepartId());// 员工部门编码
        commonparam.put("TRADE_STAFF_ID", getVisit().getStaffId());// 员工城市编码
        commonparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        commonparam.put("ROUTETYPE", inparam.getString("cond_ROUTETYPE"));// 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("cond_ROUTETYPE")))// 路由值
            commonparam.put("ROUTEVALUE", inparam.getString("cond_IDVALUE"));
        else
            commonparam.put("ROUTEVALUE", inparam.getString("cond_PROVINCE_CODE"));

        return commonparam;

    }

    private IData getLaunchLogParams(IData idata) throws Exception
    {
        //
        String remark = "VIP机场服务登记！";

        IData params = new DataMap();

        params.put("USER_ID", "88888888"); // 机场VIP标志
        params.put("SERIAL_NUMBER", idata.getString("cond_IDVALUE", "")); // 服务号码
        params.put("OPER_FEE", idata.getString("AMOUNT", "0")); // 折合应扣总金额
        String SvcLevel = StaticUtil.getStaticValue("AIRPORT_VIP_SERVICELEVEL", idata.getString("cond_SVCLEVEL", "").split(",")[0]);
        params.put("RSRV_STR1", SvcLevel); // 服务类别
        params.put("RSRV_STR2", idata.getString("cond_TOTALSCORE", "0")); // 折合应扣总积分
        params.put("RSRV_STR3", idata.getString("AMOUNT", "0")); // 折合应扣总金额
        params.put("RSRV_STR4", "0"); // 发起方
        params.put("RSRV_STR5", idata.getString("TOTALTIMES", "0")); // 应扣免费次数
        params.put("RSRV_STR6", idata.getString("cond_ATTENDANTS", "")); // 随员人数
        IDataset outData = CommparaInfoQry.getCommpara("CSM", "121", idata.getString("cond_AIRPORTNAME", ""), CSBizBean.getVisit().getStaffEparchyCode());
        if (IDataUtil.isNotEmpty(outData))
        {
            params.put("RSRV_STR7", outData.getData(0).getString("PARAM_NAME")); // 机场名称
        }
        else
        {
            params.put("RSRV_STR7", ""); // 机场名称
        }

        params.put("RSRV_STR8", idata.getString("cond_PLANLINE", "")); // 航班名称
        params.put("RSRV_STR9", idata.getString("USERSCORE", "")); // 客户总积分
        params.put("RSRV_STR10", idata.getString("FREE_TIMES", "")); // 剩余免费次数（未扣减之前）
        params.put("TRADE_TYPE_CODE", "417");
        params.put("REMARK", remark);

        return params;
    }

    /**
     * 记录操作历史
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private String insTradeHis(IData data) throws Exception
    {
        String systime = SysDateMgr.getSysTime();
        String trade_id = "";
        if (StringUtils.isBlank(data.getString("TRADE_ID")))
        {
            trade_id = data.getString("TRADE_ID");
        }
        else
        {
            trade_id = SeqMgr.getTradeId();
        }

        String order_id = SeqMgr.getOrderId();
        String trade_type_code = data.getString("TRADE_TYPE_CODE", "2101");
        String net_type_code = "00";

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", trade_id);
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", order_id);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);
        inparam.put("PRIORITY", "0");
        inparam.put("SUBSCRIBE_TYPE", "0");
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", "");
        inparam.put("CUST_NAME", "");
        inparam.put("USER_ID", "");
        inparam.put("ACCT_ID", "");
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", net_type_code);
        inparam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", "");
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", "0");
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "                    ");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", data.getString("REMARK"));

        Dao.insert("TF_BH_TRADE", inparam);
        return order_id;
    }

    public IDataset railwayServiceCharge(IData param) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        IData inparam = new DataMap();
        inparam.putAll(getDealCommpara(param));

        inparam.put("KIND_ID", "BIP2B025_T2101025_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));// 手机号码
        inparam.put("USER_PASSWD", param.getString("cond_USER_PASSWD"));// 客服密码
        inparam.put("IDCARDTYPE", logutil.decodeIdType(param.getString("cond_IDCARDTYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", param.getString("cond_IDCARDNUM"));// 证件号码

        inparam.put("START_DATE", SysDateMgr.string2Date(SysDateMgr.getLastSecond(SysDateMgr.getSysTime()), SysDateMgr.PATTERN_STAND_SHORT));// 进入时间
        // YYYYMMDDhhmmss
        inparam.put("END_DATE", SysDateMgr.getSysDate("yyyyMMddHHmmssS"));// 离开时间 YYYYMMDDhhmmss

        // 0表示需要查询天气预报
        if ("0".equals(param.getString("cond_WEATHER_FORECAST")))
        {
            // 获取对应的省代码
            IDataset idatas = StaticUtil.getStaticList("CHINA_AREA_CODE", param.getString("cond_BLOOG_AREA"));
            String destProv = idatas.getData(0).getString("PDATA_ID");
            // String destProv = StaticInfoQry.getStaticInfoByTypeIdDataId("CHINA_AREA_CODE",
            // param.getString("cond_BLOOG_AREA")).getString("PDATA_ID");
            inparam.put("DEST_PROV", destProv);
            inparam.put("DEST_CITY", param.getString("cond_BLOOG_AREA"));
            inparam.put("DEST_DATE1", SysDateMgr.getSysDate().replace("-", ""));
            if ("0".equals(param.getString("cond_ISNOWDATE")))
            {
                inparam.put("DEST_DATE2", (Integer.parseInt((SysDateMgr.addDays(SysDateMgr.getSysDate(), 1)).replace("-", ""))));
            }
        }

        inparam.put("SVC_LEVEL", param.getString("cond_SVCLEVEL"));// 服务级别

        String para_code1 = param.getString("SVCCODE_ALL");
        if (para_code1.startsWith(","))
        {
            para_code1 = para_code1.substring(2, para_code1.length());
        }
        String para_code2 = param.getString("SVCDISC_ALL");
        if (para_code2.startsWith(","))
        {
            para_code2 = para_code2.substring(2, para_code2.length());
        }
        String para_code3 = param.getString("ITEMID");
        if (para_code3.startsWith(","))
        {
            para_code3 = para_code3.substring(2, para_code3.length());
        }
        String para_code4 = param.getString("ITEMVALUE");
        if (para_code4.startsWith(","))
        {
            para_code4 = para_code4.substring(2, para_code4.length());
        }

        int num = 0;

        try
        {
            num = Integer.parseInt(param.getString("cond_ATTENDANTS", "0")) + 1;
        }
        catch (NumberFormatException e)
        {
            num = 1;
        }

        if (num > 0)
        {
            para_code1 += para_code1.trim().length() > 0 ? ", \"06\"" : "\"06\"";
            para_code2 += para_code2.trim().length() > 0 ? ", \"" + num + "\"" : "\"" + num + "\"";
        }

        inparam.put("PARA_CODE1", para_code1.substring(1, para_code1.length() - 1));// 统计项目编码
        inparam.put("PARA_CODE2", para_code2.substring(1, para_code2.length() - 1));// 统计项目内容
        inparam.put("PARA_CODE3", para_code3.substring(1, para_code3.length() - 1));// 二级项目编码
        inparam.put("PARA_CODE4", para_code4.substring(1, para_code4.length() - 1));// 二级项目值

        int score = 0;
        if ("1".equals(param.getString("cond_SVCLEVEL")))
            score = 2000;
        if ("2".equals(param.getString("cond_SVCLEVEL")))
            score = 3000;

        int totalScore;
        totalScore = score * num;

        String trade_id = SeqMgr.getTradeId();

        inparam.put("PARA_CODE5", "0"); // 金额
        inparam.put("PARA_CODE6", score); // 折合应扣积分
        inparam.put("RSRV_STR1", "0"); // 折合应扣总金额
        inparam.put("RSRV_STR2", totalScore); // 折合应扣总积分
        inparam.put("TRADE_ID", trade_id); // 业务类型，鉴权和记账都需要的

        param.put("TRADE_ID", trade_id);
        param.put("RSRV_STR1", trade_id);

        IDataset dataset = new DatasetList();

        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", inparam);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // 写tf_bh_trade记录日志
            String remark = "VIP火车站服务登记！";
            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark += xRspCode + ":" + xRspDesc;
            }
            param.put("REMARK", remark);
            param.put("SERIAL_NUMBER", param.getString("cond_IDVALUE", ""));
            param.put("TRADE_TYPE_CODE", "364");
            String order_id = insTradeHis(param);

            dataset.getData(0).put("ORDER_ID", order_id);

        }
        return dataset;

    }

    public IData transCommpara(IData inparam) throws Exception
    {
        String userRank = inparam.getString("USERRANK");// 客户级别
        String userScore = inparam.getString("USERSCORE");// 客户可用积分余额
        LanuchUtil lanuchUtil = new LanuchUtil();
        String cust_class_type = "";
        if (userRank != null)
        {
            cust_class_type = lanuchUtil.decodeCustClassType(userRank);
        }

        inparam.put("USERRANK", cust_class_type);
        inparam.put("SCORE", userScore);
        return inparam;
    }

    public IDataset vipSimBakRestore(IData param) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();
        IData inparam = new DataMap();
        inparam.putAll(getDealCommpara(param));

        inparam.put("KIND_ID", "BIP2B003_T2001003_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        if ("01".equals(param.getString("cond_IDTYPE")))
        {
            inparam.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));// 手机号码
        }
        inparam.put("USER_PASSWD", param.getString("cond_USER_PASSWD"));// 客服密码

        inparam.put("IDCARDTYPE", logutil.decodeIdType(param.getString("cond_IDCARDTYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", param.getString("cond_IDCARDNUM"));// 证件号码

        inparam.put("OLDSIMNUM", param.getString("OLDSIMNUM"));// 旧SIM卡号
        inparam.put("NEWSIMNUM", param.getString("NEWSIMNUM"));// 备卡SIM卡号
        inparam.put("OPER_FEE", param.getString("OPER_FEE"));// 手续费

        IDataset dataset = new DatasetList();

        try
        {
            dataset = IBossCall.callHttpIBOSS("IBOSS", inparam);
        }
        catch (Exception e)
        {
            IData temp = new DataMap();
            temp.put("X_RSPCODE", "-1");
            temp.put("X_RSPDESC", e.getMessage() + "/n" + "系统异常，请联系管理员");
            dataset.add(temp);
        }
        finally
        {
            // 写tf_bh_trade记录日志
            String remark = "VIP备卡激活登记！";
            if (IDataUtil.isNotEmpty(dataset))
            {
                String xRspCode = dataset.getData(0).getString("X_RSPCODE");
                String xRspDesc = dataset.getData(0).getString("X_RSPDESC");
                remark += xRspCode + ":" + xRspDesc;
            }
            param.put("REMARK", remark);
            param.put("SERIAL_NUMBER", param.getString("cond_IDVALUE"));
            param.put("TRADE_TYPE_CODE", "144");
            String order_id = insTradeHis(param);
            dataset.getData(0).put("ORDER_ID", order_id);
        }
        return dataset;
    }

}
