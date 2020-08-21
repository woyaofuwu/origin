
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;

/**
 * 校验是否有吉祥号码受理权限
 * 
 * @author Administrator
 */
public class CheckJXNumberRight extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {

        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验，
        {
            String acctTag = databus.getString("ACCT_TAG");
            if (StringUtils.equals("0", acctTag))
            {
                String serialNumber = databus.getString("SERIAL_NUMBER");
                IDataset numberInfo = ResCall.getMphonecodeInfo(serialNumber);// 查询号码信息
                if (IDataUtil.isNotEmpty(numberInfo))
                {
                    String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG");// BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
                    String classId= numberInfo.getData(0).getString("CLASS_ID","");
                    
                    /*
                     * 号码为吉祥号码
                     * 且号码的登记为：一等级：A、E、D、C、G、F、B，二等级三档：H、I、J
                     */
                    if ("1".equals(jxNumber)&&
                    		(classId.equals("A")||
                    				classId.equals("B")||
                    				classId.equals("C")||
                    				classId.equals("D")||
                    				classId.equals("E")||
                    				classId.equals("F")||
                    				classId.equals("G")||
                    				classId.equals("H")||
                    				classId.equals("I")||
                    				classId.equals("J")))
                    {// 是吉祥号码
                        // 工号是否属于自办营业厅
                        IDataset zbyytSet = CParamQry.getZBYYT(getVisit().getStaffId());// 这里查询应该走权限提供的接口

                        if (IDataUtil.isNotEmpty(zbyytSet))// 非自办营业厅
                        {
                            int recordcount = zbyytSet.getData(0).getInt("RECORDCOUNT", 0);
                            if (recordcount == 0)	// 非自办营业厅
                            {
                            	//
                            	
                                // 工号是否有“吉祥号码办理业务权限” 有数据就是有权限办理
                                boolean isJXRightPriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "isJXRight");
                                if (!isJXRightPriv)// 是吉祥号码、非自办营业厅操作员、且无操作吉祥号码权限则报错
                                {
                                    return true;
                                }
                            }
                        }else{
                        	return false;
                        }
                    }
                }
            }
        }
        return false;
    }

}
