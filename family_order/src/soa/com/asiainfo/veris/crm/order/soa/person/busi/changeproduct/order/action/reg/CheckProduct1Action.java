package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;



/**
188元、288元为优惠活动价格，只有新开户用户可办理，存量用户不能选择。
 */
public class CheckProduct1Action implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();

        String discntName1 = UDiscntInfoQry.getDiscntNameByDiscntCode("20170454");//StaticUtil.getStaticValue(null, "TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", "20170454");

        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
        if (discntTrades != null && discntTrades.size() > 0) {
            for (DiscntTradeData discntTrade : discntTrades) {
                if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {//市场套卡188元套餐    ， 市场套卡288元套餐           
                    //String productId = discntTrade.getProductId();
                    String discntCode = discntTrade.getDiscntCode();

                    if (discntCode != null) {
                    	
                    	IDataset discntCodeeles = CommparaInfoQry.getCommparaAllColByParser("CSM", "9229", discntCode, "0898");
                       
                        if (IDataUtil.isNotEmpty(discntCodeeles)) {// 20170454 15元/GB的流量加油包套餐
                            //产品必须是 市场套卡188元套餐   ， 市场套卡288元套餐  ，  市场套卡238元套餐才可订购
                            //IDataset productds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1086", productId, "1");//是否是任我用产品
                            //String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);//StaticUtil.getStaticValue(null, "TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", discntCode);
                        	String discntName = discntCodeeles.getData(0).getString("PARAM_NAME","");
                        	//业务规则：先判断是否已限速，只有已限速才能办理该流量优惠包，订购完后，需要进行恢复网速操作。
                            IData inParamNew = new DataMap();
                            inParamNew.put("USER_ID", uca.getUserId());
                            inParamNew.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                            inParamNew.put("IN_DATE", SysDateMgr.getFirstDayOfThisMonth());
                            IDataset result = PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
                            if (!result.isEmpty()) {
                                String usrStatus = result.getData(0).getString("USR_STATUS", "");
                                String execState = result.getData(0).getString("EXEC_STATE", "");
                                if (("2".equals(usrStatus) || "3".equals(usrStatus) || "4".equals(usrStatus) || "6".equals(usrStatus) || "8".equals(usrStatus)) && "2".equals(execState)) {//等于2并且执行状态=2，才是限速成功。
                                }else{
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户不是限速状态,不能订购["+discntCode+"]" + discntName + "！");
                                }
                            } else {                           	
                            	result = PCCBusinessQry.qryPccHOperationTypeForSubscriber(inParamNew);
                            	if (!result.isEmpty()) {
                                    String usrStatus = result.getData(0).getString("USR_STATUS", "");
                                    String execState = result.getData(0).getString("EXEC_STATE", "");
                                    if (("2".equals(usrStatus) || "3".equals(usrStatus) || "4".equals(usrStatus) || "6".equals(usrStatus) || "8".equals(usrStatus))  && "2".equals(execState)) {//等于2并且执行状态=2，才是限速成功。
                                    }else{
                                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户不是限速状态,不能订购["+discntCode+"]"  + discntName + "！");
                                    }
                                }else
                                {
                                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户不是限速状态,不能订购["+discntCode+"]"  + discntName + "！");
                                }
                                
                            }

                        }
                    }
                }
            }
        }

    }
}
