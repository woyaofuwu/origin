package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
/**
 * 2019移动20周年抢红包
 * @author tz
 * 如果是指定的活动完工，将TL_B_ACTIVE_PSPT_ID 表中的相应字段修改
 * SERIAL_NUMBER   
 * STATE      =1
 * ACTIVE_TIME     =sysdate
 * ACTIVE_TRADE_ID 
 */
public class PsptActiveFinishAction implements ITradeFinishAction
{
	private static transient Logger logger = Logger.getLogger(PsptActiveFinishAction.class);
    public void executeAction(IData mainTrade) throws Exception
    {
    	
    	logger.debug("进入PsptActiveFinishAction》》》》》》》》》》》》》》");
    	IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
    	if (IDataUtil.isEmpty(tradeSaleActive)){
            return;
        }
    	String serialNumber=mainTrade.getString("SERIAL_NUMBER");
        String productId=tradeSaleActive.first().getString("PRODUCT_ID");
        String packageId=tradeSaleActive.first().getString("PACKAGE_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset commParaInfo9117 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9117", productId,packageId);
        logger.debug("进入commParaInfo9117》》》》》》》》》》》》》》"+commParaInfo9117.toString());
        if(IDataUtil.isNotEmpty(commParaInfo9117)){
        	UcaData data = UcaDataFactory.getNormalUca(serialNumber);
        	String psptId = data.getCustomer().getPsptId();
        	SaleActiveBean bean  = BeanManager.createBean(SaleActiveBean.class);
        	//以|分割，前边是productId，后边是packageId
        	String productActual = commParaInfo9117.first().getString("PARA_CODE2");
        	String[] array = productActual.split("\\|");
        	IData result = bean.checkPsptValideForActive(psptId,array[0], array[1]);
        	if(IDataUtil.isNotEmpty(result)){
        		//更新台账
        		logger.debug("进入TL_B_ACTIVE_PSPT_ID更新");
        		IData inpara=new DataMap();
				inpara.put("SERIAL_NUMBER", serialNumber);//工单完工。
				inpara.put("STATE", "1");
				inpara.put("ACTIVE_TIME", SysDateMgr.getSysTime());
				inpara.put("ACTIVE_TRADE_ID", tradeId);//记录TRADE_ID。
				inpara.put("PSPT_ID", psptId);
				inpara.put("PRODUCT_ID", array[0]);
				inpara.put("PACKAGE_ID", array[1]);//记录TRADE_ID。
				Dao.executeUpdateByCodeCode("TL_B_ACTIVE_PSPT_ID", "UPD_FINISH_INFO", inpara);
        	}else{
        		String content = commParaInfo9117.first().getString("PARA_CODE3");
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,content);
        	}
        }
    	
       
    }
}