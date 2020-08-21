
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ScoreExchangeRequestData;

public class BuildScoreExchangeRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) brd;

        IDataset temp = new DatasetList(param.getString("EXCHANGE_DATA"));
        ExchangeData data = null;
        List<ExchangeData> exchangeDatas = new ArrayList<ExchangeData>();
        int tempSize = temp.size();
        for (int i = 0; i < tempSize; i++)
        {
            data = new ExchangeData();
            data.setRuleId(temp.getData(i).getString("RULE_ID"));
            data.setCount(temp.getData(i).getString("COUNT"));
            data.setEvalue(temp.getData(i).getString("EVALUE"));
            data.setHbevalue(temp.getData(i).getString("HBEVALUE"));//兑换电子券金额   REQ201703030013
            data.setReqId(temp.getData(i).getString("REQ_ID"));//请求业务流水号  REQ201703030013
            data.setActionId(temp.getData(i).getString("ACTION_ID"));//活动编号  REQ201703030013  2017/6/20  新增,因为以后活动编码会改变
            data.setProId(temp.getData(i).getString("PRO_ID"));//券别编号  REQ201703030013  2017/6/20  新增,因为以后券别编码会改变

            exchangeDatas.add(data);
        }

        if (StringUtils.isNotBlank(param.getString("CARD_NOS")))
        {
            List<String> cardNos = new ArrayList<String>();
            IDataset cardIdTemp = new DatasetList(param.getString("CARD_NOS"));
            int cardTempSize = cardIdTemp.size();
            for (int i = 0; i < cardTempSize; i++)
            {
                cardNos.add(cardIdTemp.getData(i).getString("CARD_ID"));
            }
            reqData.setValueCardNos(cardNos);
        }
        reqData.setExchangeDatas(exchangeDatas);
        reqData.setHhCardId(param.getString("HH_CARD_ID"));
        reqData.setHhCardName(param.getString("HH_CARD_NAME"));
        if (StringUtils.isNotBlank(param.getString("OBJECT_SERIAL_NUMBER")))
        {
            reqData.setConvertUca(UcaDataFactory.getNormalUca(param.getString("OBJECT_SERIAL_NUMBER")));
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreExchangeRequestData();
    }

}
