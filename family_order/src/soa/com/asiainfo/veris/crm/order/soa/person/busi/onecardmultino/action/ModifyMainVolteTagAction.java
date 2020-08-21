package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.List;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.ViceRealInfoReRegBean;

public class ModifyMainVolteTagAction implements ITradeAction
{

    
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
//        ChangeProductReqData changeproductreqdata = (ChangeProductReqData) btd.getRD();
//        String serialNumber = changeproductreqdata.getUca().getUser().getSerialNumber();
        String serialNumber = uca.getSerialNumber();
        String userId = uca.getUserId();
        
        String imsi = "";
        IDataset userResInfos = UserResInfoQry.getUserResBySelbySerialnremove(serialNumber, "1");// 查sim卡
        if (IDataUtil.isNotEmpty(userResInfos))// 虚拟用户是没有该资料的
        {
            imsi = userResInfos.getData(0).getString("IMSI", "0");
           
        }
        
        
        if("MOSP".equals(btd.getMainTradeData().getBrandCode()) && "500".equals(btd.getTradeTypeCode())){//批量开户直接订购20180109服务进行签约
			SvcTradeData svcTradeData = new SvcTradeData();
			svcTradeData.setUserId(userId);
			svcTradeData.setUserIdA("-1");
			svcTradeData.setProductId("-1");
			svcTradeData.setPackageId("-1");
			svcTradeData.setStartDate(SysDateMgr.getSysTime());
			svcTradeData.setEndDate(SysDateMgr.addDays(2)+SysDateMgr.END_DATE);
			svcTradeData.setElementId("20180109");//VOLTE服务ID
			svcTradeData.setElementType("S");
			svcTradeData.setInstId(SeqMgr.getInstId());
			svcTradeData.setMainTag("0");
			svcTradeData.setModifyTag("0");
			List<ResTradeData> tradeResList= btd.getTradeDatas(TradeTableEnum.TRADE_RES);
			if(null != tradeResList && tradeResList.size() > 0){
    			for(ResTradeData traderes:tradeResList){
    				if("1".equals(traderes.getResTypeCode())){
    					//RSRV_STR5---签约标识    RSRV_STR6---本/跨省号码标识   RSRV_STR7---副号码IMSI
    					svcTradeData.setRsrvStr3("09");
    					svcTradeData.setRsrvStr5("FOLLOW_VOLTE");
    					svcTradeData.setRsrvStr6("SAME_PROV_FOLLOW");
    					svcTradeData.setRsrvStr7(traderes.getImsi());
    					break;
    				}
    			}    	
    		}
			btd.add(serialNumber, svcTradeData);
			return;
		}  
        
        List<SvcTradeData> serviceList = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        if (!serviceList.isEmpty())
        {
            for (int i = 0; i < serviceList.size(); i++)
            {
                SvcTradeData svcTrade = serviceList.get(i);
                if ("190".equals(svcTrade.getElementId()))
                {
                    IDataset asynInfoA = RelaUUInfoQry.qryRelaUUByUserIdA(userId, "M2");
                    if(IDataUtil.isNotEmpty(asynInfoA)){
                        SvcTradeData svcTD = new SvcTradeData();
                        svcTD.setUserId(userId);
                        svcTD.setUserIdA("-1");
                        svcTD.setProductId(btd.getMainTradeData().getProductId());
                        svcTD.setPackageId("-1");
                        svcTD.setElementId("20180109");
                        svcTD.setMainTag("0");
                        svcTD.setInstId(SeqMgr.getInstId());
                        svcTD.setStartDate(SysDateMgr.getSysTime());
                        svcTD.setEndDate(SysDateMgr.addDays(2)+SysDateMgr.END_DATE);
                        svcTD.setModifyTag("0");
                        svcTD.setRsrvStr5("MAIN_VOLTE");
                        svcTD.setRsrvStr6("SAME_PROV_FOLLOW");
                        svcTD.setRsrvStr7(imsi);
                        
                        if( BofConst.MODIFY_TAG_ADD.equals(svcTrade.getModifyTag())){
                       	 svcTD.setRsrvStr3("09");
                       }else{
                       	svcTD.setRsrvStr3("10");
                       }
                        
                        btd.add(serialNumber, svcTD);
                    }
                    else
                    {
                        IDataset asynInfoB = RelaUUInfoQry.queryRelaUUBySnb(serialNumber, "M2");
                        if(IDataUtil.isNotEmpty(asynInfoB)){
                            SvcTradeData svcTD = new SvcTradeData();
                            svcTD.setUserId(userId);
                            svcTD.setUserIdA("-1");
                            svcTD.setProductId(btd.getMainTradeData().getProductId());
                            svcTD.setPackageId("-1");
                            svcTD.setElementId("20180109");
                            svcTD.setMainTag("0");
                            svcTD.setInstId(SeqMgr.getInstId());
                            svcTD.setStartDate(SysDateMgr.getSysTime());
                            svcTD.setEndDate(SysDateMgr.addDays(2)+SysDateMgr.END_DATE);
                            svcTD.setModifyTag("0");
                            svcTD.setRsrvStr5("FOLLOW_VOLTE");
                            svcTD.setRsrvStr6("SAME_PROV_MAIN");
                            svcTD.setRsrvStr7(imsi);
                            if( BofConst.MODIFY_TAG_ADD.equals(svcTrade.getModifyTag())){
                           	 svcTD.setRsrvStr3("09");
                           }else{
                           	svcTD.setRsrvStr3("10");
                           }
                            
                            btd.add(serialNumber, svcTD);
                        }
                        else
                        {
                            IData param = new DataMap();
                            param.put("SERIAL_NUMBER_B", serialNumber);
                            ViceRealInfoReRegBean bean = (ViceRealInfoReRegBean) BeanManager.createBean(ViceRealInfoReRegBean.class);
                            IDataset hdhInfo = bean.qryHdhSynInfo(param);
                            if(IDataUtil.isNotEmpty(hdhInfo))
                            {
                                SvcTradeData svcTD = new SvcTradeData();
                                svcTD.setUserId(userId);
                                svcTD.setUserIdA("-1");
                                svcTD.setProductId(btd.getMainTradeData().getProductId());
                                svcTD.setPackageId("-1");
                                svcTD.setElementId("20180109");
                                svcTD.setMainTag("0");
                                svcTD.setInstId(SeqMgr.getInstId());
                                svcTD.setStartDate(SysDateMgr.getSysTime());
                                svcTD.setEndDate(SysDateMgr.addDays(2)+SysDateMgr.END_DATE);
                                svcTD.setModifyTag("0");
                                svcTD.setRsrvStr5("FOLLOW_VOLTE");
                                svcTD.setRsrvStr6("TRANS_PROV_MAIN");
                                svcTD.setRsrvStr7(imsi);
                                if( BofConst.MODIFY_TAG_ADD.equals(svcTrade.getModifyTag())){
                               	 svcTD.setRsrvStr3("09");
                               }else{
                               	svcTD.setRsrvStr3("10");
                               }
                                
                                btd.add(serialNumber, svcTD);
                            }
                        }
                    }
                }
            }
        }
    }
}
