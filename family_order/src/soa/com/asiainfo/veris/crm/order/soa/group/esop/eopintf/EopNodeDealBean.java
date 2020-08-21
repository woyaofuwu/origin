package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import org.apache.log4j.Logger;

import com.ailk.bizservice.query.product.UProductInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IDealEospUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweBusiSpecReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformNodeBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class EopNodeDealBean extends EopIntfBaseBean {
    private static Logger logger = Logger.getLogger(EopNodeDealBean.class);

    public IData saveEopNode(IData param) throws Exception {
        IData result = new DataMap();

        String ibsysid = getMandaData(param, "IBSYSID");

        IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);// 订单资料
        if (IDataUtil.isEmpty(subScribeDataset)) {// 若业务订单不存在，则保存节点信息失败
            result.put("X_RESULTCODE", FAIL_CODE);// 保存失败，返回code为-1
            result.put("X_RESULTINFO", NOT_EXIST_ORDER_IBSYSID[1] + ibsysid);
            return result;
        }

        IData eopNodeData = buildEopNodeAddData(subScribeDataset.first(), param);
        String proudctId = eopNodeData.getString("PRODUCT_ID");
        if (StringUtils.isNotBlank(proudctId)) {
            if ("VP9983".equals(proudctId))
            {   // VP9983移动400业务产品，商品编码为9983
                proudctId = "9983";
            }

            if("VP998001".equals(proudctId) || "VP66666".equals(proudctId) || "VP99999".equals(proudctId) )
            {
                // 中小企业快速办理  , 商品编码为虚拟编码， 需要入参传入
                proudctId = param.getString("PRODUCT_ID",proudctId);
            }

            String bandCode = UProductInfoQry.getBrandCodeByProductId(proudctId);
            if ("BOSG".equals(bandCode)) {
                WorkformNodeBean.insertWorkformNode(eopNodeData); // 保存工单资料
            }
        }
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        if(!"3086".equals(tradeTypeCode) && !"3016".equals(tradeTypeCode)&& !"3849".equals(tradeTypeCode)&& !"3884".equals(tradeTypeCode)&& !"3034".equals(tradeTypeCode) &&!"3834".equals(tradeTypeCode) &&!"600".equals(tradeTypeCode)) {
            saveEopProduct(param);
        }
        if ("3884".equals(tradeTypeCode)||"3034".equals(tradeTypeCode) || "3834".equals(tradeTypeCode) || "600".equals(tradeTypeCode)) {
        	updEopProduct(param);//中小企业快速办理回写tf_b_eop_product_ext表
		}
        
        if ("3086".equals(tradeTypeCode) || "3016".equals(tradeTypeCode) || "3092".equals(tradeTypeCode) || "3095".equals(tradeTypeCode) || "3089".equals(tradeTypeCode) || "3018".equals(tradeTypeCode)||"4200".equals(tradeTypeCode) || "4201".equals(tradeTypeCode)|| "3849".equals(tradeTypeCode)) {
            updEopProductSub(param);
        }

        // 如果存在子流程，BUSIFORM_ID取子流程BUSIFORM_ID
        IDataset releInfos = WorkformReleBean.qryReleByIbsysidRecordnum(ibsysid, param.getString("RECORD_NUM"), "0");

        String bpmTempletId = subScribeDataset.first().getString("BPM_TEMPLET_ID");

        if(IDataUtil.isNotEmpty(releInfos) && !("CREDITDIRECTLINECONTINUE".equals(bpmTempletId) || "CREDITDIRECTLINEPARSE".equals(bpmTempletId))) {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务--LZ子流程>>>>>>>>>>>>>>>>>" + releInfos.toString());

            /*
            *  存在多级子流程时，rele 表存在多条数据，需要入参中 带BUSIFORM_ID进行比对
            * */
            if(releInfos.size() > 1)
            {
                for(int i = 0 ;i<releInfos.size() ; i++)
                {
                    IData releData = releInfos.getData(i);
                    String busiformId = releData.getString("SUB_BUSIFORM_ID");
                    if(busiformId.equals(param.getString("BUSIFORM_ID")))
                    {
                        result.put("BUSIFORM_ID",busiformId);
                        break;
                    }
                }
            }
            else
            {
                result.put("BUSIFORM_ID", releInfos.first().getString("SUB_BUSIFORM_ID"));
            }
        } else {
            IDataset eweNodeList = EweNodeQry.qryEweNodeByIbsysidState(eopNodeData.getString("IBSYSID", ""), "0");
            if (IDataUtil.isNotEmpty(eweNodeList)) {
                result.put("BUSIFORM_ID", eweNodeList.first().getString("BUSIFORM_ID", ""));
            }
        }

        if("CREDITDIRECTLINECONTINUE".equals(subScribeDataset.first().getString("BPM_TEMPLET_ID")) || "CREDITDIRECTLINEPARSE".equals(subScribeDataset.first().getString("BPM_TEMPLET_ID"))) {
            String nodeId = eopNodeData.getString("NODE_ID", "");
            IData input = new DataMap();
            input.put("IBSYSID", ibsysid);
            input.put("NODE_ID", nodeId);
            IDataset attrList = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(input);
            if(IDataUtil.isNotEmpty(attrList)) {
                String subIbsysid = attrList.first().getString("SUB_IBSYSID");
                result.put("SUB_IBSYSID", subIbsysid);
            }
        }

        // result.put("SUB_IBSYSID", eopNodeData.getString("SUB_IBSYSID", ""));
        result.put("IBSYSID", eopNodeData.getString("IBSYSID", ""));
        result.put("NODE_ID", eopNodeData.getString("NODE_ID", ""));
        result.put("X_RESULTCODE", SUCCESS_CODE);// 保存成功，返回code为0
        result.put("X_RESULTINFO", SUCCESS_INFO);
        return result;
    }

    public IData saveEopSubTradeAndDrive(IData param) throws Exception {
        IData result = new DataMap();

        String ibsysid = getMandaData(param, "IBSYSID");

        IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);// 订单资料
        if (IDataUtil.isEmpty(subScribeDataset)) {// 若业务订单不存在，则保存节点信息失败
            result.put("X_RESULTCODE", FAIL_CODE);// 保存失败，返回code为-1
            result.put("X_RESULTINFO", NOT_EXIST_ORDER_IBSYSID[1] + ibsysid);
            return result;
        }

        IData eopNodeData = buildEopNodeAddData(subScribeDataset.first(), param);
        String proudctId = eopNodeData.getString("PRODUCT_ID");
        if (StringUtils.isNotBlank(proudctId)) {
            String bandCode = UProductInfoQry.getBrandCodeByProductId(proudctId);
            if ("BOSG".equals(bandCode)) {
                WorkformNodeBean.insertWorkformNode(eopNodeData); // 保存工单资料
            }
        }

        updEopProductSub(param);

        // 如果存在子流程，BUSIFORM_ID取子流程BUSIFORM_ID
        IDataset releInfos = WorkformReleBean.qryReleByIbsysidRecordnum(ibsysid, param.getString("RECORD_NUM"), "0");
        if (IDataUtil.isNotEmpty(releInfos)) {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务--LZ子流程>>>>>>>>>>>>>>>>>" + releInfos.toString());

            result.put("BUSIFORM_ID", releInfos.first().getString("SUB_BUSIFORM_ID"));
        } else {
            IDataset eweNodeList = EweNodeQry.qryEweNodeByIbsysidState(eopNodeData.getString("IBSYSID", ""), "0");
            if (IDataUtil.isNotEmpty(eweNodeList)) {
                result.put("BUSIFORM_ID", eweNodeList.first().getString("BUSIFORM_ID", ""));
            }
        }

        // result.put("SUB_IBSYSID", eopNodeData.getString("SUB_IBSYSID", ""));
        result.put("IBSYSID", eopNodeData.getString("IBSYSID", ""));
        result.put("NODE_ID", eopNodeData.getString("NODE_ID", ""));
        result.put("X_RESULTCODE", SUCCESS_CODE);// 保存成功，返回code为0
        result.put("X_RESULTINFO", SUCCESS_INFO);
        return result;
    }

    public IData saveEopSubAndDrive(IData param) throws Exception {
        IData result = new DataMap();

        String ibsysid = getMandaData(param, "IBSYSID");

        IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);// 订单资料
        if (IDataUtil.isEmpty(subScribeDataset)) {// 若业务订单不存在，则保存节点信息失败
            result.put("X_RESULTCODE", FAIL_CODE);// 保存失败，返回code为-1
            result.put("X_RESULTINFO", NOT_EXIST_ORDER_IBSYSID[1] + ibsysid);
            return result;
        }

        IData eopNodeData = buildEopNodeAddData(subScribeDataset.first(), param);
        String proudctId = eopNodeData.getString("PRODUCT_ID");
        if (StringUtils.isNotBlank(proudctId)) {
            String bandCode = UProductInfoQry.getBrandCodeByProductId(proudctId);
            if ("BOSG".equals(bandCode)) {
                WorkformNodeBean.insertWorkformNode(eopNodeData); // 保存工单资料
            }
        }
        // saveEopProduct(param);
        updTradeIdByIbsysid(param);

        // 如果存在子流程，BUSIFORM_ID取子流程BUSIFORM_ID
        IDataset releInfos = WorkformReleBean.qryReleByIbsysidRecordnum(ibsysid, param.getString("RECORD_NUM"), "0");
        if (IDataUtil.isNotEmpty(releInfos)) {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务--LZ子流程>>>>>>>>>>>>>>>>>" + releInfos.toString());

            result.put("BUSIFORM_ID", releInfos.first().getString("SUB_BUSIFORM_ID"));
        } else {
            IDataset eweNodeList = EweNodeQry.qryEweNodeByIbsysidState(eopNodeData.getString("IBSYSID", ""), "0");
            if (IDataUtil.isNotEmpty(eweNodeList)) {
                result.put("BUSIFORM_ID", eweNodeList.first().getString("BUSIFORM_ID", ""));
            }
        }

        // result.put("SUB_IBSYSID", eopNodeData.getString("SUB_IBSYSID", ""));
        result.put("IBSYSID", eopNodeData.getString("IBSYSID", ""));
        result.put("NODE_ID", eopNodeData.getString("NODE_ID", ""));
        result.put("X_RESULTCODE", SUCCESS_CODE);// 保存成功，返回code为0
        result.put("X_RESULTINFO", SUCCESS_INFO);
        return result;
    }

    public void saveEopProduct(IData param) throws Exception {
        IDataset updList = new DatasetList();
        String ibsysid = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM", "0");
        IData eopProductData = WorkformProductBean.qryProductByPk(ibsysid, recordNum);
        if (IDataUtil.isNotEmpty(eopProductData)) {
            updList.add(buildEopProductUpdData(eopProductData, param));
        }

        IDataset subUseridRnumList = param.getDataset(EcConstants.SUB_USERID_RNUM);
        if (IDataUtil.isNotEmpty(subUseridRnumList)) {
            IDataset eopProductList = WorkformProductBean.qryProductByIbsysid(ibsysid);
            if (IDataUtil.isNotEmpty(eopProductList)) {
                for (int j = 0, sizeJ = subUseridRnumList.size(); j < sizeJ; j++) {
                    IData subUseridRnumData = subUseridRnumList.getData(j);
                    String subRecordNum = subUseridRnumData.getString("RECORD_NUM");
                    for (int i = 0, sizeI = eopProductList.size(); i < sizeI; i++) {
                        if (subRecordNum.equals(eopProductList.getData(i).getString("RECORD_NUM"))) {
                            updList.add(buildEopProductUpdData(eopProductList.getData(i), subUseridRnumData));
                            break;
                        }
                    }
                }
            }
        }

        StringBuilder sql = new StringBuilder(500);
        sql.append("UPDATE TF_B_EOP_PRODUCT SET TRADE_ID = :TRADE_ID ");
        if (param.containsKey("BATCH_ID")) {
            sql.append(" ,BATCH_ID = :BATCH_ID ");
        }
        if (param.containsKey("USER_ID")) {
            sql.append(" ,USER_ID = :USER_ID ");
        }
        if (param.containsKey("SERIAL_NUMBER")) {
            sql.append(" ,SERIAL_NUMBER = :SERIAL_NUMBER ");
        }
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND RECORD_NUM = :RECORD_NUM ");
        Dao.executeBatch(sql, updList, Route.getJourDb(Route.CONN_CRM_CG));
    }

    private IData buildEopNodeAddData(IData eopSubscriber, IData param) throws Exception {
        String sysTime = SysDateMgr.getSysTime();
        String staffId = param.getString("STAFF_ID", CSBizBean.getVisit().getStaffId());
        String staffPhone = StaffInfoQry.getStaffSnByStaffId(staffId);
        IData addData = new DataMap();

        addData.put("SUB_IBSYSID", SeqMgr.getSubIbsysId());
        addData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        addData.put("IBSYSID", eopSubscriber.getString("IBSYSID"));// 业务流水号,必填
        addData.put("NODE_ID", getMandaData(param, "NODE_ID"));
        addData.put("PRODUCT_ID", eopSubscriber.getString("BUSI_CODE"));
        addData.put("ORDER_ID", param.getString("ORDER_ID"));
        addData.put("DEAL_STATE", eopSubscriber.getString("DEAL_STATE"));
        addData.put("DEAL_OPTION", param.getString("DEAL_OPTION"));
        addData.put("DEAL_SRC", param.getString("DEAL_SRC"));
        addData.put("DEAL_DESC", param.getString("DEAL_DESC"));
        addData.put("DEAL_TIME", sysTime);
        addData.put("TRANSFER_PARAM", IDealEospUtil.getTransferParam(param));
        addData.put("ROLE_ID", param.getString("DEAL_DESC"));
        addData.put("WORKFORM_TYPE", "2");// 工单类型
        addData.put("INSERT_TIME", sysTime);
        addData.put("CITY_CODE", param.getString("CITY_CODE", CSBizBean.getVisit().getCityCode()));
        addData.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", CSBizBean.getVisit().getLoginEparchyCode()));
        addData.put("DEPART_ID", param.getString("DEPART_ID", CSBizBean.getVisit().getDepartId()));
        addData.put("DEPART_NAME", param.getString("DEPART_NAME", CSBizBean.getVisit().getDepartName()));
        addData.put("STAFF_NAME", param.getString("STAFF_NAME", CSBizBean.getVisit().getStaffName()));
        addData.put("STAFF_ID", staffId);
        addData.put("STAFF_PHONE", staffPhone);
        addData.put("REMARK", "");
        addData.put("RSRV_STR1", param.getString("MAIN_TEMPLET_ID", ""));// 子流程时，为该子流程所在的父节点
        if ("BBOSS".equals(eopSubscriber.getString("BPM_TEMPLET_ID"))) {
            addData.put("RSRV_STR2", param.getString("BPM_TEMPLET_ID"));// 子流程时，子流程模板
        } else {
            addData.put("RSRV_STR2", eopSubscriber.getString("BPM_TEMPLET_ID"));// 子流程时，子流程模板
        }
        addData.put("RSRV_STR3", param.getString("RSRV_STR3", ""));
        addData.put("RSRV_STR4", param.getString("RSRV_STR3", ""));
        addData.put("RSRV_STR5", param.getString("RSRV_STR3", ""));
        addData.put("RSRV_STR6", param.getString("RSRV_STR3", ""));
        addData.put("RSRV_STR7", param.getString("RSRV_STR3", ""));

        return addData;
    }

    private IData buildEopProductUpdData(IData eopProduct, IData data) throws Exception {
        IData updData = new DataMap();

        updData.put("TRADE_ID", data.getString("TRADE_ID"));
        updData.put("USER_ID", data.getString("USER_ID"));
        updData.put("IBSYSID", eopProduct.getString("IBSYSID"));
        updData.put("RECORD_NUM", eopProduct.getString("RECORD_NUM"));
        if (data.containsKey("BATCH_ID")) {
            updData.put("BATCH_ID", data.getString("BATCH_ID"));
        }
        if (data.containsKey("SERIAL_NUMBER")) {
            updData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        }

        return updData;
    }

    /**
     * BBOSS管理流程,调用用ESOP
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData saveEosData(IData inparam) throws Exception {
        String ibsysId = SeqMgr.getIbsysId();
        String subIbsysId = SeqMgr.getSubIbsysId();

        String mainIdsysId = inparam.getString("MAIN_IBSYSID");
        String bpmTempletId = inparam.getString("SUB_BPM_TEMPLET_ID");
        String nodeId = inparam.getString("SUB_NODE_ID");
        IDataset eweBusiSpecRele = EweBusiSpecReleInfoQry.qryInfoByBpmTempletId(bpmTempletId, EcEsopConstants.STATE_VALID);
        if (IDataUtil.isEmpty(eweBusiSpecRele)) {
            return null;
        }
        String operType = eweBusiSpecRele.getData(0).getString("BUSIFORM_OPER_TYPE");
        String busiType = eweBusiSpecRele.getData(0).getString("BUSI_TYPE");
        String busiCode = eweBusiSpecRele.getData(0).getString("BUSI_CODE");

        IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(mainIdsysId);// 订单资料
        if (IDataUtil.isNotEmpty(subScribeDataset)) {
            IData workformSubscribe = subScribeDataset.getData(0);
            busiCode = workformSubscribe.getString("BUSI_CODE");
            workformSubscribe.put("IBSYSID", ibsysId);
            workformSubscribe.put("BPM_TEMPLET_ID", bpmTempletId);
            workformSubscribe.put("BUSI_TYPE", busiType);
            workformSubscribe.put("RSRV_STR1", mainIdsysId);
            workformSubscribe.put("RSRV_STR2", workformSubscribe.getString("BPM_TEMPLET_ID"));
            // 设置公共参数
            IDealEospUtil.setCommonInfos(workformSubscribe);
            WorkformSubscribeBean.insertWorkformSubscribe(workformSubscribe);
        }

        IDataset eopNodeList = WorkformNodeBean.qryWorkformNodeByIbsysid(mainIdsysId);
        if (IDataUtil.isNotEmpty(eopNodeList)) {
            IData eopNodedData = eopNodeList.getData(0);
            eopNodedData.put("SUB_IBSYSID", subIbsysId);
            eopNodedData.put("IBSYSID", ibsysId);
            eopNodedData.put("NODE_ID", nodeId);
            // 设置公共参数
            IDealEospUtil.setCommonInfos(eopNodedData);
            WorkformNodeBean.insertWorkformNode(eopNodedData);
        }
        IData productInfos = WorkformProductBean.qryProductByPk(mainIdsysId, "0");
        if (IDataUtil.isNotEmpty(productInfos)) {
            productInfos.put("IBSYSID", ibsysId);
            // 设置公共参数
            IDealEospUtil.setCommonInfos(productInfos);
            WorkformProductBean.insertWorkformProduct(productInfos);
        }
        inparam.put("IBSYSID", ibsysId);
        inparam.put("SUB_IBSYSID", subIbsysId);
        inparam.put("BPM_TEMPLET_ID", bpmTempletId);
        inparam.put("OPER_TYPE", operType);
        inparam.put("BUSI_CODE", busiCode);
        return inparam;
    }

    public void updEopProductSub(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        String tradId = param.getString("TRADE_ID");
        String userId = param.getString("USER_ID");
        String serialNumber = param.getString("SERIAL_NUMBER");
        String recordNum = param.getString("RECORD_NUM");
        WorkformProductSubBean.updByIbsysid(ibsysid, tradId, userId, serialNumber, recordNum);
    }
    
    public void updEopProduct(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        String tradId = param.getString("TRADE_ID");
        String userId = param.getString("USER_ID");
        String recordNum = param.getString("RECORD_NUM");
        WorkformProductSubBean.updByIbsysidRecordnum(ibsysid, tradId, userId,recordNum);
    }
    
    public void updTradeIdByIbsysid(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        String tradId = param.getString("TRADE_ID");
        String recordNum = param.getString("RECORD_NUM");
        WorkformProductSubBean.updTradeIdByIbsysid(ibsysid, tradId, recordNum);
    }

    // public IData saveEopNode11(IData param) throws Exception
    // {
    // IData result = new DataMap();
    //
    // String operCode = getMandaData(param, "OPER_CODE");//接入方式 01:NGBOSS页面提交 02：BBOSS业务落地信息
    // String productNOs = param.getString("PRODUCTNOS");
    //
    // if(!NGBOSS_PAGE_SUBMIT.equals(operCode) && !BBOSS_COMEDOWN.equals(operCode) && !MESOP_PAGE_SUBMIT.equals(operCode))
    // {
    // result.put("X_RESULTCODE", FAIL_CODE);//失败，返回code为0
    // result.put("X_RESULTINFO", "[OPER_CODE="+operCode+"]接入方式不存在");
    // return result;
    // }
    //
    // IData subscribeData = null;
    // String ibsysid = null;
    // boolean is_new = false;
    // //如果接入方式为NGBOSS页面提交，或是BBOSS业务落地信息且传递了IBSYSID，查询订单，否则新增一笔订单
    // if (NGBOSS_PAGE_SUBMIT.equals(operCode) || ((BBOSS_COMEDOWN.equals(operCode) || MESOP_PAGE_SUBMIT.equals(operCode)) && !"".equals(param.getString("IBSYSID", ""))))
    // {
    // ibsysid = getMandaData(param, "IBSYSID");// 业务流水号
    // IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);// 订单资料
    // if (IDataUtil.isEmpty(subScribeDataset))
    // {// 若业务订单不存在，则保存节点信息失败
    // result.put("X_RESULTCODE", FAIL_CODE);// 保存失败，返回code为-1
    // result.put("X_RESULTINFO", NOT_EXIST_ORDER_IBSYSID[1] + ibsysid);
    // return result;
    // }
    // subscribeData = subScribeDataset.first();
    // }
    // else if (BBOSS_COMEDOWN.equals(operCode))
    // {
    // // subscribeData = saveBBOSSSubscribe(pd, dbBean, data);
    // is_new = true;
    // }
    // else if (MESOP_PAGE_SUBMIT.equals(operCode))
    // {
    // // subscribeData = saveMESOPSubscribe(pd, dbBean, data);
    // is_new = true;
    // }
    //
    // IData workFormData = getWorkFormData(subscribeData, param);
    // //新建流程第一个节点的SUB_IBSYSID跟IBSYSID一致
    // if(is_new)
    // {
    // workFormData.put("SUB_IBSYSID", subscribeData.getString("IBSYSID"));
    // workFormData.put("is_new", "true");
    // }
    //
    // // saveEopSubscriber(workFormData, operCode); //保存工单资料
    //
    // saveEopAttr(workFormData, param);
    //
    // String tradeId = param.getString("TRADE_ID", "");
    // String userIdA = param.getString("USER_ID_A", "");
    // if("2".equals(workFormData.getString("DEAL_STATE", "2")) && (StringUtils.isNotBlank(tradeId)||StringUtils.isNotBlank(userIdA)))
    // {
    // IData updSubscriberData = new DataMap();//要更新的订单资料
    // updSubscriberData.put("IBSYSID", subscribeData.getString("IBSYSID"));//业务流水号，根据该栏位更新订单表
    // updSubscriberData.put("USER_ID", userIdA);//用户编码
    //
    // WorkformSubscribeBean.updateWorkformSubscriber(updSubscriberData);
    //
    // }
    //
    // //如果为MESOP调用过来，并且DEAL_STATE为2，调用归档接口
    // if (MESOP_PAGE_SUBMIT.equals(operCode) && "2".equals(workFormData.getString("DEAL_STATE", "0")))
    // {
    // IData datavalue = new DataMap();
    // datavalue.put("SUBSCRIBE_ID", getMandaData(param, "IBSYSID"));
    // datavalue.put("X_GET_MODE", "0");
    // // TcsEosInfo.subScribeStateDeal(pd, datavalue);
    // }
    // return null;
    // }
    //
    // private IData getWorkFormData(IData subscribeData, IData param) throws Exception
    // {
    // String sysTime = SysDateMgr.getSysTime();
    // String staffPhone = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", getMandaData(param, "STAFF_ID"));
    //
    // IData workFormData = new DataMap();
    // workFormData.put("IBSYSID", subscribeData.getString("IBSYSID"));// 业务流水号,必填
    // workFormData.put("FLOWID", subscribeData.getString("FLOWID"));// 电子工单号
    // workFormData.put("DEAL_STATE", param.getString("DEAL_STATE", "2"));// 操作状态 0-未受理;1-受理中;2-受理完成;3-撤销流程;4-驳回;S-暂停
    // workFormData.put("PRODUCT_ID", subscribeData.getString("PRODUCT_ID"));// 产品ID
    //
    // workFormData.put("USER_ID", param.getString("USER_ID", ""));// 用户编码
    // workFormData.put("ORDER_ID", param.getString("ORDER_ID", ""));// CRM台账编码
    // workFormData.put("NODE_ID", getMandaData(param, "NODE_ID"));// 当前节点ID
    // // if (NGBOSS_PAGE_SUBMIT.equals(getMandaData(param, "OPER_CODE")))
    // // { // OPER_CODE为01:NGBOSS页面提交时，TRADE_ID必填
    // // workFormData.put("ORDER_ID", getMandaData(param, "ORDER_ID"));// CRM台账编码
    // // workFormData.put("NODE_ID", param.getString("NODE_ID", ""));// 当前节点ID
    // // }
    // // else if (BBOSS_COMEDOWN.equals(getMandaData(param, "OPER_CODE")))
    // // {
    // // workFormData.put("ORDER_ID", param.getString("ORDER_ID", ""));// CRM台账编码
    // // workFormData.put("NODE_ID", "BBOSS落地");// 当前节点ID
    // // }
    // // else if (MESOP_PAGE_SUBMIT.equals(getMandaData(param, "OPER_CODE")))
    // // {
    // // workFormData.put("NODE_ID", getMandaData(param, "NODE_ID"));// 当前节点ID
    // // }
    // workFormData.put("DEAL_OPTION", param.getString("DEAL_OPTION", ""));// 处理选项
    // workFormData.put("DEAL_SRC", param.getString("DEAL_SRC", ""));// 处理来源
    // workFormData.put("DEAL_DESC", param.getString("DEAL_DESC", ""));// 处理描述
    // workFormData.put("DEAL_TIME", sysTime);// 处理时间
    // workFormData.put("ROLE_ID", param.getString("ROLE_ID", ""));// 角色ID
    // workFormData.put("INSERT_TIME", sysTime);// 插入时间
    // workFormData.put("CITY_CODE", param.getString("CITY_CODE"));// 受理城市编码
    // workFormData.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));// 受理地州编码
    // workFormData.put("DEPART_ID", param.getString("DEPART_ID"));// 受理部门编号
    // workFormData.put("DEPART_NAME", param.getString("DEPART_NAME"));// 受理部门名称
    // workFormData.put("STAFF_ID", param.getString("STAFF_ID"));// 受理员工编号
    // workFormData.put("STAFF_NAME", param.getString("STAFF_NAME"));// 受理员工名称
    // workFormData.put("MEB_USER_ID", param.getString("MEB_USER_ID"));// 成员ID
    // workFormData.put("MEB_TRADE_ID", param.getString("MEB_TRADE_ID"));// 成员台账ID
    // workFormData.put("STAFF_PHONE", staffPhone);// 受理员工电话
    // workFormData.put("REMARK", param.getString("REMARK", ""));// 备注
    // workFormData.put("WORKFORM_TYPE", "2");// 工单类型
    // workFormData.put("RSRV_STR1", param.getString("MAIN_TEMPLET_ID", ""));// 子流程时，为该子流程所在的父节点
    //
    // if ("BBOSS".equals(subscribeData.getString("BPM_TEMPLET_ID")))
    // {
    // workFormData.put("RSRV_STR2", param.getString("BPM_TEMPLET_ID"));// 子流程时，子流程模板
    // }
    // else
    // {
    // workFormData.put("RSRV_STR2", subscribeData.getString("BPM_TEMPLET_ID"));// 子流程时，子流程模板
    // }
    //
    // workFormData.put("RSRV_STR3", param.getString("RSRV_STR3", ""));// 预留串3
    // workFormData.put("RSRV_STR4", param.getString("RSRV_STR4", ""));// 预留串4
    // workFormData.put("RSRV_STR5", param.getString("RSRV_STR5", ""));// 预留串5
    // workFormData.put("RSRV_STR6", param.getString("RSRV_STR6", ""));// 预留串6
    // workFormData.put("RSRV_STR7", param.getString("RSRV_STR7", ""));// 预留串7
    //
    // workFormData.put("TRANSFER_PARAM", IDealEospUtil.getTransferParam(param));// 页面跳转传递参数,必填
    // return workFormData;
    // }
    //
    // private void saveEopAttr(IData workFormData, IData param) throws Exception
    // {
    // IDataset eopAttrList = new DatasetList();
    // int seq = IDealEospUtil.getSeq("TF_B_EOP_ATTR", new String[]{"IBSYSID"},
    // new String[]{workFormData.getString("IBSYSID")}, Route.getJourDb(BizRoute.getRouteId()));
    //
    // IDataset bbossSubUserIdList = param.getDataset("BBOSS_SUB_USERID_RNUM");
    //
    // String execMonth = SysDateMgr.getCurMonth();
    // String sysTime = SysDateMgr.getSysTime();
    // IData eopAttr = null;
    // if(IDataUtil.isNotEmpty(bbossSubUserIdList))
    // {
    // for(int i = 0, size = bbossSubUserIdList.size(); i < size; i++)
    // {
    // String subibidRnum = bbossSubUserIdList.getData(i).getString("SUBIBID_RNUM");
    // if(StringUtils.isNotBlank(subibidRnum) && subibidRnum.contains("_"))
    // {
    // eopAttr = new DataMap();
    // dealComonEopAttr(eopAttr, workFormData, ++seq, execMonth, sysTime);
    // String[] subibidRnumArr = subibidRnum.split("_");
    // eopAttr.put("SUB_IBSYSID", subibidRnumArr[0]);
    // eopAttr.put("RECORD_NUM", subibidRnumArr[1]);
    // eopAttr.put("ATTR_CODE", "USER_ID_B");//属性编号
    // eopAttr.put("ATTR_VALUE", bbossSubUserIdList.getData(i).getString("SUB_USER_ID"));//属性值
    // eopAttrList.add(eopAttr);
    // }
    // }
    // }
    //
    // String tradeId = param.getString("TRADE_ID"); //NGBOSS-CRM主台账编码
    // if(StringUtils.isNotBlank(tradeId))
    // {
    // eopAttr = new DataMap();
    // dealComonEopAttr(eopAttr, workFormData, ++seq, execMonth, sysTime);
    // eopAttr.put("ATTR_CODE", "TRADE_ID");
    // eopAttr.put("ATTR_VALUE", tradeId);
    // eopAttr.put("ATTR_NAME", "NGBOSS-CRM主台账编码");
    // eopAttrList.add(eopAttr);
    // }
    //
    // String userId = param.getString("USER_ID"); //集团主台账中的USER_ID
    // if(StringUtils.isNotBlank(userId))
    // {
    // eopAttr = new DataMap();
    // dealComonEopAttr(eopAttr, workFormData, ++seq, execMonth, sysTime);
    // eopAttr.put("ATTR_CODE", "USER_ID");
    // eopAttr.put("ATTR_VALUE", tradeId);
    // eopAttr.put("ATTR_NAME", "集团主台账中的USER_ID");
    // eopAttrList.add(eopAttr);
    // }
    //
    // //PARAMS多层结构 eg: [[{PARAM_CODE=["ROUTE_APPROVEII"], PARAM_NAME=["下一环节"], PARAM_VALUE=[""]}, {PARAM_CODE=["AUDIT_OPTION"], PARAM_NAME=["是否同意"], PARAM_VALUE=[""]}],[{PARAM_CODE=["ROUTE_APPROVEII"], PARAM_NAME=["下一环节"], PARAM_VALUE=[""]},
    // {PARAM_CODE=["AUDIT_OPTION"], PARAM_NAME=["是否同意"], PARAM_VALUE=[""]}]]
    // IDataset params = param.getDataset("PARAMS");
    // if(IDataUtil.isNotEmpty(params))
    // {
    // for(int i = 0, sizeI = params.size(); i < sizeI; i++)
    // {
    // IDataset tmpDs = params.getDataset(i);
    // if(IDataUtil.isEmpty(tmpDs))
    // {
    // continue;
    // }
    // for(int j = 0, sizeJ = tmpDs.size(); j < sizeJ; j++)
    // {
    // IData tmp = tmpDs.getData(j);
    // eopAttr = new DataMap();
    // dealComonEopAttr(eopAttr, workFormData, ++seq, execMonth, sysTime);
    // eopAttr.put("RECORD_NUM", tmp.getString("RECORD_NUM", String.valueOf(i)));//如果PARAMS里没有RECORD_NUM，就取i
    // eopAttr.put("ATTR_CODE", tmp.getString("PARAM_CODE"));//属性编号
    // eopAttr.put("ATTR_VALUE", tmp.getString("PARAM_VALUE"));//属性值
    // eopAttr.put("ATTR_NAME", tmp.getString("PARAM_NAME"));//属性值
    // eopAttrList.add(eopAttr);
    // }
    // }
    // }
    // if(IDataUtil.isNotEmpty(eopAttrList))
    // {
    // WorkformAttrBean.insertWorkformAttr(eopAttrList);
    // }
    // }
    //
    // /**
    // * 节点工单信息表
    // * @param dbBean
    // * @param operCode
    // * @throws Exception
    // */
    // private void saveWorkformSubscriber(IData workFormData, String operCode) throws Exception
    // {
    // String ibsysid = workFormData.getString("IBSYSID");
    // String nodeId = workFormData.getString("NODE_ID");
    // IDataset eopNodeSet = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibsysid, nodeId);
    //
    // /* 如果状态ID(DEAL_STATE)为0，则说明是NGBOSS在初始化页面的时候，调用此接口进行保存一些资料， DEAL_STATE为2 即是NGBOSS页面提交资料时，会再次调用此接口，此时会将该笔资料的TRADE_ID更新为2，如果根据IBSYSID+NODE_ID没有查到资料，则直接新增一笔资料 BBOSS的落地不判断直接新增 */
    // String dealState = workFormData.getString("DEAL_STATE", "2");
    // if ("0".equals(dealState) || IDataUtil.isEmpty(eopNodeSet) || BBOSS_COMEDOWN.equals(operCode) || "dealPage".equals(nodeId))
    // {
    // // 新建流程第一个节点的SUB_IBSYSID跟IBSYSID一致
    // if (!"true".equals(workFormData.getString("is_new", "")))
    // {
    // workFormData.put("SUB_IBSYSID", SeqMgr.getSubIbsysId());// 流水号,主键
    // }
    // String orderId = workFormData.getString("ORDER_ID", "");
    // String userId = workFormData.getString("USER_ID", "");
    // String mebUserId = workFormData.getString("MEB_USER_ID", "");
    // String mebOrderId = workFormData.getString("MEB_ORDER_ID", "");
    // if (StringUtils.isBlank(orderId) && StringUtils.isBlank(userId) && StringUtils.isNotBlank(mebUserId) && StringUtils.isNotBlank(mebOrderId))
    // {
    // workFormData.put("ORDER_ID", mebOrderId);
    // workFormData.put("USER_ID", mebUserId);
    // }
    //
    // StringBuffer temp = new StringBuffer(workFormData.getString("TRANSFER_PARAM", ""));
    // temp.append("&SUB_IBSYSID=");
    // temp.append(workFormData.getString("SUB_IBSYSID"));
    // workFormData.put("TRANSFER_PARAM", temp.toString());
    // // 存储节点工单信息表
    // WorkformNodeBean.insertWorkformNode(workFormData);
    // }
    // else if ("2".equals(dealState))
    // {
    // IData toUpdateWorkFormData = new DataMap();
    // toUpdateWorkFormData.put("IBSYSID", workFormData.getString("IBSYSID", ""));
    // toUpdateWorkFormData.put("NODE_ID", workFormData.getString("NODE_ID", ""));
    // toUpdateWorkFormData.put("ORDER_ID", workFormData.getString("ORDER_ID", ""));
    // toUpdateWorkFormData.put("USER_ID", workFormData.getString("USER_ID", ""));
    // toUpdateWorkFormData.put("DEAL_STATE", dealState);
    // toUpdateWorkFormData.put("INSERT_TIME", workFormData.getString("INSERT_TIME", ""));
    // // 更新节点工单信息表
    // WorkformNodeBean.updataEopNode(toUpdateWorkFormData);
    // }
    // else
    // {// 更新节点工单信息表
    // WorkformNodeBean.updataEopNode(workFormData);
    // }
    // String subIbsysId = workFormData.getString("SUB_IBSYSID", "");
    // if (StringUtils.isBlank(subIbsysId))
    // {
    // if (IDataUtil.isNotEmpty(eopNodeSet))
    // {
    // workFormData.put("SUB_IBSYSID", eopNodeSet.getData(0).getString("SUB_IBSYSID", ""));
    // }
    //
    // }
    // }
    //
    // private IData dealComonEopAttr(IData eopAttr, IData workFormData, int seq, String exec_month, String update_time) throws Exception
    // {
    // eopAttr.put("IBSYSID", workFormData.getString("IBSYSID", ""));//订单编号
    // eopAttr.put("SUB_IBSYSID", workFormData.getString("SUB_IBSYSID", ""));//订单编号
    // eopAttr.put("NODE_ID", workFormData.getString("NODE_ID", ""));//节点ID
    // eopAttr.put("EXEC_MONTH", exec_month);//执行月份，取当前月
    // eopAttr.put("UPDATE_TIME", update_time);//更新时间
    // eopAttr.put("SEQ", seq);//序列号
    // eopAttr.put("RECORD_NUM", "0"); //默认为0
    // return eopAttr;
    // }
    

    public IData saveAttrInfoAndDrive(IData param) throws Exception {
        IData result = new DataMap();

        String ibsysid = getMandaData(param, "IBSYSID");

        IDataset subScribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);// 订单资料
        if (IDataUtil.isEmpty(subScribeDataset)) {// 若业务订单不存在，则保存节点信息失败
            result.put("X_RESULTCODE", FAIL_CODE);// 保存失败，返回code为-1
            result.put("X_RESULTINFO", NOT_EXIST_ORDER_IBSYSID[1] + ibsysid);
            return result;
        }

        IData eopNodeData = buildEopNodeAddData(subScribeDataset.first(), param);
        String proudctId = eopNodeData.getString("PRODUCT_ID");
        if (StringUtils.isNotBlank(proudctId)) {
            if ("VP9983".equals(proudctId)) {// VP9983移动400业务产品，商品编码为9983
                proudctId = "9983";
            }
            String bandCode = UProductInfoQry.getBrandCodeByProductId(proudctId);
            if ("BOSG".equals(bandCode)) {
                WorkformNodeBean.insertWorkformNode(eopNodeData); // 保存工单资料
            }
        }
        param.put("ATTR_CODE", "GRP_SERIAL_NUMBER_B");
        updateAttr(param);
        // 如果存在子流程，BUSIFORM_ID取子流程BUSIFORM_ID
        IDataset releInfos = WorkformReleBean.qryReleByIbsysidRecordnum(ibsysid, param.getString("RECORD_NUM"), "0");
        if(IDataUtil.isNotEmpty(releInfos) && !("CREDITDIRECTLINECONTINUE".equals(subScribeDataset.first().getString("BPM_TEMPLET_ID")) || "CREDITDIRECTLINEPARSE".equals(subScribeDataset.first().getString("BPM_TEMPLET_ID")))) {
            logger.debug(">>>>>>>>>>>>>>>>>进入驱动流程服务--LZ子流程>>>>>>>>>>>>>>>>>" + releInfos.toString());

            result.put("BUSIFORM_ID", releInfos.first().getString("SUB_BUSIFORM_ID"));
        } else {
            IDataset eweNodeList = EweNodeQry.qryEweNodeByIbsysidState(eopNodeData.getString("IBSYSID", ""), "0");
            if (IDataUtil.isNotEmpty(eweNodeList)) {
                result.put("BUSIFORM_ID", eweNodeList.first().getString("BUSIFORM_ID", ""));
            }
        }

        // result.put("SUB_IBSYSID", eopNodeData.getString("SUB_IBSYSID", ""));
        result.put("IBSYSID", eopNodeData.getString("IBSYSID", ""));
        result.put("NODE_ID", eopNodeData.getString("NODE_ID", ""));
        result.put("X_RESULTCODE", SUCCESS_CODE);// 保存成功，返回code为0
        result.put("X_RESULTINFO", SUCCESS_INFO);
        return result;
    }
    
    
    public void updateAttr(IData param) throws Exception {
        IDataset updList = new DatasetList();
        String ibsysid = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM", "0");
        String attrCode =  param.getString("ATTR_CODE");
        String serialNumber =  param.getString("SERIAL_NUMBER");
        IData updData =  new DataMap();
        updData.put("IBSYSID", ibsysid);
        updData.put("RECORD_NUM",recordNum);
        updData.put("ATTR_CODE", attrCode);
        updData.put("SERIAL_NUMBER", serialNumber);
        updList.add(updData);
        StringBuilder sql = new StringBuilder(500);
        sql.append("UPDATE TF_B_EOP_ATTR SET ATTR_VALUE = :SERIAL_NUMBER ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND RECORD_NUM = :RECORD_NUM ");
        sql.append(" AND ATTR_CODE = :ATTR_CODE");
        Dao.executeBatch(sql, updList, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
