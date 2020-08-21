
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.math.NumberUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class BadInfoComplainDealBean extends CSBizBean
{

	private static final Log logger = LogFactory.getLog(BadInfoComplainDealBean.class);

	
	
    public static final String PRO_CODE = "898";
    
 // 需要加黑到HLR的处理类型
    private static final String NEED_ADDED_HLR = "'01','0101','0102','02','0201','03','0305'";

    // HLR加黑类型： 举报
    private static final String HANDLING_TYPE_REPORT = "0103";

    // 数据来源 ：前台提交
    private static final String SOURCE_DATA_PLATFORM = "01";

    // 渠道来源 ：短信
    private static final String IN_MODE_CODE_SMS = "2";

    public IDataset badInfoActive(IData data) throws Exception
    {
        IData datatemp = new DataMap();// 存放数据MAP
        if("04".equals(data.getString("REPORT_TYPE_CODE", ""))) 
        {
            String badInfo = data.getString("BADNESS_INFO");
            if(!"9".equals(badInfo)) {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_93);
            }
        }
        data.put("REPORT_TYPE_CODE1", dealRequestTypeCode(data));// 全网
        String recv_id = SeqMgr.getBadnessId().substring(9, 16);
        for (int i = 7; i > recv_id.length(); i--)
        {
            recv_id = "0" + recv_id;
        }

        String serialNumber = data.getString("REPORT_SERIAL_NUMBER");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_01);
        }
        IDataset eparyout = new DatasetList();
        String provCode="";
        String areaCode="";
        if(serialNumber.startsWith("0898")||serialNumber.startsWith("898")){
        	provCode="898";
        }else{
        	
        	eparyout= BadnessInfoQry.qryEpareycodeout(serialNumber);
        	if (IDataUtil.isEmpty(eparyout))
        	{
        		CSAppException.apperr(DedInfoException.CRM_DedInfo_02);
        	}
        	provCode = eparyout.getData(0).getString("PROV_CODE");
        	areaCode = eparyout.getData(0).getString("AREA_CODE");
        }
        
        String brandCode = null;
        String custName = "";
        String custLevel = "";
        String eqrev = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD) + "CSVC" + "898" + recv_id;

        if ("898".equals(provCode))
        {
            //IDataset eparchy = BadnessInfoQry.qryEpareycode(serialNumber);
           // String eparchyCode = eparchy.getData(0).getString("EPARCHY_CODE", "");
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_84);
            }

//            String custId = userInfo.getString("CUST_ID");
            custLevel = getSubsLevel(data.getString("REPORT_SERIAL_NUMBER"));
//            IDataset custVip = CustVipInfoQry.qryVipInfoByCustId(custId);

//            if (IDataUtil.isNotEmpty(custVip))
//            {
//                custLevel = "0" + custVip.getData(0).getString("VIP_CUST_ID");
//            }

//            if (custLevel.equalsIgnoreCase("04"))
//            {
//                custLevel = "01"; // 钻卡大客户
//            }
//            else if (custLevel.equalsIgnoreCase("03"))
//            {
//                custLevel = "02"; // 金卡大客户
//            }
//            else if (custLevel.equalsIgnoreCase("02"))
//            {
//                custLevel = "03"; // 银卡大客户
//            }
//            else
//            {
//                custLevel = "04"; // 普通客户
//            }

            custName = userInfo.getString("CUST_NAME", "");
            brandCode = userInfo.getString("BRAND_CODE", "");

            if (brandCode.equalsIgnoreCase("G001"))
            {
                brandCode = "01"; // 全球通
            }
            else if (brandCode.equalsIgnoreCase("G010"))
            {
                brandCode = "02"; // 动感地带
            }
            else
            {
                brandCode = "03"; // 神州行
            }
        }

        // 1、查询用户入网时间、套餐、品牌、是否实名制
        String badSerialNumber = data.getString("BADNESS_INFO");
        if(badSerialNumber.equals(serialNumber)){
        	CSAppException.apperr(DedInfoException.CRM_DedInfo_96);
        }
        int tag = 0;// 不为网站
        if ("".equals(badSerialNumber))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_08);
        }
        else
        {
            if ("9".equals(badSerialNumber))
            {
                tag = 1;// 为网站
            }else if(badSerialNumber.length()<5){
            	 CSAppException.apperr(DedInfoException.CRM_DedInfo_94);
            }else if(badSerialNumber.startsWith("13")||badSerialNumber.startsWith("14")||badSerialNumber.startsWith("15")||badSerialNumber.startsWith("17")||badSerialNumber.startsWith("18")){
            	if(badSerialNumber.length()!=11){
            		CSAppException.apperr(DedInfoException.CRM_DedInfo_95);
            	}
            }
            for (int a = 0; a < badSerialNumber.length() - 1; a++)
            {
                if ((badSerialNumber.charAt(a) > '9') || (badSerialNumber.charAt(a) < '0'))
                {
                    CSAppException.apperr(DedInfoException.CRM_DedInfo_09);
                }
            }
        }
        IData badSnInfo = UcaInfoQry.qryUserMainProdInfoBySn(badSerialNumber);
        if (IDataUtil.isEmpty(badSnInfo))
        {// 非本省用户
            eparyout = BadnessInfoQry.qryEpareycodeout(badSerialNumber);
            if (IDataUtil.isNotEmpty(eparyout))
            {
                brandCode = "04"; // 外省移动客户
            }
            else
            {
                brandCode = "05"; // 外网
            }
            datatemp.put("BRAND_CODE", brandCode);
        }
        else
        {
            String productId = badSnInfo.getString("PRODUCT_ID");
            String productName = UProductInfoQry.getProductNameByProductId(productId);
            brandCode = badSnInfo.getString("BRAND_CODE");
            if (brandCode.equalsIgnoreCase("G001"))
            {
                brandCode = "01"; // 全球通
            }
            else if (brandCode.equalsIgnoreCase("G010"))
            {
                brandCode = "02"; // 动感地带
            }
            else
            {
                brandCode = "03"; // 神州行
            }

            String userId = badSnInfo.getString("USER_ID");
            String custId = badSnInfo.getString("CUST_ID");
            datatemp.put("BRAND_CODE", brandCode);
            datatemp.put("IN_DATE", badSnInfo.getString("IN_DATE"));
            datatemp.put("COMBO_TYPE", productId + "-" + productName);

            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
            String isRealName = null;
            if (IDataUtil.isEmpty(custInfo))
            {
                isRealName = "02";
            }
            else
            {
                isRealName = custInfo.getString("IS_REAL_NAME", "");
            }
            datatemp.put("IS_REAL_NAME", isRealName);

            // 2、查询用户停机状态
            IDataset svcState = UserSvcStateInfoQry.getUserValidMainSVCState(userId, null);
            String userState = "";
            if (IDataUtil.isNotEmpty(svcState))
            {
                if ("0".equals(svcState.getData(0).getString("STATE_CODE")))
                {
                    userState = "02";
                }
                else
                {
                    userState = "01";
                }
            }
            datatemp.put("USER_STATE_CODESET", userState);

            // 3、查询用户欠费金额
            String userScore = AcctCall.getOweFeeByUserId(userId).getString("ACCT_BALANCE");
            datatemp.put("ALLNEWROWE_FEE", userScore);

            // 4、查询用户短信功能状态
            String nodeState = "";
            IDataset noteInfo = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "5");
            if (IDataUtil.isEmpty(noteInfo))
            {
                nodeState = "02";
            }
            else
            {
                nodeState = noteInfo.getData(0).getString("STATE_CODE");
            }
            datatemp.put("NOTE_STATE", nodeState);

            // 5、查询用户彩信功能状态
            String mesState = "";
            
            IDataset platInfo = UserPlatSvcInfoQry.queryPlatSvcInfo(userId, "05");//改造 duhj
            
  
            
            if (IDataUtil.isEmpty(platInfo))
            {
                mesState = "02";
            }
            else
            {
                mesState = "01";
            }
            datatemp.put("MULT_MES_STATE", mesState);
        }

        // 6、查询用户其他业务是否消费
        datatemp.put("OTHER_BUS_FLAG", "99");

        // 7、举报对象
        datatemp.put("REPORT_TYPE_CODE", data.getString("REPORT_TYPE_CODE", ""));

        datatemp.put("INFO_RECV_ID", eqrev);
        datatemp.put("KF_CALL_IN_SERIAL_NUMBER", data.getString("KF_CALL_IN_SERIAL_NUMBER", ""));
        datatemp.put("KF_CALL_OUT_SERIAL_NUMBER", data.getString("KF_CALL_OUT_SERIAL_NUMBER", ""));
        datatemp.put("REPORT_SERIAL_NUMBER", data.getString("REPORT_SERIAL_NUMBER", ""));

        datatemp.put("REPORT_CUST_NAME", custName);
        datatemp.put("REPORT_CUST_LEVEL", custLevel);
        datatemp.put("REPORT_BRAND_CODE", brandCode);
        datatemp.put("SERV_REQUEST_TYPE", data.getString("SERV_REQUEST_TYPE", ""));
		
		
        datatemp.put("REPORT_CUST_PROVINCE", provCode); // 举报用户归属省
        datatemp.put("RECV_PROVINCE", "898");
        datatemp.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        datatemp.put("REPORT_TIME", SysDateMgr.getSysTime());
        datatemp.put("RECV_TIME", SysDateMgr.getSysTime());
        datatemp.put("BADNESS_INFO", data.getString("BADNESS_INFO", ""));
        datatemp.put("RECV_CONTENT", data.getString("RECV_CONTENT", ""));
        datatemp.put("REPORT_TYPE_CODE", data.getString("REPORT_TYPE_CODE", "")); // 举报对象类型
        datatemp.put("VRIFY_INFO", "");
        datatemp.put("RECV_STAFF_ID", getStaffId());

        String inModeCode = data.getString("IN_MODE_CODE");
        if (StringUtils.isBlank(inModeCode))
            datatemp.put("RECV_IN_TYPE", "01");
        else if (inModeCode.equals("5"))
            datatemp.put("RECV_IN_TYPE", "02");
        else if (inModeCode.equals("2"))
            datatemp.put("RECV_IN_TYPE", "03");
        else if (inModeCode.equals("6"))
            datatemp.put("RECV_IN_TYPE", "04");
        else
            datatemp.put("RECV_IN_TYPE", "06");
        
        //如果IMPORTANT_LEVEL为空，则默认将IMPORTANT_LEVEL置为02：一般
        if(StringUtils.isBlank(data.getString("IMPORTANT_LEVEL", ""))) {
            data.put("IMPORTANT_LEVEL", "02");
        }
        
        if("01".equals(data.getString("IMPORTANT_LEVEL", "")) || "02".equals(data.getString("IMPORTANT_LEVEL", "")))
        {
        	//如果入参数据的IMPORTANT_LEVEL是01或者02，则入参的IMPORTANT_LEVEL不变
        }
        else if ("04".equals(datatemp.getString("RECV_IN_TYPE")))//如果是总部数据，并且IMPORTANT_LEVEL不是01或者02，则将重要级别置为01：重要
        {
        	data.put("IMPORTANT_LEVEL", "01");
        }
        else//如果数据不是总部数据，但同时IMPORTANT_LEVEL也不是01或者02，则将重要级别置为02：一般
        {
        	data.put("IMPORTANT_LEVEL", "02");
        }

        datatemp.put("IMPORTANT_LEVEL", data.getString("IMPORTANT_LEVEL"));
        

        datatemp.put("STATE", "01"); // 待处理
        datatemp.put("OPERATE_STEP", data.getString("OPERATE_STEP", ""));

        //2014-10-23  重复条件变更
//        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
// 				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
// 				 new String[]{ "CSM", "8899", "CFJB" });
//        IDataset idataset = BadnessInfoQry.qryMaxTime(data);
//        if (IDataUtil.isEmpty(idataset))
//        {// 是否重复举报(0，不是重复举报；1，重复举报)
            datatemp.put("REPEAT_REPORT", "0");

//        }
//        else if ("".equals(idataset.getData(0).getString("MAX_TIME", "")))
//        {
//            datatemp.put("REPEAT_REPORT", "0");
//
//        }
//        else if (Integer.parseInt(idataset.getData(0).getString("MAX_TIME", "")) < Integer.parseInt(urltime) || Integer.parseInt(idataset.getData(0).getString("MAX_TIME", ""))==Integer.parseInt(urltime))
//        {
//            datatemp.put("REPEAT_REPORT", "1");
//            datatemp.put("STATE", "0A");// 归档
//
//        }
//        else if (Integer.parseInt(idataset.getData(0).getString("MAX_TIME", "")) > Integer.parseInt(urltime))
//        {
//            datatemp.put("REPEAT_REPORT", "0");
//        }
//         else 
//         {//
//         	 datatemp.put("REPEAT_REPORT", "0");
//         }
        IData dolldata =new DataMap();
        if(tag==0){
        	dolldata = dealCollation(badSerialNumber);// 分栋后数据
        }else{
        	dolldata.put("BADNESS_INFO_PROVINCE", "000"); // 被举报号码归属省/总部
        	dolldata.put("TARGET_PROVINCE", "000"); // 目标省代码
        	dolldata.put("SORT_RESULT_TYPE", "11010358000301");// 分拣分类结果
        	dolldata.put("SERV_REQUEST_TYPE", "11010358000301");// 分拣分类结果
        	dolldata.put("RSRV_STR1", "涉黄网址");// 服务请求类别
        	dolldata.put("NET_TAG", "0");//关于涉黄网站举报信息回复模板更改的需求涉黄网址成功短信内容更改
        }
        
//        IData overdata = dealDataover(dolldata, data);// 处理分拣后数据 HNYD-REQ-ZB-20120110-022关于优化10086热线人工服务请求分类的需求
//        datatemp.put("VEST_OPERATOR", dolldata.getString("VEST_OPERATOR", ""));
//        datatemp.put("VEST_OPERATOR1", dolldata.getString("VEST_OPERATOR1", ""));

        datatemp.put("IN_MODE_CODE", dolldata.getString("IN_MODE_CODE", ""));
       
       //特殊处理，海南出现BADNESS_INFO_PROVINCE字段为r的情况
        String badnessInfoProv = dolldata.getString("BADNESS_INFO_PROVINCE", "");
        if("r".equalsIgnoreCase(badnessInfoProv) || badnessInfoProv.contains("r") || !NumberUtils.isNumber(badnessInfoProv)) {
            dolldata.put("BADNESS_INFO_PROVINCE", "999");
        }
        
        datatemp.put("TARGET_PROVINCE", dolldata.getString("BADNESS_INFO_PROVINCE", "")); // 目标省代码,也即被举报号码归属省
        datatemp.put("BADNESS_INFO_PROVINCE", dolldata.getString("BADNESS_INFO_PROVINCE", "")); // 被举报号码归属省/总部
//        String sortResultType = overdata.getString("SERV_TYPE", "");
        String sortResultType = "11" + data.getString("FIRST_TYPE_CODE", "") + data.getString("SECOND_TYPE_CODE", "") + data.getString("REPORT_TYPE_CODE1");
        // 截取4位分类编码
//        if (sortResultType != null && sortResultType.length() > 10)
//        {
//            sortResultType = sortResultType.trim().substring(0, 10);
//        }
        data.put("BADNESS_INFO_PROVINCE", datatemp.getString("BADNESS_INFO_PROVINCE", ""));
        datatemp.put("SERV_REQUEST_TYPE", sortResultType);// 服务请求类别
        datatemp.put("SORT_RESULT_TYPE", sortResultType);// 分拣分类结果

        IData dealdata = new DataMap();
        dealdata.put("INFO_RECV_ID", eqrev);
        dealdata.put("DEAL_RAMARK", "");
        dealdata.put("DEAL_REMARK_MAKEUP", "");
        dealdata.put("SERV_REQUEST_TYPE", sortResultType);
        dealdata.put("DEAL_STAFF_ID", getStaffId());
        dealdata.put("DEAL_DEPART_ID", getDepartId());
        dealdata.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
        dealdata.put("STATE", PersonConst.STATE_NORMAL);// 待处理

        // 重要程度一般紧急程度为“重要”，处理时限为12小时，紧急程度为“一般”，处理时限为24小时,默认为24小时。”
        if ("01".equals(data.getString("IMPORTANT_LEVEL", "")))
        {
            datatemp.put("DEAL_LIMIT", "6");
        }
        else
        {
            datatemp.put("DEAL_LIMIT", "24");
        }
        datatemp.put("RSRV_STR1", data.getString("STICK_LIST"));
        if (!"898".equals(provCode))
        {
            IDataset badProv = BadnessInfoQry.qryProvCodeBySn(badSerialNumber);
            if (IDataUtil.isEmpty(badProv)&&tag==0)
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_89, "[" + badSerialNumber + "]被举报手机号为错误的手机号!");
            }
            IData idata = new DataMap();
            idata.put("HOME_PROV", "898");// 受理省编码RECV_PROVINCE
            idata.put("T_PROV", provCode);// 目标省编码TARGET_PROVINCE
            idata.put("ACCEPT_TIME", SysDateMgr.getSysTime());// 举报时间REPORT_TIME
            idata.put("TARGET_NO", badSerialNumber);// 被举报号码BADNESS_INFO
            idata.put("TARGET_PROV", data.getString("BADNESS_INFO_PROVINCE", ""));// 被举报号码归属省BADNESS_INFO_PROVINCE
            idata.put("MSISDN", serialNumber);// 举报人手机号码REPORT_SERIAL_NUMBER
            idata.put("REP_TYPE", data.getString("REPORT_TYPE_CODE", ""));// 举报对象REPORT_TYPE_CODE
            idata.put("SERVICE_CONTENT", data.getString("RECV_CONTENT", ""));// 举报内容RECV_CONTENT
            idata.put("PRE_TREATMENT", data.getString("OPERATE_STEP", ""));// 预处理情况OPERATE_STEP
            idata.put("SVC_TYPE_ID", sortResultType);// 服务请求编码SERV_REQUEST_TYPE
            idata.put("CONTACT_CHANNEL", "01");// 举报受理渠道RECV_IN_TYPE
            idata.put("IMP_LEVEL", data.getString("IMPORTANT_LEVEL", ""));// 重要程度IMPORTANT_LEVEL
            idata.put("INDICT_SEQ", eqrev);// 举报全网唯一编码INFO_RECV_ID

          IData ibossResult = IBossCall.dealDedInfoIboss(idata);
            datatemp.put("REPEAT_REPORT", "");
            Dao.insert("TF_F_BADNESS_INFO_LOG", datatemp, Route.CONN_CRM_CEN);
            // 发给一级bossREPORT_TYPE_CODE
             if (!"0000".equalsIgnoreCase(ibossResult.getString("X_RSPCODE")))
             {
                 CSAppException.apperr(DedInfoException.CRM_DedInfo_89, ibossResult.getString("X_RESULTINFO"));
             }
        }
        else
        {
            fieldcheck(datatemp);// HNYD-REQ-ZB-20120419-004关于现网BOSS侧32个上传集团文件校验改造需求

            Dao.insert("TF_F_BADNESS_INFO", datatemp, Route.CONN_CRM_CEN); // 将资料输入插入表
            Dao.insert("TF_F_BADNESS_INO_DEAL", dealdata, Route.CONN_CRM_CEN);

            sendSms(datatemp);
        }

//        // 点对点短信屏蔽功能，将数据投诉与被投诉号码同步到网监平台
//        String servType = datatemp.getString("ALL_SERV_REQUEST_TYPE", "").trim();
//        if(servType!=null && servType.length()>12){
//            servType = servType.trim().substring(0, 12);
//        }
//        if (servType.equals(ManageForbiddenPointBean.OTHER_PROVINCE_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.OTHER_SYS_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.CURRENT_PROVINCE_SERV_TYPE))
//        {
//            //2014-09-26新增，只对0109-对举报号码屏蔽 才插点对点表
//            if(StringUtils.equals("0109", data.getString("DEAL_RAMARK", ""))) {
//                CSAppCall.call("SS.ManageForbiddenPointSVC.addForbiddenData", datatemp);
//            }
//        }
//
//        // hlr加黑并同步到一级boss
//        String dealType = data.getString("DEAL_RAMARK", "").trim();
//        if (NEED_ADDED_HLR.indexOf("'" + dealType + "'") > -1)
//        {
//            IData param = new DataMap();
//            param.putAll(this.convertToHLRParamMap(data));
//            CSAppCall.call("SS.BadnessManageInterSVC.createHLRStopOpenReg", param);
//        }
        IData result = new DataMap();
        result.put("INFO_RECV_ID", eqrev);
        return IDataUtil.idToIds(result);
    }

    /**
     * 转换HLR所需参数
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private IData convertToHLRParamMap(IData data) throws Exception
    {
        IData hlrParamMap = new DataMap();

        hlrParamMap.put("INDICT_SEQ", data.getString("INFO_RECV_ID"));
        hlrParamMap.put("PROVINCE", data.getString("BADNESS_INFO_PROVINCE"));
        hlrParamMap.put("SUB_NUMBER", data.getString("BADNESS_INFO"));
        hlrParamMap.put("HANDLING_TIME", SysDateMgr.getSysTime());

        hlrParamMap.put("HANDLING_STATE", data.getString("DEAL_RAMARK").trim());
        hlrParamMap.put("HANDLING_TYPE", HANDLING_TYPE_REPORT);
        hlrParamMap.put("SOURCE_DATA", SOURCE_DATA_PLATFORM);

        hlrParamMap.put("SERVICE_CONTENT", data.getString("SVC_TYPE_ID", ""));
        hlrParamMap.put("IN_MODE_CODE", IN_MODE_CODE_SMS);

        return hlrParamMap;
    }

    public IData dealCollation(String badSerialNumber) throws Exception
    {
        // ///////移动号码判断/////////////
        String nettag = "0";// 0为网内，1为网外
        String protag = "0";// 0为省内，1为省外
        IData data = new DataMap();
        IData datan = new DataMap();
        datan.put("SERIAL_NUMBER", badSerialNumber);
        IDataset setnet = BadnessInfoQry.qryEpareycodeout(badSerialNumber);
        if (IDataUtil.isNotEmpty(setnet))
        {
            nettag = "0";
            if (PRO_CODE.equals(setnet.getData(0).getString("PROV_CODE", "")))
            {
                protag = "0";
                data.putAll(setnet.getData(0));
                data.put("NET_TAG", nettag);
                data.put("PRO_TAG", protag);
            }
            else
            {
                protag = "1";
                data.putAll(setnet.getData(0));
                data.put("NET_TAG", nettag);
                data.put("PRO_TAG", protag);
            }
            data.put("VEST_OPERATOR1", setnet.getData(0).getString("ASP", ""));// HNYD-REQ-20120510-004

            data.put("BADNESS_INFO_PROVINCE", setnet.getData(0).getString("PROV_CODE", ""));
            return data;
        }
        else
        {// HNYD-REQ-20120510-004
            IDataset setnetout = BadnessInfoQry.qryEpareyBySn(badSerialNumber);
            if (IDataUtil.isNotEmpty(setnetout))
            {
                data.put("VEST_OPERATOR1", setnetout.getData(0).getString("ASP", ""));// HNYD-REQ-20120510-004
                data.put("NET_TAG", "1");
                data.put("BADNESS_INFO_PROVINCE", "999");
                return data;
            }
        }

        int tag = 0;
        IDataset collset = new DatasetList();
        for (int i = 3; i <= 5; i++)
        {
            IDataset set = BadnessInfoQry.getExtend(badSerialNumber.substring(0, i), String.valueOf(i));
            if (set != null && set.size() > 0)
            {
                // 根据查询结果，区分具体为哪个运营商（联通、电信）
                nettag = "1";
                IData netdata = set.getData(0);
                netdata.put("NET_TAG", nettag);
                collset.add(netdata);
                tag = 1;
                break;
            }
        }

        if (tag == 1)
        {
            data.putAll(collset.getData(0));
            data.put("SORT_RESULT_TYPE", collset.getData(0).getString("VEST_OPERATOR", ""));
            // HNYD-REQ-20120510-004 不良信息举报回复内容修改 start
            data.put("VEST_OPERATOR1", "1");// 局方要求改为1即短信提示归为移动但实际仍发其他运营商 ，因为该表判断不出到底归属谁
            // HNYD-REQ-20120510-004 不良信息举报回复内容修改 end
            data.put("BADNESS_INFO_PROVINCE", "999"); // 其他运营商，归属为999
            return data;
        }

        IData indata = new DataMap();
        int lon = badSerialNumber.length();
        for (int j = lon; j >= 1; j--)
        {
            IDataset set = new DatasetList();
            IDataset fullset = new DatasetList();
            if (j == lon)
            {
                fullset = BadnessInfoQry.getServCodeFull(badSerialNumber.substring(0, j));
            }
            if (fullset != null && fullset.size() > 0)
            {
                set.addAll(fullset);
            }
            else
            {
                set = BadnessInfoQry.getServCode(badSerialNumber.substring(0, j), String.valueOf(j));
            }

            if (set != null && set.size() > 0)
            {
                tag = 1;
                indata = set.getData(0);
                indata.put("NET_TAG", nettag);
                String pro = indata.getString("PROV_CODE", "");
                if (!PRO_CODE.equals(pro))
                {
                    protag = "1";
                    indata.put("PRO_TAG", protag);
                }
                data.put("SERVCODE_TYPE", indata.get("SERVCODE_TYPE"));
                data.put("IN_MODE_CODE", set.getData(0).getString("IN_NET_TYPE", ""));
                data.put("BADNESS_INFO_PROVINCE", pro);
                data.put("VEST_OPERATOR1", "1");// 移动 HNYD-REQ-20120510-004 不良信息举报回复内容修改
                return data;
            }
        }
        data.put("VEST_OPERATOR1", "1");// HNYD-REQ-20120510-004局方要求改为1即短信提示归为移动但实际仍发其他运营商 ，因为该表判断不出到底归属谁
        data.put("BADNESS_INFO_PROVINCE", "999"); // 默认为本省处理--修改为999
        return data;
    }

    /**
     * 数据转换2012.2.23 HNYD-REQ-ZB-20120110-022关于优化10086热线人工服务请求分类的需求
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
//    public IData dealDataover(IData dolldata, IData data) throws Exception
//    {
//        IData temp = new DataMap();
//        String reportTypeCode = data.getString("REPORT_TYPE_CODE1", "");
//        if (StringUtils.isBlank(reportTypeCode))
//        {
//            CSAppException.apperr(DedInfoException.CRM_DedInfo_74);
//        }
//
//        String tag = "04";
//        String serv = "10" + data.getString("FIRST_TYPE_CODE", "") + data.getString("SECOND_TYPE_CODE", "") + data.getString("THIRD_TYPE_CODE", "");
//        if ("".equals(serv) || serv.length() < 6)
//        {
//            CSAppException.apperr(DedInfoException.CRM_DedInfo_75);
//        }
//        if ("1".equals(dolldata.getString("PRO_TAG", "")) && "0".equals(dolldata.getString("NET_TAG", "")))
//        {
//            tag = "POUT";
//        }
//        else if ("0".equals(dolldata.getString("PRO_TAG", "")) && "0".equals(dolldata.getString("NET_TAG", "")))
//        {
//            tag = "PIN";
//        }
//        else if (!"".equals(dolldata.getString("VEST_OPERATOR", "")))
//        {
//            tag = "N";
//        }
//        else if (!"".equals(dolldata.getString("SERVCODE_TYPE", "")))
//        {
//            tag = dolldata.getString("SERVCODE_TYPE", "");
//        }
//        else
//        {
//            tag = "07";
//        }
//
//        IDataset setfive = CommparaInfoQry.getCommparaByParaCode("CSM", "2012", "201201B", null, reportTypeCode, "5", tag);
//        if (IDataUtil.isNotEmpty(setfive))
//        {
//            temp.put("SERV_TYPE", serv + reportTypeCode + setfive.getData(0).getString("PARA_CODE2", ""));
//            temp.put("SERV_NAME", setfive.getData(0).getString("PARA_CODE7", "") + setfive.getData(0).getString("PARA_CODE4", ""));
//        }
//        else if ("07".equals(data.getString("REPORT_TYPE_CODE1", "")))
//        {
//            temp.put("SERV_TYPE", serv + reportTypeCode);
//            temp.put("SERV_NAME", "WAP涉黄网站");
//        }
//        else
//        {
//            temp.put("SERV_TYPE", serv + "99");// 其它安全问题
//            temp.put("SERV_NAME", "其它安全问题");// 其它安全问题
//        } // 测试HNYD-REQ-20120410-005屏蔽
//
//        return temp;
//    }


    public String dealReportType(String portcode) throws Exception
    {
        if ("01".equals(portcode))
        {
            portcode = "55";// 垃圾短信
        }
        else if ("02".equals(portcode))
        {
            portcode = "56";// 骚扰电话
        }
        else if ("03".equals(portcode))
        {
            portcode = "54";// 垃圾彩信
        }
        else
        {
            portcode = "58";// Wap黄色网站
        }
        return portcode;

    }
    
    public String dealRequestTypeCode(IData data) {
    	//受理只需到第三级
    	String requestTypeCode = data.getString("FOURTH_TYPE_CODE", "");
    	
    	return requestTypeCode;
    }

    /**
     * HNYD-REQ-ZB-20120419-004关于现网BOSS侧32个上传集团文件校验改造需求
     * 
     * @param data
     *            tf_f_badness_info表插入数据 HNYD-REQ-ZB-20120419-004关于现网BOSS侧32个上传集团文件校验改造需求
     * @throws Exception
     */
    public void fieldcheck(IData data) throws Exception
    {
        if (data.getString("INFO_RECV_ID", "") == null || " ".equals(data.getString("INFO_RECV_ID", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报全网唯一编码缺失！");
        }
        if (data.getString("SERV_REQUEST_TYPE", "") == null || " ".equals(data.getString("SERV_REQUEST_TYPE", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务请求编码缺失！");
        }
        if (data.getString("RECV_PROVINCE", "") == null || " ".equals(data.getString("RECV_PROVINCE", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报省编码缺失！");
        }
        if (data.getString("RECV_TIME", "") == null || " ".equals(data.getString("RECV_TIME", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报提交时间缺失！");
        }
        if (data.getString("REPORT_CUST_PROVINCE", "") == null || " ".equals(data.getString("REPORT_CUST_PROVINCE", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报号码归属省缺失！");
        }
        if (data.getString("REPORT_CUST_LEVEL", "") == null || " ".equals(data.getString("REPORT_CUST_LEVEL", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报用户级别缺失！");
        }
        if (data.getString("REPORT_BRAND_CODE", "") == null || " ".equals(data.getString("REPORT_BRAND_CODE", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "举报用户品牌缺失！");
        }
        if (data.getString("REPORT_TIME", "") == null || " ".equals(data.getString("REPORT_TIME", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户举报时间缺失！");
        }
        if (data.getString("BADNESS_INFO", "") == null || " ".equals(data.getString("BADNESS_INFO", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "被举报号码缺失！");
        }
        /*
         * if (data.getString("BADNESS_INFO_PROVINCE","") == null ||
         * " ".equals(data.getString("BADNESS_INFO_PROVINCE",""))){ common.error("被举报号码归属省缺失！"); }
         */
        if (data.getString("IMPORTANT_LEVEL", "") == null || " ".equals(data.getString("IMPORTANT_LEVEL", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "重要程度缺失！");
        }
        if (data.getString("RECV_TIME", "") == null || " ".equals(data.getString("RECV_TIME", "")))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "受理员工编号缺失！");
        }
    }

    private String getDepartId()
    {
        String departId = getVisit().getDepartId();
        String departName = getVisit().getDepartName();
        return departName + "--" + departId;
    }

    public IDataset getServRequestType(IData data) throws Exception
    {
        String reportType = data.getString("FIFTH_TYPE_CODE");
        return StaticUtil.getStaticListByParent("BAD_INFO_SERV_REQUEST_TYPE", reportType);
    }
    
    public IDataset getFourthTypeCodes(IData data) throws Exception
    {
        String reportType = data.getString("REPORT_TYPE_CODE");
        return StaticUtil.getStaticListByParent("BAD_INFO_FOURTH_TYPE_CODE", reportType);
    }
    
    public IDataset getFifthTypeCodes(IData data) throws Exception
    {
        String reportType = data.getString("FOURTH_TYPE_CODE");
        return StaticUtil.getStaticListByParent("BAD_INFO_FIFTH_TYPE_CODE", reportType);
    }
    
    public IDataset getSevenTypeCodes(IData data) throws Exception
    {
        String reportType = data.getString("SERV_REQUEST_TYPE");
        return StaticUtil.getStaticListByParent("BAD_INFO_SEVEN_TYPE_CODE", reportType);
    }

    private String getStaffId()
    {
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        return staffName + "--" + staffId;
    }

    public void sendSms(IData data) throws Exception
    {
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String badNess_info = data.getString("BADNESS_INFO", "");
        StringBuilder smsInfo = new StringBuilder();

        if (data.getString("RECV_TIME", "").length() < 11)
        {// 判断举报日期格式长度不对
            CSAppException.apperr(DedInfoException.CRM_DedInfo_48);
        }
        String smscontent="";
        String reportTypeCode= data.getString("REPORT_TYPE_CODE", "");
        if(reportTypeCode.equals("04")||badNess_info.equals("9")){//举报网站
        	String recvInType=data.getString("RECV_IN_TYPE", "");
        	if(recvInType.equals("01")||recvInType.equals("05")){
        		smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
          				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
          				 new String[]{ "CSM", "8899", "SMSTSSL" ,"01"});
        	}else if(recvInType.equals("02")){
        		smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
         				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
         				 new String[]{ "CSM", "8899", "SMSTSSL" ,"021"});
        	}else if(recvInType.equals("03")){
        		smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
        				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
        				 new String[]{ "CSM", "8899", "SMSTSSL" ,"03"});
        	}
        	
        }else{
        	String mon=data.getString("RECV_TIME", "").substring(5, 7);
            String day=data.getString("RECV_TIME", "").substring(8, 10);
            String paracode1="0";
            IDataset epary = judgePhoneType(badNess_info);
            if(badNess_info.startsWith("4001")||badNess_info.startsWith("4007")||badNess_info.startsWith("8007")
            		||(epary != null && epary.size() > 0)){
        		paracode1="01";
        	}else if(badNess_info.equals("10086999")||badNess_info.equals("13800138000")){
            	paracode1="10086999";
            }else if(badNess_info.startsWith("400")||badNess_info.startsWith("800")){
            	paracode1="400";
            }else {
            	IDataset idataset = BadnessInfoQry.queryFixedPhone(badNess_info);//是否为固定电话
    	        if (!IDataUtil.isEmpty(idataset))
    	        {
    	        	paracode1="400";
    	        }else if("1".equals(data.getString("VEST_OPERATOR1"))||"2".equals(data.getString("VEST_OPERATOR1"))||"3".equals(data.getString("VEST_OPERATOR1"))){
    	        	paracode1=data.getString("VEST_OPERATOR1");
    	        }
            }
            smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
    				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
    				 new String[]{ "CSM", "8899", "SMSTSSL" ,paracode1});
            if(!StringUtils.isBlank(smscontent)){
            	smscontent=smscontent.replace("%101!", mon).replace("%102!", day).replace("%103!", badNess_info);
            }
        }
        
        smsInfo =smsInfo.append(smscontent);
        // HNYD-REQ-20120510-004 不良信息举报回复内容修改 start
//        if ("2".equals(data.getString("VEST_OPERATOR1")))
//        {// 联通短信//BRAND_CODE
//            smsInfo.append("尊敬的客户，您");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(5, 7) + "月");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(8, 10) + "日举报");
//            smsInfo.append(data.getString("BADNESS_INFO", ""));
//            smsInfo.append("归属中国联通，我公司将把您的举报信息转给中国联通进行查证处理，感谢您的支持！中国移动");
//        }
//        else if ("3".equals(data.getString("VEST_OPERATOR1")))
//        {// 电信短信
//            smsInfo.append("尊敬的客户，您");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(5, 7) + "月");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(8, 10) + "日举报");
//            smsInfo.append(data.getString("BADNESS_INFO", ""));
//            smsInfo.append("归属中国电信，我公司将把您的举报信息转给中国电信进行查证处理，感谢您的支持！中国移动");
//        }
//        else if ("1".equals(data.getString("VEST_OPERATOR1")))
//        {// 移动短信
//            smsInfo.append("尊敬的客户，您");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(5, 7) + "月");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(8, 10) + "日举报");
//            smsInfo.append(data.getString("BADNESS_INFO", ""));
//            smsInfo.append("发送不良信息，我们已配合相关部门核实处理，感谢您对营造绿色通信环境做出的努力！中国移动");
//            // 是非中国移动号码，我公司将把您的举报信息转其它电信运营商进行查证处理，感谢您的支持！中国移动
//        }
//        else
//        {
//            smsInfo.append("尊敬的客户，您");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(5, 7) + "月");
//            smsInfo.append(data.getString("RECV_TIME", "").substring(8, 10) + "日举报");
//            smsInfo.append(data.getString("BADNESS_INFO", ""));
//            smsInfo.append("的不良信息，我们已受理，并会认真处理，感谢您对营造绿色通信环境做出的努力！中国移动");
//        }
        // HNYD-REQ-20120510-004 不良信息举报回复内容修改 end

        data.put("RECV_OBJECT", serialNumber);
        data.put("NOTICE_CONTENT", smsInfo);
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        SmsSend.insSms(data);
    }
  //举报处理及时率短信提醒
    public void SMSRemind(IData data) throws Exception
    {
    	String serialNumber=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
 				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
  				 new String[]{ "CSM", "9994", "CLDXHM"});
    	IData usinfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
    	String badImportant=getBadNum("6","01");
    	String badcommon=getBadNum("24","02");
    	StringBuilder smsInfo = new StringBuilder();
    	smsInfo.append("您好！您目前");
    	if(Integer.parseInt(badImportant)>0){
    		smsInfo.append("有"+badImportant+"条关于不良信息的重要工单未在举报成功后6小时内完成客户举报处理和回复，");
    	}
    	if(Integer.parseInt(badcommon)>0){
    		smsInfo.append("有"+badcommon+"条关于不良信息的一般工单未在举报成功后24小时内完成客户举报处理和回复，");
    	}
    	smsInfo.append("请及时处理。中国移动");
    	IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", usinfo.getString("USER_ID", "0"));
        sendInfo.put("SMS_PRIORITY",  "50");
        sendInfo.put("NOTICE_CONTENT", smsInfo.toString());
        sendInfo.put("REMARK", "举报处理及时率短信提醒");

        SmsSend.insSms(sendInfo);

    }
    public String getBadNum(String time,String level) throws Exception
    {
    	SQLParser parser = new SQLParser(new DataMap());

        parser.addSQL("SELECT COUNT(*) BADNUM_IMPORTANT FROM TF_F_BADNESS_INFO D where D.STATE='01' AND D.recv_time <sysdate - "+time+"/24 AND D.IMPORTANT_LEVEL='"+level+"'");

        IDataset out = Dao.qryByParse(parser,Route.CONN_CRM_CEN);

        return ((IData) out.get(0)).getString("BADNUM_IMPORTANT", "");
    }   
    
    /**
	 * 获取vip客户信息
	 * 
	 * @param params
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getVipInfos(IData params) throws Exception {
		params.put("REMOVE_TAG", "0");
		return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_VIPCLASS", params);
		
	}
	/**
	 * 获取用户星级资料信息
	 * 
	 * @param params
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getStarInfos(IData params) throws Exception {
		params.put("ACT_TAG", "1");
		return Dao.qryByCode("MV_TF_O_CREDITINFO", "SEL_BY_SN_VIPCLASS", params);
		
	}
	
	/**
	 * 根据手机号码获取用户级别，先出现tf_f_cust_vip表，如tf_f_cust_vip无记录再查TF_F_CUST_STAR表
	 * 在原用户级别（钻石卡客户、金卡客户、银卡客户、普通客户）取值范围的基础上，
	 * 新增五星（钻）、五星（金）、五星、四星、三星、二星、一星、准星、未评级等9个客户等级。
	 * 即兼容新旧不同的用户等级体系，又保证系统最新修改
	 * 编码	说明
	 *01钻石卡客户  02金卡客户  03银卡客户  04普通客户  05五星（钻） 06	五星（金） 07	五星  08	四星 09三星 10二星 11一星 12准星 13未评级
	 * @param params
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public String getSubsLevel(String  sn) throws Exception{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", sn);
		IDataset vipInfos = getVipInfos(inparams);//先查vip表
		String useLevel = "";
		if(!vipInfos.isEmpty()){
			IData vip = (IData) vipInfos.get(0);
			String vipClass = vip.getString("VIP_CLASS_ID");
			IDataset paramset = ParamInfoQry.getCommparaByCode1("CSM", "8866","VIPJBBM", vipClass, getVisit().getLoginEparchyCode());
			if(paramset!=null && paramset.size()>0){
				useLevel = paramset.getData(0).getString("PARA_CODE2");//根据一级boss接口规范转换
			}				
		}else{
			IDataset userInfos = UserInfoQry.getUserInfoBySn(sn,"0");
			inparams.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
			
			IDataset starInfos = getStarInfos(inparams);//vip表查不到数据时查星级表
			if(!starInfos.isEmpty()){
				IData star = (IData) starInfos.get(0);
				String starClass = star.getString("CREDIT_CLASS");
				IDataset paramset = ParamInfoQry.getCommparaByCode1("CSM", "8866", "XJJBBM", starClass, getVisit().getLoginEparchyCode());
				if(paramset!=null && paramset.size()>0){
					useLevel = paramset.getData(0).getString("PARA_CODE2");//根据一级boss接口规范转换
				}	
			}
			
		}
		if(useLevel.isEmpty()){//如果无数据则为未评级
			useLevel = "13";
		}
		return useLevel;
	}
	//获取号码是否铁通固话
    public  IDataset judgePhoneType(String serialNumber) throws Exception
    {
		IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_OTHER_OPER_EXTEND e ");
        parser.addSQL("where e.serial_number_type='02' and e.vest_operator='01' ");
        parser.addSQL("and (e.other_oper_begin_extend='"+serialNumber.substring(0,3)+"' or e.other_oper_begin_extend='"+serialNumber.substring(0,4)+"')");
		IDataset dataset = Dao.qryByParse(parser,Route.CONN_CRM_CEN);
		
		return dataset;
    }
}
