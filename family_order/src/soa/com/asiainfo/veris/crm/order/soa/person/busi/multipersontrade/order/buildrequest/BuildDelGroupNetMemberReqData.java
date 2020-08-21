package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.DelGroupNetMemberReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;

public class BuildDelGroupNetMemberReqData extends BaseBuilder implements IBuilder {

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		DelGroupNetMemberReqData reqData = (DelGroupNetMemberReqData) brd;
		
		reqData.setCancellMeb(param.getString("CANCELL_MEB"));
		
		IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));

        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String serialNumberB = mebData.getString("SERIAL_NUMBER_B");
            UcaData mebUca = UcaDataFactory.getNormalUca(serialNumberB);

            GroupMemberData groupMeb = new GroupMemberData();
            groupMeb.setUca(mebUca);

            reqData.addMebUca(groupMeb);
        }
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new DelGroupNetMemberReqData();
	}

}
