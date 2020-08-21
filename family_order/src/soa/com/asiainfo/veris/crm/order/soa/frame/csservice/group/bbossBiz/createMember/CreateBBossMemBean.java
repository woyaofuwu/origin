
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateBBossMemBean extends GroupOrderBaseBean
{

    // 商品台账编号
    private String merchTradeId = "";

    // 产品台账编号
    private String merchpTradeId = "";

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 1- 获取商品信息
        // 获取商品信息
        IData merchInfo = getMerchInfo();

        // 2- 判断商品信息是否为空，说明已经存在商品与成员间的UU关系,无需再次创建
        if (IDataUtil.isNotEmpty(merchInfo))
        {
            // 2-1 添加商品类型标志(由于商品信息和产品信息在结构上是一致的，所以这里要加上标志用来区分)
            merchInfo.put("IS_MERCH_INFO", true);
            // 2-2 创建商品的台账信息
            IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.CreateMember, "CreateClass");
            // 2-3 获取商品台账编号
            merchTradeId = MebCommonBean.getTradeId(merchOutDataset);
        }

        // 3- 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        // 4- 创建子产品的台账信息并且建立UU关系
        createMerchPTdInfo(orderInfo,map);
    }

    /*
     * 创建子产品的台账信息并且建立UU关系
     */
    protected void createMerchPTdInfo(IDataset orderInfo ,IData map) throws Exception
    {
        for (int i = 0; i < orderInfo.size(); i++)
        {
            orderInfo.getData(i).put("IS_MERCH_INFO", false);
            //bboss产品成员变更上传凭证和稽核人员信息 by zhuwj
            orderInfo.getData(i).put("MEB_VOUCHER_FILE_LIST", map.getString("MEB_VOUCHER_FILE_LIST",""));
            orderInfo.getData(i).put("AUDIT_STAFF_ID", map.getString("AUDIT_STAFF_ID",""));
            // 创建产品与成员的台账信息
            IDataset merchpOutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.CreateMember, "CreateClass");
            // 获取产品台账编号
            merchpTradeId = MebCommonBean.getTradeId(merchpOutDataset);
            
            //指定的云MAS产品不生成Limit关联  modify  by xuzh5 
            IDataset prodCommparaList = CommparaInfoQry.getCommpara("CSM", "1985",
            		orderInfo.getData(i).getString("PRODUCT_ID",""), "ZZZZ");
            if(prodCommparaList.size()>0)
            	merchpTradeId="";// 如果数据符合配置，则不插入TF_B_TRADE_LIMIT表
            
            // 登记完工依赖表(商品与成员信息不发服务开通，但必须等产品与成员信息发服务开通完成后才能完工)
            MebCommonBean.regTradeLimitInfo(merchTradeId, merchpTradeId);
        }
    }

    /** 
    * @Title: cmtOrderData_
    * @Description: 如果是行业网关云MAS业务，需要将order表中的app_type置为L，服务开通发行业网关。
    * @throws Exception    
    * @return void
    * @author chenkh
    * @time 2015年4月13日
    */ 
    protected void cmtOrderData_() throws Exception
    {
        IData merchInfo = this.getMerchInfo();
        IData merchpInfo = this.getOrderInfo(0);
        String antiFlag = merchpInfo.getString("IN_MODE_CODE"); 
        String productId = GrpCommonBean.productToMerch(merchInfo.getString("PRODUCT_ID"), 0);
        // 1- 首先判断是否为省行业网关云MAS业务
//        if (("010101016".equals(productId) || "010101017".equals(productId)) && StringUtils.equals(antiFlag, "6"))
//        {
//            // 2- 判断黑白名单表中是否存在数据，存在数据不需要发行业网关
//            String serialNumber = merchInfo.getString("SERIAL_NUMBER");
//            String userId = merchInfo.getString("USER_ID");
//            String memProductId = merchpInfo.getString("PRODUCT_ID");
//            String servCode = BbossIAGWCloudMASDealBean.getServCodeByUserId(userId, memProductId);
//
//            boolean isExist = BbossIAGWCloudMASDealBean.isBlackWhiteExist(serialNumber, userId, servCode);
//            // 3- 如果不存在，需要发送省行业网关
//            if (!isExist)
//            {
//                IData map = bizData.getOrder();
//                map.put("APP_TYPE", "C");
//            }
//        }
        super.cmtOrderData_();
    }
    
    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "4600";
    }

}
