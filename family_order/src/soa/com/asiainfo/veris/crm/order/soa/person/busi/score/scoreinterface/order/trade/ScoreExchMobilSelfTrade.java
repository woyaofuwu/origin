
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ItemInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreExchMobilSelfRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.StackPackageData;

public class ScoreExchMobilSelfTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ScoreExchMobilSelfRequestData reqData = (ScoreExchMobilSelfRequestData) bd.getRD();
        List<StackPackageData> stackPackageInfos = reqData.getStackPackage();
        ItemInfoData itemInfo = stackPackageInfos.get(0).getItemInfo().get(0);
        String itemId = itemInfo.getItemId();
        String type = null;
        if (StringUtils.isNotBlank(reqData.getType()))
        {
            type = reqData.getType();
        }
        else
        {
            type = itemInfo.getType();
        }
        String lMobile = reqData.getlMobile();
        String serialNumber = reqData.getUca().getSerialNumber();
        String user_Id=reqData.getUca().getUserId();// add by duhj
        if ("0".equals(type))
        {
            if (!lMobile.equals(serialNumber))
            {
                // 兑换类型与受理手机号码不匹配
                CSAppException.apperr(CrmUserException.CRM_USER_1146);
            }
        }
        if ("1".equals(type))
        {
            if (lMobile.equals(serialNumber))
            {
                // 兑换类型与受理手机号码不匹配
                CSAppException.apperr(CrmUserException.CRM_USER_1146);
            }
        }
        // 从台帐和历史台帐表中来判重
        IDataset tradeInfo = TradeInfoQry.queryTradeAndHistoryTrade("339", itemInfo.getSubOrderId(), serialNumber);
        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            // 子订单号在台帐或者历史台帐表中已经存在
            CSAppException.apperr(CrmUserException.CRM_USER_1149);
        }

        // 主台账
        MainTradeData mainList = bd.getMainTradeData();
        mainList.setRsrvStr1(reqData.getOrderNo());
        mainList.setRsrvStr2(itemInfo.getSubOrderId());
        mainList.setRsrvStr3(serialNumber);
        mainList.setRsrvStr4(itemId);
        mainList.setRsrvStr5(itemInfo.getItemName());
        mainList.setRsrvStr6(stackPackageInfos.get(0).getStoreId());
        mainList.setRsrvStr7(itemInfo.getItemType());
        mainList.setRsrvStr8(itemInfo.getItemPayCash());

        IData exchangeMobilType = new DataMap();
        IDataset exchangeMobilTypes = CommparaInfoQry.getCommpara("CSM", "4502", itemId, this.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(exchangeMobilTypes))
        {
            exchangeMobilType = exchangeMobilTypes.getData(0);
        }
        else
        {
            // 获取兑换的移动自有产品无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_1143);
        }
        String mobilType = exchangeMobilType.getString("PARA_CODE1");
        String mobilValue = exchangeMobilType.getString("PARA_CODE2");
        String startType = exchangeMobilType.getString("PARA_CODE5");// 叠加包生效时间区分字段
        
        //关于下发近期积分商城相关省级配合改造的通知 by zhouyl5 20141016
        String effectFlag = exchangeMobilType.getString("PARA_CODE3","");// "" or 0 :下账期生效     1：立即生效   （99叠加包 立即生效）
      	String validDate = exchangeMobilType.getString("PARA_CODE4","0");// 有效期 精确到月   （99叠加包1一个账期）

		//REQ201811060020关于申请在省BOSS上关联配置积分兑换流量礼品ID的需求 ,配置按天计算套餐标识和套餐截止天数
      	String endTimeFlag=exchangeMobilType.getString("PARA_CODE6",""); //1表示按天计算结束时间
    	String endTimeLen=exchangeMobilType.getString("PARA_CODE7","1"); //结束时间的长度
      	
        // 积分兑换GPRS叠加包月末最后一天不受理
        if ("99".equals(mobilType))
        {
            if (SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD).equals(SysDateMgr.getLastCycleThisMonth()))
            {
                // 月末最后一天不受理
                CSAppException.apperr(CrmUserException.CRM_USER_1147);
            }
        }
        // 飞信业务每月只能兑换一次
        if ("04".equals(mobilType))
        {
            // 查询本月是否有过兑换记录，即优惠表中是否有积分兑换的优惠包
            IDataset discnt = TradeDiscntInfoQry.checkFetionScoreExchange(serialNumber,user_Id);//mofify by duhj
            if (IDataUtil.isNotEmpty(discnt))
            {
                // 该用户已开通飞信会员产品，不可以订购
                CSAppException.apperr(CrmUserException.CRM_USER_1148);
            }
        }

        //关于下发近期积分商城相关省级配合改造的通知 by zhouyl5 20141016
        String mpbao = exchangeMobilType.getString("PARA_CODE5","");//流量年包季包
		if("02".equals(mobilType)&&"1".equals(mpbao)){
			IDataset mainInfo =ShareInfoQry.queryMemberRela(reqData.getUca().getUserId(),"01");;
			
			if(mainInfo != null && mainInfo.size() > 0){
				CSAppException.apperr(CrmUserException.CRM_USER_1206, mobilType);
			}
		}
		
        // 凤凰手机报新添加的处理分支
        if ("03,04,05,06,07,08".indexOf(mobilType) > -1)
        {
            IDataset giftInfos = CommparaInfoQry.queryScoreExchangePlat(itemId);
            if (IDataUtil.isEmpty(giftInfos))
            {
                return;
            }
            else
            {
                IData gift = giftInfos.getData(0);
                String serviceId = gift.getString("SERVICE_ID");
                PlatSvcData psd = new PlatSvcData();
                psd.setElementId(serviceId);
                psd.setScoreChange(true);
                String month = gift.getString("PARA_CODE3");// 积分时长 单位：月
                String discnt = gift.getString("PARA_CODE4", ""); // 获取积分兑换减免优惠
                if (!"".equals(discnt))
                {
                    this.createDiscnt(bd, reqData, discnt, month);
                }
                psd.setScoreChangeTime(SysDateMgr.getAddMonthsLastDay(Integer.parseInt(month)));
                psd.setOfficeData(PlatOfficeData.getInstance(serviceId));
                psd.setOperCode(PlatConstants.OPER_ORDER);
                psd.setOprSource("08");
                List<ProductModuleData> list = new ArrayList<ProductModuleData>();
                list.add(psd);
                ProductModuleCreator.createProductModuleTradeData(list, bd);
            }
        }
        else if ("01".equals(mobilType))
        {
            createTableTradeGiftfee(bd, reqData, mobilValue); // 赠送费用台帐
        }else if ("90".equals(mobilType)) {

            String productId = exchangeMobilType.getString("PARA_CODE2");
            String packageId = exchangeMobilType.getString("PARA_CODE3");
            if(StringUtils.isNotEmpty(productId) && StringUtils.isNotEmpty(packageId)) {
                IData saleactiveData = new DataMap();
                saleactiveData.put("SERIAL_NUMBER", serialNumber);
                saleactiveData.put("PRODUCT_ID", productId);
                saleactiveData.put("PACKAGE_ID", packageId);
                IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
            }
        }
        else
        {
            // 兑换GPRS优惠叠加包
            if ("99".equals(mobilType))
            {
                // 查询所有流量叠加包是不超过限定次数
                IData map = this.queryGprsTotalTimes(reqData.getUca().getUserId());

                int discntCount = (Integer) map.get("CURRENT_COUNT");
                int moileTime = (Integer) map.get("MAX_COUNT");

                if (discntCount >= moileTime)
                {
                    // 重复订购叠加包的次数不能大于"+moileTime+"次
                }
            }
            createTableTradeDiscnt(bd, reqData, mobilValue, mobilType, startType,effectFlag,validDate,endTimeFlag,endTimeLen); // 优惠台帐
        }

    }

    /**
     * 积分兑换绑定优惠
     * 
     * @Title : createDiscnt
     * @Param : @param btd
     * @Param : @param discnt_code
     * @Param : @param month
     * @Param : @throws NumberFormatException
     * @Param : @throws Exception
     * @return: void
     */
    private void createDiscnt(BusiTradeData btd, ScoreExchMobilSelfRequestData reqData, String discnt_code, String month) throws NumberFormatException, Exception
    {
        UcaData uca = btd.getRD().getUca();
        DiscntTradeData discntTD1 = new DiscntTradeData();
        String serialNumber = reqData.getlMobile();
        discntTD1.setUserId(uca.getUserId());
        discntTD1.setUserIdA("-1");
        discntTD1.setProductId("50000000");
        discntTD1.setPackageId("50000000");
        discntTD1.setSpecTag("1");
        discntTD1.setInstId(SeqMgr.getInstId());
        discntTD1.setElementId(discnt_code);
        discntTD1.setStartDate(SysDateMgr.getSysDate());
        discntTD1.setEndDate(SysDateMgr.getAddMonthsLastDay(Integer.parseInt(month)));
        discntTD1.setModifyTag("0");
        discntTD1.setRemark("积分兑换绑定优惠");
        btd.add(serialNumber, discntTD1);
    }

    /**
     * 优惠台帐
     * 
     * @throws Exception
     */
    private void createTableTradeDiscnt(BusiTradeData bd, ScoreExchMobilSelfRequestData reqData, String mobilValue, String mobilType, String startType,String effectFlag,String validDate,String endTimeFlag,String endTimeLen) throws Exception
    {
        String instId = SeqMgr.getInstId();
        DiscntTradeData discntData = new DiscntTradeData();
        discntData.setUserId(reqData.getUca().getUserId());
        discntData.setUserIdA("-1");
        discntData.setSpecTag("1");
        discntData.setInstId(instId);
        discntData.setElementId(mobilValue);
        discntData.setPackageId("-1");
        discntData.setProductId("-1");

        // GPRS叠加包
        if ("99".equals(mobilType))
        {
            discntData.setRemark("积分兑GPRS叠加包");
            if ("0".equals(startType))
            {// 0表示叠加包下月生效
                discntData.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
                discntData.setEndDate(SysDateMgr.getNextMonthLastDate());
            }
            else
            {
                discntData.setStartDate(SysDateMgr.getSysDate());
                discntData.setEndDate(SysDateMgr.getLastDateThisMonth());// 本账期最后一天
            }
        }
        else
        {
        	//关于下发近期积分商城相关省级配合改造的通知 by zhouyl5 20141016
            if("1".equals(effectFlag)){
            	discntData.setStartDate(SysDateMgr.getSysTime());
            	discntData.setEndDate(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getLastDateThisMonth()));
				            	
            	//按天计算结束时间 REQ201811060020关于申请在省BOSS上关联配置积分兑换流量礼品ID的需求
            	if("1".equals(endTimeFlag)){
					int endTimeLen1 = new Integer(endTimeLen);
                    String strEndDate = "";
                    //流量一日包，结束时间设为24小时后
					if (endTimeLen1 == 1){
                        strEndDate = SysDateMgr.getTomorrowTime();
                    }else {
                        int seconds = (60 * 60 * 24) * (endTimeLen1 - 1);
                        strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                        strEndDate = strEndDate.substring(0, 10);
                        strEndDate = strEndDate + SysDateMgr.END_DATE;
                    }
                    discntData.setEndDate(strEndDate);
            	}           	
            	
			}else{
				discntData.setStartDate(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getFirstDayOfNextMonth()));
				discntData.setEndDate(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getNextMonthLastDate()));
			}
            discntData.setRemark("积分兑换自有产品");
        }
        discntData.setModifyTag("0");
        bd.add(reqData.getUca().getSerialNumber(), discntData);
    }

    /**
     * 赠送费用台帐
     * 
     * @throws Exception
     */
    public void createTableTradeGiftfee(BusiTradeData btd, ScoreExchMobilSelfRequestData reqData, String mobilValue) throws Exception
    {
        GiftFeeTradeData feeData = new GiftFeeTradeData();

        feeData.setUserId(reqData.getUca().getUserId());
        feeData.setUserIdA("-1");
        feeData.setFeeMode("2");
        feeData.setFeeTypeCode("12");
        feeData.setFee(mobilValue);
        feeData.setChargeId("0");
        feeData.setDiscntGiftId("0");
        feeData.setLimitMoney("0");
        feeData.setMonths("1");
        feeData.setRemark("积分兑换自有产品:话费");
        btd.add(reqData.getlMobile(), feeData);
    }

    /**
     * 如果没有配置兑换GPRS总数默认通过
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public IData queryGprsTotalTimes(String userId) throws Exception
    {
        IData retMap = new DataMap();
        int discntCount = 0; // 用户针对优惠叠加包订购的次数;
        int toatlLimit = 0;
        // 获取总数配置
        IDataset commparaList = CommparaInfoQry.getCommParas("CSM", "4512", "9999", "99", this.getUserEparchyCode());

        if (IDataUtil.isNotEmpty(commparaList))
        {
            toatlLimit = commparaList.getData(0).getInt("PARA_CODE3");
            retMap.put("MAX_COUNT", toatlLimit);
        }
        else
        {
            // 参数表COMMPARA-4012流量加油包最大次数没有配置
            CSAppException.apperr(CrmUserException.CRM_USER_1145);
        }

        IDataset userDiscntList = UserDiscntInfoQry.getCountGprs(userId); // 根据用户和叠加包的优惠编码查询用户订购了多少条记录
        if (IDataUtil.isNotEmpty(userDiscntList))
        {
            discntCount += userDiscntList.getData(0).getInt("DONE_TOTAL");
        }

        // 判断台账表中是否存在该优惠且未完工的情况,只考虑新增的情况，一般的取消都是取消到月底
       // IDataset tradeDiscntList = UserDiscntInfoQry.getCountGprsDoning(userId, "0");// duhj
        IDataset tradeDiscntList = TradeDiscntInfoQry.getCountGprsDoning(userId, "0");

        if (IDataUtil.isNotEmpty(tradeDiscntList))
        {
            discntCount += tradeDiscntList.getData(0).getInt("DONING_TOTAL");

        }
        retMap.put("CURRENT_COUNT", discntCount);
        return retMap;
    }
}
