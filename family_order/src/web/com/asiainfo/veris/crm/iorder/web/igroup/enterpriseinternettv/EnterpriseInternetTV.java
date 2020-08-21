package com.asiainfo.veris.crm.iorder.web.igroup.enterpriseinternettv;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class EnterpriseInternetTV extends GroupBasePage
{
    private static final String serial_Number = "SERIAL_NUMBER";

    private static final String user_Id = "USER_ID";

    private static final String r_tncode = "RTNCODE";

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset dataset = new DatasetList(data.getString("FTTH_DATASET"));
        IData input = new DataMap();
        input.put("REMARK", "企业互联网电视终端申领");

        input.put("FTTH_DATASET", dataset);

        input.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        input.put(user_Id, data.getString("USER_ID_A", ""));
        input.put(serial_Number, data.getString(serial_Number, ""));

        IDataset result = CSViewCall.call(this, "SS.EnterpriseInternetTVUserSVC.crtOrder", input);
        this.setAjax(result);

    }

    /**
     * 查询商务宽带信息
     * 
     * @author xuyt
     * @param cycle
     * @throws Exception
     */
    public void qryUserWidenet(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IData input = new DataMap();
        String kdSerialNumber = data.getString("KD_NUMBER");

        input.put("SERIAL_NUMBER", kdSerialNumber);
        IData widenet = queryUserWideNetBySnInfo(input);
        this.setInfo(widenet);
        this.setAjax(widenet);

    }

    public IData queryUserWideNetBySnInfo(IData param) throws Exception
    {
        IData data = param;
        IData widenet = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataset result = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.queryUserWideNetBySnInfo", data);

        if (IDataUtil.isNotEmpty(result))
        {
            widenet = result.getData(0);
            widenet.put("KD_NUMBER", data.getString(serial_Number));
            widenet.put("KD_ADDR", widenet.getString("DETAIL_ADDRESS", ""));
            widenet.put("KD_PHONE", widenet.getString("CONTACT_PHONE", ""));
            widenet.put("CUST_NAME", widenet.getString("CONTACT", ""));
            widenet.put("KD_USERID", widenet.getString(user_Id, ""));
            widenet.put(r_tncode, "1");
            return widenet;
        }
        else
        {
            widenet.put(r_tncode, "8");// 8=已经在OTHER表，说明已经申领过光猫
            widenet.put("RTNMSG", "查询不到商务宽带号码信息。");
            return widenet;
        }

    }

    /**
     * 查询所有的机顶盒成员信息
     * 
     * @author xuyt
     * @param cycle
     * @throws Exception
     */
    public void qryGrpUser(IRequestCycle cycle) throws Exception
    {
        IData paramData = this.getData();
        String serial_number = "";
        serial_number = paramData.getString("SERIAL_NUMBER");
        if(StringUtils.isEmpty(serial_number)){
        	serial_number = paramData.getString("cond_SERIAL_NUMBER");//分页传入参数带cond
        }
        Pagination page = getPagination("pageNav");
        // 获取用户信息
        IData parem = new DataMap();
        parem.put(serial_Number, serial_number);
        parem.put("REMOVE_TAG", "0");
        parem.put("NET_TYPE_CODE", "00");
        IData userinfo = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serial_number, true);

        IDataset infos = new DatasetList();
        IData condition = new DataMap();

        if (IDataUtil.isNotEmpty(userinfo))
        {
            String userIdA = userinfo.getString(user_Id);
            String relationTypeCode = "90";
            String roleCodeB = "1";

            IData inparam = new DataMap();
            inparam.put("USER_ID_A", userIdA);
            inparam.put("RELATION_TYPE_CODE", relationTypeCode);
            inparam.put("ROLE_CODE_B", roleCodeB);

            IDataOutput dataOutput = CSViewCall.callPage(this, "SS.EnterpriseInternetTVSVC.qryRelaBBInfoByRoleCodeBForGrp", inparam, page);
            IDataset bbossuu = dataOutput.getData();
            
            if (IDataUtil.isNotEmpty(dataOutput.getData()))
            {
            	if(bbossuu==null||bbossuu.size()==0){
            		 CSViewException.apperr(GrpException.CRM_GRP_713,"该集团下无有效的宽带成员！");
            	}
            	for (int i = 0; i < bbossuu.size(); i++)
                {
                    IData data = bbossuu.getData(i);
                    IDataset userOtherEITV = CSViewCall.call(this, "SS.EnterpriseInternetTVSVC.getOtherInfoByCodeUserId", data);
                    if (IDataUtil.isEmpty(userOtherEITV))
                    {
                        data.put("CATFLAG", "0"); // 申领标识，0未申领，1已申领
                    }
                    else
                    {
                        data.put("RSRV_STR1", userOtherEITV.getData(0).getString("RSRV_STR1", ""));
                        data.put("CATFLAG", "1");
                    }
                    infos.add(data);
                }
            	setInfoCount(dataOutput.getDataCount());
            }

            // condition.put("cond_SERIAL_NUMBER", serial_number);
            // condition.put("cond_USER_ID_A", userIdA);
            // setInfoCount(dataOutput.getDataCount());

        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_197, serial_number);
        }
        
        this.setAjax(infos);
        setInfos(infos);
    }

    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午4:58:49 Modification History: Date Author Version Description ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void checkTerminal(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.EnterpriseInternetTVSVC.checkTerminal", data);
        IData retData = dataset.first();
        this.setResInfo(retData);
        this.setAjax(retData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setResInfo(IData retData);

    public abstract void setInfos(IDataset infos);

    public abstract void setParam(IData data);

    public abstract void setParaminfo(IData paraminfo);

    public abstract void setInfoCount(long infoCount);
}
