
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction.ChangeSpeedFinishAction;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


/**
 * 1、	如果用户原来的设备属于TF_F_GARDEN_DEVICE_INFO 并且移机到FTTH且带宽小于100M的，免费给用户提速到100M，使用一年。一年后恢复到原来的带宽
 * 提速接口
 * 
 */
public class ChangeFTTHSpeedAction implements ITradeAction
{
	public static final Logger logger=Logger.getLogger(ChangeSpeedFinishAction.class);

	@SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String serial_number = btd.getMainTradeData().getSerialNumber();
        String new_product_id = btd.getMainTradeData().getProductId();
		List<OtherTradeData> otherList = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
    	List<WideNetTradeData> widenetList = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
		//新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(new_product_id);
        if (Integer.valueOf(new_rate) < 102400){//带宽小于100M的
        	boolean isFTTH = false;
        	if(!otherList.isEmpty() && otherList.size()>0){
				for (int i = 0; i < otherList.size(); i++) {
					OtherTradeData data = otherList.get(i);
	        		String modifyTag = data.getModifyTag();
	        		String rsrvValueCode = data.getRsrvValueCode();
	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD) && rsrvValueCode.equals("FTTH")){
	        			isFTTH = true;
	        		}
				}
			}
        	
        	if(serial_number.startsWith("KD_")){
        		serial_number = serial_number.substring(3);
        	}
        	IData param=new DataMap();
        	param.put("SERIAL_NUMBER", serial_number);
        	//查询宽带账号的原设备ID
        	IDataset userDeviceIdInfos = CSAppCall.call("PB.AddressManageSvc.queryDeviceByAccount", param);
        	if(IDataUtil.isNotEmpty(userDeviceIdInfos)){
        		String deviceId = userDeviceIdInfos.getData(0).getString("DEVICE_ID");
        		IData param1=new DataMap();
        		param1.put("DEVICE_CODE", deviceId);
        		param1.put("REMOVE_TAG", "0");
        		param1.put("ACTIVITY_CODE", "60001");
        		//宽带账户的原设备ID在高价值小区的
        		IDataset infos = CSAppCall.call("SS.GardenDeviceInfoSVC.qryGardenDeviceInfo", param1);
        		if(IDataUtil.isNotEmpty(infos) && isFTTH){
        			if(!widenetList.isEmpty() && widenetList.size()>0){
        				for (int i = 0; i < widenetList.size(); i++) {
        					WideNetTradeData data = widenetList.get(i);
        	        		String modifyTag = data.getModifyTag();
        	        		if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
        	        			data.setRsrvNum4("102400");
        	        			data.setRsrvStr5("1");//“是否B改H提速百兆”标识
        	        		}
        				}
        			}
        		}
        	}
        }
    }
}