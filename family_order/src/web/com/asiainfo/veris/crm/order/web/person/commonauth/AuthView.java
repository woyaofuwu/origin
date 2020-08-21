
package com.asiainfo.veris.crm.order.web.person.commonauth;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AuthView extends PersonBasePage
{
	public static final int AUTH_CACHE_TIMEOUT = 300;
    /**
     * 根据选择的认证方式 进行用户、客户认证 并查询出客户下的所有用户
     * 
     * @param cycle
     * @throws Exception
     */
    public void getUsers(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String alertInfo = null;
        int checkMode = data.getInt("CHECK_MODE");
        String serialNumber = data.getString("SERIAL_NUMBER");
       
        if (checkMode == 0)
        {// 客户证件+证件类型
            // 客户证件认证 直接捞出该证件对应的客户
            IData param = new DataMap();
            param.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE"));
            param.put("PSPT_ID", data.getString("PSPT_ID"));
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            IDataset dataset = CSViewCall.call(this, "SS.AuthViewSVC.getCustInfoByPspt", param);
            if (IDataUtil.isEmpty(dataset))
            {
                alertInfo = "查无客户信息!";
                this.setAjax("ALERT_INFO", alertInfo);
                return;
            }

            IDataset isBlackData = CSViewCall.call(this, "SS.AuthViewSVC.IsBlackCust", param);
            if (isBlackData.getData(0).getBoolean("FLAG"))
            {
                alertInfo = "客户为黑户!";
                this.setAjax("ALERT_INFO", alertInfo);
                return;
            }

            // 根据第一得到的客户 获取客户信息
            IData custInfo = dataset.getData(0);
            param.clear();
            param.put("CUST_ID", custInfo.get("CUST_ID"));
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            IDataset custPersonInfos = CSViewCall.call(this, "SS.AuthViewSVC.getCustInfoByPK", param);

            if (IDataUtil.isNotEmpty(custPersonInfos))
            {
                custInfo.putAll(custPersonInfos.getData(0));
            }
            // 设置客户信息
            setCustInfo(custInfo);

            // 拼接CUST_ID
            StringBuilder custs = new StringBuilder();
            for (int i = 0; i < dataset.size(); i++)
            {
                custs.append(dataset.getData(i).getString("CUST_ID")).append(",");
            }
            param.clear();
            String custstr = custs.substring(0, custs.length() - 1);
            param.put("CUST_ID", custstr);// 去掉最后的逗号
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            // 根据CUST_ID 集合查询用户
            IDataset users = CSViewCall.call(this, "SS.AuthViewSVC.qryUserInfoByCusts", param);

            if ((IDataUtil.isEmpty(users)))
            {
                // 没有查询到用户
                alertInfo = "查无用户信息!";
                this.setAjax("ALERT_INFO", alertInfo);
                return;
            }
            else
            {
            	// 将查询到第一个生效的USER_ID传递到前台
            	//安全校验码生成保存
                Des desObj = new Des();
                String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
                	            	
                boolean bo = false;
                int size = users.size();
                
                for (int i = 0; i < size; i++)
                {
                    IData temp = users.getData(i);
                    
                    //安全检验码
                    String cust_id_user_id_sn  = temp.getString("CUST_ID", "")
                    		+temp.getString("USER_ID", "")
                    		+temp.getString("SERIAL_NUMBER", "");
                    
                    String cust_id_user_id_sn_enc =desObj.setEncPwd(cust_id_user_id_sn)+"xxyy";
                    temp.put("CUST_ID_USER_ID_SN_ENC", cust_id_user_id_sn_enc);// 勾选
                      
                    
                    if ("0".equals(temp.getString("REMOVE_TAG")))
                    {
                        if(!bo){
                          temp.put("CHECKED", true);// 勾选
                          bo =true;
                        }
                        
                        String cust_id_enc =desObj.setEncPwd(temp.getString("CUST_ID", ""))+"xxyy";
                        String user_id_enc =desObj.setEncPwd(temp.getString("USER_ID", ""))+"xxyy";
                        String serianum_enc =desObj.setEncPwd(temp.getString("SERIAL_NUMBER", ""))+"xxyy";
                        
                        SharedCache.set("CUSTIDCACHE"+sessionId, cust_id_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                        SharedCache.set("USERIDCACHE"+sessionId, user_id_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                        SharedCache.set("SERIALNUMBERCACHE"+sessionId, serianum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                        SharedCache.set("CHECKPASSTAG"+sessionId, "0", AUTH_CACHE_TIMEOUT); //放到缓存
                        if(log.isDebugEnabled()){
    				    	log.debug("号码："+temp.getString("SERIAL_NUMBER", "")+" 认证成功");
    				    }
                        //break;
                    }
                    
                    
                                        
                }
				if (!bo) {

					String cust_id_enc = desObj.setEncPwd(users.getData(0).getString("CUST_ID", "")) + "xxyy";
					String user_id_enc = desObj.setEncPwd(users.getData(0).getString("USER_ID", "")) + "xxyy";
					String serianum_enc = desObj.setEncPwd(users.getData(0).getString("SERIAL_NUMBER", "")) + "xxyy";
					users.getData(0).put("CHECKED",true);

					SharedCache.set("CUSTIDCACHE" + sessionId, cust_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
					SharedCache.set("USERIDCACHE" + sessionId, user_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
					SharedCache.set("SERIALNUMBERCACHE" + sessionId, serianum_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
					SharedCache.set("CHECKPASSTAG" + sessionId, "0", AUTH_CACHE_TIMEOUT); // 放到缓存
				    if(log.isDebugEnabled()){
				    	log.debug("号码："+users.getData(0).getString("SERIAL_NUMBER", "")+" 认证成功");
				    }

					alertInfo = "查无正常状态的用户信息!";
					this.setAjax("ALERT_INFO", alertInfo);
				}
            }
            setInfos(users);
        }
        else if (checkMode == 2)
        {// 固话号码+服务密码
            if (StringUtils.isBlank(serialNumber))
            {
                alertInfo = "请输入固话号码!";
                this.setAjax("ALERT_INFO", alertInfo);
                return;
            }
            this.getUsersBySerialNumberAndPswd(cycle);
        }
        else if (checkMode == 3)
        {// 宽带帐号+服务密码
            if (StringUtils.isBlank(serialNumber))
            {
                alertInfo = "请输入宽带帐号!";
                this.setAjax("ALERT_INFO", alertInfo);
                return;
            }
            this.getUsersBySerialNumberAndPswd(cycle);
        }
        else
        {
            // 用户认证 调用用户认证
        }
    }

    /**
     * 当checkMode == 2,3时, 通过 号码+服务密码 获取用户信息.
     * 
     * @return
     * @throws Exception
     */
    public void getUsersBySerialNumberAndPswd(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String alertInfo = null;

        // 查询用户信息
        IData param = new DataMap();
        param.put("REMOVE_TAG", "0");
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset userInfos = CSViewCall.call(this, "SS.AuthViewSVC.getUserInfoBySn", param);

        if (IDataUtil.isEmpty(userInfos))
        {
            alertInfo = "查无用户信息!";
            this.setAjax("ALERT_INFO", alertInfo);
            return;
        }

        IData userInfo = userInfos.getData(0);
        // int checkMode = data.getInt("CHECK_MODE");
        // if(checkMode == 3 && !"11".equals(userInfo.getString("NET_TYPE_CODE"))) {//宽带帐号+服务密码
        // alertInfo = "该号码不是宽带账号，请重新填写!";
        // this.setAjax("ALERT_INFO", alertInfo);
        // return;
        // }
        // if(checkMode == 2 && !"12".equals(userInfo.getString("NET_TYPE_CODE"))) {//固话号码+服务密码
        // alertInfo = "该号码不是固话号码，请重新填写!";
        // this.setAjax("ALERT_INFO", alertInfo);
        // return;
        // }

        // 获取客户信息和用户列表 begin
        IData custInfo = new DataMap();
        param.clear();
        param.put("CUST_ID", userInfo.getString("CUST_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset custPersonInfos = CSViewCall.call(this, "SS.AuthViewSVC.getCustInfoByPK", param);

        if (IDataUtil.isNotEmpty(custPersonInfos))
        {
            custInfo.putAll(custPersonInfos.getData(0));
        }
        setCustInfo(custInfo);

        // //拼接CUST_ID, 用服务号码查询时,只有一个CUST_ID.
        // String custstr = userInfo.getString("CUST_ID");
        // returnParam.put("CUST_ID_LIST", custstr);

        IDataset users = new DatasetList();// 只要组合产品的信息.
        users.add(userInfo);
        if (users == null || users.size() == 0)
        {
            // 没有查询到用户
            alertInfo = "查无用户信息!";
            this.setAjax("ALERT_INFO", alertInfo);
        }
        else
        {
        	// 将查询到第一个生效的USER_ID传递到前台
        	//安全校验码生成保存
            Des desObj = new Des();
            String sessionId =cycle.getRequestContext().getRequest().getSession().getId();
            boolean bo = false;
            for (int i = 0; i < users.size(); i++)
            {
            	IData temp = users.getData(i);
                String cust_id_user_id_sn  = temp.getString("CUST_ID", "")
                		+temp.getString("USER_ID", "")
                		+temp.getString("SERIAL_NUMBER", "");
                String cust_id_user_id_sn_enc =desObj.setEncPwd(cust_id_user_id_sn)+"xxyy";
                users.getData(i).put("CUST_ID_USER_ID_SN_ENC", cust_id_user_id_sn_enc);// 
                	
            	
                if ("0".equals(users.getData(i).getString("REMOVE_TAG")))
                {
                    bo = true;
                    // returnParam.put("USER_ID", users.getData(i).getString("USER_ID"));
                    // returnParam.put("SERIAL_NUMBER", users.getData(i).getString("SERIAL_NUMBER"));
                    // returnParam.put("MEB_USER_ID", users.getData(i).getString("MEB_USER_ID"));
                    // returnParam.put("ROLE", users.getData(i).getString("ROLE"));
                    users.getData(i).put("CHECKED", true);// 勾选
                    
                    //
                    String cust_id_enc =desObj.setEncPwd(temp.getString("CUST_ID", ""))+"xxyy";
                    String user_id_enc =desObj.setEncPwd(temp.getString("USER_ID", ""))+"xxyy";
                    String serianum_enc =desObj.setEncPwd(temp.getString("SERIAL_NUMBER", ""))+"xxyy";
                    
                    SharedCache.set("CUSTIDCACHE"+sessionId, cust_id_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                    SharedCache.set("USERIDCACHE"+sessionId, user_id_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                    SharedCache.set("SERIALNUMBERCACHE"+sessionId, serianum_enc, AUTH_CACHE_TIMEOUT); //放到缓存
                    SharedCache.set("CHECKPASSTAG"+sessionId, "0", AUTH_CACHE_TIMEOUT); //放到缓存
                    //break;
                   
                }
                               
                
            }
            if (!bo)
            {
				String cust_id_enc = desObj.setEncPwd(users.getData(0).getString("CUST_ID", "")) + "xxyy";
				String user_id_enc = desObj.setEncPwd(users.getData(0).getString("USER_ID", "")) + "xxyy";
				String serianum_enc = desObj.setEncPwd(users.getData(0).getString("SERIAL_NUMBER", "")) + "xxyy";

				SharedCache.set("CUSTIDCACHE" + sessionId, cust_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
				SharedCache.set("USERIDCACHE" + sessionId, user_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
				SharedCache.set("SERIALNUMBERCACHE" + sessionId, serianum_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
				SharedCache.set("CHECKPASSTAG" + sessionId, "0", AUTH_CACHE_TIMEOUT); // 放到缓存

                alertInfo = "查无正常状态的用户信息!";
                this.setAjax("ALERT_INFO", alertInfo);
            }
            
        }
        setInfos(users);
    }
    
	public void changeAuthUser(IRequestCycle cycle) throws Exception {
		IData data = getData();
		String alertInfo = null;
		String serialNumber = data.getString("SERIAL_NUMBER", "");
		String custid = data.getString("CUST_ID", "");
		String userid = data.getString("USER_ID", "");
		String enc = data.getString("CUST_ID_USER_ID_SN_ENC", "");
		if ("".equals(serialNumber) || "".equals(custid) || "".equals(userid) || "".equals(enc)) {

			log.info("01 非法传值，请重新认证或联系管理员！");
			
			this.setAjax("ALERT_INFO", "01 非法传值，请重新认证或联系管理员！");
			return;
		}

		Des desObj = new Des();
		String sessionId = cycle.getRequestContext().getRequest().getSession().getId();

		// 安全检验码
		String cust_id_user_id_sn = custid + userid + serialNumber;

		String cust_id_user_id_sn_enc = desObj.setEncPwd(cust_id_user_id_sn) + "xxyy";
		if (! cust_id_user_id_sn_enc.equals(enc) ) {
			log.info("02 非法传值，请重新认证或联系管理员!"+cust_id_user_id_sn);
			this.setAjax("ALERT_INFO", "02 非法传值，请重新认证或联系管理员！");
			return;
		}

		String cust_id_enc = desObj.setEncPwd(custid) + "xxyy";
		String user_id_enc = desObj.setEncPwd(userid) + "xxyy";
		String serianum_enc = desObj.setEncPwd(serialNumber) + "xxyy";

		SharedCache.set("CUSTIDCACHE" + sessionId, cust_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
		SharedCache.set("USERIDCACHE" + sessionId, user_id_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
		SharedCache.set("SERIALNUMBERCACHE" + sessionId, serianum_enc, AUTH_CACHE_TIMEOUT); // 放到缓存
		SharedCache.set("CHECKPASSTAG"+sessionId, "0", AUTH_CACHE_TIMEOUT); //放到缓存
		
		this.setAjax("ALERT_INFO", "00号码："+serialNumber+"成功认证！");
		if(log.isDebugEnabled()){
			log.debug("00号码："+serialNumber+"成功认证！");
		}


	}
    public void cancelAuthUser(IRequestCycle cycle) throws Exception
    {
		Des desObj = new Des();
		String sessionId = cycle.getRequestContext().getRequest().getSession().getId();
		IData data = getData();
		String serialNumber = data.getString("SERIAL_NUMBER", "");
		
		String serianum_enc = desObj.setEncPwd(serialNumber) + "xxyy";
		String AUTH_SPEC_SN=String.valueOf(SharedCache.get("SERIALNUMBERCACHE"+sessionId));
		

		
		if (serianum_enc.equals(AUTH_SPEC_SN)) {
			
			if (SharedCache.keyExist("CUSTIDCACHE" + sessionId)) {
				SharedCache.delete("CUSTIDCACHE" + sessionId);
			}
			if (SharedCache.keyExist("USERIDCACHE" + sessionId)) {
				SharedCache.delete("USERIDCACHE" + sessionId);
			}
			if (SharedCache.keyExist("SERIALNUMBERCACHE" + sessionId)) {
				SharedCache.delete("SERIALNUMBERCACHE" + sessionId);
			}
			if (SharedCache.keyExist("CHECKPASSTAG" + sessionId)) {
				SharedCache.delete("CHECKPASSTAG" + sessionId);
			}
			
			this.setAjax("ALERT_INFO", "00 取消认证成功！");
		}else{
			if(log.isDebugEnabled()){
				log.debug("01认证号码已变更！"+serialNumber);
			}
			this.setAjax("ALERT_INFO", "01认证号码已变更!");
		}

    }

    public abstract void setCond(IData cond);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);
}
