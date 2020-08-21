package com.asiainfo.veris.crm.iorder.web.igroup.esop.esoporderstatistics;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class EsopOrderStatistics extends EopBasePage {

    public abstract void setPattrs(IDataset pattrs) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;

    public void qryOrderInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData param = new DataMap();
        param.put("SUB_TYPE_CODE", data.getString("SUB_TYPE_CODE"));
        param.put("IBSYSID", data.getString("cond_IBSYSID"));
        param.put("PRODUCT_NO", data.getString("cond_PRODUCTNO"));
        param.put("TITLE", data.getString("cond_TITLE"));
        param.put("STAFF_ID", data.getString("cond_STAFF_ID"));

        IDataOutput output = CSViewCall.callPage(this, "SS.EsopOrderQuerySVC.queryWaitCheck", param, getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());

    }

    public void singleSenderSms2Staff(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData param = new DataMap();
        param.put("IBSYSID", data.getString("IBSYSID"));
        param.put("STAFF_PHONE", data.getString("STAFF_PHONE"));
        param.put("TITLE", data.getString("TITLE"));
        param.put("STAFF_ID", data.getString("STAFF_ID"));
        param.put("USER_EPARCHY_CODE", "0898");
        CSViewCall.call(this, "SS.EsopOrderQuerySVC.smsSender", param);
    }

    public void batchSenderSms2Staff(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset requestList = new DatasetList(data.getString("SUBMIT_PARAM"));
        /*IData param = new DataMap();
        String ibsysid = data.getString("IBSYSIDs", "");
        String staffPhone = data.getString("STAFF_PHONEs", "");
        String title = data.getString("TITLEs", "");
        String staff = data.getString("STAFF_IDs", "");*/

        /*List<String> list = new ArrayList<String>();
        list.add(ibsysid);
        list.add(staffPhone);
        list.add(title);
        list.add(staff);
        checkParameter(list);
        
        String[] ibsysids = ibsysid.substring(0, ibsysid.length() - 1).split(";");
        String[] staffPhones = staffPhone.substring(0, staffPhone.length() - 1).split(";");
        String[] titles = title.substring(0, title.length() - 1).split(";");
        String[] staffs = staff.substring(0, staff.length() - 1).split(";");*/
        if(IDataUtil.isNotEmpty(requestList)) {
            for (int i = 0; i < requestList.size(); i++) {
                IData requestData = requestList.getData(i);
                IData param = new DataMap();
                param.put("IBSYSID", requestData.getString("IBSYSID"));
                param.put("STAFF_PHONE", requestData.getString("STAFF_PHONE"));
                param.put("TITLE", requestData.getString("TITLE"));
                param.put("STAFF_ID", requestData.getString("STAFF_ID"));
                param.put("USER_EPARCHY_CODE", "0898");
                CSViewCall.call(this, "SS.EsopOrderQuerySVC.smsSender", param);
            }
        }
    }

    /*private void checkParameter(List<String> list) throws Exception {
        if(list != null && list.size() > 0) {
            int len = list.get(0).split(";").length;
            for (int i = 0; i < list.size(); i++) {
                String param = list.get(i);
                if(StringUtils.isBlank(param) || param.split(";").length != len) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "传入参数有误！");
                }
            }
        }
    }*/
}
