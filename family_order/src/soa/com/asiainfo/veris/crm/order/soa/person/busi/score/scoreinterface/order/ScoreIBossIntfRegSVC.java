
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.CheckIdentityUtil;

public class ScoreIBossIntfRegSVC extends OrderService
{

	@Override
    // 积分充值 积分回退 冲正交易 积分扣减 用同一个业务类型编码
    public String getOrderTypeCode() throws Exception
    {
        return "329";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "329";
    }
    
    
    @Override
    public final void setTrans(IData input) throws Exception
    {
    	String serial = input.getString("SERIAL_NUMBER","");
		String serial_number = input.getString("MOBILE","");
		if(!"".equals(serial)){
			//return;
		}else if("".equals(serial)){
			input.put("SERIAL_NUMBER", serial_number);
		}
		//验证是否是积分商城使用，然后鉴权
		CheckIdentityUtil.checkIdentitySvc4TradeReg(input, input.getString("SERIAL_NUMBER"));
		
		checkPointTypeInfo(input);
    }
    
    private final void checkPointTypeInfo(IData input) throws Exception
	{
		// 扣减积分类型明细信息(包括PointType用户积分类型、PayPoint扣减积分值)
		if (!"".equals(input.getString("POINT_TYPE_INFO", ""))) 
		{
			IData PointTypeMap = new DataMap();
			IDataset PointTypeInfo = input.getDataset("POINT_TYPE_INFO");
			for (int i = 0; i < PointTypeInfo.size(); i ++) 
			{
				IData item = PointTypeInfo.getData(i);
				String PointType = IDataUtil.chkParam(item, "POINT_TYPE");
				//String PayPoint = IDataUtil.chkParam(item, "PAY_POINT");
				
				if (!("00".equals(PointType) || "01".equals(PointType) || "02".equals(PointType))) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户积分类型只能00、01、02");
				}
				
				PointTypeMap.put(PointType, PointType);
			}
			
			if (PointTypeMap.containsKey("00") && (PointTypeMap.containsKey("01") || PointTypeMap.containsKey("02")))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户积分类型00与01或02不能同时存在");
			}
		}		
	}
}
