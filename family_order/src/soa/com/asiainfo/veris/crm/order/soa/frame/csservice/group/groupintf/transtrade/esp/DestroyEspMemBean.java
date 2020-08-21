
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroyEspMemBean extends GroupOrderBaseBean
{

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

    }

    /*
     * 创建子产品的台账信息并且建立BB关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {
            // 创建产品与成员的台账信息
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.DestoryMember, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
        }
    }

    
    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "4682";
    }

}
