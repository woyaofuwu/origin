package com.asiainfo.veris.crm.iorder.web.igroup.esop.complaint;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.bizview.base.CSBasePage;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;

public abstract class CreateWorkForm extends CSBasePage
{
    public abstract void setInfos(IDataset infos);

	 /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryLineInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        String groupId = data.getString("GROUP_ID", "");
    	if(StringUtils.isEmpty(groupId))
    	{
    		return ;
    	}
    	IData param = new DataMap();
    	param.put("GROUP_ID", groupId);
    	IDataset productInfos = CSViewCall.call(this, "SS.QcsGrpIntfSVC.qryGrpUserProInfo", param);
    	setInfos(productInfos);
    }
    
    public void submit(IRequestCycle cycle) throws Exception
    {
    	IData idata = getData();
    	IData data = new DataMap();
		data.put("X_TRANS_CODE", "ITF_KF_WF_CREATE"); // 请求服务名
		data.put("ACCEPT_PHONE_CODE", idata.get("ACCEPT_PHONE_CODE")); // 投诉业务对应的实际号码
		data.put("LINK_PHONE_CODE", idata.get("LINK_PHONE_CODE")); // 联系人电话
		data.put("LINK_NAME", idata.get("LINK_NAME")); // 联系人姓名
		//data.put("TERM_TYPE_CODE", "1"); // 用户类型0个人用户 1集团用户
		data.put("IS_COR_CUSTMOR", "01"); // 海南标准前面加0,TERM_TYPE_CODE字段变成IS_COR_CUSTMOR
		data.put("WORKFORM_TITLE", idata.get("WORKFORM_TITLE")); // 工单标题
		data.put("TRADE_TYPE_CODE", idata.get("TRADE_TYPE_CODE")); // 工单业务类型
		data.put("EXPLAIN_CONTENT", idata.get("EXPLAIN_CONTENT")); // ESOP生成工单
		data.put("DEAL_CLASS_CODE", idata.get("DEAL_CLASS_CODE")); // 紧急程度
		data.put("CONTENT", idata.get("CONTENT")); // 业务内容
		data.put("ACCEPT_EPARCHY_CODE", idata.get("ACCEPT_EPARCHY_CODE")); // 投诉的归属地
		data.put("SP_CODE", idata.get("SP_CODE")); // SP代码
		data.put("SP_NAME", idata.get("SP_NAME")); // SP名称
		data.put("GLOBAL_OR_LOCAL", idata.get("GLOBAL_OR_LOCAL")); // SP是全网还是本地
		data.put("GLOBAL_OR_LOCAL", idata.get("GLOBAL_OR_LOCAL").equals("")?"":"0"+idata.get("GLOBAL_OR_LOCAL")); // 海南标准前面加0
		data.put("APPEAL_CHANNEL_CODE", "10"); // 客户投诉的渠道
		data.put("ACCEPT_STAFF_ID", this.getVisit().getStaffId()); // 受理员工
		data.put("ACCEPT_CITY_CODE", this.getVisit().getCityCode()); // 受理地市
		data.put("ACCEPT_DEPART_ID", this.getVisit().getDepartId()); // 受理部门
		data.put("ACCEPT_TIME", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss")); // 工单实际受理的时间点
		data.put("ATTACH_NAME", ""); // 附件名称
		data.put("ATTACH_LENGTH", ""); // 附件大小
		data.put("ATTACH_URL", ""); // 附件下载地址
		data.put("PRE_VALUE_S1", ""); // 备用字段
		data.put("PRE_VALUE_S2", ""); // 备用字段
		data.put("PRE_VALUE_S3", ""); // 备用字段
		data.put("PRE_VALUE_S4", ""); // 备用字段
		data.put("EPARCHY_CODE", idata.get("group_CITY_CODE"));  //受理号码对应的业务归属地 即集团用户归属的分公司,如：HNHK，HNSY，HNDZ等
		data.put("ACCEPT_CITY_CODE", this.getVisit().getCityCode());  //受理地市归属地 客户经理所在分公司的归属地
		
		//调用接口生成投诉单
    }
}
