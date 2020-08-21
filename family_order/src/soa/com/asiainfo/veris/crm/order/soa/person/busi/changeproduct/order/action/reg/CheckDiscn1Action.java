package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
4G飞享套餐、4G自选套餐推出优惠半年包促销方案；50元及以上流量模组客户推出(60元、12GB，半年包)；50元以下流量模组客户推出(60元、6GB，半年包)
 */
public class CheckDiscn1Action implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐

        if (discntTrades != null && discntTrades.size() > 0) {
            //20170456、20170457 套餐业务限制判断
            check0(uca, discntTrades);

            //定向视频流量判断: 16个优惠里， 9元优惠，最多存在3个有效的记录，并且3个都不能相同。
//            check1(uca, discntTrades); // 因 [REQ201803190006【紧急】关于任我看支持第三方支付产品及新增部分话费支付产品]注释调

            //定向视频流量判断: 不能同时新办理同一个AAP下的9元和24元优惠
            check2(discntTrades);

            //定向视频流量判断: 视频定向流量，办理了9元的套餐如果再办理24元的套餐，如果是不同的APP那么24元套餐立即生效。 如是相同APP，那么24元下月生效，9元下月失效。
            check3(btd, uca, discntTrades);

        }
    }

    private void check0(UcaData uca, List<DiscntTradeData> discntTrades) throws Exception
    {
        //20170456、20170457 套餐业务限制判断

        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String msg = "只有已选4G自选优惠的用户才能办理该优惠包！";
                String discntCode = discntTrade.getDiscntCode();
                String productId = discntTrade.getProductId();
                if (discntCode != null && productId != null) {
                    // REQ201707310010关于任我享七月促销套餐办理优化
                    IDataset flowDiscntCfgs = CommparaInfoQry.getCommPkInfo("CSM", "2018", "FLOW_DISCNT_ORDER_COUNT", uca.getUserEparchyCode());
                    IDataset cfg = DataHelper.filter(flowDiscntCfgs, "PARA_CODE1=" + discntCode);
                    if (IDataUtil.isNotEmpty(cfg)) {// 60元赠送12GB半年包套餐  ,60元赠送6GB半年包套餐
//                        String discntName = StaticUtil.getStaticValue(null, "TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", discntCode);
                    	String discntName =UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
                        String discntMsg = "[" + discntCode + "]" + discntName + "！";
                        IData param = new DataMap();
                        param.put("USER_ID", uca.getUserId());
                        param.put("DISCNT_CODE", discntCode);
                        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNTCODE4", param);//判断是否存在本月已订购60元赠送12GB半年包套餐  ,60元赠送6GB半年包套餐

                        int orderCount = cfg.first().getInt("PARA_CODE2", 1); // 没有配置订购次数，默认为1次
                        if (userDiscnts.size() > orderCount-1) {

                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "1个月内，只能办理" + orderCount + "次" + discntMsg);
                        }

                        param.clear();
                        userDiscnts = null;

                        param.put("USER_ID", uca.getUserId());
                        param.put("SUBSYS_CODE", "CSM");
                        param.put("PARAM_ATTR", "1087");
                        param.put("PARA_CODE1", "1");
                        userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNTCODE3", param);//查询是否该用户在产品变更前，是否已订购4G自选优惠
                        if (IDataUtil.isNotEmpty(userDiscnts)) {
                            IData userDiscnt = userDiscnts.first();
                            String userDiscntCode = userDiscnt.getString("DISCNT_CODE", "").trim();
//                            String UserDiscntPrice = StaticUtil.getStaticValue(null, "TD_B_DISCNT", "DISCNT_CODE", "PRICE", userDiscntCode);
                        	IDataset pricePlanInfos = UpcCall.qryPricePlanInfoByOfferId(userDiscntCode,BofConst.ELEMENT_TYPE_CODE_DISCNT);
                            
                        	String UserDiscntPrice = "";
                    		
                    		if(IDataUtil.isNotEmpty(pricePlanInfos)){
                    			UserDiscntPrice = pricePlanInfos.getData(0).getString("FEE","");
                    		}

                            if (UserDiscntPrice != null && !UserDiscntPrice.trim().equals("")) {
                                if (discntCode.equals("20170456")) {
                                    //60元赠送12GB半年包套餐    //判断原套餐50元及50元以上流量模组客户才可办理该优惠
                                    if (StringUtils.isNumeric(UserDiscntPrice) && Integer.parseInt(UserDiscntPrice) < 5000) {
                                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "50元及50元以上流量客户才可办理" + discntMsg);
                                    }
                                } else if (discntCode.equals("20170457")) {
                                    //60元赠送6GB半年包套餐  //判断原套餐50元以下流量模组客户才可办理该优惠
                                    if (StringUtils.isNumeric(UserDiscntPrice) && Integer.parseInt(UserDiscntPrice) >= 5000) {
                                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "50元以下流量客户才可办理" + discntMsg);
                                    }
                                }
                            } else {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, msg);
                            }
                        } else {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, msg);
                        }
                    }
                }
            }
        }
    }

    private void check3(BusiTradeData btd, UcaData uca, List<DiscntTradeData> discntTrades) throws Exception
    {
        //视频定向流量，办理了9元的套餐如果再办理24元的套餐，如果是不同的APP那么24元套餐立即生效。 如是相同APP，那么24元下月生效，9元下月失效。
        List<DiscntTradeData> UpdDiscntTradeDataList = new ArrayList();

        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String newDiscntCode = discntTrade.getDiscntCode();

                IDataset ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1091", newDiscntCode, "1");//是否是24元优惠
                if (IDataUtil.isNotEmpty(ds)) {
                    //检查用户是否已订购同一个APP下的9元优惠，先找24元优惠的同一个app下的优惠编码，再到用户已订购天列表里看这些编码是否存在，存在的话，再找价格是9元的                        
                    List<DiscntTradeData> userdiscntTradeDatas = uca.getUserDiscnts();
                    for (int j = 0; j < userdiscntTradeDatas.size(); j++) {
                        DiscntTradeData userdiscntTradeData = userdiscntTradeDatas.get(j).clone();
                        String userDiscntCode = userdiscntTradeData.getDiscntCode();
                        IDataset pricePlanInfos = UpcCall.qryPricePlanInfoByOfferId(userDiscntCode,BofConst.ELEMENT_TYPE_CODE_DISCNT);
                        
                    	String UserDiscntPrice = "";
                		
                		if(IDataUtil.isNotEmpty(pricePlanInfos)){
                			UserDiscntPrice = pricePlanInfos.getData(0).getString("FEE","");
                		}
                        if (UserDiscntPrice != null && StringUtils.isNumeric(UserDiscntPrice) && Integer.parseInt(UserDiscntPrice) == 900) {//如果等于9元
                            IDataset ds1 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1090", newDiscntCode, "1", userDiscntCode, "ZZZZ");//查询出所有同一个app的优惠编码
                            if (IDataUtil.isNotEmpty(ds1)) {//是同一个APP下的9元优惠        

                                //将老的9元优惠的结束时间设置为本月最后1天      
                                userdiscntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
                                userdiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                UpdDiscntTradeDataList.add(userdiscntTradeData);

                                //将新订购的24元优惠的开始时间设置为下月1号
                                discntTrade.setStartDate(SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate()) + SysDateMgr.START_DATE_FOREVER);
                            }
                        }
                    }
                }
            }
        }

        if (UpdDiscntTradeDataList != null && UpdDiscntTradeDataList.size() > 0) {
            for (int i = 0; i < UpdDiscntTradeDataList.size(); i++) {
                btd.add(btd.getRD().getUca().getSerialNumber(), UpdDiscntTradeDataList.get(i));
            }
        }
    }

    private void check2(List<DiscntTradeData> discntTrades) throws Exception
    {
        //不能同时新办理同一个AAP下的9元和24元优惠
        List<String> newDiscntCodes = new ArrayList<String>();
        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String discntCode = discntTrade.getDiscntCode();
                if (discntCode != null) {
                    newDiscntCodes.add(discntCode.trim());
                }
            }
        }
        for (int i = 0; i < newDiscntCodes.size(); i++) {
            String newDiscntCode = newDiscntCodes.get(i);
            IDataset ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1090", newDiscntCode, "1");
            if (IDataUtil.isNotEmpty(ds)) {//新办理的优惠存在同一个app的判断
                for (int j = 0; j < ds.size(); j++) {
                    if (newDiscntCodes.contains(ds.getData(j).getString("PARA_CODE2", "").trim())) {//如新办理2个优惠属于同一个app的情况
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "不能同时新办理同一个APP下的不同优惠！");
                    }
                }
            }
        }
    }

    private void check1(UcaData uca, List<DiscntTradeData> discntTrades) throws Exception
    {
        //16个优惠里， 9块的优惠，每个月最多办理3个，并且3个都不能相同。
        int curreMonthOrderedDiscntNum = 0;//用户本月已订购的数量
        boolean queryCurreMonthOrderedDiscnt = false;
        int orderDiscntNum = 0;//本次产品变更订购的数量
        String errMsg = "视频定向流量9元优惠，最多订购3个!";
        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String discntCode = discntTrade.getDiscntCode();
                // select t.* from   ucr_cen1.td_s_commpara    t  where 1=1   and t.param_attr in ( 1088) and t.para_code1='1'  ; 
                IDataset discntds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1089", discntCode, "1");//判断是否是定向视频流量9元优惠之一
                if (IDataUtil.isNotEmpty(discntds)) {
                    orderDiscntNum++;
                    if (!queryCurreMonthOrderedDiscnt) {
                        IData param = new DataMap();
                        param.put("USER_ID", uca.getUserId());
                        param.put("SUBSYS_CODE", "CSM");
                        param.put("PARAM_ATTR", "1089");//该编码配置的是9月定向视频流量优惠
                        param.put("PARA_CODE1", "1");//1 有效 ，0 无效
                        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNTCODE3", param);//查询用户目前订购了多少个有效的9元优惠
                        if (IDataUtil.isNotEmpty(userDiscnts)) {
                            if (userDiscnts.size() >= 3) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, errMsg);
                            }
                            curreMonthOrderedDiscntNum = userDiscnts.size();
                            queryCurreMonthOrderedDiscnt = true;
                        }
                    }
                }
            }
        }

        if ((curreMonthOrderedDiscntNum + orderDiscntNum) > 3) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errMsg);
        }
    }
}