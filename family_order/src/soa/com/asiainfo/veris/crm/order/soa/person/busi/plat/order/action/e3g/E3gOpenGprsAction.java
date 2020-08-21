
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.e3g;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

/**
 * 3g上网本绑定时，默认开通gprs服务
 * 
 * @author bobo
 */
public class E3gOpenGprsAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        if (!PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {
            return;
        }

        List<SvcTradeData> svcList = uca.getUserSvcBySvcId("22");
        if (svcList == null || svcList.isEmpty())
        {
            IDataset pkgList = PkgElemInfoQry.getPackageIdByElementIdAndProductId("22", "S", uca.getProductId());
            if (pkgList != null && !pkgList.isEmpty())
            {
                IData pkg = pkgList.getData(0);
                SvcTradeData svcTrade = new SvcTradeData();
                svcTrade.setElementId("22");
                svcTrade.setElementType("S");
                svcTrade.setEndDate(pstd.getEndDate());
                svcTrade.setInstId(SeqMgr.getInstId());
                svcTrade.setMainTag(pkg.getString("MAIN_TAG"));
                svcTrade.setModifyTag("0");
                svcTrade.setPackageId(PlatConstants.PACKAGE_ID);
                svcTrade.setProductId(uca.getProductId());
                svcTrade.setRemark("3g上网本绑定，默认开通gprs服务");
                svcTrade.setUserId(uca.getUserId());
                svcTrade.setUserIdA("-1");
                svcTrade.setStartDate(pstd.getStartDate());
                btd.add(uca.getSerialNumber(), svcTrade);
            }
            else
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "未找到或找到不止一条gprs服务产品id包id记录！");
            }
        }
        else
        {
            SvcTradeData svcTrade = svcList.get(0);
            if (svcTrade.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
            {
                svcTrade.setMainTag(BofConst.MODIFY_TAG_UPD);
                svcTrade.setRemark("3g上网本绑定，默认开通gprs服务");
            }
        }
    }

}
