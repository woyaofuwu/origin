package com.asiainfo.veris.crm.order.soa.group.esop.esopstep;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTaskRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QryFlowNodeDescBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeHBean;

public class WorkformSendFinishSmsSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String TRANSER_SMS_TASK_ID = "710000000029";//过户B集团短信模板
    
    private static final String AUDIT_SMS_TASK_ID = "710000000035";//稽核短信模板
    /** 取专线计费时间
     * @param data
     * @return
     * @throws Exception */
    public IData getLineFeeTime(IData data) throws Exception {
        String ibsysId = data.getString("IBSYSID");
        String productNo = data.getString("PRODUCT_NO");

        IData result = new DataMap();
        result.put("IBSYSID", ibsysId);
        result.put("PRODUCT_NO", productNo);
        //获取专线资料表信息
        IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByProductNo(param);
        if(IDataUtil.isEmpty(dataLines)) {
            return result;
            //CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线实例号【" + productNo + "】未查询到专线资料信息！");
        }

        String userId = dataLines.first().getString("USER_ID");
        IDataset userDiscnts = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, EcConstants.ZERO_DISCNT_CODE);
        String endTime = "";
        if(IDataUtil.isEmpty(userDiscnts)) {//立即计费
            userDiscnts = UserDiscntInfoQry.getAllDiscntInfo(userId);
            if(IDataUtil.isEmpty(userDiscnts)) {
                return result;
                //CSAppException.apperr(GrpException.CRM_GRP_713, "根据USER_ID=【" + userId + "】未查询到专线专线资费信息！");
            }
            endTime = userDiscnts.first().getString("START_DATE");
        } else {//下账期或者下下账期计费
            String endDate = userDiscnts.first().getString("END_DATE");
            if(StringUtils.isNotBlank(endDate)){
                endTime = SysDateMgr.getFirstDayOfNextMonth(endDate);
            }
        }
        if(StringUtils.isNotBlank(endTime)) {
            endTime = endTime.substring(0, 10);
        }

        result.put("USER_ID", userId);
        result.put("START_TIME", endTime);
        return result;
    }
    
    /** 更改state表状态
     * @param data
     * @return
     * @throws Exception */
    public IDataset changeCheckinState(IData data) throws Exception {
        String busiformId = data.getString("BUSIFORM_ID");
        String ibsysid = data.getString("IBSYSID");
        //查流程订单信息
        IDataset eweRaleDatas = EweNodeQry.qryBySubBusiformId(busiformId);
        if(DataUtils.isEmpty(eweRaleDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据子流程ID：" + busiformId + "未查询到流程关系表数据！");
        }
        IData eweRaleData = eweRaleDatas.first();
        String recordNum = eweRaleData.getString("RELE_VALUE", "");
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("BUSIFORM_ID", busiformId);
        param.put("RECORD_NUM", recordNum);
        return CSAppCall.call("SS.WorkformCheckinSVC.record", param);

    }

    public IData SendAuditSms(IData data) throws Exception {
       	IData smsdata = new DataMap();
       	String staffsn =  data.getString("STAFF_SN");
       	String smsContent  = data.getString("SMS_CONTENT");
        smsdata.put("EPARCHY_CODE", "0898");
        smsdata.put("USER_EPARCHY_CODE", "0898");
        smsdata.put("RECV_OBJECT", staffsn);// 工号手机号码
        smsdata.put("NOTICE_CONTENT", smsContent);
        smsdata.put("RECV_ID", "-1");
        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
        smsdata.put("FORCE_START_TIME", "");
        smsdata.put("FORCE_END_TIME", "");
        smsdata.put("REMARK", "");
        setUserEparchyCode("0898");
        SmsSend.insSms(smsdata);
   		return null;
       	
   }
    
    public void sendTranserSmsGroupB(IData data)throws Exception{
        String bpmTempletId= data.getString("BPM_TEMPLET_ID", "");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String ibsysid = data.getString("IBSYSID", "");
        
        IDataset timerInfo = EweNodeTaskRelaInfoQry.qryByTimerId(TRANSER_SMS_TASK_ID, "1", "0");
        
        IData smsdata = new DataMap();
        if(IDataUtil.isNotEmpty(timerInfo)){
            String smsContent = timerInfo.first().getString("WARN_CONTENT");
            IData input = new DataMap();
            input.put("BPM_TEMPLET_ID", bpmTempletId);
            input.put("NODE_TYPE", "3");
            IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
            if(IDataUtil.isNotEmpty(nodeTempleteList)){
                String beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
                IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,"0","GROUP_ID");
                if(IDataUtil.isNotEmpty(attrDatas)){
                    String groupId = attrDatas.first().getString("ATTR_VALUE");
                    IData groupInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                    if(IDataUtil.isNotEmpty(groupInfo)){
                        IDataset staffInfo = StaffInfoQry.getStaffAreaInfoByID(groupInfo.getString("CUST_MANAGER_ID",""));
                        if(IDataUtil.isNotEmpty(staffInfo)){
                            String sn = staffInfo.first().getString("SERIAL_NUMBER");
                            smsdata.put("STAFF_SN", sn);
                        }
                    }
                }
            }
            IData param = new DataMap();
            param.put("IBSYSID", ibsysid);
            param.put("BUSIFORM_ID", busiformId);
            String warnSvc = timerInfo.first().getString("WARN_SVC","");
            if(StringUtils.isEmpty(warnSvc))
            {
                warnSvc = "SS.WorkformInfoSVC.queryComInfo";
            }
            IDataset resultset = CSAppCall.call(warnSvc, param);
            if(DataUtils.isNotEmpty(resultset))
            {
                IData result = resultset.first();
                CompiledTemplate compiled = TemplateCompiler.compileTemplate(smsContent);
                String flagStr = TemplateRuntime.execute(compiled, result).toString();
                smsdata.put("SMS_CONTENT", flagStr);
            }
            if(StringUtils.isNotBlank(smsdata.getString("STAFF_SN"))&&StringUtils.isNotBlank(smsdata.getString("SMS_CONTENT"))){
                SendAuditSms(smsdata);
            }
            
        }
        
    }
    
    public void sendFinshSmsForAdudit(IData data)throws Exception{
        String ibsysid = data.getString("IBSYSID");
        String auditResult = data.getString("AUDIT_RESULT");
        IDataset otherInfos = data.getDataset("OTHER_INFO");
        
        IData resultInfo = new DataMap();
        
        IDataset timerInfo = EweNodeTaskRelaInfoQry.qryByTimerId(AUDIT_SMS_TASK_ID, "1", "0");
        
        IDataset subscribeInfos = WorkformSubscribeHBean.qryScribeHInfoByIbsysid(ibsysid);
        boolean isBh = true;
        if(DataUtils.isEmpty(subscribeInfos)){
            subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
            isBh = false;
        }
        String bpmTempletId = "";
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        if("false".equals(auditResult)){
            resultInfo.put("AUDIT_RESULT", "不通过");
        }else{
            resultInfo.put("AUDIT_RESULT", "通过");
        }
        
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", bpmTempletId);
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(DataUtils.isNotEmpty(nodeTempleteList)){
            String beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
            String productNos = "";
            
            if(DataUtils.isNotEmpty(otherInfos)){
                for(int i =0;i<otherInfos.size();i++){
                    IData other = otherInfos.getData(i);
                    String recodeNum = other.getString("RECORD_NUM"); 
                    IDataset attrDatas = null;
                    IData attrParam = new DataMap();
                    attrParam.put("IBSYSID", ibsysid);
                    attrParam.put("NODE_ID", beginNodeId);
                    attrParam.put("ATTR_CODE", "PRODUCTNO");
                    attrParam.put("RECORD_NUM", recodeNum);
                    if(isBh){
                        attrDatas = WorkformAttrHBean.qryMaxHisAttrByAttrCodeRecodeNum(attrParam);
                    }else{
                        attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"PRODUCTNO");
                    }
                    
                    if(DataUtils.isNotEmpty(attrDatas)){
                        productNos += attrDatas.first().getString("ATTR_VALUE")+",";
                    }
                }
            }
            if(StringUtils.isNotBlank(productNos)){
                resultInfo.put("PRODUCT_NO", productNos.substring(0, productNos.length()-1));
            }
        }
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("BPM_NAME", resultInfo.getString("BPM_NAME",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("AUDIT_RESULT", resultInfo.getString("AUDIT_RESULT",""));
        
        if(DataUtils.isEmpty(timerInfo)){
            return;
        }
        IData smsdata = new DataMap();
        
        String smsContent = timerInfo.first().getString("WARN_CONTENT");
        CompiledTemplate compiled = TemplateCompiler.compileTemplate(smsContent);
        String flagStr = TemplateRuntime.execute(compiled, result).toString();
        
        //取发单人
        IDataset eweDatas = EweNodeQry.qryEweByIbsysid(ibsysid,"0");
        if(DataUtils.isEmpty(eweDatas)){
            eweDatas = EweNodeQry.qryEweHByIbsysid(ibsysid,"0");
        }
        String staff = eweDatas.first().getString("ACCEPT_STAFF_ID");
        IDataset staffInfo = StaffInfoQry.getStaffAreaInfoByID(staff);
        if(IDataUtil.isNotEmpty(staffInfo)){
            String sn = staffInfo.first().getString("SERIAL_NUMBER");
            smsdata.put("STAFF_SN", sn);
        }
        
        
        if(StringUtils.isNotBlank(smsdata.getString("STAFF_SN"))&&StringUtils.isNotBlank(flagStr)){
            smsdata.put("SMS_CONTENT", flagStr);
            SendAuditSms(smsdata);
        }
        
    }
}
