
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoSVC;

/**
 * 校验是否是黑名单号码
 * 
 * @author Administrator
 */
public class CheckBlackNumber extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {

        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 0查询时校验，1提交校验
        	
        {
        	 IData reqData = databus.getData("REQDATA");// 请求的数据
            // 校验服务
            IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
            System.out.println("rrrrrrrrrdddddddaaaaaa"+selectedElements);
            if (IDataUtil.isNotEmpty(selectedElements))
            {
            	System.out.println("wwwwwwwwwwwwwwwwwwwwww");
            	
                for (int i = 0, size = selectedElements.size(); i < size; i++)
                {
                    IData element = selectedElements.getData(i);

                    String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                    //服务编码
                    String elementId = element.getString("ELEMENT_ID");
                    String modifyTag = element.getString("MODIFY_TAG");
                    System.out.println("rrrrrrrrraaaaaa");
                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)&&"190".equals(elementId))
                    {
                    	System.out.println("rrrrrrrrrrrrr"+elementId);
                    	IData data = new DataMap();
                    	String serialNumber = databus.getString("SERIAL_NUMBER");
                        data.put("SERIAL_NUMBER", serialNumber);
                        IData dataNum = QueryInfoSVC.queryBlack(data);
                        if(DataUtils.isNotEmpty(dataNum)){
                        	return true;
                        }
//                        
                    }
                }
            }
        	
        }
        
        return false;
    }

}
