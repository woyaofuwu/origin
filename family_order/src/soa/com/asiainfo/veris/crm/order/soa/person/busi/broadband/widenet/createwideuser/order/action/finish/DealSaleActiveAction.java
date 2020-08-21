
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;

/**
 * 宽带回单营销活动处理
 * 
 * @author yuyj3
 */
public class DealSaleActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        
        //增加特殊处理，如果是移机并且同时终止老营销活动，办理新营销活动，需要让老营销活动先终止，然后再办理新营销活动
        //此处处理，如果用户同时拥有一笔237台账，则直接返回
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
    	String orderId = mainTrade.getString("ORDER_ID");
        if("606".equals(tradeTypeCode)){
        	IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
        	if(IDataUtil.isNotEmpty(tradeInfos)){
        		for(int i=0;i<tradeInfos.size();i++){
        			if("237".equals(tradeInfos.getData(i).getString("TRADE_TYPE_CODE"))) return ;
        		}
        	}
        }
        
        String productId = "";
        String packageId = "";
        String packageIdPre = "";
        IDataset saleActiveInfos = UserSaleActiveInfoQry.getBook2ValidSaleActiveByTradeId(tradeId, serialNumber);
        for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
        {
            IData saleActiveInfo = saleActiveInfos.getData(i);
            serialNumber = saleActiveInfo.getString("SERIAL_NUMBER");
            productId = saleActiveInfo.getString("PRODUCT_ID_B");
            packageId = saleActiveInfo.getString("PACKAGE_ID_B");
            packageIdPre = saleActiveInfo.getString("PACKAGE_ID");
            
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            String userId = userInfo.getString("USER_ID");
            
            IDataset validSaleActiveInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId,packageId);
            //IDataset validSaleActiveInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId_S(userId, productId,packageId);
            
            if(IDataUtil.isEmpty(validSaleActiveInfos)){
				//增加配置是否在活动有效期内校验,若不配置则不影响原逻辑
				boolean validSaleActiveConfigDate = queryValidSaleActiveConfigDate( productId, packageId,tradeId);
				if(!validSaleActiveConfigDate){
					//不在活动时间内,取消预受理活动
                    UserSaleActiveInfoQry.updateBookNotValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "已过活动时间,不转正式包","0");
				}else{
					UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");

					IData param = new DataMap();
					param.put("SERIAL_NUMBER", serialNumber);
					param.put("PRODUCT_ID", productId);
					param.put("PACKAGE_ID", packageId);
					param.put("TRADE_STAFF_ID", "SUPERUSR");
					param.put("TRADE_DEPART_ID", "36601");
					param.put("TRADE_CITY_CODE", "HNSJ");
					//移机时，修改营销活动的生效时间为PBOSS回单时间的下月1号
					if("606".equals(tradeTypeCode)||"237".equals(tradeTypeCode)){
						String moveTradeId = "";
						if(orderId!=null&&!"".equals(orderId)){
							IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
							if(IDataUtil.isNotEmpty(tradeInfos)){
								for(int j=0;j<tradeInfos.size();j++){
									if("606".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE")))
										moveTradeId = tradeInfos.getData(j).getString("TRADE_ID","");
								}
							}
						}

						String bookDate = SysDateMgr.getFirstDayOfNextMonth();
						if(!"".equals(moveTradeId)){
							IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(moveTradeId);
							if (IDataUtil.isEmpty(finishInfos))
							{
								CSAppException.apperr(WidenetException.CRM_WIDENET_14);
							}else{
								String finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
								bookDate = SysDateMgr.getFirstDayOfNextMonth(finishDate);
							}
						}

						param.put("BOOKING_DATE", bookDate);
						param.put("WIDENET_MOVE_SALEACTIVE_SIGN", "1");
					}
					if("600".equals(tradeTypeCode))
					{
						//标记是宽带开户营销活动
						param.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
						param.put("SALE_ACTIVE_ID", mainTrade.getString("CAMPN_ID",""));

						if ("66000213".equals(productId))
						{
							//购机类营销活动预受理转正式，预受理已经对终端做了预占用，正式包不需要做预占处理
							param.put("IS_NEED_PREOCCUPY", false);
						}

						//add by zhangxing3 for REQ201804280023优化“先装后付，免费体验”
						if ("69908001".equals(productId))
						{
							//用户订购优惠体验套餐时，修改宽带1+活动的生效时间为优惠体验套餐结束时间的下月1号
							String bookingDate = getBookingDate(tradeId);
							if (!"".equals(bookingDate))
							{
								param.put("BOOKING_DATE", bookingDate);
							}
						}

						param.put("SALEGOODS_IMEI", saleActiveInfo.getString("RES_CODE",""));
					}

					//营销活动转正式需要跳过工单互斥校验
					param.put("NO_TRADE_LIMIT", "TRUE");
					//add by zhangxing3 for REQ201810290024宽带开户界面增加手机号码套餐的判断
					param.put("NO_SALE_TRADE_LIMIT_642","TRUE");
					//add by zhangxing3 for REQ201810290024宽带开户界面增加手机号码套餐的判断

					IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
					String intfTradeId = result.getData(0).getString("TRADE_ID");

					UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeIdNew(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", intfTradeId,"1");

					//IMS固话营销活动
					IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "1887", packageIdPre, "0898");
					if(IDataUtil.isNotEmpty(commparaInfos))
					{
						//调用资源接口进行IMS固话终端实占
						occupyIMSTerminal(intfTradeId, saleActiveInfo);
					}
				}
            }else{
            	UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            }
        }
        
        //宽带提速，新增的gpon和ftth的4M宽带，自动绑定宽带1+活动
        //根据trade_id获取服务子台帐
        IDataset tradeSvcInfos = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        for (int i = 0, size = tradeSvcInfos.size(); i < size; i++)
        {
        	IData tradeSvcInfo = tradeSvcInfos.getData(i);
        	String serviceId = tradeSvcInfo.getString("SERVICE_ID");
        	String modifyTag = tradeSvcInfo.getString("MODIFY_TAG");
        	//通过524的配置，查看是否有开通的服务需要绑定营销活动的情况
        	IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "524", serviceId, mainTrade.getString("TRADE_EPARCHY_CODE"));
        	//如果有，则进行绑定
        	if(IDataUtil.isNotEmpty(commparaInfos)&&("0".equals(modifyTag)))
        	{
        		IData commparaInfo = commparaInfos.getData(0);
        		
        		//给宽带用户的手机号码绑活动
        		if ("KD_".equals(serialNumber.substring(0, 3)))
                {
                    serialNumber = serialNumber.substring(3);
                }
        		
        		IData param = new DataMap();
	            param.put("SERIAL_NUMBER", serialNumber);
	            param.put("PRODUCT_ID", commparaInfo.getString("PARA_CODE1"));
	            param.put("PACKAGE_ID", commparaInfo.getString("PARA_CODE2"));
	            param.put("TRADE_STAFF_ID", "SUPERUSR");
	            param.put("TRADE_DEPART_ID", "36601");
	            param.put("TRADE_CITY_CODE", "HNSJ");
	            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
        	}
        }

    }
    
    /**
     * 调用资源接口进行IMS固话终端实占
     * @param saleActiveInfo
     * @throws Exception 
     */
    private void occupyIMSTerminal(String tradeId, IData saleActiveInfo) throws Exception 
    {	
    	IDataset ds = UserSaleGoodsInfoQry.getByRelationTradeId(saleActiveInfo.getString("RELATION_TRADE_ID", ""));
        if(IDataUtil.isNotEmpty(ds))
        {
        	IData inparam = new DataMap(); ;
            inparam.put("RES_NO", ds.getData(0).getString("RES_CODE"));//串号
            inparam.put("SALE_FEE", "0");//销售费用:不是销售传0
            inparam.put("PARA_VALUE1", saleActiveInfo.getString("SERIAL_NUMBER"));//购机用户的手机号码
            inparam.put("PARA_VALUE7", "0");//代办费
            inparam.put("DEVICE_COST", ds.getData(0).getString("DEVICE_COST"));//进货价格--校验接口取
            inparam.put("TRADE_ID ",  tradeId);//台账流水 
            inparam.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
            inparam.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
            inparam.put("CONTRACT_ID",  tradeId);//销售订单号
            inparam.put("PRODUCT_MODE", "0");
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	inparam.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
        	inparam.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
        	inparam.put("PARA_VALUE14",  ds.getData(0).getString("DEVICE_COST"));//裸机价格  从检验接口取裸机价格
        	inparam.put("PARA_VALUE15", "0");//客户购机折让价格
        	inparam.put("PARA_VALUE16", "0");
        	inparam.put("PARA_VALUE17", "0");
        	inparam.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
        	inparam.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
        	inparam.put("STAFF_ID", "SUPERUSR");//销售员工
        	inparam.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(inparam);
        	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
        		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
        		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
        		}
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"家庭IMS固话终端实占接口调用异常！");
        	}
        }
	}
    
    private String getBookingDate(String tradeId) throws Exception 
    {	
    	String bookingDate = "";
    	IDataset ds = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        if(IDataUtil.isNotEmpty(ds))
        {
        	for (int i = 0 ; i <ds.size(); i++ )
        	{
        		String discntCode = ds.getData(i).getString("DISCNT_CODE", "");
        		String modifyTag = ds.getData(i).getString("MODIFY_TAG", "");
        		String endDate = ds.getData(i).getString("END_DATE", "");
        		if(("84010038".equals(discntCode) ||"84010039".equals(discntCode) || "84010040".equals(discntCode))
        				&& BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        		{
        			bookingDate = SysDateMgr.getFirstDayOfNextMonth(endDate);
        		}
        	}
        }
        return bookingDate;
	}

	/**
	 * REQ202003180001 “共同战疫宽带助力”活动开发需求
	 * 校验预受理活动是否完工是否在活动有效期内,不在有效期内不转正式
	 * @param productId
	 * @param packageId
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	private boolean queryValidSaleActiveConfigDate(String productId,String packageId,String tradeId) throws Exception {
		IDataset commpara3232 = CommparaInfoQry.getCommparaInfoByCode("CSM", "3232", productId, packageId,"0898");
		if(IDataUtil.isEmpty(commpara3232)){
			return true;
		}
		String startDate = commpara3232.first().getString("PARA_CODE2");
		String endDate = commpara3232.first().getString("PARA_CODE3");
		String dateType = commpara3232.first().getString("PARA_CODE4");

		String finishDate="";
		if("1".equals(dateType)){
			//1校验当前时间
			finishDate=SysDateMgr.getSysTime();
		}else if("2".equals(dateType)){
			//2校验PBOSS回单时间
			IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
			if (IDataUtil.isEmpty(finishInfos)){
				CSAppException.apperr(WidenetException.CRM_WIDENET_14);
			}
			finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
		}else{
			finishDate=SysDateMgr.getSysTime();
		}
		if(SysDateMgr.compareTo(endDate,finishDate)>=0){
			return true;
		}
		return false;
	}
}
