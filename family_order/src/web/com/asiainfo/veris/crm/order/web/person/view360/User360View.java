
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class User360View extends PersonBasePage
{

    public IData errMsg = new DataMap();

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = getData("cond", true);
        data.putAll(cond);
        setCondition(data);
    }

    /**
     * 分析结果
     */
    private void parserResults(IData param, IDataset results, IData initParam) throws Exception
    {
        initParam.remove("MANY_RECORDS");
        if (IDataUtil.isEmpty(results))
        {
            initParam.put("MANY_RECOREDS", "0");
            setAjax("ALERT_INFO", "用户号码[" + param.getString("SERIAL_NUMBER", "") + "]没有找到用户资料！");
            return;
        }
        else if (results.size() == 1)
        {
            IData userInfo = (IData) results.get(0);
            initParam.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE", "00"));
            IData custParam = new DataMap();
            custParam.put("CUST_ID", userInfo.getString("CUST_ID", ""));
            custParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));

            IDataset custPersonInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryPersonInfoByCustId", custParam);
            /*去掉对客户资料为空抛出错误的判断，为铁通用户 add by huanghui 20140923
            if (IDataUtil.isEmpty(custPersonInfo))
            {
                initParam.put("MANY_RECOREDS", "0");
                setAjax("ALERT_INFO", "客户资料信息缺失！");
                return;
            }*/
            if (IDataUtil.isNotEmpty(custPersonInfo))
            {
                String cust_name = (String) custPersonInfo.getData(0).get("CUST_NAME");
                userInfo.put("CUST_NAME", cust_name);
            }
            // 查询集团客户信息
            String userId = (String) userInfo.get("USER_ID");
            IData groupParam = new DataMap();
            groupParam.put("USER_ID", userId);
            groupParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
            IDataset groupInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryGroupName", groupParam);
            IData groupInfo = null;
            if (IDataUtil.isNotEmpty(groupInfos))
            {
                groupInfo = (IData) groupInfos.get(0);
                String groupName = (String) groupInfo.get("GROUP_CUST_NAME");
                userInfo.put("GROUP_CUST_NAME", groupName);
            }
            setUserinfo(userInfo);
            initParam.put("MANY_RECORDS", "0"); //查询成功
            //用户360查询台帐记录
            IData userParam = new DataMap();
            userParam.put("USER_ID", userInfo.getString("USER_ID", ""));
            userParam.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE", ""));
            userParam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
            userParam.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE", ""));
            userParam.put("CITY_CODE", userInfo.getString("CITY_CODE", ""));
            userParam.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID", ""));
            userParam.put("BRAND_CODE", userInfo.getString("BRAND_CODE", ""));
            userParam.put("CUST_ID", userInfo.getString("CUST_ID", ""));
            CSViewCall.call(this, "SS.GetUser360ViewSVC.writeTradeQueryLog", userParam);
            return;
        }
        else
        {
            initParam.put("MANY_RECORDS", "1"); // 页面弹出选择框
            return;
        }
    }
    /**
     *  小篮筐展示
     * @param cycle
     * @throws Exception
     */
    public void getHintInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
        IDataset hintInfo = CSViewCall.call(this, "SS.HintInfoSVC.getHintInfo", data);
        if(IDataUtil.isNotEmpty(hintInfo)){
            setAjax(hintInfo.getData(0));
        }
    }
    
    /**
     * 开始进入客户资料综合查询视图
     * 
     * @param  cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
    	
    	boolean flag2 = BizEnv.getEnvBoolean("crm_realtimemarketing_webswitch");
        
        IData param = getData();
        IData initParam = new DataMap();
        initParam.put("SERIAL_NUMBER", param.get("SERIAL_NUMBER"));
        initParam.put("SIM_CHECK", param.getString("SIM_CHECK", ""));
        initParam.put("SIM_NUMBER", param.getString("SIM_NUMBER", ""));
        initParam.put("NORMAL_USER_CHECK", param.getString("NORMAL_USER_CHECK", ""));
        initParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        initParam.put("CRM_REALTIMEMARKETING_WEBSWITCH", flag2?"1":"0");
        String eparchCode = getVisit().getStaffEparchyCode();
        String routeEparchyCode = getVisit().getStaffEparchyCode();
        String normalUserCheck = param.getString("NORMAL_USER_CHECK", "");
        if (StringUtils.isNotBlank(normalUserCheck) && "on".equals(normalUserCheck))
        {     
            param.put("REMOVE_TAG", "0"); //on是点击复选框后的默认值
        }
        else
        {
            param.remove("REMOVE_TAG");
        }

        String flag = param.getString("FLAG", ""); //FLAG标志来自通过手机号码查询出多条记录弹出页面
        if (StringUtils.isBlank(flag))
        {
            param.remove("CUST_ID");
        }
        
        // 手机号码查询用户
        if (StringUtils.isNotBlank(param.getString("SERIAL_NUMBER")))
        {
            String serialNumber = param.getString("SERIAL_NUMBER", "");
            if (StringUtils.isBlank(serialNumber))
            {
                setCondition(initParam);
                setAjax("ALERT_INFO", "请输入服务号码！");
                return;
            }
            /*
             * IDataset misdnMofficeInfo = new DatasetList();
            // 如果用户资料不为空取号段表中配置信息,排除宽带号码和铁通号码
            if (!"KD_".equals(param.getString("SERIAL_NUMBER").substring(0, 3)))
            {
                misdnMofficeInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryMofficeInfoBySn", param);
            }
            // 如果号段表中未配置则抛出异常信息
            if (IDataUtil.isEmpty(misdnMofficeInfo) && !"KD_".equals(serialNumber.substring(0, 3)))
            {
                setCondition(initParam);
                setAjax("ALERT_INFO", "号段表中未配置相关信息！");
                return;
            }

            if (!"KD_".equals(serialNumber.substring(0, 3)))
            {
                routeEparchyCode = misdnMofficeInfo.getData(0).getString("EPARCHY_CODE", "");
            }*/
            IDataset userInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfoBySerialNumber", param);

            if (StringUtils.isBlank(routeEparchyCode))
            {
                routeEparchyCode = eparchCode;
            }

            IDataset results = userInfo;
            initParam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
            parserResults(param, results, initParam);

            // 判断是否是铁通号码，综合查询页面tab清单信息子页面嵌入不同的账管页面 Add By HuangHui 2014-09-06
            if (initParam.getString("NET_TYPE_CODE","").equals("11") || initParam.getString("NET_TYPE_CODE","").equals("12")
            		||initParam.getString("NET_TYPE_CODE","").equals("13") || initParam.getString("NET_TYPE_CODE","").equals("14"))
            {
                initParam.put("BILL_LOG_INFO_PAGE", "cdr.TTBillQry");
            }

            if (!param.getString("CALLED_SN", "").equals(""))
            {
                param.put("SERIAL_NUMBER_B", param.getString("CALLED_SN", ""));
            }
            if (getVisit().getInModeCode().equals("1") && !"".equals(param.getString("SERIAL_NUMBER_B", "")) && !"".equals(param.getString("SERIAL_NUMBER", "")))
            {

                // 查询接入号码是否是客户经理，接入号码是否是服务号码的客户经理，接入号码是否是一卡双号
                IDataset serialNumberBInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBInfo", param);
                initParam.put("IS_VIP_MANAGER", serialNumberBInfo.getData(0).getString("IS_VIP_MANAGER"));
                initParam.put("VIP_MANAGER_PASS", serialNumberBInfo.getData(0).getString("VIP_MANAGER_PASS"));
                initParam.put("IS_BOTH_SN", serialNumberBInfo.getData(0).getString("IS_BOTH_SN"));
                initParam.put("SERIAL_NUMBER_B", param.getString("SERIAL_NUMBER_B", ""));
            }
            setCondition(initParam);

            // 对前台所有操作记录台帐的需求
            IPage page = cycle.getPage();
            if (page != null)
            {
                // 员工界面输入号码后查询动作时记录log
                IData data = new DataMap();
                data.put("OBJECT_ID", page.getPageName());
                data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
                data.put("CLIENT_IP", getVisit().getRemoteAddr());
                data.put("IN_MODE_CODE", "0");// 操作方式 0页面
                data.put("TRADE_STAFF_ID", getVisit().getStaffId());
                data.put("TRADE_DEPART_ID", getVisit().getDepartId());
                data.put("TRADE_CITY_CODE", getVisit().getCityCode());
                data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());

            }
            return;
 
        }
        else
        {
            String simNumber = param.getString("SIM_NUMBER", "");
            if (StringUtils.isBlank(simNumber))
            {
            }
            else
            {
                IDataset qrySerialNumberBySim = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBySim", param);
                IDataset serial_numbers =new DatasetList();
                
                /**
			     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
			     * @author zhuoyingzhi
			     * @date 20170228
                 */
                IData  checkResult=checkWhiteSIM(qrySerialNumberBySim, simNumber);
                String resultCode=checkResult.getString("resultCode");
                if("0".equals(resultCode)){
                	//第一次查询用户信息不存在，再调用资源接口，查询到sim卡
                	 String resultWhiteSimNumber=checkResult.getString("resultWhiteSimNumber", "");
                	 
                	 IData paramNew=new DataMap();
                	 //新的sim卡
                	 paramNew.put("SIM_NUMBER", resultWhiteSimNumber);
                	 //再通过新的sim卡查询用户信息
                	 serial_numbers=CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBySim", paramNew);
                	 
                }else if("2".equals(resultCode)){
                	//第一次查询用户信息不存在，再调用资源接口，查询不到新sim卡
                	
                	serial_numbers =new DatasetList();
                }else{
                	//第一次查询就查询到用户信息了
                	serial_numbers = qrySerialNumberBySim;
                }
                /**********************end***************************************/
                if (serial_numbers.size() <= 0)
                {
                    // 对前台所有操作记录台帐的需求
                    IPage page = cycle.getPage();
                    if (page != null)
                    {
                        // 员工界面输入号码后查询动作时记录log
                        IData data = new DataMap();
                        data.put("OBJECT_ID", page.getPageName());
                        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
                        data.put("CLIENT_IP", getVisit().getRemoteAddr());
                        data.put("IN_MODE_CODE", "0");// 操作方式 0页面
                        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
                        data.put("TRADE_DEPART_ID", getVisit().getDepartId());
                        data.put("TRADE_CITY_CODE", getVisit().getCityCode());
                        data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
                    }

                    setCondition(initParam);
                    setAjax("ALERT_INFO", "通过SIM卡号码没有找到服务号码！");
                    return;
                }
                IData serial_number_ = (IData) serial_numbers.get(0);
                String serial_number = serial_number_.getString("SERIAL_NUMBER", "");
                if (serial_number == null || "".equals(serial_number))
                {

                    // 对前台所有操作记录台帐的需求
                    IPage page = cycle.getPage();
                    if (page != null)
                    {
                        // 员工界面输入号码后查询动作时记录log
                        IData data = new DataMap();
                        data.put("OBJECT_ID", page.getPageName());
                        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
                        data.put("CLIENT_IP", getVisit().getRemoteAddr());
                        data.put("IN_MODE_CODE", "0");// 操作方式 0页面
                        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
                        data.put("TRADE_DEPART_ID", getVisit().getDepartId());
                        data.put("TRADE_CITY_CODE", getVisit().getCityCode());
                        data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
                    }

                    setCondition(initParam);
                    setAjax("ALERT_INFO", "通过SIM卡号码没有找到服务号码！");
                    return;
                }
                // step3:根据SERIAL_NUMBER查询用户资料
                param.remove("SERIAL_NUMBER");
                param.put("SERIAL_NUMBER", serial_number);
                routeEparchyCode = serial_number_.getString("EPARCHY_CODE");// (new

                if (StringUtils.isBlank(routeEparchyCode))
                {
                    routeEparchyCode = eparchCode;
                }
                else
                {
                    //if (!routeEparchyCode.equals(eparchCode) && !"07XX".equals(eparchCode) && !"HNAN".equals(eparchCode))
                    if (!routeEparchyCode.equals(eparchCode))
                    {
                        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_USER_QRY"))
                        {
                            // 对前台所有操作记录台帐的需求
                            IPage page = cycle.getPage();
                            if (page != null)
                            {
                                // 员工界面输入号码后查询动作时记录log
                                IData data = new DataMap();
                                data.put("OBJECT_ID", page.getPageName());
                                data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
                                data.put("CLIENT_IP", getVisit().getRemoteAddr());
                                data.put("IN_MODE_CODE", "0");// 操作方式 0页面
                                data.put("TRADE_STAFF_ID", getVisit().getStaffId());
                                data.put("TRADE_DEPART_ID", getVisit().getDepartId());
                                data.put("TRADE_CITY_CODE", getVisit().getCityCode());
                                data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
                            }
                            setCondition(initParam);
                            setAjax("ALERT_INFO", "您没有权限查询异地用户！");
                            return;
                        }
                    }
                }
                initParam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
                IDataset qryUserInfoBySerialNumber = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfoBySerialNumber", param);
                IDataset results = qryUserInfoBySerialNumber;
                // pd.getPagination());
                parserResults(param, results, initParam);
                initParam.put("SERIAL_NUMBER", serial_number);
                setCondition(initParam);
                // 对前台所有操作记录台帐的需求
                IPage page = cycle.getPage();
                if (page != null)
                {
                    // 员工界面输入号码后查询动作时记录log
                    IData data = new DataMap();
                    data.put("OBJECT_ID", page.getPageName());
                    data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
                    data.put("CLIENT_IP", getVisit().getRemoteAddr());
                    data.put("IN_MODE_CODE", "0");// 操作方式 0页面
                    data.put("TRADE_STAFF_ID", getVisit().getStaffId());
                    data.put("TRADE_DEPART_ID", getVisit().getDepartId());
                    data.put("TRADE_CITY_CODE", getVisit().getCityCode());
                    data.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
                }
            }
        }
        
        
    }
    /**
     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
     * @author zhuoyingzhi
     * @date 20170228
     * @param qrySerialNumberBySim
     * @param simNumber
     * @return
     * @throws Exception
     */
    public IData  checkWhiteSIM(IDataset qrySerialNumberBySim,String simNumber) throws Exception{
    	try {
    		IData result=new DataMap();
    		//-1默认值       0通过白卡sim获取sim卡号标识     1 第一次查询就获取用户信息    2第一次查询获取不到用户信息，第二次调用接口还是获取不到用户信息
    		result.put("resultCode", "-1");
    		result.put("resultWhiteSimNumber", "");//通过白卡sim获取sim卡号
    		
    		IData param=new DataMap();
    		//白卡信息
    		param.put("SIMNUMBER",simNumber);
			if(IDataUtil.isEmpty(qrySerialNumberBySim)){
				 //无用户信息，调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
				 qryEmptycardInfo(param, result);
			}else{
		        IData serial_number_ = (IData) qrySerialNumberBySim.get(0);
                String serial_number = serial_number_.getString("SERIAL_NUMBER", "");
                if("".equals(serial_number)||serial_number==null){
                	//无用户信息，调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
                	qryEmptycardInfo(param, result);
                }else{
                	//存在用户信息(第一次查询的时候用户信息已经存在)
                	result.put("resultCode", "1");
                }
			}
			
    		return result;
		} catch (Exception e) {
		     throw e;
		}
    }
    /**
     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
     * @author zhuoyingzhi
     * @date 20170301
     * @param param
     * @param result
     * @throws Exception
     */
    public void  qryEmptycardInfo(IData param,IData result) throws Exception{
    	try {
    		String simCardNo="";
    		//调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
            IDataset simInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySIMByWhiteSIM", param);
            if(IDataUtil.isNotEmpty(simInfo)){
            	 simCardNo=simInfo.getData(0).getString("SIM_CARD_NO", "");
                 if(!"".equals(simCardNo)&&simCardNo!=null){
                     result.put("resultCode", "0");
             		 result.put("resultWhiteSimNumber", simCardNo);//通过白卡sim获取sim卡号
                 }else{
                	 //SIM卡信息不存在
                	 result.put("resultCode", "2");                        	 
                 }
            }else{
            	//第一次查询获取不到用户信息，第二次调用接口还是获取不到用户信息
            	result.put("resultCode", "2");
            }
		} catch (Exception e) {
			throw e;
		}
    }
    public abstract void setCondition(IData condition);

    public abstract void setUserinfo(IData custinfo);

}
