
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.encrypt.CrmDes;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.svcctrl.SvcCtrl;

public class CSBizService extends BizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取默认路由
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String getRouteByDefalut(IData map) throws Exception
    {
        // 服务号码
        String sn = map.getString("SERIAL_NUMBER", "");

        if (StringUtils.isNotBlank(sn))
        {
            String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(sn);
            
            if (StringUtils.isNotBlank(routeId))
			{
            	return routeId;
			}
        }

        String routeId = map.getString(Route.ROUTE_EPARCHY_CODE, "");

        // 路由ID
        if (StringUtils.isNotBlank(routeId))
        {
            return routeId;
        }

        routeId = getVisit().getLoginEparchyCode();

        // 非数字地州，都转换成默认地州
        if (!StringUtils.isNumeric(routeId))
        {
            routeId = Route.getCrmDefaultDb();
        }

        return routeId;
    }
    
    private String getRouteJourByDefalut(IData map) throws Exception
    {
        String routeId = getRouteByDefalut(map);
        
        return "jour."+routeId;
    }

    /**
     * 根据手机号码获取路由
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String getRouteBySn(IData map) throws Exception
    {
        // 服务号码
        String sn = map.getString("SERIAL_NUMBER", "");

        if (StringUtils.isBlank(sn))
        {
            CSAppException.apperr(BizException.CRM_BIZ_655);
        }

        String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(sn);

        if (StringUtils.isBlank(routeId))
        {
            CSAppException.apperr(BizException.CRM_BIZ_654, sn);
        }

        return routeId;
    }
    
    /**
     * 根据手机号码获取jour路由
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String getRouteJourBySn(IData map) throws Exception
    {
        // 服务号码
        String sn = map.getString("SERIAL_NUMBER", "");

        if (StringUtils.isBlank(sn))
        {
            CSAppException.apperr(BizException.CRM_BIZ_655);
        }

        String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(sn);

        if (StringUtils.isBlank(routeId))
        {
            CSAppException.apperr(BizException.CRM_BIZ_654, sn);
        }

        return "jour."+routeId;
    }

    /**
     * 服务初始化方法重载
     */
    @Override
    public final void initialize(IData input) throws Exception
    {
        super.initialize(input);

        // 数据转换
        setTrans(input);

        // 设置路由
        setRoute(input);

        // 设置拦截器
        setIntercept();

        // 服务控制(异地业务权限权限)
        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            SvcCtrl.ydCtrl(input);
        }
    }

    /**
     * 设置拦截器
     * 
     * @throws Exception
     */
    public void setIntercept() throws Exception
    {
        setMethodIntercept(CSBizIntercept.class.getName());
    }

    /**
     * 设置路由
     * 
     * @param input
     * @throws Exception
     */
    protected void setRoute(IData input) throws Exception
    {
        // 路由类型
        String routeType = getAttribute("route");

        // 路由类型是否为空
        if (StringUtils.isBlank(routeType))
        {
            CSAppException.apperr(BizException.CRM_BIZ_651);
        }

        // 路由ID
        String routeId = "";

        if ("routeToCg".equals(routeType))
        {
            routeId = Route.CONN_CRM_CG;
        }
        else if ("routeToCen".equals(routeType))
        {
            routeId = Route.CONN_CRM_CEN;
        }
        else if ("routeToUpc".equals(routeType))
        {
            routeId = Route.CONN_UPC;
        }
        else if ("routeBySn".equals(routeType))
        {
            routeId = getRouteBySn(input);
        }
        else if ("routeByEparchy".equals(routeType))
        {
            routeId = input.getString(Route.ROUTE_EPARCHY_CODE, "");
        }
        else if ("routeByDefalut".equals(routeType))// default单词写错了,下面有纠正
        {
            routeId = getRouteByDefalut(input);
        }
        else if ("routeByDefault".equals(routeType))
        {
            routeId = getRouteByDefalut(input);
        }
        else if ("routeJourBySn".equals(routeType))
        {
            routeId = getRouteJourBySn(input);
        }
        else if ("routeJourByEparchy".equals(routeType))
        {
            routeId = "jour."+input.getString(Route.ROUTE_EPARCHY_CODE, "");
        }
        else if ("routeJourByDefault".equals(routeType))
        {
            routeId = getRouteJourByDefalut(input);
        }
        else if ("routeByStaff".equals(routeType))
        {
            routeId = getVisit().getLoginEparchyCode();

            // 非数字地州都转换成默认地州编码
            if (!StringUtils.isNumeric(routeId))
            {
                routeId = Route.getCrmDefaultDb();
            }
        }
        else if ("routeToEp".equals(routeType))
        {
            routeId = Route.CONN_ESOP;
        }
        else
        {
            CSAppException.apperr(BizException.CRM_BIZ_652);
        }

        // 路由ID
        if (StringUtils.isBlank(routeId))
        {
            CSAppException.apperr(BizException.CRM_BIZ_650);
        }

        // 用户归属地州
        String userEparchyCode = input.getString(Route.USER_EPARCHY_CODE, "");

        // 用户归属地州, 如果为空, 默认为路由地州
        if (StringUtils.isBlank(userEparchyCode) && routeId.length() == 4 && StringUtils.isNumeric(routeId))
        {
            userEparchyCode = routeId;
        }
        
        // 用户归属地州, 如果为空, 默认为路由地州
        if (StringUtils.isBlank(userEparchyCode) && routeId.startsWith("jour.") && routeId.length() == 9 && StringUtils.isNumeric(routeId.substring(5)))
        {
            userEparchyCode = routeId.substring(5);
        }

        // 交易地州
        String tradeEparchyCode = CSBizBean.getVisit().getLoginEparchyCode();

        // 接入渠道
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 当前工号
        String staffId = CSBizBean.getVisit().getStaffId();

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI) || ProvinceUtil.isProvince(ProvinceUtil.QHAI) )
        {
            // 接口用户地州转化
            if (InModeCodeUtil.isIntf(inModeCode, input.getString("X_SUBTRANS_CODE"), input.getString("BATCH_ID")) && (StringUtils.isBlank(userEparchyCode) || !StringUtils.isNumeric(userEparchyCode)))
            {
                userEparchyCode = Route.getCrmDefaultDb();
            }

            if (!StringUtils.equals(inModeCode, "0"))
            {
                // 接口传的是TRADE_STAFF_ID,TRADE_DEPART_ID,...
                AsynDealVisitUtil.dealVisitInfo(input);
            }

            // 非营业厅，或有全省权限，交易地州=用户地州
            if ((StringUtils.isNotBlank(staffId) && (!"0".equals(inModeCode) || (!"SUPERUSR".equalsIgnoreCase(staffId) && StaffPrivUtil.isSysProvince(staffId) == true))) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }
        }
        else
        {
            // 非营业厅，或有全省权限，交易地州=用户地州
            if ((!"0".equals(inModeCode) || StaffPrivUtil.isSysProvince(staffId) == true) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }
        }

        // HNAN 07XX 等非数字地州，都转换成用户归属地州
        if (!StringUtils.isNumeric(tradeEparchyCode) && StringUtils.isNotBlank(userEparchyCode))
        {
            tradeEparchyCode = userEparchyCode;
        }

        // 设置 服务路由ID
        setRouteId(routeId);

        // 设置 用户归属地州
        setUserEparchyCode(userEparchyCode);

        // 设置 交易地州
        setTradeEparchyCode(tradeEparchyCode);
    }

    /**
     * 设置转换
     * 
     * @param input
     * @throws Exception
     */
    public void setTrans(IData input) throws Exception
    {
    	boolean flag = BizEnv.getEnvBoolean("crm.passwd.deciphering", false);
    	if(flag)
    	{
    		String passwd = input.getString("PASSWORD");
        	String oldPasswd = input.getString("OLD_PASSWORD");
        	String first = "";
  		  	String end = "";
  		  	String key = StringUtils.isBlank(input.getString("SERIAL_NUMBER", ""))?"123456":input.getString("SERIAL_NUMBER", "");
        	
        	if(StringUtils.isNotBlank(passwd) && !StringUtils.isNumeric(passwd))
        	{
        		first = passwd.substring(0,1);
      		  	end = passwd.substring(passwd.length()-1);
      		  	
	      		if("#".equals(first) && "!".equals(end))
	    		{
	      			if(passwd.length() >= 3)
	      			{
	      				input.put("PASSWORD", decryPasswd(passwd, key));
	      			}
	    		}
        	}
        	
        	if(StringUtils.isNotBlank(oldPasswd) && !StringUtils.isNumeric(oldPasswd))
        	{
        		first = oldPasswd.substring(0,1);
      		  	end = oldPasswd.substring(oldPasswd.length()-1);
      		  	
	      		if("#".equals(first) && "!".equals(end))
	    		{
	      			if(oldPasswd.length() >= 3)
	      			{
	      				input.put("OLD_PASSWORD", decryPasswd(oldPasswd, key));
	      			}
	    		}
        	}
    	}
    }
    
    public String decryPasswd(String passWd, String key)throws Exception{
    	passWd = passWd.substring(1, passWd.length()-1);
    	return CrmDes.strDec(passWd, key, "", "");
    }
}
