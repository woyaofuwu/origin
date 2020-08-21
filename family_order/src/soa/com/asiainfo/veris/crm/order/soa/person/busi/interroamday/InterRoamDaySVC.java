
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.GPRSDiscntChangeSVC;

public class InterRoamDaySVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(GPRSDiscntChangeSVC.class);

    private static final long serialVersionUID = 1L;

    public void checkUserInterRoamSvcInfo(IData userInfo) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        bean.checkUserInterRoamSvcInfo(userInfo);
    }

    public IData geneDelDiscntDate(IData data) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        return bean.geneDelDiscntDate(data);
    }

    public IData geneNewDiscntDate(IData data) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        return bean.geneNewDiscntDate(data);
    }

    public IDataset getInterRoamDayDiscntInfo(IData userInfo) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        return bean.getInterRoamDayDiscntInfo(userInfo);
    }
    
    public void checkUserInterRoamSvcInfoIsOrder(IData userInfo) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        bean.checkUserInterRoamSvcInfoIsOrder(userInfo);
    }
    
    public IDataset queryDiscntAreas(IData input) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        IDataset results = bean.queryDiscntAreas(input);

        return results;
    }

    public IDataset queryDiscntInfos(IData input) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        IDataset results = bean.queryDiscntInfos(input);

        return results;
    }
    
    public IDataset queryDiscntDetails(IData input) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        IDataset results = bean.queryDiscntDetails(input);

        return results;
    }
    
    public IDataset interRoamQuery(IData input) throws Exception
    {
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        IDataset results = bean.interRoamQuery(input);

        return results;
    }
    
    public IDataset queryCancelRoamDict(IData input) throws Exception
    {
    	InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        IDataset results = bean.queryCancelRoamDict(input);

        return results;
    }
    
    public IData checkAcctBalanceBySerialNumber(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("RESULT_CODE", "00");
    	result.put("RESULT_INFO", "OK");
    	String userId = "";
    	String sn = "";
    	String acctBalance = "0";
    	String discntFee = input.getString("DISCNT_FEE","0");
    	IDataset userInfos = CSAppCall.call("SS.QueryInfoSVC.getUserInfo", input);
    	if(IDataUtil.isNotEmpty(userInfos)){
    		IData userInfo = userInfos.getData(0);
    		userId = userInfo.getString("USER_ID","");
    		sn = userInfo.getString("SERIAL_NUMBER","");
    		//IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
    		IDataset oweFeeData = AcctCall.QryDoRomanAccountDeposit(sn);
        	acctBalance = oweFeeData.getData(0).getString("DEPOSIT_BALANCE");
    	}
 	   	Double discntFeeNew = Double.parseDouble(discntFee) / 100;
 	    Double acctBalanceNew = Double.parseDouble(acctBalance) / 100;
 	   if (acctBalanceNew  < discntFeeNew )
       {
 		  result.put("RESULT_CODE", "-1");
       }

        return result;
    }
    
    
    public IData reCalldoRomanAccep4TradeOtherFee(IData input) throws Exception
    {
    	InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
    	IData result = bean.reCalldoRomanAccep4TradeOtherFee(input);
    	return result;
    }
    
    public void checkIsWriteOffPeriod(IData input) throws Exception
    {
    	InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        bean.checkIsWriteOffPeriod(input);
    }
    
}
