package com.asiainfo.veris.crm.iorder.web.igroup.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.EcBbossCommonViewUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.ecbasepage.EcBasePage;
import org.apache.tapestry.IRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class EcBbossManage extends EcBasePage
{
    private static final Logger logger = LoggerFactory.getLogger(EcBbossManage.class);

    public abstract void setCondition(IData cond);

    public abstract void setPricePlans(IDataset pricePlans);

    public abstract void setOptionalOffers(IDataset optionalOffers);

    public abstract void setManageInfo(IDataset manageInfo);// 管理信息

    public abstract void setPOProductPlus(IDataset pOProductPlus);

    public abstract void setBossManageInfo(IDataset bossManageInfo);

    public abstract void setManage(IData manage);

    public abstract void setBbossIssuedInfo(IDataset bbossIssuedInfo);

    public abstract void setIssuedInfo(IData issuedInfo);

    public abstract void setProdSpecs(IDataset prodSpecs);

    public abstract void setServiceOfferList(IDataset serviceOfferList);

    public abstract void setInfo(IData info);

    public abstract void setInAttr(IData inAttr);

    public abstract void setBusi(IData busi);

    public abstract void setEsop(IData esop);

    public abstract void setInfos(IDataset dataset);

    public abstract void setInfosCount(long count);

    /**
     * @param cycle
     * @throws Exception
     * @author chengjian
     * 2015-11-06
     * @Description bboss管理节点界面初始化
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData("cond", true);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        String workId = param.getString("WORK_ID", "");
    	
    	IData temp = new DataMap();
    	temp.put("TYPE_ID", "TD_S_MANAGE_STATE");
        
        if (StringUtils.isEmpty(ibsysid) && StringUtils.isEmpty(workId))
        {

            //1.初始化商品名称下拉框数据
        	EcBbossCommonViewUtil.qryMerchInfos(this,initdata);
        	
        	//2.初始化状态
        	IDataset staticInfos  = EcBbossCommonViewUtil.qryStaticInfos(this, temp);
        	initdata.put("cond_STATES", staticInfos);
        	
            this.setCondition(initdata);
        }
        else
        {
//            String offerId = param.getString("OFFER_ID", "");
//            IDataset productInfos = EcUpcViewUtil.queryOfferComRelOfferByOfferId(offerId);
//            String productId = productInfos.getData(0).getString("OFFER_ID");
//            IData temp = new DataMap();
//            temp.put("IBSYSID", ibsysid);
//            String intactItemId = "";
//            String subscriberInsId = "";
//            IData esopData = ServiceCaller.call("OrderCentre.enterprise.process.IEsopInitInForERPSV.queryManagerInfo", temp);
//            if (DataUtil.isNotEmpty(esopData.getDataset("DATAS")))
//            {
//                intactItemId = esopData.getDataset("DATAS").getData(0).getString("INTACT_ITEM_ID");
//                subscriberInsId = esopData.getDataset("DATAS").getData(0).getString("SUBSCRIBER_INS_ID");
//            }
//
//            IData inparam = new DataMap();
//            inparam.put("IBSYSID", ibsysid);
//            inparam.put("GROUP_ID", param.getString("GROUP_ID"));
//            inparam.put("OFFER_ID", offerId);
//            inparam.put("PRODUCT_ID", productId);
//            inparam.put("INTACT_ITEM_ID", intactItemId);
//            inparam.put("SUBSCRIBER_INS_ID", subscriberInsId);
//            inparam.put("OPERATE_FLAG", BbossConsts.MANAGE_NODE_NOT_OPER); //默认为未操作
//
//
//            IData result = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryBbossManageInfos", inparam);
//            if (DataUtil.isEmpty(result.getDataset("DATAS")))
//            {
//                BizException.bizerr(OfferException.CRM_OFFER_9);
//            }
//            setInfos(result.getDataset("DATAS"));
//
//            IDataset esopDataset = new DatasetList();
//            IData esop_data = new DataMap();
//            esop_data.put("IS_ESOP", "true");
//            esop_data.put("IBSYSID", ibsysid);
//            esop_data.put("NODE_ID", param.getString("NODE_ID", ""));
//            esop_data.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID", ""));
//            esop_data.put("FLOW_MAIN_ID", param.getString("FLOW_MAIN_ID", ""));
//            esop_data.put("WORK_ID", param.getString("WORK_ID", ""));
//            esop_data.put("TRADE_ID", param.getString("TRADE_ID", ""));
//            esop_data.put("PRODUCT_ID", param.getString("PRODUCT_ID", ""));
//            esop_data.put("OFFER_ID", param.getString("OFFER_ID", ""));
//            esop_data.put("SERIALNO", param.getString("SERIALNO", ""));
//            esop_data.put("GROUP_ID", param.getString("GROUP_ID", ""));
//            esopDataset.add(esop_data);
//            setEsop(esop_data); // 管理报文反馈接口回调esop用
//
//            IData condition = new DataMap();
//            condition.put("IS_ESOP", true);
//            condition.put("GROUP_ID", param.getString("GROUP_ID"));
//            condition.put("POSPECNUMBER", EcBbossCommonViewUtil.merchToProduct(offerId, 1, true));
//            condition.put("PRODUCTSPECNUMBER", EcBbossCommonViewUtil.merchToProduct(productId, 1, true));
//            setMerchInfos(condition);
//            setProductInfos(condition);
//            setCondition(condition);
        }

    }

    /**
     * @param cycle
     * @throws Exception
     * @author chengjian
     * @Description bboss管理流程数据查询显示
     */
//    public void qryInfos(IRequestCycle cycle) throws Exception
//    {
//        IData param = getData("cond", true);
//
//        String pospec_number = param.getString("POSPECNUMBER");
//        String state = param.getString("OPERATE_FLAG");
//        String group_id = param.getString("GROUP_ID");
//        String productspec_number = param.getString("PRODUCTSPECNUMBER");
//        String start_date = param.getString("START_DATE");
//        String end_date = param.getString("END_DATE");
//
//
//        IData inparam = new DataMap();
//        inparam.put("GROUP_ID", group_id);
//        inparam.put("POSPECNUMBER", pospec_number);
//        inparam.put("PRODUCTSPECNUMBER", productspec_number);
//        inparam.put("START_DATE", start_date);
//        inparam.put("END_DATE", end_date);
//        inparam.put("OPERATE_FLAG", state);
//
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryBbossManageInfos", inparam, this.getPagination());
//        if (DataUtil.isEmpty(result.getDataset("DATAS")))
//        {
//            BizException.bizerr(OfferException.CRM_OFFER_9);
//        }
//        setInfos(result.getDataset("DATAS"));
//        setInfosCount(result.getLong("X_RESULTCOUNT", 0));
//    }

    /**
     * @param cycle
     * @throws Exception
     * @author chengjian
     * 2015-11-06
     * @Description 产品名称下拉框联动性
     */
    public void setProductInfos(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);

    	//商品名称下拉框联动性产品信息
   	    EcBbossCommonViewUtil.qryProductInfos(this,inparam);
        this.setCondition(inparam);
    }

//
//    /**
//     * @throws Exception
//     * @author chengjian
//     * 2015-11-06
//     * @Description 管理节点操作状态显示
//     */
//    public void setFrontData(IData data) throws Exception
//    {
//        IData inparam = new DataMap();
//        IData result = ServiceCaller.call("CS.StaticInfoQrySVC.qryStaticListByTypeId", inparam, getPagination());
//
//        data.put("STATES_INFO", result.getDataset("STATES_INFO"));
//    }
//
//    /**
//     * 根据销售品编码加载必选产品规格和定价计划
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void showManageInfos(IRequestCycle cycle) throws Exception
//    {
//        IData idata = getData();
//        String offerId = idata.getString("SUB_OFFER_ID");
//        String orderLineId = idata.getString("ORDER_LINE_ID");
//        String subscriberInsId = idata.getString("SUBSCRIBER_INS_ID");
//        String flowInfo = idata.getString("FLOW_INFO");
//        String operateflag = idata.getString("OPERATE_FLAG");
//        String operCode = idata.getString("OPER_CODE");
//
//        IData info = new DataMap();
//
//        //判断子商品的情况 资费、服务
//        IDataset insSubOfferList = dealManageInsSubOffer(orderLineId,subscriberInsId);
//
//        IData ajaxData =querySubOfferList(offerId,operCode,"1",new String[]{""},info);
//        IDataset subOffers = ajaxData.getDataset("SUBOFFERS");
//
//        dealSubOfferData(insSubOfferList, subOffers);
////        setServiceOfferList(subOffers);
//        subOffers.addAll(insSubOfferList);
//
//        setInfo(info);
//
//        queryBbossManageAttrInfos(offerId, orderLineId, flowInfo, operateflag);
//
//        this.setAjax(ajaxData);
//    }
//
//    private void dealSubOfferData(IDataset insSubOfferList, IDataset subOffers)
//    {
//        for (int i = 0; i < insSubOfferList.size(); i++)
//        {
//            IData insSubOffer = insSubOfferList.getData(i);
//            Iterator offerIter = subOffers.iterator();
//            while (offerIter.hasNext())
//            {
//                IData subOffer =(IData) offerIter.next();
//                if (StringUtils.equals(subOffer.getString("OFFER_ID"),insSubOffer.getString("OFFER_ID")) && "0".equals(subOffer.getString("SELECT_FLAG")))
//                {
//                    subOffers.remove(subOffer);
//                    insSubOffer.put("SELECT_FLAG","0");
//                    break;
//                }
//            }
//        }
//    }
//
//    private IDataset dealManageInsSubOffer(String orderLineId, String subscriberInsId) throws Exception
//    {
//
//        //获取台账
//        IDataset omOfferData = queryOmOfferData(orderLineId);
//
//        //获取资料
//        IDataset insOfferData = queryUmOfferData(subscriberInsId);
//
//        IDataset retSubOfferData = new DatasetList();
//        IData omDelSubOfferData = new DataMap();
//        //处理订单表suboffer
//        dealOnlineSubOffer(omOfferData, retSubOfferData, omDelSubOfferData);
//        //处理资料表suboffer
//        if(DataUtil.isNotEmpty(insOfferData)){
//        	dealUmInsSubOffer(insOfferData, retSubOfferData, omDelSubOfferData);
//        }
//        
//
//        return retSubOfferData;
//    }
//
//    private void dealUmInsSubOffer(IDataset insOfferData, IDataset retSubOfferData, IData omDelSubOfferData)
//    {
//        if (insOfferData == null || insOfferData.size() <= 0)
//            return;
//        for (int i = 0; i < insOfferData.size(); i++)
//        {
//            IData insData = insOfferData.getData(i);
//            String offerId = insData.getString("OFFER_ID");
//            // 如果是产品，直接跳过
//            if ("P".equals(insData.getString("OFFER_TYPE")))
//                continue;
//            String delOfferId =omDelSubOfferData.getString(offerId);
//            // 如果在om删除列表，直接跳过
//            if (StringUtils.isNotEmpty(delOfferId))
//                continue;
//            boolean isExistOnlineInfo = false;
//            for (int j = 0; j < retSubOfferData.size(); j++)
//            {
//                IData subOffer = retSubOfferData.getData(j);
//                if (offerId.equals(subOffer.getString("OFFER_ID")))
//                {
//                    isExistOnlineInfo = true;
//                    break;
//                }
//            }
//            // 如果已经存在订单表数据，直接跳过
//            if (isExistOnlineInfo)
//                continue;
//            IData subInfo = new DataMap();
//            //已经存在
//            subInfo.putAll(insData);
//            subInfo.put("OPER_CODE", "3");
//            subInfo.put("OFFER_NAME", insData.getString("OFFER_NAME"));
//            subInfo.put("IS_SHOW_SET_TAG", insData.getString("false"));
//            subInfo.put("OFFER_ID", insData.getString("OFFER_ID"));
//            subInfo.put("SELECT_FLAG", "1");
//            subInfo.put("BRAND", insData.getString("BRAND"));
//            subInfo.put("OFFER_TYPE", insData.getString("OFFER_TYPE"));
//            subInfo.put("P_OFFER_INDEX", retSubOfferData.size());
//            retSubOfferData.add(subInfo);
//        }
//    }
//
//    private void dealOnlineSubOffer(IDataset omOfferData, IDataset retSubOfferData, IData omDelSubOfferData) throws Exception
//    {
//        if (omOfferData == null || omOfferData.size() <= 0)
//            return;
//        for (int i = 0; i < omOfferData.size(); i++)
//        {
//            IData offerInfo = omOfferData.getData(i);
//            if ("P".equals(offerInfo.getString("OFFER_TYPE")) || CisfConst.ACTION_DEL.equals(offerInfo.getString("ACTION")))
//            {
//                omDelSubOfferData.put(offerInfo.getString("OFFER_ID"), offerInfo.getString("OFFER_ID"));
//                continue;
//            }
//            IDataset offerChaInfo = EcUpcViewUtil.queryOfferChaByOfferId(offerInfo.getString("OFFER_ID"));
//            IData retOffer = new DataMap();
//            retOffer.putAll(offerInfo);
//            retOffer.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
//            retOffer.put("IS_SHOW_SET_TAG", "false");
//            retOffer.put("OFFER_ID", offerInfo.getString("OFFER_ID"));
//            retOffer.put("VALID_DATE", offerInfo.getString("VALID_DATE"));
//            retOffer.put("EXPIRE_DATE", offerInfo.getString("EXPIRE_DATE"));
//            retOffer.put("REPEAT_ORDER", "false");
//            retOffer.put("SELECT_FLAG", "1");
//            retOffer.put("BRAND", offerInfo.getString("BRAND"));
//            retOffer.put("OFFER_TYPE", offerInfo.getString("OFFER_TYPE"));
//            retOffer.put("OPER_CODE", "3");
//            retOffer.put("P_OFFER_INDEX", retSubOfferData.size());
//            if (DataUtil.isNotEmpty(offerChaInfo))
//                retOffer.put("HAS_PRICE_CHA", "true");
//
//            retSubOfferData.add(retOffer);
//        }
//    }
//
//    private IDataset queryUmOfferData(String subscriberInsId) throws Exception
//    {
//        IData input = new DataMap();
//        input.put("SUBSCRIBER_INS_ID", subscriberInsId);
//
//
//        IData offerData = ServiceCaller.call("OC.enterprise.IUmOfferQuerySV.queryOfferInstanceBySubInsIdOfferId", input);
//        if (DataUtil.isEmpty(offerData.getDataset("DATAS")))
//        {
//           return null;
//        }
//
//        return offerData.getDataset("DATAS");
//    }
//
//    private IData querySubOfferList(String offerId, String operType, String merchpOperType, String[] orderedSubOfferIds, IData info) throws Exception
//    {
//        //查询是否有商品组
//        IDataset groupOffers = EcUpcViewUtil.queryOfferGroupAndOfferByOfferId(offerId, this.getVisit().getLoginEparchyCode());
//        info.put("IS_SHOW_ADDOFFER_BTN", DataUtil.isNotEmpty(groupOffers) ? true : false);
//
//        String brand = EcUpcViewUtil.queryBrandByOfferId(offerId);
//
//        //查询子销售品
//        IDataset subOfferList = EcUpcViewUtil.querySubOffersByOfferId(offerId, this.getVisit().getLoginEparchyCode());
//
//        if (DataUtil.isNotEmpty(subOfferList))
//        {
//            String validDate = TimeUtil.getDefaultSysTimeStr();
//            IDataset serviceOffers = new DatasetList();
//            IDataset priceOffers = new DatasetList();
//            for (int i = 0, size = subOfferList.size(); i < size; i++)
//            {
//                IData subOffer = subOfferList.getData(i);
//                String offerType = subOffer.getString("OFFER_TYPE");
//
//                if (UpcConst.OFFER_TYPE_DISCNT.equals(offerType))
//                {
//                    subOffer.put("VALID_DATE", validDate); //资费默认生效时间设置为当前时间
//                    priceOffers.add(subOffer);
//                }
//                else
//                {//OFFER_TYPE=S/P放一起
//                    serviceOffers.add(subOffer);
//                }
////                setServiceOfferList(serviceOffers);
////                setPricePlans(priceOffers);
//            }
//
//            //判断是否有商品特征
//            boolean hasOfferChaSpec = EcParamViewUtil.hasOfferSpecCha(operType, merchpOperType, offerId, brand);
//            info.put("OFFER_ID", offerId);
//            info.put("IS_SHOW_OFFER_CHA_SPEC_PART", hasOfferChaSpec);
//
//            IData ajaxData = new DataMap();
//
//            IDataset subOfferDataset = buildSubOfferRequiredElements(offerId, subOfferList, operType, brand);
//            ajaxData.put("SUBOFFERS", subOfferDataset);
//            ajaxData.put("HAS_OFFER_CHA_SPECS", hasOfferChaSpec);
//            ajaxData.put("IS_SHOW_ADDOFFER_BTN", info.getString("IS_SHOW_ADDOFFER_BTN"));
//
//            return ajaxData;
//        }
//
//        return new DataMap();
//    }
//
//
//        /**
//         * 构造子商品数据结构，包含必选元素
//         * @param optionalOffers
//         * @param operType
//         * @return
//         * @throws Exception
//         */
//    private IDataset buildSubOfferRequiredElements(String mainOfferId, IDataset optionalOffers, String operType, String brand) throws Exception
//    {
//        IDataset optOfferDataset = new DatasetList();
//        if(DataUtil.isNotEmpty(optionalOffers))
//        {
//            for(int i = 0, size = optionalOffers.size(); i < size; i++)
//            {
//                IData optionOffer = optionalOffers.getData(i);
//                String offerId = optionOffer.getString("OFFER_ID");
//
//                IData optOfferData = new DataMap();
//                optOfferData.put("OFFER_ID", offerId);
//                optOfferData.put("OFFER_NAME", optionOffer.getString("OFFER_NAME"));
//                optOfferData.put("OFFER_TYPE", EcUpcViewUtil.queryOfferTypeByOfferId(offerId));
//                optOfferData.put("BRAND", optionOffer.getString("BRAND"));
//                optOfferData.put("OPER_CODE", ViewDataAssembler.ACTION_CREATE);
//                optOfferData.put("SELECT_FLAG", optionOffer.getString("SELECT_FLAG"));
//
//                // 查询必选子商品
//                IDataset subOffers = EcUpcViewUtil.queryOfferComRelOfferByOfferId(offerId);//方法已废弃：EcUpcViewUtil.queryOfferComRelDetailsByOfferIdAndRoleId(offerId, EcConstants.OFFER_COM_REL_ROLE_ID_SUBOFFER, false, this.getVisit().getLoginEparchyCode());
//                if(DataUtil.isNotEmpty(subOffers))
//                {
//                    IDataset subOfferDataset = new DatasetList();
//                    for(int j = 0, len = subOffers.size(); j < len; j++)
//                    {
//                        IData subOffer = subOffers.getData(j);
//
//                        IData subOfferData = new DataMap();
//                        subOfferData.put("OFFER_ID", subOffer.getString("OFFER_ID"));
//                        subOfferData.put("OFFER_NAME", subOffer.getString("OFFER_NAME"));
//                        subOfferData.put("OFFER_TYPE", EcUpcViewUtil.queryOfferTypeByOfferId(subOffer.getString("OFFER_ID")));
//                        subOfferData.put("BRAND", subOffer.getString("BRAND"));
//                        subOfferData.put("OPER_CODE", ViewDataAssembler.ACTION_CREATE);
//
//                        subOfferDataset.add(subOfferData);
//                    }
//
//                    if(DataUtil.isNotEmpty(subOfferDataset))
//                    {
//                        optOfferData.put("SUBOFFERS", subOfferDataset);
//                    }
//                }
//
//                boolean hasOfferCha = false;
//                hasOfferCha = EcParamViewUtil.hasOfferSpecCha(operType, EcConstants.MERCH_STATUS.MERCH_ADD.getValue(), offerId, brand);
//
//                if(hasOfferCha)
//                {
//                    optOfferData.put("IS_SHOW_SET_TAG", true);
//                }
//                else
//                {
//                    optOfferData.put("IS_SHOW_SET_TAG", false);
//                }
//                optOfferDataset.add(optOfferData);
//            }
//        }
//        return optOfferDataset;
//    }
//    /**
//     * 设置管理节点属性
//     *
//     * @param orderLineId
//     * @param flowInfo
//     * @throws Exception
//     */
//    private void queryBbossManageAttrInfos(String offerId, String orderLineId, String flowInfo, String operateflag) throws Exception
//    {
//        IData inparam = new DataMap();
//
//        //查询BBOSS下发管理信息
//        getIssuedManageInfo(orderLineId, flowInfo, inparam);
//
//        //查询需要反馈bboss的管理信息或者已经反馈bboss的管理信息
//        getManagePageAttrInfo(offerId, orderLineId, flowInfo, operateflag, inparam);
//    }
//
//    private void getManagePageAttrInfo(String offerId, String orderLineId, String flowInfo, String operateflag, IData inparam) throws Exception
//    {
//        IDataset elementInfos;
//
//        // 不管是否反馈，都可以先加载出需反馈属性
//        elementInfos = getPageAttrItem(offerId, flowInfo, inparam);
//        if (elementInfos == null) return;
//
//        // 如果是已经反馈的，给反馈属性赋值
//        getSaveAttrData(orderLineId, flowInfo, operateflag, inparam, elementInfos);
//    }
//
//    private void getSaveAttrData(String orderLineId, String flowInfo, String operateflag, IData inparam, IDataset elementInfos) throws Exception
//    {
//        if (BbossConsts.MANAGE_NODE_NOT_OPER.equals(operateflag))
//        {
//            setBossManageInfo(elementInfos);
//            return;
//        }
//        inparam.clear();
//        inparam.put("ORDER_LINE_ID", orderLineId);
//        inparam.put("FLOW_INFO", flowInfo);
//        inparam.put("OPERATE_FLAG", operateflag);
//        IData BbossResponse = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryManageAttrInfos", inparam);
//        IDataset BbossResponseSet = BbossResponse.getDataset("DATAS");
//        if (DataUtils.isEmpty(BbossResponseSet))
//        {
//            setBossManageInfo(elementInfos);
//            return;
//        }
//        //给属性赋值
//        for (int i =0;i<BbossResponseSet.size();i++)
//        {
//            IData bbossparam = BbossResponseSet.getData(i);
//            String elementId = bbossparam.getString("RSRV_STR12");
//            String elementValue = bbossparam.getString("RSRV_STR14");
//            for (int j=0;j<elementInfos.size();j++)
//            {
//                IData attrInfo = elementInfos.getData(j);
//                if (elementId.equals(attrInfo.getString("ELEMENT_ID")))
//                    attrInfo.put("ELEMENT_VALUE",elementValue);
//            }
//        }
//
//        setBossManageInfo(elementInfos);
//    }
//
//    private IDataset getPageAttrItem(String offerId, String flowInfo, IData inparam) throws Exception
//    {
//        IDataset elementInfos;
//        inparam.clear();
//        inparam.put("ELEMENT_KEY", "BBSS");
//        String attrObj = "BASE";
//        String merchpId = EcBbossCommonViewUtil.merchToProduct(offerId,1,true);
//        // 互联网专线管理节点编码不同
//        if ("111208".equals(merchpId))
//        {
//            attrObj = merchpId;
//        }
//        inparam.put("ATTR_OBJ", attrObj);
//
//        IData elementInfo = ServiceCaller.call("OC.enterprise.ICbEcPageElementQuerySV.queryPageElementsByElementKeyAttrObj", inparam);
//        elementInfos = elementInfo.getDataset("DATAS");
//
//        if (DataUtils.isEmpty(elementInfos))
//        {
//            setBossManageInfo(new DatasetList());
//            return null;
//        }
//        for (int i = 0; i < elementInfos.size(); i++)
//        {
//            String time = TimeUtil.getSysTime();
//            IData param = elementInfos.getData(i);
//            if (StringUtils.equals("审批时间", param.getString("ELEMENT_NAME")))
//            {
//                param.put("ELEMENT_VALUE", time); //审批时间赋当前时间
//            }
//            if (StringUtils.equals("@Select", param.getString("ELEMENT_JWCID")))
//            {
//                List<Map<String, String>> nextStep = StaticParamUtil.getStaticDatas(flowInfo);
//                param.put("NEXT_STEP", nextStep);
//            }
//        }
//        return elementInfos;
//    }
//
//    private void getIssuedManageInfo(String orderLineId, String flowInfo, IData inparam) throws Exception
//    {
//        inparam.put("ORDER_LINE_ID", orderLineId);
//        inparam.put("FLOW_INFO", flowInfo);
//        inparam.put("OPERATE_FLAG", BbossConsts.MANAGE_NODE_NOT_OPER);
//
//        IData BbossIssued = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryManageAttrInfos", inparam);
//
//        setBbossIssuedInfo(BbossIssued.getDataset("DATAS"));
//    }
//
//    /**
//     * 初始化产品特征规格区域
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void initOfferChaSpecBySpecId(IRequestCycle cycle) throws Exception
//    {
//        String offerid = this.getData().getString("OFFER_ID");
//        String orderLineId = this.getData().getString("ORDER_LINE_ID");
//        String flowNodeId = this.getData().getString("FLOW_ID").substring(6);
//
//        String node_id = EcBbossCommonViewUtil.merchToProduct(offerid, 1, true);
//        // 操作类型转换为全网操作类型
//        IData inAttr = new DataMap();
//        inAttr.put("FLOW_ID", flowNodeId);
//        inAttr.put("NODE_ID", node_id);
//        inAttr.put("PROD_SPEC_ID", offerid);
//        inAttr.put("LAYOUT_TYPE", "main");
//        // 特征规格入参
//        setInAttr(inAttr);
//        // 特征规格赋值
//        IData input = new DataMap();
//        input.put("ORDER_LINE_ID", orderLineId);
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryOmOfferChaForBboss", input);
//        if (DataUtil.isNotEmpty(result.getDataset("DATAS")))
//        {
//            IData busi = EcBbossCommonViewUtil.dealOnlineOfferAttrInfo(result);
//            setBusi(busi);
//        }
//    }
//
//
//
//    /**
//     * 构造销售品数据对象
//     *
//     * @param insPricePlanDataset 定价计划实例
//     * @return {"PRICE_PLANS":[{}]}
//     * @throws Exception
//     */
//    private IData buildOfferData(String offerId, String orderLineId, String operCode, IDataset insPricePlanDataset, IDataset insChildOfferDataset) throws Exception
//    {
//        IData offerData = new DataMap();
//
//        return offerData;
//    }
//
//    private IDataset queryOmOfferData(String orderLineId) throws Exception
//    {
//        IData input = new DataMap();
//        input.put("ORDER_LINE_ID", orderLineId);
//
//
//        IData offerData = ServiceCaller.call("OC.enterprise.IOmOfferQuerySV.queryOmOfferByLineId", input);
//        if (DataUtil.isEmpty(offerData.getDataset("DATAS")))
//        {
//            BizException.bizerr(OrderException.CRM_ORDER_22, orderLineId);
//        }
//
//        return offerData.getDataset("DATAS");
//    }
//
//
//    /**
//     * 前台数据提交
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void submit(IRequestCycle cycle) throws Exception
//    {
//        IDataset datas = new DatasetList(getData().getString("SUBMIT_DATA"));
//
//        // 调管理节点反馈服务
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageAndPreDealSV.rspBBossManage", datas.getData(0));
//
//        boolean isesop = BizEnv.getEnvBoolean("isesop", false);
//        if (isesop)
//        {
//            IDataset esops = new DatasetList(getData().getString("ESOP_DATA"));
//            esops.getData(0).put("OP_ID", this.getVisit().getStaffId());
//            esops.getData(0).put("OP_NAME", this.getVisit().getStaffName());
//            esops.getData(0).put("OP_PHONE", this.getVisit().getSerialNumber());
//            esops.getData(0).put("ORG_ID", this.getVisit().get("DEPART_ID"));
//            esops.getData(0).put("ORG_NAME", this.getVisit().getDepartName());
//            esops.getData(0).put("MGMT_COUNTY", this.getVisit().getCityCode());
//            esops.getData(0).put("MGMT_DISTRICT", this.getVisit().getLoginEparchyCode());
//            esops.getData(0).put("ROLE_ID", this.getVisit().getStaffRoles());
//            if (StringUtils.isNotBlank(esops.getData(0).getString("IBSYSID")))
//            {
//                ServiceCaller.call("OrderCentre.enterprise.process.IEsopInitInForERPSV.saveManagerSumbit", esops.getData(0));
//            }
//        }
//        logger.debug("=============================服务返回结果:{}", result);
//
//        setAjax(result);
//    }
}
