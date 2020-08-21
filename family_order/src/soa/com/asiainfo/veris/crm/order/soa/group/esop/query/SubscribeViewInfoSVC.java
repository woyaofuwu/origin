package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SubscribeViewInfoSVC extends CSBizService {

    private static final long serialVersionUID = 1L;

    public static IDataset qryGroupAttach(IData param) throws Exception {
        String IBSYSID = param.getString("IBSYSID");
        return SubscribeViewInfoBean.qryGroupAttach(param);
    }

    // 查询当前工单轨迹
    public IDataset queryWorksheet(IData idata) throws Exception {
        String ibsysid = idata.getString("IBSYSID");
        return SubscribeViewInfoBean.queryWorkFormNodeInfo(ibsysid);
    }

    // 查询未完成的工单的业务属性信息
    public IDataset qryEweServiceAttributes(IData idata) throws Exception {

        String ibsysid = idata.getString("IBSYSID");
        String sub_ibsysid = idata.getString("SUB_IBSYSID");
        String record_num = idata.getString("RECORD_NUM");
        return SubscribeViewInfoBean.qryEweServiceAttributes(ibsysid, sub_ibsysid, record_num);
    }

    public IDataset qryEopServiceAttributes(IData idata) throws Exception {

        String ibsysid = idata.getString("IBSYSID");
        return SubscribeViewInfoBean.qryEopServiceAttributes(ibsysid);
    }

    // 根据node_id 和 ibsysid 查询工单的节点属性
    public static IDataset qryEweAttributesByNodeIdIbsysid(IData idata) throws Exception {
        String ibsysid = idata.getString("IBSYSID");
        String node_id = idata.getString("NODE_ID");
        String record_num = idata.getString("RECORD_NUM");
        return SubscribeViewInfoBean.qryEweAttributesByNodeIdIbsysid(ibsysid, node_id, record_num);
    }

    // 查询订单的详细订单信息
    public static IDataset qryWorkformNodeByIbsysid(IData idata) throws Exception {
        String ibsysid = idata.getString("IBSYSID");
        String node_id = idata.getString("NODE_ID");
        return SubscribeViewInfoBean.qryWorkformNodeByIbsysid(ibsysid, node_id);
    }

    // 查询已经完成的工单轨迹
    public IDataset queryFinishWorksheet(IData idata) throws Exception {
        String ibsysid = idata.getString("IBSYSID");
        String busiformId = idata.getString("BUSIFORM_ID", "");
        return SubscribeViewInfoBean.queryFinishWorksheet(ibsysid, busiformId);
    }

    // 查询已经完成订单的详细订单信息
    public static IDataset qryFinishWorkformNodeByIbsysid(IData idata) throws Exception {
        String ibsysid = idata.getString("IBSYSID");
        String node_id = idata.getString("NODE_ID");
        return SubscribeViewInfoBean.qryFinishWorkformNodeByIbsysid(ibsysid, node_id);
    }

    // 查询已完成的工单的业务属性信息
    public IDataset qryFinishEweServiceAttributes(IData idata) throws Exception {

        String ibsysid = idata.getString("IBSYSID");
        String sub_ibsysid = idata.getString("SUB_IBSYSID");
        String record_num = idata.getString("RECORD_NUM");
        return SubscribeViewInfoBean.qryFinishEweServiceAttributes(ibsysid, sub_ibsysid, record_num);
    }

    // 根据node_id 和 ibsysid查询已完成的工单的业务属性信息
    public IDataset qryFinishEweAttributesByNodeIdIbsysid(IData idata) throws Exception {

        String ibsysid = idata.getString("IBSYSID");
        String node_id = idata.getString("NODE_ID");
        String record_num = idata.getString("RECORD_NUM");
        return SubscribeViewInfoBean.qryFinishEweAttributesByNodeIdIbsysid(ibsysid, node_id, record_num);
    }

    // 根据group_seq 和 ibsysid查询已完成的工单的业务(等待资管勘察回复)属性信息
    public IDataset qryFinishEweAttributesByGroupSeq(IData idata) throws Exception {

        String sub_ibsysid = idata.getString("SUB_IBSYSID");
        String group_seq = idata.getString("GROUP_SEQ");
        return SubscribeViewInfoBean.qryFinishEweAttributesByGroupSeq(sub_ibsysid, group_seq);
    }

    // 根据group_seq 和 ibsysid查询已完成的工单的业务(等待资管勘察回复)属性信息
    public IDataset qryEweAttributesByGroupSeq(IData idata) throws Exception {

        String sub_ibsysid = idata.getString("SUB_IBSYSID");
        String group_seq = idata.getString("GROUP_SEQ");
        return SubscribeViewInfoBean.qrEweAttributesByGroupSeq(sub_ibsysid, group_seq);
    }

    // 查询已经完成的工单的附件信息
    public static IDataset qryFinishGroupAttach(IData param) throws Exception {
        String IBSYSID = param.getString("IBSYSID");
        return SubscribeViewInfoBean.qryFinishGroupAttach(IBSYSID);
    }

    public IDataset qryFinishAttrByGroupSeqRecordNum(IData idata) throws Exception {

        String sub_ibsysid = idata.getString("SUB_IBSYSID");
        String group_seq = idata.getString("GROUP_SEQ");
        String record_num = idata.getString("RECORD_NUM");
        return SubscribeViewInfoBean.qryFinishAttrByGroupSeqRecordNum(sub_ibsysid, group_seq, record_num);
    }
}
