
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: SchoolCardOlcomAction.java
 * @Description: 校园卡指令处理
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 4, 2014 8:52:16 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 4, 2014 maoke v1.0.0 修改原因
 */
public class SchoolCardOlcomAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
       /* String eparchyCode = btd.getRD().getUca().getUserEparchyCode();
        String oldProductId = "";
        String newProductId = "";
        int delFlag = 0;
        int addFlag = 0;

        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);

        if (productTrades != null && productTrades.size() > 0)
        {
            for (ProductTradeData productTrade : productTrades)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(productTrade.getModifyTag()) && "1".equals(productTrade.getMainTag()))
                {
                    oldProductId = productTrade.getOldProductId();
                    newProductId = productTrade.getProductId();
                }
            }

            IDataset olcomProduct = CommparaInfoQry.getCommparaInfoBy5("CSM", "1703", "20", oldProductId, eparchyCode, null);
            if (IDataUtil.isNotEmpty(olcomProduct))
            {
                delFlag = 1;
            }
            olcomProduct.clear();

            olcomProduct = CommparaInfoQry.getCommparaInfoBy5("CSM", "1703", "20", newProductId, eparchyCode, null);
            if (IDataUtil.isNotEmpty(olcomProduct))
            {
                addFlag = 1;
            }

            List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);

            for (SvcTradeData svcTrade : svcTrades)
            {
                if (BofConst.MODIFY_TAG_INHERIT.equals(svcTrade.getModifyTag()) && "20".equals(svcTrade.getElementId()))
                {
                    if (1 == addFlag && 0 == delFlag)
                    {
                        svcTrade.setRsrvStr10("0");
                    }
                    if (0 == addFlag && 1 == delFlag)
                    {
                        svcTrade.setRsrvStr10("1");
                    }
                    svcTrade.setIsNeedPf("1");
                    break;
                }
            }
        }

        // 仅预约优惠变更时候 新增一条902服务台账 没明白为什么
        ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

        if (request.isBookingTag() && "2".equals(btd.getMainTradeData().getProcessTagSet().substring(8, 9)))
        {
            List<SvcTradeData> svcData = request.getUca().getUserSvcBySvcId("920");

            if (svcData != null && svcData.size() > 0)
            {
                for (int i = 0; i < svcData.size(); i++)
                {
                    SvcTradeData svcTD = svcData.get(i).clone();

                    if (BofConst.MODIFY_TAG_USER.equals(svcTD.getModifyTag()))
                    {
                        svcTD.setIsNeedPf("1");
                        svcTD.setModifyTag(BofConst.MODIFY_TAG_UPD);

                        btd.add(btd.getRD().getUca().getSerialNumber(), svcTD);

                        break;
                    }
                }
            }
        }*/
        
        UcaData uca = btd.getRD().getUca();
        
        List<SvcTradeData> svcData = uca.getUserSvcBySvcId("920");

        if (svcData != null && svcData.size() > 0)
        {
            for(SvcTradeData svc : svcData)
            {
                //当920存在的时候
                if("10001005".equals(svc.getProductId()) || "10001139".equals(svc.getProductId()) && BofConst.MODIFY_TAG_USER.equals(svc.getModifyTag()))
                {
                    List<DiscntTradeData> discntTrade = uca.getUserDiscnts();
                    if(discntTrade != null && discntTrade.size() > 0)
                    {
                        String addDiscntCode = "";
                        String delDiscntCode = "";
                        String startDate = "";
                        boolean delTag = false;
                        boolean addTag = false;
                        
                        for(DiscntTradeData discnt : discntTrade)
                        {
                            String elementType = DiscntInfoQry.getDiscntTypeByDiscntCode(discnt.getDiscntCode());
                            if("R".equals(elementType) && BofConst.MODIFY_TAG_ADD.equals(discnt.getModifyTag()))
                            {
                                addTag = true;
                                addDiscntCode = discnt.getDiscntCode();
                                startDate = discnt.getStartDate();
                            }
                            if("R".equals(elementType) && BofConst.MODIFY_TAG_DEL.equals(discnt.getModifyTag()))
                            {
                                delTag = true;
                                delDiscntCode = discnt.getDiscntCode();
                            }
                        }
                        if(delTag && addTag)//有变更必选优惠
                        {
                        	SvcTradeData svcTD = svc.clone();
                            svcTD.setIsNeedPf("1");
                            svcTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
                            //@author yanwu Modify
                            svcTD.setStartDate(startDate);
                            
                            btd.add(btd.getRD().getUca().getSerialNumber(), svcTD);
                            
                            String relaInstId = svc.getInstId();
                            
                            AttrTradeData attrData = uca.getUserAttrsByRelaInstIdAttrCode(relaInstId, "DST_OF_192SVC");
                            if(attrData != null)
                            {
                                AttrTradeData tradeAttr = attrData.clone();
                                tradeAttr.setAttrValue(addDiscntCode);
                                tradeAttr.setStartDate(startDate);
                                tradeAttr.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                tradeAttr.setRsrvNum1("920");
                                tradeAttr.setRsrvStr1("920");// 老代码存入服务id
                                tradeAttr.setElementId("920");// 存入服务id
                                btd.add(btd.getRD().getUca().getSerialNumber(), tradeAttr);
                                
                            } else{
                            	
                            	AttrTradeData tradeAttr = new AttrTradeData();
                            	tradeAttr.setUserId(svc.getUserId());
                            	tradeAttr.setInstType("S");
                            	tradeAttr.setAttrCode("DST_OF_192SVC");
                                tradeAttr.setAttrValue(addDiscntCode);
                                tradeAttr.setStartDate(startDate);
                                tradeAttr.setEndDate("2050-12-31 23:59:59");
                                tradeAttr.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                tradeAttr.setRelaInstId(relaInstId);
                                tradeAttr.setInstId(SeqMgr.getInstId());
                                tradeAttr.setRsrvNum1("920");
                                tradeAttr.setRsrvStr1("920");// 老代码存入服务id
                                tradeAttr.setElementId("920");// 存入服务id
                                tradeAttr.setRemark("没有服务属性，补上");// 存入服务id
                                btd.add(btd.getRD().getUca().getSerialNumber(), tradeAttr);
                                
                            }
                        }
                    }
                }
            }
        }
    }
}
