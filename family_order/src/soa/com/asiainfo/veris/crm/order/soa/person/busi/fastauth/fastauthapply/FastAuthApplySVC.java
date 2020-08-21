
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.fastauthapply;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FastAuthApplySVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset applyAuthTrade(IData data) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        int applySuccessFlag = bean.applyAuthTrade(data);
        Returndata.put("APPLY_SUCCESS_FLAG", applySuccessFlag);
        dataset.add(Returndata);
        return dataset;
    }

    /**
     * 组合URL串
     */
    public void combienUrl(IData param) throws Exception
    {

        StringBuilder strb = new StringBuilder();
        IDataset ret = queryAuthTradeType(param);
        if (ret.size() <= 0)
        {
            param.put("URL", "NullData");
        }
        else
        {
            String uu = ((IData) ret.get(0)).getString("URL");
            IData modData = parseAuthModName(uu);

            strb.append("<br>").append("<a href='javascript:void(0)' onclick=\"redirectTo('").append(modData.getString("PAGE")).append("','").append(modData.getString("LISTENER")).append("','").append(modData.getString("PARAM")).append("')\">")
                    .append("</a>");

            param.put("URL", uu);
        }

    }

    public void delAppAuthInfo(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        String ask_id = input.getString("ASK_ID");
        input.clear();
        input.put("ASK_ID", ask_id);
        input.put("RSRV_STR2", "1");
        bean.updateAuthTrade(input);
    }

    /** 对使用次数进行更新 */
    public void delAuthTimes(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        String ask_id = input.getString("ASK_ID");
        input.clear();
        input.put("ASK_ID", ask_id);
        bean.delAuthTimes(input);
    }

    private IData parseAuthModName(String str) throws Exception
    {

        IData iData = new DataMap();

        // boolean bPage = true;
        // boolean bListener = false;

        String strCode[] = str.split("&");

        iData.put("PAGE", strCode[0].substring(strCode[0].indexOf("service=page/") + 13));
        if (strCode.length > 1)
        {
            iData.put("LISTENER", strCode[1].substring(strCode[1].indexOf("=") + 1));
        }
        else
        {
            iData.put("LISTENER", "");
        }

        StringBuilder strb = new StringBuilder();
        for (int idx = 2; idx < strCode.length; idx++)
        {
            strb.append("&");
            strb.append(strCode[idx]);
        }
        iData.put("PARAM", strb.toString());

        return iData;
    }

    public IDataset queryApplyTrade(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        return bean.queryApplyTrade(input, null);
    }

    public IDataset queryAuthTradeType(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        return bean.queryAuthTradeType(input, getPagination());
    }

    public IDataset queryPwd(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        return bean.queryPwd(input, getPagination());
    }

    public IDataset queryStaffs(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        boolean flag = false;
        if (!this.hasPriv("ORG_LISTALL") && !this.hasPriv("ORG_LIST_AREA") && !this.hasPriv("ORG_LIST_AREA_" + "20") && !this.hasPriv("ORG_LIST_AREA_" + "30") && this.hasPriv("ORG_LIST_AREA_" + "40"))
        {
            flag = true;
        }
        return bean.queryStaffs(flag, input, getPagination());
    }

    public void updateAuthTrade(IData input) throws Exception
    {
        FastAuthApplyBean bean = (FastAuthApplyBean) BeanManager.createBean(FastAuthApplyBean.class);
        input.put("ASK_STAFF_ID", this.getVisit().getStaffId());
        input.put("ASK_DEPART_ID", this.getVisit().getDepartId());
        input.put("ASK_TIME", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        input.put("RSRV_STR1", input.getString("APPLY_REMARK"));// 更改授权申请时 更新申请理由
        bean.updateAuthTrade(input);
    }

}
