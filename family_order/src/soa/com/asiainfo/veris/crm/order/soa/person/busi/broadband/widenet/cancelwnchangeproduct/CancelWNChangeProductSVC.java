
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwnchangeproduct;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.cancelchangeproduct.CancelChangeProductUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.person.busi.canceltrade.CancelTradeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.GPRSDiscntChangeSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.UndoOfferRelBakDeal;

public class CancelWNChangeProductSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(GPRSDiscntChangeSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 取消产品变更
     * 
     * @return
     * @throws Exception
     */
    public IDataset cancelChangeProductTrade(IData pgData) throws Exception
    {
        IDataUtil.chkParam(pgData, "TRADE_ID");
        String tradeId = pgData.getString("TRADE_ID");
        IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
        if (IDataUtil.isEmpty(hisTradeInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_70, tradeId);// 获取台帐历史表资料:没有该笔业务!%s
        }
        
        if("JZF".equals(hisTradeInfos.getString("RSRV_STR10", "")))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333, "降资费包年活动不允许返销！");
        }

        /**************************** 数据准备 **********************/
        IData hisTrade = hisTradeInfos;// 历史台账信息
        IData pubData = this.getPublicData(tradeId, hisTrade);// 操作员相信息
        /************************ 规则校验 ********************************/

        
        //老系统的操作，生成offerRel备份信息
        UndoOfferRelBakDeal.dealOfferRelBak(hisTrade);
        
        /********************** 相关资料处理 **************************/

        CancelTradeBean commonBean = BeanManager.createBean(CancelTradeBean.class);
        String newOrderId = commonBean.createCancelOrder(hisTrade, pubData);// 生成新的返销订单信息
        IData newCancelTrade = commonBean.createCancelTrade(newOrderId, hisTrade, pgData, pubData);// 生成新的返销台账信息
        // commonBean.modifyHisTrade(pubData);// 修改原订单
        commonBean.modifyHisTradeAndTradeStaff(hisTrade, pubData);
        commonBean.createCancelStaffTrade(newOrderId, pubData);
        commonBean.cancelNotePrintLog(hisTrade, pubData);// 打印注销
        if (!StringUtils.equals(hisTrade.getString("EPARCHY_CODE"), CSBizBean.getTradeEparchyCode()))
        {
            commonBean.createCenterUndoTrade(newCancelTrade, pubData);// 生成省中心库返销订单--台账表中的地市编号和登陆员工的地市编号不一致
        }

        CancelChangeProductUtil cancelUtil = new CancelChangeProductUtil(hisTrade, false);
        
        /****************************begin REQ201512290003 优化依赖关系LIMIT_TAG为3类型预约取消限制问题@yanwu **********************/
        //cancelUtil.checkDiscntByElementLimit3();
        /****************************end REQ201512290003 优化依赖关系LIMIT_TAG为3类型预约取消限制问题@yanwu **********************/

        cancelUtil.doCancelChangeProduct();
        // 积分和费用的处理放在登记处理【避免完工时积分和费用不足 不能返销】 2013-12-3
        //TradeScore.tradeScore(newCancelTrade);// 积分返销--入参中的cancel_tag非常重要
        TradeCancelFee.cancelRecvFee(newCancelTrade, pgData);// 费用返销
        //沉淀原活动押金
        String oldDiscntTag = pgData.getString("OLD_DISCNT_TAG","");
        if(oldDiscntTag!=null && "1".equals(oldDiscntTag)){
            //3、获取默认账户  （acct_id)   
            String serialNum = pgData.getString("SERIAL_NUMBER","");
            serialNum=serialNum.replace("KD_", "");
            IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNum);
            String acctId=accts.getData(0).getString("ACCT_ID"); 
            String fee=callAcctGetFeeInfo(acctId);
            if(!"0".equals(fee)){
                callAcctBackFee(acctId,fee);
            }
        }
        String oldSaleactivetradeId=pgData.getString("OLD_SALEACTIVE_TRADEID","");
        String saleactivetradeId=pgData.getString("SALEACTIVE_TRADEID","");
        
      //营销活动返销 
        if(!"".equals(saleactivetradeId))
        {
            IData saleActiveTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(saleactivetradeId, "0", null);
            
            //老系统的操作，生成offerRel备份信息
            UndoOfferRelBakDeal.dealOfferRelBak(saleActiveTradeInfos);
            
            IData pdData = new DataMap();
            pdData.put("REMARKS", "宽带预约取消营销活动返销");
            pdData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
            pdData.put("TRADE_ID", saleactivetradeId);
            pdData.put("IS_CHECKRULE", false);
            pdData.put(Route.ROUTE_EPARCHY_CODE,hisTradeInfos.getString("TRADE_EPARCHY_CODE"));
            CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
        } 
        //237营销活动返销 
        if(!"".equals(oldSaleactivetradeId))
        {
            //1、处理营销活动终止日期更新回来
            //2、处理优惠活动插入中间表
            CancelWNChangeProductBean.doActiveEndDateAndInsTiDiscnt(oldSaleactivetradeId); 
            
            IData oldSaleActiveTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(oldSaleactivetradeId, "0", null);
            
            //老系统的操作，生成offerRel备份信息
            UndoOfferRelBakDeal.dealOfferRelBak(oldSaleActiveTradeInfos);
            
            
            IData pdData = new DataMap();
            pdData.put("REMARKS", "营销活动取消返销");
            pdData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
            pdData.put("TRADE_ID", oldSaleactivetradeId);
            pdData.put("IS_CHECKRULE", false);
            pdData.put(Route.ROUTE_EPARCHY_CODE,hisTradeInfos.getString("TRADE_EPARCHY_CODE"));
            CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
        } 
        
        //BUS202002180007关于优化FTTH迁移活动的需求
        //取消到期活动处理
        cancelExpireActiveDeal(pgData);
        
        IDataset results = new DatasetList();
        pgData.put("RESULT_CODE", "0");
        pgData.put("RESULT_INFO", "信息处理成功!");
        results.add(pgData);
        return results;
    }
    
    
    /**
     * BUS202002180007关于优化FTTH迁移活动的需求
     * 取消到期活动处理
     */
    private void cancelExpireActiveDeal(IData pgData) throws Exception{
    	String serialNumer = pgData.getString("SERIAL_NUMBER","");
    	serialNumer = serialNumer.replace("KD_", "");
    	
    	IData inparams = new DataMap();
		 inparams.put("SERIAL_NUMBER", serialNumer);
		 inparams.put("DEAL_STATE", "0");
		 inparams.put("DEAL_TYPE","WidenetEndActive");
		 IDataset expireDeals=BofQuery.queryExpireDealBySerialNumber(inparams);
		 
		 
		 boolean isCancel =false;
		 if(IDataUtil.isNotEmpty(expireDeals)){
			 for (Iterator eit = expireDeals.iterator(); eit.hasNext();){
				 IData expireDeal = (IData) eit.next();
				 Dao.delete("TF_F_EXPIRE_DEAL", expireDeal);
				 isCancel =true;
			 }
			 
			 if (isCancel) {
				 for (int j = 0; j < expireDeals.size(); j++) {
					 
					 String userID = expireDeals.getData(j).getString("USER_ID");
					 String serialNumber = expireDeals.getData(j).getString("SERIAL_NUMBER");
					 String tradeId = expireDeals.getData(j).getString("TRADE_ID");
					 String execTime = expireDeals.getData(j).getString("EXEC_TIME");
					 String execMonth = expireDeals.getData(j).getString("EXEC_MONTH");
					 String eparchyCode = expireDeals.getData(j).getString("EPARCHY_CODE");
					 
					 
					 IData param = new DataMap();
					 IData dealCond1 = new DataMap();
					 dealCond1.put("DISCNT_CODE", expireDeals.getData(j).getString("DEAL_COND"));
					 param.put("DEAL_ID", SeqMgr.getTradeId());
					 param.put("USER_ID", userID);
					 param.put("PARTITION_ID", userID.substring(userID.length() - 4));
					 param.put("SERIAL_NUMBER", serialNumber);
					 param.put("EPARCHY_CODE", eparchyCode);
					 param.put("IN_TIME", SysDateMgr.getSysTime());
					 param.put("DEAL_STATE", "2");
					 param.put("DEAL_TYPE", "WidenetEndActive");
					 param.put("EXEC_TIME", execTime);
					 param.put("DEAL_COND", dealCond1.toString());
					 param.put("EXEC_MONTH", execMonth);
					 param.put("DEAL_RESULT", "预约宽带产品取消，无须处理！");
					 param.put("TRADE_ID", tradeId);

					 Dao.insert("TF_FH_EXPIRE_DEAL", param);
				 }
				 
			}
			 
		 } 
    	
    }
    
    /**
     * REQ201611070013 宽带产品变更取消功能优化
     * 取账户9023余额
     * */
    public String callAcctGetFeeInfo(String acctId) throws Exception {
        
        IData param = new DataMap();
        param.put("ACCT_ID", acctId); 
        //调用账务查询接口 
        IDataset checkCash= AcctCall.queryAcctDeposit(param); 
        
        String allCash="0";
        if(checkCash!=null && checkCash.size()>0){
            for(int j=0;j<checkCash.size();j++){
                IData acctInfo=checkCash.getData(j);
                String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
                String DEPOSIT_BALANCE=acctInfo.getString("DEPOSIT_BALANCE");//存折余额 
                if("9023".equals(DEPOSIT_CODE)){ 
                    allCash=""+(Integer.parseInt(allCash)+Integer.parseInt(DEPOSIT_BALANCE));
                } 
            } 
        }
        return allCash;
    }
    
    /**
     * REQ201611070013 宽带产品变更取消功能优化
     * 沉淀账户9023余额
     * */
    public static void callAcctBackFee(String acctId,String tradeFee) throws Exception {
         
        IData params=new DataMap(); 
        params.put("ACCT_ID", acctId);
        params.put("CHANNEL_ID", "15000");
        params.put("PAYMENT_ID", "100021");
        params.put("PAY_FEE_MODE_CODE", "0");
        params.put("FORCE_TAG", "1");//強制清退
        params.put("REMARK", "宽带老包年套餐赠送金额沉淀！");
        IData depositeInfo=new DataMap();
        depositeInfo.put("DEPOSIT_CODE", "9023");
        depositeInfo.put("TRANS_FEE", tradeFee);//沉淀金额
        
        IDataset depositeInfos=new DatasetList();
        depositeInfos.add(depositeInfo);
        params.put("DEPOSIT_INFOS", depositeInfos);
        CSBizBean.getVisit().setStaffEparchyCode("0898");
        //调用接口，沉淀金额
        IData inAcct =AcctCall.foregiftDeposite(params);
        String callRtnType=inAcct.getString("X_RESULTCODE","");
        if(!"".equals(callRtnType)&&"0".equals(callRtnType)){ 
        }else{  
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口沉淀宽带老包年套餐赠送金额失败:" + inAcct.getString("X_RESULTINFO",""));
        }   
    }

    /**
     * 获取一些公共信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private IData getPublicData(String tradeId, IData hisTrade) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "2");// 3=被取消 先写成2
        pubData.put("TRADE_ID", tradeId);
        pubData.put("USER_ID", hisTrade.getString("USER_ID"));
        pubData.put("USER_EPARCHY_CODE", hisTrade.getString("EPARCHY_CODE"));
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }

    public IDataset getUserWidenetInfo(IData input) throws Exception
    {
        CancelWNChangeProductBean bean = BeanManager.createBean(CancelWNChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        return bean.getUserWidenetInfo(serialNumber);
    }

    /**
     * 查询预约的产品变更工单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryChangeProductTrade(IData input) throws Exception
    {
        CancelWNChangeProductBean bean = BeanManager.createBean(CancelWNChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        return bean.queryChangeProductTrade(serialNumber, tradeTypeCode);
    }

    /**
     * 查询是否存在未处理的错单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryErrorInfoTrade(IData input) throws Exception
    {
        CancelWNChangeProductBean bean = BeanManager.createBean(CancelWNChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        return bean.queryErrorInfoTrade(serialNumber);
    }

}
