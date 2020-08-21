
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.videomesage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class VideoMessageIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        String operSource = input.getString("OPR_SOURCE");
        if (operSource == null || operSource.trim().equals(""))
        {
            input.put("OPR_SOURCE", "TD");
        }

        String password = input.getString("PASSWD");
        String svcLevel = input.getString("SVC_LEVEL");

        IDataset attrs = new DatasetList();
        if (StringUtils.isNotBlank(password))
        {
            IData attr = new DataMap();
            attr.put("ATTR_CODE", "PASSWD");
            attr.put("ATTR_VALUE", password);
            attrs.add(attr);
        }
        if (StringUtils.isNotBlank(svcLevel))
        {
            IData attr = new DataMap();
            attr.put("ATTR_CODE", "3001");
            attr.put("ATTR_VALUE", svcLevel);
            attrs.add(attr);
        }
        if (IDataUtil.isNotEmpty(attrs))
        {
            input.put("ATTR_PARAM", attrs);
        }
    }

}
