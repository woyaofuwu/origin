package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.cust;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GrpCustInfo extends BizTempComponent
{
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/cust/GrpCustInfo.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        String groupId = getPage().getData().getString("GROUP_ID");
        if(StringUtils.isNotBlank(groupId))
        {
            queryCustGroupByGroupId();
        }
    }

    private void queryCustGroupByGroupId() throws Exception
    {
        String groupId = getPage().getData().getString("GROUP_ID");
        
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        
        IData esopInfo = new DataMap();
        esopInfo.put("GROUP_ID", group.getString("GROUP_ID"));
        esopInfo.put("GROUP_NAME", group.getString("CUST_NAME"));
        esopInfo.put("CUST_ID", group.getString("CUST_ID"));
        group.put("ESOP_INFO", esopInfo);
        String servLevel = group.getString("SERV_LEVEL");
        if (StringUtils.isEmpty(servLevel))
        {
            group.put("SERV_LEVEL", "4");
        }
        
        String servLevel2 = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_SERVLEVEL", group.getString("SERV_LEVEL") });
        group.put("SERV_LEVEL", servLevel2);
        
    	String classId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "CUSTGROUP_CLASSID", group.getString("CLASS_ID") });
    	group.put("CLASS_ID", classId);
        //根据集团的地市获取相对应的区县
    	String city = StaticUtil.getStaticValue(this.getVisit(), "TD_M_AREA", new String[] { "AREA_CODE" }, "AREA_NAME", new String[] { group.getString("CITY_CODE") });
        IDataset areaList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "CHANGE_AREA_BY_CITY", city});
        IDataset area = new DatasetList();
        for (int i = 0; i < areaList.size(); i++) {
            IData temp = areaList.getData(i);
            temp.put("DATA_ID", temp.getString("DATA_ID", ""));
            temp.put("DATA_NAME", temp.getString("DATA_ID", ""));
            area.add(temp);
		}
        group.put("AREAS", area);
    	
        setGroupInfo(group);
        getPage().setAjax(group);
        
        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = IUCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }
	
    public abstract void setGroupInfo(IData groupInfo) throws Exception;
    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;
    public abstract void setInfo(IData info) throws Exception;
    public abstract String getGroupId() throws Exception;
}
