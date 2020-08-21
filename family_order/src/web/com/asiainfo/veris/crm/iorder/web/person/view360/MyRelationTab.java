
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MyRelationTab extends PersonBasePage
{

    /**
     * 用户关系信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryRelationInfo", data);
        IDataset out = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryIsCmccStaff", data);
        setIsCmccStaff(out.first().getString("IS_STAFF_USER","0"));

        if (IDataUtil.isNotEmpty(output)) {
            for (Object obj : output) {
                IData relation = (DataMap) obj;
                String[] keysA = new String[]{"RELATION_TYPE_CODE", "ROLE_CODE_A"};
                String[] keysB = new String[]{"RELATION_TYPE_CODE", "ROLE_CODE_B"};
                String[] valuesA = new String[]{relation.getString("RELATION_TYPE_CODE"), relation.getString("ROLE_CODE_A")};
                String[] valuesB = new String[]{relation.getString("RELATION_TYPE_CODE"), relation.getString("ROLE_CODE_B")};
                String role  = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME", relation.getString("RELATION_TYPE_CODE"), "无");
                String custManager = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", relation.getString("CUST_MANAGER_ID"), "无");
                String roleA = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION_ROLE", keysA, "ROLE_A", valuesA, "无");
                String roleB = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION_ROLE", keysB, "ROLE_B", valuesB, "无");
                String custName = relation.getString("CUST_NAME", "无");

                relation.put("RELATION_TYPE_NAME", role);
                relation.put("CUST_MANAGER_NAME", custManager);
                relation.put("ROLE_A", roleA);
                relation.put("ROLE_B", roleB);
                relation.put("CUST_NAME", custName);
            }
            setInfos(output);
        }
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setIsCmccStaff(String isCmccStaff);
}
