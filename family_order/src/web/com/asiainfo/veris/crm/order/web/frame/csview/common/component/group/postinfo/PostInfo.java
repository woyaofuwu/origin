
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.postinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpostinfo.UserPostInfoIntfViewUtil;

public abstract class PostInfo extends CSBizTempComponent
{

    private final static String SCRIPT_ATTRIBUTE = PostInfo.class.getName();

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setUserId(null);
            setOperType(null);
            setPostInfo(null);
            setOldPostInfo(null);
            setOldAskInfo(null);
            setAskPrintInfo(null);
        }
    }

    public abstract IData getAskPrintInfo();

    // 操作类型
    public abstract String getOperType();

    public abstract IData getPostInfo();

    // 用户ID
    public abstract String getUserId();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        try
        {
            if ((BizCtrlType.ChangeUserDis).equals(getOperType()))
            {
                // 邮寄信息
                String user_id = getUserId();
                IDataset postinfos = UserPostInfoIntfViewUtil.qryGrpUserPostInfosByUserId(this, user_id);

                if (IDataUtil.isNotEmpty(postinfos))
                {
                    IData postInfo = postinfos.getData(0);
                    setOldPostInfo(postInfo.toString());
                    if ("1".equals(postInfo.getString("POST_TAG")))
                    {
                        String POST_CONTENT = postInfo.getString("POST_CONTENT", "");
                        int num = POST_CONTENT.length();
                        for (int i = 0; i < num; i++)
                        {
                            postInfo.put("POST_CONTENT" + "_" + POST_CONTENT.charAt(i), "true");
                        }

                        String POST_TYPESET = postInfo.getString("POST_TYPESET", "");
                        num = POST_TYPESET.length();
                        for (int i = 0; i < num; i++)
                        {
                            postInfo.put("POST_TYPESET" + "_" + POST_TYPESET.charAt(i), "true");
                        }
                    }
                    else
                    {
                        postInfo.put("POST_CYC", "");
                    }
                    setPostInfo(postInfo);
                }

                // 催缴及账户打印                IDataset askinfos = UserOtherInfoIntfViewUtil.qryGrpAskPrintInfoByUserIdAndRsrvValueCode(this, user_id);
                if (IDataUtil.isNotEmpty(askinfos))
                {
                    IData askInfo = askinfos.getData(0);
                    setOldAskInfo(askInfo.toString());
                    setAskPrintInfo(askInfo);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/postinfo/PostInfo.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");
        StringBuilder init_script = new StringBuilder();
        init_script.append("$(document).ready(function(){\r\n");
        init_script.append("\t initPostInfo(); \r\n");
        init_script.append("});\r\n");

        getPage().addScriptBeforeBodyEnd(SCRIPT_ATTRIBUTE, init_script.toString());

        // super.renderComponent(writer, cycle);
    }

    public abstract void setAskPrintInfo(IData postinfo);

    public abstract void setOldAskInfo(String info);

    public abstract void setOldPostInfo(String info);

    public abstract void setOperType(String operType);

    public abstract void setPostInfo(IData postinfo);

    public abstract void setUserId(String userId);
}
