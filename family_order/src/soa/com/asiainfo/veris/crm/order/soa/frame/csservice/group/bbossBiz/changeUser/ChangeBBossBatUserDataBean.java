
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ChangeBBossBatUserDataBean extends GroupBean
{

    /*
     * @description 添加商品信息，子产品修改BB关系时需要用到
     * @param userId 商品用户编号
     * @author chenyi
     * @date 2013-05-07
     */
    public static IData getDelMerchData(String userId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");
        merchOutData.put("USER_ID", userId);

        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 拼装商品数据
     * @author chenyi
     * @date 2013-07-01
     */
    protected static IData getMerchInfoData(IData map, String merchUserId, String proPoNumber) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();
        // 商品操作类型
        String merchOperType = GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue();

        merchInfo.put("PRODUCT_ID", proPoNumber);

        merchInfo.put("USER_ID", merchUserId);

        // 5- 添加BBOSS特有商品信息(套餐生效规则、业务保障等级、商品操作类型)
        IData bbossMerchInfo = new DataMap();
        setBBossMerchInfo(merchOperType, bbossMerchInfo, map);
        merchInfo.put("GOOD_INFO", bbossMerchInfo);

        return merchInfo;
    }

    /*
     * @description 组装商品注销信息
     * @author chenyi
     * @date 2013-08-30
     */
    protected static IData getMerchInfoDataForDestroy(IData map) throws Exception
    {
        IData merchInfo = new DataMap();
        merchInfo.put(Route.USER_EPARCHY_CODE, map.getString("GRP_USER_EPARCHYCODE", ""));
        merchInfo.put("IF_BOOKING", map.getString("IF_BOOKING", "false").equals("true") ? "true" : "false");
        merchInfo.put("REASON_CODE", map.getString("param_REMOVE_REASON"));
        merchInfo.put("REMARK", map.getString("param_REMARK", ""));
        merchInfo.put("IF_CENTRETYPE", map.getString("IF_CENTRETYPE", "")); // 融合标识

        IData goodInfo = new DataMap();
        goodInfo.put("MERCH_OPER_CODE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());
        merchInfo.put("GOOD_INFO", goodInfo);
        return map;
    }

    /*
     * @description 拼装商品数据
     * @author chenyi
     * @date 2013-07-01
     */
    protected static IDataset getMerchpInfoData(IData map, String merchUserId) throws Exception
    {

        IDataset productInfoset = new DatasetList();
        // 1- 定义符合基类处理的产品信息
        IData productInfo = new DataMap();

        productInfo.put("USER_ID", map.getString("USER_ID"));

        // 3- 添加预约标志(BBOSS侧没有预约)
        productInfo.put("IF_BOOKING", "false");

        // 4- 添加取消订购原因
        productInfo.put("REASON_CODE", "");

        // 5- 添加备注信息
        productInfo.put("REMARK", "");

        // 6- 添加处理标志(商品变更，产品注销)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue());

        // 7- 添加商品信息，子产品修改BB关系时需要用到
        IData merchOutData = getDelMerchData(merchUserId);
        productInfo.put("OUT_MERCH_INFO", merchOutData);

        // 8- 添加反向受理标记(反向受理不发服务开通)
        productInfo.put("IN_MODE_CODE", "6");

        // 9- 添加单条产品至产品数据集
        productInfoset.add(productInfo);

        return productInfoset;
    }

    /*
     * @description 组装产品注销信息
     * @author chenyi
     * @date 2013-08-30
     */
    protected static IDataset getMerchpInfoDataForDestroy(IData map) throws Exception
    {
        // 1- 定义产品数据
        IDataset merchpInfoList = new DatasetList();
        IData productInfo = new DataMap();
        productInfo.put("USER_ID", map.getString("USER_ID"));

        productInfo.put("PRODUCT_ID", map.getString("PRODUCT_ID"));

        // 3- 添加预约标志(BBOSS侧没有预约)
        productInfo.put("IF_BOOKING", "false");

        // 4- 添加取消订购原因
        productInfo.put("REASON_CODE", "");

        // 5- 添加备注信息
        productInfo.put("REMARK", "");

        // 6- 添加处理标志(商品变更，产品注销)
        productInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue());

        merchpInfoList.add(productInfo);
        return merchpInfoList;
    }

    /*
     * @description 拼装一键注销商产品信息
     * @author chenyi
     * @date 2013-08-30
     */
    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();

        // 查询BB关系，如果只有一条则商品注销，如果多个产品则修改商产品关系
        String merhpUserId = map.getString("USER_ID");
        String merchpProductId = map.getString("PRODUCT_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", merchpProductId, true);
        IDataset userInfo = UserInfoQry.getUserInfoByUserIdTag(merhpUserId, "0");
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }

        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(merhpUserId, merchRelationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1168);
        }
        String merchUserId = relaBBInfoList.getData(0).getString("USER_ID_A");
        relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, merchRelationTypeCode, "0");

        // 获取商品id
        IDataset merchDataset = UProductCompRelaInfoQry.queryProductRealByProductB(merchpProductId);
        String merchProductId = merchDataset.getData(0).getString("PRODUCT_ID_A");// 商品id

        // 该商品下只有一个产品 则商品注销
        if (relaBBInfoList.size() == 1)
        {
            // 2- 拼装商品数据
            IData merchInfo = getMerchInfoDataForDestroy(map);
            merchInfo.put("PRODUCT_ID", merchProductId); // 商品ID
            merchInfo.put("USER_ID", merchUserId); // 商品用户ID
            returnVal.put("MERCH_INFO", merchInfo);

            // 3- 拼装产品数据
            IDataset productInfoList = getMerchpInfoDataForDestroy(map);
            returnVal.put("ORDER_INFO", productInfoList);

        }
        else
        {
            // 有多条产品，则修改商产品组成关系

            // 2- 拼装商品数据
            IData merchInfo = getMerchInfoData(map, merchUserId, merchProductId);
            returnVal.put("MERCH_INFO", merchInfo);

            // 3- 拼装产品数据
            IDataset productInfoList = getMerchpInfoData(map, merchUserId);
            returnVal.put("ORDER_INFO", productInfoList);

        }

        // 4- 返回数据集
        return returnVal;
    }

    /*
     * @description 设置BBOSS特有商品信息(套餐生效规则、业务保障等级、商品操作类型)
     * @author chenyi
     * @date 2013-07-02
     */
    protected static void setBBossMerchInfo(String merchOperType, IData bbossMerchInfo, IData map) throws Exception
    {
        // 1- 设置生失效方式
        String pORatePolicyEffRule = "1";// 默认为立即生效
        if (IDataUtil.isNotEmpty(IDataUtil.getDataset("RSRV_STR3", map)))
        {
            pORatePolicyEffRule = (String) IDataUtil.getDataset("RSRV_STR3", map).get(0);
        }
        bbossMerchInfo.put("PAY_MODE", pORatePolicyEffRule);

        // 2- 业务保障等级一级BOSS没有传递到省BOSS，默认为普通级
        bbossMerchInfo.put("BUS_NEED_DEGREE", "4");

        // 3- 设置商品操作类型
        bbossMerchInfo.put("MERCH_OPER_CODE", merchOperType);
    }
}
