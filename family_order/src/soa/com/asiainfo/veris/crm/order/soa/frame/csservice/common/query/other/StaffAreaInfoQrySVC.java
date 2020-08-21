package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class StaffAreaInfoQrySVC extends CSBizService {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 根据用户编码查询Other表资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getStaffAreaInfoByID(IData input) throws Exception {
        String staffID = input.getString("STAFF_ID");
        IDataset dataset = StaffInfoQry.getStaffAreaInfoByID(staffID);

        return dataset;
    }

    /**
     * 根据用户编码查询Other表资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getStaffRoleInfoByID(IData input) throws Exception {
        IDataset retDataset = new DatasetList();
        IData retData = new DataMap();
        String staffID = input.getString("STAFF_ID", "");
        StringBuilder roleCodeBuilder = new StringBuilder();
        StringBuilder roleNameBuilder = new StringBuilder();
        IDataset dataset = StaffInfoQry.getStaffRoleInfoByID(staffID);
        if (dataset != null && IDataUtil.isNotEmpty(dataset) && dataset.size() > 0) {
            for (int i = 0; i < dataset.size(); i++) {
                IData roleData = dataset.getData(i);
                String roleCode = roleData.getString("ROLE_CODE", "");
                String roleName = roleData.getString("ROLE_NAME", "");
                if (!"".equals(roleCodeBuilder.toString())) {
                    roleCodeBuilder.append(",").append(roleCode);
                } else {
                    roleCodeBuilder.append(roleCode);
                }

                if (!"".equals(roleNameBuilder.toString())) {
                    roleNameBuilder.append(",").append(roleName);
                } else {
                    roleNameBuilder.append(roleName);
                }
            }
        }
        retData.put("ROLE_ID", roleCodeBuilder.toString());
        retData.put("ROLE_NAME", roleNameBuilder.toString());
        retDataset.add(retData);
        return retDataset;
    }

}
