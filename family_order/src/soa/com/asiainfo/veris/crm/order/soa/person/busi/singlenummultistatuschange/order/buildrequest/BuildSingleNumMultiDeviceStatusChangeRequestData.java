package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata.AuxDeviceData;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata.SingleNumMultiDeviceStatusChangeReqData;

public class BuildSingleNumMultiDeviceStatusChangeRequestData extends BaseBuilder implements IBuilder {
	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
		SingleNumMultiDeviceStatusChangeReqData reqData = (SingleNumMultiDeviceStatusChangeReqData) brd;
		// 获取所传的参数
		reqData.setSerialNumber(param.getString("SERIAL_NUMBER",""));
		reqData.setOprFlag(param.getString("OPR_FLAG",""));
	
		String serial_number = reqData.getSerialNumber();
		if("".equals(serial_number)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号码不能为空！");
			
		}
		String oprFlag = reqData.getOprFlag();
		String channlCode = "";
		String oprCode = "";
		
		//操作标志，1:前台暂停 2：前台恢复 3：欠费暂停（信控发起的停机） 4：缴费恢复 （信控发起的开机） 5.其他停机：主动停机 6.其他开机：主动开机
		if("1".equals(oprFlag))
		{
			channlCode = "1";
			oprCode = "04";
		}
		else if("2".equals(oprFlag) || "4".equals(oprFlag) || "6".equals(oprFlag))
		{
			oprCode = "05";
		}
		else if("3".equals(oprFlag)  || "5".equals(oprFlag))
		{
			oprCode = "04";
		}
		
		List<AuxDeviceData> auxDeviceDataList = new ArrayList<AuxDeviceData>();
        IDataset dataList = new DatasetList(param.getString("AUX_CODES"));
        if (dataList != null && dataList.size() > 0)
        {
            for (int i = 0; i < dataList.size(); i++)
            {
            	IData data = dataList.getData(i);
                AuxDeviceData auxDeviceData = new AuxDeviceData();
                auxDeviceData.setOrderno(data.getString("ORDERNO",""));
                auxDeviceData.setUserIdB(data.getString("USER_ID_B", ""));
                auxDeviceData.setSerialNumberB(data.getString("SERIAL_NUMBER_B",""));
                auxDeviceData.setAuxNickName(data.getString("AUX_NICK_NAME", ""));
                auxDeviceData.setInstId(data.getString("inst_id", ""));
                auxDeviceData.setChannlCode(channlCode);
                auxDeviceData.setOprCode(oprCode);
                auxDeviceDataList.add(auxDeviceData);
            }
        }
        
        reqData.setAuxDeviceDataList(auxDeviceDataList);
	}
	@Override
	public BaseReqData getBlankRequestDataInstance() {
		return new SingleNumMultiDeviceStatusChangeReqData();
	}
}