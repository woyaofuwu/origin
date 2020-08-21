
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.requestdata.NoPhoneWideUserCreateRequestData;

public class NoPhoneWideUserCreateRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    private static transient Logger logger = Logger.getLogger(NoPhoneWideUserCreateRegSVC.class);

    public String getOrderTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            input.put("TRADE_TYPE_CODE", "680");
        }

        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            if(input.getString("WIDE_TYPE").equals("1"))//FTTB
            {
                input.put("TRADE_TYPE_CODE", "680");
            }
        }

        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setSubscribeType("300");
    }

    @Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData noPhoneWideUserCreateRD = (NoPhoneWideUserCreateRequestData) btd.getRD();

        String  wideUserSelectedServiceIds = "";

        List<ProductModuleData> productElements = noPhoneWideUserCreateRD.getProductElements();

        for (int i = 0; i < productElements.size(); i++)
        {
            ProductModuleData productModuleData = productElements.get(i);

            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(productModuleData.getElementType()))
            {
                if (StringUtils.isNotBlank(wideUserSelectedServiceIds))
                {
                    wideUserSelectedServiceIds = wideUserSelectedServiceIds + "|" + productModuleData.getElementId();
                }
                else
                {
                    wideUserSelectedServiceIds = productModuleData.getElementId();
                }
            }
        }


        //生成宽带营销活动预受理
        if (StringUtils.isNotBlank(noPhoneWideUserCreateRD.getSaleActiveId()))
        {
            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", noPhoneWideUserCreateRD.getMainProduct().getProductId(), noPhoneWideUserCreateRD.getSaleActiveId(), getTradeEparchyCode());

            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            {
                IData saleActiveData = saleActiveIDataset.first();

                IData saleactiveData = new DataMap();
                saleactiveData.put("SERIAL_NUMBER",noPhoneWideUserCreateRD.getNormalSerialNumber());
                saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                saleactiveData.put("ACCEPT_TRADE_ID", noPhoneWideUserCreateRD.getTradeId());
                saleactiveData.put("ORDER_TYPE_CODE", "600");
                saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);

                //20170511宽带开户办理了特定的活动必须同时关联魔百和开户及营销活动受理。（NGBOSS和APP，即接口也要限制）
                String Flag = saleActiveData.getString("PARA_CODE8");
                String packageName = saleActiveData.getString("PARA_CODE3");
                if("1".equals(Flag))
                {
                    if(StringUtils.isBlank(noPhoneWideUserCreateRD.getTopSetBoxBasePkgs()) ||
                            StringUtils.isBlank(noPhoneWideUserCreateRD.getTopSetBoxProductId()))
                    {
                        CSAppException.appError("2017051101", "办理了" + packageName + "活动，必须关联办理魔百和开户！");
                    }
                    if(StringUtils.isBlank(noPhoneWideUserCreateRD.getTopSetBoxSaleActiveId()) ||
                            !"200099".equals(noPhoneWideUserCreateRD.getTopSetBoxSaleActiveId()))
                    {
                        CSAppException.appError("2017051102", "办理了" + packageName + "活动，必须关联办理移动电视尝鲜活动(预受理) ！");
                    }
                }

                //标记是宽带开户营销活动
                saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

                //认证方式
                String checkMode = btd.getRD().getCheckMode();
                saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );

                IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                String tradeId = result.getData(0).getString("TRADE_ID");
                btd.getMainTradeData().setRsrvStr5(tradeId);


                if(!"".equals(noPhoneWideUserCreateRD.getSaleActiveIdAttr()))
                {
                    String para_code11 = noPhoneWideUserCreateRD.getSaleActiveIdAttr();
                    IDataset saleActiveList = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600",null, null,para_code11, "0898");
                    if(IDataUtil.isNotEmpty(saleActiveList)){
                        IData saleactiveData2 = new DataMap();
                        saleactiveData2.put("SERIAL_NUMBER",noPhoneWideUserCreateRD.getNormalSerialNumber());
                        saleactiveData2.put("PRODUCT_ID",saleActiveList.getData(0).getString("PARA_CODE14"));
                        saleactiveData2.put("PACKAGE_ID", saleActiveList.getData(0).getString("PARA_CODE15"));
                        saleactiveData2.put("ACCEPT_TRADE_ID", noPhoneWideUserCreateRD.getTradeId());
                        saleactiveData2.put("ORDER_TYPE_CODE", "600");
                        saleactiveData2.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);

                        //标记是宽带开户营销活动
                        saleactiveData2.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                        saleactiveData2.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );

                        IDataset result2 = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData2);
                        String tradeId2 = result2.getData(0).getString("TRADE_ID");
                        btd.getMainTradeData().setRsrvStr9(tradeId2);
                    }
                }
            }
            else
            {
                CSAppException.appError("-1", "宽带营销活动包不存在！");
            }
        }

        //是否有魔百和营销活动
        if (StringUtils.isNotBlank(noPhoneWideUserCreateRD.getTopSetBoxSaleActiveId()))
        {
            IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", noPhoneWideUserCreateRD.getTopSetBoxSaleActiveId(), getTradeEparchyCode());

            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            {
                IData saleActiveData = saleActiveIDataset.first();

                IData saleactiveData = new DataMap();
                saleactiveData.put("SERIAL_NUMBER",noPhoneWideUserCreateRD.getNormalSerialNumber());
                saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                saleactiveData.put("ACCEPT_TRADE_ID", noPhoneWideUserCreateRD.getTradeId());
                saleactiveData.put("ORDER_TYPE_CODE", "600");
                saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);

                //标记是宽带开户营销活动
                saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                //20170511开户办理特殊的活动，电视尝鲜不走手机开户6个月的规则。
                saleactiveData.put("SALE_ACTIVE_ID", noPhoneWideUserCreateRD.getSaleActiveId());

                //认证方式
                String checkMode = btd.getRD().getCheckMode();
                saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );

                IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);

                String tradeId = result.getData(0).getString("TRADE_ID");
                btd.getMainTradeData().setRsrvStr6(tradeId);
            }
            else
            {
                CSAppException.appError("-1", "魔百和营销活动包不存在！");
            }
        }

        
        if(StringUtils.isNotBlank(noPhoneWideUserCreateRD.getWidenetSn())){
            /**
             * 个人用户开户受理
             */
            IData createPersonUserData = new DataMap();
            createPersonUserData.putAll(idata);
            createPersonUserData.put("SERIAL_NUMBER", noPhoneWideUserCreateRD.getWidenetSn());
            createPersonUserData.put("SIM_CARD_NO", idata.getString("SIM_CARD_NO"));
            createPersonUserData.put("USER_TYPE_CODE", "0");
            
            createPersonUserData.put("PAY_NAME", idata.getString("CUST_NAME").trim());
            createPersonUserData.put("PAY_MODE_CODE", "0");
            createPersonUserData.put("TRADE_TYPE_CODE", "10");
            createPersonUserData.put("ORDER_TYPE_CODE", "10");
            
            IDataset selectedelements = new DatasetList();
            
            //设置默认 产品ID
            IDataset productConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT");
            if (IDataUtil.isEmpty(productConfig)) {
                CSAppException.appError("-1", "无手机宽带手机用户产品没有配置,请联系管理员!");
            }
             
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
                CSAppException.appError("-1", "无手机宽带开户用户产品[84019654]没有配置,请联系管理员!");
            }
            
            createPersonUserData.put("SELECTED_ELEMENTS", selectedelements.toString());
            
            //认证方式
            String checkMode = btd.getRD().getCheckMode();
            createPersonUserData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
            
            IDataset result = CSAppCall.call("SS.CreatePersonUserRegSVC.tradeReg", createPersonUserData);
            String userId = result.getData(0).getString("USER_ID");
            
            //修改UU关系表中 userIdA
            List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            
            if (null != relationTradeDatas && relationTradeDatas.size() > 0)
            {
                relationTradeDatas.get(0).setUserIdA(userId);
            }
            
            //修改副号码的付费关系
            List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();
            
            for (BusiTradeData btdTemp : btds)
            {
                String tradeTypeCode = btdTemp.getTradeTypeCode();
                
                if ("10".equals(tradeTypeCode))
                {
                    List<PayRelationTradeData> payRelationDatas = btdTemp.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);
                    
                    if (null != relationTradeDatas)
                    {
                        payRelationDatas.get(0).setAcctId(noPhoneWideUserCreateRD.getUca().getAcctId());
                    }
                }
            }
            
            //修改other表中
            List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
            if (null != otherTradeDatas && otherTradeDatas.size() > 0)
            {
                for (int i = 0, s = otherTradeDatas.size(); i < s; i++) {
                    OtherTradeData tempData = otherTradeDatas.get(i);
                    if ("TOPSETBOX".equals(tempData.getRsrvValueCode())) {
                        tempData.setRsrvStr2(userId);
                    }
                }
            }
        }
        

    }
}
