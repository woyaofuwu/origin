/**$Id: UserParamInfo.js,v 1.3 2013/05/08 11:37:17 gujinlong Exp $*/
/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
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
	  	  userId=$.getSrcWindow().$("#USER_ID").val();//从父页面获取userId
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
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId;
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
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId;
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
*作用：自动生成服务代码扩展码
*/
function createBizCodeExtend(){
	ajaxSubmit(this,'createBizCodeExtend','',null,function(data){afterCreateBizCodeExtend(data);});
}

function afterCreateBizCodeExtend(data){

	  var extendCode = data.get(0,"EXTEND_CODE");
	  if (extendCode == "" || extendCode == null){
	    alert("自动生成错误，请重新生成!");
	  }else{
		  $("#pam_SVRCODETAIL").val(extendCode);
	     checkAccessNumber();
	  }
	}


/**校验服务代码是否可用*/
function checkAccessNumber(){
	if (!verifyField($('#pam_SVRCODETAIL'))) {
		return false;
	}
    var bizInCode = $('#pam_SI_BASE_IN_CODE').val();
    var svcCode = $('#pam_SVRCODETAIL').val();

   var accessNumber = bizInCode+svcCode;
   ajaxSubmit(this,'getDumpIdByajax','&ACCESSNUMBER='+accessNumber,null,
		   function(data){afterCheckAccessNumber(data,accessNumber);},function(data){checkAccessNumberErr(data,accessNumber);});
}

function checkAccessNumberErr(data,accessNumber){
	alert("基本接入号["+accessNumber+"]校验出错，请重新校验！");
}

/**校验服务代码AJAX刷新后的处理*/
function afterCheckAccessNumber(data,accessNumber){
	var flag = data.get(0,"ISCHECKAACCESSNUMBER");
	if(flag == "true"){
		alert("基本接入号可以使用！");
		$('#pam_BIZ_IN_CODE').val(accessNumber.replace(/(^\s*)|(\s*$)/g, ""));
	}else{
		alert("基本接入号["+accessNumber+"]不能使用，请重新生成！");
		$('#pam_BIZ_IN_CODE').val('');
		$('#pam_SVRCODETAIL').val('');
	}
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
	    
	  //dealOperStateoptions(bizincode);
	
	//只有新增时，才从库取配置
	
	//生成服务代码尾数
	//today
	var vModiState = $("#pam_MODIFY_TAG").val();
	if (vModiState == '0'&&$("#PARAM_VERIFY_SUCC").val()=="false")
	{
	   getBizCodeTail();//只有新增用户时才产生服务代码尾数(或产生 或从该用户的其他服务继承)
	}		
	else
	{
	  tableDisabled("platsvcparamtable",false);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_SERV_MODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR_TYPE").attr('disabled',true);
	  showBizInCode();
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

	checkSignExist();
	
  	if(!$.validate.verifyAll('platsvcparamtable'))
  	{
  		return false;
  	}
  	
  	var vModiState = $("#pam_MODIFY_TAG").val();
  	if(vModiState == '0'&&$("#PARAM_VERIFY_SUCC").val()=="false")
  	{
		if($('#pam_SI_BASE_IN_CODE').val() == "" || $('#pam_SVRCODETAIL').val() == ""){
			alert("请补全基本接入号!");
			return false;
		}
  	}
	commSubmit(); //设置值到IData
}

function changeButton(){
	
	if ($('#pam_BIZ_SERV_MODE').val()=='02'){
		$("#pam_BIZ_SERV_CODE").attr("nullable","no");
		$("#serv_code_class").addClass("e_required");
	}else{
		$("#pam_BIZ_SERV_CODE").attr("nullable","yes");
		$("#serv_code_class").removeClass("e_required");
	}
	
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
	svcparamdata.put("INDUSTRY_TYPE", $("#pam_INDUSTRY_TYPE").val());
	svcparamdata.put("BIZ_SERV_CODE", $("#pam_BIZ_SERV_CODE").val());
	svcparamdata.put("AUTH_MODE", $("#pam_AUTH_MODE").val());
	svcparamdata.put("BASE_SERV_CODE_PROP",$("#pam_BASE_SERV_CODE_PROP").val());
	svcparamdata.put("BIZ_ATTR_TYPE", $("#pam_BIZ_ATTR_TYPE").val());
	svcparamdata.put("BIZ_SERV_MODE", $("#pam_BIZ_SERV_MODE").val());
	svcparamdata.put("DEFAULT_SIGNLANG",$("#pam_DEFAULT_SIGNLANG").val());
	svcparamdata.put("IS_TEXT_SIGN", $("#pam_IS_TEXT_SIGN").val());
	svcparamdata.put("TEXT_SIGN_ZH", $("#pam_TEXT_SIGN_ZH").val());
	svcparamdata.put("TEXT_SIGN_EN", $("#pam_TEXT_SIGN_EN").val());
	svcparamdata.put("BIZ_IN_CODE", $("#pam_BIZ_IN_CODE").val());
	
	svcparamdata.eachKey(function(key) {
		try {
			var tmp = new Wade.DataMap();
			tmp.put("ATTR_CODE", key);
			tmp.put("ATTR_VALUE", svcparamdata.get(key, ""));
			paramset.add(tmp);
		} catch (e) {

		}
	});

	$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	$.getSrcWindow().$("#OLD_BIZ_IN_CODE").val($('#svrCodeTail').val());//设置服务代码到父页面 保证同一个产品受理时不同服务的服务代码相同
	$.setReturnValue();
}

function afterCheckSignExist() {
	var resultdataset = this.ajaxDataset;
	var paramMap = resultdataset.get(0);
	var inProduct = paramMap.get('IN_PRODUCT');
	if (inProduct == "true") {
		var hasZh = paramMap.get("HAS_ZH");
		if (hasZh == "true") {
			alert("对不起，中文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_ZH").select();
			return;
		}

		var hasEn = paramMap.get("HAS_EN");
		if (hasEn == "true") {
			alert("对不起，英文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_EN").select();
			return;
		}
	}
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist() {
	if(window.parent.document.getElementById('PRODUCT_ID') ==null)
	{
		return;
	}
	var productid = window.parent.document.getElementById('PRODUCT_ID').value;
	ajaxGet(this, 'getSensitiveTextByajax',
			'TEXT_ECGN_ZH=' + getElement('pam_TEXT_SIGN_ZH').value
					+ '&TEXT_ECGN_EN=' + getElement('pam_TEXT_SIGN_EN').value
					+ '&PRODUCT_ID=' + productid, null, null, true,
			afterCheckSignExist);
}

function dealOperStateoptions(bizincode)
{
 	var modifytag=$('#pam_MODIFY_TAG').val();
 	if("2"==modifytag)//对原有记录进行修改时
 	{
 	  $("#pam_SI_BASE_IN_CODE").css('display','none');
 	  $("#pam_SI_BASE_IN_CODE").css('display','none');
 	  $("#pam_BIZ_IN_CODE").attr('disabled',false);
 	  $('#pam_BIZ_IN_CODE').val(bizincode);
 	  $('#pam_BIZ_SERV_MODE').attr('disabled',true);
 	  $('#pam_BIZ_ATTR_TYPE').attr('disabled',true);
 	  $('#pam_BIZ_IN_CODE').attr('disabled',true) 
 	}
 	
}

function getBizCodeTail()
{	
	 var oldcodetail=$.getSrcWindow().$('#OLD_BIZ_IN_CODE').val();
	 if(oldcodetail==undefined)
	 {
	 	oldcodetail=""
	 }
	 if(oldcodetail!="")
	 {
	 	$('#svrCodeTail').val(oldcodetail);
	 	//setMoAccessNum();
	 	return;
	 }
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

/**
*作用：控制基本接入号的显示，及校验标示的去必填
*/
function showBizInCode(){
	$("#SI_BASE_IN_CODE").css("display","none");
	$("#SVRCODETAIL").css("display","none");
	$("#add").css("display","none");
	$("#BIZ_IN_CODE").css("display","block");
	$("#pam_SI_BASE_IN_CODE").attr('nullable','yes');
	$("#pam_SVRCODETAIL").attr('nullable','yes');
	if ($('#pam_BIZ_SERV_MODE').val()=='02'){
		$("#pam_BIZ_SERV_CODE").attr("nullable","no");
		$("#serv_code_class").addClass("e_required");
	}
}
