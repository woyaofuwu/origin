
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
public class QueryWidenetTransFeeBean extends CSBizBean
{

    /**
     * 校园宽带费用转移查询
     * 
     * @author chenzm
     * @param data
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryTransFee(IData data, Pagination page) throws Exception
    {

        String start_staff_id = data.getString("START_STAFF_ID");
        String end_staff_id = data.getString("END_STAFF_ID");
        String start_date = data.getString("START_DATE");
        String end_date = data.getString("END_DATE");
        String trans_type = data.getString("TRANS_TYPE");
        String campus_id = data.getString("CAMPUS_ID");
        IDataset userOtherInfos=UserOtherInfoQry.queryTransFee("CAMPUS_TRANS_FEE",start_staff_id, end_staff_id, start_date, end_date, trans_type, campus_id, page);

        for(int i=0, size =userOtherInfos.size(); i<size;i++){
        	IData userOtherInfo =userOtherInfos.getData(i);
        	String tradeId = userOtherInfo.getString("TRADE_ID");
        	IData tradeInfo=UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
            userOtherInfo.put("CUST_NAME", tradeInfo.getString("CUST_NAME",""));
            userOtherInfo.put("SERIAL_NUMBER", tradeInfo.getString("SERIAL_NUMBER",""));
            userOtherInfo.put("TRADE_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID",""));
       	
        }
        return userOtherInfos;
    }
}
