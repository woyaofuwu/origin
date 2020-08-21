
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ModifyUserPswInfoSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    
    /**
     * 热线修改密码
     * @param OPR_NUMB(操作流水),CONTACT_ID(全网客户接触ID),TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE
     * 	OCC_PASSWD(旧密码),NCC_PASSWD(新密码),IDENT_CODE(用户身份凭证),IN_MODE_CODE
     * @return X_RESULTCODE,X_RESULTINFO
     * @author yf
     * @throws Exception
     */
    public IData changePswInfo4HL(IData input) throws Exception{
    	ModifyUserPswInfoBean bean = (ModifyUserPswInfoBean) BeanManager.createBean(ModifyUserPswInfoBean.class);

        return bean.changePswInfo4HL(input);
    }

    // 修改密码
    public IDataset changePswInfoForPhone(IData input) throws Exception
    {

        ModifyUserPswInfoBean bean = (ModifyUserPswInfoBean) BeanManager.createBean(ModifyUserPswInfoBean.class);

        return bean.changePswInfoForPhone(input);
    }

    // 证件校验
    public IDataset checkPspt(IData input) throws Exception
    {

        ModifyUserPswInfoBean bean = (ModifyUserPswInfoBean) BeanManager.createBean(ModifyUserPswInfoBean.class);

        return bean.checkPspt(input);
    }

    // 修改密码校验
    public IDataset checkPwd(IData input) throws Exception
    {

        ModifyUserPswInfoBean bean = (ModifyUserPswInfoBean) BeanManager.createBean(ModifyUserPswInfoBean.class);

        return bean.checkPwd(input);
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("SERIAL_NUMBER", "")))
            {
                return;
            }
            else if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (!"".equals(input.getString("MSISDN", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
            }
            else if (!"".equals(input.getString("ID_VALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE", ""));
                return;
            }
        }else if ("1".equals(this.getVisit().getInModeCode())){  //渠道 ：热线;网厅
        	if (!"".equals(input.getString("MSISDN", ""))){
        		input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
        	}
        }
    }

}
