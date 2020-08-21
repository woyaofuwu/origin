
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifycustinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttModifyCustInfo extends PersonBasePage
{

    /**
     * 查询改名费
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxModiCustName(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        data.put("TRADE_TYPE_CODE", "9726");
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyCustInfoSVC.ajaxModiCustName", data);

        if (dataset == null || dataset.isEmpty())
        {
            dataset = new DatasetList();
        }
        setAjax(dataset);
    }

    /**
     * 获取子业务信息
     * 
     * @return
     * @throws Exception
     */
    public void getCommInfo(IData allInfo) throws Exception
    {
        IData params = new DataMap();
        IData temp = new DataMap();
        params.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        params.put("RSRV_VALUE_CODE", "REAL");
        params.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", params);// getUserOtherUserId(params);
        IData custInfo = allInfo.getData("CUST_INFO");
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData data = dataset.getData(0);
            // 如果实名制预受理时 没有填写相应的资料 则用 客户原来的资料代替
            if (StringUtils.isNotBlank(data.getString("RSRV_STR2")))
            {
                temp.put("REAL_CUST_NAME", data.getString("RSRV_STR2"));
            }
            else
            {
                temp.put("REAL_CUST_NAME", custInfo.getString("CUST_NAME"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR3")))
            {
                temp.put("REAL_PSPT_TYPE_CODE", data.getString("RSRV_STR3"));
            }
            else
            {
                temp.put("REAL_PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR4")))
            {
                temp.put("REAL_PSPT_ID", data.getString("RSRV_STR4"));
            }
            else
            {
                temp.put("REAL_PSPT_ID", custInfo.getString("PSPT_ID"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR5")))
            {
                temp.put("REAL_PSPT_ADDR", data.getString("RSRV_STR5"));
            }
            else
            {
                temp.put("REAL_PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
            }

            if (StringUtils.isNotBlank(data.getString("RSRV_STR6")))
            {
                temp.put("REAL_PHONE", data.getString("RSRV_STR6"));
            }
            else
            {
                temp.put("REAL_PHONE", custInfo.getString("PHONE"));
            }
            temp.put("REAL_REG", "1");// 存在实名制预登记记录
        }
        else
        {
            temp.put("REAL_CUST_NAME", "");
            temp.put("REAL_PSPT_TYPE_CODE", "");
            temp.put("REAL_PSPT_ID", "");
            temp.put("REAL_PSPT_ADDR", "");
            temp.put("REAL_PHONE", "");
            temp.put("REAL_REG", "0");// 不存在实名制预登记记录
        }

        params.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        params.put("GOODS_STATE", "0");

        // 根据用户标识USER_ID查询用户购机信息
        IDataset purchaseDataset = CSViewCall.call(this, "CS.UserSaleGoodsQrySVC.getPurchaseInfoByUserId", params);
        if (IDataUtil.isNotEmpty(purchaseDataset))
        {
            temp.put("IS_IN_PURCHASE", "1");// 用做前台判断，如果用户还处在营销活动期限内，则不能够修改客户名称
        }
        else
        {
            temp.put("IS_IN_PURCHASE", "0");
        }
        // 用户实名制用TF_F_CUSTOMER表中的IS_REAL_NAME字段来进行判断
        if ("1".equals(allInfo.getData("CUST_INFO").getString("IS_REAL_NAME", "")))
        {
            temp.put("REAL_NAME", "true");
        }
        else
        {
            temp.put("REAL_NAME", "");
        }
        // 是否有特殊资料修改权限
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "MODIFYSPECUSTINFO"))
        {
            temp.put("STAFF_SPECIAL_RIGTH", "true");
        }
        else
        {
            temp.put("STAFF_SPECIAL_RIGTH", "false");
        }

        setCommInfo(temp);

        // 如果员工拥有特殊权限，且用户为实名制，则记录查询日志。
        if ("true".equals(temp.getString("REAL_NAME")) && "true".equals(temp.getString("STAFF_SPECIAL_RIGTH")))
        {
            writeLanuchLog(allInfo);
        }

    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
    	 IData pgData = getData();
         pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("CUST_EPARCHY_CODE"));
         IData custInfos = CSViewCall.callone(this, "SS.ModifyCustInfoSVC.getCustInfo", pgData);
         
         /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  start *******/
         //查询责任人信息
         IData params=new DataMap();
         params.put("CUST_ID", pgData.getString("CUST_ID"));
         params.put("SERIAL_NUMBER", pgData.getString("SERIAL_NUMBER"));
         IDataset idCust = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.qryCustPersonOtherInfoByCustId", params);
         if(idCust!=null&&idCust.size()>0){
        	 IData custInfo=custInfos.getData("CUST_INFO");
        	 custInfo.put("RESP_CUST_NAME", idCust.getData(0).getString("RSRV_STR2","")); 
        	 custInfo.put("RESP_PSPT_TYPE_CODE", idCust.getData(0).getString("RSRV_STR3","")); 
        	 custInfo.put("RESP_PSPT_ID", idCust.getData(0).getString("RSRV_STR4",""));
        	 custInfo.put("RESP_PSPT_ADDR", idCust.getData(0).getString("RSRV_STR5",""));
         }
         /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  end *******/
         
         
         setCustInfo(custInfos.getData("CUST_INFO"));
         setCommInfo(custInfos.getData("COMM_INFO"));
         
         IData idUserInfo = new DataMap();
         String strUserID = pgData.getString("USER_ID", "");
         String strNetTypeCode = pgData.getString("NET_TYPE_CODE", "");
         idUserInfo.put("USER_ID", strUserID);
         idUserInfo.put("NET_TYPE_CODE", strNetTypeCode);
         
         String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", this.getVisit().getDepartId());
         IData id = new DataMap();
         String staffid = getVisit().getStaffId();
         id.put("LOGIN_STAFF_ID", staffid);
         id.put("DEPART_KIND_CODE", departKindCode);
         idUserInfo.put("LOGIN_STAFF_ID", staffid);
         idUserInfo.put("DEPART_KIND_CODE", departKindCode);
         setUserInfo(idUserInfo);
         this.setAjax(id);
    }
    
    /**
     * 证件号码数限制校验
     * 
     * @author yanwu
     * @param clcle
     * @throws Exception
     */
    public void checkRealNameLimitByPspt(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	/*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  start *******/
    	
    	//集团客户不做证件号码数限制
    	String psptTyeCode=data.getString("PSPT_TYPE_CODE");
    	if("D".equals(psptTyeCode)||"E".equals(psptTyeCode)||"G".equals(psptTyeCode)
    	    	||"L".equals(psptTyeCode)||"M".equals(psptTyeCode)){
    		data.put("MSG", "OK");
    		data.put("CODE", "0");
    		setAjax(data);
    	}else{
    		data.put("CODE", "1");
        	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt", data);
            setAjax(dataset.first());
    	}
    	
        /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  start *******/
    }

    /**
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData params = getData("custInfo", true);
        params.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        params.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
        data.putAll(params);
        IDataset dataset = CSViewCall.call(this, "SS.CttModifyCustInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAjaxDataset(IDataset ajaxDataset);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserInfo(IData userInfo);

    public void writeLanuchLog(IData allInfo) throws Exception
    {
        IData custInfo = allInfo.getData("CUST_INFO");
        IData userInfo = allInfo.getData("USER_INFO");
        IData params = new DataMap();
        params.put("CUST_ID", custInfo.getString("custInfo"));
        params.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        params.put("USER_ID", userInfo.getString("USER_ID"));
        params.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        params.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE"));
        // BRAND_CODE todo 品牌
        CSViewCall.call(this, "SS.CttModifyCustInfoSVC.writeLanuchLog", params);
    }
}
