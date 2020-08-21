package com.asiainfo.veris.crm.iorder.web.igroup.bboss;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.EcBbossCommonViewUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.ecbasepage.EcBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class EcBbossManageCreate extends EcBasePage
{
    public abstract void setCondition(IData cond);

    public abstract void setPricePlans(IDataset pricePlans);

    public abstract void setServiceOfferList(IDataset serviceOfferList);

    public abstract void setOptionalOffers(IDataset optionalOffers);

    public abstract void setManageInfo(IDataset manageInfo);// 管理信息

    public abstract void setProdSpecs(IDataset prodSpecs);

    public abstract void setInfo(IData info);

    public abstract void setInAttr(IData inAttr);

    public abstract void setInfos(IDataset dataset);

    public abstract void setInfosCount(long count);

    public abstract void setAddAttInfo(IData info);

    public abstract void setAddAuditorInfo(IData info);

    public abstract void setAddContactorInfo(IData info);

    public abstract void setAttInfo(IData info);

    public abstract void setAttInfos(IDataset infos);

    public abstract void setAuditorInfo(IData info);

    public abstract void setContactorInfo(IData info);

    public abstract void setContactorInfos(IDataset infos);

    public abstract void setAuditorInfos(IDataset infos);

    public abstract void setBusi(IData busi);

    public abstract void setEsop(IData esop);


    /**
     * @param cycle
     * @throws Exception
     * @author chengjian 2015-11-06
     * @Description bboss管理流程受理报文发送界面初始化
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {

        IData initdata = getData("cond", true);
        //todo: 测试esop初始化
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");//
        String workId = param.getString("WORK_ID", "");
        
        if (StringUtils.isEmpty(ibsysid) && StringUtils.isEmpty(workId))
        {
            //1. 初始化商品名称下拉框数据
            EcBbossCommonViewUtil.qryMerchInfos(this,initdata);
            
            this.setCondition(initdata);
        }
        else
        //esop发起
        {
//            IData input = new DataMap();
//            input.put("IBSYSID", ibsysid);
//            IData retInfo = ServiceCaller.call("OC.enterprise.IEsopQuerySV.qcsEsopData",input);
//            IData esopInfo = retInfo.getData("DATAS");
//            String orderId = esopInfo.getString("ORDER_ID");
//            String offerId = esopInfo.getString("OFFER_ID");
//            String groupId = esopInfo.getString("GROUP_ID");
//            IDataset offerList = esopInfo.getDataset("OFFER_LIST");
//            if (DataUtil.isEmpty(offerList))
//                return;
//            String productId = offerList.getData(0).getString("OFFER_ID");
//
//            IData inparam = new DataMap();
//            inparam.put("IBSYSID", ibsysid);
//            inparam.put("ORDER_ID", orderId);
//            inparam.put("OFFER_ID", offerId);
//            inparam.put("PRODUCT_ID", productId);
//            IDataset feedBackInfos = qryPreFeedBackInfo(inparam);
//            if(DataUtil.isEmpty(feedBackInfos))
//                return;
//            IData esop_data = new DataMap();
//            esop_data.put("IS_ESOP", "true");
//            esop_data.put("IBSYSID", ibsysid);
//            esop_data.put("ORDER_LINE_ID", orderId);
//            esop_data.put("SUBSCRIBER_INS_ID", esopInfo.getString("SUBSCRIBER_INS_ID"));
//            esop_data.put("CUST_NAME", feedBackInfos.first().getString("CUST_NAME"));
//            esop_data.put("PRODUCT_NAME", feedBackInfos.first().getString("MERCH_PRODUCT_NAME"));
//            esop_data.put("BUSISIGN", "BIP4B255_T4011064_0_0");
//            esop_data.put("PRODUCT_ID", param.getString("PRODUCT_ID", ""));
//            esop_data.put("OFFER_ID", param.getString("OFFER_ID", ""));
//            esop_data.put("ACCEPT_DATE", TimeUtil.getSysTime());
//            esop_data.put("GROUP_ID", groupId);
//            esopInfo.put("ESOP_RET",esop_data);
//            setEsop(esopInfo); //管理报文反馈接口回调esop用
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
     * @author cY 2015-11-06
     * @Description bboss查询预受理归档信息
     */
//    public void queryPreFeedbackInfos(IRequestCycle cycle) throws Exception
//    {
//        IData param = getData("cond", true);
//        qryPreFeedBackInfo(param);
//    }
//
//    private IDataset qryPreFeedBackInfo(IData param) throws Exception
//    {
//        // 获取已经归档的产品
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageQuerySV.queryPreFeedback", param, getPagination());
//
//        IDataset preFeedDataset = result.getDataset("DATAS");
//
//        if (DataUtil.isEmpty(preFeedDataset))
//        {
//            BizException.bizerr(OrderException.CRM_ORDER_1629);
//        }
//        setInfosCount(result.getLong("COUNT"));
//        setInfos(preFeedDataset);
//        return preFeedDataset;
//    }

    /**
     * @param cycle
     * @throws Exception
     * @author liaolc 2018-1-13
     * @Description 产品名称下拉框联动性
     * 
     */
    public void setProductInfos(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        String pospecnumber=inparam.getString("POSPECNUMBER");
        if(StringUtils.isNotBlank(pospecnumber))
        {
        	//商品名称下拉框联动性产品信息
        	 EcBbossCommonViewUtil.qryProductInfos(this,inparam);
        }
        this.setCondition(inparam);
    }




//    /**
//     * 查询预受理归档订单信息 cy 2016-1-19
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void showPreFeedbackInfos(IRequestCycle cycle) throws Exception
//    {
//        IData idata = getData();
//        String offerId = idata.getString("SUB_OFFER_ID");
//        String orderLineId = idata.getString("ORDER_LINE_ID");
//        String ibsysId = idata.getString("IBSYSID");
//        IData input = new DataMap();
//        input.put("ORDER_LINE_ID", orderLineId);
//        // 处理生成已经订购过的资费、服务等子商品结构，用于界面初始化
//        IData ajaxData = buildOrderedOfferInfo(offerId, orderLineId);
//
//        // ESOP过来的订单
//        if (StringUtils.isNotEmpty(ibsysId))
//        {
//            IData esopOfferInfo = idata.getData("OFFER_INFO");
//            // 判断该offer是否在esop界面订购上了，订购上了直接勾选上，主要是针对资费
//            IDataset esopSubOffer = esopOfferInfo.getDataset("SUBOFFERS");
//
//            IDataset subOffer = ajaxData.getDataset("SUBOFFERS");
//
//            // 先处理服务信息
//            IDataset SVCData = new DatasetList();
//            // 获得suboffer结构的合集
//            for (int i = 0; i < esopSubOffer.size(); i++)
//            {
//                IData esopOffer = esopSubOffer.getData(i);
//                boolean isExist = false;
//                for (int j = 0; j < subOffer.size(); j++)
//                {
//                    IData omOffer = subOffer.getData(j);
//                    if (!esopOffer.getString("OFFER_TYPE").equals(omOffer.getString("OFFER_TYPE")))
//                        continue;
//                    if (StringUtils.equals(omOffer.getString("OFFER_ID"), esopOffer.getString("OFFER_ID")))
//                    {
//                        isExist = true;
//                        break;
//                    }
//                }
//                if (isExist)
//                    continue;
//                if (UpcConst.OFFER_TYPE_DISCNT.equals(esopOffer.getString("OFFER_TYPE")))
//                    buildOfferStruct(esopOffer, subOffer.size() + i);
//                subOffer.add(esopOffer);
//            }
//            // 取服务列表
//            for (int i = 0; i < subOffer.size(); i++)
//            {
//                if (!UpcConst.OFFER_TYPE_SVC.equals(subOffer.getData(i).getString("OFFER_TYPE")))
//                    continue;
//                SVCData.add(subOffer.getData(i));
//            }
//            setServiceOfferList(SVCData);
//            IDataset dealPriceList = new DatasetList();
//            for (int i = 0; i < subOffer.size(); i++)
//            {
//                if (!UpcConst.OFFER_TYPE_DISCNT.equals(subOffer.getData(i).getString("OFFER_TYPE")))
//                    continue;
//                IData esopOffer = subOffer.getData(i);
//                if (CisfConst.ACTION_ADD.equals(esopOffer.getString("OPER_CODE")))
//                    dealPriceList.add(esopOffer);
//            }
//            // 处理资费信息,预受理资费肯定没有订购过，所以直接设置即可
//            setPricePlans(dealPriceList);
//            ajaxData.put("IS_ESOP", "true");
//            ajaxData.put("SUBSCRIBER_INS_ID", esopOfferInfo.getString("SUBSCRIBER_INS_ID"));
//        }
//
//        ajaxData.put("ORDER_LINE_ID", orderLineId);
//        this.setAjax(ajaxData);
//
//    }
//
//    private void buildOfferStruct(IData esopOffer, int size)
//    {
//        esopOffer.put("BRAND", "");
//        esopOffer.put("P_OFFER_INDEX", size);
//        if (IDataUtil.isNotEmpty(esopOffer.getDataset("OFFER_CHA_SPECS")))
//            esopOffer.put("HAS_PRICE_CHA", true);
//    }
//
//    private IData buildOrderedOfferInfo(String offerId, String orderLineId) throws Exception
//    {
//        // 获取当前订单商品（offer）订购信息
//        IDataset offerInfos = queryInsOfferData(orderLineId);
//        // 当前商品(bboss产品)下所有子商品(offer)信息
//        IData info = new DataMap();
//        IData ajaxData = querySubOfferList(offerId, "ecCrt", "1", info);
//        IDataset subOffer = ajaxData.getDataset("SUBOFFERS");
//        // 判断该offer是否已经在预受理的时候订购上了，订购上了直接勾选上，主要针对服务
//        for (int i = 0; i < subOffer.size(); i++)
//        {
//            IData temp = subOffer.getData(i);
//            for (int j = 0; j < offerInfos.size(); j++)
//            {
//                IData offerInfo = offerInfos.getData(j);
//                if (StringUtils.equals(offerInfo.getString("OFFER_ID"), temp.getString("OFFER_ID")))
//                {
//                    break;
//                }
//            }
//        }
//        return ajaxData;
//    }
//
//    public IData querySubOfferList(String offerId, String operType, String merchpOperType, IData info) throws Exception
//    {
//        //查询是否有商品组
//        IDataset groupOffers = EcUpcViewUtil.queryOfferGroupAndOfferByOfferId(offerId, this.getVisit().getLoginEparchyCode());
//        info.put("IS_SHOW_ADDOFFER_BTN", IDataUtil.isNotEmpty(groupOffers) ? true : false);
//
//        //查询子销售品
//        IDataset subOfferList = EcUpcViewUtil.querySubOffersByOfferId(offerId, this.getVisit().getLoginEparchyCode());
//
//        if (IDataUtil.isNotEmpty(subOfferList))
//        {
//            IDataset serviceOffers = new DatasetList();
//            IDataset priceOffers = new DatasetList();
//            for (int i = 0, size = subOfferList.size(); i < size; i++)
//            {
//                IData subOffer = subOfferList.getData(i);
//
//                String offerType = subOffer.getString("OFFER_TYPE");
//                subOffer.put("OFFER_TYPE", offerType);
//
//                if(UpcConst.OFFER_TYPE_DISCNT.equals(offerType))
//                {
//                    priceOffers.add(subOffer);
//                }
//                else
//                {
//                    serviceOffers.add(subOffer);
//                }
//            }
//            setServiceOfferList(serviceOffers);
//            setPricePlans(priceOffers);
//        }
//
//        //判断是否有商品特征
//        boolean hasOfferChaSpec = EcParamViewUtil.hasOfferSpecCha(operType, merchpOperType, offerId, "BOSG");
//        info.put("OFFER_ID", offerId);
//        info.put("IS_SHOW_OFFER_CHA_SPEC_PART", hasOfferChaSpec);
//        setInfo(info);
//        IData ajaxData = new DataMap();
//        IDataset subOfferDataset = buildSubOfferRequiredElements(offerId, subOfferList, operType, "BOSG");
//        ajaxData.put("SUBOFFERS", subOfferDataset);
//        ajaxData.put("HAS_OFFER_CHA_SPECS", hasOfferChaSpec);
//        ajaxData.put("OFFER_ID", offerId);
//        return ajaxData;
//    }
//
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
//                optOfferData.put("OFFER_TYPE", optionOffer.getString("OFFER_TYPE"));
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
//                        subOfferData.put("OFFER_TYPE", subOffer.getString("OFFER_TYPE"));
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
//                if (hasOfferCha)
//                {
//                    optOfferData.put("IS_SHOW_SET_TAG", true);
//                }
//                else
//                {
//                    optOfferData.put("IS_SHOW_SET_TAG", false);
//                }
//
//                optOfferDataset.add(optOfferData);
//            }
//        }
//        return optOfferDataset;
//    }
//
//    /**
//     * 构造销售品数据对象
//     *
//     * @param insChildOfferDataset 子商品实例
//     * @return {"SUBOFFERS":[{}]}
//     * @throws Exception
//     */
//    private IData buildOfferData(String offerId, String orderLineId, IDataset insProdDataset, IDataset insPricePlanDataset, IDataset insChildOfferDataset) throws Exception
//    {
//        IData offerData = new DataMap();
//
//        //构造产品特征规格
//        if (DataUtil.isNotEmpty(insProdDataset))
//        {
//            offerData.put("PRODS", insProdDataset);
//        }
//        //构造定价计划
//        if (DataUtil.isNotEmpty(insPricePlanDataset))
//        {
//            IDataset pricePlanDataset = new DatasetList();
//            for (int i = 0, size = insPricePlanDataset.size(); i < size; i++)
//            {
//                IData insPricePlanData = insPricePlanDataset.getData(i);
//
//                IData pricePlanData = new DataMap();
//                pricePlanData.put("PRICE_PLAN_ID", insPricePlanData.getString("PRICE_PLAN_ID"));
//                String priceName = null;//todo: EcUpcViewUtil.queryPriceNameByOfferIdAndPricePlanId(offerId, insPricePlanData.getString("PRICE_PLAN_ID"));
//                pricePlanData.put("PRICE_NAME", priceName);
//                pricePlanData.put("PRICE_PLAN_INS_ID", insPricePlanData.getString("PRICE_PLAN_INS_ID"));
//                pricePlanData.put("OFFER_INS_ID", insPricePlanData.getString("OFFER_INS_ID"));
//                pricePlanData.put("VALID_DATE", insPricePlanData.getString("VALID_DATE"));
//                pricePlanData.put("EXPIRE_DATE", insPricePlanData.getString("EXPIRE_DATE"));
//                pricePlanData.put("DEFAULT_EXPIRE_DATE", insPricePlanData.getString("EXPIRE_DATE"));
//                pricePlanData.put("PRICE_VALID_TYPE", EcConstants.EC_PRICE_PLAN_VALID_TYPE_NEXT_DAY);
//                pricePlanData.put("OPER_CODE", ViewDataAssembler.ACTION_EXITS); // 变更时，除主offer外，其余节点OPER_CODE初始为3-不变
//
//                //构造价格
//                IData input = new DataMap();
//                input.put("PRICE_PLAN_INS_ID", insPricePlanData.getString("PRICE_PLAN_INS_ID"));
//                input.put("ORDER_LINE_ID", orderLineId);
//                IData returnData = ServiceCaller.call("OrderCentre.enterprise.IOmPricePlanQuerySV.getOmPriceInfos", input);
//                if (DataUtil.isNotEmpty(returnData.getDataset("DATAS")))
//                {
//                    IData insPriceData = returnData.getDataset("DATAS").getData(0);
//
//                    IDataset priceDataset = new DatasetList();
//                    IData priceData = new DataMap();
//                    priceData.put("PRICE_ID", insPriceData.getString("PRICE_ID"));
//                    priceData.put("PRICE_VAL", insPriceData.getString("PRICE_VAL"));
//                    priceData.put("PRICE_INS_ID", insPriceData.getString("PRICE_INS_ID"));
//                    priceData.put("OPER_CODE", ViewDataAssembler.ACTION_EXITS); // 变更时，除主offer外，其余节点OPER_CODE初始为3-不变
//                    input.put("PRICE_INS_ID", insPriceData.getString("PRICE_INS_ID"));
//                    IData result = ServiceCaller.call("OrderCentre.enterprise.IOmPriceChaQuerySV.getOmPriceChaInfos", input);
//                    if (DataUtil.isNotEmpty(result.getDataset("DATAS")))
//                    {
//                        IData insPriceChaData = result.getDataset("DATAS").getData(0);
//                        IDataset priceChaDataset = new DatasetList();
//                        IData priceChaData = new DataMap();
//                        priceChaData.put("CHA_SPEC_ID", insPriceChaData.getString("CHA_SPEC_ID"));
//                        priceChaData.put("CHA_VALUE", insPriceChaData.getString("VALUE"));
//                        priceChaData.put("CHA_SPEC_CODE", insPriceChaData.getString("CHA_SPEC_CODE"));
//                        priceChaData.put("OPER_CODE", ViewDataAssembler.ACTION_EXITS);
//                        priceChaDataset.add(priceChaData);
//                        priceData.put("PRICE_CHA_SPECS", priceChaDataset);
//                    }
//                    priceDataset.add(priceData);
//
//                    pricePlanData.put("PRICES", priceDataset);
//                }
//
//                pricePlanDataset.add(pricePlanData);
//            }
//            offerData.put("PRICE_PLANS", pricePlanDataset);
//        }
//
//
//        if (DataUtil.isNotEmpty(insChildOfferDataset))
//        {
//            IDataset optChildOfferDataset = new DatasetList();
//            for (int j = 0, len = insChildOfferDataset.size(); j < len; j++)
//            {
//                IData optChildOfferData = new DataMap();
//                IData insChildOfferData = insChildOfferDataset.getData(j);
//                optChildOfferData.put("OFFER_ID", insChildOfferData.getString("OFFER_ID"));
//                optChildOfferData.put("OFFER_NAME", insChildOfferData.getString("OFFER_NAME"));
//                optChildOfferData.put("OFFER_INS_ID", insChildOfferData.getString("OFFER_INS_ID"));
//                optChildOfferData.put("SUBSCRIBER_INS_ID", insChildOfferData.getString("SUBSCRIBER_INS_ID"));
//                optChildOfferData.put("OPER_CODE", ViewDataAssembler.ACTION_EXITS); // 变更时，suboffer节点OPER_CODE初始为1-变更
//                optChildOfferData.put("SELECT_FLAG", "1"); //必选
//                optChildOfferDataset.add(optChildOfferData);
//            }
//            offerData.put("SUBOFFERS", optChildOfferDataset);
//        }
//
//        return offerData;
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
//        IDataset esopChaInfo = null;
//        String offerid = this.getData().getString("OFFER_ID");
//        String ibsysId = this.getData().getString("IBSYSID");
//        String orderLineId = this.getData().getString("ORDER_LINE_ID");
//
//        // 初始化esop数据
//        if (StringUtils.isNotEmpty(ibsysId))
//        {
//            String esopChaInfoStr = this.getData().getString("ESOP_CHA_INFOS");
//            if (StringUtils.isNotEmpty(esopChaInfoStr))
//            {
//                esopChaInfo = new DatasetList(esopChaInfoStr);
//            }
//        }
//
//        String node_id = EcBbossCommonViewUtil.merchToProduct(offerid, 1, true);
//        // 操作类型转换为全网操作类型
//        IData inAttr = new DataMap();
//        inAttr.put("FLOW_ID", "1");
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
//            if (DataUtil.isNotEmpty(esopChaInfo))
//            {
//                EcBbossCommonViewUtil.mixEsopChaWithOnlineOfferCha(busi,esopChaInfo);
//            }
//            setBusi(busi);
//        }
//    }
//
//
//    /**
//     * 将预受理转正式受理信息保存订单表
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void submit(IRequestCycle cycle) throws Exception
//    {
//        IDataset datas = new DatasetList(getData().getString("SUBMIT_DATA"));
//
//        // 调管理节点反馈服务
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageAndPreDealSV.savePreFeedBackInfo", datas.getData(0), getPagination());
//
//        setAjax(result);
//    }
//
//    /**
//     * 将预受理转正式受理更新订单表状态
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void updateOrderState(IRequestCycle cycle) throws Exception
//    {
//        IData data = this.getData();
//        IDataset orderList = new DatasetList(data.getString("ORDER_ID_LIST"));
//        IDataset orderLineList = new DatasetList(data.getString("ORDER_LINE_ID_LIST"));
//        IDataset merchOrderLineIdList = new DatasetList(data.getString("MERCH_ORDER_LINE_ID_LIST"));
//
//
//        IData inparam = new DataMap();
//        inparam.put("ORDER_ID_LIST", orderList);
//        inparam.put("ORDER_LINE_ID_LIST", orderLineList);
//        inparam.put("MERCH_ORDER_LINE_ID_LIST", merchOrderLineIdList);
//
//        IData result = ServiceCaller.call("OC.enterprise.IBbossManageAndPreDealSV.sendOrderInfoToBboss", inparam);
//
//        boolean isesop = BizEnv.getEnvBoolean("isesop", false);
//        if (isesop)
//        {
//            IDataset esops = new DatasetList(getData().getString("ESOP_DATA"));
//            if (null != esops && esops.size() > 0)
//            {
//                IData esop = esops.getData(0);
//                esop.put("OP_ID", this.getVisit().getStaffId());
//                esop.put("OP_NAME", this.getVisit().getStaffName());
//                esop.put("OP_PHONE", this.getVisit().getSerialNumber());
//                esop.put("ORG_ID", this.getVisit().get("DEPART_ID"));
//                esop.put("ORG_NAME", this.getVisit().getDepartName());
//                esop.put("MGMT_COUNTY", this.getVisit().getCityCode());
//                esop.put("MGMT_DISTRICT", this.getVisit().getLoginEparchyCode());
//                esop.put("ROLE_ID", this.getVisit().getStaffRoles());
//                esop.put("ORDER_ID_LIST", orderList);
//                esop.put("ORDER_LINE_ID_LIST", orderLineList);
//                if (StringUtils.isNotBlank(esops.getData(0).getString("IBSYSID")))
//                {
//                    //todo:ITF_EOS_TcsGrpBusi 老代码的GroupBbossManage.java 400行
//                    ServiceCaller.call("OrderCentre.enterprise.process.IEsopInitInForERPSV.saveOpenSumbit", esop);
//                }
//            }
//        }
//    }
//
//    private IDataset queryInsOfferData(String orderLineId) throws Exception
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
//    /**
//     * 查询员工手机号
//     *
//     * @param cycle
//     * @throws Exception
//     */
//    public void queryStaffInfo(IRequestCycle cycle) throws Exception
//    {
//        IData input = getData();
//        IData result = ServiceCaller.call("CrmBC.bc.common.IOrgStaffQuerySV.queryStaff", input);
//        IDataset staffInfos = result.getDataset("INFOS");
//        if (DataUtil.isNotEmpty(staffInfos))
//        {
//            String access_number = staffInfos.getData(0).getString("BILL_ID");
//            IData ajaxData = new DataMap();
//            ajaxData.put("ACCESS_NUMBER", access_number);
//            setAjax(ajaxData);
//        }
//    }

    private IData parseMapToData(Map map) throws Exception
    {
        IData data = new DataMap();

        Iterator itr = map.keySet().iterator();
        while (itr.hasNext())
        {
            String key = itr.next().toString();
            Object o = map.get(key);
            if (o instanceof String)
            {
                String value = map.get(key).toString();
                data.put(key, value);
                continue;
            }
            else if (o instanceof ArrayList)
            {
                List list = (ArrayList) map.get(key);
                IDataset dataset = new DatasetList();
                for (int i = 0, size = list.size(); i < size; i++)
                {
                    Map subMap = (HashMap) list.get(i);
                    IData subData = parseMapToData(subMap);
                    dataset.add(subData);
                }
                data.put(key, dataset);
                continue;
            }
            else if (o instanceof Map)
            {
                Map subMap = (HashMap) map.get(key);
                IData subData = parseMapToData(subMap);
                data.put(key, subData);
                continue;
            }
        }

        return data;
    }

}
