
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class WidenetChangeProductFilter implements IFilterIn
{

    /**
     * 宽带产品变更入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "PRODUCT_ID");
        IDataUtil.chkParam(param, "DISCNT_CODE");
    }

    public void transferDataInput(IData input) throws Exception
    {

        checkInparam(input);
        input.put("NEW_PRODUCT_ID", input.getString("PRODUCT_ID"));
        IDataset selectedElements = new DatasetList();
        IData discnt = new DataMap();
        discnt.put("ELEMENT_ID", input.getString("DISCNT_CODE"));
        discnt.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
        selectedElements.add(discnt);
        input.put("SELECTED_ELEMENTS", selectedElements.toString());

    }
}
