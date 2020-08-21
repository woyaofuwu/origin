
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEAreaLockReqData; 

public class BuildCPEAreaLockReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	CPEAreaLockReqData rd=(CPEAreaLockReqData) brd;
    	rd.setRemark(param.getString("REMARK"));
    	String cellId=param.getString("CELL_ID");
    	String cellName=param.getString("CELL_NAME");
    	rd.setCellId(cellId); 
    	rd.setCellName(cellName);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CPEAreaLockReqData();
    }

}
