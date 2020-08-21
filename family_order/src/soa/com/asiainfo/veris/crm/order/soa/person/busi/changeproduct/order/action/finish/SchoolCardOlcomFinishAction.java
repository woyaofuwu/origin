
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

/**
 * Copyright: Copyright 2015 Asiainfo
 * 
 * @ClassName: SchoolCardOlcomFinishAction.java
 * @Description: 产品变更校园卡指令处理完工后920服务开始时间处理
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2015-3-24 15:15 PM Modification History: Date Author Version Description
 */
public class SchoolCardOlcomFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String execTime = mainTrade.getString("ACCEPT_DATE");

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber); //获取三户资料方法
        List<SvcTradeData> svcData = uca.getUserSvcBySvcId("920");
       
        if (svcData != null && svcData.size() > 0) {
        	for(SvcTradeData svc : svcData){
        		//当920存在的时候
                if("10001005".equals(svc.getProductId()) 
                || "10001139".equals(svc.getProductId()) 
                && BofConst.MODIFY_TAG_USER.equals(svc.getModifyTag())) {
                	
                    IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId); //uca.getUserDiscnts();
                    if( IDataUtil.isNotEmpty(discntTrades) ) {
                        boolean delTag = false;
                        boolean addTag = false;
                        for (int i = 0; i < discntTrades.size(); i++) {
                        	
                        	IData discnt = discntTrades.getData(i);
                        	DiscntTradeData discntTrade = new DiscntTradeData(discnt);
                        	
                            String elementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(discntTrade.getDiscntCode());
                            
                            if("R".equals(elementType) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                                addTag = true;
                            }
                            if("R".equals(elementType) && BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag())) {
                                delTag = true;
                            }
                            
                        }
                        //有变更必选优惠
                        if(delTag && addTag){
                            IData data = svc.toData();
                            data.put("INST_ID", svc.getInstId());
                            data.put("USER_ID", userId);
                            data.put("PARTITION_ID", userId.substring(userId.length() - 4));
                            data.put("START_DATE", execTime);
                            
                            //Dao.update("TF_F_USER_SVC", param);
                            Dao.update("TF_F_USER_SVC", data, new String[]
                            { "INST_ID", "PARTITION_ID" });
                        }
                    }
                }
            }
        }
    }
}
