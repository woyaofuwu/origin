
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class DestroyBBossUserDataBean extends GroupBean
{

    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();
        returnVal.put("EOS", map.getDataset("EOS"));
        map.remove("EOS");

        // 2- 拼装商品数据
        returnVal.put("MERCH_INFO", map);

        // 3- 拼装产品数据
        IDataset productInfoSet = makeProductInfoData(map);
        returnVal.put("ORDER_INFO", productInfoSet);

        // 5- 返回结果
        return returnVal;
    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static IDataset makeProductInfoData(IData map) throws Exception
    {
        // 1- 根据商品用户编号和产品关系类型从BB关系表中得到所有子产品的用户编号
        IDataset productInfoSet = new DatasetList();

        // 2- 获取商品编号
        String merchId = map.getString("PRODUCT_ID");

        // 3- 根据商品编号获取产品关系类型
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

        // 4- 获取商品用户编号
        String userId = map.getString("USER_ID");

        // 5- 根据商品用户编号和产品关系类型查找BB关系数据(第3个参数为0时代表只找集团产品间的BB关系)
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, merchRelationTypeCode, "0");

        // 6- 循环BB关系数据，添加产品信息
        for (int i = 0; i < relaBBInfoList.size(); i++)
        {
            // 获取子产品的用户编号
            String userIdB = relaBBInfoList.getData(i).getString("USER_ID_B");
            IData productInfo = setProductInfo(userIdB, map);
            productInfoSet.add(productInfo);
        }

        // 7- 返回产品信息
        return productInfoSet;

    }

    /*
     * 格式化产品信息，使其结构与商品保持一致
     * @author xunyl
     * @date 2013-04-18
     */
    protected static IData setProductInfo(String userId, IData idata) throws Exception
    {
        // 1- 根据产品产品用户编号获取产品用户信息
        IData productUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (productUserInfo == null || productUserInfo.size() <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }

        // 2- 获取产品编号
        String productId = productUserInfo.getString("PRODUCT_ID");

        // 3- 封装产品信息
        IData productInfo = new DataMap();
        productInfo.put("USER_ID", userId);
        productInfo.put("IF_BOOKING", idata.getString("IF_BOOKING"));
        productInfo.put("REASON_CODE", idata.getString("REASON_CODE"));
        productInfo.put("REMARK", idata.getString("REMARK"));
        productInfo.put("PRODUCT_ID", productId);
        productInfo.put("BATCH_ID", idata.getString("BATCH_ID",""));

        // 4-拼装特殊参数数据
        IData bbossParamMapData = idata.getData("BBossParamInfo");
        if (IDataUtil.isNotEmpty(bbossParamMapData))
        {
            IData bbossParamInfo = new DataMap();
            bbossParamInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
            bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productInfo.getString("PRODUCT_ID")));
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
        }

        // 4- 返回产品信息
        return productInfo;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();
        returnVal.put("EOS", map.getDataset("EOS"));
        map.remove("EOS");

        // 2- 拼装商品数据
        returnVal.put("MERCH_INFO", map);

        // 3- 拼装产品数据
        IDataset productInfoSet = makeProductInfoData(map);
        returnVal.put("ORDER_INFO", productInfoSet);

        // 5- 返回结果
        return returnVal;
    }


}
