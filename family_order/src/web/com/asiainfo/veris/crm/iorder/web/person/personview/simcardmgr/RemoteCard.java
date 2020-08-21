
package com.asiainfo.veris.crm.iorder.web.person.personview.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class RemoteCard extends PersonBasePage
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
        setOldCard(output.getData(0));
        IData  outputIdate=output.getData(0);
        /**
         * REQ201705270006_关于人像比对业务优化需求
         * <br/>
         * 判断是否为携入号码或无效固话号码
         * @author zhuoyingzhi
         * @date 20170630
         */
        String  tradeTypeCode=data.getString("TRADE_TYPE_CODE", "");
        //补卡类型-----（0=补卡 1=换卡）
        String  remoteCardType=data.getString("REMOTECARD_TYPE","");
        
        if("142".equals(tradeTypeCode)&&"0".equals(remoteCardType)){

    	        IData param =new DataMap();
    			param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
    			IData npTagAndWxTag=new DataMap();
    			
    	        //携入标识   1是携入     0不是
    			npTagAndWxTag.put("NPTAG", "0");
    			//固话标识
    			npTagAndWxTag.put("WXTAG", "0");
    			
    			/**
    			 * REQ201805290007_关于人像比对优化需求
    			 * <br/>
    			 * 由于取消  携入号码或固话号码 人像比对限制,则默认返回非 携入号码或固话号码 方式处理
    			 * @author zhuoyingzhi
    			 * @date 20180712
    			 */
/*    			IDataset ids = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.queryNpQry", param);
    			if (IDataUtil.isNotEmpty(ids)) {
    				String asp = ids.getData(0).getString("ASP", "").trim();
    				if ("2".equals(asp) || "3".equals(asp)) {
    					// 2 是联通号码 3 是电信号码
    					npTagAndWxTag.put("NPTAG", "1");
    				}
    			}   			
    			//IDataset userInfo = CSViewCall.call(this, "SS.ModifyUserPwdInfoSVC.qryUserInfo", param);
    		    if(IDataUtil.isNotEmpty(userInfo)){
    		    	 String  netTypeCode=userInfo.getString("NET_TYPE_CODE", "");
    		    	 if("12".equals(netTypeCode)||"11".equals(netTypeCode)||"18".equals(netTypeCode)){
    		    		 //商务电话开户+固话装机开户（net_type_code='12')、宽带装机开户（net_type_code='11')、无线固话开户界面（net_type_code='18') 
    		    		 npTagAndWxTag.put("WXTAG", "1");
    		    	 }
    		    } */
    		    setNpTagAndWxTag(npTagAndWxTag);   
    	        /**
    	         * REQ201707060009关于补卡、密码重置、复机业务优化的需求
    	         * <br/>
    	         * 是否有经办人摄像权限
    	         * @author zhuoyingzhi
    	         * @date 20170805
    	         */
    	        boolean isAgentPortraitComparisonRight = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isAgentPortraitComparisonRight");
    	        
    	        if(isAgentPortraitComparisonRight){
    	       	   //有经办人摄像权限
    	        	outputIdate.put("isAgentRight", "0");
    	        }else{
    	        	//无经办人摄像权限
    	        	outputIdate.put("isAgentRight", "1");
    	        }    		    
        }
        /******************************************************/
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
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE", "142");
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
        // getStaticValue(getVisit(),'TD_B_PRODUCT','PRODUCT_ID','PRODUCT_NAME',userInfo.PRODUCT_ID)"
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IData newSimInfo = new DataMap(data.getString("NEW_SIM_CARD_INFO"));
        String newImsi = newSimInfo.getString("IMSI", "");
        data.put("IMSI", newImsi);
        IDataset dataset = CSViewCall.call(this, "SS.SimCardTrade.tradeReg", data);
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

    public abstract void setOldCard(IData a);

    public abstract void setUserInfo(IData userInfo);

    /*
     * public void getSimCardInfo(IRequestCycle cycle) throws Exception { IData data = getData(); IDataset dataset =
     * CSViewCall.call(this, "SS.ChangeCardSVC.getSimCardInfo", data); setNewCard(dataset.getData(0));
     * setAjax(dataset.getData(0)); }
     */
    /**
     * 校验新sim信息
     */
    public void verifySimCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER", ""));
        IDataset output = CSViewCall.call(this, "SS.SimCardCheckSVC.verifySimCard", data);
        setNewCard(output.getData(0));
        setAjax(output.getData(0));
    }

    /**
     * 检查是否是4G卡
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifySimcardUSIM(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData input = new DataMap();
        input.put("RES_TYPE_CODE", data.getString("RES_TYPE_CODE"));
        IDataset output = CSViewCall.call(this, "SS.SimCardCheckSVC.verifySimcardUSIM", input);
        setAjax(output);
    }
    
    public void checkEmptyCard(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset output = CSViewCall.call(this, "SS.SimCardCheckSVC.checkEmptyCard", data);
        setAjax(output);
    }
    public abstract void setNpTagAndWxTag(IData commInfo);
    /**
     * 人像信息比对
     * 
     * @author zhuoyingzhi
     * @param clcle
     * @throws Exception
     * @date 20170626
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
}
