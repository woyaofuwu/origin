
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;

public class ModifyPhoneCodeSVC extends CSBizService
{

    /**
     * 获取本业务的营业费用 校验手机支付平台和携入用户的校验 获取sim卡和号码资源
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset loadChildInfo(IData input) throws Exception
    {

        ModifyPhoneCodeBean phoneBean = (ModifyPhoneCodeBean) BeanManager.createBean(ModifyPhoneCodeBean.class);
        // 校验,提前至规则校验
        // IData checkTradeData = phoneBean.checkTrade(input);
        // 获取营业费
        IData operFee = phoneBean.getOperFee(input.getString("TRADE_TYPE_CODE", "143")).getData(0);
        // 获取资源信息
        IData resInfo = phoneBean.getUserRes(input);
        IDataset resultSet = new DatasetList();
        operFee.putAll(resInfo);
        resultSet.add(operFee);
        // resultSet.add(resInfo);
        return resultSet;
    }

    /**
     * 获取网上选号资料 NETCHOOSE_PSPT_ID NETCHOOSE_TYPE
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset queryNetChoosePhone(IData idata) throws Exception
    {
        ModifyPhoneCodeBean phoneBean = (ModifyPhoneCodeBean) BeanManager.createBean(ModifyPhoneCodeBean.class);

        return phoneBean.queryNetChoosePhone(idata);
    }

    public IDataset releaseNetChoosePhone(IData input) throws Exception
    {
        ModifyPhoneCodeBean phoneBean = (ModifyPhoneCodeBean) BeanManager.createBean(ModifyPhoneCodeBean.class);
        phoneBean.releaseNetChoosePhone(input);
        IDataset set = new DatasetList();
        return set;
    }

    /**
     * 校验号码或sim卡
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset verifyResourse(IData input) throws Exception
    {
        ModifyPhoneCodeBean phoneBean = (ModifyPhoneCodeBean) BeanManager.createBean(ModifyPhoneCodeBean.class);
        String htag = input.getString("hTag", "");
        IDataset outPutSet = new DatasetList();
        if (htag.equals("0"))
        {
            outPutSet = phoneBean.checkphone(input);
        }
        else
        {
            outPutSet = phoneBean.checkSimCard(input);
        }
        return outPutSet;
    }
    
    /**
     * 改号进行宽带过户
     * @param mainTrade
     * @return
     * @throws Exception
     */
    public IDataset changeWidenetTran(IData mainTrade)throws Exception{
    	IDataset result = new DatasetList();
		String tradeId = mainTrade.getString("TRADE_ID");

		// 查历史台账 如存在未返销的 才进行处理
		IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

		if (IDataUtil.isNotEmpty(mainHiTrade))
		{
		   IData data = new DataMap();
           data.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
           data.put("SERIAL_NUMBER_PRE", mainTrade.getString("RSRV_STR2"));
           data.put("TRADE_TYPE_CODE", "640");
           IData callData = CSAppCall.call("SS.WideChangeUserIntfSVC.tradeReg", data).getData(0);
           
           IData returnData = new DataMap();
		   returnData.clear();
		   returnData.put("改号宽带过户:", "ORDER_ID=[" + callData.getString("ORDER_ID", "") + "]," + "TRADE_ID=[" + callData.getString("TRADE_ID", "") + "]");

		   result.add(returnData);
		}

		return result;
    }
    
    
}
