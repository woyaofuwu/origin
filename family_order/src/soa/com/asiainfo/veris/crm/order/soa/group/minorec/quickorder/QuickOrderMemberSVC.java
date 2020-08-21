package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class QuickOrderMemberSVC extends GroupOrderService {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public static IDataset getQuickOrderMemberData(IData param) throws Exception {
        return QuickOrderMemberListBean.getQuickOrderMember(param);
    }

    /**
     * 审核查询 taosx
     */
    public static IDataset queryAuditQuickMemberData(IData param) throws Exception {

        IDataset offerList = new DatasetList();
        IDataset auditOfferData = QuickOrderDataListBean.queryAuditQuickData(param);// 先查订购了多少个产品
        if (IDataUtil.isNotEmpty(auditOfferData)) {
            for (Object object : auditOfferData) {
                IData offerUrs = new DataMap();

                IData auditOfferInfo = (IData) object;

                StringBuilder offerInitStr = new StringBuilder();// 转换产品产品参数

                buildOfferDataStr(auditOfferInfo, offerInitStr);

                IData offerInitData = new DataMap(offerInitStr.toString());

                String ecSerialNumber = offerInitData.getString("EC_SERIAL_NUMBER");

                IData data = new DataMap();
                data.put("EC_SERIAL_NUMBER", ecSerialNumber);
                data.put("IBSYSID", auditOfferInfo.getString("IBSYSID"));
                IDataset auditOfferMember = QuickOrderMemberListBean.queryAuditQuickMember(data);// 查成员信息，只要展示成员手机号码

                IDataset auditOfferMemberList = new DatasetList();

                if (IDataUtil.isNotEmpty(auditOfferMember)) {
                    for (Object object2 : auditOfferMember) {
                        IData auditOfferMemberInfo = (IData) object2;

                        StringBuilder offerInitStrMember = new StringBuilder();// 转换CODING_STR参数

                        buildOfferMemberStr(auditOfferMemberInfo, offerInitStrMember);

                        IData offerInitMemberInfo = new DataMap(offerInitStrMember.toString());
                        transferPrams(offerInitMemberInfo);
                        auditOfferMemberList.add(offerInitMemberInfo);
                    }
                }
                offerUrs.put("OFFER_MEMBER", auditOfferMemberList); // 成员用户信息
                offerUrs.putAll(offerInitData); // 集团用户，集团产品，成员产品信息
                offerList.add(offerUrs);
            }
        }

        return offerList;
    }

    /**
     ** 成员号码参数特殊转换处理
     * @param offerInitMemberInfo
     * @Date 2019年12月04日
     * @author xieqj 
     */
	private static void transferPrams(IData offerInitMemberInfo) {
		if ("380300".equals(offerInitMemberInfo.getString("PRODUCT_ID"))) {
			offerInitMemberInfo.put("SERIAL_NUMBER", offerInitMemberInfo.getString("PHONE_NUMBER", ""));
		}
	}
	/**
     * 拼接data数据 taosx
     */
    private static void buildOfferDataStr(IData auditOfferInfo, StringBuilder offerInitStr) {
        for (int i = 1; i <= 10; i++) {
            if (StringUtils.isNotBlank(auditOfferInfo.getString("CODING_DATA" + i))) {
                offerInitStr.append(auditOfferInfo.getString("CODING_DATA" + i));
            }
        }
    }

    /**
     * 拼接Member数据 taosx
     */
    private static void buildOfferMemberStr(IData auditOfferInfo, StringBuilder offerInitStr) {
        for (int i = 1; i <= 20; i++) {
            if (StringUtils.isNotBlank(auditOfferInfo.getString("CODING_STR" + i))) {
                offerInitStr.append(auditOfferInfo.getString("CODING_STR" + i));
            }
        }
    }

    /**
     * 查询attr与Other表 taosx
     */
    public static IDataset queryEopAttrOtherData(IData param) throws Exception {
        IDataset attrOtherList = new DatasetList();
        IData eopAttrOherInfo = new DataMap();
        IDataset eopAttrList = QuickOrderDataListBean.queryEopAttrData(param);// 先查主题等信息
        IDataset eopOtherList = QuickOrderDataListBean.queryEopOtherData(param);// 先查稽核等信息
        eopAttrOherInfo.put("ATTR_LIST", eopAttrList);
        eopAttrOherInfo.put("OTHER_LIST", eopOtherList);
        attrOtherList.add(eopAttrOherInfo);
        return attrOtherList;
    }

    public static IDataset getMemberInfoListBySysId(IData param) throws Exception {
        return QuickOrderMemberListBean.getMemberInfoListBySysId(param);
    }

    /**
     * @Title:getNewsMemberInfoListBySysId
     * @Description:根据IbSysId 获取最新的成员数据列表
     * @param @param param
     * @return IDataset
     * @throws
     */
    public static IDataset getNewsMemberInfoListBySysId(IData param) throws Exception {
        return QuickOrderMemberListBean.getNewsMemberInfoListBySysId(param);
    }

    /**
     * 根据IBSYSID和PRODUCT_ID 查询最新的esp成员信息
     * 
     * @param @param param
     * @return IDataset
     * @throws
     */
    public static IDataset getEspNewsMemberInfoList(IData param) throws Exception {
        return QuickOrderMemberListBean.getEspNewsMemberInfoList(param);
    }

    /**
     * 变更查询已订购的产品电子协议 taosx
     */
    public static IDataset queryArchivesInfo(IData param) throws Exception {
        IDataset archivesData = new DatasetList();
        IDataset archivesList = QuickOrderMemberListBean.queryArchivesInfo(param);// 查询集团订购的所有产品的电子协议
        String flag = param.getString("FLAG");// 集团V网选择标记
        IData archivesMap = new DataMap();
        String xnProductId = "";
        if (IDataUtil.isNotEmpty(archivesList)) {
            for (int i = 0; i < archivesList.size(); i++) {
                IData archivesInfo = new DataMap();// 把一个电子协议勾选的产品，存到一个MAP
                IData dataInfo = archivesList.getData(i);
                String contracIdI = dataInfo.getString("CONTRACT_ID");
                String contracIdA = archivesMap.getString("CONTRACT_ID", "");
                if (contracIdI.equals(contracIdA)) {// 相同合同编码只循环一次
                    continue;
                }
                String productId = dataInfo.getString("PRODUCT_ID");// 虚拟的产品ID
                if ("1".equals(flag)) {// 集团V网不做这个过滤
                    xnProductId = productId;
                    if (!productId.equals(param.getString("XN_PRODUCTID"))) {// 过滤前台传过来的流程，只查需要变更的流程合同
                        continue;
                    }
                }

                String ecProductIds = dataInfo.getString("EC_PRODUCT_ID");// 获取产品ID
                IDataset offerList = UpcCallIntf.queryOfferIdByOfferCodeAndOfferType("P", ecProductIds);// 查询产品信息
                StringBuilder offrerName = new StringBuilder();
                if (IDataUtil.isNotEmpty(offerList)) {
                    offrerName = new StringBuilder().append(offerList.getData(0).getString("OFFER_NAME"));
                }
                StringBuilder ecProductId = new StringBuilder().append(dataInfo.getString("EC_PRODUCT_ID"));// 获取产品ID
                if ("380700".equals(ecProductId.toString()) || "380300".equals(ecProductId.toString()) || "921015".equals(ecProductId.toString())) {
                    continue;
                }
                StringBuilder serialNumber = new StringBuilder().append(dataInfo.getString("SERIAL_NUMBER"));
                StringBuilder archivesName = new StringBuilder().append(dataInfo.getString("ARCHIVES_NAME"));
                StringBuilder userId = new StringBuilder().append(dataInfo.getString("USER_ID"));
                String productName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "MINOREC_XN_PRODUCT", productId });
                for (int s = 0; s < archivesList.size(); s++) {// 第二次循环，把相同的合同信息，存到一个MAP
                    IData dataInfoS = archivesList.getData(s);
                    String contracIdS = dataInfoS.getString("CONTRACT_ID");
                    if (i != s && contracIdI.equals(contracIdS)) {
                        String ecProductIdS = dataInfoS.getString("EC_PRODUCT_ID");
                        String serialNumberS = dataInfoS.getString("SERIAL_NUMBER");
                        String userIdS = dataInfoS.getString("USER_ID");
                        IDataset offerListS = UpcCallIntf.queryOfferIdByOfferCodeAndOfferType("P", ecProductIdS);
                        String offrerNameS = "";
                        if (IDataUtil.isNotEmpty(offerListS)) {
                            offrerNameS = offerListS.getData(0).getString("OFFER_NAME");
                        }
                        serialNumber = serialNumber.append(",").append(serialNumberS);
                        ecProductId = ecProductId.append(",").append(ecProductIdS);
                        offrerName = offrerName.append(",").append(offrerNameS);
                        userId = userId.append(",").append(userIdS);

                    }
                }

                if ("1".equals(flag) && "8000".equals(ecProductId.toString())) {// 商铺，过滤集团V网的合同信息
                    continue;
                } else if ("2".equals(flag) && !"8000".equals(ecProductId.toString())) {// 商铺，只加载集团V网的合同信息
                    continue;
                }

                archivesInfo.put("CONTRACT_ID", contracIdI);
                archivesInfo.put("EC_PRODUCT_ID", ecProductId.toString());
                archivesInfo.put("SERIAL_NUMBER", serialNumber.toString());
                archivesInfo.put("ARCHIVES_NAME", archivesName);
                archivesInfo.put("PRODUCT_ID", productId);
                archivesInfo.put("PRODUCT_NAME", productName);
                archivesInfo.put("OFFER_NAME", offrerName.toString());
                archivesInfo.put("USER_ID", userId.toString());
                archivesMap = archivesInfo;// 在循环外面加一个map,相同的合同编码只存一次
                archivesData.add(archivesInfo);
            }

        }

        if (IDataUtil.isEmpty(archivesData) && StringUtils.isNotBlank(xnProductId)) {
            String productName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "MINOREC_XN_PRODUCT", xnProductId });
            CSAppException.apperr(GrpException.CRM_GRP_713, "您已订购的流程电子协议是【" + productName + "】流程，当前流程没有可用的电子协议！");

        }
        return archivesData;

    }

    /**
     * 
     * @Title: getQuickOrderMemberData
     * @Description: 客户经理确认节点查询成员信息
     * @param param
     * @return
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年10月22日下午3:52:50
     */
    public static IDataset qryMemInfoForCustManager(IData param) throws Exception {
        return QuickOrderMemberListBean.qryMemInfoForCustManager(param);
    }

    /**
     * 
     * @Title: updateMemSerialNumInfo
     * @Description: ESP客户经理提交更新EC_SERIAL_NUMBER TF_B_EOP_QUICKORDER_MEB
     * @param param
     * @return
     * @throws Exception
     *             int
     * @author zhangzg
     * @date 2019年10月31日下午8:38:35
     */
    public static int updateMemSerialNumInfo(IData param) throws Exception {
        int result  = QuickOrderMemberListBean.updateMemSerialNumInfo(param);
        result += QuickOrderMemberListBean.updateDataSerialNumInfo(param);
        return result;
    }

    /**
     * 
     * @Title: updateMemStateInfoForDel
     * @Description: ESP产品变更删除成员时更新成员状态
     * @param param
     * @return
     * @throws Exception
     *             int
     * @author zhangzg
     * @date 2019年11月5日上午11:30:30
     */
    public static int updateMemStateInfoForDel(IData param) throws Exception {
        return QuickOrderMemberListBean.updateMemStateInfoForDel(param);
    }

    /**
     * 
     * @Title: updateMemStateInfoForAdd
     * @Description: ESP产品变更新增成员更新历史成员状态信息
     * @param param
     * @return
     * @throws Exception
     *             int
     * @author zhangzg
     * @date 2019年11月5日下午2:57:23
     */
    public static int updateMemStateInfoForAdd(IData param) throws Exception {
        return QuickOrderMemberListBean.updateMemStateInfoForAdd(param);
    }

    /**
     * 
     * @Title: qryExistsRelationbbMebInfos
     * @Description: 查询ESP新增成员 是否已存在TF_F_RELATION_BB
     * @param PRODUCT_ID
     * @return SERIAL_NUMBER_B
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年11月6日上午10:51:36
     */
    public static IDataset qryExistsRelationbbMebInfos(IData param) throws Exception {
		IDataset checkMebList = param.getDataset("MEMBERLIST");
		IDataset resultDataset = new DatasetList();
		if (IDataUtil.isNotEmpty(checkMebList)) {
			for (int i = 0, size = checkMebList.size(); i < size; i++) {
				IData mebInfo = checkMebList.getData(i);
				String productId = param.getString("PRODUCT_ID");
				IData result = new DataMap();
				String macNumber = "";
				if("380700".equals(productId)) {
				    macNumber = mebInfo.getString("DEV_MAC_NUMBER");
				}else if("380300".equals(productId)) {
				    macNumber = mebInfo.getString("MAC_NUMBER");
				}
				result.put("SERIAL_NUMBER_B", macNumber);
                result.put("PRODUCT_ID", productId);
                //查询TF_F_RELATION_BB表
				IDataset BBmebInfos = QuickOrderMemberListBean.qryExistsRelationbbMebInfos(result);
				//查询 TF_B_EOP_QUICKORDER_MEB
				IDataset orderMebInfos = QuickOrderMemberListBean.qryAllMebInfosByProductId(result);
				if (IDataUtil.isNotEmpty(BBmebInfos)) {
					resultDataset.addAll(BBmebInfos);
				}
				if (IDataUtil.isNotEmpty(orderMebInfos)) {
				    resultDataset.addAll(orderMebInfos);
				}
			}
		} else {
			resultDataset = QuickOrderMemberListBean.qryExistsRelationbbMebInfos(param);
		}
		return resultDataset;
    }

    /**
     * 
     * @Title: queryAuditInfoData
     * @Description: 稽核产品详细信息展示
     * @param param
     * @return
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年11月12日下午4:28:47
     */
    public static IDataset queryAuditInfoData(IData param) throws Exception {
        // 查询订购产品信息
        IDataset auditOfferData = QuickOrderDataListBean.qryAllQuickOrderInfoByIbsysidAndProductId(param);
        IDataset offerList = new DatasetList();
        if (IDataUtil.isNotEmpty(auditOfferData)) {
            IData offerUrs = new DataMap();
            IData auditOfferInfo = (IData) auditOfferData.getData(0);
            StringBuilder offerInitStr = new StringBuilder();
            // 转换产品产品参数
            buildOfferMemberStr(auditOfferInfo, offerInitStr);
            IData offerInitData = new DataMap(offerInitStr.toString());
            String ecSerialNumber = offerInitData.getString("EC_SERIAL_NUMBER");
            IData data = new DataMap();
            data.put("EC_SERIAL_NUMBER", ecSerialNumber);
            data.put("IBSYSID", auditOfferInfo.getString("IBSYSID"));
            // 查成员信息 只展示成员手机号码
            IDataset auditOfferMember = QuickOrderMemberListBean.qryAllQuickMemberInfos(data);
            IDataset auditOfferMemberList = new DatasetList();
            if (IDataUtil.isNotEmpty(auditOfferMember)) {
                for (Object object2 : auditOfferMember) {
                    IData auditOfferMemberInfo = (IData) object2;
                    StringBuilder offerInitStrMember = new StringBuilder();
                    // 转换CODING_STR参数
                    buildOfferMemberStr(auditOfferMemberInfo, offerInitStrMember);
                    IData offerInitMemberInfo = new DataMap(offerInitStrMember.toString());
                    auditOfferMemberList.add(offerInitMemberInfo);
                }
            }
            // 成员用户信息
            offerUrs.put("OFFER_MEMBER", auditOfferMemberList);
            // 集团用户，集团产品，成员产品信息
            offerUrs.putAll(offerInitData);
            offerList.add(offerUrs);
        }
        return offerList;
    }

    /**
     * 
     * @Title: qryaLLMebInfosByProductId
     * @Description: 成员导入重复校验
     * @param PRODUCT_ID
     * @return
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年11月14日下午8:49:23
     */
    public static IDataset qryAllMebInfosByProductId(IData param) throws Exception {
        return QuickOrderMemberListBean.qryAllMebInfosByProductId(param);
    }
}
