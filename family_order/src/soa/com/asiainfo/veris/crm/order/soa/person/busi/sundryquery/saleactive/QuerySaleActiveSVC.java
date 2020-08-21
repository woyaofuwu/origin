
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.saleactive;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImeiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;

public class QuerySaleActiveSVC extends CSBizService
{
	private static transient Logger logger = Logger.getLogger(QuerySaleActiveSVC.class);
    public IData queryActiveDetail(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String packageId = input.getString("PACKAGE_ID");
        String relationTradeId = input.getString("RELATION_TRADE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String instId = input.getString("INST_ID");
        IData data = new DataMap();

        // "客户资料综合查询"已销户号码查询营销活动详情调用以下方法会报错找不到用户资料，故此处过滤。已销户用户：NORMAL_USER_CHECK=off
        if (!"off".equals(input.getString("NORMAL_USER_CHECK", "")))
            UcaDataFactory.getNormalUca(serialNumber);

        IDataset discnts = UserDiscntInfoQry.getSaleActiveDiscntsByUIdPkId(userId, packageId, instId);
        data.put("DISCNTS", discnts);

        IDataset goods = UserSaleGoodsInfoQry.getByRelationTradeId(relationTradeId);
        for (int i = 0, size = goods.size(); i < size; i++)
        {
            IData good = goods.getData(i);
            String rsrvTag1 = good.getString("RSRV_TAG1");
            if ("1".equals(rsrvTag1))
            {
                good.put("RSRV_TAG1", "赠送");
            }
            else
            {
                good.put("RSRV_TAG1", "");
            }
            //REQ201807300022关于移动商城4G飞享合约资费套餐同步扩容的需求 wuhao5 190329
            IDataset activeInfos = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
            IDataset ctrmProducts = QueryListInfo.queryProuductIdByType("2");
            if(IDataUtil.isNotEmpty(activeInfos) && activeInfos.size() > 0 && IDataUtil.isNotEmpty(ctrmProducts) && ctrmProducts.size() > 0)
            {
            	for(int k = 0;k < activeInfos.size();k ++)
            	{
            		IData activeInfo = activeInfos.getData(k);
            		String activeProduct = activeInfo.getString("PRODUCT_ID","");            		
            		for(int j = 0;j < ctrmProducts.size();j ++)
            		{
            			IData ctrmProduct = ctrmProducts.getData(j);
            			String ctrmProductId = ctrmProduct.getString("PRODUCT_ID");
            			if(activeProduct.equals(ctrmProductId))
            			{
            	        	IDataset imeis =  UserImeiInfoQry.getUserImeiInfoByUserId(userId);
            	        	if(IDataUtil.isNotEmpty(imeis) && imeis.size() > 0){
            	        		IData imei = imeis.getData(0);
                				good.put("RES_CODE",imei.get("IMEI"));
            	        	}        	        	
            			}
            		}        		
            	}     	       	
            }            
        }
        data.put("GOODS", goods);

        // 调账务接口
        IDataset deposits = new DatasetList();
        IDataset results = AcctCall.queryUserDiscntAction("2", serialNumber, userId);
        if (IDataUtil.isNotEmpty(results))
        {
            IDataset depositList = results.getData(0).getDataset("DISCNT_LIST");
            if (IDataUtil.isNotEmpty(depositList))
            {	logger.error("==账务接口返回返还信息=="+depositList.toString());
                for (int i = 0, size = depositList.size(); i < size; i++)
                {
                    IData deposit = depositList.getData(i);
                    if (relationTradeId.equals(deposit.getString("OUTER_TRADE_ID")))
                    {
                        deposit.put("PACKAGE_ID", packageId);
                        deposit.put("SERIAL_NUMBER", serialNumber);
                        // deposit.put("LEFT_MONTHS", deposit.getInt("MONTHS") - deposit.getInt("TIMES"));

                        String startDate = deposit.getString("START_DATE", deposit.getString("START_CYCLE_ID"));
                        startDate = startDate.substring(0, 4) + "-" + startDate.substring(4, 6) + "-" + startDate.substring(6);

                        String endDate = SysDateMgr.getAddMonthsLastDay(deposit.getInt("MONTHS"), startDate);
                        deposit.put("START_CYCLE_ID", startDate);
                        deposit.put("END_CYCLE_ID", endDate);
                        
                        //添加返回结果及机卡绑定IMEI信息
                        //是否机卡绑定标识
                        String lastSMSId=deposit.getString("LAST_SMS_ID");
                        if("1".equals(lastSMSId)){
                        	 deposit.put("LAST_SMS_FLAG", "是");
                        }else{
                        	 deposit.put("LAST_SMS_FLAG", "否");
                        }
                        //返还结果
                        String resultInfo=deposit.getString("RESULT_INFO");
                        if(StringUtils.isEmpty(resultInfo)){
                        	deposit.put("RESULT_INFO", "");
                        }
                        //LAST_SMS_ID = 1并且RESULT_INFO返还中包含“IMEI”或“imei”
                        String imeiInfo="";
                        if("1".equals(lastSMSId)&&deposit.getString("RESULT_INFO").toUpperCase().indexOf("IMEI")>=0){
                        	IDataset imeiList=deposit.getDataset("IMEI_LIST");
                        	if (IDataUtil.isNotEmpty(imeiList)){
                        		 for(int j=0;j<imeiList.size();j++){
                        			 imeiInfo+=imeiList.getData(j).getString("IMEI");
                        			 if(j!=imeiList.size()-1){
                        				 imeiInfo+=",";
                        			 }
                        		 }
                        	}
                        }
                        deposit.put("IMEI_INFO",imeiInfo);
                        
                        deposits.add(deposit);
                    }
                }
            }
        }
        data.put("DEPOSITS", deposits);

        return data;
    }

    public IDataset querySaleActive(IData data) throws Exception
    {
        String qryType = data.getString("QRY_TYPE");
        String sn = data.getString("SERIAL_NUMBER");
        String allFlag = data.getString("ALL_FLAG");

        IDataset rtSaleActiveList = new DatasetList();
        // "客户资料综合查询"已销户号码查询非签约类营销活动会抛错，故此处过滤。已销户用户：NORMAL_USER_CHECK=off
        String userId;
        if ("off".equals(data.getString("NORMAL_USER_CHECK", ""))) {
            userId = data.getString("USER_ID");
        } else {
            IData user = UcaInfoQry.qryUserInfoBySn(sn);
            if (IDataUtil.isEmpty(user))
                CSAppException.apperr(CrmUserException.CRM_USER_1);
            userId = user.getString("USER_ID");
        }

        if ("true".equals(allFlag))
        {
            if ("1".equals(qryType))
            {
                // 营销活动综合查询
                rtSaleActiveList = UserSaleActiveInfoQry.queryAllQySaleActiveWithGoods(userId, getPagination());
            }
            else if ("2".equals(qryType))
            {
                // 非签约类营销活动综合查询
                rtSaleActiveList = UserSaleActiveInfoQry.queryAllNoQySaleActive(userId, getPagination());
            }
        }
        else
        {
            if ("1".equals(qryType))
            {
                // 营销活动综合查询
                rtSaleActiveList = UserSaleActiveInfoQry.queryValidQySaleActiveWithGoods(userId, getPagination());
            }
            else if ("2".equals(qryType))
            {
                // 非签约类营销活动综合查询
//                IData offerInfo = UpcCall.queryOfferByOfferId("K", "60008768");
                rtSaleActiveList = UserSaleActiveInfoQry.queryValidNoQySaleActive(userId, getPagination());
                
            }
        }

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", CSBizBean.getUserEparchyCode());
        for (int i = 0, size = rtSaleActiveList.size(); i < size; i++)
        {
            IData saleactive = rtSaleActiveList.getData(i);
            saleactive.put("ADVANCE_PAY", FeeUtils.Fen2Yuan(saleactive.getString("ADVANCE_PAY")));
            saleactive.put("OPER_FEE", FeeUtils.Fen2Yuan(saleactive.getString("OPER_FEE")));
            saleactive.put("FOREGIFT", FeeUtils.Fen2Yuan(saleactive.getString("FOREGIFT")));

            String productId = saleactive.getString("PRODUCT_ID");
            String packageId = saleactive.getString("PACKAGE_ID");
            IDataset offer = UpcCall.qryNeglectDateOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
            if(IDataUtil.isNotEmpty(offer))
            {
                String offerDesc = offer.getData(0).getString("DESCRIPTION");
                saleactive.put("PACKAGE_DESC", offerDesc);
                if("1".equals(qryType))
                {
                    saleactive.put("REMARK", offerDesc);
                }
            }
            
            boolean noBack = SaleActiveUtil.isInCommparaConfigs(productId, packageId, noBackConfigs);
            if (noBack)
            {
                saleactive.put("ACTIVE_TYPE", "约定在网类");
            }
            else
            {
                saleactive.put("ACTIVE_TYPE", "返还/约定消费类");
            }
            String relationTradeId = saleactive.getString("RELATION_TRADE_ID");
            IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(relationTradeId, "CREDIT_PURCHASES");
    		if(DataUtils.isNotEmpty(otherDatas)){
    			 saleactive.put("IS_CREDIT_PURCHASES", "1");
    		}else{
    			saleactive.put("IS_CREDIT_PURCHASES", "0");
    		}
            
            
        }
        
        //REQ201501040001 关于优化营销活动资料查询规则及营销活动办理时提示信息 by songlm 20150116
        IDataset is566Configs = new DatasetList();
        for (int i = rtSaleActiveList.size() - 1; i >= 0; i--)
        {
        	is566Configs.clear();
            IData saleactive = rtSaleActiveList.getData(i);
            String productId = saleactive.getString("PRODUCT_ID");
            String startDate = saleactive.getString("START_DATE");
            is566Configs = CommparaInfoQry.getCommparaCode1("CSM", "566", productId, CSBizBean.getUserEparchyCode());
            if(IDataUtil.isNotEmpty(is566Configs))//commpara表的566配置中存在该产品
            {
            	String sysDate = SysDateMgr.getSysTime();
            	int intervalMonths = Integer.parseInt(is566Configs.first().getString("PARA_CODE1"));//偏移月份
            	String newStartDate = SysDateMgr.getAddMonthsNowday(intervalMonths, startDate);//活动偏移后的开始时间
                if (sysDate.compareTo(newStartDate) > 0)
                {
                	rtSaleActiveList.remove(i);
                }
            }
        }
        //end
        
        //如果营销活动综合查询，还需要增加用户的星级
        if ("1".equals(qryType))
        {
        	String strCreditClass=UcaDataFactory.getUcaByUserId(userId).getUserCreditClass();
        	
        	UcaData uca=UcaDataFactory.getUcaByUserId(userId);
        	
        	//添加用户的星级
        	if(IDataUtil.isNotEmpty(rtSaleActiveList)){
        		for(int i=0,size=rtSaleActiveList.size();i<size;i++){
        			IData rtSaleActive=rtSaleActiveList.getData(i);
        			
        			if(strCreditClass==null||strCreditClass.equals("-1")||strCreditClass.equals("-0")){
        				rtSaleActive.put("CREDIT_CLASS", "无星级");
        			}else{
        				rtSaleActive.put("CREDIT_CLASS", strCreditClass);
        			}
        			
        			
        			String relationTradeId=rtSaleActive.getString("RELATION_TRADE_ID", "");
        			if(!relationTradeId.equals("")){
        				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);
        				
        				if (StringUtils.isNotBlank(actionCode)){
        					IData feeInfo=AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);
            				
            				if(IDataUtil.isNotEmpty(feeInfo)){
            					
            					String resultCode=feeInfo.getString("RESULT_CODE","");
            					if(StringUtils.isNotBlank(resultCode)&&resultCode.equals("0")){
            						IDataset allFeeInfos=feeInfo.getDataset("FEE_INFOS");
            						
            						double sumGiftMoney=0;
            						double sumRestGiftMoney=0;
            						
            						double sumDepositeMoeny=0;
            						double sumRestDepositeMoeny=0;
            						
            						for(int j=0,sizej=allFeeInfos.size();j<sizej;j++){
            							IData fee=allFeeInfos.getData(j);
            							String actionAttr=fee.getString("ACTION_ATTR");
            							double allMoney=fee.getDouble("ALL_MONEY",0)/100;
            							double leftMoney=fee.getDouble("LEFT_MONEY",0)/100;
            							
            							//预存返还
            							if(actionAttr.equals("0")){
            								sumDepositeMoeny=sumDepositeMoeny+allMoney;
            								sumRestDepositeMoeny=sumRestDepositeMoeny+leftMoney;
            							}
            							//赠送返还
            							else if(actionAttr.equals("1")){			
            								sumGiftMoney=sumGiftMoney+allMoney;
            								sumRestGiftMoney=sumRestGiftMoney+leftMoney;
            							}
            						}
            						
            						rtSaleActive.put("ALL_DEPOSIT", sumDepositeMoeny);
    								rtSaleActive.put("REST_DEPOSIT", sumRestDepositeMoeny);
    								
    								rtSaleActive.put("ALL_GIFT_FEE", sumGiftMoney);
    								rtSaleActive.put("REST_GIFT_FEE", sumRestGiftMoney);

            					}
            				}
        				}
        				
        			}
        		}
        	}
        }
        

        return rtSaleActiveList;
    }
}
