
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.MemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.ShareMealReqData;

public class BuildShareMealReqData extends BaseBuilder implements IBuilder
{

    // @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ShareMealReqData reqData = (ShareMealReqData) brd;
        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
        reqData.setMemberCancel(param.getString("MEMBER_CANCEL", "0"));
        int nMemberAddCount = 0;
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData meb = mebList.getData(i);
                String tag = meb.getString("tag");
                if ("0".equals(tag))
                {
                    // 新增
                    UcaData mebUca = null;
                    String sn = meb.getString("SERIAL_NUMBER");
                    mebUca = UcaDataFactory.getNormalUca(sn);

                    MemberData mebData = new MemberData();
                    mebData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    mebData.setUca(mebUca);
                    reqData.addMemberData(mebData);
                    nMemberAddCount++;
                }
                else if ("1".equals(tag))
                {
                    // 删除
                    MemberData mebData = new MemberData();
                    mebData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    mebData.setInstId(meb.getString("INST_ID"));
                    reqData.addMemberData(mebData);
                }
            }
        }
        param.put("MemberAddCount", nMemberAddCount);
    }

    // @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ShareMealReqData();
    }

}
