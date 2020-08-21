/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneTradeReqData;

/**
 * @CREATED by gongp@2014-5-4 修改历史 Revision 2014-5-4 上午11:17:44
 */
public class BuildFilteIncomePhoneTradeReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) brd;

        String dealDataStr = param.getString("SN_DATASET");

        if (!StringUtils.isBlank(dealDataStr) && dealDataStr.length() > 2)
        {

            IDataset dataset = new DatasetList(dealDataStr);

            IData temp = dataset.getData(0);

            reqData.setRejectSn(temp.getString("REJECT_SN"));

            reqData.setRemark(temp.getString("REMARK"));
        }
        else
        {
            reqData.setRejectSn(param.getString("REJECT_SN"));

            reqData.setRemark(param.getString("REMARK"));
        }

        if ("1".equals(param.getString("OPEN_SMS")))
        {
            reqData.setIsOpenSms("1");
        }
        else
        {
            reqData.setIsOpenSms("0");
        }

        reqData.setOperType(param.getString("OPER_TYPE"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new FilteIncomePhoneTradeReqData();
    }

}
