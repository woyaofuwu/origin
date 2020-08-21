
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.ProductModuleActionConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.ProductModuleTradeConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bre.svc.BreEngine;

public class ProductModuleCreator
{
    protected static Logger log = Logger.getLogger(ProductModuleCreator.class);

    private static void addProductModuleTdToBusiTd(ProductModuleTradeData dealPmtd, BusiTradeData btd, UcaData uca) throws Exception
    {
        IData cond = new DataMap();
        String elementTypeCode = dealPmtd.getElementType();
        if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC))
        {
            PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
            btd.add(uca.getSerialNumber(), pstd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SALEGOODS))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_CREDIT))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }
        else if (elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_SCORE))
        {
            btd.add(uca.getSerialNumber(), dealPmtd);
        }

        if (dealPmtd.getAttrTradeDatas() != null && dealPmtd.getAttrTradeDatas().size() > 0 && !BofConst.MODIFY_TAG_DEL.equals(dealPmtd.getModifyTag()))
        {
            int attrSize = dealPmtd.getAttrTradeDatas().size();
            for (int i = 0; i < attrSize; i++)
            {
                AttrTradeData attrTD = dealPmtd.getAttrTradeDatas().get(i);
                btd.add(uca.getSerialNumber(), attrTD);
            }
        }
    }

    public static void createProductModuleTradeData(List<ProductModuleData> productModuleDatas, BusiTradeData btd) throws Exception
    {
        ProductModuleCreator.createProductModuleTradeData(productModuleDatas, btd.getRD().getUca(), btd, null);
    }

    public static void createProductModuleTradeData(List<ProductModuleData> productModuleDatas, BusiTradeData btd, ProductTimeEnv env) throws Exception
    {
        ProductModuleCreator.createProductModuleTradeData(productModuleDatas, btd.getRD().getUca(), btd, env);
    }

    public static void createProductModuleTradeData(List<ProductModuleData> productModuleDatas, UcaData uca, BusiTradeData btd) throws Exception
    {
        ProductModuleCreator.createProductModuleTradeData(productModuleDatas, uca, btd, null);
    }

    /**
     * @param pd
     * @param productModuleDatas
     * @param uca
     * @return
     * @throws Exception
     */
    public static void createProductModuleTradeData(List<ProductModuleData> productModuleDatas, UcaData uca, BusiTradeData btd, ProductTimeEnv env) throws Exception
    {
        long beginTime = 0;
        int size = productModuleDatas.size();

        btd.setProductTimeEnv(env);

        // 对所有的元素排个序，先执行DEL的
        List<ProductModuleData> delProductModualDatas = new ArrayList<ProductModuleData>();
        List<ProductModuleData> addProductModualDatas = new ArrayList<ProductModuleData>();
        List<ProductModuleData> otherProductModualDatas = new ArrayList<ProductModuleData>();// 其他MODIFY_TAG的，如U、2等

        for (ProductModuleData productModuleData : productModuleDatas)
        {
            String modifyTag = productModuleData.getModifyTag();
            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                delProductModualDatas.add(productModuleData);
            }
            else if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                addProductModualDatas.add(productModuleData);
            }
            else
            {
                otherProductModualDatas.add(productModuleData);
            }
        }
        productModuleDatas.clear();
        productModuleDatas.addAll(delProductModualDatas);
        productModuleDatas.addAll(addProductModualDatas);
        productModuleDatas.addAll(otherProductModualDatas);
        // 排序结束

        beginTime = System.currentTimeMillis();
        if (log.isDebugEnabled())
        {
            log.debug("开始构建ProductModuleTradeData对象");
        }
        List<ProductModuleTradeData> pmtds = new ArrayList<ProductModuleTradeData>();
        for (int i = 0; i < size; i++)
        {
            ProductModuleData productModuleData = productModuleDatas.get(i);
            IProductModuleTrade productModuleTrade = ProductModuleTradeConfig.getProductModuleTrade(productModuleData);
            ProductModuleTradeData pmtd = productModuleTrade.createProductModuleTrade(productModuleData, productModuleDatas, uca, btd, env);
            //REQ201606070017神州行超享卡优化规则（首月优惠及约定在网）
             if(("3370".equals(pmtd.getElementId())||"3372".equals(pmtd.getElementId())||"3377".equals(pmtd.getElementId())||"3378".equals(pmtd.getElementId()))&&"0".equals(pmtd.getModifyTag())&&env==null){//开户超享卡特殊处理
            	pmtd.setStartDate(SysDateMgr.getSysTime());
            }
            pmtd.setPmd(productModuleData);
            pmtds.add(pmtd);

            // 添加到busiTradeData中
            addProductModuleTdToBusiTd(pmtd, btd, uca);
        }
        if (log.isDebugEnabled())
        {
            log.debug("构建ProductModuleTradeData对象 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
        }

        // action
        beginTime = System.currentTimeMillis();
        if (log.isDebugEnabled())
        {
            log.debug("开始执行ProductModuleAction");
        }
        size = pmtds.size();
        for (int i = 0; i < size; i++)
        {
            ProductModuleTradeData dealPmtd = pmtds.get(i);
            List<IProductModuleAction> actions = ProductModuleActionConfig.getProductAction(dealPmtd, btd);
            int actionSize = actions.size();
            for (int j = 0; j < actionSize; j++)
            {
                IProductModuleAction action = actions.get(j);
                action.executeProductModuleAction(dealPmtd, uca, btd);
            }
        }
        List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        for (PlatSvcTradeData platSvcTradeData : platSvcTradeDatas)
        {
            IData databus = new DataMap();
            PlatSvcData psd = (PlatSvcData) platSvcTradeData.getPmd();
            PlatOfficeData officeData = null;
            if (psd != null)
            {
                officeData = psd.getOfficeData();
            }
            else
            {
                try
                {
                    // 如果局数据都不存在,则不作处理； 局数据不存在，让作退订处理,所以不抛出异常
                    officeData = PlatOfficeData.getInstance(platSvcTradeData.getElementId());
                }
                catch (Exception e)
                {
                	//log.info("(e);
                    continue;
                }

                psd = new PlatSvcData();
                psd.setOfficeData(officeData);
                platSvcTradeData.setPmd(psd);
            }

            databus.put("ACTION_TYPE", "PlatCheckAfter");
            databus.put("TRADE_TYPE_CODE", btd.getRD().getTradeType().getTradeTypeCode());
            databus.put("ORDER_TYPE_CODE", btd.getRD().getOrderTypeCode());
            databus.put("BIZ_TYPE_CODE", officeData.getBizTypeCode());
            databus.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            databus.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            databus.put("PRODUCT_ID", "-1");
            databus.put("BRAND_CODE", "ZZZZ");
            databus.put("EPARCHY_CODE", "ZZZZ");
            databus.put("SERVICE_ID", platSvcTradeData.getElementId());
            databus.put("SP_CODE", psd.getOfficeData().getSpCode());
            databus.put("BIZ_CODE", psd.getOfficeData().getBizCode());
            databus.put(PlatConstants.RULE_UCA, uca);
            databus.put(PlatConstants.RULE_PLATSVC_TRADE, platSvcTradeData);
            databus.put(PlatConstants.TF_B_TRADE_PLATSVC, PlatConstants.TF_B_TRADE_PLATSVC);// 标识为平台业务
            IData result = BreEngine.bre4SuperLimit(databus);
            if (IDataUtil.isNotEmpty(result))
            {
                CSAppException.breerr(result);
            }
        }
        if (log.isDebugEnabled())
        {
            log.debug("执行ProductModuleAction cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
        }
    }
}
