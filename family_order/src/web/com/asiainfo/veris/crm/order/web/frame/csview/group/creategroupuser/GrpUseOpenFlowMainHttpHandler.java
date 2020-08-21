
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import java.util.Iterator;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

/**
 * @author yifur
 */
public class GrpUseOpenFlowMainHttpHandler extends CSBizHttpHandler
{

    public String getGrpProductId() throws Exception
    {
        //
        String productId = getData().getString("GRP_PRODUCT_ID", "");

        return productId;

    }

    public IDataset savePayPlanFrontData() throws Exception
    {
        String payplanString = getData().getString("PAYPLAN_INFOS", "[]");
        return new DatasetList(payplanString);
    }

    /**
     * 获取集团产品元素信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveProductElemensFrontData() throws Exception
    {

        String selectElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        IDataset selectElements = new DatasetList(selectElementStr);
        
        return selectElements;
    }

    public IDataset saveProductParamInfoFrontData() throws Exception
    {

        IDataset resultset = new DatasetList();
        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
            return null;
        Iterator<String> iterator = productParam.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = productParam.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttrset.add(productParamAttr);

        }
        result.put("PRODUCT_ID", getGrpProductId());
        result.put("PRODUCT_PARAM", productParamAttrset);
        resultset.add(result);
        return resultset;
    }

    /**
     * 获取资源信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveResInfoFrontData() throws Exception
    {

        String resInfoStr = getData().getString("DYNATABLE_RES_RECORD", "[]");

        IDataset resinfos = new DatasetList(resInfoStr);
        return resinfos;
    }

    public IDataset saveUserGrpPackageInfoFrontData() throws Exception
    {

        String grpPackageInfoStr = getData().getString("SELECTED_GRPPACKAGE_LIST", "[]");

        if ("".equals(grpPackageInfoStr))
        {
            return new DatasetList();
        }

        IDataset grpPackageInfos = new DatasetList(grpPackageInfoStr);
        return grpPackageInfos;
    }

    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {
        String custId = getData().getString("CUST_ID", "");

        String serialNumber = getData().getString("SERIAL_NUMBER", "");

        String contractId = getData().getString("SELE_CONTRACTPRODUCT_CONTRACT_ID", "");

        IDataset payPlan = savePayPlanFrontData();

        IDataset resinfos = saveResInfoFrontData();

        IDataset productElements = saveProductElemensFrontData();

        IDataset productParam = saveProductParamInfoFrontData();

        IDataset userGrpPackaeInfos = saveUserGrpPackageInfoFrontData();

        IDataset userProductInfos = new DatasetList();
        IData userProductInfo = new DataMap();
        userProductInfo.put("PRODUCT_ID", getGrpProductId());
        userProductInfo.put("MODIFY_TAG", "0");
        userProductInfos.add(userProductInfo);

        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", getGrpProductId());
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("ACCT_ID", getData().getString("acct_ACCT_ID"));
        //add by chenzg@20180711 REQ201804280001集团合同管理界面优化需求
        inparam.put("AUDIT_STAFF_ID", getData().getString("AUDIT_STAFF_ID", ""));

        IData userInfo = new DataMap();
        userInfo.put("CONTRACT_ID", contractId);
        inparam.put("USER_INFO", userInfo);

        String effectNow = getData().getString("EFFECT_NOW");// 产品资费立即生效标志 true立即生效
        if (StringUtils.isNotEmpty(effectNow))
        {
            inparam.put("EFFECT_NOW", "true".equals(effectNow) ? "true" : "false");
        }

        inparam.put("RES_INFO", resinfos);
        inparam.put("GRP_PACKAGE_INFO", userGrpPackaeInfos);
        inparam.put("PLAN_INFO", payPlan);
        inparam.put("ELEMENT_INFO", productElements);// 先用老结构

        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        inparam.put("REMARK", getData().getString("param_REMARK"));

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);// 因为表结构没有变  这块还按原来处理简单些

        // esop参数
        String eos = getData().getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            inparam.put("EOS", new DatasetList(eos));
        }

        // 集团产品受理时的用户地州为交易地州
        inparam.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        // 保存power100信息

        String power100ProductInfo = getData().getString("POWER100_PRODUCT_INFO");

        if (StringUtils.isNotEmpty(power100ProductInfo))
        {
            IData power100ProductInfoMap = new DataMap(power100ProductInfo);
            inparam.put("POWER100_INFO", power100ProductInfo);
            inparam.put("POWER100_PRODUCT_INFO", power100ProductInfoMap.getDataset("POWER100_PRODUCT_INFO"));

        }
        // 根据产品编号获取产品的品牌信息

        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
        if ("BOSG".equals(productBrandCode))
        {  // BBOSS业务
        	/*缓存方式实现取值
            String bbosskey = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), getData().getString("GROUP_ID", ""));
            IData  productGoodInfo = new DataMap(SharedCache.get(bbosskey).toString());
            */
            IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
            
            inparam.put("BBOSS_INFO", bbossData);
            inparam.put("GROUP_ID", getData().getString("GROUP_ID", ""));

            // bboss 从缓存取esop数据
            String key = CacheKey.getBBossESOPInfoKey(getVisit().getStaffId(), "EOS_" + getData().getString("GROUP_ID", ""));

            Object eosObject = SharedCache.get(key);

            if (eosObject != null)
            {
                inparam.put("EOS", (IDataset) eosObject);
            }

            IDataset result = CSViewCall.call(this, "CS.CreateBBossUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }
        if ("6130".equals(getGrpProductId()))
        {// 融合总机
            IDataset result = CSViewCall.call(this, "SS.CreateCentrexSuperTeleGroupUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }
        if ("6100".equals(getGrpProductId()))
        {// 移动总机
            IDataset result = CSViewCall.call(this, "SS.CreateSuperTeleGroupUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }
        /** VOIP专线（专网专线） */
        if ("7010".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.CreateVoipGroupUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }
        /** 互联网专线接入（专网专线） */
        if ("7011".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.CreateInternetGroupUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }
        /** 数据专线（专网专线） */
        if ("7012".equals(getGrpProductId()))
        {
            IDataset result = CSViewCall.call(this, "SS.CreateDatalineGroupUserSVC.crtOrder", inparam);
            this.setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.CreateGroupUserSvc.createGroupUser", inparam);
        this.setAjax(result);
    };

}
