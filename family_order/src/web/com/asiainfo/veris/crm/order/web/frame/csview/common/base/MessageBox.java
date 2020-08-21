
package com.asiainfo.veris.crm.order.web.frame.csview.common.base;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class MessageBox extends CSBasePage
{
    public static class Button
    {
        private String buttonName;

        private String caption;

        private String function;

        private String listener;

        private String onclick;

        private String pageName;

        private String target;

        public String getButtonName()
        {

            return buttonName;
        }

        public String getCaption()
        {

            return caption;
        }

        public String getFunction()
        {

            return function;
        }

        public String getListener()
        {

            return listener;
        }

        public String getOnclick()
        {

            return onclick;
        }

        public String getPageName()
        {

            return pageName;
        }

        public String getTarget()
        {

            return target;
        }

        public void setButtonName(String buttonName)
        {

            this.buttonName = buttonName;
        }

        public void setCaption(String caption)
        {

            this.caption = caption;
        }

        public void setFunction(String function)
        {

            this.function = function;
        }

        public void setListener(String listener)
        {

            this.listener = listener;
        }

        public void setOnclick(String onclick)
        {

            this.onclick = onclick;
        }

        public void setPageName(String pageName)
        {

            this.pageName = pageName;
        }

        public void setTarget(String target)
        {

            this.target = target;
        }
    }

    /** set common message */
    public void setCommonMessage() throws Exception
    {

        setMessage(getParameter("$msg$message"));
        setMessageType(getParameter("$msg$messageType"));

        IData currentData = null;// AppCtx.getReceiveData();
        String[] names = currentData.getNames();
        for (int i = 0; i < names.length; i++)
        {
            if (names[i].startsWith("$msg$"))
            {
                currentData.remove(names[i]);
            }
            if (names[i].startsWith("tradeData"))
            {
                currentData.remove(names[i]);
            }
        }
        setParameters(currentData);
    }

    /** 是否打分 */
    public void setGrade() throws Exception
    {

        // j2ee-movebean
        //
        // td = this.getTradeData();
        // if ("1".equals(td.getJudgeGradeTag()))
        // {
        // AppCtx.setParameter( "NEED_GRADE", "true");
        // AppCtx.setParameter( "TRADE_ID", td.getTradeId());
        // AppCtx.setParameter( "TRADE_TYPE_CODE", td.getTradeTypeCode());
        // AppCtx.setParameter( "TRADE_STAFF_ID", getVisit().getStaffId());
        // AppCtx.setParameter( "TRADE_DEPART_ID", getVisit().getDepartId());
        // AppCtx.setParameter( "START_DATE", td.getSysdate());
        // AppCtx.setParameter( "USER_ID", td.getUserId());
        // }
    }

    /** set hint message */
    public void setHintMessage(IRequestCycle cycle) throws Exception
    {

        String[] paths = null;// AppCtx.getParameters("$msg$path");
        String[] events = null;// AppCtx.getParameters("$msg$event");
        String[] buttons = null;// AppCtx.getParameters("$msg$button");
        String[] targets = null;// AppCtx.getParameters("$msg$target");
        String[] functions = null;// AppCtx.getParameters("$msg$function");
        String[] captions = null;// AppCtx.getParameters("$msg$caption");

        IDataset objects = new DatasetList();

        for (int i = 0; i < buttons.length; i++)
        {
            IData object = new DataMap();
            object.put("NAME", "BUTTON_" + (i + 1));
            object.put("PATH", paths.length < i + 1 ? (String) null : paths[i]);
            object.put("EVENT", events.length < i + 1 ? (String) null : events[i]);
            object.put("BUTTON", buttons[i]);
            object.put("TARGET", targets.length < i + 1 ? (String) null : targets[i]);
            object.put("FUNCTION", functions.length < i + 1 ? (String) null : functions[i]);
            object.put("CAPTION", captions.length < i + 1 ? (String) null : captions[i]);
            objects.add(object);
        }

        setGrade();
        setObjects(objects);
        setCommonMessage();

        // ctx.setTransfer("NEED_MSG");

        // ctx.setTransfer("CUSTOM_JS");
    }

    /** set hint message by script */
    public void setHintMessageByScript(IRequestCycle cycle) throws Exception
    {

        String[] buttons = null;// AppCtx.getParameters("$msg$button");
        String[] functions = null;// AppCtx.getParameters("$msg$function");

        if (buttons.length == 0)
        {
            if (functions.length == 1)
                buttons = new String[]
                { "返回" };
            if (functions.length == 2)
                buttons = new String[]
                { "是", "否" };
        }

        IDataset objects = new DatasetList();
        for (int i = 0; i < buttons.length; i++)
        {
            IData object = new DataMap();
            object.put("BUTTON", buttons[i]);
            object.put("FUNCTION", functions[i]);
            objects.add(object);
        }
        setObjects(objects);
        setCommonMessage();
    }

    public abstract void setMessage(String message);

    public abstract void setMessageType(String message);

    public abstract void setObjects(IDataset objects);

    public abstract void setParameters(IData params);
}
