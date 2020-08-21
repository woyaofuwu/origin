package com.asiainfo.veris.crm.iorder.web.igroup.offercha.tisms;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class SmsHandler extends BizHttpHandler
{
    public void getBizCodeTail() throws Exception
    {
        IData result = new DataMap();

        String codeC = "00001";

        IData sidata = CSViewCall.callone(this, "CS.SeqMgrSVC.getGrpMolist", result);
        codeC = sidata.getString("SEQ_GRP_MOLIST");

        String sclen = "5";
        int cLen = 0;
        try
        {
            cLen = Integer.parseInt(sclen);
        }
        catch (Exception e)
        {
            cLen = 0;
        }

        codeC = codeC.substring((codeC.length() - cLen), codeC.length());

        IData retData = new DataMap();
        retData.put("strSvcCodeTail", codeC);

        result.put("AJAX_DATA", retData);


        String ajaxdatastr = result.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }
    public void checkValidServCode() throws Exception
    {
    	IData data=this.getData();
    	IData result = new DataMap();

        String servCode = data.getString("SERV_CODE");

        IData param = new DataMap();
        param.put("SERV_CODE", servCode);
        param.put("BIZ_STATE_CODE", "");

        // 调用后台服务，判断tf_f_user_grp_platsvc和台账记录中 是否已经存在(使用)该serCode;
        IDataset userSerCode = CSViewCall.call(this, "CS.UserGrpPlatSvcInfoQrySVC.getuserPlatsvcbyservcode", param); 
        IDataset tradeSerCode = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryTradeByRsrvstr1", param);

        if (IDataUtil.isNotEmpty(userSerCode) || IDataUtil.isNotEmpty(tradeSerCode))
        {
            param.put("ERROR_MESSAGE", "该服务代码已经使用!请重新输入!");
            param.put("RESULT", "false");
        }
        
        result.put("AJAX_DATA", param);
        String ajaxdatastr = result.getString("AJAX_DATA", "");
        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }
}
