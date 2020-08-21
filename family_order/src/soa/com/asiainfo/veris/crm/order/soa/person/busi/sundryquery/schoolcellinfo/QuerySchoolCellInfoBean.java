
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.schoolcellinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QuerySchoolCellInfoQry;

public class QuerySchoolCellInfoBean extends CSBizBean
{
    /**
     * 查询校园卡名称
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset querySchoolCards() throws Exception
    {

       // return QuerySchoolCellInfoQry.querySchoolCards();  查询校园卡名称
    	//调用产商品接口,by duhj
    	IDataset results=UpcCall.queryAllOffersByOfferIdWithDiscntTypeFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT,"10001005","R");
        if(IDataUtil.isNotEmpty(results)){
        	for(int i=0;i<results.size();i++){
				IData temp = results.getData(i);
				temp.put("DISCNT_CODE", temp.getString("OFFER_CODE"));
				temp.put("DISCNT_NAME", temp.getString("OFFER_NAME"));


        	}
        }
    return results;
    }

    /**
     * 功能：校园卡基站信息查询 作者：GongGuang
     */
    public IDataset querySchoolCellInfo(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();

        String discntCode = data.getString("DISCNT_CODE", "");
        String lac = data.getString("LAC", "");
        String cellId = data.getString("CELL_ID", "");
        IDataset dataSet = QuerySchoolCellInfoQry.querySchoolCellInfo(discntCode, lac, cellId, page);
        return dataSet;
    }
}
