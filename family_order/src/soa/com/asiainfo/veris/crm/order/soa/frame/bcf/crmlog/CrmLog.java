
package com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog;

import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.cache.LogCfgCache;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.IvrData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog.CrmLogCache.CrmOperCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog.AuditLogCache.AuditOperCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public final class CrmLog extends LogBaseBean
{
    public static final Logger logger = Logger.getLogger(CrmLog.class);

    private static Object logbean = null;

    private static Map<String, IData> recSvcMap;

    private static CrmOperCfg getCrmOperCfg(String pageName, String svcName) throws Exception
    {
        String key = CrmLog.getIvrkey(pageName, svcName);

        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CrmLogCache.class);
        CrmOperCfg crmOperCfg = (CrmOperCfg) cache.get(key);

        return crmOperCfg;
    }

    private static AuditOperCfg getAuditOperCfg(String pageName, String svcName) throws Exception
    {
        String key = CrmLog.getIvrkey(pageName, svcName);

        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(AuditLogCache.class);
        if(cache != null) {
            AuditOperCfg auditOperCfg = (AuditOperCfg) cache.get(key);
            return auditOperCfg;
        }else {
            return null;
        }
    }

    public static String getIvrkey(String pageName, String svcName) throws Exception
    {
        StringBuilder key = new StringBuilder(40);

        key.append(pageName).append("_").append(svcName);

        return key.toString();
    }

    private static Object getLogbean() throws Exception
    {
        if (logbean == null)
        {
            Class<?> bean = Class.forName("com.ailk.resservice.bean.BossLogBean");
            logbean = bean.newInstance();
        }
        return logbean;
    }

    /**
     * 依据KIND_ID判断是否记录BOSS日志
     * 
     * @param kindId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private static boolean isRecLog(String kindId, String eparchyCode) throws Exception
    {
        if (recSvcMap == null)
        {
            recSvcMap = new HashMap<String, IData>();
            // 通过反射调用记录日志服务表
            logbean = getLogbean();
            Method method = logbean.getClass().getMethod("getBossLogServiceList", String.class);
            IDataset dataset = (IDataset) method.invoke(logbean, eparchyCode);
            if (!IDataUtil.isEmpty(dataset))
            {
                int size = dataset.size();
                IData codeData = null;
                String codekindId = null;
                for (int i = 0; i < size; i++)
                {
                    codeData = dataset.getData(i);
                    codekindId = codeData.getString("PARA_CODE2");// KIND_ID
                    recSvcMap.put(codekindId, codeData);
                }
            }
        }
        return recSvcMap.containsKey(kindId);
    }

    public static void log(String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 当前服务是否记录日志
        String pageName = CSBizBean.getVisit().getSourceName();

        if (StringUtils.isBlank(pageName))
        {
            return;
        }

        CrmOperCfg crmOperCfg = getCrmOperCfg(pageName, svcName);

        // 不记录退出
        if (crmOperCfg == null)
        {
            return;
        }

        // sysTime
        crmOperCfg.sysTime = SysDateMgr.getSysTime();

        // 依次判断 是否记录各种 垃圾日志

        // 记录日志 TF_F_OPERLOG (IVR)
        boolean isLog = BizEnv.getEnvBoolean("crm.log.operlog", false);

        if (isLog && StringUtils.isNotBlank(crmOperCfg.opCode))
        {
            logIvr(crmOperCfg, svcName, inData, outDataset);
        }

        // 记录日志 TF_B_STAFFOPERLOG
        isLog = BizEnv.getEnvBoolean("crm.log.staffoper", false);

        if (isLog && StringUtils.isNotBlank(crmOperCfg.operTypeCode))
        {
            logStaffOper(crmOperCfg, svcName, inData, outDataset);
        }

        // 记录日志 TL_B_CRM_OPERLOG
        isLog = BizEnv.getEnvBoolean("crm.log.crmoper", false);

        if (isLog && StringUtils.isNotBlank(crmOperCfg.operMod))
        {
            logCrmOper(crmOperCfg, svcName, inData, outDataset);
        }
    }

    /**
     * 在TD_S_COMMPARA配置一级BOSS发起的业务类型相应参数，记录日志到统计库表TF_B_EPOINT_LOG。
     * 
     * @param svcName
     * @param inData
     * @param outDataset
     * @throws Exception
     */
    public static void logBoss(String svcName, IData head, IData inData, IDataset outDataset) throws Exception
    {
        // 记录日志 TF_B_EPOINT_LOG（统计库日志）
        boolean isLog = BizEnv.getEnvBoolean("crm.log.bosslog", false);

        if (isLog == false)
        {
            return;
        }

        // 依次判断是否一级Boss日志
        // IN_MODE_CODE
        String inModeCode = head.getString("IN_MODE_CODE", "");

        // 一级BOSS IN_MODE_CODE=6
        if (!inModeCode.equals("6"))
        {
            return;
        }

        String eparchyCode = inData.getString("EPARCHY_CODE", "0731");

        // KIND_ID
        String kindId = inData.getString("KIND_ID", "");

        // 依据服务名判断是否记录日志
        boolean isRecLog = isRecLog(kindId, eparchyCode);
        if (kindId.equals("") || !isRecLog)
        {
            return;
        }

        // 用户品牌
        if (!IDataUtil.isEmpty(outDataset))
        {
            IData temp = outDataset.getData(0);
            if (temp != null && temp.size() > 0)
            {
                inData.put("USER_BRAND", temp.getString("USER_BRAND", ""));
            }
        }

        // 服务配置数据
        IData codeData = recSvcMap.get(kindId);
        if (codeData != null)
        {
            inData.put("CODE_DATA", codeData);
        }

        // 因数据源原因，记录LOG代码放到资源。
        logbean = getLogbean();
        Method method = logbean.getClass().getMethod("sendLog", IData.class);
        method.invoke(logbean, inData);
    }

    private static void logCrmOper(CrmOperCfg crmOperCfg, String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 登录工号
        String OPER_ID = SeqMgr.getOperId();

        // 操作
        String sn = inData.getString("SERIAL_NUMBER", "");

        String OPER_DESC = "";
        if (StringUtils.isNotBlank(sn))
        {
            OPER_DESC = "输入参数为:服务号码=" + sn;
        }

        // 拼装日志数据
        StringBuilder msg = new StringBuilder(500);

        msg.append(OPER_ID); // OPER_ID
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getStaffId()); // STAFF_ID
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.operMod); // OPER_MOD 操作模块：见参数表TD_S_STATIC/CRMOPERLOG_OPERMOD
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.operType); // OPER_TYPE 操作类型：见参数表TD_S_STATIC/CRMOPERLOG_OPERTYPE
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.operLevel); // OPER_LEVEL 操作级别：见参数表TD_S_STATIC/CRMOPERLOG_OPERLEVEL
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.sysTime); // OPER_TIME 操作时间
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(OPER_DESC); // OPER_DESC 操作描述
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.sysTime); // UPDATE_TIME 更新时间
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getStaffId()); // UPDATE_STAFF_ID 更新员工
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getDepartId()); // UPDATE_DEPART_ID 更新部门
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getCityCode()); // RSRV_STR2 更新业务区
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getRemoteAddr()); // RSRV_STR3 客户端IP地址
        msg.append(LOG_SEPARATOR_COMMA);

        sendLog(LOG_TYPE_CRMOPER, "0", 0, 0, msg);
    }
	
    public static void auditLog(String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 当前服务是否记录日志
        String pageName = CSBizBean.getVisit().getSourceName();

        AuditOperCfg auditOperCfg = getAuditOperCfg(pageName, svcName);

        // 不记录退出
        if (auditOperCfg == null)
        {
            return;
        }

        // sysTime
        auditOperCfg.sysTime = SysDateMgr.getSysTime();

        // 记录日志 TL_B_CRM_OPERLOG
        boolean isLog = BizEnv.getEnvBoolean("crm.log.auditoper", false);

        if (isLog)
        {
            logAuditOper(auditOperCfg, svcName, inData, outDataset);
        }
    }

    private static void logAuditOper(AuditOperCfg auditOperCfg, String svcName, IData inData, IDataset outDataset) throws Exception
    {

        logger.debug("审计日志svcName：" + svcName);
        logger.debug("审计日志inData：" + inData);
        logger.debug("审计日志inData：" + outDataset);
        logger.debug("审计日志Visit：" + CSBizBean.getVisit().getAll());


        //是否绕行 Y:绕行 N:非绕行
        String roundAudit = "Y";
        if(StringUtils.isNotEmpty(CSBizBean.getVisit().getMainId())) { //是否绕行 Y:绕行 N:非绕行
            roundAudit = "N";
        }

        String serialNumber = inData.getString("SERIAL_NUMBER", "空");

        String amount = inData.getString("AMOUNT", "");

        String checkMode = inData.getString("CHECK_MODE", "");
        String authType = "0";
        //checkMode:0- 证件号码 1-服务密码 2-服务密码 + SIM卡号(白卡号) 3-服务号码 + 证件号码 4-服务密码 + 证件号码
        // 5-短信验证 + SIM卡号(白卡号) 6-服务密码+验证码 7-验证码 8-SIM卡号(白卡号) 9-证件号码+验证码
        if(StringUtils.equals(checkMode, "0")) {
            authType = "2";
        }else if (StringUtils.equals(checkMode, "1")) {
            authType = "1";
        }else if (StringUtils.equals(checkMode, "2")) {
            authType = "10";
        }else if (StringUtils.equals(checkMode, "3")) {
            authType = "3";
        }else if (StringUtils.equals(checkMode, "4")) {
            authType = "4";
        }else if (StringUtils.equals(checkMode, "5")) {
            authType = "5";
        }else if (StringUtils.equals(checkMode, "6")) {
            authType = "6";
        }else if (StringUtils.equals(checkMode, "7")) {
            authType = "7";
        }else if (StringUtils.equals(checkMode, "8")) {
            authType = "8";
        }else if (StringUtils.equals(checkMode, "9")) {
            authType = "9";
        }else if (StringUtils.equals(checkMode, "F")) {
            authType = "0";
        }

        String SignnatureNumber = "";

        String operResult = "1";
        if (IDataUtil.isNotEmpty(outDataset))
        {
            operResult = "0";

            Object obj = outDataset.get(0);
            if (obj instanceof IData) {
                Object obj1 = ((IData) obj).get("ORDER_ID");
                if (null != obj1) {
                    if (obj1 instanceof String) {
                        SignnatureNumber = (String) obj1;
                    }
                }
            }
        }
        //参数
        String OPER_TIME = auditOperCfg.sysTime;
        String SESSION_ID = StringUtils.defaultIfEmpty(CSBizBean.getVisit().getLoginLogId(), "");
        String OPER_ACCT = StringUtils.defaultIfEmpty(CSBizBean.getVisit().getStaffId(), "");
        String USER_NAME = StringUtils.defaultIfEmpty(CSBizBean.getVisit().getMainId(), "");
        String ROUND_AUDIT = roundAudit;
        String OPER_OBJ = serialNumber;
        String SRC_IP = StringUtils.defaultIfEmpty(CSBizBean.getVisit().getLoginIP(), "");
        String DEST_IP = "10.200.174.118";
        String SRC_PORT = "";
        String DEST_PORT = "80";
        String OPER_TYPE = StringUtils.defaultIfEmpty(auditOperCfg.auditOperType, "");
        String OPER_SUB_TYPE = StringUtils.defaultIfEmpty(auditOperCfg.auditOperSubType, "");
        String OPER = StringUtils.defaultIfEmpty(auditOperCfg.operTypeCode, "");
        String OPER_DESC = StringUtils.defaultIfEmpty(auditOperCfg.operTypeName, "");
        String OPER_RESULT = operResult;
        String WORK_ORDER = "";
        String AMOUNT = amount;
        String APP_ID = StringUtils.defaultIfEmpty(auditOperCfg.auditRsrvstr3, "");
        String APP_NAME = StringUtils.defaultIfEmpty(auditOperCfg.auditRsrvstr2, "");
        String MOD_ID = StringUtils.defaultIfEmpty(auditOperCfg.auditInfoLevel, "");
        String MOD_NAME = "";
        String BATCH_TAG = StringUtils.defaultIfEmpty(auditOperCfg.batchTag, "");
        String IS_AUTO = "0";
        String AUTH_TYPE = authType;
        String SIGNNATURE_NUMBER = SignnatureNumber;
        String FROM_SUB_SYS = StringUtils.defaultIfEmpty(auditOperCfg.fromSubSys, "");
        String FROM_SYS = "BOSS_NEW";



        // 拼装日志数据
        StringBuilder msg = new StringBuilder(500);

        msg.append(OPER_TIME); // OPER_TIME 操作时间
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(SESSION_ID); // 会话ID
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_ACCT); // 操作人账号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(USER_NAME); // 4A主帐号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(ROUND_AUDIT); //是否绕行 Y:绕行 N:非绕行
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_OBJ); // 操作对象帐号, 如手机号码
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(SRC_IP); //源IP
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(DEST_IP); //目的IP
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(SRC_PORT); //源端口
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(DEST_PORT); //目的I端口
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_TYPE); // 操作类型
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_SUB_TYPE); // 操作子类型
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER); // 操作
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_DESC); // 操作描述
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(OPER_RESULT); // 操作结果
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(WORK_ORDER); // 工单号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(AMOUNT); // 金额
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(APP_ID); // 应用编号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(APP_NAME); // 应用名称
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(MOD_ID); // 模块编号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(MOD_NAME); // 模块名称
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(BATCH_TAG); // 判断是否单个办理还是批量办理业务
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(IS_AUTO); // 判断是否程序办理还是个人办理的
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(AUTH_TYPE); // 鉴权方式
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(SIGNNATURE_NUMBER); // 填写电子签名单号
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(FROM_SUB_SYS); // 填写来源子系统
        msg.append(LOG_SEPARATOR_COMMA_1);
        msg.append(FROM_SYS); // 事件所属系统名称

        /*IReadOnlyCache cache = CacheFactory.getReadOnlyCache(LogCfgCache.class);
        LogCfgCache.LogCfg cfg = (LogCfgCache.LogCfg)cache.get(LOG_TYPE_AUDITOPER);
        if (null == cfg) {
            throw new NullPointerException("根据msgType=" + LOG_TYPE_AUDITOPER + ",未取到日志配置信息!");
        }
        InetAddress addr = cfg.getAddr();
        int port = cfg.getServPort();
        byte[] payload = msg.toString().getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(payload, payload.length, addr, port);
        DatagramSocket client = new DatagramSocket();
        client.send(packet);
        client.close();*/

        logger.debug("本地审计开始：" + msg);
        sendLog("2030", "0", 0, 0, msg);
        logger.debug("本地审计结束：" + msg);
    }

    private static void logIvr(CrmOperCfg crmOperCfg, String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 登录工号
        String STAFF_ID = CSBizBean.getVisit().getStaffId();

        // 得到缓存key
        String cacheKey = CacheKey.getIvrKey(STAFF_ID);

        // 如果没有则不记录
        if (StringUtils.isBlank(cacheKey))
        {
            return;
        }

        // 从缓存中取数据
        Object cacheObj = null;

        try
        {
            cacheObj = SharedCache.get(cacheKey);
        }
        catch (Exception e)
        {
            cacheObj = null;

            StringBuilder sb = new StringBuilder("从SharedCache获取IVR缓存数据失败[").append(cacheKey).append("]");

            logger.error(sb);

            return;
        }

        // 如果没有就退出
        if (cacheObj == null)
        {
            return;
        }

        // 默认值
        String opCode = crmOperCfg.opCode;

        // 根据matchkey进行匹配
        if ("key".equals(crmOperCfg.matchKey))
        {
            opCode = inData.getString(crmOperCfg.opCode, "");
        }
        else if ("list".equals(crmOperCfg.matchKey))
        {
            String value = inData.getString(crmOperCfg.opCode, "") + "=";

            String s[] = crmOperCfg.matchValue.split(",");
            String tmp = "";
            int iIndex = 0;
            boolean bFind = false;
            String defVal = "";
            String defStr = "default=";

            for (int i = 0, isize = s.length; i < isize; i++)
            {
                tmp = s[i];
                iIndex = tmp.indexOf(value);

                if (iIndex != -1)
                {
                    opCode = tmp.substring(value.length());

                    bFind = true;
                }

                if (bFind == false)
                {
                    iIndex = tmp.indexOf(defStr);

                    if (iIndex != -1)
                    {
                        defVal = tmp.substring(defStr.length());
                    }
                }
            }

            if (bFind == false)
            {
                opCode = defVal;
            }
        }

        // 得到ivr数据
        IvrData ivrData = (IvrData) cacheObj;

        // 用户标识
        String USER_ID = inData.getString("USER_ID", "");

        // 客户标识
        String CUST_ID = inData.getString("CUST_ID", "");

        // 订单ID
        String TRADE_ID = "";

        if (IDataUtil.isNotEmpty(outDataset))
        {
            Object objData = outDataset.get(0);

            if (objData instanceof IData)
            {
                TRADE_ID = ((IData) objData).getString("ORDER_ID", "");
            }
        }

        // 登录地州编码
        String EPARCHY_CODE = "";
        try
        {
            EPARCHY_CODE = CSBizBean.getTradeEparchyCode();
        }
        catch (Exception e)
        {
        }

        // 用户归属地州
        String UPDATE_EPARCHY_CODE = "";
        try
        {
            UPDATE_EPARCHY_CODE = CSBizBean.getUserEparchyCode();
        }
        catch (Exception e)
        {
        }

        // 拼装日志数据
        StringBuilder msg = new StringBuilder(500);

        msg.append(SeqMgr.getOperId()); // OP_ID 操作流水号
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.SERIAL_NUMBER_B); // SERIAL_NUMBER_B VARCHAR2(20) Y 主叫号码
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.SERIAL_NUMBER); // SERIAL_NUMBER 受理服务号码
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.CALL_LEVEL); // CALL_LEVEL 受理号码等级:0-普通用户;1-红名单;2-黑名单(科创)
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.CALL_EPARCHY_CODE); // CALL_EPARCHY_CODE 主叫号码/呼入号码归属地,默认为ZZZZ
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.IS_NATIVE); // IS_NATIVE 是否本省号码 --0:本省号码 1:外省号码
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(USER_ID); // USER_ID 用户标识
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CUST_ID); // CUST_ID 客户标识
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(TRADE_ID); // TRADE_ID 订单标识，对应TF_F_ORDER表中的ORDER_ID
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getRemoteAddr()); // CLIENT_IP 办理业务的客户端IP
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(opCode); // OP_CODE 业务编码:001xxxxx(查询类);002xxxxx(受理类);003xxxxx(IBOOS类);004xxxxx(短信发送类)
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.opType); // OP_TYPE 操作类型:0-查询,1-受理
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append("0"); // OP_RESULTCODE 操作状态:0-tradeOK,其他失败
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(STAFF_ID); // STAFF_ID 登录工号
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getDepartId()); // DEPART_ID 登录部门
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(EPARCHY_CODE); // EPARCHY_CODE 登录地州编码
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(UPDATE_EPARCHY_CODE); // UPDATE_EPARCHY_CODE 办理业务的服务号码所属地州
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.sysTime); // UPDATE_TIME 更新时间
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ivrData.IVRCALLID); // RSRV_STR1 呼入流水号：科创出入IVRCALLID
        msg.append(LOG_SEPARATOR_COMMA);

        sendLog(LOG_TYPE_OPERLOG, "0", 0, 0, msg);
    }

    private static void logStaffOper(CrmOperCfg crmOperCfg, String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 业务受理月份
        String ACCEPT_DATE = SysDateMgr.getSysDate("MMdd");

        // 操作对象的号码
        String SERIAL_NUMBER = inData.getString("SERIAL_NUMBER", "");

        // 如果sn为"",则记录CUST_ID
        if (StringUtils.isEmpty(SERIAL_NUMBER))
        {
            SERIAL_NUMBER = inData.getString("CUST_ID", "");
        }
        // 如果sn为"",则记录GROUP_ID
        if (StringUtils.isEmpty(SERIAL_NUMBER))
        {
            SERIAL_NUMBER = inData.getString("GROUP_ID", "");
        }
        // 拼装日志数据
        StringBuilder msg = new StringBuilder(500);

        msg.append(crmOperCfg.operTypeCode); // OPER_TYPE_CODE 操作类型编码，对应参数表TD_B_STAFFOPERTYPE
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(SERIAL_NUMBER); // SERIAL_NUMBER 操作对象的号码
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(ACCEPT_DATE); // ACCEPT_DATE 业务受理月份
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getRemoteAddr()); // CLIENT_IP 操作客户端IP
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getLoginMAC()); // CLIENT_MAC 操作客户端MAC
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getInModeCode()); // IN_MODE_CODE 接入渠道
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(crmOperCfg.sysTime); // OPER_TIME 操作时间
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getStaffId()); // TRADE_STAFF_ID 操作员工
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getDepartId()); // TRADE_DEPART_ID 操作员工归属部门
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getCityCode()); // TRADE_CITY_CODE 操作员工归属业务区
        msg.append(LOG_SEPARATOR_COMMA);
        msg.append(CSBizBean.getVisit().getStaffEparchyCode()); // TRADE_EPARCHY_CODE 操作员工归属地州
        msg.append(LOG_SEPARATOR_COMMA);

        sendLog(LOG_TYPE_STAFFOPER, "0", 0, 0, msg);
    }

}
