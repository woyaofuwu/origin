package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.MergeWideUserCreateSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.intf.PersonCallResIntfSVC;

/**
 * 海南宽带新装专区
 * 工具类
*/
public class BroadBandUtilHNKDZQ {
	/**
	 * 响应结果
	 * @param result
	 * @return
	 */
	public static IData responseData (IData result)
	{
		IData output = new DataMap();
		IData object = new DataMap();
		object.put("respCode", "0");
		object.put("respDesc", "success");
		object.put("result", result);
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
		return output;
	}
	/**
	 * 响应查询结果
	 * @param result
	 * @return
	 */
	public static IData responseQryData (IData result)
	{
		IData output = new DataMap();
		IData object = new DataMap();
		if("1".equals(result.getString("flag"))){
			object.put("respCode", "0");
			object.put("respDesc", "success");
			result.remove("flag");
			result.remove("desc");
			object.put("result", result);
		}else{
			object.put("respCode", "-1");
			object.put("respDesc", result.getString("desc"));
			result.remove("flag");
			result.remove("desc");
			object.put("result", result);
		}
		
		output.put("rtnCode", "0");
		output.put("rtnMsg", "成功！");
		output.put("object", object);
		return output;
	}
	/**
	 * 查询并设定登陆员工信息
	 * @param crmpfPubInfo
	 * @param visit
	 * @return
	 * @throws Exception
	 */
	public static IData qryLoginStaffInfo(IData crmpfPubInfo,BizVisit visit) throws Exception{
		IData staffParam=new DataMap();
		staffParam.put("STAFF_ID", crmpfPubInfo.getString("staffId"));
		IDataset staffDataSet=PersonCallResIntfSVC.callRes("RCF.resource.IResComponentQuerySV.queryStaffInfoByStaffId", staffParam);
		IData staffData=null;
		if(staffDataSet!=null&&staffDataSet.getData(0)!=null
				&&staffDataSet.getData(0).getDataset("DATAS")!=null){
			
			staffData=staffDataSet.getData(0).getDataset("DATAS").getData(0);
		}
		
		if(staffData==null){
			throw new Exception("对不起，staffData为空");
		}
		IDataUtil.chkParam(staffData, "STAFF_ID");
		IDataUtil.chkParam(staffData, "STAFF_NAME");
		IDataUtil.chkParam(staffData, "DEPART_ID");
		IDataUtil.chkParam(staffData, "CITY_CODE");
		//IDataUtil.chkParam(staffData, "SERIAL_NUMBER");
		
		visit.setStaffId(staffData.getString("STAFF_ID"));
		visit.setStaffName(staffData.getString("STAFF_NAME"));
		visit.setDepartId(staffData.getString("DEPART_ID"));
		visit.setCityCode(staffData.getString("CITY_CODE"));
		visit.setLoginEparchyCode("0898");
		//visit.setInModeCode("4");
		visit.setStaffEparchyCode("0898");
		
		return staffData;
	}
}
