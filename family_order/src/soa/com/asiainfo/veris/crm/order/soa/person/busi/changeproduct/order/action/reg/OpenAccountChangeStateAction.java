
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 单位证件开户将tf_f_user.acct_tag改成2，待激活
 */
public class OpenAccountChangeStateAction implements ITradeAction
{
    protected static Logger log = Logger.getLogger(OpenAccountChangeStateAction.class);

     @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        log.debug(">>>>>>>>>>>>>>>OpenAccountChangeStateAction Start>>>>>>>>>>>");
        UcaData uca = btd.getRD().getUca();
        String psptTypeCode = uca.getCustomer().getPsptTypeCode();
        String userTypeCode = uca.getUser().getUserTypeCode();
        String brandCode = uca.getBrandCode();
        String serialNumber = btd.getMainTradeData().getSerialNumber();
        if("A".equals(userTypeCode)||"PWLW".equals(brandCode)){
            log.error(">>>>>>>>>>>>>>>OpenAccountChangeStateAction 测试机用户或物联网不做处理>>>>>>>>>>>"+serialNumber);
            return;
        }
        CustPersonTradeData custPerson = uca.getCustPerson();
        String custName = custPerson.getRsrvStr5();
        String usePsptTypeCode = custPerson.getRsrvStr6();
        String usePsptId = custPerson.getRsrvStr7();
        if(StringUtils.isBlank(custName)&&StringUtils.isBlank(usePsptTypeCode)&&StringUtils.isBlank(usePsptId))
        {
            log.debug(">>>>>>>>>>>>>>>OpenAccountChangeStateAction 没有使用人不做处理>>>>>>>>>>>");
            return;
        }
        /**
         * 如果是以下证件开户的时候
         * 营业执照、组织机构代码证、事业单位法人证书、社会团体法人登记证书、单位证明
         * 修改tf_b_trade_user表的acct_tag字段
         * E	营业执照
         * M	组织机构代码证
         * G	事业单位法人证书
         * L	社会团体法人登记证书
         * D	单位证明
         */
        if("E".equals(psptTypeCode)
                ||"M".equals(psptTypeCode)
                ||"G".equals(psptTypeCode)
                ||"L".equals(psptTypeCode)
                ||"D".equals(psptTypeCode)){

            List<UserTradeData> userTrades = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
            userTrades.get(0).setAcctTag("2");

            List<CustomerTradeData> customerTrades =  btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
            customerTrades.get(0).setIsRealName("0");
            //设置为1，服开送停机指令
            btd.getMainTradeData().setRsrvStr6("1");

            //插入单位证件开户记录表
            insertRecord(btd);


        }
    }

    private void insertRecord(BusiTradeData btd) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER",btd.getRD().getUca().getSerialNumber());
        param.put("PSPT_TYPE_CODE",btd.getRD().getUca().getCustPerson().getRsrvStr6());
        param.put("PSPT_ID",btd.getRD().getUca().getCustPerson().getRsrvStr7());
        param.put("STATE","1");//默认为预开状态
        param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        param.put("UPDATA_TIME",SysDateMgr.getSysTime());
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
        param.put("ORDER_NO",btd.getRD().getOrderId());
        param.put("CUST_NAME",btd.getRD().getUca().getCustPerson().getRsrvStr5());
        Dao.insert("TL_B_UNITOPEN_RECORD", param);
    }
}
