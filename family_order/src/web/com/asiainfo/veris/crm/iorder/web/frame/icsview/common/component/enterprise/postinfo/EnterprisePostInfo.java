package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.postinfo;


import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class EnterprisePostInfo extends BizTempComponent {

	public abstract void setPostInfo(IData postInfo);
	public abstract void setAskPrintInfo(IData askPrintInfo);
	public abstract String getUserId();
	
	public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		boolean isAjax = isAjaxServiceReuqest(cycle);
		String listener = getPage().getData().getString("ajaxListener", "");
		
		if ((isAjax) && (StringUtils.isNotBlank(listener))) {
			if ("queryMemberInfo".equals(listener)){
				queryMemberInfo();
			}else if("queryPostInfo".equals(listener)){
				queryPostInfo(getPage().getData().getString("USER_ID", ""));
			}
		}else{
			initPage(informalParametersBuilder, writer, cycle,isAjax);
		}

	}

	private void initPage(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle,boolean isAjax)
			throws Exception {
		String js1 = "scripts/iorder/icsserv/component/enterprise/postinfo/EnterprisePostInfo.js";
		if (isAjax)
			includeScript(writer, js1, false, false);
		else {
			getPage().addResAfterBodyBegin(js1, false, false);
		}
	}
	
    public void queryPostInfo(String userId) throws Exception{
        if (StringUtils.isBlank(userId))
        {
        	return;
        }
        // 邮寄信息

        IData param = new DataMap();
        param.put("ID", userId);
        param.put("ID_TYPE", "1");
        // 修改为调用服务

        IDataset postinfos = CSViewCall.call(this, "CS.UserPostInfoQrySVC.qryPostInfoForGrp", param);

        if (postinfos != null && postinfos.size() > 0)
        {
            IData postInfo = postinfos.getData(0);
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

        // 催缴及账户打印

        param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "GRUA");
        // 修改为调用服务

        IDataset askinfos = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherByUserRsrvValueCodeForGrp", param);

        if (askinfos != null && askinfos.size() > 0)
        {
            IData askInfo = askinfos.getData(0);
            setAskPrintInfo(askInfo);
        }
    }
    public void queryMemberInfo() throws Exception{
        String strMebSn = getPage().getData().getString("cond_SERIAL_NUMBER");
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);
        IData idUserInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn, false);
        if (idUserInfo != null)
        {
        	getPage().setAjax("flag", "true");
        }
        else
        {
        	getPage().setAjax("flag", "false");
        }

    }
}