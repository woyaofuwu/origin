
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateEspMemBean extends GroupOrderBaseBean
{

    // 商品台账编号
    private String merchTradeId = "";

    // 产品台账编号
    private String merchpTradeId = "";

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
//        // 1- 获取商品信息
//        // 获取商品信息
//        IData merchInfo = getMerchInfo();
//
//        // 2- 判断商品信息是否为空，说明已经存在商品与成员间的UU关系,无需再次创建
//        if (IDataUtil.isNotEmpty(merchInfo))
//        {
//            // 2-1 添加商品类型标志(由于商品信息和产品信息在结构上是一致的，所以这里要加上标志用来区分)
//            merchInfo.put("IS_MERCH_INFO", true);
//            // 2-2 创建商品的台账信息
//            IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.CreateMember, "CreateClass");
//            // 2-3 获取商品台账编号
//            merchTradeId = MebCommonBean.getTradeId(merchOutDataset);
//        }

        // 3- 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        // 4- 创建子产品的台账信息并且建立UU关系
        createMerchPTdInfo(orderInfo);
    }

    /*
     * 创建子产品的台账信息并且建立UU关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {
//            orderInfo.getData(i).put("IS_MERCH_INFO", false);
            // 创建产品与成员的台账信息
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.CreateMember, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
//            // 登记完工依赖表(商品与成员信息不发服务开通，但必须等产品与成员信息发服务开通完成后才能完工)
//            MebCommonBean.regTradeLimitInfo(merchTradeId, merchpTradeId);
        }
    }

	@Override
	protected String setOrderTypeCode() throws Exception {
		// TODO Auto-generated method stub
		return "4680";
	}
    

}
