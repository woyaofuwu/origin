
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.requestdata.NoPhoneTopSetBoxRequestData;
/**
 * @Description: 机顶盒正式受理or换机登记服务
 * @author: zhengkai5
 */
public class NoPhoneTopSetBoxRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "4910");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "4910");
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception {
        
        NoPhoneTopSetBoxRequestData noPhoneTopSetBoxRD = (NoPhoneTopSetBoxRequestData) btd.getRD();
        
        String topSetBoxTime = noPhoneTopSetBoxRD.getTopSetBoxTime();
        String topSetBoxFee = noPhoneTopSetBoxRD.getTopSetBoxFee();
        
        //修改平台服务表中魔百和受理时长，受理费用，受理时间。
        List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        if (null != platSvcTradeDatas)
        {
        
          //现网问题，无手机魔百和换机后，预留字段4，5，6全为空了。续费的时候就会报错
            String interTvSource = input.getString("INTERNET_TV_SOURCE", "");
            if(StringUtils.equals("CHANGE_TOPSETBOX", interTvSource)){
                String instId = platSvcTradeDatas.get(0).getInstId();
                IDataset userPlatSvcs = BofQuery.queryUserAllPlatSvc( noPhoneTopSetBoxRD.getUca().getUserId(), CSBizBean.getUserEparchyCode());                            
                if(IDataUtil.isNotEmpty(userPlatSvcs)){
                    for(int i=0;i<userPlatSvcs.size();i++){
                        IData tempData = userPlatSvcs.getData(i);
                        String tempInstId = tempData.getString("INST_ID");
                        if(StringUtils.equals(tempInstId, instId)){
                            platSvcTradeDatas.get(0).setRsrvStr4(tempData.getString("RSRV_STR4"));
                            platSvcTradeDatas.get(0).setRsrvStr5(tempData.getString("RSRV_STR5"));
                            platSvcTradeDatas.get(0).setRsrvStr6(tempData.getString("RSRV_STR6"));
                            break;
                        }
                    }
                                        
                }
            }else {
                platSvcTradeDatas.get(0).setRsrvStr4(topSetBoxTime);
                platSvcTradeDatas.get(0).setRsrvStr5(topSetBoxFee);
                platSvcTradeDatas.get(0).setRsrvStr6(SysDateMgr.getSysDateYYYYMMDDHHMMSS());

            }
        
        
        }
        
        dealNewResinfo(input,btd);
        
        
        
    }

    private void dealNewResinfo(IData input,BusiTradeData btd)  throws Exception
    {
        //以前的逻辑，换机后，res表会多一条imsi为空的垃圾数据，换机后就没法拆机了，这里修改下，删除这条为空的数据，只针对新数据模型
        //老的数据 KD_6002688   新的数据 KD_17889847025
        //拼装魔百和开户受理数据，OPEN_TOPSETBOX : 开户  ; CHANGE_TOPSETBOX : 换机 ;
        String interTvSource = input.getString("INTERNET_TV_SOURCE", "");
        String serialNumberB = input.getString("SERIAL_NUMBER_B","");//手机号码
        if (StringUtils.equals("CHANGE_TOPSETBOX", interTvSource))
        {
            List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
            if (resTradeDatas != null && resTradeDatas.size() > 0 && serialNumberB.length() > 10)
            {
                for (int i = 0; i < resTradeDatas.size(); i++)
                {
                    String imsi = resTradeDatas.get(i).getImsi();
                    if (StringUtils.isBlank(imsi))
                    {
                        resTradeDatas.remove(i);
                        i--;
                    }
                }
            }
        }
                
    }
    
}
