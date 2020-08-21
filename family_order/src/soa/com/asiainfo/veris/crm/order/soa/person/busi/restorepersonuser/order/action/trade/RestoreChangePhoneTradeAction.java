
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RentTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreChangePhoneTradeAction.java
 * @Description: 复机改号,TF_F_USER(UPD_CITY_BY_SN),TF_F_USER_SHARE_RELA,TF_F_RELATION_UU
 *               TF_F_USER_SALE_ACTIVE,TF_F_USER_RENT,TF_F_USER_IMEI
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-7-30 下午9:25:06
 */
public class RestoreChangePhoneTradeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        String userId = btd.getRD().getUca().getUserId();
        String newPhoneNumber = "";
        for (ResTradeData resTradeData : resTradeDatas)
        {
            // 新换的手机号码
            if (BofConst.MODIFY_TAG_ADD.equals(resTradeData.getModifyTag()) && StringUtils.equals("1", resTradeData.getRsrvTag1()) && StringUtils.equals("0", resTradeData.getResTypeCode()))
            {
                newPhoneNumber = resTradeData.getResCode();
                break;
            }
        }
        if (StringUtils.isNotEmpty(newPhoneNumber))
        {
            List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
            UserTradeData userTradeData = userTradeDatas.get(0);
            userTradeData.setSerialNumber(newPhoneNumber);

            List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            if (relationTradeDatas != null && relationTradeDatas.size() > 0)
            {
                for (int i = 0, count = relationTradeDatas.size(); i < count; i++)
                {
                    RelationTradeData tempRelationTradeData = relationTradeDatas.get(i);
                    if (StringUtils.equals(userId, tempRelationTradeData.getUserIdB()))
                    {
                        tempRelationTradeData.setSerialNumberB(newPhoneNumber);
                    }
                    else if (StringUtils.equals(userId, tempRelationTradeData.getUserIdA()))
                    {
                        tempRelationTradeData.setSerialNumberA(newPhoneNumber);
                    }
                }
            }

            List<ShareRelaTradeData> shareRelaTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SHARE_RELA);
            if (shareRelaTradeDataList != null && shareRelaTradeDataList.size() > 0)
            {
                for (int i = 0, count = shareRelaTradeDataList.size(); i < count; i++)
                {
                    ShareRelaTradeData tempShareRelaTradeData = shareRelaTradeDataList.get(i);
                    if (StringUtils.equals(userId, tempShareRelaTradeData.getUserIdB()))
                    {
                        tempShareRelaTradeData.setSerialNumber(newPhoneNumber);
                    }
                }
            }

            List<SaleActiveTradeData> saleActiveTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
            if (saleActiveTradeDataList != null && saleActiveTradeDataList.size() > 0)
            {
                for (int i = 0, count = saleActiveTradeDataList.size(); i < count; i++)
                {
                    SaleActiveTradeData tempSaleActiveTradeData = saleActiveTradeDataList.get(i);
                    if (StringUtils.equals(userId, tempSaleActiveTradeData.getUserId()))
                    {
                        tempSaleActiveTradeData.setSerialNumber(newPhoneNumber);
                    }
                }
            }

            List<RentTradeData> rentTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_RENT);
            if (rentTradeList != null && rentTradeList.size() > 0)
            {
                for (int i = 0, count = rentTradeList.size(); i < count; i++)
                {
                    RentTradeData rentTradeData = rentTradeList.get(i);
                    if (StringUtils.equals(userId, rentTradeData.getUserId()))
                    {
                        rentTradeData.setSerialNumber(newPhoneNumber);
                    }
                }
            }
        }
    }
}
