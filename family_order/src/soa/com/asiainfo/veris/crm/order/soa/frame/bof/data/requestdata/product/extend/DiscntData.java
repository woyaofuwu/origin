
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * @author Administrator
 */
public class DiscntData extends ProductModuleData
{
    public DiscntData() throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
    }

    public DiscntData(IData map) throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
        if (!StringUtils.isBlank(map.getString("DISCNT_CODE")))
        {
            this.setElementId(map.getString("DISCNT_CODE"));
        }
        else
        {
            this.setElementId(map.getString("ELEMENT_ID"));
        }
        this.setModifyTag(map.getString("MODIFY_TAG"));
        this.setStartDate(map.getString("START_DATE"));
        this.setEndDate(map.getString("END_DATE"));
        this.setProductId(map.getString("PRODUCT_ID"));
        this.setPackageId(map.getString("PACKAGE_ID"));
        this.setCampnId(map.getString("CAMPN_ID"));
        this.setInstId(map.getString("INST_ID"));
        this.setRemark(map.getString("REMARK"));

        IDataset attrs = map.getDataset("ATTR_PARAM");
        if (IDataUtil.isNotEmpty(attrs))
        {
            List<AttrData> attrDatas = new ArrayList<AttrData>();
            int size = attrs.size();
            for (int i = 0; i < size; i++)
            {
                IData attr = attrs.getData(i);
                AttrData attrData = new AttrData();
                attrData.setAttrCode(attr.getString("ATTR_CODE"));
                attrData.setAttrValue(attr.getString("ATTR_VALUE"));
                attrData.setModifyTag(attr.getString("MODIFY_TAG", this.getModifyTag()));
                attrDatas.add(attrData);
                this.setAttrs(attrDatas);
            }
        }
        this.setPkgElementConfig();
    }
}
