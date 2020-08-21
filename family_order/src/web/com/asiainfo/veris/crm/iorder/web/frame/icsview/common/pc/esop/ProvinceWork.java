package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.pc.esop;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.client.ServiceFactory;

public abstract class ProvinceWork extends BizPage {

    //订单待办
    private static final String orderundolist = "fddb";
    private static final String orderundomore = "ddb";

    //超时提醒
    private static final String delyremindlist = "fdtx";
    private static final String delyremindmore = "dtx";

    //订单待阅
    private static final String orderunreadlist = "fddy";
    private static final String orderunreadmore = "ddy";

    //订单已办
    private static final String orderdonelist = "fdyb";
    private static final String orderdonemore = "dyb";

    //工单待办
    private static final String workundolist = "fgdb";
    private static final String workundomore = "gdb";

    //工单待阅
    private static final String workunreadlist = "fgdy";
    private static final String workunreadmore = "gdy";

    //工单已办
    private static final String workdonelist = "fgyb";
    private static final String workdonemore = "gyb";

    private static final String companyNumber = "871";

    public abstract void setInfo(IData info);

    public void init(IRequestCycle cycle) throws Exception {

        IData info = new DataMap();

        String staffNo = getStaffNumber();
        
        //订单待办
//        info.put("ORDER_UNDO_LIST",getUrl(orderundolist));
        info.put("ORDER_UNDO_MORE",getUrl(orderundomore, staffNo));

        //超时提醒
//        info.put("DELY_REMIND_LIST",getUrl(delyremindlist));
        info.put("DELY_REMIND_MORE",getUrl(delyremindmore, staffNo));

        //订单待阅
//        info.put("ORDER_UNREAD_LIST",getUrl(orderunreadlist));
        info.put("ORDER_UNREAD_MORE",getUrl(orderunreadmore, staffNo));

        //订单已办
//        info.put("ORDER_DONE_LIST",getUrl(orderdonelist));
        info.put("ORDER_DONE_MORE",getUrl(orderdonemore, staffNo));

        //工单待办
//        info.put("WORK_UNDO_LIST",getUrl(workundolist));
        info.put("WORK_UNDO_MORE",getUrl(workundomore, staffNo));

        //工单待阅
//        info.put("WORK_UNREAD_LIST",getUrl(workunreadlist));
        info.put("WORK_UNREAD_MORE",getUrl(workunreadmore, staffNo));

        //工单已办
//        info.put("WORK_DONE_LIST",getUrl(workdonelist));
        info.put("WORK_DONE_MORE",getUrl(workdonemore, staffNo));

        setInfo(info);

    }

    private String getUrl(String tag,  String staffNumber ) throws Exception {
      
        IData data = new DataMap();
        data.put("STAFF_NO", staffNumber);
//老接口        
//        IData result = ServiceCaller.call("CommonBusinessCentre.proviwork.IProviWorkSV.queryProviWork", data);      
//        IDataset idata = result.getDataset("OUTDATA");
        //接口未替换
        IDataOutput out = ServiceFactory.call("CommonBusinessCentre.proviwork.IProviWorkSV.queryProviWork", createDataInput(data));

        IDataset idata = out.getData();
        IData staffInfo = idata.getData(0);
        
        String beginUrl =  staffInfo.getString("URL");
        String currentTime = staffInfo.getString("TIME"); 
        String staff_no = staffInfo.getString("STAFF_NO");
        String url = beginUrl+"?tag="+tag+"&companyNumber="+companyNumber+"&UserName="+staff_no+"&TimeStamp="+currentTime;

        return url;
    }

    private String getStaffNumber() throws Exception {

        String staffId = getVisit().getStaffId();
        IData data = new DataMap();
        data.put("STAFF_ID", staffId);
//老接口         
//        IData result = ServiceCaller.call("CommonBusinessCentre.secframe.orgstaffmgt.IOrgStaffQuerySV.querySatffBbossBySatffId", data);
//        IDataset staffInfo = result.getDataset("INFOS");

        //接口未替换
        IDataOutput out = ServiceFactory.call("CommonBusinessCentre.secframe.orgstaffmgt.IOrgStaffQuerySV.querySatffBbossBySatffId", createDataInput(data));
        IDataset staffInfo = out.getData();

        if (staffInfo.size() == 0) {
            pageutil.error("工号" + staffId + "在系统中找不到对应的总部ESOP系统工号，请确认！");
        }

        String staffNumber = staffInfo.getData(0).getString("STAFF_NUMBER");
        return staffNumber;
    }



}