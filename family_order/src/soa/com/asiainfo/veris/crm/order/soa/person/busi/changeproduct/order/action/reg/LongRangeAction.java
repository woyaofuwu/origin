
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: LongRangeAction.java
 * @Description: 主产品变更时 当老产品下长途、漫游服务级别小于新产品下时,默认升级新产品下的长途、漫游服务
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 16, 2014 7:41:25 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 16, 2014 maoke v1.0.0 迁移湖南版本
 */
public class LongRangeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ChangeProductReqData req = (ChangeProductReqData) btd.getRD();
        boolean needAddLong = true;
        boolean needAddRoam = true;
        List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        if (tradeSvcs == null || tradeSvcs.size() <= 0)
        {
            return;
        }
        if (req.getNewMainProduct() != null)
        {
            // 表示有主产品变更
            String newProductId = req.getNewMainProduct().getProductId();
            if ("30286002".equals(newProductId))
            {
                return;
            }
            UcaData uca = btd.getRD().getUca();
            int size = tradeSvcs.size();
            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
            IDataset productElements = ProductElementsCache.getProductElements(newProductId);
            for (int i = 0; i < size; i++)
            {
                SvcTradeData pmtd = tradeSvcs.get(i);
                String elementId = pmtd.getElementId();
                if (BofConst.MODIFY_TAG_DEL.equals(pmtd.getModifyTag()))
                {
                    // 原长途漫游元素被删除,表示该元素无法被继承
                    if ("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId))
                    {
                        IData productDate = this.getProductDate(btd);
                        
                        IData defaultLong = this.getProductDefaultLong(productElements);
                        if (IDataUtil.isNotEmpty(defaultLong))
                        {
                            boolean isFind = false;
                            List<SvcTradeData> longs = uca.getUserSvcsBySvcIdArray("13,14,15");
                            int longSize = longs.size();
                            for (int j = 0; j < longSize; j++)
                            {
                                if (BofConst.MODIFY_TAG_ADD.equals(longs.get(j).getModifyTag()))
                                {
                                    isFind = true;
                                    break;
                                }
                            }
                            if (isFind)
                            {
                                continue;
                            }
                            defaultLong.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                            defaultLong.put("START_DATE", productDate.getString("NEW_PRODUCT_START_DATE", btd.getRD().getAcceptTime()));
                            defaultLong.put("END_DATE", SysDateMgr.getTheLastTime());
                            SvcData svcData = new SvcData(defaultLong);
                            elements.add(svcData);
                        }
                    }
                    else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId))
                            && !"G005".equals(req.getNewMainProduct().getBrandCode()))
                    {
                        IData productDate = this.getProductDate(btd);
                        IData defaultRoam = this.getProductDefaultRoam(productElements);
                        if (IDataUtil.isNotEmpty(defaultRoam))
                        {
                            boolean isFind = false;
                            List<SvcTradeData> roams = uca.getUserSvcsBySvcIdArray("16,17,18,19,100,102,122,134");
                            int roamSize = roams.size();
                            for (int j = 0; j < roamSize; j++)
                            {
                                if (BofConst.MODIFY_TAG_ADD.equals(roams.get(j).getModifyTag()))
                                {
                                    isFind = true;
                                    break;
                                }
                            }
                            if (isFind)
                            {
                                continue;
                            }
                            defaultRoam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                            defaultRoam.put("START_DATE", productDate.getString("NEW_PRODUCT_START_DATE", btd.getRD().getAcceptTime()));
                            defaultRoam.put("END_DATE", SysDateMgr.getTheLastTime());
                            SvcData svcData = new SvcData(defaultRoam);
                            elements.add(svcData);
                        }
                    }
                }
                else if (BofConst.MODIFY_TAG_INHERIT.equals(pmtd.getModifyTag()))
                {
                    // 原长途漫游元素被删除,表示该元素无法被继承
                    if ("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId))
                    {
                        IData productDate = this.getProductDate(btd);
                        IData defaultLong = this.getProductDefaultLong(productElements);
                        if (IDataUtil.isNotEmpty(defaultLong))
                        {
                            if (elementId.compareTo(defaultLong.getString("ELEMENT_ID")) < 0)
                            {
                                if (uca.getUserSvcBySvcId(defaultLong.getString("ELEMENT_ID")) != null)
                                {
                                    defaultLong.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                                    defaultLong.put("START_DATE", productDate.getString("NEW_PRODUCT_START_DATE", btd.getRD().getAcceptTime()));
                                    defaultLong.put("END_DATE", SysDateMgr.getTheLastTime());
                                    SvcData svcData = new SvcData(defaultLong);
                                    elements.add(svcData);
                                    pmtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    pmtd.setEndDate(productDate.getString("OLD_PRODUCT_END_DATE", btd.getRD().getAcceptTime()));
                                }
                            }
                        }
                    }
                    else if (("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId))
                            && !"G005".equals(req.getNewMainProduct().getBrandCode()))
                    {
                        IData productDate = this.getProductDate(btd);
                        IData defaultRoam = this.getProductDefaultRoam(productElements);
                        if (IDataUtil.isNotEmpty(defaultRoam))
                        {
                            if (elementId.compareTo(defaultRoam.getString("ELEMENT_ID")) < 0)
                            {
                                if (uca.getUserSvcBySvcId(defaultRoam.getString("ELEMENT_ID")) != null)
                                {
                                    defaultRoam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                                    defaultRoam.put("START_DATE", productDate.getString("NEW_PRODUCT_START_DATE", btd.getRD().getAcceptTime()));
                                    defaultRoam.put("END_DATE", SysDateMgr.getTheLastTime());
                                    SvcData svcData = new SvcData(defaultRoam);
                                    elements.add(svcData);
                                    pmtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    pmtd.setEndDate(productDate.getString("OLD_PRODUCT_END_DATE", btd.getRD().getAcceptTime()));
                                }
                            }
                        }
                    }
                }
            }
            if (elements.size() > 0)
            {
                ProductModuleCreator.createProductModuleTradeData(elements, btd);
            }
        }
    }

    private IData getProductDate(BusiTradeData btd) throws Exception
    {
        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        IData result = new DataMap();
        if (productTrades != null)
        {
            int size = productTrades.size();
            for (int i = 0; i < size; i++)
            {
                ProductTradeData productTrade = productTrades.get(i);
                if ("1".equals(productTrade.getMainTag()) && BofConst.MODIFY_TAG_ADD.equals(productTrade.getModifyTag()))
                {
                    result.put("NEW_PRODUCT_START_DATE", productTrade.getStartDate());
                }
                else if ("1".equals(productTrade.getMainTag()) && BofConst.MODIFY_TAG_DEL.equals(productTrade.getModifyTag()))
                {
                    result.put("OLD_PRODUCT_END_DATE", productTrade.getEndDate());
                }
            }
        }
        return result;
    }
    
    private IData getProductDefaultLong(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            String elementId = element.getString("ELEMENT_ID");
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && ("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
            {
                return element;
            }
        }
        return null;
    }

    private IData getProductDefaultRoam(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            String elementId = element.getString("ELEMENT_ID");
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG"))
                    && ("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId))
                    && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
            {
                return element;
            }
        }
        return null;
    }
}
