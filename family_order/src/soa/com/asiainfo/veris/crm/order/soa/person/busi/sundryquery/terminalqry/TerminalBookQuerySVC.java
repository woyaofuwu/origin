
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.terminalqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class TerminalBookQuerySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset queryTerminalBooks(IData param) throws Exception
    {
    	IDataset result = new DatasetList();
        if (StringUtils.isEmpty(param.getString("PROCESS_TAG")))
        {
        	IDataset userSaleActiveList = UserSaleActiveInfoQry.qryTERMINALORDERbyParams(param.getString("SERIAL_NUMBER"), param.getString("PROCESS_TAG"), param.getString("START_DATE"), param.getString("END_DATE"), getPagination());
            //不能直接关联查询产商品，需调接口查询
        	if(DataSetUtils.isNotBlank(userSaleActiveList)){
        		for(int i = 0 ; i < userSaleActiveList.size() ; i++){
        			String packageId = userSaleActiveList.getData(i).getString("USE_PACKAGE_ID","");//营销活动包ID
        			//需根据productId packageID 调产商品接口查询名称
        			IData packageData = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "");
        			if(IDataUtil.isNotEmpty(packageData)){
            			userSaleActiveList.getData(i).put("USE_PACKAGE_ID", packageData.getString("OFFER_NAME", ""));
        			}
        		}
        	}
        	result.addAll(userSaleActiveList);
        	return result;
        
        }
        else
        {
        	IDataset userSaleActiveList = UserSaleActiveInfoQry.qryTERMINALORDERbyParams2(param.getString("SERIAL_NUMBER"), param.getString("PROCESS_TAG"), param.getString("START_DATE"), param.getString("END_DATE"), getPagination());
            //不能直接关联查询产商品，需调接口查询
        	if(DataSetUtils.isNotBlank(userSaleActiveList)){
        		for(int i = 0 ; i < userSaleActiveList.size() ; i++){
//        			String productId = userSaleActiveList.getData(i).getString("USE_PRODUCT_ID","");//营销活动ID
        			String packageId = userSaleActiveList.getData(i).getString("USE_PACKAGE_ID","");//营销活动包ID
        			//需根据productId packageID 调产商品接口查询名称
        			IData packageData = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "");
        			if(IDataUtil.isNotEmpty(packageData)){
            			userSaleActiveList.getData(i).put("USE_PACKAGE_ID", packageData.getString("OFFER_NAME", ""));
        			}
        		}
        	}
        	result.addAll(userSaleActiveList);
        	return result;
        }
    }
}
