
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEActiveMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEActiveReqData; 

public class BuildCPEActiveReqData extends BaseBuilder implements IBuilder
{

	/*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	CPEActiveReqData reqData = (CPEActiveReqData) brd;

        reqData.setRemark(param.getString("REMARK"));

        reqData.setxTag(param.getString("X_TAG"));

        reqData.setModifyTag(param.getString("MODIFY_TAG", ""));

        reqData.setIntfFlag(param.getString("INTF_FLAG", "0"));
        
        

        IDataset memberDatas = new DatasetList(param.getString("MEMBER_DATAS", "[]"));

        List<CPEActiveMemberData> memberlists = new ArrayList<CPEActiveMemberData>();

        if (IDataUtil.isNotEmpty(memberDatas))
        {

            for (int i = 0, size = memberDatas.size(); i < size; i++)
            {

                IData member = memberDatas.getData(i);

                CPEActiveMemberData memberData = new CPEActiveMemberData();

                UcaData ucaData = UcaDataFactory.getNormalUca(member.getString("SERIAL_NUMBER_B"));

                memberData.setUca(ucaData);

                memberData.setMemberSn(member.getString("SERIAL_NUMBER_B"));

                memberData.setModifyTag(member.getString("MODIFY_TAG"));
                
                memberData.setEquipNum(member.getString("EQU_NUM", "0"));
                memberData.setFirstTimeTag(member.getString("FIRSTTIME_TAG", "0"));

                memberlists.add(memberData);
            }
        }

        reqData.setMemberList(memberlists);
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new CPEActiveReqData();
    }

}
