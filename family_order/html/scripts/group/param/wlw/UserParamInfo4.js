/**$Id: UserParamInfo.js,v 1.3 2013/05/08 11:37:17 gujinlong Exp $*/
/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 如果没有查出来的话，再调用getServiceParamsByajax查一把
 */
function loadUserParamInfo(){
	var serviceId="";	
	var prodcutId =""; 
	var packageId ="";
	var param = "";
	var svcparamvalue ="";
	var userId ="";
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	
	try
	  {
		  userId=$.getSrcWindow().$("#MEB_USER_ID").val();//从父页面获取userId
	  	  userIda=$.getSrcWindow().$("#GRP_USER_ID").val();//从父页面获取userId
	  	  
	  	  //增加个人业务过来的情况
	  	  if(userId == undefined && userId == ""){
	  		  userId=$.getSrcWindow().$("#USER_ID").val();//从父页面获取userId
	  	  }
	  	  
	  	  //个人和集团都为空，此时走正常流程
	  	  if(userId==undefined)
	  	  {
	  	  	userId="";
	  	  }
	  }
	  catch(e)
	  {
		  userId="";
	  }
	  
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
	
		if(pairName=="PRODUCT_ID")
		{
		      prodcutId=pairValue;
		}
		if(pairName=="PACKAGE_ID")
		{
		      packageId=pairValue;
		      
		}
		if(pairName=="ELEMENT_ID")
		{
		      serviceId=pairValue;
		}

	}

	if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
			if(userId != undefined && userId != "")
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+"&USER_ID_A="+userIda;
			}
			else
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId;
			}
			$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){
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
		var datasetsize=svcparamvalue.getCount();
		if(datasetsize<=1) //表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验,第二条记录才是具体的参数信息)
		{
		
			if(userId != undefined && userId != "")
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+"&USER_ID_A="+userIda;
			}
			else
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId;
			}
			$.ajax.submit('this', 'getServiceParamsByajax', param, null, function(data){
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
	
	//根据集团的userida 判断是否是集团过来的入参，来判断是否改变入参
	var selfParam = "";
	if(userIda == undefined && userIda == ""){
		selfParam = '&SERVICE_ID='+serviceId+'&GROUP_ID='+$.getSrcWindow().$("#GROUP_ID").val() ;
	}else{
		//从个人过来
		selfParam = '&SERVICE_ID='+serviceId+'&USER_ID='+userId ;
	}
	
	//alert(selfParam);
	$.ajax.submit('this', 'getApproveParamByajax', selfParam ,'DiscntRateListPart', function(data){
		//根据配置来隐藏显示页面属性
    	//全部隐藏 0
		if(data.get("PARAM_CODE")=="0"){
			$("#spanID1").css("display","none");
    		$("#isShare").css("display","none");
    		$('#pam_CanShare').attr("nullable", "yes");
    		
    		$("#spanID2").css("display","none");
    		$("#month").css("display","none");
    		$('#pam_Months').attr("nullable", "yes");
    		
    		$("#spanID3").css("display","none");
    		$("#year").css("display","none");
    		$('#pam_YearDiscount').attr("nullable", "yes");
		}
    	//隐藏是否共享 1（eg.I00010100178）
		else if(data.get("PARAM_CODE")=="1"){
    		$("#spanID1").css("display","none");
    		$("#isShare").css("display","none");
    		$('#pam_CanShare').attr("nullable", "yes");
    	}
    	//隐藏月份，年份折扣 2
    	else if(data.get("PARAM_CODE")=="2"){
    		$("#spanID2").css("display","none");
    		$("#month").css("display","none");
    		$('#pam_Months').attr("nullable", "yes");
    		
    		$("#spanID3").css("display","none");
    		$("#year").css("display","none");
    		$('#pam_YearDiscount').attr("nullable", "yes");
    	}
    	
       },
       function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
	});
}

/**
 * 作用：如果URL中没有带参数，AJAX刷新后的处理
 */
function putpagedataByajax(serviceId)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId);
}


/**
 * 作用：从url取关键值,并根据关键值从父页面取值   (如果URL中没有带参数，AJAX刷新后的处理)
 */
function putpagedata(dataset,serviceId)
{
	  var paramVerifySucc=dataset.get(0); //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面
	  var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
	  var platsvcdata=paramMap.get('PLATSVC');
	  var bizincode = platsvcdata.get('pam_BIZ_IN_CODE');
	  platsvcdata.eachKey(                   
	 
	 	 function(key)
	 	 { 
	     	try
	     	{
	     		var tempElement = $("#"+key);
	     		if(tempElement != "undefined" && tempElement != ""){
	     			tempElement.val(platsvcdata.get(key,""));
	     		}
	     	}
	     	catch(e)
	     	{
	     	}
	     }
	    );	
    var discnt_rate = $('#pam_Discount').val();
    if(discnt_rate >= 60){
		$('#spanID').removeClass("e_required");
		$('#pam_ApprovalNum').attr("nullable", "yes");
	}
    
}




/**
 * 作用：如果URL中没有带参数，AJAX刷新后的处理
 */
function putpagedataByajax(serviceId) {
	var serverParamdataset = this.ajaxDataset;
	putpagedata(serverParamdataset, serviceId);
}

function setCancleData(obj) {
	var serviceId="";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var svcparamvalue="";
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		if(pairName=="HIDDEN_NAME")
		{
		  try
		  {
		  	svcparamvalue=window.parent.document.getElementById(pairValue).value;  
		  }
		  catch(e)
		  {
		   svcparamvalue="";
		  }
		}
		if(pairName=="SERVICE_ID")
		{
		  serviceId=pairValue;
		}
	}
	
	if(svcparamvalue==""||svcparamvalue=="[]")   //没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
		var dataset = new Wade.DatasetList();
		var paramVerifySuccMap=new Wade.DataMap();
		paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
		dataset.add(paramVerifySuccMap);
		svcparamvalue=dataset.toString();
	}
	
	$.setReturnValue();
}


//------------------------------确定时用--------------------------------
function setData(obj) {
	
  	if(!$.validate.verifyAll('platsvcparamtable'))
  	{
  		return false;
  	}
  	
  	var yearRate = $('#pam_YearDiscount').val();
  	var month = $('#pam_Months').val();
  	if(month != undefined && month != ""){
  		if(month % 12 != 0){
  	  		alert("月份数必须是12的倍数！");
  			return false;
  	  	}
  	}
  	if(yearRate != undefined && yearRate != ""){
  		if(yearRate > 100 || yearRate <= 0){
  			alert("年份折扣（固定值）不允许大于100或者小于等于0，请填写合理的年份折扣（固定值）！");
  			return false;
  		}
  	}
  	
	commSubmit(); //设置值到IData
}

function commSubmit() {
	
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
	
	var paramset = $.DatasetList();
	
	var paramVerifySucc = $.DataMap(); //加上一个表示点过确认按钮的 DataMap
	paramVerifySucc.put("PARAM_VERIFY_SUCC","true");
	paramset.add(paramVerifySucc);
	
	var svcparam=$.DataMap();
	var platsvcparam=$.ajax.buildJsonData("adcServicParamsForm_pam","pam");//获取表单以pam开头的数据
	var platsvcdata=$.DataMap(platsvcparam);
	svcparam.put("PLATSVC",platsvcdata);//PLATSVC存放页面以pam开头的数据,重新打开子页面的时候保持原来的值用 
	paramset.add(svcparam);
	
	var svcparamdata=$.DataMap();
	svcparamdata.put("Discount", $('#pam_Discount').val());
	svcparamdata.put("ApprovalNum", $('#pam_ApprovalNum').val());
	svcparamdata.put("APNNAME", $('#pam_APNNAME').val());
	svcparamdata.put("CanShare", $('#pam_CanShare').val());
	svcparamdata.put("Months", $('#pam_Months').val());
	svcparamdata.put("YearDiscount", $('#pam_YearDiscount').val());
	
	svcparamdata.eachKey(function(key) {
		try {
			var tmp = new Wade.DataMap();
			tmp.put("ATTR_CODE", key);
			tmp.put("ATTR_VALUE", svcparamdata.get(key, ""));
			paramset.add(tmp);
		} catch (e) {

		}
	});

	//alert(paramset);
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	$.getSrcWindow().$("#OLD_BIZ_IN_CODE").val($('#svrCodeTail').val());//设置服务代码到父页面 保证同一个产品受理时不同服务的服务代码相同
	$.setReturnValue();
}




function setApprovalNoValue(data){
	var a = $(data)[0].parentNode.parentNode;
	var discnt_rate = a.cells[0].innerText;
	var approval_no = a.cells[1].innerText;
	$('#pam_Discount').val(discnt_rate);
	$('#pam_ApprovalNum').val(approval_no);
}

function checkDiscntRate(){
	var discnt_rate = $('#pam_Discount').val();
	if(discnt_rate != undefined && discnt_rate != ""){
		$('#pam_ApprovalNum').val("");
		if(discnt_rate > 100 || discnt_rate <= 0){
			alert("折扣率不允许大于100或者小于等于0，请填写合理的折扣率！");
			return false;
		}
		if(discnt_rate >= 60){
			$('#spanID').removeClass("e_required");
			$('#pam_ApprovalNum').attr("nullable", "yes");
		}else{
			$('#spanID').addClass("e_required");
			$('#pam_ApprovalNum').attr("nullable", "no");
		}
	}
}