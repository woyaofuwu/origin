package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.before;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class DealDayDiscntAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
		String serialNumber = btd.getRD().getPageRequestData().getString("SERIAL_NUMBER","");
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	//找不到就是集团商务宽带，集团宽带没有候鸟
    	if(IDataUtil.isEmpty(userInfo)){
    		return;
    	}
    	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
		String discntCodeList = "84013241,84013242";
		//查询用户是否存在候鸟按天计费套餐  ，如果存在终止。
        List<DiscntTradeData> discntData6 = ucaData.getUserDiscntsByDiscntCodeArray(discntCodeList);  
        System.out.println("==============DealBirdsDiscntAction==========discntData6:"+discntData6);
	    if (ArrayUtil.isNotEmpty(discntData6)){
	        	for (int i =0; i < discntData6.size(); i++){
		        	DiscntTradeData delDiscntTD6 = discntData6.get(i).clone();	
		        	String discntCode = delDiscntTD6.getDiscntCode();
		        	delDiscntTD6.setRsrvStr1(delDiscntTD6.getEndDate());
		        	delDiscntTD6.setRsrvStr2("1");
		        	if("1605".equals(tradeTypeCode))
		        	{
				        //获取预约时间
				        String ordertime = btd.getRD().getPageRequestData().getString("DESTORYTIME","");
				        System.out.println("==============DealBirdsDiscntAction==========DESTORYTIME:"+ordertime);

				        if(!"".equals(ordertime))
				    	{
				    		String ordertime2 =SysDateMgr.getLastSecond(ordertime);//优惠截止时间
		        		    delDiscntTD6.setEndDate(ordertime2);
				    	}
				        else
				        {
				        	delDiscntTD6.setEndDate(SysDateMgr.getSysTime());
				        }
		        	}
		        	else if ("601".equals(tradeTypeCode)){
		        		
		        		//获取预约时间
				        String ordertime = btd.getRD().getPageRequestData().getString("BOOKING_DATE","");
				        System.out.println("==============DealBirdsDiscntAction==========BOOKING_DATE:"+ordertime);
				        if(!"".equals(ordertime))
				    	{
				    		String ordertime2 =SysDateMgr.getLastSecond(ordertime);//优惠截止时间
		        		    delDiscntTD6.setEndDate(ordertime2);
				    	}
				        else
				        {
				        	delDiscntTD6.setEndDate(SysDateMgr.getSysTime());
				        }
		        	}
		        	else
		        	{
				        delDiscntTD6.setEndDate(SysDateMgr.getSysTime());
		        	}
		        	delDiscntTD6.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        	delDiscntTD6.setRemark("终止按天计费套餐！");
		            btd.add(serialNumber, delDiscntTD6);
	        	}
		}     
	}

}
