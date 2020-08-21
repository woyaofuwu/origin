package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.personlogin;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;

public abstract class HintInfo extends BizTempComponent {

	public void renderComponent(StringBuilder informalParametersBuilder,
			IMarkupWriter writer, IRequestCycle cycle) throws Exception {
		 boolean isAjax =this.isAjaxServiceReuqest(cycle);
		 if(isAjax){
			 getPage().addResBeforeBodyEnd("frame/login/person/HintInfo.js");
		 }else{
			  includeScript(writer, "frame/login/person/HintInfo.js");
		 }
		 
		 //初始化时不刷新
		 if(!isAjax){
			 return;
		 }
		
		  IData data = getPage().getData();
		  String action = data.getString("ACTION");
		  if(StringUtils.equals(action, "clear")){
			  setInfos(null);
			  return ;
		  }
		 
		  IData params = new DataMap(data.getString("HINT_INFO","{}")); 
		  if(DataUtils.isEmpty(params)){
			  return;
		  }
		  //原传入的参数名叫SERIAL_NUMBER
		  
		  IDataInput input = new DataInput(); 
		  input.getData().putAll(params);
		  IDataOutput output =ServiceFactory.call("CS.HintInfoSVC.getAllHintInfos", input);
		  IDataset infos = output.getData(); 
		/*  for(int i=0;i<infos.size();i++){
			  IData info = infos.getData(i);
			  String value = info.getString("VALUE");
			  if(StringUtils.isNotBlank(value)){
				  try{
					  String[] array = value.split("\\:");
					  if(array.length==2){
						  info.put("HINT_KEY", array[0]);
						  info.put("HINT_VALUE", array[1]);
					  }else{
						  info.put("HINT_VALUE", value);
					  }
					 
				  }catch(Exception e){
					  
				  }
				  
			  }
		  }*/
		  
		  setInfos(infos);
		 

		/*IDataset dataset = new DatasetList();
		String hintInfo1 = "开户日期:1998-03-19 08:33:23~~已经开户:5643天~~用户品牌:全球通测试~~当前产品:全球通奥运套餐~~";
		IData temp = new DataMap();
		temp.put("KEY", "HINT_INFO1");
		temp.put("VALUE", hintInfo1);
		dataset.add(temp);
		String hintInfo2 = "级别：金卡~~使用人：凡人修仙~~卡号：20130813~~客户经理：宋小兵~~客户经理联系电话：13755009543~~";
		IData temp2 = new DataMap();
		temp2.put("KEY", "HINT_INFO2");
		temp2.put("VALUE", hintInfo2);
		dataset.add(temp2);
		String strR8Msg = "用户目前【是】高级付费关系客户";
		IData tmpR10 = new DataMap();
		tmpR10.put("KEY", "ROAM_INFO10");
		tmpR10.put("VALUE", strR8Msg);
		dataset.add(tmpR10);
		setInfos(dataset);*/

	}

	public abstract void setInfos(IDataset infos);
}
