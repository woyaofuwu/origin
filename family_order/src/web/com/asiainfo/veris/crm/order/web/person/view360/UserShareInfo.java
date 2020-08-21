
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserShareInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 用户共享信息查询
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String msisdn = data.getString("SERIAL_NUMBER", "");
        String userId = data.getString("USER_ID", "");
        IDataset output = new DatasetList();
        IData result = new DataMap();
        output.add(result);
        IDataset tableMember = new DatasetList();
        boolean flag = false;
        if (StringUtils.isNotBlank(userId))
        {
            output = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryMemberAll", data);

            if (IDataUtil.isNotEmpty(output))
            {
                String shareId = output.getData(0).getString("SHARE_ID", "");
                for (int i = 0; i < output.size(); i++)
                {
                    IData out = output.getData(i);
                    String role = out.getString("ROLE_CODE", "");
                    if (role.equals("01"))
                    {
                        tableMember.add(out);
                    }

                    if (out.getString("USER_ID_B", "").equals(userId))
                    {
                        if (role.equals("02"))
                        {// 副卡用户
                            tableMember.add(out);
                        }
                    }

                    if (out.getString("USER_ID_B", "").equals(userId) && role.equals("01"))
                    {
                        flag = true;
                    }

                }

                if (flag)
                { // 主卡
                    setMembers(output);
                }
                else
                {
                    setMembers(tableMember);
                }

                IData discnt = new DataMap();
                discnt.put("USER_ID_A", shareId);
                discnt.put("SERIAL_NUMBER", msisdn);
                IDataset shareDiscntInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryShareDiscnt", discnt);
                setDiscnts(shareDiscntInfo);
                this.setAjax("SHOWTIP", "NO");
            }
            else
            {
                setTipInfo("该用户尚未办理共享业务");
                this.setAjax("SHOWTIP", "YES");
            }
        }
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setTipInfo("努力查询中！");
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setDiscnt(IData discnt);

    public abstract void setDiscnts(IDataset discnts);

    public abstract void setMember(IData member);

    public abstract void setMembers(IDataset members);

    public abstract void setTipInfo(String tipInfo);

}
