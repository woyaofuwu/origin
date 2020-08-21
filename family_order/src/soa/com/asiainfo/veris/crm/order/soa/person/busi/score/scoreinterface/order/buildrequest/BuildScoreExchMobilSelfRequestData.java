
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ItemInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreExchMobilSelfRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.StackPackageData;

public class BuildScoreExchMobilSelfRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ScoreExchMobilSelfRequestData reqData = (ScoreExchMobilSelfRequestData) brd;
        reqData.setOrderNo(param.getString("ORDER_ID"));
        reqData.setlMobile(param.getString("L_MOBILE"));
        if (StringUtils.isNotBlank(param.getString("TYPE")))
        {
            reqData.setType(param.getString("TYPE"));
        }
        reqData.setProvCode(param.getString("PROV_CODE"));
        reqData.setOrdOprTime(param.getString("ORD_OPR_TIME"));

        IDataset temp = new DatasetList(param.getString("STACK_PACKAGE"));
        StackPackageData data = null;
        List<StackPackageData> stackPackageDatas = new ArrayList<StackPackageData>();
        List<ItemInfoData> itemDatas = new ArrayList<ItemInfoData>();
        int tempSize = temp.size();
        for (int i = 0; i < tempSize; i++)
        {
            data = new StackPackageData();
            IDataset item = new DatasetList(temp.getData(i).getString("ITEM_INFO"));
            ItemInfoData itemData = null;
            for (int j = 0; j < item.size(); j++)
            {
                itemData = new ItemInfoData();
                IData itemTemp = new DataMap();
                itemTemp.putAll(item.getData(j));
                itemData.setItemId(itemTemp.getString("ITEM_ID"));
                itemData.setBusiType(itemTemp.getString("BUSI_TYPE"));
                itemData.setItemName(itemTemp.getString("ITEM_NAME"));
                itemData.setItemType(itemTemp.getString("ITEM_TYPE"));
                itemData.setStatus(itemTemp.getString("STATUS"));
                itemData.setSubOrderId(itemTemp.getString("SUB_ORDER_ID"));
                itemData.setItemPayCash(itemTemp.getString("ITEM_PAY_CASH"));
                if (StringUtils.isNotBlank(itemTemp.getString("TYPE")))
                {
                    itemData.setType(itemTemp.getString("TYPE"));
                }
                itemDatas.add(itemData);
            }
            data.setStoreId(temp.getData(i).getString("STORE_ID"));
            data.setPagDeliverFee(temp.getData(i).getString("PAG_DELIVER_FEE"));
            data.setPagProductFee(temp.getData(i).getString("PAG_PRODUCT_FEE"));
            data.setItemInfo(itemDatas);
            stackPackageDatas.add(data);
        }
        reqData.setStackPackage(stackPackageDatas);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new ScoreExchMobilSelfRequestData();
    }

}
