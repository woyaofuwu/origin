
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml;

import java.lang.reflect.Constructor;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;

public class ProxyParam extends CSBizHttpHandler
{
    public void productParamInvoker() throws Exception
    {
        IData data = getData();

        String paramclassName = data.getString("CLASS_NAME", "");
        String methodName = data.getString("METHOD_NAME", "");
        Object[] objectGroup = new Object[]
        { this, data };
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

        String ajaxdatastr = result.getString("AJAX_DATA", "");
        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }

    }

}
