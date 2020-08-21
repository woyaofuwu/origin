package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IDealEospUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.esopmanage.IEsopData;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTraQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class EsopBaseDataImpl implements IEsopData {

    public String ibsysId = ""; // ESOP主工单号

    public String subIbsysid = ""; // ESOP节点流水号

    public String busiformId = "";// eos主工单号

    public String bpmTempletId = "";

    public String mainProdcutId = "";// 主商品ID

    public IData repMap = new DataMap();// 公共用参数信息

    public IData offers = new DataMap();// 产品信息

    public IData contrInfo = new DataMap();// 合同信息

    @Override
    public IDataset actEsopInfo(IData inparam) throws Exception {
        // 1.处理公共信息
        dealCommData(inparam);

        // 2.获取esop产品信息
        queryOfferInfo();

        // 3.获取合同信息
        queryContractInfo();

        // 4.处理esop数据给外系统返回结果
        IDataset dataset = sendEosData();

        return dataset;
    }

    /**
     * 处理公共信息
     * 
     * @param map
     * @throws Exception
     */
    private void dealCommData(IData map) throws Exception {
        repMap = new DataMap(map);

        if (IDataUtil.isEmpty(repMap)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口数据获取失败！");
        }

        ibsysId = repMap.getString("IBSYSID");// ESOP主工单号
        busiformId = repMap.getString("BUSIFORM_ID");// eos主工单号
        bpmTempletId = repMap.getString("BPM_TEMPLET_ID");
        mainProdcutId = repMap.getString("BUSI_CODE");

        // 1.业务流程关系配置表
        IDataset ewe = EweNodeQry.qryEweByIbsysid(ibsysId, "0");
        if (IDataUtil.isEmpty(ewe)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据ibsysId=" + ibsysId + "未查询到tf_b_ewe流程信息！");
        }
        IData data = ewe.getData(0);
        String operType = "";// 业务单操作类型 20 开通 23 变更 25 注销
        if ("MOBILE400OPEN".equals(bpmTempletId) || "DATAFREEDOMRECHARGEOPEN".equals(bpmTempletId)) {
            operType = "20";
            repMap.put("OPER_TYPE", "CrtUs");
        } else if ("MOBILE400CHANGE".equals(bpmTempletId) || "DATAFREEDOMRECHARGECHANGE".equals(bpmTempletId)) {
            operType = "23";
            repMap.put("OPER_TYPE", "ChgUs");
        } else if ("MOBILE400CANCEL".equals(bpmTempletId) || "DATAFREEDOMRECHARGECANCEL".equals(bpmTempletId)) {
            operType = "25";
            repMap.put("OPER_TYPE", "DstUs");
        }
        repMap.put("OPER_CODE", EcEsopConstants.eopOperTypeToCrmOperCode(operType));
        repMap.put("BUSIFORM_OPER_TYPE", operType);

        // 2.查询EOP订单主表group_id
        IDataset subscribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if (IDataUtil.isEmpty(subscribeDataset)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + ibsysId + "未查询到流程订单主表【TF_B_EOP_SUBSCRIBE】信息！");

        }
        IData subData = subscribeDataset.getData(0);

        // 3.查询集团客服信息
        String groupId = subData.getString("GROUP_ID");
        IData grpInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据GROUP_ID=" + groupId + "未查询集团信息！");
        }
        repMap.put("GROUP_ID", groupId);

        // 4.查询EOP申请时的SUB_IBSYSID
        String nodeId = "apply";
        String brandCode = UProductInfoQry.getBrandCodeByProductId(mainProdcutId);
        if ("BOSG".equals(brandCode)) // 如果是BBOSS产品
        {
            nodeId = EcEsopConstants.BBOSS_NODE_APPLY;
        }
        // 5.降序查询轨迹表信息
        IDataset nodeTraSet = EweNodeTraQry.qryEweNodeTraByBusiformIdAndNodeId(busiformId, nodeId);
        if (IDataUtil.isEmpty(nodeTraSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据busiformId=" + busiformId + "查询不到流程轨迹表【TF_B_EWE_NODE_TRA】信息！");
        }
        IData nodeTraData = nodeTraSet.getData(0);
        subIbsysid = nodeTraData.getString("SUB_BI_SN");
        repMap.put("SUB_IDSYSID", subIbsysid);

    }

    @Override
    public void queryOfferInfo() throws Exception {
        IDataset eopProudctList = WorkformProductBean.qryProductByIbsysid(ibsysId);
        if (IDataUtil.isNotEmpty(eopProudctList)) {
            for (int i = 0, iSize = eopProudctList.size(); i < iSize; i++) {
                /****************************************************************************************/
                // 1.查询产品
                IData data = eopProudctList.getData(i);
                String productId = data.getString("PRODUCT_ID");
                String recordNum = data.getString("RECORD_NUM");
                String subIbsysId_RNum = subIbsysid + "_" + recordNum;
                offers.put("OFFER_CODE", productId);
                offers.put("OFFER_ID", UProductInfoQry.getOfferIdByProductId(productId));
                offers.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId));
                offers.put("OFFER_NAME", data.getString("PRODUCT_NAME"));
                offers.put("OPER_CODE", repMap.getString("OPER_CODE"));
                offers.put("OFFER_INDEX", recordNum);
                offers.put(EcConstants.SUBIBID_RNUM, subIbsysId_RNum);// 产品受理时，需要登记到CRM-PRODUCT表RSRV_STR4，用于CRM与ESOP产品对应关系
                offers.put("START_DATE", SysDateMgr.getSysTime());
                offers.put("END_DATE", SysDateMgr.getTheLastTime());
                offers.put("USER_ID", data.getString("USER_ID"));
                // 1.1 设置产品参数
                IDataset offerChaSpecs = IDealEospUtil.qryAttrTranOfferChaSpecs(repMap.getString("SUB_IDSYSID"), recordNum);
                if (IDataUtil.isNotEmpty(offerChaSpecs)) {
                    offers.put("OFFER_CHA_SPECS", offerChaSpecs);
                }

                /****************************************************************************************/
                // 2.查询产品资费
                // todo
                // 2.1设置资费参数
                /****************************************************************************************/
                // 3.查询服务
                // todo
                // 3.设置服务参数
                /****************************************************************************************/
            }

        }
    }

    @Override
    public void queryContractInfo() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * 处理esop数据给外系统返回结果
     * 
     * @return
     * @throws Exception
     */
    private IDataset sendEosData() throws Exception {
        IDataset eopDataset = new DatasetList();
        IData eopData = new DataMap();
        eopData.put("OFFER_DATA", offers);
        eopData.put("CONTRACT_INFO", contrInfo);
        eopData.put("COMMON_DATA", repMap);
        eopDataset.add(eopData);
        return eopDataset;
    }
}
