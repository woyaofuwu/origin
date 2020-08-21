
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterfaceInfo extends PersonBasePage
{
	protected static Logger logger = Logger.getLogger(InterfaceInfo.class);
    public void getInterfaceBySubsys(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        IData param = new DataMap();
        param.put("SUBSYS_CODE", data.getString("cond_SUBSYS_CODE"));
        IDataset interfaces = CSViewCall.call(this, "CS.InterfaceInfoSVC.queryAllInterfaces", param);
        setInterfaces(interfaces);
    }

    public void getInterfaceInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        IData param1 = new DataMap();
        param1.put("SUBSYS_CODE", param.getString("cond_SUBSYS_CODE"));
        IDataset interfaces = CSViewCall.call(this, "CS.InterfaceInfoSVC.queryAllInterfaces", param1);
        setInterfaces(interfaces);

        IDataset interInfos = CSViewCall.call(this, "CS.InterfaceInfoSVC.queryInterfaceById", param);
        if (interInfos.size() > 0)
        {
            IData interface0 = interInfos.getData(0);
            param.put("cond_INTERFACE_ADDR", interface0.getString("ADDR", ""));
            param.put("cond_LOGIN_INFO", interface0.getString("LOGININ_INFOS", ""));
            param.put("cond_INTERFACE_CODE", interface0.getString("CODE", ""));
            param.put("cond_UPDATE_STAFF_ID", interface0.getString("UPDATE_STAFF_ID", ""));
        }
        setCondition(param);
        IDataset interscenes = CSViewCall.call(this, "CS.InterfaceInfoSVC.getSceneById", param);
        setInterfacescenes(interscenes);

    }

    public void getSceneInfoById(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        // set the conditions that page already had
        getInterfaceInfos(cycle);
        // get the scene infos
        IDataset interscenes = CSViewCall.call(this, "CS.InterfaceInfoSVC.getSceneInfoById", param);
        String sSceneInfo = interscenes.size() > 0 ? interscenes.getData(0).getString("SCENE_VL", "") : "";
        setSceneInfo(sSceneInfo);

    }

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        IData param = new DataMap();
        param.put("SUBSYS_CODE", data.getString("cond_SUBSYS_CODE"));
        IDataset interfaces = CSViewCall.call(this, "CS.InterfaceInfoSVC.queryAllInterfaces", param);
        setInterfaces(interfaces);
    }

    public void invokeIBossInterface(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData data = prepareData(cycle);
        boolean flag = checkStaffId(data);
        if(flag){
        	String tipInfo = "测试工号和登录工号不一致，不允许办理。";
        	setTipInfo(tipInfo);
        	return;
        }
        data.put("IBOSS", "Y");

        IData dbInfo = new DataMap();
        String tipInfo = "";
        IDataset dataset = null;
        try
        {
            dataset = CSViewCall.call(this, "CS.InterfaceInfoSVC.invokeInterface", data);
            tipInfo = dataset.toString();
            // headInfo = dataOutput.getHead().toString();

            // 服务调用成功后,记录参数场景到tl_interface_log表,add by longtian3 2013-07-25
            if (IDataUtil.isNotEmpty(dataset))
            {
                IData log = new DataMap();
                log.put("INTER_ID", param.getString("cond_INTERFACE_NAME", ""));
                log.put("SCENE_VL", param.getString("cond_PARAM_INFO", ""));
                log.put("INTER_CODE", param.getString("cond_INTERFACE_CODE", ""));
                CSViewCall.call(this, "CS.InterfaceInfoSVC.logInterface", log);
            }
        }
        catch (Exception e)
        {
            tipInfo = Utility.getStackTrace(Utility.getBottomException(e)).toString();
        }
        finally
        {
            dbInfo.put("SCENE_ID", param.getString("cond_INTERFACE_SCENE").trim());
            dbInfo.put("INTER_ID", param.getString("cond_INTERFACE_NAME").trim());
            dbInfo.put("RESULTS", tipInfo);
            CSViewCall.call(this, "CS.InterfaceInfoSVC.upInterfaceResultById", dbInfo);
            setTipInfo(tipInfo);
        }
    }

    public void invokeInterface(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData data = prepareData(cycle);
        boolean flag = checkStaffId(data);
        if(flag){
        	String tipInfo = "测试工号和登录工号不一致，不允许办理。";
        	setTipInfo(tipInfo);
        	return;
        }
        
        String isChecked_ld = param.getString("cond_IBOSS_LD", "");
        if ("on".equals(isChecked_ld))
        {
            String url = data.getString("INTERFACE_ADDR");
            String svcName = data.getString("INTERFACE_CODE");
            String inparams = data.getString("INTERFACE_DATA");

            IData dbInfo = new DataMap();
            String tipInfo = "";
            try
            {
                String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
                List list = Wade3DataTran.strToList(out);
                IDataset dataset = Wade3DataTran.wade3To4Dataset(list);

                tipInfo = dataset.toString();
            }
            catch (Exception e)
            {
                tipInfo = Utility.getStackTrace(Utility.getBottomException(e)).toString();
            }
            finally
            {
                dbInfo.put("SCENE_ID", param.getString("cond_INTERFACE_SCENE").trim());
                dbInfo.put("INTER_ID", param.getString("cond_INTERFACE_NAME").trim());
                dbInfo.put("RESULTS", tipInfo);
                CSViewCall.call(this, "CS.InterfaceInfoSVC.upInterfaceResultById", dbInfo);

                setTipInfo(tipInfo);
            }

        }
        else
        {
            String isChecked = param.getString("cond_ISCHECKED", "");
            Pagination page = null;
            if ("on".equals(isChecked))
            {// 进行分页处理
                String strCount = param.getString("cond_PAGE_COUNT");
                String strSize = param.getString("cond_PAGE_SIZE");
                String strCurrent = param.getString("cond_PAGE_CURRENT");

                long count = StringUtils.isBlank(strCount) ? 0L : Long.parseLong(strCount);
                int pageSize = StringUtils.isBlank(strSize) ? 10 : Integer.parseInt(strSize);
                int current = StringUtils.isBlank(strCurrent) ? 1 : Integer.parseInt(strCurrent);

                page = new Pagination();
                page.setCount(count);
                page.setPageSize(pageSize);
                page.setCurrent(current);
                page.setOnlyCount(false);
                page.setNeedCount(true);
            }

            IData dbInfo = new DataMap();
            String tipInfo = "";
            try
            {
                IDataset dataset = CSViewCall.call(this, "CS.InterfaceInfoSVC.invokeInterface", data);
                tipInfo = dataset.toString();
                // 服务调用成功后,记录参数场景到tl_interface_log表,add by longtian3 2013-07-25
                if (IDataUtil.isNotEmpty(dataset))
                {
                    IData log = new DataMap();
                    log.put("INTER_ID", param.getString("cond_INTERFACE_NAME", ""));
                    log.put("SCENE_VL", param.getString("cond_PARAM_INFO", ""));
                    log.put("INTER_CODE", param.getString("cond_INTERFACE_CODE", ""));
                    log.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    CSViewCall.call(this, "CS.InterfaceInfoSVC.logInterface", log);
                }
            }
            catch (Exception e)
            {
                tipInfo = Utility.getStackTrace(Utility.getBottomException(e)).toString();
            }
            finally
            {
                dbInfo.put("SCENE_ID", param.getString("cond_INTERFACE_SCENE").trim());
                dbInfo.put("INTER_ID", param.getString("cond_INTERFACE_NAME").trim());
                dbInfo.put("RESULTS", tipInfo);
                CSViewCall.call(this, "CS.InterfaceInfoSVC.upInterfaceResultById", dbInfo);

                setTipInfo(tipInfo);
            }
        }
    }

    
    public void invokeHwInterface(IRequestCycle cycle) throws Exception
    {
    	IData param = getData();
    	IData data = prepareData(cycle);
        boolean flag = checkStaffId(data);
        if(flag){
        	String tipInfo = "测试工号和登录工号不一致，不允许办理。";
        	setTipInfo(tipInfo);
        	return;
        }
        String url = param.getString("cond_INTERFACE_ADDR", "");
    	String svcName = param.getString("cond_INTERFACE_CODE", "");
    	String inparams2String = param.getString("cond_PARAM_INFO", "");
    	String terminalResult = Wade3ClientRequest.request(url, svcName, inparams2String, "GBK");
    	setTipInfo(terminalResult);
    }
    
    public void invokeIupcInterface(IRequestCycle cycle) throws Exception
    {
    	IData param = getData();
        String url = param.getString("cond_INTERFACE_ADDR", "");
    	String svcName = param.getString("cond_INTERFACE_CODE", "");
    	
    	IData data = prepareData(cycle);
        boolean flag = checkStaffId(data);
        if(flag){
        	String tipInfo = "测试工号和登录工号不一致，不允许办理。";
        	setTipInfo(tipInfo);
        	return;
        }
    	ServiceRequest request = new ServiceRequest();
	    request.setData(((IDataInput) data.get("INTERFACE_DATAINPUT")).getData());
	    
	    ServiceResponse response = BizServiceFactory.call(url, svcName, request, null, true, false, 6000000, 60000);
        IData out = response.getBody();
    	
    	setTipInfo(out.toString());
    }
    
    private IData prepareData(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String sAddr = param.getString("cond_INTERFACE_ADDR", "");
        String sCode = param.getString("cond_INTERFACE_CODE", "");

        String isChecked_ld = param.getString("cond_IBOSS_LD", "");

        if ("on".equals(isChecked_ld))
        {
            String sParam = param.getString("cond_PARAM_INFO", "");
            IData data = new DataMap();
            if (sParam.length() > 0)
            {
                IData wade4Data = Wade3DataTran.wade3To4DataMap(Wade3DataTran.strToMap(sParam));
                String svcName = wade4Data.getString("X_TRANS_CODE");
                if (StringUtils.isBlank(svcName))
                {
                    svcName = sCode;
                }

                data.put("INTERFACE_ADDR", sAddr);
                data.put("INTERFACE_CODE", svcName);
                data.put("INTERFACE_DATA", sParam);
            }
            return data;
        }
        else
        {
            IDataInput dataInput = new DataInput();
            String sLogininfos = param.getString("cond_LOGIN_INFO", "");
            if (sLogininfos.length() > 0)
            {
                String[] keyvals = sLogininfos.split(",");
                if (keyvals.length > 0)
                {
                    for (int i = 0; i < keyvals.length; i++)
                    {
                        String keyval = keyvals[i];
                        String[] keyvalparis = keyval.split("=");
                        if (keyvalparis.length > 1)
                        {
                            dataInput.getHead().put(keyvalparis[0], keyvalparis[1]);
                        }
                    }
                }
            }

            String sParam = param.getString("cond_PARAM_INFO", "");
            if (sParam.length() > 0)
            {
                String[] keyvals2 = sParam.split(",");
                if (keyvals2.length > 0)
                {
                    for (int i = 0; i < keyvals2.length; i++)
                    {
                        String keyval2 = keyvals2[i];
                        String[] keyvalparis2 = keyval2.split("=");
                        if (keyvalparis2.length > 1)
                        {
                            // 模式匹配自动解析
                            if (keyvalparis2[1].startsWith("$") && keyvalparis2[1].endsWith("$"))
                            {
                                String keyval3 = keyvalparis2[1].substring(1, keyvalparis2[1].length() - 1);
                                String interId = param.getString("cond_INTERFACE_NAME");
                                IData params = new DataMap();
                                params.put("INTER_ID", interId);
                                params.put("INTER_KEY", keyval3);
                                IDataset interInfoparams = CSViewCall.call(this, "CS.InterfaceInfoSVC.getInterfaceParam", params);
                                StringBuilder sb = new StringBuilder();
                                for (int k = 0; k < interInfoparams.size(); k++)
                                {
                                    sb.append(interInfoparams.get(k, "INTER_VAL"));
                                }
                                dataInput.getData().put(keyvalparis2[0], sb.toString());
                            }
                            // 处理IDATA iData字符串里面的“，”得事先存成#
                            else if (keyvalparis2[1].startsWith("!") && keyvalparis2[1].endsWith("!"))
                            {
                                String keyval3 = keyvalparis2[1].substring(1, keyvalparis2[1].length() - 1);
                                keyval3 = keyval3.replace("#", ",");
                                keyval3 = keyval3.replace("*", "\"");
                                IData iData = new DataMap(keyval3);
                                dataInput.getData().put(keyvalparis2[0], iData);
                            }
                            // 处理IDATASET iDataSet字符串里面的“，”得事先存成#
                            else if (keyvalparis2[1].startsWith("@") && keyvalparis2[1].endsWith("@"))
                            {
                                String keyval3 = keyvalparis2[1].substring(1, keyvalparis2[1].length() - 1);
                                keyval3 = keyval3.replace("#", ",");
                                keyval3 = keyval3.replace("*", "\"");
                                IDataset iDataset = new DatasetList(keyval3);
                                dataInput.getData().put(keyvalparis2[0], iDataset);
                            }
                            else
                            {
                                dataInput.getData().put(keyvalparis2[0], keyvalparis2[1]);
                            }
                        }
                    }
                }
            }
            if(dataInput.getData().isEmpty()){
            	if(sParam==null || sParam.trim().length()==0){
            		sParam = "{}";
            	}
            	IData paramInfo = new DataMap(sParam);
            	dataInput.getData().putAll(paramInfo);
            }

            IData data = new DataMap();
            data.put("INTERFACE_ADDR", sAddr);
            data.put("INTERFACE_CODE", sCode);
            data.put("INTERFACE_DATAINPUT", dataInput);

            return data;
        }
    }
    
    private boolean checkStaffId(IData data) throws Exception{
    	boolean flag = false;
    	String testStaffId = getVisit().getStaffId();
        IDataInput iDataInput = (IDataInput) data.get("INTERFACE_DATAINPUT");
        String loginStaffId = iDataInput.getHead().getString("STAFF_ID","");
        if(!testStaffId.equals(loginStaffId)){
        	flag = true;
        }
    	return flag;
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInterfaces(IDataset interfaces);

    public abstract void setInterfacescenes(IDataset interfacescenes);

    public abstract void setSceneInfo(String sceneInfo);

    public abstract void setTipInfo(String tipInfo);

}
