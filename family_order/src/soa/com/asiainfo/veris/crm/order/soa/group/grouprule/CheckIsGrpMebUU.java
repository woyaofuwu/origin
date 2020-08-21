
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckIsGrpMebUU extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        String strMebUserIdB = databus.getString("USER_ID_B", "");// 成员用户标识
        String productId = databus.getString("PRODUCT_ID", "");// 集团产品标识

        IDataset dataset = ParamInfoQry.getCommparaByCode1("CGM", "961", null, productId, "ZZZZ");
        if (IDataUtil.isEmpty(dataset))
        {
            return true;
        }
        // 查tf_f_user_product表
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        IDataset mebGrpInfos = null;
        
        //物联网产品
        if("20005013".equals(productId) || "20005014".equals(productId)
        || "20005015".equals(productId) || "20005016".equals(productId)){
            
            mebGrpInfos = UserProductInfoQry.getUserProductByUserIdEnd(strMebUserIdB, mebProductId);
            
        }else{
            mebGrpInfos = UserProductInfoQry.getUserProductByUserIdProductId(strMebUserIdB, mebProductId);
        }

        

        if (IDataUtil.isEmpty(mebGrpInfos))
        {
            return true;
        }

        String userIdA = mebGrpInfos.getData(0).getString("USER_ID_A");

        IData grpUserInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            return true;
        }

        String strCustId = grpUserInfo.getString("CUST_ID");

        IData custInfo = UcaInfoQry.qryGrpInfoByCustId(strCustId);
        if (IDataUtil.isEmpty(custInfo))
        {
            return true;
        }

        // 得到集团客户编码、名称
        String strGroupId = custInfo.getString("GROUP_ID");
        String strGroupName = custInfo.getString("CUST_NAME");
        String productName = UProductInfoQry.getProductNameByProductId(productId);
        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "1032", "业务受限提示：该客户已是【" + strGroupId + strGroupName + "】的成员。一个成员只能加入一个" + productName + "，不能再次加入！");

        return false;
    }

}
