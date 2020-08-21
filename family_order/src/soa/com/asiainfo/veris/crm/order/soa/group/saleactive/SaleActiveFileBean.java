package com.asiainfo.veris.crm.order.soa.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleActiveFileBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
	 * 查询文件
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset querySaleFileInfos(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_GROUP_FTPFILE", "SEL_SALEACTIVE_FILE_ALL", inparams, pagination);
    }
    
    /**
     * 根据FILE_ID删除表里的数据
     * @param fileId
     * @throws Exception
     */
    public IDataset delSaleFileByFileId(IData inparams) throws Exception
    {
        if(IDataUtil.isNotEmpty(inparams)){
            String fileId = inparams.getString("FILE_ID","");
            if(StringUtils.isNotBlank(fileId)){
                String fileIds[] = fileId.split(",");
                for(int i=0;i < fileIds.length; i++){
                    if(StringUtils.isNotBlank(fileIds[i])){
                        SaleActiveFileQry.delSaleFileByFileId(fileIds[i]); 
                    }
                }
            }
        }
        return new DatasetList();
    }
    
}
