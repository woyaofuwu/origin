
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: GprsKingDiscntAction.java
 * @Description: GPRS流量王优惠时间计算 1. 用户存在两条5元的gprs套餐，一条截止到月底，一条下月1号开始生效，这时候办理5元版的流量王就截止到这个月底 2.
 *               用户存在5元和10元的gprs优惠，5元截止当月底，10元下月1号开始生效，现办理10元对应的流量王套餐，那么要求流量王套餐的开始截止时间与10元的一致 3.
 *               用户存在5元和10元的gprs优惠，5元截止当月底，10元下月1号开始生效，现办理5元对应的流量王套餐，那么要求流量王套餐的截止时间与5元的一致 4.
 *               同时办理gprs套餐+流量王套餐，那么流量王套餐的开始时间和gprs套餐的一致
 * @version: v1.0.0
 * @author: maoke
 * @date: Aug 27, 2014 3:44:07 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Aug 27, 2014 maoke v1.0.0 修改原因
 */
public class GprsKingDiscntAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();

        List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();

        if (userDiscnts != null && userDiscnts.size() > 0)
        {
            for (DiscntTradeData discnt : userDiscnts)
            {
                String elementId = discnt.getElementId();
                String modifyTag = discnt.getModifyTag();

                String discntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);

                if (PersonConst.DISCNT_TYPE_LLCX.equals(discntType) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    IDataset relyDiscnts =  UpcCall.queryOfferRelWithDiscntTypeFilter(BofConst.ELEMENT_TYPE_CODE_DISCNT, elementId, "2", "5");//ElemLimitInfoQry.getGprsKingDiscntByGprs(elementId);

                    if (IDataUtil.isNotEmpty(relyDiscnts))
                    {
                        String relyElementId = relyDiscnts.getData(0).getString("REL_OFFER_CODE");// 依赖的那个GPRS优惠

                        List<DiscntTradeData> gprsDiscnts = uca.getUserDiscntByDiscntId(relyElementId);

                        if (gprsDiscnts != null && gprsDiscnts.size() > 0)
                        {
                            String tempStartDate = SysDateMgr.END_DATE_FOREVER;
                            String tempEndDate = SysDateMgr.END_DATE_FOREVER;
                            
                            String llcxStartDate = SysDateMgr.decodeTimestamp(discnt.getStartDate(), SysDateMgr.PATTERN_STAND);
                            String llcxEndDate = SysDateMgr.decodeTimestamp(discnt.getEndDate(), SysDateMgr.PATTERN_STAND);
                            
                            for (DiscntTradeData gprsDiscnt : gprsDiscnts)
                            {
                                String gprsStartDate = SysDateMgr.decodeTimestamp(gprsDiscnt.getStartDate(), SysDateMgr.PATTERN_STAND);
                                String gprsEndDate = SysDateMgr.decodeTimestamp(gprsDiscnt.getEndDate(), SysDateMgr.PATTERN_STAND);
                                
                                if(tempStartDate.compareTo(gprsStartDate) > 0)
                                {
                                    tempStartDate = gprsStartDate;
                                }
                                
                                if(tempEndDate.compareTo(gprsEndDate) > 0)
                                {
                                    tempEndDate = gprsEndDate;
                                }
                            }
                            if(llcxStartDate.compareTo(tempStartDate) < 0)
                            {
                                discnt.setStartDate(tempStartDate);
                            }
                            if(llcxEndDate.compareTo(tempEndDate) > 0)
                            {
                                discnt.setEndDate(tempEndDate);
                            }
                        
                            OfferUtil.resetOfferRelDate(discnt, btd, uca, true);
                        }
                    }
                }
            }
        }
    }
}
