package com.asiainfo.veris.crm.iorder.web.igroup.minorec.phoneworktaskquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class QueryPhoneWorkTask extends GroupBasePage {

    public void queryInfos(IRequestCycle cycle) throws Exception{

        IData input = new DataMap();
        input.put("GROUP_ID",getData().getString("cond_GROUP_ID"));
        input.put("IBSYSID",getData().getString("cond_IBSYSID"));
        input.put("STAFF_ID",getVisit().getStaffId());
        //未处理
        input.put("INFO_STATUS",EcEsopConstants.WORKINFO_STATUS_UNDO);
        //处理人
        input.put("RECE_OBJ_TYPE", "2");
        //代办
        input.put("INFO_TYPE", "3");
        
        String SPEEDINESSCHANGE = "MINORECSPEEDINESSCHANGE";
        
        String COMPLEXPROCESSCHANGE = "COMPLEXPROCESSCHANGE";

        //取中小企业流程
        IDataset dataset = new DatasetList();
        IDataset bpmList =  pageutil.getStaticList("MINOREC_BPM_TEPMENTID");
        if(DataUtils.isNotEmpty(bpmList)){
            dataset.addAll(bpmList);
        }
        bpmList = pageutil.getStaticList("MINOREC_BPM_PRODUCTID_LIST");
        if(DataUtils.isNotEmpty(bpmList)){
            dataset.addAll(bpmList);
        }
        bpmList = pageutil.getStaticList("MINOREC_BPM_TEPMENTID_CHANGE");
        if(DataUtils.isNotEmpty(bpmList)){
            dataset.addAll(bpmList);
        }
        String str = "";
        for(Object obj : dataset){
            IData data = (IData) obj;
            if(!str.contains(data.getString("DATA_ID"))){
                str += "'" + data.getString("DATA_ID") + "',";
            }
        }
        //为了不影响业务，写死新增两个变更流程
        str += "'" + SPEEDINESSCHANGE + "','"+COMPLEXPROCESSCHANGE+"',";
        if(StringUtils.isNotBlank(str)){
            str = str.substring(0,str.length()-1);
            input.put("BPM_TEMPLET_ID",str);
        }

        IDataOutput output = CSViewCall.callPage(this,"SS.WorkTaskMgrSVC.getWorkTaskInfos",input,getPagination("navbar1"));
        setInfos(output.getData());
        setInfoCount(output.getDataCount());

    }

    public abstract void setInfos(IDataset infos) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;
}
