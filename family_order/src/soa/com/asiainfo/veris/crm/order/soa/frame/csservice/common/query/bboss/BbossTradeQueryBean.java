
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class BbossTradeQueryBean
{

    public IData get400SerialNumer(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IData result = UcaInfoQry.qryUserInfoBySn(serialNumber);
        return (result == null || result.size() == 0) ? null : result;
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchMebTradeInfo(String serialNumber, String routeId) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchMebTradeInfo(serialNumber, routeId);
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpMebTradeInfo(String serialNumber, String productOfferId, String routeId) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchpMebTradeInfo(serialNumber, productOfferId, routeId);
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpMebTradeInfoByUserId(String userId, String productOfferId, String routeId) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchpMebTradeInfoByUserId(userId, productOfferId, routeId);
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author jch
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpTradeInfo(IData param) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchpTradeInfo(param);
    }

    /**
     * @Description:根据产品订购关系编码获取集团产品台帐信息
     * @author jch
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpTradeInfoByProductOrder(IData param) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchpTradeInfoByProductOrder(param);
    }

    /**
     * @Description:根据产品用户的user_id 找产品的服务信息
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchpUserSvc(String userId, String productId, String userIdA, String routeId) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchpUserSvc(userId, productId, userIdA, routeId);

    }

    /**
     * @Description:获取集团商品台帐信息
     * @author jch
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradehInfo(IData param) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchTradehInfo(param);
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author jch
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradeInfo(IData param) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchTradeInfo(param);
    }

    /**
     * @Description:获取集团商品台帐信息
     * @author jch
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getMerchTradeInfoByOfferId(IData param) throws Exception
    {
        BbossTradeQueryDAO dao = new BbossTradeQueryDAO();
        return dao.getMerchTradeInfoByOfferId(param);
    }

    /**
     * @Description:执行台帐修改语句
     * @author hud
     * @date
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public void updateTradeInfo(String sql) throws Exception
    {
        // Dao.executeUpdate(sql);
    }
}
