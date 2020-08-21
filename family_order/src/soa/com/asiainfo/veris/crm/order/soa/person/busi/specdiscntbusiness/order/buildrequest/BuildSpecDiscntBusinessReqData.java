
package com.asiainfo.veris.crm.order.soa.person.busi.specdiscntbusiness.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.specdiscntbusiness.order.requestdata.SpecDiscntBusinessReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildSpecDiscntBusinessReqData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 28, 2014 11:08:36 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 28, 2014 maoke v1.0.0 修改原因
 */
public class BuildSpecDiscntBusinessReqData extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SpecDiscntBusinessReqData request = (SpecDiscntBusinessReqData) brd;

        IDataset discntList = new DatasetList(param.getString("DISCNT_LIST"));

        List<ProductModuleData> elements = new ArrayList<ProductModuleData>();

        if (IDataUtil.isNotEmpty(discntList))
        {
            for (int i = 0, size = discntList.size(); i < size; i++)
            {
                IData discnt = discntList.getData(i);

                String tag = discnt.getString("tag");
                String startDate = discnt.getString("START_DATE");
                String endDate = discnt.getString("END_DATE");
                String discntCode = discnt.getString("DISCNT_CODE");

                if (BofConst.MODIFY_TAG_ADD.equals(tag))
                {
                    DiscntData discntData = new DiscntData();

                    discntData.setProductId("-1");
                    discntData.setPackageId("-1");
                    discntData.setElementId(discntCode);
                    discntData.setStartDate(startDate);
                    discntData.setEndDate(endDate);
                    discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    discntData.setInstId(SeqMgr.getInstId());

                    elements.add(discntData);
                }
                if (BofConst.MODIFY_TAG_DEL.equals(tag))
                {
                    if (StaffPrivUtil.isDistPriv(CSBizBean.getVisit().getStaffId(), discntCode + "_DEL"))
                    {
                        UcaData uca = brd.getUca();

                        List<DiscntTradeData> userDiscnt = uca.getUserDiscntByDiscntId(discntCode);

                        DiscntData discntData = new DiscntData(userDiscnt.get(0).toData());

                        discntData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        discntData.setEndDate(endDate);

                        elements.add(discntData);
                    }
                    else
                    {
                        CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_4, discntCode);
                    }
                }

                request.setProductElements(elements);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_363);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new SpecDiscntBusinessReqData();
    }
}
