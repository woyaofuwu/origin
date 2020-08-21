
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeEspMemBean extends GroupOrderBaseBean
{


    // 产品台账编号
    private String merchpTradeId = "";

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {

        // 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        // 创建子产品的台账信息并且建立BB关系
        createMerchPTdInfo(orderInfo);
    }

    /*
     * 创建子产品的台账信息并且建立BB关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {

//            orderInfo.getData(i).put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
//            orderInfo.getData(i).put("RULE_BIZ_KIND_CODE", "GrpMebChg");
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.ChangeMemberDis, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
            // 登记完工依赖表(商品与成员信息不发服务开通，但必须等产品与成员信息发服务开通完成后才能完工)
//            MebCommonBean.regTradeLimitInfo(merchTradeId, merchpTradeId);
        }
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "4681";
    }
}
