
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

/**
 * 宽带续费
 * 
 * @CREATED by gongp@2013-10-10 修改历史 Revision 2013-10-10 上午10:05:10
 */
public class CttBroadBandContinuePaySVC extends CSBizService
{

    private static final long serialVersionUID = 7570431184545983304L;

    private final static transient Logger logger = Logger.getLogger(CttBroadBandContinuePaySVC.class);

    /**
     * 资费，服务，依赖 互斥判断
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset chkSelectedElements(IData data) throws Exception
    {
        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.dealSelectedElementsForChg(data);
    }

    /**
     * 一次性获得宽带续费的信息 if 不变更资费
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getBroadbandContinuePayInfo(IData param) throws Exception
    {
        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getBroadbandContinuePayInfo(param);
    }

    /**
     * 得到宽带包元素，根据PACKAGE_ID
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getBroadBandPackageElements(IData param) throws Exception
    {
        return ProductInfoQry.getBroadBandPackageElements(param.getString("PACKAGE_ID"), param.getString("EPARCHY_CODE"));
    }

    /**
     * 获得变更产品的费用
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getChangeProductFee(IData param) throws Exception
    {

        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getChangeProductFee(param);

    }

    /**
     * 计算一个资费的结束时间
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getElementEndDate(IData data) throws Exception
    {

        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getElementEndDate(data);

    }

    public IDataset getOldBroadBandInfo(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        IDataset addrInfoDataset = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        // IDataset addrInfoDataset = BroadBandInfoQry.queryBroadBandAddressInfo(param);
        if (IDataUtil.isEmpty(addrInfoDataset))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_13, param.getString("SERIAL_NUMBER"));
        }
        IData infos = addrInfoDataset.getData(0);
        String addrName = infos.getString("STAND_ADDRESS");
        String userId = infos.getString("USER_ID");
        IDataset accessActInfos = WidenetInfoQry.getUserWidenetActInfosByUserid(userId);
        // IDataset accessActInfos = BroadBandInfoQry.getBroadBandAccessAcctByUserId(param.getString("USER_ID"));
        if (IDataUtil.isEmpty(accessActInfos))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, param.getString("SERIAL_NUMBER"));
        }
        String accessAct = accessActInfos.getData(0).getString("ACCT_ID");
        String startDate = accessActInfos.getData(0).getString("START_DATE");
        String endDate = accessActInfos.getData(0).getString("END_DATE");
        String custId = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER")).getString("CUST_ID");
        String phone = UcaInfoQry.qryCustInfoByCustId(custId).getString("PHONE");
        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("ADDR_NAME", addrName);
        data.put("ACCESS_ACCT", accessAct);
        data.put("ACCESS_START_DATE", startDate.substring(0, 10));
        data.put("ACCESS_END_DATE", endDate.substring(0, 10));
        data.put("PHONE", phone);
        result.add(data);
        return result;
    }

    /**
     * 获得用户结余
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctBalanceInfo(IData param) throws Exception
    {

        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getUserAcctBalanceInfo(param);
    }

    /**
     * 获得用户资费
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserDiscnts(IData param) throws Exception
    {

        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getUserDiscnts(param);

    }

    /**
     * 获得用户服务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserSvcs(IData param) throws Exception
    {

        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);

        return bean.getUserSvcs(param);

    }

}
