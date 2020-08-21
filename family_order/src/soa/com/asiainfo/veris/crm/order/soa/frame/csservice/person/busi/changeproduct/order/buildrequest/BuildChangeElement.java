
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.SpecialChangeProductReqData;

public class BuildChangeElement extends BuildChangeProduct implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	SpecialChangeProductReqData request = (SpecialChangeProductReqData) brd;

        super.buildBusiRequestData(param, brd);
        
        if (StringUtils.isNotBlank(param.getString("GROUP_ID")))
        {
            request.setGroupID(param.getString("GROUP_ID"));
        }
        
        if (StringUtils.isNotBlank(param.getString("OUT_ORDER_ID")))
        {
            request.setOutOrderId(param.getString("OUT_ORDER_ID"));
        }
        
        if (StringUtils.isNotBlank(param.getString("FLOW_ITEM")))
        {
            request.setFlowItems(param.getString("FLOW_ITEM"));
        }
    }

   

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SpecialChangeProductReqData();
    }
}
