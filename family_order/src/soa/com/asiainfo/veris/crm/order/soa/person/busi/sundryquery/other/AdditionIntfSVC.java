
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.firstcall.FirstCallTimeSVC;
/**
 * @description 补充接口调用服务
 * @author tz
 * @date 2019-02-14
 *  */
public class AdditionIntfSVC extends CSBizService
{
	/**
	 * 
	 * @description 花卡激活调用能开补充调用接口，仅供测试调用
	 * @param 
	 * @return IData
	 * @author tanzheng
	 * @date 2019年2月14日
	 */
    private static Logger logger = Logger.getLogger(FirstCallTimeSVC.class);
	public IData sysnAbilityForWrong(IData input) throws Exception {
		String iv_serial_number = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData ucaData = UcaInfoQry.qryUserMainProdInfoBySn(iv_serial_number);
		String productId = ucaData.getString("PRODUCT_ID");
		IDataset productDataSet = ProductInfoQry.qryProductByUserIdAndProId(ucaData.getString("USER_ID"),productId);
		if(IDataUtil.isEmpty(productDataSet)){
			CSAppException.appError("-1", "查询产品出错！");
		}
		IDataset comparas =CommparaInfoQry.getCommparaByCode1("CSM","2578",productId,"ZZZZ");
		if(IDataUtil.isEmpty(comparas)){
			CSAppException.appError("-1", "用户办理的不是花卡产品，请确认！");
		}
		IData tempData = comparas.getData(0);
		String Abilityurl = "";
		IData param1 = new DataMap();
	    param1.put("PARAM_NAME", "crm.ABILITY.CIP85");
	    StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
		IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		if (Abilityurls != null && Abilityurls.size() > 0)
		{
			Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
		}
		else
		{
			CSAppException.appError("-1", "crm.ABILITY.CIP85接口地址未在TD_S_BIZENV表中配置");
		}
		String apiAddress = Abilityurl;
		IData param = new DataMap();
		String iv_opendate = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND_SHORT);
		param.put("number",iv_serial_number);//号卡号码
		param.put("numberActivateTime",iv_opendate);//号卡激活时间
		param.put("planId",tempData.getString("PARA_CODE4"));//主套餐编码,转换为一级能开产品ID
		param.put("planName",tempData.getString("PARA_CODE3"));//号码主套餐
		String startDate =  ((IData)productDataSet.get(0)).getString("START_DATE");
		param.put("startTime",startDate.replaceAll("-","").replaceAll(":","").replaceAll(" ",""));//套餐生效时间


		logger.debug("调用能开参数："+param.toString());
		IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,param);
		IData result = new DataMap();
		result.put("param", param.toString());
		result.put("result", stopResult);
		
		
		return result;
		
	}

}
