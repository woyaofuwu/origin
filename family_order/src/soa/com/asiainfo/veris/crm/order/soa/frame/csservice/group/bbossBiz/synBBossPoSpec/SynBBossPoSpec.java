
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossPoSpec;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductPlusQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProvQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePolicysQry;

/**
 * BBOSS商品同步, 商品规格(ProductOfferingSpecification)同步 术语：POSpec 参考：《中国移动通信集团公司一级BOSS枢纽系统接口规范(V1.4.5)》
 * 全称：商品规格(ProductOfferingSpecification) 字段意义：参考接口 POSpecSvcCont 和 POSpecHeader
 * 
 * @author CaoRuiLin
 */
public class SynBBossPoSpec
{
    public static void main(String[] args) throws Exception
    {
        // 测试用
        SynBBossPoSpec pos = new SynBBossPoSpec();
        // String dataStr = "{ORIGDOMAIN=[\"BOSS\"], HOMEDOMAIN=[\"BOSS\"],
        // BIPCODE=[\"BIP4B253\"], ACTIVITYCODE=[\"T4011002\"],
        // TESTFLAG=[\"0\"], X_TRANS_CODE=[\"ITF_CRM_BBossPOSpecSyncIntf\"],
        // KIND_ID=[\"BIP4B253_T4011002_1_0\"],
        // BUSI_SIGN=[\"BIP4B253_T4011002_1_0\"],
        // UIPBUSIID=[\"309060311431100431890\"], BIPVER=[\"0100\"],
        // ACTIONCODE=[\"0\"], ROUTETYPE=[\"01\"], ROUTEVALUE=[\"13987245057\"],
        // PROCID=[\"20090520200813892374\"],
        // TRANSIDO=[\"20090520200823112444\"], TRANSIDH=[\"\"],
        // PROCESSTIME=[\"20090520200823\"], TRANSIDC=[\"\"], CUTOFFDAY=[\"\"],
        // OSNDUNS=[\"\"], HSNDUNS=[\"\"], CONVID=[\"\"], MSGSENDER=[\"8711\"],
        // MSGRECEIVER=[\"8710\"], SVCCONTVER=[\"0100\"], ACTION=[\"1\", \"1\"],
        // RSRV_STR3=[\"15\", \"15\"], RSRV_STR4=[\"51\", \"51\"],
        // STATUS=[\"1\", \"1\"], STARTDATE=[\"18\", \"18\"], ENDDATE=[\"19\",
        // \"19\"], DESCRIPTION=[\"12\", \"12\"], PROVINCE_NO=[[\"1\", \"2\"],
        // [\"1\", \"2\"]], POSPECRATEPOLICY_ID=[[\"11\", \"12\"], [\"11\",
        // \"12\"]], POSPECRATEPOLICY_NAME=[[\"21\", \"22\"], [\"21\", \"22\"]],
        // START_TIME=[[\"1\", \"2\"], [\"1\", \"2\"]], END_TIME=[[\"1\",
        // \"2\"], [\"1\", \"2\"]], PLAN_ID=[[[\"1\", \"4\"], [\"2\"]],
        // [[\"1\"], [\"2\"]]], POICB_NUMBER=[[[[\"41\"], [\"44\"]],
        // [[\"42\"]]], [[[\"41\"]], [[\"42\"]]]], POICB_DESC=[[[[\"1\"],
        // [\"4\"]], [[\"2\"]]], [[[\"1\"]], [[\"2\"]]]],
        // PARA_CODE=[[[[[\"67\"]], [[\"64\"]]], [[[\"62\"]]]], [[[[\"67\"]]],
        // [[[\"62\"]]]]], PARANAME=[[[[[\"1\"]], [[\"4\"]]], [[[\"12\"]]]],
        // [[[[\"1\"]]], [[[\"12\"]]]]], PRO_NUMBER=[[\"23\", \"22\"], [\"23\",
        // \"23\"]], PRO_NAME=[[\"34\", \"32\"], [\"34\", \"34\"]],
        // PRO_STATUS=[[\"1\",\"1\"], [\"1\", \"1\"]], PRO_RATEPLANID=[[[\"98\",
        // \"98\"], [\"98\"]], [[\"98\", \"98\"], [\"98\", \"98\"]]],
        // PRO_ICB=[[[[\"78\"], [\"71\", \"51\"]], [[\"78\"]]], [[[\"78\"],
        // [\"78\"]], [[\"78\"], [\"78\"]]]], PRO_DESC=[[[[\"1\"], [\"1\",
        // \"1\"]], [[\"1\"]]], [[[\"1\"], [\"1\"]], [[\"1\"], [\"1\"]]]],
        // PARA_NUMBER=[[[[[\"9\"]], [[\"7\"], [\"7\", \"8\"]]], [[[\"9\"]]]],
        // [[[[\"9\"]], [[\"9\"]]], [[[\"9\"]], [[\"9\"]]]]],
        // PARA_NAME=[[[[[\"1\"]], [[\"1\"], [\"1\", \"18\"]]], [[[\"1\"]]]],
        // [[[[\"1\"]], [[\"1\"]]], [[[\"1\"]], [[\"1\"]]]]],
        // PRO_CNUMBER=[[[\"1\"], [\"1\"]], [[\"1\"], [\"1\"]]],
        // PRO_CNAME=[[[\"1\"], [\"1\"]], [[\"1\"], [\"1\"]]],
        // PRO_CVALUE=[[[\"*\"], [\"*\"]], [[\"*\"], [\"*\"]]],
        // PRO_SPECIDB=[[[\"7\"], [\"7\"]], [[\"7\"], [\"7\"]]],
        // PRO_RTYPE=[[[\"2\"], [\"2\"]], [[\"2\"], [\"2\"]]],
        // TRADE_CITY_CODE=[\"INTF\"], TRADE_DEPART_ID=[\"00309\"],
        // TRADE_STAFF_ID=[\"IBOSS000\"], TRADE_EPARCHY_CODE=[\"INTF\"],
        // IN_MODE_CODE=[\"6\"]}";
        String dataStr = "{ORIGDOMAIN=[\"BOSS\"], HOMEDOMAIN=[\"BOSS\"], BIPCODE=[\"BIP4B253\"], ACTIVITYCODE=[\"T4011002\"], TESTFLAG=[\"0\"], X_TRANS_CODE=[\"ITF_CRM_BBossPOSpecSyncIntf\"], KIND_ID=[\"BIP4B253_T4011002_1_0\"], BUSI_SIGN=[\"BIP4B253_T4011002_1_0\"], UIPBUSIID=[\"309060311431100431890\"], BIPVER=[\"0100\"], ACTIONCODE=[\"0\"], ROUTETYPE=[\"01\"], ROUTEVALUE=[\"13987245057\"], PROCID=[\"20090520200813892374\"], TRANSIDO=[\"20090520200823112444\"], TRANSIDH=[\"\"], PROCESSTIME=[\"20090520200823\"], TRANSIDC=[\"\"], CUTOFFDAY=[\"\"], OSNDUNS=[\"\"], HSNDUNS=[\"\"], CONVID=[\"\"], MSGSENDER=[\"8711\"], MSGRECEIVER=[\"8710\"], SVCCONTVER=[\"0100\"], ACTION=[\"0\", \"0\"], RSRV_STR3=[\"RSRVSTR1\", \"RSRVSTR2\"], RSRV_STR4=[\"RSRV_STR401\", \"RSRV_STR403\"], STATUS=[\"ss\", \"s\"], STARTDATE=[\"20000101000000\", \"20000101000000\"], ENDDATE=[\"29991231000000\", \"29991231000000\"], DESCRIPTION=[\"DESCRIPTION001\", \"DESCRIPTION002\"], PROVINCE_NO=[[\"010\", \"020\"], [\"030\", \"040\"]], POSPECRATEPOLICY_ID=[[\"POLICY01\", \"POLICY02\"], [\"POLICY03\", \"POLICY04\"]], POSPECRATEPOLICY_NAME=[[\"POLICY_NAME 001\", \"POLICY_NAME 002\"], [\"POLICY_NAME 003\", \"POLICY_NAME 004\"]], START_TIME=[[\"20000101000000\", \"20000101000000\"], [\"20000101000000\", \"20000101000000\"]], END_TIME=[[\"29991231000000\", \"29991231000000\"], [\"29991231000000\", \"29991231000000\"]], PLAN_ID=[[[\"PLAN_ID1\", \"PLAN_ID2\"], [\"PLAN_ID3\"]], [[\"PLAN_ID41\"], [\"PLAN_ID5\"]]], POICB_NUMBER=[[[[\"POICB1\"], [\"POICB2\"]], [[\"POICB3\"]]], [[[\"POICB4\"]], [[\"POICB5\"]]]], POICB_DESC=[[[[\"POICB_DESC1\"], [\"POICB_DESC2\"]], [[\"POICB_DESC3\"]]], [[[\"POICB_DESC4\"]], [[\"POICB_DESC5\"]]]], PARA_CODE=[[[[[\"PARA_CODE1\"]], [[\"PARA_CODE2\"]]], [[[\"PARA_CODE3\"]]]], [[[[\"PARA_CODE4\"]]], [[[\"PARA_CODE5\"]]]]], PARANAME=[[[[[\"PARANAME1\"]], [[\"PARANAME2\"]]], [[[\"PARANAME3\"]]]], [[[[\"PARANAME4\"]]], [[[\"PARANAME5\"]]]]], PRO_NUMBER=[[\"PRONO1\", \"PRONO2\"], [\"PRONO3\", \"PRONO4\"]], PRO_NAME=[[\"PRO_NAME1\", \"PRO_NAME2\"], [\"PRO_NAME3\", \"PRO_NAME4\"]], PRO_STATUS=[[\"s\", \"s\"], [\"s\", \"s\"]], PRO_RATEPLANID=[[[\"PROPLAN1\", \"PROPLAN2\"], [\"PROPLAN3\"]], [[\"PROPLAN4\", \"PROPLAN5\"], [\"PROPLAN6\", \"PROPLAN7\"]]], PRO_ICB=[[[[\"PROICB1\"], [\"PROICB2\", \"PROICB3\"]], [[\"PROICB4\"]]], [[[\"PROICB5\"], [\"PROICB6\"]], [[\"PROICB7\"], [\"PROICB8\"]]]], PRO_DESC=[[[[\"PRO_DESC1\"], [\"PRO_DESC2\", \"PRO_DESC3\"]], [[\"PRO_DESC4\"]]], [[[\"PRO_DESC5\"], [\"PRO_DESC6\"]], [[\"PRO_DESC7\"], [\"PRO_DESC8\"]]]], PARA_NUMBER=[[[[[\"PARA_NUM1\"]], [[\"PARA_NUM2\"], [\"PARA_NUM3\", \"PARA_NUM4\"]]], [[[\"PARA_NUM5\"]]]], [[[[\"PARA_NUM6\"]], [[\"PARA_NUM7\"]]], [[[\"PARA_NUM8\"]], [[\"PARA_NUM9\"]]]]], PARA_NAME=[[[[[\"PARA_NAME1\"]], [[\"PARA_NAME2\"], [\"PARA_NAME3\", \"PARA_NAME4\"]]], [[[\"PARA_NAME5\"]]]], [[[[\"PARA_NAME6\"]], [[\"PARA_NAME7\"]]], [[[\"PARA_NAME8\"]], [[\"PARA_NAME9\"]]]]], PRO_CNUMBER=[[[\"PRO_CN1\"], [\"PRO_CN2\"]], [[\"PRO_CN3\"], [\"PRO_CN4\"]]], PRO_CNAME=[[[\"PRO_CNAME1\"], [\"PRO_CNAME2\"]], [[\"PRO_CNAME3\"], [\"PRO_CNAME4\"]]], PRO_CVALUE=[[[\"PRO_CVALUE1\"], [\"PRO_CVALUE2\"]], [[\"PRO_CVALUE3\"], [\"PRO_CVALUE4\"]]], PRO_SPECIDB=[[[\"SPECIDB1\"], [\"SPECIDB2\"]], [[\"SPECIDB3\"], [\"SPECIDB4\"]]], PRO_RTYPE=[[[\"1\"], [\"2\"]], [[\"3\"], [\"4\"]]], TRADE_CITY_CODE=[\"INTF\"], TRADE_DEPART_ID=[\"00309\"], TRADE_STAFF_ID=[\"IBOSS000\"], TRADE_EPARCHY_CODE=[\"INTF\"], IN_MODE_CODE=[\"6\"]}";
        IData RootData = new DataMap(dataStr);

        pos.processPosSyncInfo(RootData);

    }

    /** 返回码 defalut: 99 --其它错误, 00--受理成功* */
    private String RSPCODE = "00";

    private String RSP_DESC = "OK";

    private String RSP_DESC1 = "OK";

    // 1层结构 如： ACTION=["1"],或ACTION=["0", "1"]
    private String ACTION = ""; // F1 商品规格操作 0-删除 1-增加

    private String RSRV_STR3 = ""; // V9 商品规格编码

    private String RSRV_STR4 = ""; // V40 商品规格名称

    private String STATUS = ""; // F1 商品规格状态 A-正常商用 S-内部测试 T-测试待审 R-试商用

    private String STARTDATE = ""; // F14 商品生效时间 YYYYMMDDHH24MMSS

    private String ENDDATE = ""; // F14 商品失效时间 YYYYMMDDHH24MMSS

    private String DESCRIPTION = ""; // V500 商品规格描述

    // 2层结构 如： POSPECRATEPOLICY_ID=[["010"]] 或 POSPECRATEPOLICY_ID=[["010",
    // "020"]]，或POSPECRATEPOLICY_ID＝[["010", "020"], ["aa"]]
    private String PROVINCE_NO = ""; // 商品开放省,格式如： [["010","020","396"]]

    private String POSPECRATEPOLICY_ID = ""; // V20 资费策略编码

    private String POSPECRATEPOLICY_NAME = ""; // V40 资费策略名称

    private String START_TIME = ""; // F14 资费策略生效时间 YYYYMMDDHH24MMSS

    private String END_TIME = ""; // F14 资费策略失效时间 YYYYMMDDHH24MMSS

    // 3层结构 如:PLAN_ID=[[["PLAN_ID001"],
    // ["PLAN_ID002"]]],或PLAN_ID=[[["PLAN_ID001"]]],或PLAN_ID=[[["PLAN_ID001"],
    // ["PLAN_ID002", "PLAN_ID003"]]]
    private String PLAN_ID = ""; // V20 资费计划标识

    private String PLAN_DESC = ""; // V300 商品描述

    // 4层结构 如
    // POICB_NUMBER=[[[["POICB_NUMBER001"]]]，[[["POICB_NUMBER002"]，["POICB_NUMBER003",
    // "POICB_NUMBER004"]]]]
    private String POICB_NUMBER = ""; // V20 资费模式

    private String POICB_DESC = ""; // V500 资费模式描述

    // 5层结构 如:PARA_CODE=[[[[["PARA_CODE01",
    // "PARA_CODE02"]]]]，[[[["PARA_CODE03"]]，[[""],["PARA_CODE04"]]]]]
    private String PARA_CODE = ""; // V20 参数编码

    private String PARANAME = ""; // V200 参数名

    // 2层结构
    private String PRO_NUMBER = ""; // V7 产品规格编号

    private String PRO_NAME = ""; // V40 产品规格名称

    private String PRO_STATUS = ""; // F1 产品规格状态 A-正常商用 S-内部测试 T-测试待审 R-试商用

    // 3层结构
    private String PRO_RATEPLANID = ""; // V20 资费计划标识

    private String PRO_RATEPLANIDESC = ""; // V300 产品描述

    // 4层结构
    private String PRO_ICB = ""; // V20 资费模式

    private String PRO_DESC = ""; // V500 资费模式描述

    // 5层结构
    private String PARA_NUMBER = ""; // V20 参数编码

    private String PARA_NAME = ""; // V200 参数名

    // 3层结构
    private String PRO_CNUMBER = ""; // V11 产品属性代码

    private String PRO_CNAME = ""; // V40 属性名称

    private String PRO_CVALUE = ""; // V40 属性枚举值取值，可能有多个

    private String PRO_SPECIDB = ""; // V20 产品规格标识

    private String PRO_RTYPE = ""; // F1 依赖关系 1-依赖 2-互斥 3-组成

    // 1层结构
    private IDataset ACTION_List = new DatasetList();

    private IDataset RSRV_STR3_List = new DatasetList();

    private IDataset RSRV_STR4_List = new DatasetList();

    private IDataset STATUS_List = new DatasetList();

    private IDataset STARTDATE_List = new DatasetList();

    private IDataset ENDDATE_List = new DatasetList();

    private IDataset DESCRIPTION_List = new DatasetList();

    // 2层结构
    private IDataset PROVINCE_NO_List = new DatasetList();

    private IDataset POSPECRATEPOLICY_ID_List = new DatasetList();

    private IDataset POSPECRATEPOLICY_NAME_List = new DatasetList();

    private IDataset START_TIME_List = new DatasetList();

    private IDataset END_TIME_List = new DatasetList();

    // 3层结构
    private IDataset PLAN_ID_List = new DatasetList();

    private IDataset PLAN_DESC_List = new DatasetList();

    // 4层结构
    private IDataset POICB_NUMBER_List = new DatasetList();

    private IDataset POICB_DESC_List = new DatasetList();

    // 5层结构
    private IDataset PARA_CODE_List = new DatasetList();

    private IDataset PARANAME_List = new DatasetList();

    // 2层结构
    private IDataset PRO_NUMBER_List = new DatasetList();

    private IDataset PRO_NAME_List = new DatasetList();

    private IDataset PRO_STATUS_List = new DatasetList();

    // 3层结构
    private IDataset PRO_RATEPLANID_List = new DatasetList();

    private IDataset PRO_RATEPLANIDESC_List = new DatasetList();

    // 4层结构
    private IDataset PRO_ICB_List = new DatasetList();

    private IDataset PRO_DESC_List = new DatasetList();

    // 5层结构
    private IDataset PARA_NUMBER_List = new DatasetList();

    private IDataset PARA_NAME_List = new DatasetList();

    // 3层结构
    private IDataset PRO_CNUMBER_List = new DatasetList();

    private IDataset PRO_CNAME_List = new DatasetList();

    private IDataset PRO_CVALUE_List = new DatasetList();

    private IDataset PRO_SPECIDB_List = new DatasetList();

    private IDataset PRO_RTYPE_List = new DatasetList();

    private String SysDate = ""; // 获得当前时间(YYYY-MM-DD HH24:MI:SS)

    private String SysDate4Id = "";// 获取数据库当前时间[YYYYMMDDHH24MISS]

    /**
     * 集中定义 所有数据的 取值 及 验证
     * 
     * @param RootData
     */
    public void checkValue(IData RootData)
    {
        RSP_DESC = "\n";

        /***********************************************************************
         * 单层结构，可以直接解析出数据 如： ACTION=["1"],或ACTION=["0", "1"]
         */
        ACTION = RootData.getString("ACTION"); // F1 商品规格操作 0-删除 1-增加
        if (!isValidDataStr1Layer(ACTION, true, true, 1, ACTION_List, false))
        {
            RSP_DESC += "\t商品规格操作(ACTION) 必选值，长度为1,固定值为0或1\n";
            RSP_DESC1 = "商品规格操作错误";
            RSPCODE = "01";
        }

        RSRV_STR3 = RootData.getString("RSRV_STR3");// V9 商品规格编码
        if (!isValidDataStr1Layer(RSRV_STR3, true, false, 9, RSRV_STR3_List, false))
        {
            RSP_DESC += "\t商品规格编码(RSRV_STR3) 必选值，长度最大值为9\n";
            RSP_DESC1 = "商品规格编码错误";
            RSPCODE = "02";
        }

        RSRV_STR4 = RootData.getString("RSRV_STR4");// V40 商品规格名称
        if (!isValidDataStr1Layer(RSRV_STR4, true, false, 40, RSRV_STR4_List, false))
        {
            RSP_DESC += "\t商品规格名称(RSRV_STR3) 必选值，长度最大值为40\n";
            RSP_DESC1 = "商品规格名称错误";
            RSPCODE = "03";
        }

        STATUS = RootData.getString("STATUS");// F1 商品规格状态 A-正常商用 S-内部测试
        // T-测试待审 R-试商用
        if (!isValidDataStr1Layer(STATUS, true, true, 1, STATUS_List, false))
        {
            RSP_DESC += "\t商品规格状态(STATUS) 必选值，长度为1,固定值为A或S或T或R \n";
            RSP_DESC1 = "商品生命周期状态错误";
            RSPCODE = "15";
        }

        STARTDATE = RootData.getString("STARTDATE");// F14 商品生效时间
        // YYYYMMDDHH24MMSS
        if (!isValidDataStr1Layer(STARTDATE, true, true, 14, STARTDATE_List, true))
        {
            RSP_DESC += "\t商品生效时间(STARTDATE) 必选值，日期格式为YYYYMMDDHH24MMSS 如20000101235959 \n";
            RSP_DESC1 = "生效时间错误";
            RSPCODE = "18";
        }

        ENDDATE = RootData.getString("ENDDATE");// F14 商品失效时间 YYYYMMDDHH24MMSS
        if (!isValidDataStr1Layer(ENDDATE, true, true, 14, ENDDATE_List, true))
        {
            RSP_DESC += "\t商品失效时间(ENDDATE) 必选值，日期格式为YYYYMMDDHH24MMSS 如20000101235959 \n";
            RSP_DESC1 = "失效时间错误";
            RSPCODE = "19";
        }

        DESCRIPTION = RootData.getString("DESCRIPTION");// V500 商品规格描述
        if (!isValidDataStr1Layer(DESCRIPTION, true, false, 500, DESCRIPTION_List, false))
        {
            RSP_DESC += "\t商品规格描述(DESCRIPTION) 可选值，长度最大值为500\n";
            RSP_DESC1 = "商品规格描述错误";
            RSPCODE = "20";
        }

        /***********************************************************************
         * 2层结构，解析数据 如：POSPECRATEPOLICY_ID=[["010"]] 或 POSPECRATEPOLICY_ID=[["010",
         * "020"]]，或POSPECRATEPOLICY_ID＝[["010", "020"], ["aa"]]
         */
        PROVINCE_NO = RootData.getString("PROVINCE_NO");// 商品开放省,格式如：
        // [["010","020","396"]]
        if (!isValidDataStr2Layer(PROVINCE_NO, true, true, 3, PROVINCE_NO_List, false))
        {
            RSP_DESC += "\t商品开放省(PROVINCE_NO) 必选值，格式为\"010\",\"020\",\"396\" 或者 \"010\" \n";
            RSP_DESC1 = "商品开放省错误";
            RSPCODE = "21";
        }

        POSPECRATEPOLICY_ID = RootData.getString("POSPECRATEPOLICY_ID");// V20
        // 资费策略编码
        if (!isValidDataStr2Layer(POSPECRATEPOLICY_ID, true, false, 20, POSPECRATEPOLICY_ID_List, false))
        {
            RSP_DESC += "\t资费策略编码(POSPECRATEPOLICY_ID) 必选值，长度最大值为20\n";
            RSP_DESC1 = "资费策略编码错误";
            RSPCODE = "04";
        }

        POSPECRATEPOLICY_NAME = RootData.getString("POSPECRATEPOLICY_NAME");// V40
        // 资费策略名称
        if (!isValidDataStr2Layer(POSPECRATEPOLICY_NAME, true, false, 20, POSPECRATEPOLICY_NAME_List, false))
        {
            RSP_DESC += "\t资费策略名称(POSPECRATEPOLICY_NAME) 必选值，长度最大值为40\n";
            RSP_DESC1 = "资费策略名称错误";
            RSPCODE = "05";
        }

        START_TIME = RootData.getString("START_TIME");// F14 资费策略生效时间
        // YYYYMMDDHH24MMSS
        if (!isValidDataStr2Layer(START_TIME, true, true, 14, START_TIME_List, true))
        {
            RSP_DESC += "\t资费策略生效时间(START_TIME) 必选值，日期格式为YYYYMMDDHH24MMSS 如20000101235959 \n";
            RSP_DESC1 = "期望生效时间错误";
            RSPCODE = "06";
        }

        END_TIME = RootData.getString("END_TIME");// F14 资费策略失效时间
        // YYYYMMDDHH24MMSS
        if (!isValidDataStr2Layer(END_TIME, true, true, 14, END_TIME_List, true))
        {
            RSP_DESC += "\t资费策略失效时间(END_TIME) 必选值，日期格式为YYYYMMDDHH24MMSS 如20000101235959 \n";
            RSP_DESC1 = "期望生效时间错误";
            RSPCODE = "07";
        }

        /***********************************************************************
         * 3层结构，解析数据 如:PLAN_ID=[[["PLAN_ID001"], ["PLAN_ID002"]]],或PLAN_ID=[[["PLAN_ID001"]]],或PLAN_ID=[[["PLAN_ID001"],
         * ["PLAN_ID002", "PLAN_ID003"]]]
         */
        PLAN_ID = RootData.getString("PLAN_ID");// V20 资费计划标识
        if (!isValidDataStr3Layer(PLAN_ID, true, false, 20, PLAN_ID_List, false))
        {
            RSP_DESC += "\t资费计划标识(PLAN_ID) 可选值，长度最大值为20\n";
            RSP_DESC1 = "资费计划标识错误";
            RSPCODE = "08";
        }

        PLAN_DESC = RootData.getString("PLAN_DESC");// V300 资费描述错误
        if (!isValidDataStr3Layer(PLAN_DESC, false, false, 300, PLAN_DESC_List, false))
        {
            RSP_DESC += "\t资费描述错误(PLAN_DESC) 可选值，长度最大值为300\n";
            RSP_DESC1 = "资费描述错误";
            RSPCODE = "09";
        }

        /***********************************************************************
         * 4层结构，解析数据 如 POICB_NUMBER=[[[["POICB_NUMBER001"]]]，[[["POICB_NUMBER002"]，["POICB_NUMBER003",
         * "POICB_NUMBER004"]]]]
         */
        POICB_NUMBER = RootData.getString("POICB_NUMBER");// V20 资费模式
        if (!isValidDataStr4Layer(POICB_NUMBER, false, false, 20, POICB_NUMBER_List, false))
        {
            RSP_DESC += "\t资费模式(POICB_NUMBER) 可选值，长度最大值为20\n";
            RSP_DESC1 = "ICB资费模式错误";
            RSPCODE = "10";
        }

        POICB_DESC = RootData.getString("POICB_DESC");// V500 资费模式描述
        if (!isValidDataStr4Layer(POICB_DESC, false, false, 500, POICB_DESC_List, false))
        {
            RSP_DESC += "\t资费模式描述(POICB_DESC) 可选值，长度最大值为500\n";
            RSP_DESC1 = "ICB资费模式描述错误";
            RSPCODE = "11";
        }

        /***********************************************************************
         * 5层结构，解析数据 如:PARA_CODE=[[[[["PARA_CODE01", "PARA_CODE02"]]]]，[[[["PARA_CODE03"]]，[[""],["PARA_CODE04"]]]]]
         */
        PARA_CODE = RootData.getString("PARA_CODE");// V20 参数编码
        if (!isValidDataStr5Layer(PARA_CODE, false, false, 20, PARA_CODE_List, false))
        {
            RSP_DESC += "\t参数编码(PARA_CODE) 可选值，长度最大值为20\n";
            RSP_DESC1 = "ICB参数编码错误";
            RSPCODE = "12";
        }

        PARANAME = RootData.getString("PARANAME");// V200 参数名
        if (!isValidDataStr5Layer(PARANAME, false, false, 200, PARANAME_List, false))
        {
            RSP_DESC += "\t参数名(PARANAME) 可选值，长度最大值为200\n";
            RSP_DESC1 = "ICB参数名错误";
            RSPCODE = "13";
        }

        /***********************************************************************
         * 2层结构，解析数据 如：PROVINCE_NO=[["010"]] 或 PROVINCE_NO=[["010","020"]]
         */
        PRO_NUMBER = RootData.getString("PRO_NUMBER");// V7 产品规格编号
        if (!isValidDataStr2Layer(PRO_NUMBER, true, false, 7, PRO_NUMBER_List, false))
        {
            RSP_DESC += "\t产品规格编号(PRO_NUMBER) 必选值，长度最大值为7\n";
            RSP_DESC1 = "产品规格编号错误";
            RSPCODE = "22";
        }

        PRO_NAME = RootData.getString("PRO_NAME");// V40 产品规格名称
        if (!isValidDataStr2Layer(PRO_NAME, true, false, 40, PRO_NAME_List, false))
        {
            RSP_DESC += "\t产品规格名称(PRO_NUMBER) 必选值，长度最大值为40\n";
            RSP_DESC1 = "产品规格名称错误";
            RSPCODE = "23";
        }

        PRO_STATUS = RootData.getString("PRO_STATUS");// F1 产品规格状态 A-正常商用
        // S-内部测试 T-测试待审 R-试商用
        if (!isValidDataStr2Layer(PRO_STATUS, true, true, 1, PRO_STATUS_List, false))
        {
            RSP_DESC += "\t产品规格状态(PRO_STATUS) 必选值，长度为1,固定值为A或S或T或R\n";
            RSP_DESC1 = "产品规格状态错误";
            RSPCODE = "24";
        }

        /***********************************************************************
         * 3层结构，解析数据 如:PLAN_ID=[[["PLAN_ID001"], ["PLAN_ID002"]]],或PLAN_ID=[[["PLAN_ID001"]]],或PLAN_ID=[[["PLAN_ID001"],
         * ["PLAN_ID002", "PLAN_ID003"]]]
         */
        PRO_RATEPLANID = RootData.getString("PRO_RATEPLANID");// V20 资费计划标识
        if (!isValidDataStr3Layer(PRO_RATEPLANID, true, false, 20, PRO_RATEPLANID_List, false))
        {
            RSP_DESC += "\t资费计划标识(PRO_RATEPLANID) 可选值，长度最大值为20\n";
            RSP_DESC1 = "产品级资费计划标识错误";
            RSPCODE = "26";
        }

        PRO_RATEPLANIDESC = RootData.getString("PRO_RATEPLANIDESC");// V300
        // 资费计划描述
        if (!isValidDataStr3Layer(PRO_RATEPLANIDESC, false, false, 300, PRO_RATEPLANIDESC_List, false))
        {
            RSP_DESC += "\t产品级资费描述错误(PRO_RATEPLANIDESC) 可选值，长度最大值为300\n";
            RSP_DESC1 = "产品级资费描述错误";
            RSPCODE = "27";
        }

        /***********************************************************************
         * 4层结构，解析数据 如: POICB_NUMBER=[[[["POICB_NUMBER001"]]]，[[["POICB_NUMBER002"]，["POICB_NUMBER003",
         * "POICB_NUMBER004"]]]]
         */
        PRO_ICB = RootData.getString("PRO_ICB");// V20 资费模式
        if (!isValidDataStr4Layer(PRO_ICB, false, false, 20, PRO_ICB_List, false))
        {
            RSP_DESC += "\t资费模式(PRO_ICB) 可选值，长度最大值为20\n";
            RSP_DESC1 = "产品级ICB资费模式错误";
            RSPCODE = "28";
        }

        PRO_DESC = RootData.getString("PRO_DESC");// V500 资费模式描述
        if (!isValidDataStr4Layer(PRO_DESC, false, false, 500, PRO_DESC_List, false))
        {
            RSP_DESC += "\t资费模式描述(PRO_ICB) 可选值，长度最大值为500\n";
            RSP_DESC1 = "产品级ICB资费模式描述错误";
            RSPCODE = "29";
        }

        /***********************************************************************
         * 5层结构，解析数据 如:PARA_CODE=[[[[["PARA_CODE01", "PARA_CODE02"]]]]，[[[["PARA_CODE03"]]，[[""],["PARA_CODE04"]]]]]
         */
        PARA_NUMBER = RootData.getString("PARA_NUMBER");// V20 参数编码
        if (!isValidDataStr5Layer(PARA_NUMBER, false, false, 20, PARA_NUMBER_List, false))
        {
            RSP_DESC += "\t参数编码(PARA_NUMBER) 可选值，长度最大值为20\n";
            RSP_DESC1 = "产品级ICB参数编码错误";
            RSPCODE = "30";
        }

        PARA_NAME = RootData.getString("PARA_NAME");// V200 参数名
        if (!isValidDataStr5Layer(PARA_NAME, false, false, 200, PARA_NAME_List, false))
        {
            RSP_DESC += "\t参数名(PARA_NAME) 可选值，长度最大值为200\n";
            RSP_DESC1 = "产品级ICB参数名错误";
            RSPCODE = "31";
        }

        /***********************************************************************
         * 3层结构，解析数据 如:PLAN_ID=[[["PLAN_ID001"], ["PLAN_ID002"]]],或PLAN_ID=[[["PLAN_ID001"]]],或PLAN_ID=[[["PLAN_ID001"],
         * ["PLAN_ID002", "PLAN_ID003"]]]
         */
        PRO_CNUMBER = RootData.getString("PRO_CNUMBER");// V11 产品属性代码
        if (!isValidDataStr3Layer(PRO_CNUMBER, true, false, 11, PRO_CNUMBER_List, false))
        {
            RSP_DESC += "\t产品属性代码(PRO_CNUMBER) 必选值，长度最大值为11\n";
            RSP_DESC1 = "产品属性代码错误";
            RSPCODE = "33";
        }

        PRO_CNAME = RootData.getString("PRO_CNAME");// V40 属性名称
        if (!isValidDataStr3Layer(PRO_CNAME, true, false, 40, PRO_CNAME_List, false))
        {
            RSP_DESC += "\t属性名称(PRO_CNAME) 必选值，长度最大值为40\n";
            RSP_DESC1 = "产品属性名称错误";
            RSPCODE = "34";
        }

        PRO_CVALUE = RootData.getString("PRO_CVALUE");// V40 属性枚举值取值，可能有多个
        if (!isValidDataStr3Layer(PRO_CVALUE, true, false, 40, PRO_CVALUE_List, false))
        {
            RSP_DESC += "\t属性枚举值取值(PRO_CVALUE) 必选值，长度最大值为40\n";
            RSP_DESC1 = "产品属性枚举值取值错误";
            RSPCODE = "35";
        }

        PRO_SPECIDB = RootData.getString("PRO_SPECIDB");// V20 产品规格标识
        if (!isValidDataStr3Layer(PRO_SPECIDB, false, false, 20, PRO_SPECIDB_List, false))
        {
            RSP_DESC += "\t产品规格标识(PRO_SPECIDB) 可选值，长度最大值为20\n";
            RSP_DESC1 = "关联产品规格标识错误";
            RSPCODE = "36";
        }

        PRO_RTYPE = RootData.getString("PRO_RTYPE");// F1 依赖关系 1-依赖 2-互斥 3-组成
        if (!isValidDataStr3Layer(PRO_RTYPE, false, false, 1, PRO_RTYPE_List, false))
        {
            RSP_DESC += "\t依赖关系(PRO_RTYPE) 可选值，长度为1,固定值为1或2或3\n";
            RSP_DESC1 = "关联产品依赖关系错误";
            RSPCODE = "37";
        }

        /***********************************************************************
         * 其他判断,判断一些主要号码中是否存在相同号码（主键冲突）
         */
        /*
         * if(ACTION_List.size()==1){//当为更新时会有两条重复记录，不能通过验证，现暂只判断只有一条记录的情况，多条记录有重复时数据库会抛错误 if(
         * isExistsSameStr(RSRV_STR3)) RSP_DESC+= "\t RSRV_STR3数据中存在相同号码\n"; if( isExistsSameStr(POSPECRATEPOLICY_ID))
         * RSP_DESC+= "\t POSPECRATEPOLICY_ID数据中存在相同号码\n"; if( isExistsSameStr(PLAN_ID)) RSP_DESC+=
         * "\t PLAN_ID数据中存在相同号码\n"; if( isExistsSameStr(POICB_NUMBER)) RSP_DESC+= "\t POICB_NUMBER数据中存在相同号码\n"; if(
         * isExistsSameStr(PARA_CODE)) RSP_DESC+= "\t PARA_CODE数据中存在相同号码\n"; if( isExistsSameStr(PRO_NUMBER)) RSP_DESC+=
         * "\t PRO_NUMBER数据中存在相同号码\n"; if( isExistsSameStr(PRO_RATEPLANID)) RSP_DESC+= "\t PRO_RATEPLANID数据中存在相同号码\n";
         * if( isExistsSameStr(PRO_ICB)) RSP_DESC+= "\t PRO_ICB数据中存在相同号码\n"; if( isExistsSameStr(PARA_NUMBER))
         * RSP_DESC+= "\t PARA_NUMBER数据中存在相同号码\n"; if( isExistsSameStr(PRO_SPECIDB)) RSP_DESC+=
         * "\t PRO_SPECIDB数据中存在相同号码\n"; }
         */
        /*********************************************************************************************************************************
		 *
		 */
        if (RSP_DESC.length() > 2)
        {// 表示的格式错误
            // RSPCODE = "99";
        }
        else
        { // 无格式错误
            RSPCODE = "00";
            RSP_DESC = "OK";
            RSP_DESC1 = "OK";
        }
        // logger.debug("========Called :CheckValue() Fineshed! RSP_DESC
        // is:=============\n"+RSP_DESC);
    }

    /**
     * 得到老的产品规格信息,数据库表TD_F_POPRODUCT
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getOldPOProductInfos(IData RootData) throws Exception
    {
        IDataset POProductInfos = new DatasetList();// 产品规格信息列表 return;
        for (int i = 0; i < PRO_NUMBER_List.size(); i++)
        {
            String POSpecNumber = (String) RSRV_STR3_List.get(i);
            IDataset ProNumber_list = PRO_NUMBER_List.getDataset(i);
            IDataset POProductInfo = new DatasetList();
            for (int j = 0; j < ProNumber_list.size(); j++)
            {
                String ProductSpecNumber = (String) ProNumber_list.get(j);
                IData data = new DataMap();
                data.put("POSpecNumber".toUpperCase(), POSpecNumber);// 序号18.1.1.2：POSpecNumber
                // 商品规格编码
                // 1 V9
                data.put("ProductSpecNumber".toUpperCase(), ProductSpecNumber);// 序号18.1.1.10.1.1.1：ProductSpecNumber
                // 产品规格编号
                // 1 V7
                /*
                 * data.put("ProductSpecName".toUpperCase(), ProductSpecName);//序号18.1.1.10.1.1.2：ProductSpecName 产品规格名称
                 * 1 V40 data.put("Status".toUpperCase(), Status);//序号18.1.1.10.1.1.3：Status 产品规格状态 1 V40 (A-正常商用 S-内部测试
                 * T-测试待审 R-试商用 ) data.put("DESCRIPTION".toUpperCase(), Description);
                 * data.put("UPDATE_TIME".toUpperCase(), SysDate); if(null!=PRO_SPECIDB_List){ IDataset ProSpecIDB_list
                 * = (IDataset) PRO_SPECIDB_List.get(i); IDataset ProRType_list = (IDataset) PRO_RTYPE_List.get(i);
                 * IDataset ProSpecIDB_list1 = (IDataset) ProSpecIDB_list.get(0); IDataset ProRType_list1 = (IDataset)
                 * ProRType_list.get(0); if( ProSpecIDB_list1!=null && ProSpecIDB_list1.size()>0){ String
                 * PRODUCTSPECID_B = (String) ProSpecIDB_list1.get(0); String RelationType = (String)
                 * ProRType_list1.get(0); data.put("PRODUCTSPECID_B".toUpperCase(),
                 * PRODUCTSPECID_B);//序号18.1.1.10.1.4.1：ProductSpecID_B 产品规格标识 1 V20
                 * data.put("RelationType".toUpperCase(), RelationType);//序号18.1.1.10.1.4.2：RelationType 依赖关系 1 F1 (
                 * 1-依赖 ,2-互斥 ,3-组成) } }
                 */
                POProductInfo.add(PoProductQry.getPoProductInfoByPK(data));
            }
            POProductInfos.add(POProductInfo);
        }
        return POProductInfos;
    }

    /**
     * 得到老的产品补充信息表，数据库表TD_F_POPRODUCTPLUS
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getOldPOProductPluss(IData RootData) throws Exception
    {
        IDataset POProductPluss = new DatasetList();
        for (int i = 0; i < PRO_CNUMBER_List.size(); i++)
        {
            IDataset ProCNumber_list = PRO_CNUMBER_List.getDataset(i);
            IDataset ProCValue_list = PRO_CVALUE_List.getDataset(i);
            IDataset ProNumber_list = PRO_NUMBER_List.getDataset(i);
            IDataset POProductPlus = new DatasetList();
            for (int j = 0; j < ProCNumber_list.size(); j++)
            {
                String ProductSpecNumber = (String) ProNumber_list.get(j);
                IDataset ProCNumber_list1 = ProCNumber_list.getDataset(j);
                IDataset ProCValue_list1 = ProCValue_list.getDataset(j);
                for (int k = 0; k < ProCNumber_list1.size(); k++)
                {
                    String ProductSpecCharacterNumber = (String) ProCNumber_list1.get(k);
                    String ValueSource = (String) ProCValue_list1.get(k);
                    ValueSource = ValueSource.replaceAll(",", ";"); // 转换为用;分开的字段
                    IData data = new DataMap();
                    data.put("ProductSpecNumber".toUpperCase(), ProductSpecNumber);
                    data.put("PRODUCTSPECCHARACTERNUMBER".toUpperCase(), ProductSpecCharacterNumber);// 序号18.1.1.10.1.3.1.1：ProductSpecCharacterNumber
                    // 产品属性代码
                    // 1
                    // V11
                    // data.put("Name".toUpperCase(),
                    // ProductSpecCharacter_Name);//序号18.1.1.10.1.3.1.12：Name
                    // 属性名称 1 V40
                    // data.put("ValueSource".toUpperCase(),
                    // ValueSource);//序号18.1.1.10.1.3.1.3：ValueSource
                    // 属性枚举值取值，可能有多个 1 V40
                    // data.put("UPDATE_TIME".toUpperCase(), SysDate);
                    POProductPlus.add(PoProductPlusQry.getPoProductPlusInfoByParams(data));
                }
            }
            POProductPluss.add(POProductPlus);
        }
        return POProductPluss;
    }

    /**
     * 得到老商品开放省,数据库表TD_F_POPRIV
     * 
     * @param RootData
     * @return
     */
    public IDataset getOldPOProvinceInfos(IData RootData) throws Exception
    {
        IDataset POProvinceInfos = new DatasetList();
        for (int i = 0, sizei = ACTION_List.size(); i < sizei; i++)
        {
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset Province_No_list = PROVINCE_NO_List.getDataset(i);
            IDataset dataset = new DatasetList();
            for (int j = 0, sizej = Province_No_list.size(); j < sizej; j++)
            {
                String ENABLECOMPANYID = Province_No_list.get(j).toString();
                IData data = new DataMap();
                data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);
                data.put("ENABLECOMPANYID".toUpperCase(), ENABLECOMPANYID);
                dataset.add(PoProvQry.getPoProvInfoByPK(data));
            }
            POProvinceInfos.add(dataset);
        }

        return POProvinceInfos;
    }

    /**
     * 得到老的资费计划信息【TD_F_PORATEPLANICB】
     * 
     * @param RootData
     * @return
     * @throws Exception
     */

    public IDataset getOldPORatePlanICBs(IData RootData) throws Exception
    {
        IDataset PORatePlanICBs = new DatasetList();

        IDataset PORatePlanICBs1 = new DatasetList();
        IDataset PORatePlanICBs2 = new DatasetList();
        /***********************************************************************
         * 商品部分
         */
        for (int i = 0; i < PARA_CODE_List.size(); i++)
        {
            IDataset ParaCode_list = PARA_CODE_List.getDataset(i);
            IDataset POICB_list = POICB_NUMBER_List.getDataset(i);
            IDataset PLANID_list = PLAN_ID_List.getDataset(i);
            IDataset PORatePlanICB = new DatasetList();
            for (int j = 0; j < ParaCode_list.size(); j++)
            {
                IDataset ParaCode_list2 = ParaCode_list.getDataset(j);
                IDataset POICB_list2 = POICB_list.getDataset(j);
                IDataset PLANID_list2 = PLANID_list.getDataset(j);
                for (int k = 0; k < ParaCode_list2.size(); k++)
                {
                    String RATEPLANID = PLANID_list2.get(k).toString();
                    IDataset ParaCode_list3 = ParaCode_list2.getDataset(k);
                    IDataset POICB_list3 = POICB_list2.getDataset(k);
                    for (int m = 0; m < ParaCode_list3.size(); m++)
                    {
                        String ICB_NO = POICB_list3.get(m).toString();
                        IDataset ParaCode_list4 = ParaCode_list3.getDataset(m);
                        for (int n = 0; n < ParaCode_list4.size(); n++)
                        {
                            String PARAMETERNUMBER = (String) ParaCode_list4.get(n);
                            IData data = new DataMap();
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID);
                            data.put("ICB_NO".toUpperCase(), ICB_NO);
                            data.put("PARAMETER_NO".toUpperCase(), PARAMETERNUMBER);
                            PORatePlanICB.add(PoRatePlanIcbQry.getPoRatePlanIcbInfoByPK(data));
                        }
                    }
                }
            }
            PORatePlanICBs1.add(PORatePlanICB);
        }
        /***********************************************************************
         * 产品部分
         */
        for (int i = 0; i < PARA_NUMBER_List.size(); i++)
        {
            IDataset ParaNumber_List = PARA_NUMBER_List.getDataset(i);
            IDataset ProICB_list = PRO_ICB_List.getDataset(i);
            IDataset ProRateId_list = PRO_RATEPLANID_List.getDataset(i);
            IDataset PORatePlanICB = new DatasetList();
            for (int j = 0; j < ParaNumber_List.size(); j++)
            {
                IDataset ParaNumber_List2 = ParaNumber_List.getDataset(j);
                IDataset ProICB_list2 = ProICB_list.getDataset(j);
                IDataset ProRateId_list2 = ProRateId_list.getDataset(j);
                for (int k = 0; k < ParaNumber_List2.size(); k++)
                {
                    String RATEPLANID2 = (String) ProRateId_list2.get(k);
                    IDataset ParaNumber_List3 = ParaNumber_List2.getDataset(k);
                    IDataset ProICB_list3 = ProICB_list2.getDataset(k);
                    for (int m = 0; m < ParaNumber_List3.size(); m++)
                    {
                        String ICB_NO2 = (String) ProICB_list3.get(m);
                        IDataset ParaNumber_List4 = ParaNumber_List3.getDataset(m);
                        for (int n = 0; n < ParaNumber_List4.size(); n++)
                        {
                            String PARAMETERNUMBER2 = (String) ParaNumber_List4.get(n);
                            IData data = new DataMap();
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID2);
                            data.put("ICB_NO".toUpperCase(), ICB_NO2);
                            data.put("PARAMETER_NO".toUpperCase(), PARAMETERNUMBER2);
                            PORatePlanICB.add(PoRatePlanIcbQry.getPoRatePlanIcbInfoByPK(data));
                        }
                    }
                }
            }
            PORatePlanICBs2.add(PORatePlanICB);
        }
        // 分类汇总到ACTION_List.size()个Dataset中去
        for (int i = 0; i < ACTION_List.size(); i++)
        {
            IDataset ds = new DatasetList();
            IDataset ds1 = PORatePlanICBs1.getDataset(i);
            IDataset ds2 = PORatePlanICBs2.getDataset(i);
            for (int j = 0; j < ds1.size(); j++)
            {
                ds.add(ds1.getData(j));
            }
            for (int j = 0; j < ds2.size(); j++)
            {
                ds.add(ds2.getData(j));
            }
            PORatePlanICBs.add(ds);
        }

        return PORatePlanICBs;
    }

    /**
     * 得到老的商品级和产品级资费计划,数据库表TD_F_PORATEPLAN
     * 
     * @param RootData
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset getOldPORatePlans(IData RootData) throws Exception
    {
        IDataset PORatePlans = new DatasetList();

        IDataset PORatePlans1 = new DatasetList();
        IDataset PORatePlans2 = new DatasetList();
        getOldPOSpecRatePlans(RootData, PORatePlans1);
        getOldProductRatePlans(RootData, PORatePlans2);

        for (int i = 0, sizei = ACTION_List.size(); i < sizei; i++)
        {
            IDataset ds = new DatasetList();
            IDataset ds1 = PORatePlans1.getDataset(i);
            IDataset ds2 = PORatePlans2.getDataset(i);
            for (int j = 0, sizej = ds1.size(); j < sizej; j++)
            {
                ds.add(ds1.getData(j));
            }
            for (int j = 0, sizej = ds2.size(); j < sizej; j++)
            {
                ds.add(ds2.getData(j));
            }
            PORatePlans.add(ds);
        }

        return PORatePlans;
    }

    /**
     * 得到老的商品资费策略信息【TD_F_PORATEPOLICYS】
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getOldPORatePolicys(IData RootData) throws Exception
    {
        IDataset PORatePolicys = new DatasetList();
        /***********************************************************************
         * 商品部分资费策略信息,产品部分没有资费策略，但有资费计划
         */
        for (int i = 0, sizei = POSPECRATEPOLICY_ID_List.size(); i < sizei; i++)
        {
            String POSpecNumber = RSRV_STR3_List.get(i).toString();

            IDataset POS_ID_list = POSPECRATEPOLICY_ID_List.getDataset(i);
            IDataset dataset = new DatasetList();
            for (int j = 0, sizej = POS_ID_list.size(); j < sizej; j++)
            {

                String POSPECRATEPOLICY_ID = POS_ID_list.get(j).toString();

                IData data = new DataMap();
                data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);
                data.put("POSPECRATEPOLICYID".toUpperCase(), POSPECRATEPOLICY_ID);
                dataset.add(PoRatePolicysQry.getPoRatePolicysInfoByPK(data));
            }
            PORatePolicys.add(dataset);
        }

        return PORatePolicys;
    }

    /**
     * 获得老的商品级资费计划，并赋值到参数PORatePlans中
     * 
     * @author weixb3
     * @param RootData
     * @param PORatePlans
     * @throws Exception
     */
    public void getOldPOSpecRatePlans(IData RootData, IDataset PORatePlans) throws Exception
    {

        /**
         * POICB_NUMBER_List.size()==PLAN_ID_List.size()==POSPECRATEPOLICY_ID_List.size()==ACTION_List.size()
         * PLAN_ID_List结构＝＝POICB_NUMBER_List结构＝＝POICB_DESC_List结构
         */
        String RATETYPE = "0";// 商品级标识0,产品级标识1
        for (int i = 0, sizei = POICB_NUMBER_List.size(); i < sizei; i++)
        {// 获得商品级资费计划，并赋值到参数PORatePlans中
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset POICB_Number_list = POICB_NUMBER_List.getDataset(i);
            IDataset POICB_Desc_list = POICB_DESC_List.getDataset(i);
            IDataset Plan_Id_list = PLAN_ID_List.getDataset(i);
            IDataset Plan_DESC_list = PLAN_DESC_List.getDataset(i);
            IDataset POSpecRatePolicy_Id_list = POSPECRATEPOLICY_ID_List.getDataset(i);
            IDataset PORatePlanset = new DatasetList();
            for (int j = 0, sizej = POICB_Number_list.size(); j < sizej; j++)
            {
                String POSPECRATEPOLICY_ID = POSpecRatePolicy_Id_list.get(j).toString();
                IDataset POICB_Number_list2 = POICB_Number_list.getDataset(j);
                IDataset POICB_Desc_list2 = POICB_Desc_list.getDataset(j);
                IDataset Plan_Id_list2 = Plan_Id_list.getDataset(j);
                IDataset Plan_DESC_list2 = Plan_DESC_list.getDataset(j);
                for (int k = 0, sizek = POICB_Number_list2.size(); k < sizek; k++)
                {
                    String PLAN_ID = Plan_Id_list2.get(k).toString();
                    String PLAN_DESC = Plan_DESC_list2.get(k).toString();
                    IDataset POICB_Number_list3 = POICB_Number_list2.getDataset(k);
                    IDataset POICB_Desc_list3 = POICB_Desc_list2.getDataset(k);
                    if (IDataUtil.isNotEmpty(POICB_Number_list3))
                    {
                        for (int m = 0, sizem = POICB_Number_list3.size(); m < sizem; m++)
                        {
                            String POICB_NUMBER = POICB_Number_list3.get(m).toString();
                            String POICB_DESC = POICB_Desc_list3.get(m).toString();
                            IData data = new DataMap();
                            data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                            data.put("PORATENUMBER".toUpperCase(), POSPECRATEPOLICY_ID);// 商品资费策略编码
                            data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                            data.put("RATEPLANID".toUpperCase(), PLAN_ID);// 商品资费计划编码
                            data.put("ICB_NO".toUpperCase(), POICB_NUMBER);// 商品资费计划下的
                            // 资费模式编码
                            data.put("RATEPLANDESCRIPTION".toUpperCase(), POICB_DESC);// 商品资费计划下的
                            // 资费模式描述
                            data.put("ICBDESCRIPTION".toUpperCase(), PLAN_DESC);// 商品资费计划下的
                            // 资费描述
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            if (null != PoRatePlanQry.getPoRatePlanInfoByPK(data))
                            {
                                PORatePlanset.add(PoRatePlanQry.getPoRatePlanInfoByPK(data));
                            }
                            else
                            {
                                PORatePlanset.add(data);
                            }
                        }
                    }
                    else
                    {
                        IData data = new DataMap();
                        data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                        data.put("PORATENUMBER".toUpperCase(), POSPECRATEPOLICY_ID);// 商品资费策略编码
                        data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                        data.put("RATEPLANID".toUpperCase(), PLAN_ID);// 商品资费计划编码
                        PORatePlanset.add(PoRatePlanQry.getPoRatePalnInfoByParams(data));
                    }
                }
            }
            PORatePlans.add(PORatePlanset);
        }

    }

    /**
     * 获得老的产品级资费计划，并赋值到参数PORatePlans中
     * 
     * @author weixb3
     * @param RootData
     * @param PORatePlans
     * @throws Exception
     */
    public void getOldProductRatePlans(IData RootData, IDataset PORatePlans) throws Exception
    {

        String RATETYPE = "1";// 商品级标识0,产品级标识1
        // ICB参数可以没有,但有RatePlan，而ICB_NO为TD_F_PORATEPLAN主键，当没有时ICB_NO为操作时间加流水号
        for (int i = 0, sizei = PRO_ICB_List.size(); i < sizei; i++)
        {// 获得产品级资费计划，并赋值到参数PORatePlans中
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset ProICB_list = PRO_ICB_List.getDataset(i);
            IDataset ProDesc_list = PRO_DESC_List.getDataset(i);
            IDataset Pro_RatePlanID_list = PRO_RATEPLANID_List.getDataset(i);
            IDataset PRO_RATEPLANIDESC_list = PRO_RATEPLANIDESC_List.getDataset(i);
            IDataset Pro_Number_list = PRO_NUMBER_List.getDataset(i);
            IDataset PORatePlan = new DatasetList();
            for (int j = 0, sizej = ProICB_list.size(); j < sizej; j++)
            {
                String ProductSpecNumber = Pro_Number_list.get(j).toString();
                IDataset ProICB_list2 = ProICB_list.getDataset(j);
                IDataset ProDesc_list2 = ProDesc_list.getDataset(j);
                IDataset Pro_RatePlanID_list2 = Pro_RatePlanID_list.getDataset(j);
                IDataset PRO_RATEPLANIDESC_list2 = PRO_RATEPLANIDESC_list.getDataset(j);
                for (int k = 0; k < ProICB_list2.size(); k++)
                {
                    String RATEPLANID = Pro_RatePlanID_list2.get(k).toString();
                    String PRO_RATEPLANIDESC = (String) PRO_RATEPLANIDESC_list2.get(k);
                    IDataset ProICB_list3 = ProICB_list2.getDataset(k);
                    IDataset ProDesc_list3 = ProDesc_list2.getDataset(k);
                    if (IDataUtil.isNotEmpty(ProICB_list3))
                    {
                        for (int m = 0, sizem = ProICB_list3.size(); m < sizem; m++)
                        {
                            String ICB_NO = ProICB_list3.get(m).toString();
                            String RATEPLANDESCRIPTION = ProDesc_list3.get(m).toString();
                            IData data = new DataMap();
                            data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                            data.put("PORATENUMBER".toUpperCase(), ProductSpecNumber);// 商品资费策略编码
                            data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID);// 商品资费计划编码
                            data.put("ICB_NO".toUpperCase(), ICB_NO);// 商品资费计划下的
                            // 资费模式编码
                            data.put("RATEPLANDESCRIPTION".toUpperCase(), RATEPLANDESCRIPTION);// 商品资费计划下的
                            // 资费模式描述
                            data.put("ICBDESCRIPTION".toUpperCase(), PRO_RATEPLANIDESC);// 商品资费计划下的
                            // 资费描述
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            if (null != PoRatePlanQry.getPoRatePlanInfoByPK(data))
                            {
                                PORatePlan.add(PoRatePlanQry.getPoRatePlanInfoByPK(data));
                            }
                            else
                            {
                                PORatePlan.add(data);
                            }
                        }
                    }
                    else
                    {
                        IData data = new DataMap();
                        data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                        data.put("PORATENUMBER".toUpperCase(), ProductSpecNumber);// 商品资费策略编码
                        data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                        data.put("RATEPLANID".toUpperCase(), RATEPLANID);// 商品资费计划编码
                        PORatePlan.add(PoRatePlanQry.getPoRatePalnInfoByParams(data));
                    }
                }
            }
            PORatePlans.add(PORatePlan);
        }
    }

    /**
     * 商品规格信息,数据库表TD_F_PO
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getPOInfos(IData RootData) throws Exception
    {
        IDataset POInfos = new DatasetList();

        for (int i = 0, size = ACTION_List.size(); i < size; i++)
        {
            String Action = ACTION_List.get(i).toString();
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            String POSpecName = RSRV_STR4_List.get(i).toString();
            String Status = STATUS_List.get(i).toString();
            String STARTDATE = STARTDATE_List.get(i).toString();
            String ENDDATE = ENDDATE_List.get(i).toString();
            String DESCRIPTION = DESCRIPTION_List.get(i).toString();

            IData data = new DataMap();
            data.put("POSpecNumber".toUpperCase(), POSpecNumber);// 序号18.1.1.2：POSpecNumber
            // 商品规格编码 1
            // V9
            data.put("POSpecName".toUpperCase(), POSpecName);// 序号18.1.1.3：POSpecName
            // 商品规格名称 1 V40
            // ( )
            data.put("Action".toUpperCase(), Action);// 序号18.1.1.1：Action
            // 商品规格操作 1 F1
            // (0-删除,1-增加,当为变更时，则有两条记录，第一条为删除旧属性值（Action=0）;第二条为新增一个属性值（action=1）
            data.put("Status".toUpperCase(), Status);// 序号18.1.1.4：Status
            // 商品规格状态 1 F1 ( A-正常商用,
            // S-内部测试, T-测试待审, R-试商用
            // )
            data.put("STARTDATE".toUpperCase(), STARTDATE);// 序号18.1.1.5：StartDate
            // 商品生效时间 1 F14
            // (YYYYMMDDHH24MMSS)
            data.put("ENDDATE".toUpperCase(), ENDDATE);// 序号18.1.1.6：EndDate
            // 商品失效时间 1 F14
            // (YYYYMMDDHH24MMSS)
            data.put("DESCRIPTION".toUpperCase(), DESCRIPTION);// 序号18.1.1.7：Description
            // 商品规格描述 1 V500
            data.put("update_time".toUpperCase(), SysDate);//
            POInfos.add(data);
        }

        return POInfos;
    }

    /**
     * 产品规格信息,数据库表TD_F_POPRODUCT
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getPOProductInfos(IData RootData) throws Exception
    {
        IDataset POProductInfos = new DatasetList();// 产品规格信息列表 return;

        for (int i = 0, sizei = PRO_NUMBER_List.size(); i < sizei; i++)
        {
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset ProNumber_list = PRO_NUMBER_List.getDataset(i);
            IDataset ProName_list = PRO_NAME_List.getDataset(i);
            IDataset ProStatus_list = PRO_STATUS_List.getDataset(i);
            IDataset POProductInfo = new DatasetList();
            for (int j = 0, sizej = ProNumber_list.size(); j < sizej; j++)
            {
                String ProductSpecNumber = ProNumber_list.get(j).toString();
                String ProductSpecName = ProName_list.get(j).toString();
                String Status = ProStatus_list.get(j).toString();
                String Description = ProductSpecName; // 描述与名称相同

                IData data = new DataMap();
                data.put("POSpecNumber".toUpperCase(), POSpecNumber);// 序号18.1.1.2：POSpecNumber
                // 商品规格编码
                // 1 V9
                data.put("ProductSpecNumber".toUpperCase(), ProductSpecNumber);// 序号18.1.1.10.1.1.1：ProductSpecNumber
                // 产品规格编号
                // 1 V7
                data.put("ProductSpecName".toUpperCase(), ProductSpecName);// 序号18.1.1.10.1.1.2：ProductSpecName
                // 产品规格名称
                // 1
                // V40
                data.put("Status".toUpperCase(), Status);// 序号18.1.1.10.1.1.3：Status
                // 产品规格状态 1 V40
                // (A-正常商用 S-内部测试
                // T-测试待审 R-试商用 )
                data.put("DESCRIPTION".toUpperCase(), Description);
                data.put("UPDATE_TIME".toUpperCase(), SysDate);
                if (IDataUtil.isNotEmpty(PRO_SPECIDB_List))
                {
                    IDataset ProSpecIDB_list = PRO_SPECIDB_List.getDataset(i);
                    IDataset ProRType_list = PRO_RTYPE_List.getDataset(i);

                    IDataset ProSpecIDB_list1 = ProSpecIDB_list.getDataset(0);
                    IDataset ProRType_list1 = ProRType_list.getDataset(0);
                    if (null != ProSpecIDB_list1 && IDataUtil.isNotEmpty(ProSpecIDB_list1))
                    {
                        String PRODUCTSPECID_B = ProSpecIDB_list1.get(0).toString();
                        String RelationType = ProRType_list1.get(0).toString();
                        data.put("PRODUCTSPECID_B".toUpperCase(), PRODUCTSPECID_B);// 序号18.1.1.10.1.4.1：ProductSpecID_B
                        // 产品规格标识
                        // 1
                        // V20
                        data.put("RelationType".toUpperCase(), RelationType);// 序号18.1.1.10.1.4.2：RelationType
                        // 依赖关系
                        // 1 F1
                        // (
                        // 1-依赖
                        // ,2-互斥
                        // ,3-组成)
                    }
                }
                POProductInfo.add(data);
            }
            POProductInfos.add(POProductInfo);
        }
        return POProductInfos;
    }

    /**
     * 产品补充信息表，数据库表TD_F_POPRODUCTPLUS
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getPOProductPluss(IData RootData) throws Exception
    {
        IDataset POProductPluss = new DatasetList();

        for (int i = 0, sizei = PRO_CNUMBER_List.size(); i < sizei; i++)
        {
            IDataset ProCNumber_list = PRO_CNUMBER_List.getDataset(i);
            IDataset ProCName_list = PRO_CNAME_List.getDataset(i);
            IDataset ProCValue_list = PRO_CVALUE_List.getDataset(i);
            IDataset ProNumber_list = PRO_NUMBER_List.getDataset(i);
            IDataset POProductPlus = new DatasetList();
            for (int j = 0, sizej = ProCNumber_list.size(); j < sizej; j++)
            {
                String ProductSpecNumber = ProNumber_list.get(j).toString();
                IDataset ProCNumber_list1 = ProCNumber_list.getDataset(j);
                IDataset ProCName_list1 = ProCName_list.getDataset(j);
                IDataset ProCValue_list1 = ProCValue_list.getDataset(j);
                for (int k = 0, sizek = ProCNumber_list1.size(); k < sizek; k++)
                {
                    String ProductSpecCharacterNumber = ProCNumber_list1.get(k).toString();
                    String ProductSpecCharacter_Name = ProCName_list1.get(k).toString();
                    String ValueSource = ProCValue_list1.get(k).toString();
                    ValueSource = ValueSource.replaceAll(",", ";"); // 转换为用;分开的字段
                    IData data = new DataMap();
                    data.put("ProductSpecNumber".toUpperCase(), ProductSpecNumber);
                    data.put("PRODUCTSPECCHARACTERNUMBER".toUpperCase(), ProductSpecCharacterNumber);// 序号18.1.1.10.1.3.1.1：ProductSpecCharacterNumber
                    // 产品属性代码
                    // 1
                    // V11
                    data.put("Name".toUpperCase(), ProductSpecCharacter_Name);// 序号18.1.1.10.1.3.1.12：Name
                    // 属性名称
                    // 1
                    // V40
                    data.put("ValueSource".toUpperCase(), ValueSource);// 序号18.1.1.10.1.3.1.3：ValueSource
                    // 属性枚举值取值，可能有多个
                    // 1 V40
                    data.put("UPDATE_TIME".toUpperCase(), SysDate);
                    POProductPlus.add(data);
                }
            }
            POProductPluss.add(POProductPlus);
        }
        return POProductPluss;
    }

    /**
     * 商品开放省,数据库表TD_F_POPRIV
     * 
     * @param RootData
     * @return
     */
    public IDataset getPOProvinceInfos(IData RootData)
    {
        IDataset POProvinceInfos = new DatasetList();

        for (int i = 0, sizei = ACTION_List.size(); i < sizei; i++)
        {
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset Province_No_list = PROVINCE_NO_List.getDataset(i);
            IDataset dataset = new DatasetList();
            for (int j = 0, sizej = Province_No_list.size(); j < sizej; j++)
            {
                String ENABLECOMPANYID = Province_No_list.get(j).toString();
                IData data = new DataMap();
                data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);
                data.put("ENABLECOMPANYID".toUpperCase(), ENABLECOMPANYID);
                data.put("UPDATE_TIME".toUpperCase(), SysDate);
                dataset.add(data);
            }
            POProvinceInfos.add(dataset);
        }

        return POProvinceInfos;
    }

    public IDataset getPORatePlanICBs(IData RootData) throws Exception
    {
        IDataset PORatePlanICBs = new DatasetList();

        IDataset PORatePlanICBs1 = new DatasetList();
        IDataset PORatePlanICBs2 = new DatasetList();
        /***********************************************************************
         * 商品部分
         */
        for (int i = 0, sizei = PARA_CODE_List.size(); i < sizei; i++)
        {
            IDataset ParaCode_list = PARA_CODE_List.getDataset(i);
            IDataset ParaName_list = PARANAME_List.getDataset(i);
            IDataset POICB_list = POICB_NUMBER_List.getDataset(i);
            IDataset PLANID_list = PLAN_ID_List.getDataset(i);
            IDataset PORatePlanICB = new DatasetList();
            for (int j = 0, sizej = ParaCode_list.size(); j < sizej; j++)
            {
                IDataset ParaCode_list2 = ParaCode_list.getDataset(j);
                IDataset ParaName_list2 = ParaName_list.getDataset(j);
                IDataset POICB_list2 = POICB_list.getDataset(j);
                IDataset PLANID_list2 = PLANID_list.getDataset(j);
                for (int k = 0, sizek = ParaCode_list2.size(); k < sizek; k++)
                {
                    String RATEPLANID = PLANID_list2.get(k).toString();
                    IDataset ParaCode_list3 = ParaCode_list2.getDataset(k);
                    IDataset ParaName_list3 = ParaName_list2.getDataset(k);
                    IDataset POICB_list3 = POICB_list2.getDataset(k);
                    for (int m = 0, sizem = ParaCode_list3.size(); m < sizem; m++)
                    {
                        String ICB_NO = POICB_list3.get(m).toString();
                        IDataset ParaCode_list4 = ParaCode_list3.getDataset(m);
                        IDataset ParaName_list4 = ParaName_list3.getDataset(m);
                        for (int n = 0, sizen = ParaCode_list4.size(); n < sizen; n++)
                        {
                            String PARAMETERNUMBER = ParaCode_list4.get(n).toString();
                            String PARAMETERNAME = ParaName_list4.get(n).toString();
                            IData data = new DataMap();
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID);
                            data.put("ICB_NO".toUpperCase(), ICB_NO);
                            data.put("PARAMETER_NO".toUpperCase(), PARAMETERNUMBER);
                            data.put("PARAMETERNAME".toUpperCase(), PARAMETERNAME);
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            PORatePlanICB.add(data);
                        }
                    }
                }
            }
            PORatePlanICBs1.add(PORatePlanICB);
        }
        /***********************************************************************
         * 产品部分
         */
        for (int i = 0, sizei = PARA_NUMBER_List.size(); i < sizei; i++)
        {
            IDataset ParaNumber_List = PARA_NUMBER_List.getDataset(i);
            IDataset ParaName_List = PARA_NAME_List.getDataset(i);
            IDataset ProICB_list = PRO_ICB_List.getDataset(i);
            IDataset ProRateId_list = PRO_RATEPLANID_List.getDataset(i);
            IDataset PORatePlanICB = new DatasetList();
            for (int j = 0, sizej = ParaNumber_List.size(); j < sizej; j++)
            {
                IDataset ParaNumber_List2 = ParaNumber_List.getDataset(j);
                IDataset ParaName_List2 = ParaName_List.getDataset(j);
                IDataset ProICB_list2 = ProICB_list.getDataset(j);
                IDataset ProRateId_list2 = ProRateId_list.getDataset(j);
                for (int k = 0, sizek = ParaNumber_List2.size(); k < sizek; k++)
                {
                    String RATEPLANID2 = ProRateId_list2.get(k).toString();
                    IDataset ParaNumber_List3 = ParaNumber_List2.getDataset(k);
                    IDataset ParaName_List3 = ParaName_List2.getDataset(k);
                    IDataset ProICB_list3 = ProICB_list2.getDataset(k);
                    for (int m = 0, sizem = ParaNumber_List3.size(); m < sizem; m++)
                    {
                        String ICB_NO2 = ProICB_list3.get(m).toString();
                        IDataset ParaNumber_List4 = ParaNumber_List3.getDataset(m);
                        IDataset ParaName_List4 = ParaName_List3.getDataset(m);
                        for (int n = 0, sizen = ParaNumber_List4.size(); n < sizen; n++)
                        {
                            String PARAMETERNUMBER2 = ParaNumber_List4.get(n).toString();
                            String PARAMETERNAME2 = ParaName_List4.get(n).toString();
                            IData data = new DataMap();
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID2);
                            data.put("ICB_NO".toUpperCase(), ICB_NO2);
                            data.put("PARAMETER_NO".toUpperCase(), PARAMETERNUMBER2);
                            data.put("PARAMETERNAME".toUpperCase(), PARAMETERNAME2);
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            PORatePlanICB.add(data);
                        }
                    }
                }
            }
            PORatePlanICBs2.add(PORatePlanICB);
        }
        // 分类汇总到ACTION_List.size()个Dataset中去
        for (int i = 0, sizei = ACTION_List.size(); i < sizei; i++)
        {
            IDataset ds = new DatasetList();
            IDataset ds1 = PORatePlanICBs1.getDataset(i);
            IDataset ds2 = PORatePlanICBs2.getDataset(i);
            for (int j = 0, sizej = ds1.size(); j < sizej; j++)
            {
                ds.add(ds1.getData(j));
            }
            for (int j = 0, sizej = ds2.size(); j < sizej; j++)
            {
                ds.add(ds2.getData(j));
            }
            PORatePlanICBs.add(ds);
        }

        return PORatePlanICBs;
    }

    /**
     * 商品级和产品级资费计划,数据库表TD_F_PORATEPLAN
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getPORatePlans(IData RootData) throws Exception
    {
        IDataset PORatePlans = new DatasetList();

        IDataset PORatePlans1 = new DatasetList();
        IDataset PORatePlans2 = new DatasetList();
        getPOSpecRatePlans(RootData, PORatePlans1);
        getProductRatePlans(RootData, PORatePlans2);

        for (int i = 0, sizei = ACTION_List.size(); i < sizei; i++)
        {
            IDataset ds = new DatasetList();
            IDataset ds1 = PORatePlans1.getDataset(i);
            IDataset ds2 = PORatePlans2.getDataset(i);
            for (int j = 0, sizej = ds1.size(); j < sizej; j++)
            {
                ds.add(ds1.getData(j));
            }
            for (int j = 0, sizej = ds2.size(); j < sizej; j++)
            {
                ds.add(ds2.getData(j));
            }
            PORatePlans.add(ds);
        }

        return PORatePlans;
    }

    /**
     * 商品资费策略信息【TD_F_PORATEPOLICYS】
     * 
     * @param RootData
     * @return
     * @throws Exception
     */
    public IDataset getPORatePolicys(IData RootData) throws Exception
    {
        IDataset PORatePolicys = new DatasetList();

        /***********************************************************************
         * 商品部分资费策略信息,产品部分没有资费策略，但有资费计划
         */
        for (int i = 0, sizei = POSPECRATEPOLICY_ID_List.size(); i < sizei; i++)
        {
            String POSpecNumber = RSRV_STR3_List.get(i).toString();

            IDataset POS_ID_list = POSPECRATEPOLICY_ID_List.getDataset(i);
            IDataset POS_Name_list = POSPECRATEPOLICY_NAME_List.getDataset(i);
            IDataset Start_Time_list = START_TIME_List.getDataset(i);
            IDataset End_Time_list = END_TIME_List.getDataset(i);
            IDataset dataset = new DatasetList();
            for (int j = 0, sizej = POS_ID_list.size(); j < sizej; j++)
            {

                String POSPECRATEPOLICY_ID = POS_ID_list.get(j).toString();
                String POSPECRATEPOLICY_NAME = POS_Name_list.get(j).toString();
                String START_TIME = Start_Time_list.get(j).toString();
                String END_TIME = End_Time_list.get(j).toString();

                IData data = new DataMap();
                data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);
                data.put("POSPECRATEPOLICYID".toUpperCase(), POSPECRATEPOLICY_ID);
                data.put("NAME".toUpperCase(), POSPECRATEPOLICY_NAME);
                data.put("STARTDATE".toUpperCase(), START_TIME);
                data.put("ENDDATE".toUpperCase(), END_TIME);
                data.put("UPDATE_TIME".toUpperCase(), SysDate);
                dataset.add(data);
            }
            PORatePolicys.add(dataset);
        }

        return PORatePolicys;
    }

    /**
     * 获得商品级资费计划，并赋值到参数PORatePlans中
     * 
     * @param RootData
     * @param PORatePlans
     * @throws Exception
     */
    public void getPOSpecRatePlans(IData RootData, IDataset PORatePlans) throws Exception
    {

        /**
         * POICB_NUMBER_List.size()==PLAN_ID_List.size()==POSPECRATEPOLICY_ID_List.size()==ACTION_List.size()
         * PLAN_ID_List结构＝＝POICB_NUMBER_List结构＝＝POICB_DESC_List结构
         */
        String RATETYPE = "0";// 商品级标识0,产品级标识1
        for (int i = 0; i < POICB_NUMBER_List.size(); i++)
        {// 获得商品级资费计划，并赋值到参数PORatePlans中
            String POSpecNumber = (String) RSRV_STR3_List.get(i);
            IDataset POICB_Number_list = POICB_NUMBER_List.getDataset(i);
            IDataset POICB_Desc_list = POICB_DESC_List.getDataset(i);
            IDataset Plan_Id_list = PLAN_ID_List.getDataset(i);
            IDataset Plan_DESC_list = PLAN_DESC_List.getDataset(i);
            IDataset POSpecRatePolicy_Id_list = POSPECRATEPOLICY_ID_List.getDataset(i);
            IDataset PORatePlanset = new DatasetList();
            for (int j = 0; j < POICB_Number_list.size(); j++)
            {
                String POSPECRATEPOLICY_ID = (String) POSpecRatePolicy_Id_list.get(j);
                IDataset POICB_Number_list2 = POICB_Number_list.getDataset(j);
                IDataset POICB_Desc_list2 = POICB_Desc_list.getDataset(j);
                IDataset Plan_Id_list2 = Plan_Id_list.getDataset(j);
                IDataset Plan_DESC_list2 = Plan_DESC_list.getDataset(j);
                for (int k = 0; k < POICB_Number_list2.size(); k++)
                {
                    String PLAN_ID = (String) Plan_Id_list2.get(k);
                    String PLAN_DESC = (String) Plan_DESC_list2.get(k);
                    IDataset POICB_Number_list3 = POICB_Number_list2.getDataset(k);
                    IDataset POICB_Desc_list3 = POICB_Desc_list2.getDataset(k);
                    if (POICB_Number_list3.size() > 0)
                    {
                        for (int m = 0; m < POICB_Number_list3.size(); m++)
                        {
                            String POICB_NUMBER = (String) POICB_Number_list3.get(m);
                            String POICB_DESC = (String) POICB_Desc_list3.get(m);
                            IData data = new DataMap();
                            data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                            data.put("PORATENUMBER".toUpperCase(), POSPECRATEPOLICY_ID);// 商品资费策略编码
                            data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                            data.put("RATEPLANID".toUpperCase(), PLAN_ID);// 商品资费计划编码
                            data.put("ICB_NO".toUpperCase(), POICB_NUMBER);// 商品资费计划下的
                            // 资费模式编码
                            data.put("RATEPLANDESCRIPTION".toUpperCase(), POICB_DESC);// 商品资费计划下的
                            // 资费模式描述
                            data.put("ICBDESCRIPTION".toUpperCase(), PLAN_DESC);// 商品资费计划下的
                            // 资费描述
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            PORatePlanset.add(data);
                        }
                    }
                    else
                    {
                        IData data = new DataMap();
                        data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                        data.put("PORATENUMBER".toUpperCase(), POSPECRATEPOLICY_ID);// 商品资费策略编码
                        data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                        data.put("RATEPLANID".toUpperCase(), PLAN_ID);// 商品资费计划编码
                        data.put("ICB_NO".toUpperCase(), SysDate4Id + (k + 1));// 商品资费计划下的
                        // 资费模式编码
                        data.put("RATEPLANDESCRIPTION".toUpperCase(), "");// 商品资费计划下的
                        // 资费模式描述
                        data.put("ICBDESCRIPTION".toUpperCase(), PLAN_DESC);// 商品资费计划下的
                        // 资费描述
                        data.put("UPDATE_TIME".toUpperCase(), SysDate);
                        PORatePlanset.add(data);
                    }
                }
            }
            PORatePlans.add(PORatePlanset);
        }

    }

    /**
     * 获得产品级资费计划，并赋值到参数PORatePlans中
     * 
     * @param RootData
     * @param PORatePlans
     * @throws Exception
     */
    public void getProductRatePlans(IData RootData, IDataset PORatePlans) throws Exception
    {

        String RATETYPE = "1";// 商品级标识0,产品级标识1
        // ICB参数可以没有,但有RatePlan，而ICB_NO为TD_F_PORATEPLAN主键，当没有时ICB_NO为操作时间加流水号
        for (int i = 0, sizei = PRO_ICB_List.size(); i < sizei; i++)
        {// 获得产品级资费计划，并赋值到参数PORatePlans中
            String POSpecNumber = RSRV_STR3_List.get(i).toString();
            IDataset ProICB_list = PRO_ICB_List.getDataset(i);
            IDataset ProDesc_list = PRO_DESC_List.getDataset(i);
            IDataset Pro_RatePlanID_list = PRO_RATEPLANID_List.getDataset(i);
            IDataset PRO_RATEPLANIDESC_list = PRO_RATEPLANIDESC_List.getDataset(i);
            IDataset Pro_Number_list = PRO_NUMBER_List.getDataset(i);
            IDataset PORatePlan = new DatasetList();
            for (int j = 0, sizej = ProICB_list.size(); j < sizej; j++)
            {
                String ProductSpecNumber = Pro_Number_list.get(j).toString();
                IDataset ProICB_list2 = ProICB_list.getDataset(j);
                IDataset ProDesc_list2 = ProDesc_list.getDataset(j);
                IDataset Pro_RatePlanID_list2 = Pro_RatePlanID_list.getDataset(j);
                IDataset PRO_RATEPLANIDESC_list2 = PRO_RATEPLANIDESC_list.getDataset(j);
                for (int k = 0, sizek = ProICB_list2.size(); k < sizek; k++)
                {
                    String RATEPLANID = Pro_RatePlanID_list2.get(k).toString();
                    String PRO_RATEPLANIDESC = PRO_RATEPLANIDESC_list2.get(k).toString();
                    IDataset ProICB_list3 = ProICB_list2.getDataset(k);
                    IDataset ProDesc_list3 = ProDesc_list2.getDataset(k);
                    if (!ProICB_list3.isEmpty())
                    {
                        for (int m = 0, sizem = ProICB_list3.size(); m < sizem; m++)
                        {
                            String ICB_NO = ProICB_list3.get(m).toString();
                            String RATEPLANDESCRIPTION = ProDesc_list3.get(m).toString();
                            IData data = new DataMap();
                            data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                            data.put("PORATENUMBER".toUpperCase(), ProductSpecNumber);// 商品资费策略编码
                            data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                            data.put("RATEPLANID".toUpperCase(), RATEPLANID);// 商品资费计划编码
                            data.put("ICB_NO".toUpperCase(), ICB_NO);// 商品资费计划下的
                            // 资费模式编码
                            data.put("RATEPLANDESCRIPTION".toUpperCase(), RATEPLANDESCRIPTION);// 商品资费计划下的
                            // 资费模式描述
                            data.put("ICBDESCRIPTION".toUpperCase(), PRO_RATEPLANIDESC);// 商品资费计划下的
                            // 资费描述
                            data.put("UPDATE_TIME".toUpperCase(), SysDate);
                            PORatePlan.add(data);
                        }
                    }
                    else
                    {
                        IData data = new DataMap();
                        data.put("POSPECNUMBER".toUpperCase(), POSpecNumber);// 商品规格编码
                        data.put("PORATENUMBER".toUpperCase(), ProductSpecNumber);// 商品资费策略编码
                        data.put("RATETYPE".toUpperCase(), RATETYPE);// 商品级标识0,产品级标识1
                        data.put("RATEPLANID".toUpperCase(), RATEPLANID);// 商品资费计划编码
                        data.put("ICB_NO".toUpperCase(), SysDate4Id + String.valueOf(j) + String.valueOf(k));// 商品资费计划下的
                        // 资费模式编码
                        data.put("RATEPLANDESCRIPTION".toUpperCase(), "");// 商品资费计划下的
                        // 资费模式描述
                        data.put("ICBDESCRIPTION".toUpperCase(), PRO_RATEPLANIDESC);// 商品资费计划下的
                        // 资费描述
                        data.put("UPDATE_TIME".toUpperCase(), SysDate);
                        PORatePlan.add(data);
                    }
                }
            }
            PORatePlans.add(PORatePlan);
        }
    }

    /**
     * 判断是否日期类型 日期格式为YYYYMMDDHH24MMSS 如20000101235959
     * 
     * @param str
     * @return
     */
    public boolean isDateType(String str)
    {
        try
        {
            if (null == str || 14 != str.length())
                return false;

            String YY = str.substring(0, 4);
            String MM = str.substring(4, 6);
            String DD = str.substring(6, 8);
            String HH24 = str.substring(8, 10);
            String mm = str.substring(10, 12);
            String ss = str.substring(12, 14);

            if (Integer.valueOf(YY) < 1000)
                return false;
            if (Integer.valueOf(MM) <= 0 || Integer.valueOf(MM) > 12)
                return false;

            int day = Integer.valueOf(DD);
            if (day <= 0)
                return false;

            if (MM.equals("01") || MM.equals("03") || MM.equals("05") || MM.equals("07") || MM.equals("08") || MM.equals("10") || MM.equals("12"))
            {
                if (day > 31)
                    return false;
            }
            else if (MM.equals("04") || MM.equals("06") || MM.equals("09") || MM.equals("11"))
            {
                if (day > 30)
                    return false;
            }
            else if (MM.equals("02"))
            {
                int year = Integer.valueOf(YY);
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                {
                    if (day > 28)
                        return false;
                }
                else
                {
                    if (day > 27)
                        return false;
                }
            }
            else
            {
                return false;
            }

            if (Integer.valueOf(DD) <= 0 || Integer.valueOf(DD) > 31)
                return false;
            if (Integer.valueOf(HH24) < 0 || Integer.valueOf(HH24) > 23)
                return false;
            if (Integer.valueOf(mm) < 0 || Integer.valueOf(mm) > 59)
                return false;
            if (Integer.valueOf(ss) < 0 || Integer.valueOf(ss) > 59)
                return false;

        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * 判断一些主要号码中是否存在相同号码（主键冲突）
     * 
     * @param str
     * @return
     */
    public boolean isExistsSameStr(String str)
    {
        if (null == str || str.trim().equals(""))
            return false;
        String[] strArray = str.split("\"");
        for (int i = 1; i < strArray.length; i += 2)
        {
            String tmpStr = strArray[i];
            for (int j = i + 2; j < strArray.length; j += 2)
            {
                String nextStr = strArray[j];
                if (tmpStr.equals(nextStr))
                    return true;
            }
        }
        return false;
    }

    /**
     * 数据验证Facade方法 目前没有采用，只是为了上面能够更好的掌握各个数据的层次关系！
     * 
     * @param strData
     * @param isMustFlag
     * @param isFixedLength
     * @param strSize
     * @param IDataset
     * @param isDateType
     * @return
     */
    public boolean isValidDataStr(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        if (strData.length() < 5 || strData.indexOf("[") < 0)
            return isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);// 只有一个元素的直接验证
        if (strData.substring(0, 5).equals("[[[[["))
        {
            return this.isValidDataStr5Layer(strData, isMustFlag, isFixedLength, strSize, IDataset, isDateType);
        }
        else if (strData.substring(0, 4).equals("[[[["))
        {
            return this.isValidDataStr4Layer(strData, isMustFlag, isFixedLength, strSize, IDataset, isDateType);
        }
        else if (strData.substring(0, 3).equals("[[["))
        {
            return this.isValidDataStr3Layer(strData, isMustFlag, isFixedLength, strSize, IDataset, isDateType);
        }
        else if (strData.substring(0, 2).equals("[["))
        {
            return this.isValidDataStr2Layer(strData, isMustFlag, isFixedLength, strSize, IDataset, isDateType);
        }
        else if (strData.substring(0, 1).equals("["))
        {
            return this.isValidDataStr1Layer(strData, isMustFlag, isFixedLength, strSize, IDataset, isDateType);
        }
        return false;

    }

    /**
     * 1层结构，解析数据 如：ACTION=["1"] 或 ACTION=["1", "1"] 传入的strData数据结构要为["1"]或["1", "1"]这种结构 并给传入数组设值
     * 
     * @param strData
     *            传入的数据
     * @param isMustFlag
     *            是否必须
     * @param isFixedLength
     *            是否变量（或固定值）
     * @param strSize
     *            数据最大长度（或固定值的固定长度）
     * @param IDataset
     *            设置 字符数组 的值
     * @param isDateType
     *            是否14位的固定日期格式为YYYYMMDDHH24MMSS 如20000101235959
     * @return
     */
    public boolean isValidDataStr1Layer(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        try
        {
            if (strData.indexOf("[") < 0)
            { // 当传入字符串中不包含“[”标识时，表示只有一个元素，而不是一个复合数据结构
                boolean flag = isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);
                if (flag)
                {
                    if (null != IDataset)
                        IDataset.add(strData);
                }
                return flag;
            }
            /**
             * *********************************************************************************************************
             * *****************************
             */
            IDataset ds = new DatasetList(strData);
            ds = Wade3DataTran.wade3To4Dataset(ds);
            if (IDataUtil.isEmpty(ds) && isMustFlag)
                return false;// 不能为空

            for (int j = 0, sizej = ds.size(); j < sizej; j++)
            {// 2层
                String tmpStr = ds.get(j).toString();

                if (!isValidStr(tmpStr, isMustFlag, isFixedLength, strSize, isDateType))
                    return false;
                if (null != IDataset)
                    IDataset.add(tmpStr);
                // 设值
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 2层结构，解析数据 如：PROVINCE_NO=[["010"]] 或 PROVINCE_NO=[["010","020"]] 传入的strData数据结构要为[["1","2","3"]]或[["1", "3"],
     * ["2"]]这种结构 并给传入数组设值
     * 
     * @param strData
     *            传入的数据
     * @param isMustFlag
     *            是否必须
     * @param isFixedLength
     *            是否变量（或固定值）
     * @param strSize
     *            数据最大长度（或固定值的固定长度）
     * @param IDataset
     *            设置 字符数组 的值
     * @param isDateType
     *            是否14位的固定日期格式为YYYYMMDDHH24MMSS 如20000101235959
     * @return
     */
    public boolean isValidDataStr2Layer(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        try
        {
            if (strData.indexOf("[") < 0)
            { // 当传入字符串中不包含“[”标识时，表示只有一个元素，而不是一个复合数据结构
                boolean flag = isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);
                if (flag)
                {
                    if (null != IDataset)
                        IDataset.add(strData);
                }
                return flag;
            }
            /**
             * *********************************************************************************************************
             * *****************************
             */
            IDataset ds = new DatasetList(strData);
            ds = Wade3DataTran.wade3To4Dataset(ds);
            if (IDataUtil.isEmpty(ds) && isMustFlag)
                return false;// 不能为空

            for (int i = 0, sizei = ds.size(); i < sizei; i++)
            {// 1层
                IDataset ds2 = (IDataset) ds.get(i);
                IDataset list2 = new DatasetList();
                for (int j = 0, sizej = ds2.size(); j < sizej; j++)
                {// 2层
                    String tmpStr = ds2.get(j).toString();

                    if (!isValidStr(tmpStr, isMustFlag, isFixedLength, strSize, isDateType))
                        return false;
                    list2.add(tmpStr);
                }
                if (null != IDataset)
                    IDataset.add(list2);
                // 设值
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 3层结构，解析数据 如:PLAN_ID=[[["PLAN_ID001"], ["PLAN_ID002"]]], 或PLAN_ID=[[["PLAN_ID001"]]], 或PLAN_ID=[[["PLAN_ID001"],
     * ["PLAN_ID002", "PLAN_ID003"]]] 传入的strData数据结构要为[[["PLAN_ID001"],
     * ["PLAN_ID002"]]]或[[["PLAN_ID001"]]]或[[["PLAN_ID001"], ["PLAN_ID002", "PLAN_ID003"]]]这种结构 并给传入List设值
     * 
     * @param strData
     * @param isMustFlag
     * @param isFixedLength
     * @param strSize
     * @param strArray
     * @param isDateType
     * @param postion
     * @return
     */
    public boolean isValidDataStr3Layer(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        try
        {
            if (strData.indexOf("[") < 0)
            { // 当传入字符串中不包含“[”标识时，表示只有一个元素，而不是一个复合数据结构
                return isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);
            }
            /**
             * *********************************************************************************************************
             * *****************************
             */
            IDataset ds = new DatasetList(strData);
            ds = Wade3DataTran.wade3To4Dataset(ds);
            if (IDataUtil.isEmpty(ds) && isMustFlag)

                return false;// 不能为空

            for (int i = 0, sizei = ds.size(); i < sizei; i++)
            {// 1层
                IDataset list2 = new DatasetList();
                IDataset ds2 = ds.getDataset(i);
                for (int j = 0, sizej = ds2.size(); j < sizej; j++)
                {// 2层
                    IDataset ds3 = ds2.getDataset(j);
                    IDataset list3 = new DatasetList();
                    for (int k = 0, sizek = ds3.size(); k < sizek; k++)
                    {// 3层
                        String tmpStr = ds3.get(k).toString();

                        // 仅用于测试输出
                        if (!isValidStr(tmpStr, isMustFlag, isFixedLength, strSize, isDateType))
                            return false;
                        list3.add(tmpStr);
                    }
                    list2.add(list3);
                }
                if (null != IDataset)
                    IDataset.add(list2);
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 4层结构，解析数据 如:POICB_NUMBER=[[[[\"POICB_NUMBER001\"]]], [[[\"POICB_NUMBER002\"], [\"POICB_NUMBER003\",
     * \"POICB_NUMBER004\"]]]] 传入的strData数据结构要为[[[[\"POICB_NUMBER001\"]]], [[[\"POICB_NUMBER002\"],
     * [\"POICB_NUMBER003\", \"POICB_NUMBER004\"]]]]这种结构 并给传入List设值
     * 
     * @param strData
     * @param isMustFlag
     * @param isFixedLength
     * @param strSize
     * @param IDataset
     * @param isDateType
     * @return
     */
    public boolean isValidDataStr4Layer(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        try
        {
            if (strData.indexOf("[") < 0)
            { // 当传入字符串中不包含“[”标识时，表示只有一个元素，而不是一个复合数据结构
                return isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);
            }
            /**
             * *********************************************************************************************************
             * *****************************
             */
            IDataset ds = new DatasetList(strData);
            ds = Wade3DataTran.wade3To4Dataset(ds);
            if (IDataUtil.isEmpty(ds) && isMustFlag)
                return false;// 不能为空

            for (int i = 0, sizei = ds.size(); i < sizei; i++)
            { // 1层
                IDataset list2 = new DatasetList();
                IDataset ds2 = ds.getDataset(i);
                for (int j = 0, sizej = ds2.size(); j < sizej; j++)
                { // 2层
                    IDataset ds3 = ds2.getDataset(j);
                    IDataset list3 = new DatasetList();
                    for (int k = 0, sizek = ds3.size(); k < sizek; k++)
                    { // 3层
                        IDataset ds4 = ds3.getDataset(k);
                        IDataset list4 = new DatasetList();
                        for (int m = 0, sizem = ds4.size(); m < sizem; m++)
                        { // 4层
                            String tmpStr = ds4.get(m).toString();

                            // 仅用于测试输出
                            if (!isValidStr(tmpStr, isMustFlag, isFixedLength, strSize, isDateType))
                                return false;
                            list4.add(tmpStr);
                        }
                        list3.add(list4);
                    }
                    list2.add(list3);
                }
                if (null != IDataset)
                    IDataset.add(list2);
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 5层结构，解析数据 如:POICB_NUMBER=[[[[\"POICB_NUMBER001\"]]], [[[\"POICB_NUMBER002\"], [\"POICB_NUMBER003\",
     * \"POICB_NUMBER004\"]]]] 传入的strData数据结构要为[[[[\"POICB_NUMBER001\"]]], [[[\"POICB_NUMBER002\"],
     * [\"POICB_NUMBER003\", \"POICB_NUMBER004\"]]]]这种结构 并给传入List设值
     * 
     * @param strData
     * @param isMustFlag
     * @param isFixedLength
     * @param strSize
     * @param IDataset
     * @param isDateType
     * @return
     */
    public boolean isValidDataStr5Layer(String strData, boolean isMustFlag, boolean isFixedLength, int strSize, IDataset IDataset, boolean isDateType)
    {

        try
        {
            if (strData.indexOf("[") < 0)
            { // 当传入字符串中不包含“[”标识时，表示只有一个元素，而不是一个复合数据结构
                return isValidStr(strData, isMustFlag, isFixedLength, strSize, isDateType);
            }
            /**
             * *********************************************************************************************************
             * *****************************
             */
            IDataset ds = new DatasetList(strData);
            ds = Wade3DataTran.wade3To4Dataset(ds);
            if (IDataUtil.isEmpty(ds) && isMustFlag)
                return false;// 不能为空

            for (int i = 0, sizei = ds.size(); i < sizei; i++)
            { // 1层
                IDataset list2 = new DatasetList();
                IDataset ds2 = ds.getDataset(i);
                for (int j = 0, sizej = ds2.size(); j < sizej; j++)
                { // 2层
                    IDataset ds3 = ds2.getDataset(j);
                    IDataset list3 = new DatasetList();
                    for (int k = 0, sizek = ds3.size(); k < sizek; k++)
                    { // 3层
                        IDataset ds4 = ds3.getDataset(k);
                        IDataset list4 = new DatasetList();
                        for (int m = 0, sizem = ds4.size(); m < sizem; m++)
                        { // 4层
                            IDataset ds5 = ds4.getDataset(m);
                            IDataset list5 = new DatasetList();
                            for (int n = 0, sizen = ds5.size(); n < sizen; n++)
                            {
                                String tmpStr = ds5.get(n).toString();

                                // //todo.. 仅用于测试输出
                                if (!isValidStr(tmpStr, isMustFlag, isFixedLength, strSize, isDateType))
                                    return false;
                                list5.add(tmpStr);
                            }
                            list4.add(list5);
                        }
                        list3.add(list4);
                    }
                    list2.add(list3);
                }
                if (null != IDataset)
                    IDataset.add(list2);
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 返回验证结果
     * 
     * @param inStr
     *            传入字符串
     * @param isMustFlag
     *            是否必须值
     * @param isFixedLength
     *            是否固定长度值
     * @param strSize
     *            数据最大长度（或固定值的固定长度）
     * @param isDateType
     *            是否14位的固定日期格式为YYYYMMDDHH24MMSS 如20000101235959
     * @return
     */
    public boolean isValidStr(String inStr, boolean isMustFlag, boolean isFixedLength, int strSize, boolean isDateType)
    {

        if (StringUtils.isEmpty(inStr))
            return true;
        // 补充的空串数据,不验证

        if (isMustFlag)
        { // 必须的
            if (null == inStr || StringUtils.isEmpty(inStr))
                return false; // 不能有空值
            if (isFixedLength)
            { // 固定长度的
                if (strSize != inStr.length())
                    return false; // 只能为固定长度
            }
            else
            { // 不固定长度的
                if (inStr.length() > strSize)
                    return false; // 不能超过最大长度
            }
            if (isDateType)
            { // 为了防止出现isFixedLength＝false,并且，isDateType＝true时，出现不能验证，特别提出放于此处
                if (!isDateType(inStr))
                    return false;
            }
        }
        else
        { // 非必须值
            if (isFixedLength)
            { // 固定长度的
                if (null != inStr && strSize != inStr.length())
                    return false; // 只能为固定长度
                if (isDateType)
                {
                    if (!isDateType(inStr))
                        return false;
                }
            }
            else
            { // 不固定长度的
                if (null != inStr && inStr.length() > strSize)
                    return false; // 不能超过最大长度
            }

            if (null != inStr && isDateType)
            { // 为了防止出现isFixedLength＝false,并且，isDateType＝true时，出现不能验证，特别提出放于此处
                if (!isDateType(inStr))
                    return false;
            }
        }

        return true;

    }

    /**
     * 处理BBOSS商品同步 接口的入口 [[["98", "98"], ["98"]], [["98", "98"], ["98", "98"]]]
     * 
     * @return [["1", "1"], ["1", "1"]]
     * @throws Exception
     * @throws Exception
     */
    public IDataset processPosSyncInfo(IData RootData) throws Exception
    {

        IDataset ids = new DatasetList();
        String POSpecCNumber = "";// 如果有多个商品规格编码，则只取第一个,用于返回,在checkValue(RootData)方法之后取值
        try
        {
            checkValue(RootData);// 检验数据的有效性,及初始相关数据的值！在后面操作数据时，尽量减少意外，提高程序的稳定及健壮性
            POSpecCNumber = RSRV_STR3_List.get(0).toString();// 如果有多个商品规格编码，则只取第一个,用于返回
            SysDate = SysDateMgr.getSysDate();// 获得当前时间(YYYY-MM-DD
            // HH24:MI:SS),用于设置update_time字段
            SysDate4Id = SysDateMgr.getSysDate("yyyyMMddHHmmss");// 获取数据库当前时间[YYYYMMDDHH24MISS]，F14，用于返回操作时间
            if (!RSPCODE.equals("00"))
            {
                IData data = new DataMap();// 返回结果
                data.put("OPR_TIME".toUpperCase(), SysDate4Id);
                data.put("POSPE_CNUMBER".toUpperCase(), POSpecCNumber);
                data.put("RSPCODE".toUpperCase(), RSPCODE);
                data.put("RSP_DESC".toUpperCase(), RSP_DESC);
                data.put("X_RESULTCODE".toUpperCase(), RSPCODE);// 其他错误 select *
                data.put("X_RESULTINFO".toUpperCase(), RSP_DESC1 + ":" + RSP_DESC);
                ids.add(data);
                return ids;
            }
            IDataset POInfos = getPOInfos(RootData);// 商品规格信息,数据库表TD_F_PO
            IDataset POProvinceInfos = getPOProvinceInfos(RootData);// 商品开放省,数据库表TD_F_POPRIV
            IDataset PORatePolicys = getPORatePolicys(RootData);// 商品资费策略信息【TD_F_PORATEPOLICYS】
            IDataset PORatePlans = getPORatePlans(RootData);// 商品级和产品级资费计划,数据库表TD_F_PORATEPLAN
            IDataset PORatePlanICBs = getPORatePlanICBs(RootData);// 商品和产品
            // 资费模式【TD_F_PORATEPLANICB】
            IDataset POProductInfos = getPOProductInfos(RootData);// 产品规格信息,数据库表TD_F_POPRODUCT
            IDataset POProductPluss = getPOProductPluss(RootData);// 产品补充信息表，数据库表TD_F_POPRODUCTPLUS
            
            
          
            IDataset delPoList = new DatasetList();
            IDataset delPoRatePlanList = new DatasetList();
            IDataset delPOProductInfoList = new DatasetList();
            
            IDataset delPOProductPlusList = new DatasetList();
            IDataset delPOProvinceInfoList = new DatasetList();

            IDataset delPORatePolicyList = new DatasetList();
            IDataset delPORatePlanICBList = new DatasetList();
            
            
            IDataset delPoHistoryList = new DatasetList();
            IDataset delPoRatePlanHISTORYList = new DatasetList();
            IDataset delPOProductInfoHISTORYList = new DatasetList();
            
            IDataset delPOProductPlusHISTORYList = new DatasetList();
            IDataset delPOProvinceInfoHISTORYList = new DatasetList();

            IDataset delPORatePolicyHISTORYList = new DatasetList();
            IDataset delPORatePlanICBHISTORYList = new DatasetList();
            
            
            
            for (int i = 0; i < ACTION_List.size(); i++)
            {
                String Action = ACTION_List.get(i).toString();
                IData POInfo = POInfos.getData(i);
                IData POProvinceInfo = POProvinceInfos.getData(i);
                IData PORatePolicy = PORatePolicys.getData(i);
                IData PORatePlan = PORatePlans.getData(i);
                IData PORatePlanICB = PORatePlanICBs.getData(i);
                IData POProductInfo = POProductInfos.getData(i);
                IData POProductPlus = POProductPluss.getData(i);

                /**
                 * 为了提高程序的健壮性，即使Action.equals("1")时，我们也先删除一下数据，再进行添加工作 防止别人在已经存在记录的情况下，再次提交一次Action.equals("1")的数据
                 * 因为BBOSS上的商品是保持最新的，多更新一次，也没关系的 当系统中不存在这些记录时，我们对系统进行删除操作也不会造成任何影响
                 */
                if (Action.equals("0"))
                {// 删除
                	
                	IData  dpo = new DataMap();
                	dpo.put("POSPECNUMBER",POInfo.getString("POSPECNUMBER"));
                	dpo.put("MODIFY_TAG","1");
                	delPoList.add(dpo);
                	
                	
                	IData  dpoRatePlan = new DataMap();
                	dpoRatePlan.put("ICB_NO",PORatePlan.getString("ICB_NO"));
                	dpoRatePlan.put("POSPECNUMBER",PORatePlan.getString("POSPECNUMBER"));
                	dpoRatePlan.put("PORATENUMBER",PORatePlan.getString("PORATENUMBER"));
                	dpoRatePlan.put("RATETYPE",PORatePlan.getString("RATETYPE"));
                	dpoRatePlan.put("RATEPLANID",PORatePlan.getString("RATEPLANID"));
                	dpoRatePlan.put("MODIFY_TAG","1");
                	delPoRatePlanList.add(dpoRatePlan);
                	
                	
                	IData  dpoProductInfo = new DataMap();
                	dpoProductInfo.put("ICB_NO",POProductInfo.getString("ICB_NO"));
                	dpoProductInfo.put("PRODUCTSPECNUMBER",POProductInfo.getString("POSPECNUMBER"));
                	dpoProductInfo.put("MODIFY_TAG","1");
                	delPOProductInfoList.add(dpoProductInfo);
                	
                	IData  dProductPlus = new DataMap();
                	dProductPlus.put("PRODUCTSPECNUMBER",POProductPlus.getString("PRODUCTSPECNUMBER"));
                	dProductPlus.put("PRODUCTSPECCHARACTERNUMBER",POProductPlus.getString("PRODUCTSPECCHARACTERNUMBER"));
                	dProductPlus.put("MODIFY_TAG","1");
                	delPOProductPlusList.add(dProductPlus);
                	
                	
                	IData  dProvinceInfo = new DataMap();
                	dProvinceInfo.put("ENABLECOMPANYID",POProvinceInfo.getString("ENABLECOMPANYID"));
                	dProvinceInfo.put("POSPECNUMBER",POProvinceInfo.getString("MODIFY_TAG"));
                	dProvinceInfo.put("MODIFY_TAG","1");
                	delPOProvinceInfoList.add(dProvinceInfo);
                	
                	
                	IData  dRatePolicy = new DataMap();
                	dRatePolicy.put("POSPECNUMBER",PORatePolicy.getString("POSPECNUMBER"));
                	dRatePolicy.put("POSPECRATEPOLICYID",PORatePolicy.getString("POSPECRATEPOLICYID"));
                	dRatePolicy.put("MODIFY_TAG","1");
                	delPORatePolicyList.add(dRatePolicy);
                	
                	
                	IData  dPORatePlanICB = new DataMap();
                	dPORatePlanICB.put("ICB_NO",PORatePlanICB.getString("ICB_NO"));
                	dPORatePlanICB.put("RATEPLANID",PORatePlanICB.getString("RATEPLANID"));
                	dPORatePlanICB.put("PARAMETER_NO",PORatePlanICB.getString("PARAMETER_NO"));
                	dPORatePlanICB.put("MODIFY_TAG","1");
                	delPORatePlanICBList.add(dPORatePlanICB);
                	
                    
                    delPoHistoryList.add(POInfo.put("MODIFY_TAG","0"));
                    delPoRatePlanHISTORYList.add(PORatePlan.put("MODIFY_TAG","0"));
                    delPOProductInfoHISTORYList.add(POProductInfo.put("MODIFY_TAG","0"));
                    delPOProductPlusHISTORYList.add(POProductPlus.put("MODIFY_TAG","0"));
                    delPOProvinceInfoHISTORYList.add(POProvinceInfo.put("MODIFY_TAG","0"));
                    delPORatePolicyHISTORYList.add(PORatePolicy.put("MODIFY_TAG","0"));
                    delPORatePlanICBHISTORYList.add(PORatePlanICB.put("MODIFY_TAG","0"));
                    
                    
                 

                }else if (Action.equals("1"))
                {// 增加

                	delPoList.add(POInfo.put("MODIFY_TAG","0"));
                	
                	delPoRatePlanList.add(PORatePlan.put("MODIFY_TAG","0"));
                
                	delPOProductInfoList.add(POProductInfo.put("MODIFY_TAG","0"));
                	
                	delPOProductPlusList.add(POProductPlus.put("MODIFY_TAG","0"));
                	
                	delPOProvinceInfoList.add(POProvinceInfo.put("MODIFY_TAG","0"));
                	
                	delPORatePolicyList.add(PORatePolicy.put("MODIFY_TAG","0"));
                	
                	delPORatePlanICBList.add(PORatePlanICB.put("MODIFY_TAG","0"));
                	
                }
            }
            
            IData requestData  = new DataMap();
            requestData.put("PM_PO",delPoList);
            requestData.put("PM_PORATEPLAN",delPoRatePlanHISTORYList);
            requestData.put("PM_POPRODUCT",delPOProductInfoHISTORYList);
            
            requestData.put("PM_POPRODUCTPLUS",delPOProductPlusHISTORYList);
            requestData.put("PM_POPRIV",delPOProvinceInfoList);
            
            requestData.put("PM_PORATEPOLICYS",delPORatePolicyList);
            requestData.put("PM_PORATEPLANICB",delPORatePlanICBList);
            
            
            requestData.put("PM_PO_H",delPoHistoryList);
            requestData.put("PM_PORATEPLAN_H",delPoHistoryList);
            requestData.put("PM_POPRODUCT_H",delPoHistoryList);
            requestData.put("PM_POPRODUCTPLUS_H",delPoHistoryList);
            requestData.put("PM_POPRIV_H",delPOProvinceInfoHISTORYList);

            requestData.put("PM_PORATEPOLICYS_H",delPORatePolicyHISTORYList);
            requestData.put("PM_PORATEPLANICB_H",delPORatePlanICBHISTORYList);
            
            
            IData returnData = UpcCallIntf.synBBossPoInfo(requestData);
            if (!"0".equals(returnData.getString("X_RESULTCODE"))) {
            	
            	 IData data = new DataMap();// 返回结果
            	 RSPCODE = "99";
                 RSP_DESC = returnData.getString("X_EXCEPTION") ;//e.getMessage();
           
                 data.put("OPR_TIME".toUpperCase(), SysDate4Id);
                 data.put("POSPE_CNUMBER".toUpperCase(), POSpecCNumber);
                 data.put("RSPCODE".toUpperCase(), RSPCODE);
                 data.put("RSP_DESC".toUpperCase(), RSP_DESC);

                 data.put("X_RESULTCODE".toUpperCase(), RSPCODE);// 其他错误 select *
                 data.put("X_RESULTINFO".toUpperCase(), "其它错误:" + RSP_DESC);

                 ids.add(data);
                 return ids;
            	
            }
            
           
           

        }
        catch (Exception e)
        {
            RSPCODE = "99";
            RSP_DESC = e.getMessage();
            IData data = new DataMap();// 返回结果
            data.put("OPR_TIME".toUpperCase(), SysDate4Id);
            data.put("POSPE_CNUMBER".toUpperCase(), POSpecCNumber);
            data.put("RSPCODE".toUpperCase(), RSPCODE);
            data.put("RSP_DESC".toUpperCase(), RSP_DESC);

            data.put("X_RESULTCODE".toUpperCase(), RSPCODE);// 其他错误 select *
            data.put("X_RESULTINFO".toUpperCase(), "其它错误:" + RSP_DESC);

            ids.add(data);
            return ids;
        }

        IData data = new DataMap();// 返回结果
        data.put("OPR_TIME".toUpperCase(), SysDate4Id);
        data.put("POSPE_CNUMBER".toUpperCase(), POSpecCNumber);
        data.put("RSPCODE".toUpperCase(), "00");
        data.put("RSP_DESC".toUpperCase(), "OK");

        ids.add(data);
        return ids;
    }
}
