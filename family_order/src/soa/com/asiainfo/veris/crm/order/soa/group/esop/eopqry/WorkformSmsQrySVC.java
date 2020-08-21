package com.asiainfo.veris.crm.order.soa.group.esop.eopqry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynAttrQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QryFlowNodeDescBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeHBean;

public class WorkformSmsQrySVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData qryStopSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
        
        IData resultInfo = new DataMap();
        
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            if(StringUtils.isBlank(resultInfo.getString("FLOW_DESC"))){
                resultInfo.put("FLOW_DESC", resultInfo.getString("RSRV_STR4"));
            }
        }
        
        //取专线实例号
        String beginNodeId = "";
        IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
        if(DataUtils.isNotEmpty(eweInfos)){
            IData eweInfo = eweInfos.first();
            if(!"0".equals(eweInfo.getString("TEMPLET_TYPE",""))){
                IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);
                if(DataUtils.isNotEmpty(releInfos)) {
                    String recodeNum = releInfos.first().getString("RELE_VALUE", ""); 
                    IData input = new DataMap();
                    input.put("BPM_TEMPLET_ID", bpmTempletId);
                    input.put("NODE_TYPE", "3");
                    IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                    if(DataUtils.isNotEmpty(nodeTempleteList)){
                        beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
                        IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"PRODUCTNO");
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
                        }
                    }
                }
            }
        }
        if("CREDITDIRECTLINEPARSE".equals(bpmTempletId)){
            IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,"bossChange","1","PRODUCTNO");
            if(DataUtils.isNotEmpty(attrDatas)){
                resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
            }
            attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,"bossChange","1","TRADENAME");
            if(DataUtils.isNotEmpty(attrDatas)){
                resultInfo.put("TRADENAME", attrDatas.first().getString("ATTR_VALUE"));
            }
        }
        if("CREDITDIRECTLINECONTINUE".equals(bpmTempletId)){
            IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,"groupOwe","1","PRODUCTNO");
            if(DataUtils.isNotEmpty(attrDatas)){
                resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
            }
            attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,"groupOwe","1","TRADENAME");
            if(DataUtils.isNotEmpty(attrDatas)){
                resultInfo.put("TRADENAME", attrDatas.first().getString("ATTR_VALUE"));
            }
        }
        
        //获取专线资料表信息
        IData param = new DataMap();
        param.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByProductNo(param);
        
        IDataset nodeDatas = EweNodeQry.qryByBusiformNodeId(busiformNodeId);
        String nodeId = "";
        if(IDataUtil.isNotEmpty(nodeDatas)){
            nodeId = nodeDatas.first().getString("NODE_ID","");
        }
        
        //取计费时间
        if(IDataUtil.isNotEmpty(dataLines) && ("bossChange".equals(nodeId)||"bossHandle".equals(nodeId))) {// 停/复计费节点
            if("CREDITDIRECTLINEPARSE".equals(bpmTempletId) ||"MANUALSTOP".equals(bpmTempletId)){
                String userId = dataLines.first().getString("USER_ID");
                //语音专线资费存在other表
                if(!"7".equals(dataLines.first().getString("SHEET_TYPE"))){
                    IDataset discntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserIdForGrp(userId);
                    if(IDataUtil.isNotEmpty(discntInfos)){
                        for(int i=0;i<discntInfos.size();i++){
                            IData discntInfo = discntInfos.getData(i);
                            //停机标记     7-信控停机   9-人工停机
                            if("7".equals(discntInfo.getString("RSRV_STR5"))||"9".equals(discntInfo.getString("RSRV_STR5"))){
                                String endDate = discntInfo.getString("END_DATE");
                                if(StringUtils.isNotBlank(endDate)){
                                    endDate = SysDateMgr.getDateNextMonthFirstDay(endDate);
                                    resultInfo.put("START_TIME", endDate);
                                }
                                break;
                            }
                        }
                    }
                }else{
                    IData inparam = new DataMap();
                    inparam.put("USER_ID", userId);
                    inparam.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
                    //CSAppException.apperr(CrmCommException.CRM_COMM_103);
                    IDataset otherDatas = TradeOtherInfoQry.queryUserOtherByUserIdStop(inparam);
                    if(IDataUtil.isNotEmpty(otherDatas)){
                        for(int i=0;i<otherDatas.size();i++){
                            IData otherData = otherDatas.getData(i);
                            if("7".equals(otherData.getString("RSRV_STR13"))||"9".equals(otherData.getString("RSRV_STR13"))){
                                String endDate = otherData.getString("END_DATE");
                                if(StringUtils.isNotBlank(endDate)){
                                    endDate = SysDateMgr.getDateNextMonthFirstDay(endDate);
                                    resultInfo.put("START_TIME", endDate);
                                    
                                }
                                break;
                            }
                        }
                    }
                    
                }
            }else if("CREDITDIRECTLINECONTINUE".equals(bpmTempletId)||"MANUALBACK".equals(bpmTempletId)){
                String userId = dataLines.first().getString("USER_ID");
                //语音专线资费存other表
                if(!"7".equals(dataLines.first().getString("SHEET_TYPE"))){
                    IDataset discntInfos = UserDiscntInfoQry.getAllDiscntInfo(userId);
                    if(IDataUtil.isNotEmpty(discntInfos)){
                        IData discntInfo = discntInfos.first();
                        String endDate = discntInfo.getString("START_DATE");
                        resultInfo.put("START_TIME", endDate);
                    }
                }else{
                    IData inparam = new DataMap();
                    inparam.put("USER_ID", userId);
                    inparam.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
                    inparam.put("RSRV_VALUE_CODE", "N001");
                    IDataset otherDatas = TradeOtherInfoQry.queryUserOtherInfoByUserIdProductNo(inparam);
                    if(IDataUtil.isNotEmpty(otherDatas)){
                        IData otherData = otherDatas.first();
                        String endDate = otherData.getString("START_DATE");
                        resultInfo.put("START_TIME", endDate);
                    }
                }
            }
        }else if(IDataUtil.isNotEmpty(dataLines) && ("waitConfirm".equals(nodeId)||"waitReply".equals(nodeId))){//停/复功能节点
            //取自动复机时间
            if("MANUALSTOP".equals(bpmTempletId)){
                IDataset otherDatas = null;
                if(StringUtils.isNotBlank(ibsysid)&&StringUtils.isNotBlank(beginNodeId)){
                    otherDatas = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, beginNodeId);
                    if(IDataUtil.isNotEmpty(otherDatas)){
                        for(int i = 0;i<otherDatas.size();i++){
                            IData otherData = otherDatas.getData(i);
                            if("BACK_TIME".equals(otherData.getString("ATTR_CODE"))){
                                String backTime = otherData.getString("ATTR_VALUE");
                                if(StringUtils.isNotBlank(backTime)){
                                    backTime = SysDateMgr.date2String(SysDateMgr.string2Date(backTime, "yyyy-MM"), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                    resultInfo.put("BACK_TIME", backTime);
                                }
                            }
                        }
                    }
                }
            }
            
            //资管回单时间
            resultInfo.put("SYSTIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        }
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("START_TIME", resultInfo.getString("START_TIME",""));
        result.put("BACK_TIME", resultInfo.getString("BACK_TIME",""));
        result.put("TRADENAME", resultInfo.getString("TRADENAME",""));
        result.put("SYSTIME", resultInfo.getString("SYSTIME",""));
        
        return result;
    }
    
    public IData qryCancelSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
        
        IData resultInfo = new DataMap();
        
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        //取专线实例号
        String beginNodeId = "";
        IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
        if(DataUtils.isNotEmpty(eweInfos)){
            IData eweInfo = eweInfos.first();
            if(!"0".equals(eweInfo.getString("TEMPLET_TYPE",""))){
                IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);
                if(DataUtils.isNotEmpty(releInfos)) {
                    String recodeNum = releInfos.first().getString("RELE_VALUE", ""); 
                    IData input = new DataMap();
                    input.put("BPM_TEMPLET_ID", bpmTempletId);
                    input.put("NODE_TYPE", "3");
                    IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                    if(DataUtils.isNotEmpty(nodeTempleteList)){
                        beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
                        IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"PRODUCTNO");
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
                        }
                    }
                }
            }
        }
        
        //获取专线资料表信息
        IData param = new DataMap();
        param.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        IDataset dataLines = TradeOtherInfoQry.queryAllUserDataLineByProductNo(param);
        
        if(IDataUtil.isNotEmpty(dataLines)){
            //专线名称
            resultInfo.put("TRADE_NAME", dataLines.first().getString("RSRV_STR5"));
            String userId = dataLines.first().getString("USER_ID");
            
            IDataset discntInfos = UserDiscntInfoQry.queryUserAllDisnctByCode(userId, null);
            
            if(IDataUtil.isNotEmpty(discntInfos)){
                String endDate = discntInfos.first().getString("END_DATE");
                if(StringUtils.isNotBlank(endDate)){
                    endDate = endDate.substring(0, 7);
                    resultInfo.put("END_DATE", endDate);
                }
            }
        }
        
        resultInfo.put("SYSTIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("TRADE_NAME", resultInfo.getString("TRADE_NAME",""));
        result.put("END_DATE", resultInfo.getString("END_DATE",""));
        result.put("SYSTIME", resultInfo.getString("SYSTIME",""));
        
        return result;
        
    }

    public IData qryChangeSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
        
        IData resultInfo = new DataMap();
        
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        //取专线实例号
        String beginNodeId = "";
        IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
        if(DataUtils.isNotEmpty(eweInfos)){
            IData eweInfo = eweInfos.first();
            if(!"0".equals(eweInfo.getString("TEMPLET_TYPE",""))){
                IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);
                if(DataUtils.isNotEmpty(releInfos)) {
                    String recodeNum = releInfos.first().getString("RELE_VALUE", ""); 
                    IData input = new DataMap();
                    input.put("BPM_TEMPLET_ID", bpmTempletId);
                    input.put("NODE_TYPE", "3");
                    IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                    if(DataUtils.isNotEmpty(nodeTempleteList)){
                        beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
                        IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"PRODUCTNO");
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
                        }
                        IDataset changeModeDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,"0","CHANGEMODE");
                        if(DataUtils.isNotEmpty(changeModeDatas)){
                            resultInfo.put("CHANGEMODE", changeModeDatas.first().getString("ATTR_VALUE"));
                        }
                    }
                }
            }
        }
        
        //获取专线资料表信息
        IData param = new DataMap();
        param.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByProductNo(param);
        
        if(IDataUtil.isNotEmpty(dataLines)){
            //专线名称
            resultInfo.put("TRADE_NAME", dataLines.first().getString("RSRV_STR5"));
            String userId = dataLines.first().getString("USER_ID");
            
            //只有扩容场景存在计费方式
            if("扩容".equals(resultInfo.getString("CHANGEMODE",""))){
                IDataset otherDatas = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, beginNodeId);
                if(IDataUtil.isNotEmpty(otherDatas)){
                    for(int i = 0;i<otherDatas.size();i++){
                        IData otherData = otherDatas.getData(i);
                        if("ACCEPTTANCE_PERIOD".equals(otherData.getString("ATTR_CODE"))){
                            String accepttancePeriod = otherData.getString("ATTR_VALUE");
                            if("0".equals(accepttancePeriod)){
                                resultInfo.put("START_TIME", SysDateMgr.getSysDate("yyyy-MM"));
                            }else if("1".equals(accepttancePeriod)){
                                String startTime =  SysDateMgr.getFirstDayOfNextMonth(SysDateMgr.getSysDate());
                                if(StringUtils.isNotBlank(startTime)){
                                    startTime = startTime.substring(0, 7);
                                    resultInfo.put("START_TIME", startTime);
                                }
                            }
                        }
                    }
                }
            }else{
                String day = SysDateMgr.getCurDay();
                String startDate = "";
                if(Integer.valueOf(day) < 25) {
                    startDate = SysDateMgr.getFirstDayOfThisMonth();
                } else {
                    startDate = SysDateMgr.getFirstDayOfNextMonth();
                }
                if(StringUtils.isNotBlank(startDate)){
                    startDate = startDate.substring(0, 7);
                    resultInfo.put("START_TIME", startDate);
                }
            }
            
        }
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("TRADE_NAME", resultInfo.getString("TRADE_NAME",""));
        result.put("START_TIME", resultInfo.getString("START_TIME",""));
        result.put("CHANGEMODE", resultInfo.getString("CHANGEMODE",""));
        result.put("BPM_NAME", resultInfo.getString("BPM_NAME",""));
        
        return result;
    }
    
    public IData qryCancelOrderSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
        String moveEsop = data.getString("MOVE_ESOP", "");
        
        IData resultInfo = new DataMap();
        
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeHBean.qryScribeHInfoByIbsysid(ibsysid);
        boolean isBh = true;
        if(DataUtils.isEmpty(subscribeInfos)){
            subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
            isBh = false;
        }
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        //取专线实例号
        String beginNodeId = "";
        IDataset eweInfos = EweNodeQry.qryEweHByBusiformId(busiformId);
        if(DataUtils.isNotEmpty(eweInfos)){
            IData eweInfo = eweInfos.first();
            if(!"0".equals(eweInfo.getString("TEMPLET_TYPE",""))){
                IDataset releInfos = WorkformReleBean.qryBhBySubBusiformId(busiformId);
                if(DataUtils.isNotEmpty(releInfos)) {
                    String recodeNum = releInfos.first().getString("RELE_VALUE", ""); 
                    IData input = new DataMap();
                    input.put("BPM_TEMPLET_ID", bpmTempletId);
                    input.put("NODE_TYPE", "3");
                    IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                    if(DataUtils.isNotEmpty(nodeTempleteList)){
                        beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
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
                            resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
                        }
                        
                        attrParam.put("ATTR_CODE", "TRADENAME");
                        if(isBh){
                            attrDatas = WorkformAttrHBean.qryMaxHisAttrByAttrCodeRecodeNum(attrParam);
                        }else{
                            attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"TRADENAME");
                        }
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("TRADE_NAME", attrDatas.first().getString("ATTR_VALUE"));
                        }
                    }
                }
            }
        }
        
        resultInfo.put("SYSTIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO", ""));
        result.put("TRADE_NAME", resultInfo.getString("TRADE_NAME",""));
        result.put("BPM_NAME", resultInfo.getString("BPM_NAME",""));
        result.put("SYSTIME", resultInfo.getString("SYSTIME",""));
        
        return result;
        
    }
    
    public IData qryTranserSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        
        IData resultInfo = new DataMap();
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        String productNo = "";
        String oldGrpUserId = "";
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", bpmTempletId);
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(DataUtils.isNotEmpty(nodeTempleteList)){
            String beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
            IData attrParam = new DataMap();
            attrParam.put("IBSYSID", ibsysid);
            attrParam.put("NODE_ID", beginNodeId);
            attrParam.put("ATTR_CODE", "PRODUCTNO");
            IDataset attrDatas = WorkformAttrBean.qryMaxNewAttrByAttrCode(attrParam);
            String productNos = "";
            if(DataUtils.isNotEmpty(attrDatas)){
                for(int i = 0;i<attrDatas.size();i++){
                    IData attrData = attrDatas.getData(i);
                    productNo = attrData.getString("ATTR_VALUE");
                    productNos += attrData.getString("ATTR_VALUE") + ",";
                }
            }
            if(StringUtils.isNotBlank(productNos)){
                resultInfo.put("PRODUCT_NO", productNos.substring(0, productNos.length()-1));
            }
            
            attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,"0","USER_ID_A");
            if(DataUtils.isNotEmpty(attrDatas)){
                oldGrpUserId = attrDatas.first().getString("ATTR_VALUE");
            }
            
        }
        
        IDataset oldGrpUserInfos = UserInfoQry.getUserInfoByUserIdTag(oldGrpUserId,"0");
        if(DataUtils.isNotEmpty(oldGrpUserInfos)){
            String oldGrpSN = oldGrpUserInfos.first().getString("SERIAL_NUMBER");
            resultInfo.put("SERIAL_NUMBER_A", oldGrpSN);
            String custId = oldGrpUserInfos.first().getString("CUST_ID");
            IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
            if(DataUtils.isNotEmpty(custInfo)){
                resultInfo.put("GROUP_ID_A", custInfo.getString("GROUP_ID"));
                IDataset staffInfo = StaffInfoQry.getStaffAreaInfoByID(custInfo.getString("CUST_MANAGER_ID",""));
                if(DataUtils.isNotEmpty(staffInfo)){
                    resultInfo.put("STAFF_NAME_A", staffInfo.first().getString("STAFF_NAME"));
                }
            }
        }
        
        //获取专线资料表信息
        IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByProductNo(param);
        if(DataUtils.isNotEmpty(dataLines)){
            String mebUserId = dataLines.first().getString("USER_ID");
            IDataset relaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(mebUserId);
            if(DataUtils.isNotEmpty(relaInfos)){
                String grpUserId = relaInfos.first().getString("USER_ID_A");
                resultInfo.put("SERIAL_NUMBER_B", relaInfos.first().getString("SERIAL_NUMBER_A"));
                IDataset grpUserInfos = UserInfoQry.getUserInfoByUserIdTag(grpUserId,"0");
                if(DataUtils.isNotEmpty(grpUserInfos)){
                    String custIdb = grpUserInfos.first().getString("CUST_ID");
                    IData custInfob = UcaInfoQry.qryGrpInfoByCustId(custIdb);
                    if(DataUtils.isNotEmpty(custInfob)){
                        resultInfo.put("GROUP_ID_B", custInfob.getString("GROUP_ID"));
                    }
                }
            }
        }
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("GROUP_ID_A", resultInfo.getString("GROUP_ID_A",""));
        result.put("SERIAL_NUMBER_A", resultInfo.getString("SERIAL_NUMBER_A",""));
        result.put("GROUP_ID_B", resultInfo.getString("GROUP_ID_B",""));
        result.put("SERIAL_NUMBER_B", resultInfo.getString("SERIAL_NUMBER_B",""));
        result.put("STAFF_NAME_A", resultInfo.getString("STAFF_NAME_A",""));
        
        return result;
    }
    
    public IData qryRedListSmsInfos(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID","");
        
        IData resultInfo = new DataMap();
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", bpmTempletId);
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(DataUtils.isNotEmpty(nodeTempleteList)){
            String beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
            IData attrParam = new DataMap();
            attrParam.put("IBSYSID", ibsysid);
            attrParam.put("NODE_ID", beginNodeId);
            attrParam.put("ATTR_CODE", "SERIAL_NUMBER");
            IDataset attrDatas = WorkformAttrBean.qryMaxNewAttrByAttrCode(attrParam);
            String productNos = "";
            if(DataUtils.isNotEmpty(attrDatas)){
                for(int i = 0;i<attrDatas.size();i++){
                    IData attrData = attrDatas.getData(i);
                    String sn = attrData.getString("ATTR_VALUE");
                    IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
                    if(DataUtils.isNotEmpty(userInfo)){
                        String userId = userInfo.getString("USER_ID");
                        IData dataLineParam = new DataMap();
                        dataLineParam.put("USER_ID", userId);
                        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(dataLineParam);
                        if(DataUtils.isNotEmpty(dataLines)){
                            for(int j = 0;j<dataLines.size();j++){
                                String productNo = dataLines.getData(j).getString("PRODUCT_NO","");
                                productNos += productNo+",";
                            }
                        }
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
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("CUST_NAME", resultInfo.getString("CUST_NAME",""));
        return result;
    }
    
    public IData qryAcctSmsInfos(IData data) throws Exception{
        String ibsysid = data.getString("IBSYSID","");
        
        IData resultInfo = new DataMap();
        
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", bpmTempletId);
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(DataUtils.isNotEmpty(nodeTempleteList)){
            String beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
            IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,"0","OPERTYPE");
            if(DataUtils.isNotEmpty(attrDatas)){
                String operType = attrDatas.first().getString("ATTR_VALUE");
                resultInfo.put("ACCT_TYPE", StaticUtil.getStaticValue("EOP_ACCT_OPERTYPE", operType));
            }
            IDataset snDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,"0","SERIAL_NUMBER");
            if(DataUtils.isNotEmpty(snDatas)){
                //去除产品编码重复
                Set<String> snSet = new HashSet<String>();
                Set<String> acctSet = new HashSet<String>();
                String productNos = "";
                for(int i=0;i<snDatas.size();i++){
                    String mebSn = snDatas.getData(i).getString("ATTR_VALUE");
                    IDataset relaInfos = RelaUUInfoQry.getRelatsBySNB(mebSn);
                    if(DataUtils.isNotEmpty(relaInfos)){
                        snSet.add(relaInfos.first().getString("SERIAL_NUMBER_A"));
                        String mebUserId = relaInfos.first().getString("USER_ID_B");
                        IData payData = UcaInfoQry.qryDefaultPayRelaByUserId(mebUserId);
                        if(DataUtils.isNotEmpty(payData)){
                            acctSet.add(payData.getString("ACCT_ID"));
                        }
                        
                        IData dataLineParam = new DataMap();
                        dataLineParam.put("USER_ID", mebUserId);
                        IDataset dataLines = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(dataLineParam);
                        if(DataUtils.isNotEmpty(dataLines)){
                            for(int j = 0;j<dataLines.size();j++){
                                String productNo = dataLines.getData(j).getString("PRODUCT_NO","");
                                productNos += productNo+",";
                            }
                        }
                    }
                }
                if(StringUtils.isNotBlank(productNos)){
                    resultInfo.put("PRODUCT_NO", productNos.substring(0, productNos.length()-1));
                }
                
                Iterator<String> acctIt = acctSet.iterator();
                String acctIds = "";
                while(acctIt.hasNext()){
                    acctIds += acctIt.next()+",";
                }
                if(StringUtils.isNotBlank(acctIds)){
                    resultInfo.put("ACCT_ID", acctIds.substring(0, acctIds.length()-1));
                }
                
                Set<String> productSet = new HashSet<String>();
                String grpSns = "";
                Iterator<String> snIt = snSet.iterator();
                while(snIt.hasNext()){
                    String grpSn = snIt.next();
                    grpSns +=  grpSn+",";
                    IData productData = UcaInfoQry.qryUserMainProdInfoBySn(grpSn);
                    productSet.add(productData.getString("PRODUCT_NAME"));
                }
                if(StringUtils.isNotBlank(grpSns)){
                    resultInfo.put("SERIAL_NUMBER", grpSns.substring(0, grpSns.length()-1));
                }
                
                Iterator<String> producIt = productSet.iterator();
                String productNames = "";
                while(producIt.hasNext()){
                    String productName = producIt.next();
                    productNames += productName+",";
                }
                if(StringUtils.isNotBlank(productNames)){
                    resultInfo.put("PRODUCT_NAME", productNames.substring(0, productNames.length()-1));
                }
            }
        }
        //CSAppException.apperr(GrpException.CRM_GRP_713);
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("GROUP_ID", resultInfo.getString("GROUP_ID",""));
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("ACCT_TYPE", resultInfo.getString("ACCT_TYPE",""));
        result.put("ACCT_ID", resultInfo.getString("ACCT_ID",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("SERIAL_NUMBER", resultInfo.getString("SERIAL_NUMBER",""));
        result.put("PRODUCT_NAME", resultInfo.getString("PRODUCT_NAME",""));
        return result;
    }
    
    public IData qryHangSmsInfos(IData data) throws Exception{
        String ibsysid = data.getString("IBSYSID","");
        String busiformId = data.getString("BUSIFORM_ID", "");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
        
        //取流程名、工单主题
        IData resultInfo = new DataMap();
        String bpmTempletId = "";
        IDataset subscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(subscribeInfos))
        {
            bpmTempletId =  subscribeInfos.first().getString("BPM_TEMPLET_ID","");
            resultInfo.putAll(subscribeInfos.first());
            
            IDataset flowDatas = QryFlowNodeDescBean.qryFlowDescBy(bpmTempletId, "0");
            if(DataUtils.isNotEmpty(flowDatas)){
                resultInfo.put("BPM_NAME", flowDatas.first().getString("TEMPLET_NAME"));
            }
        }
        
        //取专线实例号、专线名称
        String beginNodeId = "";
        IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
        if(DataUtils.isNotEmpty(eweInfos)){
            IData eweInfo = eweInfos.first();
            if(!"0".equals(eweInfo.getString("TEMPLET_TYPE",""))){
                IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);
                if(DataUtils.isNotEmpty(releInfos)) {
                    String recodeNum = releInfos.first().getString("RELE_VALUE", ""); 
                    IData input = new DataMap();
                    input.put("BPM_TEMPLET_ID", bpmTempletId);
                    input.put("NODE_TYPE", "3");
                    IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                    if(DataUtils.isNotEmpty(nodeTempleteList)){
                        beginNodeId = nodeTempleteList.first().getString("NODE_ID","");
                        IDataset attrDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"PRODUCTNO");
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("PRODUCT_NO", attrDatas.first().getString("ATTR_VALUE"));
                        }
                        IDataset tradeDatas = WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,beginNodeId,recodeNum,"TRADENAME");
                        if(DataUtils.isNotEmpty(attrDatas)){
                            resultInfo.put("TRADE_NAME", tradeDatas.first().getString("ATTR_VALUE"));
                        }
                    }
                }
            }
        }
        
        //取挂起、解挂原因
        IDataset nodeDatas = EweNodeQry.qryByBusiformNodeId(busiformNodeId);
        if(DataUtils.isNotEmpty(nodeDatas)){
            String preNodeId = nodeDatas.first().getString("PRE_NODE_ID");
            String preBusiformNodeId = nodeDatas.first().getString("PRE_BUSIFORM_NODE_ID");
            String nodeId = nodeDatas.first().getString("NODE_ID");
            String applyType = "";
            if("waitUnhangWorkSheet".equals(nodeId)&&"replyhangWorkSheet".equals(preNodeId)){
                applyType = "00";//客户类原因挂起
            }else if("waitUnhangWorkSheet".equals(nodeId)&&"waitConfirm".equals(preNodeId)){
                IData attrData = getAsynData(busiformId,preBusiformNodeId,preNodeId);
                applyType = attrData.getString("applyType","");//施工纠纷类或其他原因挂起
            }else if("waitConfirm".equals(nodeId)&&"waitUnhangWorkSheet".equals(preNodeId)){
                IData attrData = getAsynData(busiformId,preBusiformNodeId,preNodeId);
                applyType = attrData.getString("applyType","");
            }
            if("01".equals(applyType)){//施工纠纷类
                resultInfo.put("APPLY_TYPE", "施工纠纷");
            }else if("99".equals(applyType)){//其他
                resultInfo.put("APPLY_TYPE", "其他");
            }else {//客户类原因  00
                resultInfo.put("APPLY_TYPE", "客户原因");
            }
        }
        
        IData result = new DataMap();
        result.put("IBSYSID", ibsysid);
        result.put("FLOW_DESC", resultInfo.getString("FLOW_DESC",""));
        result.put("BPM_NAME", resultInfo.getString("BPM_NAME",""));
        result.put("PRODUCT_NO", resultInfo.getString("PRODUCT_NO",""));
        result.put("TRADE_NAME", resultInfo.getString("TRADE_NAME",""));
        result.put("APPLY_TYPE", resultInfo.getString("APPLY_TYPE",""));
        
        return result;
    }
    
    private IData getAsynData(String busiformId,String busiformNodeId,String nodeId) throws Exception{
        
        IData result = new DataMap();
        IDataset asynInfo = EweAsynInfoQry.qryInfosByBusiformNode(busiformNodeId,busiformId,nodeId);
        boolean isBh = false;
        if(DataUtils.isEmpty(asynInfo)){
            asynInfo = EweAsynInfoQry.qrybHInfosByBusiformNode(busiformNodeId,busiformId,nodeId);
            isBh = true;
        }
        
        if(DataUtils.isNotEmpty(asynInfo)){
            String asynId = asynInfo.first().getString("ASYN_ID");
            IDataset asynAttrs = null;
            if(isBh){
                asynAttrs = EweAsynAttrQry.qrybHAsynAttrInfosByAsynId(asynId);
            }else{
                asynAttrs = EweAsynAttrQry.qryAsynAttrInfosByAsynId(asynId);
            }
            if(DataUtils.isNotEmpty(asynAttrs)){
                for(int i =0;i<asynAttrs.size();i++){
                    IData asynAttr = asynAttrs.getData(i);
                    result.put(asynAttr.getString("ATTR_CODE"), asynAttr.getString("ATTR_VALUE"));
                }
            }
            
        }
        
        return result;
        
    }
    
}
