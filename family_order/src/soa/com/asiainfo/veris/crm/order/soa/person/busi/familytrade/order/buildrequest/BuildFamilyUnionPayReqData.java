/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

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
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyUnionPayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.UnionPayMemberData;

/**
 * @CREATED by gongp@2014-5-22 修改历史 Revision 2014-5-22 上午09:27:18
 */
public class BuildFamilyUnionPayReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) brd;

        reqData.setRemark(param.getString("REMARK"));

        reqData.setxTag(param.getString("X_TAG"));

        reqData.setModifyTag(param.getString("MODIFY_TAG", ""));

        reqData.setIntfFlag(param.getString("INTF_FLAG", "0"));
        
        reqData.setEndDate_flag(param.getString("ENDDATE_FLAG", "0"));//k3

        reqData.setPayitemCode(param.getString("PAYITEM_CODE", ""));
        
        IDataset memberDatas = new DatasetList(param.getString("MEMBER_DATAS", "[]"));

        List<UnionPayMemberData> memberlists = new ArrayList<UnionPayMemberData>();

        if (IDataUtil.isNotEmpty(memberDatas))
        {

            for (int i = 0, size = memberDatas.size(); i < size; i++)
            {

                IData member = memberDatas.getData(i);

                UnionPayMemberData memberData = new UnionPayMemberData();

                UcaData ucaData = UcaDataFactory.getNormalUca(member.getString("SERIAL_NUMBER_B"));

                memberData.setUca(ucaData);

                memberData.setMemberSn(member.getString("SERIAL_NUMBER_B"));

                memberData.setModifyTag(member.getString("MODIFY_TAG"));

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
        return new FamilyUnionPayReqData();
    }

}
