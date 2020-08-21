package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import java.util.ArrayList;
import java.util.List;


import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

/**
 * 营销活动通用规则校验
 * 
 * @author Mr.Z
 */
public class CheckSaleTradeLimit extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 8687825166597590097L;

    private static final Logger log = Logger.getLogger(CheckSaleTradeLimit.class);

    private IData buildLimitDependData(IDataset saleTradeLimitDataset)
    {
        IDataset saleTradeLimitDependSet = new DatasetList();
        for (int index = 0, size = saleTradeLimitDataset.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataset.getData(index);
            String limitTag = saleTradeLimitData.getString("LIMIT_TAG");
            if (SaleActiveBreConst.SALE_TRADE_LIMIT_TAG_DEPEND.equals(limitTag))
            {
                saleTradeLimitDependSet.add(saleTradeLimitData);
            }
        }

        IData limitTypeData = new DataMap();
        for (int index = 0, size = saleTradeLimitDependSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDependSet.getData(index);
            String limitType = saleTradeLimitData.getString("LIMIT_TYPE");

            IDataset tempDataset = null;
            if (limitTypeData.containsKey(limitType))
            {
                tempDataset = limitTypeData.getDataset(limitType);
                tempDataset.add(saleTradeLimitData);

            }
            else
            {
                tempDataset = new DatasetList();
                tempDataset.add(saleTradeLimitData);
            }
            limitTypeData.put(limitType, tempDataset);
        }

        return limitTypeData;
    }

    private IDataset buildLimitMutexDataset(IDataset saleTradeLimitDataset)
    {
        IDataset saleTradeLimitMutexSet = new DatasetList();
        for (int index = 0, size = saleTradeLimitDataset.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataset.getData(index);
            String limitTag = saleTradeLimitData.getString("LIMIT_TAG");
            if (SaleActiveBreConst.SALE_TRADE_LIMIT_TAG_MUTEX.equals(limitTag))
            {
                saleTradeLimitMutexSet.add(saleTradeLimitData);
            }
        }
        return saleTradeLimitMutexSet;
    }

    /**
     * 依赖规则校验
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDepend(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataset) throws Exception
    {
        boolean result = true;

        IData limitTypeData = buildLimitDependData(saleTradeLimitDataset);

        if (log.isDebugEnabled())
        {
            log.debug("checkTradeLimitDependDiscntTroop.....limitTypeData............" + limitTypeData);
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_BRAND))
        {
            result = checkTradeLimitDependByBrand(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_BRAND));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PRODUCT))
        {
            result = checkTradeLimitDependByProduct(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PRODUCT));
        }
        //add by zhangxing3 for 开学抢红包送终端活动开发需求--新增活动规则
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PRODUCT_SPEC))
        {
            result = checkTradeLimitDependByProductSpec(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PRODUCT_SPEC));
        }
        //add by zhangxing3 for 开学抢红包送终端活动开发需求--新增活动规则

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT))
        {
            result = checkTradeLimitDependDiscnt(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT));
        }
        //BUS202004130028关于信用购业务优化的需求    -----add by huangmx5
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_ALL_DISCNT))
        {
            result = checkTradeLimitDependAllDiscnt(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_ALL_DISCNT));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SERVICE))
        {
            checkTradeLimitDependService(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SERVICE));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PLATSVC_BIZ_TYPE_CODE))
        {
            checkTradeLimitDependPlatSvcBizTypeCode(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_PLATSVC_BIZ_TYPE_CODE));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT_TROOP))
        {
            result = checkTradeLimitDependDiscntTroop(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT_TROOP));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SN))
        {
            result = checkTradeLimitDependBySn(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SN));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SALE_PRODUCT))
        {
            result = checkTradeLimitDependSaleProduct(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_SALE_PRODUCT));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_OPEN_DATE))
        {
            result = checkTradeLimitDependOpenDate(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_OPEN_DATE));
        }
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_OPEN_DATE1593))
        {
            result = checkTradeLimitDependOpenDate1593(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_OPEN_DATE1593));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_1588))
        {
            result = checkTradeLimitDepend1588(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_1588));
        }

        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_968))
        {
        	boolean is1593Active = SaleActiveBreUtil.is1593Active(databus.getString("PRODUCT_ID"), databus.getString("EPARCHY_CODE"));
        	boolean has1593OtherActives = SaleActiveBreUtil.isExists1593OtherActives(databus.getDataset("TF_F_USER_SALE_ACTIVE"), databus.getString("PRODUCT_ID"), databus.getString("EPARCHY_CODE"));
        	
        	if(!is1593Active || !has1593OtherActives)
        	{
               result = checkTradeLimitDepend968(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_968));
        	}
        }
        
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_955)){
        	 result = checkTradeLimitDepend955(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_955));
        }
        if(limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_956)){
        	result = checkTradeLimitDepend956(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_956));
        }
        
        if(limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_1220)){
        	result = checkTradeLimitDependPlatSvcBizTypeCode1220(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_1220));
        }
        
        /**
         * REQ201704140015_关于和路通业务营销活动开发需
         * @author zhuoyingzhi
         * @date 20170513
         */
        if(limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT_20170513)){
        	result = checkTradeLimitDependByUserDiscnt20170513(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_DISCNT_20170513));
        }  
        
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_IMS))
        {
            result = checkTradeLimitIMS(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_IMS));
        }

        //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 start
        //td_s_commpara表param_attr=155 增加顺延生效配置9155,
        //155中活动在9155中配置指定活动,且用户已办理活动包含指定活动,则可以顺延生效办理该活动,此处增加此类规则顺延生效限定月份校验
        if (limitTypeData.containsKey(SaleActiveBreConst.DEPEND_LIMIT_TYPE_9155)){
            result = checkTradeLimitDepend9155(databus, ruleParam, limitTypeData.getDataset(SaleActiveBreConst.DEPEND_LIMIT_TYPE_9155));
        }
        //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 end
        return result;
    }
    
    private boolean checkTradeLimitIMS(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
    	IDataset relations = databus.getDataset("TF_F_RELATION_UU");
        boolean flg = true;
        IData limitData = saleTradeLimitDataSet.first();
        String limitCode = limitData.getString("LIMIT_CODE");
        for (int j = 0, s = relations.size(); j < s; j++)
        {
            IData relation = relations.getData(j);
            String type = relation.getString("RELATION_TYPE_CODE");
            if("MS".equals(type)){
            	 String startDate = relation.getString("START_DATE");
            	 String endDate = relation.getString("END_DATE");
            	 String userIDA = relation.getString("USER_ID_A");
                 String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
                 if(SysDateMgr.dayInterval(sysDate,endDate)>0){
                	 IDataset relaUUInfos = RelaUUInfoQry.qryGrpRelaUUByUserIdA(userIDA,"MS");
                	 if(IDataUtil.isNotEmpty(relaUUInfos)){
                		 for (Object relaUU : relaUUInfos) {
             				IData trade =(IData) relaUU ;
             				String  roleCodeB =  trade.getString("ROLE_CODE_B");
             				 if("2".equals(roleCodeB)){
                        		 String serialNumber = trade.getString("SERIAL_NUMBER_B");
                        		 long tradeIdL = 0;
                        		 IDataset tradeDatas = TradeHistoryInfoQry.getInfosBySnTradeTypeCode("6800", serialNumber, "") ; 
                        		 for (Object object : tradeDatas) {
                    				IData tradeData =(IData) object ;
                    				Long selTradeIdL =  tradeData.getLong("TRADE_ID");
                    				if(selTradeIdL>tradeIdL){
                    				    tradeIdL = selTradeIdL;
                    			 	}
                    			 }
                        		 
                        		 IDataset tradeData = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(String.valueOf(tradeIdL));
                        		 if(IDataUtil.isNotEmpty(tradeData)){
                        			String finishDate = tradeData.first().getString("FINISH_DATE");
                        		    int num = SysDateMgr.dayInterval(sysDate,finishDate);
                        			if(num<=Integer.parseInt(limitCode)){
                        				 flg = false ;
                                      	 break;
                        			}
                        		 }
                        	 }
             			 }
                	 }
                	
                 }
            }
           
        }

        if (flg)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201909, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }
        return true;
    	
    }
    
    private boolean checkTradeLimitDepend955(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet1) throws Exception
    {
    	String userId = databus.getString("USER_ID");
    	String limitCode1 = saleTradeLimitDataSet1.getData(0).getString("LIMIT_CODE", "0");
    	limitCode1 = SysDateMgr.getDateForSTANDYYYYMMDD(limitCode1);
    	IDataset userSaleActives1 = SaleActiveInfoQry.getUserSaleActiveInfoByLimitCode(userId, limitCode1);
    	userSaleActives1 = SaleActiveUtil.filterUserSaleActivesByQyyx(userSaleActives1);
    	if(IDataUtil.isNotEmpty(userSaleActives1)){
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20158, saleTradeLimitDataSet1.getData(0).getString("LIMIT_MSG"));
    	}
        return true;
    }
    
    private boolean checkTradeLimitDepend956(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet2) throws Exception
    {
    	String userId = databus.getString("USER_ID");
    	String limitCode2 = saleTradeLimitDataSet2.getData(0).getString("LIMIT_CODE", "0");
    	limitCode2 = SysDateMgr.getDateForSTANDYYYYMMDD(limitCode2);
    	IDataset userSaleActives2 = SaleActiveInfoQry.getUserSaleActiveInfoByLimitCodeN(userId, limitCode2);
    	userSaleActives2 = SaleActiveUtil.filterUserSaleActivesByQyyx(userSaleActives2);
    	if(IDataUtil.isNotEmpty(userSaleActives2)){
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20158, saleTradeLimitDataSet2.getData(0).getString("LIMIT_MSG"));
    	}
        return true;
    }

    private boolean checkTradeLimitDepend1588(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        String paramProductId = saleTradeLimitDataSet.getData(0).getString("LIMIT_CODE", "0");
        String userId = databus.getString("USER_ID");

        IDataset memberActives = BreQry.getPayMemberBActives(userId, paramProductId);

        if (IDataUtil.isEmpty(memberActives))
        {
            String productName = UProductInfoQry.getProductNameByProductId(paramProductId);
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20157, "统一付费成员须办理[" + productName + "]活动才可办理此活动!");
        }

        return true;
    }

    private boolean checkTradeLimitDepend968(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", databus.getString("EPARCHY_CODE"));
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);

        UcaData uca = UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER"));
        List<SaleActiveTradeData> userBookAndBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBookAndBackSaleActives);

        if (CollectionUtils.isEmpty(userBookAndBackSaleActives))
            return true;

        String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userBookAndBackSaleActives);
        String limitMoth = saleTradeLimitDataSet.getData(0).getString("LIMIT_CODE", "0");
        int monthInterval = SysDateMgr.monthInterval(SysDateMgr.getSysTime(), maxEndDate);
        if (monthInterval > Integer.parseInt(limitMoth))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20114, "用户已办理的活动不在可顺延期限内，不能办理本活动！");
        }

        return true;
    }

    /**
     * 用户是XX品牌，才能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependByBrand(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        String userBrand = databus.getString("BRAND_CODE");
        boolean isDependBrand = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            if (userBrand.equals(limitCode))
            {
                isDependBrand = true;
                break;
            }
        }

        if (!isDependBrand)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024059, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 用户是XX产品，才能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependByProduct(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        String userProductId = databus.getString("USER_PRODUCT_ID");
        boolean isDependBrand = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            if (userProductId.equals(limitCode))
            {
                isDependBrand = true;
                break;
            }
        }

        if (!isDependBrand)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024060, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }
    
    /**
     * 开学抢红包送终端活动开发需求--新增活动规则
     * 用户证件号下的其他号码是XX产品，才能办理该型营销活动
     * @author zhangxing3
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependByProductSpec(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        String strSerialNumber = databus.getString("SERIAL_NUMBER");
        String strPsptId = "";
        boolean isDependBrand = false;
        String errInfo = "";
        IDataset psptInfos = CustomerInfoQry.getCustInfoPsptBySn(strSerialNumber);
        if (IDataUtil.isNotEmpty(psptInfos))
        {
        	strPsptId = psptInfos.getData(0).getString("PSPT_ID", "");
        }
        
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            IDataset userInfos = UserInfoQry.getUserInfoByPsptProdID(strPsptId,limitCode);

            if (IDataUtil.isNotEmpty(userInfos))
            {
                isDependBrand = true;
                break;
            }
        }

        if (!isDependBrand)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024060, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }
    
    /**
     * 用户是XX号段，才能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependBySn(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        boolean isDependSn = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            if (limitCode.indexOf(serialNumber.substring(0, 2)) > -1)
            {
                isDependSn = true;
                break;
            }
        }
        if (!isDependSn)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024066, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }
        return true;
    }

    /**
     * 用户有XX时间前办理的YY优惠，才能办理该型营销活动；XX时间目前都是2050，先写死。
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependDiscnt(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        boolean isDependDiscnt = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userDiscnts.size(); j < s; j++)
            {
                IData userDiscnt = userDiscnts.getData(j);
                String discntCode = userDiscnt.getString("DISCNT_CODE");
                String endDate = userDiscnt.getString("END_DATE");
                if (limitCode.indexOf(discntCode) > -1 && endDate.compareTo(SysDateMgr.END_TIME_FOREVER) >= 0)
                {
                    isDependDiscnt = true;
                    break;
                }
            }
        }

        if (!isDependDiscnt)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024061, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }
    /**
     * 用户必须办理配置的所有优惠，才能办理该型营销活动    -----add by huangmx5
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependAllDiscnt(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        log.debug("打印优惠编码呀=============+"+userDiscnts);
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
        	log.debug("====================="+size);
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            log.debug("打印优惠限制编码呀=============+"+limitCode);
            String[] limitCodeStr =limitCode.split("\\|");
            
            for(int i = 0;i<limitCodeStr.length;i++){
    			String limitStr = limitCodeStr[i];
    			if(StringUtils.isNotBlank(limitStr)){
    				//判断限制编码和优惠编码是否相等标记,默认为false不相等
        			boolean isEquals = false;
        			for(int j = 0, s = userDiscnts.size(); j < s; j++){
        				IData userDiscnt = userDiscnts.getData(j);
                        String discntCode = userDiscnt.getString("DISCNT_CODE");
                        String endDate = userDiscnt.getString("END_DATE");
        				if(limitStr.equals(discntCode) && endDate.compareTo(SysDateMgr.END_TIME_FOREVER) >= 0){
        					isEquals = true;
        					break;
        				}
        			}
        			//循环完之后如果isEquals=false直接报错
        			if(!isEquals){
        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024061, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        				break;
        			}
    			}
    		}
        }
        return true;
    }

    /**
     * 用户有XX时间前办理的YY优惠，才能办理该型营销活动；目标客户不做此判断，XX时间目前都是2050，先写死。
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependDiscntTroop(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("checkTradeLimitDependDiscntTroop.................");
        }
        boolean isTroopMember = databus.getBoolean("IS_TROOP_MEMBER");
        if (isTroopMember)
            return true;
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        boolean isDependDiscnt = false;

        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userDiscnts.size(); j < s; j++)
            {
                IData userDiscnt = userDiscnts.getData(j);
                String discntCode = "|" + userDiscnt.getString("DISCNT_CODE") + "|";
                if (limitCode.indexOf(discntCode) > -1)
                {
                    isDependDiscnt = true;
                    break;
                }
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug("checkTradeLimitDependDiscntTroop.........isDependDiscnt................." + isDependDiscnt);
        }

        if (!isDependDiscnt)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024065, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 用户入网xx天内，才能办理该活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitDataSet
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependOpenDate(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        boolean isDependOpenDate = false;
        String openDate = databus.getString("OPEN_DATE");
        String limitCode = saleTradeLimitDataSet.getData(0).getString("LIMIT_CODE", "0");
        String sysDate = SysDateMgr.getSysTime();
        String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(limitCode));

        if (tempDate.compareTo(sysDate) > 0)
            isDependOpenDate = true;

        if (!isDependOpenDate)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024068, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }
    
    /**
     * 用户入网xx天外，才能办理该活动
     * chenxy3 2016-07-20
     */
    private boolean checkTradeLimitDependOpenDate1593(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
    	//add 20170511 特定的宽带营销活动不需要判断魔百和6个月的规则。
    	String saleActiveId = databus.getString("SALE_ACTIVE_ID","");
//    	String productId = databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","");
//    	IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", productId, saleActiveId, getTradeEparchyCode());
    	//110006	个人手机约定消费68元/月含50M宽带(预受理)
    	//110007	个人手机约定消费68元/月含50M宽带(预受理)
    	//110008	个人手机约定消费108元/月含100M宽带(预受理)
    	//110009	个人手机约定消费108元/月含100M宽带(预受理)
    	if ("110006".equals(saleActiveId)||"110007".equals(saleActiveId)||"110008".equals(saleActiveId)||"110009".equals(saleActiveId))
        {
    		return true;
        }
    	
    	//如果顺便办理了
    	
        boolean isDependOpenDate = false;
        String openDate = databus.getString("OPEN_DATE");
        String limitCode = saleTradeLimitDataSet.getData(0).getString("LIMIT_CODE", "0");
        String sysDate = SysDateMgr.getSysTime();
        String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(limitCode));

        if (tempDate.compareTo(sysDate) < 0)
            isDependOpenDate = true;

        if (!isDependOpenDate)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024068, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 用户有XX BIZ_TYPE_CODE 的平台服务，才能办理该型营销活动；
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependPlatSvcBizTypeCode(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userPlatSvcs = databus.getDataset("TF_F_USER_PLATSVC");
        if (IDataUtil.isEmpty(userPlatSvcs))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024063, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }
        boolean isDependPlatSvc = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userPlatSvcs.size(); j < s; j++)
            {
                IData userPlatSvc = userPlatSvcs.getData(j);
                String serviceId = userPlatSvc.getString("SERVICE_ID");
                IDataset svcData = UpcCall.qrySpInfoByOfferId(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
                String bizTypeCode = svcData.getData(0).getString("BIZ_TYPE_CODE");
                String bizSateCode = userPlatSvc.getString("BIZ_STATE_CODE");
                if (limitCode.equals(bizTypeCode) && ("A".equals(bizSateCode) || "N".equals(bizSateCode) || "L".equals(bizSateCode)))
                {
                    isDependPlatSvc = true;
                    break;
                }
            }
        }
        if (!isDependPlatSvc)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024064, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 用户办理了XX活动，才能办理YY活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitDataSet
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependSaleProduct(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        if (IDataUtil.isEmpty(userSaleActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024067, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
            return true;
        }
        boolean isDependSaleProduct = false;

        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userSaleActives.size(); j < s; j++)
            {
                IData userSaleActive = userSaleActives.getData(j);
                String saleProductId = userSaleActive.getString("PRODUCT_ID");
                if (limitCode.equals(saleProductId))
                {
                    isDependSaleProduct = true;
                    break;
                }
            }
        }
        if (!isDependSaleProduct)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024067, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 用户有XX服务，才能办理该型营销活动；
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependService(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userSvcs = databus.getDataset("TF_F_USER_SVC");
        boolean isDependSvc = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userSvcs.size(); j < s; j++)
            {
                IData userSvc = userSvcs.getData(j);
                String serviceId = userSvc.getString("SERVICE_ID");
                if (limitCode.equals(serviceId) || limitCode.indexOf(serviceId) >= 0)
                {
                    isDependSvc = true;
                    break;
                }
            }
        }
        if (!isDependSvc)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024062, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * 互斥规则校验
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutex(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataset) throws Exception
    {
        boolean result = true;
        IDataset saleTradeLimitMutexSet = buildLimitMutexDataset(saleTradeLimitDataset);
        //标志是否为宽带移机附加的营销活动，如果是，则在业务提交时已经做校验，此处不进行产品、优惠、互斥营销活动的校验
        String wideNetMoveSign = databus.getString("WIDENET_MOVE_SALEACTIVE_SIGN","0");
        String SpecialSaleFlag = databus.getString("SPECIAL_SALE_FLAG",""); //add 20170511
        
        if (log.isDebugEnabled())
        {
            log.debug("checkTradeLimitDependDiscntTroop.....saleTradeLimitMutexSet............" + saleTradeLimitMutexSet);
        }

        String productId = databus.getString("PRODUCT_ID");
//        IData productInfo = ProductInfoQry.queryAllProductInfo(productId);
//        String tagSet = productInfo.getString("TAG_SET", "");
        
        String tagSet = SaleActiveBreUtil.getProductTagSet(productId);//productInfo.getString("TAG_SET","");
        
        boolean isProductTagset10 = tagSet.length() > 9 && tagSet.substring(9, 10).equals("1") ? true : false;
        
        checkSameActiveThreeMonths(databus, ruleParam, isProductTagset10);

        for (int index = 0, size = saleTradeLimitMutexSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitMutexSet.getData(index);

            String limitType = saleTradeLimitData.getString("LIMIT_TYPE");
            if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_ACTIVE_PRODUCT_10.equals(limitType))
            {
            	boolean is1593Active = SaleActiveBreUtil.is1593Active(productId, databus.getString("EPARCHY_CODE"));
            	boolean has1593OtherActives = SaleActiveBreUtil.isExists1593OtherActives(databus.getDataset("TF_F_USER_SALE_ACTIVE"), productId, databus.getString("EPARCHY_CODE"));
            	/**
            	 * REQ201707240016 允许宽带包年营销活动客户变更为宽带1+活动客户
            	 * 有权限的工号允许在已有宽带包年情况下办理宽带1+（完工会终止包年活动）
            	 * */
            	String CATALOG_ID=saleTradeLimitData.getString("CATALOG_ID");
            	String limitCode=saleTradeLimitData.getString("LIMIT_CODE");
            	String passTag="0";
            	
            	//---add by zhangxing3 for REQ201707240016允许宽带包年营销活动客户变更为宽带1+活动客户---start
            	/*if("69908001".equals(CATALOG_ID) && "67220428".equals(limitCode))
            	{
            		//System.out.println("========checkTradeLimitMutex======="+databus.getString("SERIAL_NUMBER"));
            		UcaData uca = UcaDataFactory.getNormalUca("KD_"+databus.getString("SERIAL_NUMBER"));
            		String userId = uca.getUserId();
            		IDataset userInfo = UserInfoQry.getUserInfoByUserIdTag(userId, "0");
            		//System.out.println("========checkTradeLimitMutex=======userInfo:"+userInfo);
	                if(IDataUtil.isNotEmpty(userInfo))
	                {
	        	        String inDate = userInfo.getData(0).getString("OPEN_DATE");
	        			String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);
	        			String currMonth = SysDateMgr.getNowCycle().substring(0, 6);
	        			if (inMonth.equals(currMonth))//判断是不是宽带首月免费期内不能由宽带包年变更为宽带1+
	        			{
	                		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180319, "宽带免费期内不能由宽带包年变更为宽带1+!");
	        			}
	                }
	
	                // 查询生效的优惠
	                IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
	                //System.out.println("========checkTradeLimitMutex=======discntInfo:"+discntInfo);
	                if (IDataUtil.isEmpty(discntInfo))
	                {
	            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180319, "宽带免费期内不能由宽带包年变更为宽带1+!");

	                }
            	
            	}*/
            	//--add by zhangxing3 for REQ201707240016允许宽带包年营销活动客户变更为宽带1+活动客户---end
            	
            	if("69908001".equals(CATALOG_ID) && "67220428".equals(limitCode)){
            		if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_SALEACTIVE_CHANGE_RULE1")){
            			passTag="1";
            		}
            	}
            	
            	if((!is1593Active || !has1593OtherActives)&&!"1".equals(wideNetMoveSign)&&"0".equals(passTag))
            	{
                    result = checkTradeLimitMutexByActiveProduct(databus, ruleParam, saleTradeLimitData, isProductTagset10);
            	}
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_FQY_ACTIVE_PRODUCT.equals(limitType))
            {
                String campyType = databus.getString("CAMPN_TYPE");
                if (SaleActiveUtil.isQyyx(campyType))
                {
                    result = checkTradeLimitMutexByFqyActiveProduct(databus, ruleParam, saleTradeLimitData);
                }
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_QY_ACTIVE_PRODUCT.equals(limitType))
            {
                String campyType = databus.getString("CAMPN_TYPE");
                if (!SaleActiveUtil.isQyyx(campyType))
                {
                    result = checkTradeLimitMutexByQyActiveProduct(databus, ruleParam, saleTradeLimitData);
                }
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_BRAND.equals(limitType))
            {
                result = checkTradeLimitMutexByBrand(databus, ruleParam, saleTradeLimitData);
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_GPON_DESTORY.equals(limitType))
            {
                result = checkTradeLimitMutexByGponDestory(databus, ruleParam, saleTradeLimitData);
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_PRODUCT.equals(limitType)&&!"1".equals(wideNetMoveSign))
            {
                result = checkTradeLimitMutexByUserProduct(databus, ruleParam, saleTradeLimitData);
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_DISCNT_SPEC.equals(limitType)&&!"1".equals(wideNetMoveSign))
            {
                result = checkTradeLimitMutexByUserDiscntSpec(databus, ruleParam, saleTradeLimitData);
            }
            //add by zhangxing3 forBUG20190326102924优化宽带1+活动办理问题
            
			else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_DISCNT_KDYJ.equals(limitType))
            {
                result = checkTradeLimitMutexByUserDiscntKDYJ(databus, ruleParam, saleTradeLimitData);
            }
			
            //add by zhangxing3 forBUG20190326102924优化宽带1+活动办理问题
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_DISCNT.equals(limitType)&&!"1".equals(wideNetMoveSign))
            {
                result = checkTradeLimitMutexByUserDiscnt(databus, ruleParam, saleTradeLimitData);
            }
            //add by zhangxing3 for REQ201903050005订咪咕会员享三个月话费优惠，流量月月送营销活动
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_PLATSVC_BIZ_TYPE_CODE.equals(limitType))
            {
                result = checkTradeLimitMutexPlatSvcBizTypeCode(databus, ruleParam, saleTradeLimitData);
            }
            //add by zhangxing3 for REQ201903050005订咪咕会员享三个月话费优惠，流量月月送营销活动
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_ACTIVE_PRODUCT_610.equals(limitType))
            {
                result = checkTradeLimitMutexByActiveProduct610(databus, ruleParam, saleTradeLimitData);
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_DISCNT_HIS.equals(limitType)&&!"1".equals(wideNetMoveSign))
            {
                result = checkTradeLimitMutexByUserDiscntHis(databus, ruleParam, saleTradeLimitData);
            }
            else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_DISCNT_KD.equals(limitType)&&!"1".equals(wideNetMoveSign) && !"1".equals(SpecialSaleFlag))// up 20170511
            {
                result = checkTradeLimitMutexByUserDiscnt641(databus, ruleParam, saleTradeLimitData);
            }else if (SaleActiveBreConst.MUTEX_LIMIT_TYPE_SALEACTIVE_HIS.equals(limitType))
            {
            	String catalogId=saleTradeLimitData.getString("CATALOG_ID");
            	String packageId=databus.getString("PACKAGE_ID");
                result = checkTradeLimitMutexSaleActive(databus, ruleParam, saleTradeLimitData, catalogId, packageId);
            }else if(SaleActiveBreConst.MUTEX_LIMIT_TYPE_WIDNET_TYPE.equals(limitType)){
                result = checkTradeLimitMutexWidnetType(databus, ruleParam, saleTradeLimitData);
            }
        }
        return result;
    }
    
    /**
     * 用户XX天内办理过XX活动，不能再次办理
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexSaleActive(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData, String catalogId, String packageId) throws Exception
    {
    	String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE");
    	String userId = databus.getString("USER_ID");
    	IDataset userSaleActives = UserSaleActiveInfoQry.getUserSaleactiveByDay(userId,limitUserDiscntCode,catalogId,packageId);
    	if (IDataUtil.isNotEmpty(userSaleActives))
    	{
    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024180, saleTradeLimitData.getString("LIMIT_MSG"));
    	}
        return true;
    }
    
    /**
     * 用户的宽带号码下有XX优惠，不能办理当前活动
     */
    private boolean checkTradeLimitMutexByUserDiscnt641(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
    	String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE");
    	String serialNumber = databus.getString("SERIAL_NUMBER");
    	IDataset kdUserInfos = UserInfoQry.getUserInfoBySn("KD_"+serialNumber, "0");//获取KD_宽带号码的user_id
    	
    	//如果存在宽带用户
    	if (IDataUtil.isNotEmpty(kdUserInfos))
    	{ 
        	String kdUserId = kdUserInfos.getData(0).getString("USER_ID");
        	IDataset kdUserDiscntInfos = UserDiscntInfoQry.getUserByDiscntCode(kdUserId, limitUserDiscntCode);
        	if (IDataUtil.isNotEmpty(kdUserDiscntInfos))
        	{
        		boolean flag = true ;
        		//判断是否为产品变更配置的包年优惠，如果为包年优惠，则最后一个月可以办理
        		//查询TD_S_COMMPARA 表 532 601 参数 判断包年优惠
        		IDataset commpara532 = CommparaInfoQry.getCommparaInfoByCode("CSM","532","601",limitUserDiscntCode,"0898");
        		if(commpara532 != null && commpara532.size() > 0)
        		{
        			//判断是否最后一个月或预约日期大于结束日期也允许办理
        			flag = false;
        			for(int i = 0 ; i < kdUserDiscntInfos.size() ; i++)
        			{
        				String endDate = kdUserDiscntInfos.getData(i).getString("END_DATE");
        				
        				String lastDayOfThisMonth = SysDateMgr.getLastDateThisMonth();
        				if(SysDateMgr.compareTo(endDate, lastDayOfThisMonth) > 0 )
        				{
        					flag = true;
        				}
        			}
        			
        			String bookDate = databus.getString("BOOKING_DATE",""); //预约生效时间，如果大于结束时间，也允许办理
    				if(bookDate != null && !"".equals(bookDate))
    				{
    					if(SysDateMgr.compareTo(bookDate, kdUserDiscntInfos.getData(0).getString("END_DATE")) > 0)
            			{
    						flag = false;
            			}
    				}
        		}
        		if(flag)
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024072, saleTradeLimitData.getString("LIMIT_MSG"));
        	}
    	}
    	
    	//是否是宽带未完工用户
    	IDataset kdUserUnFinishInfos = BreQry.noFinishTradeInfos(serialNumber);//宽带开户未完工工单信息
    	if (IDataUtil.isNotEmpty(kdUserUnFinishInfos))
    	{
    		String kdTradeId = kdUserUnFinishInfos.getData(0).getString("TRADE_ID");
    		IDataset kdUserDiscntInfos = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(kdTradeId, limitUserDiscntCode);
        	if (IDataUtil.isNotEmpty(kdUserDiscntInfos))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024072, saleTradeLimitData.getString("LIMIT_MSG"));
        	}
    	}
    	
        return true;
    }
    
    /**
     * 用户有XX优惠，xx优惠所在自然年内不能办理当前活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByUserDiscntHis(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
    	String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE");
    	String userId = databus.getString("USER_ID");
    	IDataset userSaleActives = UserDiscntInfoQry.getUserDiscntByYear(userId, limitUserDiscntCode);
    	if (IDataUtil.isNotEmpty(userSaleActives))
    	{
    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024071, saleTradeLimitData.getString("LIMIT_MSG"));
    	}
        return true;
    }
    
    
    /**
     * 用户近6个月内未订购过咪咕视频钻石会员/咪咕阅读至尊全站包，才能办理该型营销活动；
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexPlatSvcBizTypeCode(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
    	IDataset userPlatSvcs = UserPlatSvcInfoQry.qryUserPlatByUserServiceIdMonthNew(databus.getString("USER_ID"),saleTradeLimitData.getString("LIMIT_CODE"),"6");
        if (IDataUtil.isNotEmpty(userPlatSvcs))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024063, saleTradeLimitData.getString("LIMIT_MSG"));
        }
        return true;
    }
    
    /**
     * 活动互斥校验，用户存在xx活动产品（无论是否已经失效），不能办理当前活动！
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByActiveProduct610(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
    	IDataset userSaleActives = SaleActiveInfoQry.getUserAllSaleActiveInfo(databus.getString("USER_ID"),saleTradeLimitData.getString("LIMIT_CODE"));
    	if (IDataUtil.isNotEmpty(userSaleActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024070, saleTradeLimitData.getString("LIMIT_MSG"));
        }
    	return true;
    }

    /**
     * 活动互斥校验，用户存在xx活动产品，不能办理当前活动！
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByActiveProduct(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData, boolean isProductTagset10) throws Exception
    {
        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");

        if (IDataUtil.isEmpty(userSaleActives))
        {
        	return true;
        }
        
        String wideNetMoveSign = databus.getString("WIDENET_MOVE_SALEACTIVE_SIGN","0");
        String SpecialSaleFlag = databus.getString("SPECIAL_SALE_FLAG",""); //add 20170511
        
        if (!SaleActiveUtil.isQyyx(databus.getString("CAMPN_TYPE")))
        {
            IDataset sameActiveDataset = SaleActiveBreUtil.getActivesByProductId(userSaleActives, saleTradeLimitData.getString("LIMIT_CODE"));
            if (IDataUtil.isNotEmpty(sameActiveDataset))
            {
            	boolean errorFlag = true ;
            	//增加判断产品变更标记,如果是升档，且是活动变更活动则允许变更，或包年变活动，且是最后一个月也允许
            	String changeUpDownTag = databus.getString("CHANGE_UP_DOWN_TAG",""); //0 不变，1：升档,2:降档,3:产品变，速率不变
            	/**
            	 * 新增非签约类续约配置commpara=167。用于宽带包年营销活动续约
            	 * chenxy3
            	 * */
            	IDataset limit167NEW = CommparaInfoQry.getCommparaByCodeCode1("CSM", "167", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
            	if (IDataUtil.isNotEmpty(limit167NEW)&&"".equals(changeUpDownTag))
                {
                	changeUpDownTag="0";//【宽带产品变更】功能带过来的；营销活动办理为空。这里借该值做宽带包年活动续约，最后一个月可以办理。
                }
            	/**
            	 * 新增非签约类续约配置commpara=168,用于宽带1+续约
            	 * zhangxing
            	 * */
            	IDataset limit168 = CommparaInfoQry.getCommparaAllColByParser("CSM", "168", databus.getString("PRODUCT_ID"),"0898");
            	if (IDataUtil.isNotEmpty(limit168)&&"".equals(changeUpDownTag))
                {
                	changeUpDownTag="0";//营销活动办理为空。这里借该值做宽带1+活动续约，最后一个月可以办理。
                }
            	

            	//宽带资费的优化 新增非签约类配置commpara=1681,用于宽带1+

            	IDataset limit1681 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1681", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
            	if (IDataUtil.isNotEmpty(limit1681))
                {
            		IDataset hasPackageInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(databus.getString("USER_ID"), 
            				databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));

            		if (IDataUtil.isEmpty(hasPackageInfos))
            		{
            			String configPackageId=limit1681.getData(0).getString("PARA_CODE2", "");

                		for(int i = 0 ; i < sameActiveDataset.size() ; i++ )
            			{
            				String packageId = sameActiveDataset.getData(i).getString("PACKAGE_ID","");
            				if(configPackageId.indexOf("|"+packageId+"|") > -1 )
            				{
            					errorFlag = false ;
            				}
            			}
            		}
            		
                }
            	//宽带资费的优化
            	
            	if("1".equals(changeUpDownTag) || "2".equals(changeUpDownTag) || "3".equals(changeUpDownTag) || "0".equals(changeUpDownTag))
            	{
            		if("1".equals(changeUpDownTag))
            			errorFlag = false ;
            		else
            		{
            			//取活动的结束时间，如果是最后一个月，则允许变更
            			for(int i = 0 ; i < sameActiveDataset.size() ; i++ )
            			{

            				String endDate = sameActiveDataset.getData(i).getString("END_DATE");
            				String packageId = sameActiveDataset.getData(i).getString("PACKAGE_ID");
//            				IDataset pkgExtDataSet = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE","0898"));
            				IDataset pkgExtDataSet = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
            				String endEnableTag = pkgExtDataSet.getData(0).getString("END_ENABLE_TAG");

            				//3代后貌似END_ENABLE_TAG被替换为DISABLE_MODE了
            				if ( endEnableTag == null )
            				{
            					endEnableTag = pkgExtDataSet.getData(0).getString("DISABLE_MODE","");
            				}
            				if("0".equals(endEnableTag))
            				{
            					//如果是绝对时间，则取TD_S_COMMPARA ,181参数查询合约期限，重新计算结束时间
            					IDataset com181 = CommparaInfoQry.getCommparaInfoByCode("CSM", "181", "-1", packageId, "0898");
            					String months = "12";//默认12个月
            		        	if (IDataUtil.isNotEmpty(com181))
            		        	{
            		        		months = com181.getData(0).getString("PARA_CODE4","12");
            		        	}
            		        	String startDate = sameActiveDataset.getData(i).getString("START_DATE");
            		        	
            		        	
//            		        	endDate = SysDateMgr.endDate(startDate, "1", pkgExtDataSet.getData(0).getString("END_ABSOLUTE_DATE"), months, "3");
            		        	endDate = SysDateMgr.addDays(SysDateMgr.addMonths(startDate, Integer.valueOf(months)),-1) + SysDateMgr.getEndTime235959();
            				}
            				
            				String lastDayOfThisMonth = SysDateMgr.getLastDateThisMonth();
            				// edit by zhangxing3 for QR-20180425-03用户宽带拆机无法重新办理问题
            				//if(endDate.substring(0, 7).equals(lastDayOfThisMonth.substring(0, 7)))
            				if(SysDateMgr.compareTo(lastDayOfThisMonth, endDate) >= 0)
            				{
            					errorFlag = false ;
            				}
            				
            				String bookDate = databus.getString("BOOKING_DATE",""); //预约生效时间，如果大于结束时间，也允许办理
            				if(bookDate != null && !"".equals(bookDate))
            				{
            					if(SysDateMgr.compareTo(bookDate, endDate) > 0)
                    			{
            						errorFlag = false ;
                    			}
            				}
            			}
            		}
            	}
            	
            	//BUG20170711092611  魔百和活动无法续约问题 
            	IData modata = new DataMap();
            	modata.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
            	modata.put("PACKAGE_ID", databus.getString("PACKAGE_ID"));
            	modata.put("USER_ID", databus.getString("USER_ID"));
            	if(IDataUtil.isNotEmpty(UserSaleActiveInfoQry.querySaleActiveByUserId(modata)))
            	{
            		errorFlag = false;
            	}
            	
            	if("1".equals(wideNetMoveSign) || "1".equals(SpecialSaleFlag))  //up 20170511 宽带产品变更时办理特定活动也不限制时间。
            		errorFlag = false;
            	if(errorFlag)
            	{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024053, saleTradeLimitData.getString("LIMIT_MSG"));
            	}
            }

            IDataset userBookSaleActives = SaleActiveInfoQry.getUserBookSaleActive(databus.getString("USER_ID"), saleTradeLimitData.getString("LIMIT_CODE"), SaleActiveConst.ACTIVE_BOOK_TYPE_WIDENET);

            if (IDataUtil.isNotEmpty(userBookSaleActives) && !databus.getString("PRODUCT_ID").equals(userBookSaleActives.getData(0).getString("PRODUCT_ID_B")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024053, "您已预约登记[" + saleTradeLimitData.getString("LIMIT_CODE") + "]活动, 不能再办理该类型活动!");
            }
        }
        else
        {
            boolean isErrorLimit = true;

            IDataset limit166 = CommparaInfoQry.getCommparaAllColByParser("CSM", "166", databus.getString("PRODUCT_ID"), "0898");
            if (IDataUtil.isNotEmpty(limit166))
            {
                String limitMoth = limit166.getData(0).getString("PARA_CODE2", "0");
                String limitProductId = limit166.getData(0).getString("PARA_CODE1", "0");
                IDataset sameActiveDataset = SaleActiveBreUtil.getActivesByProductId(userSaleActives, limitProductId);
                
                if(IDataUtil.isNotEmpty(sameActiveDataset))
                {
                	IData maxEndDateActive = SaleActiveBreUtil.getMaxEndDateActiveFromUserSaleActive(sameActiveDataset);
                    int intervalMoths = SaleActiveBreUtil.getIntervalMoths(SysDateMgr.getSysTime(), maxEndDateActive.getString("END_DATE"));

                    if (intervalMoths < Integer.parseInt(limitMoth))
                    {
                        isErrorLimit = false;
                    }
                }
            }

            IDataset limit1007 = CommparaInfoQry.getCommparaAllColByParser("CSM", "1007", databus.getString("PRODUCT_ID"), "0898");
            if (IDataUtil.isNotEmpty(limit1007))
            {
                IDataset sameActiveDataset = SaleActiveBreUtil.getActivesByProductId(userSaleActives, databus.getString("PRODUCT_ID"));
                if (IDataUtil.isNotEmpty(sameActiveDataset))
                {
                    isErrorLimit = false;
                }
            }

            IDataset limitActiveDataset = SaleActiveBreUtil.getActivesByProductId(userSaleActives, saleTradeLimitData.getString("LIMIT_CODE"));

            if (isErrorLimit && IDataUtil.isNotEmpty(limitActiveDataset))
            {
            	boolean errorFlag = true ;
            	//增加判断产品变更标记,如果是升档，且是活动变更活动则允许变更，或包年变活动，且是最后一个月也允许
            	String changeUpDownTag = databus.getString("CHANGE_UP_DOWN_TAG",""); //0 不变，1：升档,2:降档,3:产品变，速率不变
            	
            	/**
            	 * 新增非签约类续约配置commpara=168,用于最后一个月可以办理
            	 * zhangxing
            	 * */
            	IDataset limit168 = CommparaInfoQry.getCommparaAllColByParser("CSM", "168", databus.getString("PRODUCT_ID"),"0898");
            	if (IDataUtil.isNotEmpty(limit168)&&"".equals(changeUpDownTag))
                {
                	changeUpDownTag="0";//营销活动办理为空。这里借该值做，最后一个月可以办理。
                }
            	
            	if("1".equals(changeUpDownTag) || "2".equals(changeUpDownTag) || "3".equals(changeUpDownTag) || "0".equals(changeUpDownTag))
            	{
            		if("1".equals(changeUpDownTag))
            			errorFlag = false ;
            		else
            		{
            			//取活动的结束时间，如果是最后一个月，则允许变更
            			for(int i = 0 ; i < limitActiveDataset.size() ; i++ )
            			{
            				String endDate = limitActiveDataset.getData(i).getString("END_DATE");
            				String packageId = limitActiveDataset.getData(i).getString("PACKAGE_ID");
//            				IDataset pkgExtDataSet = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE","0898"));
            				IDataset pkgExtDataSet = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
            				String endEnableTag = pkgExtDataSet.getData(0).getString("END_ENABLE_TAG");
            				//3代后貌似END_ENABLE_TAG被替换为DISABLE_MODE了
            				if ( endEnableTag == null )
            				{
            					endEnableTag = pkgExtDataSet.getData(0).getString("DISABLE_MODE","");
            				}
            				if("0".equals(endEnableTag))
            				{
            					//如果是绝对时间，则取TD_S_COMMPARA ,181参数查询合约期限，重新计算结束时间
            					IDataset com181 = CommparaInfoQry.getCommparaInfoByCode("CSM", "181", "-1", packageId, "0898");
            					String months = "12";//默认12个月
            		        	if (IDataUtil.isNotEmpty(com181))
            		        	{
            		        		months = com181.getData(0).getString("PARA_CODE4","12");
            		        	}
            		        	String startDate = limitActiveDataset.getData(i).getString("START_DATE");
//            		        	endDate = SysDateMgr.endDate(startDate, "1", pkgExtDataSet.getData(0).getString("END_ABSOLUTE_DATE"), months, "3");
            		        	
            		        	endDate = SysDateMgr.addDays(SysDateMgr.addMonths(startDate, Integer.valueOf(months)),-1) + SysDateMgr.getEndTime235959();
            				}
            				
            				String lastDayOfThisMonth = SysDateMgr.getLastDateThisMonth();
            				// edit by zhangxing3 for QR-20180425-03用户宽带拆机无法重新办理问题
                			//if(endDate.substring(0, 7).equals(lastDayOfThisMonth.substring(0, 7)))
                			if(SysDateMgr.compareTo(lastDayOfThisMonth, endDate) >= 0)
            				{
            					errorFlag = false ;
            				}
            				
            				String bookDate = databus.getString("BOOKING_DATE",""); //预约生效时间，如果大于结束时间，也允许办理
            				if(bookDate != null && !"".equals(bookDate))
            				{
            					if(SysDateMgr.compareTo(bookDate, endDate) > 0)
                    			{
            						errorFlag = false ;
                    			}
            				}
            			}
            		}
            	}
            	
            	//BUG20170711092611  魔百和活动无法续约问题 
            	IData modata = new DataMap();
            	modata.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
            	modata.put("PACKAGE_ID", databus.getString("PACKAGE_ID"));
            	modata.put("USER_ID", databus.getString("USER_ID"));
            	if(IDataUtil.isNotEmpty(UserSaleActiveInfoQry.querySaleActiveByUserId(modata)))
            	{
            		errorFlag = false;
            	}
            	
            	if("1".equals(wideNetMoveSign)) 
            		errorFlag = false;
            	
            	if(errorFlag)
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14324053, saleTradeLimitData.getString("LIMIT_MSG"));
            }
        }

        return true;
    }
    
    
    private boolean checkSameActiveThreeMonths(IData databus, BreRuleParam ruleParam, boolean isProductTagset10)throws Exception
    {
    	IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
    	
    	if (IDataUtil.isEmpty(userSaleActives))
        {
        	return true;
        }
    	
    	if (isProductTagset10) // 判断自身3个月
        {
            IDataset sameActiveDataset = SaleActiveBreUtil.getActivesByProductId(userSaleActives, databus.getString("PRODUCT_ID"));

            if (IDataUtil.isNotEmpty(sameActiveDataset))
            {
                IData maxStartDateActive = SaleActiveBreUtil.getMaxStartDateActiveFromUserSaleActive(sameActiveDataset);
                int intervalMoths = SaleActiveBreUtil.getIntervalMoths(SysDateMgr.getSysTime(), maxStartDateActive.getString("START_DATE"));

                String userProductId = sameActiveDataset.getData(0).getString("PRODUCT_ID");

                if (intervalMoths < 3 && userProductId.equals(databus.getString("PRODUCT_ID")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024053, "已经参加过营销活动，不能办理该业务！");
                }
            }

        }
    	return true;
    }

    /**
     * 用户是XX品牌，不能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByBrand(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        String userBrand = databus.getString("BRAND_CODE");

        if (saleTradeLimitData.getString("LIMIT_CODE").equals(userBrand))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024056, saleTradeLimitData.getString("LIMIT_MSG"));
        }

        return true;
    }
    
    /**
     * 用户已办理GPON宽带预约拆机，不能办理当前活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByGponDestory(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
    	String kdSerialNumber = "KD_"+ databus.getString("SERIAL_NUMBER");//获取宽带号码
    	IDataset userInfos = UserInfoQry.getUserInfoBySn(kdSerialNumber,"0");//获取宽带号码的信息
    	if(IDataUtil.isNotEmpty(userInfos) && userInfos.size() > 0)
    	{
    		String kdUserId = userInfos.getData(0).getString("USER_ID", "");//获取宽带号码的USER_ID
        	
        	//判断是否存在GPON宽带预约拆机
        	IData tempParam = new DataMap();
        	tempParam.put("USER_ID", kdUserId);
            IDataset destoryInfos = Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_GPONDESTORYINFO_BY_USERID", tempParam);
            
            //如果存在预约拆机记录，则拦截
            if(IDataUtil.isNotEmpty(destoryInfos) && destoryInfos.size() > 0)
            {
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 16012101, saleTradeLimitData.getString("LIMIT_MSG"));
            }
    	}
    	return true;
    }

    /**
     * 用户办理签约类活动，是否存在非签约的互斥活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByFqyActiveProduct(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");

        userSaleActives = SaleActiveBreUtil.getNoQyyxActives(userSaleActives);

        for (int index = 0, size = userSaleActives.size(); index < size; index++)
        {
            IData userSaleActive = userSaleActives.getData(index);
            String userSaleProductId = userSaleActive.getString("PRODUCT_ID");
            String limitUserSaleProductId = saleTradeLimitData.getString("LIMIT_CODE");

            if (userSaleProductId.equals(limitUserSaleProductId))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024054, saleTradeLimitData.getString("LIMIT_MSG"));
            }
        }
        // TODO 显示产品名称
        IDataset userBookSaleActives = SaleActiveInfoQry.getUserBookSaleActive(databus.getString("USER_ID"), saleTradeLimitData.getString("LIMIT_CODE"), SaleActiveConst.ACTIVE_BOOK_TYPE_WIDENET);

        if (IDataUtil.isNotEmpty(userBookSaleActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024055, "用户存在" + saleTradeLimitData.getString("LIMIT_CODE") + "的预登记活动，不能办理该活动！");
        }

        return true;
    }

    /**
     * 用户办理非签约活动，是否存在签约类的活动互斥
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByQyActiveProduct(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");

        userSaleActives = SaleActiveBreUtil.getQyyxActives(userSaleActives);

        for (int index = 0, size = userSaleActives.size(); index < size; index++)
        {
            IData userSaleActive = userSaleActives.getData(index);
            String userSaleProductId = userSaleActive.getString("PRODUCT_ID");
            String limitUserSaleProductId = saleTradeLimitData.getString("LIMIT_CODE");

            if (userSaleProductId.equals(limitUserSaleProductId))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024054, saleTradeLimitData.getString("LIMIT_MSG"));
            }
        }
        return true;
    }

    /**
     * 用户有XX优惠，不能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByUserDiscnt(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        for (int index = 0, size = userDiscnts.size(); index < size; index++)
        {
            IData userDiscnt = userDiscnts.getData(index);
            String userDiscntCode = userDiscnt.getString("DISCNT_CODE");
            String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE");

            if (userDiscntCode.equals(limitUserDiscntCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024058, saleTradeLimitData.getString("LIMIT_MSG"));
                break;
            }
        }
        return true;
    }

    /**
     * 用户是XX产品，不能办理该型营销活动
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexByUserProduct(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        String userProductId = databus.getString("USER_PRODUCT_ID");
        if (saleTradeLimitData.getString("LIMIT_CODE").equals(userProductId))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024057, saleTradeLimitData.getString("LIMIT_MSG"));
        }

        return true;
    }
    
    /**
     * 用户有当前生效或下月生效的XX优惠，不能办理当前活动
     * @author zhangxing3 for REQ201810290024宽带开户界面增加手机号码套餐的判断
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    
    private boolean checkTradeLimitMutexByUserDiscntSpec(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE","");
    
        String noSaleTradeLimit642 = databus.getString("NO_SALE_TRADE_LIMIT_642", "");
        if("TRUE".equals(noSaleTradeLimit642))
        {
        	return true;
        }
        //System.out.println("===========checkTradeLimitMutexByUserDiscntSpec==============limitUserDiscntCode:"+limitUserDiscntCode);
        for (int index = 0, size = userDiscnts.size(); index < size; index++)
        {
            IData userDiscnt = userDiscnts.getData(index);
            //System.out.println("===========checkTradeLimitMutexByUserDiscntSpec==============userDiscnts:"+userDiscnts);
            String userDiscntCode = userDiscnt.getString("DISCNT_CODE");
            String startDate = userDiscnt.getString("START_DATE");
            String endDate = userDiscnt.getString("END_DATE");

            String nextMonthLastDate = SysDateMgr.getNextMonthLastDate();
            //System.out.println("===========checkTradeLimitMutexByUserDiscntSpec==============startDate:"+startDate);
            //System.out.println("===========checkTradeLimitMutexByUserDiscntSpec==============nextMonthLastDate:"+nextMonthLastDate);

            int t = nextMonthLastDate.compareTo(startDate);
            int s = endDate.compareTo(SysDateMgr.getLastDateThisMonth());
            //System.out.println("===========checkTradeLimitMutexByUserDiscntSpec==============t:"+t+",s:"+s);
            if (limitUserDiscntCode.indexOf("|"+userDiscntCode+"|") > -1 && t >= 0 && s > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024058, saleTradeLimitData.getString("LIMIT_MSG"));
                break;
            }
        }
        return true;
    }
    
    
    /**
     * 用户有当前生效且下月生效的XX优惠，不能办理当前活动（该优惠本月底结束时，还是可以办理该活动的。）
     * @author zhangxing3 for BUG20190326102924优化宽带1+活动办理问题
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
     
    private boolean checkTradeLimitMutexByUserDiscntKDYJ(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception
    {
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        
        //add by zhangxing3 for 宽带资费的优化
        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        if (IDataUtil.isNotEmpty(userSaleActives))
        {
        	for (int i = 0; i < userSaleActives.size(); i++)
            {
        		String usePackageId = userSaleActives.getData(i).getString("PACKAGE_ID", "");
        		IDataset limit1681 = CommparaInfoQry.getEnableCommparaInfoByCode12New("CSM", "1681", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"),usePackageId,"0898");
            	if (IDataUtil.isNotEmpty(limit1681))
                {
            		return true;
                }
            }
        }
        //add by zhangxing3 for 宽带资费的优化
        String limitUserDiscntCode = saleTradeLimitData.getString("LIMIT_CODE","");
        for (int index = 0, size = userDiscnts.size(); index < size; index++)
        {
            IData userDiscnt = userDiscnts.getData(index);
            String userDiscntCode = userDiscnt.getString("DISCNT_CODE");
            String endDate = userDiscnt.getString("END_DATE");

            String nextMonthLastDate = SysDateMgr.getNextMonthLastDate();

            int s = endDate.compareTo(SysDateMgr.getLastDateThisMonth());
            if (limitUserDiscntCode.indexOf("|"+userDiscntCode+"|") > -1 && s > 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024058, saleTradeLimitData.getString("LIMIT_MSG"));
                break;
            }
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private IDataset getSaleTradeLimit(String campnType, String productId, String packageId) throws Exception
    {
//        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(SaleTradeLimitCache.class);
//        List<SaleTradeLimitObj> tradeLimitList = (List<SaleTradeLimitObj>) cache.get(SaleTradeLimitObj.SLAE_TRADE_LIMIT_CHCHE_KEY);
//        IDataset returnTradeLimitSet = new DatasetList();
//
//        if (CollectionUtils.isNotEmpty(tradeLimitList))
//        {
//            SaleTradeLimitObj saleTradeLimitObj = null;
//            for (int index = 0, size = tradeLimitList.size(); index < size; index++)
//            {
//                saleTradeLimitObj = tradeLimitList.get(index);
//
//                if ((campnType.equals(saleTradeLimitObj.getCampnType()) || "-1".equals(saleTradeLimitObj.getCampnType())) && (productId.equals(saleTradeLimitObj.getProductId()) || "-1".equals(saleTradeLimitObj.getProductId()))
//                        && (packageId.equals(saleTradeLimitObj.getPackageId()) || "-1".equals(saleTradeLimitObj.getPackageId())))
//                {
//                    returnTradeLimitSet.add(saleTradeLimitObj.toData());
//                }
//
//            }
//        }
        
    	
    	OfferCfg offerCfg = OfferCfg.getInstance(packageId, "K");
        IDataset saleTradeLimits = UpcCall.qrySaleOfferTradeLimits(productId, offerCfg.getOfferId());
        IDataset returnTradeLimitSet = new DatasetList();
        
        if (IDataUtil.isNotEmpty(saleTradeLimits))
        {
            IData saleTradeLimitObj = null;
            for (int index = 0, size = saleTradeLimits.size(); index < size; index++)
            {
                saleTradeLimitObj = saleTradeLimits.getData(index);
                returnTradeLimitSet.add(saleTradeLimitObj);
            }
        }

        return returnTradeLimitSet;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckSaleTradeLimit() >>>>>>>>>>>>>>>>>>");
        }

        String campnType = databus.getString("CAMPN_TYPE");
        String productId = databus.getString("PRODUCT_ID");
        String packageId = databus.getString("PACKAGE_ID");

        IDataset saleTradeLimitDataset = getSaleTradeLimit(campnType, productId, packageId);
        if (IDataUtil.isEmpty(saleTradeLimitDataset))
            return true;

        checkTradeLimitMutex(databus, ruleParam, saleTradeLimitDataset);
        checkTradeLimitDepend(databus, ruleParam, saleTradeLimitDataset);
        
        if (log.isDebugEnabled())
        {
            log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckSaleTradeLimit() >>>>>>>>>>>>>>>>>>");
        }
        return false;
    }
    
    /**
     * 用户有XX BIZ_TYPE_CODE 的平台服务，才能办理该型营销活动且存在配置1220；
     * 
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependPlatSvcBizTypeCode1220(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        IDataset userPlatSvcs = databus.getDataset("TF_F_USER_PLATSVC");
        if (IDataUtil.isEmpty(userPlatSvcs))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024063, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
            return true;
        }
        boolean isDependPlatSvc = false;
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int j = 0, s = userPlatSvcs.size(); j < s; j++)
            {
                IData userPlatSvc = userPlatSvcs.getData(j);
                String serviceId = userPlatSvc.getString("SERVICE_ID");
                IDataset svcData = UpcCall.qrySpInfoByOfferId(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
                String bizTypeCode = svcData.getData(0).getString("BIZ_TYPE_CODE");
                String bizSateCode = userPlatSvc.getString("BIZ_STATE_CODE");
                if (limitCode.equals(bizTypeCode) && ("A".equals(bizSateCode) || "N".equals(bizSateCode) || "L".equals(bizSateCode)))
                {
                	IData data = new DataMap();
                	data.put("SUBSYS_CODE", "CSM");
                	data.put("PARAM_ATTR", "1220");
                	data.put("PARAM_CODE", "1220");
                	data.put("PARA_CODE1", serviceId);
                	data.put("PARA_CODE2", databus.getString("PRODUCT_ID"));
                	data.put("PARA_CODE3", limitCode);
                	data.put("EPARCHY_CODE", "0898");
                    IDataset limit1220 = CommparaInfoQry.getCommparaInfoBy1To6(data);
                    log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> serviceId >>>>>>>>>>>>>>>>>>"+serviceId);
                    if(IDataUtil.isEmpty(limit1220))
                    {
                    	continue;
                    }else{
                    	if("1".equals(limit1220.getData(0).getString("PARA_CODE4","")))
                    	{
                    		String paraCode5 = limit1220.getData(0).getString("PARA_CODE5","");
                    		String paraCode6 = limit1220.getData(0).getString("PARA_CODE6","");

                    		IDataset platAttr = UserPlatSvcInfoQry.getPlatSvcAttrByUserIdSIdAttr(databus.getString("USER_ID"),serviceId);
                    		if(IDataUtil.isEmpty(platAttr))
                    		{
                    			continue;
                    		}
                    	}
                    }

                    isDependPlatSvc = true;
                    break;
                }
            }
        }
        if (!isDependPlatSvc)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024064, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }

        return true;
    }

    /**
     * REQ201704140015_关于和路通业务营销活动开发需求
     * <br/>
     *  必须为一下几种套餐才能办理该营销活动(配置)：
     * 	套餐编码：20175501 和路通32元功能费套餐：32元/月，赠送1GB通用流量，超出后按0.29元/M收取；
		套餐编码：20175502 和路通48元功能费套餐：48元/月，赠送2GB通用流量，超出后按0.29元/M收取；
		套餐编码：20175503 和路通65元功能费套餐：65元/月，赠送3GB通用流量，超出后按0.29元/M收取；
		套餐编码：20175504 和路通80元功能费套餐：80元/月，赠送4GB通用流量，超出后按0.29元/M收取；
		套餐编码：20175505 和路通112元功能费套餐：112元/月，赠送6GB通用流量，超出后按0.29元/M收取。
     * @author zhuoyingzhi
     * @date 20170513
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitDataSet
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDependByUserDiscnt20170513(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception
    {
        boolean isDependDiscnts = false;
        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
        log.debug(" >>>>>>checkTradeLimitDependByUserDiscnt20170513>>>>>>> userDiscnts >>>>>>>>>>>>>"+userDiscnts);
        if (IDataUtil.isEmpty(userDiscnts))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2017051311, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
            return true;
        }
        
        //td_b_sale_trade_limit配置中的信息
        for (int index = 0, size = saleTradeLimitDataSet.size(); index < size; index++)
        {
            IData saleTradeLimitData = saleTradeLimitDataSet.getData(index);
            String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
            for (int i = 0; i < userDiscnts.size(); i++)
            {
                IData userDiscnt = userDiscnts.getData(i);
                //用户的优惠套餐
                String userDiscntCode = userDiscnt.getString("DISCNT_CODE");
                log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> userDiscntCode >>>>>>>>>>>>>>>>>>"+userDiscntCode);
                if(limitCode.equals(userDiscntCode)){
                	//只要有一个套餐优惠满足就可以办理该营销活动
                	isDependDiscnts=true;
                	break;
                }
            }
            if(isDependDiscnts){
            	break;
            }
        }
        log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> isDependDiscnts >>>>>>>>>>>>>>>>>>"+isDependDiscnts);
        if(!isDependDiscnts){
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2017051312, saleTradeLimitDataSet.getData(0).getString("LIMIT_MSG"));
        }
        return true;
    }

    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 start
    /**
     * 155中活动在9155中配置指定活动,且用户已办理活动包含指定活动,则可以顺延生效办理该活动,此方法为此类规则顺延生效限定月份校验
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitDataSet
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitDepend9155(IData databus, BreRuleParam ruleParam, IDataset saleTradeLimitDataSet) throws Exception{

        String saleProductId=databus.getString("PRODUCT_ID");
        String salePackageId=databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");

        //查询用户已办理活动
        List<SaleActiveTradeData> userSaleActives= UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER")).getUserSaleActives();

        //获取用户办理的活动在9155配置中指定的,且为用户已办理的活动
        List<SaleActiveTradeData> userExists9155Actives=SaleActiveBreUtil.getUserExists9155Actives(userSaleActives,saleProductId,salePackageId,eparchyCode);

        //校验指定已办理活动最大结束时间是否在限定月份内,不在则进行拦截
        if((CollectionUtils.isNotEmpty(userExists9155Actives))){
            String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userExists9155Actives);
            String limitMoth = saleTradeLimitDataSet.getData(0).getString("LIMIT_CODE", "0");
            int monthInterval = SysDateMgr.monthInterval(SysDateMgr.getSysTime(), maxEndDate);
            if (monthInterval > Integer.parseInt(limitMoth))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20114, "用户已办理的活动不在可顺延期限内，不能办理本活动！");
            }
        }
        return true;
    }
    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 end

    /**
     * REQ202003180001 “共同战疫宽带助力”
     * @param databus
     * @param ruleParam
     * @param saleTradeLimitData
     * @return
     * @throws Exception
     */
    private boolean checkTradeLimitMutexWidnetType(IData databus, BreRuleParam ruleParam, IData saleTradeLimitData) throws Exception {
        String limitCode = saleTradeLimitData.getString("LIMIT_CODE");
        //是否有有效的宽带
        String kdSerialNumber = databus.getString("SERIAL_NUMBER");
        if (!kdSerialNumber.startsWith("KD")) {
            kdSerialNumber = "KD_" + kdSerialNumber;
        }
        //校园宽带不能受理
        IDataset wideNetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(kdSerialNumber);
        if(IDataUtil.isNotEmpty(wideNetInfos)){
            if (StringUtils.equals(limitCode, wideNetInfos.first().getString("RSRV_STR2"))) {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020032301, saleTradeLimitData.getString("LIMIT_MSG"));
            }
        }
        return true;
    }


}
