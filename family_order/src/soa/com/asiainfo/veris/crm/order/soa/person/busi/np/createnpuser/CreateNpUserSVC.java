
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CreateNpUserSVC extends CSBizService
{

	public IDataset getSwith(IData param) throws Exception
	{
		return CommparaInfoQry.getCommpara("CSM", "6261", "switch", "0898");
	}
	
	public IDataset dealChange(IData param) throws Exception
	{
		IDataset dataset = new DatasetList();
		String staffId = getVisit().getStaffId();
		if(!("AAY00032".equals(staffId))){
			CSAppException.apperr(CrmCommException.CRM_COMM_1,"该界面只允许市场经营部刁玉操作，工号为：AAY00032!");
		}
		Dao.executeUpdateByCodeCode("TD_S_COMMPARA","UPDATE_SWITCH",param,Route.CONN_CRM_CEN);
		return dataset;
	}
	
    public IData checkAndInitProduct(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);
        IData data = new DataMap();
        data.put("checkSerialNumber", bean.checkSerialNumber(param));
        //TODO lijun  新产品组件不需要查询产品目录      data.put("getProductInfos", bean.getProductInfos(param));
        data.put("getProductInfos", new DatasetList());
        return data;
    }

    public IDataset checkRealNameLimitByPspt(IData input) throws Exception
    {
        IDataset ajaxDataset = new DatasetList();
        String custName = input.getString("custName");
        String psptId = input.getString("psptId");
        IData param = new DataMap();
        if (!"".equals(custName) && !"".equals(psptId))
        {
            param.put("CUST_NAME", custName);
            param.put("PSPT_ID", psptId);
            int rCount = UserInfoQry.getRealNameUserCountByPspt(custName, psptId).size();// 改造成强对象
            int rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);
            IData ajaxData = new DataMap();
            if (rCount < rLimit)
            {
                ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
            }
            else
            {
                ajaxData.put("MSG", "证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);
        }
        return ajaxDataset;
    }

    /**
     * 获取合户号码的三户资料
     * 
     * @param pd
     * @param param
     * @param td
     * @return
     * @throws Exception
     */
    public IData querySameAcctInfo(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.querySameAcctInfo(param, getPagination());
    }

    /**
     * 合户号码校验
     * 
     * @param pd
     * @param param
     * @param td
     * @return
     * @throws Exception
     */
    public IDataset checkAccQueryNo(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.checkAccQueryNo(param, getPagination());
    }

    public IData queryCustInfoByCustId(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.queryCustInfoByCustId(param);
    }
    
    /**
     * 携入开户获取员工是否需要显示授权码下拉框配置
     * 
     * @param pd
     * @param param
     * @param td
     * @return
     * @throws Exception
     */
    public IData queryAuthStaffId(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.queryAuthStaffId(param);
    }

    /**
     * 携入开户证件号码校验
     * 
     * @param pd
     * @param param
     * @param td
     * @return
     * @throws Exception
     */
    public IData checkCustQueryNo(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.checkCustQueryNo(param, getPagination());
    }

    public IData checkSerialNumber(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.checkSerialNumber(param);
    }

    public IData checkSimCardNo(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);

        return bean.checkSimCardNo(param);
    }

    public IDataset getProductFeeInfo(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);
        return bean.getProductFeeInfo(param);
    }

    public IData getProductInfos(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);
        return bean.getProductInfos(param);
    }
    
    public IData openSaleActiveNpReg(IData param) throws Exception
    {
        CreateNpUserBean bean = BeanManager.createBean(CreateNpUserBean.class);
        return bean.openSaleActiveNpReg(param);
    }
    
	public IDataset aopOpenSaleActive (IData data)throws Exception{
		HashMap<String, Object> param=new HashMap();
		String dealCond=data.getString("DEAL_COND","");
		if(!"".equals(dealCond)){
			JSONObject jasonObject = JSONObject.fromObject(dealCond);
			Set<String> keySet = jasonObject.keySet();
			for (String key : keySet) {  
		        Object obj = jasonObject.get(key);
		        param.put(key, obj);
		    }
		}
		IData p=new DataMap(param);		
		return  CSAppCall.call("SS.saleactiveintf.submitSaleActiveVer2", p);
	}
	
}
