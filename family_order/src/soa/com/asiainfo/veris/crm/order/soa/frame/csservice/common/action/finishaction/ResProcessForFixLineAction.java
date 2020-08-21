
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

public class ResProcessForFixLineAction implements ITradeFinishAction
{
    /**
     * IMS固话号码占用及释放
     * 
     * @author liuzz
     * @return
     * @throws Exception
     */
    public void executeAction(IData mainTrade) throws Exception
    {

        // 资源占用
        String strTradeId = mainTrade.getString("TRADE_ID");
        
        // 获取业务台帐资源子表
        IDataset tradeResInfos = TradeResInfoQry.queryAllTradeResByTradeId(strTradeId);
        IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(strTradeId);
        IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(strTradeId,"RESID");
        
        if (IDataUtil.isEmpty(tradeResInfos) || IDataUtil.isEmpty(tradeProductInfos))
        {
            return;
        }
        String productId = tradeProductInfos.getData(0).getString("PRODUCT_ID");
        String brandCode = tradeProductInfos.getData(0).getString("BRAND_CODE");
        
        for (int i = 0; i < tradeResInfos.size(); i++)
        {
            String strResCode = tradeResInfos.getData(i).getString("RES_CODE");
            String modifyTag = tradeResInfos.getData(i).getString("MODIFY_TAG");
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {// 资源新增实占
                ResCall.resPossessForMphone("", "", strResCode, productId, strTradeId, brandCode, "0");
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {// 资源删除占用销户
                ResCall.destroyRealeaseMphone(strResCode, "2");
            }

        }
        //终端实占
//        if(IDataUtil.isNotEmpty(tradeOtherInfos) && tradeOtherInfos.size()>0){
//        	String modifyTag = tradeOtherInfos.getData(0).getString("MODIFY_TAG");
//        	String strResCode = tradeOtherInfos.getData(0).getString("RSRV_VALUE");
//        	String activesaleId = tradeOtherInfos.getData(0).getString("RSRV_STR10","");//营销活动ID
//        	if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && activesaleId.equals("84070042"))
//            {
//        		mainTrade.put("RSRV_STR1", "0");//0:购买
//        		mainTrade.put("RSRV_STR7", "0");//费用
//        		mainTrade.put("RSRV_STR6", strResCode);//终端串号
//        		IDataset returnResult = HwTerminalCall.saleOrChange4SetTopBox(mainTrade);
//        		if(IDataUtil.isEmpty(returnResult)){
//    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
//    			}else{
//    				String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
//    				if(!resultCode.equals("0")){
//    					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
//    							getData(0).getString("X_RESULTINFO",""));
//    				}
//    			}
//            }
//        }
    }
}
