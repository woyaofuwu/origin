
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf.bean;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.intf.InterfaceCreateQry;

public class InterfaceCreateBean extends CSBizBean
{

    private static final Logger logger = Logger.getLogger(InterfaceCreateBean.class);

    public IDataset createInterfaceInfos(IData data) throws Exception
    {
        // 设置参数
        IData param = new DataMap();
        param.put("UPDATE_DEPART_ID", "00000");
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("SUBSYS_CODE", data.getString("cond_SUBSYS_CODE"));
        param.put("INTER_NAME", data.getString("cond_INTERFACE_NAME"));
        param.put("LOGININ_INFOS", data.getString("cond_LOGIN_INFO"));
        param.put("ADDR", data.getString("cond_INTERFACE_ADDR"));
        param.put("CODE", data.getString("cond_INTERFACE_CODE"));
        param.put("UPDATE_STAFF_ID", data.getString("cond_UPDATE_STAFF_ID"));
        param.put("VALID_FLAG", "0");

        // boolean infos = createInfos(param);
        boolean infos = InterfaceCreateQry.createInfosByParam(param);
        return InterfaceCreateQry.queryInterfaceByCode(param.getString("CODE"));
    }

    public IDataset createInterfaceScenes(IData data) throws Exception
    {
        IData param = new DataMap();
        IDataset saveSceneIds = new DatasetList();
        param.put("INTER_ID", data.getString("INTER_ID"));
        param.put("SCENE_NM", data.getString("SCENE_NM"));
        param.put("SCENE_VL", data.getString("SCENE_VL"));

        // createScene(param);
        InterfaceCreateQry.createSceneByParam(param);
        return saveSceneIds;
    }

    public boolean deleteInterfaceScene(IData data) throws Exception
    {
        return InterfaceCreateQry.deleteInterfaceScene(data);
    }

    public IDataset queryInterfaceByCode(IData data) throws Exception
    {
        IDataset dataset = InterfaceCreateQry.queryInterfaceByCode(data.getString("CODE"));

        return dataset;
    }

    public IDataset querySceneByInterId(IData data) throws Exception
    {
        IDataset dataset = InterfaceCreateQry.querySceneByInterId(data);

        return dataset;
    }

    // private boolean createInfos(IData data) throws Exception {
    // return InterfaceCreateQry.createTabByParam("TD_S_INTERFACE_INFOS", data);//Dao.insert("TD_S_INTERFACE_INFOS",
    // data, ConnMgr.CONN_CRM_CEN);
    // }
    //	
    // private boolean createScene(IData data) throws Exception {
    // return InterfaceCreateQry.createTabByParam("TD_S_INTERFACE_SCENE", data);//Dao.insert("TD_S_INTERFACE_SCENE",
    // data, ConnMgr.CONN_CRM_CEN);
    // }

    public IDataset updateInterfaceInfos(IData data) throws Exception
    {
        // 设置参数
        IData param = new DataMap();
        param.put("INTER_ID", data.getString("cond_INTER_ID"));
        param.put("UPDATE_DEPART_ID", "00000");
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("SUBSYS_CODE", data.getString("cond_SUBSYS_CODE"));
        String interName = data.getString("cond_INTERFACE_NAME");
        if (interName.contains("★★"))
        {
            interName = interName.substring(interName.indexOf("★★") + 2);
        }
        param.put("INTER_NAME", interName);
        param.put("LOGININ_INFOS", data.getString("cond_LOGIN_INFO"));
        param.put("ADDR", data.getString("cond_INTERFACE_ADDR"));
        param.put("CODE", data.getString("cond_INTERFACE_CODE"));
        param.put("UPDATE_STAFF_ID", data.getString("cond_UPDATE_STAFF_ID"));
        param.put("VALID_FLAG", "0");

        InterfaceCreateQry.updateInfosByInterId(param);

        return null;
    }
}
