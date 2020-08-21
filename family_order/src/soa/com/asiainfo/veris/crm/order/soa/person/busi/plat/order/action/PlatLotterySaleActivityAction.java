package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTradeCJInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 *  平台业务抽奖营销活动处理
 *  需求：客户办理指定计费业务，成功办理后，系统将下发抽奖短信，客户回复CJ即可参加抽奖，若客户不退订业务，则订购后的第二个月，系统将在6日下发抽奖短信。
 * @author bobo
 *
 */
public class PlatLotterySaleActivityAction implements IProductModuleAction {

	@Override
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {
		PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
		String serialNumber = uca.getSerialNumber();
		String userId = uca.getUserId();
		String smsContent = "";
		
		//先看当前平台服务是否在配置里面，只有在订购了在配置里面的平台业务，才能参加抽奖活动
		IDataset commpara3712List = CommparaInfoQry.getCommByParaAttr("CSM", "3712", "ZZZZ");
		IData commpara = null;
		boolean flag = false;
		if(commpara3712List!=null)
		{
			for(int i =0;i<commpara3712List.size();i++)
			{
				commpara = commpara3712List.getData(i);
				String serviceId = commpara.getString("PARAM_CODE","");
				if(serviceId.equals(pstd.getElementId()) &&  
						PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
				{
					smsContent = commpara.getString("PARA_CODE24");
					String discntCode = commpara.getString("PARA_CODE2");
					//首免套餐,有首免套餐不能参加
					List discntList = uca.getUserDiscntByDiscntId(discntCode);
					if(discntList ==null || discntList.size()==0)
					{
						flag = true;
					}
					
				}
			}
		}
		
		if(flag)
		{
			if("98001901".equals(pstd.getElementId()))
			{
			  String memberLevel = PlatUtils.getAttrValue("302", pstd.getAttrTradeDatas());
			  //只有高级的会员能参加活动
			  if(!"3".equals(memberLevel))
			  {
				  return;
			  }
			}
			  
			  //判断是否通过主资费套餐订购的，如果是，不下发短信
			  IDataset commpara153List = CommparaInfoQry.getCommparaByAttrCode1("CSM", "153", pstd.getElementId(), CSBizBean.getUserEparchyCode(),null);
			  List<DiscntTradeData> userDiscntList = new ArrayList<DiscntTradeData>();
			  for(int j =0;j<commpara153List.size();j++)
			  {
				  commpara = commpara153List.getData(j);
				  userDiscntList.addAll(uca.getUserDiscntByDiscntId(commpara.getString("PARAM_CODE")));
			  }
			  
			  if(userDiscntList.size() >0)
			  {
				  return;
			  }
			  
			 IDataset infos = UserTradeCJInfoQry.qryCJInfos(serialNumber, userId, pstd.getElementId());
			 if(infos.size()>0)
			 {
				 return ;
			 }
			 
			 
			  IData tradeCJParam = new DataMap();
			  tradeCJParam.put("TRADE_ID", btd.getTradeId());
			  tradeCJParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			  tradeCJParam.put("USER_ID", userId);
			  tradeCJParam.put("SERIAL_NUMBER", serialNumber);
			  tradeCJParam.put("SERVICE_ID", pstd.getElementId());
			  tradeCJParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
			  tradeCJParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			  tradeCJParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			  tradeCJParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
			  tradeCJParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			  tradeCJParam.put("CANCEL_TAG", "0");
			  tradeCJParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
			  tradeCJParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			  tradeCJParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			  tradeCJParam.put("RSRV_STR1", "1");
			  tradeCJParam.put("REMARK", "业务平台发生短信");
			  Dao.insert("TF_B_TRADE_CJ", tradeCJParam);
			  
			  PlatUtils.insertSms(serialNumber, userId, smsContent, CSBizBean.getUserEparchyCode(), "2014平台业务营销活动短信");
			  
			}
			
		}
		
}
