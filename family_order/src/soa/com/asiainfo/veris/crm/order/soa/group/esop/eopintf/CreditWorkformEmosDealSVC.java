package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class CreditWorkformEmosDealSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static transient Logger logger = Logger.getLogger(CreditWorkformEmosDealSVC.class);

    public IData dealEmosAttr(IData data) throws Exception {

        String ibsysId = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        String recordNum = data.getString("RECORD_NUM", "0");

        //新增subIbsysId
        String subIbsysId = SeqMgr.getSubIbsysId();

        IDataset attrDatas = new DatasetList();
        //获取订单信息
        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysId + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();
        String bpmTempletId = subscriberData.getString("BPM_TEMPLET_ID");
        //String productId = subscriberData.getString("BUSI_CODE");

        //查询订单产品表信息
        IData productInfo = WorkformProductBean.qryProductByPk(ibsysId, "0");
        if(DataUtils.isEmpty(productInfo)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysId + "未查询到产品表信息！");
        }
        //无成员专线直接取集团userId
        String userId = productInfo.getString("USER_ID");
        String productId = productInfo.getString("PRODUCT_ID");
        String productName = productInfo.getString("PRODUCT_NAME");

        if("7011".equals(productId) || "7012".equals(productId)) {
            //查询订单产品子表信息
            IData productSubInfos = WorkformProductSubBean.qryProductByPk(ibsysId, "1");
            if(DataUtils.isEmpty(productSubInfos)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysId + "未查询到产品子表信息！");
            }
            //有成员专线取成员userId
            userId = productSubInfos.getString("USER_ID");
        }

        //查询集团信息
        //UcaInfoQry.qryGrpInfoByGrpId
        IData custInfo = UcaInfoQry.qryGrpInfoByUserId(userId);
        if(DataUtils.isEmpty(custInfo)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据用户编码【" + userId + "】未获取到集团客户信息！");
        }
        
        /*IData mainProdInfo = UcaInfoQry.qryMainProdInfoByUserId(grpUserId, Route.CONN_CRM_CG);
        if(DataUtils.isEmpty(mainProdInfo)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据集团用户编码【" + grpUserId + "】未获取到集团用户产品信息！");
        }*/
        custInfo.put("PRODUCT_NAME", productName);

        //查询原集团公共信息
        IDataset userAttrList = UserAttrInfoQry.getUserProductAttrByUTForGrp(userId, "P", null);
        if(IDataUtil.isEmpty(userAttrList)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID:[" + userId + "]未查询到原集团公共信息！");
        }
        IData userAttrData = new DataMap();
        for (int i = 0; i < userAttrList.size(); i++) {
            IData userAttr = userAttrList.getData(i);
            userAttrData.put(userAttr.getString("ATTR_CODE"), userAttr.getString("ATTR_VALUE"));
        }
        //添加客户经理信息
        String custMgrId = custInfo.getString("CUST_MANAGER_ID");
        IDataset custMgrDatas = StaffInfoQry.qryCustManagerStaffById(custMgrId);
        IData custMgrData = null;
        if(IDataUtil.isNotEmpty(custMgrDatas)) {
            custMgrData = custMgrDatas.first();
            custInfo.put("CUST_MANAGER_NAME", custMgrData.getString("CUST_MANAGER_NAME",userAttrData.getString("ACCOUNTMANAGERNAME")));
        } else {
            custMgrData = StaffInfoQry.qryStaffInfoByPK(custMgrId);
            custInfo.put("CUST_MANAGER_NAME", custMgrData.getString("STAFF_NAME", userAttrData.getString("ACCOUNTMANAGERNAME")));
        }
        custInfo.put("CUSTMANAGER_SERIAL_NUMBER", custMgrData.getString("SERIAL_NUMBER", userAttrData.getString("ACCOUNTMANAGERPHONE")));
        custInfo.put("CUST_MANAGER_EMAIL", custMgrData.getString("EMAIL", userAttrData.getString("ACCOUNTMANAGERMAIL")));

        custInfo.putAll(userAttrData);
        
        custInfo.put("TITLE", subscriberData.getString("RSRV_STR4"));
        custInfo.put("URGENCY_LEVEL", "1");

        IDataset commonDataList = buildCommonData(custInfo);
        

        //取dataline_attr数据
        //int seq = 0;
        String updataTime = SysDateMgr.getSysTime();
        String acceptMonth = SysDateMgr.getCurMonth();
        IDataset datalines = null;
        String errorMessage = "";
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        String changeMode = "";
        if("CREDITDIRECTLINEPARSE".equals(bpmTempletId)) {
            datalines = TradeOtherInfoQry.queryUserDataLineByUserId(inparam);
            changeMode = "信控停机";
            errorMessage = "未查询到用户[" + userId + "]可停机专线！";
        } else if("CREDITDIRECTLINECONTINUE".equals(bpmTempletId)) {
            datalines = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNoForDatalineKJ(inparam);
            changeMode = "信控复机";
            errorMessage = "未查询到用户[" + userId + "]可开机专线！";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未配置流程信息：" + bpmTempletId);
        }
        IData changeModeData = new DataMap();
        changeModeData.put("ATTR_CODE", "CHANGEMODE");
        changeModeData.put("ATTR_NAME", "业务调整场景");
        changeModeData.put("ATTR_VALUE", changeMode);
        commonDataList.add(changeModeData);
        IData isControlData = new DataMap();
        isControlData.put("ATTR_CODE", "ISCONTROL");
        isControlData.put("ATTR_NAME", "是否自动信控");
        isControlData.put("ATTR_VALUE", "是");
        commonDataList.add(isControlData);
        //commonData.put("ISCONTROL", "是");
        IData CRMNOData = new DataMap();
        CRMNOData.put("ATTR_CODE", "CRMNO");
        CRMNOData.put("ATTR_NAME", "BSS工单号");
        CRMNOData.put("ATTR_VALUE", ibsysId);
        commonDataList.add(CRMNOData);
        //commonData.put("CRMNO", ibsysId);
        //commonData.put("SUBSCRIBE_ID", ibsysId);

        for (int k = 0; k < commonDataList.size(); k++) {
            IData commonData = commonDataList.getData(k);

            IData temp = dealComonEopAttr(SeqMgr.getAttrSeq(), nodeId, ibsysId, subIbsysId, acceptMonth, updataTime);
            temp.put("ATTR_CODE", commonData.getString("ATTR_CODE"));
            temp.put("ATTR_NAME", commonData.getString("ATTR_NAME"));
            temp.put("ATTR_VALUE", commonData.getString("ATTR_VALUE"));
            temp.put("RECORD_NUM", "0");
            attrDatas.add(temp);
        }

        if(DataUtils.isEmpty(datalines)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, errorMessage);
        }
        IDataset esopParam = EweConfigQry.qryByConfigName("LINEPARAM_CRM_ESOP", "0");
        //StaticUtil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
        for (int i = 0; i < datalines.size(); i++) {
            IData dataline = datalines.getData(i);

            Iterator<String> it = dataline.keySet().iterator();
            while (it.hasNext()) {

                String key = it.next();
                String value = dataline.getString(key);

                for (int j = 0; j < esopParam.size(); j++) {
                    String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                    if(key.equals(paramValue)) {
                        key = esopParam.getData(j).getString("PARAMNAME");
                        IData temp = dealComonEopAttr(SeqMgr.getAttrSeq(), nodeId, ibsysId, subIbsysId, acceptMonth, updataTime);
                        temp.put("ATTR_CODE", key);
                        temp.put("ATTR_VALUE", value);
                        temp.put("ATTR_NAME", esopParam.getData(j).getString("VALUEDESC"));
                        temp.put("RECORD_NUM", i + 1);
                        attrDatas.add(temp);
                        break;
                    }
                }


            }
            

            //IDataset datalineTrades = TradeOtherInfoQry.queryDatalineAttrTrade(dataline.getString("PRODUCT_NO"), "PRODUCTNO");
            //IDataset datalineAttrs = TradeOtherInfoQry.queryDatalineAttr(datalineTrades.getData(0).getString("TRADE_ID", ""), null);
            //boolean flag1 = true;
            //boolean flag2 = true;
            //IData datalineInfo = new DataMap();
            //for (int j = 0; j < datalineAttrs.size(); j++) {
            // IData datalineAttr = datalineAttrs.getData(j);
                
            //datalineInfo.put(datalineAttr.getString("ATTR_CODE"), datalineAttr.getString("ATTR_VALUE"));
                /*IData datalineAttr = datalineAttrs.getData(j);
                IData eopAttr = dealComonEopAttr(nodeId, ibsysId, subIbsysId, acceptMonth, updataTime);
                String attrCode = datalineAttr.getString("ATTR_CODE");
                eopAttr.put("ATTR_CODE", attrCode);
                if("CHANGEMODE".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", changeMode);
                    flag1 = false;
                } else if("SUBSCRIBE_ID".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", ibsysId);
                } else if("SERIALNO".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", "ESOP" + ibsysId + "1");
                } else if("CRMNO".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", ibsysId);
                } else if("TITLE".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", changeMode);
                } else if("ISCONTROL".equals(attrCode)) {
                    eopAttr.put("ATTR_VALUE", "是");
                    flag2 = false;
                } else {
                    eopAttr.put("ATTR_VALUE", datalineAttr.getString("ATTR_VALUE"));
                }
                eopAttr.put("SEQ", seq++);
                eopAttr.put("RECORD_NUM", i + 1);
                //eopAttr.put("GROUP_SEQ", i + 1);
                attrDatas.add(eopAttr);*/
            //}
            /*if(flag1) {
                IData eopAttr = dealComonEopAttr(nodeId, ibsysId, subIbsysId, acceptMonth, updataTime);
                eopAttr.put("ATTR_CODE", "CHANGEMODE");
                eopAttr.put("ATTR_VALUE", changeMode);
                eopAttr.put("SEQ", seq++);
                eopAttr.put("RECORD_NUM", i + 1);
                //eopAttr.put("GROUP_SEQ", i + 1);
                attrDatas.add(eopAttr);
            }
            if(flag2) {
                IData eopAttr = dealComonEopAttr(nodeId, ibsysId, subIbsysId, acceptMonth, updataTime);
                eopAttr.put("ATTR_CODE", "ISCONTROL");
                eopAttr.put("ATTR_VALUE", "是");
                eopAttr.put("SEQ", seq++);
                eopAttr.put("RECORD_NUM", i + 1);
                //eopAttr.put("GROUP_SEQ", i + 1);
                attrDatas.add(eopAttr);
            }*/

        }

        int[] result = WorkformAttrBean.insertWorkformAttr(attrDatas);
        IData resultData = new DataMap();
        resultData.put("INSERT_NUM", result.length);
        if(logger.isDebugEnabled()){
            logger.debug("____________________插入ATTR表数据记录：" + result.length);
        }

        //更新节点表subIbsysId
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID");
        IDataset nodeDates = EweNodeQry.qryByBusiformNodeId(busiformNodeId);
        if(IDataUtil.isEmpty(nodeDates)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSIFORM_NODE_ID=" + busiformNodeId + "未查询到节点表信息!");
        }
        EweNodeQry.updWorkformNodeSubBNByPk(busiformNodeId, subIbsysId);
        return resultData;
    }

    private IDataset buildCommonData(IData custInfo) throws Exception {
        IDataset commonDataList = new DatasetList();
        IDataset configList = EweConfigQry.qryByConfigName("CREDIT_PUBLIC_PARAM", "0");
        if(DataUtils.isEmpty(configList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到信控公共参数配置！");
        }
        for (int i = 0; i < configList.size(); i++) {
            IData configData =configList.getData(i);
            String key = configData.getString("PARAMNAME");
            String value = custInfo.getString(configData.getString("PARAMVALUE"), "");
            if(StringUtils.isBlank(value)) {
                value = custInfo.getString(key);
            }
            //没有取到客户服务等级时默认为“标”
            if("SERVICELEVEL".equals(key)) {
                if(StringUtils.isBlank(value)) {
                    value = "标";
                } else {
                    value = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_SERVLEVEL", value });
                }
            }
            //转换集团级别
            if("CUSTOMLEVEL".equals(key) && StringUtils.isNotBlank(value)) {
                value = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_CLASSID", value });
            }
            //转换所属地市
            if("CITYNAME".equals(key)) {
                if(StringUtils.isBlank(value)){
                    value="海口";//默认海口
                }/*else{
                    value = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "AREA_CODE_NEW", value });
                }*/
            }

            IData commonData = new DataMap();
            commonData.put("ATTR_CODE", key);
            commonData.put("ATTR_NAME", configData.getString("VALUEDESC"));
            commonData.put("ATTR_VALUE", value);
            commonDataList.add(commonData);
        }
        return commonDataList;

    }

    private IData dealComonEopAttr(String seq, String nodeId, String ibsysid, String subIbsysId, String acceptMonth, String updataTime) throws Exception {
        IData eopAttr = new DataMap();
        eopAttr.put("UPDATE_TIME", updataTime);
        eopAttr.put("NODE_ID", nodeId);
        eopAttr.put("IBSYSID", ibsysid);
        eopAttr.put("SUB_IBSYSID", subIbsysId);
        eopAttr.put("ACCEPT_MONTH", acceptMonth);
        eopAttr.put("ACCEPT_MONTH", acceptMonth);
        eopAttr.put("SEQ", seq);
        eopAttr.put("GROUP_SEQ", "0");
        return eopAttr;
    }

}
