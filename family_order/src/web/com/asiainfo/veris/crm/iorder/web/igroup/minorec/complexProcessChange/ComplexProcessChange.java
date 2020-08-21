package com.asiainfo.veris.crm.iorder.web.igroup.minorec.complexProcessChange;

import java.util.Iterator;

import com.ailk.common.data.IDataOutput;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class ComplexProcessChange extends EopBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {

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

    /**
     * 通过客户名称模糊查询集团客户信息
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByCustName(IRequestCycle cycle) throws Exception
    {
        String custName = getData().getString("CUST_NAME");

        IDataOutput output = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, custName, getPagination());
        IDataset datas  = output.getData();

        if(IDataUtil.isEmpty(datas))
        {
            CSViewException.apperr(GrpException.CRM_GRP_1, custName);
        }
        setCustGroupList(datas);

    }

    /**
     * 查询流程信息
     * 
     * @Author liqian11
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

    public void setOperType(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData dataOfferCode = new DataMap(getData().getString("DATE_OFFERCODE"));// 通过选择合同获取的offerCode
        String userIdAs = pageData.getString("USER_ID_AS");// 已有协议的所有userId
        String productIdAs = pageData.getString("PRODUCT_ID_AS");// 已有协议的所有产品ID
        String offerCodes = pageData.getString("CONTRACT_PRODUCT_ID");// 合同传过来的产品ID
        String templetId = pageData.getString("TEMPLET_ID");// 流程ID
        String templetName = StaticUtil.getStaticValue("MINOREC_BPM_TEPMENTID_CHANGE", templetId);// 流程名
        IDataset offerCodeList = new DatasetList();
        String[] offerCodeInfo = offerCodes.split(",");
        String[] userIdAsInfo = userIdAs.split(",");
        String[] productIdAsInfo = productIdAs.split(",");
        String offerCode = "";
        String offerCodeZ = "";
        // 冒泡排序,把多媒体桌面电话放到V网前面
        int[] intOfferCode = intOfferCodeSort(offerCodeInfo);

        for (int i = 0; i < intOfferCode.length; i++) {
            offerCode = Integer.toString(intOfferCode[i]);
            String userId = "";
            for (int j = 0; j < productIdAsInfo.length; j++) {// 通过已有协议，获取产品的userId
                String offerCodeAs = productIdAsInfo[j];
                if (offerCode.equals(offerCodeAs)) {
                    userId = userIdAsInfo[j];
                }
            }
            
            IData grpUserProduct = new DataMap();
            IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
            grpUserProduct.put("OFFER_CODE", offer.getString("OFFER_CODE"));
            grpUserProduct.put("OFFER_ID", offer.getString("OFFER_ID"));
            grpUserProduct.put("OFFER_NAME", offer.getString("OFFER_NAME"));
            offerCodeList.add(grpUserProduct);
            
            if ("FUSECOMMUNICATIONCHANGE".equals(templetId)) {
                offerCode = "VP998001";
            } else if ("YIDANQINGSHANGPUCHANGE".equals(templetId) || "8000".equals(offerCode)) {
                offerCode = "VP99999";
            } else if ("YIDANQINGJIUDIAANCHANGE".equals(templetId)) {
                offerCode = "VP66666";
            }
        }
        // 查询流程信息
        operTypeByTempletId("COMPLEXPROCESSCHANGE", offerCode, templetName);

        // 根据操作类型，界面展示不同的信息
        //pattrOfferData(productIdAs, templetId);

        IData productInfo = new DataMap();
        productInfo.put("PARAM_OFFER", "2");
        productInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
        productInfo.put("ENTERPRISEBROADBAND", offerCodeZ);
        productInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
        productInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
        productInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
        productInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));
        
        setPattrInfo(productInfo);
        setOfferList(offerCodeList);
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

    
    public void pattrOfferData(String productIdAs, String templetId) throws Exception {

        IData productInfo = new DataMap();
        productInfo.put("PARAM_OFFER", "2");
        productInfo.put("OFFER_CODES", productIdAs);
        if ("q".equals(templetId)) {
            productInfo.put("PAM_URGENCY_LEVEL", "DSB");
        }

        setPattrInfo(productInfo);
    }
    
    /**
     * 获取支付方式等
     * 
     * @Author liqian11
     * @throws Exception
     */
    public void mergeWideUserStyleCheck(String offerCodeZ) throws Exception {

        // 付费模式权限控制
        IDataset widenetPayMode = StaticUtil.getStaticList("WIDENET_PAY_MODE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDENET_PAY_MODE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(widenetPayMode)) {
                for (int k = 0; k < widenetPayMode.size(); k++) {
                    if ("A".equals(widenetPayMode.getData(k).getString("DATA_ID"))) {
                        widenetPayMode.remove(k);
                        break;
                    }
                }
            }
        }

        // 宽带开户方式权限控制
        IDataset mergeWideUserStyleInfos = StaticUtil.getStaticList("HGS_WIDE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HGS_WIDE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(mergeWideUserStyleInfos)) {
                for (int k = 0; k < mergeWideUserStyleInfos.size(); k++) {
                    if ("1".equals(mergeWideUserStyleInfos.getData(k).getString("DATA_ID"))) {
                        mergeWideUserStyleInfos.remove(k);
                        break;
                    }
                }
            }
        }

        setMergeWideUserStyleList(mergeWideUserStyleInfos);
        setWidenetPayModeList(widenetPayMode);
    }

    /**
     * 获取宽商务宽带开户特殊处理，号码需要在原号码后加0000，并递增
     * 
     * @Author liqian11
     * @throws Exception
     */
    public void getWideSerialNumber(IDataset addressList, String serNumber, String custId) throws Exception {

        IDataset wideAddressList = new DatasetList();// 把没有宽带号码的放到一个集合里面
        IDataset wideAddressList1 = new DatasetList();// 把已有宽带号码的放到一个集合里面
        IData data = new DataMap();
        String serialNumb = "";// 获取已有的最大宽带号码
        for (int i = 0; i < addressList.size(); i++) {
            IData adderssInfo = addressList.getData(i);
            String serialNumber = adderssInfo.getString("WIDE_SERIAL_NUMBER", "");
            if (StringUtils.isBlank(serialNumber)) {
                wideAddressList.add(adderssInfo);
                continue;
            } else {
                wideAddressList1.add(adderssInfo);
                for (int j = 0; j < addressList.size(); j++) {
                    IData adderssInfoJ = addressList.getData(j);
                    String SerialNumberJ = adderssInfoJ.getString("WIDE_SERIAL_NUMBER", "");
                    if (StringUtils.isNotBlank(SerialNumberJ) && SerialNumberJ.compareTo(serialNumber) > 0) {
                        serialNumb = SerialNumberJ;
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(wideAddressList)) {
            int serNumberSize = wideAddressList.size();
            data.put("WIDE_SERIAL_NUMBER", serialNumb);
            data.put("SERIAL_NUMBER", serNumber);
            data.put("SERNUMBER_SIZE", serNumberSize);
            data.put("OPER_TYPE", "crtCg");
            data.put("CUST_ID", custId);
            // 获取新的宽带号码
            IDataset wideSNdataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWideSerialNumberMinorec", data);
            // 给新增号码赋值
            if (IDataUtil.isNotEmpty(wideSNdataset)) {
                for (int i = 0; i < wideSNdataset.size(); i++) {
                    IData sideSnInfo = wideSNdataset.getData(i);
                    String sideSerNumber = sideSnInfo.getString("SERIAL_NUMBER");
                    IData wideAddressInfo = wideAddressList.getData(i);
                    wideAddressInfo.put("WIDE_SERIAL_NUMBER", sideSerNumber);
                }
            }
            wideAddressList1.addAll(wideAddressList);// 拼接已有宽带与新增宽带号码
            setWidenetInfos(wideAddressList1);
        } else {
            // 如果都有宽带号码直接返回
            setWidenetInfos(addressList);
        }
    }

    /**
     * 获取宽带产品
     * 
     * @Author liqian11
     * @throws Exception
     */
    public void changeWideProductType(String openType, String serNumber) throws Exception {

        IData data = new DataMap();
        data.put("ROUTE_EPARCHY_CODE", "0898");
        data.put("FLAG", "MINOREC");
        data.put("SERIAL_NUMBER", serNumber);
        if ("铁通ADSL".equals(openType)) {
            data.put("WIDE_PRODUCT_TYPE", "2");
        } else if ("移动FTTH".equals(openType) || "铁通FTTH".equals(openType)) {
            data.put("WIDE_PRODUCT_TYPE", "5");
        } else if ("移动FTTB".equals(openType) || "铁通FTTB".equals(openType)) {
            data.put("WIDE_PRODUCT_TYPE", "6");
        }
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);

        setProductList(dataset);
    }


    public void queryEcAccountList(IRequestCycle cycle) throws Exception {
        String custId = this.getData().getString("CUST_ID");
        IDataset accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
        setEcAccountList(accounts);
    }

    public void initOfferCha(IRequestCycle cycle) throws Exception {
        String offerId = getData().getString("OFFER_ID");
        String ecMebType = getData().getString("EC_MEB_TYPE");
        String operCode = getData().getString("OPER_CODE");

        String operType = PageDataTrans.transOperCodeToOperType(operCode, ecMebType);

        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId, UpcConst.QUERY_COM_CHA_YES);
        if (IDataUtil.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + offerId + "没有查询到商品信息！");
        }

        String ifCentreType = getData().getString("IF_CENTRETYPE", "");
        if ("2".equals(ifCentreType)) {// 8000产品即可为普通v网也可为融合V网，两者参数不一样
                                       // 如果是融合V网，为了跟普通V网区分，加Centre
            operType = "Centre" + operType;
        }

        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", offerId); // POINT_ONE
        inAttr.put("NODE_ID", operType); // POINT_TWO

        if ("BOSG".equals(offerInfo.getString("BRAND_CODE"))) {
            // BBOSS本地产品编码转换为全网产品编码
            IData input = new DataMap();
            String merchOperType = this.getData().getString("MERCHP_OPER_TYPE");
            input.put("OPER_TYPE", inAttr.getString("NODE_ID"));
            input.put("PROD_SPEC_ID", offerInfo.getString("OFFER_CODE"));
            input.put("MERCHP_OPER_TYPE", merchOperType);
            FrontProdConverter.prodConverter(this, input, false);

            // 操作类型转换为全网操作类型
            inAttr.put("FLOW_ID", input.getString("PROD_SPEC_ID"));// POINT_ONE
            inAttr.put("NODE_ID", input.getString("OPER_TYPE"));// POINT_TWO
        }

        setInAttr(inAttr);

        if ("EC".equals(ecMebType)) {
            queryEcOfferChaValue(getData(), operType, offerInfo);
        } else if ("MEB".equals(ecMebType)) {
            queryMebOfferChaValue(getData(), operType, offerInfo);
        }
    }

    private void queryEcOfferChaValue(IData pageData, String operType, IData curOffer) throws Exception {
        String ecOfferCode = pageData.getString("EC_OFFER_CODE");
        String offerCode = pageData.getString("OFFER_CODE");

        String curOfferCode = curOffer.getString("OFFER_CODE"); // 当前设置属性的商品
        String curOfferType = curOffer.getString("OFFER_TYPE"); // 当前设置属性的商品
        String brandCode = curOffer.getString("BRAND_CODE");

        String idType = "S";
        if (((curOfferCode.equals(offerCode)) && !("S".equals(curOfferType))) || "BOSG".equals(brandCode)) {
            idType = "P";
        }
        String svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, idType, operType, "InitOfferCha");

        IDataset grpItemInfo = new DatasetList();
        if (StringUtils.isBlank(svcName)) {// 没有配置，取默认服务初始化
            if (BizCtrlType.CreateUser.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            } else if (BizCtrlType.ChangeUserDis.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE=" + operType);
            }
        }

        String offerId = pageData.getString("OFFER_ID");
        String offerType = pageData.getString("OFFER_TYPE");
        String custId = pageData.getString("CUST_ID");
        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("OFFER_ID", offerId);
        input.put("ATTR_OBJ", "0");
        input.put("EPARCHY_CODE", getTradeEparchyCode());
        // input.put("USER_ID", subscriberInsId);
        // input.put("OFFER_INS_ID", offerInsId);
        input.put("INST_TYPE", offerType);// USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", "false");
        input.put("CUST_ID", custId);
        input.put("PRODUCT_ID", offerCode);
        input.put("OFFER_CODE", ecOfferCode);// 方便ADC对特殊产品进行判断,对逻辑无影响
        input.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = CSViewCall.call(this, svcName, input);
        if (IDataUtil.isNotEmpty(result)) {
            IData groupParamData = result.getData(0);
            IData attrGroupData = groupParamData.getData("ATTR_GROUP_MAP");
            if ("BOSG".equals(brandCode) && IDataUtil.isNotEmpty(attrGroupData)) {
                if (IDataUtil.isNotEmpty(attrGroupData)) {
                    Iterator itr = attrGroupData.keySet().iterator();
                    while (itr.hasNext()) {
                        String key = itr.next().toString();
                        IDataset attrInfos = attrGroupData.getDataset(key);
                        for (int i = 0; i < attrInfos.size(); i++) {
                            IData attrInfo = attrInfos.getData(i);
                            IData paramInfo = new DataMap();

                            paramInfo.put("CHA_SPEC_ID", attrInfo.getString("FIELD_NAME").split("_")[0]);
                            paramInfo.put("ATTR_VALUE", attrInfo.getString("ATTR_VALUE"));
                            paramInfo.put("ATTR_GROUP", attrInfo.getString("ATTR_GROUP"));
                            paramInfo.put("ATTR_CODE", attrInfo.getString("FIELD_NAME").split("_")[0]);

                            grpItemInfo.add(paramInfo);
                        }
                    }
                }
            } else {
                IData param = result.getData(0);
                Iterator itr = param.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().toString();
                    if (key.contains("_")) {
                        String[] keys = key.split("♂♂");
                        if (keys.length > 1) {
                            key = keys[0];
                        }
                    }
                    IData paramData = param.getData(key);
                    String value = param.getString(key);
                    // 针对属性组的情况进行特殊处理
                    if (value.contains("♂♂")) {
                        value = paramData.getString("VALUE");
                        String[] groupItem = value.split("♂♂");
                        if (groupItem.length > 1) {
                            value = groupItem[0];
                            String groupAttr = groupItem[1];
                            IData paramInfo = new DataMap();
                            paramInfo.put("CHA_SPEC_ID", key);
                            paramInfo.put("CHA_VALUE", value);
                            paramInfo.put("GROUP_ATTR", groupAttr);
                            grpItemInfo.add(paramInfo);
                        }
                    }
                    busi.put(key, paramData);
                }
            }
        }
        setBusi(busi);
    }

    private void queryMebOfferChaValue(IData pageData, String operType, IData curOffer) throws Exception {
        String ecOfferCode = pageData.getString("EC_OFFER_CODE");
        String offerId = pageData.getString("OFFER_ID");
        String offerCode = pageData.getString("OFFER_CODE");
        String brandCode = pageData.getString("BRAND_CODE");

        // BBoss 静态表加载的数据 通过 产品编码 拿到商品编码
        if ("BOSG".equals(brandCode)) {
            IDataset upOfferIdList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null, offerId, "4");
            if (IDataUtil.isNotEmpty(upOfferIdList)) {
                String upOfferId = upOfferIdList.first().getString("OFFER_ID");
                offerCode = IUpcViewCall.getOfferCodeByOfferId(upOfferId);
            }
        }

        // 初始化产品特征(非静态表加载的数据)
        // 存在既有特殊产品参数又有服务参数的情况,产品参数配置P,服务参数配置S,通过当前设置的商品类型来做区分
        String idType = "S".equals(curOffer.getString("OFFER_TYPE")) ? "S" : "P";
        String svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, idType, operType, "InitOfferCha");
        if (StringUtils.isBlank(svcName)) {// 没有配置，取默认服务初始化
            if (BizCtrlType.CreateMember.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE=" + operType);
            }
        }

        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("EC_OFFER_ID", pageData.getString("EC_OFFER_ID")); // 集团主商品编码
        input.put("OFFER_ID", offerId);
        input.put("OFFER_CODE", ecOfferCode);
        input.put("ATTR_OBJ", "1"); // 成员是1
        // input.put("EPARCHY_CODE", userEparchyCode);
        input.put("MEM_OFFER_ID", pageData.getString("MEB_OFFER_ID"));
        // input.put("USER_ID", subscriberInsId);
        // input.put("EC_USER_ID", ecSubscriberInsId);
        input.put("CUST_ID", pageData.getString("CUST_ID"));
        // input.put("OFFER_INS_ID", pageData.getString("OFFER_INS_ID"));
        input.put("INST_TYPE", pageData.getString("OFFER_TYPE"));// USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", true);
        input.put("GROUP_ID", pageData.getString("GROUP_ID"));
        input.put("SUB_OFFER_CODE", offerCode);
        input.put(Route.ROUTE_EPARCHY_CODE, "0898");
        input.put("OPER_TYPE", operType);
        input.put("EC_OFFER_CODE", ecOfferCode);
        input.put("EcIntegrateOrder", true);
        IDataset result = CSViewCall.call(this, svcName, input);

        if (IDataUtil.isNotEmpty(result)) {
            IData param = result.getData(0);
            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().toString();
                IData paramData = param.getData(key);

                busi.put(key, paramData);
            }
        }
        setBusi(busi);
    }

    public void queryArchivesInfo(IRequestCycle cycle) throws Exception {
        IData param = this.getData();
        String groupId = param.getString("GROUP_ID");
        String templetId = param.getString("TEMPLET_ID");
        // 获取虚拟的产品ID
        String xnProductId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "PDATA_ID" }, "DATA_ID", new String[] { "MINOREC_XN_PRODUCT", templetId });
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("XN_PRODUCTID", xnProductId);
        IDataset archivesList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryArchivesInfo", param);

        setArchivesList(archivesList);

    }
    
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
        // inParam.put("FLAG", "1");
        // IDataset info = CSViewCall.call(this, "SS.QcsGrpIntfSVC.qryStaffinfoForESOPNEW", inParam);

    }
    
    public void changOperTypeSet(IRequestCycle cycle) throws Exception {
        IData param = this.getData();
        String templetId = param.getString("TEMPLET_ID");
        IDataset operTypeList = pageutil.getList("TD_S_STATIC", "DATA_ID", "DATA_NAME", "TYPE_ID", "MINOREC_CHANGE_OPER_TYPE");
        Iterator<Object> operTypeInfo = operTypeList.iterator();
        while (operTypeInfo.hasNext()) {
            IData operTypeData = (IData) operTypeInfo.next();
            String dataId = operTypeData.getString("DATA_ID");
            if ("ENTERPRISEBROADBANDCHANGE".equals(templetId) && "CrtUser".equals(dataId)) {
                operTypeInfo.remove();
            } else if ("FUSECOMMUNICATIONCHANGE".equals(templetId) && "ChgWn".equals(dataId)) {
                operTypeInfo.remove();
            } else if (("ChgWn".equals(dataId) || "CrtUser".equals(dataId)) && ("SUMBUSINESSTVCHANGE".equals(templetId) || "CLOUDWIFICHANGE".equals(templetId) || "CLOUDTAVERNCHANGE".equals(templetId))) {
                operTypeInfo.remove();
            }

        }
        setOperTypeList(operTypeList);
    }
    
    /**
     * 复杂流程存data表合同信息
     */
    @Override
    public void buildOtherSvcParam(IData submmitParam) throws Exception
    {
    	MinorecIntegrateTrans.transformCpcSubByChangeApply(submmitParam);

    }

    public abstract void setSaleActiveList(IData elementInfo) throws Exception;

    public abstract void setWideInfo(IData wideInfo) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset selectedElement) throws Exception;

    public abstract void setOperTypeList(IDataset operTypeList) throws Exception;

    public abstract void setArchivesList(IDataset archivesList) throws Exception;

    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList) throws Exception;

    public abstract void setWidenetPayModeList(IDataset templetId) throws Exception;

    public abstract void setProductList(IDataset productList) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setOfferList(IDataset offerList) throws Exception;

    public abstract void setWidenetInfos(IDataset widenetInfos) throws Exception;

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setCustGroupList(IDataset custGroupList) throws Exception;

    public abstract void setCustGroupInfo(IData custGroupInfo) throws Exception;

}
