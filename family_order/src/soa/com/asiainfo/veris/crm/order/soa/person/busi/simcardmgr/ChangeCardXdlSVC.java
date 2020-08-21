package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class ChangeCardXdlSVC extends CSBizService{
	
	/**
     * @Description: 新大陆-确认换卡接口
     * @param input
     * @return
     * @throws Exception
     * @author: wujy3
     */
    public IDataset changeCard(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        String sim_card_no = input.getString("SIM_CARD_NO");
        String imsi = input.getString("IMSI");
    	String pay_money_code = input.getString("X_TRADE_PAYMONEY");
    	String money = input.getString("X_TRADE_FEESUB");
        if (StringUtils.isEmpty(sn))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "SERIAL_NUMBER");
        }
        
        if (StringUtils.isEmpty(sim_card_no))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "SIM_CARD_NO");
        }
        
        if (StringUtils.isEmpty(imsi))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "IMSI");
        }

        if (StringUtils.isEmpty(pay_money_code))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "X_TRADE_PAYMONEY");
        }
        
        if (StringUtils.isEmpty(money))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "X_TRADE_FEESUB");
        }
/*        IDataset payInfos = new DatasetList();
        IData payInfo = new DataMap();
        payInfo.put("PAY_MONEY_CODE", pay_money_code);
        payInfo.put("MONEY", money);
        payInfos.add(payInfo);
        input.put("X_TRADE_PAYMONEY", payInfos);*/
        IDataset dataset = CSAppCall.call( "SS.SimCardTrade.tradeReg", input);
        return dataset;
    }

}
