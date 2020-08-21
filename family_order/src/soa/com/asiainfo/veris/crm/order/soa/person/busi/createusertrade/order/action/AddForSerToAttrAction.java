
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;

/**
 * 920服务加入特定优惠到用户属性表
 * 
 * @author sunxin
 */
public class AddForSerToAttrAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        //是否有新产品
        String newProductId = "";
        List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        
        if(productTrade != null && productTrade.size() > 0)
        {
            for(ProductTradeData product : productTrade)
            {
                if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag()))
                {
                    newProductId = product.getProductId();
                }
            }
        }
        
        if(StringUtils.isNotBlank(newProductId) && ("10001005".equals(newProductId) || "10001139".equals(newProductId)))
        {
            boolean isExist = false;
            String instId = "";// 用于确认后续拼attr的instid一致
            List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            for (SvcTradeData svcTradeData : svcTradeDatas)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()))
                {
                    if (svcTradeData.getElementId().equals("920"))
                    {
                        isExist = true;
                        instId = svcTradeData.getInstId();
                        break;
                    }

                }
            }
            if (isExist)
            {
                List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                for (DiscntTradeData discntTradeData : discntTradeDatas)
                {
                    // 判断R类型优惠 拼入attr
                    if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
                    {
                        String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(discntTradeData.getElementId());
                        
                        if ("R".equals(discntElementType))
                        {
                            AttrTradeData AttrTradeData = new AttrTradeData();

                            AttrTradeData.setUserId(btd.getRD().getUca().getUserId());
                            AttrTradeData.setInstType("S");
                            AttrTradeData.setInstId(SeqMgr.getInstId());
                            AttrTradeData.setRelaInstId(instId);// 老代码就是使用原有instid
                            AttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            AttrTradeData.setAttrCode("DST_OF_192SVC");
                            AttrTradeData.setAttrValue(discntTradeData.getDiscntCode());
                            AttrTradeData.setStartDate(discntTradeData.getStartDate());
                            AttrTradeData.setEndDate(discntTradeData.getEndDate());
                            AttrTradeData.setRsrvDate1(discntTradeData.getRsrvDate1());
                            AttrTradeData.setRsrvDate2(discntTradeData.getRsrvDate2());
                            AttrTradeData.setRsrvDate3(discntTradeData.getRsrvDate3());
                            AttrTradeData.setRsrvNum1(discntTradeData.getRsrvNum1());
                            AttrTradeData.setRsrvNum2(discntTradeData.getRsrvNum2());
                            AttrTradeData.setRsrvNum3(discntTradeData.getRsrvNum3());
                            AttrTradeData.setRsrvNum4(discntTradeData.getRsrvNum4());
                            AttrTradeData.setRsrvNum5(discntTradeData.getRsrvNum5());
                            AttrTradeData.setRsrvStr1("920");// 老代码存入服务id
                            AttrTradeData.setElementId("920");// 存入服务id
                            AttrTradeData.setRsrvStr2(discntTradeData.getRsrvStr2());
                            AttrTradeData.setRsrvStr3(discntTradeData.getRsrvStr3());
                            AttrTradeData.setRsrvStr4(discntTradeData.getRsrvStr4());
                            AttrTradeData.setRsrvStr5(discntTradeData.getRsrvStr5());
                            AttrTradeData.setRsrvTag1(discntTradeData.getRsrvTag1());
                            AttrTradeData.setRsrvTag2(discntTradeData.getRsrvTag2());
                            AttrTradeData.setRsrvTag3(discntTradeData.getRsrvTag3());
                            btd.add(btd.getRD().getUca().getSerialNumber(), AttrTradeData);
                        }
                    }
                }
            }
        }
    }
}
