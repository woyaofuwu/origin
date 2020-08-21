function initPageParam(){
    // $("#pam_MAX_ITEM_PRE_DAY").val(0);
    var operState=$('#pam_MODIFY_TAG').val();
    var bodyHtml;
    if (operState == "2"){
        bodyHtml = $("#BIZ_IN_CODE").html();
        $("#SI_BASE_IN_CODE").parent().html(bodyHtml);
        $("#pam_BIZ_IN_CODE").attr("readOnly",true);
    }
    var service_id = $("#SERVICE_ID").val();

    var modifytag=$('#pam_MODIFY_TAG').val();
    if("0"==modifytag)//新增时
    {
        var operStateList = new Wade.DatasetList();
        var operState = new Wade.DataMap();
        operState.put("TEXT", "新增");
        operState.put("VALUE", "0");
        operStateList.add(operState);
        $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
        $("#pam_OPER_STATE").val("0"); //新增
    }else if("2"==modifytag)//对原有记录进行修改时
    {
        $('#pam_OPER_STATE').html("");
        $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
        var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
        if(platsyncState == '')
        {
            platsyncState="1";
        }
        if(platsyncState=="P")//暂停
        {
            var operStateList = new Wade.DatasetList();
            var operState = new Wade.DataMap();
            operState.put("TEXT", "恢复");
            operState.put("VALUE", "05");
            operStateList.add(operState);
            $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);

            $("#pam_OPER_STATE").val("05");//默认选为恢复
        }else if(platsyncState=="1")//正常在用
        {
            $('#pam_OPER_STATE').html("");
            $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
            var operStateList = new Wade.DatasetList();
            var operState = new Wade.DataMap();
            operState.put("TEXT", "暂停");
            operState.put("VALUE", "04");
            operStateList.add(operState);
            var operState1 = new Wade.DataMap();
            operState1.put("TEXT", "变更");
            operState1.put("VALUE", "08");
            operStateList.add(operState1);
            $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
            $("#pam_OPER_STATE").val("08"); //默认选为变更
        }
    }
    var bizCode=$("#pam_BIZ_CODE").val();

    if( bizCode == "AHN0019102" ||  bizCode == "2190029102" )
    {
        $('#pam_TEXT_ECGN_ZH').attr('disabled',true);//
    }

}


//用来初始化页面的显示
function loadMebSvcParamInfo() 
{

	var userIda="";
	var userId="";
	var memeparchycode="";
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
		//var dataset = new Wade.DatasetList(svcparamvalue);
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

//从ajax取值,赋给putpagedata
function putpagedataByajax(serviceId)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId);
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
		 	 	 
		     	try
		     	{   
		     		var tempElement = $("#"+key);
		     		if(tempElement != "undefined" && tempElement != "" && tempElement != null){
		     			tempElement.val(platsvcdata.get(key,""));
		     		} 
		     		if(key=="pam_BIZ_IN_CODE_A")
		     		{
		     			$("#pam_ACCESS_MODE").val(platsvcdata.get(key,""));
		     		}
		     	}
		     	catch(e)
		     	{
		     	}
		     }
		    );
		    
		  var otherlistdataset=paramMap.get('OTHERLIST');//扩展参数数据 IDataset结构
		  if(otherlistdataset)
		  {
		  	var otherlistcount=otherlistdataset.getCount();
		  	for(j=0;j<otherlistcount;j++)
		 	 { 
		  		 var otherdata=otherlistdataset.get(j);
		   		 otherdata.eachKey(
		 			 function(mokey)
		 	 		 {
		     			try
		     			{
		     				//alert(">>>>>"+mokey+"===="+otherdata.get(mokey,""));
		     				$("#"+mokey+j).val(otherdata.get(mokey,""));
		     			}
		     			catch(ee)
		     			{
		     			}
		     		}
		    	);
		   	   }
	  }
	dealOperStateoptions(serviceId);
}


function dealOperStateoptions(serviceId)
{  
 	var modifytag=$("#pam_MODIFY_TAG").val();
 	var bizattr=$("#pam_BIZ_ATTR").val();
 	//$("#pam_OPER_STATE options").length = 0; 
 	if("0"==modifytag)//新增时
 	{ 
 		$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项 
 		jsAddItemToSelect($("#pam_OPER_STATE"), '新增', '01');
 		$("#pam_OPER_STATE").val('01');
 		$("#tjOtherTable tbody tr").attr("status", "0");//修改扩展参数动态表格的tag为新增标志0，默认是修改标志2
 	}else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }
 	  
 	  if (platsyncState == "1"){
 	    $('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项 
 	  	jsAddItemToSelect($("#pam_OPER_STATE"), '暂停', '04');
 	  	jsAddItemToSelect($('#pam_OPER_STATE'), '变更', '08');
 	  }else{
 	   	$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项 
 	  	jsAddItemToSelect($("#pam_OPER_STATE"), '恢复', '05'); 
 	  	$("#pam_OPER_STATE").val('05');
 	  }
 	  //getParamLists(serviceId);
 	} 
 	setStatetype();		
}

/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	04- 暂停 05-恢复
 */
function setStatetype(){
  var operState=$("#pam_OPER_STATE").val();
  if (operState == "04"){
	  tableDisabled("platsvctab",true);
	  //tableDisabled("tjOtherTable",true);
	  $("#pam_OPER_STATE").attr('disabled',false);
  } 
  if (operState == "05"){
	  tableDisabled("platsvctab",true);
	  //tableDisabled("tjOtherTable",true);
	   $("#pam_OPER_STATE").attr('disabled',false);
	  MessageBox.alert("","您确定要暂停、恢复成员此业务吗？否则请按底页的[取消]按钮!");
  }
  if (operState == "01"){  
	tableDisabled("platsvctab",true);
	$("#pam_OPER_STATE").attr('disabled',false);
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
	tableDisabled("platsvctab",false);
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
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#HIDDEN_NAME").val());
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
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,svcparamvalue.toString());
	$.setReturnValue();
}



/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
    //判断是否存在        
    if (jsSelectIsExitItem(objSelect, objItemValue)) {        
        MessageBox.alert("","该Item的Value值已经存在");        
    } else {   
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
       //objSelect.options.add(varItem);   
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
 * 作用：动态表格列元素值的修改，
 */
/**
 * 作用：动态表格列元素值的修改，
 */
function inputBlur(ipt){
  var field = ipt.getAttribute("field");
  var value = $(ipt).val();
  if(field != "" && field != null && field != value){
     	ipt.parentNode.parentNode.cells[0].innerText="2";
  }else{
     	ipt.parentNode.parentNode.cells[0].innerText="0";
  }
     	ipt.parentNode.parentNode.cells[4].innerText=$(ipt).val();
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

function getTableData() {
	//var data = $.table.get("MoListTable").getTableData(null,true);
	//$("#MoListTable tbody tr")[1].setAttribute("status", "2");
		var data1 = $.table.get("tjOtherTable").getTableData(null, true);
	MessageBox.alert("",data1);
}

