package com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class WlwParamHandler extends BizHttpHandler
{
    public void queryApplyTypebs() throws Exception
    {
    	IData data = this.getData();
    	IData resultData = new DataMap();
        IData output = new DataMap();
        String strTA = data.getString("APPLY_TYPE_A", "");
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9980");
        param.put("PARAM_CODE", strTA);
        param.put("EPARCHY_CODE", "0898");
        IDataset applyTypebs = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
        IDataset applyTypebsA =new DatasetList();
        if(IDataUtil.isNotEmpty(applyTypebs)) {
        	applyTypebsA = DataHelper.distinct(applyTypebs, "PARA_CODE1", "");
        }
        output.put("APPLY_TYPE_B_LIST", applyTypebsA);
        
        resultData.put("DATA_VAL", output);

        String ajaxdatastr = resultData.getString("DATA_VAL", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }

	// 获取自动生成扩展码
    public void createBizCodeExtend() throws Exception
    {
        IData data = new DataMap();
        IDataset out = CSViewCall.call(this, "CS.SeqMgrSVC.getWlwBizCode", data);

        String extendCode = out.getData(0).getString("seq_id", "");
        setAjax("EXTEND_CODE", extendCode);
    }
    
    //作用:判断服务代码是否可用
    public void getDumpIdByajax() throws Exception
    {
        IData data = getData();

        IData param = new DataMap();
        param.put("INST_TYPE", "S");
        param.put("ATTR_CODE", "BIZ_IN_CODE");
        param.put("ATTR_VALUE", data.getString("ACCESSNUMBER", ""));
        IDataset idata = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByTypeCodeValue", param);

        String flag = "true";
        if (IDataUtil.isNotEmpty(idata))
        {
            flag = "false";
        }
        setAjax("ISCHECKAACCESSNUMBER", flag);
    }
    
    public void getSensitiveTextByajax() throws Exception
    {
    	IData data = getData();
        String productId = data.getString("PRODUCT_ID");
        // 中英文签名敏感字符过滤产品配置
        IDataset proDatas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CGM", "4387", productId);

        IData tmp = new DataMap();
        if (IDataUtil.isNotEmpty(proDatas))
        {
            tmp.put("IN_PRODUCT", "true");// 在判断敏感字符产品列表之内
            String textEcgnZh = data.getString("TEXT_ECGN_ZH");// 中文签名
            String textEcgnEn = data.getString("TEXT_ECGN_EN");// 英文签名
            // 敏感字符
            IDataset sensitiveWords = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CGM", "4386", "ADCZHEN");

            tmp.put("HAS_ZH", "false");
            tmp.put("HAS_EN", "false");
            if (IDataUtil.isNotEmpty(sensitiveWords))
            {
                for (int i = 0; i != sensitiveWords.size(); ++i)
                {

                    if (textEcgnZh.indexOf(sensitiveWords.getData(i).getString("PARAM_NAME")) != -1)
                    {
                        tmp.put("HAS_ZH", "true");// 含有中文敏感字符
                        tmp.put("PARAM_NAME", sensitiveWords.getData(i).getString("PARAM_NAME"));
                        break;
                    }

                    if (textEcgnEn.indexOf(sensitiveWords.getData(i).getString("PARAM_NAME")) != -1)
                    {
                        tmp.put("HAS_EN", "true");// 含有英文敏感字符
                        tmp.put("PARAM_NAME", sensitiveWords.getData(i).getString("PARAM_NAME"));
                        break;
                    }

                }
            }
        }
        else
        {
            tmp.put("IN_PRODUCT", "false");// 在判断敏感字符产品列表之外
        }

        setAjax(tmp);
    }
    
    public void queryCommonApnTemplate() throws Exception
    {
        IData data = getData();
        String apnName = data.getString("APNNAME", "");
       
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "1511");
        param.put("PARAM_CODE", apnName);
        param.put("EPARCHY_CODE", "0898");
        IDataset apnTemplates = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
        IData apnTemplate=apnTemplates.getData(0);
       
        //para_code1对应低功耗模式，para_code2对应RAU/TAU定时器是否需填
        apnTemplate.put("LOWPOWERMODE", apnTemplate.getString("PARA_CODE1"));
        apnTemplate.put("RAUTAUTIMER", apnTemplate.getString("PARA_CODE2"));
        
        setAjax(apnTemplate);
    }
}
