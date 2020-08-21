package com.asiainfo.veris.crm.order.web.person.onecardmultino;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class ChooseOnecardmultiNo extends PersonBasePage {
    public void getSerialNumber(IRequestCycle cycle) throws Exception {
        String eparchyCode = getVisit().getStaffEparchyCode();
        IData param = getData();
        IData data = new DataMap();
        data.put("START_NUM", param.getString("START_NUM",""));
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset phoneList = CSViewCall.call(this, "SS.OneCardMultiNoSVC.qrySubSerialNumber", data);
        //查询出来后将上一页的分页号码释放
        releaseUnuseNumber(cycle);
        //IDataset phoneList = new DatasetList();
//        for(int i = 0; i < 10; i++){
//        	IData temp = new DataMap();
//        	temp.put("ACCESS_NUMBER", "1888186521"+i);
//        	phoneList.add(temp);
//        }
        if (IDataUtil.isNotEmpty(phoneList)) {
        	//排序
            DataHelper.sort(phoneList, "ACCESS_NUMBER", IDataset.TYPE_STRING);
            setPhoneList(phoneList);
        } else {
            setHintInfo("未获取到可以办理的和多号虚拟副号，请先进行批开!");
        }
    }
    
    /**
	 * 释放未选择号码
	 * @param cycle
	 * @throws Exception 
	 */
	public void releaseUnuseNumber(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData selectedNums = new DataMap();
		String followMsisdns = pageData.getString("FOLLOW_MSISDN_S","");
		String selectViceNum = pageData.getString("SERIAL_NUMBER_B");
		if(StringUtils.isNotBlank(followMsisdns)) {
			IDataset viceNums = new DatasetList(followMsisdns);
			IDataset followMsisdnStr = new DatasetList();
			if(StringUtils.isNotBlank(selectViceNum)){
				for (int i = 0; i < viceNums.size(); i++) {
					if (!viceNums.getData(i).getString("ACCESS_NUMBER").equals(selectViceNum)) {
						followMsisdnStr.add(viceNums.getData(i).getString("ACCESS_NUMBER"));
					}
				}
			}else{
				for (int i = 0; i < viceNums.size(); i++) {
					followMsisdnStr.add(viceNums.getData(i).getString("ACCESS_NUMBER"));
				}
			}
			selectedNums.put("VIRTUAL_ARR", followMsisdnStr);
			CSViewCall.call(this, "SS.OneCardMultiNoSVC.releaseVirtualUseNum", selectedNums);
		}
	}

    public abstract void setPhoneList(IDataset PhoneList);
    public abstract void setPhoneData(IData PhoneData);
    public abstract void setRowIndex(int RowIndex);
    public abstract void setHintInfo(String hintInfo);
}
