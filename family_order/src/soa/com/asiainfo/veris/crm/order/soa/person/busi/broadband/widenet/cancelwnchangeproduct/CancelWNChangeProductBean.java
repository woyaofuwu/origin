
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwnchangeproduct;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class CancelWNChangeProductBean extends CSBizBean
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

    public IDataset getUserWidenetInfo(String serialNumber) throws Exception
    {
        return WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
    }


    /**
     * 查询预约产品变更的工单
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryChangeProductTrade(String serialNumber, String tradeTypeCode) throws Exception
    {
        IDataset prodTradeSet=new DatasetList();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
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
                        prodTradeSet= TradeHistoryInfoQry.queryChgProductTrade(userId, tradeTypeCode, instId);
                        
                    }
                }
            }
            /**
             * REQ201611070013 宽带产品变更取消功能优化
             * chenxy3 2017-2-13
             * */
            if(prodTradeSet!=null && prodTradeSet.size()>0){
                String orderId=prodTradeSet.getData(0).getString("ORDER_ID");
                IDataset saleActiveSet=TradeHistoryInfoQry.qryTradeByTradeTypeOrderId(orderId,"0","240");
                if(saleActiveSet!=null && saleActiveSet.size()>0){
                    String saleActive_tradeid=saleActiveSet.getData(0).getString("TRADE_ID","");
                    String saleActive_prodId=saleActiveSet.getData(0).getString("RSRV_STR1","");
                    String saleActive_prodName=saleActiveSet.getData(0).getString("RSRV_STR3","");
                    String saleActive_packId=saleActiveSet.getData(0).getString("RSRV_STR2","");
                    String saleActive_packName=saleActiveSet.getData(0).getString("RSRV_STR4","");
                    String saleActive_startdate=saleActiveSet.getData(0).getString("RSRV_STR5","");
                    String saleActive_enddate=saleActiveSet.getData(0).getString("RSRV_STR6","");
                    prodTradeSet.getData(0).put("SALEACTIVE_TRADEID", saleActive_tradeid);
                    prodTradeSet.getData(0).put("SALEACTIVE_PRODID", saleActive_prodId);
                    prodTradeSet.getData(0).put("SALEACTIVE_PRODNAME", saleActive_prodName);
                    prodTradeSet.getData(0).put("SALEACTIVE_PACKID", saleActive_packId);
                    prodTradeSet.getData(0).put("SALEACTIVE_PACKNAME", saleActive_packName);
                    prodTradeSet.getData(0).put("SALEACTIVE_STARTDATE", saleActive_startdate);
                    prodTradeSet.getData(0).put("SALEACTIVE_ENDDATE", saleActive_enddate);  
                }
                IDataset oldSaleActiveSet=TradeHistoryInfoQry.qryTradeByTradeTypeOrderId(orderId,"0","237");
                if(oldSaleActiveSet!=null && oldSaleActiveSet.size()>0){
                    String oldSaleActiveTradeId=oldSaleActiveSet.getData(0).getString("TRADE_ID","");
                    prodTradeSet.getData(0).put("OLD_SALEACTIVE_TRADEID", oldSaleActiveTradeId);  
                }
                
                String tradeId=prodTradeSet.getData(0).getString("TRADE_ID");
                
                //判断修改的原活动是否是老的包年套餐，如果是的话要设置标记退钱。
                /*要将赠送存折的金额沉淀
                 * 1、select t.*,t.rowid from tf_b_trade_discnt t where t.trade_id=1117021449757695 and t.modify_tag='1'; 
                 * 2、取上面的优惠，判断优惠（discnt_code)是否存在老套餐中select t.*,t.rowid from td_s_commpara t where t.param_attr='535' 
                 * 3、如果存在，则设置标记 oldDiscntTag='1'.
                 * 4、在退费的时候，判断到上面的标记=1，则要取赠送话费村则金额，如果>0则沉淀。
                 * *****取存折金额接口： 
                 * */
                IData param = new DataMap();
                param.put("TRADE_ID", tradeId);  
                IDataset resultDiscnts =Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_ATTR", param, Route.getJourDbDefault());
                if(resultDiscnts.size()>0)
                {                   
                    prodTradeSet.getData(0).put("OLD_DISCNT_TAG", "1");  
                }
                return prodTradeSet;
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
    
    /**
     * 返回TF_F_USER_SALE_ACTIVE原来的终止日期
     * */
    public static void doActiveEndDateAndInsTiDiscnt(String oldSaleActiveTradeId)throws Exception{
        IDataset actives=getSaleActiveBakByTradeId(oldSaleActiveTradeId);
        String endDate = "";
        String userId = "";
        String productId = "";
        String packageId = "";
        if(actives!=null && actives.size()>0){
            endDate=actives.getData(0).getString("END_DATE","");
            endDate=endDate.substring(0,endDate.indexOf("."));
            userId=actives.getData(0).getString("USER_ID","");
            productId=actives.getData(0).getString("PRODUCT_ID","");
            packageId=actives.getData(0).getString("PACKAGE_ID","");
            CancelWNChangeProductBean.updSaleActiveBakByTradeId(userId, endDate, productId, packageId);
        }
    }

    /**
     * 吉祥号码营销活动返回TF_F_USER_SALE_ACTIVE原来的终止日期
     * */
    public static void recoverActiveEndDateByBak(String oldSaleActiveTradeId)throws Exception{
        IDataset actives=getBeautifulNumberSaleActiveBakByTradeId(oldSaleActiveTradeId);
        String endDate = "";
        String userId = "";
        String productId = "";
        String packageId = "";
        if(actives!=null && actives.size()>0){
            endDate=actives.getData(0).getString("END_DATE","");
            endDate=endDate.substring(0,endDate.indexOf("."));
            userId=actives.getData(0).getString("USER_ID","");
            productId=actives.getData(0).getString("PRODUCT_ID","");
            packageId=actives.getData(0).getString("PACKAGE_ID","");
            CancelWNChangeProductBean.updSaleActiveEndDateByBak(userId, endDate, productId, packageId);
        }
    }
    /**
     * 根据tradeId查询用户备份有效活动数据
     *  
     */
    public static IDataset getSaleActiveBakByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        
        IDataset tradeBhInfo = TradeInfoQry.queryTradeBhByTradeIdAndTypeCode(tradeId, "237");
        
        if (IDataUtil.isEmpty(tradeBhInfo))
        {
            return null;
        }
        
        return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE_BAK", "SEL_TRADESALEACTIVE_BAK_BY_TRADEID", params);
    }

    /**
     * 根据tradeId查询用户备份有效吉祥号码活动数据
     *
     */
    public static IDataset getBeautifulNumberSaleActiveBakByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);

        IDataset tradeBhInfo = TradeInfoQry.queryTradeBhByTradeIdAndTypeCode(tradeId, "237");

        if (IDataUtil.isEmpty(tradeBhInfo))
        {
            return null;
        }

        return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE_BAK", "SEL_BEAUTIFULNUMBERSALEACTIVE_BAK_BY_TRADEID", params);
    }

    public static void updSaleActiveBakByTradeId(String userId,String endDate,String productId,String packageId) throws Exception
    {
        IData params = new DataMap();
        params.put("END_DATE", endDate);
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_END_DATE_BACK", params);
    }

    public static void updSaleActiveEndDateByBak(String userId,String endDate,String productId,String packageId) throws Exception
    {
        IData params = new DataMap();
        params.put("END_DATE", endDate);
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_END_DATE_BYBAK", params);
    }
    /**
     * 根据tradeId查询用户备份有效活动数据
     */
    public static IDataset getDiscntBakByTradeId(String oldDiscntTradeId,String productId,String packageId) throws Exception
    {
        IData params = new DataMap(); 
        params.put("TRADE_ID", oldDiscntTradeId);
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TF_B_TRADE_DISCNT_BAK", "SEL_DISCNT_BAK_BY_TRADEID", params);
    }

    /**
     * 插入优惠中间表主表
     * */
    public static void insTiSync(String iv_sync_sequence) throws Exception
    {
        IData synchInfoData = new DataMap();
        synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
        String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
        synchInfoData.put("SYNC_DAY", syncDay);
        synchInfoData.put("SYNC_TYPE", "0");
        synchInfoData.put("TRADE_ID", "0");
        synchInfoData.put("STATE", "0");
        synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
        synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        Dao.insert("TI_B_SYNCHINFO", synchInfoData, Route.getJourDbDefault());
    }

}
