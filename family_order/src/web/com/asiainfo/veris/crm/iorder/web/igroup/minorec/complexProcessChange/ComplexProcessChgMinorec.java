package com.asiainfo.veris.crm.iorder.web.igroup.minorec.complexProcessChange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class ComplexProcessChgMinorec extends EopBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {
        super.initPage(cycle);
        IData param = getData();

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, param.getString("GROUP_ID"));
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("EPARCHY_CODE", group.getString("EPARCHY_CODE"));
        // 查询attr和other表，获取公共信息
        queryEopAttrOtherData(param);

        // 获取EOP_SUBSCRIBE表数据
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单IBSYSID=【" + param.getString("IBSYSID") + "】不存在！");
        }
        subscribeData.put("NODE_ID", param.getString("NODE_ID"));
        subscribeData.put("BUSIFORM_NODE_ID", param.getString("BUSIFORM_NODE_ID"));
        param.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        // 获取节点ID
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if (IDataUtil.isNotEmpty(nodeTempleteList)) {
            param.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));
        }
        input.clear();
        input.put("BUSI_CODE", param.getString("PRODUCT_ID"));
        input.put("OPER_TYPE", "20");
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            param.put("BUSI_SPEC_RELE", busiSpecReleList.first());
        }
        IData nodeTempletedData = new DataMap();
        nodeTempletedData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        nodeTempletedData.put("NODE_ID", param.getString("NODE_ID"));
        param.put("NODE_TEMPLETE", nodeTempletedData);

        setCondition(param);
    }

    /**
     * 查询attr和other表，获取公共信息
     * 
     * @param
     * @throws Exception
     */
    public void queryEopAttrOtherData(IData param) throws Exception {
        // 查询attr和other表，获取公共信息
        IDataset attrOtherList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryEopAttrOtherData", param);
        if (IDataUtil.isNotEmpty(attrOtherList)) {
            IData pulicInfo = new DataMap();
            IDataset attrInfo = attrOtherList.getData(0).getDataset("ATTR_LIST");// 获取ATTR表参数
            IDataset otherInfo = attrOtherList.getData(0).getDataset("OTHER_LIST");// 获取OTHER表参数
            IDataset otherAttrList = new DatasetList();
            otherAttrList.addAll(otherInfo);
            otherAttrList.addAll(attrInfo);
            for (Object object : otherAttrList) {
                IData oaInfo = (IData) object;
                pulicInfo.put(oaInfo.getString("ATTR_CODE"), oaInfo.getString("ATTR_VALUE"));
            }
            String productId = param.getString("PRODUCT_ID");
            String bpmTempletId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });
            pulicInfo.put("TEMPLET_ID", bpmTempletId);

            setInfo(pulicInfo);
            IData productInfo = new DataMap();
            productInfo.put("PARAM_OFFER", "2");
            setOperTypeMap(productInfo);
            // 查询集团与成员入表信息
            queryAuditQuickMemberData(param, pulicInfo.getString("OPER_TYPE"),bpmTempletId);

        }
    }

    /**
     * 查询集团与成员入表信息
     * 
     * @param
     * @throws Exception
     */
    public void queryAuditQuickMemberData(IData param, String operType,String bpmTempletId) throws Exception {

        IDataset offerList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditQuickMemberData", param);
        if (IDataUtil.isNotEmpty(offerList)) {
            contractData(offerList, param,bpmTempletId);// 获取电子协议合同信息

        }

    }

    /**
     * 回显合同信息
     * 
     * @param
     * @throws Exception
     */
    public void contractData(IDataset offerList, IData param, String bpmTempletId) throws Exception {
    	IData productInfo = new DataMap();
        if ("YIDANQINGSHANGPUCHANGE".equals(bpmTempletId)) {// 判断是否是商铺类产品，是的话是否包含集团V网的电子协议
            for (Object object : offerList) {
                IData offerData = (IData) object;
                IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
                String productId = offerData.getString("PRODUCT_ID");
                IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
                if ("8000".equals(productId)) {// 回显集团V网的电子协议信息
                    productInfo.put("CONTRACT_NAME_VW", contractInfo.getString("CONTRACT_NAME"));
                    productInfo.put("CONTRACT_ID_VW", contractInfo.getString("CONTRACT_ID"));
                    productInfo.put("CONTRACT_END_DATE_VW", contractInfo.getString("CONTRACT_END_DATE"));
                    productInfo.put("CONTRACT_WRITE_DATE_VW", contractInfo.getString("CONTRACT_WRITE_DATE"));
                    productInfo.put("OFFER_IDS_VW", contractInfo.getString("OFFER_IDS"));
                    productInfo.put("GROUP_VW", "true");
                } else {// 回显其他产品的电子协议信息
                    productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
                    productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
                    productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
                    productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
                    productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
                    productInfo.put("CONTRACT_IDS", "true");
                }
            }
            setPattrInfo(productInfo);

        } else {// 其他流程只需获取其中一个电子协议信息即可
            IData ecCommonInfo = offerList.first().getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
            productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
            productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
            productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
            productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
            setPattrInfo(productInfo);
        }
        
        String groupId = param.getString("GROUP_ID");
        String productId = param.getString("PRODUCT_ID");
        String templetId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });
        // 获取虚拟的产品ID
        String xnProductId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "PDATA_ID" }, "DATA_ID", new String[] { "MINOREC_XN_PRODUCT", templetId });
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("XN_PRODUCTID", xnProductId);
        IDataset archivesList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryArchivesInfo", param);
        if (IDataUtil.isNotEmpty(archivesList)) {
            String contId = productInfo.getString("CONTRACT_ID");
            String offerIds = productInfo.getString("OFFER_IDS_VW");
            IData archiveData = new DataMap();
            for (Object object : archivesList) {
                IData archiveInfo = (IData) object;
                String archiceContId = archiveInfo.getString("CONTRACT_ID");
                if (archiceContId.equals(contId)) {
                    archiveData = archiveInfo;
                    break;
                }
                
            }
            setArchiveData(archiveData);
            if("8000".equals(offerIds)) {
            	for (Object object : archivesList) {
                    IData archiveInfoVw = (IData) object;
                    String productsId = archiveInfoVw.getString("PRODUCT_ID");
                    if (productsId.equals(offerIds)) {
                        archiveData = archiveInfoVw;
                        break;
                    }
                }
            	setArchiveVwData(archiveData);
            }
        }
    }

    //合同信息确认
    public void setOperType(IRequestCycle cycle) throws Exception {
        IData pageData = getData();

        IData dataOfferCode = new DataMap(pageData.getString("DATE_OFFERCODE"));// 通过选择合同获取的offerCode
        IData contractInfo = new DataMap();
        contractInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
        contractInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
        contractInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
        contractInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
        contractInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));

        if ("8000".equals(dataOfferCode.getString("OFFER_IDS"))) {// 回显集团V网的电子协议信息
            contractInfo.put("CONTRACT_NAME_VW", dataOfferCode.getString("CONTRACT_NAME"));
            contractInfo.put("CONTRACT_ID_VW", dataOfferCode.getString("CONTRACT_ID"));
            contractInfo.put("CONTRACT_END_DATE_VW", dataOfferCode.getString("CONTRACT_END_DATE"));
            contractInfo.put("CONTRACT_WRITE_DATE_VW", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
            contractInfo.put("OFFER_IDS_VW", dataOfferCode.getString("OFFER_IDS"));
            contractInfo.put("GROUP_VW", "true");
        } else {// 回显其他产品的电子协议信息
            contractInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
            contractInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
            contractInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
            contractInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
            contractInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));
        }
        setPattrInfo(contractInfo);
    }   

    public int[] intOfferCodeSort(String[] offerCodeInfo) throws Exception {

        int intOfferCode[] = new int[offerCodeInfo.length];
        for (int i = 0; i < offerCodeInfo.length; i++) {
            intOfferCode[i] = Integer.parseInt(offerCodeInfo[i]);
        }
        for (int i = 0; i < intOfferCode.length - 1; i++) {
            for (int j = 0; j < intOfferCode.length - 1 - i; j++) {
                if (intOfferCode[j] > intOfferCode[j + 1]) {
                    int temp = intOfferCode[j];
                    intOfferCode[j] = intOfferCode[j + 1];
                    intOfferCode[j + 1] = temp;
                }
            }
        }

        return intOfferCode;

    }
    
    /**
     * 查询流程信息
     * 
     * @param taosx
     * @throws Exception
     */
    public void operTypeByTempletId(String templetId, String offerCode, String templetName) throws Exception {
        IData commInfo = new DataMap();
        IData info = new DataMap();
        IData templetInfo = WorkfromViewCall.getOperTypeByTempletId(this, templetId);
        IData input = new DataMap();
        input.put("BUSI_CODE", offerCode);
        input.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));
        
        commInfo.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            busiSpecReleList.first().put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecReleList.first());
            info.put("TEMPLET_BUSI_CODE", busiSpecReleList.first().getString("BUSI_CODE"));

            // 查询流程节点信息
            input.clear();
            input.put("BPM_TEMPLET_ID", busiSpecReleList.first().getString("BPM_TEMPLET_ID"));
            input.put("NODE_TYPE", "3");
            IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
            if (IDataUtil.isNotEmpty(nodeTempleteList)) {
                IData nodeTempletedData = new DataMap();
                String bpmTempletId = nodeTempleteList.first().getString("BPM_TEMPLET_ID");
                nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
                nodeTempletedData.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));

                info.put("NODE_TEMPLETE", nodeTempletedData);

                commInfo.put("FLOW_ID", bpmTempletId); // POINT_ONE
                commInfo.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID")); // POINT_TWO
                commInfo.put("PRODUCT_ID", offerCode);
                commInfo.put("PAGE_LEVE", templetId);
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置未配置主流程或流程节点信息，不能办理该业务！");
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置业务流程关系信息，不能办理该业务！");
        }
        info.put("CHANGE_BPM_TEMPLET_ID", templetId);
        setComminfo(commInfo);
        setInfo(info); 
    }
    

    public void changeSaleActiveList(String productId) throws Exception {

        IData map = new DataMap();
        map.put("NEW_PRODUCT_ID", productId);
        map.put("ROUTE_EPARCHY_CODE", "0898");
        IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getWidenetUserOpenElements", map);
        if (IDataUtil.isNotEmpty(dataset)) {
            IData elementInfo = new DataMap();
            IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
            // 写死资费属性
            IDataset saleActiveInfos = new DatasetList();
            IData saleActiveInfo = new DataMap();

            saleActiveInfo.put("ELEMENT_ID", "20001088");
            saleActiveInfo.put("ELEMENT_TYPE_CODE", "D");
            saleActiveInfo.put("PRODUCT_ID", "20150150");
            saleActiveInfo.put("PACKAGE_ID", "20150153");
            saleActiveInfo.put("MODIFY_TAG", "0");
            saleActiveInfo.put("START_DATE", SysDateMgr.getSysDate());
            saleActiveInfo.put("END_DATE", "2050-12-31 00:00:00 ");
            saleActiveInfo.put("INST_ID", "");
            saleActiveInfo.put("ELEMENT_NAME", "宽带套餐(0元包月-集团商务宽带使用)");
            saleActiveInfos.add(saleActiveInfo);
            selectedElements.addAll(saleActiveInfos);
            elementInfo.put("SELECTED_ELEMENTS", selectedElements);
            setSaleActiveListAttr(saleActiveInfos);
            setSaleActiveList(elementInfo);
        }

    }

    //稽核员工
    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
        IData input = getData();
        IData inParam = new DataMap();
        String staffName = input.getString("cond_StaffName", "");
        /* if (StringUtils.isNotBlank(staffName)) { inParam.put("STAFF_NAME", staffName); } */
        String roleId = pageutil.getStaticValue("TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "AUDIT_ROLE", "ROLE_ID" });
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取计费方式审核角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
        // input.put("DEPART_ID", departId);
        inParam.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        inParam.put("START_MAX", "0");
        inParam.put("ROWNUM_", "1000");
        inParam.put("X_GETMODE", "13");
        inParam.put("RIGHT_CODE", roleId);
        IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", inParam);
        if (StringUtils.isNotBlank(staffName)) {
            for (int i = 0; i < staffList.size(); i++) {
                IData staff = staffList.getData(i);
                if (staffName.equals(staff.getString("STAFF_NAME"))) {
                    IDataset staffListName = new DatasetList();
                    staffListName.add(staff);
                    setInfos(staffListName);
                }
            }
        } else {
            setInfos(staffList);
        }
    }

    
    /**
     * 查询集团客户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByGroupId(IRequestCycle cycle) throws Exception {
        String groupId = getData().getString("GROUP_ID");

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");

        setGroupInfo(group);

        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId)) {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }

	/*
	 * @Override public void buildOtherSvcParam(IData submmitParam) throws Exception
	 * {
	 * MinorecIntegrateTrans.transformCreateHotelIntegrationSubmitData(submmitParam)
	 * ; }
	 */

    public abstract void setWideInfo(IData wideInfo) throws Exception;

    public abstract void setCondition(IData cond) throws Exception;

    public abstract void setSaleActiveList(IData saleActiveList) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset saleActiveListAttr) throws Exception;

    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList) throws Exception;

    public abstract void setWidenetPayModeList(IDataset widenetPayModeList) throws Exception;

    public abstract void setProductList(IDataset productList) throws Exception;

    public abstract void setGroupInfo(IData groupInfo) throws Exception;

    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setOfferCodeList(IDataset offerCodeList) throws Exception;

    public abstract void setWidenetInfos(IDataset widenetInfos) throws Exception;

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setOperTypeMap(IData operTypeMap) throws Exception;

    public abstract void setOperTypeList(IDataset operTypeList) throws Exception;
    
    public abstract void setArchiveData(IData archiveData) throws Exception;
    
    public abstract void setArchiveVwData(IData archiveVwData) throws Exception;

    public abstract void setOfferList(IDataset operTypeList) throws Exception;
}
