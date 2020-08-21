
package com.asiainfo.veris.crm.order.web.group.upgprelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class DesUnifyPayRelation extends GroupBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData condData = new DataMap();
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", "7345");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, 
                "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        
        if (IDataUtil.isNotEmpty(resultSet))
        {
            condData.put("MEB_FILE_SHOW","true");
        }
        
        setCondition(condData);
        setMessage("请输入查询条件~~");
    }

    /**
     * 查询集团统一付费产品的付费关系信息
     * @param cycle
     * @throws Exception
     */
    public void qryInfoList(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        IData svcData = new DataMap();
        String svcName = "CS.PayRelaInfoQrySVC.qryUnifyPayRelaBySerialNumber";

        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataOutput output = CSViewCall.callPage(this, svcName, 
                svcData, getPagination("pageNavInfo"));
        // 设置返回值
        setInfoList(output.getData());
        setInfoCount(output.getDataCount());

        setCondition(getData());
        setMessage("查询结果~~");
    }
    
    /**
     * 注销统一付费关系
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        // 服务数据
        IData svcData = new DataMap();
        
        String checkValue = getData().getString("CHECKVALUE_LIST");
        String serialNumber = getData().getString("SERIAL_NUMBER");
        String actionFlag = getData().getString("ACTION_FLAG");
        
        String mebFileShow = getData().getString("MEB_FILE_SHOW");
        String mebFileList = getData().getString("MEB_FILE_LIST");
        
        
        // 解析前台传过来的字符串信息
        String[] checkListArray = checkValue.split(",");
        
        if(checkListArray.length != 4)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        
        String acctId = checkListArray[0];//集团产品的统付账户
        String userId = checkListArray[1];//被统付的成员userId
        String payItemCode = checkListArray[2];//统付的付费编码
        
        IData params = new DataMap();
        params.put("ACCT_ID", acctId);
        params.put("USER_ID", userId);
        params.put("PAYITEM_CODE", payItemCode);
        params.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 调用服务
        IDataset accInfosList = CSViewCall.call(this, 
                "CS.UserSpecialPayInfoQrySVC.qryUserSpecialPayForUPGP",
                params);
        
        if (IDataUtil.isEmpty(accInfosList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_638, acctId, userId);
        }
        
        String userIdA = accInfosList.getData(0).getString("USER_ID_A");//统付的集团产品的user_ida
        
        svcData.put("ACCT_ID", acctId);
        svcData.put("USER_ID", userIdA);
        svcData.put("PAY_ITEM_CODE", payItemCode);
               
        
        if(StringUtils.isNotBlank(mebFileShow) 
                && StringUtils.equals(mebFileShow, "true"))
        {
            if(StringUtils.isNotBlank(mebFileList))
            {
                svcData.put("MEB_FILE_SHOW", mebFileShow);
                svcData.put("MEB_FILE_LIST", mebFileList);
            }
        }

        svcData.put("ACTION_FLAG", actionFlag);
        svcData.put("SERIAL_NUMBER", serialNumber);//被统付的成员号码
                
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        //add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
        svcData.put("MEB_VOUCHER_FILE_LIST", this.getData().getString("MEB_VOUCHER_FILE_LIST", ""));
        svcData.put("AUDIT_STAFF_ID", this.getData().getString("AUDIT_STAFF_ID", ""));

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DesUnifyPayRelationSVC.crtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfoList(IDataset infoList);

    public abstract void setMessage(String message);
}
