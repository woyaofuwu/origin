package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.action;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;


public class InterRoamAction implements ITradeAction{
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
	    String inModeCode  = btd.getMainTradeData().getInModeCode();
        String tradeTypeCode  = btd.getMainTradeData().getTradeTypeCode();
        String isRoamOpen = btd.getRD().getPageRequestData().getString("IS_ROAM_OPEN","");
        String isRoamDiscnt= btd.getRD().getPageRequestData().getString("IS_ROAM_DISCNT","");
        String isRoamPackage= btd.getRD().getPageRequestData().getString("IS_ROAM_PACKAGE","");
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        if(StringUtils.equals("true", isRoamDiscnt)||StringUtils.equals("true", isRoamOpen)){//国漫业务前台是走300
            return ;
        }
	    List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	    String serialNumber = btd.getMainTradeData().getSerialNumber();
        for (int i = 0; i < discntTradeDatas.size(); i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            String elemid = discntTradeData.getElementId();
            String modifytag = discntTradeData.getModifyTag();
            String routeType = "00";
            String routeValue = "000";
            IData result = new DataMap();
            IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742", elemid, btd.getMainTradeData().getEparchyCode());
            if (commparaSet != null && commparaSet.size() > 0)
            {
            	if(StringUtils.equals("true", isRoamPackage)){
            		IDataset results = IBossCall.getInterRoamPackage(serialNumber,"01",commparaSet.getData(0).getString("PARA_CODE2", ""), serialNumber);
            		result = results.first();
                    if (!result.getString("X_RSPCODE").equals("0000"))
                    {
                        CSAppException.apperr(IBossException.CRM_IBOSS_4, result.getString("X_RSPCODE"), result.getString("X_RSPDESC"));
                    }
            	}else{
	            	// 前台新增的传值  订购流水号 --- add by huangyq
	            	String prodInstId ="";
	            	if(BofConst.MODIFY_TAG_DEL.equals(modifytag))
	            	{
	            		String instid = "";
	            		IDataset discntDataset = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(discntTradeData.getUserId(), btd.getRD().getPageRequestData().getString("DISCNT_CODE"),CSBizBean.getTradeEparchyCode());
	            		if(DataUtils.isNotEmpty(discntDataset))
	                    {
	            			instid = discntDataset.getData(0).getString("INST_ID");
	                    }
	            		IData prodIns= UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(discntTradeData.getUserId(), instid, "PROD_INST_ID", CSBizBean.getTradeEparchyCode());
	                    if(DataUtils.isNotEmpty(prodIns))
	                    {
	                    	prodInstId = prodIns.getString("ATTR_VALUE");
	                    }
	            	}
	//                result = IBossCall.InterRoamDayforIboss(commparaSet.getData(0).getString("PARA_CODE2", ""), serialNumber, modifytag, routeValue, routeType);
	                //  换用携带流水号的 方法   --- add by huangyq
	            	result = IBossCall.InterRoamDayforIbossTakeProdInstId(prodInstId,commparaSet.getData(0).getString("PARA_CODE2", ""), serialNumber, modifytag, routeValue, routeType);
	
	                if (!result.getString("X_RSPCODE").equals("0000"))
	                {
	                    CSAppException.apperr(IBossException.CRM_IBOSS_4, result.getString("X_RSPCODE"), result.getString("X_RSPDESC"));
	                }
            	}
                // 预受理表数据
                IData preOderData = new DataMap();
                preOderData.clear();
                preOderData.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());// 主卡
                preOderData.put("PRE_TYPE", "InterRoam");
                preOderData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                preOderData.put("ORDER_ID", btd.getRD().getOrderId());
                preOderData.put("SVC_NAME", "SS.InterRoamDayTradeRegSVC.tradeReg");
                preOderData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                preOderData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                preOderData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

                TwoCheckSms.twoCheck(btd.getTradeTypeCode(), 0, preOderData, null);
                btd.getRD().setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);
            }else if("0".equals(inModeCode)&&"300".equals(tradeTypeCode)){
                CSAppException.apperr(ElementException.CRM_ELEMENT_65, elemid);
            }
        }
	}

}
