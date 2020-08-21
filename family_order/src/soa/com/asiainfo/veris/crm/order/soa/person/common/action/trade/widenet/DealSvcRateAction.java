
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import org.apache.log4j.Logger;

import java.util.List;

public class DealSvcRateAction implements ITradeAction
{

    /**
     * 生成速率
     * 
     * @author yuyj3
     */
    private static Logger logger = Logger.getLogger(DealSvcRateAction.class);
    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (SvcTradeData svcTradeData : svcTradeDatas)
        {    	
            String mainTag = svcTradeData.getMainTag();
            String serviceId = svcTradeData.getElementId();
            String modifytag = svcTradeData.getModifyTag();
            String startDate = svcTradeData.getStartDate();
            if (!"0".equals(mainTag))
            {
                continue;
            }
            IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "4000", serviceId, btd.getRD().getUca().getUserEparchyCode());
            if (IDataUtil.isNotEmpty(commparaInfos) && commparaInfos.size() == 1)
            {
                svcTradeData.setRsrvStr1(commparaInfos.getData(0).getString("PARA_CODE1"));

                svcTradeData.setRsrvStr2(commparaInfos.getData(0).getString("PARA_CODE2",""));

                if(BofConst.MODIFY_TAG_ADD.equals(modifytag)){
                    List<BaseTradeData> wideTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
                    logger.error("========DealSvcRateAction===wideTradeData:" + wideTradeData);
                    boolean flag = false;
                    if(wideTradeData!=null&&wideTradeData.size()>0) {
                        for (int i = 0; i < wideTradeData.size(); i++) {
                            WideNetTradeData wntd = (WideNetTradeData) wideTradeData.get(i);
                            if (BofConst.MODIFY_TAG_ADD.equals(wntd.getModifyTag())) {

                                //变更非FTTH制式
                                logger.error("========DealSvcRateAction===wideNetType:" + wntd.getRsrvStr2());
                                if (!BofConst.WIDENET_TYPE_FTTH.equals(wntd.getRsrvStr2())
                                        && !BofConst.WIDENET_TYPE_TTFTTH.equals(wntd.getRsrvStr2())) {
                                    flag = true;
                                    break;
                                }

                                //移机到非城区
                                String deviceId = wntd.getRsrvNum1();
                                IData param = new DataMap();
                                param.put("DEVICE_ID", deviceId);
                                IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
                                logger.error("========DealSvcRateAction===城区判断==param:" + param + ";rs:" + rs);
                                if (IDataUtil.isNotEmpty(rs)) {
                                    IData data = rs.first();
                                    if ("0".equals(data.getString("status", ""))) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if(flag){//变更非FTTH制式 或 移机到非城区
                        continue;
                    }

                    String serialNumber = btd.getRD().getUca().getSerialNumber();
                    if (serialNumber.startsWith("KD_")) {
                        serialNumber = serialNumber.substring(3);
                    }
                    logger.error("========DealSvcRateAction===serialNumber:" + serialNumber);

                    IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
                    logger.error("========DealSvcRateAction===userInfo:" + userInfo);

                    if (IDataUtil.isEmpty(userInfo)) {
                        continue;
                    }

                    String userId = userInfo.getString("USER_ID");

                    IDataset userSPAMDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(userId, "80176874");
                    logger.error("========DealSvcRateAction===userSPAMDiscnts:" + userSPAMDiscnts);

                    String widenetUserRate = commparaInfos.getData(0).getString("PARA_CODE1");
                    if (IDataUtil.isNotEmpty(userSPAMDiscnts)) {//有300M免费提速包优惠

                        String discntEndDate = userSPAMDiscnts.getData(0).getString("END_DATE");//优惠截止时间
                        logger.error("========DealSvcRateAction==startDate="+startDate+";discntEndDate="+discntEndDate+";timeCompare:" + SysDateMgr.compareTo(startDate,discntEndDate));

                        if(SysDateMgr.compareTo(startDate,discntEndDate) <= 0){//开始时间大于优惠截止时间，则不做处理
                            if (Integer.valueOf(widenetUserRate) < (300 * 1024)) {//此次变更小于300M
                                svcTradeData.setRsrvStr1("307200");//300M速率保留
                                svcTradeData.setRsrvStr10("300M提速优惠80176874");//300M速率保留备注
                            }
                        }
                    }
                }
            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_2);
            }
        }
    }
}
