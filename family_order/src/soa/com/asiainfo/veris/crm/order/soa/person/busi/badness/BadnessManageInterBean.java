
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.ManageForbiddenPointBean;

public class BadnessManageInterBean extends CSBizBean
{
	//HLR加黑类型： 举报 
	private static final String HANDLING_TYPE_REPORT ="0103";
	//数据来源 ：前台提交
	private static final String SOURCE_DATA_PLATFORM = "01";
	// 需要加黑到HLR的处理类型
	private static final String NEED_ADDED_HLR = "'01','0101','0102','02','0201','03','0305'";
	  static transient final Logger logger = Logger.getLogger(BadnessManageInterBean.class);	

	/**
	 * 点对点短信屏蔽外围接口调用落地方接口
	 * @create_date 
	 * @author 
	 */
	public IDataset dealPTPmessage(IData data) throws Exception{		
		// 点对点短信屏蔽功能，将数据投诉与被投诉号码同步到网监平台
      String servType = data.getString("SERV_REQUEST_TYPE", "").trim();
      if(servType!=null && servType.length()>12){
          servType = servType.trim().substring(0, 12);
      }
      if (servType.equals(ManageForbiddenPointBean.OTHER_PROVINCE_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.OTHER_SYS_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.CURRENT_PROVINCE_SERV_TYPE))
      {
          //2014-09-26新增，只对0109-对举报号码屏蔽 才插点对点表
//          if(StringUtils.equals("0109", data.getString("DEAL_RAMARK", ""))) {
              CSAppCall.call("SS.ManageForbiddenPointSVC.addForbiddenData", data);
//          }
      }

      // hlr加黑并同步到一级boss
//      String dealType = data.getString("DEAL_RAMARK", "").trim();
//      if (NEED_ADDED_HLR.indexOf("'" + dealType + "'") > -1)
//      {
//      if(data.getString("BADNESS_INFO_PROVINCE", "").equals("898")){
//    	  IData param = new DataMap();
//          param.putAll(this.convertToHLRParamMap(data));
//          CSAppCall.call("SS.BadnessManageInterSVC.createHLRStopOpenReg", param);
//      }
          
//      }
      IData backInfo = new DataMap();
      backInfo.put("X_RESULTCODE", "0");
      backInfo.put("X_RESULTINFO", "成功！");
      return IDataUtil.idToIds(backInfo);		
	}
		
	 /**
     * 转换HLR所需参数
     * @param data
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	private IData convertToHLRParamMap(IData data) throws Exception{
    	IData hlrParamMap = new DataMap();   	
    	hlrParamMap.put("KIND_ID", "BIP2C097_T2002097_0_0");   	
    	hlrParamMap.put("INDICT_SEQ", data.get("INFO_RECV_ID"));
    	hlrParamMap.put("PROVINCE", data.get("BADNESS_INFO_PROVINCE"));
    	hlrParamMap.put("SUB_NUMBER", data.get("BADNESS_INFO"));
    	//获取当前时间
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	hlrParamMap.put("HANDLING_TIME", sdf);
    	
    	hlrParamMap.put("HANDLING_STATE", "0101");//处理意见？
    	hlrParamMap.put("HANDLING_TYPE", HANDLING_TYPE_REPORT);
    	hlrParamMap.put("SOURCE_DATA", SOURCE_DATA_PLATFORM );
    	
    	hlrParamMap.put("SERVICE_CONTENT", "不良信息");//加黑原因？
//    	hlrParamMap.put("SERVICE_CONTENT", this.getPageData().getData().getString("DEAL_ASSORT", "不良信息"));
    	hlrParamMap.put("IN_MODE_CODE", "2");
    	
    	return hlrParamMap;
    }

    public void afterReg(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("INDICT_SEQ", data.getString("INDICT_SEQ", ""));
        param.put("PROVINCE", data.getString("PROVINCE", ""));
        param.put("SERIAL_NUMBER", data.getString("SUB_NUMBER", ""));
        param.put("HANDLING_TIME", data.getString("HANDLING_TIME", ""));
        param.put("HANDLING_STATE", data.getString("HANDLING_STATE", ""));
        param.put("HANDLING_TYPE", data.getString("HANDLING_TYPE", ""));
        param.put("SOURCE_DATA", data.getString("SOURCE_DATA", ""));
        param.put("ORI_HANDING_SUGGEST", data.getString("ORI_HANDING_SUGGEST", ""));
        param.put("REMOVE_HANDLING_STATE", data.getString("REMOVE_HANDLING_STATE", ""));
        param.put("DATA_CHNL", data.getString("DATA_CHNL", ""));// 01表示界面加解黑
        param.put("BLACK_STATE", data.getString("BLACK_STATE", ""));
        param.put("BADNESS_CLASS", data.getString("BADNESS_CLASS", ""));
        param.put("HANDLING_TIME", data.getString("HANDLING_TIME", ""));
        param.put("UPDATE_TIME", data.getString("UPDATE_TIME", ""));

        String serialNumber = param.getString("SERIAL_NUMBER");
        String handState = data.getString("HANDLING_STATE", "");
        String dataChnl = data.getString("DATA_CHNL", "");
        String flag = "";
        if (StringUtils.isNotBlank(handState))
        {
            // 1:接口HLR加黑 3:界面HLR加黑
            param.put("RELA_ADD_TRADEID", data.getString("TRADE_ID"));
            param.put("ADD_RESULT", data.getString("ADD_RESULT", ""));
            param.put("SERVICE_CONTENT", data.getString("SERVICE_CONTENT", ""));
            param.put("START_DATE", data.getString("UPDATE_TIME", ""));
            param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            Dao.insert("TF_F_GROUP_BADINFO", param, Route.CONN_CRM_CEN);

            if ("01".equals(dataChnl))
            {
                flag = "3";
            }
            else
            {
                flag = "1";
            }
        }
        else
        {
            // 2:接口HLR解黑 4:界面HLR解黑
            String indictSeq = "";
            if ("01".equals(dataChnl))
            {
                indictSeq = data.getString("OLD_INDICT_SEQ", "");
                param.put("RSRV_STR1", data.getString("INDICT_SEQ"));
            }
            else
            {
                IDataset dataset = BadnessInfoQry.qryGroupBadInfo(serialNumber, "01", null, null, null, null);
                indictSeq = dataset.getData(0).getString("INDICT_SEQ");
            }

            IDataset groupBad = BadnessInfoQry.getGroupBadInfoById(indictSeq);
            if (IDataUtil.isEmpty(groupBad))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_88, indictSeq);
            }
            if (!"01".equals(groupBad.getData(0).getString("BLACK_STATE")))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_47, indictSeq);
            }

            param = groupBad.getData(0);
            param.put("RELA_REMOVE_TRADEID", data.getString("TRADE_ID"));
            param.put("REMOVE_RESULT", data.getString("ADD_RESULT", ""));
            param.put("REMOVE_SVC_CONTENT", data.getString("SERVICE_CONTENT", ""));
            param.put("REMOVE_HANDLING_STATE", data.getString("REMOVE_HANDLING_STATE", ""));
            param.put("REMOVE_SOURCE_DATA", data.getString("SOURCE_DATA", ""));
            param.put("UPDATE_TIME", data.getString("UPDATE_TIME", ""));
            param.put("BLACK_STATE", "02");
            param.put("REMOVE_DATA_CHNL", data.getString("DATA_CHNL", ""));
            param.put("REMOVE_HANDLING_TYPE", data.getString("HANDLING_TYPE", ""));
            param.put("END_DATE", data.getString("UPDATE_TIME", ""));

            Dao.update("TF_F_GROUP_BADINFO", param, null, Route.CONN_CRM_CEN);

            if ("01".equals(dataChnl))
            {
                flag = "4";
            }
            else
            {
                flag = "2";
            }
        }

       callIBoss(data, flag);
    }

    private IData callIBoss(IData idata, String flag) throws Exception
    {
        IData param = new DataMap();
        if ("1".equals(flag)) // 1:接口HLR加黑 3:界面HLR加黑 //2:接口HLR解黑 4:界面HLR解黑
        {
            param.put("KIND_ID", "BIP2C097_T2002097_0_0");
        }
        if ("2".equals(flag))
        {
            param.put("KIND_ID", "BIP2C097_T2002097_0_0");
        }
        if ("3".equals(flag))
        {
            param.put("KIND_ID", "BIP2C097_T2002097_0_0");
        }
        if ("4".equals(flag))
        {
            param.put("KIND_ID", "BIP2C098_T2002098_0_0");
        }

        if ("4".equals(flag))
        {
            param.put("INDICT_SEQ", idata.getString("INDICT_SEQ", ""));
            param.put("PROVINCE", idata.getString("PROVINCE", ""));
            param.put("SUB_NUMBER", idata.getString("SERIAL_NUMBER", "")); // SubNumber CallerNo
            param.put("CALLER_NO", idata.getString("SERIAL_NUMBER", "")); // 2012-10-26
            param.put("HANDLING_TIME", idata.getString("HANDLING_TIME", ""));
            param.put("HANDLING_TYPE", idata.getString("HANDLING_TYPE", ""));
            param.put("CENTER_TYPE", idata.getString("SOURCE_DATA", ""));
        }
        else
        {
            param.put("INDICT_SEQ", idata.getString("INDICT_SEQ", ""));
            param.put("PROVINCE", idata.getString("PROVINCE", ""));
            param.put("SUB_NUMBER", idata.getString("SERIAL_NUMBER", "")); // SubNumber CallerNo
            param.put("CALLER_NO", idata.getString("SERIAL_NUMBER", "")); // 2012-10-26
            param.put("HANDLING_TIME", idata.getString("HANDLING_TIME", ""));
            param.put("HANDLING_TYPE", idata.getString("HANDLING_TYPE", ""));
            param.put("SOURCE_DATA", idata.getString("SOURCE_DATA", "")); // 数据来源 SOURCE_DATA: 全网垃圾短信管控平台、全网骚扰电话管控平台
            // CenterType
            param.put("SERVICE_CONTENT", idata.getString("SERVICE_CONTENT", ""));
            param.put("HANDLING_RESULT", "1");
            param.put("CAUSE_FAIL", idata.getString("SOURCE_DATA", ""));
        }

        IData result = IBossCall.dealHLRStopAndOpen(param);
        if (!"0000".equals(result.getString("X_RSPCODE")))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_46, result.getString("X_RSPDESC"));
        }
        return result;
    }

    private void createBadDealInfoArch(IData data) throws Exception
    {
        IData inData = new DataMap();
        inData.put("INFO_RECV_ID", data.getString("INFO_RECV_ID", ""));
        inData.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
        inData.put("SERV_REQUEST_TYPE", data.getString("SERV_REQUEST_TYPE", ""));
        inData.put("DEAL_STAFF_ID", getVisit().getStaffId());
        inData.put("DEAL_DEPART_ID", getVisit().getDepartId());
        inData.put("DEAL_DATE", data.getString("FINISH_DATE", ""));// 归档日期
        inData.put("STATE", PersonConst.STATE_ARCH);// 处理归档
        Dao.save("TF_F_BADNESS_INO_DEAL", inData, Route.CONN_CRM_CEN);
    }

    /**
     * 创建处理不良信息【通知不良信息归属省接口】
     * 
     * @param pd
     * @param data
     *            INDICT_SEQ|举报全网唯一编码,HANDLING_DEPT|处理部门,DEAL_RESULT|举报处理意见, FULL_RSLT|处理结果补充说明
     * @throws Exception
     */
    private void createBadDealInfoByMsgIntf(IData data) throws Exception
    {
        IData inData = new DataMap();
        String deptId = data.getString("HANDLING_DEPT", "");
        if (StringUtils.isBlank(deptId))
        {
            deptId = data.getString("TRADE_DEPART_ID");
        }
        String staffId = data.getString("HANDLING_STAFF", "");
        if (StringUtils.isBlank(staffId))
        {
            staffId = data.getString("TRADE_STAFF_ID");
        }

        // 2.从基本信息表中获取服务请求类别
        String indictSeq = data.getString("INDICT_SEQ", "");
       // IData rData = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", indictSeq);
        IDataset rData1 = BadnessInfoQry.queryAccessoryLists(indictSeq);
        IData rData=rData1.getData(0);


        // 3.判断不良信息处理表获取是否已存在
        IData dData = BadnessInfoQry.qryBadnessDealInfoByPK(indictSeq, "0B");

        // 4.准备不良处理信息数据
        inData.put("INFO_RECV_ID", indictSeq);// 请求服务标识
        inData.put("DEAL_RAMARK", data.getString("DEAL_RESULT", ""));// 举报处理意见
        inData.put("DEAL_REMARK_MAKEUP", data.getString("FULL_RSLT", ""));// 处理意见补充说明
        inData.put("SERV_REQUEST_TYPE", rData == null ? "" : rData.getString("SERV_REQUEST_TYPE", "").trim());// 服务请求类别
        inData.put("DEAL_STAFF_ID", staffId);// 处理员工
        inData.put("DEAL_DEPART_ID", deptId);// 处理部门
        inData.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
        inData.put("STATE", "0B");// 处理通知状态
        if (IDataUtil.isNotEmpty(dData))// 5.更新-不良处理信息数据
            Dao.save("TF_F_BADNESS_INO_DEAL", inData, Route.CONN_CRM_CEN);
        else
            // 6.新增-不良处理信息数据
            Dao.insert("TF_F_BADNESS_INO_DEAL", inData, Route.CONN_CRM_CEN);
    }

    /**
     * 创建不良基本信息【通知不良信息归属省接口】
     * 
     * @param pd
     * @param data
     *            INDICT_SEQ|举报全网唯一编码,MSISDN|举报人手机号码,SUBS_NAME|举报人姓名,
     *            TARGET_NO|被举报号码,SERVICE_CONTENT|举报内容,INDICT_TIME|举报时间, DEAL_RESULT|举报处理意见,FULL_RSLT|处理结果补充说明
     * @throws Exception
     */
    private void createBadInfoByMsgIntf(IData data) throws Exception
    {
        IData inData = new DataMap();
        // 1.判断基本信息表是否存在
        String indictSeq = data.getString("INDICT_SEQ", "");// 请求服务标识
        //IData rData = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", indictSeq);
        IDataset rData = BadnessInfoQry.queryAccessoryLists(indictSeq);

        inData.put("INFO_RECV_ID", indictSeq);
        if (IDataUtil.isNotEmpty(rData))
        {// 2.更新不良基本信息
            inData.put("STATE", "0B");// 通知状态
            inData.put("DEAL_RAMARK", data.getString("DEAL_RESULT", ""));// 举报处理意见
            inData.put("DEAL_REMARK_MAKEUP", data.getString("FULL_RSLT", ""));// 处理意见补充说明
            inData.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
            Dao.executeUpdateByCodeCode("TF_F_BADNESS_INFO", "UPD_BADNESS_BY_PK", inData, Route.CONN_CRM_CEN);
        }
        else
        {// 3.创建不良基本信息
            inData.put("REPORT_SERIAL_NUMBER", data.getString("MSISDN", ""));// 举报人手机号码
            inData.put("REPORT_CUST_NAME", data.getString("SUBS_NAME", ""));// 举报人姓名
            inData.put("BADNESS_INFO", data.getString("TARGET_NO", ""));// 被举报号码
            inData.put("RECV_CONTENT", data.getString("SERVICE_CONTENT", ""));// 举报内容
            inData.put("REPORT_TIME", data.getString("INDICT_TIME", ""));// 举报时间
            inData.put("DEAL_RAMARK", data.getString("DEAL_RESULT", ""));// 举报处理意见
            inData.put("DEAL_REMARK_MAKEUP", data.getString("FULL_RSLT", ""));// 处理意见补充说明
            inData.put("STATE", "0B");// 通知状态
            inData.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
            Dao.insert("TF_F_BADNESS_INFO", inData, Route.CONN_CRM_CEN);
        }
    }

    public IDataset createHLRStopOpenReg(IData data) throws Exception
    {
        IDataset result = new DatasetList();
        String kindId = data.getString("KIND_ID");
        String handState = "";
        String serialNumber = "";
        if ("BIP2C097_T2002097_1_0".equals(kindId))
        {
            IDataUtil.chkParam(data, "INDICT_SEQ");
            IDataUtil.chkParam(data, "PROVINCE");
            serialNumber = IDataUtil.chkParam(data, "SUB_NUMBER");
            IDataUtil.chkParam(data, "HANDLING_TIME");
            IDataUtil.chkParam(data, "HANDLING_TYPE");
            IDataUtil.chkParam(data, "SOURCE_DATA");
            IDataUtil.chkParam(data, "SERVICE_CONTENT");
            IDataUtil.chkParam(data, "HANDLING_RESULT");
            return result;
        }
        else if ("BIP2C098_T2002098_1_0".equals(kindId))
        {
            IDataUtil.chkParam(data, "INDICT_SEQ");
            IDataUtil.chkParam(data, "PROVINCE");
            IDataUtil.chkParam(data, "HANDLING_TIME");
            IDataUtil.chkParam(data, "HANDLING_TYPE");
            IDataUtil.chkParam(data, "CALLER_NO");
            IDataUtil.chkParam(data, "CENTER_TYPE");
            return result;
        }
        else
        {
            IDataUtil.chkParam(data, "INDICT_SEQ");
            IDataUtil.chkParam(data, "PROVINCE");
            serialNumber = IDataUtil.chkParam(data, "SUB_NUMBER");
            IDataUtil.chkParam(data, "HANDLING_TIME");
            handState = IDataUtil.chkParam(data, "HANDLING_STATE");// 处理方式
            IDataUtil.chkParam(data, "HANDLING_TYPE");
            IDataUtil.chkParam(data, "SOURCE_DATA");
            IDataUtil.chkParam(data, "SERVICE_CONTENT");
            IDataUtil.chkParam(data, "IN_MODE_CODE");
        }

        IDataset paraInfo = CommparaInfoQry.getInfoParaCode3("CSM", "1720", handState);
        if (IDataUtil.isEmpty(paraInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_932, handState, CSBizBean.getTradeEparchyCode());
        }
        String tradeTypeCode = paraInfo.getData(0).getString("PARA_CODE1");
        String removeTag = paraInfo.getData(0).getString("PARA_CODE7");// 1为加黑, 2位解黑
        if (StringUtils.isBlank(tradeTypeCode) || StringUtils.isBlank(removeTag))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_933);
        }

        data.put("SERIAL_NUMBER", serialNumber);
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("PROVINCE", "898");
        data.put("HANDLING_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); // 人工触发处理时间为空
        data.put("UPDATE_TIME", data.getString("HANDLING_TIME", "")); // 最后处理时间
        data.put("ORI_HANDING_SUGGEST", data.getString("DEAL_RAMARK", ""));// 原始处理意见 SEL_BY_PARA_CODE3
        if ("1".equals(removeTag))// 1为加黑
            data.put("HANDLING_STATE", handState); // 次处是处理意见，经配置后与规范定义一致 ,,'设计文档中的处理方式'
        else
        {
            data.put("REMOVE_HANDLING_STATE", handState); // 解黑处置方式
            data.put("HANDLING_STATE", "");
        }
        data.put("HANDLING_TYPE", data.getString("HANDLING_TYPE", "").trim());// "0103");//today 这里可能不对.
        // "1");//"HLR加解黑类型为空"); 设计文档中,接口定义为:0103
        // 投诉
        data.put("SOURCE_DATA", data.getString("SOURCE_DATA", "").trim());// "03");// "数据来源,'01'表示前台举报受理 01：垃圾短信平台
        // 02：骚扰电话平台 先写'03',表示用户投诉
        data.put("SERVICE_CONTENT", data.getString("SERVICE_CONTENT", "").trim()); // data.getString("DEAL_REMARK_MAKEUP",
        // ""));//"HLR加解黑原因描述为空");
        data.put("NEED_TRADETYPE", tradeTypeCode);
        data.put("ADD_RESULT", "10");// 10 其他信息convertBalckReason(pd,data.getString("BADINFO_SERVREQUESTTYPE", "")));
        data.put("BLACK_STATE", "01"); // 前台插入的初始状态
        data.put("DATA_CHNL", "02"); // 表示接口写入
        String badness_tag = "";
        if ("01".equals(data.getString("SOURCE_DATA", "")))
            badness_tag = "04";
        else if ("02".equals(data.getString("SOURCE_DATA", "")))
            badness_tag = "06";
        else
            badness_tag = "99";
        data.put("BADNESS_CLASS", badness_tag);

        result = createHLRStopOpenRegIner(data);
        return result;
    }

    public IDataset createHLRStopOpenRegIner(IData data) throws Exception
    {
//        String handState = data.getString("HANDLING_STATE");
//        IData inparam = new DataMap();
//        inparam.put("BLACK_STATE", "01");
//        inparam.put("SERIAL_NUMBER", data.getString("SUB_NUMBER"));
//        IDataset set = BadnessInfoQry.getGROUPInfo(inparam, null);
//        if(StringUtils.isNotBlank(handState)) {
//            if(IDataUtil.isNotEmpty(set)) {//加黑
//                //加黑如果表中有数据，判断svcstate状态，如果已经是加黑状态，返回succ
//                CSAppException.apperr(DedInfoException.CRM_DedInfo_91, set.getData(0).getString("INDICT_SEQ"), set.getData(0).getString("SERIAL_NUMBER"));
//            }
//        }else {
//            if(IDataUtil.isEmpty(set)) {//解黑
//              //解黑如果表中有数据，判断svcstate状态，如果已经是加黑状态，返回succ
//              CSAppException.apperr(DedInfoException.CRM_DedInfo_92, inparam.getString("SERIAL_NUMBER"));
//            }
//        }
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        param.put("SERIAL_NUMBER", data.getString("SUB_NUMBER"));
        IDataset dataset = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", param);
        data.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));

        afterReg(data);
        return IDataUtil.idToIds(data);
    }

    /**
     * @Function: dealBadness
     * @Description: 不良信息处理 ITF_CRM_DealBadness
     * @date May 17, 2014 3:16:20 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealBadness(IData data) throws Exception
    {
        String infoRecvId = data.getString("INDICT_SEQ", "");
        IDataset badInfo = BadnessInfoQry.qryBadnessInfos(infoRecvId, null, null, null, null, null, null, null, null, null, null, null,null);
        if (IDataUtil.isEmpty(badInfo))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_86);
        }

        IData datas = new DataMap();
        datas.put("MSISDN", badInfo.getData(0).getString("REPORT_SERIAL_NUMBER", ""));
        datas.put("INFO_RECV_ID", data.getString("INDICT_SEQ", ""));
        datas.put("DEAL_RAMARK", data.getString("DEAL_RESULT", ""));
        datas.put("DEAL_REMARK_MAKEUP", data.getString("FULL_RSLT", ""));
        datas.put("SERV_REQUEST_TYPE", data.getString("SVC_TYPE_ID", ""));
        datas.put("DEAL_STAFF_ID", data.getString("HANDING_STAFF", ""));
        datas.put("DEAL_DEPART_ID", data.getString("HANDING_DEPART", ""));
        datas.put("DEAL_DATE", SysDateMgr.getSysDate());
        datas.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE", ""));
        datas.put("STATE", "02");
        datas.put("PRETREATMENT", data.getString("PRETREATMENT"));//2014-10-22加入预处理信息
        Dao.insert("TF_F_BADNESS_INO_DEAL", datas, Route.CONN_CRM_CEN);

        IData param = new DataMap();
        param.clear();
        param.put("INFO_RECV_ID", infoRecvId);
        param.put("STATE", "02");
        param.put("SORT_RESULT_TYPE", data.getString("SVC_TYPE_ID", ""));
        param.put("SERV_REQUEST_TYPE", data.getString("SVC_TYPE_ID", ""));
        param.put("REPORT_TYPE_CODE", data.getString("REP_TYPE", ""));
        param.put("COMBO_TYPE", data.getString("COMBO_TYPE", ""));
        param.put("BRAND_CODE", data.getString("SUBS_BRAND", ""));
        param.put("IN_DATE", data.getString("JOIN_TIME", ""));
        param.put("IS_REAL_NAME", data.getString("REAL_NAME_SYS_FLAG", ""));
        param.put("USER_STATE_CODESET", data.getString("OUTAGE_STATE", ""));
        param.put("ALLNEWROWE_FEE", data.getString("ARREARAGE", ""));
        param.put("NOTE_STATE", data.getString("NOTE_STATE", ""));
        param.put("MULT_MES_STATE", data.getString("MULT_MES_STATE", ""));
        param.put("OTHER_BUS_FLAG", data.getString("OTHER_BUS_FLAG", ""));
        
        Dao.save("TF_F_BADNESS_INFO", param, new String[]
        { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

        if (!"".equals(data.getString("REPTYPE")) && data.getString("REPTYPE") != null)
        {
            param.put("REPORT_TYPE_CODE", data.getString("REPTYPE"));// 举报对象类型
        }
        param.put("OPEN_DATE", data.getString("JOINTIME"));// 扩展字段 
        param.put("PRODUCT_NAME", data.getString("COMBOTYPE"));
        param.put("BRAND_NO", data.getString("SUBSBRAND"));
        param.put("OWE_FEE", data.getString("ARREARAGE"));
        param.put("USER_STATE_CODESET", data.getString("OUTAGESTATE"));
        param.put("SMS_CODE", data.getString("NOTESTATE"));// 短信
        param.put("SMSC_CODE", data.getString("MULTMESSTATE"));// 彩信
        param.put("OTHER_STATUS", data.getString("OTHERBUSFLAG"));// 其他是否消费
        param.put("IS_REAL_NAME", data.getString("REALNAMESYSFLAG"));
        param.put("REPORT_TYPE_CODE", data.getString("REPTYPE"));// 举报对象类型

        datas.put("INDICT_TIME", badInfo.getData(0).getString("RECV_TIME", ""));
        datas.put("RECV_IN_TYPE", badInfo.getData(0).getString("RECV_IN_TYPE", ""));

        sendMms(datas);

        updateBadInfoArch(datas);

        IData backInfo = new DataMap();
        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "成功！");
        backInfo.put("X_RECORDNUM", "1");
        return IDataUtil.idToIds(backInfo);
    }

    /**
     * @Function: dealReprot
     * @Description: ITF_CRM_BossReportbedinfoDeal 不良信息举报受理一级boss(异地举报)
     * @date Jun 11, 2014 10:27:40 AM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprot(IData data) throws Exception
    {
    	BadInfoComplainDealBean dealBean = new BadInfoComplainDealBean();
        IData param = transInput(data);
        param.put("FIRST_TYPE_CODE", "01");
        param.put("SECOND_TYPE_CODE", "01");
        param.put("THIRD_TYPE_CODE", "02");
        String serialNumber = param.getString("REPORT_SERIAL_NUMBER");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_01);// 举报手机号不能为空
        }

        IDataset epary = BadnessInfoQry.qryEpareycodeout(serialNumber);
        if (IDataUtil.isEmpty(epary))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_70);// 举报手机号为外省手机号
        }
        if (!"898".equals(epary.getData(0).getString("PROV_CODE")))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_70);// 举报手机号为外省手机号
        }
        logger.info("buliangxinxi1112:"+param.toString());
        String badSerialNumber = param.getString("BADNESS_INFO","");
        if(badSerialNumber.equals(serialNumber)){
        	CSAppException.apperr(DedInfoException.CRM_DedInfo_95);
        }
        int tag = 0;// 不为网站
        if ("".equals(badSerialNumber))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_08);
        }
        else
        {
            if (badSerialNumber.startsWith("9"))
            {
                tag = 1;// 为网站
            }else if(badSerialNumber.length()<5){
            	 CSAppException.apperr(DedInfoException.CRM_DedInfo_93);
            }else if(badSerialNumber.startsWith("13")||badSerialNumber.startsWith("14")||badSerialNumber.startsWith("15")||badSerialNumber.startsWith("17")||badSerialNumber.startsWith("18")){
            	if(badSerialNumber.length()!=11){
            		CSAppException.apperr(DedInfoException.CRM_DedInfo_94);
            	}
            }
        }

        String eparchy = epary.getData(0).getString("AREA_CODE", ""); // 地州编码
        String recvProvince = param.getString("RECV_PROVINCE", "");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_84);// 举报手机号无正常数据
        }

        String reportlevel = dealBean.getSubsLevel(serialNumber);
        IDataset vipInfo = CustVipInfoQry.qryVipInfoByCustId(userInfo.getString("CUST_ID"));
//        if (IDataUtil.isNotEmpty(vipInfo))
//        {
//            reportlevel = "0" + vipInfo.getData(0).getString("VIP_CLASS_ID", "");
//        }
//        if (reportlevel.equalsIgnoreCase("04"))
//        {
//            reportlevel = "01"; // 钻卡大客户
//        }
//        else if (reportlevel.equalsIgnoreCase("03"))
//        {
//            reportlevel = "02"; // 金卡大客户
//        }
//        else if (reportlevel.equalsIgnoreCase("02"))
//        {
//            reportlevel = "03"; // 银卡大客户
//        }
//        else
//        {
//            reportlevel = "04"; // 普通客户
//        }

        String brandCode = userInfo.getString("BRAND_CODE", "");
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

        String indate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);
        String eqrev = param.getString("INFO_RECV_ID", "");// 各省均需要修改

        IData datatemp = new DataMap();
        datatemp.put("INFO_RECV_ID", eqrev);
        datatemp.put("KF_CALL_IN_SERIAL_NUMBER", param.getString("KF_CALL_IN_SERIAL_NUMBER", ""));
        datatemp.put("KF_CALL_OUT_SERIAL_NUMBER", param.getString("KF_CALL_OUT_SERIAL_NUMBER", ""));
        datatemp.put("REPORT_SERIAL_NUMBER", param.getString("REPORT_SERIAL_NUMBER", ""));

        datatemp.put("REPORT_CUST_NAME", param.getString("REPORT_CUST_NAME", ""));
        if ("".equals(data.getString("REPORT_CUST_NAME", "")))
        {
            datatemp.put("REPORT_CUST_NAME", param.getString("REPORT_CUST_NAME", ""));
        }
        else
        {
            datatemp.put("REPORT_CUST_NAME", vipInfo.getData(0).getString("CUST_NAME", ""));
        }

        // 重要程度一般紧急程度为“重要”，处理时限为12小时，紧急程度为“一般”，处理时限为24小时。”
        if ("01".equals(data.getString("IMPORTANT_LEVEL", "")))
        {
            datatemp.put("DEAL_LIMIT", "12");
        }
        else
        {
            datatemp.put("DEAL_LIMIT", "24");
        }
        datatemp.put("REPORT_CUST_LEVEL", reportlevel);
        datatemp.put("REPORT_BRAND_CODE", brandCode);

        String importLevel = param.getString("IMPORTANT_LEVEL", "");
        if (StringUtils.isBlank(importLevel))
        {
            importLevel = "01";
        }
        datatemp.put("IMPORTANT_LEVEL", importLevel);
        datatemp.put("REPORT_CUST_PROVINCE", "898"); // 举报用户归属省
        datatemp.put("RECV_PROVINCE", recvProvince); // 举报受理省
        datatemp.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", "0898"));

        datatemp.put("REPORT_TIME", SysDateMgr.getSysTime());
        datatemp.put("RECV_TIME", SysDateMgr.getSysTime());
        datatemp.put("BADNESS_INFO", param.getString("BADNESS_INFO", ""));

        datatemp.put("RECV_CONTENT", param.getString("RECV_CONTENT", ""));
        datatemp.put("REPORT_TYPE_CODE", param.getString("REP_TYPE", "")); // 举报对象类型
        datatemp.put("VRIFY_INFO", "");
        datatemp.put("RECV_STAFF_ID", param.getString("DEAL_STAFF_ID"));
        datatemp.put("RECV_IN_TYPE", "01"); // 受理渠道
        datatemp.put("STICK_LIST", param.getString("STICK_LIST", ""));
        datatemp.put("RECORD_FILE_LIST", "");
        datatemp.put("STATE", "01");

//        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
//				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
//				 new String[]{ "CSM", "8899", "CFJB" });
//        IDataset iDataset = BadnessInfoQry.qryMaxTime(data);
        // 是否重复举报(0，不是重复举报；1，重复举报)
//        if (IDataUtil.isEmpty(iDataset) || "".equals(iDataset.getData(0).getString("MAX_TIME", "")))
//        {
            datatemp.put("REPEAT_REPORT", "0");
//        }
//        else if (Integer.parseInt(iDataset.getData(0).getString("MAX_TIME", "")) < Integer.parseInt(urltime))
//        {
//            datatemp.put("REPEAT_REPORT", "1");
//            datatemp.put("STATE", PersonConst.STATE_ARCH);// 归档
//            // dao.insert("tf_f_badness_info", datatemp); // 将资料输入插入表 :TODO老系统有这个，感觉不对，是否去掉，等下一版本合版本
//        }
//        else if (Integer.parseInt(iDataset.getData(0).getString("MAX_TIME", "")) > Integer.parseInt(urltime))
//        {
//            datatemp.put("REPEAT_REPORT", "0");
//        }


        
        param.put("REPORT_TYPE_CODE1", dealBean.dealReportType(param.getString("REPORT_TYPE_CODE", "")));// 全网
        IData dolldata = dealBean.dealCollation(param.getString("BADNESS_INFO"));
//        IData overdata = dealBean.dealDataover(dolldata, param);
        datatemp.put("TARGET_PROVINCE", dolldata.getString("BADNESS_INFO_PROVINCE", "")); // 目标省代码 BADNESS_INFO_PROVINCE
        datatemp.put("BADNESS_INFO_PROVINCE", dolldata.getString("BADNESS_INFO_PROVINCE", ""));
        // datatemp.put("OPERATE_STEP", dolldata.getString(""));
        if ("".equals(dolldata.getString("BADNESS_INFO_PROVINCE", "")))
        {
            datatemp.put("BADNESS_INFO_PROVINCE", "898"); // 被举报号码归属省/总部

        }
        else
        {
            datatemp.put("BADNESS_INFO_PROVINCE", dolldata.getString("BADNESS_INFO_PROVINCE", "")); // 被举报号码归属省/总部
        }

        if (param.getString("SERV_REQUEST_TYPE", "").trim().length() > 0)
        {
            datatemp.put("SORT_RESULT_TYPE", param.getString("SERV_REQUEST_TYPE", ""));// 分拣分类结果
            datatemp.put("SERV_REQUEST_TYPE", param.getString("SERV_REQUEST_TYPE", ""));// 服务请求类别
        }
//        else
//        {
//            datatemp.put("SORT_RESULT_TYPE", overdata.getString("SERV_TYPE", ""));// 分拣分类结果
//            datatemp.put("SERV_REQUEST_TYPE", overdata.getString("SERV_TYPE", ""));// 服务请求类别
//        }
//        datatemp.put("RSRV_STR2", overdata.getString("SERV_NAME", ""));// 服务请求类别2012.2.23

        IData dealdata = new DataMap();
        dealdata.put("INFO_RECV_ID", eqrev);
        dealdata.put("DEAL_RAMARK", "");
        dealdata.put("DEAL_REMARK_MAKEUP", "");
        dealdata.put("SERV_REQUEST_TYPE", param.getString("SERV_REQUEST_TYPE", ""));

        // 服务请求类别修改为4级10位
//        if (datatemp.get("SERV_REQUEST_TYPE") != null && datatemp.get("SERV_REQUEST_TYPE").toString().trim().length() > 10)
//        {
//            datatemp.put("SERV_REQUEST_TYPE", datatemp.get("SERV_REQUEST_TYPE").toString().trim().substring(0, 10));
//        }
        dealdata.put("DEAL_STAFF_ID", param.getString("DEAL_STAFF_ID"));
        dealdata.put("DEAL_DEPART_ID", param.getString("DEAL_DEPART_ID"));
        dealdata.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
        dealdata.put("STATE", PersonConst.STATE_NORMAL);
        dealBean.fieldcheck(datatemp);
        Dao.insert("TF_F_BADNESS_INFO", datatemp, Route.CONN_CRM_CEN); // 将资料输入插入表

        Dao.insert("TF_F_BADNESS_INO_DEAL", dealdata, Route.CONN_CRM_CEN);

        if (!"06".equals(data.getString("RECV_IN_TYPE", "")))
        {
            dealBean.sendSms(datatemp);// shenh
        }

        IData backInfo = new DataMap();
        if ("1".equals(datatemp.getString("REPEAT_REPORT", "")))
        {
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "成功！但重复举报");
            backInfo.put("X_RECORDNUM", "1");
            backInfo.put("X_RSPCODE", "0000");
            backInfo.put("X_RSPTYPE", "0");
            backInfo.put("X_RSPDESC", "成功！但重复举报");
            return IDataUtil.idToIds(backInfo);
        }

        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "成功！");
        backInfo.put("X_RECORDNUM", "1");
        backInfo.put("X_RSPCODE", "0000");
        backInfo.put("X_RSPTYPE", "0");
        backInfo.put("X_RSPDESC", "成功！");
        return IDataUtil.idToIds(backInfo);
    }

    /**
     * @Function: dealReprotnet
     * @Description: 外围网上营业厅调用接口 垃圾短信举报(new) ITF_CRM_NetReportbedinfoDeal
     * @date Jun 11, 2014 10:22:16 AM
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprotnet(IData data) throws Exception
    {
        data.put("FIRST_TYPE_CODE", "01");
        data.put("SECOND_TYPE_CODE", "03");
        data.put("THIRD_TYPE_CODE", "02");
        data.put("FOURTH_TYPE_CODE", "55");
        BadInfoComplainDealBean bean = new BadInfoComplainDealBean();
        IDataset badInfo = bean.badInfoActive(data);
        if (IDataUtil.isNotEmpty(badInfo))
        {
            String recvId = badInfo.getData(0).getString("INFO_RECV_ID");
            // 本省受理，被举报号码归属于本省，解析彩信内容文件上传FTP
            if ("898".equals(data.getString("BADNESS_INFO_PROVINCE")))
            {
                String reportTypeCode = data.getString("REPORT_TYPE_CODE", "");
                String attachName = data.getString("ATTACH_NAME", "");
                if ("03".equals(reportTypeCode) && StringUtils.isNotBlank(attachName))
                {
                    String fileList = Mm7Parse.resolvingMmsContent(attachName, recvId, 0);
                    if (fileList == null || fileList.length() < 1)
                    {
                        fileList = "";
                    }
                    IData params = new DataMap();
                    params.put("INFO_RECV_ID", recvId);
                    params.put("STICK_LIST", data.getString("ATTACH_NAME", ""));
                    params.put("RSRV_STR4", fileList);
                    Dao.save("TF_F_BADNESS_INFO", params, new String[]
                    { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);
                }
            }
        }

        IData backInfo = new DataMap();
        if (IDataUtil.isNotEmpty(badInfo))
        {
            backInfo.put("INFO_RECV_ID", badInfo.getData(0).getString("INFO_RECV_ID"));
        }
        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "成功！");
        backInfo.put("X_RECORDNUM", "1");
        return IDataUtil.idToIds(backInfo);
    }

    /**
     * @Function: dealReprotout
     * @Description: 不良信息举报一级客服派发 ITF_CRM_ReportbedinfooutDeal
     * @date May 30, 2014 2:48:24 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealReprotout(IData data) throws Exception
    {
        IData temp = transInput(data);
//        IDataset reportEparchy = BadnessInfoQry.qryEpareycodeout(temp.getString("REPORT_SERIAL_NUMBER"));// 找举报号码归属省
//        if (IDataUtil.isNotEmpty(reportEparchy))
//        {
//            temp.put("REPORT_CUST_PROVINCE", reportEparchy.getData(0).getString("PROV_CODE"));
//        }
//        else
//        {
            temp.put("REPORT_CUST_PROVINCE", temp.getString("RECV_PROVINCE", ""));
//        }

        BadInfoComplainDealBean dealBean = new BadInfoComplainDealBean();
//        IData dolldata = dealBean.dealCollation(temp.getString("BADNESS_INFO"));
//        if (!"898".equals(dolldata.getString("BADNESS_INFO_PROVINCE")))
//        {
//            IData ibossData = new DataMap();
//            ibossData.put("INDICT_SEQ", data.getString("INDICT_SEQ", ""));// 举报全网唯一编码INFO_RECV_ID
//            ibossData.put("SVC_TYPE_ID", "1001010299");// 服务请求编码
//            ibossData.put("DEAL_RESULT", "0114");// 举报处理结果
//            ibossData.put("FULL_RSLT", "工单分拣归属省错误，请重新分拣举报");// 处理结果补充说明
//            ibossData.put("HANDING_STAFF", "SUPERUSR");// 回复员工
//            ibossData.put("HANDING_DEPART", "移动省公司--36601");// 回复部门
//
//            ibossData.put("FIRST_REPLY_TIME", SysDateMgr.getSysTime());// 回复时间
//            ibossData.put("CONTACT_PHONE", data.getString("CONTACT_PHONE", ""));// 联系号码
//            ibossData.put("ATTACH_LIST", data.getString("ATTACH_LIST", ""));// 附件列表
//            ibossData.put("ATTACH_NAME", data.getString("ATTACH_NAME", ""));// 附件文件名
//            ibossData.put("SUBS_BRAND", "05");
//            IBossCall.dealQBadnessImpeachInfoIboss(ibossData);
//            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "工单分拣归属省错误，请重新分拣举报");
//            IData backInfo = new DataMap();
//            backInfo.put("X_RESULTCODE", "0");
//            backInfo.put("X_RESULTINFO", "工单分拣归属省错误，请重新分拣举报");
//            backInfo.put("X_RECORDNUM", "1");
//            return IDataUtil.idToIds(backInfo);
//        }

//        IDataset badEparchy = BadnessInfoQry.qryEpareycodeout(temp.getString("BADNESS_INFO"));// 找被举报号码归属省
//        if (IDataUtil.isNotEmpty(badEparchy))
//        {
//            temp.put("BADNESS_INFO_PROVINCE", badEparchy.getData(0).getString("PROV_CODE", ""));
//            temp.put("TARGET_PROVINCE", badEparchy.getData(0).getString("PROV_CODE", ""));
//        }
//        data1.put("KF_CALL_IN_SERIAL_NUMBER", data1.getString("PROCESS_TYPE", "3"));// 区分一级交易
        String sysTime = SysDateMgr.getSysTime();
        temp.put("REPORT_TIME", sysTime);
        temp.put("RECV_TIME", sysTime);
        temp.put("STATE", PersonConst.STATE_HEAD);

//        if(!"000".equals(temp.getString("RECV_PROVINCE")))
//        {
//	        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
//					 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
//					 new String[]{ "CSM", "8899", "CFJB" });
//	        IDataset iDataset = BadnessInfoQry.qryMaxTime(temp);
	        // 是否重复举报(0，不是重复举报；1，重复举报)
//	        if (IDataUtil.isEmpty(iDataset) || "".equals(iDataset.getData(0).getString("MAX_TIME", "")))
//	        {
	            temp.put("REPEAT_REPORT", "0");
//	        }
//	        else if (Integer.parseInt(iDataset.getData(0).getString("MAX_TIME", "")) < Integer.parseInt(urltime))
//	        {
//	            temp.put("REPEAT_REPORT", "1");
//	            temp.put("STATE", PersonConst.STATE_ARCH);// 归档
//	        }
//	        else if (Integer.parseInt(iDataset.getData(0).getString("MAX_TIME", "")) > Integer.parseInt(urltime))
//	        {
//	            temp.put("REPEAT_REPORT", "0");
//	        }
//        }
//        
//        else
//        {
//        	temp.put("REPEAT_REPORT", "0");
//        }


        temp.put("DEAL_DATE", sysTime);
        temp.put("REPORT_BRAND_CODE", "04");
        dealBean.fieldcheck(temp);// HNYD-REQ-ZB-20120419-004关于现网BOSS侧32个上传集团文件校验改造需求
        
     // 重要程度一般紧急程度为“重要”，处理时限为12小时，紧急程度为“一般”，处理时限为24小时。”
        if ("01".equals(temp.getString("IMPORTANT_LEVEL", "")))
        {
            temp.put("DEAL_LIMIT", "6");
        }
        else
        {
            temp.put("DEAL_LIMIT", "24");
        }
        Dao.insert("TF_F_BADNESS_INFO", temp, Route.CONN_CRM_CEN);// 将资料输入插入表

        
        Dao.insert("TF_F_BADNESS_INO_DEAL", temp, Route.CONN_CRM_CEN);

        IData backInfo = new DataMap();
        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "成功！");
        backInfo.put("X_RECORDNUM", "1");
        return IDataUtil.idToIds(backInfo);
    }

    private IData getHttpdata(IData data) throws Exception
    {
        IData temp = new DataMap();
        String recvId = IDataUtil.chkParam(data, "INDICT_SEQ");
        temp.put("INFO_RECV_ID", recvId);// 举报全网唯一编码
        temp.put("DEAL_STAFF_ID", data.getString("HANDING_STAFF", ""));// 催办员工
        temp.put("DEAL_DEPART_ID", data.getString("HANDING_DEPART", ""));// 催办部门
        temp.put("DEAL_REMARK_MAKEUP", data.getString("HANDLING_COMMNET", ""));// 催办原因

        return temp;
    }

    /**
     * @Function: hastenBadness
     * @Description: 不良信息催办 ITF_CRM_HastenBadness
     * @date May 29, 2014 10:03:31 AM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset hastenBadness(IData data) throws Exception
    {
        IData param = getHttpdata(data);
        // 判断不良信息处理表获取是否已存在
        IData dealInfo = BadnessInfoQry.qryBadnessDealInfoByPK(param.getString("INFO_RECV_ID"), PersonConst.STATE_HASTEN);

        param.put("DEAL_DATE", SysDateMgr.getSysDate());
        param.put("STATE", PersonConst.STATE_HASTEN);
        if (IDataUtil.isNotEmpty(dealInfo))
        {
            Dao.save("TF_F_BADNESS_INO_DEAL", param, new String[]
            { "INFO_RECV_ID", "STATE" }, Route.CONN_CRM_CEN);
        }
        else
        {
            Dao.insert("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);
        }

        param.clear();
        param.put("INFO_RECV_ID", data.getString("INDICT_SEQ", ""));
        param.put("HASTEN_STATE", PersonConst.STATE_HASTEN);

        Dao.save("TF_F_BADNESS_INFO", param, new String[]
        { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

        IData backInfo = new DataMap();
        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "成功！");
        backInfo.put("X_RECORDNUM", "1");
        return IDataUtil.idToIds(backInfo);
    }

    /**
     * @Function: queryBadness
     * @Description: 举报信息查询 ITF_CRM_QueryReportInfo
     * @date May 29, 2014 10:22:26 AM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset queryBadness(IData data) throws Exception
    {
        String revcID = data.getString("INDICT_SEQ", "");
        IDataset result = BadnessInfoQry.queryBadnessInfoByRecvId(revcID);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_11);
        }
        else
        {
            IData temp = result.getData(0);
            temp.put("INDICT_SEQ", temp.getString("INFO_RECV_ID", ""));
            temp.put("CURRENT_NODE", temp.getString("STATE", ""));
            temp.put("HANDING_DEPART", temp.getString("DEAL_DEPART_ID", ""));
            temp.put("HANDING_STAFF", temp.getString("DEAL_STAFF_ID", ""));
            temp.put("STAFF_CONTACT_PHONE", temp.getString("CONTACT_SERIAL_NUMBER", ""));
            temp.put("IS_ITERANCE", temp.getString("REPEAT_REPORT", ""));
            temp.put("IS_VALID", temp.getString("IS_VALID", ""));
        }
        return result;
    }

    private void sendMms(IData data) throws Exception
    {
        if (data.getString("INDICT_TIME", "").length() < 11)
        {
        	CSAppException.apperr(DedInfoException.CRM_DedInfo_48);
        }

        StringBuilder smsInfo = new StringBuilder();
        String serialNumber = data.getString("MSISDN", "");
        String badNess_info = data.getString("BADNESS_INFO", "");
        String smscontent="";
        String reportTypeCode= data.getString("REP_TYPE", "");//举报对象
        if(reportTypeCode.equals("04")){//举报网站
        	String recvInType=data.getString("RECV_IN_TYPE", "");
        	String dealRamark=data.getString("DEAL_RESULT", "");//举报处理意见
        	
        	if(recvInType.equals("04")){
        		smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
       				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
       				 new String[]{ "CSM", "8899", "SMSTSCL" ,recvInType});
        	}else{
        		if(dealRamark.equals("0401")||dealRamark.equals("0402")||dealRamark.equals("0403")||dealRamark.equals("0405")){
        			smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
             				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE2","PARA_CODE3" }, "PARA_CODE23", 
             				 new String[]{ "CSM", "8899", "SMSTSCL" ,recvInType,dealRamark});
            	}else{
            		String mon=data.getString("RECV_TIME", "").substring(5, 7);
                    String day=data.getString("RECV_TIME", "").substring(8, 10);
                    
                    smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
            				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
            				 new String[]{ "CSM", "8899", "SMSTSCL" ,"0"});
                    if(StringUtils.isBlank(smscontent)){
                    	smscontent=smscontent.replace("%101!", mon).replace("%102!", day).replace("%103!", badNess_info);
                    }
            	}
        	}
//        	else {
//        		smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
//        				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
//        				 new String[]{ "CSM", "8899", "SMSTSCL" ,recvInType});
//        	}
        	
        }else{
        	String mon=data.getString("INDICT_TIME", "").substring(5, 7);
            String day=data.getString("INDICT_TIME", "").substring(8, 10);
            
            smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
    				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
    				 new String[]{ "CSM", "8899", "SMSTSCL" ,"0"});
            if(!StringUtils.isBlank(smscontent)){
            	smscontent=smscontent.replace("%101!", mon).replace("%102!", day).replace("%103!", badNess_info);
            }
        }
        
        smsInfo =smsInfo.append(smscontent);
//        StringBuilder smsInfo = new StringBuilder();
//        smsInfo.append("尊敬的客户，您");
//        smsInfo.append(data.getString("INDICT_TIME", "").substring(5, 7) + "月");
//        smsInfo.append(data.getString("INDICT_TIME", "").substring(8, 10) + "日举报");
//        smsInfo.append(data.getString("TARGET_NO", ""));
//        smsInfo.append("曾向您发送了不良信息，我们已配合相关部门核实并做了妥善处理，感谢您对营造绿色通信环境做出的努力！ 中国移动");

        if (!"06".equals(data.getString("RECV_IN_TYPE", "")))
        {
            IData sms = new DataMap();
            sms.put("NOTICE_CONTENT", smsInfo);
            sms.put("CHAN_ID", "0000");
            sms.put("FORCE_OBJECT", "10086999");
            sms.put("RECV_OBJECT", serialNumber);
            SmsSend.insSms(sms);
        }

       updateArchForIboss(data);
    }

    private IData transInput(IData data) throws Exception
    {
        IData temp = new DataMap();
        String indictSeq = IDataUtil.chkParam(data, "INDICT_SEQ");
        String msisdn = IDataUtil.chkParam(data, "MSISDN");
        String targetNo = IDataUtil.chkParam(data, "TARGET_NO");

        String staffid = data.getString("HANDING_STAFF", "");
        if (staffid == null || staffid.trim().length() <= 0)
        {
            staffid = "一级BOSS--IBOSS000";
        }
        String deptid = data.getString("HANDLING_DEPT", "");
        if (deptid == null || deptid.trim().length() <= 0)
        {
            deptid = "一级BOSS--00309";
        }

        temp.put("RECV_PROVINCE", data.getString("HOME_PROV", ""));// 受理省编码HomeProv
        temp.put("TARGET_PROVINCE", data.getString("T_PROV", ""));// 目标省编码TProv
        temp.put("REPORT_TIME", data.getString("ACCEPT_TIME", ""));// 举报时间AcceptTime
        temp.put("BADNESS_INFO", targetNo);// 被举报号码TargetNo
        temp.put("BADNESS_INFO_PROVINCE", data.getString("TARGET_PROV", ""));// 被举报号码归属省TargetProv
        // temp.put("REPORT_CUST_NAME", data.getString("SUBS_NAME",""));//举报人姓名SubsName
        temp.put("REPORT_SERIAL_NUMBER", msisdn);// 举报人手机号码 MSISDN
        temp.put("REPORT_CUST_LEVEL", data.getString("SUBS_LEVEL", ""));// 举报人等级
        temp.put("REPORT_BRAND_CODE", data.getString("SUBS_BRAND", ""));// 举报人名牌
        temp.put("REPORT_TYPE_CODE", data.getString("REP_TYPE", ""));// 举报对象RepType
        temp.put("RECV_CONTENT", data.getString("SERVICE_CONTENT", ""));// 举报内容 ServiceContent
        temp.put("OPERATE_STEP", data.getString("PRE_TREATMENT", ""));// 预处理情况 Pretreatment
        temp.put("VRIFY_INFO", data.getString("IS_VERIFY", "")); // 核实信息
        temp.put("SERV_REQUEST_TYPE", data.getString("SVC_TYPE_ID", ""));// 服务请求编码SvcTypeId
        temp.put("SORT_RESULT_TYPE", data.getString("SVC_TYPE_ID", ""));// 服务请求编码SvcTypeId
        temp.put("RECV_IN_TYPE", data.getString("CONTACT_CHANNEL", ""));// 举报受理渠道ContactChannel
        temp.put("PROCESS_TYPE", data.getString("PROCESS_TYPE", ""));// 流程类别
        temp.put("IMPORTANT_LEVEL", data.getString("IMP_LEVEL", "01"));// 重要程度 ImpLevel
        if(!"".equals(data.getString("REPORT_TYPE")) && data.getString("REPORT_TYPE")!=null)
        {
        	temp.put("REPORT_TYPE_CODE", data.getString("REPORT_TYPE", ""));
        }

        temp.put("RSRV_STR1", data.getString("CONN_FASHION", ""));
        temp.put("STICK_LIST", data.getString("ATTACH_NAME", ""));// 附件列表 add by wangzq3 for 垃圾彩信 20120621
        temp.put("INFO_RECV_ID", indictSeq);// 举报全网唯一编码

        temp.put("DEAL_STAFF_ID", staffid);// 处理员工
        temp.put("DEAL_DEPART_ID", deptid);// 处理部门
        temp.put("RECV_STAFF_ID", staffid);
        temp.put("DEAL_DATE", data.getString("INDICT_TIME", ""));// 举报时间
        temp.put("DEAL_RAMARK", data.getString("DEAL_RESULT", ""));// 举报处理意见
        temp.put("DEAL_REMARK_MAKEUP", data.getString("FULL_RSLT", ""));// 处理结果补充说明

        return temp;
    }

    /**
     * 不良信息自动归档(IBOSS)
     * 
     * @Function: updateArchForIboss
     * @Description: TODO
     * @date May 19, 2014 11:49:15 AM
     * @param data
     * @throws Exception
     * @author longtian3
     */
    private IData updateArchForIboss(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("HANDING_DEPART", getVisit().getDepartId() + "-" + getVisit().getDepartName());// 归档部门
        param.put("HANDING_STAFF", getVisit().getStaffId() + "-" + getVisit().getStaffName());// 归档员工
        param.put("HANDLING_COMMNET", data.getString("DEAL_REMARK_MAKEUP", ""));// 归档意见
        param.put("PIGEONHOLE_TIME", SysDateMgr.getSysDate());// 归档时间

        if ("".equals(data.getString("INDICT_SEQ", "")))
        {
            param.put("INDICT_SEQ", data.getString("INFO_RECV_ID", ""));// 举报全网唯一编码
        }
        else
        {
            param.put("INDICT_SEQ", data.getString("INDICT_SEQ", ""));// 举报全网唯一编码
        }
        return IBossCall.dealArchIboss(param);
    }

    /**
     * @Function: updateBadDealInfoByMsgIntf 不良短信举报通知归属省 落地方
     * @Description: ITF_CRM_LOCAL_INFORM
     * @date Jul 10, 2014 9:42:35 AM
     * @param input
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IData updateBadDealInfoByMsgIntf(IData input) throws Exception
    {
        IData result = new DataMap();
        if (input.getString("INDICT_TIME", "").length() < 11)
        {// 判断举报日期格式长度不对
            result.put("X_RESULTCODE", "-1");// 失败标志
            result.put("X_RESULTINFO", "举报日期格式长度不正确");
            return result;
        }
        String serialNumber = input.getString("MSISDN", "");
        StringBuilder smsInfo = new StringBuilder();
        smsInfo.append("尊敬的客户，您");
        smsInfo.append(input.getString("INDICT_TIME", "").substring(5, 7) + "月");
        smsInfo.append(input.getString("INDICT_TIME", "").substring(8, 10) + "日举报");
        smsInfo.append(input.getString("TARGET_NO", ""));
        smsInfo.append("曾向您发送了不良信息，我们已配合相关部门核实并做了妥善处理，感谢您对营造绿色通信环境做出的努力！ 中国移动");
        if (!"06".equals(input.getString("RECV_IN_TYPE", "")))
        {
            IData data = new DataMap();
            data.put("RECV_OBJECT", serialNumber);
            data.put("NOTICE_CONTENT", smsInfo);
            data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            SmsSend.insSms(data);
        }

        createBadInfoByMsgIntf(input);// 1.创建不良基本信息或更新不良信息状态与完成时间
        createBadDealInfoByMsgIntf(input);// 2.创建不良信息处理通知过程信息
        result = updateArchForIboss(input);// shenh自动归档
        if ("0".equals(result.getString("X_RESULTCODE")))
        {
            IData param = new DataMap();
            param.put("STATE", PersonConst.STATE_ARCH);// 归档状态
            param.put("INFO_RECV_ID", input.getString("INDICT_SEQ", ""));// 请求服务标识
            param.put("FINISH_DATE", input.getString("PIGEONHOLE_TIME", ""));// 归档时间
            param.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 归档意见
            param.put("DEAL_DATE", input.getString("PIGEONHOLE_TIME", ""));// 处理时间
            Dao.executeUpdateByCodeCode("TF_F_BADNESS_INFO", "UPD_BADNESS_BY_PK", param, Route.CONN_CRM_CEN);

            IData inData = new DataMap();
            String deptId = input.getString("HANDLING_DEPT", "");
            if (StringUtils.isBlank(deptId))
            {
                deptId = input.getString("HandlingDept");
            }
            String staffId = input.getString("HANDLING_STAFF", "");
            if (StringUtils.isBlank(staffId))
            {
                staffId = input.getString("HandlingStaff");
            }

            // 2.从基本信息表中获取服务请求类别
            String indictSeq = input.getString("INDICT_SEQ", "");
            //IData rData = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", indictSeq);
            IDataset rData1 = BadnessInfoQry.queryAccessoryLists(indictSeq);
            IData rData=rData1.getData(0);

            // 3.判断不良信息处理表获取是否已存在
            IData dData = BadnessInfoQry.qryBadnessDealInfoByPK(indictSeq, PersonConst.STATE_ARCH);

            // 4.准备不良处理信息数据
            inData.put("INFO_RECV_ID", indictSeq);// 请求服务标识
            inData.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 处理意见补充说明
            inData.put("SERV_REQUEST_TYPE", rData == null ? "" : rData.getString("SERV_REQUEST_TYPE", "").trim());// 服务请求类别
            inData.put("DEAL_STAFF_ID", staffId);// 处理员工
            inData.put("DEAL_DEPART_ID", deptId);// 处理部门
            inData.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
            inData.put("STATE", PersonConst.STATE_ARCH);// 处理通知状态
            if (IDataUtil.isNotEmpty(dData))// 5.更新-不良处理信息数据
                Dao.save("TF_F_BADNESS_INO_DEAL", inData, Route.CONN_CRM_CEN);
            else
                // 6.新增-不良处理信息数据
                Dao.insert("TF_F_BADNESS_INO_DEAL", inData, Route.CONN_CRM_CEN);
        }
        return result;
    }

    private void updateBadInfoArch(IData data) throws Exception
    {
        IData inData = new DataMap();
        String sysTime = SysDateMgr.getSysTime();
        inData.put("STATE", PersonConst.STATE_ARCH);// 归档状态
        inData.put("INFO_RECV_ID", data.getString("INFO_RECV_ID", ""));// 请求服务标识
        inData.put("FINISH_DATE", sysTime);// 归档时间
        inData.put("FINISH_DATE", sysTime);
        inData.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));// 归档意见

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_F_BADNESS_INFO SET STATE=:STATE,");
        sql.append(" FINISH_DATE=TO_DATE(:FINISH_DATE,'YYYY-MM-DD HH24:MI:SS')");
        if (!"".equals(data.getString("DEAL_REMARK_MAKEUP", "")))
        {
            sql.append(" , DEAL_REMARK_MAKEUP=:DEAL_REMARK_MAKEUP");
        }
        sql.append(" WHERE INFO_RECV_ID=:INFO_RECV_ID");
        Dao.executeUpdate(sql, inData, Route.CONN_CRM_CEN);

        createBadDealInfoArch(data);
    }

    public IDataset updateBadInfoByArchIntf(IData input) throws Exception
    {
        IData result = new DataMap();
        // 从基本信息表中获取已发送短信状态
        String indictSeq = input.getString("INDICT_SEQ", "");
        IData badInfo = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", indictSeq);

        // 校验状态是否为已发送短信状态
        if (IDataUtil.isNotEmpty(badInfo) && PersonConst.STATE_FEEDBACK.equals(badInfo.getString("STATE")))
        {
            // 1.更新归档不良信息状态与完成时间
            IData param = new DataMap();
            param.put("STATE", PersonConst.STATE_ARCH);// 归档状态
            param.put("INFO_RECV_ID", input.getString("INDICT_SEQ", ""));// 请求服务标识
            param.put("FINISH_DATE", input.getString("PIGEONHOLE_TIME", ""));// 归档时间
            param.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 归档意见
            param.put("DEAL_DATE", input.getString("PIGEONHOLE_TIME", ""));// 处理时间
            Dao.executeUpdateByCodeCode("TF_F_BADNESS_INFO", "UPD_BADNESS_BY_PK", param, Route.CONN_CRM_CEN);

            // 2.创建不良信息处理归档过程信息
            param.clear();
            String[] staffArr = input.getString("HANDING_STAFF", "").split("-");
            String[] deptArr = input.getString("HANDING_DEPART", "").split("-");
            param.put("INFO_RECV_ID", indictSeq);// 请求服务标识
            param.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 归档意见
            param.put("SERV_REQUEST_TYPE", badInfo.getString("SERV_REQUEST_TYPE", "").trim());// 服务请求类别
            param.put("DEAL_STAFF_ID", staffArr[0]);// 归档员工
            param.put("DEAL_DEPART_ID", deptArr[0]);// 归档部门
            param.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
            param.put("STATE", PersonConst.STATE_ARCH);// 处理归档状态
            IData dealInfo = BadnessInfoQry.qryBadnessDealInfoByPK(indictSeq, PersonConst.STATE_ARCH);
            if (IDataUtil.isNotEmpty(dealInfo))
            {
                Dao.save("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);// 更新-不良处理信息数据
            }
            else
            {
                Dao.insert("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);// 新增-不良处理信息数据
            }
        }
        else
        {
            // CSAppException.apperr(DedInfoException.CRM_DedInfo_87);
            result.put("X_RESULTCODE", "-1");// 失败标志
            result.put("X_RESULTINFO", "当前状态不是已发送短信状态,失败");
        }

        return IDataUtil.idToIds(result);
    }

    private IDataset updateBadInfoByIntf(IData input) throws Exception
    {
        // 从基本信息表中获取已发送短信状态
        String indictSeq = input.getString("INDICT_SEQ", "");
        IData badInfo = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", indictSeq);

        // 校验状态是否为已发送短信状态
        if (IDataUtil.isNotEmpty(badInfo))
        {
            // 1.更新归档不良信息状态与完成时间
            IData param = new DataMap();
            param.put("STATE", PersonConst.STATE_ARCH);// 归档状态
            param.put("INFO_RECV_ID", input.getString("INDICT_SEQ", ""));// 请求服务标识
            param.put("FINISH_DATE", input.getString("PIGEONHOLE_TIME", ""));// 归档时间
            param.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 归档意见
            param.put("DEAL_DATE", input.getString("PIGEONHOLE_TIME", ""));// 处理时间
            Dao.executeUpdateByCodeCode("TF_F_BADNESS_INFO", "UPD_BADNESS_BY_PK", param, Route.CONN_CRM_CEN);

            // 2.创建不良信息处理归档过程信息
            param.clear();
            String[] staffArr = input.getString("HANDING_STAFF", "").split("-");
            String[] deptArr = input.getString("HANDING_DEPART", "").split("-");
            param.put("INFO_RECV_ID", indictSeq);// 请求服务标识
            param.put("DEAL_REMARK_MAKEUP", input.getString("HANDLING_COMMNET", ""));// 归档意见
            param.put("SERV_REQUEST_TYPE", badInfo.getString("SERV_REQUEST_TYPE", "").trim());// 服务请求类别
            param.put("DEAL_STAFF_ID", staffArr[0]);// 归档员工
            param.put("DEAL_DEPART_ID", deptArr[0]);// 归档部门
            param.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
            param.put("STATE", PersonConst.STATE_ARCH);// 处理归档状态
            IData dealInfo = BadnessInfoQry.qryBadnessDealInfoByPK(indictSeq, PersonConst.STATE_ARCH);
            if (IDataUtil.isNotEmpty(dealInfo))
            {
                Dao.save("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);// 更新-不良处理信息数据
            }
            else
            {
                Dao.insert("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);// 新增-不良处理信息数据
            }
        }
        else
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_87);
        }

        sendMms(input);
        return new DatasetList();
    }
}
