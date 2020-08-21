
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.MebDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class CreateBBossMemDataBean extends GroupBean
{

    /*
     * @description根据前台传过来的对象拼装产品信息,拼装成的对象跟前台商品的一致
     * @author xunyl
     * @Date 2013-04-23
     */
    protected static IDataset getOrderInfo(IData map, IData bbossData) throws Exception
    {
        IDataset merchPMaps = new DatasetList();// ORDER_INFO

        // 获取产品属性
        IData productParamMap = bbossData.getData("PRODUCT_PARAM");

        // 获取资费和服务
        IData productElementMap = bbossData.getData("PRODUCTS_ELEMENT");

        // 获取产品信息
        IDataset productInfoList = bbossData.getDataset("PRODUCT_INFO_LIST");

        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            IData merchPInfo = new DataMap();
            String userId = productInfo.getString("USER_ID", "");
            merchPInfo.put("USER_ID", userId);
            merchPInfo.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", ""));
            merchPInfo.put("EFFECT_NOW", map.getString("EFFECT_NOW"));
            merchPInfo.put("MEM_ROLE_B", "1");// 添加商品角色(BBOSS默认的成员角色为BBOSS成员)
            merchPInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

            // 产品属性对象
            IData productParam = productParamMap.getData(userId);// 获取用户对应的成员属性
            IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(productParam);
            IDataset productParamList = new DatasetList();// 这里用List来传递成员产品参数其实只是为了符合框架结构，因为List里面只有一个Map
            IData productParamObj = new DataMap();// 放置成员基础产品编号和对应的产品参数
            productParamObj.put("PRODUCT_PARAM", merchPParams);
            productParamObj.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));// 成员基础产品编号
            productParamList.add(productParamObj);

            // 产品元素对象
            IDataset productElements = productElementMap.getDataset(userId);

            merchPInfo.put("PRODUCT_PARAM_INFO", productParamList);
            merchPInfo.put("ELEMENT_INFO", productElements);

            // 产品信息
            merchPInfo.put("PRODUCT_INFO", productInfo);

            // 如果为集团付费
            String plan_type_code = map.getString("PLAN_TYPE_CODE");
            merchPInfo.put("PLAN_TYPE_CODE", plan_type_code);

            merchPMaps.add(merchPInfo);
        }
        return merchPMaps;
    }

    public static IData makeData(IData map) throws Exception
    {
        IData returnVal = new DataMap();// 符合后台基类处理的上产品数据集

        // 取出产品信息
        IData bbossData = map.getData("BBOSS_INFO");

        // 添加商品角色(BBOSS默认的成员角色为BBOSS成员)
        map.put("MEM_ROLE_B", "1");

        // 商品信息
        map.remove("BBOSS_INFO");
        MebDisAttrTransBean.disDataToAttrData(map);// 成员特殊属性资费互换
        returnVal.put("MERCH_INFO", map);

        // 产品信息
        IDataset orderInfo = getOrderInfo(map, bbossData);
        MebDisAttrTransBean.disDataToAttrData(orderInfo.getData(0));// 成员特殊属性资费互换
        returnVal.put("ORDER_INFO", orderInfo);

        return returnVal;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        IData returnVal = new DataMap();// 符合后台基类处理的上产品数据集

        // 取出产品信息
        IData bbossData = map.getData("BBOSS_INFO");

        // 添加商品角色(BBOSS默认的成员角色为BBOSS成员)
        map.put("MEM_ROLE_B", "1");

        // 商品信息
        map.remove("BBOSS_INFO");
        MebDisAttrTransBean.disDataToAttrData(map);// 成员特殊属性资费互换
        returnVal.put("MERCH_INFO", map);

        // 产品信息
        IDataset orderInfo = getOrderInfo(map, bbossData);
        MebDisAttrTransBean.disDataToAttrData(orderInfo.getData(0));// 成员特殊属性资费互换
        returnVal.put("ORDER_INFO", orderInfo);

        return returnVal;
    }
}
