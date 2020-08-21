package com.asiainfo.veris.crm.iorder.web.igroup.minorec.minorecphoneworkorm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author pangs
 */
public abstract class AbstractPhoneMyWorkfom extends GroupBasePage {

    public void queryInfos(IRequestCycle cycle) throws Exception{
        IData input = new DataMap();
        input.put("GROUP_ID",getData().getString("cond_GROUP_ID"));
        input.put("IBSYSID",getData().getString("cond_IBSYSID"));
        input.put("STAFF_ID",getVisit().getStaffId());

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

        IDataOutput output = CSViewCall.callPage(this,"SS.WorkformSubscribeSVC.qrySubScribeInfoByIbsysidOrGroupId",input,getPagination("navbar1"));
        setInfos(output.getData());
        setInfoCount(output.getDataCount());

    }

    public void cancelWorkform(IRequestCycle cycle) throws Exception{
        IData input = new DataMap();
        if(StringUtils.isBlank(getData().getString("BUSIFORM_ID"))){
            CSViewException.apperr(GrpException.CRM_GRP_713, "未获取到BUSIFORM_ID！");
        }

        input.put("BUSIFORM_ID", getData().getString("BUSIFORM_ID"));
        input.put("STATE", "4");
        CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", input);
    }

    public abstract void setInfos(IDataset infos) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;
}
