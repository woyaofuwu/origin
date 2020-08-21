
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.TradeActionConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share.CalElementDateShare;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share.IElementCalDateAction;

public class ProductModuleCalDateExtend
{
    public static void calElementDate(ProductModuleData pmd, ProductTimeEnv env, UcaData uca, List<ProductModuleData> pmds) throws Exception
    {
        CalElementDateShare share = SessionManager.getInstance().getShareObject(CalElementDateShare.class);
        List<IElementCalDateAction> actions = share.getActions();
        for (IElementCalDateAction action : actions)
        {
            action.calElementDate(pmd, env, uca, pmds);
        }
    }

    public static void calElementDate(String tradeTypeCode, IDataset elements, ProductTimeEnv env, UcaData uca, List<ProductModuleData> pmds) throws Exception
    {
        if (IDataUtil.isNotEmpty(elements))
        {
            if (StringUtils.isBlank(DataBusManager.getDataBus().getAcceptTime()))
            {
                DataBusManager.getDataBus().setAcceptTime(SysDateMgr.getSysTime());
            }
            loadCalElementActions(tradeTypeCode);
            int size = elements.size();
            for (int i = 0; i < size; i++)
            {
                IData element = elements.getData(i);
                ProductModuleData pmd = null;
                String elementType = element.getString("ELEMENT_TYPE_CODE");
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType))
                {
                    pmd = new DiscntData(element);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementType))
                {
                    pmd = new SvcData(element);
                }

                calElementDate(pmd, env, uca, pmds);
                if (StringUtils.isNotBlank(pmd.getStartDate()))
                {
                    element.put("START_DATE", pmd.getStartDate());
                }
                if (StringUtils.isNotBlank(pmd.getEndDate()))
                {
                    element.put("END_DATE", pmd.getEndDate());
                }
            }
        }
    }

    public static void loadCalElementActions(String tradeTypeCode) throws Exception
    {
        List<IElementCalDateAction> actions = TradeActionConfig.getElementCalDateAction(tradeTypeCode);
        CalElementDateShare share = SessionManager.getInstance().getShareObject(CalElementDateShare.class);
        share.clean();
        share.addAll(actions);
    }
}
