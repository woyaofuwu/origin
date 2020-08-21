
package com.asiainfo.veris.crm.order.soa.person.busi.cancelchangeproduct;

import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.cancelchangeproduct.CancelChangeProductUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeScore;
import com.asiainfo.veris.crm.order.soa.person.busi.canceltrade.CancelTradeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.GPRSDiscntChangeSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.UndoOfferRelBakDeal;

public class CancelChangeProductSVC extends CSBizService
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

        /**************************** 数据准备 **********************/
        IData hisTrade = hisTradeInfos;// 历史台账信息
        IData pubData = this.getPublicData(tradeId, hisTrade);// 操作员相信息
        /************************ 规则校验 ********************************/
        //老系统的操作，生成offerRel备份信息
        UndoOfferRelBakDeal.dealOfferRelBak(hisTrade);
        // 营销活动办理顺带预约产品变更的判断
        String strRSRV_STR7 = hisTrade.getString("RSRV_STR7");
        String strTradetypecode = hisTrade.getString("TRADE_TYPE_CODE");
        if ("1".equals(strRSRV_STR7) && "110".equals(strTradetypecode))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_330, tradeId);
        }
        if (!"".equals(strRSRV_STR7) && "110".equals(strTradetypecode))
        {// strRSRV_STR7记录的营销活动产品id。这个是营销活动一键办理，顺带的预约产品变更
            String userId = hisTrade.getString("USER_ID");
            // 查询用户当前是否存在该营销活动产品。需要先取消营销活动才能办理取消产品变更
            IDataset sale  = BreQry.qryUserSaleActiveInfo(strRSRV_STR7,userId);
            log.debug("CancelChangeProductSVC---------->sale"+sale);
            boolean flag =false;
            if (IDataUtil.isNotEmpty(sale)){// 查到有记录，证明该用户当前未返销营销活动
                flag = true;
            }
            if (flag){
                CSAppException.apperr(TradeException.CRM_TRADE_336, tradeId);
            }
        }

        CancelChangeProductBean chgBean = BeanManager.createBean(CancelChangeProductBean.class);
        chgBean.chkTradeBeforeCancel(pubData, hisTrade);

        /*
         * 需求编号	REQ201904090061  
         * 需求标题	新增全球通爆米花套餐及其合约活动
         * mqx 20190513
         * 取消指定主产品（畅享主产品），用户存在指定营销活动（畅享活动）,则提示拦截
         */
        chgBean.checkPopcorn(hisTrade);
        /****************************begin REQ201502110012 新增优惠依赖类型判断及预约业务取消限制@yanwu **********************/
        //queryElementLimitByElementIdB3(hisTrade, tradeId);
        /****************************end REQ201502110012 新增优惠依赖类型判断及预约业务取消限制@yanwu **********************/
        
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
        cancelUtil.checkDiscntByElementLimit3();
        /****************************end REQ201512290003 优化依赖关系LIMIT_TAG为3类型预约取消限制问题@yanwu **********************/

        cancelUtil.doCancelChangeProduct();
        // 积分和费用的处理放在登记处理【避免完工时积分和费用不足 不能返销】 2013-12-3
        TradeScore.tradeScore(newCancelTrade);// 积分返销--入参中的cancel_tag非常重要
        TradeCancelFee.cancelRecvFee(newCancelTrade, pgData);// 费用返销
        
        /*
         * 需求编号	REQ201904090061  
         * 需求标题	新增全球通爆米花套餐及其合约活动
         * mqx 20190513
         * 4、取消预约的全球通爆米花套餐时，标识还原为原标识。
         */
        chgBean.dealPopcornProduct(hisTrade);
        
        IDataset results = new DatasetList();
        pgData.put("RESULT_CODE", "0");
        pgData.put("ORDER_ID", newOrderId);
        pgData.put("TRADE_ID", tradeId);
        pgData.put("RESULT_INFO", "信息处理成功!");
        results.add(pgData);
        return results;
    }
    
    /**
     * @author yanwu
     * @param hisTrade
     * @param tradeId
     * @throws Exception
     */
    private void queryElementLimitByElementIdB3(IData hisTrade, String tradeId) throws Exception {
    	
    	String userId = hisTrade.getString("USER_ID");
		String epcharCode = hisTrade.getString("EPARCHY_CODE");
		String strsYsSD = SysDateMgr.getSysTime();
        IDataset addDiscnts = new DatasetList();
		IDataset tradeDiscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		if( IDataUtil.isNotEmpty(tradeDiscnts) ){
			IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(userId, epcharCode);
			for (int i = 0, size = tradeDiscnts.size(); i < size; i++)
			{
				IData tradeDiscnt = tradeDiscnts.getData(i);
				String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
				String instId = tradeDiscnt.getString("INST_ID");
				String startDate = tradeDiscnt.getString("START_DATE");
				//String endDate = tradeDiscnt.getString("END_DATE");
				
				IData userDiscnt = this.getElement(instId, userDiscnts);
				if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
				{
					if (userDiscnt == null)
					{
						// 预约产品变更时新增，但是后续又被删除，这种无需处理
						continue;
					}
					else
					{
						if( startDate.compareTo(strsYsSD) > 0 ){
							// 预约产品变更时新增，后续再无任何操作，需要匹配
							addDiscnts.add(tradeDiscnt);
						}
					}
				}
			}
		
			if( IDataUtil.isNotEmpty(addDiscnts) ){
				for (int nIndex = 0; nIndex < addDiscnts.size(); nIndex++)
				{
					IData addDiscnt = addDiscnts.getData(nIndex);
					/*String modifyTag = addDiscnt.getString("MODIFY_TAG");
					String instId = addDiscnt.getString("INST_ID");
					String startDate = addDiscnt.getString("START_DATE");
					String endDate = addDiscnt.getString("END_DATE");*/
					String elementIdB = addDiscnt.getString("DISCNT_CODE");
					
					// 查看后续是否有添加依赖的元素
					IDataset limitElements = ElemLimitInfoQry.queryElementLimitByElementIdB("D", elementIdB, "3", epcharCode);
					if( IDataUtil.isNotEmpty(limitElements) ){
						for (int i = 0, size = limitElements.size(); i < size; i++)
						{
							IData limitElement = limitElements.getData(i);
							String limitElementType = limitElement.getString("ELEMENT_TYPE_CODE_A");
							String limitElementId = limitElement.getString("ELEMENT_ID_A");
							if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(limitElementType))
							{
								if( IDataUtil.isNotEmpty(userDiscnts) ){
									for (int j = 0; j < userDiscnts.size(); j++)
									{
										IData userDiscnt = userDiscnts.getData(j);
										String elementIdA = userDiscnt.getString("DISCNT_CODE");
										String userSD = userDiscnt.getString("END_DATE");
										if ( limitElementId.equals(elementIdA) && userSD.compareTo(strsYsSD) > 0 )
										{
											String elementIdBName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementIdB);
											String elementIdAName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementIdA);
											//您所预约【elementIdBName】套餐被【elementIdAName】套餐所依赖，不允许取消此工单!
											CSAppException.apperr(ElementException.CRM_ELEMENT_309, elementIdBName, elementIdAName);
										}
									}
								}
							}
							
						}
					}
				}
			}
		}
	}

    /**
     * @author yanwu
     * @param instId
     * @param elements
     * @return
     */
    private IData getElement(String instId, IDataset elements)
	{
		if (IDataUtil.isNotEmpty(elements))
		{
			for (int i = 0, size = elements.size(); i < size; i++)
			{
				IData element = elements.getData(i);
				if (instId.equals(element.getString("INST_ID")))
				{
					return element;
				}
			}
		}
		return null;
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

    /**
     * 查询预约的产品变更工单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryChangeProductTrade(IData input) throws Exception
    {
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        if (StringUtils.isNotBlank(input.getString("NET_TYPE_CODE")))
        {
            return bean.queryChangeProductTradeByTDFixed(serialNumber);
        }
        return bean.queryChangeProductTrade(serialNumber);
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
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        return bean.queryErrorInfoTrade(serialNumber);
    }

    public IDataset queryTradeDiscntInfos(IData input) throws Exception
    {
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        String tradeId = input.getString("TRADE_ID");
        return bean.queryTradeDiscntInfos(tradeId);
    }

    public IDataset queryTradeProductInfos(IData input) throws Exception
    {
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        String tradeId = input.getString("TRADE_ID");
        return bean.queryTradeProductInfos(tradeId);
    }

    public IDataset queryTradeSvcInfos(IData input) throws Exception
    {
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        String tradeId = input.getString("TRADE_ID");
        return bean.queryTradeSvcInfos(tradeId);
    }
}
