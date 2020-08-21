
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 产品变更, 处理绑定的相关权益
 * @author: liwei29
 * @date: 2020-7-16
 */
public class DealChangeWelfareOfferAction implements ITradeAction
{
    protected static Logger logger = Logger.getLogger(DealChangeWelfareOfferAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        ProductTradeData productTradeData = btd.getRD().getUca().getUserMainProduct();
        // 判断是否存在主产品变更
        ChangeProductReqData req = (ChangeProductReqData) btd.getRD();
        String EndDate = "";
        if (req.getNewMainProduct() != null)
        {
            // 判断是否有预约变更，设置一下失效时间
            if (req.isBookingTag())
            {
                EndDate = req.getBookingDate();
            }
            else
            {
                EndDate = productTradeData.getEndDate();
            }
            String nowProductInstId = productTradeData.getInstId();

            // 查询是否有Q关联
            IDataset offerList = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferTypeAndInstId(WelfareConstants.RelType.WEFFARE.getValue(), nowProductInstId);
            if (IDataUtil.isNotEmpty(offerList))
            {
                // 循环，将user_offer_rel的记录逐条失效并找出关联的inst_id
                for (int i = 0; i < offerList.size(); i++)
                {
                    String instId = offerList.getData(i).getString("INST_ID");
                    String offerType = offerList.getData(i).getString("OFFER_TYPE");
                    String offerInst = offerList.getData(i).getString("OFFER_INS_ID");
                    String offerCode = offerList.getData(i).getString("OFFER_CODE");
                    OfferRelTradeData offerRels = uca.getOfferRelByRelUserIdAndInstId(instId);
                    offerRels.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    offerRels.setEndDate(EndDate);
                    btd.add(uca.getSerialNumber(), offerRels);
                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(offerType))
                    {
                        SvcTradeData userSvcTrade = uca.getUserSvcByInstId(offerInst);
                        userSvcTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        userSvcTrade.setEndDate(EndDate);
                        btd.add(uca.getSerialNumber(), userSvcTrade);
                    }
                    else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
                    {
                        DiscntTradeData userDiscntTrade = uca.getUserDiscntByInstId(offerInst);
                        userDiscntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        userDiscntTrade.setEndDate(EndDate);
                        btd.add(uca.getSerialNumber(), userDiscntTrade);
                    }
                    else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(offerType))
                    {
                        PlatSvcTradeData platSvcTrade = uca.getUserPlatSvcByInstId(offerInst);
                        // IDataset platInfos = UpcCall.queryPlatSvc(offerCode, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
                        IData data = new DataMap();
                        // IDataset ids = new DatasetList();
                        // data.put("SERVICE_ID",offerCode);
                        // data.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
                        // data.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
                        // data.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
                        // data.put("MODIFY_TAG", "1");
                        // data.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                        IData temp = platSvcTrade.toData();
                        List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();
                        temp.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                        PlatSvcData psd = new PlatSvcData(temp);
                        psd.setEndDate(EndDate);
                        pmds.add(psd);
                        ProductModuleCreator.createProductModuleTradeData(pmds, btd);
                        //
                        // ids.add(data);
                        // if(IDataUtil.isNotEmpty(ids)){
                        // IData input = new DataMap();
                        // input.put("SERIAL_NUMBER",serialNumber);
                        // input.put("USER_ID",userid);
                        // input.put("SELECTED_ELEMENTS",ids);
                        // input.put("NO_TRADE_LIMIT", "TRUE");
                        // input.put("SKIP_RULE", "TRUE");
                        // input.put("REMARK","权益商品平台服务退订");
                        // IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeReg", input);
                        //
                        // }
                    }

                }
            }

        }
    }
}
