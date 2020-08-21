package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.noPhoneRenewTopSetBox.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.noPhoneRenewTopSetBox.order.requestdata.NoPhoneRenewTopSetBoxReqData;

public class NoPhoneRenewTopSetBoxTrade extends BaseTrade implements ITrade {

	
	
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		NoPhoneRenewTopSetBoxReqData req = (NoPhoneRenewTopSetBoxReqData)bd.getRD(); 
		
		appendTradeMainData(bd);
		
		/*
		 * 开启平台服务
		 * 订购暂停保号服务、优惠，包括服务属性
		 * 在资源表当中做记录说明
		 */
		UcaData uca=bd.getRD().getUca();
		
		String userId=uca.getUserId();
		
		//查询用户资源表 
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
		IData boxInfo=boxInfos.first();
		
		/*
		 * 修改必选服务预存字段
		 */
		String basePlatSvcIdTemp=boxInfo.getString("RSRV_STR2","");	//必选套餐
		if(!basePlatSvcIdTemp.equals("")&&basePlatSvcIdTemp.indexOf(",")!=-1){
			String[] basePlatSvcIdArr=basePlatSvcIdTemp.split(",");
			if(basePlatSvcIdArr!=null&&basePlatSvcIdArr.length>0){
				String basePlatSvcId=basePlatSvcIdArr[0];
				if(basePlatSvcId!=null&&!basePlatSvcId.trim().equals("")){
					
					IDataset userBaseServices=UserPlatSvcInfoQry.
							queryUserPlatSvcByUserIdAndServiceId(userId, basePlatSvcId);
					if(IDataUtil.isNotEmpty(userBaseServices)){
						PlatSvcTradeData platSvcTradeData=new PlatSvcTradeData(userBaseServices.getData(0));
						
						platSvcTradeData.setOprSource("08");
						platSvcTradeData.setIsNeedPf("0");
						if(req.getTopSetBoxStateTag().equals("0"))  //未欠费  ，续费
						{
							String time = platSvcTradeData.getRsrvStr4();
							String fee =  platSvcTradeData.getRsrvStr5();
							platSvcTradeData.setRsrvStr4(String.valueOf(Integer.valueOf(time)+Integer.valueOf(req.getTopSetBoxTime())));
							platSvcTradeData.setRsrvStr5(String.valueOf(Long.valueOf(fee)+Long.valueOf(req.getTopSetBoxFee())));
						}
						else if(req.getTopSetBoxStateTag().equals("2"))  // 欠费停机之后续费
						{	 
							platSvcTradeData.setRsrvStr4(req.getTopSetBoxTime());
							platSvcTradeData.setRsrvStr5(req.getTopSetBoxFee());
							platSvcTradeData.setRsrvStr6(SysDateMgr.getSysDateYYYYMMDD());
						}
						
						platSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
						bd.add(uca.getSerialNumber(), platSvcTradeData);
					}else{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
					}
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
				}
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在必选基本套餐服务！");
		}
	}
	

    /**
     * 修改主台帐字段
     * @author zhengkai5
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        NoPhoneRenewTopSetBoxReqData rd = (NoPhoneRenewTopSetBoxReqData)btd.getRD(); 
        btd.getMainTradeData().setRsrvStr4(rd.getTopSetBoxTime());
        btd.getMainTradeData().setRsrvStr5(rd.getTopSetBoxFee());
        btd.getMainTradeData().setRsrvStr6(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    }
}
