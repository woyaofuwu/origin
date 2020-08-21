
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.NoPhoneWideUserCreateBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.requestdata.NoPhoneWideUserActiveRequestData;

public class NoPhoneWideUserActiveTrade extends BaseTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
        
        btd.getMainTradeData().setSubscribeType("300");
        
        btd.getMainTradeData().setRsrvStr1(widenetProductRD.getCreateUserTradeId());
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        //用户宽带台账
        createWidenetTrade(btd);
        
        //用户台账
        createUserTrade(btd);
        
        //宽带账户台账
        createAccountTrade(btd);
        
        //宽带用户产品台账
        createProductTrade(btd);
        
        //宽带用户服务台账
        createSvcTrade(btd);
        
        //宽带用户优惠台账
        createDiscntTrade(btd);
        
        //将宽带账号状态改为选占
        updateWideNetAccoutState(btd);
        
        // 更新无手机宽带开户订单
        updateNoPhoneWideCreateTrade(btd);
        
        appendTradeMainData(btd);
        
    }
    
    
    /**
     * 用户宽带台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createWidenetTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
        IData widenetInfo = widenetInfos.getData(0);
        WideNetTradeData wtd = new WideNetTradeData(widenetInfo);
        
        wtd.setConstructionAddr(widenetProductRD.getConstructionAddr());
        wtd.setConstStaffId(widenetProductRD.getConstStaffId());
        wtd.setConstPhone(widenetProductRD.getConstPhone());
        
        wtd.setRsrvTag3(widenetProductRD.getRsrvTag3());
        
        //设备号
        if (StringUtils.isNotBlank(widenetProductRD.getDeviceId()))
        {
            wtd.setRsrvNum1(widenetProductRD.getDeviceId());
        }
        
        if (StringUtils.isNotBlank(widenetProductRD.getPortId()))
        {
            wtd.setRsrvNum4(widenetProductRD.getPortId());
        }
        
        wtd.setRsrvDate3(widenetProductRD.getFinishDate());
        
        if (StringUtils.isNotBlank(widenetProductRD.getStandAddress()))
        {
            wtd.setStandAddress(widenetProductRD.getStandAddress());
        }
        
        wtd.setStartDate(widenetProductRD.getFinishDate());
        
        wtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }
    
    
    /**
     * 用户台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createUserTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
        
        UserTradeData userTradeData = widenetProductRD.getUca().getUser().clone();
        
        userTradeData.setOpenDate(widenetProductRD.getFinishDate());
        userTradeData.setAcctTag("0");//用户状态改成回调后激活@tanzheng@20171020
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }
    
    /**
     * 宽带账户台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createAccountTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
        
        AccountTradeData acctTradeData = widenetProductRD.getUca().getAccount().clone();
        
        //激活账户，无手宽带开户是账户状态为待激活
        acctTradeData.setAcctTag("0");
        
        acctTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            
        btd.add(btd.getRD().getUca().getSerialNumber(), acctTradeData);
    }
    
    /**
     * 宽带产品台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createProductTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
       
        List<ProductTradeData> productTradeDataList = widenetProductRD.getUca().getUserProducts();
        
        int size = productTradeDataList.size();
        
        if (null != productTradeDataList)
        {
            for (int i = 0; i < size; i++)
            {
                ProductTradeData productTradeData = productTradeDataList.get(i).clone();
                
                productTradeData.setStartDate(widenetProductRD.getFinishDate());
                
                productTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                
                btd.add(btd.getRD().getUca().getSerialNumber(), productTradeData);
            }
        }
    }
    
    
    /**
     * 宽带服务台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createSvcTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
       
        List<SvcTradeData> svcTradeDataList = widenetProductRD.getUca().getUserSvcs();
        
        int size = svcTradeDataList.size();
        
        if (null != svcTradeDataList)
        {
            for (int i = 0; i < size; i++)
            {
                SvcTradeData svcTradeData = svcTradeDataList.get(i).clone();
                
                String finishDate = widenetProductRD.getFinishDate().substring(0, 10) + SysDateMgr.START_DATE_FOREVER;
                
                svcTradeData.setStartDate(finishDate);
                
                svcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                
                btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);
                
                //修改商品关系时间
                List<OfferRelTradeData> offerRelTradeDataList = widenetProductRD.getUca().getOfferRelByRelUserIdAndRelOfferInsId(svcTradeData.getInstId());
                if (null != offerRelTradeDataList && offerRelTradeDataList.size() > 0)
                {
                    for (int j = 0; j < offerRelTradeDataList.size(); j++)
                    {
                        OfferRelTradeData offerRelTradeData = offerRelTradeDataList.get(j);
                        
                        offerRelTradeData.setStartDate(finishDate);
                        offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        
                        btd.add(btd.getRD().getUca().getSerialNumber(), offerRelTradeData);
                    }
                }
            }
        }
    }
    
    
    /**
     * 宽带服务台帐
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createDiscntTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
       
        List<DiscntTradeData> discntTradeDataList = widenetProductRD.getUca().getUserDiscnts();
        
        if (null != discntTradeDataList)
        {
            String finishDate =  widenetProductRD.getFinishDate();
            
            // 判断回单时间在25日的前后
            IDataset tagInos = TagInfoQry.getTagInfo(CSBizBean.getUserEparchyCode(), "CS_WIDENET_OPENFINISH", "0", null);
            
            //默认为25
            String tagDate = "25";
            
            if (IDataUtil.isNotEmpty(tagInos))
            {
                tagDate = tagInos.getData(0).getString("TAG_INFO");
            }

            
            String finishDay = finishDate.substring(8, 10);
            
            // 默认下月开通
            String startDate = SysDateMgr.getDateNextMonthFirstDay(finishDate);
            
            // 如大于标识日期，则下下月开通
            if (finishDay.compareTo(tagDate) >= 0)
            {
                startDate = SysDateMgr.getDateNextMonthFirstDay(startDate);
            }
            
            int size = discntTradeDataList.size();
            
            for (int i = 0; i < size; i++)
            {
                DiscntTradeData discntTradeData = discntTradeDataList.get(i).clone();
                
                DiscntData discntData = new DiscntData(discntTradeData.toData());
                //edit by zhangxing3 for 候鸟月、季、半年套餐（海南）
                String endDate = "";
                String discntCode = discntData.getElementId();
                if("84003843".equals(discntCode))
                {
                	endDate = SysDateMgr.getAddMonthsLastDayNoEnv(12, startDate);
                }
                else{
                	endDate = SysDateMgr.endDate(startDate, discntData.getEndEnableTag(), discntData.getEndAbsoluteDate(), discntData.getEndOffSet(), discntData.getEndUnit());
                }
                //edit by zhangxing3 for 候鸟月、季、半年套餐（海南）
                discntTradeData.setStartDate(startDate);
                
                discntTradeData.setEndDate(endDate);
                
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                
                btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);
                
                //修改商品关系时间
                List<OfferRelTradeData> offerRelTradeDataList = widenetProductRD.getUca().getOfferRelByRelUserIdAndRelOfferInsId(discntTradeData.getInstId());
                if (null != offerRelTradeDataList && offerRelTradeDataList.size() > 0)
                {
                    for (int j = 0; j < offerRelTradeDataList.size(); j++)
                    {
                        OfferRelTradeData offerRelTradeData = offerRelTradeDataList.get(j);
                        
                        offerRelTradeData.setStartDate(startDate);
                        offerRelTradeData.setEndDate(endDate);
                        offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(btd.getRD().getUca().getSerialNumber(), offerRelTradeData);
                    }
                }
                
                //修改定价计划时间
                List<PricePlanTradeData> pricePlanTradeDatas = widenetProductRD.getUca().getPricePlansByOfferInsId(discntTradeData.getInstId());
                if (pricePlanTradeDatas != null && pricePlanTradeDatas.size() > 0)
                {
                    pricePlanTradeDatas.get(0).setStartDate(startDate);
                    pricePlanTradeDatas.get(0).setEndDate(endDate);
                    pricePlanTradeDatas.get(0).setModifyTag(BofConst.MODIFY_TAG_UPD);
                    btd.add(btd.getRD().getUca().getSerialNumber(), pricePlanTradeDatas.get(0));
                }
                
                //无手机度假宽带开户跨月生效后，无手机魔百和优惠未按度假宽带活动结束时间结束
                if("84071448".equals(discntCode) || "84071449".equals(discntCode)|| "84074442".equals(discntCode))
                {
                	IDataset relaUU = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(btd.getRD().getUca().getUserId(), "47");
                	//System.out.println("------------------NoPhoneWideUserActiveTrade----------------relaUU:"+relaUU);
                	if (IDataUtil.isNotEmpty(relaUU))
                	{
                		String serialNumberA = relaUU.getData(0).getString("SERIAL_NUMBER_A", "");
                		String updDiscnts="84071643";//度假宽带减免魔百和功能费套餐（无手机）
    	             	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumberA);
                    	//System.out.println("----------------NoPhoneWideUserActiveTrade------------------ucaData:"+ucaData);
    	                List<DiscntTradeData> userDiscntUpd = ucaData.getUserDiscntsByDiscntCodeArray(updDiscnts);
                    	//System.out.println("------------------NoPhoneWideUserActiveTrade----------------userDiscntUpd:"+userDiscntUpd);

    	                if (ArrayUtil.isNotEmpty(userDiscntUpd)){
				        	for (int j =0; j < userDiscntUpd.size(); j++){
					        	DiscntTradeData updDiscntTD = userDiscntUpd.get(j).clone();	
					        	updDiscntTD.setEndDate(endDate);
					        	updDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
					            btd.add(serialNumberA, updDiscntTD);
				        	}
				            
				        }
                	}
                }
                //无手机度假宽带开户跨月生效后，无手机魔百和优惠未按度假宽带活动结束时间结束
                
            }
            
            
        }
    }
    
    /**
     * 将宽带账号改为占用
     * @param btd
     * @throws Exception
     * @author yuyj3
     */
    private void updateWideNetAccoutState(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        
        if (serialNumber.startsWith("KD_"))
        {
            serialNumber = serialNumber.substring(3);
        }
        
        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        bean.updateWideNetAccoutState(serialNumber, "3", null, null, btd.getRD().getAcceptTime(), null);
    }
    
    /**
     * 更新无手机宽带开户订单
     * @param btd
     * @throws Exception
     * @author yuyj3
     */
    private void updateNoPhoneWideCreateTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
        
        String noPhoneUserCreateTradeId = widenetProductRD.getCreateUserTradeId();
        
        
        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        
        //将此产品变更订单ＩＤ　更新到关联的无手机宽带开户的订单中
        bean.updateBhTradeRsrvStr9(noPhoneUserCreateTradeId, widenetProductRD.getTradeId());
    }
}
