package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyRecepHallUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroyJKDTUserBean extends GroupOrderBaseBean{


    // 商品信息
    private IData merchInfo = null;

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 1- 获取商品信息
        merchInfo = map.getData("MERCH_INFO");

        // 2- 创建商品的台账信息
        IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.DestoryUser, "CreateClass");

        // 3- 获取商品台账信息中的用户编号
        IData bbossMerChInfo = getBBossMerchInfo(merchOutDataset);

        //因为全网和本省的数据结构不一样,不确定destroygroupuser取值的时候有无问题,所以如果是本省产品,把IF_JKDT这个字段设为1,如果是全网不做标记,自己的处理类会单独入那三张表
        //给orderInfo加入是从集客大厅发起的标记 便于后台数据进行判断
        IDataset orderInfo = getOrderInfo();
        int size = orderInfo.size();
        for(int i=0;i<size;i++){
            orderInfo.getData(i).put("IF_JKDT", "1");//集客大厅发起并且是本省产品才给这个标记
        }

        // 4- 创建子产品的台账信息
        createMerchPTdInfo(bbossMerChInfo, getOrderInfo());
    }

    /**
     * @description 如果是BBOSS管理节点中的集团业务，需要将order表状态改成W，避免aee直接完工
     * @auhtor xunyl
     * @date 2013-12-14
     * @throws Exception
     */
    protected void cmtOrderData_() throws Exception
    {
        IData bbossMerchInfo = getMerchInfo();
        boolean isManageCreate = bbossMerchInfo.getBoolean("BBOSS_MANAGE_CREATE");
        if ("6".equals(CSBizBean.getVisit().getInModeCode()) && isManageCreate == true)
        {
            IData map = bizData.getOrder();
            map.put("ORDER_STATE", "W");
        }

        String productId = bbossMerchInfo.getString("PRODUCT_ID");
//        if ((("010101016".equals(GrpCommonBean.productToMerch(productId, 0)) || "010101017".equals(GrpCommonBean.productToMerch(productId, 0)))) && "6".equals(merchInfo.getString("IN_MODE_CODE", "")))
//        {
//            IData map = bizData.getOrder();
//            map.put("APP_TYPE", "C");
//        }
        super.cmtOrderData_();
    }

    /*
     * @description 修改子产品的台账信息并且建立BB关系
     * @author xunyl
     * @date 2013-07-10
     */
    protected void createMerchPTdInfo(IData bbossMerChInfo, IDataset productInfoList) throws Exception
    {
        if (null == productInfoList || productInfoList.size() == 0)
        {
            // 抛出异常
        }
        for (int i = 0; i < productInfoList.size(); i++)
        {
            productInfoList.getData(i).put("OUT_MERCH_INFO", bbossMerChInfo);
            IDataset merchPoutDataset = GrpInvoker.ivkProduct(productInfoList.getData(i), BizCtrlType.DestoryUser, "CreateClass");
        }
    }

    /*
     * @desctiption 获取商品台账信息的用户编号
     * @auhtor xunyl
     * @date 2014-04-24
     */
    protected IData getBBossMerchInfo(IDataset outDataset) throws Exception
    {
        for (int i = 0; i < outDataset.size(); i++)
        {
            IData merchOutData = outDataset.getData(i);
            if (merchOutData.containsKey("BBOSS_TAG"))
            {// 说明是BBOSS开户时返回的
                return merchOutData;
            }

        }
        return new DataMap();
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "2300";
    }


}
