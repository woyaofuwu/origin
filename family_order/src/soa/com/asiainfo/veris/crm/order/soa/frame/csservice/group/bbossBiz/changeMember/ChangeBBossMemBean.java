
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeBBossMemBean extends GroupOrderBaseBean
{

    // 商品台账编号
    private String merchTradeId = "";

    // 产品台账编号
    private String merchpTradeId = "";

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 获取商品信息
        IData merchInfo = getMerchInfo();
        // 2- 判断商品信息是否为空，说明已经存在商品与成员间的BB关系,无需再次创建
        if (IDataUtil.isNotEmpty(merchInfo))
        {
            // 2-1 添加商品类型标志(由于商品信息和产品信息在结构上是一致的，所以这里要加上标志用来区分)
            merchInfo.put("IS_MERCH_INFO", true);
            merchInfo.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
            merchInfo.put("RULE_BIZ_KIND_CODE", "GrpMebChg");
           
            // 2-2 创建商品的台账信息
            IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.ChangeMemberDis, "CreateClass");
            // 2-3 获取商品台账编号
            merchTradeId = MebCommonBean.getTradeId(merchOutDataset);
        }

        // 3- 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        // 4- 创建子产品的台账信息并且建立BB关系
        createMerchPTdInfo(orderInfo,map);
    }

    /*
     * 创建子产品的台账信息并且建立BB关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo,IData map) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {
            orderInfo.getData(i).put("IS_MERCH_INFO", false);
            orderInfo.getData(i).put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
            orderInfo.getData(i).put("RULE_BIZ_KIND_CODE", "GrpMebChg");
            //bboss产品成员变更上传凭证和稽核人员信息 by zhuwj
            orderInfo.getData(i).put("MEB_VOUCHER_FILE_LIST", map.getString("MEB_VOUCHER_FILE_LIST",""));
            orderInfo.getData(i).put("AUDIT_STAFF_ID", map.getString("AUDIT_STAFF_ID",""));
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.ChangeMemberDis, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
            // 登记完工依赖表(商品与成员信息不发服务开通，但必须等产品与成员信息发服务开通完成后才能完工)
            MebCommonBean.regTradeLimitInfo(merchTradeId, merchpTradeId);
        }
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "4600";
    }
}
