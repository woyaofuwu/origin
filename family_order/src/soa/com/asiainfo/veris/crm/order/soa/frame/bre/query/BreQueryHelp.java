
package com.asiainfo.veris.crm.order.soa.frame.bre.query;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;

public class BreQueryHelp extends BreBase
{
    private static Logger logger = Logger.getLogger(BreQueryHelp.class);

    public static IData getMainTrade(IData param) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(">>>>>>>>>>>>>>>>>>>> getMainTrade in start " + System.currentTimeMillis());

        param.put("CANCEL_TAG", "0");

        IData data = UTradeInfoQry.qryTradeByTradeId(param.getString("TRADE_ID"), "0");

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("<<<<<<<<<<<<<<<<<<<< getMainTrade in end   " + System.currentTimeMillis());

        return data;
    }

    /**
     * 根据编码获取名字
     * 
     * @param strName
     * @param strCode
     * @return
     * @throws Exception
     */
    public static String getNameByCode(String strKey, String strCode) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> getNameByCode[" + strKey + "][" + strCode + "]");
        }

        String strColName = "";
        String strTabName = "";
        String strColKey = "";
        //产商品改造
        String name="";

        if (strKey.equals("ServiceName") || strKey.equals("SERVICE_NAME") || strKey.equals("SERVICE_ID"))
        {
            strColKey = "SERVICE_ID";
            strColName = "SERVICE_NAME";
            strTabName = "TD_B_SERVICE";
            name=USvcInfoQry.getSvcNameBySvcId(strCode);
        }
        else if (strKey.equals("DiscntName") || strKey.equals("DISCNT_NAME") || strKey.equals("DISCNT_CODE"))
        {
            strColKey = "DISCNT_CODE";
            strColName = "DISCNT_NAME";
            strTabName = "TD_B_DISCNT";
            name=UDiscntInfoQry.getDiscntNameByDiscntCode(strCode);
        }
        else if (strKey.equals("PackageName") || strKey.equals("PACKAGE_ID") || strKey.equals("PACKAGE_NAME"))
        {
            strColKey = "PACKAGE_ID";
            strColName = "PACKAGE_NAME";
            strTabName = "TD_B_PACKAGE";
            name=UPackageInfoQry.getNameByPackageId(strCode);
        }
        else if (strKey.equals("ProductName") || strKey.equals("PRODUCT_NAME") || strKey.equals("PRODUCT_ID"))
        {
            strColKey = "PRODUCT_ID";
            strColName = "PRODUCT_NAME";
            strTabName = "TD_B_PRODUCT";
            name=UProductInfoQry.getProductNameByProductId(strCode);
        }
        else if (strKey.equals("BrandName") || strKey.equals("BRAND_CODE") || strKey.equals("BRAND_NAME"))
        {
            strColKey = "BRAND_CODE";
            strColName = "BRAND";
            strTabName = "TD_S_BRAND";
            name=UBrandInfoQry.getBrandNameByBrandCode(strCode);
        }
        return name;
        //return StaticUtil.getStaticValue(getVisit(), strTabName, strColKey, strColName, strCode);
    }

    public static String getNameByCode(String strKey, String[] strValues) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> getNameByCode[" + strKey + "][" + strValues + "]");

        String strColName = "", strTabName = "";
        String[] strColKey = null;
        IData param = new DataMap();

        if (strKey.equals("SvcStateName") || strKey.equals("STATE_NAME"))
        {
            strColKey = new String[]
            { "SERVICE_ID", "STATE_CODE" };
            strColName = "STATE_NAME";
            strTabName = "TD_S_SERVICESTATE";
            IDataset prodStaData = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(strValues[0],BofConst.ELEMENT_TYPE_CODE_SVC,strValues[1]);  
            if(IDataUtil.isEmpty(prodStaData))
            {
                return "";
            } 
            return prodStaData.getData(0).getString("STATUS_NAME");
        }

        return StaticUtil.getStaticValue(getVisit(), strTabName, strColKey, strColName, strValues);
    }

    /**
     * 根据手机号码获取路由地州
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static String getRouteEparchyBySn(String strSerialNumber) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> getRouteEparchyBySn + [" + strSerialNumber + "]");

        if ("".equals(strSerialNumber))
        {
            return null;
        }

        // 查询moffice号段表
        String eparchyCode = RouteInfoQry.getEparchyCodeBySn(strSerialNumber);

        String strRouteEparchyCode = null;

        if (StringUtils.isNotBlank(eparchyCode))
        {
            strRouteEparchyCode = eparchyCode;
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< getRouteEparchyBySn + [" + strSerialNumber + "][" + strRouteEparchyCode + "]");

        return strRouteEparchyCode;
    }

    public static IDataset getRuleFlow(IData param) throws Exception
    {
        String strTradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String strOrderTypeCode = param.getString("ORDER_TYPE_CODE");

        IDataset listAllRule = null;

        if ("*".equals(strOrderTypeCode) || strTradeTypeCode.equals(strOrderTypeCode))
        {
            listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_ALL_RULE", param, Route.CONN_CRM_CEN);
        }
        else
        // order_type_code不等于trade_type_code情况
        {
            IDataset orderBizRuleInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_ORDER_TRADE_BIZ_RULEID", param, Route.CONN_CRM_CEN);
            if (IDataUtil.isEmpty(orderBizRuleInfos))// 该order_type_code/trade_type_code不存在任何配置数据，则执行trade_type_code所有的规则
            {
                listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_ALL_RULE", param, Route.CONN_CRM_CEN);
            }
            else
            {
                // 存在配置为-1的数据，则代表不需要执行任何规则 直接返回,注意sql中有对rule_biz_id升序排序
                if (StringUtils.equals("-1", orderBizRuleInfos.getData(0).getString("RULE_BIZ_ID")))
                {
                    return new DatasetList();
                }
                else
                {
                    if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                        logger.debug("在TD_BRE_ORDER_RULE中找到配置，走SEL_ALL_RULE_BY_ORDER_CFG查询条件");
                    listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_ALL_RULE_BY_ORDER_CFG", param, Route.CONN_CRM_CEN);
                }
            }
        }

        /* 如果没有找到规则就直接返回 */
        if (listAllRule.size() == 0)
        {
            return listAllRule;
        }
        else
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" CCCCCCCCCCCCCC 查询到规则数据[" + listAllRule.size() + "]");
        }

        /* 判断产品， 品牌，superusr， 和员工权限 */
        String strRightCode = "";
        String strRuleProductId = "";
        String strRuleBrandCode = "";
        String strRulePackageId = "";
        IDataset idsRet = new DatasetList();
        boolean priv = false;
        boolean isSupserUser = "SUPERUSR".equals(getVisit().getStaffId());

        String strProductId = param.getString("PRODUCT_ID");
        String strBrandCode = param.getString("BRAND_CODE");
        String strPackageId = param.getString("PACKAGE_ID");

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("UUUUUUUUUUU 用户信息 BRAND_CODE=[" + strBrandCode + "];PRODUCT_ID=[" + strProductId + "];PACKAGE_ID=[" + strPackageId + "]");
        }

        for (Iterator iterator = listAllRule.iterator(); iterator.hasNext();)
        {
            IData bizrule = (IData) iterator.next();

            /* 先判断产品和品牌 */
            strRuleProductId = bizrule.getString("PRODUCT_ID");
            strRuleBrandCode = bizrule.getString("BRAND_CODE");
            strRulePackageId = bizrule.getString("PACKAGE_ID");

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug("UUUUUUUUUUU BREBIZCFG BRAND_CODE=[" + strRuleBrandCode + "];PRODUCT_ID=[" + strRuleProductId + "];PACKAGE_ID=[" + strRulePackageId + "]");
            }

            if (("-1".equals(strRuleProductId) || strRuleProductId.equals(strProductId)) && ("ZZZZ".equals(strRuleBrandCode) || strRuleBrandCode.equals(strBrandCode)) && ("-1".equals(strRulePackageId) || strRulePackageId.equals(strPackageId)))
            {
                strRightCode = bizrule.getString("RIGHT_CODE", "");

                /* SUPERUSR 工号的特殊处理 */
                if (isSupserUser)
                {
                    if (bizrule.getString("RIGHT_CODE") == null || StringUtils.isBlank(bizrule.getString("RIGHT_CODE")))
                    {
                        idsRet.add(bizrule);
                    }
                }
                else
                {
                    if (StringUtils.isBlank(strRightCode))
                    {
                        idsRet.add(bizrule);
                        continue;
                    }

                    /* 判断数据权限 */
                    priv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), strRightCode);

                    /* 员工有权限就跳过，不判断规则 */
                    if (!priv)
                    {
                        idsRet.add(bizrule);
                    }
                    else
                    {
                        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                            logger.debug(" QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ [" + bizrule.getString("RULE_BIZ_ID") + "][" + strRightCode + "]");
                    }
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" GGGGGGGGGGGGGGGGGGG 过滤产品，品牌，SUPERUSR,和权限后， 剩下规则数据[" + idsRet.size() + "]");

        return idsRet;
    }

    public static boolean isParallel() throws Exception
    {
        IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode("ZZZZ", "BRE_IS_PARALLEL", "BRE", "0");

        if (listTag.size() == 0)
        {
            return false;
        }

        return "1".equals(listTag.getData(0).getString("TAG_CHAR")) ? true : false;
    }

    /**
     * add by gaoyuan @2013-12-04 start desc :: 特殊RULE_BIZ_ID,保留,其他RULE_BIZ_ID直接过滤掉 调用方： BreEngine.getRuleList
     * 
     * @param list
     *            -- 查询出来的规则列表
     * @param listBizId
     *            -- 需要保留的RULE_BIZ_ID列表
     * @return
     * @throws Exception
     */
    public static IDataset specListBizId(IDataset list, IDataset listBizId) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" 传入指定 RULE_BIZ_ID 的特殊过滤处理 start");

        boolean bRlist = (listBizId != null && listBizId.size() > 0);
        IDataset listTmp = null;

        if (bRlist)
        {
            listTmp = new DatasetList();

            for (Iterator iter = list.iterator(); iter.hasNext();)
            {
                IData rule = (IData) iter.next();

                for (Iterator iterator = listBizId.iterator(); iterator.hasNext();)
                {
                    IData bizid = (IData) iterator.next();

                    if (rule.getString("RULE_BIZ_ID").equals(bizid.getString("RULE_BIZ_ID")))
                    {
                        listTmp.add(rule);
                        break;
                    }
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" 传入指定 RULE_BIZ_ID 的特殊过滤处理 end");

        return bRlist ? listTmp : list;
    }
    public static void fillTradeProductIdAndPackageId(IData databus,IDataset elementList) throws Exception
    {
        IDataset tradeOfferRels = databus.getDataset("TF_B_TRADE_OFFER_REL");

        if (IDataUtil.isEmpty(tradeOfferRels))
        {
            return;
        }
        if (IDataUtil.isEmpty(elementList))
        {
            return;
        }
        for (int i = 0; i < elementList.size(); i++)
        {
            IData element = elementList.getData(i);
            String modifyTag = element.getString("MODIFY_TAG","");
            String instId = element.getString("INST_ID","");
            if(tradeOfferRels != null && tradeOfferRels.size() > 0){
            	 for (int j = 0; j < tradeOfferRels.size(); j++){
                    IData tradeOfferRel = tradeOfferRels.getData(j);
                    if(instId.equals(tradeOfferRel.getString("REL_OFFER_INS_ID"))&&modifyTag.equals(tradeOfferRel.getString("MODIFY_TAG")))
                    {
                        element.put("PRODUCT_ID", tradeOfferRel.getString("OFFER_CODE"));
                        element.put("PACKAGE_ID", tradeOfferRel.getString("GROUP_ID"));
                        continue;
    				}
    			}
    		}
        }
    }
}
