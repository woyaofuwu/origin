
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossDiscntDateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;

public class CreateBBossUserDataBean extends GroupBean
{
    static IData productGoodInfos = new DataMap();// BBOSS侧的商产品信息

    /*
     * @description 根据套餐生效时间处理资费开始时间
     * @author zhangc
     * @date 2013-08-02
     * @version_1
     */
    public static IDataset dealStartDate(IDataset set, String operType) throws Exception
    {
        // 1- 资费信息为空时，不做任何处理
        if (set == null || set.size() == 0)
        {
            return set;
        }

        // 2- 循环处理资费的生效时间
        for (int i = 0; i < set.size(); i++)
        {
            IData map = set.getData(i);
            if (map.containsKey("ELEMENT_TYPE_CODE") && "D".equals(map.getString("ELEMENT_TYPE_CODE")))
            {
                if (map.containsKey("START_DATE"))
                {
                    // 表示立即生效
                    if ("1".equals(operType))
                    {
                        map.put("START_DATE", SysDateMgr.getSysTime());
                    }
                    else if ("2".equals(operType))// 表示下账期失效
                    {
                        map.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                    }
                }
            }

        }
        return set;
    }

    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();
        returnVal.put("EOS", map.getDataset("EOS"));
        map.remove("EOS");

        // 2- 从BBOSS信息中取出成员操作类型和集团编号，分情况处理
        productGoodInfos = map.getData("BBOSS_INFO");
        String groupId = map.getString("GROUP_ID", "");// 集团编号
        map.remove("BBOSS_INFO");
        map.remove("GROUP_ID");

        // 3- 设置商品信息
        IData merch = productGoodInfos.getData("GOOD_INFO");
        // 3-1 设置集团编号
        merch.put("GROUP_ID", groupId);
        String payMode = merch.getString("PAY_MODE");
        // 3-2 设置主办省
        String hostCompany = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_SEQUID", ProvinceUtil.getProvinceCodeGrpCorp(), CSBizBean.getTradeEparchyCode());
        merch.put("HOST_COMPANY", hostCompany);
        map.put("BBOSS_MERCH_INFO", merch);
        // 3-3 处理商品级资费开始时间
        IDataset merchElements = map.getDataset("ELEMENT_INFO");
        
        merchElements = DealBBossDiscntDateBean.dealDiscntStartDate(merchElements, merch, payMode);// 将资费编码的开始时间按套餐生效规则封装
        returnVal.put("MERCH_INFO", map);

        // 4- 设置产品信息
        IDataset newProductInfos = new DatasetList();// 存放格式调整后的产品信息集合
        IDataset productInfos = productGoodInfos.getDataset("PRODUCT_INFO_LIST");
        for (int i = 0; i < productInfos.size(); i++)
        {
            IData product = productInfos.getData(i);
            String productId = product.getString("PRODUCT_ID");
            String productIndex = product.getString("PRODUCT_INDEX");

            // 产品对应的元素信息
            IData productElement = productGoodInfos.getData("PRODUCTS_ELEMENT");
            IDataset merchPElements = productElement.getDataset(productId + "_" + productIndex);
            
            
            // liuxx3 --start
            merchPElements = DealBBossDiscntDateBean.dealDiscntStartDate(merchPElements, product, payMode);
            // -- end

            // merchPElements = dealStartDate(merchPElements, payMode);// 将资费编码的开始时间按套餐生效规则封装

            // 产品对应的产品属性信息
            IData bbossProductParam = productGoodInfos.getData("PRODUCT_PARAM");
            IData paramMap = bbossProductParam.getData(productId + "_" + productIndex);
            // 将IData对象转换为Dataset对象
            IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(paramMap);

            // 产品对应的集团定制信息
            IDataset grpPackageInfoList = new DatasetList();
            IData grpPackageInfo = productGoodInfos.getData("GRP_PACKAGE_INFO");
            if (IDataUtil.isNotEmpty(grpPackageInfo))
            {
                String strGrpPackageInfo = grpPackageInfo.getString(productId + "_" + productIndex, "[]");
                if (StringUtils.isNotBlank(strGrpPackageInfo))
                {
                    grpPackageInfoList = new DatasetList(strGrpPackageInfo);
                }
            }

            // 拼装对象
            IData productMap = merchPToDataset(map, merchPElements, merchPParams, product, grpPackageInfoList);
            newProductInfos.add(productMap);
        }
        // 6- 产品信息入ORDER_INFO
        returnVal.put("ORDER_INFO", newProductInfos);

        // 7- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyInparamsBySpecialBiz(returnVal, GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());

        // 8- 返回数据
        return returnVal;
    }
    
    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();
        returnVal.put("EOS", map.getDataset("EOS"));
        map.remove("EOS");

        // 2- 从BBOSS信息中取出成员操作类型和集团编号，分情况处理
        productGoodInfos = map.getData("BBOSS_INFO");
        String groupId = map.getString("GROUP_ID", "");// 集团编号
        map.remove("BBOSS_INFO");
        map.remove("GROUP_ID");

        // 3- 设置商品信息
        IData merch = productGoodInfos.getData("GOOD_INFO");
        // 3-1 设置集团编号
        merch.put("GROUP_ID", groupId);
        String payMode = merch.getString("PAY_MODE");
        // 3-2 设置主办省
        String hostCompany = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_SEQUID", ProvinceUtil.getProvinceCodeGrpCorp(), CSBizBean.getTradeEparchyCode());
        merch.put("HOST_COMPANY", hostCompany);
        map.put("BBOSS_MERCH_INFO", merch);
        // 3-3 处理商品级资费开始时间
        IDataset merchElements = map.getDataset("ELEMENT_INFO");
        
        merchElements = DealBBossDiscntDateBean.dealDiscntStartDate(merchElements, merch, payMode);// 将资费编码的开始时间按套餐生效规则封装
        returnVal.put("MERCH_INFO", map);

        // 4- 设置产品信息
        IDataset newProductInfos = new DatasetList();// 存放格式调整后的产品信息集合
        IDataset productInfos = productGoodInfos.getDataset("PRODUCT_INFO_LIST");
        for (int i = 0; i < productInfos.size(); i++)
        {
            IData product = productInfos.getData(i);
            String productId = product.getString("PRODUCT_ID");
            String productIndex = product.getString("PRODUCT_INDEX");

            // 产品对应的元素信息
            IData productElement = productGoodInfos.getData("PRODUCTS_ELEMENT");
            IDataset merchPElements = productElement.getDataset(productId + "_" + productIndex);
            
            
            // liuxx3 --start
            merchPElements = DealBBossDiscntDateBean.dealDiscntStartDate(merchPElements, product, payMode);
            // -- end

            // merchPElements = dealStartDate(merchPElements, payMode);// 将资费编码的开始时间按套餐生效规则封装

            // 产品对应的产品属性信息
            IData bbossProductParam = productGoodInfos.getData("PRODUCT_PARAM");
            IData paramMap = bbossProductParam.getData(productId + "_" + productIndex);
            // 将IData对象转换为Dataset对象
            IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(paramMap);

            // 产品对应的集团定制信息
            IDataset grpPackageInfoList = new DatasetList();
            IData grpPackageInfo = productGoodInfos.getData("GRP_PACKAGE_INFO");
            if (IDataUtil.isNotEmpty(grpPackageInfo))
            {
                String strGrpPackageInfo = grpPackageInfo.getString(productId + "_" + productIndex, "[]");
                if (StringUtils.isNotBlank(strGrpPackageInfo))
                {
                    grpPackageInfoList = new DatasetList(strGrpPackageInfo);
                }
            }

            // 拼装对象
            IData productMap = merchPToDataset(map, merchPElements, merchPParams, product, grpPackageInfoList);
            newProductInfos.add(productMap);
        }
        // 6- 产品信息入ORDER_INFO
        returnVal.put("ORDER_INFO", newProductInfos);

        // 7- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyJKDTInparamsBySpecialBiz(returnVal, GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());

        // 8- 返回数据
        return returnVal;
    }

    /*
     * @desctiption 将产品信息拼装成符合商品格式的MAP对象
     * @author xunyl
     * @date 2013-04-16
     */
    protected static IData merchPToDataset(IData map, IDataset elementInfo, IDataset paramInfo, IData productInfo, IDataset grpPackageInfoList) throws Exception
    {
        // 获取前台传递过来的值
        String custId = map.getString("CUST_ID");
        String productId = productInfo.getString("PRODUCT_ID");
        String serialNumber = map.getString("SERIAL_NUMBER");
        String effectNow = map.getString("EFFECT_NOW");

        // 处理产品计费号
        IData idGen = new DataMap();
        idGen.put("GROUP_ID", map.getData("BBOSS_MERCH_INFO").getString("GROUP_ID"));
        idGen.put("PRODUCT_ID", productId);
        serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
        serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, paramInfo);

        IData merchP = new DataMap();
        merchP.put("CUST_ID", custId);
        merchP.put("PRODUCT_ID", productId);
        merchP.put("SERIAL_NUMBER", serialNumber);
        merchP.put("EFFECT_NOW", effectNow);// 立即生失效标记
        merchP.put("ACCT_IS_ADD", true);// 这里的值为true,既产品生成台帐时也会新增账户，但是账户编号会根据商品的返回值来定
        merchP.put("ELEMENT_INFO", elementInfo);
        merchP.put("GROUP_ID", map.getData("BBOSS_MERCH_INFO").getString("GROUP_ID"));
        merchP.put("GRP_PACKAGE_INFO", grpPackageInfoList);
        IData bbossParamInfo = new DataMap();
        bbossParamInfo.put("PRODUCT_ID", productId);
        bbossParamInfo.put("PRODUCT_PARAM", paramInfo);
        merchP.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));

        // BBOSS_PRODUCT_INFO仅供BBOSS子类使用，用于创建tf_f_user_grp_merchp表
        merchP.put("BBOSS_PRODUCT_INFO", productInfo);
        return merchP;
    }
}
