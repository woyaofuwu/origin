
package com.asiainfo.veris.crm.order.web.group.param.gdzy;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData paraInfo = result.getData("PARAM_INFO");

        String productNo = paraInfo.getString("PRODUCT_ID");
        
        // 调用后台服务查页面上需要显示的商务宽带参数信息
        IDataset productInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "GDZY", "ZZZZ");
        
        paraInfo.put("GRPGDZY_INFO", productInfo);
        paraInfo.put("NOTIN_METHOD_NAME", "ChgUs");
        paraInfo.put("pam_NOTIN_CURRENT_DATE", SysDateMgr.getSysDate());

        IDataset feeNameInfo = StaticUtil.getStaticList("GDZY_FEENAME");
        paraInfo.put("FEENAME_INFO", feeNameInfo);
        
        String userId = data.getString("USER_ID");
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "GDZY");
        IDataset userOtherInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);
        //判断OTHER表中有没有数据
        if (null != userOtherInfo && userOtherInfo.size() > 0)
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < userOtherInfo.size(); i++)
            {
                IData userOtherData = (IData) userOtherInfo.get(i);
                IData userData = new DataMap();

                userData.put("pam_NOTIN_OPER_TAG", userOtherData.get("RSRV_VALUE"));
                userData.put("pam_NOTIN_PROJECT_NAME", userOtherData.getString("RSRV_STR1"));//项目名称
                
                String dataId = userOtherData.getString("RSRV_STR2","");
                userData.put("pam_NOTIN_FEE_NAME", dataId);//收费名称
                userData.put("pam_NOTIN_FEE_NAME_V", "");//收费名称
                if(StringUtils.isNotBlank(dataId))
                {
                	String feeName = StaticUtil.getStaticValue("GDZY_FEENAME",dataId);
                	if(StringUtils.isNotBlank(feeName))
                	{
                		userData.put("pam_NOTIN_FEE_NAME_V", feeName);
                	}
                }
                
                userData.put("pam_NOTIN_FEE_COST", userOtherData.getString("RSRV_STR3","0"));//收费金额
                userData.put("pam_NOTIN_FEE_END_DATE", userOtherData.getString("RSRV_STR4"));//收费截止时间
                userData.put("pam_NOTIN_REMARK", userOtherData.getString("RSRV_STR6"));//备注
                dataset.add(userData);
            }
            paraInfo.put("GDZY_INFO", dataset);
            paraInfo.put("NOTIN_AttrGdzy", dataset);
            paraInfo.put("NOTIN_OLD_AttrGdzy", dataset);
        }
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData paraInfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            paraInfo = result.getData("PARAM_INFO");
        }
        String productNo = paraInfo.getString("PRODUCT_ID");
        
        // 调用后台服务查页面上需要显示的定制云参数信息
        IDataset productInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "GDZY", "ZZZZ");
        paraInfo.put("GRPGDZY_INFO", productInfo);
        
        IDataset feeNameInfo = StaticUtil.getStaticList("GDZY_FEENAME");
        paraInfo.put("FEENAME_INFO", feeNameInfo);
               
        paraInfo.put("NOTIN_METHOD_NAME", "CrtUs");
        
        paraInfo.put("pam_NOTIN_CURRENT_DATE", SysDateMgr.getSysDate());
        
        return result;
    }
}
