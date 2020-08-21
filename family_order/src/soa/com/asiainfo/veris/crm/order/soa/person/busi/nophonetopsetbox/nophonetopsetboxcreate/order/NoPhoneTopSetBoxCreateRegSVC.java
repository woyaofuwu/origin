package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.requestdata.NoPhoneTopSetBoxCreateRequestData;

public class NoPhoneTopSetBoxCreateRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "4900");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "4900");
    }
    
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {
        NoPhoneTopSetBoxCreateRequestData noPhoneTopSetBoxCreateRD = (NoPhoneTopSetBoxCreateRequestData) btd.getRD();
        
        if ("Y".equals(idata.getString("NEW_TAG")))
        {// 新数据模型 178号码已经开户了，没必要再去开户了
            
            String userId ="";
            String serial_numberb=noPhoneTopSetBoxCreateRD.getSerialNumberB();
            IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serial_numberb);
            if(IDataUtil.isNotEmpty(userInfos)){
                userId=userInfos.getData(0).getString("USER_ID");
            }else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"无手机宽带开户没有查到绑定的有效178号码");
            }
             
            // 修改UU关系中服务号码的userId
            List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            if (null != relationTradeDatas)
            {
                relationTradeDatas.get(0).setUserIdA(userId);
            }

            // 修改other表中
            List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
            if (null != otherTradeDatas)
            {
                otherTradeDatas.get(0).setRsrvStr2(userId);
            }
        }
        else
        {

            /**
             * 个人用户开户受理
             */
            UcaData ucas = btd.getRD().getUca();
            String payName = ucas.getAccount().getPayName();
            String psptTypeCode = ucas.getCustomer().getPsptTypeCode();
            String psptId = ucas.getCustomer().getPsptId();
            String custName = ucas.getCustomer().getCustName();

            IData createPersonUserData = new DataMap();
            createPersonUserData.putAll(idata);
            createPersonUserData.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER_B"));
            createPersonUserData.put("SIM_CARD_NO", idata.getString("SIM_CARD_NO"));
            createPersonUserData.put("USER_TYPE_CODE", "0");

            createPersonUserData.put("PAY_NAME", payName);
            createPersonUserData.put("PAY_MODE_CODE", "0");
            createPersonUserData.put("TRADE_TYPE_CODE", "10");
            createPersonUserData.put("ORDER_TYPE_CODE", "10");

            createPersonUserData.put("PSPT_TYPE_CODE", psptTypeCode);
            createPersonUserData.put("PSPT_ID", psptId);
            createPersonUserData.put("CUST_NAME", custName);

            IDataset selectedelements = new DatasetList();

            // 设置默认 产品ID
           // String productId = "20170998";

            // 设置默认 产品ID
            IDataset productConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT");
            if (IDataUtil.isEmpty(productConfig))
            {
                CSAppException.appError("-1", "无手机宽带手机用户产品没有配置,请联系管理员!");
            }

//            String productId = productConfig.first().getString("DATA_ID", "20170998");
            String productId = productConfig.first().getString("DATA_ID", "20191209");
            createPersonUserData.put("PRODUCT_ID", productId);

            // 必选或者默认的元素
            IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
            forceElements = ProductUtils.offerToElement(forceElements, productId);

            if (IDataUtil.isNotEmpty(forceElements))
            {
                String sysDate = SysDateMgr.getSysTime();

                for (int i = 0; i < forceElements.size(); i++)
                {
                    IData element = new DataMap();
                    element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
                    element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
                    element.put("PRODUCT_ID", createPersonUserData.getString("PRODUCT_ID"));
                    element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
                    element.put("MODIFY_TAG", "0");
                    element.put("START_DATE", sysDate);
                    element.put("END_DATE", "2050-12-31");
                    selectedelements.add(element);
                }
            }
            else
            {
                CSAppException.appError("-1", "无手机宽带开户用户产品[20170998]没有配置,请联系管理员!");
            }

            createPersonUserData.put("SELECTED_ELEMENTS", selectedelements.toString());

            // 认证方式
            String checkMode = btd.getRD().getCheckMode();
            createPersonUserData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z");

            // 受理单标记：无手机宽带开户融合个人开户时：个人开户 不打印 受理单
            createPersonUserData.put("TEMPLATE", "N");

            IDataset result = CSAppCall.call("SS.CreatePersonUserRegSVC.tradeReg", createPersonUserData);
            String userId = result.getData(0).getString("USER_ID");

            // 修改UU关系中服务号码的userId
            List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            if (null != relationTradeDatas)
            {
                relationTradeDatas.get(0).setUserIdA(userId);
            }

            // 修改other表中
            List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
            if (null != otherTradeDatas)
            {
                otherTradeDatas.get(0).setRsrvStr2(userId);
            }

            // 修改副号码的付费关系
            List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();

            for (BusiTradeData btdTemp : btds)
            {
                String tradeTypeCode = btdTemp.getTradeTypeCode();

                if ("10".equals(tradeTypeCode))
                {
                    List<PayRelationTradeData> payRelationDatas = btdTemp.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);

                    if (null != relationTradeDatas)
                    {
                        payRelationDatas.get(0).setAcctId(noPhoneTopSetBoxCreateRD.getUca().getAcctId());
                    }
                }
            }
        }        

    }
    
}
