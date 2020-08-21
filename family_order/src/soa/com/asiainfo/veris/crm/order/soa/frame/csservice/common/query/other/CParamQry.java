/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

import java.util.Iterator;

/***
 * 
 */
public class CParamQry
{

    public static boolean existsUserDiscntNotime(String userId, String eparchyCode, String paraCode21) throws Exception
    {
        boolean flag = true;
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("EPARCHY_CODE", eparchyCode);
        cond.put("PARAM_CODE", paraCode21);
        flag = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsUserDiscntNotime", cond);

        return flag;
    }

    public static boolean existsUserRecomm(String userId, String elementId) throws Exception
    {
        boolean flag = true;
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("ELEMENT_ID", elementId);
        flag = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsUserRecomm", cond);

        return flag;
    }
    public static IDataset getAbnormalityUser(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsAbnormalityUser", data);
    }

    public static IDataset getAbnormalityUserA(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsAbnormalityUserA", data);
    }
    public static IDataset getCreditClassDiscntByUserId(String userId) throws Exception
    {
    	IData data = new DataMap();
        data.put("USER_ID", userId);

        return Dao.qryByCode("TD_S_CPARAM", "SEL_CREDITCLASSDISCNT_BY_USERID", data);
    }
    /**
     * 根据吉祥号码的号码规则查询
     * 
     * @param feeCodeRuleE
     *            资源号码表中查询出来的数据
     * @return
     * @throws Exception
     */
    public static IDataset getJXNumber(String feeCodeRuleE, String paramAttr) throws Exception
    {
        IData data = new DataMap();
        data.put("PARA_CODE2", feeCodeRuleE);
        data.put("PARAM_ATTR", paramAttr);
        return Dao.qryByCode("TD_S_CPARAM", "isJXNumber2", data);
    }
    public static IDataset getIsPurchaseUserD(String userId, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserD", data);
    }
    public static IDataset getIsPurchaseUserC(String userId, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserC", data);
    }
    public static IDataset getLimitActives(String userId, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PARAM_CODE", paramCode);
        //return Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES", data);
        //modify by zhangyc5 on 20160622
        IDataset activeInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES_NEW", data);

        if (IDataUtil.isNotEmpty(activeInfos))
        {
            for (int i =0; i < activeInfos.size(); i++)
            {
                IData activeInfo = activeInfos.getData(i);

                String startDate = activeInfo.getString("START_DATE");
                String endDate = activeInfo.getString("END_DATE");
                String productId = activeInfo.getString("PRODUCT_ID");
                String packageId = activeInfo.getString("PACKAGE_ID");

                String newEndDate = SaleActiveBreUtil.getEndDateByPid(startDate, endDate, paramCode, productId, packageId);

                activeInfo.put("END_DATE", newEndDate);

                int monthInterval = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getNowCyc(), SysDateMgr.decodeTimestamp(newEndDate, SysDateMgr.PATTERN_TIME_YYYYMM));

                if (monthInterval > 0)
                {
                    activeInfo.put("FLAG", "1");
                }
                else
                {
                    activeInfo.put("FLAG", "0");
                }
            }
        }

        return activeInfos;
    }

    public static IDataset getNewLimitActives(String userId, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PARAM_CODE", paramCode);
        //return Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES", data);
        //modify by zhangyc5 on 20160622
        IDataset activeInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES_NEW", data);
//        System.out.println("====activeInfos==="+activeInfos);
        if (IDataUtil.isNotEmpty(activeInfos)) {
            //经讨论，宽带1+活动，在有效期的展示，不在有效期的不展示
            for (Iterator<Object> iterator = activeInfos.iterator(); iterator.hasNext(); ) {
                IData activeInfo = (IData) iterator.next();
//                System.out.println("====activeInfos==activeInfo="+activeInfo);
                if (IDataUtil.isNotEmpty(activeInfo)) {
                    String startDate = activeInfo.getString("START_DATE");
                    String endDate = activeInfo.getString("END_DATE");
                    String productId = activeInfo.getString("PRODUCT_ID");
                    String productname = activeInfo.getString("PRODUCT_NAME");
                    String packageId = activeInfo.getString("PACKAGE_ID");

                    String newEndDate = SaleActiveBreUtil.getNewEndDateByPid(startDate, endDate, "41", productId, packageId);
//                    System.out.println("====activeInfos==newEndDate=" + newEndDate);
                    /**
                     * 69900926	约定消费38元三亚学院宽带套餐5折
                     69908001	宽带1+活动
                     69908012	宽带1+(魔百和)
                     69908015	宽带1+（含预存款）
                     99992832	宽带激活或留家赠送话费活动，赠送60元话费礼包（分三个月返还）
                     */
                    if ("69900926,69908001,69908012,69908015,99992832".contains(productId) && "expired".equals(newEndDate)) {
//                        System.out.println("====activeInfos==remove=");
                        iterator.remove();
                    } else {
//                        System.out.println("====activeInfos==END_DATE=" + newEndDate);
                        activeInfo.put("END_DATE", newEndDate);
                    }
                }
            }
        }

        return activeInfos;
    }
    
    
    
    public static IDataset getLimitActiveDiscnts(String userId, String paramCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PARAM_CODE", paramCode);
        
        IDataset activeInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES4", data);
        
        if (IDataUtil.isNotEmpty(activeInfos))
        {
            for (int i =0; i < activeInfos.size(); i++)
            {
                IData activeInfo = activeInfos.getData(i);
                
                String startDate = activeInfo.getString("START_DATE");
                String endDate = activeInfo.getString("END_DATE");
                String productId = activeInfo.getString("PRODUCT_ID");
                String packageId = activeInfo.getString("PACKAGE_ID");
                
                String newEndDate = SaleActiveBreUtil.getEndDateByPid(startDate, endDate, paramCode, productId, packageId);
                
                int monthInterval = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getNowCyc(), SysDateMgr.decodeTimestamp(newEndDate, SysDateMgr.PATTERN_TIME_YYYYMM));
                
                if (monthInterval > 0)
                {
                    activeInfo.put("FLAG", "1");
                }
                else
                {
                    activeInfo.put("FLAG", "0");
                }
            }
        }
        
        return activeInfos;
    }
    

    public static IDataset getPurchaseUserA(String userId, String productId, String processTag) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        data.put("PROCESS_TAG", processTag);
        return Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserA", data);
    }

    /**
     * @Function: getPurchaseUserB
     * @Description: 该用户处在营销活动有效期内，不允许携出!
     * @param userId
     * @param productId
     * @param processTag
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-23 上午10:52:40
     */
    public static IDataset getPurchaseUserB(String userId, String productId, String processTag) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        data.put("PROCESS_TAG", processTag);
        return Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserB", data);
    }

    public static IDataset getRealNameUser(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsRealNameUser", data);
    }

    /**
     * TODO SQL待优化
     * 
     * @param userId
     * @param realtionTypeCode
     * @return
     * @throws Exception
     */
    public static int getRelationMemberNumber(String userId, String realtionTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("RELATION_TYPE_CODE", realtionTypeCode);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isRelationMember", data);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count);
    }

    public static IDataset getUndoneTrade1(String userId, String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TD_S_CPARAM", "IsUndoneTrade1", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getUndoneTrade41(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsUndoneTrade41", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset getXieChuIng(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsXieChuIng", data);
    }
    public static IDataset getXieChuIngA(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsXieChuIngA", data);
    }
    public static IDataset getXieChuIngB(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_S_CPARAM", "IsXieChuIngB", data);
    }

    /**
     * 查询工号是否自有营业厅
     * 
     * @param tradeid
     * @param canceltag
     * @return
     * @throws Exception
     */
    public static IDataset getZBYYT(String staffId) throws Exception
    {
        IData data = new DataMap();
        data.put("STAFF_ID", staffId);
        return Dao.qryByCode("TD_S_CPARAM", "isZBYYT", data, Route.CONN_SYS);
    }

    public static boolean is898GroupMember(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "is898GroupMember", data);
        String count = dataset.getData(0).getString("CNT");

        return Integer.parseInt(count) > 0 ? true : false;
    }

    public static boolean isCommpara142GroupMember(String userId, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isCommpara142GroupMember", data);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    /**
     * 办理ADC产品时，判断集团客户资料是否同步
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static boolean IsCustAdcSyncTag12(String custId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsCustAdcSyncTag12", data, Route.CONN_CRM_CG);
        String count = dataset.getData(0).getString("RECORDCOUNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    /**
     * 办理MAS产品时，判断集团客户资料是否同步
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static boolean IsCustMasSyncTag14(String custId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "IsCustMasSyncTag14", data, Route.CONN_CRM_CG);
        String count = dataset.getData(0).getString("RECORDCOUNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    /**
     * TODO SQL待优化
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static boolean isSpecDiscntMainUser(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isSpecPayMainUser", data);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    /**
     * TODO SQL待优化
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static boolean isSpecOpenMainUser(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isSpecOpenMainUser", data);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    public static boolean isTargetGroupMember(String userId, String productId) throws Exception
    {
    	boolean result = false;
        IData data = new DataMap();
        data.put("USER_ID", userId);
        
        IDataset offers = UpcCall.qryOffersByCatalogId(productId);
        if(IDataUtil.isNotEmpty(offers))
        {
        	for(Object obj : offers)
        	{
        		IData offer = (IData) obj;
        		String offerId = offer.getString("OFFER_ID");
        		IData pkgInfo = UpcCall.queryTempChaByCond(offerId, "TD_B_PACKAGE_EXT");
        		String rsrvStr3 = pkgInfo.getString("RSRV_STR3");
        		
        		data.put("RSRV_STR3", rsrvStr3);
        		IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isTargetGroupUser", data);
        		String count = dataset.getData(0).getString("CNT");
                if(Integer.parseInt(count) > 0 )
                {
                	result = true;
                	break;
                }
        	}
        }
        
        return result;
    }

    public static boolean isTargetVpmnGroupMember(String userId, String productId) throws Exception
    {
    	boolean result = false;
        IData data = new DataMap();
        data.put("USER_ID", userId);
        
        IDataset offers = UpcCall.qryOffersByCatalogId(productId);
        if(IDataUtil.isNotEmpty(offers))
        {
        	for(Object obj : offers)
        	{
        		IData offer = (IData) obj;
        		String offerId = offer.getString("OFFER_ID");
        		IData pkgInfo = UpcCall.queryTempChaByCond(offerId, "TD_B_PACKAGE_EXT");
        		String rsrvStr4 = pkgInfo.getString("RSRV_STR4");
        		
        		data.put("RSRV_STR4", rsrvStr4);
        		IDataset dataset = Dao.qryByCode("TD_S_CPARAM", "isTargetVpmnGroupUser", data);
        		String count = dataset.getData(0).getString("CNT");
                if(Integer.parseInt(count) > 0 )
                {
                	result = true;
                	break;
                }
        	}
        }
        
        return result;
    }
    
    /**
     * @Function: getPurchaseUserB
     * @Description: 该用户处在营销活动有效期内，不允许携出!
     * @param userId
     * @param productId
     * @param processTag
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-23 上午10:52:40
     */
    public static IDataset getPurchaseUserBNew(String userId, String productId, String processTag, String starLevel) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        data.put("PROCESS_TAG", processTag);
        data.put("STAR_LEVEL", starLevel);

        IDataset activeInfos = Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserBNEW", data);
//        System.out.println("====activeInfos==="+activeInfos);
        if (IDataUtil.isNotEmpty(activeInfos)) {
            //经讨论，宽带1+活动，在有效期的展示，不在有效期的不展示
            for (Iterator<Object> iterator = activeInfos.iterator(); iterator.hasNext(); ) {
                IData activeInfo = (IData) iterator.next();
//                System.out.println("====activeInfos==activeInfo="+activeInfo);
                if (IDataUtil.isNotEmpty(activeInfo)) {
                    String startDate = activeInfo.getString("START_DATE");
                    String endDate = activeInfo.getString("END_DATE");
                    productId = activeInfo.getString("PRODUCT_ID");
                    String productname = activeInfo.getString("PRODUCT_NAME");
                    String packageId = activeInfo.getString("PACKAGE_ID");

                    String newEndDate = SaleActiveBreUtil.getNewEndDateByPid(startDate, endDate, "41", productId, packageId);
//                    System.out.println("====activeInfos==newEndDate=" + newEndDate);
                    /**
                     * 69900926	约定消费38元三亚学院宽带套餐5折
                     69908001	宽带1+活动
                     69908012	宽带1+(魔百和)
                     69908015	宽带1+（含预存款）
                     99992832	宽带激活或留家赠送话费活动，赠送60元话费礼包（分三个月返还）
                     */
                    if ("69900926,69908001,69908012,69908015,99992832".contains(productId) && "expired".equals(newEndDate)) {
//                        System.out.println("====activeInfos==remove=");
                        iterator.remove();
                    } else {
//                        System.out.println("====activeInfos==END_DATE=" + newEndDate);
                        activeInfo.put("END_DATE", newEndDate);
                    }
                }
            }
        }

        return activeInfos;
    }
    
    public static IDataset getPurchaseUserANew(String userId, String productId, String processTag, String starLevel) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        data.put("PROCESS_TAG", processTag);
        data.put("STAR_LEVEL", starLevel);

        IDataset activeInfos = Dao.qryByCode("TD_S_CPARAM", "IsPurchaseUserANEW", data);

//        System.out.println("====activeInfos==="+activeInfos);
        if (IDataUtil.isNotEmpty(activeInfos)) {
            //经讨论，宽带1+活动，在有效期的展示，不在有效期的不展示
            for (Iterator<Object> iterator = activeInfos.iterator(); iterator.hasNext(); ) {
                IData activeInfo = (IData) iterator.next();
//                System.out.println("====activeInfos==activeInfo="+activeInfo);
                if (IDataUtil.isNotEmpty(activeInfo)) {
                    String startDate = activeInfo.getString("START_DATE");
                    String endDate = activeInfo.getString("END_DATE");
                    productId = activeInfo.getString("PRODUCT_ID");
                    String productname = activeInfo.getString("PRODUCT_NAME");
                    String packageId = activeInfo.getString("PACKAGE_ID");

                    String newEndDate = SaleActiveBreUtil.getNewEndDateByPid(startDate, endDate, "41", productId, packageId);
//                    System.out.println("====activeInfos==newEndDate=" + newEndDate);
                    /**
                     * 69900926	约定消费38元三亚学院宽带套餐5折
                     69908001	宽带1+活动
                     69908012	宽带1+(魔百和)
                     69908015	宽带1+（含预存款）
                     99992832	宽带激活或留家赠送话费活动，赠送60元话费礼包（分三个月返还）
                     */
                    if ("69900926,69908001,69908012,69908015,99992832".contains(productId) && "expired".equals(newEndDate)) {
//                        System.out.println("====activeInfos==remove=");
                        iterator.remove();
                    } else {
//                        System.out.println("====activeInfos==END_DATE=" + newEndDate);
                        activeInfo.put("END_DATE", newEndDate);
                    }
                }
            }
        }

        return activeInfos;
    }
    
    public static IDataset get898GroupMember(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.GROUP_ID, B.CLASS_ID from tf_f_cust_groupmember a,tf_f_cust_group b  ");
		parser.addSQL(" where a.user_id = :USER_ID ");
		parser.addSQL(" and a.partition_id = MOD(:USER_ID, 10000) ");
		parser.addSQL(" and a.remove_tag = '0' ");
		parser.addSQL(" and a.group_id = b.group_id ");
		parser.addSQL(" and b.remove_tag = '0' ");
		parser.addSQL(" and b.GROUP_ID like '898%' ");
		
		return Dao.qryByParse(parser);
    }
    public static IData getWhiteByPk(String userId) throws Exception {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("STATE", "0");
    	return Dao.qryByPK("TF_F_USER_NP_WHITE", param);
    }
}
