package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changesvcstate.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class DealMigratoryBirdsAction implements ITradeAction {
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		String discntCode = "84071446";//度假宽带40元月功能费
		Boolean TAG = true;
		Boolean TAG2 = false;
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		//TF_B_TRADE_USER.RSRV_STR10 = BNBD,代表商务宽带，跳出action
		String rsrvStr10 = "";
		List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
		if(userTradeDatas != null && userTradeDatas.size() > 0)
	    {
			rsrvStr10 = userTradeDatas.get(0).getRsrvStr10();
	    }
		//System.out.println("-------------DealMigratoryBirdsAction------------TF_B_TRADE_USER.RSRV_STR10:"+rsrvStr10);
		if("BNBD".equals(rsrvStr10))
		{
			return;
		}

        DiscntTradeData newDiscnt = new DiscntTradeData();
        
        //1.如果用户存在有效的候鸟活动、宽带1+、宽带包年优惠时，设置TAG为false，不绑候鸟包天套餐
        String mobileNumber = "";
        if(serialNumber.startsWith("KD_"))
        {
        	mobileNumber = serialNumber.substring(3);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(mobileNumber); 
        IDataset ids = UserSaleActiveInfoQry.querySaleActiveByUserIdProcess(ucaData.getUserId(),"0");
        if (IDataUtil.isNotEmpty(ids)){
        	for ( int i =0; i < ids.size() ; i++)
        	{
        		String productIdSaleActive = ids.getData(i).getString("PRODUCT_ID", "");
        		if ("66000602".equals(productIdSaleActive) || "69908001".equals(productIdSaleActive) || "67220428".equals(productIdSaleActive)
        				|| "66004809".equals(productIdSaleActive))
    			{
        			TAG = false;
    			}
        	}
        }
        //2.查询用户是否存在优惠84003439候鸟减免50M宽带套餐  ，如果存在才绑定候鸟包天套餐，否则不绑!
        IDataset ids2 = UserSaleActiveInfoQry.checkUserDJKDActive(ucaData.getUserId());
        if (IDataUtil.isNotEmpty(ids2) && TAG){
        	String djkdActive = ids2.getData(0).getString("PRODUCT_ID", "");
        	if("66004809".equals(djkdActive))
        	{
        		TAG2 = true;
        	}
        }
        
        List<DiscntTradeData> discntData5 = ucaData.getUserDiscntByDiscntId("84071456");
        
        if ( TAG2 && ArrayUtil.isNotEmpty(discntData5)){
        	//判断关联的手机号码目前是否为欠费停机状态，如是则提示用户须交清费用后开机；
        	//若手机号码为报停状态则提示用户手机号码报开后才能宽带报开
        	SvcStateTradeData svcStateTradeData = ucaData.getUserSvcsStateByServiceId("0");
        	if(svcStateTradeData != null)
        	{
        		String mobileMainStateCode = svcStateTradeData.getStateCode();
        		if("1".equals(mobileMainStateCode))
        		{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户手机号码为报停状态，用户手机号码报开后才能宽带报开！"+mobileMainStateCode);
        		}
        		if("5".equals(mobileMainStateCode))
        		{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户手机号码为欠费停机状态，请缴清费用后再办理宽带开机业务！"+mobileMainStateCode);

        		}
        	}
            List<DiscntTradeData> discntData4 = ucaData.getUserDiscntByDiscntId(discntCode);
        	if(ArrayUtil.isEmpty(discntData4))
        	{
	        	newDiscnt.setElementId(discntCode);        	
	        	newDiscnt.setUserId(ucaData.getUserId());
	            newDiscnt.setUserIdA("-1");
	            newDiscnt.setProductId("-1");
	            newDiscnt.setPackageId("-1");
	        	newDiscnt.setInstId(SeqMgr.getInstId());
	            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	            newDiscnt.setSpecTag("0");
	            newDiscnt.setStartDate(SysDateMgr.getFirstDayOfThisMonth4WEB());
	            newDiscnt.setEndDate(SysDateMgr.getLastDateThisMonth4WEB());
	            newDiscnt.setRemark("宽带报开时，绑定度假宽带40元月功能费套餐！");
	            btd.add(mobileNumber, newDiscnt);
        	}
            //add by zhangxing3 for 度假宽带2019用户自动停机后，报开时需要关联魔百和报开
    		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(ucaData.getUserId(), "4", "J");
    		if(IDataUtil.isNotEmpty(boxInfos)){   			
    			IData boxInfo=boxInfos.first();
        		String stopSignal=boxInfo.getString("RSRV_TAG3","");
                IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3901", ucaData.getUserId(), "0");
        		if(stopSignal.equals("1") && IDataUtil.isEmpty(outDataset)){
        			IData param1 = new DataMap();
    				param1.put("SERIAL_NUMBER", mobileNumber);
    	            param1.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
    	            param1.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
    	            param1.put("TRADE_TYPE_CODE", "3901");	            
    	            try{
    	            	IDataset result = CSAppCall.call("SS.StartTopSetBoxRegSVC.tradeReg", param1);
    	            }catch (Exception e) {
    	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,e.toString());
    				}
                }
    			
    			
            } 
            //add by zhangxing3 for 度假宽带2019用户自动停机后，报开时需要关联魔百和报开
        }
        
	}

}
