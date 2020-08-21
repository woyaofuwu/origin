
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

/*
 * @description 处理集团BBOSS业务的产品变更
 * @date 2013-05-02
 * @author xunyl
 */
public class ChangeBBossUserBean extends GroupOrderBaseBean
{
    // 商品信息
    private IData merchInfo = null;

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 1- 获取商品信息
        merchInfo = map.getData("MERCH_INFO");

        // 2- 创建商品的台账信息
        merchInfo.put("ORDER_ID", getOrderId());
        IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.ChangeUserDis, "CreateClass");

        // 3- 获取产品信息
        IDataset orderInfo = getOrderInfo();

        // 4- 创建子产品的台账信息
        if (IDataUtil.isNotEmpty(orderInfo))
        {
            createMerchPTdInfo(orderInfo);
        }
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
        
        // 如果为省行业网关云MAS，并且为反向过来的业务，需要服务开通发指令给省行业网关，经过协商在order表中的APP_TYPE字段中放L。
//        if ("6".equals(merchInfo.getString("IN_MODE_CODE", "")) && ("010101016".equals(merchInfo.getString("PRODUCT_ID")) || "010101017".equals(merchInfo.getString("PRODUCT_ID"))))
//        {
//            IData map = bizData.getOrder();
//            map.put("APP_TYPE", "C");
//        }
        
        super.cmtOrderData_();
    }

    /*
     * 创建子产品的台账信息
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {

            // 获取处理标记,调用相应的处理类进行处理
            String dealType = orderInfo.getData(i).getString("DEAL_TYPE");
            orderInfo.getData(i).remove("DEAL_TYPE");

            IDataset merchPoutDataset = new DatasetList();
            if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue().equals(dealType))
            {// 对应产品操作为新增产品

                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.CreateUser, "CreateClass");

            }
            else if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue().equals(dealType))
            {// 对应的产品操作为取消产品订购
                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.DestoryUser, "CreateClass");
            }
            else if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue().equals(dealType) || GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue().equals(dealType))
            {// 对应的产品操作为属性变更、资费变更、产品暂停、恢复，预取消，冷冻期预取消恢复等

                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.ChangeUserDis, "CreateClass");

            }
        }
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "4600";
    }
}
