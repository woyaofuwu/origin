
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData; 

/**
 * REQ201608190015 开户时触发账本同步内存库账本的优化
 * 批量预开户用户，在做激活时（实名制登记），后台做0元缴费操作。
 */
public class ModifyFeeZeroBatUser implements ITradeAction
{
	private static final Logger log = Logger.getLogger(ModifyFeeZeroBatUser.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	ModifyCustInfoReqData rd = (ModifyCustInfoReqData) btd.getRD();// 获取请求数据对象
    	String flag="";
    	String serialNumber=rd.getUca().getSerialNumber();
    	IData param=new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber); 
    	IDataset qryResult=Dao.qryByCode("TF_F_USER", "SEL_USER_IS_BAT_OPEN", param);
    	//优化 IDataset qryBhRsult=Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SERIAL_NUMBER_14", param,Route.getJourDb(BizRoute.getRouteId()));
    	if(IDataUtil.isNotEmpty(qryResult)){
    		String developDate=DateFormatUtils.format(SysDateMgr.string2Date(qryResult.getData(0).getString("DEVELOP_DATE","2000-01-01"), SysDateMgr.PATTERN_STAND_YYYYMMDD),SysDateMgr.PATTERN_STAND_YYYYMMDD);
    		String lastFirstDate=SysDateMgr.firstDayOfDate(SysDateMgr.getFirstDayOfThisMonth(),-1);
    		if(StringUtils.isNotEmpty(lastFirstDate)&&lastFirstDate.equals(developDate)){
    			flag="YES";	
    		}
    	}else{
    		flag="NO";
    	}
    	//log.info("("********cxy*******qryResult="+qryResult);
    	if("YES".equals(flag)){
	    	List<FeeTradeData> feeTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
			
			if(feeTradeDatas == null || feeTradeDatas.size()==0){
				FeeTradeData feeTradeData = new FeeTradeData();
				feeTradeData.setFee("0");
				feeTradeData.setFeeMode("2");
				feeTradeData.setFeeTypeCode("0");
				feeTradeData.setOldfee("0");
				feeTradeData.setUserId(btd.getRD().getUca().getUserId());
				btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
			}
			else
			{
				boolean addZeroFee = false ;
				for(int i = 0 ; i < feeTradeDatas.size() ; i++)
				{
					FeeTradeData feeTrade = feeTradeDatas.get(i);
					String feeMode = feeTrade.getFeeMode();
					if("2".equals(feeMode))
					{
						addZeroFee = true;
						break;
					}
				}
				
				if(!addZeroFee)
				{
					FeeTradeData feeTradeData = new FeeTradeData();
					feeTradeData.setFee("0");
					feeTradeData.setFeeMode("2");
					feeTradeData.setFeeTypeCode("0");
					feeTradeData.setOldfee("0");
					feeTradeData.setUserId(btd.getRD().getUca().getUserId());
					btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
				}
			}	
    	}
    } 
}
