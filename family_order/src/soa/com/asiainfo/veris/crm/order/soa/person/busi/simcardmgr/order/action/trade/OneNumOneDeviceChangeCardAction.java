
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 初始化服务密码
 * 
 * @author
 */
public class OneNumOneDeviceChangeCardAction implements ITradeAction
{
    protected static Logger log = Logger.getLogger(OneNumOneDeviceChangeCardAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        log.debug("========OneNumOneDeviceChangeCardAction===begin======");
        String tradeType = btd.getMainTradeData().getTradeTypeCode();
        String simNoOccupyTag  = btd.getMainTradeData().getRsrvStr4();
        String remark = btd.getMainTradeData().getRemark();
        boolean isNoOccupy = ("142".equals(tradeType) && "1".equals(simNoOccupyTag));//跨区补区功能不占用卡
        log.debug("========OneNumOneDeviceChangeCardAction===isNoOccupy="+isNoOccupy);

        if (!isNoOccupy)  {
            log.debug("========OneNumOneDeviceChangeCardAction===remark="+remark);

            if(StringUtils.isNotBlank(remark) && "OneNoOneTerminal".equals(remark)){
                //向一级能力开放平台发起补换eSIM成功通知，并发起Profile准确的请求
                oneNoOneTerminal(btd);
            }
        }
        log.debug("========OneNumOneDeviceChangeCardAction===end======");
    }

    private void oneNoOneTerminal(BusiTradeData btd) throws Exception {
        log.debug("=======private=oneNoOneTerminal===begin=");

        String oldIccid = btd.getMainTradeData().getRsrvStr9();
        String newEid = "";
        String newIccid = btd.getMainTradeData().getRsrvStr10();
        String primarymsisdn = "";
        String imei = "";
        String oldEid="";
        List<ResTradeData> tradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        if(tradeDatas!=null && tradeDatas.size()>0){
            for(int i=0,j=tradeDatas.size();i<j;i++){
                ResTradeData resTradeData = tradeDatas.get(i);
                if("E".equals(resTradeData.getResTypeCode()) && "OneNoOneTerminal".equals(resTradeData.getRsrvStr1())){
                    String [] newEids = resTradeData.getRsrvStr2().split("@");
                    newEid = newEids[0];
                    primarymsisdn = resTradeData.getRsrvStr3();
                    imei = resTradeData.getRsrvStr5();
                    oldEid = resTradeData.getRsrvStr4();
                }
            }
        }
        IData param = new DataMap();
        param.put("msisdn", btd.getMainTradeData().getSerialNumber());
        log.debug("=======private=oneNoOneTerminal===primarymsisdn="+primarymsisdn);
        if(StringUtils.isNotEmpty(primarymsisdn)){
            param.put("primarymsisdn", primarymsisdn);
            param.put("deviceType", "1");
        }else{
            param.put("deviceType", "2");
        }
        param.put("eid", newEid);
        param.put("imei", imei);
        param.put("iccid1", newIccid);
        log.debug("=======private=oneNoOneTerminal===oldEid="+oldEid+";newEid="+newEid);
        if(StringUtils.equals(oldEid,newEid)){
            param.put("bizType", "002");//补写卡
        }else {
            param.put("bizType", "003");//补换设备
        }
//		param.put("eid2", newEid);
        param.put("iccid2", oldIccid);
        param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        log.debug("=======private=oneNoOneTerminal===param="+param);

        insertESIMQRCodeInfo(param,btd.getMainTradeData().getUserId());
        log.debug("=======private=oneNoOneTerminal===end=");

    }

    private void insertESIMQRCodeInfo(IData param, String userId) throws Exception	{
        IData input = new DataMap();

        input.put("QR_CODE_ID", SeqMgr.getInstId());
        input.put("USER_ID", userId);
        input.put("SERIAL_NUMBER", param.getString("msisdn"));
        input.put("BIZ_TYPE", param.getString("bizType"));
        input.put("BIZ_TYPE_TIME", param.getString("biztypeTime"));
        input.put("PRIMARY_MSISDN", param.getString("primarymsisdn"));
        input.put("DEVICE_TYPE", param.getString("deviceType"));
        input.put("EID", param.getString("eid"));
        input.put("IMEI", param.getString("imei"));
        input.put("ICCID1", param.getString("iccid1"));
        input.put("ICCID2", param.getString("iccid2"));
        input.put("ACCEPT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        input.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); //保存补换esim设备，营业员工号
        input.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        input.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        input.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        Dao.insert("TF_B_ESIM_QRCODE", input);
    }
}
