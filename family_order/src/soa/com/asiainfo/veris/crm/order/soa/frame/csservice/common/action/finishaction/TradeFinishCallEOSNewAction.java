package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweAsynBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class TradeFinishCallEOSNewAction implements ITradeFinishAction {
    private static Logger logger = Logger.getLogger(TradeFinishCallEOSNewAction.class);

    public void executeAction(IData mainTrade) throws Exception {
        logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION>>>>>>>>>>>>>>>>>>");
        String tradeId = mainTrade.getString("TRADE_ID");

        logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION--TRADEID>>>>" + tradeId + ">>>>>>>>>>>>>>");

        IDataset extTrades = TradeExtInfoQry.getTradeEsopInfoTradeId(tradeId);
        if (IDataUtil.isEmpty(extTrades))
            return;

        IData extTrade = extTrades.getData(0);
        String eosTag = extTrade.getString("RSRV_STR10");
        String newFlag = extTrade.getString("RSRV_STR5");
        String discntFlag = extTrade.getString("RSRV_STR7");

        if (!"EOS".equals(eosTag))
            return;

        if (!"NEWFLAG".equals(newFlag))
            return;
        if ("DISCNTCHANGE".equals(discntFlag)||"DATAHANGE".equals(discntFlag))
            return;

        // 集团专线的特殊处理,开户变更,改由pboss调用esop接口
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", "");
        String userId = mainTrade.getString("USER_ID", "");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER", "");
        IData params = new DataMap();
        if ("3016".equals(tradeTypeCode) || "3086".equals(tradeTypeCode) || "3088".equals(tradeTypeCode) || "3018".equals(tradeTypeCode)||"3849".equals(tradeTypeCode)||"3850".equals(tradeTypeCode)) {
            IData result = new DataMap();
            String ibsysId = extTrade.getString("ATTR_VALUE");
            String recodNum = extTrade.getString("RSRV_STR6");
            String busiformId = extTrade.getString("RSRV_STR4");
            String nodeId = extTrade.getString("RSRV_STR1");
            // 根据ibsysId查询稽核员工工号
            IData staffInfo = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId, "AUDITSTAFF", "0");
            if (IDataUtil.isEmpty(staffInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到稽核人员信息！");
            }
            IDataset eweNodeInfo = EweNodeQry.qryEweNodeByBusiformIdAndNodeId(busiformId, nodeId);
            if (IDataUtil.isEmpty(eweNodeInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取节点信息为空！");
            }
            String busiforNodeId = eweNodeInfo.first().getString("BUSIFORM_NODE_ID");
            // 获取bpmtmplteId
            IDataset eweInfo = EweNodeQry.qryEweByBusiformId(busiformId);
            if (IDataUtil.isEmpty(eweInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取流程信息为空！");
            }
            String bpmTempletId = eweInfo.first().getString("BPM_TEMPLET_ID");
            String staffId = staffInfo.getString("ATTR_VALUE");
            String url = "/order/iorder?service=page/igroup.esop.audit.Audit&listener=initial&IBSYSID=" + ibsysId + "&RECORD_NUM=" + recodNum + "&BPM_TEMPLET_ID=" + bpmTempletId + "&BUSIFORM_NODE_ID=" + busiforNodeId + "&TRADE_ID=" + tradeId;
            result.put("URL", url);
            result.put("BI_SN", ibsysId);
            result.put("BUSIFORM_NODE_ID", busiforNodeId);
            // result.put("DEAL_STAFF_ID", staffId);
            result.put("INFO_AUTH", staffId);
            String svcName = "SS.EsopWorkTaskDataSVC.getReadTaskDataInfo";

            result.put("BUSI_TYPE_CODE", result.getString("BUSIFORM_OPER_TYPE", ""));
            result.put("NODE_DESC", "专线开通稽核");
            if("3016".equals(tradeTypeCode) || "3086".equals(tradeTypeCode)|| "3849".equals(tradeTypeCode)){
            	result.put("BUSI_TYPE", "订单开通");
            }else{
            	result.put("BUSI_TYPE", "订单变更");
            }
            // result.put("INFO_TYPE",EosStaticData.TASK_TYPE_READ);
            IDataset taskDatas = CSAppCall.call(svcName, result);
            if (DataUtils.isEmpty(taskDatas)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "BUSIFORM_NODE_ID:" + result.getString("BUSIFORM_NODE_ID") + "待阅工单生成数据抽取失败！！");
            }
            IData taskInfoData = taskDatas.getData(0);
            taskInfoData.put("INFO_SIGN", tradeId);
            taskInfoData.remove("RECE_OBJS");
            taskInfoData.put("RECE_OBJ", staffId);
         //   taskInfoData.put("INFO_CHILD_TYPE", "35");
            taskInfoData.put("INFO_SEND_TIME", SysDateMgr.getSysTime());
            taskInfoData.put("END_TIME", SysDateMgr.END_DATE_FOREVER);
            // 生成待阅工单
            logger.debug(">>>>>>>>>>>>>>>>>生成待办时入参>>>>>>>>>>>>>>>>>>" + taskInfoData.toString());
            CSAppCall.call("SS.WorkTaskMgrSVC.crtWorkTaskInfo", taskInfoData).first();
        }
        if ("2991".equals(tradeTypeCode)) {
            String orderId = mainTrade.getString("ORDER_ID");
            IDataset orderList = TradeInfoQry.queryTradeByOrerId(orderId, "0");
            if (orderList.size() > 1)
                return;
        }
        if("2990".equals(tradeTypeCode)){
        	String rsrvStr10 =   mainTrade.getString("RSRV_STR10");
        	if("EOS".equals(rsrvStr10))
        		return;	
        }
        if ("2990".equals(tradeTypeCode) || "3010".equals(tradeTypeCode) || "3011".equals(tradeTypeCode) || "3080".equals(tradeTypeCode) || "3081".equals(tradeTypeCode) || "4200".equals(tradeTypeCode) || "4201".equals(tradeTypeCode)
                || "3086".equals(tradeTypeCode) || "3016".equals(tradeTypeCode) || "4690".equals(tradeTypeCode) || "3846".equals(tradeTypeCode)|| "3849".equals(tradeTypeCode)) {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION的判断业务类型>>>>>>>>>>>>>>>>>>");
            /* params.put("BUSIFORM_ID", extTrade.getString("RSRV_STR4")); // 流程标识ID params.put("NODE_ID", extTrade.getString("RSRV_STR1")); // 流程节点 //推动流程 CSAppCall.call("SS.WorkformDriveSVC.execute", params); */
            params.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
            params.put("NODE_ID", extTrade.getString("RSRV_STR1"));
            params.put("ORDER_ID", mainTrade.getString("ORDER_ID", ""));
            params.put("USER_ID", userId);
            params.put("BATCH_ID", mainTrade.getString("BATCH_ID", ""));
            params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER", ""));
            params.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
            params.put("TRADE_TYPE_CODE", tradeTypeCode);
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("RECORD_NUM", extTrade.getString("RSRV_STR6"));
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION>>>>>>>>>>>>>>>>>>" + params.toString());
            if("GROUPATTRCHANGE".equals(discntFlag)){
            	CSAppCall.call("SS.EopIntfSVC.saveAttrInfoAndDrive", params);
            }else{
            	CSAppCall.call("SS.EopIntfSVC.saveEopNodeAndDrive", params);
                // 完工保存checkinWorkSheet数据
                CSAppCall.call("SS.WorkformCheckinSVC.record", params);
            }

        }

        if("3088".equals(tradeTypeCode) || "3018".equals(tradeTypeCode) || "2991".equals(tradeTypeCode) || "1602".equals(tradeTypeCode)||"3850".equals(tradeTypeCode)) {
            String pfWait = mainTrade.getString("PF_WAIT");
            params.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
            params.put("NODE_ID", extTrade.getString("RSRV_STR1"));
            params.put("ORDER_ID", mainTrade.getString("ORDER_ID", ""));
            params.put("USER_ID", userId);
            params.put("BATCH_ID", mainTrade.getString("BATCH_ID", ""));
            params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER", ""));
            params.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
            params.put("TRADE_TYPE_CODE", tradeTypeCode);
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("RECORD_NUM", extTrade.getString("RSRV_STR6"));
            /* if("0".equals(pfWait)){ CSAppCall.call("SS.EopIntfSVC.saveEopSubAndDrive", params); } if("1".equals(pfWait)){ */
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程ACTION>>>>>>>>>>>>>>>>>>" + params.toString());
            CSAppCall.call("SS.EopIntfSVC.saveEopSubTradeAndDrive", params);
            /* } */

            // 完工保存checkinWorkSheet数据
            CSAppCall.call("SS.WorkformCheckinSVC.record", params);
        }
        if ("3002".equals(tradeTypeCode) || "3092".equals(tradeTypeCode) || "3095".equals(tradeTypeCode)|| "3848".equals(tradeTypeCode)) {
            IData data = new DataMap();

            data.put("IBSYSID", extTrade.getString("ATTR_VALUE"));
            // data.put("SUB_IBSYSID", result.getString("SUB_IBSYSID"));
            data.put("BUSIFORM_ID", extTrade.getString("RSRV_STR4"));
            data.put("NODE_ID", extTrade.getString("RSRV_STR1"));
            data.put("RECORD_NUM", extTrade.getString("RSRV_STR6"));
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务WorkformDriveSVC>>>>>>>>>>>>>>>>>" + data.toString());

            // CSAppCall.call("SS.WorkformDriveSVC.execute", data);
            EweAsynBean.saveAsynInfo(data);

            // 完工保存checkinWorkSheet数据
            CSAppCall.call("SS.WorkformCheckinSVC.record", data);
        }

    }

}
