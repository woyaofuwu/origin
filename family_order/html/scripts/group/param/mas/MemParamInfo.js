//用来初始化页面的显示
function loadMebSvcParamInfo() 
{
	var userIda="";
	var userId="";
	var groupId="";
	var serviceId="";
	var svcparamvalue ="";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var mebEparchyCode=$("#MEB_EPARCHY_CODE").val();  
	
	try
	  {
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	  if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	  {
	  	  	svcparamvalue="";
	  	  }
	  }
	  catch(e)
	  {
		  svcparamvalue="";
	  }
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		if(pairName=="ELEMENT_ID")
		{
		  serviceId=pairValue;
		}
	}
	
	if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
		 userIda=$.getSrcWindow().$("#GRP_USER_ID").val();
		 userId=$.getSrcWindow().$("#MEB_USER_ID").val();
		 groupId=$.getSrcWindow().$("#GROUP_ID").val();
		 if(userIda==undefined)	userIda="";
	  	 if(userId==undefined) userId="";
	  	 if(groupId==undefined) groupId="";
		var param ='&SERVICE_ID='+serviceId+'&USER_ID='+userId+'&USER_ID_A='+userIda+'&GROUP_ID='+groupId+'&MEB_EPARCHY_CODE='+mebEparchyCode;
		$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){
		putpagedata(data,serviceId);
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
		
	}
	else //已经存在值 但还不确定是不是就是详细的参数信息
	{
		var datasetsize=svcparamvalue.getCount();
		if(datasetsize<=1)//表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验,第二条记录才是具体的参数信息)
		{
			 userIda=$.getSrcWindow().$("#GRP_USER_ID").val();
			 userId=$.getSrcWindow().$("#MEB_USER_ID").val();
			 groupId=$.getSrcWindow().$("#GROUP_ID").val();
			 
			 if(userIda==undefined)	userIda="";
		  	 if(userId==undefined) userId="";
		  	 if(groupId==undefined) groupId="";
		  	 
			var param ='&SERVICE_ID='+serviceId+'&USER_ID='+userId+'&USER_ID_A='+userIda+'&GROUP_ID='+groupId+'&MEB_EPARCHY_CODE='+mebEparchyCode;
			
			$.ajax.submit('this', 'getServiceParamsByajax', param,null, function(data){
			putpagedata(data,serviceId);
			$.endPageLoading();
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}
		else
		{
			putpagedata(svcparamvalue,serviceId);
		}
	}
		
}



//从url取关键值,并根据关键值从父页面取值
function putpagedata(dataset,serviceId)
{
     	
     //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  var paramVerifySucc=dataset.get(0);
	  $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面
	 
	  var paramMap=dataset.get(1);//改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
	  var platsvcdata=paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
	   //将platsvcdata参数 填充到PlatSvcParamForm
	  platsvcdata.eachKey(
	 	 function(key)
	 	 {
	 	 	   if(key=="pam_BIZ_IN_CODE_A")
	     		{
	     			$("#pam_ACCESS_MODE").val(platsvcdata.get(key,""));
	     		}
	     	try
	     	{   
	     		var tempElement = $("#"+key);
	     		if(tempElement != "undefined" && tempElement != "" && tempElement != null){
	     			tempElement.val(platsvcdata.get(key,""));
	     		} 
	
	     	}
	     	catch(e)
	     	{
	     	}
	     }
	    );
	    
	dealOperStateoptions(serviceId);
	
	// 变更时不能变服务参数信息，显示回返按钮
	if($('#pam_MODIFY_TAG').val()=='2')
	{
	   	$('#OPER_STATE_TR').css("display","none");
		$('#add_Button').css("display","none");
		$('#mod_Button').css("display","block");
		
	}
}

function dealOperStateoptions(serviceId)
{  
 	var modifytag=$("#pam_MODIFY_TAG").val();
 	var bizattr=$("#pam_BIZ_ATTR").val();
 	
 	$('#pam_OPER_STATE').html("");
 	$('#pam_OPER_STATE option').length = 0;  //清空操作状态选项 
 	if("0"==modifytag)//新增时
 	{ 
 		jsAddItemToSelect($("#pam_OPER_STATE"), '新增', '01');
 		$("#pam_OPER_STATE").val('01');
 	}
 	else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }
 	  if (platsyncState == "P")
 	  {
 	  	jsAddItemToSelect($("#pam_OPER_STATE"), '恢复', '05'); 
 	  	$("#pam_OPER_STATE").val('05');
 	  }
 	  else
 	  {
 	  	jsAddItemToSelect($('#pam_OPER_STATE'), '暂停', '04');
 	    jsAddItemToSelect($('#pam_OPER_STATE'), '变更', '08');
 	  }
 	} 
 	
 	setStatetype();		
}

/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	04- 暂停 05-恢复 ,08-变更
 */
function setStatetype(){
    var operState=$("#pam_OPER_STATE").val();
    tableDisabled("platsvcparamtable",true);
    if (operState == "08"||operState == "04")
    {
	  $("#pam_OPER_STATE").attr('disabled',false);
	  //MessageBox.alert("","您确定要暂停成员此业务吗？否则请按底页的[取消]按钮!");
    } 

}


//点确定按钮返回数据到父页面隐藏字段
function setData(obj)
{
	if(!$.validate.verifyAll("memParamsInfoForm"))
	{
		return;
	}
	if($("#pam_MODIFY_TAG").val()=="0"&&$("#pam_GRP_PLAT_SYNC_STATE").val()=="P")
	{
		MessageBox.alert("",'该业务集团订购为暂停状态,暂时不能加入成员 \n点击 取消 按钮返回主界面');
		return false;
	}
	var itemIndex= "";
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1];

		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		} 
	}
	$("#CANCLE_FLAG").val('false');
	tableDisabled("platsvcparamtable",false);
	var paramset = $.DatasetList();
	var svcparamdata=$.DataMap();
	
	var platsvcparam=$.ajax.buildJsonData("memParamsInfoForm","pam");
	var platsvcdata=$.DataMap(platsvcparam);
	
	svcparamdata.put("PLATSVC",platsvcdata);
	svcparamdata.put("CANCLE_FLAG",$("#CANCLE_FLAG").val());
	
	var serviceId=$("#SERVICE_ID").val();
	svcparamdata.put("ID",serviceId);
	
	//加上一个表示点过确认按钮的 DataMap
	var paramVerifySucc=new Wade.DataMap();
	paramVerifySucc.put("PARAM_VERIFY_SUCC","true");
	paramset.add(paramVerifySucc);
	paramset.add(svcparamdata);
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	$.setReturnValue();
	setStatetype();
}

//点取消按钮返回原值数据到父页面隐藏字段
function setCancleData(obj)
{
    var itemIndex= "";	
	var serviceId="";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var svcparamvalue="";
		try
	  {
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	  if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	  {
	  	  	svcparamvalue="";
	  	  }
	  }
	  catch(e)
	  {
		  svcparamvalue="";
	  }
	
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		if(pairName=="ELEMENT_ID")
		{
		  serviceId=pairValue;
		}
		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		} 
	}
	var modifyTag = $("#pam_MODIFY_TAG").val();
	if((svcparamvalue==""||svcparamvalue=="[]") && modifyTag == "0")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
			var dataset = new Wade.DatasetList();
			var paramVerifySuccMap=new Wade.DataMap();
			if (modifyTag == "0"){
				paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
			}
			dataset.add(paramVerifySuccMap);
			svcparamvalue=dataset.toString();
	}
	//$.getSrcWindow().selectedElements.updateAttr(itemIndex,svcparamvalue.toString());
	$.setReturnValue();
}

/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
    //判断是否存在        
    if (jsSelectIsExitItem(objSelect, objItemValue)) {        
        alert("该Item的Value值已经存在");        
    } else {   
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
    }        
}   		

 function jsSelectIsExitItem(objSelect, objItemValue) {  
    var isExit=false; 
    objSelect.children("option").each(function(){
         if(this.value==objItemValue)
            return !isExit;
    });
   return isExit;
} 

/**
 * 作用：控制TABLE里的值是否可填
 * @param tableName 
 * @param flag
 */
function tableDisabled(tableName, flag){

	$("#" + tableName + " input").each(function(){
	    this.disabled=flag;
	});
		  
	$("#" + tableName + " SELECT").each(function(){
	     this.disabled=flag;
	});
}

