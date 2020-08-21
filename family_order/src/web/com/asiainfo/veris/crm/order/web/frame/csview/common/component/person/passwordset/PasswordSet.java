
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.passwordset;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class PasswordSet extends CSBizTempComponent
{
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getAfterAction();

    public abstract String getBeforeAction();

    public abstract boolean getHasOldPass();

    public abstract String getPassword();

    public abstract String getPsptId();

    public abstract boolean getRenderBtn();

    public abstract String getSerialNumber();

    public abstract String getUserId();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData data = getPage().getData();
        String action = data.getString("ACTION", "");
        if (null == action || "".equals(action))
        {
            /**
             * 组件初始化载入认证的脚本
             */
            getPage().addResAfterBodyBegin("scripts/csserv/component/person/CommLib.js");
            getPage().addResAfterBodyBegin("scripts/csserv/component/person/passwordset/PasswordSet.js");
            writer.printRaw("<script language=\"javascript\">\n");
            writer.printRaw("$($.password._init);\n");
            writer.printRaw("</script>\n");
        }
        else if ("CHECK_PASSWD".equals(action))
        {
            IDataset infos = CSViewCall.call(this, "CS.AuthCheckSVC.checkPasswd", data);
            getPage().setAjax(infos.getData(0));

            setRenderContent(false); // 不刷新组件
        }else if("AUTH_CHECKPSW".equals(action)){
        	 setRenderContent(false); // 不刷新组件
        	checkPassWork(data);//add bu fufn REQ201710120004
        }
    }
    
    public void checkPassWork(IData data) throws Exception
    {	//add bu fufn REQ201710120004
    	IData returnData = new DataMap();
    	returnData.put("RESULT_CODE", 0);
    	String passWord=data.getString("PassWork","");
    	// 参数容器
        IData param = new DataMap();
        // SUBSYS_CODE
        param.put("SUBSYS_CODE", "CSM");
        // 配置PARAM_ATTR
        param.put("PARAM_ATTR", "251");
        // 得到业务编码
        param.put("PARAM_CODE", "0");
        param.put("EPARCHY_CODE", "0898");
        // 判断所要配置的业务编码是否已配置过
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ManageTaskSVC.isTaskConfigured", param, new Pagination());
        IDataset results = dataCount.getData();
        if(results.size()>0){
        	for(int i =0;i<results.size();i++){
        		IData result=results.getData(i);
        		if(result.getString("PARA_CODE1", "").equals(passWord)){
        			returnData.put("RESULT_CODE", 1);break;
        		}
        	}
        }
    	getPage().setAjax(returnData);
    }

    public abstract void setAfterAction(String afterAction);

    public abstract void setBeforeAction(String beforeAction);

    public abstract void setHasOldPass(boolean hasOldPass);

    public abstract void setPassword(String password);

    public abstract void setPsptId(String psptId);

    public abstract void setRenderBtn(boolean renderBtn);

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setUserId(String userId);

}
