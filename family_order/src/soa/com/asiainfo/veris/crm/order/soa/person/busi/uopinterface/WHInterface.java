package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class WHInterface extends CSBizService{
	/** 
	 *  add by wuhao5 对外接口专用
	 */
	private static final long serialVersionUID = 1L;

	public IData qryStaffInfo(IData input){
		IData idata = new DataMap();		
		String tradeCityCode = "";
		String tradeCityName = "";
		String xResultCode = "0000";
		String xResultInfo = "成功!";		
		try{
			IDataUtil.chkParam(input, "TRADE_STAFF_ID");
			input.put("STAFF_ID", input.getString("TRADE_STAFF_ID"));
			IDataset ids = Dao.qryByCode("TD_M_STAFF", "SEL_VALID_BY_PK", input, Route.CONN_SYS);
			if(IDataUtil.isNotEmpty(ids) && ids.size() > 0){
				IData temp = ids.getData(0);
				System.out.print(temp.toString());
				tradeCityCode = temp.getString("CITY_CODE");				
				tradeCityName = StaticUtil.getStaticValue(null,"TD_M_AREA", "AREA_CODE","AREA_NAME",temp.getString("CITY_CODE"));
				idata.put("TRADE_CITY_CODE", tradeCityCode);
				idata.put("TRADE_CITY_NAME", tradeCityName);
			}
		}catch(Exception e){			
			xResultCode = "2999";
			if(e.getMessage() != null && e.getMessage().length() > 30){
				xResultInfo = e.getMessage().substring(0,30);
			}else{
				xResultInfo = e.getMessage();
			}			
		}
		idata.put("X_RESULTCODE", xResultCode);
		idata.put("X_RESULTINFO", xResultInfo);
		return idata;
	}

    /**
     * 关于开发集团成员5G套餐预约办理界面的需求
     * 用户预约套餐查询接口 add by wuhao5
     * @param input
     * @return
     */
	public IData selBookProduct(IData input){
		IData idata = new DataMap();
        String bookMainProductId = "";
        String bookMainProductName = "";
        boolean bookTag = false;
        String xResultCode = "0000";
		String xResultInfo = "接口调用成功";

		try{
			String sysDate = SysDateMgr.getSysTime();
			String sn = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            UcaData uca = UcaDataFactory.getNormalUca(sn);
			IDataset productInfos = UserProductInfoQry.queryUserMainProduct(uca.getUserId());
			if (IDataUtil.isNotEmpty(productInfos) && productInfos.size() > 0) {
				for (int i = 0 ; i < productInfos.size() ; i ++) {
					IData productInfo = productInfos.getData(i);
					if (productInfo.getString("START_DATE").compareTo(sysDate) > 0) {
						bookMainProductId = productInfo.getString("PRODUCT_ID");
						bookTag = true;
						IData productName = UProductInfoQry.getProductInfo(bookMainProductId);
						if(productName != null && IDataUtil.isNotEmpty(productName)){
							bookMainProductName = productName.getString("PRODUCT_NAME");
						}else {
							xResultCode = "2998";
							xResultInfo = "获取不到该产品信息";
						}
						break;
					}
				}
			}
		}catch(Exception e){
			xResultCode = "2999";
			if(e.getMessage() != null && e.getMessage().length() > 100){
				xResultInfo = e.getMessage().substring(0,100);
			}else{
				xResultInfo = e.getMessage();
			}
		}
		idata.put("RSP_CODE", xResultCode);
		idata.put("RSP_DESC", xResultInfo);
		idata.put("BOOK_TAG",bookTag ? "1" : "0");//用户预约标识 0--没有，1--有
		idata.put("PRODUCT_NAME", bookMainProductName);//产品名称
		idata.put("PRODUCT_ID", bookMainProductId);//产品编码
		return idata;
	}
}
