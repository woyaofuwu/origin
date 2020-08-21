package com.asiainfo.veris.crm.iorder.web.igroup.ecbasepage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupRuleConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class EcBasePage extends CSBasePage {
    /**
     * 将子商品分资费和服务存放
     * 
     * @param subOfferDataset
     * @param serviceOfferDataset
     * @param priceOfferDataset
     * @throws Exception
     */
    public void setServicePriceDataset(IDataset subOfferDataset, IDataset serviceOfferDataset, IDataset priceOfferDataset) throws Exception {
        if (IDataUtil.isEmpty(subOfferDataset)) {
            return;
        }
        for (int i = 0, size = subOfferDataset.size(); i < size; i++) {
            IData subOffer = subOfferDataset.getData(i);
            if (IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOffer.getString("OFFER_TYPE"))) {
                subOffer.put("P_OFFER_INDEX", i); // 标识资费类商品序号

                priceOfferDataset.add(subOffer);
            } else {// bboss的产品也加到serviceOffer中
                serviceOfferDataset.add(subOffer);
            }
        }
    }

    /**
     * 构造商品数据对象
     * 
     * @param insOfferData
     *            主体销售品实例或附加销售品实例
     * @param insEcPackageDataset
     *            集团定制信息
     * @param insSubOfferDataset
     *            子商品实例
     * @param type
     *            EC或者MEM
     * @param group
     *            订购的子商品所属的商品组信息
     * @return {"OFFER_ID":"",...,"OFFER_INS_ID":"","SUBOFFERS":[{}]}
     * @throws Exception
     */
    public IData buildOfferData(IData insOfferData, IDataset insSubOfferDataset, String operType, String type, IData group) throws Exception {
        IData offerData = new DataMap();

        // 先为了逻辑正确
        String qryofferId = insOfferData.getString("OFFER_ID");

        String offerId = insOfferData.getString("OFFER_ID");
        offerData.put("OFFER_ID", offerId);
        offerData.put("OFFER_CODE", insOfferData.getString("OFFER_CODE"));
        offerData.put("OFFER_NAME", insOfferData.getString("OFFER_NAME"));
        offerData.put("BRAND_CODE", insOfferData.getString("BRAND_CODE"));
        offerData.put("USER_ID", insOfferData.getString("USER_ID"));
        offerData.put("OFFER_TYPE", insOfferData.getString("OFFER_TYPE"));
        offerData.put("OPER_CODE", PageDataTrans.transOperTypeToOperCode(operType));
        offerData.put("TRADE_TYPE_CODE", insOfferData.getString("TRADE_TYPE_CODE", ""));
        if (StringUtils.isNotBlank(insOfferData.getString("OFFER_INS_ID"))) {
            offerData.put("OFFER_INS_ID", insOfferData.getString("OFFER_INS_ID"));
        }
        if (StringUtils.isNotBlank(insOfferData.getString("ROLE_ID"))) {
            offerData.put("ROLE_ID", insOfferData.getString("ROLE_ID"));
        }
        // 构造子销售品
        if (IDataUtil.isNotEmpty(insSubOfferDataset)) {
            if (BizCtrlType.ChangeUserDis.equals(operType) || BizCtrlType.ChangeMemberDis.equals(operType)) {
                String delaType = "";
                if (BizCtrlType.ChangeMemberDis.equals(operType)) {
                    offerId = insOfferData.getString("EC_OFFER_ID");
                    delaType = "1";
                }
                queryOrderedOfferInGroupInfo(group, offerId, insSubOfferDataset, delaType);
            }
            IDataset subOfferDataset = new DatasetList();
            for (int i = 0, size = insSubOfferDataset.size(); i < size; i++) {
                IData subOfferData = new DataMap();
                IData insSubOfferData = insSubOfferDataset.getData(i);
                if ("DLBG".equals(insOfferData.getString("BRAND_CODE")) && "P".equals(insSubOfferData.getString("OFFER_TYPE"))) {
                    continue;// 动力不在这里拼子商品结构，此方法结束后，单独处理
                }
                String subOfferInsId = insSubOfferData.getString("OFFER_INS_ID");
                String subOfferId = insSubOfferData.getString("OFFER_ID");
                String subOfferCode = insSubOfferData.getString("OFFER_CODE");
                String relOfferId = insSubOfferData.getString("REL_OFFER_ID", insSubOfferData.getString("MAIN_OFFER_ID"));// 当前元素归属的产品
                String relOfferCode = insSubOfferData.getString("REL_OFFER_CODE");// 当前元素归属的产品

                String subscriberInsId = insSubOfferData.getString("USER_ID");
                String offerType = insSubOfferData.getString("OFFER_TYPE");
                subOfferData.put("REL_OFFER_CODE", relOfferCode);
                subOfferData.put("OFFER_ID", subOfferId);
                subOfferData.put("OFFER_CODE", subOfferCode);
                subOfferData.put("OFFER_NAME", insSubOfferData.getString("OFFER_NAME"));
                subOfferData.put("OFFER_INS_ID", subOfferInsId);
                subOfferData.put("USER_ID", subscriberInsId);
                subOfferData.put("OFFER_TYPE", offerType);
                subOfferData.put("OPER_CODE", PageDataTrans.ACTION_EXITS); // 变更时，suboffer节点OPER_CODE初始为3-不变
                if ("BOSG".equals(insOfferData.getString("BRAND_CODE")) && BizCtrlType.DestoryUser.equals(operType)) {
                    subOfferData.put("OPER_CODE", PageDataTrans.ACTION_DELETE);
                }
                subOfferData.put("OFFER_INDEX", "EC".equals(type) ? insSubOfferData.getString("OFFER_INDEX") : "0"); // bboss一单多线标记
                subOfferData.put("GROUP_ID", insSubOfferData.getString("GROUP_ID", ""));
                subOfferData.put("START_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, insSubOfferData.getString("START_DATE")));
                subOfferData.put("END_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, insSubOfferData.getString("END_DATE")));

                subOfferData.put("OLD_END_DATE", ""); // 原失效时间，用于变更时存放前一次的失效时间

                subOfferData.put("CANCEL_END_DATE", ""); // 删除资费的失效时间，用于变更操作

                IDataset priceOfferData = IUpcViewCall.queryOfferEnableMode(qryofferId, insSubOfferData.getString("GROUP_ID", ""), subOfferCode, offerType);
                if (DataUtils.isNotEmpty(priceOfferData)) {// 取配置 计算生失效时间
                    String cancelDate = "";
                    if ("4".equals(priceOfferData.first().getString("CANCEL_MODE"))) {
                        cancelDate = "CANCEL_MODE4";// 给个标记，4的情况不让删除
                    } else {
                        cancelDate = SysDateMgr4Web.cancelDate(priceOfferData.first().getString("CANCEL_MODE"), priceOfferData.first().getString("ABSOLUTE_CANCEL_DATE"), priceOfferData.first().getString("CANCEL_OFFSET"), priceOfferData.first()
                                .getString("CANCEL_UNIT"));
                    }

                    subOfferData.put("CANCEL_END_DATE", cancelDate);
                }
                if (IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType)) {
                    IData offer = UpcViewCall.queryOfferByOfferId(this, offerType, subOfferCode);
                    subOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));

                    subOfferData.put("P_OFFER_INDEX", insSubOfferData.getString("P_OFFER_INDEX")); // 标识资费类商品序号

                    subOfferData.put("REPEAT_ORDER", insSubOfferData.getString("REPEAT_ORDER")); // 标识资费类商品是否可以重复订购

                    subOfferData.put("FORCE_TAG", insSubOfferData.getBoolean("FORCE_TAG", false)); // 标识是否构成关系

                    IDataset offerChas = IUpcViewCall.queryChaByOfferId(subOfferId);
                    subOfferData.put("HAS_PRICE_CHA", IDataUtil.isNotEmpty(offerChas) ? true : false);
                }
                if (IUpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)) {// 如果是产品才需要构造子销售品
                                                                            // 构造子销售品的子销售品
                    IDataset insChildOfferDataset = getSelectedOffers(subOfferCode, subscriberInsId);
                    if (IDataUtil.isNotEmpty(insChildOfferDataset)) {
                        if (BizCtrlType.ChangeUserDis.equals(operType) || BizCtrlType.ChangeMemberDis.equals(operType)) {
                            queryOrderedOfferInGroupInfo(group, subOfferId, insChildOfferDataset, operType);
                        }

                        // 查询必选子商品列表
                        IDataset mustSelSubOffers = IUpcViewCall.queryOfferComRelOfferByOfferId(offerType, subOfferCode, getVisit().getLoginEparchyCode());

                        IDataset subChildOfferDataset = new DatasetList();
                        for (int j = 0, len = insChildOfferDataset.size(); j < len; j++) {
                            IData subChildOfferData = new DataMap();
                            IData insChildOfferData = insChildOfferDataset.getData(j);
                            String childOfferCode = insChildOfferData.getString("OFFER_CODE");
                            String childOfferType = insChildOfferData.getString("OFFER_TYPE");

                            subChildOfferData.put("OFFER_ID", insChildOfferData.getString("OFFER_ID"));
                            subChildOfferData.put("OFFER_CODE", childOfferCode);
                            subChildOfferData.put("OFFER_NAME", insChildOfferData.getString("OFFER_NAME"));
                            subChildOfferData.put("OFFER_INS_ID", insChildOfferData.getString("OFFER_INS_ID"));
                            subChildOfferData.put("USER_ID", insChildOfferData.getString("USER_ID"));
                            subChildOfferData.put("OFFER_TYPE", childOfferType);
                            subChildOfferData.put("OPER_CODE", PageDataTrans.ACTION_EXITS); // 变更时，suboffer节点OPER_CODE初始为3-不变
                            if ("BOSG".equals(insOfferData.getString("BRAND_CODE")) && BizCtrlType.DestoryUser.equals(operType)) {
                                subChildOfferData.put("OPER_CODE", PageDataTrans.ACTION_DELETE);
                            }
                            subChildOfferData.put("OFFER_INDEX", "EC".equals(type) ? i : "0"); // bboss一单多线标记
                            subChildOfferData.put("GROUP_ID", insChildOfferData.getString("GROUP_ID", ""));

                            if (IUpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(insChildOfferData.getString("OFFER_TYPE"))) {
                                IData offer = UpcViewCall.queryOfferByOfferId(this, childOfferType, childOfferCode);
                                subChildOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));

                                subChildOfferData.put("START_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, insChildOfferData.getString("START_DATE")));
                                subChildOfferData.put("END_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, insChildOfferData.getString("END_DATE")));

                                subChildOfferData.put("OLD_END_DATE", ""); // 原失效时间，用于变更时存放前一次的失效时间

                                subChildOfferData.put("CANCEL_END_DATE", SysDateMgr4Web.getSysTime()); // 删除资费的失效时间，用于变更操作
                                IDataset priceChildOfferData = IUpcViewCall.queryOfferEnableMode(insChildOfferData.getString("OFFER_ID"), insChildOfferData.getString("GROUP_ID", ""), childOfferCode, childOfferType);
                                if (DataUtils.isNotEmpty(priceChildOfferData)) {// 取配置 计算生失效时间
                                    String cancelDate = "";
                                    if ("4".equals(priceChildOfferData.first().getString("CANCEL_MODE"))) {
                                        cancelDate = "CANCEL_MODE4";// 给个标记，4的情况不让删除
                                    } else {
                                        cancelDate = SysDateMgr4Web.cancelDate(priceChildOfferData.first().getString("CANCEL_MODE"), priceChildOfferData.first().getString("ABSOLUTE_CANCEL_DATE"), priceChildOfferData.first()
                                                .getString("CANCEL_OFFSET"), priceChildOfferData.first().getString("CANCEL_UNIT"));
                                    }

                                    subChildOfferData.put("CANCEL_END_DATE", cancelDate);
                                }
                                subChildOfferData.put("P_OFFER_INDEX", j); // 标识资费类商品序号

                                // 标识是否构成关系
                                boolean forceTag = false;
                                for (int k = 0, sizeK = mustSelSubOffers.size(); k < sizeK; k++) {
                                    if (insChildOfferData.getString("OFFER_ID").equals(mustSelSubOffers.getData(k).getString("OFFER_ID"))) {
                                        forceTag = true;
                                        break;
                                    }
                                }
                                subChildOfferData.put("FORCE_TAG", forceTag);

                                IDataset offerChas = IUpcViewCall.queryChaByOfferId(insChildOfferData.getString("OFFER_ID"));
                                subChildOfferData.put("HAS_PRICE_CHA", IDataUtil.isNotEmpty(offerChas) ? true : false);
                            }
                            subChildOfferDataset.add(subChildOfferData);
                        }
                        subOfferData.put("SUBOFFERS", subChildOfferDataset);
                    }
                }
                subOfferDataset.add(subOfferData);
            }
            offerData.put("SUBOFFERS", subOfferDataset);
        }

        return offerData;
    }

    /**
     * 查询商品构成实例
     * 
     * @param offerInsId
     * @param subscriberInsId
     * @param mainOfferId
     * @return
     * @throws Exception
     */
    public IDataset queryInsBundleOfferDataset(String subscriberInsId, String mainOfferCode, String mainOfferId) throws Exception {
        IDataset insOptOfferDataset = getSelectedOffers(mainOfferCode, subscriberInsId);

        IDataset returnList = new DatasetList();
        if (IDataUtil.isNotEmpty(insOptOfferDataset)) {
            IDataset mustSelJoinOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(mainOfferId, "4", "0", "");
            IDataset mustSelSubOffers = IUpcViewCall.queryOfferComRelOfferByOfferId(mainOfferId, getVisit().getLoginEparchyCode());
            if (IDataUtil.isEmpty(mustSelSubOffers)) {
                mustSelSubOffers = new DatasetList();
            }
            if (IDataUtil.isNotEmpty(mustSelJoinOffers)) {
                mustSelSubOffers.addAll(mustSelJoinOffers);
            }

            for (int i = 0, size = insOptOfferDataset.size(); i < size; i++) {
                IData insOffer = insOptOfferDataset.getData(i);
                String productId = insOffer.getString("REL_OFFER_CODE");

                String relOfferId = mainOfferId;
                String relOfferCode = mainOfferCode;
                if (!mainOfferCode.equals(productId) && !"-1".equals(productId)) {
                    relOfferCode = productId;
                    relOfferId = IUpcViewCall.getOfferIdByOfferCode(productId);
                }
                insOffer.put("REL_OFFER_CODE", relOfferCode);
                insOffer.put("REL_OFFER_ID", relOfferId);

                insOffer.put("MAIN_OFFER_ID", mainOfferId);
                insOffer.put("OFFER_INDEX", i);

                // 标识构成关系
                boolean forceTag = false;
                for (int j = 0, sizeJ = mustSelSubOffers.size(); j < sizeJ; j++) {
                    if (insOffer.getString("OFFER_ID", "").equals(mustSelSubOffers.getData(j).getString("OFFER_ID"))) {// 标识必选子商品（构成关系或者必选包的必选元素）
                        forceTag = true;
                        break;
                    }
                }
                // 解决BBOSS商品级子资费必选元素变更时可删除问题
                if ("D".equals(insOffer.getString("OFFER_TYPE"))) {
                    String groupId = insOffer.getString("GROUP_ID");
                    IDataset groupList = IUpcViewCall.queryChildOfferByGroupId(groupId, this.getVisit().getLoginEparchyCode());
                    if (IDataUtil.isNotEmpty(groupList)) {
                        for (int gSize = 0; gSize < groupList.size(); gSize++) {
                            if (groupList.getData(gSize).getString("OFFER_ID").equals(insOffer.getString("OFFER_ID"))) {
                                String selectFlag = groupList.getData(gSize).getString("SELECT_FLAG");
                                if ("0".equals(selectFlag)) {
                                    forceTag = true;
                                    break;
                                }

                            }
                        }

                    }

                }
                insOffer.put("FORCE_TAG", forceTag);
                if ("R".equals(insOffer.getString("ORDER_MODE")) || "C".equals(insOffer.getString("ORDER_MODE"))) {
                    insOffer.put("REPEAT_ORDER", true);
                }
                returnList.add(insOffer);
            }
        }
        return returnList;
    }

    /**
     * 查询商品构成实例
     * 
     * @param offerInsId
     * @param subscriberInsId
     * @param mainOfferId
     * @return
     * @throws Exception
     */
    public IDataset queryMebInsBundleOfferDataset(String ecSubscriberInsId, String mebSubscriberInsId, String mainOfferCode, String mainOfferId) throws Exception {
        IDataset insOptOfferDataset = getMebSelectedOffers(mainOfferCode, mebSubscriberInsId, ecSubscriberInsId);

        IDataset returnList = new DatasetList();
        if (IDataUtil.isNotEmpty(insOptOfferDataset)) {
            IDataset mustSelSubOffers = IUpcViewCall.queryOfferComRelOfferByOfferId(mainOfferId, getVisit().getLoginEparchyCode());
            for (int i = 0, size = insOptOfferDataset.size(); i < size; i++) {
                IData insOptOfferData = insOptOfferDataset.getData(i);
                String productId = insOptOfferData.getString("PRODUCT_ID");

                String relOfferId = mainOfferId;
                String relOfferCode = mainOfferCode;
                if (!mainOfferCode.equals(productId)) {
                    relOfferCode = productId;
                    relOfferId = IUpcViewCall.getOfferIdByOfferCode(productId);
                }
                IData insOffer = new DataMap();
                insOffer.put("MAIN_OFFER_ID", mainOfferId);
                insOffer.put("REL_OFFER_ID", relOfferId);// 归属产品
                insOffer.put("REL_OFFER_CODE", relOfferCode);// 归属产品
                insOffer.put("OFFER_INDEX", i);
                insOffer.put("OFFER_CODE", insOptOfferData.getString("ELEMENT_ID"));
                insOffer.put("OFFER_TYPE", insOptOfferData.getString("ELEMENT_TYPE_CODE"));
                insOffer.put("OFFER_INS_ID", insOptOfferData.getString("INST_ID"));
                insOffer.put("GROUP_ID", insOptOfferData.getString("PACKAGE_ID"));
                insOffer.put("START_DATE", insOptOfferData.getString("START_DATE"));
                insOffer.put("END_DATE", insOptOfferData.getString("END_DATE"));
                insOffer.put("GROUP_ID", insOptOfferData.getString("PACKAGE_ID"));
                insOffer.put("OFFER_NAME", insOptOfferData.getString("ELEMENT_NAME"));
                insOffer.put("USER_ID", insOptOfferData.getString("USER_ID"));

                IData offer = UpcViewCall.queryOfferByOfferId(this, insOptOfferData.getString("ELEMENT_TYPE_CODE"), insOptOfferData.getString("ELEMENT_ID"), "");
                insOffer.put("OFFER_ID", offer.getString("OFFER_ID"));

                // 标识构成关系
                boolean forceTag = false;
                for (int j = 0, sizeJ = mustSelSubOffers.size(); j < sizeJ; j++) {
                    if (insOffer.getString("OFFER_ID", "").equals(mustSelSubOffers.getData(j).getString("OFFER_ID"))) {// 标识必选子商品（构成关系或者必选包的必选元素）
                        forceTag = true;
                        break;
                    }
                }
                insOffer.put("FORCE_TAG", forceTag);
                returnList.add(insOffer);
            }
        }
        return returnList;
    }

    /**
     * 从后台获取静态参数页面配置
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryProdParam(IRequestCycle cycle) throws Exception {
        String offerId = getData().getString("OFFER_ID", "-1");
        IData offer = IUpcViewCall.getOfferInfoByOfferId(offerId);
        String offerCode = offer.getString("OFFER_CODE");
        String offerType = offer.getString("OFFER_TYPE");
        String operType = getData().getString("OPER_TYPE", "-1");
        IData param = new DataMap();

        String value = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, offerType, operType, "dynamicPage");
        if (StringUtils.isNotBlank(value)) {
            // CFG_PROD_ATTR_ITEM VALUE 字段配置示例
            // {"subpage":"enterprise.cs.bboss.BBossOfferParam","listener":"initPage","jspath":"enterprise/cs/bboss/scripts/BBossOfferParam.js","jslistener":"initBBossOfferParam"}
            param = new DataMap(value);
        }
        setAjax(param);
    }

    /**
     * 查询已订购商品所在的组信息
     * 
     * @param offerId
     *            子商品的上一层商品id
     * @param subOfferList
     *            子商品实例
     * @return
     * @throws Exception
     */
    public void queryOrderedOfferInGroupInfo(IData group, String offerId, IDataset subOfferList, String dealType) throws Exception {
        IDataset groupOffers = null;
        if ("1".equals(dealType)) {// 成员操作，根据集团商品编码查询其下所有产品的包特殊处理
            String mebOfferId = IUpcViewCall.queryMemOfferIdByOfferId(offerId);
            groupOffers = IUpcViewCall.queryOfferGroups(mebOfferId, this.getVisit().getLoginEparchyCode());
        } else {
            groupOffers = IUpcViewCall.queryOfferGroups(offerId, this.getVisit().getLoginEparchyCode());
        }
        if (IDataUtil.isNotEmpty(groupOffers)) {
            for (int i = 0, sizeI = groupOffers.size(); i < sizeI; i++) {
                IData groupInfo = groupOffers.getData(i);
                String groupId = groupInfo.getString("GROUP_ID");
                String groupName = groupInfo.getString("GROUP_NAME");
                String maxNum = groupInfo.getString("MAX_NUM");
                String minNum = groupInfo.getString("MIN_NUM");
                String limitType = groupInfo.getString("LIMIT_TYPE");
                String groupSelectFlag = groupInfo.getString("SELECT_FLAG");
                IDataset offerList = IUpcViewCall.queryChildOfferByGroupId(groupInfo.getString("GROUP_ID"), this.getVisit().getLoginEparchyCode());

                if (IDataUtil.isEmpty(offerList)) {
                    continue;
                }
                StringBuilder mustSelOfferIds = new StringBuilder(500);
                for (int j = 0, sizeJ = offerList.size(); j < sizeJ; j++) {
                    IData offer = offerList.getData(j);
                    String groupOfferId = offer.getString("OFFER_ID");
                    String offerSelectFlag = offer.getString("SELECT_FLAG");
                    if ("0".equals(offerSelectFlag)) {// 记录商品组内必选元素
                        mustSelOfferIds.append(groupOfferId).append("@");
                    }
                    for (int k = 0, sizeK = subOfferList.size(); k < sizeK; k++) {
                        if (groupOfferId.equals(subOfferList.getData(k).getString("OFFER_ID"))) {// 将已订购的商品加到组内已选商品列表
                            IData groupData = group.getData(groupId, new DataMap());
                            if (IDataUtil.isEmpty(groupData)) {
                                groupData.put("GROUP_ID", groupId);
                                groupData.put("GROUP_NAME", groupName);
                                groupData.put("MAX_NUM", maxNum);
                                groupData.put("MIN_NUM", minNum);
                                groupData.put("LIMIT_TYPE", limitType);
                                groupData.put("SELECT_FLAG", groupSelectFlag); // 商品组必选标记
                                groupData.put("MUST_SEL_OFFER", ""); // 商品组内必选元素

                                group.put(groupId, groupData);
                            }
                            if (SysDateMgr4Web.getLastDateThisMonth().equals(subOfferList.getData(k).getString("END_DATE"))) {
                                continue;
                            }
                            IDataset selOffers = groupData.getDataset("SEL_OFFER", new DatasetList());
                            IData selOffer = new DataMap();
                            selOffer.put("OFFER_ID", groupOfferId);
                            selOffer.put("OFFER_TYPE", subOfferList.getData(k).getString("OFFER_TYPE"));
                            selOffer.put("SELECT_FLAG", offerSelectFlag);
                            selOffers.add(selOffer);
                            groupData.put("SEL_OFFER", selOffers);
                        }
                    }
                }
                IData groupData = group.getData(groupId);
                if (IDataUtil.isNotEmpty(groupData) && mustSelOfferIds.length() > 0) {
                    String mustSelOfferId = mustSelOfferIds.substring(0, mustSelOfferIds.length() - 1).toString();
                    groupData.put("MUST_SEL_OFFER", mustSelOfferId);
                }
            }
        }
        // return group;
    }

    /**
     * 查询已订购子商品
     * 
     * @return
     * @throws Exception
     */
    public IDataset getSelectedOffers(String productId, String userId) throws Exception {
        IData input = new DataMap();
        input.put("USER_ID", userId);
        input.put("PRODUCT_ID", productId);
        input.put(Route.USER_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());

        IDataset result = CSViewCall.call(this, "CS.SelectedElementSVC.getGrpChildOffers", input);
        if (IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }

    /**
     * 查询已订购子商品
     * 
     * @return
     * @throws Exception
     */
    public IDataset getMebSelectedOffers(String productId, String userId, String grpUserId) throws Exception {
        // 此处应该还需要增加子产品的查询逻辑，后续补充
        IData input = new DataMap();
        input.put("MEB_USER_ID", userId);
        input.put("GRP_USER_ID", grpUserId);
        input.put("PRODUCT_ID", productId);
        input.put(Route.USER_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
        input.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());

        IDataset result = CSViewCall.call(this, "CS.SelectedElementSVC.getGrpMebChgElements", input);
        if (IDataUtil.isNotEmpty(result)) {
            return result.first().getDataset("SELECTED_ELEMENTS");
        }
        return null;
    }

    /*************************************** 集团规则开始 ***************************************************/
    /**
     * 选择商品后调用规则引擎（原baseinfo校验点）
     * 
     * @param offerId
     * @return
     * @throws Exception
     */
    public void checkEcOpenBaseInfoRule(String ibsysid, String custId, String eparchyCode, String productId) throws Exception {
        IData checkParam = new DataMap();

        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("CUST_ID", custId);
        checkParam.put("EPARCHY_CODE", eparchyCode);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("IBSYSID", ibsysid);

        CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);
    }

    /**
     * 选择商品后调用规则引擎（原baseinfo校验点）
     * 
     * @param offerId
     * @return
     * @throws Exception
     */
    public void checkEcInsBaseInfoRule(String userId, String custId, String eparchyCode, String productId, String operType) throws Exception {
        String svc = "";
        if (BizCtrlType.ChangeUserDis.equals(operType)) {
            svc = "CS.chkGrpUserChg";
        } else if (BizCtrlType.DestoryUser.equals(operType)) {
            svc = "CS.chkGrpUserDestroy";
        }
        IData checkParam = new DataMap();

        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("CUST_ID", custId);
        checkParam.put("EPARCHY_CODE", eparchyCode);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("USER_ID", userId);

        CSViewCall.call(this, svc, checkParam);
    }

    /**
     * 点击提交后，调用后台服务前规则（原productinfo）
     * 
     * @return
     * @throws Exception
     */
    public void checkEcProductInfoRule(IData conParams, IDataset subOffers, String operType) throws Exception {
        String svc = "";
        IDataset listOfferInfo = new DatasetList();
        if (BizCtrlType.CreateUser.equals(operType)) {
            svc = "CS.chkGrpUserOpen";
            listOfferInfo = conParams.getDataset("ELEMENT_INFO");
        } else if (BizCtrlType.ChangeUserDis.equals(operType)) {
            svc = "CS.chkGrpUserChg";
            // 变更的情况需要传入所有已订购过的元素，但是conParams中数据已经剔除了无变化的元素，所以此处重新计算
            if (DataUtils.isNotEmpty(subOffers)) {
                String relOfferCode = conParams.getString("PRODUCT_ID");
                for (int j = subOffers.size(); j > 0; j--) {
                    IData subOffer = subOffers.getData(j - 1);
                    if (StringUtils.isNotBlank(subOffer.getString("REL_OFFER_CODE"))) {
                        relOfferCode = subOffer.getString("REL_OFFER_CODE");
                    }
                    IData offerData = new DataMap();
                    offerData.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
                    offerData.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
                    offerData.put("INST_ID", subOffer.getString("OFFER_INS_ID", ""));
                    offerData.put("PACKAGE_ID", subOffer.getString("GROUP_ID", "-1")); // 商品组标识
                    offerData.put("PRODUCT_ID", relOfferCode);
                    offerData.put("MODIFY_TAG", "3".equals(subOffer.getString("OPER_CODE")) ? "exist" : subOffer.getString("OPER_CODE"));

                    offerData.put("START_DATE", subOffer.getString("START_DATE"));
                    offerData.put("END_DATE", subOffer.getString("END_DATE"));

                    IDataset compofferChaList = subOffer.getDataset("OFFER_CHA_SPECS");
                    offerData.put("ATTR_PARAM", compofferChaList);
                    listOfferInfo.add(offerData);
                }
            }
        } else if (BizCtrlType.DestoryUser.equals(operType)) {
            return;
        }

        String productId = conParams.getString("PRODUCT_ID");
        String custId = conParams.getString("CUST_ID");
        String sn = conParams.getString("SERIAL_NUMBER");
        String ibsysid = conParams.getString("IBSYSID", "");

        IData checkParam = new DataMap();

        checkParam.put("ALL_SELECTED_ELEMENTS", listOfferInfo);

        /**
         * HXYD-YZ-REQ-20160509-007郴州公司关于一元村组网校园网优惠的请示 add by luows 20160527 增加定制信息，规则校验用
         */
        IDataset grpPackages = conParams.getDataset("GRP_PACKAGE_INFO");
        if (DataUtils.isNotEmpty(grpPackages)) {
            checkParam.put("SELECTED_GRPPACKAGE_LIST", grpPackages);
        }
        // add end

        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("SELECTED_USER_ID", "-1");
        checkParam.put("POWER100_INFO", conParams.getString("POWER100_INFO"));
        checkParam.put("EPARCHY_CODE", conParams.getString("EPARCHY_CODE"));
        checkParam.put("CUST_ID", custId);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("IBSYSID", ibsysid);
        if (StringUtils.isNotBlank(conParams.getString("USER_ID", ""))) {
            checkParam.put("USER_ID", conParams.getString("USER_ID", ""));
        }

        checkParam.put("SERIAL_NUMBER", sn);
        CSViewCall.call(this, svc, checkParam);
    }

    /**
     * 点击提交后，调用后台服务前规则（原bboss校验点） 还需要根据实际情况调整
     * 
     * @return
     * @throws Exception
     */
    public void checkBbossEcOpenProductInfoRule(IData conParams) throws Exception {
        String operType = conParams.getString("OPER_TYPE");
        String groupId = conParams.getString("GROUP_ID");
        String productIdList = conParams.getString("PRODUCT_ID_LIST").toString();
        String currentProduct = conParams.getString("CURRENT_PRODUCT");

        IData checkParam = new DataMap();

        // HXYD-YZ-REQ-20171106-004新增气象通（政务版）资费 增加规则判断
        String productElementStr = conParams.getString("ELEMENT_LIST");
        IDataset productElements = new DatasetList(productElementStr);
        IDataset allElement = new DatasetList();
        if (IDataUtil.isNotEmpty(productElements)) {
            for (int i = 0; i < productElements.size(); i++) {
                String eleStr = productElements.get(i).toString();
                allElement.addAll(new DatasetList(eleStr));
            }
            checkParam.put("ALL_SELECTED_ELEMENTS", allElement);
        }

        checkParam.put("OPER_TYPE", operType);
        checkParam.put("GROUP_ID", groupId);
        checkParam.put("PRODUCT_ID_LIST", new DataMap(productIdList));
        checkParam.put("PRODUCT_ID", currentProduct);
        checkParam.put("CHK_FLAG", GroupRuleConst.BBoss);

        CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);
    }

    /*************************************** 集团规则结束 ***************************************************/

    /*************************************** 成员规则开始 ***************************************************/
    // 成员点击订购、变更、注销时调用规则（原baseinfo）
    public void checkMebBaseInfoRule(String ifCentretype, String grpUserId, String mebSN, String operType, String tradeTypeCode, String ecOfferCode) throws Exception {
        String svc = "";
        if (BizCtrlType.CreateMember.equals(operType)) {
            svc = "CS.chkGrpMebOrder";
        } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
            svc = "CS.chkGrpMebChg";
        } else if (BizCtrlType.DestoryMember.equals(operType)) {
            svc = "CS.chkGrpMebDestory";
        }
        IData conParam = new DataMap();
        if (ifCentretype.equals("2")) // 融合V网
        {
            conParam.put("IF_CENTRETYPE", ifCentretype);
        }
        conParam.put("CHK_FLAG", "BaseInfo");
        conParam.put("USER_ID", grpUserId);
        conParam.put("SERIAL_NUMBER", mebSN);
        conParam.put("TRADE_TYPE_CODE", tradeTypeCode);
        conParam.put("PRODUCT_ID", ecOfferCode);
        CSViewCall.call(this, svc, conParam);
    }

    /**
     * 集团成员Productinfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkProductInfoRule(String mebUserId, IData conParams, IDataset subOffers, String operType) throws Exception {
        String svc = "";
        IDataset listOfferInfo = new DatasetList();
        if (BizCtrlType.CreateMember.equals(operType)) {
            svc = "CS.chkGrpMebOrder";
            listOfferInfo = conParams.getDataset("ELEMENT_INFO");
        } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
            svc = "CS.chkGrpMebChg";
            // 变更的情况需要传入所有已订购过的元素，但是conParams中数据已经剔除了无变化的元素，所以此处重新计算
            if (DataUtils.isEmpty(subOffers))
                return;
            for (int j = subOffers.size(); j > 0; j--) {
                IData subOffer = subOffers.getData(j - 1);
                String relOfferCode = "";
                if (StringUtils.isNotBlank(subOffer.getString("REL_OFFER_CODE"))) {
                    relOfferCode = subOffer.getString("REL_OFFER_CODE");
                }
                IData offerData = new DataMap();
                offerData.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
                offerData.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
                offerData.put("INST_ID", subOffer.getString("OFFER_INS_ID", ""));
                offerData.put("PACKAGE_ID", subOffer.getString("GROUP_ID", "-1")); // 商品组标识
                offerData.put("PRODUCT_ID", relOfferCode);
                offerData.put("MODIFY_TAG", "3".equals(subOffer.getString("OPER_CODE")) ? "exist" : subOffer.getString("OPER_CODE"));

                offerData.put("START_DATE", subOffer.getString("START_DATE"));
                offerData.put("END_DATE", subOffer.getString("END_DATE"));

                IDataset compofferChaList = subOffer.getDataset("OFFER_CHA_SPECS");
                offerData.put("ATTR_PARAM", compofferChaList);
                listOfferInfo.add(offerData);
            }
        } else if (BizCtrlType.DestoryMember.equals(operType)) {
            return;
        }
        if (DataUtils.isEmpty(listOfferInfo))
            return;
        String grpUserId = conParams.getString("USER_ID");
        String mebSN = conParams.getString("SERIAL_NUMBER", "");

        IData checkParam = new DataMap();
        checkParam.put("USER_ID", grpUserId);
        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("SELECTED_USER_ID", mebUserId);
        checkParam.put("ALL_SELECTED_ELEMENTS", listOfferInfo);
        checkParam.put("SERIAL_NUMBER", mebSN);
        // checkParam.put("SKIP_FORCE_PACKAGE_FOR_PRODUCT", conParams.getString("SKIP_FORCE_PACKAGE_FOR_PRODUCT"));
        CSViewCall.call(this, svc, checkParam);
    }

    /*************************************** 成员规则结束 ***************************************************/

    /**
     * 把构成数据拼到SUBOFFERS liaolc 2018-3-6
     */
    public void buildMustSelOfferInSubOffers(IDataset mustSelOfferList, IDataset subOfferList, IData offerData) throws Exception {
        if (IDataUtil.isNotEmpty(mustSelOfferList)) {
            for (int i = 0, size = mustSelOfferList.size(); i < size; i++) {
                IData mustSelOfferData = mustSelOfferList.getData(i);
                String mustSelOfferId = mustSelOfferData.getString("OFFER_ID");
                boolean isExist = false;
                if (IDataUtil.isNotEmpty(subOfferList)) {
                    for (int j = 0, sizeJ = subOfferList.size(); j < sizeJ; j++) {
                        if (mustSelOfferId.equals(subOfferList.getData(j).getString("OFFER_ID"))) {
                            isExist = true;
                            break;
                        }
                    }
                }
                if (!isExist)// 构成数据在SUBOFFERS信息里不存在,拼入SUBOFFERS
                {// mustSelOfferData->subOfferList
                    if (IDataUtil.isNotEmpty(mustSelOfferList)) {
                        for (int j = 0, sizeJ = mustSelOfferList.size(); j < sizeJ; j++) {
                            IData subOffer = mustSelOfferList.getData(j);
                            String subOfferId = subOffer.getString("OFFER_ID");

                            // 查询OFFER信息
                            IData offer = IUpcViewCall.queryOfferByOfferId(subOfferId);
                            if (DataUtils.isEmpty(offer)) {
                                continue;
                            }
                            String subOfferCode = offer.getString("OFFER_CODE");
                            String subOfferType = offer.getString("OFFER_TYPE");

                            IData subOfferData = new DataMap();
                            subOfferData.put("OFFER_ID", offer.getString("OFFER_ID"));
                            subOfferData.put("OFFER_CODE", subOfferCode);
                            subOfferData.put("OFFER_TYPE", subOfferType);
                            subOfferData.put("OPER_CODE", offerData.getString("OPER_CODE"));
                            subOfferData.put("GROUP_ID", "-1"); // 构成的组为-1
                            subOfferData.put("OFFER_NAME", subOffer.getString("OFFER_NAME"));
                            subOfferData.put("FORCE_TAG", subOffer.getString("FORCE_TAG")); // 标识是否构成关系
                            if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType)) {
                                subOfferData.put("BRAND_CODE", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, subOfferCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
                            }
                            subOfferData.put("START_DATE", SysDateMgr4Web.getSysTime());
                            subOfferData.put("END_DATE", TimeUtil.EXPIRE_DATE);
                            subOfferList.add(subOfferData);
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理SUBOFFERS数据，包括构成数据 liaolc 2018-3-6
     * 
     */
    public void dealSubOffers(IData subOffer, IData offerData, IData subOfferData, IDataset serviceOfferList, IDataset priceOfferList, boolean isFrist) throws Exception {
        String mainOfferId = offerData.getString("OFFER_ID");
        String subOfferId = subOffer.getString("OFFER_ID");
        // 1.2.1查询OFFER信息
        IData offer = IUpcViewCall.queryOfferByOfferId(subOfferId);

        if (DataUtils.isEmpty(offer)) {
            return;
        }
        String subOfferCode = offer.getString("OFFER_CODE");
        String subOfferType = offer.getString("OFFER_TYPE");

        subOfferData.put("MAIN_OFFER_ID", mainOfferId);
        subOfferData.put("OFFER_ID", subOfferId);
        subOfferData.put("OFFER_CODE", subOfferCode);
        subOfferData.put("OFFER_TYPE", subOfferType);
        subOfferData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
        subOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));
        subOfferData.put("OPER_CODE", offerData.getString("OPER_CODE"));
        subOfferData.put("GROUP_ID", subOffer.getString("GROUP_ID"));
        subOfferData.put("FORCE_TAG", subOffer.getString("FORCE_TAG")); // 标识是否构成关系
        subOfferData.put("START_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, subOffer.getString("START_DATE")));
        subOfferData.put("END_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, subOffer.getString("END_DATE")));

        // 1.2.2添加是否待设置标签
        IDataset subOfferChaSpecs = subOffer.getDataset("OFFER_CHA_SPECS");
        if (DataUtils.isNotEmpty(subOfferChaSpecs)) {
            subOfferData.put("OFFER_CHA_SPECS", subOfferChaSpecs);
        }

        // 1.2.3资费
        if (UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOfferType)) {// 类型为资费，需查询商品默认生失效时间以及是否有弹性资费

            subOfferData.put("P_OFFER_INDEX", 0); // 标识资费类商品序号

            isHavePirceCha(subOfferData, offerData.getString("OPER_TYPE"), offerData.getString("OPER_CODE"), subOfferId, "");

            if (isFrist) // 第一层循环设置priceOfferList
            {
                priceOfferList.add(subOfferData);
            }

        }
        // 1.2.4服务
        else if (UpcConst.ELEMENT_TYPE_CODE_SVC.equals(subOfferType)) {// 非资费类型的，需查询其下必选子商品
            boolean hasOfferCha = CommonViewCall.hasOfferSpecCha(this, offerData.getString("OPER_TYPE"), offerData.getString("OPER_CODE"), subOfferId, "");
            subOfferData.put("IS_SHOW_SET_TAG", hasOfferCha);
            if (isFrist) // 第一层循环设置服务和产品都serviceOfferList
            {
                serviceOfferList.add(subOfferData);
            }
        }
        // 1.2.5子商品
        else if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType)) {
            // 标记产品序号，主要是针对BBOSS产品，一单多线的情况
            subOfferData.put("BRAND_CODE", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, subOfferCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
            subOfferData.put("OFFER_INDEX", subOffer.getString("OFFER_INDEX"));
            subOfferData.put(EcConstants.SUBIBID_RNUM, subOffer.getString(EcConstants.SUBIBID_RNUM));// 需要登记到CRM-tf_B_trade表RSRV_STR4，用于CRM与ESOP产品对应关系
            subOfferData.put("IS_SHOW_SET_TAG", true); // 如果是产品，显示待设置标签
            if (isFrist) // 第一层循环设置服务和产品都serviceOfferList
            {
                serviceOfferList.add(subOfferData);
            }
        }

    }

    /**
     * 处理SUBOFFERS数据，包括构成数据 liaolc 2018-3-21
     * 
     */
    public void dealUpdataAndDeleSubOffers(IData subOffer, IData offerData, IData subOfferData, IDataset serviceOfferList, IDataset priceOfferList, boolean isFrist) throws Exception {
        String mainOfferId = offerData.getString("OFFER_ID");
        String subOfferId = subOffer.getString("OFFER_ID");
        // 1.2.1查询OFFER信息
        IData offer = IUpcViewCall.queryOfferByOfferId(subOfferId);
        if (DataUtils.isEmpty(offer)) {
            return;
        }
        String subOfferCode = offer.getString("OFFER_CODE");
        String subOfferType = offer.getString("OFFER_TYPE");

        subOfferData.put("MAIN_OFFER_ID", mainOfferId);
        subOfferData.put("OFFER_ID", subOfferId);
        subOfferData.put("OFFER_CODE", subOfferCode);
        subOfferData.put("OFFER_TYPE", subOfferType);
        subOfferData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
        subOfferData.put("DESCRIPTION", offer.getString("DESCRIPTION"));
        subOfferData.put("OPER_CODE", PageDataTrans.ACTION_EXITS);
        subOfferData.put("GROUP_ID", subOffer.getString("GROUP_ID"));
        subOfferData.put("START_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, subOffer.getString("START_DATE")));
        subOfferData.put("END_DATE", TimeUtil.format(TimeUtil.YYYY_MM_DD_HH_MM_SS, subOffer.getString("END_DATE")));
        subOfferData.put("USER_ID", subOffer.getString("USER_ID"));
        subOfferData.put("OFFER_INS_ID", subOffer.getString("OFFER_INS_ID"));
        subOfferData.put("OLD_END_DATE", ""); // 原失效时间，用于变更时存放前一次的失效时间

        subOfferData.put("CANCEL_END_DATE", SysDateMgr4Web.getSysTime()); // 删除资费的失效时间，用于变更操作

        IData data = new DataMap();
        IData mainOffer = IUpcViewCall.queryOfferByOfferId(mainOfferId);
        String productId = mainOffer.getString("OFFER_CODE");
        data.put("PRODUCT_ID", productId);

        IDataset priceOfferData = null;
        if ("7342".equals(productId) || "7343".equals(productId) || "7344".equals(productId)) {
            String groupId = offerData.getString("GROUP_ID");
            IData customer = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            data.put("USER_EPARCHY_CODE", "0898");
            data.put("GROUP_ID", groupId);
            data.put("CUST_ID", customer.getString("CUST_ID"));
            /* try { priceOfferData = IUpcViewCall.queryOfferByOfferIdGroupIdRelOfferIdType(mainOfferId, subOfferId, subOffer.getString("GROUP_ID", ""), null, null); if(DataUtils.isNotEmpty(priceOfferData)) {// 取配置 计算生失效时间 String cancelDate = "";
             * if("4".equals(priceOfferData.first().getString("CANCEL_MODE"))) { cancelDate = "CANCEL_MODE4";// 给个标记，4的情况不让删除 } else { cancelDate = SysDateMgr4Web.cancelDate(priceOfferData.first().getString("CANCEL_MODE"),
             * priceOfferData.first().getString("ABSOLUTE_CANCEL_DATE"), priceOfferData.first().getString("CANCEL_OFFSET"), priceOfferData.first() .getString("CANCEL_UNIT")); }
             * 
             * subOfferData.put("CANCEL_END_DATE", cancelDate); } } catch (Exception e) { */
            priceOfferData = CSViewCall.call(this, "CS.SelectedElementSVC.getGrpUserOpenElements", data);
            if (DataUtils.isNotEmpty(priceOfferData)) {// 取配置 计算生失效时间
                String cancelDate = "";
                IDataset selectElements = priceOfferData.first().getDataset("SELECTED_ELEMENTS");
                for (int i = 0; i < selectElements.size(); i++) {
                    IData selectElement = selectElements.getData(i);
                    if (StringUtils.equals(subOfferCode, selectElement.getString("ELEMENT_ID"))) {
                        cancelDate = "CANCEL_MODE4";// 给个标记，4的情况不让删除
                        break;
                    } else {
                        cancelDate = SysDateMgr4Web.getLastDateThisMonth();
                    }
                }

                if (StringUtils.isNotBlank(cancelDate)) {
                    subOfferData.put("CANCEL_END_DATE", cancelDate);
                }
            }
            // }
        } else {
            priceOfferData = IUpcViewCall.queryOfferByOfferIdGroupIdRelOfferIdType(mainOfferId, subOfferId, subOffer.getString("GROUP_ID", ""), null, null);
            if (DataUtils.isNotEmpty(priceOfferData)) {// 取配置 计算生失效时间
                String cancelDate = "";
                if ("4".equals(priceOfferData.first().getString("CANCEL_MODE"))) {
                    cancelDate = "CANCEL_MODE4";// 给个标记，4的情况不让删除
                } else {
                    cancelDate = SysDateMgr4Web.cancelDate(priceOfferData.first().getString("CANCEL_MODE"), priceOfferData.first().getString("ABSOLUTE_CANCEL_DATE"), priceOfferData.first().getString("CANCEL_OFFSET"), priceOfferData.first()
                            .getString("CANCEL_UNIT"));
                }
                subOfferData.put("CANCEL_END_DATE", cancelDate);
            }
        }

        // 1.2.2添加是否待设置标签
        IDataset subOfferChaSpecs = subOffer.getDataset("OFFER_CHA_SPECS");
        if (DataUtils.isNotEmpty(subOfferChaSpecs)) {
            subOfferData.put("OFFER_CHA_SPECS", subOfferChaSpecs);
        }

        // 1.2.3资费
        if (UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(subOfferType)) {// 类型为资费，需查询商品默认生失效时间以及是否有弹性资费

            subOfferData.put("P_OFFER_INDEX", 0); // 标识资费类商品序号
            isHavePirceCha(subOfferData, offerData.getString("OPER_TYPE"), offerData.getString("OPER_CODE"), subOfferId, "");
            if (isFrist) // 第一层循环设置priceOfferList
            {
                priceOfferList.add(subOfferData);
            }
        }
        // 1.2.4服务
        else if (UpcConst.ELEMENT_TYPE_CODE_SVC.equals(subOfferType)) {// 非资费类型的，需查询其下必选子商品
            boolean hasOfferCha = CommonViewCall.hasOfferSpecCha(this, offerData.getString("OPER_TYPE"), offerData.getString("OPER_CODE"), subOfferId, "");
            subOfferData.put("IS_SHOW_SET_TAG", hasOfferCha);
            if (isFrist) // 第一层循环设置服务和产品都serviceOfferList
            {
                serviceOfferList.add(subOfferData);
            }
        }
        // 1.2.5子商品
        else if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType)) {
            // 标记产品序号，主要是针对BBOSS产品，一单多线的情况
            subOfferData.put("BRAND_CODE", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, subOfferCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
            subOfferData.put("OFFER_INDEX", subOffer.getString("OFFER_INDEX"));
            subOfferData.put(EcConstants.SUBIBID_RNUM, subOffer.getString(EcConstants.SUBIBID_RNUM));// 需要登记到CRM-tf_B_trade表RSRV_STR4，用于CRM与ESOP产品对应关系
            subOfferData.put("IS_SHOW_SET_TAG", true); // 如果是产品，显示待设置标签
            if (isFrist) // 第一层循环设置服务和产品都serviceOfferList
            {
                serviceOfferList.add(subOfferData);
            }
        }

    }

    private void isHavePirceCha(IData optOfferData, String operType, String merchpOperType, String offerId, String brand) throws Exception {
        // 判断是否有商品特征
        boolean hasOfferChaSpec = CommonViewCall.hasOfferSpecCha(this, operType, merchpOperType, offerId, brand);
        if (hasOfferChaSpec) {
            optOfferData.put("HAS_PRICE_CHA", true);
            optOfferData.put("PRICE_CHA_TYPE", "POINT");// 通过point表配置
        } else {
            IDataset offerChas = IUpcViewCall.queryChaByOfferId(offerId);
            optOfferData.put("HAS_PRICE_CHA", DataUtils.isNotEmpty(offerChas) ? true : false);
            optOfferData.put("PRICE_CHA_TYPE", "OFFER");// 通过产商品表配置
        }
    }

}
