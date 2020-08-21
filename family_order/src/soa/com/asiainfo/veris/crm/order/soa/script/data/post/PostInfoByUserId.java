
package com.asiainfo.veris.crm.order.soa.script.data.post;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;

public class PostInfoByUserId extends BreBase implements IBREDataPrepare
{

    private static Logger logger = Logger.getLogger(PostInfoByUserId.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 PostInfoByUserId() >>>>>>>>>>>>>>>>>>");

        /* 获取 TF_F_POSTINFO */
        String strUserId = databus.getDataset("TF_F_USER").getData(0).getString("USER_ID");

        IDataset ids = UserPostInfoQry.qryUserPostInfo(strUserId, "1");

        databus.put("TF_F_POSTINFO", ids);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 PostInfoByUserId() <<<<<<<<<<<<<<<<<<<");

    }

}
