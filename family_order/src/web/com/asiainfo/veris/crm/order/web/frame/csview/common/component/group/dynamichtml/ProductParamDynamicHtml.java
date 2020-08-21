
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml;

import java.lang.reflect.Constructor;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizDynamicComponent;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.wade.web.v4.tapestry.component.dynamic.ECLTemplate;

public abstract class ProductParamDynamicHtml extends CSBizDynamicComponent
{

    private IData attrItem;

    private IDataset attrItemSet;

    private String busiType;

    private String includeJs;

    private String initJs;

    private String methodName;

    private String pageName;

    private String paramclassName;

    private String productId;

    public IData getAttrItem()
    {

        return attrItem;
    }

    public IDataset getAttrItemSet()
    {

        return attrItemSet;
    }
    
    public IDataset getAttrItemSource(String key)
    {
        IData data = (IData) attrItem.get(key);

        if ((data == null) || (data.size() == 0))
            return null;
        else
            return data.getDataset("DATA_VAL");
    }

    public String getAttrItemValue(String key, String type)
    {
        if (attrItem == null)
            attrItem = new DataMap();
        IData data = (IData) attrItem.get(key);

        if ((data == null) || (data.size() == 0))
            return "";
        else
            return data.getString(type);
    }

    @Override
    public String getHtmlTemplate() throws Exception
    {
        productId = getPage().getParameter("GRP_PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            productId = getPage().getParameter("PRODUCT_ID");
            if (StringUtils.isBlank(productId))
            {
                return "";
            }
        }

        String productCtrlInfoStr = getPage().getParameter("PRODUCT_CTRL_INFO");
        IData productCtrlInfo = new DataMap();
        if (StringUtils.isBlank(productCtrlInfoStr))
        {
            busiType = getPage().getParameter("BUSI_TYPE");
            if (StringUtils.isBlank(busiType))
                return "";
            productCtrlInfo = AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType);
        }
        else
        {
            productCtrlInfo = new DataMap(productCtrlInfoStr);
        }
        IData paramInfo = productCtrlInfo.getData("ParamInfo");
        if (paramInfo == null)
            return "";
        IData ParamClassNameInfo = productCtrlInfo.getData("ParamClassName");

        if (ParamClassNameInfo == null)
        {
            paramclassName = IProductParamDynamic.class.getName();
        }
        else
        {
            paramclassName = ParamClassNameInfo.getString("ATTR_VALUE");
        }
        pageName = paramInfo.getString("ATTR_VALUE");
        methodName = "init" + paramInfo.getString("ATTR_OBJ");
        includeJs = productCtrlInfo.getData("jsFile") == null ? "" : productCtrlInfo.getData("jsFile").getString("ATTR_VALUE");
        initJs = productCtrlInfo.getData("jsFile") == null ? "" : productCtrlInfo.getData("jsFile").getString("RSRV_STR1");

        return ECLTemplate.getTemplate(pageName);
    }

    public abstract String getParam();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;
        IData param = this.getPage().getData();
        String isRender = param.getString("IS_RENDER", "");
        if (!isRender.equals("true"))
        {
            param = new DataMap();
            String paramstr = getParam();
            if (paramstr != null && paramstr.length() != 0)
            {
                param = new DataMap(paramstr);
            }
            else
            {
                param.put("PRODUCT_ID", productId);
            }

            if (StringUtils.isNotBlank(includeJs))
            {
                String[] includeJStrings = includeJs.split(",");
                for (int i = 0; i < includeJStrings.length; i++)
                {
                    cycle.getPage().addResAfterBodyBegin(includeJStrings[i]);
                }
            }

            if (StringUtils.isNotBlank(initJs))
            {
                StringBuilder init_script = new StringBuilder();
                init_script.append("$(document).ready(function(){\r\n").append("\t ").append(initJs).append(";\r\n").append("});\r\n");
                getPage().addScriptBeforeBodyEnd("SCRIPT_ATTRIBUTE", init_script.toString());
            }

        }
        else
        {
            if (StringUtils.isNotBlank(includeJs))
            {
                String[] includeJStrings = includeJs.split(",");
                for (int i = 0; i < includeJStrings.length; i++)
                {
                    addResourceFile(writer, includeJStrings[i]);
                }
            }

            if (StringUtils.isNotBlank(initJs))
            {
                StringBuilder init_script = new StringBuilder();
                init_script.append("$(document).ready(function(){\r\n").append("\t ").append(initJs).append(";\r\n").append("});\r\n");
                addScriptContent(writer, init_script.toString());

            }
        }

        if (StringUtils.isEmpty(paramclassName))
            return;

        Object[] objectGroup = new Object[]
        { getPage(), param };
        Class[] classGroup = new Class[]
        { IBizCommon.class, IData.class };

        Class groupClass = Class.forName(paramclassName);
        Constructor cons = groupClass.getConstructor(new Class[]
        {});
        Object object = cons.newInstance(new Object[]
        {});
        java.lang.reflect.Method method = groupClass.getMethod(methodName, classGroup);
        IData result = (IData) method.invoke(object, objectGroup);
        if (IDataUtil.isEmpty(result))
            result = new DataMap();
        attrItem = result.getData("ATTRITEM");
        attrItemSet = result.getDataset("ATTRITEMSET");
        IData info = result.getData("PARAM_INFO");
        if (IDataUtil.isNotEmpty(info))
        {
            info.put("PRODUCTPARAM_METHOD_NAME", busiType);
        }
        setInfo(info);
    }

    public abstract void setCondition(IData info);

    public abstract void setInfo(IData info);
}
