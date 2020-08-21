
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterfaceCreate extends PersonBasePage
{
    static final Logger logger = Logger.getLogger(InterfaceCreate.class);

    private String getLoginInfo()
    {
        StringBuilder info = new StringBuilder();
        info.append("STAFF_ID").append("=").append(getVisit().getStaffId()).append(",");
        info.append("STAFF_NAME").append("=").append(getVisit().getStaffName()).append(",");
        info.append("LOGIN_EPARCHY_CODE").append("=").append(getVisit().getStaffEparchyCode()).append(",");
        info.append("STAFF_EPARCHY_CODE").append("=").append(getVisit().getStaffEparchyCode()).append(",");
        info.append("IN_MODE_CODE").append("=").append(getVisit().getInModeCode()).append(",");
        info.append("CITY_CODE").append("=").append(getVisit().getCityCode()).append(",");
        info.append("DEPART_ID").append("=").append(getVisit().getDepartId()).append(",");
        info.append("DEPART_CODE").append("=").append(getVisit().getDepartCode());
        Map map = getVisit().getAll();
        return info.toString();
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String interId = data.getString("cond_INTER_ID");
        if (StringUtils.isBlank(interId))
        {
            IDataset dataset = CSViewCall.call(this, "CS.InterfaceCreateSVC.createInterfaceInfos", data);
            interId = dataset.getData(0).getString("INTER_ID");
        }
        else
        {
            CSViewCall.call(this, "CS.InterfaceCreateSVC.updateInterfaceInfos", data);
        }

        IData dataInput = new DataMap();
        String params = data.get("SCENE_TABLE").toString();
        IDataset param = new DatasetList(params);
        // IDataset addInfo = new DatasetList();
        // IDataset delInfo = new DatasetList();
        for (int i = 0; i < param.size(); i++)
        {
            dataInput.clear();
            dataInput = param.getData(i);

            dataInput.put("INTER_ID", interId);

            String sceneid = dataInput.getString("SCENE_ID");
            if (StringUtils.isBlank(sceneid))
            {
                CSViewCall.call(this, "CS.InterfaceCreateSVC.createInterfaceScenes", dataInput);
                // addInfo.add(dataInput);
            }
            else
            {
                dataInput.clear();
                dataInput.put("SCENE_ID", sceneid);
                CSViewCall.call(this, "CS.InterfaceCreateSVC.deleteInterfaceScene", dataInput);
                // delInfo.add(dataInput);
            }
        }

        IData ajax = new DataMap();
        ajax.put("X_RESULTINFO", "OK!");
        setAjax(ajax);
    }

    public void queryInterfaceByCode(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IData param = new DataMap();
        param.put("CODE", data.getString("INTERFACE_ID"));

        IDataset dataset = CSViewCall.call(this, "CS.InterfaceCreateSVC.queryInterfaceByCode", param);
        IData interfaceinfo = new DataMap();

        if (IDataUtil.isNotEmpty(dataset))
        {
            String sysCode = dataset.getData(0).getString("SUBSYS_CODE");
            String interName = dataset.getData(0).getString("INTER_NAME");
            String addr = dataset.getData(0).getString("ADDR");
            String code = dataset.getData(0).getString("CODE");
            String staffId = dataset.getData(0).getString("UPDATE_STAFF_ID");
            String loginInfos = dataset.getData(0).getString("LOGININ_INFOS");
            String interId = dataset.getData(0).getString("INTER_ID");

            IDataset interscenes = CSViewCall.call(this, "CS.InterfaceCreateSVC.querySceneByInterId", dataset.getData(0));
            setSceneinfos(interscenes);

            interfaceinfo.put("cond_INTER_ID", interId);
            interfaceinfo.put("cond_SUBSYS_CODE", sysCode);
            interfaceinfo.put("cond_INTERFACE_NAME", interName);
            interfaceinfo.put("cond_INTERFACE_ADDR", addr);
            interfaceinfo.put("cond_INTERFACE_CODE", code);
            interfaceinfo.put("cond_UPDATE_STAFF_ID", staffId);
            interfaceinfo.put("cond_LOGIN_INFO", loginInfos);
            interfaceinfo.put("AJAX_CODE", "1");
        }
        else
        {
            String ip = getVisit().getRemoteAddr();
            StringBuilder sb = new StringBuilder("http://").append(ip).append(":8080/personserv/service");
            interfaceinfo.put("cond_INTERFACE_CODE", data.getString("INTERFACE_ID"));
            if ("10.154.59.151".equals(ip))
            {
                interfaceinfo.put("cond_INTERFACE_ADDR", "http://10.154.59.151:10000/service");
            }
            else
            {
                interfaceinfo.put("cond_SUBSYS_CODE", "CRM");
                interfaceinfo.put("cond_INTERFACE_ADDR", sb.toString());
            }

            // "STAFF_ID=SUPERUSR,STAFF_NAME=SUPERUSR,LOGIN_EPARCHY_CODE=0731,STAFF_EPARCHY_CODE=0731,IN_MODE_CODE=0,CITY_CODE=0731,DEPART_ID=00000,DEPART_CODE=00000"
            interfaceinfo.put("cond_LOGIN_INFO", getLoginInfo());
            interfaceinfo.put("AJAX_CODE", "0");
        }

        setCondition(interfaceinfo);
        setAjax(interfaceinfo);
    }

    public abstract void setCondition(IData condition);

    public abstract void setSceneinfos(IDataset sceneinfos);
}
