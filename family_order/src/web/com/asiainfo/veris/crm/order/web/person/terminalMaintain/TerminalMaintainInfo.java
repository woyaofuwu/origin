/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.terminalMaintain;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * REQ201702200014关于在NGBOSS系统中开发终端维修查询界面的申请
 * 
 * @author zhuoyingzhi
 * @date 20170316
 * 
 */
public abstract class TerminalMaintainInfo extends PersonBasePage {
	static Logger logger = Logger.getLogger(TerminalMaintainInfo.class);

	public void terminalMaintainInfoQry(IRequestCycle cycle) throws Exception {

		IData data = this.getData("cond", true);
		Pagination page = getPagination("pageNav");

		IDataOutput result = CSViewCall.callPage(this, "SS.TerminalMaintainInfoSVC.terminalMaintainInfoQry", data, page);

		IDataset list = result.getData();
		if (list.size() <= 0) {
			// 无数据
			setAjax("DATA_COUNT", "0");
		} else {
			if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012")) {
				// 没有权限，需要模糊化处理客户名称
				for (int i = 0; i < list.size(); i++) {
					String custName = list.getData(i).getString("PERSONNAME", "");
					custName = maskName(custName);
					list.getData(i).put("PERSONNAME", custName);
				}
			}

			setInfos(list);
			setPageCount(list.size());
		}
		setCondition(data);

	}

	/**
	 * 
	 * 如果有三个以下包含三个，就从第一个开始模糊。 如果有三个以上的就从第三个开始模糊
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String maskName(String name) throws Exception {
		StringBuilder maskName = new StringBuilder();
		char character = '*';
		if (name != null) {
			name = name.trim();
		}
		if (name == null || name.length() == 0) {

		} else if (name.length() == 1 || name.length() == 2) {
			maskName.append(name.charAt(0));
			maskName.append(character);
		} else if (name.length() == 3) {
			maskName.append(name.charAt(0));
			for (int i = 0; i < name.length() - 1; i++) {
				maskName.append(character);
			}
		} else {
			maskName.append(name.charAt(0));
			maskName.append(name.charAt(1));
			for (int i = 0; i < name.length() - 2; i++) {
				maskName.append(character);
			}
		}
		return maskName.toString();
	}

	public abstract void setCondition(IData cond);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

	public abstract void setPageCount(long l);
}
