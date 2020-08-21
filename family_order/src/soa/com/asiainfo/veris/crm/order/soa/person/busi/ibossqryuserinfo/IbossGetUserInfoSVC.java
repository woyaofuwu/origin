
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class IbossGetUserInfoSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7555187242959492357L;

    /**
     * 一级BOSS落地方客户资料查询
     * 
     * @param pd
     * @param inparams
     *            KIND_ID IDTYPE IDVALUE USER_PASSWD IDCARDTYPE IDCARDNUM TYPEIDSET START_DATE END_DATE
     * @return IDataset
     * @throws Exception
     * @author huangwei
     */
    public IDataset getUserInfo(IData data) throws Exception
    {

        IbossGetUserInfoBean bean = BeanManager.createBean(IbossGetUserInfoBean.class);
        try
        {
            // 一级BOSS客户资料查询,路由特殊处理
            if ("01".equals(data.getString("IDTYPE")))// 手机号码
            {
                if (data.getString("IDVALUE").length() > 30)
                {
                    data.put("IDVALUE", data.getString("IDVALUE").substring(0, 30));
                }
                data.put("SERIAL_NUMBER", data.getString("IDVALUE"));
            }
            else if ("04".equals(data.getString("IDTYPE")))// VIP卡号
            {
                // 获取大客户信息,如果存在,用户归属该地址
                IData idata = new DataMap();
                idata.clear();
                idata.put("REMOVE_TAG", "0");
                idata.put("VIP_NO", data.getString("IDVALUE"));
                IDataset users = bean.getVipsInfo(idata);
                if (users == null || users.size() <= 0)
                {
                    // IData res = new DataMap();
                    // res.put("X_RESULTCODE", 0);
                    // res.put("X_RSPTYPE", "2");
                    // res.put("X_RSPCODE", "2107");
                    // res.put("X_RSPDESC", "手机号对应的用户不存在");
                    // res.put("X_RESULTINFO", "用户资料不存在");
                    // return res.toDataset();
                    CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2107);
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            if ("01".equals(data.getString("IDTYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2107);
            }
            else if ("04".equals(data.getString("IDTYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2108);
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2109);
            }
        }
        return bean.getUserInfo(data);
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("SS.IbossGetUserInfoSVC.getUserInfo".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        }
    }

}
