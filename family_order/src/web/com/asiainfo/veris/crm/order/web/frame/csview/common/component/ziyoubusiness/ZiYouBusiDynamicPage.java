package com.asiainfo.veris.crm.order.web.frame.csview.common.component.ziyoubusiness;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizDynamicComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v4.tapestry.component.dynamic.DynamicComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class ZiYouBusiDynamicPage extends CSBizDynamicComponent
{
    
	static DataMap componentQry = new DataMap();
    public IData getRequestData()
    {
        IData data = null;
        try
        {
            data = super.getContext().getData();
        }
        catch (Exception e)
        {
        }
        return data;
    }

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
            return;
        
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/ziyoubusiness/ziyoubusidynamicpage.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/iorder/icsserv/component/ziyoubusiness/ziyoubusidynamicpage.js");
        }
        super.renderComponent(writer, cycle);
    }

    public String getHtmlTemplate()
    {
        IData source = getRequestData();
        if (source == null || !source.containsKey("REFRESH"))
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try
        {
            IDataset result1 = CSViewCall.call(this,"SS.ZiYouBusiDynamicPageSVC.initPageElement", source);
            log.debug("333333333333333333333333333333333333333333333"+result1);
            IDataset result = result1.getData(0).getDataset("newPages");
            if (!(result == null))
            {
            	 for (int i = 0; i < result.size(); i++)
                 {
                     IData data = result.getData(i);
                     parseTag(data, sb);
                 }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        log.debug("2222222222222222222222222222222222222222222222222"+sb.toString());
        return sb.toString();
    }

    private void parseTag(IData data, StringBuilder sb) throws Exception
    {
        String tag = data.getString("TAG");
        sb.append("<" + tag);
        data.remove("TAG");
        dealParameters(data, sb);
        sb.append(" >");
        if (data.containsKey("INNER_HTML"))
        {
            sb.append(data.getString("INNER_HTML", ""));// 这里一般是文本或者title
        }
        if (data.containsKey("CHILDREN"))
        {
            IDataset childrens = data.getDataset("CHILDREN");
            if (childrens!=null)
                for (int i = 0; i < childrens.size(); i++)
                {
                    IData child = childrens.getData(i);
                    if (child!=null)
                        parseTag(child, sb);
                }
        }
        sb.append("</" + tag + ">");
    }

    private void dealParameters(IData data, StringBuilder sb) throws Exception
    {
        if (StringUtils.isNotEmpty(data.getString("JWCID")))
        {
            String jwcid = data.getString("JWCID", "");
            String[] jwcids = jwcid.split("@");
            if (jwcids.length != 2)
            {
            	BizException.bizerr(BizErr.BIZ_ERR_1, "页面配置错误！");
            }
            String id = jwcids[0];
            if (StringUtils.isNotEmpty(id))
            {
                data.put("ID", id);
                data.put("NAME", id);
            }
        }
        if (StringUtils.isNotEmpty(data.getString("ID")) && StringUtils.isEmpty(data.getString("NAME")))
        {
            data.put("NAME", data.getString("ID"));
        }
        sb.append(" id=" +"\""+ data.getString("ID")+"\""+ " ");
        sb.append(" name="+"\"" + data.getString("NAME") +"\""+ " ");
        if (StringUtils.isNotEmpty(data.getString("JWCID")))
        {
            sb.append(" jwcid=" +"\"" + data.getString("JWCID")+"\"" + " ");
        }
        if (StringUtils.isNotEmpty(data.getString("CLASS")))
        {
            sb.append(" class=" + "\""+data.getString("CLASS") + "\""+" ");
        }

        if (StringUtils.isNotEmpty(data.getString("STYLE")))
        {
            sb.append(" style=" + "\""+data.getString("STYLE")+ "\"" + " ");
        }

        if (StringUtils.isNotEmpty(data.getString("PARAM")))
        {
            sb.append(" " + data.getString("PARAM") + " ");
        }

        if (StringUtils.isNotEmpty(data.getString("BINDING_VALUE")))
        {
            IData source = getRequestData();
            String bingValue = data.getString("BINDING_VALUE");
            String[] bingValues = bingValue.split(",");
            for (int i = 0; i < bingValues.length; i++)
            {
                String kbv = bingValues[i];
                String[] kbvs = kbv.split("=");
                String key = kbvs[0].trim();
                String value = kbvs[1].trim();
                if (value.startsWith(":"))
                {
                    value = source.getString(value.substring(1));
                }
                else if (value.startsWith("&"))
                {
                    kbv = kbv.substring(kbv.indexOf("=") + 1);
                    kbvs = kbv.split("&");
                    for (int j = 0; j < kbvs.length; j++)
                    {
                        String str = kbvs[j];
                        if (str.length() > 0)
                        {
                            String[] ss = str.split("=");
                            if (ss[1].startsWith(":"))
                            {
                                ss[1] = source.getString(ss[1].substring(1));
                            }
                            kbvs[j] = StringUtils.join(ss, "=");
                        }
                    }
                    value = StringUtils.join(kbvs, "&");
                }
                sb.append(" " + key + "=" + value + " ");
            }
        }
        
    }
}
