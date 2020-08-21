package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * @ClassName ChooseIdleWideAcctIdSVC
 * @Description TODO
 * @Author ApeJungle
 * @Date 2019/7/8 17:58
 * @Version 1.0
 */
public class ChooseIdleWideAcctIdSVC extends CSBizService {

    private static final long serialVersionUID = 57458169539835088L;

    public IDataset getSerialNumberPrefixList(IData input) throws Exception {
        IDataset paraInfos = CommparaInfoQry.getCommparaAllCol("CSM", "2828", "680", "ZZZZ");
        
        if (IDataUtil.isEmpty(paraInfos)) {
            CSAppException.apperr(BizException.CRM_BIZ_5, "TD_S_COMMPARA表[PARAM_ATTR=2828][PARAM_CODE=680]参数配置信息不存在！");
        }

        return paraInfos;
    }

    public IDataset qrySerialNumberList(IData input) throws Exception {
        return ResCall.queryNumInfo4AreaSel(input);
    }
}
