
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupmember;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class CrtGrpMebFlowMainHttpHandler extends CSBizHttpHandler
{

    private String shortCode;

    /**
     * 获取产品ID
     * 
     * @return
     * @throws Exception
     */
    public String getGrpProductId() throws Exception
    {

        return this.getData().getString("GRP_PRODUCT_ID", "");

    }

    public String savePayPlanFrontData() throws Exception
    {
        return this.getData().getString("PAY_PLAN_SEL_PLAN_TYPE", "P");
    }

    /**
     * 获取集团产品元素信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveProductElemensFrontData() throws Exception
    {

        String selectElementStr = getData().getString("SELECTED_ELEMENTS", "[]");

        IDataset selectElements = new DatasetList(selectElementStr);
        
        
        return selectElements;
    }

    public IDataset saveProductParamInfoFrontData() throws Exception
    {

        IDataset resultset = new DatasetList();
        IData result = new DataMap();
        IDataset productParamAttrset = new DatasetList();
        IData productParam = getData("pam", true);
        if (IDataUtil.isEmpty(productParam))
            return null;
        Iterator<String> iterator = productParam.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = productParam.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            if ("SHORT_CODE".equals(key))
            {
                shortCode = (String) value;
            }
            productParamAttrset.add(productParamAttr);

        }

        result.put("PRODUCT_ID", getGrpProductId());
        result.put("PRODUCT_PARAM", productParamAttrset);
        resultset.add(result);
        return resultset;
    }

    /**
     * 获取资源信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset saveResInfoFrontData() throws Exception
    {

        String resInfoStr = getData().getString("DYNATABLE_RES_RECORD", "[]");

        IDataset resinfos = new DatasetList(resInfoStr);
        return resinfos;
    }

    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {
    	IData pgData = this.getData();
        IData inparam = new DataMap();
        String bBossFlag=getData().getString("BBOSS_FLAG", "");
        if(bBossFlag.equals("true")){//后端校验凭证信息是否上传了，前端找不到对应的js文件，先在后端做，以后再优化 包月zhuwj
        	if(this.getData().getString("MEB_VOUCHER_FILE_LIST", "").equals("")){
        		throw new Exception("凭证文件为空，请上传凭证文件！");
        	}
        	if(this.getData().getString("AUDIT_STAFF_ID", "").equals("")){
        		throw new Exception("稽核人员工号为空，请填写稽核人员工号！");
        	}
        }
        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        //add by chenzg@20180711 REQ201804280001集团合同管理界面优化需求
        inparam.put("MEB_VOUCHER_FILE_LIST", pgData.getString("MEB_VOUCHER_FILE_LIST", ""));
        inparam.put("AUDIT_STAFF_ID", pgData.getString("AUDIT_STAFF_ID", ""));

        String ifBooking = getData().getString("IF_BOOKING", "false"); // 业务预约时间
        if (ifBooking.equals("true"))
        {
            inparam.put("IF_BOOKING", "true");
        }

        String checkModeString = getData().getString("cond_CHECK_MODE");
        if (StringUtils.isNotEmpty(checkModeString))
        {
            inparam.put("CHECK_MODE", checkModeString);
        }
        //添加是否需要二次确认的判断
        String isSec =getData().getString("PAGE_SELECTED_TC");
        String Pid=getGrpProductId();
        if(Pid.equals("10009805")){
        	if(isSec.equals("false")){
        		throw new Exception("成员是否二次确认为必选项，请选择！");
        	}
        }
        if (StringUtils.isNotEmpty(isSec))
        {
            inparam.put("PAGE_SELECTED_TC", isSec);
        }

        String effectNow = getData().getString("EFFECT_NOW");// 产品资费立即生效标志 true立即生效
        if (StringUtils.isNotEmpty(effectNow))
        {
            inparam.put("EFFECT_NOW", "true".equals(effectNow) ? "true" : "false");
        }

        String ifOutNet = getData().getString("IF_ADD_MEB", "false");// 新增成员三户信息
        if (ifOutNet.equals("true"))
        {
            inparam.put("IS_OUT_NET", "true");
            IData mebUserInfo = new DataMap();
            mebUserInfo.put("PRODUCT_ID", getData().getString("MEB_PRODUCT_ID", ""));
            mebUserInfo.put("EPARCHY_CODE", getData().getString("MEB_EPARCHY_CODE", ""));
            mebUserInfo.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
            inparam.put("MEB_USER_INFO", mebUserInfo);
        }

        IDataset productElements = saveProductElemensFrontData();
        shortCode = null;
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        if (IDataUtil.isNotEmpty(resinfos))
            inparam.put("RES_INFO", resinfos);

        if (IDataUtil.isNotEmpty(productParam))
        {
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        }
        if (!StringUtils.isEmpty(shortCode))
        {
            inparam.put("SHORT_CODE", shortCode);
        }
        inparam.put("PLAN_TYPE_CODE", savePayPlanFrontData());

        //附件列表
        String mebFileShow = getData().getString("MEB_FILE_SHOW");
        if (StringUtils.isNotEmpty(mebFileShow) && StringUtils.equals("true", mebFileShow)){
        	inparam.put("MEB_FILE_SHOW", mebFileShow);
        	inparam.put("MEB_FILE_LIST", getData().getString("MEB_FILE_LIST",""));
        }
        
        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // 发展人信息
        inparam.put("DEVELOP_STAFF_ID", getData().getString("DEVELOP_STAFF_ID"));
        
        //凭证上传信息 add by chenzg@20180627 REQ201804280001集团合同管理界面优化需求
        inparam.put("MEB_VOUCHER_FILE_LIST", this.getData().getString("MEB_VOUCHER_FILE_LIST", ""));
        inparam.put("AUDIT_STAFF_ID", this.getData().getString("AUDIT_STAFF_ID", ""));

        // 根据产品编号获取产品的品牌信息
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
            inparam.put("BBOSS_INFO", bbossData);
            inparam.put(Route.ROUTE_EPARCHY_CODE, getData().getString("MEB_EPARCHY_CODE"));
            IDataset result = CSViewCall.call(this, "CS.CreateBBossMemSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }
        if ("10005742".equals(getGrpProductId()))
        {// ADC校讯通
            IDataset result = CSViewCall.call(this, "SS.CreateAdcGroupMemberSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.CreateGroupMemberSvc.createGroupMember", inparam);
        setAjax(result);
    }
}
