package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

/**
 * 处理星级客户装维时间
 * 装机实际要求：星级客户0-3级：2天；4级：1天；5级：12小时。
 * 默认为48小时内提供装维，客户可选择在48小时外的预约施工时间（时间自上午8：00—16：00）。
 * 业务受理单上要有体现这个时间，把集团公司关于安装时间的承诺加到业务受理单上。
 * （20160330：星级和时间阀值支持可配置）
 * 星级客户装机时间要传给PBOSS。
 * @author yuyj3
 *
 */
public class DealStarCustSuggestDateAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		//
		MergeWideUserCreateRequestData reqData = (MergeWideUserCreateRequestData) btd.getRD();
		String normalSerialNumber = reqData.getNormalSerialNumber();
		
		UcaData uca = UcaDataFactory.getNormalUca(normalSerialNumber);
        
        //获取星级客户登记
        String starClass = uca.getUserCreditClass();
		
		int hour = 48;
		
		//标记用户宽带速率是否大于指定值
		boolean flag = false;
		
		//宽带客户宽带装维时间阈值
		IDataset wideSpeedInstallCommpara = CommparaInfoQry.getCommparaAllCol("CSM","1601","WIDE_SPEED_INSTALL_WIDENET_TIME",reqData.getUca().getUser().getEparchyCode());
		
		if (IDataUtil.isNotEmpty(wideSpeedInstallCommpara))
		{
		    //宽带客户宽带装维时间阈值 配置的速率
		    String  commparaSpeed = wideSpeedInstallCommpara.getData(0).getString("PARA_CODE1");
		    
		    if (StringUtils.isNotBlank(commparaSpeed))
		    {
		        List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
	            
		        for (SvcTradeData svcTradeData : svcTradeDatas)
	            {
	                String mainTag = svcTradeData.getMainTag();
	                String serviceId = svcTradeData.getElementId();
	                
	                if (!"0".equals(mainTag))
	                {
	                    continue;
	                }
	                
	                IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "4000", serviceId, btd.getRD().getUca().getUserEparchyCode());
	                
	                if (IDataUtil.isNotEmpty(commparaInfos) && commparaInfos.size() == 1)
	                {
	                    //宽带用户速率
	                    String wideSpeed = commparaInfos.getData(0).getString("PARA_CODE1");
	                    
	                    if (StringUtils.isNotBlank(wideSpeed))
	                    {
	                        //宽带用户的速率大于等于 配置的阈值
	                        if (Integer.valueOf(wideSpeed) >= Integer.valueOf(wideSpeed))
	                        {
	                            hour = wideSpeedInstallCommpara.getData(0).getInt("PARA_CODE2", 24);
	                            
	                            flag = true;
	                        }
	                    }
	                }
	            }
		    }
		}
		
		//用户宽带速率大于100M，则不需要查询用户星级配置阈值
		if (!flag)
		{
	        //根据星级等级获取配置的时间阀值
	        IDataset commpara = CommparaInfoQry.getCommparaInfoBy5("CSM","1601","STAR_CUST_INSTALL_WIDENET_TIME",starClass,reqData.getUca().getUser().getEparchyCode(),null);
	        
	        if(IDataUtil.isNotEmpty(commpara))
	        {
	            hour = commpara.getData(0).getInt("PARA_CODE2", 48);
	        }
		}
		
		
		String suggestDate = reqData.getSuggestDate();
		
		if(suggestDate == null || "".equals(suggestDate))
		{
		    suggestDate = SysDateMgr.getSysTime();
		}
			
		String endSuggestDate = SysDateMgr.getAddHoursDate(suggestDate,hour);
		List<WideNetTradeData> wideNetTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
		
		if(wideNetTradeData != null && wideNetTradeData.size() >0)
		{
			WideNetTradeData wideNetTrade = wideNetTradeData.get(0);
			
			//设置安装截至时间，该时间为预约施工时间 + 装维时间
			wideNetTrade.setRsrvDate1(endSuggestDate); 
			wideNetTrade.setRsrvNum2(String.valueOf(hour));
			wideNetTrade.setRsrvNum3(starClass);
		}
	}

}
