
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import org.apache.log4j.Logger;

import java.util.List;

public class DealSPAMRateAction implements ITradeAction
{
    public static final Logger logger=Logger.getLogger(DealSPAMRateAction.class);

    public void executeAction(BusiTradeData btd) throws Exception {

        String serialNumber = btd.getMainTradeData().getSerialNumber();
        logger.debug("========DealSPAMRateAction==1=serialNumber:" + serialNumber);

        if (serialNumber.startsWith("KD_")) {
            serialNumber = serialNumber.substring(3);
        }
        logger.debug("========DealSPAMRateAction==2=serialNumber:" + serialNumber);

        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        logger.debug("========DealSPAMRateAction===userInfo:" + userInfo);

        if (IDataUtil.isEmpty(userInfo)) {
            return;
        }

        String userId = userInfo.getString("USER_ID");
        IDataset userSPAMDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(userId, "80176874");
        logger.debug("========DealSPAMRateAction===userSPAMDiscnts:" + userSPAMDiscnts);

        if (IDataUtil.isEmpty(userSPAMDiscnts)) {
            return;//没有300M免费提速包优惠
        }

        IData userSPAMDiscnt = userSPAMDiscnts.getData(0);//300M免费提速包优惠
        logger.debug("========DealSPAMRateAction===userSPAMDiscnt:" + userSPAMDiscnt);
        if (IDataUtil.isEmpty(userSPAMDiscnt)) {
            return;//没有300M免费提速包优惠
        }

        String discntEndDate = userSPAMDiscnt.getString("END_DATE");//优惠截止时间
        //变更速率大于300M情况
        List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (SvcTradeData svcTradeData : svcTradeDatas) {
            String serviceId = svcTradeData.getElementId();
            String modifyTag = svcTradeData.getModifyTag();
            String mainTag = svcTradeData.getMainTag();
            String startDate = svcTradeData.getStartDate();

            logger.debug("========DealSPAMRateAction==startDate="+startDate+";discntEndDate="+discntEndDate+";timeCompare:" + SysDateMgr.compareTo(startDate,discntEndDate));

            if(SysDateMgr.compareTo(startDate,discntEndDate) > 0){//开始时间大于优惠截止时间，则不做处理
                continue;
            }

            logger.debug("========DealSPAMRateAction===serviceId:" + serviceId + "==mainTag:" + mainTag + "==modifyTag:" + modifyTag);
            if ("0".equals(mainTag) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
                IDataset rateDs = CommparaInfoQry.getCommpara("CSM", "4000", serviceId, "0898");

                logger.debug("========DealSPAMRateAction===rateDs:" + rateDs);
                if (IDataUtil.isNotEmpty(rateDs)) {
                    String widenetUserRate = rateDs.getData(0).getString("PARA_CODE1", "0");
                    logger.debug("========DealSPAMRateAction===widenetUserRate:" + widenetUserRate);
                    if (Integer.valueOf(widenetUserRate) > (300 * 1024)) {
                        //判断宽带产品变更速率大于300M，则300M提速包优惠截止时间为新速率开始时间。
                        String endDate = SysDateMgr.getLastSecond(startDate);
                        String remake = "宽带变更速率大于300M，截止该300M提速包优惠";

                        DiscntTradeData discntTrade = new DiscntTradeData(userSPAMDiscnt);
                        discntTrade.setRemark(remake);
                        discntTrade.setEndDate(endDate);
                        discntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        logger.debug("========DealSPAMRateAction===discntTrade:" + discntTrade);

                        btd.add(btd.getRD().getUca().getSerialNumber(), discntTrade);
                        return;
                    }
                }
            }
        }

        //变更非FTTH制式 或 移机到非城区
        List<WideNetTradeData> wideNetTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
        logger.debug("========DealSPAMRateAction===wideNetTradeDatas:" + wideNetTradeDatas);
        for (WideNetTradeData wideNetTradeData : wideNetTradeDatas) {
            logger.debug("========DealSPAMRateAction===wideNetTradeData:" + wideNetTradeData);

            String modifyTag = wideNetTradeData.getModifyTag();
            if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
                continue;
            }

            String startDate = wideNetTradeData.getStartDate();//新地址生效时间
            String endDate = SysDateMgr.getLastSecond(startDate);

            if(SysDateMgr.compareTo(startDate,discntEndDate) > 0){//开始时间大于优惠截止时间，则不做处理
                continue;
            }
            //变更非FTTH制式
            logger.debug("========DealSPAMRateAction===wideNetType:" + wideNetTradeData.getRsrvStr2());
            if (!BofConst.WIDENET_TYPE_FTTH.equals(wideNetTradeData.getRsrvStr2())
                    && !BofConst.WIDENET_TYPE_TTFTTH.equals(wideNetTradeData.getRsrvStr2())) {
                String remake = "宽带变更非FTTH制式，截止该300M提速包优惠";

                DiscntTradeData discntTrade = new DiscntTradeData(userSPAMDiscnt);
                discntTrade.setRemark(remake);
                discntTrade.setEndDate(endDate);
                discntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                logger.debug("========DealSPAMRateAction===discntTrade:" + discntTrade);

                btd.add(btd.getRD().getUca().getSerialNumber(), discntTrade);
                return;
            }

            //移机到非城区
            String deviceId = wideNetTradeData.getRsrvNum1();
            IData param = new DataMap();
            param.put("DEVICE_ID",deviceId);
            IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
            logger.debug("========DealSPAMRateAction===城区判断==param:"+param+";rs:"+rs);
            if(IDataUtil.isNotEmpty(rs))
            {
                IData data = rs.first();
                if("0".equals(data.getString("status",""))){
                    String remake = "宽带移机到非城区，截止该300M提速包优惠";

                    DiscntTradeData discntTrade = new DiscntTradeData(userSPAMDiscnt);
                    discntTrade.setRemark(remake);
                    discntTrade.setEndDate(endDate);
                    discntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    logger.debug("========DealSPAMRateAction===discntTrade:" + discntTrade);

                    btd.add(btd.getRD().getUca().getSerialNumber(), discntTrade);
                    return;
                }
            }
        }
    }
}
