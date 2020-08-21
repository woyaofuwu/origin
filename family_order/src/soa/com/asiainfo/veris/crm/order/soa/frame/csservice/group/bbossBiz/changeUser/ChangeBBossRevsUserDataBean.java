
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepOfferfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeRecepHallUser.ChangeJKDTUserSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class ChangeBBossRevsUserDataBean extends GroupBean
{

	 private static final Logger logger = LoggerFactory.getLogger(ChangeJKDTUserSVC.class);
    // 定义中断类型(程序处理时遇到某些情况会自动中断后面的进程，例如在集团产品变更过程中，产品信息为空，则不需要继续进行受理)
    static String breakType = "0";// 0表示变更过程正常进行，否则中断受理过程

    /*
     * @description 校验是否存在有程序中断情况，如果有重新定义返回数据
     * @author xunyl
     * @date 2013-07-01
     */
    protected static void checkOperResult(IData map, IData returnVal) throws Exception
    {
        // 1- 检查中断类型
        if ("0".equals(breakType))
        {
            return;
        }

        // 2- 清空返回数据
        returnVal.clear();

        // 3- 定义应答报文
        IData rep_data = new DataMap();
        if ("1".equals(breakType))
        {// 集团受理过程中没有产品信息
            rep_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            rep_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            rep_data.put("RSPCODE".toUpperCase(), "00");
            rep_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
            rep_data.put("TRADE_ID", "-1");
            rep_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            rep_data.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));
            rep_data.put("SUBSCRIBE_ID", map.getString("SUBSCRIBE_ID"));
        }

        // 4- 重新定义返回数据
        returnVal.put("BREAK_TYPE", breakType);
        returnVal.put("REP_DATA", IDataUtil.idToIds(rep_data));
    }

    /*
     * @description 拼装新增的商品资费
     * @author xunyl
     * @date 2013-07-23
     */
    protected static IData getAddMerchDiscntInfo(String proPoNumber, String elementId, String packageId, String elementName) throws Exception
    {
        // 1- 定义新增商品资费对象
        IData elementInfo = new DataMap();

        // 2- 添加资费实例ID
        elementInfo.put("INST_ID", "");

        // 3- 添加元素类型，资费类型对应为"D"
        elementInfo.put("ELEMENT_TYPE_CODE", "D");

        // 4- 添加资费状态,新增默认为0
        elementInfo.put("MODIFY_TAG", "0");

        // 5- 添加产品产品编号
        elementInfo.put("PRODUCT_ID", proPoNumber);

        // 6- 添加元素ID
        elementInfo.put("ELEMENT_ID", elementId);

        // 7- 添加包信息
        elementInfo.put("PACKAGE_ID", packageId);

        // 8- 添加元素名称
        elementInfo.put("ELEMENT_NAME", elementName);

        // 9- 添加开始时间
        elementInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数
        elementInfo.put("ATTR_PARAM", "");

        // 12- 返回商品资费对象
        return elementInfo;
    }

    /*
     * @description 添加商品信息，子产品新增创建BB关系时需要用到
     * @param userId 商品用户编号
     * @author xunyl
     * @date 2013-05-07
     */
    public static IData getCrtMerchData(String userId, String serialNum, String acctId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");

        // 将商品用户编号，虚拟手机号，创建BB关系用
        merchOutData.put("USER_ID", userId);
        merchOutData.put("SERIAL_NUMBER", serialNum);

        // 将商品账户信息返回，子产品账户信息新增用
        IData acctInfo = getAcctByUserId(userId);

        merchOutData.put("ACCT_INFO", acctInfo.getData("ACCT_INFO"));

        // 将商品账户编号返回,子产品账户信息用
        merchOutData.put("ACCT_ID", acctId);

        // 将商品规格编号和支付省信息返回，BBOSS侧产品表用（TF_F_USER_GRP_MERCHP）
        // 根据用户编号查询BBOSS侧的商品表
        IDataset grpMerchInfo = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, null, null, null);
        if (grpMerchInfo == null || grpMerchInfo.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_540);
        }
        String merchSpecCode = grpMerchInfo.getData(0).getString("MERCH_SPEC_CODE");// 商品规格编号
        String hostCompany = grpMerchInfo.getData(0).getString("HOST_COMPANY");// 支付省信息
        merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);
        merchOutData.put("HOST_COMPANY", hostCompany);

        // 将业务开展模式返回，判断BBOSS业务是否计费用
        String bizMode = grpMerchInfo.getData(0).getString("HOST_COMPANY");
        merchOutData.put("BIZ_MODE", bizMode);

        // 返回商品信息
        return merchOutData;
    }
    
    /*
     * @description 添加商品信息，子产品新增创建BB关系时需要用到
     * @param userId 商品用户编号
     * @author xunyl
     * @date 2013-05-07
     */
    public static IData getJKDTCrtMerchData(String userId, String serialNum, String acctId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");

        // 将商品用户编号，虚拟手机号，创建BB关系用
        merchOutData.put("USER_ID", userId);
        merchOutData.put("SERIAL_NUMBER", serialNum);

        // 将商品账户信息返回，子产品账户信息新增用
        IData acctInfo = getAcctByUserId(userId);

        merchOutData.put("ACCT_INFO", acctInfo.getData("ACCT_INFO"));

        // 将商品账户编号返回,子产品账户信息用
        merchOutData.put("ACCT_ID", acctId);
        
        // 将商品规格编号和支付省信息返回，BBOSS侧产品表用（TF_F_USER_GRP_MERCHP）
        // 根据用户编号查询BBOSS侧的商品表
        IDataset grpMerchInfo = UserEcrecepOfferfoQry.qryJKDTMerchInfoByUserIdMerchSpecStatus(userId, null, null);
        if (grpMerchInfo == null || grpMerchInfo.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_540);
        }
        String merchSpecCode = grpMerchInfo.getData(0).getString("MERCH_SPEC_CODE");// 商品规格编号
        String hostCompany = grpMerchInfo.getData(0).getString("HOST_COMPANY");// 支付省信息
        merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);
        merchOutData.put("HOST_COMPANY", hostCompany);

        // 将业务开展模式返回，判断BBOSS业务是否计费用
        String bizMode = grpMerchInfo.getData(0).getString("HOST_COMPANY");
        merchOutData.put("BIZ_MODE", bizMode);


        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 根据EC客户编号获取本省客户编号和集团编号
     * @author xunyl
     * @date 2013-06-21
     */
    protected static IData geCustInfoByEcNumber(IData map) throws Exception
    {
        // 1- 定义客户信息对象
        IData custInfo = new DataMap();

        // 2- 获取EC客户编号
        String ecCustNumber = map.getString("EC_SERIAL_NUMBER", "");

        // 3- 根据EC客户编号获取集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(ecCustNumber, null);

        // 4- 获取集团客户信息为空的情况，抛出异常
        if (groupInf == null || groupInf.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, ecCustNumber);
        }

        // 5- 返回客户信息
        custInfo.put("CUST_ID", groupInf.getData(0).getString("CUST_ID"));
        custInfo.put("GROUP_ID", groupInf.getData(0).getString("GROUP_ID"));
        custInfo.put("CUST_NAME", groupInf.getData(0).getString("CUST_NAME"));
        return custInfo;
    }

    /*
     * @description 添加商品信息，子产品修改BB关系时需要用到
     * @param userId 商品用户编号
     * @author xunyl
     * @date 2013-05-07
     */
    public static IData getDelMerchData(String userId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");
        merchOutData.put("USER_ID", userId);

        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 拼装删除的商品资费
     * @author xunyl
     * @date 2013-07-23
     */
    protected static IData getDelMerchDiscntInfo(String elementId, String merchUserId) throws Exception
    {
        // 1- 根据用户编号和资费计划标识查询资费信息
        IDataset reteInfos = UserDiscntInfoQry.getUserPlatDiscnt(merchUserId, elementId);
        if (null == reteInfos || reteInfos.size() == 0)
        {
            return new DataMap();
        }

        // 3- 获取资费信息
        IData rateInfo = reteInfos.getData(0);

        // 4- 添加资费实例ID
        rateInfo.put("INST_ID", rateInfo.getString("INST_ID"));

        // 5- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 6- 添加资费状态，删除时默认为"1"
        rateInfo.put("MODIFY_TAG", "1");

        // 7- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", rateInfo.getString("PRODUCT_ID"));

        // 8- 添加元素ID
        rateInfo.put("ELEMENT_ID", elementId);

        // 9- 添加用户编号
        rateInfo.put("USER_ID", merchUserId);

        // 10- 添加包信息
        rateInfo.put("PACKAGE_ID", rateInfo.getString("PACKAGE_ID"));

        // 11- 添加开始时间
        rateInfo.put("START_DATE", rateInfo.getString("START_DATE"));

        // 12- 添加结束时间
        rateInfo.put("END_DATE", rateInfo.getString("END_DATE"));

        // 13- 返回被删除的资费对象
        return rateInfo;

    }

    /*
     * @description 拼装必选元素信息
     * @auhtor xunyl
     * @date 2013-06-27
     */
    protected static void getElementInfo(IData forceElement, IData elementInfo) throws Exception
    {
        // 1- 添加资费实例ID，受理时默认为""
        elementInfo.put("INST_ID", "");

        // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
        elementInfo.put("ELEMENT_TYPE_CODE", forceElement.getString("ELEMENT_TYPE_CODE"));

        // 3- 添加资费状态，受理时默认为"0"
        elementInfo.put("MODIFY_TAG", "0");

        // 4- 添加产品产品编号
        elementInfo.put("PRODUCT_ID", forceElement.getString("PRODUCT_ID"));

        // 5- 添加元素ID
        elementInfo.put("ELEMENT_ID", forceElement.getString("ELEMENT_ID"));

        // 6- 添加包信息
        elementInfo.put("PACKAGE_ID", forceElement.getString("PACKAGE_ID"));

        // 9- 添加开始时间
        elementInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加参数信息
        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), CSBizBean.getUserEparchyCode());
        IDataset attrParams = new DatasetList();
        for (int i = 0; i < attrItemAList.size(); i++)
        {
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", attrItemAList.getData(i).getString("ATTR_CODE"));
            attrParam.put("ATTR_NAME", attrItemAList.getData(i).getString("ATTR_LABLE"));
            attrParam.put("ATTR_VALUE", attrItemAList.getData(i).getString("ATTR_INIT_VALUE"));
            attrParams.add(attrParam);
        }
        if (null == attrItemAList || attrItemAList.size() == 0)
        {
            elementInfo.put("ATTR_PARAM", "");
        }
        else
        {
            elementInfo.put("ATTR_PARAM", attrParams);
        }

    }

    /*
     * @description 拼装商品资费信息
     * @author xunyl
     * @date 2013-07-23
     */
    protected static void getMerchDiscnt(IData map, IDataset merchDiscnt, String proPoNumber, String merchUserId) throws Exception
    {
        // 1- 获取商品套餐、资费信息
        IDataset tempRatePolicyIdSet = IDataUtil.getDataset("PRSRV_STR1", map);// 商品套餐ID
        IDataset tempActionSet = IDataUtil.getDataset("ACTION_CV1", map);// 套餐操作代码
        IDataset tempPlanIdSet = IDataUtil.getDataset("PLAN_ID", map);// 资费计划标识
        IDataset tempDescriptionSet = IDataUtil.getDataset("DESCRIPTION", map);// 资费描述

        // 2- 拼装资费信息
        for (int i = 0; i < tempRatePolicyIdSet.size(); i++)
        {
            // 2-1 根据商品套餐ID获取包ID
            String ratePolicyId = GrpCommonBean.nullToString(tempRatePolicyIdSet.get(i));
            String packageId = GrpCommonBean.merchToProduct(ratePolicyId, 1, proPoNumber);

            // 循环资费计划标志拼装商品资费信息
            IDataset actionSet = IDataUtil.modiIDataset(tempActionSet.get(i), "ACTION_CV1");
            IDataset planIdset = IDataUtil.modiIDataset(tempPlanIdSet.get(i), "PLAN_ID");
            IDataset descriptionSet = IDataUtil.modiIDataset(tempDescriptionSet.get(i), "DESCRIPTION");
            for (int j = 0; j < planIdset.size(); j++)
            {
                // 2-2-1 获取资费状态
                String action = GrpCommonBean.nullToString(actionSet.get(j));

                // 2-2-2 定义商品元素对象
                IData elementInfo = new DataMap();

                // 2-2-3 获取元素编号
                String planId = GrpCommonBean.nullToString(planIdset.get(j));
                String elementId = GrpCommonBean.merchToProduct(planId, 1, proPoNumber);

                // 2-2-4 根据资费状态拼装商品资费信息
                if ("0".equals(action))
                {
                    String elementName = GrpCommonBean.nullToString(descriptionSet.get(j));
                    elementInfo = getAddMerchDiscntInfo(proPoNumber, elementId, packageId, elementName);
                }
                else if ("0".equals(action))
                {
                    elementInfo = getDelMerchDiscntInfo(elementId, merchUserId);
                }

                // 2-2-5 将资费对象添加至商品资费集中
                merchDiscnt.add(elementInfo);
            }
        }
    }

    /*
     * @description 获取元素信息
     * @author xunyl
     * @date 2013-06-27
     */
    protected static void getProductElementInfo(String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 添加必须服务
        IDataset forceSvcs = ProductInfoQry.getProductForceSvc(productId, CSBizBean.getUserEparchyCode());
        for (int i = 0; i < forceSvcs.size(); i++)
        {
            IData forceSvc = forceSvcs.getData(i);
            // 1-1 拼装服务数据
            IData svcElement = new DataMap();
            getElementInfo(forceSvc, svcElement);
            // 1-2 将拼好的服务元素放入元素集
            elementInfo.add(svcElement);
        }

        // 2- 定义必选元素编号数据集(一级BOSS发送过来的资费与本地获取到的必选资费也许会有重复，一级BOSS发送过来的资费编号放入到该List,后续添加必选资费时校验重复)
        IData elementIdMap = new DataMap();

        // 3- 添加一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("".equals(ratePlanId))
            {
                continue;
            }
            String elementId = GrpCommonBean.merchToProduct(ratePlanId, 1, productId);
            elementIdMap.put(elementId, elementId);
            getProductRateInfo(j, productId, elementId, secondProductInfo, elementInfo);
        }

        // 4- 添加必选资费
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(productId);
        for (int i = 0; i < forceDiscnts.size(); i++)
        {
            IData forceDct = forceDiscnts.getData(i);
            // 4-1 获取资费编号并判断资费编号在必选元素编号集中是否存在，存在说明重复，进行下一次循环
            String dctId = forceDct.getString("ELEMENT_ID");
            if (elementIdMap.containsKey(dctId))
            {
                continue;
            }
            // 4-2 拼装资费数据
            IData dctElement = new DataMap();
            getElementInfo(forceDct, dctElement);
            // 4-3 将拼好的服务元素放入元素集
            elementInfo.add(dctElement);
        }
    }

    
    /*
     * @description 获取元素信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getJKDTProductElementInfo(String userId, String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 获取一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");

        // 2- 循环添加资费信息
        for (int i = 0; i < ratePlanIdList.size(); i++)
        {
            // 2-1 获取资费计划标识
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(i));
            if ("".equals(ratePlanId))
            {
                continue;
            }

            // 2-2 获取产品级资费操作代码
            String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(i));

            
            // 2-3 拼装资费状态为删除的产品资费信息
            if ("0".equals(ratePlanAction))
            {
                getJKDTProductOldRateInfo(userId, i, productId, ratePlanId, secondProductInfo, elementInfo);
            }

            // 2-4 拼装资费状态为新增的产品资费信息
            if ("1".equals(ratePlanAction))
            {
                getJKDTProductNewRateInfo(i, productId, ratePlanId, secondProductInfo, elementInfo);
            }

        }
    }

    
    /*
     * @description 获取元素信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getProductElementInfo(String userId, String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 获取一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");

        // 2- 循环添加资费信息
        for (int i = 0; i < ratePlanIdList.size(); i++)
        {
            // 2-1 获取资费计划标识
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(i));
            if ("".equals(ratePlanId))
            {
                continue;
            }

            // 2-2 获取产品级资费操作代码
            String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(i));

            // 2-3 拼装资费状态为删除的产品资费信息
            if ("0".equals(ratePlanAction))
            {
                getProductOldRateInfo(userId, i, productId, ratePlanId, secondProductInfo, elementInfo);
            }

            // 2-4 拼装资费状态为新增的产品资费信息
            if ("1".equals(ratePlanAction))
            {
                getProductNewRateInfo(i, productId, ratePlanId, secondProductInfo, elementInfo);
            }

        }
    }

    /*
     * @description 拼装新增的产品资费信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getJKDTProductNewRateInfo(int i, String productId, String ratePlanId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(i, secondProductInfo, thirdProductInfo);

        // 2- 定义资费信息
        IData rateInfo = new DataMap();

        // 3- 添加资费实例ID，受理时默认为""
        rateInfo.put("INST_ID", "");

        // 4- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 5- 添加资费状态，受理时默认为"0"
        rateInfo.put("MODIFY_TAG", "0");

        // 6- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", productId);

        // 7- 添加元素ID(元素ID即省内的资费编号，与一级BOSS传递过来的RATE_PLAN_ID形成一一对应关系)
        String elementId = GrpCommonBean.merchJKDTToProduct(ratePlanId, 1, productId);
        rateInfo.put("ELEMENT_ID", elementId);

        // 8- 添加包信息
        IDataset packageElement = PkgElemInfoQry.getPackageElementByProductId(productId, "D", elementId);
        if (null == packageElement || packageElement.size() == 0)
        {
            // 根据元素编号%s获取不到对应的包信息
            CSAppException.apperr(CrmUserException.CRM_USER_907);
        }
        String packageId = packageElement.getData(0).getString("PACKAGE_ID");
        rateInfo.put("PACKAGE_ID", packageId);

        // 9- 添加开始时间      daidl
        String poRatePolicyEffRule = secondProductInfo.getString("PO_RATE_POLICY_EFF_RULE");
        if("2".equals(poRatePolicyEffRule)){
             //下账期生效，下月第一天生效
            rateInfo.put("START_DATE",getFirstDayOfNextMonth());
            logger.debug("=======  SysDateMgr.getFirstDayOfNextMonth()  ======="+getFirstDayOfNextMonth());
        }else if("4".equals(poRatePolicyEffRule)){
        	//下一天生效
        	 rateInfo.put("START_DATE", getTomorrowDate());

        	 logger.debug("=======  getTomorrowDate()   ======="+getTomorrowDate());
        	 
        }else {
        	//poRatePolicyEffRule为1，立即生效
        	rateInfo.put("START_DATE", SysDateMgr.getSysTime());
        }

        // 10- 添加结束时间
        rateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数信息
        IDataset productIcbParamInfo = new DatasetList();
        GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfo);
        if (null == productIcbParamInfo || productIcbParamInfo.size() == 0)
        {
            rateInfo.put("ATTR_PARAM", "");
        }
        else
        {
            rateInfo.put("ATTR_PARAM", productIcbParamInfo);
        }

        // 12- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }
    
    public static String getTomorrowDate() throws Exception
    {
        return SysDateMgr.getTomorrowDate()+" 00:00:01";
    }
    public static String getFirstDayOfNextMonth() throws Exception
    {
        return SysDateMgr.getFirstDayOfNextMonth()+" 00:00:01";
    }



    /*
     * @description 拼装新增的产品资费信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getProductNewRateInfo(int i, String productId, String ratePlanId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(i, secondProductInfo, thirdProductInfo);

        // 2- 定义资费信息
        IData rateInfo = new DataMap();

        // 3- 添加资费实例ID，受理时默认为""
        rateInfo.put("INST_ID", "");

        // 4- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 5- 添加资费状态，受理时默认为"0"
        rateInfo.put("MODIFY_TAG", "0");

        // 6- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", productId);

        // 7- 添加元素ID(元素ID即省内的资费编号，与一级BOSS传递过来的RATE_PLAN_ID形成一一对应关系)
        String elementId = GrpCommonBean.merchToProduct(ratePlanId, 1, productId);
        rateInfo.put("ELEMENT_ID", elementId);

        // 8- 添加包信息
        IDataset packageElement = PkgElemInfoQry.getPackageElementByProductId(productId, "D", elementId);
        if (null == packageElement || packageElement.size() == 0)
        {
            // 根据元素编号%s获取不到对应的包信息
            CSAppException.apperr(CrmUserException.CRM_USER_907);
        }
        String packageId = packageElement.getData(0).getString("PACKAGE_ID");
        rateInfo.put("PACKAGE_ID", packageId);

        // 9- 添加开始时间
        rateInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        rateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数信息
        IDataset productIcbParamInfo = new DatasetList();
        GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfo);
        if (null == productIcbParamInfo || productIcbParamInfo.size() == 0)
        {
            rateInfo.put("ATTR_PARAM", "");
        }
        else
        {
            rateInfo.put("ATTR_PARAM", productIcbParamInfo);
        }

        // 12- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }

   

    
    /*
     * @description 拼装删除的产品资费信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getJKDTProductOldRateInfo(String userId, int i, String productId, String ratePlanId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(i, secondProductInfo, thirdProductInfo);

        // 2- 根据用户编号和资费计划标识查询资费信息
        String elementId = GrpCommonBean.merchJKDTToProduct(ratePlanId, 1, productId);
        
        IDataset reteInfos = UserDiscntInfoQry.getUserPlatDiscnt(userId, elementId);
        

        
        if (null == reteInfos || reteInfos.size() == 0)
        {
            return;
        }

        // 3- 获取资费信息
        IData rateInfo = reteInfos.getData(0);

        // 4- 添加资费实例ID
        rateInfo.put("INST_ID", rateInfo.getString("INST_ID"));

        // 5- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 6- 添加资费状态，删除时默认为"1"
        rateInfo.put("MODIFY_TAG", "1");

        // 7- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", rateInfo.getString("PRODUCT_ID"));

        // 8- 添加元素ID
        rateInfo.put("ELEMENT_ID", elementId);

        // 9- 添加用户编号
        rateInfo.put("USER_ID", userId);

        // 10- 添加包信息
        rateInfo.put("PACKAGE_ID", rateInfo.getString("PACKAGE_ID"));

        // 11- 添加开始时间
        rateInfo.put("START_DATE", rateInfo.getString("START_DATE"));

        // 12- 添加结束时间
        String poRatePolicyEffRule = secondProductInfo.getString("PO_RATE_POLICY_EFF_RULE");
        if("2".equals(poRatePolicyEffRule)){ //下账期生效，本月最后一天失效
           rateInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
           logger.debug("=======  SysDateMgr.getLastDateThisMonth()  ======="+SysDateMgr.getLastDateThisMonth());
        }else if("4".equals(poRatePolicyEffRule)){	//今天23：59：59失效
       	   rateInfo.put("END_DATE", getSysDate());
       	   logger.debug("=======  getSysDate()   ======="+getSysDate());
       	 
        }else {	//poRatePolicyEffRule为1，立即失效
       	   rateInfo.put("END_DATE", SysDateMgr.getSysTime());
        }

        // 13- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }
    
    public static String getSysDate() throws Exception
    {
        return SysDateMgr.getSysDate()+" 23:59:59";
    }
    

    
    /*
     * @description 拼装删除的产品资费信息
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void getProductOldRateInfo(String userId, int i, String productId, String ratePlanId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(i, secondProductInfo, thirdProductInfo);

        // 2- 根据用户编号和资费计划标识查询资费信息
        String elementId = GrpCommonBean.merchToProduct(ratePlanId, 1, productId);
        IDataset reteInfos = UserDiscntInfoQry.getUserPlatDiscnt(userId, elementId);
        if (null == reteInfos || reteInfos.size() == 0)
        {
            return;
        }

        // 3- 获取资费信息
        IData rateInfo = reteInfos.getData(0);

        // 4- 添加资费实例ID
        rateInfo.put("INST_ID", rateInfo.getString("INST_ID"));

        // 5- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 6- 添加资费状态，删除时默认为"1"
        rateInfo.put("MODIFY_TAG", "1");

        // 7- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", rateInfo.getString("PRODUCT_ID"));

        // 8- 添加元素ID
        rateInfo.put("ELEMENT_ID", elementId);

        // 9- 添加用户编号
        rateInfo.put("USER_ID", userId);

        // 10- 添加包信息
        rateInfo.put("PACKAGE_ID", rateInfo.getString("PACKAGE_ID"));

        // 11- 添加开始时间
        rateInfo.put("START_DATE", rateInfo.getString("START_DATE"));

        // 12- 添加结束时间
        rateInfo.put("END_DATE", SysDateMgr.getSysTime());

        // 13- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }

    /*
     * @description 拼装产品资费信息
     * @author xunyl
     * @date 2013-06-25
     */
    protected static void getProductRateInfo(int j, String productId, String elementId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(j, secondProductInfo, thirdProductInfo);

        // 2- 定义资费信息
        IData rateInfo = new DataMap();

        // 3- 添加资费实例ID，受理时默认为""
        rateInfo.put("INST_ID", "");

        // 4- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 5- 添加资费状态，受理时默认为"0"
        rateInfo.put("MODIFY_TAG", "0");

        // 6- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", productId);

        // 7- 添加元素ID(元素ID即省内的资费编号，与一级BOSS传递过来的RATE_PLAN_ID形成一一对应关系)
        rateInfo.put("ELEMENT_ID", elementId);

        // 8- 添加包信息
        IDataset packageElement = PkgElemInfoQry.getPackageElementByProductId(productId, "D", elementId);
        if (null == packageElement || packageElement.size() == 0)
        {
            // 根据元素编号%s获取不到对应的包信息
            CSAppException.apperr(CrmUserException.CRM_USER_907);
        }
        String packageId = packageElement.getData(0).getString("PACKAGE_ID");
        rateInfo.put("PACKAGE_ID", packageId);

        // 9- 添加开始时间
        rateInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        rateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数信息
        IDataset productIcbParamInfo = new DatasetList();
        GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfo);
        if (null == productIcbParamInfo || productIcbParamInfo.size() == 0)
        {
            rateInfo.put("ATTR_PARAM", "");
        }
        else
        {
            rateInfo.put("ATTR_PARAM", productIcbParamInfo);
        }

        // 12- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }

    /*
     * @description 设置产品操作类型对应为新增产品的产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeAddProdOpData(int i,IData firstProductInfo, IData secondProductInfo, IData map, IDataset productInfoset) throws Exception
    {
        // 1- 定义产品信息变量
        IData productInfo = new DataMap();

        // 2- 获取商品用户编号
        String merchUserId = GrpCommonBean.getMerchUserIdByProdOffId(map.getString("RSRV_STR2"));

        // 3- 根据商品用户编号查询商品用户数据
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(merchUserId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_80);
        }

        // 4- 添加客户编号
        String custId = userInfo.getString("CUST_ID");
        productInfo.put("CUST_ID", custId);

        // 5- 添加产品编号
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
        String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
        productInfo.put("PRODUCT_ID", productId);

        // 6- 添加帐户信息
        productInfo.put("ACCT_IS_ADD", true);// 这里的值为true,既产品生成台帐时也会新增账户，但是账户编号会根据商品的返回值来定

        // 7- 添加帐户编号
        IData inparams = new DataMap();
        inparams.put("ID", merchUserId);
        IData payRelation = PayRelaInfoQry.getPayRelation(inparams);
        String acctId = payRelation.getString("ACCT_ID");
        productInfo.put("ACCT_ID", acctId);

        // 8- 添加处理标志(商品变更，产品新增)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue());

        // 9- 添加元素信息(包括服务与资费)
        IDataset elementInfo = new DatasetList();
        getProductElementInfo(productId, secondProductInfo, elementInfo);
        if (null == elementInfo || elementInfo.size() == 0)
        {
            productInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            productInfo.put("ELEMENT_INFO", elementInfo);
        }

        // 10- 添加产品属性信息
        IData productParam = new DataMap();
        IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
        productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

        // 11- 添加虚拟手机号
        IData idGen = new DataMap();
        idGen.put("GROUP_ID", UcaInfoQry.qryGrpInfoByCustId(custId).getString("GROUP_ID"));
        idGen.put("PRODUCT_ID", productId);
        String serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
        serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, productParamInfoList);
        productInfo.put("SERIAL_NUMBER", serialNumber);

        // 12- 添加BBOSS特有产品信息
        IData merchOutData = getCrtMerchData(merchUserId, userInfo.getString("SERIAL_NUMBER"), acctId);
        productInfo.put("OUT_MERCH_INFO", merchOutData);

        // 13- BBOSS_PRODUCT_INFO仅供BBOSS子类使用，用于创建tf_f_user_grp_merchp表
        IData product = new DataMap();
        product.put("PRODUCT_ID", productId);
        product.put("PRODUCT_OPER_CODE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue());
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        String productOfferId = GrpCommonBean.nullToString(productOfferIdSet.get(i));
        product.put("PRODUCT_OFFER_ID", productOfferId);
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        String productOrderId = GrpCommonBean.nullToString(productOrderIdSet.get(i));
        product.put("PRODUCT_ORDER_ID", productOrderId);
        productInfo.put("BBOSS_PRODUCT_INFO", product);

        // 14- 判断新增的产品在现有的表中是否存在，如果存在则不需要再次新增，直接退出
        IDataset merchpUserInfoList =UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
        IDataset merchpTradeInfoList =TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferId,null);
        if(IDataUtil.isNotEmpty(merchpUserInfoList) || IDataUtil.isNotEmpty(merchpTradeInfoList)){
            return;
        }

        // 15- 添加反向受理标记(反向受理不发服务开通)
        productInfo.put("IN_MODE_CODE", "6");

        // 16- 将产品信息添加至产品数据集
        productInfoset.add(productInfo);
    }

    /*
     * @description 设置产品操作类型对应为修改产品资费的产品数据
     * @author xunyl
     * @date 2013-07-03
     */
    protected static void makeChgDisOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getMerchpUserIdByProdId(productOfferId);
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加用户处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

            // 2-8 添加元素信息
            IDataset elementInfo = new DatasetList();
            getProductElementInfo(merchpUserId, productId, secondProductInfo, elementInfo);
            if (null == elementInfo || elementInfo.size() == 0)
            {
                productInfo.put("ELEMENT_INFO", "");
            }
            else
            {
                productInfo.put("ELEMENT_INFO", elementInfo);
            }

            // 2-9 添加产品操作类型
            productInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue());

            // 2-10 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-11 添加商品信息，子产品处理BBOSS侧的子表用
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-12 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-13 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }

    /*
     * @description 设置产品操作类型对应为修改产品属性的产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeChgParamOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getMerchpUserIdByProdId(productOfferId);
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加用户处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

            // 2-8 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-9 添加产品操作类型
            productInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue());

            // 2-10 添加商品信息，子产品处理BBOSS侧的资费表用
            IData merchOutData = new DataMap();

            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }

    /*
     * @description 将一级BOSS传递过来的数据拼装成符合集团产品受理基类处理的数据集
     * @author xunyl
     * @date 2013-07-01
     */
    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        makeMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeMerchPInfoData(map, returnVal);

        // 4- 特殊情况下，需要将拼好的标准结构进行特殊处理
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        GrpCommonBean.modifyInparamsBySpecialBiz(returnVal, merchOperType);

        // 5- 校验是否有中断情况出现，如果有中断情况，重新定义返回数据
        checkOperResult(map, returnVal);

        // 6- 返回结果
        return returnVal;
    }

    /*
     * @description 设置产品操作类型对应为删除产品的产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeDelProdOpData(int i, IData firstProductInfo, IData map, IDataset productInfoset) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData productInfo = new DataMap();

        // 2- 添加产品用户编号
        String merchpUserId = GrpCommonBean.getMerchpUserIdByProdId(firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER").get(i).toString());

        productInfo.put("USER_ID", merchpUserId);

        // 3- 添加预约标志(BBOSS侧没有预约)
        productInfo.put("IF_BOOKING", "false");

        // 4- 添加取消订购原因
        productInfo.put("REASON_CODE", "");

        // 5- 添加备注信息
        productInfo.put("REMARK", "");

        // 6- 添加处理标志(商品变更，产品注销)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue());

        // 7- 添加商品信息，子产品修改BB关系时需要用到
        String merchUserId = GrpCommonBean.getMerchUserIdByProdOffId(map.getString("RSRV_STR2"));
        IData merchOutData = getDelMerchData(merchUserId);
        productInfo.put("OUT_MERCH_INFO", merchOutData);

        productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

        // 8- 添加反向受理标记(反向受理不发服务开通)
        productInfo.put("IN_MODE_CODE", "6");

        // 9- 添加单条产品至产品数据集
        productInfoset.add(productInfo);
    }

    /*
     * @description 拼装商品数据
     * @author xunyl
     * @date 2013-07-01
     */
    protected static void makeMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 3- 添加用户编号
        String merchUserId = GrpCommonBean.getMerchUserIdByProdOffId(map.getString("RSRV_STR2"));
        merchInfo.put("USER_ID", merchUserId);

        // 4- 添加商品资费信息(当商品操作类型为对应为修改商品资费时，会有商品资费信息的产生)
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType))
        {
            // 4-1 获取商品资费信息
            IDataset merchDiscnt = new DatasetList();
            getMerchDiscnt(map, merchDiscnt, proPoNumber, merchUserId);

            // 4-2 添加商品资费信息至商品对象
            merchInfo.put("ELEMENT_INFO", merchDiscnt);
        }

        // 6- 添加BBOSS特有商品信息(套餐生效规则、业务保障等级、商品操作类型)
        IData bbossMerchInfo = new DataMap();
        setBBossMerchInfo(merchOperType, bbossMerchInfo, map);
        merchInfo.put("GOOD_INFO", bbossMerchInfo);

        // 7- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 8-判断是不是管理节点数据
        String busiSign = map.getString("BUSI_SIGN", "");
        if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
        {
            merchInfo.put("BBOSS_MANAGE_CREATE", true);
        }

        // 9- 添加商品信息至返回结果中
        returnVal.put("MERCH_INFO", merchInfo);

        // 10- 操作类型为合同变更时，商品附件信息发往客管侧保存
        if(StringUtils.equals("21", merchOperType)){
            IData custInfo = geCustInfoByEcNumber(map);
            String custId = custInfo.getString("CUST_ID");
            GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
        }
    }

    /*
     * @description 拼装产品数据
     * @auhtor xunyl
     * @date 2013-07-01
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 2- 分情况处理不同商品操作类型下的产品信息
        // 2-1 获取商品操作类型
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);

        // 2-2 商品操作类型为修改商品资费
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType))
        {
            makeChgDisOpData(map, productInfoset);
        }

        // 2-3 商品操作类型为商品暂停、恢复，预取消商品订购，冷冻期恢复商品订购(产品下面不需要拼装资费与参数信息)
        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType)
                ||GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue().equals(merchOperType)
                ||GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchOperType) )
        {
            makeOtherOpData(map, productInfoset);
        }

        // 2-4 商品操作类型为修改订购商品组成关系(包括新增产品订购、取消产品订购)
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperType))
        {
            makeModifyGroupData(map, productInfoset);
        }

        // 2-5 商品操作类型为修改订购产品属性
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue().equals(merchOperType))
        {
            makeChgParamOpData(map, productInfoset);
        }

        // 3- 校验集团产品受理程序是否继续进行
        if (null == productInfoset || productInfoset.size() == 0)
        {
            breakType = "1";
        }

        // 3- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }

    /*
     * @description 设置商品操作类型为修改商品组成关系的产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeModifyGroupData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 获取产品操作类型
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));

            // 3-3 根据产品操作类型分情况拼装产品数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperCode))
            {
                makeAddProdOpData(i,firstProductInfo,secondProductInfo, map, productInfoset);
            }
            else if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(productOperCode))
            {
                makeDelProdOpData(i, firstProductInfo, map, productInfoset);
            }
        }
    }

    /*
     * @description 设置商品操作类型为商品暂停、恢复、预取消、冷冻期商品预恢复的产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeOtherOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getMerchpUserIdByProdId(productOfferId);
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加产品操作类型
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            productInfo.put("PRODUCT_OPER_TYPE", productOperCode);

            // 2-8 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-9添加BBOSS基本信息及产品订单信息
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-10 添加处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue());

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }

    /*
     * @description 设置BBOSS特有商品信息(套餐生效规则、业务保障等级、商品操作类型)
     * @author xunyl
     * @date 2013-07-02
     */
    protected static void setBBossMerchInfo(String merchOperType, IData bbossMerchInfo, IData map) throws Exception
    {
        // 1- 设置生失效方式
        String pORatePolicyEffRule = "1";// 默认为立即生效
        if (null != IDataUtil.getDataset("RSRV_STR3", map) && IDataUtil.getDataset("RSRV_STR3", map).size() > 0)
        {
            pORatePolicyEffRule = (String) IDataUtil.getDataset("RSRV_STR3", map).get(0);
        }
        bbossMerchInfo.put("PAY_MODE", pORatePolicyEffRule);

        // 2- 业务保障等级一级BOSS没有传递到省BOSS，默认为普通级
        bbossMerchInfo.put("BUS_NEED_DEGREE", "4");

        // 3- 设置商品操作类型
        bbossMerchInfo.put("MERCH_OPER_CODE", merchOperType);

        if (StringUtils.isNotEmpty(map.getString("SUBSCRIBE_ID")))
        {
            // 4- 商品订单ID BBOSS下发
            String order_id = map.getString("SUBSCRIBE_ID");
            bbossMerchInfo.put("MERCH_ORDER_ID", order_id);
        }
        if (StringUtils.isNotEmpty(map.getString("RSRV_STR2")))
        {
            // 5- 商品订购关系ID BBOSS下发
            String offer_id = map.getString("RSRV_STR2");
            bbossMerchInfo.put("MERCH_OFFER_ID", offer_id);
        }
      //REQ201911040021_(集团全网)关于更新集客大厅与省公司接口规范的通知--启用成员叠加包、增加订购渠道信息  add by huangzl3
        // 2-8 添加BBOSS渠道信息
        IData channelInfo = new DataMap();
        channelInfo.put("MAINTEANCE_CHANNELID", map.getString("MAINTEANCE_CHANNELID", ""));
        channelInfo.put("CHANNEL_TYPE", map.getString("CHANNEL_TYPE", ""));
        channelInfo.put("PROJECT_NAME", map.getString("PROJECT_NAME", ""));
        channelInfo.put("PROJECT_ID", map.getString("PROJECT_ID", ""));
        channelInfo.put("SETTLEMENT_RATIO", map.getString("SETTLEMENT_RATIO", ""));
        channelInfo.put("CHANNEL_ID", map.getString("CHANNEL_ID", ""));

        bbossMerchInfo.put("CHANNEL_INFO", channelInfo);
    }

    /**
     * @description 添加产品order_id,offer_id
     * @author chengjian
     * @date 2014-11-28
     */
    protected static void getProductBaseInfo(IData map ,String productOfferId, String productOrderId, IData merchOutData ) throws Exception
    {
        // 1- 添加BBOSS标志
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");

        // 2- 添加商品规格编号
        String merchSpecCode = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0);
        merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);

        // 3- 添加产品订购关系ID BBOSS下发
        if (StringUtils.isNotEmpty(productOfferId))
        {
            merchOutData.put("PRODUCT_OFFER_ID", productOfferId);
        }
        // 4- 添加产品订单ID BBOSS下发
        if (StringUtils.isNotEmpty(productOfferId))
        {
            merchOutData.put("PRODUCT_ORDER_ID", productOrderId);
        }
    }


    /*
     * @description 根据客户编号查询帐户信息
     * @author chenyi
     * @date 2014-9-18
     */
    protected static IData getAcctByUserId(String userId) throws Exception
    {
        // 1- 定义返回数据
        IData accoutResult = new DataMap();
        IData payInfo = UcaInfoQry.qryAcctInfoByUserIdForGrp(userId);
        if (IDataUtil.isNotEmpty(payInfo))
        {
            // 拼装帐户信息
            IData acctInfo = new DataMap();
            acctInfo.put("SAME_ACCT", 0);
            acctInfo.put("PAY_MODE_CODE", payInfo.getString("PAY_MODE_CODE", "0"));
            acctInfo.put("ACCT_ID", payInfo.getString("ACCT_ID"));
            acctInfo.put("PAY_NAME", payInfo.getString("PAY_NAME"));
            acctInfo.put("RSRV_STR8", payInfo.getString("RSRV_STR8"));// 打印模式 分产品项展示费用
            acctInfo.put("RSRV_STR9", payInfo.getString("RSRV_STR9"));// 发票模式：实收发票
            accoutResult.put("ACCT_INFO", acctInfo);
        }
        // 4-返回帐户信息
        return accoutResult;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        makeJKDTMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeJKDTMerchPInfoData(map, returnVal);

        // 4- 特殊情况下，需要将拼好的标准结构进行特殊处理
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        GrpCommonBean.modifyJKDTInparamsBySpecialBiz(returnVal, merchOperType);

        // 5- 校验是否有中断情况出现，如果有中断情况，重新定义返回数据
        checkOperResult(map, returnVal);

        // 6- 返回结果
        return returnVal;
    }

    protected static void makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchJKDTToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 3- 添加用户编号
        String merchUserId = GrpCommonBean.getJKDTMerchUserIdByProdOffId(map.getString("RSRV_STR2"));
        merchInfo.put("USER_ID", merchUserId);

        // 4- 添加商品资费信息(当商品操作类型为对应为修改商品资费时，会有商品资费信息的产生)
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType))
        {
            // 4-1 获取商品资费信息
            IDataset merchDiscnt = new DatasetList();
            getJKDTMerchDiscnt(map, merchDiscnt, proPoNumber, merchUserId);

            // 4-2 添加商品资费信息至商品对象
            merchInfo.put("ELEMENT_INFO", merchDiscnt);
        }

        // 6- 添加BBOSS特有商品信息(套餐生效规则、业务保障等级、商品操作类型)
        IData bbossMerchInfo = new DataMap();
        setBBossMerchInfo(merchOperType, bbossMerchInfo, map);
        merchInfo.put("GOOD_INFO", bbossMerchInfo);

        // 7- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 8-判断是不是管理节点数据
        String busiSign = map.getString("BUSI_SIGN", "");
        if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
        {
            merchInfo.put("BBOSS_MANAGE_CREATE", true);
        }

        // 9- 添加商品信息至返回结果中
        returnVal.put("MERCH_INFO", merchInfo);

        // 10- 操作类型为合同变更时，商品附件信息发往客管侧保存
        if(StringUtils.equals("21", merchOperType)){
            IData custInfo = geCustInfoByEcNumber(map);
            String custId = custInfo.getString("CUST_ID");
            GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
        }
    }
    protected static void getJKDTMerchDiscnt(IData map, IDataset merchDiscnt, String proPoNumber, String merchUserId) throws Exception
    {
        // 1- 获取商品套餐、资费信息
        IDataset tempRatePolicyIdSet = IDataUtil.getDataset("PRSRV_STR1", map);// 商品套餐ID
        IDataset tempActionSet = IDataUtil.getDataset("ACTION_CV1", map);// 套餐操作代码
        IDataset tempPlanIdSet = IDataUtil.getDataset("PLAN_ID", map);// 资费计划标识
        IDataset tempDescriptionSet = IDataUtil.getDataset("DESCRIPTION", map);// 资费描述

        // 2- 拼装资费信息
        for (int i = 0; i < tempRatePolicyIdSet.size(); i++)
        {
            // 2-1 根据商品套餐ID获取包ID
            String ratePolicyId = GrpCommonBean.nullToString(tempRatePolicyIdSet.get(i));
            String packageId = GrpCommonBean.merchJKDTToProduct(ratePolicyId, 1, proPoNumber);

            // 循环资费计划标志拼装商品资费信息
            IDataset actionSet = IDataUtil.modiIDataset(tempActionSet.get(i), "ACTION_CV1");
            IDataset planIdset = IDataUtil.modiIDataset(tempPlanIdSet.get(i), "PLAN_ID");
            IDataset descriptionSet = IDataUtil.modiIDataset(tempDescriptionSet.get(i), "DESCRIPTION");
            for (int j = 0; j < planIdset.size(); j++)
            {
                // 2-2-1 获取资费状态
                String action = GrpCommonBean.nullToString(actionSet.get(j));

                // 2-2-2 定义商品元素对象
                IData elementInfo = new DataMap();

                // 2-2-3 获取元素编号
                String planId = GrpCommonBean.nullToString(planIdset.get(j));
                String elementId = GrpCommonBean.merchJKDTToProduct(planId, 1, proPoNumber);

                // 2-2-4 根据资费状态拼装商品资费信息
                if ("1".equals(action))
                {
                    String elementName = GrpCommonBean.nullToString(descriptionSet.get(j));
                    elementInfo = getAddMerchDiscntInfo(proPoNumber, elementId, packageId, elementName);
                    
                    if("1725".equals(planId) || "1741".equals(planId)){
                        // 设置最低消息费生效月(特定商品) fuzn 2019-06-14
                        assembleMinCEffMtoAttr(proPoNumber,map,elementInfo);
                    }
                }
                else if ("0".equals(action))
                {
                    elementInfo = getDelMerchDiscntInfo(elementId, merchUserId);
                }

                // 2-2-5 将资费对象添加至商品资费集中
                merchDiscnt.add(elementInfo);
            }
        }
    }

    protected static void makeJKDTMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 2- 分情况处理不同商品操作类型下的产品信息
        // 2-1 获取商品操作类型
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);

        // 2-2 商品操作类型为修改商品资费
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType))
        {
            makeJKDTChgDisOpData(map, productInfoset);
        }

        // 2-3 商品操作类型为商品暂停、恢复，预取消商品订购，冷冻期恢复商品订购(产品下面不需要拼装资费与参数信息)
        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType)
                ||GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue().equals(merchOperType)
                ||GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchOperType) )
        {
            makeJKDTOtherOpData(map, productInfoset);
        }

        // 2-4 商品操作类型为修改订购商品组成关系(包括新增产品订购、取消产品订购)
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperType))
        {
        	makeJKDTModifyGroupData(map, productInfoset);
        }

        // 2-5 商品操作类型为修改订购产品属性
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue().equals(merchOperType))
        {
            makeJKDTChgParamOpData(map, productInfoset);
        }
        
        // 2-6 商品操作类型为暂停/恢复是添加成员/叠加包
        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue().equals(merchOperType)||GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchOperType))
        {

            makeJKDTChgMebFluxData(map, productInfoset);
        }
        // 3- 校验集团产品受理程序是否继续进行
        if (null == productInfoset || productInfoset.size() == 0)
        {
            breakType = "1";
        }

        // 3- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }

    protected static void makeJKDTOtherOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(productOfferId);
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2, null);
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加产品操作类型
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            productInfo.put("PRODUCT_OPER_TYPE", productOperCode);

            // 2-8 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-9添加BBOSS基本信息及产品订单信息
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-10 添加处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue());

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }

    protected static void makeJKDTChgDisOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(productOfferId);
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加用户处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

            // 2-8 添加元素信息
            IDataset elementInfo = new DatasetList();
            

            getJKDTProductElementInfo(merchpUserId, productId, secondProductInfo, elementInfo);

            
            if (null == elementInfo || elementInfo.size() == 0)
            {
                productInfo.put("ELEMENT_INFO", "");
            }
            else
            {
                productInfo.put("ELEMENT_INFO", elementInfo);
            }

            // 2-9 添加产品操作类型
            productInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue());

            // 2-10 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-11 添加商品信息，子产品处理BBOSS侧的子表用
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-12 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-13 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }
    protected static void makeJKDTModifyGroupData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 获取产品操作类型
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));

            // 3-3 根据产品操作类型分情况拼装产品数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperCode))
            {
                makeJKDTAddProdOpData(i,firstProductInfo,secondProductInfo, map, productInfoset);
            }
            else if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(productOperCode))
            {
                makeJKDTDelProdOpData(i, firstProductInfo, map, productInfoset);
            }
        }
    }

    protected static void makeJKDTChgParamOpData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        
        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(productOfferId);//daidl
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加用户处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

            // 2-8 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-9 添加产品操作类型
            productInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue());

            // 2-10 添加商品信息，子产品处理BBOSS侧的资费表用
            IData merchOutData = new DataMap();

            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");
            
            
            // 2-12 添加元素信息  商品级资费就走5  产品级别资费都走9
            IDataset elementInfo = new DatasetList();
            getJKDTProductElementInfo(merchpUserId, productId, secondProductInfo, elementInfo);
            if (null == elementInfo || elementInfo.size() == 0)
            {
                productInfo.put("ELEMENT_INFO", "");
            }
            else
            {
                productInfo.put("ELEMENT_INFO", elementInfo);
            }

            // 2-13 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }
    }
    
    protected static void makeJKDTChgMebFluxData(IData map, IDataset productInfoset) throws Exception
    {
        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 2-2 定义产品信息变量
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 2-5 添加产品用户编号
            String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(productOfferId);//daidl
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2 ,null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加产品操作类型
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            productInfo.put("PRODUCT_OPER_TYPE", productOperCode);

            // 2-8 添加产品参数信息
            IData productParam = new DataMap();
            GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 2-9 添加BBOSS基本信息及产品订单信息
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

            // 2-10 添加处理标志
            productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue());

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }

    }

    protected static void makeJKDTAddProdOpData(int i,IData firstProductInfo, IData secondProductInfo, IData map, IDataset productInfoset) throws Exception
    {
        // 1- 定义产品信息变量
        IData productInfo = new DataMap();

        // 2- 获取商品用户编号
        String merchUserId = GrpCommonBean.getJKDTMerchUserIdByProdOffId(map.getString("RSRV_STR2"));

        // 3- 根据商品用户编号查询商品用户数据
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(merchUserId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_80);
        }

        // 4- 添加客户编号
        String custId = userInfo.getString("CUST_ID");
        productInfo.put("CUST_ID", custId);

        // 5- 添加产品编号
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
        String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
        productInfo.put("PRODUCT_ID", productId);

        // 6- 添加帐户信息
        productInfo.put("ACCT_IS_ADD", true);// 这里的值为true,既产品生成台帐时也会新增账户，但是账户编号会根据商品的返回值来定

        // 7- 添加帐户编号
        IData inparams = new DataMap();
        inparams.put("ID", merchUserId);
        IData payRelation = PayRelaInfoQry.getPayRelation(inparams);
        String acctId = payRelation.getString("ACCT_ID");
        productInfo.put("ACCT_ID", acctId);

        // 8- 添加处理标志(商品变更，产品新增)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue());

        // 9- 添加元素信息(包括服务与资费)
        IDataset elementInfo = new DatasetList();
        getJKDTProductElementInfo(productId, secondProductInfo, elementInfo);
        if (null == elementInfo || elementInfo.size() == 0)
        {
            productInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            productInfo.put("ELEMENT_INFO", elementInfo);
        }

        // 10- 添加产品属性信息
        IData productParam = new DataMap();
        IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
        productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

        // 11- 添加虚拟手机号
        IData idGen = new DataMap();
        idGen.put("GROUP_ID", UcaInfoQry.qryGrpInfoByCustId(custId).getString("GROUP_ID"));
        idGen.put("PRODUCT_ID", productId);
        String serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
        serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, productParamInfoList);
        productInfo.put("SERIAL_NUMBER", serialNumber);

        // 12- 添加BBOSS特有产品信息
        //这块还在用老BBOSS导致报错，先注掉
        IData merchOutData = getJKDTCrtMerchData(merchUserId, userInfo.getString("SERIAL_NUMBER"), acctId);
        productInfo.put("OUT_MERCH_INFO", merchOutData);

        // 13- BBOSS_PRODUCT_INFO仅供BBOSS子类使用，用于创建tf_f_user_grp_merchp表
        IData product = new DataMap();
        product.put("PRODUCT_ID", productId);
        product.put("PRODUCT_OPER_CODE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue());
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        String productOfferId = GrpCommonBean.nullToString(productOfferIdSet.get(i));
        product.put("PRODUCT_OFFER_ID", productOfferId);
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        String productOrderId = GrpCommonBean.nullToString(productOrderIdSet.get(i));
        product.put("PRODUCT_ORDER_ID", productOrderId);
        productInfo.put("BBOSS_PRODUCT_INFO", product);

        // 14- 判断新增的产品在现有的表中是否存在，如果存在则不需要再次新增，直接退出
        IDataset merchpUserInfoList =UserEcrecepProductInfoQry.qryEcrEceppInfosByPro(productOfferId,null);
        IDataset merchpTradeInfoList =TradeEcrecepProductInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferId,null);
        if(IDataUtil.isNotEmpty(merchpUserInfoList) || IDataUtil.isNotEmpty(merchpTradeInfoList)){
            return;
        }

        // 15- 添加反向受理标记(反向受理不发服务开通)
        productInfo.put("IN_MODE_CODE", "6");

        // 16- 将产品信息添加至产品数据集
        productInfoset.add(productInfo);
    }
    
    protected static void makeJKDTDelProdOpData(int i, IData firstProductInfo, IData map, IDataset productInfoset) throws Exception
    {
    
        // 1- 定义符合基类处理的产品信息
        IData productInfo = new DataMap();

        // 2- 添加产品用户编号
        String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER").get(i).toString());

        productInfo.put("USER_ID", merchpUserId);

        // 3- 添加预约标志(BBOSS侧没有预约)
        productInfo.put("IF_BOOKING", "false");

        // 4- 添加取消订购原因
        productInfo.put("REASON_CODE", "");

        // 5- 添加备注信息
        productInfo.put("REMARK", "");

        // 6- 添加处理标志(商品变更，产品注销)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue());

        // 7- 添加商品信息，子产品修改BB关系时需要用到
        String merchUserId = GrpCommonBean.getJKDTMerchUserIdByProdOffId(map.getString("RSRV_STR2"));
        IData merchOutData = getDelMerchData(merchUserId);
        productInfo.put("OUT_MERCH_INFO", merchOutData);

        productInfo.put("BBOSS_PRODUCT_INFO", merchOutData);

        // 8- 添加反向受理标记(反向受理不发服务开通)
        productInfo.put("IN_MODE_CODE", "6");

        // 9- 添加单条产品至产品数据集
        productInfoset.add(productInfo);
    }
    
    /*
     * @description 获取元素信息
     * @author xunyl
     * @date 2013-06-27
     */
    protected static void getJKDTProductElementInfo(String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 添加必须服务
        IDataset forceSvcs = ProductInfoQry.getProductForceSvc(productId, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(forceSvcs))
        {
            for (int i = 0; i < forceSvcs.size(); i++)
            {
                IData forceSvc = forceSvcs.getData(i);
                // 1-1 拼装服务数据
                IData svcElement = new DataMap();
                getElementInfo(forceSvc, svcElement);
                // 1-2 将拼好的服务元素放入元素集
                elementInfo.add(svcElement);
            }
        }

        // 2- 定义必选元素编号数据集(一级BOSS发送过来的资费与本地获取到的必选资费也许会有重复，一级BOSS发送过来的资费编号放入到该List,后续添加必选资费时校验重复)
        IData elementIdMap = new DataMap();

        // 3- 添加一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("".equals(ratePlanId))
            {
                continue;
            }
            String elementId = GrpCommonBean.merchJKDTToProduct(ratePlanId, 1 ,productId);
            elementIdMap.put(elementId, elementId);
            getProductRateInfo(j, productId, elementId, secondProductInfo, elementInfo);
        }

        // 4- 添加必选资费
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(productId);
        if (IDataUtil.isNotEmpty(forceDiscnts))
        {
            for (int i = 0; i < forceDiscnts.size(); i++)
            {
                IData forceDct = forceDiscnts.getData(i);
                // 4-1 获取资费编号并判断资费编号在必选元素编号集中是否存在，存在说明重复，进行下一次循环
                String dctId = forceDct.getString("ELEMENT_ID");
                if (elementIdMap.containsKey(dctId))
                {
                    continue;
                }
                // 4-2 拼装资费数据
                IData dctElement = new DataMap();
                getElementInfo(forceDct, dctElement);
                // 4-3 将拼好的服务元素放入元素集
                elementInfo.add(dctElement);
            }
        }

    }
    
    // 设置最低消息费生效月(特定商品)
    private static void assembleMinCEffMtoAttr(String proPoNumber,IData map,IData rateInfo) throws Exception{
    	if(!"50018".equals(proPoNumber) && !"50019".equals(proPoNumber)){
    		return;
    	}
        IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", map); // 多条产品规格编号
        IDataset proAttrCodeList = IDataUtil.getDataset("RSRV_STR15", map);// 产品属性代码
        IDataset proAttrValueList = IDataUtil.getDataset("RSRV_STR16", map);// 产品属性值
        
        for (int i = 0; i < productNumberSet.size(); i++)
        {
        	String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
        	
	         if(productSpecNumber.equals("5001801") || productSpecNumber.equals("5001901")){
            	IDataset codeList = IDataUtil.modiIDataset(proAttrCodeList.get(i), "RSRV_STR15");
            	IDataset valueList = IDataUtil.modiIDataset(proAttrValueList.get(i), "RSRV_STR16");
            	boolean isMinAttr = false;
             	for(int n=0; n<codeList.size(); n++){
             		String attrcode = GrpCommonBean.nullToString(codeList.get(n));
             		if(attrcode.equals("50019010008") || attrcode.equals("50018010008")){
             			String attrValue = GrpCommonBean.nullToString(valueList.get(n));
             			if(attrValue.equals("0")){
             				rateInfo.put("START_DATE", SysDateMgr.getSysTime());
             			}else if(attrValue.equals("1")){
             				rateInfo.put("START_DATE", SysDateMgr.firstDayOfMonth(1)+" 00:00:01");
             			}else if(attrValue.equals("2")){
             				rateInfo.put("START_DATE", SysDateMgr.firstDayOfMonth(2)+" 00:00:01");
             			}else if(attrValue.equals("3")){
             				rateInfo.put("START_DATE", SysDateMgr.firstDayOfMonth(3)+" 00:00:01");
             			}
             			isMinAttr = true;
             			break;
             		}
             	}
             	if(isMinAttr){
             		break;
             	}
	         }
        }
    }

}