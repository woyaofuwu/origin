
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化
 * @author: xiaocl
 */
public class CheckElementLimitByDelete implements IElementLimitByDelete
{

    private static Logger logger = Logger.getLogger(CheckElementLimitByDelete.class);

    @Override
    public void checkAllElementLimitDelete(IData databus, IDataset listTradeElement, IDataset listUserAllElement, boolean bIsPkgInsideElmentLimit, CheckProductData checkProductData) throws Exception
    {

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 进入 prodcheck CheckElementLimitByDelete() 函数 listTradeElement.size = [" + listTradeElement.size() + "]");

        String strElementType = "", strKeyId = "", strKeyName = "";

        int irlCount = 0;

        String strtpModifyTag = "", strtpElementId = "", strtpStartDate = "", strtpEndDate = "", strtpProductId = "", strtpPackageId = "", strtpElementTypeCode = "";
        String strueModifyTag = "", strueElementId = "", strueStartDate = "", strueEndDate = "", strueElementTypeCode = "", strueProductId = "", struePackageId = "";
        String strrlElementId = "", strrlElementTypeCode = "";

        boolean bCurMonth = false, bNextMonth = false;

        IDataset listEvERelationLimit;

        int iCountTradeElement = listTradeElement.size();
        for (int itpIdx = 0; itpIdx < iCountTradeElement; itpIdx++)
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug("rule prodcheck CheckElementLimitByDelete 进入到第一层循环");

            IData tradeElement = listTradeElement.getData(itpIdx);
            strtpModifyTag = tradeElement.getString("MODIFY_TAG");
            strtpElementId = tradeElement.getString("ELEMENT_ID");
            strtpStartDate = tradeElement.getString("START_DATE");
            strtpEndDate = tradeElement.getString("END_DATE");
            strtpProductId = tradeElement.getString("PRODUCT_ID", "");
            strtpPackageId = tradeElement.getString("PACKAGE_ID", "");
            strtpElementTypeCode = tradeElement.getString("ELEMENT_TYPE_CODE");

            if (strtpElementTypeCode.equals("S"))
            {
                strElementType = "服务";
                strKeyName = "ServiceName";
                strKeyId = "SERVICE_ID";
            }
            else if (strtpElementTypeCode.equals("D"))
            {
                strElementType = "优惠";
                strKeyName = "DiscntName";
                strKeyId = "DISCNT_CODE";
            }

            if (!listTradeElement.getData(itpIdx).getString("USER_ID").equals(checkProductData.getUserId()))
            {
                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    logger.debug("rule prodcheck CheckElementLimitByDelete 服务表 user_id 和 台账表user_id 不相同, continue 跳过第一层循环");

                continue;
            }

            bCurMonth = (strtpStartDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) <= 0); // 是否需要判断当月
            bNextMonth = (strtpEndDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) > 0); // 是否需要判断下月

            // 2.1 被完全依赖
            listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "2", strtpElementTypeCode, "B", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
            if (IDataUtil.isNotEmpty(listEvERelationLimit))
            {
                irlCount = listEvERelationLimit.size();

                for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
                {
                    IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
                    strrlElementId = eveRelationLimit.getString("ELEMENT_ID_A");
                    strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_A");

                    int iCountUserAllElement = listUserAllElement.size();
                    for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
                    {
                        IData userAllElement = listUserAllElement.getData(iueIdx);
                        strueModifyTag = userAllElement.getString("MODIFY_TAG");
                        // modify by wangzq2 listUserAllElement没有DISCNT_CODE
                        strueElementId = userAllElement.getString("ELEMENT_ID");
                        strueStartDate = userAllElement.getString("START_DATE");
                        strueEndDate = userAllElement.getString("END_DATE");
                        struePackageId = userAllElement.getString("PACKAGE_ID", "");
                        strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
                        struePackageId = userAllElement.getString("PACKAGE_ID", "");

                        // modify by wangzq2 如果依赖的优惠本月底结束则算过去
                        if (strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && strrlElementTypeCode.equals("D") && strueEndDate.compareTo(checkProductData.getStrFirstDayOfNextMonth()) < 0)
                        {
                            continue;
                        }
                        // modify by wangzq2 10.24 删除B时，只有当被依赖的A结束时间大于B的结束时间时才判断，而不是大于（等于）
                        if (strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && !"1".equals(strueModifyTag) && strueEndDate.compareTo(strtpEndDate) > 0)
                        {
                            String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
                            String strName = "", strKeyColumn = "", strNameB = "";

                            if (strrlElementTypeCode.equals("S"))
                            {
                                strName = "服务";
                                strKeyColumn = "ServiceName";
                            }
                            else if (strrlElementTypeCode.equals("D"))
                            {
                                strName = "优惠";
                                strKeyColumn = "DiscntName";
                            }
                            strNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

                            String strError = null;

                            strError = "#产品依赖互斥判断:|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能删除, 因为它被用户|" + strName + "|" + strrlElementId + "|" + strNameB + "|所依赖，业务不能继续办理！";

                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201509", strError);
                        }
                    }
                }
            }

            // 2.2 被部分依赖
            listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "1", strtpElementTypeCode, "B", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);

            if (IDataUtil.isNotEmpty(listEvERelationLimit))
            {
                irlCount = listEvERelationLimit.size();

                for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
                {
                    IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
                    strrlElementId = eveRelationLimit.getString("ELEMENT_ID_A");
                    strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_A");

                    int iCountUserAllElement = listUserAllElement.size();
                    for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
                    {
                        IData userAllElement = listUserAllElement.getData(iueIdx);
                        strueModifyTag = userAllElement.getString("MODIFY_TAG");
                        strueElementId = userAllElement.getString("ELEMENT_ID");
                        strueStartDate = userAllElement.getString("START_DATE");
                        strueEndDate = userAllElement.getString("END_DATE");
                        strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
                        struePackageId = userAllElement.getString("PACKAGE_ID", "");

                        // modify by wangzq2 如果依赖的优惠本月底结束则算过去
                        if (strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && strrlElementTypeCode.equals("D") && strueEndDate.compareTo(checkProductData.getStrFirstDayOfNextMonth()) < 0)
                        {
                            continue;
                        }

                        // modify by wangzq2 10.24 删除B时，只有当被依赖的A结束时间大于B的结束时间时才判断，而不是大于（等于）
                        if (!"1".equals(strueModifyTag) && strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && strueEndDate.compareTo(strtpEndDate) > 0)
                        {
                            // 被部分依赖用到,重新定义B变量
                            int irlCountB = 0;
                            IDataset listEvERelationLimitB;
                            String strueModifyTagB = "", strueElementIdB = "", strueStartDateB = "", strueEndDateB = "", strueElementTypeCodeB = "", struePackageIdB = "";
                            String strrlElementIdB = "", strrlElementTypeCodeB = "";

                            listEvERelationLimitB = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strueElementId, "1", strueElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
                            if (IDataUtil.isNotEmpty(listEvERelationLimitB))
                            {
                                irlCountB = listEvERelationLimitB.size();

                                boolean bfindedB = false;

                                int iCountUserAllElementB = listUserAllElement.size();
                                for (int iueIdxB = 0; iueIdxB < iCountUserAllElementB; iueIdxB++)
                                {
                                    IData userAllElementB = listUserAllElement.getData(iueIdxB);
                                    strueModifyTagB = userAllElementB.getString("MODIFY_TAG", "");
                                    strueElementIdB = userAllElementB.getString("ELEMENT_ID", "");
                                    strueStartDateB = userAllElementB.getString("START_DATE", "");
                                    strueEndDateB = userAllElementB.getString("END_DATE", "");
                                    struePackageIdB = userAllElementB.getString("PACKAGE_ID", "");
                                    strueElementTypeCodeB = userAllElementB.getString("ELEMENT_TYPE_CODE", "");
                                    struePackageIdB = userAllElementB.getString("PACKAGE_ID", "");

                                    for (int irlIdxB = 0; irlIdxB < irlCountB; irlIdxB++)
                                    {
                                        IData eveRelationLimitB = listEvERelationLimitB.getData(irlIdxB);
                                        strrlElementIdB = eveRelationLimitB.getString("ELEMENT_ID_B");
                                        strrlElementTypeCodeB = eveRelationLimitB.getString("ELEMENT_TYPE_CODE_B");

                                        // modify by wangzq2 修改成取A的结束时间
                                        if (!strueModifyTagB.equals("1") && strueElementTypeCodeB.equals(strrlElementTypeCodeB) && strueElementIdB.equals(strrlElementIdB) && strueEndDateB.compareTo(strueEndDate) >= 0)
                                        {
                                            bfindedB = true;
                                            break;
                                        }
                                    }

                                    if (bfindedB)
                                    {
                                        break;
                                    }
                                }

                                if (!bfindedB)
                                {
                                    String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
                                    String strName = "", strKeyColumn = "", strNameB = "";

                                    if (strrlElementTypeCode.equals("S"))
                                    {
                                        strName = "服务";
                                        strKeyColumn = "ServiceName";
                                    }
                                    else if (strrlElementTypeCode.equals("D"))
                                    {
                                        strName = "优惠";
                                        strKeyColumn = "DiscntName";
                                    }
                                    strNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

                                    String strError = null;

                                    strError = "#产品依赖互斥判断:|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能删除, 因为它被用户的|" + strName + "|" + strrlElementId + "|" + strNameB + "|所依赖， 业务不能继续办理！";

                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201510", strError);
                                }
                            }
                        }
                    }
                }
            }
            // 3.2 被部分依赖@yanwu
            listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "3", strtpElementTypeCode, "B", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
            if (IDataUtil.isNotEmpty(listEvERelationLimit))
            {
                irlCount = listEvERelationLimit.size();
                for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
                {
                    IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
                    strrlElementId = eveRelationLimit.getString("ELEMENT_ID_A");
                    strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_A");

                    int iCountUserAllElement = listUserAllElement.size();
                    for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
                    {
                        IData userAllElement = listUserAllElement.getData(iueIdx);
                        strueModifyTag = userAllElement.getString("MODIFY_TAG");
                        strueElementId = userAllElement.getString("ELEMENT_ID");
                        strueStartDate = userAllElement.getString("START_DATE");
                        strueEndDate = userAllElement.getString("END_DATE");
                        strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
                        struePackageId = userAllElement.getString("PACKAGE_ID", "");

                        // modify by wangzq2 如果依赖的优惠本月底结束则算过去
                        if (strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && strrlElementTypeCode.equals("D") && strueEndDate.compareTo(checkProductData.getStrFirstDayOfNextMonth()) < 0)
                        {
                            continue;
                        }

                        // modify by wangzq2 10.24 删除B时，只有当被依赖的A结束时间大于B的结束时间时才判断，而不是大于（等于）
                        if (!"1".equals(strueModifyTag) && strueElementTypeCode.equals(strrlElementTypeCode) && strueElementId.equals(strrlElementId) && strueEndDate.compareTo(strtpEndDate) > 0)
                        {
                            // 被部分依赖用到,重新定义B变量
                            int irlCountB = 0;
                            IDataset listEvERelationLimitB;
                            String strueModifyTagB = "", strueElementIdB = "", strueStartDateB = "", strueEndDateB = "", strueElementTypeCodeB = "", struePackageIdB = "";
                            String strrlElementIdB = "", strrlElementTypeCodeB = "";

                            listEvERelationLimitB = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strueElementId, "3", strueElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
                            if (IDataUtil.isNotEmpty(listEvERelationLimitB))
                            {
                                irlCountB = listEvERelationLimitB.size();
                                boolean bfindedB = false;

                                int iCountUserAllElementB = listUserAllElement.size();
                                for (int iueIdxB = 0; iueIdxB < iCountUserAllElementB; iueIdxB++)
                                {
                                    IData userAllElementB = listUserAllElement.getData(iueIdxB);
                                    strueModifyTagB = userAllElementB.getString("MODIFY_TAG", "");
                                    strueElementIdB = userAllElementB.getString("ELEMENT_ID", "");
                                    strueStartDateB = userAllElementB.getString("START_DATE", "");
                                    strueEndDateB = userAllElementB.getString("END_DATE", "");
                                    struePackageIdB = userAllElementB.getString("PACKAGE_ID", "");
                                    strueElementTypeCodeB = userAllElementB.getString("ELEMENT_TYPE_CODE", "");
                                    struePackageIdB = userAllElementB.getString("PACKAGE_ID", "");

                                    for (int irlIdxB = 0; irlIdxB < irlCountB; irlIdxB++)
                                    {
                                        IData eveRelationLimitB = listEvERelationLimitB.getData(irlIdxB);
                                        strrlElementIdB = eveRelationLimitB.getString("ELEMENT_ID_B");
                                        strrlElementTypeCodeB = eveRelationLimitB.getString("ELEMENT_TYPE_CODE_B");

                                        // modify by wangzq2 修改成取A的结束时间
                                        if (!strueModifyTagB.equals("1") && strueElementTypeCodeB.equals(strrlElementTypeCodeB) && strueElementIdB.equals(strrlElementIdB) && strueEndDateB.compareTo(strueEndDate) >= 0)
                                        {
                                            bfindedB = true;
                                            break;
                                        }
                                    }

                                    if (bfindedB)
                                    {
                                        break;
                                    }
                                }

                                if (!bfindedB)
                                {
                                    String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
                                    String strName = "", strKeyColumn = "", strNameB = "";

                                    if (strrlElementTypeCode.equals("S"))
                                    {
                                        strName = "服务";
                                        strKeyColumn = "ServiceName";
                                    }
                                    else if (strrlElementTypeCode.equals("D"))
                                    {
                                        strName = "优惠";
                                        strKeyColumn = "DiscntName";
                                    }
                                    strNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

                                    String strError = null;

                                    strError = "#产品依赖互斥判断:|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能删除, 因为它被用户的|" + strName + "|" + strrlElementId + "|" + strNameB + "|所依赖， 业务不能继续办理！";

                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201510", strError);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
