
package com.asiainfo.veris.crm.order.soa.person.busi.badness.sundryquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.ReporterBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.badness.BadInfoComplainDealBean;

public class BadnessQueryBean extends CSBizBean
{

	private static final Log logger = LogFactory.getLog(BadnessQueryBean.class);

	
    private static final String DEAL_STATE_ARCH = "0A";

    // 需要加黑到HLR的处理类型
    private static final String NEED_ADDED_HLR = "'01','0101','0102','02','0201','03','0305'";

    // HLR加黑类型： 举报
    private static final String HANDLING_TYPE_REPORT = "0103";

    // 数据来源 ：前台提交
    private static final String SOURCE_DATA_PLATFORM = "01";

    // 渠道来源 ：短信
    private static final String IN_MODE_CODE_SMS = "2";

    private void blackReg(IData data) throws Exception
    {
        IDataset ret = CommparaInfoQry.qryCommParaByLike("CSM", "1720", ",%" + data.getString("DEAL_RAMARK", "") + "%,");
        if (IDataUtil.isEmpty(ret))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_932, data.getString("DEAL_RAMARK", ""), "898");
        }
        String need_tradeType = ret.getData(0).getString("PARA_CODE1", "");
        String add_handing_state = ret.getData(0).getString("PARA_CODE3", "");
        if (StringUtils.isBlank(need_tradeType))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_933);
        }
        String badness_type = data.getString("DEAL_ASSORT", ""); // 前台的 举报对象类型
        if ("8888".equals(need_tradeType))
        {
            return;
        }
        if ("".equals(badness_type))
        {
            badness_type = data.getString("SHORT_SERV_REQ_TYPE", "");
        }

        IData tmp = new DataMap();
        tmp.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
        tmp.put("TRADE_CITY_CODE", getVisit().getCityCode());
        tmp.put("TRADE_STAFF_ID", getVisit().getStaffId());
        tmp.put("TRADE_DEPART_ID", getVisit().getDepartId());
        tmp.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        tmp.put("INDICT_SEQ", data.getString("INDICT_SEQ", ""));
        tmp.put("PROVINCE", "898");
        tmp.put("SUB_NUMBER", data.getString("SERIAL_NUMBER", ""));
        tmp.put("HANDLING_TIME", SysDateMgr.getSysTime()); // 人工触发处理时间为空
        tmp.put("UPDATE_TIME", tmp.getString("HANDLING_TIME", "")); // 最后处理时间
        /*
         * 0101 仅关闭短信功能 0102 关闭主叫号码所有功能（即停机） 0103 开启短信功能 0104 开启主叫号码所有功能（即开机） 0201 仅关闭语音主叫功能 0202 关闭主叫号码所有功能（即停机） 0203
         * 开启语音主叫功能 0204 开启主叫号码所有功能（即开机）
         */// 次处只是记录一下规范中的编码，解黑是也是一样，因为实际操作，只能以业务类型的服务变更配置为准
        tmp.put("HANDLING_STATE", add_handing_state); // 次处是处理意见，经配置后与规范定义一致 ,,'设计文档中的处理方式'
        tmp.put("ORI_HANDING_SUGGEST", data.getString("DEAL_RAMARK", ""));// 原始处理意见
        tmp.put("REMOVE_HANDLING_STATE", "");// 与接口来的数据相同处理 ,统一置空remove_handing_state ) ; //解黑处置方式
        tmp.put("HANDLING_TYPE", "0103");// today 这里可能不对. "1");//"HLR加解黑类型为空"); 设计文档中,接口定义为:0103 投诉
        tmp.put("SOURCE_DATA", "03");// "数据来源,'01'表示前台举报受理 01：垃圾短信平台 02：骚扰电话平台 先写'03',表示用户投诉
        tmp.put("SERVICE_CONTENT", data.getString("DEAL_REMARK_MAKEUP", ""));// "HLR加解黑原因描述为空");
        tmp.put("NEED_TRADETYPE", need_tradeType);
        tmp.put("ADD_RESULT", convertBalckReason(data.getString("BADINFO_SERVREQUESTTYPE", "")));
        tmp.put("IN_MODE_CODE", "1");// "接入渠道为空");
        tmp.put("BLACK_STATE", "01"); // 前台插入的初始状态
        tmp.put("DATA_CHNL", "01"); // 表示举报处理写入
        tmp.put("BADNESS_CLASS", badness_type); // 不良信息类别 规范中有要求,即举报处理中的服务请求类别,举报受理中的举报对象类型
        tmp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        tmp.put("TRADE_TYPE_CODE", need_tradeType);
        CSAppCall.call("SS.BadnessManageInterSVC.createHLRStopOpenRegIner", tmp);
    }

    private String convertBalckReason(String str) throws Exception
    {
        IDataset ret = CommparaInfoQry.getCommparaAllCol("CSM", "1721", str, CSBizBean.getTradeEparchyCode());
        if (DataUtils.isEmpty(ret))
            return "NODEFINE";
        return ret.getData(0).getString("PARA_CODE1", "");
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

    private IDataset qryBadnessInfoLogs(IData data, Pagination page) throws Exception
    {
        String infoRecvId = data.getString("INFO_RECV_ID", "");
        String state = data.getString("STATE", "");
        String recvProv = data.getString("RECV_PROVINCE", "");
        String badnessProv = data.getString("BADNESS_INFO_PROVINCE", "");
        String reportProv = data.getString("REPORT_CUST_PROVINCE", "");
        String badnessInfo = data.getString("BADNESS_INFO", "");
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String requestType = data.getString("SERV_REQUEST_TYPE", "");
        String startTime = data.getString("REPORT_START_TIME", "");
        String endTime = data.getString("REPORT_END_TIME", "");
        String reportTypeCode = data.getString("REPORT_TYPE_CODE", "");
        IDataset result = BadnessInfoQry.qryBadnessInfoLogs(infoRecvId, state, recvProv, badnessProv, reportProv, badnessInfo, serialNumber, requestType, startTime, endTime, reportTypeCode, page);

        return result;
    }

    private IDataset qryBadnessInfos(IData data, Pagination page) throws Exception
    {
        String infoRecvId = data.getString("INFO_RECV_ID", "");
        String state = data.getString("STATE", "");
        String recvProv = data.getString("RECV_PROVINCE", "");
        String badnessProv = data.getString("BADNESS_INFO_PROVINCE", "");
        String reportProv = data.getString("REPORT_CUST_PROVINCE", "");
        String badnessInfo = data.getString("BADNESS_INFO", "");
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String requestType = data.getString("SERV_REQUEST_TYPE", "");
        String startTime = data.getString("REPORT_START_TIME", "");
        String endTime = data.getString("REPORT_END_TIME", "");
        String reportTypeCode = data.getString("REPORT_TYPE_CODE", "");
        String badnessInfoNet = data.getString("BADNESS_INFO_NET", "");
        IDataset result = BadnessInfoQry.qryBadnessInfos(infoRecvId, state, recvProv, badnessProv, reportProv, badnessInfo, serialNumber, requestType, startTime, endTime, reportTypeCode,badnessInfoNet, page);

        return result;
    }

    public IDataset queryBadHastenInfo(IData data, Pagination page) throws Exception
    {
        String infoRecvId = data.getString("INFO_RECV_ID", "");
        String state = data.getString("STATE", "");
        String recvProv = data.getString("RECV_PROVINCE", "");
        String badnessProv = data.getString("BADNESS_INFO_PROVINCE", "");
        String reportProv = data.getString("REPORT_CUST_PROVINCE", "");
        String badnessInfo = data.getString("BADNESS_INFO", "");
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String requestType = data.getString("SERV_REQUEST_TYPE", "");
        String startTime = data.getString("REPORT_START_TIME", "");
        String endTime = data.getString("REPORT_END_TIME", "");
        String reportTypeCode = data.getString("REPORT_TYPE_CODE", "");
        IDataset result = BadnessInfoQry.queryBadHastenInfo(infoRecvId, state, recvProv, badnessProv, reportProv, badnessInfo, serialNumber, requestType, startTime, endTime, reportTypeCode, page);
        return result;
    }

    public IDataset queryBadnessInfoImpeach(IData data, Pagination page) throws Exception
    {

        String infoRecvId = data.getString("INFO_RECV_ID", "");
        String state = data.getString("STATE", "");
        String recvProv = data.getString("RECV_PROVINCE", "");
        String badnessProv = data.getString("BADNESS_INFO_PROVINCE", "");
        String reportProv = data.getString("REPORT_CUST_PROVINCE", "");
        String badnessInfo = data.getString("BADNESS_INFO", "");
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String requestType = data.getString("SERV_REQUEST_TYPE", "");
        String startTime = data.getString("REPORT_START_TIME", "");
        String endTime = data.getString("REPORT_END_TIME", "");
        String reportTypeCode = data.getString("REPORT_TYPE_CODE", "");
        String badnessInfoNet = data.getString("BADNESS_INFO_NET", "");
        IDataset result = BadnessInfoQry.qryBadImpeachInfo(infoRecvId, state, recvProv, badnessProv, reportProv, badnessInfo, serialNumber, requestType, startTime, endTime, reportTypeCode,badnessInfoNet, page);
        if (IDataUtil.isEmpty(result))
        {
            return result;
        }
        else
        {
            // IDataset urlset = CommparaInfoQry.getInfoParaCode3("CSM", "6666", "url");
            // if (IDataUtil.isEmpty(urlset))
            // {
            // CSAppException.apperr(DedInfoException.CRM_DedInfo_16);
            // }
            // else
            // {
            StringBuilder sn = new StringBuilder(1000);
            for (int i = 0; i < result.size(); i++)
            {
                IData temp = result.getData(i);
                String fileId = temp.getString("RSRV_STR1", "");
                temp.put("FILE_ID", fileId);
                if (StringUtils.isBlank(fileId))
                {
                    temp.put("TAG", "无附件");
                }
                else
                {
                    temp.put("TAG", "点击下载");
                }
                if (i == 0)
                {
                    sn.append("'").append(temp.getString("REPORT_SERIAL_NUMBER"));// sn = "'" +
                    // result.getData(0).getString("REPORT_SERIAL_NUMBER");
                }
                else
                {
                    sn.append("','").append(temp.getString("REPORT_SERIAL_NUMBER"));// sn = sn +
                    // "','"+result.getData(i).getString("REPORT_SERIAL_NUMBER",
                    // "");
                }
            }
            sn.append("'");

            IDataset dataset = BadnessInfoQry.qryReportBlack(sn.toString());
            for (int i = 0; i < result.size(); i++)
            {
                IData temp = result.getData(i);
                if (IDataUtil.isEmpty(dataset))
                {
                    temp.put("IS_BLACK", "不存在");
                }
                else
                {
                    for (int j = 0; j < dataset.size(); j++)
                    {
                        if (temp.getString("REPORT_SERIAL_NUMBER", "").equals(dataset.getData(j).getString("SERIAL_NUMBER", "")))
                        {
                            if (dataset.getData(j).getString("IS_BLACK").equals("1"))
                            {
                                temp.put("IS_BLACK", "黑名单");
                            }
                            else if (dataset.getData(j).getString("IS_BLACK").equals("0"))
                            {
                                temp.put("IS_BLACK", "白名单");
                            }
                            continue;
                        }
                    }
                }
                // }
            }
        }
        return result;
    }

    public IDataset queryBadnessReleaseInfo(IData data, Pagination page) throws Exception
    {
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER");
        String state = "01";
        String source = data.getString("RECV_IN_TYPE");
        String endTime = data.getString("REPORT_END_TIME");
        String startTime = data.getString("REPORT_START_TIME");

        return BadnessInfoQry.qryGroupBadInfo(serialNumber, state, source, endTime, startTime, page);
    }

    public IDataset queryBaseBadnessInfo(IData data, Pagination page) throws Exception
    {
        IDataset result = qryBadnessInfos(data, page);
        return result;
    }

    public IDataset queryOtherBadnessInfo(IData data) throws Exception
    {
        IData alertInfo = new DataMap();
        String badinfo = data.getString("BADNESS_TABLE");
        IDataset ids = new DatasetList(badinfo);
        if (IDataUtil.isEmpty(ids))
        {
            // CSAppException.apperr(DedInfoException.CRM_DedInfo_13);
            alertInfo.put("ALERT_INFO", "请选择不良信息后，再继续办理业务！");
        }

        for (int i = 0; i < ids.size(); i++)
        {
            IData badnessInfos = ids.getData(i);
            String revcId = badnessInfos.getString("INFO_RECV_ID");
            IDataset result = BadnessInfoQry.qryBadnessInfos(revcId, null, null, null, null, null, null, null, null, null, null, null, null);
            String recv_province = result.getData(0).getString("RECV_PROVINCE");
            String badness_info_province = result.getData(0).getString("BADNESS_INFO_PROVINCE");
            String report_cust_province = result.getData(0).getString("REPORT_CUST_PROVINCE");

            if (!"898".equals(badness_info_province))
            {
                IData params = new DataMap();
                params.put("DEAL_RAMARK", "");
                params.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));

                String dealdeptId = getVisit().getDepartId() + "--" + getVisit().getDepartName();
                String dealStaffId = getVisit().getStaffId() + "--" + getVisit().getStaffName();
                params.put("DEAL_STAFF_ID", dealStaffId);
                params.put("DEAL_DEPART_ID", dealdeptId);
                params.put("DEAL_DATE", SysDateMgr.getSysDate());
                params.put("INDICT_SEQ", revcId);
                IData ibossresult = null;
                try
                {
                    ibossresult = IBossCall.dealQBadInfoIboss(params);
                    
                	
                	
                }
                catch (Exception e)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "不良信息异地举报查询失败：" + e.getMessage() + "[" + e.getCause().getMessage() + "]");
                }
                // IData ibossresult = new DataMap();

                if (!"0000".equalsIgnoreCase(ibossresult.getString("X_RSPCODE")))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "不良信息异地举报查询失败’：" + ibossresult.getString("X_RSPCODE") + ":" + ibossresult.getString("X_RESULTINFO"));
                }
                else
                {
                    IData temp = new DataMap();
                    temp.put("INFO_RECV_ID", ibossresult.getString("INDICT_SEQ", ""));// 举报全网唯一编码
                    temp.put("STATE", ibossresult.getString("CURRENT_NODE", ""));// 举报当前处理状态
                    temp.put("HANDING_DEPART", ibossresult.getString("HANDING_DEPART", ""));// 当前处理部门
                    temp.put("HANDING_STAFF", ibossresult.getString("HANDING_STAFF", ""));// 举报处理人
                    temp.put("STAFF_CONTACT_PHONE", ibossresult.getString("STAFF_CONTACT_PHONE", ""));// 举报处理人联系方式
                    String isiterange = "不重复";
                    String isvalid = "有效";
                    if (ibossresult.getString("IS_ITERANCE", "").equalsIgnoreCase("0"))
                    {
                        isiterange = "重复";
                    }
                    if (ibossresult.getString("IS_VALID", "").equalsIgnoreCase("0"))
                    {
                        isvalid = "无效";
                    }
                    temp.put("IS_ITERANCE", isiterange);// 0代表重复，1代表不重复，可以为空
                    temp.put("IS_VALID", isvalid);// 是否有效举报 0代表无效，1代表有效，可以为空

                    return IDataUtil.idToIds(temp);
                }
            }
            else
            {
                // CSAppException.apperr(DedInfoException.CRM_DedInfo_76);
                alertInfo.put("ALERT_INFO", "此业务不能进行举报查询，被举报号码为非本省号码时才可以进行异地举报查询！");
            }
        }
        return IDataUtil.idToIds(alertInfo);
    }

    public IDataset queryOtherProvBadnessInfo(IData data, Pagination page) throws Exception
    {
        String isOtherProv = data.getString("IS_OTHERPROVINCE");
        IDataset result = null;
        if ("0".equals(isOtherProv))
        {
            result = qryBadnessInfos(data, page);
        }
        else
        {
            result = qryBadnessInfoLogs(data, page);
        }
        return result;
    }

    public IDataset queryReporterBlack(IData data) throws Exception
    {
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER");
        String isBlack = data.getString("IS_BLACK");
        String startDate = data.getString("REPORT_START_TIME");
        String endDate = data.getString("REPORT_END_TIME");
        return ReporterBlackInfoQry.qryReportBlackBySn(serialNumber, isBlack, startDate, endDate);
    }

    private void sendDeal(IData data) throws Exception
    {
        StringBuilder smsInfo = new StringBuilder(2000);
        if (data.getString("RECV_TIME", "").length() < 11)
        {// 判断举报日期格式长度不对
            CSAppException.apperr(DedInfoException.CRM_DedInfo_48);
        }
        String serialNumber = data.getString("REPORT_SERIAL_NUMBER", "");
        String badNess_info = data.getString("BADNESS_INFO", "");
        String smscontent="";
        String reportTypeCode= data.getString("REPORT_TYPE_CODE", "");
        if(reportTypeCode.equals("04")){//举报网站
        	String recvInType=data.getString("RECV_IN_TYPE", "");
        	String dealRamark=data.getString("DEAL_RAMARK", "");
        	
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
        	String mon=data.getString("RECV_TIME", "").substring(5, 7);
            String day=data.getString("RECV_TIME", "").substring(8, 10);
            
            smscontent=StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
    				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE","PARA_CODE1" }, "PARA_CODE23", 
    				 new String[]{ "CSM", "8899", "SMSTSCL" ,"0"});
            if(!StringUtils.isBlank(smscontent)){
            	smscontent=smscontent.replace("%101!", mon).replace("%102!", day).replace("%103!", badNess_info);
            }
        }
        
        smsInfo =smsInfo.append(smscontent);
//        smsInfo.append("尊敬的客户，您");
//        smsInfo.append(data.getString("RECV_TIME", "").substring(5, 7) + "月");
//        smsInfo.append(data.getString("RECV_TIME", "").substring(8, 10) + "日举报");
//        smsInfo.append(data.getString("BADNESS_INFO", ""));
//        smsInfo.append("的不良信息，我们已配合相关部门核实并做了妥善处理，感谢您对营造绿色通信环境做出的努力！中国移动");

        // 发送短信
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", data.getString("USER_ID", "0"));
        sendInfo.put("SMS_PRIORITY", data.getString("PRIORITY", "50"));
        sendInfo.put("NOTICE_CONTENT", smsInfo.toString());
        sendInfo.put("REMARK", "不良信息短信处理");

        SmsSend.insSms(sendInfo);
    }

    public IDataset submitBadnessInfos(IData data) throws Exception
    {
        IData alertInfo = new DataMap();
        String badinfo = data.getString("BADNESS_TABLE");
        String badnessInfoNet = data.getString("BADNESS_INFO_NET", "");
        IDataset ids = new DatasetList(badinfo);
        if (IDataUtil.isEmpty(ids))
        {
            alertInfo.put("ALERT_INFO", "请选择不良信息后，再继续办理业务！");
            alertInfo.put("ALERT_CODE", "-1");
        }

        if (StringUtils.isBlank(data.getString("DEAL_RAMARK")))
        {
            alertInfo.put("ALERT_INFO", "请选择处理意见编码！");
            alertInfo.put("ALERT_CODE", "-1");
        }

        if (StringUtils.isBlank(data.getString("SVC_TYPE_ID")))
        {
            alertInfo.put("ALERT_INFO", "请选择服务请求类别！");
            alertInfo.put("ALERT_CODE", "-1");
        }

        int sucessCount = 0;
        String failID = "";
        for (int i = 0; i < ids.size(); i++)
        {
            IData temp = ids.getData(i);
            String recvId = temp.getString("INFO_RECV_ID");
            IDataset badInfos = BadnessInfoQry.qryBadImpeachInfo(temp.getString("INFO_RECV_ID"), "01", null, "898", "898", null, null, null, null, null, null, badnessInfoNet,null);
            IData badInfo = new DataMap();
            if (IDataUtil.isNotEmpty(badInfos))
            {
                badInfo = badInfos.getData(0);
            }

            if ("898".equals(badInfo.getString("REPORT_CUST_PROVINCE")) && "898".equals(badInfo.getString("BADNESS_INFO_PROVINCE")) && "01".equals(badInfo.getString("STATE")))
            {
                IDataset snSet = BadnessInfoQry.getBadnessSNbyCEN(recvId);
                if (IDataUtil.isNotEmpty(snSet))
                {
                    IData epary = RouteInfoQry.getMofficeInfoBySn(snSet.getData(0).getString("SERIAL_NUMBER"));
                    if (IDataUtil.isNotEmpty(epary))
                    {
                        IData userInfo = UcaInfoQry.qryUserInfoBySn(snSet.getData(0).getString("SERIAL_NUMBER"), epary.getString("EPARCHY_CODE"));
                        if (IDataUtil.isNotEmpty(userInfo))
                        {
                            String buf_serv_req_type = badInfo.getString("SERV_REQUEST_TYPE", "").trim();
                            String short_serv_req_type="";
                            if(!"".equals(buf_serv_req_type)){
                            	if(buf_serv_req_type.length() == 8){
                            		short_serv_req_type=buf_serv_req_type.substring(buf_serv_req_type.length()-2, buf_serv_req_type.length());
                            	}else if(buf_serv_req_type.length() == 10){
                            		short_serv_req_type=buf_serv_req_type.substring(buf_serv_req_type.length()-4, buf_serv_req_type.length()-2);
                            	}else if(buf_serv_req_type.length() == 12){
                            		short_serv_req_type=buf_serv_req_type.substring(buf_serv_req_type.length()-6, buf_serv_req_type.length()-4);
                            	}else if(buf_serv_req_type.length() == 14){
                            		short_serv_req_type=buf_serv_req_type.substring(buf_serv_req_type.length()-8, buf_serv_req_type.length()-6);
                            	}
                            }
                           
                            
                            if(!StringUtils.isEmpty(badInfo.getString("FOURTH_TYPE_CODE", "").trim())) {
                            	short_serv_req_type = badInfo.getString("FOURTH_TYPE_CODE", "").trim();
                            }

                            IData blackparam = new DataMap();
                            blackparam.put("SERIAL_NUMBER", snSet.getData(0).getString("SERIAL_NUMBER"));
                            blackparam.put("INDICT_SEQ", recvId);
                            blackparam.put("DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));
                            blackparam.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                            blackparam.put("BADINFO_SERVREQUESTTYPE", data.getString("SVC_TYPE_ID"));
                            blackparam.put("SHORT_SERV_REQ_TYPE", short_serv_req_type);
                            blackparam.put("DEAL_ASSORT", data.getString("DEAL_ASSORT", ""));
                            blackReg(blackparam);
                        }
                    }
                }

                IData tmp = new DataMap();
                tmp.put("INFO_RECV_ID", recvId);
                tmp.put("DEAL_RAMARK", data.getString("DEAL_RAMARK"));
                tmp.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                tmp.put("SERV_REQUEST_TYPE", "110103" + dealRequestTypeCode(data));
                tmp.put("DEAL_STAFF_ID", getVisit().getStaffId());
                tmp.put("DEAL_DEPART_ID", getVisit().getDepartId());
                tmp.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE"));
                tmp.put("DEAL_DATE", SysDateMgr.getSysTime());
                tmp.put("STATE", DEAL_STATE_ARCH);  
                tmp.put("PRETREATMENT", data.getString("PRETREATMENT"));//2014-10-22加入预处理信息

                IData dealInfo = BadnessInfoQry.qryBadnessDealInfoByPK(recvId, DEAL_STATE_ARCH);
                if (IDataUtil.isEmpty(dealInfo))
                {
                    Dao.insert("TF_F_BADNESS_INO_DEAL", tmp, Route.CONN_CRM_CEN);
                }
                else
                {
                    Dao.save("TF_F_BADNESS_INO_DEAL", tmp, new String[]
                    { "INFO_RECV_ID", "STATE" }, Route.CONN_CRM_CEN);
                }

                if (!"06".equals(badInfo.getString("RECV_IN_TYPE", "")))
                {
                	badInfo.put("DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));
                    sendDeal(badInfo);
                }

                tmp.clear();
//                tmp.put("OPERATE_STEP", data.getString("OPERATE_STEP"));//2014-10-22加入预处理信息
                tmp.put("INFO_RECV_ID", recvId);
                tmp.put("STATE", DEAL_STATE_ARCH);
                tmp.put("DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));
                tmp.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
                if (!"".equals(data.getString("DEAL_ASSORT", "")))
                {
                    tmp.put("REPORT_TYPE_CODE", data.getString("DEAL_ASSORT", ""));
                }

                tmp.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE"));
                tmp.put("DEAL_DATE", SysDateMgr.getSysTime());
                tmp.put("FINISH_DATE", SysDateMgr.getSysTime());// 归档时间
                String servReqType = badInfo.getString("SERV_REQUEST_TYPE1").trim();
                if (servReqType.length() >= 6) {
                	servReqType = servReqType.substring(0, 6) + dealRequestTypeCode(data);
                } else {
                	servReqType = "110103" + dealRequestTypeCode(data);
                }
                tmp.put("SERV_REQUEST_TYPE", servReqType);//
                tmp.put("SORT_RESULT_TYPE", badInfo.getString("SORT_RESULT_TYPE").trim());
                Dao.save("TF_F_BADNESS_INFO", tmp, new String[]
                { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

                sucessCount++;
            }
            else
            {
                failID = failID + recvId + " ";
            }

            // 点对点短信屏蔽功能，将数据投诉与被投诉号码同步到网监平台
//            String servType = badInfo.getString("ALL_SERV_REQUEST_TYPE", "").trim();
//            if(servType!=null && servType.length()>12){
//                servType = servType.trim().substring(0, 12);
//            }
//            if (servType.equals(ManageForbiddenPointBean.OTHER_PROVINCE_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.OTHER_SYS_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.CURRENT_PROVINCE_SERV_TYPE))
//            {
//                //2014-09-26新增，只对0109-对举报号码屏蔽 才插点对点表
//                if(StringUtils.equals("0109", data.getString("DEAL_RAMARK", ""))) {
//                    CSAppCall.call("SS.ManageForbiddenPointSVC.addForbiddenData", badInfo);
//                }
//            }
//
//            // hlr加黑并同步到一级boss
//            String dealType = data.getString("DEAL_RAMARK", "").trim();
//            if (NEED_ADDED_HLR.indexOf("'" + dealType + "'") > -1)
//            {
//                IData param = new DataMap();
//                param.putAll(this.convertToHLRParamMap(data));
//                CSAppCall.call("SS.BadnessManageInterSVC.createHLRStopOpenReg", param);
//            }
        }

        int failCount = ids.size() - sucessCount;
        if (failCount > 0)
        {
            failID = "处理失败流水为：" + failID;
        }
        alertInfo.put("ALERT_INFO", "信息处理结果，成功：" + sucessCount + " 条；失败：" + failCount + " 条。" + failID);
        alertInfo.put("ALERT_CODE", "0");
        return IDataUtil.idToIds(alertInfo);
    }
    
    public String dealRequestTypeCode(IData data) {
    	String requestTypeCode = data.getString("FOURTH_TYPE_CODE", "");
    	
    	if (!StringUtils.isEmpty(data.getString("FIFTH_TYPE_CODE", ""))) {
    		requestTypeCode = data.getString("FIFTH_TYPE_CODE", "");
    	}
    	
    	if (!StringUtils.isEmpty(data.getString("SERV_REQUEST_TYPE", ""))) {
    		requestTypeCode = data.getString("SERV_REQUEST_TYPE", "");
    	} else if (!StringUtils.isEmpty(data.getString("SVC_TYPE_ID", ""))) {
    		requestTypeCode = data.getString("SVC_TYPE_ID", "");
    	}
    	
    	return requestTypeCode;
    }

    public IDataset tempDeal(IData data) throws Exception
    {
        IData alertInfo = new DataMap();
        String badinfo = data.getString("BADNESS_TABLE", "[]");
        IDataset ids = new DatasetList(badinfo);
        if (IDataUtil.isEmpty(ids))
        {
            alertInfo.put("ALERT_INFO", "请选择不良信息后，再继续办理业务！");
        }

        if (StringUtils.isBlank(data.getString("DEAL_RAMARK")))
        {
            alertInfo.put("ALERT_INFO", "请选择处理意见编码！");
        }

        int sucessCount = 0;
        for (int i = 0; i < ids.size(); i++)
        {
            IData param = new DataMap();
            param.put("DEAL_STAFF_ID", getVisit().getStaffId());
            param.put("INFO_RECV_ID", ids.getData(i).getString("INFO_RECV_ID"));
            param.put("TEMP_DEAL", data.getString("DEAL_REMARK_MAKEUP"));
            Dao.save("TF_F_BADNESS_INFO", param, new String[]
            { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);
            sucessCount++;
        }
        alertInfo.put("ALERT_INFO", "登记临时处理信息，成功：" + sucessCount + " 条");
        return IDataUtil.idToIds(alertInfo);
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
}
