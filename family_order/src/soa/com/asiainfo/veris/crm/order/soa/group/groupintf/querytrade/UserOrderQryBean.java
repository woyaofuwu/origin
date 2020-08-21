
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UElementLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class UserOrderQryBean
{

    /**
     * 拼串
     * 
     * @param returnData
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IData getSpellData(IData returnData, IData inParam) throws Exception
    {
        String groupId = inParam.getString("GROUP_ID", "");
        String productId = inParam.getString("PRODUCT_ID", "");

        returnData.put("GROUP_ID", groupId);
        returnData.put("PRODUCT_ID", productId);

        IDataset userProductList = inParam.getDataset("USER_PRODUCT_INFOS");

        if (IDataUtil.isNotEmpty(userProductList))
        {
            String selUserId = "";
            IDataset userProductInfoUserIdset = new DatasetList();

            IDataset svcIdList = new DatasetList();// 服务ID
            IDataset svcNameList = new DatasetList();// 服务NAME
            IDataset svcAttrNameList = new DatasetList();// 服务参数NAME
            IDataset svcAttrCodeList = new DatasetList();// 服务参数CODE
            IDataset svcAttrValueList = new DatasetList();// 服务参数VALUE

            IDataset discntIdList = new DatasetList();// 资费ID
            IDataset discntNameList = new DatasetList();// 资费NAME
            IDataset discntAttrNameList = new DatasetList();// 资费参数NAME
            IDataset discntAttrCodeList = new DatasetList();// 资费参数CODE
            IDataset discntAttrValueList = new DatasetList();// 资费参数VALUE

            for (int i = 0; i < userProductList.size(); i++)
            {
                IData userProductData = userProductList.getData(i);
                String userId = userProductData.getString("USER_ID", "");

                if (i == 0)
                {
                    selUserId = userId;
                }
                else
                {
                    selUserId = selUserId + "," + userId;
                }

                IData productInfo = userProductData.getData("PRODUCT_INFOS");

                IDataset selSvcIdList = new DatasetList();
                IDataset selSvcNameList = new DatasetList();
                IDataset selSvcAttrNameList = new DatasetList();
                IDataset selSvcAttrCodeList = new DatasetList();
                IDataset selSvcAttrValueList = new DatasetList();

                IDataset selDiscntIdList = new DatasetList();
                IDataset selDiscntNameList = new DatasetList();
                IDataset selDiscntAttrNameList = new DatasetList();
                IDataset selDiscntAttrCodeList = new DatasetList();
                IDataset selDiscntAttrValueList = new DatasetList();

                /** 拆分SERV数据开始 */
                IDataset svcInfoList = productInfo.getDataset("SERV");

                if (IDataUtil.isNotEmpty(svcInfoList))
                {
                    for (int m = 0; m < svcInfoList.size(); m++)
                    {
                        IData svcData = svcInfoList.getData(m);
                        String svcId = svcData.getString("ELEMENT_ID", "");
                        String svcName = svcData.getString("ELEMENT_NAME", "");
                        IDataset attrCodeList = new DatasetList();
                        IDataset attrValueList = new DatasetList();
                        IDataset attrNameList = new DatasetList();

                        selSvcIdList.add(svcId);
                        selSvcNameList.add(svcName);

                        /** 拆分SERV_PARAM数据开始 */
                        IDataset svcParamList = svcData.getDataset("SERV_PARAM");
                        if (IDataUtil.isNotEmpty(svcParamList))
                        {
                            for (int j = 0; j < svcParamList.size(); j++)
                            {
                                IData servParam = svcParamList.getData(j);
                                String attrCode = servParam.getString("ATTR_CODE", "");
                                String attrValue = servParam.getString("ATTR_VALUE", "");
                                String attrName = servParam.getString("ATTR_NAME", "");

                                attrCodeList.add(attrCode);
                                attrValueList.add(attrValue);
                                attrNameList.add(attrName);
                            }
                        }
                        selSvcAttrNameList.add(attrNameList);
                        selSvcAttrCodeList.add(attrCodeList);
                        selSvcAttrValueList.add(attrValueList);
                        /** 拆分SERV_PARAM数据结束 */
                    }

                }

                svcIdList.add(selSvcIdList);
                svcNameList.add(selSvcNameList);
                svcAttrNameList.add(selSvcAttrNameList);
                svcAttrCodeList.add(selSvcAttrCodeList);
                svcAttrValueList.add(selSvcAttrValueList);
                /** 拆分SERV数据结束 */

                /** 拆分DISCNT数据开始 */
                IDataset discntInfoList = productInfo.getDataset("DISCNT");

                if (IDataUtil.isNotEmpty(discntInfoList))
                {
                    for (int n = 0; n < discntInfoList.size(); n++)
                    {
                        IData discntData = discntInfoList.getData(n);
                        String discntId = discntData.getString("ELEMENT_ID", "");
                        String discntName = discntData.getString("ELEMENT_NAME", "");

                        selDiscntIdList.add(discntId);
                        selDiscntNameList.add(discntName);

                        IDataset attrNameList = new DatasetList();
                        IDataset attrCodeList = new DatasetList();
                        IDataset attrValueList = new DatasetList();

                        /** 拆分DISCNT_PARAM数据开始 */
                        IDataset discntParamList = discntData.getDataset("DISCNT_PARAM");
                        if (IDataUtil.isNotEmpty(discntParamList))
                        {
                            for (int l = 0; l < discntParamList.size(); l++)
                            {
                                IData discntParam = discntParamList.getData(l);
                                String attrCode = discntParam.getString("ATTR_CODE", "");
                                String attrValue = discntParam.getString("ATTR_VALUE", "");
                                String attrName = discntParam.getString("ATTR_NAME", "");

                                attrNameList.add(attrName);
                                attrCodeList.add(attrCode);
                                attrValueList.add(attrValue);
                            }
                        }
                        selDiscntAttrNameList.add(attrNameList);
                        selDiscntAttrCodeList.add(attrCodeList);
                        selDiscntAttrValueList.add(attrValueList);
                        /** 拆分DISCNT_PARAM数据结束 */
                    }

                }
                discntIdList.add(selDiscntIdList);
                discntNameList.add(selDiscntNameList);
                discntAttrNameList.add(selDiscntAttrNameList);
                discntAttrCodeList.add(selDiscntAttrCodeList);
                discntAttrValueList.add(selDiscntAttrCodeList);
                /** 拆分DISCNT数据结束 */
            }

            userProductInfoUserIdset.add(selUserId);
            returnData.put("USER_ID", userProductInfoUserIdset);

            returnData.put("SERVICE_ID", svcIdList);
            returnData.put("SERVICE_NAME", svcNameList);
            returnData.put("SERVICE_ATTR_NAME", svcAttrNameList);
            returnData.put("SERVICE_ATTR_CODE", svcAttrCodeList);
            returnData.put("SERVICE_ATTR_VALUE", svcAttrValueList);

            returnData.put("DISCNT_CODE", discntIdList);
            returnData.put("DISCNT_NAME", discntNameList);
            returnData.put("DISCNT_ATTR_NAME", discntAttrNameList);
            returnData.put("DISCNT_ATTR_CODE", discntAttrCodeList);
            returnData.put("DISCNT_ATTR_VALUE", discntAttrValueList);
        }
        /** 拆分USER_PRODUCT_INFOS数据结束 */

        return returnData;
    }

    /**
     * 拼产品属性到报文
     * 
     * @param map
     * @param commData
     * @return
     * @throws Exception
     */
    private static void spellMessage(IData returnData, IData commData) throws Exception
    {
        // 处理商品信息
        IDataset merchList = commData.getDataset("MERCH");

        if (IDataUtil.isEmpty(merchList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_198);
        }

        IDataset retMerchUserIdList = new DatasetList();// 返回值
        for (int m = 0; m < merchList.size(); m++)
        {
            retMerchUserIdList.add(merchList.getData(m).getString("USER_ID", ""));
        }
        returnData.put("USER_ID", retMerchUserIdList);

        // 处理产品信息
        IDataset merchPList = commData.getDataset("MERCHP");
        if (IDataUtil.isNotEmpty(merchPList))
        {
            IDataset retProductIdList = new DatasetList(); // 产品ID
            IDataset retUserIdList = new DatasetList(); // 产品用户ID

            for (int i = 0; i < merchPList.size(); i++)
            {
                IDataset productList = merchPList.getDataset(i); // 某个商品对应的产品列表

                IDataset productIdList = new DatasetList(); // 产品编码
                IDataset userIdList = new DatasetList(); // 产品user_id

                for (int j = 0; j < productList.size(); j++)
                {
                    productIdList.add(productList.getData(j).getString("PRODUCT_ID", ""));
                    userIdList.add(productList.getData(j).getString("USER_ID", ""));
                }
                retProductIdList.add(productIdList);
                retUserIdList.add(userIdList);
            }

            returnData.put("PRSRV_STR10", retProductIdList); // 产品编码
            returnData.put("RSRV_STR4", retUserIdList); // 子产品USER_ID
        }

        // 处理资费信息
        IDataset merchPDiscntList = commData.getDataset("MERCHP_DISCNT");

        if (IDataUtil.isNotEmpty(merchPDiscntList))
        {
            IDataset retDiscntCodeList = new DatasetList();
            IDataset retDiscntExplanList = new DatasetList();

            IDataset retIcbNameList = new DatasetList();
            IDataset retIcbCodeList = new DatasetList();
            IDataset retIcbValueList = new DatasetList();

            for (int d = 0; d < merchPDiscntList.size(); d++)
            {
                IDataset productList = merchPDiscntList.getDataset(d);// 某个商品下的产品资费列表

                IDataset pDiscntCodeList = new DatasetList();
                IDataset pDiscntExplanList = new DatasetList();

                IDataset pIcbNameList = new DatasetList();
                IDataset pIcbCodeList = new DatasetList();
                IDataset pIcbValueList = new DatasetList();

                for (int i = 0; i < productList.size(); i++)
                {
                    IDataset discntList = productList.getDataset(i); // 某个产品下资费列表

                    IDataset discntCodeList = new DatasetList();
                    IDataset discntExplanList = new DatasetList();

                    // 产品资费的ICB参数
                    IDataset icbNameList = new DatasetList();
                    IDataset icbCodeList = new DatasetList();
                    IDataset icbValueList = new DatasetList();

                    for (int j = 0; j < discntList.size(); j++)// 遍历资费列表
                    {
                        IData discntData = discntList.getData(j);

                        discntCodeList.add(discntData.getString("DISCNT_CODE")); // BOSS侧资费编码
                        discntExplanList.add(discntData.getString("DISCNT_EXPLAIN")); // BOSS侧资费描述

                        IDataset icbList = discntData.getDataset("ICB");

                        IDataset codeList = new DatasetList();
                        IDataset nameList = new DatasetList();
                        IDataset valueList = new DatasetList();

                        if (IDataUtil.isNotEmpty(icbList))
                        {
                            for (int k = 0; k < icbList.size(); k++)
                            {
                                IData icbData = icbList.getData(k);
                                codeList.add(icbData.getString("ATTR_CODE"));
                                nameList.add(icbData.getString("ATTR_NAME"));
                                valueList.add(icbData.getString("ATTR_VALUE"));
                            }
                        }

                        icbNameList.add(nameList);
                        icbCodeList.add(codeList);
                        icbValueList.add(valueList);
                    }

                    pDiscntCodeList.add(discntCodeList);
                    pDiscntExplanList.add(discntExplanList);

                    pIcbNameList.add(icbNameList);
                    pIcbCodeList.add(icbCodeList);
                    pIcbValueList.add(icbValueList);
                }

                retDiscntCodeList.add(pDiscntCodeList);
                retDiscntExplanList.add(pDiscntExplanList);
                retIcbNameList.add(pIcbNameList);
                retIcbCodeList.add(pIcbCodeList);
                retIcbValueList.add(pIcbValueList);

            }

            returnData.put("RSRV_STR8", retDiscntCodeList);
            returnData.put("FEE_DESC", retDiscntExplanList);
            returnData.put("RSRV_STR9", retIcbCodeList);
            returnData.put("RSRV_STR10", retIcbNameList);
            returnData.put("RSRV_STR11", retIcbNameList);
        }

        // 产品属性
        IDataset paramList = commData.getDataset("PRODUCT_PARAM");

        if (IDataUtil.isNotEmpty(paramList))
        {
            // 返回值
            IDataset retParamNameList = new DatasetList();
            IDataset retParamCodeList = new DatasetList();
            IDataset retParamValueList = new DatasetList();

            for (int k = 0; k < paramList.size(); k++)
            {
                IDataset productParamList = paramList.getDataset(k); // 某商品所有产品的产品属性集合

                IDataset productNameList = new DatasetList();
                IDataset productCodeList = new DatasetList();
                IDataset productValueList = new DatasetList();

                for (int i = 0; i < productParamList.size(); i++)
                {
                    IDataset paramAttrList = productParamList.getDataset(i); // 某产品下的产品属性集合

                    IDataset paramCodeList = new DatasetList();
                    IDataset paramValueList = new DatasetList();
                    IDataset paramNameList = new DatasetList();

                    for (int j = 0; j < paramAttrList.size(); j++)
                    {
                        IData attrData = paramAttrList.getData(j);

                        if (!"".equals(attrData.getString("ATTR_VALUE", "")))
                        {
                            String[] paramArray = attrData.getString("ATTR_VALUE").split(";"); // 属性值如果用“;”分隔，需要拆分为多个属性值
                            for (int z = 0; z < paramArray.length; z++)
                            {
                                paramCodeList.add(attrData.getString("ATTR_CODE", ""));
                                paramValueList.add(paramArray[z]);
                                paramNameList.add(attrData.getString("ATTR_NAME", ""));
                            }
                        }
                        else
                        {
                            paramCodeList.add(attrData.getString("ATTR_CODE", ""));
                            paramValueList.add(attrData.getString("ATTR_VALUE", ""));
                            paramNameList.add(attrData.getString("ATTR_NAME", ""));
                        }
                    }
                    productNameList.add(paramNameList);
                    productCodeList.add(paramCodeList);
                    productValueList.add(paramValueList);

                }

                retParamNameList.add(productNameList);
                retParamCodeList.add(productCodeList);
                retParamValueList.add(productValueList);
            }

            returnData.put("RSRV_STR15", retParamCodeList);// 属性代码
            returnData.put("RSRV_STR16", retParamValueList);// 属性值
            returnData.put("RSRV_STR17", retParamNameList);// 属性名
        }
    }

    /**
     * 对平台服务参数重新拼串
     * 
     * @param params
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getPlatSvcParam(IData param) throws Exception
    {
        IDataset elementset = new DatasetList();
        String id = param.getString("ID", "");
        String idType = "S";
        String attrObj = "0";
        String eparchyCode = CSBizBean.getUserEparchyCode();

        IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO(id, idType, attrObj, eparchyCode, null);
        IData oldPlatsvc = IDataUtil.hTable2StdSTable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE");

        IData temp = new DataMap();
        temp.put("ATTR_CODE", "OPER_STATE");
        String operState = param.getString("OPER_STATE", "");
        if ("".equals(operState))
        {
            temp.put("ATTR_VALUE", oldPlatsvc.getString("OPER_STATE", ""));
        }
        else
        {
            temp.put("ATTR_VALUE", operState);
        }
        temp.put("ATTR_NAME", "操作类型");
        elementset.add(temp);
        IData temp1 = new DataMap();
        temp1.put("ATTR_CODE", "PLAT_SYNC_STATE");
        String platSyncState = param.getString("PLAT_SYNC_STATE", "");
        if ("".equals(platSyncState))
        {
            temp1.put("ATTR_VALUE", oldPlatsvc.getString("PLAT_SYNC_STATE", ""));
        }
        else
        {
            temp1.put("ATTR_VALUE", platSyncState);
        }
        temp1.put("ATTR_NAME", "当前服务状态");
        elementset.add(temp1);
        IData temp2 = new DataMap();
        temp2.put("ATTR_CODE", "BIZ_CODE");
        String bizCode = param.getString("BIZ_CODE", "");
        if ("".equals(bizCode))
        {
            temp2.put("ATTR_VALUE", oldPlatsvc.getString("BIZ_CODE", ""));
        }
        else
        {
            temp2.put("ATTR_VALUE", bizCode);
        }
        temp2.put("ATTR_NAME", "业务代码");
        elementset.add(temp2);
        IData temp3 = new DataMap();
        temp3.put("ATTR_CODE", "BIZ_NAME");
        String bizName = param.getString("BIZ_NAME", "");
        if ("".equals(bizName))
        {
            temp3.put("ATTR_VALUE", oldPlatsvc.getString("BIZ_NAME", ""));
        }
        else
        {
            temp3.put("ATTR_VALUE", bizName);
        }
        temp3.put("ATTR_NAME", "业务名称");
        elementset.add(temp3);
        IData temp4 = new DataMap();
        temp4.put("ATTR_CODE", "BIZ_IN_CODE");
        String bizInCode = param.getString("BIZ_IN_CODE", "");
        if ("".equals(bizInCode))
        {
            temp4.put("ATTR_VALUE", oldPlatsvc.getString("BIZ_IN_CODE", ""));
        }
        else
        {
            temp4.put("ATTR_VALUE", bizInCode);
        }
        temp4.put("ATTR_NAME", "服务代码校验");
        elementset.add(temp4);
        IData temp5 = new DataMap();
        temp5.put("ATTR_CODE", "SI_BASE_IN_CODE");
        String siBaseInCode = param.getString("SI_BASE_IN_CODE", "");
        if ("".equals(siBaseInCode))
        {
            temp5.put("ATTR_VALUE", oldPlatsvc.getString("SI_BASE_IN_CODE", ""));
        }
        else
        {
            temp5.put("ATTR_VALUE", siBaseInCode);
        }
        temp5.put("ATTR_NAME", "基本接入号");
        elementset.add(temp5);
        IData temp6 = new DataMap();
        temp6.put("ATTR_CODE", "BIZ_ATTR");
        String bizAttr = param.getString("BIZ_ATTR", "");
        if ("".equals(bizAttr))
        {
            temp6.put("ATTR_VALUE", oldPlatsvc.getString("BIZ_ATTR", ""));
        }
        else
        {
            temp6.put("ATTR_VALUE", bizAttr);
        }
        temp6.put("ATTR_NAME", "业务属性");
        elementset.add(temp6);
        IData temp7 = new DataMap();
        temp7.put("ATTR_CODE", "ACCESS_MODE");
        String accessMode = param.getString("ACCESS_MODE", "");
        if ("".equals(accessMode))
        {
            temp7.put("ATTR_VALUE", oldPlatsvc.getString("ACCESS_MODE", ""));
        }
        else
        {
            temp7.put("ATTR_VALUE", accessMode);
        }
        temp7.put("ATTR_NAME", "访问模式");
        elementset.add(temp7);
        IData temp8 = new DataMap();
        temp8.put("ATTR_CODE", "SI_BASE_IN_CODE_A");
        String siBaseInCodeA = param.getString("SI_BASE_IN_CODE_A", "");
        if ("".equals(siBaseInCodeA))
        {
            temp8.put("ATTR_VALUE", oldPlatsvc.getString("SI_BASE_IN_CODE_A", ""));
        }
        else
        {
            temp8.put("ATTR_VALUE", siBaseInCodeA);
        }
        temp8.put("ATTR_NAME", "SI_BASE_IN_CODE_A");
        elementset.add(temp8);
        IData temp9 = new DataMap();
        temp9.put("ATTR_CODE", "EC_BASE_IN_CODE_A");
        String ecBaseInCodeA = param.getString("EC_BASE_IN_CODE_A", "");
        if ("".equals(ecBaseInCodeA))
        {
            temp9.put("ATTR_VALUE", param.getString("EC_BASE_IN_CODE_A", ""));
        }
        else
        {
            temp9.put("ATTR_VALUE", ecBaseInCodeA);
        }
        temp9.put("ATTR_NAME", "EC_BASE_IN_CODE_A");
        elementset.add(temp9);
        IData temp10 = new DataMap();
        temp10.put("ATTR_CODE", "BIZ_STATUS");
        String bizStatus = param.getString("BIZ_STATUS", "");
        if ("".equals(bizStatus))
        {
            temp10.put("ATTR_VALUE", param.getString("BIZ_STATUS", ""));
        }
        else
        {
            temp10.put("ATTR_VALUE", bizStatus);
        }
        temp10.put("ATTR_NAME", "业务状态");
        elementset.add(temp10);
        IData temp11 = new DataMap();
        temp11.put("ATTR_CODE", "BILLING_TYPE");
        String billingType = param.getString("BILLING_TYPE", "");
        if ("".equals(billingType))
        {
            temp11.put("ATTR_VALUE", param.getString("BILLING_TYPE", ""));
        }
        else
        {
            temp11.put("ATTR_VALUE", billingType);
        }
        temp11.put("ATTR_NAME", "计费类型");
        elementset.add(temp11);
        IData temp12 = new DataMap();
        temp12.put("ATTR_CODE", "PRICE");
        String price = param.getString("PRICE", "");
        if ("".equals(price))
        {
            temp12.put("ATTR_VALUE", param.getString("PRICE", ""));
        }
        else
        {
            temp12.put("ATTR_VALUE", price);
        }
        temp12.put("ATTR_NAME", "单价（人民币分）");
        elementset.add(temp12);
        IData temp13 = new DataMap();
        temp13.put("ATTR_CODE", "AUTH_CODE");
        String authCode = param.getString("AUTH_CODE", "");
        if ("".equals(authCode))
        {
            temp13.put("ATTR_VALUE", param.getString("AUTH_CODE", ""));
        }
        else
        {
            temp13.put("ATTR_VALUE", authCode);
        }
        temp13.put("ATTR_NAME", "业务接入号鉴权方式");
        elementset.add(temp13);
        IData temp14 = new DataMap();
        temp14.put("ATTR_CODE", "DELIVER_NUM");
        String deliverNum = param.getString("DELIVER_NUM", "");
        if ("".equals(deliverNum))
        {
            temp14.put("ATTR_VALUE", param.getString("DELIVER_NUM", ""));
        }
        else
        {
            temp14.put("ATTR_VALUE", deliverNum);
        }
        temp14.put("ATTR_NAME", "限制下发次数(0为不限制)");
        elementset.add(temp14);
        IData temp15 = new DataMap();
        temp15.put("ATTR_CODE", "BIZ_TYPE_CODE");
        String bizTypeCode = param.getString("BIZ_TYPE_CODE", "");
        if ("".equals(bizTypeCode))
        {
            temp15.put("ATTR_VALUE", param.getString("BIZ_TYPE_CODE", ""));
        }
        else
        {
            temp15.put("ATTR_VALUE", bizTypeCode);
        }
        temp15.put("ATTR_NAME", "业务种类");
        elementset.add(temp15);
        IData temp16 = new DataMap();
        temp16.put("ATTR_CODE", "USAGE_DESC");
        String usageDesc = param.getString("USAGE_DESC", "");
        if ("".equals(usageDesc))
        {
            temp16.put("ATTR_VALUE", param.getString("USAGE_DESC", ""));
        }
        else
        {
            temp16.put("ATTR_VALUE", usageDesc);
        }
        temp16.put("ATTR_NAME", "业务方法描述");
        elementset.add(temp16);
        IData temp17 = new DataMap();
        temp17.put("ATTR_CODE", "BIZ_PRI");
        String bizPri = param.getString("BIZ_PRI", "");
        if ("".equals(bizPri))
        {
            temp17.put("ATTR_VALUE", param.getString("BIZ_PRI", ""));
        }
        else
        {
            temp17.put("ATTR_VALUE", bizPri);
        }
        temp17.put("ATTR_NAME", "业务优先级");
        elementset.add(temp17);
        IData temp18 = new DataMap();
        temp18.put("ATTR_CODE", "INTRO_URL");
        String introUrl = param.getString("INTRO_URL", "");
        if ("".equals(introUrl))
        {
            temp18.put("ATTR_VALUE", param.getString("INTRO_URL", ""));
        }
        else
        {
            temp18.put("ATTR_VALUE", introUrl);
        }
        temp18.put("ATTR_NAME", "业务的介绍网址");
        elementset.add(temp18);
        IData temp19 = new DataMap();
        temp19.put("ATTR_CODE", "PRE_CHARGE");
        String preCharge = param.getString("PRE_CHARGE", "");
        if ("".equals(preCharge))
        {
            temp19.put("ATTR_VALUE", param.getString("PRE_CHARGE", ""));
        }
        else
        {
            temp19.put("ATTR_VALUE", preCharge);
        }

        temp19.put("ATTR_NAME", "预付费标记");
        elementset.add(temp19);
        IData temp20 = new DataMap();
        temp20.put("ATTR_CODE", "CS_URL");
        String csUrl = param.getString("CS_URL", "");
        if ("".equals(csUrl))
        {
            temp20.put("ATTR_VALUE", param.getString("CS_URL", ""));
        }
        else
        {
            temp20.put("ATTR_VALUE", csUrl);
        }
        temp20.put("ATTR_NAME", "SIProvision的URL");
        elementset.add(temp20);
        IData temp21 = new DataMap();
        temp21.put("ATTR_CODE", "MAX_ITEM_PRE_DAY");
        String maxItemPreDay = param.getString("MAX_ITEM_PRE_DAY", "");
        if ("".equals(maxItemPreDay))
        {
            temp21.put("ATTR_VALUE", param.getString("MAX_ITEM_PRE_DAY", ""));
        }
        else
        {
            temp21.put("ATTR_VALUE", maxItemPreDay);
        }
        temp21.put("ATTR_NAME", "每天最大短信数");
        elementset.add(temp21);
        IData temp22 = new DataMap();
        temp22.put("ATTR_CODE", "MAX_ITEM_PRE_MON");
        String marItemPreMon = param.getString("MAX_ITEM_PRE_MON", "");
        if ("".equals(marItemPreMon))
        {
            temp22.put("ATTR_VALUE", param.getString("MAX_ITEM_PRE_MON", ""));
        }
        else
        {
            temp22.put("ATTR_VALUE", marItemPreMon);
        }
        temp22.put("ATTR_NAME", "每月最大短信数");
        elementset.add(temp22);
        IData temp23 = new DataMap();
        temp23.put("ATTR_CODE", "IS_TEXT_ECGN");
        String isTextEcgn = param.getString("IS_TEXT_ECGN", "");
        if ("".equals(isTextEcgn))
        {
            temp23.put("ATTR_VALUE", param.getString("IS_TEXT_ECGN", ""));
        }
        else
        {
            temp23.put("ATTR_VALUE", isTextEcgn);
        }
        temp23.put("ATTR_NAME", "短信正文签名");
        elementset.add(temp23);
        IData temp24 = new DataMap();
        temp24.put("ATTR_CODE", "DEFAULT_ECGN_LANG");
        String defaultEcgnLang = param.getString("DEFAULT_ECGN_LANG", "");
        if ("".equals(defaultEcgnLang))
        {
            temp24.put("ATTR_VALUE", param.getString("DEFAULT_ECGN_LANG", ""));
        }
        else
        {
            temp24.put("ATTR_VALUE", defaultEcgnLang);
        }
        temp24.put("ATTR_NAME", "签名语言");
        elementset.add(temp24);
        IData temp25 = new DataMap();
        temp25.put("ATTR_CODE", "TEXT_ECGN_ZH");
        String textEcgnZh = param.getString("TEXT_ECGN_ZH", "");
        if ("".equals(textEcgnZh))
        {
            temp25.put("ATTR_VALUE", param.getString("TEXT_ECGN_ZH", ""));
        }
        else
        {
            temp25.put("ATTR_VALUE", textEcgnZh);
        }
        temp25.put("ATTR_NAME", "中文签名");
        elementset.add(temp25);
        IData temp26 = new DataMap();
        temp26.put("ATTR_CODE", "TEXT_ECGN_EN");
        String textEcgnEn = param.getString("TEXT_ECGN_EN", "");
        if ("".equals(textEcgnEn))
        {
            temp26.put("ATTR_VALUE", param.getString("TEXT_ECGN_EN", ""));
        }
        else
        {
            temp26.put("ATTR_VALUE", textEcgnEn);
        }
        temp26.put("ATTR_NAME", "英文签名");
        elementset.add(temp26);
        IData temp27 = new DataMap();
        temp27.put("ATTR_CODE", "FORBID_START_TIME_A");
        String forbidStartTimeA = param.getString("FORBID_START_TIME_A", "");
        if ("".equals(forbidStartTimeA))
        {
            temp27.put("ATTR_VALUE", param.getString("FORBID_START_TIME_A", ""));
        }
        else
        {
            temp27.put("ATTR_VALUE", forbidStartTimeA);
        }
        temp27.put("ATTR_NAME", "不允许下发开始时间1");
        elementset.add(temp27);
        IData temp28 = new DataMap();
        temp28.put("ATTR_CODE", "FORBID_END_TIME_A");
        String forbidEndTimeA = param.getString("FORBID_END_TIME_A", "");
        if ("".equals(forbidEndTimeA))
        {
            temp28.put("ATTR_VALUE", param.getString("FORBID_END_TIME_A", ""));
        }
        else
        {
            temp28.put("ATTR_VALUE", forbidEndTimeA);
        }
        temp28.put("ATTR_NAME", "不允许下发结束时间1");
        elementset.add(temp28);
        IData temp29 = new DataMap();
        temp29.put("ATTR_CODE", "FORBID_START_TIME_B");
        String forbidStartTimeB = param.getString("FORBID_START_TIME_B", "");
        if ("".equals(forbidStartTimeB))
        {
            temp29.put("ATTR_VALUE", param.getString("FORBID_START_TIME_B", ""));
        }
        else
        {
            temp29.put("ATTR_VALUE", forbidStartTimeB);
        }
        temp29.put("ATTR_NAME", "不允许下发开始时间2");
        elementset.add(temp29);
        IData temp30 = new DataMap();
        temp30.put("ATTR_CODE", "FORBID_END_TIME_B");
        String forbidEndTimeB = param.getString("FORBID_END_TIME_B", "");
        if ("".equals(forbidEndTimeB))
        {
            temp30.put("ATTR_VALUE", param.getString("FORBID_END_TIME_B", ""));
        }
        else
        {
            temp30.put("ATTR_VALUE", forbidEndTimeB);
        }
        temp30.put("ATTR_NAME", "不允许下发结束时间2");
        elementset.add(temp30);
        IData temp31 = new DataMap();
        temp31.put("ATTR_CODE", "FORBID_START_TIME_C");
        String forbidStartTimeC = param.getString("FORBID_START_TIME_C", "");
        if ("".equals(forbidStartTimeC))
        {
            temp31.put("ATTR_VALUE", param.getString("FORBID_START_TIME_C", ""));
        }
        else
        {
            temp31.put("ATTR_VALUE", forbidStartTimeC);
        }
        temp31.put("ATTR_NAME", "不允许下发开始时间3");
        elementset.add(temp31);
        IData temp32 = new DataMap();
        temp32.put("ATTR_CODE", "FORBID_END_TIME_C");
        String forbidEndTimeC = param.getString("FORBID_END_TIME_C", "");
        if ("".equals(forbidEndTimeC))
        {
            temp32.put("ATTR_VALUE", param.getString("FORBID_END_TIME_C", ""));
        }
        else
        {
            temp32.put("ATTR_VALUE", forbidEndTimeC);
        }
        temp32.put("ATTR_NAME", "不允许下发结束时间3");
        elementset.add(temp32);
        IData temp33 = new DataMap();
        temp33.put("ATTR_CODE", "FORBID_START_TIME_D");
        String forbidStartTimeD = param.getString("FORBID_START_TIME_D", "");
        if ("".equals(forbidStartTimeD))
        {
            temp33.put("ATTR_VALUE", param.getString("FORBID_START_TIME_D", ""));
        }
        else
        {
            temp33.put("ATTR_VALUE", forbidStartTimeD);
        }
        temp33.put("ATTR_NAME", "不允许下发开始时间4");
        elementset.add(temp33);
        IData temp34 = new DataMap();
        temp34.put("ATTR_CODE", "FORBID_END_TIME_D");
        String forbidEndTimeD = param.getString("FORBID_END_TIME_D", "");
        if ("".equals(forbidEndTimeD))
        {
            temp34.put("ATTR_VALUE", param.getString("FORBID_END_TIME_D", ""));
        }
        else
        {
            temp34.put("ATTR_VALUE", forbidEndTimeD);
        }
        temp34.put("ATTR_NAME", "不允许下发结束时间4");
        elementset.add(temp34);

        IData temp35 = new DataMap();
        temp35.put("ATTR_CODE", "SVRCODETAIL");
        String svrcodetail = param.getString("SVRCODETAIL", "");
        if ("".equals(svrcodetail))
        {
            temp35.put("ATTR_VALUE", param.getString("SVRCODETAIL", ""));
        }
        else
        {
            temp35.put("ATTR_VALUE", svrcodetail);
        }
        temp35.put("ATTR_NAME", "服务代码扩展");
        elementset.add(temp35);

        IData temp36 = new DataMap();
        temp36.put("ATTR_CODE", "EC_BASE_IN_CODE");
        String ecBaseInCode = param.getString("EC_BASE_IN_CODE", "");
        if ("".equals(ecBaseInCode))
        {
            temp36.put("ATTR_VALUE", param.getString("EC_BASE_IN_CODE", ""));
        }
        else
        {
            temp36.put("ATTR_VALUE", ecBaseInCode);
        }
        temp36.put("ATTR_NAME", "业务接入号");
        elementset.add(temp36);

        IData temp37 = new DataMap();
        temp37.put("ATTR_CODE", "BILLING_MODE");
        String billingMode = param.getString("BILLING_MODE", "");
        if ("".equals(billingMode))
        {
            temp37.put("ATTR_VALUE", param.getString("BILLING_MODE", ""));
        }
        else
        {
            temp37.put("ATTR_VALUE", billingMode);
        }
        temp37.put("ATTR_NAME", "计费模式");
        elementset.add(temp37);

        return elementset;
    }

    /**
     * 组织预受理完成的BBOSS产品相关数据,为报文拼装准备数据
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    private IData getProSpellDataFromTrade(IData inParam) throws Exception
    {
        IData commData = new DataMap();

        String groupId = inParam.getString("GROUP_ID");
        String custId = inParam.getString("CUST_ID");
        String productId = inParam.getString("PRODUCT_ID");

        String productName = UProductInfoQry.getProductNameByProductId(productId);

        // 查询预受理台账信息,新增用户和修改用户(分批受理产品时)业务类型
        IDataset tradeList = TradeInfoQry.qryBbossAheadTrade(custId, productId, null);

        if (IDataUtil.isEmpty(tradeList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_765, groupId, productName);
        }

        IDataset merchList = new DatasetList();// 商品列表
        IDataset merchPList = new DatasetList();// 商品下产品列表
        IDataset merchPDiscntList = new DatasetList();// 商品下产品资费列表
        IDataset merchPParamList = new DatasetList();// 商品下产品参数列表

        for (int i = 0, iRow = tradeList.size(); i < iRow; i++)
        {
            IData tradeData = tradeList.getData(i);

            IDataset selProductList = new DatasetList();
            IDataset selDiscntList = new DatasetList();
            IDataset selParamList = new DatasetList();

            String tradeId = tradeData.getString("TRADE_ID", "");

            // 查询商品台账信息
            IDataset tradeMerchList = TradeGrpMerchInfoQry.qryAllMerchInfoByTradeId(tradeId, null);

            if (IDataUtil.isNotEmpty(tradeMerchList))
            {
                // 每个TRADE只有一个商品
                merchList.add(tradeMerchList.getData(0));
            }

            // liuxx3 --2014 -08-08--start
            // 根据商品台账编号查询到订单号order_id
            IDataset orderInfos = TradeInfoQry.queryTradeInfoByTradeIdAndTableName(tradeId, "TF_B_TRADE");

            // 每个TRADE只有一个order_id
            String orderId = orderInfos.getData(0).getString("ORDER_ID");

            // 查询产品台账信息
            IDataset tradeMerchPList = TradeGrpMerchpInfoQry.qryGrpMerchpByOrderId(orderId, null);
            // --end

            for (int j = 0, jRow = tradeMerchPList.size(); j < jRow; j++)
            {
                IData productData = tradeMerchPList.getData(j);

                String userId = productData.getString("USER_ID", "");// 商品下产品用户ID
                String productSpecCode = productData.getString("PRODUCT_SPEC_CODE", "");
                String merchPtradeId = productData.getString("TRADE_ID");// ---liuxx3 2014 -08-08

                IDataset attrList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productSpecCode, null);

                if (IDataUtil.isEmpty(attrList))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_900, productSpecCode);
                }

                productData.put("PRODUCT_ID", attrList.getData(0).getString("ATTR_CODE"));// 产品ID

                // 查询产品INST_ID--liuxx3 2014 -08-08 start
                String pInstId = "";
                IDataset tradeproInfos = TradeProductInfoQry.getTradeProduct(merchPtradeId, null);
                if (IDataUtil.isEmpty(tradeproInfos))
                {
                    IDataset paramAttrList = new DatasetList();// 产品参数列表
                    selParamList.add(paramAttrList);
                    continue;
                }

                pInstId = tradeproInfos.getData(0).getString("INST_ID", "");
                /*
                 * String pInstId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TF_B_TRADE_PRODUCT", new String[] {
                 * "TRADE_ID", "USER_ID" }, "INST_ID", new String[] { tradeId, userId }); if
                 * (StringUtils.isEmpty(pInstId)) { IDataset paramAttrList = new DatasetList();// 产品参数列表
                 * selParamList.add(paramAttrList); continue; }
                 */

                // 查询产品参数信息
                IDataset paramAttrList = TradeAttrInfoQry.getUserAttrByTradeIDNew(merchPtradeId, userId, "P", pInstId);
                // --end

                if (IDataUtil.isNotEmpty(paramAttrList))
                {
                    for (int k = paramAttrList.size() - 1; k >= 0; k--)
                    {
                        IData paramAttrData = paramAttrList.getData(k);

                        String attrCode = paramAttrData.getString("ATTR_CODE", "");

                        String attrName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_F_POPRODUCTPLUS", new String[]
                        { "PRODUCTSPECNUMBER", "PRODUCTSPECCHARACTERNUMBER" }, "NAME", new String[]
                        { productSpecCode, attrCode });
                        if (StringUtils.isEmpty(attrName))
                        {
                            paramAttrList.remove(k);
                            continue;
                        }

                        paramAttrData.put("ATTR_NAME", attrName);
                    }
                }

                // 查询资费台账信息信息--liuxx3 --2014 -08 -09 --start
                IDataset discntList = TradeDiscntInfoQry.queryTradeDiscntByTradeIdAndTag(merchPtradeId, TRADE_MODIFY_TAG.Add.getValue(), userId);

                // 防止查询结果返回为null值的风险判断操作
                if (IDataUtil.isEmpty(discntList))
                {
                    discntList = new DatasetList();
                }
                /*
                 * if (IDataUtil.isEmpty(discntList)) { CSAppException.apperr(TradeException.CRM_TRADE_94, userId); }
                 */
                // --end

                for (int k = 0; k < discntList.size(); k++)
                {
                    IData discntData = discntList.getData(k);

                    String discntCode = discntData.getString("DISCNT_CODE", "");
                    String discntExplain = DiscntInfoQry.getDiscntExplanByDiscntCode(discntCode);

                    discntData.put("DISCNT_EXPLAIN", discntExplain);

                    String dInstId = discntData.getString("INST_ID", "");

                    IData param = new DataMap();
                    param.put("TRADE_ID", merchPtradeId);
                    param.put("USER_ID", userId);
                    param.put("INST_TYPE", "D");
                    param.put("INST_ID", dInstId);

                    // 查询资费参数信息
                    IDataset discntAttrList = TradeAttrInfoQry.getUserAttrByTradeID(param);

                    if (IDataUtil.isNotEmpty(discntAttrList))
                    {
                        // 倒序删除
                        for (int l = discntAttrList.size() - 1; l >= 0; l--)
                        {
                            IData attrData = discntAttrList.getData(l);
                            String attrCode = attrData.getString("ATTR_CODE");
                            String attrName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_ITEMA", new String[]
                            { "ID", "ID_TYPE", "ATTR_CODE" }, "ATTR_LABLE", new String[]
                            { discntCode, "D", attrCode });

                            if (StringUtils.isEmpty(attrName))
                            {
                                discntAttrList.remove(l);
                                continue;
                            }
                            attrData.put("ATTR_NAME", attrName);
                        }
                    }
                    discntData.put("ICB", discntAttrList);
                }

                // 添加数据
                selProductList.add(productData);
                selParamList.add(paramAttrList);
                selDiscntList.add(discntList);
            }

            // 添加数据
            merchPList.add(selProductList);
            merchPParamList.add(selParamList);
            merchPDiscntList.add(selDiscntList);
        }

        // 商品表
        commData.put("MERCH", merchList);

        // 产品表
        commData.put("MERCHP", merchPList);

        // 产品资费表
        commData.put("MERCHP_DISCNT", merchPDiscntList);

        // 产品属性
        commData.put("PRODUCT_PARAM", merchPParamList);

        return commData;

    }

    /**
     * 组织用户已办理的某BBOSS产品相关数据,为报文拼装准备数据
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    private IData getProSpellDataFromUser(IData inParam) throws Exception
    {
        IData commData = new DataMap();

        String groupId = inParam.getString("GROUP_ID");
        String merchProductId = inParam.getString("PRODUCT_ID");// 商品ID
        String paramUserId = inParam.getString("USER_ID", "");

        IDataset merchList = new DatasetList();// 商品列表
        IDataset merchPList = new DatasetList();// 商品下产品列表
        IDataset merchPParamList = new DatasetList();// 商品下产品参数列表
        IDataset merchPDiscntList = new DatasetList();// 商品下产品资费列表

        // 查询商品信息
        merchList = UserGrpMerchInfoQry.qryUserMerchInfoNew(groupId, merchProductId, paramUserId, null);

        if (IDataUtil.isEmpty(merchList))
        {
            return commData;
        }

        for (int i = 0; i < merchList.size(); i++)
        {
            IData merchData = merchList.getData(i);

            IDataset selProductList = new DatasetList();// 选择的产品列表
            IDataset selParamList = new DatasetList();// 选择的产品参数列表
            IDataset selDiscntList = new DatasetList();// 选择的产品资费列表

            String merchUserId = merchData.getString("USER_ID", "");// 商品用户ID
            String merchSpecCode = merchData.getString("MERCH_SPEC_CODE");// 商品规则编码

            String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(merchProductId);

            // IDataset relaInfoList = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(merchUserId, relationTypeCode, null);
            // --liuxx3 --start 2014 -08-08
            IDataset relaInfoList = RelaBBInfoQry.getBBByUserIdAB(merchUserId, null, null, relationTypeCode);
            // --end

            for (int j = 0; j < relaInfoList.size(); j++)
            {
                String userId = relaInfoList.getData(j).getString("USER_ID_B");

                // 查询产品信息
                IDataset tempList = UserGrpMerchpInfoQry.qryMerchpInfoByUserIdMerchSpecStatus(userId, merchSpecCode, "A", null);
                if (IDataUtil.isNotEmpty(tempList))
                {
                    selProductList.add(tempList.getData(0));
                }
            }

            if (IDataUtil.isNotEmpty(selProductList))
            {
                for (int j = 0; j < selProductList.size(); j++)
                {
                    IData selProductData = selProductList.getData(j);

                    // 本地与集团产品规则转化
                    String productSpecCode = selProductData.getString("PRODUCT_SPEC_CODE", "");

                    IDataset attrList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productSpecCode, null);

                    if (IDataUtil.isEmpty(attrList))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_900, productSpecCode);
                    }

                    selProductData.put("PRODUCT_ID", attrList.getData(0).getString("ATTR_CODE"));// 产品ID

                    String merchPUserId = selProductData.getString("USER_ID", "");// 商品下产品用户ID

                    // 产品参数处理
                    IDataset userProductList = UserProductInfoQry.queryProductByUserId(merchPUserId);

                    IDataset productAttr = new DatasetList();

                    if (IDataUtil.isEmpty(userProductList))
                    {
                        // 某产品的产品属性为空,填充空节点
                        selParamList.add(productAttr);
                        continue;
                    }

                    String pInstId = userProductList.getData(0).getString("INST_ID", "");

                    // 查询产品参数信息
                    IDataset productParamList = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(merchPUserId, "P", pInstId);

                    if (IDataUtil.isNotEmpty(productParamList))
                    {
                        // 倒序删除
                        for (int k = productParamList.size() - 1; k >= 0; k--)
                        {
                            IData productAttrData = productParamList.getData(k);

                            String attrCode = productAttrData.getString("ATTR_CODE", "");

                            String attrName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_F_POPRODUCTPLUS", new String[]
                            { "PRODUCTSPECNUMBER", "PRODUCTSPECCHARACTERNUMBER" }, "NAME", new String[]
                            { productSpecCode, attrCode });
                            if (StringUtils.isEmpty(attrName))
                            {
                                productParamList.remove(k);
                                continue;
                            }

                            productAttrData.put("ATTR_NAME", attrName);
                        }
                    }

                    // 查询用户资费信息
                    IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(merchPUserId, merchUserId);

                    for (int k = 0; k < userDiscntList.size(); k++)
                    {
                        IData discntData = userDiscntList.getData(k);
                        String discntCode = discntData.getString("DISCNT_CODE", "");

                        String discntExplain = DiscntInfoQry.getDiscntExplanByDiscntCode(discntCode);
                        discntData.put("DISCNT_EXPLAIN", discntExplain);

                        // 查询资费参数信息
                        String dInstId = discntData.getString("INST_ID", "");

                        IDataset discntAttrList = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(merchPUserId, "D", dInstId);

                        if (IDataUtil.isNotEmpty(discntAttrList))
                        {
                            // 倒序删除
                            for (int l = discntAttrList.size() - 1; l >= 0; l--)
                            {
                                IData attrData = discntAttrList.getData(l);
                                String attrCode = attrData.getString("ATTR_CODE");
                                String attrName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_ITEMA", new String[]
                                { "ID", "ID_TYPE", "ATTR_CODE" }, "ATTR_LABLE", new String[]
                                { discntCode, "D", attrCode });

                                if (StringUtils.isEmpty(attrName))
                                {
                                    discntAttrList.remove(l);
                                    continue;
                                }
                                attrData.put("ATTR_NAME", attrName);
                            }
                        }

                        discntData.put("ICB", discntAttrList);
                    }

                    // 添加数据
                    selParamList.add(productParamList);
                    selDiscntList.add(userDiscntList);
                }// 产品循环结束
            }

            // 添加数据
            merchPList.add(selProductList);
            merchPDiscntList.add(selDiscntList);
            merchPParamList.add(selParamList);
        }

        // 商品
        commData.put("MERCH", merchList);

        // 产品表
        commData.put("MERCHP", merchPList);

        // 产品资费表
        commData.put("MERCHP_DISCNT", merchPDiscntList);

        // 产品属性
        commData.put("PRODUCT_PARAM", merchPParamList);

        return commData;
    }

    /**
     * 获取用户的服务优惠及其个性化参数
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IData getServAndDisInfo(IData inParam) throws Exception
    {
        IData returnData = new DataMap();
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();

        String userId = inParam.getString("USER_ID", "");
        String productId = inParam.getString("PRODUCT_ID", "");
        String eparchyCode = inParam.getString("TRADE_EPARCHY_CODE", "");

        // 查询集团用户订购了哪些服务及优惠
        IDataset userElementList = UserSvcInfoQry.getElementFromPackageByUser(userId, productId, null);

        for (int i = 0; i < userElementList.size(); i++)
        {
            IData elementData = userElementList.getData(i);

            String elementId = elementData.getString("ELEMENT_ID", "");
            String elementName = elementData.getString("ELEMENT_NAME", "");
            String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE", "");
            String instId = elementData.getString("INST_ID", "");

            boolean isPlatSvc = false;// 是否平台服务

            // 处理平台服务
            if ("S".equals(elementTypeCode))
            {
                IDataset platSvcList = AttrItemInfoQry.qryAttrItemForPlatSvc(elementId);

                if (IDataUtil.isNotEmpty(platSvcList))
                {
                    isPlatSvc = true;
                }
            }

            if (isPlatSvc)
            {
                IDataset platSvcList = UserPlatSvcInfoQry.qryUserPlatSvcByUserIdAndServiceId(userId, elementId);
                IData platSvcData = platSvcList.getData(0);

                platSvcData.put("ID", elementId);
                IDataset platSvc = getPlatSvcParam(platSvcData);

                IData svcData = new DataMap();
                svcData.put("ELEMENT_ID", elementId);
                svcData.put("ELEMENT_TYPE_CODE", elementTypeCode);
                svcData.put("SERV_PARAM", platSvc);
                svcList.add(svcData);
            }
            else
            {
                // 元素参数信息
                IDataset elementAttrList = UserAttrInfoQry.getUserAttrByUserIdInstidForGrp(userId, elementTypeCode, instId);

                IData param = new DataMap();
                param.put("ID", elementId);
                param.put("ID_TYPE", elementTypeCode);
                param.put("EPARCHY_CODE", inParam.getString("EPARCHY_CODE"));

                // 对个性化参数重新拼串
                IDataset elementset = setElementAttrList(elementAttrList, param);

                if ("S".equals(elementTypeCode))
                {
                    IData svcData = new DataMap();

                    svcData.put("ELEMENT_ID", elementId);
                    svcData.put("ELEMENT_NAME", elementName);
                    svcData.put("ELEMENT_TYPE_CODE", elementTypeCode);
                    svcData.put("EPARCHY_CODE", eparchyCode);
                    svcData.put("SERV_PARAM", elementset);

                    svcList.add(svcData);
                }
                else if ("D".equals(elementTypeCode))
                {
                    IData discntData = new DataMap();

                    discntData.put("ELEMENT_ID", elementId);
                    discntData.put("ELEMENT_NAME", elementName);
                    discntData.put("ELEMENT_TYPE_CODE", elementTypeCode);
                    discntData.put("EPARCHY_CODE", eparchyCode);
                    discntData.put("DISCNT_PARAM", elementset);

                    discntList.add(discntData);
                }
            }
        }

        returnData.put("DISCNT", discntList);
        returnData.put("SERV", svcList);
        return returnData;
    }

    /**
     * 组织用户已办理的BZBG,ADCG,MASG,HYBG,DLBG产品相关数据，为报文拼装准备数据
     * 
     * @param inParam
     *            GROUP_ID PRODUCT_ID BZBG,ADCG,MASG,HYBG产品在BOSS系统对应的产品ID
     * @return
     * @throws Exception
     */
    public IData getSimpleProSpellDataFromUser(IData inParam) throws Exception
    {
        IData returnData = new DataMap();

        String groupId = inParam.getString("GROUP_ID", "");
        String custId = inParam.getString("CUST_ID", "");
        String productId = inParam.getString("PRODUCT_ID", "");
        String paramUserId = inParam.getString("USER_ID", "");

        IDataset userInfoList = UserInfoQry.getUserInfoByCstIdProIdForGrpNew(custId, productId, paramUserId, null);

        if (IDataUtil.isNotEmpty(userInfoList))
        {
            IDataset userProductList = new DatasetList();

            for (int i = 0; i < userInfoList.size(); i++)
            {
                String userId = userInfoList.getData(i).getString("USER_ID", "");

                inParam.put("USER_ID", userId);

                // 获取优惠服务信息
                IData servAndDisData = getServAndDisInfo(inParam);

                IData userProductData = new DataMap();
                userProductData.put("USER_ID", userId);
                userProductData.put("PRODUCT_INFOS", servAndDisData);
                userProductList.add(userProductData);
            }
            returnData.put("GROUP_ID", groupId);
            returnData.put("PRODUCT_ID", productId);
            returnData.put("USER_PRODUCT_INFOS", userProductList);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_122);
        }

        return returnData;
    }

    /**
     * 是否有预受理操作
     * 
     * @param productId
     *            (BBOSS商品的ID)
     * @return
     * @throws Exception
     */
    private boolean hasAheadOper(String productId) throws Exception
    {
        // 查询必选子产品
        IDataset compRelaList = ProductCompRelaInfoQry.queryProductComp(productId, "1");

        if (IDataUtil.isNotEmpty(compRelaList))
        {
            for (int i = 0, row = compRelaList.size(); i < row; i++)
            {
                // 判断下面的子产品是否有预受理的操作
                String productIdB = compRelaList.getData(i).getString("PRODUCT_ID_B", "");

                String attrValue = AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(productIdB, "AHEAD");

                if (StringUtils.isNotBlank(attrValue))
                {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * 提供给端对端调用，进行集团变更或是集团注销时，查询用户的相关信息，资费、资费参数、产品属性
     * 
     * @param inParam
     *            GROUP_ID PRODUCT_ID SEND_CODE 01：查询(现有用户的数据状态) 02：受理(查询出预受理完成的数据) 03：变更(查询出可变更的数据) 04：注销(查询出可注销的数据)
     * @return
     * @throws Exception
     */
    public IDataset qryBBOSSUserOrderInfo(IData inParam) throws Exception
    {
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String sendCode = IDataUtil.getMandaData(inParam, "SEND_CODE");
        String staffId = IDataUtil.getMandaData(inParam, "TRADE_STAFF_ID");
        String eparchyCode = IDataUtil.getMandaData(inParam, "TRADE_EPARCHY_CODE");
        String userId = inParam.getString("USER_ID", "");

        IData retData = new DataMap();// 返回数据

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        String custId = grpCustData.getString("CUST_ID", "");
        inParam.put("CUST_ID", custId);

        // 品牌编码
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        if (StringUtils.isEmpty(brandCode))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_216, productId);
        }

        if ("BZBG".equals(brandCode) || "HYBG".equals(brandCode) || "DLBG".equals(brandCode))
        {
            IData param = new DataMap();
            param.put("GROUP_ID", groupId);
            param.put("CUST_ID", custId);
            param.put("PRODUCT_ID", productId);
            param.put("TRADE_EPARCHY_CODE", eparchyCode);
            param.put("TRADE_STAFF_ID", staffId);
            param.put("USER_ID", userId);

            if ("01".equals(sendCode))// 01：查询(现有用户的数据状态)
            {
                getSpellData(retData, getSimpleProSpellDataFromUser(param));
            }
            else if ("03".equals(sendCode))// 03：变更(查询出可变更的数据)
            {
                getSpellData(retData, getSimpleProSpellDataFromUser(param));
            }
            else if ("04".equals(sendCode))// 04：注销(查询出可注销的数据)
            {
                getSpellData(retData, getSimpleProSpellDataFromUser(param));
            }
        }
        else if ("BOSG".equals(brandCode))
        {
            IData param = new DataMap();
            param.put("GROUP_ID", groupId);
            param.put("PRODUCT_ID", productId);
            param.put("CUST_ID", custId);
            param.put("USER_ID", userId);

            retData.put("PRODUCT_ID", productId);
            retData.put("GROUP_ID", groupId);

            if ("01".equals(sendCode))// 01：查询(现有用户的数据状态)
            {
                spellMessage(retData, getProSpellDataFromUser(param));
            }
            else if ("02".equals(sendCode))// 02：受理(查询出预受理完成的数据)
            {
                if (hasAheadOper(productId))
                {
                    spellMessage(retData, getProSpellDataFromTrade(param));
                }
                else
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_218, productId);
                }
            }
            else if ("03".equals(sendCode))// 03：变更(查询出可变更的数据)
            {
                spellMessage(retData, getProSpellDataFromUser(param)); // 用户现有产品数据，如有特殊业务规则需另外组织报文数据
            }
            else if ("04".equals(sendCode))// 04：注销(查询出可注销的数据)
            {
                spellMessage(retData, getProSpellDataFromUser(param)); // 用户现有产品数据，如有特殊业务规则需另外组织报文数据
            }
        }
        else
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_217, productId);
        }

        return IDataUtil.idToIds(retData);
    }

    /**
     * 根据集团编号PRODUCT_ID，GROUP_ID查询BBOSS办理过的子产品信息
     * 
     * @author liuxx3
     * @date 2014-06-27
     */
    public IDataset qryOperProductForBBOSS(IData inParam, Pagination pg) throws Exception
    {

        String productIdA = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");

        IDataset productInfoList = UProductCompRelaInfoQry.getCompReleInfo(productIdA);
        IDataset result = new DatasetList();

        if (IDataUtil.isNotEmpty(productInfoList))
        {
            for (int i = 0; i < productInfoList.size(); i++)
            {
                IData productInfo = productInfoList.getData(i);
                String productIdB = productInfo.getString("PRODUCT_ID_B");
                IData proInfo = UProductInfoQry.qryProductByPK(productIdB);
                if (IDataUtil.isEmpty(proInfo))
                {
                    continue;
                }
                String productId = proInfo.getString("PRODUCT_ID");
                IDataset idsProductInfoList = UserInfoQry.getBBossProByESOPInfo(groupId, productId, null);
                for (int j = 0; j < idsProductInfoList.size(); j++)
                {
                    IData idsProductInfo = idsProductInfoList.getData(j);
                    idsProductInfo.put("PRODUCT_NAME", proInfo.getString("PRODUCT_NAME"));
                    idsProductInfo.put("BRAND_CODE", proInfo.getString("BRAND_CODE"));

                    result.add(idsProductInfo);
                }

            }
        }

        for (int i = 0; i < result.size(); i++)
        {
            result.getData(i).put("TOTAL_NUM", result.size());// 把TOTAL_NUM放入每一条记录
        }

        return result;
    }

    /**
     * 输入：商品实例ID或产品实例ID（下表格中） 输出：GROUP_ID 集团ID EPARCHY_CODE 集团所在地市
     * 
     * @author liuxx3
     * @date 2014-06-27
     */
    public IDataset qryDataByMerchOfferId(IData inParam, Pagination pg) throws Exception
    {

        // 商品实例ID或产品实例ID
        String MerchOfferId = IDataUtil.getMandaData(inParam, "MERCH_OFFER_ID");

        IDataset rstDataset = UserGrpMerchInfoQry.qryGrpMerchInfoByMerchOfferId(MerchOfferId);

        if (IDataUtil.isEmpty(rstDataset) || StringUtils.isBlank(rstDataset.getData(0).getString("GROUP_ID")))
        {
            CSAppException.apperr(GrpException.GRM_GRP_751, MerchOfferId);
        }

        String GROUP_ID = rstDataset.getData(0).getString("GROUP_ID");// 集团ID

        IDataset rstDatasetTwo = UserGrpInfoQry.getGrpEparchyCodeByGId(GROUP_ID);

        if (IDataUtil.isEmpty(rstDatasetTwo) || StringUtils.isBlank(rstDatasetTwo.getData(0).getString("EPARCHY_CODE")))
        {
            CSAppException.apperr(GrpException.CRM_GRP_755, GROUP_ID);
        }

        String EPARCHY_CODE = rstDataset.getData(0).getString("EPARCHY_CODE");// 集团所在地市

        IData info = new DataMap();
        info.put("GROUP_ID", GROUP_ID);
        info.put("EPARCHY_CODE", EPARCHY_CODE);

        IDataset resultset = new DatasetList();
        resultset.add(info);

        return resultset;
    }

    /**
     * 对元素的个性化参数进行重新拼串
     * 
     * @param attrList
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset setElementAttrList(IDataset attrList, IData inParam) throws Exception
    {
        IDataset dataset = new DatasetList();

        for (int i = 0; i < attrList.size(); i++)
        {
            IData attrData = attrList.getData(i);

            IData addAttrData = new DataMap();

            String id = attrData.getString("ELEMENT_ID", "");
            String elementTypeCode = attrData.getString("ELEMENT_TYPE_CODE", "");
            String attrCode = attrData.getString("ATTR_CODE", "");
            String attrValue = attrData.getString("ATTR_VALUE", "");
            String eparchyCode = attrData.getString("EPARCHY_CODE", "");

            String attrName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_ITEMA", new String[]
            { "ID", "ID_TYPE", "ATTR_CODE", "EPARCHY_CODE" }, "ATTR_LABLE", new String[]
            { id, elementTypeCode, attrCode, eparchyCode });

            addAttrData.put("ATTR_CODE", attrCode);
            addAttrData.put("ATTR_VALUE", attrValue);
            addAttrData.put("ATTR_NAME", attrName);
            dataset.add(addAttrData);
        }
        return dataset;
    }

    /**
     * 作用：根据员工号、产品类型查询集团可办理的产品,网厅不支持一个产品可以订购多次
     * 
     * @author liuxx3
     * @date 2014-06-30
     */
    public IDataset getCanOpenProduct(IData inParam, Pagination pg) throws Exception
    {

        String staffId = IDataUtil.getMandaData(inParam, "TRADE_STAFF_ID");// 员工号
        String productTypeCode = IDataUtil.getMandaData(inParam, "PRODUCT_TYPE_CODE");// 商品类型

        // 用productTypeCode查询出商品集
        IDataset productInfoList = ProductTypeInfoQry.qryProInfoByProTypeCode(productTypeCode);

        // 过滤员工权限
        ProductPrivUtil.filterProductListByPriv(staffId, productInfoList);

        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");// 集团编码
        String eparchyCode = IDataUtil.getMandaData(inParam, "TRADE_EPARCHY_CODE");// 地州

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }
        
        IDataset newProductInfoList = new DatasetList();

        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            String productId = productInfo.getString("PRODUCT_ID");

            IDataset fistResult = UserGrpInfoQry.qryGrpExitsByGIdAndPId(groupId, productId);
            //IDataset secondReslut = ProductInfoQry.qryProExitsByECodeAndPId(productId, eparchyCode);

            if (IDataUtil.isEmpty(fistResult))
            {
                newProductInfoList.add(productInfo);
            }

        }

        return newProductInfoList;
    }

    /**
     * 查询集团用户订购了的套餐和可以订购的套餐接口
     * 
     * @author liuxx3
     * @date 2014-07-01
     */
    public IDataset qryGrpUserDiscntInfos(IData inParam, Pagination pg) throws Exception
    {

        String xMode = inParam.getString("X_GETMODE", "");// 0-已订购,1-未订购
        String productId = inParam.getString("PRODUCT_ID", "");
        String userIdA = inParam.getString("USER_ID_A", "");
        String tradeStaffId = inParam.getString("TRADE_STAFF_ID", "");
        String eparchyCode = inParam.getString("TRADE_EPARCHY_CODE", "");

        if (StringUtils.isEmpty(xMode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_767, xMode);
        }
        if (StringUtils.isEmpty(productId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_768, productId);
        }
        if (StringUtils.isEmpty(userIdA))
        {
            CSAppException.apperr(GrpException.CRM_GRP_769, userIdA);
        }
        if (StringUtils.isEmpty(tradeStaffId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_770, tradeStaffId);
        }
        if (StringUtils.isEmpty(eparchyCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_771, eparchyCode);
        }

        IDataset userElement = UserSvcInfoQry.getElementFromPackageByUser(userIdA, productId, pg);

        IDataset newUserElement = DataHelper.filter(userElement, "ELEMENT_TYPE_CODE=D");// 过滤出来资费

        if (IDataUtil.isNotEmpty(newUserElement))
        {
            for (int i = 0; i < newUserElement.size(); i++)
            {
                newUserElement.getData(i).put("DISCNT_TYPE", "U");// 集团优惠
            }
        }

        IData compProductInfo = ProductCompInfoQry.getProductFromComp(productId);

        // 集团定制
        IData param = new DataMap();
        param.put("USER_ID", userIdA);

        IDataset userDiscntInfos = new DatasetList();
        if (StringUtils.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue(), compProductInfo.getString("USE_TAG")))
        {
            IDataset memDiscnt = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(userIdA, pg);// 已定制优惠
            for (int i = 0; i < memDiscnt.size(); i++)
            {
                IData sourceData = memDiscnt.getData(i);
                sourceData.put("DISCNT_TYPE", "M");
                sourceData.put("GRP_CUSTOMIZE", "false");
            }
            userDiscntInfos.addAll(memDiscnt);
        }
        userDiscntInfos.addAll(newUserElement);

        if (IDataUtil.isNotEmpty(userDiscntInfos)) // 处理接口返回的值DISCNT_CODE、DISCNT_NAME、DISCNT_EXPLAIN
        {
            for (int k = 0; k < userDiscntInfos.size(); k++)
            {

                IData userDiscnt = userDiscntInfos.getData(k);
                String disCode = userDiscnt.getString("ELEMENT_ID");
                String disExplain = DiscntInfoQry.getDiscntExplanByDiscntCode(disCode);
                userDiscnt.put("DISCNT_CODE", disCode);
                userDiscnt.put("DISCNT_NAME", userDiscnt.getString("ELEMENT_NAME"));
                userDiscnt.put("DISCNT_EXPLAIN", disExplain);

            }
        }

        IDataset results = new DatasetList();
        IDataset userResults = new DatasetList();
        IDataset disResults = new DatasetList();

        // 查询集团产品套餐
        IDataset disInfos = getGrpUserDiscnt(inParam, pg);

        // 处理用户可以订购的资费
        if (IDataUtil.isNotEmpty(disInfos))
        {
            for (int i = 0; i < disInfos.size(); i++)
            {
                for (int j = 0; j < userDiscntInfos.size(); j++)
                {
                    String discntInfo = disInfos.getData(i).getString("ELEMENT_ID", "");
                    String userDiscntInfo = userDiscntInfos.getData(j).getString("ELEMENT_ID", "");
                    String userEndDate = userDiscntInfos.getData(j).getString("END_DATE");

                    if (discntInfo.equals(userDiscntInfo) && userEndDate.compareTo(SysDateMgr.getFirstDayOfNextMonth()) >= 0)// 已定购资费只选可以再次变更的
                    {
                        disInfos.getData(i).put("START_DATE", userDiscntInfos.getData(j).getString("START_DATE"));
                        disInfos.getData(i).put("END_DATE", userDiscntInfos.getData(j).getString("END_DATE"));
                        if (!userResults.contains(disInfos.getData(i)))
                        {
                            userResults.add(disInfos.getData(i));
                        }
                    }

                }
            }
            disResults = disInfos;
            disResults.removeAll(userResults);
        }

        if ("0".equals(xMode))
        {
            results = userDiscntInfos;
        }
        else if ("1".equals(xMode))
        {
            results = disResults;
        }

        return results;
    }

    /**
     * 作用：获取集团用户资费
     * 
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset getGrpUserDiscnt(IData inParam, Pagination pg) throws Exception
    {
        IDataset returnDs = new DatasetList();
        IDataset memResult = new DatasetList();
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String tradeEparchyCode = IDataUtil.getMandaData(inParam, "TRADE_EPARCHY_CODE");
        String userId = inParam.getString("USER_ID_A");

        IData param = new DataMap();
        if (StringUtils.isEmpty(userId))
        {
            param.put("USER_ID", "0000000000000000");
        }
        else
        {
            param.put("USER_ID", userId);
        }
        param.put("EPARCHY_CODE", tradeEparchyCode);

        // 获取用户资费
        //IDataset result = getUserBaseDiscnt(param, productId, pg);

        IData compProductInfo = ProductCompInfoQry.getProductFromComp(productId);

        // 集团定制
        if (StringUtils.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue(), compProductInfo.getString("USE_TAG")))
        {
            String productIdB = UProductMebInfoQry.getMemberMainProductByProductId(productId); 
            memResult = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(productIdB, "D");
            if(IDataUtil.isNotEmpty(memResult))
            {
            	for(int i=0; i<memResult.size(); i++)
            	{
            		IData ele = memResult.getData(i);
            		ele.put("MIN_NUMBER", ele.getString("MIN_NUMBER", "-1"));
            		ele.put("MAX_NUMBER", ele.getString("MAX_NUMBER", "-1"));
            		ele.put("DISCNT_TYPE", "M"); // M 成员优惠
            		ele.put("DISCNT_CODE", ele.getString("ELEMENT_ID"));
            		ele.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("DISCNT_EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("REORDER", UDiscntInfoQry.getOrderModeByDiscntCode(ele.getString("ELEMENT_ID")));
            	}
            }
            returnDs.addAll(memResult);

        }
        else
        {
        	IDataset result = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(productId, "D");
        	if(IDataUtil.isNotEmpty(result))
            {
            	for(int i=0; i<result.size(); i++)
            	{
            		IData ele = result.getData(i);
            		ele.put("MIN_NUMBER", ele.getString("MIN_NUMBER", "-1"));
            		ele.put("MAX_NUMBER", ele.getString("MAX_NUMBER", "-1"));
            		ele.put("DISCNT_TYPE", "U"); // U 集团优惠
            		ele.put("DISCNT_CODE", ele.getString("ELEMENT_ID"));
            		ele.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("DISCNT_EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(ele.getString("ELEMENT_ID")));
            		ele.put("REORDER", UDiscntInfoQry.getOrderModeByDiscntCode(ele.getString("ELEMENT_ID")));
            	}
            }
        	returnDs.addAll(result);
        }
        
        // 保存分页
        if (pg != null && IDataUtil.isNotEmpty(returnDs))
        {
            for (int i = 0; i < returnDs.size(); i++)
            {
                returnDs.getData(i).put("X_PAGE_COUNT", returnDs.size());
            }
        }

        return returnDs;
    }

    /**
     * 获取用户订购时的资费
     * 
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset getUserBaseDiscnt(IData inParam, String productId, Pagination pg) throws Exception
    {
        IDataset result = new DatasetList();
        String userId = inParam.getString("USER_ID");
        //String eparchyCode = inParam.getString("EPARCHY_CODE");
        IDataset packages = UPackageInfoQry.getPackagesByProductId(productId);
        for (int i = 0; i < packages.size(); i++)
        {
            IData tmp = packages.getData(i);
            String packageId = tmp.getString("PACKAGE_ID");

            IDataset element = ProductInfoQry.getMemberPackageElements(userId, productId, packageId, false);
            if (IDataUtil.isNotEmpty(element))
            {
                for (int j = 0; j < element.size(); j++)
                {
                    IData discnt = element.getData(j);
                    if ("D".equals(discnt.getString("ELEMENT_TYPE_CODE")))
                    {
                        discnt.put("MIN_NUMBER", tmp.getString("MIN_NUMBER", "-1"));
                        discnt.put("MAX_NUMBER", tmp.getString("MAX_NUMBER", "-1"));
                        discnt.put("DISCNT_TYPE", "U"); // U 集团优惠
                        discnt.put("DISCNT_CODE", discnt.getString("ELEMENT_ID", ""));
                        discnt.put("DISCNT_NAME", discnt.getString("ELEMENT_NAME", ""));
                        discnt.put("DISCNT_EXPLAIN", discnt.getString("ELEMENT_EXPLAIN", ""));
                    }
                }
            }
            result.addAll(element);
        }
        return result;
    }

    /**
     * 获取用户订购时成员资费
     * 
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset getMemberBaseDiscnt(IDataset memProductList, IData inParam, Pagination pg) throws Exception
    {
        IDataset memResult = new DatasetList();
        //String eparchyCode = inParam.getString("EPARCHY_CODE");
        String userId = inParam.getString("USER_ID");

        for (int i = 0; i < memProductList.size(); i++)
        {
            IData memProduct = memProductList.getData(i);
            String productIdB = memProduct.getString("PRODUCT_ID_B");
            IDataset packages = UPackageInfoQry.getPackagesByProductId(productIdB);
            for (int j = 0; j < packages.size(); j++)
            {
                IData tmp = packages.getData(j);
                String packageId = tmp.getString("PACKAGE_ID");

                IDataset element = ProductInfoQry.getMemberPackageElements(userId, productIdB, packageId, true);
                if (IDataUtil.isNotEmpty(element))
                {
                    for (int k = 0; k < element.size(); k++)
                    {
                        IData discnt = element.getData(k);
                        if ("D".equals(discnt.getString("ELEMENT_TYPE_CODE")))
                        {
                            discnt.put("MIN_NUMBER", tmp.getString("MIN_NUMBER", "-1"));
                            discnt.put("MAX_NUMBER", tmp.getString("MAX_NUMBER", "-1"));
                            discnt.put("DISCNT_TYPE", "M"); // M 成员优惠
                            discnt.put("DISCNT_CODE", discnt.getString("ELEMENT_ID", ""));
                            discnt.put("DISCNT_NAME", discnt.getString("ELEMENT_NAME", ""));
                            discnt.put("DISCNT_EXPLAIN", discnt.getString("ELEMENT_EXPLAIN", ""));
                            memResult.add(discnt);
                        }
                    }
                }
            }

        }

        return memResult;
    }

    /**
     * 集团产品下成员列表查询接口(带分页查询)
     * 
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset qryGrpMemberListInfo(IData inParam, Pagination pg) throws Exception
    {
        String groupId = IDataUtil.getMandaData(inParam, "GROUP_ID");// 集团编码
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");// 产品编码
        String userId = IDataUtil.getMandaData(inParam, "USER_ID");// 集团用户ID

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put("USER_ID_A", userId);

        IDataset productUserInfoList = UserInfoQry.getUserInfoByGPUID(param, pg);

        IData productMap = UProductInfoQry.qryProductByPK(productId);

        if (IDataUtil.isNotEmpty(productMap) && IDataUtil.isNotEmpty(productUserInfoList))
        {
            for (int i = 0; i < productUserInfoList.size(); i++)
            {
                IData productUserInfo = productUserInfoList.getData(i);
                productUserInfo.put("PRODUCT_NAME", productMap.getString("PRODUCT_NAME"));
            }
        }

        param.clear();
        param.put("USER_ID_A", userId);
        param.put("PRODUCT_ID", productId);

        String count = GroupInfoQueryDAO.getUsrMemCount(param).getData(0).getString("TOTAL_NUM", "");

        for (int i = 0; i < productUserInfoList.size(); i++)
        {
            productUserInfoList.getData(0).put("TOTAL_COUNT", count);// 集团成员总数量
        }

        return productUserInfoList;

    }

    /**
     * 查询集团成员用户订购的集团业务(集团V网、集团彩铃、农信通、校讯通、手机邮箱等)
     * 
     * @author liuxx3
     * @date 2014-07-08
     */
    public IDataset qryGrpMebOrderInfo(IData inParam, Pagination pg) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");

        IData userData = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, BizRoute.getRouteId());

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        String userId = userData.getString("USER_ID");

        IDataset relaList = RelaUUInfoQry.qryRelaAllByUserIdB(userId, pg);

        IDataset returnList = new DatasetList();

        IDataset configList = ParamInfoQry.qryTdBQryConfig();

        for (int i = 0; i < relaList.size(); i++)
        {
            IData relaData = relaList.getData(i);

            String userIdA = relaData.getString("USER_ID_A");
            String userIdB = relaData.getString("USER_ID_B");

            IData grpUserData = UcaInfoQry.qryUserInfoByUserIdForGrp(userIdA);

            if (IDataUtil.isEmpty(grpUserData))
            {
                continue;
            }

            String grpCustId = grpUserData.getString("CUST_ID");

            IData grpCustData = UcaInfoQry.qryCustomerInfoByCustIdForGrp(grpCustId);

            if (IDataUtil.isEmpty(grpCustData))
            {
                continue;
            }

            String grpCustName = grpCustData.getString("CUST_NAME");

            IData grpUserProductData = UcaInfoQry.qryMainProdInfoByUserIdForGrp(userIdA);

            if (IDataUtil.isEmpty(grpUserProductData))
            {
                continue;
            }

            String productId = grpUserProductData.getString("PRODUCT_ID");

            if (IDataUtil.isEmpty(DataHelper.filter(configList, "ID=" + productId)))
            {
                continue;
            }

            String productExplan = ProductInfoQry.getProductExplanByProductId(productId);

            IDataset userDiscntList = UserDiscntInfoQry.getUserDiscntByUserIdUserIdA(userIdB, userIdA);

            String discntCode = "";
            String discntExplan = "";

            if (IDataUtil.isNotEmpty(userDiscntList))
            {
                discntCode = userDiscntList.getData(0).getString("DISCNT_CODE");

                discntExplan = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
            }
            
            //如果是集团彩铃成员，账务侧收取了6元，但是CRM这边并没有对应的资费， 这个接口有问题，暂时写死
            if (StringUtils.equals("6200", productId))
            {
                discntCode = "950";
                discntExplan = "集团彩铃功能费6元";
            }

            relaData.put("PRODUCT_ID", productId);
            relaData.put("PRODUCT_NAME", productExplan + "-" + grpCustName);
            relaData.put("DISCNT_CODE", discntCode);
            relaData.put("DISCNT_EXPLAIN", discntExplan);
            relaData.put("GROUP_ID", grpCustData.getString("GROUP_ID"));
            relaData.put("CUST_NAME", grpCustData.getString("CUST_NAME"));
            relaData.put("UPDATE_TIME", userData.getString("UPDATE_TIME"));
            relaData.put("BIZ_STATE_CODE", "");
            relaData.put("BILLFLG", "");
            relaData.put("SERV_ATTR", "");
            relaData.put("REMARK", userData.getString("REMARK"));
            relaData.put("CREATE_STAFF_ID", "");
            relaData.put("CREATE_DEPART_ID", "");
            relaData.put("PRICE", "");
            relaData.put("UNIT", "");

            IDataset grpMebOrder = ParamInfoQry.getCommparaByCode1("CSM", "9912", "GRP_MEB_ORDER", discntCode, "ZZZZ");

            if (IDataUtil.isNotEmpty(grpMebOrder)) {
                relaData.put("PRICE", grpMebOrder.getData(0).getString("PARA_CODE2"));
                relaData.put("UNIT", grpMebOrder.getData(0).getString("PARA_CODE3"));
            }

            returnList.add(relaData);
        }
        return returnList;
    }

    /**
     * 为esop查询BBOSS商品订单号和产品订单号 add by esop
     * 
     * @author liuxx3
     * @date 2014-06-30
     */

    public IDataset getBBossOrderInfoForEsop(IData inParam, Pagination pg) throws Exception
    {
        String merchtradeId = inParam.getString("TRADE_ID", "0000");

        IDataset merchInfos = TradeGrpMerchInfoQry.getBBossMerchInfoByOrderId(merchtradeId, pg);

        if (IDataUtil.isEmpty(merchInfos))
        {
            return new DatasetList();
        }

        String orderId = merchInfos.getData(0).getString("ORDER_ID");

        IDataset merchPInfos = TradeGrpMerchpInfoQry.getBBossMerchPInfoByOrderId(orderId, pg);

        if (IDataUtil.isEmpty(merchPInfos))
        {
            return new DatasetList();
        }

        for (int i = 0; i < merchPInfos.size(); i++)
        {
            IData merchPInfo = merchPInfos.getData(i);

            merchPInfo.put("MERCH_ORDER_ID", merchInfos.getData(0).getString("MERCH_ORDER_ID", ""));
        }

        return merchPInfos;
    }

    /**
     * 自由充流量产品
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public IDataset qryGrpGfffMebOrderInfo(IData inParam, Pagination pg) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER_A");
        
        IData userData = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, BizRoute.getRouteId());

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        String userId = userData.getString("USER_ID");
        String productId = userData.getString("PRODUCT_ID");
        String relaTypeCode = "";
        String opertTypeS = "";
        
        if("7344".equals(productId)){//流量自由充(限量统付)产品
            relaTypeCode = "T7";
            opertTypeS = "5";
        } else if("7342".equals(productId)){//流量自由充(全量统付)产品
            relaTypeCode = "T5";
            opertTypeS = "3";
        } else if("7343".equals(productId)){//流量自由充(定额统付)产品
            relaTypeCode = "T6";
            opertTypeS = "4";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_876,serialNumber);
        }
        
        IData paramData = new DataMap();
        paramData.put("USER_ID_A", userId);
        paramData.put("RELATION_TYPE_CODE", relaTypeCode);
        
        String serialNumberB = inParam.getString("SERIAL_NUMBER_B","");
        if(StringUtils.isNotBlank(serialNumberB)){
            paramData.put("SERIAL_NUMBER_B", serialNumberB);
        }
        
        String discntCode = inParam.getString("DISCNT_CODE","");
        if(StringUtils.isNotBlank(discntCode)){
            paramData.put("DISCNT_CODE", discntCode);
        }
        String startDate = inParam.getString("START_DATE","");
        if(StringUtils.isNotBlank(startDate)){
            paramData.put("START_DATE", startDate);
        }
        String endDate = inParam.getString("END_DATE","");
        if(StringUtils.isNotBlank(endDate)){
            paramData.put("END_DATE", endDate);
        }
        
        IDataset returnList = new DatasetList();
        
        paramData.put("MP_GROUP_CUST_CODE", userId);
        paramData.put("OPER_TYPE", opertTypeS);
        paramData.put("PRODUCT_OFFER_ID", productId);
        
        returnList = UserGrpInfoQry.queryAllMebCenPayInfoByUserId(paramData,pg);
        
        return returnList;
    }
    
    /**
     * 定额统付集团查询
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public IDataset qryGrpGfffMebDiscntCountByUserId(IData inParam) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER_A");
        
        IData userData = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, BizRoute.getRouteId());

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        String userId = userData.getString("USER_ID");
        String productId = userData.getString("PRODUCT_ID");
        String relaTypeCode = "";
        
        if("7343".equals(productId)){//流量自由充(定额统付)产品
            relaTypeCode = "T6";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_872,serialNumber);
        }
        
        IData paramData = new DataMap();
        paramData.put("USER_ID_A", userId);
        paramData.put("RELATION_TYPE_CODE", relaTypeCode);
        paramData.put("OPER_TYPE", "4");
        IDataset returnList = UserGrpInfoQry.queryMebCenPayDiscntCountByUserId(paramData);

        return returnList;
    }
    
    /**
     * 根据GroupId查询集团管理员手机号
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public IDataset qryGrpMgrSerialNumberByGroupId(IData inParam) throws Exception
    {
        String groupId = IDataUtil.chkParam(inParam, "GROUP_ID");
        
        //IDataset custInfoList = GrpInfoQry.qryGrpInfoByGroupIdAndRemoveTag(groupId,"0");
        
        IData paramData = new DataMap();
        paramData.put("GROUP_ID", groupId);
        paramData.put("REMOVE_TAG", "0");
        
        IDataset custInfoList = GrpUserQryIntf.qryGrpCustInfo(paramData);
        
        if (IDataUtil.isEmpty(custInfoList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_142, groupId);
        }

        IData custData = new DataMap();
        custData.put("GROUP_MGR_SN", custInfoList.getData(0).getString("GROUP_MGR_SN",""));
        IDataset returnList = new DatasetList();
        returnList.add(custData);
        
        return returnList;
    }
    
    /**
     * 获取集团用户服务和资费
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public IDataset getGrpUserServiceDiscnt(IData inParam, Pagination pg) throws Exception
    {
        IDataset returnDs = new DatasetList();
        IDataset memResult = new DatasetList();
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String tradeEparchyCode = IDataUtil.getMandaData(inParam, "TRADE_EPARCHY_CODE");
        String userId = inParam.getString("USER_ID_A");

        IData param = new DataMap();
        if (StringUtils.isEmpty(userId))
        {
            param.put("USER_ID", "0000000000000000");
        }
        else
        {
            param.put("USER_ID", userId);
        }
        param.put("EPARCHY_CODE", tradeEparchyCode);

        // 获取用户资费
        IDataset result = getUserBaseDiscntNew(param, productId, pg);
//        String useTag = "0";
//        if (StringUtils.isNotBlank(productId))
//        {
//            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
//            if (StringUtils.isNotEmpty(userTagTemp))
//                useTag = userTagTemp;
//        }
//        if (StringUtils.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue(), useTag))
//        {
//            IDataset memProductList = ProductUtil.getMebProduct(productId);
//            param.put("PRODUCT_ID", productId);
//            memResult = getMemberBaseDiscntNew(memProductList, param, pg);
//            returnDs.addAll(memResult);
//
//        }
        returnDs.addAll(result);

        // 保存分页
        if (pg != null && IDataUtil.isNotEmpty(returnDs))
        {
            for (int i = 0; i < returnDs.size(); i++)
            {
                returnDs.getData(i).put("X_PAGE_COUNT", returnDs.size());
            }
        }

        return returnDs;
    }
    
    /**
     * 获取用户订购时的资费
     *
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset getUserBaseDiscntNew(IData inParam, String productId, Pagination pg) throws Exception
    {
        IDataset result = new DatasetList();
        String userId = inParam.getString("USER_ID");
        //String eparchyCode = inParam.getString("EPARCHY_CODE");
        String useTag = "0";
        if (StringUtils.isNotBlank(productId))
        {
            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
            if (StringUtils.isNotEmpty(userTagTemp))
                useTag = userTagTemp;
        }
        
        IDataset element = new DatasetList();
        
        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", productId);
        inparams.put("USER_ID", userId);
        
        // 定制
        if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag)&&!"0000000000000000".equals(userId))
        {
            element = Dao.qryByCodeParser("TF_F_USER_GRP_PACKAGE", "SEL_BY_PK", inparams, Route.CONN_CRM_CG);
            if(IDataUtil.isNotEmpty(element))
            {
            	for(int i=0; i<element.size(); i++)
            	{
            		IData ele = element.getData(i);
            		String elementType = ele.getString("ELEMENT_TYPE_CODE");
            		String elementId = ele.getString("ELEMENT_ID");
            		String elementName = "";
            		String orderMode = "";
            		if(elementType.equals("S"))
            		{
            			elementName = USvcInfoQry.getSvcNameBySvcId(elementId);
            			orderMode = USvcInfoQry.getOrderModeBySvcId(elementId);
            		}
            		if(elementType.equals("D"))
            		{
            			elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
            			orderMode = UDiscntInfoQry.getOrderModeByDiscntCode(elementId);
            		}
            		ele.put("ELEMENT_NAME", elementName);
            		ele.put("ORDER_MODE", orderMode);
            	}
            }
        }
        else
        {
        	/*IDataset svcElement = USvcInfoQry.getSvcByProduct(productId);
            if(IDataUtil.isNotEmpty(svcElement))
            	element.addAll(svcElement);
            IDataset discntElement = UDiscntInfoQry.getDiscntByProduct(productId);
            if(IDataUtil.isNotEmpty(discntElement))
            	element.addAll(discntElement);*/
            element = UProductElementInfoQry.getElementInfosByProductId(productId);
            //element = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKAGE_ELEMENT_NO_PRIV", inparams, Route.CONN_CRM_CG);
        }
        
        if (IDataUtil.isNotEmpty(element))
        {
            for (int j = 0; j < element.size(); j++)
            {
                IData serviceInfo = element.getData(j);
                if ("S".equals(serviceInfo.getString("ELEMENT_TYPE_CODE")))
                {
                    serviceInfo.put("SERVICE_CODE", serviceInfo.getString("ELEMENT_ID"));
                    serviceInfo.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serviceInfo.getString("ELEMENT_ID")));
                    serviceInfo.put("DISCNT_CODE", "");
                    serviceInfo.put("DISCNT_NAME", "");
                    serviceInfo.put("DISCNT_EXPLAIN", "");
                }
                if ("D".equals(serviceInfo.getString("ELEMENT_TYPE_CODE")))
                {
                    serviceInfo.put("DISCNT_CODE", serviceInfo.getString("ELEMENT_ID"));
                    serviceInfo.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(serviceInfo.getString("ELEMENT_ID")));
                    serviceInfo.put("DISCNT_EXPLAIN", UDiscntInfoQry.getDiscntExplainByDiscntCode(serviceInfo.getString("ELEMENT_ID")));
                    
                    serviceInfo.put("SERVICE_CODE", "");
                    serviceInfo.put("SERVICE_NAME", "");
                }
            }
        }
        result.addAll(element);
        return result;
    }
    
    /**
     * 获取集团服务资费的成员产品信息
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public IDataset getGrpUserServiceDiscnt4Child(IData inParam, Pagination pg) throws Exception
    {
        IDataset returnDs = new DatasetList();
        IDataset memResult = new DatasetList();
        String productId = IDataUtil.getMandaData(inParam, "PRODUCT_ID");
        String tradeEparchyCode = IDataUtil.getMandaData(inParam, "TRADE_EPARCHY_CODE");
        String userId = inParam.getString("USER_ID_A");
        String serviceCode = inParam.getString("SERVICE_CODE");
        String dicntCode =  inParam.getString("DISCNT_CODE");
        IData param = new DataMap();
        if (StringUtils.isEmpty(userId))
        {
            param.put("USER_ID", "0000000000000000");
        }
        else
        {
            param.put("USER_ID", userId);
        }
        param.put("EPARCHY_CODE", tradeEparchyCode);
        param.put("SERVICE_CODE", serviceCode);
        param.put("DISCNT_CODE", dicntCode);
        // 获取用户资费
        //IDataset result = getUserBaseDiscntNew(param, productId, pg);
        String useTag = "0";
        if (StringUtils.isNotBlank(productId))
        {
            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
            if (StringUtils.isNotEmpty(userTagTemp))
                useTag = userTagTemp;
        }
        if (StringUtils.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue(), useTag))
        {
            IDataset memProductList = ProductUtil.getMebProduct(productId);
            if(IDataUtil.isNotEmpty(memProductList)){
                param.put("PRODUCT_ID", productId);
                memResult = getMemberBaseDiscntNew(memProductList, param, pg);
                returnDs.addAll(memResult);
            }
        }
        // 保存分页
        if (pg != null && IDataUtil.isNotEmpty(returnDs))
        {
            for (int i = 0; i < returnDs.size(); i++)
            {
                returnDs.getData(i).put("X_PAGE_COUNT", returnDs.size());
            }
        }

        return returnDs;
    }
    
    public IDataset getMemberBaseDiscntNew(IDataset memProductList, IData inParam, Pagination pg) throws Exception
    {
        IDataset memResult = new DatasetList();
        //String eparchyCode = inParam.getString("EPARCHY_CODE");
        //String userId = inParam.getString("USER_ID");
        String serviceCodeStr = inParam.getString("SERVICE_CODE");
        String serviceCodeParam[] = serviceCodeStr.split(",");
        if(null!=serviceCodeParam&&serviceCodeParam.length>0){
            for(int t=0;t<serviceCodeParam.length;t++){
                String serviceCode = serviceCodeParam[t];
                String mebServerId = MemParams.getServIdByGrpORmebServId(serviceCode);//根据集团的服务查询对应的成员服务
                IData memProduct = memProductList.getData(0);
                String productIdB = memProduct.getString("PRODUCT_ID_B");
                IDataset element = new DatasetList(); 
                IDataset svcElement = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(productIdB, "S");
                if(IDataUtil.isNotEmpty(svcElement))
                	element.addAll(svcElement);
                /*IDataset discntElement = UDiscntInfoQry.getDiscntByProduct(productIdB);
                if(IDataUtil.isNotEmpty(discntElement))
                	element.addAll(discntElement);*/
                if (IDataUtil.isNotEmpty(element))
                {
                    for (int k = 0; k < element.size(); k++)
                    {
                        IData discnt = element.getData(k);
                        if ("D".equals(discnt.getString("ELEMENT_TYPE_CODE")))
                        {
                            //暂不支持，根据主产品优惠获取到对应的成员优惠
                            continue;
                        }
                        if ("S".equals(discnt.getString("ELEMENT_TYPE_CODE")))
                        {
                            //根据主产品服务获取到对应的成员服务，再根据成员服务获取到所依赖的成员优惠
                            if(StringUtils.isNotBlank(serviceCode)){
                                String elementId = discnt.getString("ELEMENT_ID", "");
                                if(elementId.equals(mebServerId)){
                                    discnt.put("PRODUCT_ID_A", productIdB);//子产品
                                    discnt.put("SERVICE_CODE", serviceCode);//子产品
                                    discnt.put("SERVICE_CODE_A", elementId);
                                    discnt.put("SERVICE_NAME_A", USvcInfoQry.getSvcNameBySvcId(elementId));
                                    discnt.put("PACKAGE_ID_SVC", discnt.getString("PACKAGE_ID", ""));
                                    
                                    IDataset limitElements = UElementLimitInfoQry.queryElementLimitByElementIdB(elementId, "S", "1");
                                    if(IDataUtil.isNotEmpty(limitElements)&&"D".equals(limitElements.getData(0).getString("ELEMENT_TYPE_CODE_A"))){//成员服务依赖的成员优惠
                                        String discntCode4Child = limitElements.getData(0).getString("ELEMENT_ID_A");
                                        IData discntName4Child = UDiscntInfoQry.getDiscntInfoByPk(discntCode4Child);
                                        if(IDataUtil.isNotEmpty(discntName4Child)){
                                            IDataset packageInfo = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productIdB, discntCode4Child, "D");
                                            if(IDataUtil.isNotEmpty(packageInfo)){
                                                discnt.put("PACKAGE_ID_DISCNT", packageInfo.getData(0).get("PACKAGE_ID"));
                                            }
                                            discnt.put("DISCNT_CODE_A", discntCode4Child);
                                            discnt.put("DISCNT_NAME_A", discntName4Child.get("DISCNT_NAME"));//优惠名称
                                            discnt.put("DISCNT_EXPLAIN_A", discntName4Child.get("DISCNT_EXPLAIN"));//优惠描述
                                        }
                                    }
                                    if(IDataUtil.isNotEmpty(discnt)&&!discnt.containsKey("DISCNT_CODE_A")){
                                        discnt.put("PACKAGE_ID_DISCNT", "");
                                        discnt.put("DISCNT_CODE_A", "");
                                        discnt.put("DISCNT_NAME_A", "");//优惠名称
                                        discnt.put("DISCNT_EXPLAIN_A", "");//优惠描述
                                    }
                                    memResult.add(discnt);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return memResult;
    }
    
}
