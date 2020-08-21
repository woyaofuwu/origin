
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author think
 */
public class InformationManageRequestData extends BaseCustomerTitleRequestData
{
    private List<InformationManageData> formationInfo = new ArrayList<InformationManageData>();

    public final List<InformationManageData> getFormationInfo()
    {
        return formationInfo;
    }

    public final void setFormationInfo(List<InformationManageData> formationInfo)
    {
        this.formationInfo = formationInfo;
    }

}
