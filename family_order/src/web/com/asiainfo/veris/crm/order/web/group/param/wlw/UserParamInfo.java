
package com.asiainfo.veris.crm.order.web.group.param.wlw;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class UserParamInfo extends GroupParamPage
{
    public UserParamInfo()
    {
        super();
    }

    // 获取自动生成扩展码
    public void createBizCodeExtend(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        IDataset out = CSViewCall.call(this, "CS.SeqMgrSVC.getWlwBizCode", data);

        String extendCode = out.getData(0).getString("seq_id", "");
        setAjax("EXTEND_CODE", extendCode);
    }

    /**
     * 作用:判断服务代码是否可用
     * 
     * @author luojh 2009-11-07
     * @param cycle
     * @throws Exception
     */
    public void getDumpIdByajax(IRequestCycle cycle) throws Exception
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

    public void getSensitiveTextByajax(IRequestCycle cycle) throws Exception
    {
        String productId = getData().getString("PRODUCT_ID");
        // 中英文签名敏感字符过滤产品配置
        IDataset proDatas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CGM", "4387", productId);
        IDataset result = new DatasetList();
        IData tmp = new DataMap();
        if (IDataUtil.isNotEmpty(proDatas))
        {
            tmp.put("IN_PRODUCT", "true");// 在判断敏感字符产品列表之内
            String textEcgnZh = getData().getString("TEXT_ECGN_ZH");// 中文签名
            String textEcgnEn = getData().getString("TEXT_ECGN_EN");// 英文签名
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
        result.add(tmp);

        setAjax(result);
    }

    public void getServiceParamsByajax(IRequestCycle cycle) throws Exception
    {
        IData serviceInparam = new DataMap();
        serviceInparam.put("USER_ID", "");
        serviceInparam.put("PRODUCT_ID", getData().getString("PRODUCT_ID", ""));
        serviceInparam.put("PACKAGE_ID", getData().getString("PACKAGE_ID", ""));
        serviceInparam.put("SERVICE_ID", getData().getString("SERVICE_ID", ""));

        IDataset serviceparamset = CSViewCall.call(this, "SS.WLWUserParamsSVC.getServiceParam", serviceInparam); // paramsbean.getServiceParam(pd,
        setAjax(serviceparamset);
    }

    /*
     * 初始服务新增 参数界面
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
        String hiddenName = getData().getString("ITEM_INDEX");
        this.setHiddenName(hiddenName);
        String buttenName = getData().getString("POPUP_BTN_NAME");
        this.setButtenName(buttenName);
        String serviceId = getData().getString("SERVICE_ID");

        this.setServiceId(serviceId);
        String cancleflag = getData().getString("CANCLE_FLAG", "");
        this.setCancleFlag(cancleflag);

        IDataset bizCodeset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CSM", "9017", "bizincode");
        if (IDataUtil.isEmpty(bizCodeset))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_410);
        }
        else
        {
            IData bizData = new DataMap();
            bizData = bizCodeset.getData(0);
            this.setBizInCode(bizData.getString("PARA_CODE1"));
        }
    }

    public abstract void setBizInCode(String baseServCodeHead);

    public abstract void setButtenName(String buttenName);

    public abstract void setCancleFlag(String cancleFlag);

    public abstract void setComboBox(int cobobox);

    public abstract void setComboBoxValue(IDataset dataset);

    public abstract void setHiddenName(String hiddenName);

    public abstract void setParamVerifySucc(String paramVerifySucc);

    public abstract void setPlatsvcparam(IData info);

    public abstract void setServiceId(String serviceId);

    public abstract void setSvcotherlists(IDataset svcotherlists);
}
