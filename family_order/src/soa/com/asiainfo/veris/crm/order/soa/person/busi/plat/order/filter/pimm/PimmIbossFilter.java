
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.pimm;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class PimmIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {

        String userPassword = input.getString("USER_PASSWD");

        if (StringUtils.isNotEmpty(userPassword))
        {
            IDataset infoCodes = new DatasetList();
            IDataset infoValues = new DatasetList();

            if (input.getString("INFO_CODE", "").startsWith("["))
            {
                JSONArray infoCodeArray = JSONArray.fromObject(input.getString("INFO_CODE"));
                JSONArray infoValueArray = JSONArray.fromObject(input.getString("INFO_VALUE"));
                if (infoCodeArray != null && infoCodeArray.size() > 0)
                {
                    for (int i = 0; i < infoCodeArray.size(); i++)
                    {
                        infoCodes.add(infoCodeArray.get(i).toString());
                        infoValues.add(infoValueArray.get(i).toString());
                    }
                }

            }
            else if (StringUtils.isNotEmpty(input.getString("INFO_CODE", "")))
            {
                infoCodes.add(input.getString("INFO_CODE"));
                infoCodes.add(input.getString("INFO_VALUE"));
            }
            infoCodes.add("PIM_NPWD");
            infoValues.add(userPassword);

            input.put("INFO_CODE", infoCodes.toString());
            input.put("INFO_VALUE", infoValues.toString());
        }

    }

}
