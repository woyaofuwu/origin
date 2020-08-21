package com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class MinorecIntegrateTrans {
    private static final transient Logger logger = Logger.getLogger(MinorecIntegrateTrans.class);

    /**
     * 开通申请, 数据处理
     * */
    public static void transformCreateHotelIntegrationSubmitData(IData submitData) throws Exception {

        String openType = submitData.getString("OPER_TYPE");

        IDataset quickOrderOfferList = submitData.getDataset("OFFER_LIST");
        if (IDataUtil.isEmpty(quickOrderOfferList)) {
            return;
        }

        IData custInfo = submitData.getData("CUST_DATA");
        if (IDataUtil.isEmpty(custInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "客户信息不能为空！");
        }

        String custId = custInfo.getString("CUST_ID");

        IDataset offerMebList = new DatasetList();
        IDataset offerDataList = new DatasetList();

        IData offerData = submitData.getData("OFFER_DATA_LIST");

        IDataset SUBOFFERS = new DatasetList();

        for (int i = 0; i < quickOrderOfferList.size(); i++) {
            IData quickOrderOffer = quickOrderOfferList.getData(i);

            String dealType = quickOrderOffer.getString("DEAL_TYPE", "");

            // 产品存产品表
            IData ecOffer = quickOrderOffer.getData("EC_OFFER");
            IData mebOffer = quickOrderOffer.getData("MEB_OFFER");

            IData ecOfferData = new DataMap();
            ecOfferData.putAll(ecOffer);
            ecOfferData.put("DEAL_TYPE", dealType);

            String ecProductId = ecOffer.getString("OFFER_CODE");
            if ("7341".equals(ecProductId)) {
                IData widenetData = quickOrderOffer.getData("WIDENET_OPEN_DATA");
                if (IDataUtil.isNotEmpty(widenetData)) {
                    IDataset addressList = widenetData.getDataset("ADDRESS_LIST");

                    IDataset productExtList = new DatasetList();

                    for (int j = 0; j < addressList.size(); j++)
                    {
                        IData addressData = addressList.getData(j);

                        String wnSn = addressData.getString("WIDE_SERIAL_NUMBER");
                        addressData.put("PRODUCT_ID", widenetData.getString("WIDE_PRODUCT_ID", addressData.getString("WIDE_PRODUCT_ID", "")));
                        addressData.put("PRODUCT_NAME", widenetData.getString("WIDE_PRODUCT_NAME", addressData.getString("WIDE_PRODUCT_NAME", "")));
                        addressData.put("CUST_ID", widenetData.getString("CUST_ID"));
                        addressData.put("DETAIL_ADDRESS", addressData.getString("STAND_ADDRESS"));

                        String mebSn = wnSn.startsWith("KD_") ? wnSn.replace("KD_", "") : wnSn;
                        String ecSn = mebSn.substring(0, mebSn.length()-4);

                        addressData.put("EC_SERIAL_NUMBER", ecSn);
                        addressData.put("SERIAL_NUMBER", addressData.getString("WIDE_SERIAL_NUMBER"));
                        addressData.put("RSRV_STR1", "WIDENET");

                        if (BizCtrlType.MinorecDestroyMember.equals(openType)) // 宽带注销
                        {
                             /*  wideNetInfo.put("HIDDEN_STATE","" );
                                wideNetInfo.put("AUTH_SERIAL_NUMBER","" );     //集团服务号码
                                wideNetInfo.put("SELECTED_AUTH_USER","" );     //成员userId
                                wideNetInfo.put("SERIAL_NUMBER","" );          //成员宽带号码
                                wideNetInfo.put("WIDE_NET_SERIAL_NUMBER","" ); //成员宽带号码
                                wideNetInfo.put("WIDE_TYPE_CODE","" );         //宽带类型*/


                            addressData.put("AUTH_SERIAL_NUMBER", ecSn); // 集团 服务号码
                            addressData.put("SELECTED_AUTH_USER", addressData.getString("MEB_USER_ID")); // 成员user_id
                            addressData.put("WIDE_NET_SERIAL_NUMBER", addressData.getString("WIDE_SERIAL_NUMBER"));
                        }
                        else if (BizCtrlType.MinorecChangeWideNet.equals(openType)) // 宽带变更
                        {
                            addressData.put("AUTH_SERIAL_NUMBER",  ecSn); // 集团 服务号码
                            addressData.put("SELECTED_AUTH_USER",  addressData.getString("MEB_USER_ID")); // 成员的USERID
                            addressData.put("WIDE_NET_SERIAL_NUMBER", addressData.getString("WIDE_SERIAL_NUMBER"));
                            addressData.put("WIDE_NET_USER_ID", addressData.getString("MEB_USER_ID")); // 成员的USERID
                            addressData.put("WIDE_USER_SELECTED_SERVICEIDS", addressData.getString("PRODUCT_ID")); // 成员的USERID

                            IDataset selectedElements = new DatasetList(addressData.getString("SELECTED_ELEMENTS"));
                            addressData.put("SELECTED_ELEMENTS",selectedElements);

                        }

                        widenetData.remove("ADDRESS_LIST");

                        // SELECTED_ELEMENTS 在开通申请时 取 widenetData ， 变更时 取addressList
                        IDataset selecementDataList = widenetData.getDataset("SELECTED_ELEMENTS");
                        if (IDataUtil.isNotEmpty(selecementDataList))
                        {
                            addressData.remove("SELECTED_ELEMENTS");
                        }

                        addressData.putAll(widenetData);

                        offerMebList.add(addressData);

                        IData productExt = new DataMap();
                        productExt.put("SERIAL_NUMBER", addressData.getString("SERIAL_NUMBER"));
                        productExt.put("OFFER_CODE", addressData.getString("PRODUCT_ID"));
                        productExt.put("OFFER_NAME", addressData.getString("PRODUCT_NAME"));
                        productExt.put("USER_ID", addressData.getString("USER_ID", addressData.getString("MEB_USER_ID", "")));
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            if (IDataUtil.isNotEmpty(mebOffer) && !"ESP".equals(dealType)) {
                String mebProductId = mebOffer.getString("OFFER_CODE");
                String ecSn = mebOffer.getString("EC_SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (IDataUtil.isNotEmpty(mebList)) {
                    IDataset productExtList = new DatasetList();

                    for (int j = 0; j < mebList.size(); j++) {
                        IData mebInfo = mebList.getData(j);
                        mebInfo.put("PRODUCT_ID", mebProductId);
                        mebInfo.put("OFFER_CODE", mebProductId);
                        mebInfo.put("OFFER_NAME", mebOffer.getString("OFFER_NAME"));
                        mebInfo.put("OFFER_ID", mebOffer.getString("OFFER_ID"));
                        mebInfo.put("CUST_ID", mebInfo.getString("CUST_ID", custId)); // 本应 存 成员客户id ；实际存的集团客户ID【custId】
                        mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                        mebInfo.put("CATALOG_ID", mebOffer.getString("CATALOG_ID", ""));
                        mebInfo.put("IS_EXPER_DATALINE", mebOffer.getString("IS_EXPER_DATALINE", ""));
                        mebInfo.put("RSRV_STR1", "MEB");

                        offerMebList.add(mebInfo);

                        IData productExt = new DataMap();
                        productExt.putAll(mebInfo);
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            // ESP系列产品 存入成员信息表
            if ("ESP".equals(dealType)) {
                // 无成员商品,取主产品信息
                String productId = ecOffer.getString("OFFER_CODE");
                String ecSn = ecOffer.getString("SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (IDataUtil.isNotEmpty(mebList)) {
                    // 操作类型
                    String operType = quickOrderOffer.getString("OPER_TYPE");
                    if (DataUtils.isEmpty(operType)) {
                        // 开通
                        for (int j = 0; j < mebList.size(); j++) {
                            IData mebInfo = mebList.getData(j);
                            mebInfo.put("PRODUCT_ID", productId);
                            mebInfo.put("CUST_ID", custId);
                            if ("380700".equals(productId)) {
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("DEV_MAC_NUMBER"));
                            } else if ("380300".equals(productId)) {
                                // 开通时，成员号码SERIAL_NUMBER需要转换成PHONE_NUMBER
                                mebInfo.put("PHONE_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("MAC_NUMBER"));
                            } else {
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                            }
                            mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                            mebInfo.put("RSRV_STR1", "MEB");
                            mebInfo.put("DEAL_TYPE", dealType);
                            offerMebList.add(mebInfo);
                        }
                    } else {
                        // 变更
                        IDataset productExtList = new DatasetList();

                        for (int j = 0; j < mebList.size(); j++) {
                            IData mebInfo = mebList.getData(j);
                            mebInfo.put("PRODUCT_ID", productId);
                            mebInfo.put("CUST_ID", custId);
                            if ("380700".equals(productId)) {
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("DEV_MAC_NUMBER"));
                            } else if ("380300".equals(productId)) {
                                // 开通时，成员号码SERIAL_NUMBER需要转换成PHONE_NUMBER
                                mebInfo.put("PHONE_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("MAC_NUMBER"));
                            } else {
                                mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                            }
                            mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                            mebInfo.put("RSRV_STR1", "MEB");
                            mebInfo.put("DEAL_TYPE", dealType);
                            offerMebList.add(mebInfo);

                            IData productExt = new DataMap();
                            productExt.put("OFFER_CODE", productId);
                            productExt.put("OFFER_ID", ecOffer.getString("OFFER_ID"));
                            productExt.put("OFFER_NAME", ecOffer.getString("OFFER_NAME"));
                            productExt.put("RSRV_STR5", "ESP");
                            productExt.putAll(mebInfo);
                            productExtList.add(productExt);
                        }

                        ecOfferData.put("PRODUCT_EXT", productExtList);
                    }
                }
            }

            SUBOFFERS.add(ecOfferData);

            quickOrderOffer.put("PRODUCT_ID", ecProductId);
            quickOrderOffer.put("CUST_ID", custId);
            quickOrderOffer.put("SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            quickOrderOffer.put("EC_SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            quickOrderOffer.put("RSRV_STR1", "OFFER");

            quickOrderOffer.remove("MEB_LIST");
            quickOrderOffer.remove("WIDENET_OPEN_DATA");

            offerDataList.add(quickOrderOffer);
        }

        // offerData.put("PRODUCT_EXT",productExtList);

        submitData.remove("OFFER_LIST");
        submitData.put("QUICKORDER_OFFER_MEB_LIST", offerMebList);
        submitData.put("QUICKORDER_OFFER_DATA_LIST", offerDataList);

        offerData.put("SUBOFFERS", SUBOFFERS);
        submitData.put("OFFER_DATA_LIST", offerData); // 拼凑 product && productExt 数据

    }

    /**
     * 审核通过后，数据提交到 con表 处理
     * */
    public static void transformAfterAcceptSubmitDataList(IData submitData) throws Exception {
        IDataset offerList = submitData.getDataset("OFFER_LIST");

        IData custInfo = submitData.getData("CUST_INFO");

        if (IDataUtil.isNotEmpty(offerList)) {
            IDataset commonData = new DatasetList();

            for (int i = 0; i < offerList.size(); i++) {
                IData offerListData = offerList.getData(i);

                String dealType = offerListData.getString("DEAL_TYPE", "");
                if (!"ESP".equals(dealType)) {
                    IData ecOffer = offerListData.getData("EC_OFFER");
                    IData ecCommonInfo = offerListData.getData("EC_COMMON_INFO");

                    String ecSerialNumber = offerListData.getString("EC_SERIAL_NUMBER");
                    String ecProductId = offerListData.getString("PRODUCT_ID");

                    IData ecContractInfo = ecCommonInfo.getData("CONTRACT_INFO");
                    ecContractInfo.put("SERIAL_NUMBER", ecSerialNumber);

                    IData inParams = new DataMap();
                    inParams.put("OFFERS", IDataUtil.idToIds(ecOffer));
                    inParams.put("COMMON_INFO", ecCommonInfo);
                    inParams.put("CUST_INFO", custInfo);
                    inParams.put("SUBSCRIBER", ecContractInfo);
                    inParams.put("ACCT_INFO", ecCommonInfo.getData("ACCT_INFO"));
                    inParams.put("OPER_TYPE", BizCtrlType.CreateUser);
                    inParams.put("SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
                    inParams.put("OFFER_CODE", ecProductId);
                    inParams.put("PRODUCT_ID", ecProductId);

                    IData ecOfferParam = new DataMap();
                    IData ecOfferData = getSvcNameByTransOffer(inParams, BizCtrlType.CreateUser);

                    ecOfferParam.put("CUST_ID", offerListData.getString("CUST_ID"));
                    ecOfferParam.put("PRODUCT_ID", offerListData.getString("PRODUCT_ID"));
                    ecOfferParam.put("SERIAL_NUMBER", ecSerialNumber);
                    ecOfferParam.put("RSRV_STR1", "EC");
                    ecOfferParam.put("CODING_STR", ecOfferData);
                    commonData.add(ecOfferParam);

                    IData mebOffer = offerListData.getData("MEB_OFFER");
                    IData mebCommonInfo = offerListData.getData("MEB_COMMON_INFO");

                    IDataset mebList = offerListData.getDataset("OFFER_MEMBER");

                    if (IDataUtil.isNotEmpty(mebList) || "7341".equals(ecProductId)) {
                        for (int j = 0; j < mebList.size(); j++) {
                            IData mebListData = mebList.getData(j);
                            String mebSerialNumber = mebListData.getString("SERIAL_NUMBER");

                            if (!"7341".equals(ecProductId)) {
                                IData mebInParam = new DataMap();
                                mebInParam.put("SERIAL_NUMBER", mebSerialNumber);
                                mebInParam.put("OPER_TYPE", BizCtrlType.CreateMember);
                                mebInParam.put("CUST_INFO", custInfo); // 集团客户信息
                                mebInParam.put("COMMON_INFO", mebCommonInfo);
                                mebInParam.put("OFFERS", IDataUtil.idToIds(mebOffer));
                                mebInParam.put("MEM_SUBSCRIBER", mebListData);

                                String memProductId = mebOffer.getString("OFFER_CODE");
                                String memProductName = mebOffer.getString("OFFER_NAME");
                                mebInParam.put("PRODUCT_ID", memProductId);
                                mebInParam.put("OFFER_CODE", memProductId);
                                mebInParam.put("PRODUCT_NAME", memProductName);

                                IData subscriber = new DataMap();
                                subscriber.put("SERIAL_NUMBER", ecSerialNumber);
                                subscriber.put("USER_ID", "#USER_ID#");
                                mebInParam.put("SUBSCRIBER", subscriber); // 集团用户信息

                                IData mebOfferData = getSvcNameByTransOffer(mebInParam, BizCtrlType.CreateMember);

                                IData mebOfferParam = new DataMap(); // 成员信息
                                mebOfferParam.put("CUST_ID", mebListData.getString("CUST_ID"));
                                mebOfferParam.put("SHORT_CODE", mebListData.getString("SHORT_CODE"));
                                mebOfferParam.put("PRODUCT_ID", memProductId);
                                mebOfferParam.put("SERIAL_NUMBER", mebSerialNumber);
                                mebOfferParam.put("RSRV_STR1", "MEB");
                                mebOfferParam.put("RSRV_STR5", ecSerialNumber);
                                mebOfferParam.put("CODING_STR", mebOfferData);
                                commonData.add(mebOfferParam);
                            } else {
                                IData mebOfferParam = new DataMap(); // 成员信息
                                mebOfferParam.put("CUST_ID", mebListData.getString("CUST_ID")); // 宽带 没有客户编码，存放集团的客户编码
                                mebOfferParam.put("PRODUCT_ID", mebListData.getString("PRODUCT_ID"));
                                mebOfferParam.put("SERIAL_NUMBER", mebListData.getString("SERIAL_NUMBER"));
                                mebOfferParam.put("RSRV_STR1", mebListData.getString("RSRV_STR1"));
                                mebOfferParam.put("RSRV_STR5", mebListData.getString("EC_SERIAL_NUMBER")); // 字段5 ： 集团用户编码
                                mebOfferParam.put("CODING_STR", mebListData);
                                commonData.add(mebOfferParam);
                            }
                        }
                    }
                }
            }

            submitData.put("QUICKORDER_OFFER_COND_LIST", commonData);

        }
    }

    /**
     * 转换 商品 数据串
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private static IData getSvcNameByTransOffer(IData param, String operType) throws Exception {
        IData input = new DataMap();
        input.put("IN_PARAMS", IDataUtil.idToIds(param));
        input.put("OPER_TYPE", operType);
        input.put("OFFER_CODE", param.getString("OFFER_CODE"));
        PageDataTrans pageTransData = PageDataTrans.getInstance(input);

        IData svcParam = pageTransData.transformData();
        svcParam.put("SVC", pageTransData.getSvcName());

        return svcParam;
    }

    public static String getSvcName(String productId, String brandCode, String operType) throws Exception {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("OFFER_CODE", productId);
        param.put("BRAND_CODE", brandCode);

        IData input = new DataMap();
        input.put("IN_PARAMS", IDataUtil.idToIds(param));
        input.put("OPER_TYPE", operType);
        input.put("OFFER_CODE", productId);

        if (BizCtrlType.DestoryMember.equals(operType)) {
            if ("BOSG".equals(brandCode)) {
                return "CS.DestroyBBossMemSVC.dealBBossMebBiz";
            } else {
                return EcConstants.EC_OPER_SERVICE.DELETE_ENTERPRISE_MEMBER.getValue();
            }
        } else if (BizCtrlType.CreateMember.equals(operType)) {
            if ("BOSG".equals(brandCode)) {
                return "CS.CreateBBossMemSVC.crtOrder";
            } else if ("10005742".equals(productId)) {
                return "SS.CreateAdcGroupMemberSVC.crtOrder";
            } else {
                return EcConstants.EC_OPER_SERVICE.CREATE_ENTERPRISE_MEMBER.getValue();
            }
        } else if (BizCtrlType.DestoryUser.equals(operType)) {
            if ("BOSG".equals(brandCode)) {
                return "CS.DestroyBBossUserSVC.dealDelBBossBiz";
            } else if ("6130".equals(productId)) {// 融合总机
                return "SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder";
            } else if ("6100".equals(productId)) {// 移动总机
                return "SS.DestroySuperTeleGroupUserSVC.crtOrder";
            } else {
                return EcConstants.EC_OPER_SERVICE.DELETE_ENTERPRISE_SUBSCRIBER.getValue();
            }
        }

        PageDataTrans pageTransData = PageDataTrans.getInstance(input);

        return pageTransData.getSvcName();
    }

    /**
     * 
     * @Title:transformAddMemberSubmitData
     * @Description:成员补录 数据转换
     * @param submitData
     *            页面提交数据
     */
    public static void transformAddMemberSubmitData(IData submitData) throws Exception {
        IDataset quickOrderOfferList = submitData.getDataset("OFFER_LIST");

        IDataset offerMebList = new DatasetList(); // QUICKORDER_OFFER_MEB_LIST

        // TF_B_EOP_product 信息
        IData offerData = submitData.getData("OFFER_DATA_LIST");

        IDataset suboffers = new DatasetList();

        for (int i = 0, len = quickOrderOfferList.size(); i < len; i++) {

            IData quickOrderOffer = quickOrderOfferList.getData(i);

            IData custInfo = submitData.getData("CUST_DATA");
            if (DataUtils.isEmpty(custInfo)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "客户信息不能为空！");
            }
            String custId = custInfo.getString("CUST_ID");
            // 产品存产品表
            IData ecOffer = quickOrderOffer.getData("EC_OFFER");
            IData mebOffer = quickOrderOffer.getData("MEB_OFFER");

            // 区分产品分类标记 分为ESPG和非ESPG
            String dealType = quickOrderOffer.getString("DEAL_TYPE", "");

            IData ecOfferData = new DataMap();
            ecOfferData.putAll(ecOffer);
            ecOfferData.put("DEAL_TYPE", dealType);

            // 商务宽带成员处理
            String ecProductId = ecOffer.getString("OFFER_CODE");
            if ("7341".equals(ecProductId))
            {
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");

                if (IDataUtil.isNotEmpty(mebList))
                {
                    IDataset productExtList = new DatasetList();
                    for (int j = 0, length = mebList.size(); j < length; j++)
                    {
                        IData addressData = mebList.getData(j);
                        offerMebList.add(addressData);

                        IData productExt = new DataMap();
                        productExt.put("SERIAL_NUMBER", addressData.getString("SERIAL_NUMBER"));
                        productExt.put("OFFER_CODE", addressData.getString("PRODUCT_ID"));
                        productExt.put("OFFER_NAME", addressData.getString("PRODUCT_NAME"));
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            if (DataUtils.isNotEmpty(mebOffer)) {
                String mebProductId = mebOffer.getString("OFFER_CODE");
                String ecSn = mebOffer.getString("EC_SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (DataUtils.isNotEmpty(mebList)) {
                    IDataset productExtList = new DatasetList();

                    for (int j = 0; j < mebList.size(); j++)
                    {
                        IData mebInfo = mebList.getData(j);
                        mebInfo.put("PRODUCT_ID", mebProductId);
                        mebInfo.put("CUST_ID", custId);
                        mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                        mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                        mebInfo.put("RSRV_STR1", "MEB");
                        mebInfo.put("DEAL_TYPE", dealType);
                        mebInfo.put("OFFER_CODE", mebProductId);
                        mebInfo.put("OFFER_NAME", mebOffer.getString("OFFER_NAME"));
                        mebInfo.put("OFFER_ID", mebOffer.getString("OFFER_ID"));
                        mebInfo.put("CATALOG_ID", mebOffer.getString("CATALOG_ID", ""));
                        mebInfo.put("IS_EXPER_DATALINE", mebOffer.getString("IS_EXPER_DATALINE", ""));
                        mebInfo.put("RSRV_STR1", "MEB");
                        offerMebList.add(mebInfo);

                        IData productExt = new DataMap();
                        productExt.putAll(mebInfo);
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            // ESP系列产品 存入成员信息表
            if ("ESP".equals(dealType)) {
                // 无成员商品,取主产品信息
                String productId = ecOffer.getString("OFFER_CODE");
                String ecSn = ecOffer.getString("SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (IDataUtil.isNotEmpty(mebList)) {
                    for (int j = 0; j < mebList.size(); j++) {
                        IData mebInfo = mebList.getData(j);
                        mebInfo.put("PRODUCT_ID", productId);
                        mebInfo.put("CUST_ID", custId);
                        if ("380700".equals(productId)) {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("DEV_MAC_NUMBER"));
                        } else if ("380300".equals(productId)) {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("MAC_NUMBER"));
                        } else {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                        }
                        mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                        mebInfo.put("RSRV_STR1", "MEB");
                        mebInfo.put("DEAL_TYPE", dealType);
                        offerMebList.add(mebInfo);
                    }
                }
            }
            suboffers.add(ecOfferData);
        }

        submitData.put("QUICKORDER_OFFER_MEB_LIST", offerMebList);
        // 拼凑 productExt 数据
        offerData.put("SUBOFFERS", suboffers);
        submitData.put("OFFER_DATA_LIST", offerData);
    }

    /**
     * 中小企业 ———— 变更审核 操作类型： 新增成员 数据转换
     * */
    public static void transformCrtMebByChangeAudit(IData submitData) throws Exception
    {
        IDataset offerList = submitData.getDataset("OFFER_LIST");

        IData custInfo = submitData.getData("CUST_INFO");

        if (IDataUtil.isEmpty(offerList))
        {
            return ;
        }

        IDataset commonData = new DatasetList();

        for (int i = 0; i < offerList.size(); i++)
        {
            IData offerListData = offerList.getData(i);

            IData ecOffer = offerListData.getData("EC_OFFER");

            if (IDataUtil.isNotEmpty(ecOffer))
            {
                String userId = ecOffer.getString("USER_ID");
                String ecSerialNumber = ecOffer.getString("SERIAL_NUMBER");
                String ecProductId = ecOffer.getString("PRODUCT_ID");

                String dealType = offerListData.getString("DEAL_TYPE", "");

                if (!"ESP".equals(dealType))
                {
                    IData mebOffer = offerListData.getData("MEB_OFFER");
                    IData mebCommonInfo = offerListData.getData("MEB_COMMON_INFO");

                    IDataset mebList = offerListData.getDataset("OFFER_MEMBER");

                    if (IDataUtil.isNotEmpty(mebList) || "7341".equals(ecProductId))
                    {
                        for (int j = 0; j < mebList.size(); j++)
                        {
                            IData mebData = mebList.getData(j);
                            String mebSerialNumber = mebData.getString("SERIAL_NUMBER");

                            if (!"7341".equals(ecProductId))
                            {
                                IData mebInParam = new DataMap();
                                mebInParam.put("SERIAL_NUMBER", mebSerialNumber);
                                mebInParam.put("OPER_TYPE", BizCtrlType.CreateMember);
                                mebInParam.put("CUST_INFO", custInfo); // 集团客户信息
                                mebInParam.put("COMMON_INFO", mebCommonInfo);
                                mebInParam.put("OFFERS", IDataUtil.idToIds(mebOffer));
                                mebInParam.put("MEM_SUBSCRIBER", mebData);

                                mebInParam.put("PRODUCT_ID", mebOffer.getString("OFFER_CODE"));
                                mebInParam.put("OFFER_CODE", mebOffer.getString("OFFER_CODE"));
                                mebInParam.put("PRODUCT_NAME", mebOffer.getString("OFFER_NAME"));

                                IData subscriber = new DataMap();
                                subscriber.put("SERIAL_NUMBER", ecSerialNumber);
                                subscriber.put("USER_ID", userId);
                                mebInParam.put("SUBSCRIBER", subscriber); // 集团用户信息

                                IData mebOfferData = getSvcNameByTransOffer(mebInParam, BizCtrlType.CreateMember);

                                IData mebCond = new DataMap(); // 成员信息
                                mebCond.put("CUST_ID", mebData.getString("CUST_ID"));
                                mebCond.put("SHORT_CODE", mebData.getString("SHORT_CODE"));
                                mebCond.put("PRODUCT_ID", mebOffer.getString("OFFER_CODE"));
                                mebCond.put("SERIAL_NUMBER", mebSerialNumber);
                                mebCond.put("RSRV_STR1", "MEB");
                                mebCond.put("RSRV_STR5", ecSerialNumber);
                                mebCond.put("CODING_STR", mebOfferData);
                                commonData.add(mebCond);
                            }
                            else
                            {
                                mebData.put("SVC", "SS.MergeWideUserCreateRegSVC.tradeReg");
                                IData mebCond = new DataMap(); // 成员信息
                                mebCond.put("CUST_ID", mebData.getString("CUST_ID")); // 宽带 没有客户编码，存放集团的客户编码
                                mebCond.put("PRODUCT_ID", mebData.getString("PRODUCT_ID"));
                                mebCond.put("SERIAL_NUMBER", mebData.getString("SERIAL_NUMBER"));
                                mebCond.put("RSRV_STR1", mebData.getString("RSRV_STR1"));
                                mebCond.put("RSRV_STR5", mebData.getString("EC_SERIAL_NUMBER")); // 字段5 ： 集团用户编码
                                mebCond.put("CODING_STR", mebData);
                                commonData.add(mebCond);
                            }
                        }
                    }
                }
            }
        }
        submitData.put("QUICKORDER_OFFER_COND_LIST", commonData);
    }

    /**
     * 中小企业 ———— 变更审核 操作类型： 商务宽带成员变更 数据转换
     * */
    public static void transformChgWnByChangeAudit(IData submitData) throws Exception {
        IDataset offerList = submitData.getDataset("OFFER_LIST");

        if (IDataUtil.isEmpty(offerList))
        {
            return;
        }

        IDataset commonData = new DatasetList();

        for (int i = 0; i < offerList.size(); i++)
        {
            IData offerListData = offerList.getData(i);

            IData ecOffer = offerListData.getData("EC_OFFER");

            if (IDataUtil.isEmpty(ecOffer))
            {
                continue;
            }

            String ecProductId = ecOffer.getString("PRODUCT_ID");

            IDataset mebList = offerListData.getDataset("OFFER_MEMBER");

            if (IDataUtil.isNotEmpty(mebList) && "7341".equals(ecProductId)) {
                for (int j = 0; j < mebList.size(); j++) {
                    IData mebData = mebList.getData(j);
                    mebData.put("SVC", "SS.WidenetChangeProductNewRegSVC.tradeReg"); // 宽带变更SS.WidenetChangeProductNewRegSVC.tradeReg

                    mebData.put("BOOKDATE", SysDateMgr4Web.getFirstDayOfNextMonth()); // 取下个月一号
                    mebData.put("BOOKING_DATE", SysDateMgr4Web.getFirstDayOfNextMonth()); // 取下个月一号

                    IData mebOfferParam = new DataMap(); // 成员信息
                    mebOfferParam.put("CUST_ID", mebData.getString("CUST_ID")); // 宽带 没有客户编码，存放集团的客户编码
                    mebOfferParam.put("PRODUCT_ID", mebData.getString("PRODUCT_ID"));
                    mebOfferParam.put("SERIAL_NUMBER", mebData.getString("SERIAL_NUMBER"));
                    mebOfferParam.put("RSRV_STR1", mebData.getString("RSRV_STR1"));
                    mebOfferParam.put("RSRV_STR5", mebData.getString("EC_SERIAL_NUMBER")); // 字段5 ： 集团用户编码
                    mebOfferParam.put("CODING_STR", mebData);
                    commonData.add(mebOfferParam);
                }
            }
        }
        submitData.put("QUICKORDER_OFFER_COND_LIST", commonData);

    }

    /**
     * 中小企业 ———— 变更审核 操作类型： 删除成员 数据转换
     * */
    public static void transformDelMebByChangeAudit(IData submitData) throws Exception
    {
        IDataset offerList = submitData.getDataset("OFFER_LIST");

        if (IDataUtil.isEmpty(offerList))
        {
            return ;
        }

        IDataset commonData = new DatasetList();

        for (int i = 0; i < offerList.size(); i++)
        {
            IData offerListData = offerList.getData(i);

            String dealType = offerListData.getString("DEAL_TYPE", "");
            String ecProductId = offerListData.getString("PRODUCT_ID", "");

            IDataset offerMemberList = offerListData.getDataset("OFFER_MEMBER");

            if (!"ESP".equals(dealType) && IDataUtil.isNotEmpty(offerMemberList))
            {
                for (int j = 0; j < offerMemberList.size(); j++) {

                    IData offerMember = offerMemberList.getData(j);

                    String brandCode = offerMember.getString("BRAND_CODE");

                    String svc = "";
                    IData mebCond = new DataMap();
                    if (!"7341".equals(ecProductId))
                    {
                        IData codingStr = new DataMap();
                        codingStr.put("SERIAL_NUMBER", offerMember.getString("SERIAL_NUMBER"));
                        codingStr.put("USER_ID", offerMember.getString("USER_ID"));

                        svc = getSvcName(offerMember.getString("PRODUCT_ID"), brandCode, BizCtrlType.DestoryMember);
                        codingStr.put("SVC", svc);
                        mebCond.put("CODING_STR", codingStr);
                        mebCond.put("RSRV_STR1", "MEB");
                    }
                    else
                    {
                        svc = "SS.WidenetDestroyNewRegSVC.tradeReg"; // 成员宽带注销
                        offerMember.put("SVC", svc);
                        mebCond.put("CODING_STR", offerMember);
                        mebCond.put("RSRV_STR1", "WIDENET");
                    }

                    mebCond.put("CUST_ID", offerMember.getString("CUST_ID"));
                    mebCond.put("PRODUCT_ID", offerMember.getString("PRODUCT_ID"));
                    mebCond.put("SERIAL_NUMBER", offerMember.getString("SERIAL_NUMBER"));
                    mebCond.put("RSRV_STR5", offerMember.getString("EC_SERIAL_NUMBER"));

                    commonData.add(mebCond);
                }
            }
        }
        submitData.put("QUICKORDER_OFFER_COND_LIST", commonData);

    }

    /**
     * 中小企业 ———— 变更审核 操作类型： 删除集团 数据转换
     * */
    public static void transformDstSubByChangeAudit(IData submitData) throws Exception
    {
        IDataset offerList = submitData.getDataset("OFFER_LIST");

        if (IDataUtil.isEmpty(offerList))
        {
            return;
        }

        IDataset commonData = new DatasetList();

        for (int i = 0; i < offerList.size(); i++) {
            IData offerListData = offerList.getData(i);

            String dealType = offerListData.getString("DEAL_TYPE", "");
            if (!"ESP".equals(dealType))
            {
                IData ecOffer = offerListData.getData("EC_OFFER");

                if (IDataUtil.isEmpty(ecOffer))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取集团产品信息失败！");
                }

                String ecSerialNumber = offerListData.getString("EC_SERIAL_NUMBER");
                String brandCode = offerListData.getString("BRAND_CODE");

                IData ecOfferData = new DataMap();
                ecOfferData.put("ROUTE_EPARCHY_CODE", "0898");
                ecOfferData.put("USER_EPARCHY_CODE", "0898");
                ecOfferData.put("REASON_CODE", "1");
                ecOfferData.put("USER_ID", ecOffer.getString("USER_ID"));
                ecOfferData.put("PRODUCT_ID", ecOffer.getString("PRODUCT_ID"));

                String svc = getSvcName(offerListData.getString("PRODUCT_ID"), brandCode, BizCtrlType.DestoryUser);
                ecOfferData.put("SVC", svc);

                IData ecOfferParam = new DataMap();
                ecOfferParam.put("CUST_ID", offerListData.getString("CUST_ID"));
                ecOfferParam.put("PRODUCT_ID", offerListData.getString("PRODUCT_ID"));
                ecOfferParam.put("SERIAL_NUMBER", ecSerialNumber);
                ecOfferParam.put("RSRV_STR1", "EC");
                ecOfferParam.put("CODING_STR", ecOfferData);
                commonData.add(ecOfferParam);
            }
        }
        submitData.put("QUICKORDER_OFFER_COND_LIST", commonData);
    }

    /**
     ** 变更业务 删除成员补录 拼接数据
     * 
     * @param submitData
     * @throws Exception
     * @Date 2019年11月1日
     * @author xieqj
     */
    public static void transformDelMemberSubmitData(IData submitData) throws Exception {
        IDataset quickOrderOfferList = submitData.getDataset("OFFER_LIST");

        IDataset offerMebList = new DatasetList(); // QUICKORDER_OFFER_MEB_LIST

        IData offerData = submitData.getData("OFFER_DATA_LIST");

        IDataset suboffers = new DatasetList();

        for (int i = 0, len = quickOrderOfferList.size(); i < len; i++)
        {

            IData quickOrderOffer = quickOrderOfferList.getData(i);

            IData custInfo = submitData.getData("CUST_DATA");
            if (DataUtils.isEmpty(custInfo))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "客户信息不能为空！");
            }
            String custId = custInfo.getString("CUST_ID");
            // 产品存产品表
            IData ecOffer = quickOrderOffer.getData("EC_OFFER");
            IData mebOffer = quickOrderOffer.getData("MEB_OFFER");

            // 区分产品分类标记 分为ESPG和非ESPG
            String dealType = quickOrderOffer.getString("DEAL_TYPE", "");

            IData ecOfferData = new DataMap();
            ecOfferData.putAll(ecOffer);
            ecOfferData.put("DEAL_TYPE", dealType);
            ecOfferData.put("OFFER_TYPE", "P");

            // 商务宽带成员处理
            String ecProductId = ecOffer.getString("OFFER_CODE");
            if ("7341".equals(ecProductId)) {
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");

                if (IDataUtil.isNotEmpty(mebList)) {
                    IDataset productExtList = new DatasetList();
                    for (int j = 0, length = mebList.size(); j < length; j++) {
                        IData addressData = mebList.getData(j);
                        offerMebList.add(addressData);

                        IData productExt = new DataMap();
                        productExt.put("SERIAL_NUMBER", addressData.getString("SERIAL_NUMBER"));
                        productExt.put("OFFER_CODE", addressData.getString("PRODUCT_ID"));
                        productExt.put("OFFER_NAME", addressData.getString("PRODUCT_NAME"));
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            if (DataUtils.isNotEmpty(mebOffer) && !"ESP".equals(dealType)) {
                String mebProductId = mebOffer.getString("OFFER_CODE");
                String ecSn = mebOffer.getString("EC_SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (DataUtils.isNotEmpty(mebList)) {
                    IDataset productExtList = new DatasetList();

                    for (int j = 0; j < mebList.size(); j++) {
                        IData mebInfo = mebList.getData(j);
                        mebInfo.put("PRODUCT_ID", mebProductId);
                        mebInfo.put("CUST_ID", custId);
                        mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                        mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                        mebInfo.put("RSRV_STR1", "MEB");
                        mebInfo.put("DEAL_TYPE", dealType);
                        mebInfo.put("OFFER_CODE", mebProductId);
                        mebInfo.put("OFFER_NAME", mebOffer.getString("OFFER_NAME"));
                        mebInfo.put("OFFER_ID", mebOffer.getString("OFFER_ID"));
                        mebInfo.put("CATALOG_ID", mebOffer.getString("CATALOG_ID", ""));
                        mebInfo.put("IS_EXPER_DATALINE", mebOffer.getString("IS_EXPER_DATALINE", ""));
                        mebInfo.put("RSRV_STR1", "MEB");
                        offerMebList.add(mebInfo);

                        IData productExt = new DataMap();
                        productExt.putAll(mebInfo);
                        productExtList.add(productExt);
                    }
                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }

            // ESP系列产品 存入成员信息表
            if ("ESP".equals(dealType)) {
                // 无成员商品,取主产品信息
                String productId = ecOffer.getString("OFFER_CODE");
                String ecSn = ecOffer.getString("SERIAL_NUMBER");

                // 成员，存入成员信息表
                IDataset mebList = quickOrderOffer.getDataset("MEB_LIST");
                if (IDataUtil.isNotEmpty(mebList)) {

                    IDataset productExtList = new DatasetList();

                    for (int j = 0; j < mebList.size(); j++) {
                        IData mebInfo = mebList.getData(j);
                        mebInfo.put("PRODUCT_ID", productId);
                        mebInfo.put("CUST_ID", custId);
                        if ("380700".equals(productId)) {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("DEV_MAC_NUMBER"));
                        } else if ("380300".equals(productId)) {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("MAC_NUMBER"));
                        } else {
                            mebInfo.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
                        }
                        mebInfo.put("EC_SERIAL_NUMBER", ecSn);
                        mebInfo.put("RSRV_STR1", "MEB");
                        mebInfo.put("DEAL_TYPE", dealType);
                        offerMebList.add(mebInfo);

                        IData productExt = new DataMap();
                        productExt.put("OFFER_CODE", productId);
                        productExt.put("OFFER_ID", ecOffer.getString("OFFER_ID"));
                        productExt.put("OFFER_NAME", ecOffer.getString("OFFER_NAME"));
                        productExt.put("RSRV_STR5", "ESP");
                        productExt.putAll(mebInfo);
                        productExtList.add(productExt);
                    }

                    ecOfferData.put("PRODUCT_EXT", productExtList);
                }
            }
            suboffers.add(ecOfferData);
        }

        submitData.put("QUICKORDER_OFFER_MEB_LIST", offerMebList);
        // 拼凑 productExt 数据
        offerData.put("SUBOFFERS", suboffers);
        submitData.put("OFFER_DATA_LIST", offerData);
    }

    /**
     * 中小企业 ———— 变更申请 注销集团 数据提交转换
     *
     * */
    public static void transformDstSubByChangeApply(IData submitData) throws Exception
    {
        IDataset quickOrderOfferList = submitData.getDataset("OFFER_LIST");
        if (IDataUtil.isEmpty(quickOrderOfferList))
        {
            return;
        }

        IData custInfo = submitData.getData("CUST_DATA");
        if (IDataUtil.isEmpty(custInfo))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "客户信息不能为空！");
        }

        String custId = custInfo.getString("CUST_ID");

        IDataset offerDataList = new DatasetList();

        IData offerData = submitData.getData("OFFER_DATA_LIST");

        IDataset suboffers = new DatasetList();

        for (int i = 0; i < quickOrderOfferList.size(); i++)
        {
            IData quickOrderOfferData = quickOrderOfferList.getData(i);

            IData ecOffer = quickOrderOfferData.getData("EC_OFFER");

            String dealType = quickOrderOfferData.getString("DEAL_TYPE", "");

            // 拼凑data表数据
            quickOrderOfferData.put("PRODUCT_ID", ecOffer.getString("PRODUCT_ID"));
            quickOrderOfferData.put("CUST_ID", custId);
            quickOrderOfferData.put("SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            quickOrderOfferData.put("EC_SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            quickOrderOfferData.put("RSRV_STR1", "OFFER");
            quickOrderOfferData.put("DEAL_TYPE", dealType);

            offerDataList.add(quickOrderOfferData);

            // 拼凑product表数据
            IData ecOfferData = new DataMap();
            ecOfferData.putAll(ecOffer);
            ecOfferData.put("DEAL_TYPE", dealType);
            ecOfferData.put("OFFER_TYPE", "P");

            suboffers.add(ecOfferData);
        }
        submitData.remove("OFFER_LIST");
        submitData.put("QUICKORDER_OFFER_DATA_LIST", offerDataList);

        offerData.put("SUBOFFERS", suboffers);
        submitData.put("OFFER_DATA_LIST", offerData); // 拼凑 product && productExt 数据
    }

    /**
     * 中小企业 ———— 复杂变更
     *
     * */
    public static void transformCpcSubByChangeApply(IData submitData) throws Exception {
        IDataset quickOrderOfferList = submitData.getDataset("OFFER_LIST");
        if (IDataUtil.isEmpty(quickOrderOfferList)) {
            return;
        }

        IData custInfo = submitData.getData("CUST_DATA");
        if (IDataUtil.isEmpty(custInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "客户信息不能为空！");
        }

        String custId = custInfo.getString("CUST_ID");

        IDataset offerDataList = new DatasetList();

        IData offerData = submitData.getData("OFFER_DATA_LIST");

        for (int i = 0; i < quickOrderOfferList.size(); i++) {
            IData ecOffer = quickOrderOfferList.getData(i);

            ecOffer.put("PRODUCT_ID", ecOffer.getString("PRODUCT_ID"));
            ecOffer.put("CUST_ID", custId);
            ecOffer.put("SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            ecOffer.put("EC_SERIAL_NUMBER", ecOffer.getString("SERIAL_NUMBER"));
            ecOffer.put("RSRV_STR1", "OFFER");
            offerDataList.add(ecOffer);
        }
        submitData.remove("OFFER_LIST");
        submitData.put("QUICKORDER_OFFER_DATA_LIST", offerDataList);
        submitData.put("OFFER_DATA_LIST", offerData); // 拼凑 product && productExt 数据
    }
}
