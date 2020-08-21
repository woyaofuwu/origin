
package com.asiainfo.veris.crm.order.soa.script.query;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;

/**
 * 产品相关查询
 * 
 * @author Administrator
 */
public class BreQryForProduct extends BreBase
{
    private static Logger logger = Logger.getLogger(BreQryForProduct.class);

    private final static StringBuilder strbEndDate = new StringBuilder("2030-12-31").append(SysDateMgr.START_DATE_FOREVER);

    /**
     * 固定所有元素结束时间
     * 
     * @param listPackage
     * @param listElement
     * @throws Exception
     */
    public static IDataset fixedDate(IDataset list, String strUserId) throws Exception
    {
        IDataset ls = new DatasetList();

        for (Iterator iter = list.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();

            if (element.containsKey("END_DATE") && strbEndDate.toString().compareTo(element.getString("END_DATE")) < 0)
            {
                element.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            }

            if (strUserId.equals(element.getString("USER_ID")))
            {
                ls.add(element);
            }
        }

        return ls;
    }

    /**
     * 把服务，资费数据拼装成元素数据
     * 
     * @param listElement
     * @param listSvc
     * @param listDiscnt
     * @throws Exception
     */
    public static IDataset getAllElement(IDataset listElement, IDataset listSvc, IDataset listDiscnt) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("rule 进入 prodcheck getTradeElement() 函数");

        IData tmp = null;
        String strEndDate = null;

        for (Iterator iter = listSvc.iterator(); iter.hasNext();)
        {
            tmp = new DataMap();
            IData element = (IData) iter.next();

            tmp.put("TRADE_ID", element.getString("TRADE_ID"));
            tmp.put("ELEMENT_TYPE_CODE", "S");
            tmp.put("USER_ID", element.getString("USER_ID"));
            tmp.put("ID_TYPE", "0");
            tmp.put("USER_ID_A", element.getString("USER_ID_A"));
            tmp.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
            tmp.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
            tmp.put("ELEMENT_ID", element.getString("SERVICE_ID"));
            tmp.put("START_DATE", element.getString("START_DATE"));

            strEndDate = element.getString("END_DATE");
            if (strEndDate.compareTo("2030-12-31") >= 0)
            {
                strEndDate = SysDateMgr.END_DATE_FOREVER;
            }
            tmp.put("END_DATE", strEndDate);
            tmp.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
            tmp.put("INST_ID", element.getString("INST_ID"));
            tmp.put("IS_NEED_PF", element.getString("IS_NEED_PF"));

            listElement.add(tmp);
        }

        for (Iterator iter = listDiscnt.iterator(); iter.hasNext();)
        {
            tmp = new DataMap();
            IData element = (IData) iter.next();

            tmp.put("TRADE_ID", element.getString("TRADE_ID"));
            tmp.put("ELEMENT_TYPE_CODE", "D");
            tmp.put("USER_ID", element.getString("USER_ID"));
            tmp.put("ID_TYPE", "0");
            tmp.put("USER_ID_A", element.getString("USER_ID_A"));
            tmp.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
            tmp.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
            tmp.put("ELEMENT_ID", element.getString("DISCNT_CODE"));
            tmp.put("START_DATE", element.getString("START_DATE"));

            strEndDate = element.getString("END_DATE");
            if (strEndDate.compareTo("2030-12-31") >= 0)
            {
                strEndDate = SysDateMgr.END_DATE_FOREVER;
            }
            tmp.put("END_DATE", strEndDate);
            tmp.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
            tmp.put("INST_ID", element.getString("INST_ID"));
            tmp.put("IS_NEED_PF", element.getString("IS_NEED_PF"));
            listElement.add(tmp);
        }

        if (logger.isDebugEnabled())
            logger.debug("rule 退出 prodcheck getTradeElement() 函数");

        return listElement;
    }

    /**
     * 把服务，资费数据拼装包操作
     * 
     * @param listElement
     * @param listSvc
     * @param listDiscnt
     * @throws Exception
     */
    public static IDataset getAllPackage(IDataset listPackage, IDataset listElement) throws Exception
    {
        boolean bExists = true;

        for (int eidx = 0, iSize = listElement.size(); eidx < iSize; eidx++)
        {
            bExists = true;

            IData elmt = listElement.getData(eidx);
            for (int kidx = 0, iiSize = listPackage.size(); kidx < iiSize; kidx++)
            {
                IData pkg = listPackage.getData(kidx);
                if (StringUtils.equals(elmt.getString("PACKAGE_ID", "-1"), pkg.getString("PACKAGE_ID")) && StringUtils.equals(elmt.getString("PRODUCT_ID"), pkg.getString("PRODUCT_ID"))
                        && StringUtils.equals(elmt.getString("USER_ID_A", ""), pkg.getString("USER_ID_A", "")))
                {
                    if (elmt.getString("START_DATE").compareTo(pkg.getString("START_DATE")) < 0 && !"1".equals(elmt.getString("MODIFY_TAG")))
                    {
                        pkg.put("START_DATE", elmt.getString("START_DATE"));
                    }

                    if (elmt.getString("END_DATE").compareTo(pkg.getString("END_DATE")) > 0)
                    {
                        pkg.put("END_DATE", elmt.getString("END_DATE"));
                    }

                    if ("1".equals(pkg.getString("MODIFY_TAG")) && !"1".equals(elmt.getString("MODIFY_TAG")))
                    {
                        pkg.put("MODIFY_TAG", elmt.getString("MODIFY_TAG"));
                    }

                    bExists = false;
                    break;
                }
            }

            if (bExists)
            {
                IData tmp = new DataMap();

                tmp.put("TRADE_ID", elmt.getString("TRADE_ID"));
                tmp.put("USER_ID", elmt.getString("USER_ID"));
                tmp.put("PACKAGE_ID", elmt.getString("PACKAGE_ID"));
                tmp.put("START_DATE", elmt.getString("START_DATE"));
                tmp.put("MODIFY_TAG", elmt.getString("MODIFY_TAG"));
                tmp.put("PRODUCT_ID", elmt.getString("PRODUCT_ID"));
                tmp.put("USER_ID_A", elmt.getString("USER_ID_A"));

                String strEndDate = elmt.getString("END_DATE");
                if (strEndDate.compareTo(strbEndDate.toString()) >= 0)
                {
                    strEndDate = SysDateMgr.END_DATE_FOREVER;
                }
                tmp.put("END_DATE", strEndDate);

                listPackage.add(tmp);
            }
        }

        return listPackage;
    }

    /**
     * 获取全局包与包之间的依赖互斥关系
     * 
     * @param strPackageId
     * @param strLimitType
     * @param strProductId
     * @param strLimitTag
     * @param strEparchyCode
     * @param list
     * @return
     * @throws Exception
     */
    public static int getAllPackageLimit(String strPackageId, String strLimitType, String strProductId, String strLimitTag, String strEparchyCode, IDataset list) throws Exception
    {
        String strSqlRef = "", strKeyColumn = "";

        IData param = new DataMap();

        param.put("LIMIT_TAG", strLimitTag);
        param.put("EPARCHY_CODE", strEparchyCode);

        if (strLimitType == "A")
        {
            strSqlRef = "SEL_BY_PACKAGEID_A";
            param.put("PACKAGE_ID_A", strPackageId);
            list.addAll(Dao.qryByCode("TD_B_PACKAGE_LIMIT", "SEL_BY_PACKAGEID_A", param, Route.CONN_CRM_CEN));
        }
        else if (strLimitType == "B")
        {
            strSqlRef = "SEL_BY_PACKAGEID_B";
            param.put("PACKAGE_ID_B", strPackageId);
            list.addAll(Dao.qryByCode("TD_B_PACKAGE_LIMIT", "SEL_BY_PACKAGEID_B", param, Route.CONN_CRM_CEN));
        }

        return list.size();
    }

    /**
     * 根据优惠编码获得优惠信息
     * 
     * @param strDiscntCode
     * @return
     * @throws Exception
     */
    public static IData getDiscntByCode(String strDiscntCode) throws Exception
    {
        IData param = new DataMap();

        param.put("DISCNT_CODE", strDiscntCode);

        return Dao.qryByCode("TD_B_DISCNT", "SEL_BY_PK", param, Route.CONN_CRM_CEN).getData(0);
    }

    /**
     * 获取元素下必选参数
     * 
     * @param strType
     * @param strEparchyCode
     * @param strELementId
     * @return
     * @throws Exception
     */
    public static IDataset getElementForceAttr(String strType, String strEparchyCode, String strELementId) throws Exception
    {
        IDataset list = new DatasetList();
        if ("S".equals(strType))
        {

            // list = UpcCall.qryOfferChaSpecsByOfferIdIsNull("S",strELementId);
            try
            { // modify hefeng 对抛错的信息进行捕获处理
                list = UpcCall.qryOfferChaSpecsByOfferIdIsNull("S", strELementId);
                ;
            }
            catch (Exception e)
            {
                // log.info("(""+e);
                list = new DatasetList();
            }

            // list = Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_SVCID_NOTNULL", param, Route.CONN_CRM_CEN);
        }
        else
        {
            list = UpcCall.qryOfferChaSpecsByOfferIdIsNull("D", strELementId);
            // list = Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_DISCNT_NOTNULL", param, Route.CONN_CRM_CEN);
        }

        return list;
    }

    /**
     * 获取包下必选元素
     * 
     * @param strPackageId
     * @param strTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getForceElementByPackage(String strPackageId, String strTypeCode) throws Exception
    {
        IDataset list = new DatasetList();

        IData param = new DataMap();

        if ("B".equals(strTypeCode) || "S".equals(strTypeCode))
        {
            param.put("ELEMENT_TYPE_CODE", "S");
            param.put("PACKAGE_ID", strPackageId);
            // IDataset PackageElements =
            // ProductUtils.offerToElement(UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PACKAGE,
            // strPackageId, "1", "1"), strPackageId);
            // list.addAll(Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_ELEMENT_AFTER_TRADE", param,
            // Route.CONN_CRM_CEN));
        }

        if ("B".equals(strTypeCode) || "D".equals(strTypeCode))
        {
            param.put("ELEMENT_TYPE_CODE", "D");
            // list.addAll(Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_ELEMENT_AFTER_TRADE", param,
            // Route.CONN_CRM_CEN));
        }

        return list;
    }

    /**
     * 获取产品下必选包
     * 
     * @param strProductId
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getForcePackageByProduct(String strProductId, String strEparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", strProductId);
        param.put("EPARCHY_CODE", strEparchyCode);
        IDataset data = ProductPkgInfoQry.getPackageByProId(strProductId, strEparchyCode);
        // 必选或者默认的元素
        /*
         * IDataset forceElements=
         * UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, strProductId,
         * "1", null); if (IDataUtil.isNotEmpty(forceElements)) { for (int i = forceElements.size()-1; i >=0; i--) {
         * IData offer = forceElements.getData(i); if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT,
         * offer.getString("OFFER_TYPE")) && StringUtils.equals("1", offer.getString("REL_TYPE")))// 集团和成员的关系要过滤掉 {
         * forceElements.remove(i); } } forceElements = DataHelper.filter(forceElements, "FORCE_TAG=1");// 产商品的过滤不准确 }
         * return ProductUtils.offerToElement(forceElements, strProductId);
         */
        return data;
    }

    /**
     * 根据package_id 获取包信息
     * 
     * @param strPackageId
     * @return
     * @throws Exception
     */
    public static IDataset getPackageById(String strPackageId, String tempElementTypeCode, String elementId) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", strPackageId);
        return UpcCallIntf.qryOfferGroupRelByOfferIdGroupId(strPackageId, tempElementTypeCode, elementId);
        // return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id 获取产品信息
     * 
     * @param strProductId
     * @return
     * @throws Exception
     */
    public static IData getProductByIdOfAll(String strProductId) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", strProductId);
        return UpcCall.queryOfferByOfferId("P", strProductId);
        // return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PK_ALL", param, Route.CONN_CRM_CEN).getData(0);
    }

    /**
     * 获取产品与产品依赖互斥关系_A
     * 
     * @param strProductId
     * @param strLimitTag
     * @param listProductLimit
     * @return
     * @throws Exception
     */
    public static int getProductLimitA(String strProductId, String strLimitTag, IDataset listProductLimit) throws Exception
    {
        listProductLimit.clear();
        IDataset resultList = new DatasetList();
        IData param = new DataMap();
        param.put("PRODUCT_ID_A", strProductId);
        param.put("LIMIT_TAG", strLimitTag);
        resultList = UpcCall.queryOfferRelByCond("P", strProductId, strLimitTag);
        if (IDataUtil.isNotEmpty(resultList))
        {
            for (int i = 0; i < resultList.size(); i++)
            {
                IData data = resultList.getData(i);
                data.put("PRODUCT_ID_B", data.getString("OFFER_CODE"));
                data.put("PRODUCT_ID_A", strProductId);
                data.put("LIMIT_TAG", data.getString("REL_TYPE"));
            }
        }
        listProductLimit.addAll(resultList);
        // listProductLimit.addAll(Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODUCTID_A", param, Route.CONN_CRM_CEN));
        return listProductLimit.size();
    }

    /**
     * 获取产品与产品依赖互斥关系_B
     * 
     * @param strProductId
     * @param strLimitTag
     * @param listProductLimit
     * @return
     * @throws Exception
     */
    public static int getProductLimitB(String strProductId, String strLimitTag, IDataset listProductLimit) throws Exception
    {
        listProductLimit.clear();
        IDataset resultList = new DatasetList();
        IData param = new DataMap();
        param.put("PRODUCT_ID_B", strProductId);
        resultList = UpcCall.queryOfferRelByRelOfferIdAndRelType(strProductId, "P", strLimitTag);
        if (IDataUtil.isNotEmpty(resultList))
        {
            for (int i = 0; i < resultList.size(); i++)
            {
                IData data = resultList.getData(i);
                data.put("PRODUCT_ID_A", data.getString("OFFER_CODE"));
                data.put("PRODUCT_ID_B", strProductId);
                data.put("LIMIT_TAG", data.getString("REL_TYPE"));
            }
        }
        listProductLimit.addAll(resultList);
        // listProductLimit.addAll(Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODUCTID_B", param, Route.CONN_CRM_CEN));

        return listProductLimit.size();
    }

    /**
     * 获取产品依赖互斥在td_s_commpara 中的配置, 作用:: 配置是否需要走产品依赖互斥校验
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset getProductLimitOnCommpara(String strTradeTypeCode, String strEparchyCode) throws Exception
    {
        IData productLimitOnCommpara = new DataMap();
        IData inParam = new DataMap();
        inParam.put("TRADE_TYPE_CODE", strTradeTypeCode);
        inParam.put("EPARCHY_CODE", strEparchyCode);

        return Dao.qryByCode("TD_S_CPARAM", "SEL_CP_ProductLimit", inParam, Route.CONN_CRM_CEN);
    }

    /**
     * 获取产品转换关系
     * 
     * @param strProductId
     * @param strOldProductId
     * @return
     * @throws Exception
     */
    public static IDataset getProductTransLimit(String strProductId, String strOldProductId) throws Exception
    {
        return UpcCall.queryOfferJoinRelBy2OfferIdRelType("P", strOldProductId, "P", strProductId);
        // return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取产品转换关系_NEW
     * 
     * @param strProductId
     * @param strOldProductId
     * @return
     * @throws Exception
     */
    public static IDataset getProductTransLimitNew(String strProductId, String strOldProductId) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID_A", strOldProductId);
        param.put("PRODUCT_ID_B", strProductId);
        // TD_S_PTYPE_TRANS表不存在
        return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_BY_PK_NEW", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据新PRODUCT_ID和ELEMENT_ID得到可以带到新产品的元素
     * 
     * @param pd
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getTransElement(String productId, String elementId, String ElementTypeCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("ELEMENT_ID", elementId);
        params.put("ELEMENT_TYPE_CODE", ElementTypeCode);
        IDataset tmp = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ELEMENT_TRANS", params);
        return tmp;
    }

    /**
     * 根据元素ID获取与该元素所有有关的元素
     * 
     * @param strElementId
     * @param strLimitTag
     * @param strElementTypeCode
     * @param strSqlTag
     * @param strEparchyCode
     * @param listEvERelationLimit
     * @param bIsGlobeElmenetLimit
     * @return
     * @throws Exception
     */
    public static IDataset tacGetAllEleVsEleLimit(String strPackageId, String strElementId, String strLimitTag, String strElementTypeCode, String strSqlTag, String strEparchyCode, boolean bIsPkgInsideElmentLimit)
            throws Exception
    {
        // mc
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
        String tabName  = ""; 
        String versionOffer;
        String versionOfferRel;
        
        // 
        tabName = "PM_OFFER";
        versionOffer = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_REL";
        versionOfferRel= (String)cache.get(tabName);
        
        StringBuilder sb = new StringBuilder(1000);
        
        sb.append("BreQryForProduct.tacGetAllEleVsEleLimit_").append(SysDateMgr.getSysDate("dd")).append("_").
        append(versionOffer).append("_").append(versionOfferRel).append("_").
        append(strPackageId).append("_").append(strElementId).append("_").
        append(strLimitTag).append("_").
        append(strElementTypeCode).append("_").
        append(strSqlTag).append("_").
        append(strEparchyCode).append("_").
        append(bIsPkgInsideElmentLimit);
        
        // get mc
        String cacheKey  = sb.toString();
        IDataset listEvERelationLimitCache = (IDataset) SharedCache.get(cacheKey);
         
        // if mc null
        if (!IDataUtil.isNull(listEvERelationLimitCache))
        {
            return listEvERelationLimitCache;
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> prodcheck 进入 tacGetAllEleVsEleLimit[" + strElementTypeCode + "][" + strElementId + "][" + strLimitTag + "]");
        }
        
        String strSqlRef = "", strTableName = "";
        IDataset listEvERelationLimit = new DatasetList();

        IData param = new DataMap();
        IDataset result = new DatasetList();
        param.put("LIMIT_TAG", strLimitTag);
        param.put("EPARCHY_CODE", strEparchyCode);
        param.put("PACKAGE_ID", strPackageId);

        if (bIsPkgInsideElmentLimit)
        {
            if (strSqlTag == "A") // 依赖查询
            { 
                param.put("ELEMENT_ID_A", strElementId);
                param.put("ELEMENT_TYPE_CODE_A", strElementTypeCode);
                //废弃 listEvERelationLimit.addAll(Dao.qryByCode("TD_B_PACKAGE_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_A", param, Route.CONN_CRM_CEN));
            }
            else if (strSqlTag == "B") // 被依赖查询
            {
                param.put("ELEMENT_ID_B", strElementId);
                param.put("ELEMENT_TYPE_CODE_B", strElementTypeCode);
                // 废弃 listEvERelationLimit.addAll(Dao.qryByCode("TD_B_PACKAGE_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_B", param, Route.CONN_CRM_CEN));
            }
        }
        else
        {
            if (strSqlTag == "A") // 依赖查询
            {
                param.put("ELEMENT_ID_A", strElementId);
                param.put("ELEMENT_TYPE_CODE_A", strElementTypeCode);
               
    			try{ //modify hefeng 对抛错的信息进行捕获处理
    				 result=UpcCall.queryOfferRelByCond(strElementTypeCode,strElementId,strLimitTag);
    			}catch (Exception e){
    				//log.info("(""+e);
    				result=new DatasetList();
    			}
                if(IDataUtil.isNotEmpty(result))
                {
                	for(int i=0; i<result.size(); i++)
                	{
                		IData data = result.getData(i);
                		data.put("ELEMENT_TYPE_CODE_B", data.getString("REL_OFFER_TYPE"));
                		data.put("ELEMENT_ID_B", data.getString("REL_OFFER_CODE"));
                		data.put("ELEMENT_TYPE_CODE_A", strElementTypeCode);
                		data.put("ELEMENT_ID_A", strElementId);
                		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
                	}
                }
                listEvERelationLimit.addAll(result);
                //listEvERelationLimit.addAll(Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_A", param, Route.CONN_CRM_CEN));
            }
            else if (strSqlTag == "B") // 被依赖查询
            {
                param.put("ELEMENT_ID_B", strElementId);
                param.put("ELEMENT_TYPE_CODE_B", strElementTypeCode);
              
              	try{ //modify hefeng 对抛错的信息进行捕获处理
              	  result=UpcCall.queryOfferRelByRelOfferIdAndRelType(strElementId,strElementTypeCode,strLimitTag);
        		}catch (Exception e){
        			//log.info("(""+e);
        			result=new DatasetList();
        		}
                if(IDataUtil.isNotEmpty(result))
                {
                	for(int i=0; i<result.size(); i++)
                	{
                		IData data = result.getData(i);
                		data.put("ELEMENT_TYPE_CODE_A", data.getString("OFFER_TYPE"));
                		data.put("ELEMENT_ID_A", data.getString("OFFER_CODE"));
                		data.put("ELEMENT_TYPE_CODE_B", strElementTypeCode);
                		data.put("ELEMENT_ID_B", strElementId);
                		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
                	}
                }
                listEvERelationLimit.addAll(result);
                //listEvERelationLimit.addAll(Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_B", param, Route.CONN_CRM_CEN));
            }
        }
       
        // set mc
        SharedCache.set(cacheKey, listEvERelationLimit, 0);

        if (logger.isDebugEnabled())
        {
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< prodcheck 退出 tacGetAllEleVsEleLimit irlCount = [" + listEvERelationLimit.size() + "]");
        }
        
        return listEvERelationLimit;
    }

    /**
     * 根据元素获取元素与包的关系
     * 
     * @param strElmenetId
     * @param strLimitTag
     * @param strElementTypeCode
     * @param strEparchyCode
     * @param listEvPRelationLimit
     * @return
     * @throws Exception
     */
    public static int tacGetEleVsPkgLimit(String strElmenetId, String strLimitTag, String strElementTypeCode, String strEparchyCode, IDataset listEvPRelationLimit) throws Exception
    {
        listEvPRelationLimit.clear();

        IData param = new DataMap();
        param.put("ELEMENT_ID", strElmenetId);
        param.put("LIMIT_TAG", strLimitTag);
        param.put("ELEMENT_TYPE_CODE", strElementTypeCode);
        param.put("EPARCHY_CODE", strEparchyCode);

        listEvPRelationLimit.addAll(Dao.qryByCode("TD_B_ELEMENT_PACKAGE_LIMIT", "SEL_BY_ELEMENTID", param, Route.CONN_CRM_CEN));

        return listEvPRelationLimit.size();
    }

    /**
     * 获取同一产品下包与包之间限制关系数据
     * 
     * @param strPackageId
     * @param strLimitType
     * @param strProductId
     * @param strLimitTag
     * @param strEparchyCode
     * @param listPvPRelationLimit
     * @return
     * @throws Exception
     */
    public static int tacGetPackageLimit(String strPackageId, String strLimitType, String strProductId, String strLimitTag, String strEparchyCode, IDataset listPvPRelationLimit) throws Exception
    {
        String strSqlRef = "", strKeyColumn = "";

        IData param = new DataMap();

        if (strLimitType == "A")
        {
            strSqlRef = "SEL_BY_PACKAGEID_A";
            strKeyColumn = "PACKAGE_ID_A";
        }
        else if (strLimitType == "B")
        {
            strSqlRef = "SEL_BY_PACKAGEID_B";
            strKeyColumn = "PACKAGE_ID_B";
        }

        listPvPRelationLimit.clear();

        param.put("PRODUCT_ID", strProductId);
        param.put(strKeyColumn, strPackageId);
        param.put("LIMIT_TAG", strLimitTag);
        param.put("EPARCHY_CODE", strEparchyCode);

        listPvPRelationLimit.addAll(Dao.qryByCode("TD_B_PROD_PACKAGE_LIMIT", strSqlRef, param, Route.CONN_CRM_CEN));

        return listPvPRelationLimit.size();
    }

    /**
     * 根据包标识和限制类型获取与包有限制关系的所有元素
     * 
     * @param strPackageId
     * @param strLimitTag
     * @param strEparchyCode
     * @param listPvERelationLimit
     * @return
     * @throws Exception
     */
    public static int tacGetPkgVsEleRelationLimit(String strPackageId, String strLimitTag, String strEparchyCode, IDataset listPvERelationLimit) throws Exception
    {
        listPvERelationLimit.clear();

        IData param = new DataMap();
        param.put("PACKAGE_ID", strPackageId);
        param.put("LIMIT_TAG", strLimitTag);
        param.put("EPARCHY_CODE", strEparchyCode);

        listPvERelationLimit.addAll(Dao.qryByCode("TD_B_ELEMENT_PACKAGE_LIMIT", "SEL_BY_PACKAGEID", param, Route.CONN_CRM_CEN));

        return listPvERelationLimit.size();
    }

    /**
     * 根据元素ID获取与该元素所有有关的元素
     * 
     * @param strServcieId
     * @param strStateCode
     * @param strLimitTag
     * @param strSqlTag
     * @param strEparchyCode
     * @param listSvcStateLimit
     * @return
     * @throws Exception
     */
    public static int tacGetSvcStateLimit(String strServcieId, String strStateCode, String strLimitTag, String strSqlTag, String strEparchyCode, IDataset listSvcStateLimit) throws Exception
    {
        listSvcStateLimit.clear();

        String strSqlRef = "";
        IData param = new DataMap();
        IDataset resultList = new DatasetList();
        if (strSqlTag == "A") // 依赖查询
        {
            strSqlRef = "JUDGE_A_LIMIT_EXISTS";
            param.put("STATE_CODE_A", strStateCode);
            resultList = UpcCallIntf.qryOfferFuncStaRelByCond("S", strServcieId, strStateCode, strLimitTag, "A");
            listSvcStateLimit.addAll(offerToElementList(resultList, strServcieId));
        }
        else if (strSqlTag == "B") // 被依赖查询
        {
            strSqlRef = "JUDGE_B_LIMIT_EXISTS";
            param.put("STATE_CODE_B", strStateCode);
            resultList = UpcCallIntf.qryOfferFuncStaRelByCond("S", strServcieId, strStateCode, strLimitTag, "B");
            listSvcStateLimit.addAll(offerToElementList(resultList, strServcieId));
        }

        param.put("SERVICE_ID", strServcieId);
        param.put("LIMIT_TAG", strLimitTag);
        param.put("EPARCHY_CODE", strEparchyCode);
        // listSvcStateLimit.addAll(Dao.qryByCode("TD_S_SVCSTATE_LIMIT", strSqlRef, param, Route.CONN_CRM_CEN));
        return listSvcStateLimit.size();
    }

    /**
     * 新的产商品模型装换成老Element 参数转换
     * 
     * @param result
     * @return
     * @throws Exception
     * @author fangwz
     */
    public static IDataset offerToElementList(IDataset resultList, String strServcieId) throws Exception
    {

        if (IDataUtil.isNotEmpty(resultList))
        {
            // TD_S_SVCSTATE_LIMIT转
            for (int i = 0; i < resultList.size(); i++)
            {
                IData data = resultList.getData(i);
                data.put("START_DATE", data.getString("VALID_DATE"));
                data.put("END_DATE", data.getString("EXPIRE_DATE"));
                data.put("STATE_CODE_A", data.getString("FUNC_STATUS_A"));
                data.put("STATE_CODE_B", data.getString("FUNC_STATUS_B"));
                data.put("SERVICE_ID", strServcieId);
                data.put("LIMIT_TAG", data.getString("REL_TYPE"));
            }
        }
        return resultList;
    }
}
