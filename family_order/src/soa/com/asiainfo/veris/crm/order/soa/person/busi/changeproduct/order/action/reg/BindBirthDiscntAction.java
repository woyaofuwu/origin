package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 校验用户办理的优惠是否是生日权益包，如果是的话按照入参指定的开始，结束时间订购资费
 * dengyi5
 */
public class BindBirthDiscntAction implements ITradeAction{
	
	private static final Logger logger = Logger.getLogger(BindBirthDiscntAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		if(logger.isDebugEnabled())
		{
			logger.error("----BindBirthDiscntAction------in31="+btd);
		}
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
		IData param = btd.getRD().getPageRequestData();
		if(logger.isDebugEnabled())
		{
			logger.error("----BindBirthDiscntAction------in37="+param);
		}
		//判断是否存在优惠子台帐
		if (discntTrades != null && discntTrades.size() > 0)
        {
			String elementId = "";
			IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2745", "ROAM_BIRTH", CSBizBean.getVisit().getStaffEparchyCode());
	    	if (DataUtils.isNotEmpty(commparaSet))
			{
				elementId = commparaSet.getData(0).getString("PARA_CODE1");
			}
	    	else
	    	{
	    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取用户全球通生日权益资费编码失败");
	    	}
			//循环优惠
			for (DiscntTradeData discntTrade : discntTrades)
            {
				//判断优惠是否是新增
				if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
                {
					//判断是否是生日权益包
					if (elementId.equals(discntTrade.getElementId()))
					{
//						SS.InterRoamingSVC.roamBirthQry
						String startDate = param.getString("START_DATE","").trim();
						String endDate = param.getString("END_DATE","").trim();
						logger.error("----BindBirthDiscntAction------in67START_DATE= "+startDate);
						logger.error("----BindBirthDiscntAction------in68END_DATE= "+endDate);

						if(startDate.equals("")||endDate.equals("")){
							
							UcaData uca= btd.getRD().getUca();
							
							IData cond = new DataMap();
							cond.put("SERIAL_NUMBER", uca.getSerialNumber());
							logger.error("----BindBirthDiscntAction------in74= "+cond);
							
		    				IDataset rtDataset = CSAppCall.call("SS.InterRoamingSVC.roamBirthQry", cond);
		    				IData redata = rtDataset.first();
		    				String resultcode = redata.getString("X_RESULTCODE","").trim();
		    				if(!resultcode.equals("0000")){
								CSAppException.apperr(CrmCommException.CRM_COMM_103, redata.getString("X_RESULTINFO","").trim());
							}
							logger.error("----BindBirthDiscntAction------in77= "+rtDataset);									    				
		    				
		    				cond.clear();
		    				cond.put("SERIAL_NUMBER", uca.getSerialNumber());
		    				cond.put("BIRTHDAY", redata.getString("BIRTHDAY",""));
							logger.error("----BindBirthDiscntAction------in84= "+cond);
							
		    				rtDataset = CSAppCall.call("SS.InterRoamingSVC.roamBirthCheck", cond);
							logger.error("----BindBirthDiscntAction------in87= "+rtDataset);
		    				
							redata = rtDataset.first();
		    				resultcode = redata.getString("X_RESULTCODE","").trim();
		    				if(!resultcode.equals("0000")){
								CSAppException.apperr(CrmCommException.CRM_COMM_103, redata.getString("X_RESULTINFO","").trim());
							}
							
							logger.error("----BindBirthDiscntAction------in90= "+redata);
							
		    				startDate = redata.getString("BIRTH_START_DATE","").trim();
		    				endDate = redata.getString("BIRTH_END_DATE","").trim();
		    				
						}
						
						discntTrade.setStartDate(startDate);
						discntTrade.setEndDate(endDate);
						if(logger.isDebugEnabled())
						{
							logger.error("----BindBirthDiscntAction------in115= "+discntTrade);
						}
						
					}
                }
            }
        }
	}
}
