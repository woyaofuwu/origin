
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyTradeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.MemberData;

public class BuildFamilyTradeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyTradeReqData reqData = (FamilyTradeReqData) brd;
        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
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
                    String sn = meb.getString("SERIAL_NUMBER_B");
                    // 相同亲情号码校验，此处屏蔽，添加时已校验
                    /*
                     * for(int j = 0, length = mebList.size(); j < length; j++){ IData tempMeb = mebList.getData(j);
                     * String tempSn = tempMeb.getString("SERIAL_NUMBER_B"); if (sn.equals(tempSn) && i != j) {
                     * CSAppException.apperr(FamilyException.CRM_FAMILY_57); } }
                     */
                    IData tmp = UcaInfoQry.qryUserInfoBySn(sn);
                    if (IDataUtil.isEmpty(tmp))
                    {
                        // 异地或外网号码
                        mebUca = new UcaData();
                        UserTradeData user = new UserTradeData();
                        user.setUserId(SeqMgr.getUserId());
                        user.setSerialNumber(sn);

                        mebUca.setUser(user);
                    }
                    else
                    {
                        mebUca = UcaDataFactory.getNormalUca(sn);
                    }

                    MemberData mebData = new MemberData();
                    mebData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    mebData.setUca(mebUca);
                    reqData.addMemberData(mebData);
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
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyTradeReqData();
    }

}
