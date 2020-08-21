
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ChkVpmn3030Valid extends BreBase implements IBREScript
{
    /**
     * Vpmn集团受理，tradetypecode=3030的操作规则,因考虑页面参数量大，暂时不走该规则，代码保留
     */
    private static final long serialVersionUID = -245534769209563115L;

    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
        String userGrppackageStr = databus.getString("SELECTED_GRPPACKAGE_LIST");

        String userId = databus.getString("SELECTED_USER_ID", "-1");
        String userIdA = databus.getString("SELECTED_USER_ID_A", "-1");
        if (StringUtils.isBlank(userElementsStr))
            userElementsStr = "[]";
        IDataset userElements = new DatasetList(userElementsStr);
        if (IDataUtil.isNotEmpty(userElements))
        {
            int size = userElements.size();

            for (int i = 0; i < size; i++)
            {
                IData element = userElements.getData(i);

                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && "801".equals(element.getString("ELEMENT_ID", "")))
                {

                }
            }
        }
        //

        return false;
    }
}
