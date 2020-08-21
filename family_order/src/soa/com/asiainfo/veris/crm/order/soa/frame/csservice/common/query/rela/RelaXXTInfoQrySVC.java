
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RelaXXTInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询校讯通学生参数信息
     *
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryStuDisParamInfoBySnAUserIdASnBDsiType(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String userId = param.getString("EC_USER_ID");
        String serialNumberB = param.getString("SERIAL_NUMBER_B");
        String elementTypeCode = param.getString("ELEMENT_TYPE_CODE");
        return RelaXxtInfoQry.qryStuDisParamInfoBySnAUserIdASnBDsiType(serialNumber, userId, serialNumberB, elementTypeCode);
    }

    /**
     * 查询同一集团 同一个付费号下所有代付号码
     *
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryMemInfoBySNandUserIdA(IData param) throws Exception
    {
        String userId = param.getString("EC_USER_ID");
        String serialNumber = param.getString("SERIAL_NUMBER");
        return RelaXxtInfoQry.queryMemInfoBySNandUserIdA(serialNumber, userId);
    }
    /**
     * 查询学护卡家长号码订购校讯通产品的信息
     * 用户学护卡
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryMemInfoBySNandUserIdA(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        return RelaXxtInfoQry.qryMemInfoBySNandUserIdA(serialNumber);
    }

    /**
     * 根据SERIAL_NUMBER_A或者SERIAL_NUMBER_B查询TF_F_RELATION_XXT
     *
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryXxtInfoBySnaOrSnb(IData param) throws Exception
    {
        return RelaXxtInfoQry.queryXxtInfoBySnaOrSnb(param);
    }

    /**
     * 根据SERIAL_NUMBER_A或者SERIAL_NUMBER_B查询TF_F_RELATION_XXT
     *
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryXxtInfoBySnaGroup(IData param) throws Exception
    {
        return RelaXxtInfoQry.queryXxtInfoBySnaGroup(param);
    }

    public IDataset qrymsisdn(IData param) throws Exception
    {
        return RelaXxtInfoQry.qrymsisdn(param);
    }

    public IDataset qryMemInfoBySNForUIP(IData param) throws Exception {
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	return RelaXxtInfoQry.qryMemInfoBySNForUIP(serialNumber);
    }

    public IDataset qryMemInfoBySNForUIPDestroy(IData param) throws Exception {
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	String ecUserId = param.getString("USER_ID_A");
    	return RelaXxtInfoQry.qryMemInfoBySNForUIPDestroy(serialNumber,ecUserId);
    }
    
    /*
     * 根据SERIAL_NUMBER_A查询TF_F_RELATION_XXT 校讯通异网号码互查 界面使用
     */
    public IDataset queryXxtInfoBySnGroup(IData param) throws Exception
    {
        return RelaXxtInfoQry.queryXxtInfoBySnGroup(param);
    }

}
