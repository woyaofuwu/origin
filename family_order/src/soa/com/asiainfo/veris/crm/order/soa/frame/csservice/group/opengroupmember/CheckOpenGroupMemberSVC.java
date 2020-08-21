
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.opengroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.privm.CheckPriv;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class CheckOpenGroupMemberSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset checkResourceSn(IData param) throws Exception
    {
    	IDataset dataset = new DatasetList();
    	
    	//检验号码是否属于RES_TT_NUMBER表的号码
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", param.getString("RES_VALUE"));
        IDataset datasetTTNum = ResCall.callRes("RCF.resource.INumberIntfQuerySV.qryTTNumInfos",input);
        System.out.println(getVisit().getStaffId());
        if (datasetTTNum.size() > 0 && !CheckPriv.checkFuncPermission(getVisit().getStaffId(), "SYSTTOPEN")) {
        	//操作处理
			input.put("X_RESULTCODE", "-1");
			input.put("X_RESULTINFO", "该号码为特殊入库的铁通号码,此工号无特殊铁通号码开户权限,无法完成开户。");
			dataset.add(input);
		}else {
			//原流程
        	OpenGroupMemberBean bean = new OpenGroupMemberBean();
            IData data = bean.checkResourceSn(param);
            dataset.add(data);
		}
        return dataset;
    }

    /**
     * 生成集团产品成员用户服务号码
     * 
     * @author fengsl
     * @date 2013-04-11
     * @throws Exception
     */
    public IDataset genGrpMebSn(IData idata) throws Exception
    {
        return OpenGroupMemberBean.genGrpMebSn(idata);
    }

}
