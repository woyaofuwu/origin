
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckCancelG010Colorring extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckCancelG010Colorring.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBlackList() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String err = "";
        String mebUserId = databus.getString("USER_ID_B", "");
        String mebBrandCode = databus.getString("BRAND_CODE_B", ""); // 个人的品牌
        String mebProductId = databus.getString("PRODUCT_ID_B", ""); // 个人的产品
        // 1. 询问是否取消动感地带赠送的个人彩铃 start
        // lixiuyu@20100720 用户要求动感地带套餐办理集团彩铃注销如果有集团个人彩铃时提示
        if ("G010".equals(mebBrandCode))
        {

            IDataset defaultDataset = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", mebProductId, null, "1");
            if (IDataUtil.isNotEmpty(defaultDataset))
            {
                for (int i = 0; i < defaultDataset.size(); i++)
                {
                    IData defaultdata = defaultDataset.getData(i);
                    String elementId = defaultdata.getString("OFFER_CODE", "");
                    String elementType = defaultdata.getString("OFFER_TYPE", "");
                    if ("20".equals(elementId) && "S".equals(elementType))
                    {

                        IDataset svcInfos = UserSvcInfoQry.queryUserSvcByUseridSvcid(mebUserId, "20");
                        if (IDataUtil.isNotEmpty(svcInfos) && svcInfos.size() == 1)
                        {
                            IData svcInfo = svcInfos.getData(0);
                            if (!svcInfo.getString("USER_ID_A", "").equals("-1"))
                            {
                                IData tempdata = new DataMap();

                                tempdata = UProductInfoQry.qryProductByPK(mebProductId);

                                String productName = tempdata.getString("PRODUCT_NAME", "");

                                err = "您已办理动感地带" + productName + "，该套餐含有免费赠送的个人彩铃，由于您开通集团彩铃之前未开通个人彩铃， 如现在取消集团彩铃，您已开通的个人彩铃及相关铃音库也会一并取消，请确认是否取消，确认取消后务必在【产品变更】界面重新办理个人彩铃。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, errCode, err);
                                bResult = false;
                                break;
                            }

                        }
                    }

                }
            }
        }
        // 1. 询问是否取消动感地带赠送的个人彩铃 end

        // 2. 询问是否联系客户经理取消 start
        IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(mebUserId, "DLMR");
        if (IDataUtil.isNotEmpty(otherInfos))
        {
            err = "不可来电取消业务，如需取消该成员集团彩铃，请联系客户经理取消，是否继续？";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, errCode, err);
            bResult = false;
        }
        // 2. 询问是否联系客户经理取消 end

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CheckCancelG010Colorring() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
