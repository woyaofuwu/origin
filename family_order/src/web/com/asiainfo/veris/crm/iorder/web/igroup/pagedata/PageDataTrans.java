package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PageDataTrans {
    protected static final Logger logger = LoggerFactory.getLogger(PageDataTrans.class);

    public final static String ACTION_CREATE = "0";

    public final static String ACTION_UPDATE = "2";

    public final static String ACTION_DELETE = "1";

    public final static String ACTION_EXITS = "3";

    public final static String ACTION_PAUSE = "5";

    public final static String ACTION_CONTINUE = "6";

    private IDataset inParams = new DatasetList();

    private String svcName = "";

    public static PageDataTrans getInstance(IData cond) throws Exception {
        PageDataTrans transData = null;
        String offerCode = cond.getString("OFFER_CODE");
        String operType = cond.getString("OPER_TYPE");
        String clazzName = CommonViewCall.getAttrValueFromAttrBiz(offerCode, "P", operType, "TransformClass");
        if (StringUtils.isNotBlank(clazzName)) {
            Class clazz = Class.forName(clazzName);
            transData = (PageDataTrans) clazz.newInstance();
        } else {
            if (BizCtrlType.CreateUser.equals(operType)) {
                transData = new CreateGroupUserPageDataTrans();
            } else if (BizCtrlType.ChangeUserDis.equals(operType)) {
                transData = new ChangeGroupUserPageDataTrans();
            } else if (BizCtrlType.DestoryUser.equals(operType)) {
                transData = new DestroyGroupUserPageDataTrans();
            } else if (BizCtrlType.CreateMember.equals(operType)) {
                transData = new CreateGroupMemberPageDataTrans();
            } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
                transData = new ChangeGroupMemberPageDataTrans();
            } else if (BizCtrlType.DestoryMember.equals(operType)) {
                transData = new DestroyGroupMemberPageDataTrans();
            } else if (BizCtrlType.OpenGroupMeb.equals(operType)) {
                transData = new OpenEnterpriseMemberPageDataTrans();
            }
        }

        if (transData != null) {
            transData.setInParams(cond.getDataset("IN_PARAMS"));
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据传入条件获取数据转换类失败！ 条件:" + cond);
        }

        return transData;
    }

    /**
     * 子类重写改方法实现页面数据转换，并实现setServiceName
     * 
     * @return
     * @throws Exception
     */
    public IData transformData() throws Exception {
        setServiceName();

        return new DataMap();
    }

    /**
     * 转换资源信息数据结构
     * 
     * @param commonInfo
     * @return
     * @throws Exception
     */
    protected IDataset transformResInfo(IData commonInfo) throws Exception {
        IDataset resInfoList = new DatasetList();

        if (IDataUtil.isEmpty(commonInfo)) {
            return resInfoList;
        }

        IDataset resourceInfoList = commonInfo.getDataset("RES_INFO");
        if (IDataUtil.isEmpty(resourceInfoList)) {
            return resInfoList;
        }

        for (int i = 0, size = resourceInfoList.size(); i < size; i++) {
            IData resInfo = new DataMap();
            resInfo.put("RES_CODE", resourceInfoList.getData(i).getString("RES_CODE"));
            resInfo.put("RES_TYPE_CODE", resourceInfoList.getData(i).getString("RES_TYPE_CODE"));
            resInfo.put("RES_TYPE", resourceInfoList.getData(i).getString("RES_TYPE"));
            resInfo.put("MODIFY_TAG", resourceInfoList.getData(i).getString("MODIFY_TAG", ""));

            // 解决资源号码不入res表的问题,塞入modify_tag字段且写死为0,资源号码可以顺利入表,但不清楚其他特殊资源信息有无问题,但基本逻辑维持和原来一样
            if (StringUtils.isBlank(resInfo.getString("MODIFY_TAG"))) {
                resInfo.put("MODIFY_TAG", "0");
            }

            resInfoList.add(resInfo);
        }
        return resInfoList;
    }

    /**
     * 转换offer数据结构
     * 
     * @param offerInfos
     * @return
     * @throws Exception
     */
    protected IDataset transformOfferList(IDataset offerInfos) throws Exception {
        IDataset listOfferInfo = new DatasetList();

        IData mainOfferInfo = new DataMap();
        IDataset attachOfferList = new DatasetList();

        for (int i = 0, size = offerInfos.size(); i < size; i++) {
            IData offerInfo = offerInfos.getData(i);
            String roleId = offerInfo.getString("ROLE_ID", "");
            if ("4".equals(roleId)) {// 附加商品
                attachOfferList.add(offerInfo);
            } else {// 主商品
                mainOfferInfo.putAll(offerInfo);
            }
        }

        String mainOfferCode = mainOfferInfo.getString("OFFER_CODE");
        IDataset subOffers = mainOfferInfo.getDataset("SUBOFFERS");
        if (DataUtils.isNotEmpty(subOffers)) {
            IDataset subOfferList = buildListOfferInfo(subOffers, mainOfferCode);
            listOfferInfo.addAll(subOfferList);
        }

        if (DataUtils.isNotEmpty(attachOfferList)) {
            IDataset subOfferList = buildListOfferInfo(attachOfferList, mainOfferCode);
            listOfferInfo.addAll(subOfferList);
        }

        return listOfferInfo;
    }

    protected IDataset transformOfferChaList(String offerCode, IDataset offerChaList) throws Exception {
        IDataset paramAttrList = new DatasetList();

        if (IDataUtil.isNotEmpty(offerChaList)) {
            IData paramAttr = new DataMap();
            paramAttr.put("PRODUCT_ID", offerCode);
            paramAttr.put("PRODUCT_PARAM", offerChaList);
            paramAttrList.add(paramAttr);
        }

        return paramAttrList;
    }

    protected IDataset buildListOfferInfo(IDataset subOffers, String relOfferCode) throws Exception {
        IDataset listOfferInfo = new DatasetList();
        IDataset tempSubOffers = subOffers;
        for (int j = tempSubOffers.size(); j > 0; j--) {
            IData subOffer = tempSubOffers.getData(j - 1);

            /* String subOfferOperCode = subOffer.getString("OPER_CODE"); if(ACTION_UPDATE.equals(subOfferOperCode) || ACTION_EXITS.equals(subOfferOperCode)) { boolean isChaValueChg = subOffer.getBoolean("IS_CHA_VALUE_CHANGE", false); IDataset
             * subOfferChaList = subOffer.getDataset("OFFER_CHA_SPECS"); if(IDataUtil.isNotEmpty(subOfferChaList) && isChaValueChg) { subOffer.put("OPER_CODE", ACTION_UPDATE); } else { subOffer.put("OPER_CODE", ACTION_EXITS); } } */
            if (ACTION_EXITS.equals(subOffer.getString("OPER_CODE"))) {// 如果子商品没有发生改变，则移除
                tempSubOffers.remove(j - 1);
                continue;
            }
            if (StringUtils.isNotBlank(subOffer.getString("REL_OFFER_CODE"))) {
                relOfferCode = subOffer.getString("REL_OFFER_CODE");
            }
            IData offerData = new DataMap();
            offerData.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
            offerData.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
            offerData.put("INST_ID", subOffer.getString("OFFER_INS_ID", ""));
            offerData.put("PACKAGE_ID", subOffer.getString("GROUP_ID", "0")); // 商品组标识
            offerData.put("PRODUCT_ID", relOfferCode);
            offerData.put("MODIFY_TAG", subOffer.getString("OPER_CODE"));

            offerData.put("START_DATE", subOffer.getString("START_DATE"));
            offerData.put("END_DATE", subOffer.getString("END_DATE"));

            if (IDataUtil.isNotEmpty(subOffer.getDataset("OFFER_CHA_SPECS", new DatasetList()))) {
                offerData.put("ATTR_PARAM", subOffer.getDataset("OFFER_CHA_SPECS"));
            }

            listOfferInfo.add(offerData);
        }
        return listOfferInfo;
    }

    protected IDataset transAskPostInfoList(IData commonInfo) throws Exception {
        IData newAsk = commonInfo.getData("ASK_INFO", new DataMap());
        IData oldAsk = newAsk.getData("OLD_ASKPRINT_INFO", new DataMap());
        newAsk.remove("OLD_ASKPRINT_INFO");
        IDataset askprintIfos = GroupBaseView.compareData(newAsk, oldAsk, "RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6");

        return askprintIfos;
    }

    /**
     * 设置业务受理服务名
     * 
     * @throws Exception
     */
    public abstract void setServiceName() throws Exception;

    public String getSerialNumber() throws Exception {
        return getInParams().first().getString("SERIAL_NUMBER", "");
    }

    public String getProductId() throws Exception {
        String productId = "";
        IDataset offerList = getOfferList();
        if (IDataUtil.isNotEmpty(offerList)) {
            productId = offerList.first().getString("OFFER_CODE");
        }
        return productId;
    }

    public String getBrandCode() throws Exception {
        String brandCode = "";
        IDataset offerList = getOfferList();
        if (IDataUtil.isNotEmpty(offerList)) {
            brandCode = offerList.first().getString("BRAND_CODE");
        }
        return brandCode;
    }

    /**
     * 转换商品特征数据结构
     * 
     * @param offerInfos
     * @return
     * @throws Exception
     */
    public IDataset getOfferChaList() throws Exception {
        IDataset offerInfos = getOfferList();
        IDataset compofferChaList = new DatasetList();

        for (int i = 0, sizeI = offerInfos.size(); i < sizeI; i++) {
            IData offerInfo = offerInfos.getData(i);
            if ("4".equals(offerInfo.getString("ROLE_ID"))) {// 附加商品
                continue;
            }

            compofferChaList = offerInfo.getDataset("OFFER_CHA_SPECS");

            if (IDataUtil.isEmpty(compofferChaList) && !ACTION_CREATE.equals(offerInfo.getString("OPER_CODE")) && !ACTION_DELETE.equals(offerInfo.getString("OPER_CODE"))) {// 前台没有变更属性也需要查询属性实例，防止后台报错
                String userId = offerInfo.getString("USER_ID");
                String relaInstId = offerInfo.getString("OFFER_INS_ID");
                IData data = new DataMap();
                data.put("USER_ID", userId);
                data.put("INST_TYPE", "P");
                data.put("INST_ID", relaInstId);
                data.put(Route.ROUTE_EPARCHY_CODE, getEparchyCode());
                IDataInput input = new DataInput();
                input.getData().putAll(data);
                IDataOutput out = ServiceFactory.call("CS.UserAttrInfoQrySVC.getUserAttrByUserIdInstid", input);
                IDataset userAttrList = out.getData();
                compofferChaList = buildOfferChaSpecsFromAttrList(userAttrList);
            }
            // if(IDataUtil.isNotEmpty(compofferChaList) && !ACTION_CREATE.equals(offerInfo.getString("OPER_CODE")) && !offerInfo.getBoolean("IS_CHA_VALUE_CHANGE", false))
            // {//当商品不是新增状态且商品特征没有改变，则不返回商品特征
            // compofferChaList.clear();
            // break;
            // }
        }

        return compofferChaList;
    }

    /**
     * BBOSS预受理识别
     * 
     * @param offerCode
     * @return
     * @throws Exception
     */
    public IDataset getAheadData(String offerCode) throws Exception {
        IData tempBiz = new DataMap();
        tempBiz.put("ID", "1");
        tempBiz.put("ID_TYPE", "B");
        tempBiz.put("ATTR_OBJ", "AHEAD");
        tempBiz.put("ATTR_CODE", offerCode);
        tempBiz.put(Route.ROUTE_EPARCHY_CODE, getEparchyCode());

        IDataInput input = new DataInput();
        input.getData().putAll(tempBiz);

        IDataOutput out = ServiceFactory.call("CS.AttrBizInfoQrySVC.getBizAttr", input);
        IDataset userAttrList = out.getData();

        return userAttrList;
    }

    public IDataset getOfferList() throws Exception {
        return getInParams().first().getDataset("OFFERS");
    }

    public IData getCommonData() throws Exception {
        IData commonData = getInParams().first().getData("COMMON_INFO");
        return IDataUtil.isNotEmpty(commonData) ? commonData : new DataMap();
    }

    public IData getOtherData() throws Exception {
        IData otherData = getInParams().first().getData("OTHER_INFO");
        return IDataUtil.isNotEmpty(otherData) ? otherData : new DataMap();
    }

    public IData getEcAccount() throws Exception {
        return getInParams().first().getData("ACCT_INFO");
    }

    public IData getEcCustomer() throws Exception {
        return getInParams().first().getData("CUST_INFO");
    }

    public IData getEcSubscriber() throws Exception {
        return getInParams().first().getData("SUBSCRIBER");
    }

    public IData getMemAccount() throws Exception {
        return getInParams().first().getData("MEM_ACCT_INFO");
    }

    public IData getMemCustomer() throws Exception {
        return getInParams().first().getData("MEM_CUST_INFO");
    }

    public IData getMemSubscriber() throws Exception {
        return getInParams().first().getData("MEM_SUBSCRIBER");
    }

    public IDataset getInParams() {
        return inParams;
    }

    public void setInParams(IDataset inParams) {
        this.inParams = inParams;
    }

    public String getSvcName() {
        return svcName;
    }

    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }

    public static String transOperCodeToOperType(String operCode, String mode) throws Exception {
        String obj = "";

        if ("EC".equals(mode)) {
            if (ACTION_CREATE.equals(operCode)) {
                obj = BizCtrlType.CreateUser;
            } else if (ACTION_UPDATE.equals(operCode)) {
                obj = BizCtrlType.ChangeUserDis;
            } else if (ACTION_DELETE.equals(operCode)) {
                obj = BizCtrlType.DestoryUser;
            }
        } else if ("MEM".equals(mode) || "MEB".equals(mode)) {
            if (ACTION_CREATE.equals(operCode)) {
                obj = BizCtrlType.CreateMember;
            } else if (ACTION_UPDATE.equals(operCode)) {
                obj = BizCtrlType.ChangeMemberDis;
            } else if (ACTION_DELETE.equals(operCode)) {
                obj = BizCtrlType.DestoryMember;
            }
        }
        return obj;
    }

    public static String transOperTypeToOperCode(String operType) {
        String operCode = ACTION_EXITS;

        if (BizCtrlType.CreateUser.equals(operType) || BizCtrlType.CreateMember.equals(operType)) {
            operCode = ACTION_CREATE;
        } else if (BizCtrlType.ChangeUserDis.equals(operType) || BizCtrlType.ChangeMemberDis.equals(operType)) {
            operCode = ACTION_UPDATE;
        } else if (BizCtrlType.DestoryUser.equals(operType) || BizCtrlType.DestoryMember.equals(operType)) {
            operCode = ACTION_DELETE;
        }

        return operCode;
    }

    private String getEparchyCode() throws Exception {
        String eparchyCode = "cg";
        IData memSubscriber = getMemSubscriber();
        if (IDataUtil.isNotEmpty(memSubscriber)) {
            eparchyCode = memSubscriber.getString("EPARCHY_CODE");
        }
        return eparchyCode;
    }

    private IDataset buildOfferChaSpecsFromAttrList(IDataset userAttrList) throws Exception {
        IDataset offerChaSpecs = new DatasetList();
        if (IDataUtil.isNotEmpty(userAttrList)) {
            for (int i = 0, size = userAttrList.size(); i < size; i++) {
                IData offerCha = new DataMap();
                offerCha.put("ATTR_CODE", userAttrList.getData(i).getString("ATTR_CODE"));
                offerCha.put("ATTR_VALUE", userAttrList.getData(i).getString("ATTR_VALUE"));
                offerChaSpecs.add(offerCha);
            }
        }
        return offerChaSpecs;
    }

}
