package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SaleActiveFileSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author:
 * @date:
 */

public class GrpMemberFileSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 查询文件
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryGrpMemberFiles(IData input) throws Exception {
	    GrpMemberFileBean memberFileBean = (GrpMemberFileBean) BeanManager
				.createBean(GrpMemberFileBean.class);
		return memberFileBean.queryGrpMemberFiles(input, getPagination());
	}

	/**
	 * 根据FILE_ID删除表里的数据
	 * @param input
	 * @throws Exception
	 */
	public IDataset delGrpMebFileByFileId(IData input) throws Exception {
	    GrpMemberFileBean memberFileBean = (GrpMemberFileBean) BeanManager
                .createBean(GrpMemberFileBean.class);
        return memberFileBean.delGrpMebFileByFileId(input);
    }
	
}
