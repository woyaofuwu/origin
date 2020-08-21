package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyRecepHallMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

/**
 * @program: hain_order
 * @description: JKDT成员删除
 * @author: zhangchengzhi
 * @create: 2018-10-08 10:04
 **/

public class DestroyReceptionHallMemBean extends GroupOrderBaseBean {


    // 商品台账编号
    private String merchTradeId = "";

    // 产品台账编号
    private String merchpTradeId = "";

    /*
     * @description 先注销产品与成员的关系再注销商品与成员的关系
     * @author xunyl
     * @date 2013-07-17
     */
    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 1- 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        // 2- 创建子产品的台账信息并且建立BB关系
        createMerchPTdInfo(orderInfo);
        // 3- 获取商品信息
        IData merchInfo = getMerchInfo();

        // 4- 判断商品信息是否为空，为空说明此成员只是与商品下的某个成员产品解除成员关系，并非与商品下的所有成员产品都解除成员关系
        if (IDataUtil.isNotEmpty(merchInfo))
        {
            // 4-1 添加商品类型标志(由于商品信息和产品信息在结构上是一致的，所以这里要加上标志用来区分)
            merchInfo.put("IS_MERCH_INFO", true);
            // 4-2 创建商品的台账信息
            IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.DestoryMember, "CreateClass");
            // 4-3 获取商品台账编号
            merchTradeId = MebCommonBean.getTradeId(merchOutDataset);
            // 登记完工依赖表(商品与成员信息不发服务开通，但必须等产品与成员信息发服务开通完成后才能完工)
            MebCommonBean.regTradeLimitInfo(merchTradeId, merchpTradeId);
        }

    }

    /*
     * 创建子产品的台账信息并且建立BB关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {
            orderInfo.getData(i).put("IS_MERCH_INFO", false);
            // 创建产品与成员的台账信息
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.DestoryMember, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
        }
    }

    /**
     * @Title: cmtOrderData_
     * @Description: 如果是行业网关云MAS业务，需要将order表中的app_type置为L，服务开通发行业网关。
     * @throws Exception
     * @return void
     * @author chenkh
     * @time 2015年4月21日
     */
    protected void cmtOrderData_() throws Exception
    {
        IData merchInfo = this.getMerchInfo();
        IData merchpInfo = this.getOrderInfo(0);
        if (IDataUtil.isNotEmpty(merchInfo))
        {
            String MerchUserId = merchInfo.getString("USER_ID", "");
            IData grpUserData = UserInfoQry.getGrpUserInfoByUserIdForGrp(MerchUserId, "0"); // 查询集团用户信息;
            String productId = GrpCommonBean.productToMerch(grpUserData.getString("PRODUCT_ID", ""), 0);
            // 1- 首先判断是否为省行业网关云MAS业务
//            if ("010101016".equals(productId) || "010101017".equals(productId))
//            {
//                // 2- 判断黑白名单表中是否存在数据，存在数据需要发行业网关
//                String serialNumber = merchInfo.getString("SERIAL_NUMBER");
//                String userId = merchInfo.getString("USER_ID");
//                String memProductId = merchpInfo.getString("PRODUCT_ID");
//                String servCode = BbossIAGWCloudMASDealBean.getServCodeByUserId(userId, memProductId);
//
//                boolean isExist = BbossIAGWCloudMASDealBean.isBlackWhiteExist(serialNumber, userId, servCode);
//                // 3- 如果存在，需要发送省行业网关,并且将黑白名单表的记录删除
//                if (isExist)
//                {
//                    IData map = bizData.getOrder();
//                    map.put("APP_TYPE", "C");
//                }
//            }
        }

        super.cmtOrderData_();
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "2300";
    }

}
