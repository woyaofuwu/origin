package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class SendEosUtil
{
    private static final Logger log = Logger.getLogger(SendEosUtil.class);

    public static IDataset sendEos(IData params, IData eosData,IDataset otherInfos) throws Exception
    {
        params.put("IBSYSID", eosData.getString("IBSYSID", ""));            // 定单号
        params.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID"));       // 
        params.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID"));             // 产品标识
        params.put("OPER_TYPE", eosData.getString("BUSIFORM_OPER_TYPE", "")); // 操作类型 参见订单操作类型编码
        params.put("PRIORITY", eosData.getString("PRIORITY", ""));                                             // 优先级
        params.put("IN_MODE_CODE", eosData.getString("IN_MODE_CODE"));         // 接入方式编码
        params.put("BUSI_CODE", eosData.getString("BUSI_CODE"));             // 产品标识
        params.put("BUSIFORM_NODE_ID", eosData.getString("BUSIFORM_NODE_ID",""));       // 节点流水号（流程驱动时用到）
        params.put("BUSIFORM_ID", eosData.getString("BUSIFORM_ID",""));       // 流程标识ID
        params.put("NODE_ID", eosData.getString("NODE_ID",""));       // 流程节点
        if(DataUtils.isNotEmpty(otherInfos))
        {
        	params.put("OTHER_INFO", otherInfos);
        }
        // 调用eos接口创建或者推动流程
        log.debug("调用eos接口创建或者推动流程_入参===" + params);
        IDataset returnParams =	CSAppCall.call(eosData.getString("SVC_NAME"), params);
        log.debug("调用eos接口创建或者推动流程_出参===" + returnParams);
        return returnParams;
    }
}
