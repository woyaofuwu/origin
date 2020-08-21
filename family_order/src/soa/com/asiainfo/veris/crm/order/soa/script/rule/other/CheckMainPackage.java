
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;
import org.apache.log4j.Logger;

public class CheckMainPackage extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(CheckMainPackage.class);

    /**
     * 
     * @Description: 校验配置在commpara表1303中的活动（需要询问用户是否变更主套餐的活动）
     * @author: xiaocl
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMainPackage() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String actionType = databus.getString("ACTION_TYPE");
        // 获取当前用户的主套餐编码
        String userProductId = databus.getString("USER_PRODUCT_ID");
        String orderTypeCode = databus.getString("ORDER_TYPE_CODE","240");
        String userID = databus.getString("USER_ID","");
        logger.debug("CheckMainPackage-----userProductId"+userProductId);
        logger.debug("CheckMainPackage-----actionType"+actionType);
        logger.debug("CheckMainPackage-----userID"+userID);


        if ("checkSaleActiveTrade".equals(actionType) && "240".equals(orderTypeCode))
        {// 指定是营销活动受理界面——提交前校验
            String productId= databus.getString("PRODUCT_ID", "");
            String packageId= databus.getString("PACKAGE_ID", "");
            // 根据配置表commparam1303 判断是否需要进行 “变更主产品”
            IDataset active = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1303", productId, packageId);
            logger.debug("CheckMainPackage-----active"+active);
            if (IDataUtil.isNotEmpty(active)){// 该营销产品下的营销包均提示：是否变更主产品？
                // 获取即将变更的主产品编码
                String param_code2 = active.first().getString("PARA_CODE2","");
                // 根据用户id、及指定要办理的主套餐编码，查找该用户是否已预约办理该主套餐
                boolean isTargit = BreQry.qryUserNextMianProductInfo(param_code2,userID);
                logger.debug("CheckMainPackage-----isTargit"+isTargit);
                if(isTargit){// 用户有存在目标主产品的生效记录: 可能是预约的主产品、也可能是当前生效的主产品
                        ;
                }else {// 用户生效的主套餐不是目标主套餐，返回true提示用户是否进行产品变更
                    bResult = true;
                }
            }else {// 不需要进行“一键办理”的营销活动，不需要进行提示
                bResult = false;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckMainPackage() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
