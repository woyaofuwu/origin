
package com.asiainfo.veris.crm.order.soa.script.data.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForUser;

public class UserByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(UserByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UserByUserId() >>>>>>>>>>>>>>>>>>");

        String strUserId = databus.getString("USER_ID");

        IDataset ids = null;

        try
        {
            ids = IDataUtil.idToIds(UcaInfoQry.qryUserMainProdInfoByUserId(strUserId));
            // IDataset ids = BreQryForUser.getUserInfoByUserId(strUserId);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug(" >>>> >>>>>>>>>>>>>>>>>> 缓存中没有获取到 userInfo！");
        }

        if (ids == null || ids.size() == 0)
        {
            ids = BreQryForUser.getUserInfoByUserId(strUserId);
        }

        databus.put("TF_F_USER", ids);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 UserByUserId() <<<<<<<<<<<<<<<<<<<");

    }

}
