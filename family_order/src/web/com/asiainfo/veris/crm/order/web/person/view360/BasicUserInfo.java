
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BasicUserInfo extends PersonBasePage
{
	
	protected static Logger log = Logger.getLogger(BasicUserInfo.class);
	
    /**
     * 用户基本信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
    	//log.info("("linsl queryInfo start");
        IData data = getData();
        
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        IDataset infos = CSViewCall.call(this, "CS.UserListSVC.queryUserBySn", params);
        
        //log.info("("linsl infos:" + infos.toString());
        
        if (infos == null || infos.size() == 0)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"获取用户资料无数据!");
        }
        
        if(infos.size() > 0)
        {
        	data.put("USER_ID", infos.getData(0).getString("USER_ID", ""));
        	data.put("CUST_ID", infos.getData(0).getString("CUST_ID", ""));
        }
        
                
        if (StringUtils.isNotBlank(data.getString("USER_ID", "")))
        {

            data.put("USER_ID", data.getString("USER_ID", ""));
            data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

            IDataset userInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfo", data);
            IDataset accountInfos =CSViewCall.call(this, "SS.GetUser360ViewSVC.qryAccountInfo", data);            
            IDataset resInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserResSimInfo", data);
  
            IData user = new DataMap();
            IData accountInfo = new DataMap();

            if(IDataUtil.isNotEmpty(userInfo))
            {
                //add by duhj 通过调产商品接口,查询产品与名牌名称  2017/03/06
                IData result = CSViewCall.callone(this, "SS.GetUser360ViewSVC.getUserName", userInfo.getData(0));

            	
            	user = userInfo.getData(0);
            	user.putAll(result);
            }
            
            if(IDataUtil.isNotEmpty(accountInfos))
            {
            	accountInfo = accountInfos.getData(0);
            	//获取积分
            	accountInfo.put("SCORE_VALUE", queryScoreValue(data));
            }
            
            if(IDataUtil.isNotEmpty(resInfo))
            {
            	user.put("RES_KIND_NAME", resInfo.getData(0).getString("RES_KIND_NAME",""));
            }
            
            //获取用户星级等级
            IData condition = new DataMap();
            String sn = data.getString("SERIAL_NUMBER", "");
            String userId = data.getString("USER_ID", "");
            condition.put("SERIAL_NUMBER", sn);
            condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
            condition.put("USER_ID", userId);
            setCond(condition);
            condition.put("IDTYPE", "0");
            IDataset output = new DatasetList();
//            IData userInfo = new DataMap();
            if (StringUtils.isNotBlank(userId))
            {
                output = CSViewCall.call(this, "SS.GetUser360ViewSVC.getCreditInfo", condition);
                if (IDataUtil.isNotEmpty(output))
                {
                    if (!"-1".equals(output.getData(0).getString("CREDIT_CLASS", "")) && !"".equals(output.getData(0).getString("CREDIT_CLASS", "")) && output.getData(0).getString("CREDIT_CLASS", "") != null)
                    {
                    	String classLevel = output.getData(0).getString("CREDIT_CLASS", "");
                    	if("6".equals(classLevel)){
                            user.put("CREDIT_CLASS", "5星金");
                    	}else if("7".equals(classLevel)){
                            user.put("CREDIT_CLASS", "5星钻");
                    	}else{
                            user.put("CREDIT_CLASS", output.getData(0).getString("CREDIT_CLASS", "") + "星级");
                    	}
                    }
                    else
                    {
                        user.put("CREDIT_CLASS", "未评级");
                    }
                    user.put("CREDIT_VALUE", output.getData(0).getString("CREDIT_VALUE", "0"));

                }
            }

            setCond(data);
            setInfo(user);
            setAccountInfo(accountInfo);

            // 增加查询客户综合资料时显示左边的业务信息
            IData inParam = userInfo.getData(0);
            inParam.put("TRADE_TYPE_CODE", "2101");
            IDataset dataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryPopuInfo", data);
            setPopuInfo(dataset.getData(0));
            
            
            IData custInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryCustInfo", data);
            if(custInfo.size() > 0)
            {
            	if ("1".equals(custInfo.getString("IS_REAL_NAME", "")))
                {
            		custInfo.put("IS_REAL_NAME", "是");//实名
                }else
                {
                	custInfo.put("IS_REAL_NAME", "非实名");//非实名
                }
            }
            
            setCustomerInfo(custInfo);
            setAjax(custInfo);

        }

    }

    /**
     * 发送客户经理号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void sendCustManagerNum(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = null;
        String custmanagerid = data.getString("CUST_MANAGER_ID", "");
        String serialnum = data.getString("SERIAL_NUMBER", "");
        if ("".equals(custmanagerid) || "".equals(serialnum))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_783, "用户服务号码或客户经理ID为空！");
        }
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", custmanagerid);
        param.put("SERIAL_NUMBER", serialnum);
        IDataset dataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.sendCustManagerNum", param);
        result = dataset.getData(0);
        setInfo(result);
    }
    
    /**
     * 年度积分查询
     * @param accountInfos
     */
    public String queryScoreValue(IData param) throws Exception{
    	param.put("YEAR_ID", SysDateMgr.getSysDate().substring(0, 4));
    	IData sumscore = new DataMap();
    	int scoreSum = 0;//积分合计
    	IData accountinfo = new DataMap();
    	IDataset dataset = new DatasetList();
    	try {
    		dataset = CSViewCall.call(this, "SS.ScoreQueryTradeSVC.queryScoreExchangeYear", param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(IDataUtil.isNotEmpty(dataset)){
			for (int i = 0; i < dataset.size(); i++)
	        {
	            sumscore = dataset.getData(i);
	            String temp_score = sumscore.getString("SCORE_VALUE", "");
	            if (StringUtils.isNotBlank(temp_score))
	            {
	                try {
	                	scoreSum = scoreSum + Integer.parseInt(temp_score);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        }
		}
        
        String scoreValue = String.valueOf(scoreSum);
        return scoreValue;
    }
    
    public abstract void setAccountInfos(IDataset accountInfos);
    
    public abstract void setAccountInfo(IData accountInfo);

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setPlatInfos(IDataset platInfos);

    public abstract void setPopuInfo(IData popuInfo);

    public abstract void setCustomerInfo(IData customerInfo);
}
