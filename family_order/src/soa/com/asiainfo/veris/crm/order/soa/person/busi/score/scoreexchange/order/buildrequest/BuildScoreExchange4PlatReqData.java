
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ScoreExchangeRequestData;

public class BuildScoreExchange4PlatReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        String ruleId = IDataUtil.chkParam(param, "RULE_ID");
        String count = IDataUtil.chkParam(param, "COUNT");
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) brd;

        ExchangeData data = new ExchangeData();
        List<ExchangeData> exchangeDatas = new ArrayList<ExchangeData>();
        data.setRuleId(ruleId);
        data.setCount(count);
        exchangeDatas.add(data);
        
        //通过332配置附带绑定优惠
        IDataset discntList = CommparaInfoQry.getCommpara("CSM", "332",ruleId,reqData.getUca().getUserEparchyCode() );
        if(IDataUtil.isNotEmpty(discntList)){
        	for (int i = 0, size = discntList.size(); i < size; i++)
        	{
        		String paraCode1 = discntList.getData(i).getString("PARA_CODE1");
             	ExchangeData dataDiscnt = new ExchangeData();
             	dataDiscnt.setRuleId(paraCode1);
             	dataDiscnt.setCount(count);
             	exchangeDatas.add(dataDiscnt);
             }
        }
        
        reqData.setExchangeDatas(exchangeDatas);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreExchangeRequestData();
    }

}
