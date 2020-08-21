
package com.asiainfo.veris.crm.order.web.frame.csview.group.changememelement;

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

public class ChgMemEleFlowMainHttpHandler extends CSBizHttpHandler
{

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
        
        if (!StringUtils.startsWith(resInfoStr, "["))   //短号信息为空时，传的是"" 特殊替换下，不然报 A JSONArray text must start with '[' at character 0 of
        {
            resInfoStr = "[]";
        }

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
        if(bBossFlag.equals("true")){//后端校验凭证信息是否上传了，前端找不到对应的js文件，先在后端做，以后再优化 by zhuwj
        	if(this.getData().getString("MEB_VOUCHER_FILE_LIST", "").equals("")){
        		throw new Exception("凭证文件为空，请上传凭证文件！");
        	}
        	if(this.getData().getString("AUDIT_STAFF_ID", "").equals("")){
        		throw new Exception("稽核人员工号为空，请填写稽核人员工号！");
        	}
        }
        inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("REMARK", getData().getString("parm_REMARK"));
        inparam.put("MEM_ROLE_B", getData().getString("ROLE_CODE_B", ""));
        inparam.put("PRODUCT_ID", getGrpProductId());
        IDataset productElements = saveProductElemensFrontData();
        IDataset productParam = saveProductParamInfoFrontData();
        IDataset resinfos = saveResInfoFrontData();
        inparam.put("ELEMENT_INFO", productElements);
        inparam.put("RES_INFO", resinfos);
        //add by chenzg@20180711 REQ201804280001集团合同管理界面优化需求
        inparam.put("MEB_VOUCHER_FILE_LIST", pgData.getString("MEB_VOUCHER_FILE_LIST", ""));
        inparam.put("AUDIT_STAFF_ID", pgData.getString("AUDIT_STAFF_ID", ""));

        String checkModeString = getData().getString("cond_CHECK_MODE");
        if (StringUtils.isNotEmpty(checkModeString))
        {
            inparam.put("CHECK_MODE", checkModeString);
        }
        
        //附件列表
        String mebFileShow = getData().getString("MEB_FILE_SHOW");
        if (StringUtils.isNotEmpty(mebFileShow) && StringUtils.equals("true", mebFileShow)){
        	inparam.put("MEB_FILE_SHOW", mebFileShow);
        	inparam.put("MEB_FILE_LIST", getData().getString("MEB_FILE_LIST",""));
        }
        
        //凭证上传信息 add by chenzg@20180627 REQ201804280001集团合同管理界面优化需求
        inparam.put("MEB_VOUCHER_FILE_LIST", this.getData().getString("MEB_VOUCHER_FILE_LIST", ""));
        inparam.put("AUDIT_STAFF_ID", this.getData().getString("AUDIT_STAFF_ID", ""));
        
        // 费用信息
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));

        // esop参数
        String eos = getData().getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            inparam.put("EOS", new DatasetList(eos));
        }

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);

        // 业务是否预约 true 预约 false 非预约工单
        String ifBooking = getData().getString("IF_BOOKING", "false");
        if (ifBooking.equals("true"))
            inparam.put("IF_BOOKING", ifBooking);

        // 根据产品编号获取产品的品牌信息
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, getGrpProductId());
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IData bbossData = new DataMap(getData().getString("productGoodInfos"));// BBOSS商产品信息
            inparam.put("BBOSS_INFO", bbossData);
            inparam.put(Route.ROUTE_EPARCHY_CODE, getData().getString("MEB_EPARCHY_CODE"));
            IDataset result = CSViewCall.call(this, "CS.ChangeBBossMemSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }
        if ("10005742".equals(getGrpProductId()))
        {// ADC校讯通
            IDataset result = CSViewCall.call(this, "SS.ChangeAdcMemElementSVC.crtOrder", inparam);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.ChangeMemElementSvc.changeMemElement", inparam);
        setAjax(result);
    }

}
