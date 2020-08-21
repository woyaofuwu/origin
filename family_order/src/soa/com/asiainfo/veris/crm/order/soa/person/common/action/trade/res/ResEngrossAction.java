
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.res;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class ResEngrossAction implements ITradeAction
{
	protected static Logger log = Logger.getLogger(ResEngrossAction.class);
    /**
     * 资源预占：将资源从选占状态变为预占状态，变更前需校验该资源号码是否仍为选占状态。
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        String remark = mainTrade.getRemark();
        String orderTypeCode = DataBusManager.getDataBus().getOrderTypeCode();

        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        
        if ("680".equals(orderTypeCode))
        {
            // duhj_kd 无手机宽带开户可以不需要sim卡
            if (resTradeDatas.size() > 0 && null != resTradeDatas)
            {
                for (int i = 0; i < resTradeDatas.size(); i++)
                {
                    ResTradeData tempRes = resTradeDatas.get(i);
                    // 无手机宽带开户会生成10的开户单子，但这个卡不作为实际用途，只是为了订购魔百和服务,加个开关配置是否发送联指 duhj_kd
                    //无手机宽带如果不需要sim卡，会默认传一个898600key值，在此移除掉
                    if ("1".equals(tempRes.getResTypeCode()))
                    {
                        boolean simTag = BizEnv.getEnvBoolean("crm.nophoneWide.simSwitch", false);
                        if (!simTag)
                        {
                            resTradeDatas.remove(i);
                            i--;
                        }
                    }
                }
            }
        }
        for (ResTradeData resTradeData : resTradeDatas)
        {

            if (BofConst.MODIFY_TAG_ADD.equals(resTradeData.getModifyTag()))
            {
                if (resTradeData.getResTypeCode().equals("0"))
                {
                    if(StringUtils.isNotBlank(remark) && (remark.endsWith("mobileuseful")||remark.endsWith("OneCardOneDevice"))) //最好用手机开户
                    {

                        if(remark.endsWith("OneCardOneDevice")){
                            ResCall.resEngrossOneTerminalForMphone(resTradeData.getResCode());
                        }else {
                            String remarks[] = remark.split("@");
                            String busiType = remarks[2];//001 最好用手机 002 一号一终端
                            if (StringUtils.isNotBlank(busiType) && "002".equals(busiType)) {
                                ResCall.resEngrossOneTerminalForMphone(resTradeData.getResCode());
                            } else {
                                ResCall.resEngrossForMphoneEasy(resTradeData.getResCode());
                            }
                        }
                    }
                    else
                    {
                        if (StringUtils.isNotBlank(resTradeData.getRsrvStr5()) && "01".equals(resTradeData.getRsrvStr5()))
                        {
                            ResCall.resEngrossForIOTMphone("0", "0", resTradeData.getResCode(), resTradeData.getResTypeCode());
                        }
                        else
                        {
                            ResCall.resEngrossForMphone(resTradeData.getResCode());
                        }
                    }
                }
                else if (resTradeData.getResTypeCode().equals("1"))
                {
                    if(StringUtils.isNotBlank(remark) && remark.endsWith("mobileuseful")) //最好用手机开户
                    {   String remarks[] = remark.split("@");
                        String busiType = remarks[2];//001 最好用手机 002 一号一终端
                        if(StringUtils.isNotBlank(busiType) &&"002".equals(busiType)){
                            ResCall.resEngrossOneTerminalForSim("0", resTradeData.getResCode(), btd.getRD().getUca().getSerialNumber());
                        }else{
            				ResCall.resEngrossForSimEasy("0", resTradeData.getResCode(), btd.getRD().getUca().getSerialNumber());
            			}
                    }
                    else
                    {
                        if (StringUtils.isNotBlank(resTradeData.getRsrvStr5()) && "01".equals(resTradeData.getRsrvStr5()))
                        {
                            ResCall.resEngrossForIOTSim("0", "0", resTradeData.getResCode(), resTradeData.getResTypeCode());
                        }
                        else
                        {
                            ResCall.resEngrossForSim("0", resTradeData.getResCode(), btd.getRD().getUca().getSerialNumber());
                        }
                    }
                }
                else if (resTradeData.getResTypeCode().equals("L"))
                {
                    ResCall.resEngrossForCmnet(resTradeData.getResTypeCode(), resTradeData.getResCode());
                }

            }

        }

    }

}
