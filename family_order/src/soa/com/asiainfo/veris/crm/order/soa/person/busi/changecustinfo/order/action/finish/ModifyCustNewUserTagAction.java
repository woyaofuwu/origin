
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.queryuser.queryNewCardIdUserBean;

/**
 * QR-20190620-08	O2O激活接口调用超时
 * @author mengqx 20190722
 */
public class ModifyCustNewUserTagAction implements ITradeFinishAction
{
	static Logger logger = Logger.getLogger(ModifyCustNewUserTagAction.class); 
	
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
        IDataset tradeOtherInfo = TradeOtherInfoQry.getTradeOtherByTradeId(mainTrade.getString("TRADE_ID"));

        logger.error("=============ModifyCustNewUserTagAction=============tradeOtherInfo="+tradeOtherInfo);
        
		if(tradeOtherInfo!=null&&tradeOtherInfo.size()>0){
			for (int i = 0; i < tradeOtherInfo.size(); i++) {
				IData tradeOther = tradeOtherInfo.getData(i);
		        logger.error("=============ModifyCustNewUserTagAction=============tradeOther="+tradeOther);

				if(tradeOther!=null&&tradeOther.size()>0){
					if("CHRN".equals(tradeOther.getString("RSRV_VALUE_CODE",""))&&"实名制办理".equals(tradeOther.getString("RSRV_VALUE",""))){
						//办理实名制
						//REQ201904240014 关于新开户用户新增“纯新增”标识的需求
						
						IDataset tradeCustomerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(mainTrade.getString("TRADE_ID",""));
						logger.error("=============ModifyCustNewUserTagAction=============tradeCustomerDs="+tradeCustomerDs);
				        if(tradeCustomerDs!=null&&tradeCustomerDs.size()>0){
				            IData customerData = tradeCustomerDs.getData(0);
				            String psptTypeCode = customerData.getString("PSPT_TYPE_CODE", "");
				            String psptId = customerData.getString("PSPT_ID", "");

				            if("0".equals(psptTypeCode)||"1".equals(psptTypeCode) ){//只有身份证才执行
				            	//REQ201904240014 关于新开户用户新增“纯新增”标识的需求
				            	IData redata = new DataMap();
				            	redata.put("CARD_ID_NUM",psptId);
				            	queryNewCardIdUserBean bean = BeanManager.createBean(queryNewCardIdUserBean.class);
				            	logger.error("=============ModifyCustNewUserTagAction=============redata="+redata);
				            	IDataset queryInfos = bean.Query(redata);
				            	System.out.println("纯新增标识"+queryInfos);
				            	if (IDataUtil.isEmpty(queryInfos)){
				            		IData otherTD = new DataMap();
				            		String userId = mainTrade.getString("USER_ID");
				            		otherTD.put("USER_ID", userId);
				            		otherTD.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
				            		otherTD.put("RSRV_VALUE_CODE", "NEW_USER_TAG");
				            		otherTD.put("RSRV_VALUE", mainTrade.getString("SERIAL_NUMBER"));
				            		otherTD.put("RSRV_STR1", CSBizBean.getVisit().getStaffId());
				            		otherTD.put("RSRV_STR2", SysDateMgr.getSysTime());
				            		otherTD.put("RSRV_STR5", CSBizBean.getVisit().getStaffName());
				            		otherTD.put("START_DATE", SysDateMgr.getSysTime());
				            		otherTD.put("END_DATE", SysDateMgr.getTheLastTime());
				            		otherTD.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
				            		otherTD.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
				            		otherTD.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
				            		String tradeId = mainTrade.getString("TRADE_ID");
				            		otherTD.put("TRADE_ID", tradeId);
				            		otherTD.put("UPDATE_TIME", SysDateMgr.getSysTime());
				            		otherTD.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				            		otherTD.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				            		otherTD.put("INST_ID", SeqMgr.getInstId());
				            		Dao.insert("TF_F_USER_OTHER", otherTD);
				            	}
				            }
				        }
						
					}
				}
			}
			
		}
		
		
	}
}
