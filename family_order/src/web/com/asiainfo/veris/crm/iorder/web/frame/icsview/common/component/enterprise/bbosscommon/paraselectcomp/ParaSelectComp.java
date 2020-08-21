
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.bbosscommon.paraselectcomp;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.wade.web.v5.tapestry.component.base.Select;

public abstract class ParaSelectComp extends Select
{
    public abstract void setDataType(String dataType);

    public abstract void setSource(Object source);

    public abstract String getDataType();
    
    public abstract String getIsMeb();
    
    public abstract String getParaCode();

    public void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        // 1、 查询组件下拉框数据
        IData inParam = new DataMap();
        // 1.1 查询条件(元素编码和查询数据类型)
        if ("BBSS_BOUND_DATA".equals(getDataType()) && "".equals(getIsMeb()))
        {
            String nameStr = getName();
            
            StringBuilder sbf = new StringBuilder();
            
            if(StringUtils.isNotEmpty(nameStr))
            {
                for(int i = 0;i<nameStr.length();i++)
                {
                    int j = (int)nameStr.charAt(i);
                    if((j > 47 && j < 58))
                    {
                        sbf.append(nameStr.charAt(i));
                    }
                 }
            }
            inParam.put("ELEMENT_ID", sbf.toString());
            inParam.put("PARAMCODE", sbf.toString());
        }
        else if ("BBSS_BOUND_DATA".equals(getDataType()) && "true".equals(getIsMeb())) {        	
            inParam.put("ELEMENT_ID", getName().substring(4));             
            inParam.put("PARAMCODE", getName().substring(5));
        }
        else if(StringUtils.isNotEmpty(getParaCode()))
        {
        	inParam.put("ELEMENT_ID", getParaCode());
        	inParam.put("PARAMCODE", getParaCode());
        }
        else
        {
            inParam.put("ELEMENT_ID", getName());
            inParam.put("PARAMCODE", getName());
        }
        inParam.put("PARA_TYPE", getDataType());
        IDataset ds =null;
        IDataset dataset =null;
        try
        {
             IDataInput input = new DataInput();
             input.getData().putAll(inParam);
             IDataOutput output = ServiceFactory.call("CS.BoundDataQrySVC.qryBoundDataByParamcode", input);
              dataset = output.getData();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        setSource(dataset);

        super.renderComponent(writer, cycle);
    }
}
