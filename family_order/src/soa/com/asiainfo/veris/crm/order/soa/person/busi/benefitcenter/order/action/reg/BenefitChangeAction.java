package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.reg;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import java.util.List;

/**
 * COMMPARA表param_attr=7172 的配置的优惠优效时间修改
 * CRM权益中心相关需求
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 8:43
 */
public class BenefitChangeAction implements ITradeAction {

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        List<DiscntTradeData> discntTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        List<OfferRelTradeData> offerRelTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);

        String startDate = btd.getRD().getPageRequestData().getString("START_DATE");
        String endDate = btd.getRD().getPageRequestData().getString("END_DATE");
        if(CollectionUtils.isEmpty(discntTradeList)){
            return;
        }
        if(StringUtils.isBlank(startDate)||StringUtils.isBlank(endDate)){
            return;
        }
        if(discntTradeList!=null&&discntTradeList.size()>0){
            for(DiscntTradeData discnts:discntTradeList){
                String elementId = discnts.getElementId();
                String modifyTag = discnts.getModifyTag();
                //查询是否为配置的指定的优惠套餐
                if("0".equals(modifyTag)){
                    IDataset discntConfig =CommparaInfoQry.getCommparaAllColByParser("CSM", "7172", elementId,  "0898");;
                    if(IDataUtil.isNotEmpty(discntConfig)){
                        discnts.setStartDate(startDate);
                        discnts.setEndDate(endDate);
                        //修改对应的offerRel
                        if(offerRelTradeList!=null&&offerRelTradeList.size()>0){
                            for(OfferRelTradeData offerRel:offerRelTradeList){
                                String relOfferCode = offerRel.getRelOfferCode();
                                if("0".equals(offerRel.getModifyTag())&&elementId.equals(relOfferCode)){
                                    offerRel.setStartDate(startDate);
                                    offerRel.setEndDate(endDate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
