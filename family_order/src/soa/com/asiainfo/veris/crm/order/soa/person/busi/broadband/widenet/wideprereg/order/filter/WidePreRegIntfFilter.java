
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.filter;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 宽带需求登记 接口参数转换
 * @author yuyj3
 *
 */
public class WidePreRegIntfFilter implements IFilterIn
{


    public void transferDataInput(IData input) throws Exception
    {
    	
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IDataUtil.chkParam(input, "CUST_NAME");
    	IDataUtil.chkParam(input, "CUST_PONE");
    	IDataUtil.chkParam(input, "BAND_WIDTH");
        IDataUtil.chkParam(input, "Reason");
        
        //地市编码
        IDataUtil.chkParam(input, "CityCode");
        //地市
        IDataUtil.chkParam(input, "City");
        //区县编码
        IDataUtil.chkParam(input, "CountyCode");
        //区县
        IDataUtil.chkParam(input, "County");
        //街道
        IDataUtil.chkParam(input, "Town");
        //小区
        IDataUtil.chkParam(input, "Village");
        //楼栋
        IDataUtil.chkParam(input, "Building");
        //房号，房号可以不填  
        // IDataUtil.chkParam(input, "Room");
        
        /**
    	 * REQ201806270003+在智能CRM（APP）新增宽带需求收集界面——进行接口改造 --mengqx--20180720
    	 * 
    	 */
        //------------------------------------------校验参数	-----------------------------------------------
		//联系人电话：只能输入数字；
		String contactSN = input.getString("CUST_PONE");
		boolean flag = contactSN.matches("[0-9]+");
		if(!flag){
			CSAppException.apperr(CrmUserException.CRM_USER_783,"联系人电话号码只能输入数字!");
		}
		
		//联系人姓名：不能输入特殊字符；
		String custName = input.getString("CUST_NAME");
	    String specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》！？…（）—、。.；：";
		for(int i=0; i < specialStr.length();i++)
		{
			if (custName.indexOf(specialStr.charAt(i)) > -1)
			{
				CSAppException.apperr(CrmUserException.CRM_USER_783,"联系人姓名包含特殊字符！");
			}
		}
		
		//预装原因校验
		String preCause = input.getString("Reason");
		String preCauseValue =  StaticUtil.getStaticValue("WIDE_PRE_CAUSE", preCause);
		if(preCauseValue == null || preCauseValue.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_783,"预装原因不正确！");
		}
		
		//申请带宽校验
		String wbbw = input.getString("BAND_WIDTH");
		String capacityValue =  StaticUtil.getStaticValue("WIDE_CAPACITY", wbbw);
		if(capacityValue == null || capacityValue.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_783,"申请宽带带宽不正确！");
		}
		//------------------------------------------校验参数	end-----------------------------------------------

        
        String cityCode = input.getString("CityCode");
        String city = input.getString("City");
        String countyCode = input.getString("CountyCode");
        String county = input.getString("County");
        String town = input.getString("Town");
        String village = input.getString("Village");
        String building = input.getString("Building");
        String room = input.getString("Room","");
        
        String setAddr = "";
        String homeAddr = "";
        
    	if(city.equals(county))
    	{
    		setAddr = county + town + village + building + room;
    	}
    	else
    	{
    		setAddr = city + county + town + village + building + room;
    	}
    	
    	homeAddr = county + town + village + building;
    	
    	//同个服务号码，同个地址，报错不能重复提交相同内容！
		//表TF_F_WIDENET_BOOK
		//服务号码serialNumber : RSRV_STR2 和 地址setAddr : SET_ADDR
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		inparam.put("SET_ADDR", setAddr);
		IDataset result = Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_BY_SET_ADDR_AND_RSRV_STR2", inparam);
		if(result.getData(0).getInt("COUNT") != 0){
			CSAppException.apperr(CrmUserException.CRM_USER_783,"不能重复提交相同内容！");
		}
    	
        StringBuilder addrCode = new StringBuilder(1000);
        addrCode.append(cityCode);
        addrCode.append(",");
        addrCode.append(countyCode);
        addrCode.append(",");
        addrCode.append(",");
        
        input.put("HOME_ADDR", homeAddr);
    	input.put("SET_ADDR", setAddr);
        input.put("ADDR_CODE", addrCode.toString());
        input.put("AREA_CODE", cityCode);
        input.put("CONTACT_SN", input.getString("CUST_PONE"));
        input.put("WBBW", input.getString("BAND_WIDTH"));
        input.put("PRE_CAUSE", input.getString("Reason"));
    }
    
    
}
