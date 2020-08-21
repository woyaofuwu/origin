/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.requestdata.HEMUTerminalReqData;


/**
 * 
 * @author tanzheng
 *
 */
public class HEMUTerminalManageTrade extends BaseTrade implements ITrade
{
	public static Logger logger = Logger.getLogger(HEMUTerminalManageTrade.class);
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	HEMUTerminalReqData hrd = (HEMUTerminalReqData) btd.getRD();
    	buildSaleGoodsTrade(btd,hrd);
    }
	/**
	 * @Description：创建sale_goods台账
	 * @param:@param btd
	 * @param:@param hrd
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-17下午05:03:47
	 */
	private void buildSaleGoodsTrade(BusiTradeData btd, HEMUTerminalReqData hrd) throws Exception {
		String actionType = hrd.getAction_type();//定义在static表中HEMU_TERMINAL_TRADE_TYPE---1：换机，2：退机，3：申领
		UcaData uca = hrd.getUca();
		btd.getMainTradeData().setRsrvStr4(actionType);//操作类型
		btd.getMainTradeData().setRsrvStr2(hrd.getIsHSW());//是否为和商务用户
		btd.getMainTradeData().setRsrvStr3(uca.getSerialNumber());//手机号码号
		btd.getMainTradeData().setRsrvStr1(hrd.getTerminalId());//串号
		List<SaleGoodsTradeData> saleGoodsTDList = uca.getUserSaleGoods();
		SaleGoodsTradeData ownSaleGoods = new SaleGoodsTradeData();
		ownSaleGoods = findOwnGoods(ownSaleGoods,saleGoodsTDList,hrd);
		if("1".equals(actionType)){//换机
			btd.getMainTradeData().setRemark("和目终端换机");
			btd.getMainTradeData().setRsrvStr5(hrd.getOldTerminalId());//操作类型
			if("0".equals(hrd.getIsHSW())){
				ownSaleGoods.setResCode(hrd.getTerminalId());
				ownSaleGoods.setGoodsName(hrd.getTerminalName());
				ownSaleGoods.setRemark("和目终端换机");
				ownSaleGoods.setModifyTag(BofConst.MODIFY_TAG_UPD);
				btd.add(uca.getSerialNumber(), ownSaleGoods);
			}else{
				IData  data = findHemuOther(uca.getUserId());
				OtherTradeData otherTradeData = new OtherTradeData();
	            otherTradeData.setRsrvValueCode("HEMU");
	            otherTradeData.setRsrvValue("HEMU_APPLY");
	            otherTradeData.setUserId(uca.getUserId());
	            otherTradeData.setInstId(hrd.getInstId());
	            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD); 
	            otherTradeData.setRemark("和目终端换机"); //终端串号
	            otherTradeData.setRsrvStr1(hrd.getTerminalId());//终端串号
	            otherTradeData.setStartDate(data.getString("START_DATE"));
	            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
	            otherTradeData.setRsrvStr3(uca.getSerialNumber());//手机号码
	            otherTradeData.setRsrvStr6("");//终端型号
	            otherTradeData.setRsrvStr2("100");//押金
	            otherTradeData.setRsrvStr7("0");//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
	            otherTradeData.setRsrvStr8(hrd.getDepositTradeId());//BOSS押金转移流水
	            otherTradeData.setRsrvStr9(hrd.getTerminalName());//终端名称
	            otherTradeData.setRsrvTag2("2");//终端状态:1:申领，2:换机，3:退机
	            btd.add(uca.getSerialNumber(), otherTradeData);
			}
			
		}else if ("2".equals(actionType)){//退机
			btd.getMainTradeData().setRemark("和目终端退机");
			btd.getMainTradeData().setRsrvStr5(hrd.getOldTerminalId());//操作类型
			
			if("0".equals(hrd.getIsHSW())){
				ownSaleGoods.setRemark("和目终端退机");
				ownSaleGoods.setCancelDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				ownSaleGoods.setModifyTag(BofConst.MODIFY_TAG_UPD);
				btd.add(uca.getSerialNumber(), ownSaleGoods);
				
			}else{
				IData data = findHemuOther(uca.getUserId());
				OtherTradeData otherTradeData = new OtherTradeData();
	            otherTradeData.setRsrvValueCode("HEMU");
	            otherTradeData.setRsrvValue("HEMU_APPLY");
	            otherTradeData.setUserId(uca.getUserId());
	            otherTradeData.setInstId(hrd.getInstId());
	            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL); 
	            otherTradeData.setRemark("和目终端退机"); //终端串号
	            otherTradeData.setRsrvStr1(hrd.getTerminalId());//终端串号
	            otherTradeData.setStartDate(data.getString("START_DATE"));
	            otherTradeData.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	            otherTradeData.setRsrvStr6("");//终端型号
	            otherTradeData.setRsrvStr2("0");//押金
	            otherTradeData.setRsrvStr7("2");//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
	            otherTradeData.setRsrvStr8(btd.getTradeId());//BOSS押金转移流水
	            btd.getMainTradeData().setRsrvStr6(hrd.getDepositTradeId());//BOSS押金冻结转移流水
	            otherTradeData.setRsrvTag2("3");//终端状态:1:申领，2:换机，3:退机
	            btd.add(uca.getSerialNumber(), otherTradeData);
			}
		}else if ("3".equals(actionType)){//申领
			btd.getMainTradeData().setRemark("和目终端申领");
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setRsrvValueCode("HEMU");
            otherTradeData.setRsrvValue("HEMU_APPLY");
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
            otherTradeData.setRemark("和目终端申领"); //终端串号
            otherTradeData.setRsrvStr1(hrd.getTerminalId());//终端串号
            otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setRsrvStr3(uca.getSerialNumber());//手机号码
            otherTradeData.setRsrvStr9(hrd.getTerminalName());//终端名称
            otherTradeData.setRsrvStr2("100");//押金
            otherTradeData.setRsrvStr7("0");//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
            otherTradeData.setRsrvTag2("1");//光猫状态:1:申领，2:更改，3:退还，4:丢失
            
            //调用账户相关接口
            IDataset checkCash= AcctCall.queryAccountDepositBySn(uca.getSerialNumber());
	    	int cash = 0;
	    	if(DataSetUtils.isNotBlank(checkCash)){
	    		for(int i = 0 ; i < checkCash.size() ; i++){
	    			cash = cash + Integer.parseInt(checkCash.getData(i).getString("DEPOSIT_BALANCE","0"));
	    		}
	    	}
	    	if(cash<10000){
	    		CSAppException.appError("61311", "账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(String.valueOf(cash))/100+"元，押金金额：100元");
	    	}else{
	    		//5、调账务提供的接口将现金存折的钱转到宽带光猫押金存折； 
	    		IData inparams=new DataMap();
	    		//获取转账存折
				StringBuffer depositeNotes=new StringBuffer();
				IDataset noteDatas=CommparaInfoQry.getCommNetInfo("CSM", "1627", "TOP_SET_BOX_NOTES");
				if(IDataUtil.isNotEmpty(noteDatas)){
					
					for(int i=0,size=noteDatas.size();i<size;i++){
						IData noteData=noteDatas.getData(i);
						
						depositeNotes.append(noteData.getString("PARA_CODE1"));
						if(i<size-1){
							depositeNotes.append("|");
						}
					}
				}else{
					CSAppException.appError("39102", "取转账存折错误！");
				}
	    		inparams.put("SERIAL_NUMBER", uca.getSerialNumber());
	    		inparams.put("OUTER_TRADE_ID", btd.getRD().getTradeId());
	    		inparams.put("DEPOSIT_CODE_OUT", depositeNotes.toString());
	    		inparams.put("DEPOSIT_CODE_IN", "9037"); 
	    		inparams.put("TRADE_FEE", 10000);
	    		inparams.put("CHANNEL_ID", "15000");
	    		inparams.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
	    		inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
	    		inparams.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
	    		inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
	    		inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
	    		IData inAcct=AcctCall.transFeeInADSL(inparams);
	    		String result=inAcct.getString("RESULT_CODE","");
	    		if(!"".equals(result) && "0".equals(result)){
	    			// 成功！ 处理other表
	    			otherTradeData.setRsrvStr8(btd.getTradeId());//BOSS押金转移流水
	    			btd.add(uca.getSerialNumber(), otherTradeData);
	    		}else{
	    			CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金错误:"+inAcct.getString("RESULT_INFO"));
	    		}
	    	}
            
            
            
            
			
		}
		
	}
	/**
	 * @Description：
	 * @param:@param userId
	 * @param:@return
	 * @return OtherTradeData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-19下午04:40:47
	 */
	private IData findHemuOther(String userId) throws Exception {
		
		IData data = null;
		IDataset other = UserOtherInfoQry.queryUserOtherInfos(userId, "HEMU", "HEMU_APPLY");
		if(IDataUtil.isNotEmpty(other)){
			data = (IData) other.first();
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"找不到对应的用户资料信息！");
		}
		
		return data;
	}
	/**
	 * @Description：获取用户的sale_goods信息
	 * @param:@param ownSaleGoods
	 * @param:@param saleGoodsTDList
	 * @param:@param hrd
	 * @param:@return
	 * @return SaleGoodsTradeData
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-18下午04:08:40
	 */
	private SaleGoodsTradeData findOwnGoods(SaleGoodsTradeData ownSaleGoods,
			List<SaleGoodsTradeData> saleGoodsTDList, HEMUTerminalReqData hrd) {
		if(saleGoodsTDList!=null && saleGoodsTDList.size()>0){
			
			 for (int i = saleGoodsTDList.size() - 1; i >= 0; i--)
	            {
	                SaleGoodsTradeData saleGoodsTD = saleGoodsTDList.get(i);
	                if (hrd.getInstId().equals(saleGoodsTD.getInstId()))
	                {
	                	ownSaleGoods = saleGoodsTD.clone();
	                    break;
	                }
	            }
			
		}
		return ownSaleGoods;
	}


}
