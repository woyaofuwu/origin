
package com.asiainfo.veris.crm.order.soa.person.busi.cancelchangeproduct;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass.GiveUserClassBean;

public class CancelChangeProductBean extends CSBizBean
{
    /**
     * 取消前规则校验
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void chkTradeBeforeCancel(IData input, IData hisTrade) throws Exception
    {
        /************* 加上是否能返销老系统工单的限制 ****************************/
        String newNgBossTime = StaticUtil.getStaticValue("NEW_NGBOSS_TIME", "TIME");// 新系统上线时间点
        if (StringUtils.isNotEmpty(newNgBossTime))
        {
            String execTime = hisTrade.getString("ACCEPT_DATE");
            if (SysDateMgr.getTimeDiff(execTime, newNgBossTime, SysDateMgr.PATTERN_STAND) > 0)// 工单是在割接之前则报错
            {
                String userId = hisTrade.getString("USER_ID");
                String trade_id = hisTrade.getString("TRADE_ID");
                String partitionId = String.valueOf(Long.valueOf(StringUtils.substring(userId, userId.length() - 4, userId.length())));
                IDataset ccInfos = UserOtherInfoQry.getUserOtherInfo(userId, "UNDO", partitionId, trade_id);// 查询老系统产生的该工单是否允许（解决投诉使用）
                if (IDataUtil.isEmpty(ccInfos))
                {
                    CSAppException.apperr(TradeException.CRM_TRADE_95, "该工单是在老系统受理的，新系统无法返销老系统产生的工单！");
                }
            }
        }

        IData inRuleParam = new DataMap();
        inRuleParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("USER_EPARCHY_CODE"));
        inRuleParam.put("ID", input.getString("USER_ID"));
        inRuleParam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        inRuleParam.put("TRADE_EPARCHY_CODE", input.getString("LOGIN_EPARCHY_CODE"));
        inRuleParam.put("TRADE_STAFF_ID", input.getString("STAFF_ID"));
        inRuleParam.put("TRADE_CITY_CODE", input.getString("CITY_CODE"));
        inRuleParam.put("TRADE_DEPART_ID", input.getString("DEPART_ID"));
        inRuleParam.put("TRADE_ID", input.getString("TRADE_ID"));
        inRuleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckBeforeCancel");
        inRuleParam.put("RULE_BIZ_KIND_CODE", "TradeCheckBeforeCancel");
        inRuleParam.put("ACTION_TYPE", "TradeCheckBeforeCancel");
        inRuleParam.put("TRADE_TYPE_CODE", hisTrade.getString("TRADE_TYPE_CODE"));// 当前被取消的业务类型编码
        inRuleParam.put("BRAND_CODE", hisTrade.getString("BRAND_CODE"));
        inRuleParam.put("PRODUCT_ID", hisTrade.getString("PRODUCT_ID"));
        inRuleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inRuleParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inRuleParam.put("TRADE_INFO", hisTrade);// 将历史trade信息传入
        IData data = BizRule.bre4SuperLimit(inRuleParam);
        CSAppException.breerr(data);
    }
    /**
     * 查询无线固话预约产品变更的工单
     *
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryChangeProductTradeByTDFixed(String serialNumber) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (null != userInfo)
        {
            String userId = userInfo.getString("USER_ID");

            IDataset mainProductInfos = UserProductInfoQry.queryUserMainProduct(userId);
            if (IDataUtil.isNotEmpty(mainProductInfos) && mainProductInfos.size() > 1)
            {
                String sysTime = SysDateMgr.getSysTime();
                for (int i = 0; i < mainProductInfos.size(); i++)
                {
                    IData tempData = mainProductInfos.getData(i);
                    // 判断是否下周期生效的产品
                    if (SysDateMgr.getTimeDiff(sysTime, tempData.getString("START_DATE"), SysDateMgr.PATTERN_STAND) > 0)
                    {
                        String instId = tempData.getString("INST_ID");
                        IDataset ds = TradeHistoryInfoQry.queryChgProductTrade(userId, "3803", instId);

                        return ds;
                    }
                }
            }
        }
        return new DatasetList();
    }
    /**
     * 查询预约产品变更的工单
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryChangeProductTrade(String serialNumber) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (null != userInfo)
        {
            String userId = userInfo.getString("USER_ID");

            IDataset mainProductInfos = UserProductInfoQry.queryUserMainProduct(userId);
            if (IDataUtil.isNotEmpty(mainProductInfos) && mainProductInfos.size() > 1)
            {
                String sysTime = SysDateMgr.getSysTime();
                for (int i = 0; i < mainProductInfos.size(); i++)
                {
                    IData tempData = mainProductInfos.getData(i);
                    // 判断是否下周期生效的产品
                    if (SysDateMgr.getTimeDiff(sysTime, tempData.getString("START_DATE"), SysDateMgr.PATTERN_STAND) > 0)
                    {
                        String instId = tempData.getString("INST_ID");
                        IDataset ds = TradeHistoryInfoQry.queryChgProductTrade(userId, "110", instId);
                        if(IDataUtil.isNotEmpty(ds)){
                        	for(int k=0;k<ds.size();k++)
                            {
                            	IData map = ds.getData(k);
                            	String productId = map.getString("PRODUCT_ID","");
                            	
                            	if(StringUtils.isNotBlank(productId))
                            	{
                                	IData commpara = new DataMap();
                                    commpara.put("SUBSYS_CODE", "CSM");
                                    commpara.put("PARAM_ATTR", "5544");
                                    commpara.put("PARAM_CODE", "SHARE");
                                    commpara.put("PARA_CODE1", productId);
                                    IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                                    if(IDataUtil.isNotEmpty(commparaDs))
                                    {
                                    	map.put("TIPFLAG", "Y");
                                    }
                                    //存在预约生效的共享优惠
                                    IDataset discntInfo = null;
                                    discntInfo = ShareInfoQry.queryDiscntsNEW(userId);
                                    if(IDataUtil.isNotEmpty(discntInfo) && null != discntInfo && discntInfo.size() > 0){
                                    	map.put("TIPFLAG", "Y");
                                    }
                            	}
                            }
                        }
                        return ds;
                    }
                }
            }
        }
        return new DatasetList();
    }

    /**
     * 根据号码查询是否存在错单
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryErrorInfoTrade(String serialNumber) throws Exception
    {
        return TradeInfoQry.queryErrorTrade(serialNumber);
    }

    public IDataset queryTradeDiscntInfos(String tradeId) throws Exception
    {
        return TradeDiscntInfoQry.getTradeDiscntInfosByTradeId(tradeId);
    }

    public IDataset queryTradeProductInfos(String tradeId) throws Exception
    {
        return TradeProductInfoQry.getTradeProductInfosByTradeId(tradeId);
    }

    public IDataset queryTradeSvcInfos(String tradeId) throws Exception
    {
        return TradeSvcInfoQry.getTradeSvcInfosByTradeId(tradeId);
    }

    /**
     * 需求编号	REQ201904090061  
     * 需求标题	新增全球通爆米花套餐及其合约活动
     * mqx 20190513
     * 4、取消预约的全球通爆米花套餐时，标识还原为原标识。
     * 畅享套餐预约变更其他套餐，取消预约变更后没有还原全球通标识。
     * @throws Exception 
     */
	public void dealPopcornProduct(IData hisTrade) throws Exception {
		if (StringUtils.equals("110", hisTrade.getString("TRADE_TYPE_CODE", "0"))) {
			String tradeId = hisTrade.getString("TRADE_ID");
			String userId = hisTrade.getString("USER_ID");
			String serialNum = hisTrade.getString("SERIAL_NUMBER");

			// IDataset productInfos =
			// TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId,
			// "0");
			IDataset listTradeDiscnt = TradeDiscntInfoQry.queryDiscntTradeByTradeIdAndModifyTag(tradeId, "0", Route.getJourDb());
			System.out.println("CancelChangeProductBeanxxxxxxxxxxxxxxx176 " + listTradeDiscnt);

			// TradeDiscntInfoQry.getTradeDiscntByTradeId
            GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
			if (IDataUtil.isNotEmpty(listTradeDiscnt) && listTradeDiscnt.size() != 0) {
				for (int i = 0, size = listTradeDiscnt.size(); i < size; i++) {
					String discntCode = listTradeDiscnt.getData(i).getString("DISCNT_CODE");
					IDataset commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_DISCNT", discntCode, "0898");
					if (IDataUtil.isNotEmpty(commpara423) && commpara423.size() != 0) {
					    String endDate = SysDateMgr.getSysTime();
					    String userClass = commpara423.first().getString("PARA_CODE3");
					    String remark = "产品变更预约取消【"+discntCode+"】";
                        bean.delClassInfo(userId, serialNum,  endDate, userClass, "3",remark);
                    }
				}
			}
			
			//全球通套餐预约变更其他套餐，取消预约变更后还原全球通标识。
			IDataset userDiscntInfos = TradeDiscntInfoQry.queryDiscntTradeByTradeIdAndModifyTag(tradeId, "1", Route.getJourDb());
			System.out.println("CancelChangeProductBeanxxxxxxxxxxxxxxx177 " + userDiscntInfos);
			if (IDataUtil.isNotEmpty(userDiscntInfos) && userDiscntInfos.size() != 0) {
				for (int i = 0, size = userDiscntInfos.size(); i < size; i++) {
					String discntCode = userDiscntInfos.getData(i).getString("DISCNT_CODE");
					IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_DISCNT", discntCode, "0898");
					System.out.println("CancelChangeProductBeanxxxxxxxxxxxxxxx178 " + Commpara423);

					if (IDataUtil.isNotEmpty(Commpara423) && Commpara423.size() != 0) {
						if (1 == 1) {
							IData inData = new DataMap();
							inData.put("USER_ID", userId);
							inData.put("SERIAL_NUMBER", serialNum);
							IDataset currdataset = UserClassInfoQry.queryUserClass_1(inData);// 查取消预约变更前生效的标识记录,条件是：start_date<sysdate and
							// end_date>sysdate
							System.out.println("CancelChangeProductBeanxxxxxxxxxxxxxxx179 " + currdataset);

							if (IDataUtil.isNotEmpty(currdataset) && currdataset.size() > 0) {// 当前生效记录设置为2050-12-3123:59:59
								String rowid = currdataset.first().getString("ROWID");
								inData.clear();
								inData.put("ROWID", rowid);
								inData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
								Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPD_END_DATE_2", inData);
							}
						}
					}
				}
			}
		}
	}

	/**
     * 需求编号	REQ201904090061  
     * 需求标题	新增全球通爆米花套餐及其合约活动
     * mqx 20190513
     * 取消指定主产品（畅享主产品），用户存在指定营销活动（畅享活动）,则提示拦截
     */
	public void checkPopcorn(IData hisTrade) throws Exception {
		
		if (StringUtils.equals("110", hisTrade.getString("TRADE_TYPE_CODE", "0"))){
            String tradeId = hisTrade.getString("TRADE_ID");
            String userId = hisTrade.getString("USER_ID");

            IDataset productInfos = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, "0");
            
            if(IDataUtil.isNotEmpty(productInfos) && productInfos.size()!=0) 
			{
            	for (int i = 0, size = productInfos.size(); i < size; i++)
				{
            		String productId = productInfos.getData(i).getString("PRODUCT_ID");
            		IDataset Commpara423Product = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_PRODUCT", productId, "0898");
            		if(IDataUtil.isNotEmpty(Commpara423Product) && Commpara423Product.size()!=0) 
            		{
            			
            			IDataset Commpara423Activity = CommparaInfoQry.getCommparaAllCol("CSM", "423", "POPCORN_ACTIVITY", "0898");
            			
            			for (int j = 0, sizej = Commpara423Activity.size(); j < sizej; j++)
            			{
            				String catalogId = Commpara423Activity.getData(j).getString("PARA_CODE1");
            				String packageId = Commpara423Activity.getData(j).getString("PARA_CODE2");
            				
            				IDataset userSaleActive = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, catalogId, packageId);
            				if (IDataUtil.isNotEmpty(userSaleActive) && userSaleActive.size()>0)//存在“爆米花套餐合约送话费活动”营销活动
            				{
            					String salePackageName = userSaleActive.getData(0).getString("PACKAGE_NAME");
            					CSAppException.apperr(TradeException.CRM_TRADE_333, "你已办理【"+salePackageName+"】，不能进行变更当前主产品。");
            				}
            			}
            			
            		}
				}
			}
		}
	}
  
}
