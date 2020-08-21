
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MySharingTab extends PersonBasePage {

    /**
     * 客户资料综合查询 - 用户共享信息查询
     *
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String msisdn = data.getString("SERIAL_NUMBER", "");
        String userId = data.getString("USER_ID", "");
        IDataset output = new DatasetList();
        IData result = new DataMap();
        output.add(result);
        IDataset tableMember = new DatasetList();
        boolean flag = false;
        if (StringUtils.isNotBlank(userId)) {
            output = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryMemberAll", data);
            DataHelper.sort(output, "ROLE_CODE", IDataset.TYPE_INTEGER);

            if (IDataUtil.isNotEmpty(output)) {
                String shareId = output.getData(0).getString("SHARE_ID", "");
                for (int i = 0; i < output.size(); i++) {
                    IData out = output.getData(i);
                    String role = out.getString("ROLE_CODE", "");
                    
                    /**
                     * 紧急模糊化需求开发
                     * mengqx
                     * 20190619
                     * 副号做模糊，格式188****8787
                     */
                    String serialNumberB = out.getString("SERIAL_NUMBER", "");
                    if(serialNumberB!=null && !"".equals(serialNumberB)){
                    	serialNumberB = serialNumberB.substring(0, 3) + "****" + serialNumberB.substring(7, 11);
                    	out.put("SERIAL_NUMBER", serialNumberB);
                    }
                    //end mengqx
                    
                    if (role.equals("01")) {
                        tableMember.add(out);
                    }
                    if (out.getString("USER_ID_B", "").equals(userId)) {
                        if (role.equals("02")) { // 副卡用户
                            tableMember.add(out);
                        }
                    }
                    if (out.getString("USER_ID_B", "").equals(userId) && role.equals("01")) {
                        flag = true;
                    }

                }

                if (flag) { // 主卡
                    setMembers(output);
                } else {
                    setMembers(tableMember);
                }

                IData discnt = new DataMap();
                discnt.put("USER_ID_A", shareId);
                discnt.put("SERIAL_NUMBER", msisdn);
                IDataset shareDiscntInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryShareDiscnt", discnt);
                setDiscnts(shareDiscntInfo);
            }
        }
    }

    public abstract void setDiscnts(IDataset discnts);

    public abstract void setMembers(IDataset members);
}
