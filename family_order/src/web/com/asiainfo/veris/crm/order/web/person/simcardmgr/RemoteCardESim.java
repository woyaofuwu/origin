
package com.asiainfo.veris.crm.order.web.person.simcardmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemoteCardESim extends PersonBasePage
{

    public abstract IData getUserInfo();

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
 
    	
        IData data = getData();
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        
        // 用户资料
       //IData custInfo =new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        /**
         * bug 海口 刘思宜----补换卡报错
         * <br/>
         * 当客户名称为特殊字时data里面的CUST_INFO格式为乱
         * @author zhuoyingzhi
         * @date 20170904
         */ 
        IData custInfo=new DataMap();
        try {
        	 custInfo =new DataMap(data.getString("CUST_INFO", ""));
		} catch (Exception e) {
			//出现特殊字,则需要转换,获取客户信息方式
	        String custId=userInfo.getString("CUST_ID", "");
	        IData custInfoParam=new DataMap();
	        custInfoParam.put("CUST_ID", custId);
	        custInfoParam.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
	        custInfo = CSViewCall.callone(this, "SS.ChangeCardSVC.getCustInfo", custInfoParam);
		}
        /*************************************/
        
        setCustInfo(custInfo);
        //TODO huanghua 翻译产品名称与品牌名称
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo.getString("PRODUCT_ID","")));
        userInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE","")));
        setUserInfo(userInfo);
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.getUserResource", userInfo);// data ) ;

        //查询OLD EID
        IData oldCard = output.getData(0);
        IData  in = new DataMap();
        in.put("USER_ID", userInfo.getString("USER_ID"));
        in.put("USER_ID_A", "-1");
        in.put("RES_TYPE_CODE", "E");
        IDataset userResset = CSViewCall.call(this, "SS.BestUseMobileSVC.getUserResInfoByUserIdRestype", in);

        if(IDataUtil.isNotEmpty(userResset)){
            String OLD_EID = userResset.first().getString("RSRV_STR2","");
            String [] eidImei = OLD_EID.split("@");
            if(eidImei.length>1){
                OLD_EID = eidImei[0];
                //oldImei = eidImei[1];
            }
            oldCard.put("EID", OLD_EID);
        }
        System.out.println("---------RemoteCardESim------1008------oldCard"+oldCard);

        setOldCard(oldCard);
        IData  outputIdate=output.getData(0);
        setAjax(outputIdate);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-5 下午07:45:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE", "141");
        String authType = pgData.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setInfo(info);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String serialNumber = getData().getString("AUTH_SERIAL_NUMBER");
        if("true".equals(data.getString("RES_TYPE_FLAG"))){
        	data.put("RES_TYPE", data.getString("RES_KIND_CODE"));
        }
        // getStaticValue(getVisit(),'TD_B_PRODUCT','PRODUCT_ID','PRODUCT_NAME',userInfo.PRODUCT_ID)"
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    
        IData newSimInfo = new DataMap(data.getString("NEW_SIM_CARD_INFO"));
        String newImsi = newSimInfo.getString("IMSI", "");
        data.putAll(newSimInfo);
        //查询OLD EID
        IData  in = new DataMap();
        in.put("USER_ID", data.getString("USER_ID"));
        in.put("USER_ID_A", "-1");
        in.put("RES_TYPE_CODE", "E");
        IDataset userResset = CSViewCall.call(this, "SS.BestUseMobileSVC.getUserResInfoByUserIdRestype", in);
        System.out.println("---------RemoteCardESim------1009------userResset"+userResset);

        if(IDataUtil.isNotEmpty(userResset)){
        	String OLD_EID = userResset.first().getString("RSRV_STR2","");
        	String [] eidImei = OLD_EID.split("@");
			if(eidImei.length>1){
				OLD_EID = eidImei[0];
				//oldImei = eidImei[1];
			}
        	data.put("OLD_EID", OLD_EID);
        }
        System.out.println("---------RemoteCardESim------1009------data"+data);

        data.put("SERIAL_NUMBER", serialNumber);
        data.put("IMSI", newImsi);
        data.put("ORDER_TYPE_CODE","142");
        data.put("TRADE_TYPE_CODE","142");
        data.put("REMARK", "OneNoOneTerminal");
        IDataset dataset = CSViewCall.call(this, "SS.ChangeCardSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 释放选占
     * 
     * @param cycle
     * @throws Exception
     */

    public void releaseSingleRes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.CreatePersonUserSVC.releaseSingleRes", data);
        setAjax(output);
    }

    public abstract void setCustInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setNewCard(IData a);
    
    public abstract void setCondition(IData comm);

    public abstract void setOldCard(IData a);

    public abstract void setUserInfo(IData userInfo);

    /**
     * 校验新sim信息
     */
    public void verifySimCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER", ""));

        String oldSimCardInfo = data.getString("OLD_SIM_CARD_INFO");
        
        if(StringUtils.isBlank(oldSimCardInfo))
        {
            data.put("PHONE_NUM", data.getString("AUTH_SERIAL_NUMBER", ""));
            IData userInfo = CSViewCall.call(this, "SS.QueryUserInfoSVC.queryUserInfo", data).getData(0);
            userInfo.put("REMOTECHANGECARD_TAG", "1");
            IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.getUserResource", userInfo);
            data.put("OLD_SIM_CARD_INFO", output.getData(0));
        }
        String newImei = data.getString("IMEI");
        if(StringUtils.isNotBlank(newImei)){
        	String[] imeis = newImei.split(",");
            newImei = imeis[0];
            data.put("NEW_IMEI", newImei);
        }
        String eid = data.getString("NEW_EID");
        data.put("EID", eid);
        IDataset results = CSViewCall.call(this, "SS.BestUseMobileSVC.qryApplyReplaceESIM", data);
        setNewCard(results.getData(0));
        setAjax(results.getData(0));
    }

    /**
     * 校验新sim信息
     */
    public void verifyIMEI(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String newImei = data.getString("NEW_IMEI");
        String[] imeis = newImei.split(",");
        newImei = imeis[0];
        data.put("NEW_IMEI", newImei);
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER", ""));
        IDataset output = CSViewCall.call(this, "SS.BestUseMobileSVC.verifyIMEI", data);
        //setNewCard(output.getData(0));
        setAjax(output.getData(0));
    }

    /**
     * 需求名: 关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
     * 接口描述：前台检索eSIM设备合法性校验结果
     * @author by tancs 2019-05-22
     * @throws Exception
     */
    public void queryCheckResult(IRequestCycle cycle) throws Exception{
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER", ""));

        IDataset results = CSViewCall.call(this, "SS.BestUseMobileSVC.queryCheckResult", data);
        setNewCard(results.getData(0));
        setAjax(results.getData(0));
    }

}
