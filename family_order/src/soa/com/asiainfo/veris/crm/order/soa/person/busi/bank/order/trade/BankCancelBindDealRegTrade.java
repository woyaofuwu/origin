
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.trade;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.BankBean;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata.BankBindDealRequestData;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

/**
 * 手机和银联卡解绑台账登记
 * 
 * @author wukw3
 */
public class BankCancelBindDealRegTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	// 更新主台账的费用信息
        dealMainTradeData(btd);
        createTradeOther(btd);
    }
    
    public void dealMainTradeData(BusiTradeData btd) throws Exception
    {
    	BankBindDealRequestData reqData = (BankBindDealRequestData) btd.getRD();
        // 更新主台账的费用信息
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(reqData.getBankCardNo());
        mainTradeData.setRsrvStr2(reqData.getBankName());
        mainTradeData.setRemark(reqData.getRemark());
    }

    /**
     * 处理台帐Other子表的数据
     * 
     * @param reqData
     * @param btd
     * @throws Exception
     */
    private void createTradeOther(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        // createTradeDatumOther(btd)//TODO 可选材料登记台账
    	BankBindDealRequestData reqData = (BankBindDealRequestData) btd.getRD();
    	IData inparam = new DataMap();
    	inparam.put("USER_ID", reqData.getUca().getUserId());
    	inparam.put("RSRV_STR1", reqData.getBankCardNo());
    	
    	IDataset userOtherset = BankBean.qryBankBindInfo(inparam);
    	if(IDataUtil.isEmpty(userOtherset)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "20141203001:该用户下没有绑定的银联信息，请确认后再办理！");
    	}
    	
        OtherTradeData otherTD = new OtherTradeData(userOtherset.getData(0));
        
        otherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        otherTD.setStartDate(reqData.getAcceptTime());
        otherTD.setEndDate(reqData.getAcceptTime());
        otherTD.setRemark(reqData.getRemark());

        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    }

}
