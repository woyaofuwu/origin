package com.asiainfo.veris.crm.iorder.web.igroup.minorec.acceptanceperiodAudit;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import org.apache.tapestry.IRequestCycle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AcceptanceperiodChgAudit extends EopBasePage {

    public void initPage(IRequestCycle cycle) throws Exception {
        super.initPage(cycle);
        IData param = getData();

        IData info = new DataMap();

        param.put("IBSYSID", param.getString("IBSYSID"));
        param.put("TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));

        IData busiData = new DataMap();
        busiData.put("BUSI_CODE", param.getString("BUSI_CODE"));
        busiData.put("OPER_TYPE", param.getString("BUSIFORM_OPER_TYPE"));

        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", busiData);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            IData busiSpecData = busiSpecReleList.first();
            busiSpecData.put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecData);
        }

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, param.getString("GROUP_ID"));
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("EPARCHY_CODE", group.getString("EPARCHY_CODE"));
        // 查询attr和other表，获取公共信息
        IDataset attrOtherList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryEopAttrOtherData", param);
        if (IDataUtil.isNotEmpty(attrOtherList)) {
            IDataset attrInfo = attrOtherList.getData(0).getDataset("ATTR_LIST");// 获取ATTR表参数
            IDataset otherInfo = attrOtherList.getData(0).getDataset("OTHER_LIST");// 获取OTHER表参数
            IDataset otherAttrList = new DatasetList();
            otherAttrList.addAll(otherInfo);
            otherAttrList.addAll(attrInfo);
            for (Object object : otherAttrList) {
                IData oaInfo = (IData) object;
                info.put(oaInfo.getString("ATTR_CODE"), oaInfo.getString("ATTR_VALUE"));
            }

            String productId = param.getString("PRODUCT_ID");

            String bpmTempletId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });

            info.put("TEMPLET_ID", bpmTempletId);

            IData nodeTempletedData = new DataMap();
            nodeTempletedData.put("BPM_TEMPLET_ID", param.getString("TEMPLET_ID"));
            nodeTempletedData.put("NODE_ID", param.getString("NODE_ID"));
            info.put("NODE_TEMPLETE", nodeTempletedData);
            setInfo(info);
        }

        // 查询集团与成员入表信息
        IDataset offerList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditQuickMemberData", param);
        if (IDataUtil.isNotEmpty(offerList)) {
            queryOffer(offerList, param);// 获取订购产品
        }
        // 获取EOP_SUBSCRIBE表数据
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单IBSYSID=【" + param.getString("IBSYSID") + "】不存在！");
        }
        subscribeData.put("NODE_ID", param.getString("NODE_ID"));
        subscribeData.put("BUSIFORM_NODE_ID", param.getString("BUSIFORM_NODE_ID"));
        subscribeData.put("BUSIFORM_ID", param.getString("BUSIFORM_ID"));
        subscribeData.put("BUSIFORM_OPER_TYPE", param.getString("BUSIFORM_OPER_TYPE"));
        param.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        // 获取审核节点ID
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        input.put("NODE_TYPE", "1");
        IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if (IDataUtil.isNotEmpty(nodeTempleteList)) {
            for (Object object : nodeTempleteList) {
                IData nodeTempletInfo = (IData) object;
                String nodeName = nodeTempletInfo.getString("NODE_NAME");
                if (nodeName.indexOf("审核") >= 0) {
                    param.put("NODE_ID", nodeTempletInfo.getString("NODE_ID"));
                    break;
                }
            }
        }

        setCondition(param);
    }

    /**
     * 展示产品信息
     * 
     * @param
     * @throws Exception
     */
    public void queryOffer(IDataset offerList, IData param) throws Exception {
        String bpmTempletId = param.getString("BPM_TEMPLET_ID");
        IDataset offerCodeList = new DatasetList();
        // 此处防止重复用Set
        Set<String> contractSet = new HashSet<String>();
        for (Object object : offerList) {
            IData offerCInfo = new DataMap();
            IData offerData = (IData) object;
            String offerCode = offerData.getString("PRODUCT_ID");
            IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
            String offerId = IUpcViewCall.getOfferIdByOfferCode(offerCode);// 获取OFFERID
            String brandCode = IUpcViewCall.queryBrandCodeByOfferId(offerId);// 获取品牌
            offerCInfo.put("OFFER_CODE", offer.getString("OFFER_CODE"));
            offerCInfo.put("OFFER_ID", offerId);
            offerCInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
            offerCInfo.put("BRAND_CODE", brandCode);
            offerCInfo.put("OFFER_MEMBER", offerData.getDataset("OFFER_MEMBER"));// 所有的成员手机号码
            offerCInfo.put("MEB_OFFER", offerData.getData("MEB_OFFER"));// 成员产品资费等信息
            offerCInfo.put("EC_OFFER", offerData.getData("EC_OFFER"));// 集团产品资费等信息
            offerCInfo.put("EC_COMMON_INFO", offerData.getData("EC_COMMON_INFO"));// 账户及付费关系信息
            offerCodeList.add(offerCInfo);

            IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            contractSet.add(contractInfo.getString("CONTRACT_ID"));
        }

        setOfferCodeList(offerCodeList);

        // 处理合同信息
        IDataset contractNameList = ElecLineUtil.queryContractNameInfo(contractSet,this);
        setContractNameList(contractNameList);

        IDataset contractList = ElecLineUtil.queryContractInfos(contractSet,this);
        setContractList(contractList);
    }

    /**
     * 解析数据
     * 
     * @param
     * @throws Exception
     */
    public void analyslsOfferData(IRequestCycle cycle) throws Exception {
        IData param = getData();

        IData ecCommonInfo = new DataMap(param.getString("EC_COMMON_INFO_DATA"));// 账户及付费关系信息
        IData mebofferInfo = new DataMap(param.getString("MEB_OFFER_DATA"));// 成员产品资费等信息
        IData ecOfferInfo = new DataMap(param.getString("EC_OFFER_DATA"));// 集团产品资费等信息
        IDataset offerMemberInfo = new DatasetList(param.getString("OFFER_MEMBER_DATA"));// 所有的成员手机号码

        String operType = param.getString("OPER_TYPE");

        if (BizCtrlType.MinorecAddMember.equals(operType) || BizCtrlType.MinorecDestroyMember.equals(operType) || BizCtrlType.MinorecCreateUser.equals(operType)) {

            if (IDataUtil.isNotEmpty(offerMemberInfo)) {
                setMemberList(offerMemberInfo);// 展示成员手机号码
            }

            if (IDataUtil.isNotEmpty(ecCommonInfo)) {
                acctEcCommon(ecCommonInfo);// 转换集团账户信息及付费信息
            }
            if (IDataUtil.isNotEmpty(mebofferInfo) && DataUtils.isNotEmpty(mebofferInfo.getString("SUBOFFERS"))) {
                IDataset mebofferData = new DatasetList(mebofferInfo.getString("SUBOFFERS"));
                setMemberOfferList(mebofferData);// 转换集团账户信息及付费信息
            }
            //宽带资费处理
            if (DataUtils.isNotEmpty(offerMemberInfo) && DataUtils.isNotEmpty(offerMemberInfo.getData(0)) && DataUtils.isNotEmpty(offerMemberInfo.getData(0).getDataset("SELECTED_ELEMENTS"))) {
            	builderWideNetOffer(offerMemberInfo);
			}
            if (IDataUtil.isNotEmpty(ecOfferInfo)) {
                // ESP产品处理
                String offerCode = ecOfferInfo.getString("OFFER_CODE");
                String brandCode = ecOfferInfo.getString("BRAND_CODE");
                IData espInfo = new DataMap();
                espInfo.put("OFFER_CODE", offerCode);
                espInfo.put("BRAND_CODE", brandCode);
                setOfferInfo(espInfo);
                
                // 页面删除为空的参数
                if (DataUtils.isNotEmpty(ecOfferInfo.getString("OFFER_CHA_SPECS"))) {
                	
                	IDataset ecOfferChaList = new DatasetList(ecOfferInfo.getString("OFFER_CHA_SPECS"));
                	Iterator<Object> ecOfferChaInfo = ecOfferChaList.iterator();
                	while (ecOfferChaInfo.hasNext()) {
                		IData ecOfferChaIf = (IData) ecOfferChaInfo.next();
                		String attrName = ecOfferChaIf.getString("ATTR_NAME");
                		String attrValue = ecOfferChaIf.getString("ATTR_VALUE");
                		if ("宽带信息".equals(attrName) || "专线操作方法名".equals(attrName) || "表格隐藏标记".equals(attrName)) {// 不展示宽带信息参数，没有存NAME，做过滤
                			ecOfferChaInfo.remove();
                		} else if (StringUtils.isBlank(attrValue)) {
                			ecOfferChaInfo.remove();
                		}
                		// 转换下拉框枚举值
                		offerChaSpan(offerCode, ecOfferChaIf);
                	}
                	
                	setEcOfferChaList(ecOfferChaList);// 转换集团产品参数
				}

                IDataset grpPackageList = ecOfferInfo.getDataset("GRP_PACKAGE_INFO");

                if (IDataUtil.isNotEmpty(grpPackageList)) {
                    setGrpPackageList(grpPackageList);// 转换集团定制信息
                }
            }

        } else if ("DstUser".equals(operType)) {

        } else if ("ChgWn".equals(operType)) {
        	if (IDataUtil.isNotEmpty(ecCommonInfo)) {
                acctEcCommon(ecCommonInfo);// 转换集团账户信息及付费信息
            }
            if (IDataUtil.isNotEmpty(offerMemberInfo)) {
                setMemberList(offerMemberInfo);// 展示成员手机号码
            }
            //宽带资费处理
            if (DataUtils.isNotEmpty(offerMemberInfo) && DataUtils.isNotEmpty(offerMemberInfo.getData(0)) && DataUtils.isNotEmpty(offerMemberInfo.getData(0).getDataset("SELECTED_ELEMENTS"))) {
            	builderWideNetOffer(offerMemberInfo);
			}

        }
		IData info = new DataMap();
		info.put("OFFER_ID", ecOfferInfo.getString("OFFER_ID"));
		setInfo(info);
    }

	private void builderWideNetOffer(IDataset offerMemberInfo) throws Exception {
		IDataset widenets = offerMemberInfo.getData(0).getDataset("SELECTED_ELEMENTS");
		IDataset widenetList = new DatasetList();
		for (int i = 0, len = widenets.size(); i < len; i++) {
			IData data = widenets.getData(i);
			
			//查询宽带产品名称
			String offerName = UpcViewCall.queryOfferNameByOfferId(this, data.getString("ELEMENT_TYPE_CODE"), data.getString("ELEMENT_ID"));
			
            if(StringUtils.isNotEmpty(offerName))
            {
                IData widenet = new DataMap();
                widenet.put("OFFER_CODE", data.getString("ELEMENT_ID"));
                widenet.put("OFFER_NAME", offerName);
                widenet.put("START_DATE", data.getString("START_DATE"));
                widenet.put("END_DATE", data.getString("END_DATE"));
                widenetList.add(widenet);
            }
		}
		setMemberOfferList(widenetList);// 转换集团账户信息及付费信息
	}

    public void acctEcCommon(IData ecCommonInfo) throws Exception {
        IDataset ecCommonList = new DatasetList();
        IData acctInfo = ecCommonInfo.getData("ACCT_INFO");
        if (IDataUtil.isNotEmpty(acctInfo)) {
            String acctType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CRM_STATIC_PAYMODECODE", acctInfo.getString("ACCT_TYPE") });
            IData acctNameMap = new DataMap();
            acctNameMap.put("ATTR_NAME", "账户名称");
            acctNameMap.put("ATTR_VALUE", acctInfo.getString("ACCT_NAME"));
            ecCommonList.add(acctNameMap);
            IData acctTypeMap = new DataMap();
            acctTypeMap.put("ATTR_NAME", "账户类别");
            acctTypeMap.put("ATTR_VALUE", acctType);
            ecCommonList.add(acctTypeMap);
            IData acctPanlMap = new DataMap();
            if ("P".equals(ecCommonInfo.getString("PAY_PLAN_INFO"))) {
                acctPanlMap.put("ATTR_NAME", "付费关系");
                acctPanlMap.put("ATTR_VALUE", "集团付费");
                ecCommonList.add(acctPanlMap);
            } else if ("P,G".equals(ecCommonInfo.getString("PAY_PLAN_INFO"))) {
                acctPanlMap.put("ATTR_NAME", "付费关系");
                acctPanlMap.put("ATTR_VALUE", "集团付费,个人付费");
                ecCommonList.add(acctPanlMap);
            } else if ("G".equals(ecCommonInfo.getString("PAY_PLAN_INFO"))) {
                acctPanlMap.put("ATTR_NAME", "付费关系");
                acctPanlMap.put("ATTR_VALUE", "个人付费");
                ecCommonList.add(acctPanlMap);
            }
            setEcCommonList(ecCommonList);
        }
    }


    public void offerChaSpan(String offerCode, IData ecOfferChaIf) throws Exception {

        if ("8001".equals(offerCode)) {
            if ("GRP_CALL_DISP_MODE".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                if ("1".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "均显示短号");
                } else if ("2".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "均显示长号");
                } else if ("3".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "拨短号显示短号；拨长号显示长号");
                }
            } else if ("GRP_CALL_PACMODE".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                if ("1".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "带出群字冠");
                } else if ("2".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "不带出群字冠");
                }
            }
        } else if ("7341".equals(offerCode)) {
            if ("PSPT_TYPE_CODE".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                String staticDataName = StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", ecOfferChaIf.getString("ATTR_VALUE", ""));
                ecOfferChaIf.put("ATTR_VALUE", staticDataName);
            } else if ("RSRV_STR3".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                String staticDataName = StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", ecOfferChaIf.getString("ATTR_VALUE", ""));
                ecOfferChaIf.put("ATTR_VALUE", staticDataName);
            } else if ("AGENT_PSPT_TYPE_CODE".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                String staticDataName = StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", ecOfferChaIf.getString("ATTR_VALUE", ""));
                ecOfferChaIf.put("ATTR_VALUE", staticDataName);
            } else if ("NOTIN_HAS_FEE_PRIV".equals(ecOfferChaIf.getString("ATTR_CODE")) || "checkGlobalMorePsptIdFlag".equals(ecOfferChaIf.getString("ATTR_CODE"))) {
                if ("true".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "是");
                } else if ("false".equals(ecOfferChaIf.getString("ATTR_VALUE", ""))) {
                    ecOfferChaIf.put("ATTR_VALUE", "否");
                }
            }
        }
    }


    @Override
    public void buildOtherSvcParam(IData submmitParam) throws Exception {

        String adultResult = submmitParam.getString("ADULT_RESULT");

        // 根据ibsysid查询 data , meb 表信息
        if ( "2".equals(adultResult))   // 2 : 审核通过  ，  1： 审核不通过
        {
            IData commonData = submmitParam.getData("COMMON_DATA");
            IDataset eopDataList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditQuickMemberData", commonData);

            // 查询客户信息
            IData custData = submmitParam.getData("CUST_DATA");
            IData custInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", custData);
            if (DataUtils.isEmpty(custInfo))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据 客户编码 查询不到客户信息！");
            }

            submmitParam.remove("OFFER_LIST");
            submmitParam.remove("CUST_INFO");
            submmitParam.put("OFFER_LIST", eopDataList);
            submmitParam.put("CUST_INFO", custInfo);
        }
        else
        {
            submmitParam.remove("OFFER_LIST");
            return;
        }

        String operType = submmitParam.getString("OPER_TYPE");
        if(BizCtrlType.MinorecAddMember.equals(operType))
        {
            // 新增成员
            MinorecIntegrateTrans.transformCrtMebByChangeAudit(submmitParam);
        }
        else if(BizCtrlType.MinorecCreateUser.equals(operType))
        {
            // 新增集团
            MinorecIntegrateTrans.transformAfterAcceptSubmitDataList(submmitParam);
        }
        else if(BizCtrlType.MinorecChangeWideNet.equals(operType))
        {
            // 宽带变更
            MinorecIntegrateTrans.transformChgWnByChangeAudit(submmitParam);
        }
        else if(BizCtrlType.MinorecDestroyMember.equals(operType))
        {
            // 删除成员
            MinorecIntegrateTrans.transformDelMebByChangeAudit(submmitParam);
        }
        else if(BizCtrlType.MinorecDestroyUser.equals(operType))
        {
            // 删除集团
            MinorecIntegrateTrans.transformDstSubByChangeAudit(submmitParam);
        }


    }

    public abstract void setCondition(IData cond) throws Exception;;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setOfferInfo(IData info) throws Exception;

    public abstract void setGrpPackageList(IDataset grpPackageList) throws Exception;

    public abstract void setEcOfferChaList(IDataset ecOfferChaList) throws Exception;

    public abstract void setMemberOfferList(IDataset memberOfferList) throws Exception;

    public abstract void setEcCommonList(IDataset ecCommonList) throws Exception;

    public abstract void setOfferCodeList(IDataset offerCodeList) throws Exception;

    public abstract void setMemberList(IDataset memberList) throws Exception;

    public abstract void setContractNameList(IDataset contracNametList) throws Exception;

    public abstract void setContractList(IDataset contractList) throws Exception;

    public abstract void setContractData(IData contractData) throws Exception;

}
