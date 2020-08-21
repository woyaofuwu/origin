
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class ProductInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /*
     * description 通过集团PRODUCT_ID ELEMENT_ID 查询元素信息
     * @author xunyl
     * @date 2013-10-05
     */
    public static IDataset getElementByProductIdElemId(IData inparam) throws Exception
    {
        String productId = inparam.getString("PRODUCT_ID");
        String elementId = inparam.getString("ELEMENT_ID");
        return ProductInfoQry.getElementByProductIdElemId(productId, elementId);
    }

    /**
     * 根据product_id查询产品信息
     * 
     * @author xunyl
     * @param data
     * @return
     * @throws Exception
     * @date 2013-03-20
     */
    public static IData getProductByPK(IData data) throws Exception
    {
        String productId = data.getString("PRODUCT_ID");
        return UProductInfoQry.qryProductByPK(productId);
    }

    /**
     * 作用： 根据product_id查询TD_B_PRODUCT_COMP中的产品
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset getProductFromComp(IData inData) throws Exception
    {

        IData resultList = ProductInfoQry.getProductFromComp(inData.getString("PRODUCT_ID"));
        IDataset resultLists = new DatasetList();
        resultLists.add(resultList);
        return resultLists;
    }

    public static IData getProductNameByProductId(IData inData) throws Exception
    {
        String proName = UProductInfoQry.getProductNameByProductId(inData.getString("PRODUCT_ID"));
        IData resultList = new DataMap();
        resultList.put("PRODUCT_NAME", proName);
        return resultList;
    }

    /**
     * 作用： 根据user_Id查询用户已订购的产品
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByUserIdForGrp(IData inData) throws Exception
    {

        String user_id = inData.getString("USER_ID");
        IDataset resultLists = ProductInfoQry.getUserProductByUserIdForGrp(user_id);
        return resultLists;
    }

    /**
     * 作用：处理通常及特殊情况下产品信息
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset queryProductListByBrandMode(IData inData) throws Exception
    {
        IDataset resultList = ProductInfoQry.queryProductListByBrandMode(inData);
        return resultList;
    }

    private boolean checkUserIsExistElement(IDataset userElements, String elementTypeCode, String elementId) throws Exception
    {
        boolean flag = false;
        int size = userElements.size();
        String strKey = "";
        if ("Z".equals(elementTypeCode) || "S".equals(elementTypeCode))
        {
            strKey = "SERVICE_ID";
        }
        if ("D".equals(elementTypeCode))
        {
            strKey = "DISCNT_CODE";
        }
        for (int i = 0; i < size; i++)
        {
            IData userElement = userElements.getData(i);
            String elementCode = userElement.getString(strKey);
            if (elementId.equals(elementCode))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * @description 通过品牌、产品模式等来获得主产品相关信息
     * @param inparam
     * @author yish
     * @return
     * @throws Exception
     */
    public IDataset getBindleProductsBrand(IData inparam) throws Exception
    {
        String productMode = inparam.getString("PRODUCT_MODE");
        String brandCode = inparam.getString("BRAND_CODE");
        String releaseTag = inparam.getString("RELEASE_TAG");
        String eparchyCode = inparam.getString("EPARCHY_CODE");
        String productId = inparam.getString("PRODUCT_ID", "");
        String www_capacity = inparam.getString("WWW_CAPACITY", "");

        return ProductInfoQry.getBindleProductsBrand(productMode, brandCode, releaseTag, eparchyCode, productId, www_capacity);
    }

    public IDataset getMebProductForceElements(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchycode = CSBizBean.getUserEparchyCode();
        IDataset resultsetDataset = new DatasetList();
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        if (StringUtils.isBlank(mebProductId))
        {
            return new DatasetList();
        }
        resultsetDataset = UProductElementInfoQry.queryForceElementsByProductId(mebProductId);
        return resultsetDataset;

    }

    public IDataset getPackagesByProductId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE", "");
        IDataset products = ProductInfoQry.getPackagesByProductId(productId, eparchyCode);

        return products;
    }

    public IDataset getPlusProductByProdId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchycode = input.getString("TRADE_EPARCHY_CODE");
        IDataset data = ProductdLimitInfoQry.getPlusProductByProdId(productId, eparchycode);

        return data;
    }

    public IDataset getProductInfoByID(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset data = IDataUtil.idToIds(UProductInfoQry.qryProductByPK(productId));

        return data;
    }

    public IDataset getProductsBySelByPModeBrand(IData input) throws Exception
    {
        String brandCode = input.getString("BRAND_CODE");
        String productMode = input.getString("PRODUCT_MODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        return ProductInfoQry.getProductsBySelByBrandFamily(brandCode, productMode, eparchyCode);
    }

    public IDataset getProductsByTypeForGroup(IData input) throws Exception
    {
        String productTypeCode = input.getString("PRODUCT_TYPE_CODE");
        String eparchyCode = input.getString("TRADE_EPARCHY_CODE");
        IDataset data = ProductInfoQry.getProductsByTypeForGroup(productTypeCode, eparchyCode, null);

        return data;
    }

    /**
     * 作用：根porduct_id查询主产品所需资源信息
     * 
     * @author fengsl
     * @date 2013-03-26
     * @param data
     * @return
     * @throws Exception
     */

    public IDataset getResTypeByMainProduct(IData data) throws Exception
    {
        String productId = data.getString("PRODUCT_ID");
        return ProductInfoQry.getResTypeByMainProduct(productId);
    }

    public IDataset getrsrvstr2(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset data = ProductInfoQry.getrsrvstr2(productId);

        return data;
    }

    public IDataset getUserProductElement(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String userIdA = data.getString("USER_ID_A");

        IData productData = new DataMap();
        IData packageData = new DataMap();
        IDataset dataset = ProductInfoQry.qryUserProductElement(userId, userIdA);
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData map = dataset.getData(i);
            if (StringUtils.equals(map.getString("ELEMENT_TYPE_CODE"), "R"))
            {
                map.put("PRODUCT_NAME", "");
                map.put("PACKAGE_NAME", "");
                continue;
            }
            
            String productId = map.getString("PRODUCT_ID");
            String packageId = map.getString("PACKAGE_ID");
            if (StringUtils.isBlank(productId))
            {
                map.put("PRODUCT_ID", "");
                map.put("PRODUCT_NAME", "");
                map.put("PACKAGE_ID", "");
                map.put("PACKAGE_NAME", "");
                continue;
            }
            
            String productName = productData.getString(productId);
            if (StringUtils.isBlank(productName))
            {
                productName = UProductInfoQry.getProductNameByProductId(productId);
                productData.put(productId, productName);
            }
            map.put("PRODUCT_NAME", productName);
            
            
            if (StringUtils.isBlank(packageId))
            {
                IData packageInfoData = UProductElementInfoQry.queryElementInfoByProductIdAndElementIdElemetnTypeCode(productId, map.getString("ELEMENT_ID"), map.getString("ELEMENT_TYPE_CODE"));
                if (IDataUtil.isNotEmpty(packageInfoData))
                {
                    map.put("PACKAGE_ID", packageInfoData.getString("PACKAGE_ID"));
                    packageId = packageInfoData.getString("PACKAGE_ID");
                }
                else
                {
                    map.put("PACKAGE_NAME", "");
                    continue;
                }
            }
            if(StringUtils.isBlank(packageId) || packageId.equals("-1") || packageId.equals("0"))
            {
                map.put("PACKAGE_NAME", "");
                continue;
            }
            
            String packageName = packageData.getString(packageId);
            if (StringUtils.isBlank(packageName))
            {
                packageName = UPackageInfoQry.getNameByPackageId(packageId);
                packageData.put(packageId, packageName);
            }
            map.put("PACKAGE_NAME", packageName);
        }

        return dataset;
    }

    /**
     * 根据product_mode查询产品
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset qryProductsByProductMode(IData iData) throws Exception
    {
        String productMode = iData.getString("PRODUCT_MODE");
        
        return ProductInfoQry.getWidenetProductInfo(productMode, "0898");
    }
    
    public IDataset qrySaleActiveProductInfo(IData iData) throws Exception
    {     
        return ProductInfoQry.qrySaleActiveProductInfo();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:59:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryByPkgId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        return ProductInfoQry.queryByPkgId(packageId);
    }

    public IData queryContractInfoById(IData data) throws Exception
    {
        IData rtData = new DataMap();
        IData contractInfo = new DataMap();
        String productId = data.getString("PRODUCT_ID");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String deviceModelCode = data.getString("DEVICE_MODEL_CODE");
        String userId = data.getString("USER_ID");
        IData saleactivePkgInfo = new DataMap();

        // 先把用户所有有效的服务、优惠、平台业务查出来，方便后面匹配时不用多次连数据库查
        IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(userId, eparchyCode);
        IDataset userSvcs = BofQuery.queryUserAllSvc(userId, eparchyCode);
        IDataset userPlatsvcs = BofQuery.queryUserAllPlatSvc(userId, eparchyCode);

        // 查询合约计划下的组
        IDataset saleGroups = new DatasetList();
        IDataset tempSaleGroups = ProductPkgInfoQry.queryPackagesByProductId(productId);
        IData voicePkgInfo = new DataMap();
        int size = tempSaleGroups.size();
        for (int i = 0; i < size; i++)
        {
            IData tempSaleGroup = tempSaleGroups.getData(i);
            String packageId = tempSaleGroup.getString("PACKAGE_ID");
            IDataset elems = PkgElemInfoQry.getPackageElementByPackageId(packageId);
            int elemSize = elems.size();
            if (elemSize == 0)
            {
                continue;
            }
            IData packageInfo = PkgInfoQry.getPackageByPK(packageId);
            String packageTypeCode = packageInfo.getString("PACKAGE_TYPE_CODE");
            if ("4".equals(packageTypeCode) || "Y".equals(packageTypeCode))
            {
                if ("4".equals(packageTypeCode))
                {
                    saleactivePkgInfo = packageInfo;
                }
                else if ("Y".equals(packageTypeCode))
                {
                    voicePkgInfo = packageInfo;
                }
                continue;
            }
            tempSaleGroup.putAll(packageInfo);

            IDataset saleElements = new DatasetList();
            for (int j = 0; j < elemSize; j++)
            {
                IData elem = elems.getData(j);
                String elementTypeCode = elem.getString("ELEMENT_TYPE_CODE");
                String elementId = elem.getString("ELEMENT_ID");
                if ("D".equals(elementTypeCode))
                {
                    IData discnt = UDiscntInfoQry.getDiscntInfoByPk(elementId);
                    if (IDataUtil.isEmpty(discnt))
                    {
                        continue;
                    }
                    if (!"ZZZZ".equals(discnt.getString("EPARCHY_CODE")) && !eparchyCode.equals(discnt.getString("EPARCHY_CODE")))
                    {
                        continue;
                    }

                    elem.put("ELEMENT_NAME", discnt.getString("DISCNT_NAME"));
                    elem.put("PRICE", discnt.getString("PRICE", "0"));
                    boolean flag = checkUserIsExistElement(userDiscnts, "D", elem.getString("ELEMENT_ID"));
                    if (flag)
                    {
                        elem.put("EXIST", "TRUE");
                    }
                }
                else if ("Z".equals(elementTypeCode))
                {
                    IDataset spBizs = PlatSvcInfoQry.querySpBizBySvcId(elementId);
                    if (IDataUtil.isEmpty(spBizs))
                    {
                        continue;
                    }
                    int price = spBizs.getData(0).getInt("PRICE") / 10;// 除以10是因为平台业务存的是厘
                    elem.put("ELEMENT_NAME", spBizs.getData(0).getString("SERVICE_NAME"));
                    elem.put("PRICE", String.valueOf(price));
                    boolean flag = checkUserIsExistElement(userPlatsvcs, "Z", elem.getString("ELEMENT_ID"));
                    if (flag)
                    {
                        elem.put("EXIST", "TRUE");
                    }
                }
                else if ("S".equals(elementTypeCode))
                {
                    IData svc = USvcInfoQry.qryServInfoBySvcId(elementId);
                    if (IDataUtil.isEmpty(svc))
                    {
                        continue;
                    }
                    elem.put("ELEMENT_NAME", svc.getString("SERVICE_NAME"));
                    elem.put("PRICE", svc.getString("PRICE", "0"));
                    boolean flag = checkUserIsExistElement(userSvcs, "S", elem.getString("ELEMENT_ID"));
                    if (flag)
                    {
                        elem.put("EXIST", "TRUE");
                    }
                }
                else if ("P".equals(elementTypeCode))
                {
                    // 这里要改
                    IData product = UProductInfoQry.qryProductByPK(elementId);
                    elem.put("ELEMENT_NAME", product.getString("PRODUCT_NAME"));
                    elem.put("PRICE", product.getString("PRICE", "0"));
                    boolean flag = checkUserIsExistElement(userDiscnts, "D", elem.getString("ELEMENT_ID"));
                    if (flag)
                    {
                        elem.put("EXIST", "TRUE");
                    }
                }
                saleElements.add(elem);
            }
            if (saleElements.size() > 0)
            {
                DataHelper.sort(saleElements, "PRICE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
                tempSaleGroup.put("SALE_ELEMENTS", saleElements);
                saleGroups.add(tempSaleGroup);
            }
        }
        rtData.put("SALE_GROUP", saleGroups);

        // 处理语音包
        if (IDataUtil.isNotEmpty(voicePkgInfo))
        {
            String packageId = voicePkgInfo.getString("PACKAGE_ID");
            IDataset elems = PkgElemInfoQry.getPackageElementByPackageId(packageId);
            IDataset voices = new DatasetList();
            IData tmpProductData = new DataMap();
            size = elems.size();
            for (int i = 0; i < size; i++)
            {
                IData elem = elems.getData(i);
                String elementTypeCode = elem.getString("ELEMENT_TYPE_CODE");
                String elementId = elem.getString("ELEMENT_ID");
                if ("D".equals(elementTypeCode))
                {
                    String voiceProductId = elem.getString("RSRV_STR1");

                    IData discnt = UDiscntInfoQry.getDiscntInfoByPk(elementId);
                    if (IDataUtil.isEmpty(discnt))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到优惠信息");
                    }

                    if (!"ZZZZ".equals(discnt.getString("EPARCHY_CODE")) && !eparchyCode.equals(discnt.getString("EPARCHY_CODE")))
                    {
                        continue;
                    }
                    elem.put("ELEMENT_NAME", discnt.getString("DISCNT_NAME"));
                    elem.put("PRICE", discnt.getString("PRICE", "0"));
                    boolean flag = checkUserIsExistElement(userDiscnts, "D", elem.getString("ELEMENT_ID"));
                    if (flag)
                    {
                        elem.put("EXIST", "TRUE");
                    }

                    if (tmpProductData.containsKey(voiceProductId))
                    {
                        IDataset voiceDiscnts = tmpProductData.getDataset(voiceProductId);
                        voiceDiscnts.add(elem);
                    }
                    else
                    {
                        IDataset voiceDiscnts = new DatasetList();
                        voiceDiscnts.add(elem);
                        tmpProductData.put(voiceProductId, voiceDiscnts);
                    }
                }
            }
            String[] keys = tmpProductData.getNames();
            size = keys.length;
            for (int i = 0; i < size; i++)
            {
                String voiceProductId = keys[i];
                IData voice = new DataMap();
                IDataset voiceDiscnts = tmpProductData.getDataset(voiceProductId);
                DataHelper.sort(voiceDiscnts, "PRICE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
                voice.put("VOICE_DISCNTS", voiceDiscnts);
                IData product = UProductInfoQry.qryProductByPK(voiceProductId);
                if (IDataUtil.isEmpty(product))
                {
                    // 报错
                }
                voice.put("PRODUCT_ID", voiceProductId);
                voice.put("PRODUCT_NAME", product.getString("PRODUCT_NAME"));
                voices.add(voice);
            }
            rtData.put("VOICES", voices);
        }

        // 查询捆绑时长
        IDataset contractMonths = AttrItemInfoQry.getItembByIdAndType(productId, "P", "CONTRACT_MONTHS", eparchyCode);
        rtData.put("CONTRACT_MONTHS", contractMonths);

        // 查询折扣列表
        IDataset contractDiscountsList = AttrItemInfoQry.getItembByIdAndType(productId, "P", "CONTRACT_DISCOUNTS", eparchyCode);
        // IDataset contractDiscounts = new DatasetList();
        DataHelper.sort(contractDiscountsList, "ATTR_FIELD_CODE", IDataset.TYPE_DOUBLE, IDataset.ORDER_ASCEND);
        if (IDataUtil.isNotEmpty(contractDiscountsList))
        {
            for (int i = 0, contractDiscountsSize = contractDiscountsList.size(); i < contractDiscountsSize; i++)
            {
                IData contractDiscount = contractDiscountsList.getData(i);
                String ruleBizIds = contractDiscount.getString("RSRV_STR1");
                String discount = contractDiscount.getString("ATTR_FIELD_CODE");
                if (StringUtils.isBlank(ruleBizIds))
                {
                    contractInfo.put("DISCOUNT", discount);
                    break;
                }
                // 判规则过滤
                IData user = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
                IData ruleParam = new DataMap();
                ruleParam.put("BRAND_CODE", user.getString("BRAND_CODE"));
                ruleParam.put("PRODUCT_ID", user.getString("PRODUCT_ID"));

                ruleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckBefore");
                ruleParam.put("RULE_BIZ_KIND_CODE", "ContractSaleSpecCheck");
                ruleParam.put("USER_ID", user.getString("USER_ID"));
                ruleParam.put("TRADE_TYPE_CODE", "240");
                ruleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                ruleParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                ruleParam.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));

                ruleParam.put("CAMPN_TYPE", "ct001");
                ruleParam.put("PRODUCT_ID_A", productId);
                // ruleParam.put("PACKAGE_ID_A", input.getString("PACKAGE_ID"));
                ruleParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                ruleParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                ruleParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                ruleParam.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                // ruleParam.put("RSRV_STR1", input.getString("PRODUCT_ID"));
                // ruleParam.put("RSRV_STR2", input.getString("PACKAGE_ID"));
                // ruleParam.put("RSRV_STR9", input.getString("CAMPN_TYPE"));
                // ruleParam.put("ACCT_ID", uca.getAcctId());
                // ruleParam.put("OPEN_DATE", uca.getUser().getOpenDate());
                ruleParam.put("TIPS_TYPE", "0|4");
                ruleParam.put("ORDER_TYPE_CODE", "210");
                ruleParam.put("DISCOUNT", discount);
                // ruleParam.put("STU_MOBILPHONE", input.getString("STU_MOBILPHONE", ""));

                // 加入RULE_BIZ_ID
                IDataset ruleBizIdList = new DatasetList();
                String[] arrRuleBizIds = ruleBizIds.split("\\|");
                for (String ruleBizId : arrRuleBizIds)
                {
                    IData ruleBizIdData = new DataMap();
                    ruleBizIdData.put("RULE_BIZ_ID", ruleBizId);
                    ruleBizIdList.add(ruleBizIdData);
                }
                ruleParam.put("LIST_BIZ_ID", ruleBizIdList);

                IData ruleResult = BizRule.bre4SuperLimit(ruleParam);
                if (IDataUtil.isEmpty(ruleResult.getDataset("TIPS_TYPE_ERROR")))
                {
                    // 如果规则都通过则判断下一个
                    contractInfo.put("DISCOUNT", discount);
                    break;
                }
            }
        }
        // rtData.put("CONTRACT_DISCOUNTS", contractDiscounts);

        // 查询合约信息开始
        String saleactivePkgId = saleactivePkgInfo.getString("PACKAGE_ID");
        IDataset saleDeposits = SaleDepositInfoQry.querySaleDepositByPackageId(saleactivePkgId);
        if (IDataUtil.isEmpty(saleDeposits))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有找到A元素");
            // 报错
        }
        IData saleDeposit = saleDeposits.getData(0);

        contractInfo.put("DISCNT_GIFT_ID", saleDeposit.getString("DISCNT_GIFT_ID"));

        // IData userCrditInfos = CreditCall.queryUserCreditInfos(userId);
        // contractInfo.put("CREDIT_CLASS", userCrditInfos.getString("userCrditInfos"));
        contractInfo.put("CREDIT_CLASS", "1");

        IDataset params = ProductInfoQry.queryContractInfoByIdAndResType(deviceModelCode, productId, eparchyCode);
        if (IDataUtil.isEmpty(params))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_220);
            // 报错
        }
        IData param = params.getData(0);
        contractInfo.put("NET_PRICE", param.getString("NET_PRICE"));
        contractInfo.put("CONTRACT_PRICE", param.getString("CONTRACT_PRICE"));
        contractInfo.put("DISCT_RULE_CODE", param.getString("DISCT_RULE_CODE"));
        contractInfo.put("RETURN_RATE", param.getString("RETURN_RATE"));
        contractInfo.put("SUBSIDY_RATE", param.getString("SUBSIDY_RATE"));
        contractInfo.put("SALEACTIVE_PACKAGE_ID", saleactivePkgId);

        // 查询灵活返款
        IDataset customReturnRateConfig = AttrItemInfoQry.getItembByIdAndType(productId, "P", "CUSTOM_RETURNRATE", eparchyCode);
        contractInfo.put("CUSTOM_RETURNRATE_CONFIG", customReturnRateConfig);

        rtData.put("CONTRACT_INFO", contractInfo);

        String campnType = LabelInfoQry.getLogicLabelIdByElementId(productId);
        contractInfo.put("CAMPN_TYPE", campnType);// 查询活动类型

        // 查最低消费档次
        IDataset discnts = DiscntInfoQry.queryDiscntsByPkgId(saleactivePkgId);
        if (IDataUtil.isEmpty(discnts))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有找到最低消费元素");
            // 报错
        }
        IData discnt = discnts.getData(0);
        IDataset consumeLevels = AttrItemInfoQry.getItembByIdAndType(discnt.getString("DISCNT_CODE"), "D", "MONEY", eparchyCode);
        DataHelper.sort(consumeLevels, "PRICE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        rtData.put("CONSUME_LEVELS", consumeLevels);
        // 重新构造
        IDataset consumeElements = new DatasetList();
        for (int i = consumeLevels.size() - 1; i >= 0; i--)
        {
            IData consumeLevel = consumeLevels.getData(i);
            IData consumeElement = new DataMap();
            consumeElement.put("ELEMENT_NAME", consumeLevel.getString("ATTR_FIELD_NAME"));
            consumeElement.put("PRICE", consumeLevel.getString("ATTR_FIELD_CODE"));
            consumeElements.add(consumeElement);
        }
        DataHelper.sort(consumeElements, "PRICE", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
        rtData.put("CONSUME_ELEMENTS", consumeElements);

        return rtData;
    }

    public IDataset queryContractSaleByResType(IData data) throws Exception
    {
        String resType = data.getString("RES_TYPE");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String productTypeCode = data.getString("PRODUCT_TYPE_CODE");
        IDataset result = ProductInfoQry.queryContractSaleByResType(resType, eparchyCode);
        IDataset contractSaleList = new DatasetList();
        for (int i = result.size() - 1; i >= 0; i--)
        {
            IData productInfo = result.getData(i);
            String privCode = productInfo.getString("RSRV_STR2");
            if (StringUtils.isNotBlank(privCode))
            {
                boolean flag = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), privCode);
                if (flag)
                {
                    contractSaleList.add(productInfo);
                }
            }
            else
            {
                contractSaleList.add(productInfo);
            }
        }

        return contractSaleList;
    }

    public IDataset queryProductByComp(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String relation_type_code = input.getString("RELATION_TYPE_CODE");
        String force_tag = input.getString("FORCE_TAG");
        IDataset data = ProductInfoQry.queryProductByComp(productId, relation_type_code, force_tag);

        return data;
    }

    public IDataset querySaleActiveProduct(IData input) throws Exception
    {
        String campnType = input.getString("RSRV_STR2");
        String eparchyCode = input.getString("EPARCHY_CODE", "");
        IDataset products = ProductInfoQry.querySaleActiveProduct(eparchyCode, campnType);

        return products;
    }
    
    public IDataset getSaleActivePackageByProduct(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset products = ProductInfoQry.getSaleActivePackageByProduct(productId);

        return products;
    }
}
