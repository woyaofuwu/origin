package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule.CheckTradeBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 用于拼甩单的请求数据
 * 即根据规则看要生成什么类型的工单，要调用哪个TradeReg
 * 这个类主要用于准备TradeReg需要的入参数据,然后生成甩单工单（也有可能会生成TF_B_TRADE工单）
 * 2020/7/30
 **/
public abstract class ReqBuildService extends CSBizService {
    public static Logger logger = Logger.getLogger(ReqBuildService.class);
    /**
     * input 要包含TP_ORDER_TEMPL_CFG中的数据
     */
    public IData createTPOrder(IData input) throws Exception{
        //处理数据前做checkbefore
        String checkTag = input.getString("CHECK_TAG");
        if(StringUtils.isNotEmpty(checkTag) && checkTag.startsWith("0")){
            IData param = checkInput(input);
            IDataset dataset = checkBefore(param);
            if(DataUtils.isNotEmpty(dataset)){
                IData data = dataset.first();
                //获取checkBefore返回的错误规则信息并抛出规则异常
                IDataset errorSet = data.getDataset("TIPS_TYPE_ERROR");
                if(DataUtils.isNotEmpty(errorSet)){
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < errorSet.size();i++){
                        IData errorInfo = errorSet.getData(i);
                        String tipsInfo = errorInfo.getString("TIPS_INFO");
                        stringBuilder.append((i+1)+"、"+tipsInfo+"</br> ");
                    }
                    CSAppException.apperr(TpOrderException.TP_ORDER_40000,stringBuilder.toString());
                }
            }
        }
        return initReqData(input);
    }

    /**
     * checkBefore必填字段校验
     * @param input
     * @throws Exception
     */
    private IData checkInput(IData input) throws Exception{
        IData param = new DataMap();
        String serialNumber = input.getString(TpConsts.comKey.serialNumber);
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        param.put(TpConsts.comKey.serialNumber,serialNumber);
        param.put(TpConsts.comKey.tradeTypeCode,getTradeTypeCode(input));
        param.put("USER_ID",ucaData.getUserId());
        param.put("CUST_ID",ucaData.getCustId());
        param.put("X_CHOICE_TAG","0");
        return param;
    }

    /**
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkBefore(IData input)throws Exception{
        CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
        IDataset dataset = bean.checkBeforeTrade(input);
        return dataset;
    }

    public abstract IData initReqData(IData input)throws Exception;

    public abstract String getTradeTypeCode(IData input)throws Exception;
}
