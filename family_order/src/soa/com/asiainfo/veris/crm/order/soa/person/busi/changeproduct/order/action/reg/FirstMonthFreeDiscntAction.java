
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: FirstMonthFreeDiscntAction.java
 * @Description: 首月免费优惠处理【TD_S_DISCNT_TYPE=Y|H|F】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 27, 2014 7:59:31 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 27, 2014 maoke v1.0.0 修改原因
 */
public class FirstMonthFreeDiscntAction implements ITradeAction
{

    /**
     * @Description: 首月优惠(20号)校验规则
     * @param userId
     * @param elementId
     * @param elementName
     * @param eparchyCode
     * @throws Exception
     * @author: maoke
     * @date: Jun 27, 2014 8:01:28 PM
     */
    public void checkUserDiscntSvcByDay20(String userId, String elementId, String elementName, String eparchyCode) throws Exception
    {
        if ("3608".equals(elementId))
        {
            boolean flagSvc = false;
            boolean flagAttr = false;
            String servName = "";
            String servId = "";

            IDataset svcData = UserSvcInfoQry.getSvcByUserIdSvc20_1(userId, "914", elementId, eparchyCode);
            

            if (IDataUtil.isNotEmpty(svcData))
            {
               
                servId = svcData.getData(0).getString("SERVICE_ID");
//                servName = svcData.getData(0).getString("SERVICE_NAME");
                servName = UPlatSvcInfoQry.getSvcNameBySvcId(servId);
                // 前三个月(含本月)办理过彩铃
                flagSvc = true;
            }

            if (flagSvc)
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_230, servId, servName);
            }

            IDataset platAttrData = UserPlatSvcInfoQry.getPlatSvcAttrByUserIdSId(userId, "98001901");

            if (IDataUtil.isNotEmpty(platAttrData))
            {
            	flagAttr = true;
                for (int i = 0, size = platAttrData.size(); i < size; i++)
                {
                    IData attrData = platAttrData.getData(i);

                    String attrCode = attrData.getString("ATTR_CODE");
                    String attrValue = attrData.getString("ATTR_VALUE");

                    if ("302".equals(attrCode) && "3".equals(attrValue))
                    {
                        // 咪咕特级会员
                        flagAttr = false;
                        break;
                    }
                }
            }

//            if (flagAttr)
//            {
//                CSAppException.apperr(ProductException.CRM_PRODUCT_231, elementName);
//            }
        }

        // 判断用户是否已经订购了首月免费优惠
        IDataset userDiscnt = UserDiscntInfoQry.getDiscntByUserIdDiscnt20(userId, "910", elementId, eparchyCode);

        if (IDataUtil.isNotEmpty(userDiscnt))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_232, elementName);
        }

        // 判断用户最近三个月内使用过首月免费优惠绑定的相应服务
//        IDataset userPlatSvcData = UserPlatSvcInfoQry.queryPlatSvcByCommpara(userId, "911", elementId, eparchyCode);
        
       IDataset userPlatSvcData =UserPlatSvcInfoQry.queryPlatSvcByCommpara_1(userId, "911", elementId, eparchyCode);
        if (IDataUtil.isNotEmpty(userPlatSvcData) && userPlatSvcData.size() > 1)
        {
            String servId = userPlatSvcData.getData(0).getString("SERVICE_ID", "");
            
            String servName =UPlatSvcInfoQry.getSvcNameBySvcId(servId);

            CSAppException.apperr(ProductException.CRM_PRODUCT_230, servId, servName);
        }
        
        // 判断用户当月新办理首月免费优惠绑定的相应服务
        IDataset userPlatSvcData2 =UserPlatSvcInfoQry.queryPlatSvcByCommpara_2(userId, "911", elementId, eparchyCode);        
        if (IDataUtil.isEmpty(userPlatSvcData2))
        {
        	IDataset commpara = CommparaInfoQry.getCommparaCode1("CSM", "911", elementId, eparchyCode);
            String servId = commpara.getData(0).getString("PARA_CODE1", "");
            
            String servName = UPlatSvcInfoQry.getSvcNameBySvcId(servId);

            CSAppException.apperr(ProductException.CRM_PRODUCT_524, servId, servName);
        }
        
        // 判断用户最近三个月内使用过首月免费优惠
//        IDataset userDiscntData = UserDiscntInfoQry.getUserDiscntByCommparaValid(userId, elementId, "912", eparchyCode);
         IDataset userDiscntData = UserDiscntInfoQry.getUserDiscntByCommparaValid_1(userId, elementId, "911", eparchyCode);
        if (IDataUtil.isNotEmpty(userDiscntData))
        {
           
            String discntCode = userDiscntData.getData(0).getString("DISCNT_CODE", "");
            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);

            CSAppException.apperr(ProductException.CRM_PRODUCT_230, discntCode, discntName);
        }
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        UcaData uca = btd.getRD().getUca();

        if (discntTrades != null && discntTrades.size() > 0)
        {
            for (DiscntTradeData discntTrade : discntTrades)
            {
                String elementId = discntTrade.getElementId();
                String disnctCodeType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
                String eparchyCode = uca.getUserEparchyCode();

                if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
                {
                    String endDate = SysDateMgr.getLastDateThisMonth();
                    String startDate = discntTrade.getStartDate();
                    String day = SysDateMgr.getStringDayByDate(startDate);
                    String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                    String acctTag = uca.getUser().getAcctTag();
                    String userId = uca.getUserId();

                    if ("Y".equals(disnctCodeType))
                    {
                        /* 全时通及彩铃首月免费套餐,客户20日（含20日）后办理则免费体验期为办理当天至次月底 */
                        String symfSpecDay = PersonConst.FIRST_MONTH_FREE_DISCNT_DAY_27;

                        if ("875".equals(elementId) || "1234".equals(elementId))
                        {
                            symfSpecDay = PersonConst.FIRST_MONTH_FREE_DISCNT_DAY_20;
                        }

                        if (day.compareTo(symfSpecDay) >= 0)
                        {
                            endDate = SysDateMgr.getAddMonthsLastDay(2, startDate);
                        }
                        else
                        {
                            endDate = SysDateMgr.getAddMonthsLastDay(1, startDate);
                        }
                        if ("6731".equals(elementId))
                        {
                            // SP_CODE=699400 BIZ_CODE=0001000000000001 对应service_id=99166969 只查询是否办理过即可
                            IDataset userPlatSvcs = UserPlatSvcInfoQry.qryUserPlatByUserServiceIdMonth(btd.getRD().getUca().getUserId(), "99166969", "3");

                            if (IDataUtil.isNotEmpty(userPlatSvcs))
                            {
                                CSAppException.apperr(ProductException.CRM_PRODUCT_228);
                            }

                            // 如果是6731手机电视优惠 则有效期再加一个月
                            endDate = SysDateMgr.getAddMonthsLastDay(2, endDate);
                        }

                        discntTrade.setEndDate(endDate);
                        
                        String productId = uca.getUserNewMainProductId();
                        this.resetOfferRelDate(discntTrade, btd, uca, productId);
                    }
                    else if ("H".equals(disnctCodeType))
                    {
                        this.noActiveUserError(acctTag, elementId, elementName);

                        this.checkUserDiscntSvcByDay20(userId, elementId, elementName, eparchyCode);
                        
                        if ("6409".equals(elementId))//add by xusf V+喜乐包优惠前3个月免费
                        {
                        	 endDate = SysDateMgr.getAddMonthsLastDay(3, endDate);
                        	
                        }
                        else if ("6511".equals(elementId))/*add by songxw 车友助理免费体验套餐截止日期为本月底*/
                        {
                        	endDate = SysDateMgr.getAddMonthsLastDay(1, startDate);
                        }

                        else if (day.compareTo(PersonConst.FIRST_MONTH_FREE_DISCNT_DAY_20) >= 0)
                        {
                            endDate = SysDateMgr.getAddMonthsLastDay(2, startDate);
                        }
                        else
                        {
                            endDate = SysDateMgr.getAddMonthsLastDay(1, startDate);
                        }

                        discntTrade.setEndDate(endDate);
                        String productId = uca.getUserNewMainProductId();
                        this.resetOfferRelDate(discntTrade, btd, uca, productId);
                    }
                    else if ("F".equals(disnctCodeType))
                    {
                        this.noActiveUserError(acctTag, elementId, elementName);
                    }
                }

                // 不允许从界面办理退订该类业务
                if (BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                {
                    IDataset commpara1552 = CommparaInfoQry.getCommparaCode1("CSM", "1552", elementId, eparchyCode);

                    if (IDataUtil.isNotEmpty(commpara1552))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_227);
                    }
                }
            }
        }
    }

    /**
     * @Description: 该规则限制未激活用户不能订购优惠
     * @param acctTag
     * @param elementId
     * @param elementName
     * @throws Exception
     * @author: maoke
     * @date: Jun 27, 2014 8:01:17 PM
     */
    public void noActiveUserError(String acctTag, String elementId, String elementName) throws Exception
    {
        if (!"0".equals(acctTag))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_229, elementId, elementName);
        }
    }
    
    private void resetOfferRelDate(DiscntTradeData discnt, BusiTradeData btd, UcaData uca, String productId) throws Exception{
    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
    	for(OfferRelTradeData offerRel : offerRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(productId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    	
    	List<OfferRelTradeData> userOfferRels = uca.getOfferRelsByRelUserId();
    	for(OfferRelTradeData offerRel : userOfferRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(productId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    }
}
