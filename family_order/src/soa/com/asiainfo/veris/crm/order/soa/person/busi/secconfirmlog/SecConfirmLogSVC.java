package com.asiainfo.veris.crm.order.soa.person.busi.secconfirmlog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SecConfirmLogSVC extends CSBizService {

    private static final long serialVersionUID = 5706213859418709172L;

    /**
     * 二次确认日志接收接口，提供给外部渠道调用
     */
    public IData acceptLog(IData input) throws Exception {
        SecConfirmLogBean bean = new SecConfirmLogBean();
        return bean.acceptLog(input);
    }

    /**
     * 二次确认日志查询接口，提供给客服调用
     */
    public IDataset querySecConfirmLog(IData input) throws Exception {
        SecConfirmLogBean bean = new SecConfirmLogBean();

        IDataset  secConfirmLogDatas= bean.querySecConfirmLog(input);

        if (IDataUtil.isNotEmpty(secConfirmLogDatas)) {//查找业务名称
            for (int i = 0; i < secConfirmLogDatas.size(); i++) {
                IData iData=secConfirmLogDatas.getData(i);

                if (StringUtils.isNotEmpty(iData.getString("SP_CODE"))&&StringUtils.isNotEmpty(iData.getString("OPERATOR_CODE"))){

                    try {
                        IDataset  upcDatas = UpcCall.querySpServiceAndInfoAndParamByCond("",iData.getString("SP_CODE"), iData.getString("OPERATOR_CODE"),
                                "");
                        if (IDataUtil.isNotEmpty(upcDatas)){
                            secConfirmLogDatas.getData(i).put("BIZ_NAME",upcDatas.getData(0).getString("BIZ_NAME"));
                            secConfirmLogDatas.getData(i).put("PRICE",upcDatas.getData(0).getString("PRICE"));

                        }


                    } catch (Exception e) {

                    }


                }
            }
        }


        return secConfirmLogDatas;
    }

    /**
     * 清理业务取消5个月的日志信息，提供给定时任务调用
     */
    public void autoCleanSecConfirmLog(IData input) throws Exception {
        SecConfirmLogBean bean = new SecConfirmLogBean();
        bean.autoCleanSecConfirmLog(input);
    }
}