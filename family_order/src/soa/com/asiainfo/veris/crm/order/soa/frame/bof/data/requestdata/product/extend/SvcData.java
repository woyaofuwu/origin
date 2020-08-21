
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;

/**
 * @author Administrator
 */
public class SvcData extends ProductModuleData
{
    private String mainTag;

    public SvcData() throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SVC);
    }

    public SvcData(IData map) throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SVC);
        if (!StringUtils.isBlank(map.getString("SERVICE_ID")))
        {
            this.setElementId(map.getString("SERVICE_ID"));
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

    public String getMainTag()
    {
        return mainTag;
    }

    private void setMainTag(String mainTag)
    {
        this.mainTag = mainTag;
    }

    @Override
    public void setPkgElementConfig() throws Exception
    {
        IData enableMode = new DataMap();
        try {
        	enableMode = UPackageElementInfoQry.queryElementEnableMode(this.getProductId(), this.getPackageId(), this.getElementId(), this.getElementType());
		} catch (Exception e) {
			// TODO: handle exception
		}
        if (IDataUtil.isNotEmpty(enableMode))
        {
            this.setEnableTag(enableMode.getString("ENABLE_TAG"));
            this.setStartAbsoluteDate(enableMode.getString("START_ABSOLUTE_DATE"));
            this.setStartOffset(enableMode.getString("START_OFFSET"));
            this.setStartUnit(enableMode.getString("START_UNIT"));
            this.setEndEnableTag(enableMode.getString("END_ENABLE_TAG"));
            this.setEndAbsoluteDate(enableMode.getString("END_ABSOLUTE_DATE"));
            this.setEndOffSet(enableMode.getString("END_OFFSET"));
            this.setEndUnit(enableMode.getString("END_UNIT"));
            this.setCancelTag(enableMode.getString("CANCEL_TAG"));
            this.setCancelAbsoluteDate(enableMode.getString("CANCEL_ABSOLUTE_DATE"));
            this.setCancelOffSet(enableMode.getString("CANCEL_OFFSET"));
            this.setCancelUnit(enableMode.getString("CANCEL_UNIT"));
        }
        IData elementCfg = new DataMap();
        try {
        	elementCfg = ProductElementsCache.getElement(this.getProductId(), this.getElementId(), "S");
        	if(elementCfg == null){
            	this.setMainTag("0");
            }
            else{
            	this.setMainTag(elementCfg.getString("IS_MAIN"));
            }
		} catch (Exception e) {
			// TODO: handle exception
			this.setMainTag("0");
		}
        
    }
}
