
package com.asiainfo.veris.crm.iorder.web.igroup.minorec.audit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

import scala.collection.mutable.StringBuilder;

public abstract class MinorecAudit extends CSBasePage
{
    private static String infosign = "";
    
    public static String TOTAL_FLAG = "true";
    
    private static final Integer COND_FIRST_NUM = 1;
    
    private static final Integer COND_LAST_NUM = 10;

    /**
     * @Title: initial
     * @Description: 页面初始化方法
     * @param cycle
     * @throws Exception
     * @author zhangzg
     * @date 2019年11月12日上午10:52:07
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        TOTAL_FLAG = "true";
        IData input = getData();
        infosign = input.getString("BUSIFORM_NODE_ID");
        String ibsysId = input.getString("IBSYSID");
        String bpmTepletId = input.getString("SUB_BPM_TEMPLET_ID");
        String productId = input.getString("PRODUCT_ID");
        String busiformId = input.getString("BUSIFORM_ID");
        //处理旧数据 已生成待阅
        if(StringUtils.isBlank(bpmTepletId)) {
            bpmTepletId = input.getString("BPM_TEMPLET_ID");
        }
        IData param = new DataMap();
        param.put("BPM_TEMPLET_ID", bpmTepletId);
        param.put("IBSYSID", ibsysId);
        param.put("PRODUCT_ID", productId);
        param.put("BUSIFORM_ID", busiformId);
        param.put("TEMPLET_TYPE", "0");
        IData expData = new DataMap();
        String errorInfo = "";
        //判断主流程是否完工
        IData finishEweInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByIbsysid",param);
        if(IDataUtil.isNotEmpty(finishEweInfo)) {
            errorInfo = "请等待主流程完工后在进行业务稽核！";
            expData.put("EXP_INFO", errorInfo);
            setExpInfo(expData);
            return;
        }
        String offerCode = "";
        // 查询稽核展示信息 EWE SUBSCRIBE PRODUCT
        IData eweInfo = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryInfoByBpmTempletIdAndBiSn", param);
        try
        {
            offerCode = eweInfo.getString("BUSI_CODE");
        }
        catch (Exception e)
        {
            errorInfo = "根据业务类型" + bpmTepletId + "和订单号" + ibsysId + "未查询到数据！";
            expData.put("EXP_INFO", errorInfo);
            setExpInfo(expData);
            return;
        }
        setExpInfo(expData);
        IData tmpParam = new DataMap();
        tmpParam.put("BPM_TEMPLET_ID", bpmTepletId);
        // 若当前流程为子流程 则查询父流程
        IData tmpInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qrySubRelaInfoByTemplet", tmpParam);
        String parentBpmTepletId = "";
        if (IDataUtil.isNotEmpty(tmpInfos))
        {
            tmpParam.put("IBSYSID", ibsysId);
            tmpParam.put("BUSIFORM_ID", busiformId);
            IData eopProductInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryEopProductByIbsysidAndBusiformId", tmpParam);
            offerCode = eopProductInfos.getString("PRODUCT_ID");
            IData parentBpmtempletInfo = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryParentBpmtempletIdByIbsysId", tmpParam);
            if(IDataUtil.isNotEmpty(parentBpmtempletInfo)) {
                parentBpmTepletId = parentBpmtempletInfo.getString("BPM_TEMPLET_ID");
            }
        }
        // 查询产品名称
        String productName = IUpcViewCall.getOfferInfoByOfferCode(offerCode).getString("OFFER_NAME");
        eweInfo.put("PRODUCT_NAME", productName);
        // 查询对应对应流程名
        String bpmName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
        { "MINOREC_AUDIT_BPM", bpmTepletId });
        if (StringUtils.isBlank(bpmName) && StringUtils.isNotBlank(parentBpmTepletId))
        {
            bpmName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]
            { "MINOREC_AUDIT_BPM", parentBpmTepletId });
        }
        eweInfo.put("BPM_TEMPLET_NAME", bpmName);
        IData otherMap = new DataMap();
        IData info = new DataMap();
        info.putAll(eweInfo);
        otherMap.put("RECORD_NUM", eweInfo.getString("RECORD_NUM"));
        otherMap.put("ATTR_CODE", "AUDITESOPINFO");
        otherMap.put("IBSYSID", ibsysId);
        // 查询TF_BH_EOP_OTHER是否有稽核记录
        IData otherInfo = CSViewCall.callone(this, "SS.WorkformOtherSVC.qryOtherByIbsysidAttrCodeRecordNum", otherMap);
        if (IDataUtil.isEmpty(otherInfo))
        {
            info.put("IS_AUDIT", "否");
            info.put("AUDIT_FLAG", "1");
        }
        else
        {
            info.put("IS_AUDIT", "是");
            info.put("AUDIT_FLAG", "0");
        }
        setOrderInfo(eweInfo);
        info.put("USER_ID", eweInfo.getString("USER_ID_A"));
        // 查询产品数据 TF_B_EOP_QUICKORDER_DATA 
        param.put("PRODUCT_ID", offerCode);
        IData orderInfos = CSViewCall.callone(this, "SS.QuickOrderDataSVC.qryAllQuickOrderInfoByIbsysidAndProductId", param);
        String contractId = "";
        if (IDataUtil.isNotEmpty(orderInfos))
        {
            StringBuilder codingstr = new StringBuilder();
            buildOfferStr(orderInfos, codingstr);
            IData offerInitData = new DataMap(codingstr.toString());
            // 获取合同标识 CONTRACT_ID
            contractId = offerInitData.getData("EC_COMMON_INFO").getData("CONTRACT_INFO").getString("CONTRACT_ID");
            // 查询协议信息和合同附件
            Set<String> contractSet = new HashSet<String>();
            contractSet.add(contractId);
            IDataset contractNameList = ElecLineUtil.queryContractNameInfo(contractSet,this);
            setContractNameList(contractNameList);
            IDataset contractList = ElecLineUtil.queryContractInfos(contractSet,this);
            setContractList(contractList);
        }
        // 查询集团与成员入表信息
        IDataset offerList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditInfoData", param);
        if (IDataUtil.isNotEmpty(offerList))
        {
            // 获取订购产品
            queryOffer(offerList);
        }
        //自动稽核
        IData autoParams = new DataMap();
        autoParams.put("USER_ID", eweInfo.getString("USER_ID_A"));
        autoParams.put("AGREEMENT_ID", contractId);
        autoParams.put("PRODUCT_ID", offerCode);
        autoParams.put("SERIAL_NUMBER", eweInfo.getString("SERIAL_NUMBER"));
        autoAuditInfos(autoParams);
        info.put("TOTAL_FLAG", TOTAL_FLAG);
        info.put("PRODUCT_ID", productId);
        info.put("EC_SERIAL_NUMBER", orderInfos.getString("EC_SERIAL_NUMBER"));
        setInfo(info);
    }
    
    /**
     * 
    * @Title: autoAuditInfos 
    * @Description: 处理自动稽核信息
    * @param PRODUCT_ID  USER_ID  AGREEMENT_ID
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月22日上午9:44:44
     */
    public void autoAuditInfos(IData params) throws Exception {
        IDataset autoAuditInfos = new DatasetList();
        if(StringUtils.isNotBlank(params.getString("PRODUCT_ID")) && StringUtils.isNotBlank(params.getString("USER_ID"))) {
            String productId = params.getString("PRODUCT_ID");
            if("7341".equals(productId)) {
                //政企宽带自动稽核信息处理
                IDataset paramCodeList = StaticUtil.getList(getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", new String[] { "TYPE_ID", "PDATA_ID" }, new String[] { "ELEC_AUDIT_CODE", productId });
                StringBuilder paramCode = new StringBuilder(100);
                for(Object obj : paramCodeList) {
                    IData data = (IData)obj;
                    paramCode.append("'" + data.getString("DATA_ID") + "',");
                }
//                params.put("PARAM_CODE","'EP_WITH','EP_NUM'");
                params.put("PARAM_CODE", paramCode.substring(0, paramCode.length()-1).toString());
                IDataset elecContractInfos = new DatasetList();
                if(StringUtils.isNotBlank(params.getString("AGREEMENT_ID"))) {
                    //查询电子协议信息 PARAM_CODE
                    elecContractInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryElecDataForFusercommunication", params);
                }
                String epWith1 = "";
                String epNum1 = "";
                if(IDataUtil.isNotEmpty(elecContractInfos)) {
                    IData transferContractInfo = transferContractInfo(elecContractInfos);
                    epWith1 = transferContractInfo.getString("EP_WITH");
                    epNum1 = transferContractInfo.getString("EP_NUM");
                }
                //查询宽带条数
                String epNum2 = "";
                params.put("RSRV_VALUE_CODE", "N002");
                // 获取集团最大受理宽带条数
                IDataset mebProductList = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", params);
                if (IDataUtil.isNotEmpty(mebProductList)) {
                    epNum2 = mebProductList.first().getString("RSRV_STR1", "");
                }
                //查询带宽速率
                IData finishBrandwithInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryFinishDataForBandwith", params);
                String epWith2 = "";
                if(IDataUtil.isNotEmpty(finishBrandwithInfos)) {
                    epWith2 = finishBrandwithInfos.getString("RATE");
                }
                IData epWithData = dealTrueOrFalse(epWith1, epWith2, "宽带带宽（M）");
                autoAuditInfos.add(epWithData);
                IData epNumData = dealTrueOrFalse(epNum1, epNum2, "宽带条数");
                autoAuditInfos.add(epNumData);
            }else if("2222".equals(productId)) {
                //融合通信 多媒体桌面电话 自动稽核信息处理
                IDataset paramCodeList = StaticUtil.getList(getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", new String[] { "TYPE_ID", "PDATA_ID" }, new String[] { "ELEC_AUDIT_CODE", productId });
                StringBuilder paramCode = new StringBuilder(100);
                for(Object obj : paramCodeList) {
                    IData data = (IData)obj;
                    paramCode.append("'" + data.getString("DATA_ID") + "',");
                }
//                params.put("PARAM_CODE","'CC_PHONE_FEE', 'CC_WDS_FEE', 'CC_PHONE_OUTFEE','CC_COMSHOW_FEE','CC_CRBT'");
                params.put("PARAM_CODE", paramCode.substring(0, paramCode.length()-1).toString());
                //查询电子协议信息
                IDataset elecContractInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryElecDataForFusercommunication", params);
                String ccPhoneFee1 = "";
                String ccPhoneOutfee1 = "";
                String ccWdsFee1 = "";
                String ccComshowFee1 = "";
                String ccCrbt1 = "";
                if(IDataUtil.isNotEmpty(elecContractInfos)) {
                    IData transferContractInfo = transferContractInfo(elecContractInfos);
                    ccPhoneFee1 = transferContractInfo.getString("CC_PHONE_FEE");
                    ccPhoneOutfee1 = transferContractInfo.getString("CC_PHONE_OUTFEE");
                    ccWdsFee1 = transferContractInfo.getString("CC_WDS_FEE");
                    ccComshowFee1 = transferContractInfo.getString("CC_COMSHOW_FEE");
                    ccCrbt1 = transferContractInfo.getString("CC_CRBT");
                }
                if(StringUtils.isNotBlank(ccCrbt1)) {
                  //是否订购多媒体彩铃服务
                    ccCrbt1 = "已订购";
                }
                String ccComshowFee2 = "";
                String ccCrbt2 = "";
                String ccPhoneFee2 = "";
                String ccPhoneOutfee2 = "";
                String ccWdsFee2 = "";
                // 3元来电显示资费
                params.put("DISCNT_CODE","22220101");
                IDataset discntInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryFinishDataInDiscnt", params);
                if(IDataUtil.isNotEmpty(discntInfos)) {
                    //是否订购3元来电显示
                    ccComshowFee2 = "3";
                }
                //多媒体彩铃服务
                params.put("SERVICE_ID","10122824");
                IDataset svcInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryFinishDataInSVC", params);
                if(IDataUtil.isNotEmpty(svcInfos)) {
                    //是否订购多媒体彩铃服务
                    ccCrbt2 = "已订购";
                }
                //多媒体桌面电话集团优惠 
                params.put("DISCNT_CODE","800109");
                IDataset discntAttrInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryFinishDataInUserAttr", params);
                if(IDataUtil.isNotEmpty(discntAttrInfos)) {
                    IData transferDiscntInfo = transferContractInfo(discntAttrInfos);
                    ccPhoneFee2 = transferDiscntInfo.getString("20000000");
                    ccPhoneOutfee2 = transferDiscntInfo.getString("20000001");
                    ccWdsFee2 = transferDiscntInfo.getString("20000002");
                }
                IData ccPhoneFeeData = dealTrueOrFalse(ccPhoneFee1, ccPhoneFee2, "国内通信费（元/月）");
                autoAuditInfos.add(ccPhoneFeeData);
                IData ccPhoneOutfeeData = dealTrueOrFalse(ccPhoneOutfee1, ccPhoneOutfee2, "超出后的国内通信费折扣");
                autoAuditInfos.add(ccPhoneOutfeeData);
                IData ccWdsFeeData = dealTrueOrFalse(ccWdsFee1, ccWdsFee2, "月功能费（元/月）");
                autoAuditInfos.add(ccWdsFeeData);
                IData ccComshowFeeData = dealTrueOrFalse(ccComshowFee1, ccComshowFee2, "来电显示费用（元/月）");
                autoAuditInfos.add(ccComshowFeeData);
                IData ccCrbtData = dealTrueOrFalse(ccCrbt1, ccCrbt2, "多媒体彩铃服务");
                autoAuditInfos.add(ccCrbtData);
            }
        }
        if(IDataUtil.isNotEmpty(autoAuditInfos)) {
            //判断所有资费是否正确
            for(Object object : autoAuditInfos) {
                IData auditData = (IData)object;
                boolean flag = auditData.getBoolean("AUTO_FLAG");
                if(!flag) {
                    TOTAL_FLAG = "false";
                    break;
                }
            }
            setAutoAuditInfos(autoAuditInfos);
        }
    }


    /**
     * @Title: queryOffer
     * @Description: 获取产品展示信息
     * @param offerList
     * @param offerList
     * @throws Exception
     * @author zhangzg
     * @date 2019年11月12日上午11:18:16
     */
    public void queryOffer(IDataset offerList) throws Exception
    {
        IDataset offerCodeList = new DatasetList();
        for (Object object : offerList)
        {
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
        }
        setOfferCInfo(offerCodeList.first());
    }

    /**
     * @Title: buildOfferStr
     * @Description: 处理查询字段 CODING_STR
     * @param quickorderCond
     * @param offerInitStr
     * @author zhangzg
     * @date 2019年11月12日上午10:11:08
     */
    private void buildOfferStr(IData quickorderCond, StringBuilder offerInitStr)
    {
        for (int i = COND_FIRST_NUM; i <= COND_LAST_NUM; i++)
        {
            if (StringUtils.isNotBlank(quickorderCond.getString("CODING_STR" + i)))
            {
                offerInitStr.append(quickorderCond.getString("CODING_STR" + i));
            }
        }
    }
    
    /**
     * 
    * @Title: analyslsOfferData 
    * @Description: 展示产品详情信息
    * @param cycle
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月12日下午4:39:40
     */
    public void analyslsOfferData(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String ecSerialNumber = param.getString("EC_SERIAL_NUMBER");
        IData ecCommonInfo = new DataMap(param.getString("EC_COMMON_INFO_DATA"));// 账户及付费关系信息
        IData mebofferInfo = new DataMap(param.getString("MEB_OFFER_DATA"));// 成员产品资费等信息
        IData ecOfferInfo = new DataMap(param.getString("EC_OFFER_DATA"));// 集团产品资费等信息
        IDataset offerMemberInfo = new DatasetList(param.getString("OFFER_MEMBER_DATA"));// 所有的成员手机号码
        String brandCode = ecOfferInfo.getString("BRAND_CODE");
        String productId = ecOfferInfo.getString("OFFER_CODE");
        if (StringUtils.isNotBlank(brandCode) && "ESPG".equals(brandCode))
        {
            IData params = new DataMap();
            params.put("SERIAL_NUMBER", ecSerialNumber);
            params.put("PRODUCT_ID", productId);
            IDataset userAttrDatas = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryEspProductInfosBySerialNumber", params);
            IDataset staticList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "DATA_NAME", new String[]
                    { "TYPE_ID" }, new String[]{ productId });
            if (IDataUtil.isNotEmpty(userAttrDatas) && IDataUtil.isNotEmpty(staticList))
            {
                Iterator<Object> attrIterator = userAttrDatas.iterator();
                while (attrIterator.hasNext())
                {
                    IData attrData = (IData) attrIterator.next();
                    String attrCode = attrData.getString("ATTR_CODE");
                    Iterator<Object> iterator = staticList.iterator();
                    boolean attrNameFlag = false;
                    while (iterator.hasNext())
                    {
                        IData data = (IData) iterator.next();
                        String dataId = data.getString("DATA_ID");
                        if (attrCode.equals(dataId))
                        {
                            attrNameFlag = true;
                            attrData.put("ATTR_NAME", data.getString("DATA_NAME"));
                            if (transferStaticValues(productId, attrCode))
                            {
                                String attrValue = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
                                        { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[]{ attrCode, attrData.getString("ATTR_VALUE") });
                                attrData.put("ATTR_VALUE", attrValue);
                            }
                            iterator.remove();
                            break;
                        }
                    }
                    if(!attrNameFlag) {
                        attrIterator.remove();
                    }
                }
                setEcOfferChaList(userAttrDatas);// 转换集团产品参数
            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(offerMemberInfo))
            {
                setMemberList(offerMemberInfo);// 展示成员手机号码
            }
            if (IDataUtil.isNotEmpty(ecCommonInfo))
            {
                acctEcCommon(ecCommonInfo);// 转换集团账户信息及付费信息
            }
            if (IDataUtil.isNotEmpty(mebofferInfo))
            {
                IDataset mebofferData = new DatasetList(mebofferInfo.getString("SUBOFFERS"));
                setMemberOfferList(mebofferData);// 转换集团账户信息及付费信息
            }
            if (IDataUtil.isNotEmpty(ecOfferInfo))
            {
                IDataset ecOfferChaList = new DatasetList(ecOfferInfo.getString("OFFER_CHA_SPECS"));
                // ESP产品处理
                String offerCode = ecOfferInfo.getString("OFFER_CODE");
                IData espInfo = new DataMap();
                espInfo.put("OFFER_CODE", offerCode);
                espInfo.put("BRAND_CODE", brandCode);
                setOfferInfo(espInfo);
                // 页面删除为空的参数
                Iterator<Object> ecOfferChaInfo = ecOfferChaList.iterator();
                while (ecOfferChaInfo.hasNext())
                {
                    IData ecOfferChaIf = (IData) ecOfferChaInfo.next();
                    String attrName = ecOfferChaIf.getString("ATTR_NAME");
                    String attrValue = ecOfferChaIf.getString("ATTR_VALUE");
                    if ("宽带信息".equals(attrName) || "专线操作方法名".equals(attrName) || "表格隐藏标记".equals(attrName))
                    {// 不展示宽带信息参数，没有存NAME，做过滤
                        ecOfferChaInfo.remove();
                    }
                    else if (StringUtils.isBlank(attrValue))
                    {
                        ecOfferChaInfo.remove();
                    }
                    // 转换下拉框枚举值
                    offerChaSpan(offerCode, ecOfferChaIf);
                }

                setEcOfferChaList(ecOfferChaList);// 转换集团产品参数

                IDataset grpPackageList = ecOfferInfo.getDataset("GRP_PACKAGE_INFO");

                if (IDataUtil.isNotEmpty(grpPackageList))
                {
                    setGrpPackageList(grpPackageList);// 转换集团定制信息
                }
            }
        }
    }
    
    /**
     * 
    * @Title: acctEcCommon 
    * @Description: 转换集团账户信息及付费信息 
    * @param ecCommonInfo
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月12日下午4:42:47
     */
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
    
    /**
     * 
    * @Title: offerChaSpan 
    * @Description: 转换下拉框枚举值 
    * @param offerCode
    * @param ecOfferChaIf
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月12日下午4:43:13
     */
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

    /**
     * 
    * @Title: submit 
    * @Description: 稽核提交 
    * @param cycle
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月13日上午9:24:17
     */
    public void submit(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        String offerCode = condData.getString("PRODUCT_ID");
        IDataset tables = new DatasetList(condData.getString("ROWDATAS"));
        IDataset otherInfos = new DatasetList();
        String ibsysId = tables.getData(0).getString("IBSYSID");
        String remark = condData.getString("REMARK");
        String bpmTempletId = condData.getString("BPM_TEMPLET_ID");
        String busiformId = condData.getString("BUSIFORM_ID");
        IData param = new DataMap();
        param.put("IBSYSID", ibsysId);
        param.put("BPM_TEMPLET_ID", bpmTempletId);
        // 若当前流程为子流程 则查询父流程
        IData tmpInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qrySubRelaInfoByTemplet", param);
        String productId = offerCode;
        if("VP66666".equals(productId)) {
            //一单清酒店ESP产品处理
            param.put("BUSIFORM_ID", busiformId);
            IDataset releInfos = CSViewCall.call(this, "SS.QryAuditInfoSVC.qryEweReleBySubBusiformId", param);
            if(IDataUtil.isEmpty(releInfos))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_ID:" + busiformId + ",查询主流程失败！");
            }
            String recodeNum = releInfos.first().getString("RELE_VALUE", "");
            param.put("RECORD_NUM", recodeNum);
            IData productInfo = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryProductByPk", param);
            if (IDataUtil.isEmpty(productInfo))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibsysId+"找不到TF_B_EOP_PRODUCT表的记录！");
            }
            productId = productInfo.getString("PRODUCT_ID");
        }else if(!"380700".equals(productId) && !"380300".equals(productId) && !"921015".equals(productId)){
            //非ESP单产品处理
            if (IDataUtil.isNotEmpty(tmpInfos))
            {
                param.put("BUSIFORM_ID", busiformId);
                IData eopProductInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryEopProductByIbsysidAndBusiformId", param);
                productId = eopProductInfos.getString("PRODUCT_ID");
            }
        }
        //获取稽核结果
        param.put("TARIFF_FLAG", condData.getString("TARIFF_FLAG"));
        //判断是否稽核通过
        Boolean getdiff = getdiff(param);
        IData other = new DataMap();
        other.put("IBSYSID", ibsysId);
        other.put("NODE_ID", "auditInfo");
        other.put("ATTR_CODE", "AUDITESOPINFO");
        //稽核不通过创建稽核流程
        if (!getdiff)
        {
            other.put("ATTR_VALUE", "2");
            // 创建流程
            IData submitData = new DataMap();
            IDataset offerChaList = new DatasetList();
            // 查询产品名
            IData offerInfo = IUpcViewCall.getOfferInfoByOfferCode(productId);
            for (int i = 0; i < tables.size(); i++)
            {
                IData table = tables.getData(i);
                IData MebOfferCha = new DataMap();
                MebOfferCha.put("USER_ID", table.getString("USER_ID"));
                MebOfferCha.put("OFFER_CODE", offerCode);
                MebOfferCha.put("PRODUCT_ID", offerCode);
                MebOfferCha.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
                MebOfferCha.put("OFFER_TYPE", "P");
                MebOfferCha.put("OFFER_ID", offerInfo.getString("OFFER_ID"));
                MebOfferCha.put("OPER_CODE", "2");
                offerChaList.add(MebOfferCha);
            }
            // 查询产品名
            IData offerMainInfo = IUpcViewCall.getOfferInfoByOfferCode(productId);
            IData offerChaLists = new DataMap();
            offerChaLists.put("SUBOFFERS", offerChaList);
            offerChaLists.put("OFFER_CODE", offerCode);
            offerChaLists.put("PRODUCT_ID", offerCode);
            offerChaLists.put("OFFER_NAME", offerMainInfo.getString("OFFER_NAME"));
            offerChaLists.put("OFFER_ID", offerMainInfo.getString("OFFER_ID"));

            // 订单级信息
            IData orderDate = new DataMap();
            IData orderMap = new DataMap();
            IData custMap = new DataMap();
            orderMap.put("IBSYSID", ibsysId);
            //查询流程订单信息 TF_B_EOP_SUBSCRIBE
            orderDate = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", orderMap);
            if (IDataUtil.isEmpty(orderDate))
            {
                orderDate = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformHSubscribeByIbsysid", orderMap);
            }
            orderDate.put("TITLE", orderDate.getString("FLOW_DESC"));
            String agreeResult = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
                { "URGENCY_LEVEL", orderDate.getString("FLOW_LEVEL") }); 
            orderDate.put("URGENCY_LEVEL", agreeResult);
            custMap.put("CUST_NAME", orderDate.getString("CUST_NAME"));
            custMap.put("GROUP_ID", orderDate.getString("GROUP_ID"));
            IDataset atttDatas = new DatasetList();
            IData atttData = new DataMap();
            atttData.put("TITLE", orderDate.getString("FLOW_DESC"));
            atttData.put("URGENCY_LEVEL", agreeResult);
            atttData.put("REMARK", remark);
            atttDatas = saveProductParamInfoFrontData(atttData);
            IData busiSpecRele = new DataMap();
            //拼接流程参数
            busiSpecRele.put("BPM_TEMPLET_ID", "MINORECAUDIT");
            busiSpecRele.put("BUSI_TYPE", "P");
            busiSpecRele.put("BUSI_CODE", offerCode);
            busiSpecRele.put("BUSIFORM_OPER_TYPE", "22");
            IData nodeInfo = new DataMap();
            nodeInfo.put("NODE_ID", "apply");
            nodeInfo.put("PRODUCT_ID", offerCode);
            IData commData = new DataMap();
            commData.put("PRODUCT_ID", offerCode);
            commData.put("BUSIFORM_OPER_TYPE", "22");
            commData.put("OPER_TYPE", "P");
            commData.put("IN_MODE_CODE", "0");
            commData.put("BUSI_CODE", offerCode);
            commData.put("BPM_TEMPLET_ID", "MINORECAUDIT");
            submitData.put("NODE_TEMPLETE", nodeInfo);
            submitData.put("BUSI_SPEC_RELE", busiSpecRele);
            submitData.put("ORDER_DATA", orderDate);
            submitData.put("OFFER_DATA", offerChaLists);
            submitData.put("COMMON_DATA", commData);
            submitData.put("EOMS_ATTR_LIST", atttDatas);
            submitData.put("CUST_DATA", custMap);
            IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
            IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", submitParam);
            IData traceMap = new DataMap();
            traceMap.put("IBSYSID", result.first().getString("IBSYSID"));
            traceMap.put("MAIN_IBSYSID", ibsysId);
            traceMap.put("BUSIFORM_ID", result.first().getString("BUSIFORM_ID"));
            traceMap.put("ATTR_MAIN_VALUE", busiformId);
            // 插入 关联流程信息变动表 TF_B_EOP_MODI_TRACE
            CSViewCall.call(this, "SS.QryAuditInfoSVC.insertModiTrace", traceMap);
            String newIbsysid = result.first().getString("IBSYSID");
            // 存入稽核信息 TF_B_EOP_ATTR
            IDataset auditParams = saveauditParamInfoFrontData(param, newIbsysid);
            for (int i = 0; i < auditParams.size(); i++)
            {
                IData data = auditParams.getData(i);
                CSViewCall.callone(this, "SS.WorkformAttrSVC.insertAttrAudtiInfo", data);
            }
            this.setAjax(result.first());
        }
        else
        {
            //稽核通过
            other.put("ATTR_VALUE", "1");
        }
        for (int i = 0; i < tables.size(); i++)
        {
            IData table = tables.getData(i);
            table.putAll(other);
            table.put("RECORD_NUM", table.getString("RECORD_NUM","0"));
            otherInfos.add(table);
            IData map = new DataMap();
            map.put("INFO_SIGN", infosign);
            map.put("INFO_CHILD_TYPE", "41");
            // 查询待阅
            IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map); 
            IData temp = new DataMap();
            temp.put("INFO_STATUS", "9");
            temp.put("INFO_ID", infoId.getString("INFO_ID"));
            // 将待阅设置为已读
            CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); 
        }

        IData maps = new DataMap();
        maps.put("OTHER_INFO", otherInfos);
        maps.put("USER_EPARCHY_CODE", getVisit().getLoginEparchyCode());
        // 入表TF_BH_EOP_OTHER
        CSViewCall.callone(this, "SS.WorkformOtherSVC.insertHotherInfo", maps);
    }

    /**
     * 
    * @Title: updateWorkForm 
    * @Description: 更新待阅
    * @throws Exception void
    * @author zhangzg
    * @date 2019年11月14日上午10:25:01
     */
    public void updateWorkForm() throws Exception
    {
        IData map = new DataMap();
        map.put("INFO_SIGN", infosign);
        map.put("INFO_CHILD_TYPE", "41");
        IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map); // 存attr表
        IData temp = new DataMap();
        temp.put("INFO_STATUS", "9");
        temp.put("INFO_ID", infoId.getString("INFO_ID"));
        CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); // 设置为已读待阅
    }

    /**
     * 
    * @Title: getdiff 
    * @Description: 循环结果判断是否稽核通过 
    * @param param
    * @return
    * @throws Exception Boolean
    * @author zhangzg
    * @date 2019年11月13日上午9:26:11
     */
    public Boolean getdiff(IData param) throws Exception
    {
        Iterator<String> itr = param.keySet().iterator();
        while (itr.hasNext())
        {
            String attrCode = itr.next();
            String attrValue = param.getString(attrCode);
            if ("0".equals(attrValue))
            {
                return false;
            }
        }
        return true;

    }
    
    /**
    * @Title: transferContractInfo 
    * @Description: 行转列 转换ATTR表查询结果集 
    * @param contractList
    * @return IData
    * @author zhangzg
    * @date 2019年11月21日下午5:44:13
     */
    private static IData transferContractInfo(IDataset contractList) {
        IData contractcData = new DataMap();
        for(Object obj : contractList) {
            IData data = (IData)obj;
            contractcData.put(data.getString("ATTR_CODE"), data.getString("ATTR_VALUE"));
        }
        return contractcData;
    }
    
    /**
     * 
    * @Title: dealTrueOrFalse 
    * @Description: 自动稽核处理数据 
    * @param elecParam
    * @param dataParam
    * @param name
    * @return IData
    * @author zhangzg
    * @date 2019年11月21日下午8:59:22
     */
    private static IData dealTrueOrFalse(String elecParam, String dataParam, String name) {
        IData result = new DataMap();
        String flag = "false";
        if(StringUtils.isNotBlank(elecParam) && StringUtils.isNotBlank(dataParam) && elecParam.equals(dataParam)) {
            flag = "true";
        }
        result.put("AUDIT_NAME", name);
        result.put("DATA_PARAM", dataParam);
        result.put("ELEC_PARAM", elecParam);
        result.put("AUTO_FLAG", flag);
        return result;
    }

    /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfoFrontData(IData resultSetDataset) throws Exception
    {
        IDataset productParamAttrset = new DatasetList();
        Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttr.put("RECORD_NUM", "0");
            productParamAttrset.add(productParamAttr);

        }
        return productParamAttrset;

    }

    /**
     * 转换稽核信息
     */
    public static IDataset saveauditParamInfoFrontData(IData resultSetDataset, String ibsysid) throws Exception
    {
        IDataset auditParamAttrset = new DatasetList();
        Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData auditParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            auditParamAttr.put("ATTR_CODE", key);
            auditParamAttr.put("ATTR_VALUE", value);
            auditParamAttr.put("RECORD_NUM", "0");
            auditParamAttr.put("NODE_ID", "apply");
            auditParamAttr.put("IBSYSID", ibsysid);
            auditParamAttrset.add(auditParamAttr);

        }
        return auditParamAttrset;

    }
    
    public static boolean transferStaticValues(String productId, String attrCode)
    {
        if ("380700".equals(productId))
        {
            if ("380700001".equals(attrCode) || "380700002".equals(attrCode) || "380700007".equals(attrCode) || "380700006".equals(attrCode) 
                    || "380700008".equals(attrCode) || "3807000013".equals(attrCode))
            {
                return true;
            }
        }
        else if ("380300".equals(productId))
        {
            if ("3803000011".equals(attrCode) || "3803000006".equals(attrCode) || "3803000007".equals(attrCode) || "3803000008".equals(attrCode) 
                    || "3803000014".equals(attrCode))
            {
                return true;
            }

        }
        else if ("921015".equals(productId))
        {
            if ("92100150006".equals(attrCode) || "92100150007".equals(attrCode))
            {
                return true;
            }
        }
        return false;
    }

    public abstract void setInfo(IData info);

    public abstract void setExpInfo(IData info);

    public abstract void setContractInfos(IDataset contractInfos);

    public abstract void setContractInfo(IData contractInfo);

    public abstract void setOrderInfos(IDataset orderInfos);

    public abstract void setOrderInfo(IData orderInfo);

    public abstract void setCondition(IData condition);

    public abstract void setContractList(IDataset contractList) throws Exception;
    
    public abstract void setContractNameList(IDataset contractList) throws Exception;

    public abstract void setContractData(IData contractData) throws Exception;
    
    public abstract void setOfferCInfo(IData offerData) throws Exception;

    public abstract void setOfferCodeList(IDataset offerList) throws Exception;
    
    public abstract void setMemberList(IDataset memberList) throws Exception;
    
    public abstract void setMemberOfferList(IDataset memberOfferList) throws Exception;
    
    public abstract void setOfferInfo(IData info) throws Exception;
    
    public abstract void setEcOfferChaList(IDataset ecOfferChaList) throws Exception;
    
    public abstract void setGrpPackageList(IDataset grpPackageList) throws Exception;
    
    public abstract void setEcCommonList(IDataset ecCommonList) throws Exception;
    
    public abstract void setAutoAuditInfo(IData autoAuditInfo) throws Exception;
    
    public abstract void setAutoAuditInfos(IDataset autoAuditInfos) throws Exception;
}
