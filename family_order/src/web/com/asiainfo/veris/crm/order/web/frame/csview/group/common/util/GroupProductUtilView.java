
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

final public class GroupProductUtilView
{

    public static IData createGrpSn(IBizCommon bc, String productId, String grpUserEparchyCode, String resTypeCode) throws Exception
    {

        IData infoData = new DataMap();
        if (StringUtils.isEmpty(productId))
        {
            return null;
        }

        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        IData grpSnData = new DataMap();
        for (int i = 0; i < 10; i++)
        {
            grpSnData = CSViewCall.callone(bc, "CS.GrpGenSnSVC.genGrpSn", param);

            String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

            if (StringUtils.isEmpty(serialNumber))
            {
                break;
            }

            IData userList = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(bc, serialNumber, false);

            if (IDataUtil.isEmpty(userList))
            {
                break;
            }
        }
        // 避免服务号码的重复 add end

        String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

        String ifResCode = grpSnData.getString("IF_RES_CODE", "");
        // 资源类型 默认为G\
        if (StringUtils.isBlank(resTypeCode))
        {

            if (StringUtils.isNotEmpty(serialNumber) && "8070".equals(productId) && ("0").equals(ifResCode))
            {
                resTypeCode = "T";
            }
            else
            {
                resTypeCode = "G";
            }
        }

        // 服务号码信息
        infoData.put("SERIAL_NUMBER", serialNumber);
        if ("0".equals(ifResCode))
        {
            // 服务号码校验通过
            infoData.put("HIDDEN_SERIAL_NUMBER", serialNumber);
        }
        infoData.put("RES_TYPE_CODE", resTypeCode);
        infoData.put("IF_RES_CODE", ifResCode);
        return infoData;
    }

    /**
     * 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
     * 
     * @param productList
     * @param limit_type
     * @param limit_products
     * @param matches
     *            匹配字段
     * @throws Exception
     */
    public static void dealLimitProduct(IDataset productList, String limit_type, String limit_products, String matches) throws Exception
    {
        // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_products))
        {
            if (StringUtils.equals("0", limit_type))
            {
                for (int i = productList.size() - 1; i >= 0; i--)
                {
                    if (limit_products.indexOf(productList.getData(i).getString(matches)) > -1)
                    {
                        productList.remove(i); // 匹配上移除
                    }
                }
            }

            if (StringUtils.equals("1", limit_type))
            {
                for (int i = productList.size() - 1; i >= 0; i--)
                {
                    if (limit_products.indexOf(productList.getData(i).getString(matches)) == -1)
                    {
                        productList.remove(i); // 没匹配上移除
                    }
                }
            }
        }
    }

    /**
     * 查询成员订购时，是否支持高级付费
     * 
     * @author luojh 2009-09-11 17:00
     * @param ctx
     * @param tradeData
     * @throws Exception
     */
    public static String getCompixAccountTag(IBizCommon bc, String grpUserId) throws Exception
    {

        String compix_account = "0";
        IData comparam = new DataMap();
        comparam.put("GRP_USER_ID", grpUserId);
        IDataset compixAccountSet = CSViewCall.call(bc, "SS.CrtMbProductInfoBeanSVC.getCompixAccountTag", comparam);
        if (IDataUtil.isNotEmpty(compixAccountSet))
        {
            IData compixAccountData = compixAccountSet.getData(0);
            if (IDataUtil.isNotEmpty(compixAccountData))
                compix_account = compixAccountData.getString("COMPIX_ACCOUNT", "0");
        }
        return compix_account;

    }

    /**
     * 查询产品的类型
     * 
     * @param bc
     * @param grpUserId
     * @return
     * @throws Exception
     */
    public static String getCompixProductTag(IBizCommon bc, String productId) throws Exception
    {
        IData product = ProductInfoIntfViewUtil.qryProductInfoByProductId(bc, productId);
        String compixProduct = product.getString("RSRV_STR1", "0");
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(compixProduct);
        if (isNum.matches())
        {
            compixProduct = "0";
        }

        return compixProduct;

    }

    /**
     * 查询集团产品参数信息 为PRODUCTEXPLAIN组件使用
     * 
     * @param ctx
     * @param tradeData
     * @throws Exception
     */
    public static IData getProductExplainInfo(IBizCommon bc, String productId) throws Exception
    {

        if (StringUtils.isEmpty(productId))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_25);
        }

        IData productInfo = ProductInfoIntfViewUtil.qryProductInfoByProductId(bc, productId, true);

        String compixProduct = productInfo.getString("RSRV_STR1", "0");
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(compixProduct);
        if (isNum.matches())
        {
            compixProduct = "0";
        }
        
        // 针对BBOSS和动力100处理
        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(bc, productId, false);
        if (brandCode.equals("BOSG"))
        {
            compixProduct = "B";
        }
        else if(brandCode.equals("DLBG"))
        {
            compixProduct = "D";
        }
        
        productInfo.put("COMPIX_PRODUCT", compixProduct);

        // 获取产品定制信息
        String useTag = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(bc, productId, false);
        productInfo.put("USE_TAG", useTag);

        // 获取产品分散信息
        boolean ifImmdiDayProduct = CommParaInfoIntfViewUtil.qryDiverImmediProductTagBooByGrpProductId(bc, productId);
        productInfo.put("IMMEDI_TAG", ifImmdiDayProduct);

        // 获取产品分散信息
        boolean ifNatureDayProduct = CommParaInfoIntfViewUtil.qryDiverNatureProductTagBooByGrpProductId(bc, productId);
        productInfo.put("PRODUCT_NATURETAG", ifNatureDayProduct);
        return productInfo;

    }

    public static String getProductNames(IBizCommon bc, String productIdStr) throws Exception
    {
        String productNameS = "";
        String[] productIdS = productIdStr.split(",");
        int productLeng = productIdS.length;
        for (int i = 0; i < productLeng; i++)
        {
            String productId = productIdS[i];
            String productName = ProductInfoIntfViewUtil.qryProductNameStrByProductId(bc, productId);
            if (productNameS.equals(""))
                productNameS = productName;
            else
                productNameS += "," + productName;

        }
        return productNameS;
    }

    /**
     * 初始用户的资源信息，组织res格式数据
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @throws Exception
     */
    public static IDataset initResList(IBizCommon bc, String userId, String userIdA, String routeId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("USER_ID_A", userIdA);
        idata.put(Route.ROUTE_EPARCHY_CODE, routeId);
        IDataset resInfo = CSViewCall.call(bc, "CS.UserResInfoQrySVC.getUserResByUserIdA", idata);

        IDataset datasetList = new DatasetList();
        if (IDataUtil.isNotEmpty(resInfo))
        {
            for (int i = 0; i < resInfo.size(); i++)
            {
                IData param = new DataMap();
                param.put("RES_TYPE_CODE", resInfo.get(i, "RES_TYPE_CODE"));
                param.put("RES_CODE", resInfo.get(i, "RES_CODE"));
                param.put("MODIFY_TAG", "EXIST");
                param.put("CHECKED", "true");
                param.put("DISABLED", "true");
                datasetList.add(param);
            }
        }

        return datasetList;
    }

    /**
     * 如果集团用户处于升级状态，不允许办理业务
     * 
     * @param bp
     * @param productId
     * @param userId
     * @return
     * @throws Throwable
     */
    public static void judgeGroupUserCutState(IBizCommon bc, String grpUserId) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_USER_ID", grpUserId);
        IDataset upgradeInfoset = CSViewCall.call(bc, "CS.GroupCutInfoQrySVC.getGroupCutInfoByUserId", param);
        if (IDataUtil.isNotEmpty(upgradeInfoset))
        {
            IData upgradeInfo = upgradeInfoset.getData(0);
            String dealState = upgradeInfo.getString("DEAL_STATE");
            if (!"30".equals(dealState))
            {
                CSViewException.apperr(GrpException.CRM_GRP_209, grpUserId);
            }
        }

    }

    public static IDataset qryRoleListByProductId(IBizCommon bc, String productId) throws Exception
    {
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(bc, productId);
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset roleList = CSViewCall.call(bc, "CS.StaticInfoQrySVC.getRoleCodeList", param);
        return roleList;
    }

}
