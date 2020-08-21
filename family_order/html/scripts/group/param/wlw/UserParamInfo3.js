function loadUserParamInfo(){	
	var product_id = "";
	var package_id = "";
	var service_id = "";
	var discnt_id = "";
	var element_id = "";
	var element_type = "";
	var user_id = "";
	var user_id_a = "";
	var param = "";
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	var item_index = "";
	
	try
	{
		user_id = $.getSrcWindow().$("#MEB_USER_ID").val();
		user_id_a = $.getSrcWindow().$("#GRP_USER_ID").val();
		if(user_id == undefined){
			user_id = "";
		}
	}
	catch(e)
	{
		user_id = "";
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
		if(pairName=="ITEM_INDEX")
		{
			item_index=pairValue;
		      
		}
		if(pairName=="ELEMENT_ID")
		{
			element_id=pairValue;
		}
		if(pairName=="ELEMENT_TYPE_CODE")
		{
			element_type=pairValue;
		}
	}
	
	if(element_type != "S"){
		discnt_id = element_id;
		$(".service").css("display","none");
	}else{
		service_id = element_id;
		$(".discnt").css("display","none");
	}
	
	try
	{
		svcparamvalue=$.getSrcWindow().selectedElements.getAttrs(item_index);
		if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined){
			svcparamvalue="";
  	  	}
	}
	catch(e)
	{	
		svcparamvalue="";
	}
	
	
	if(svcparamvalue==""||svcparamvalue=="[]")
	{
		if(user_id == undefined || user_id == "")
		{
			param = "&SERVICE_ID="+service_id+"&DISCNT_ID="+discnt_id+"&DISCNT_CODE="+package_id+"&PRODUCT_ID="+product_id;
		}
		else
		{
			param = "&SERVICE_ID="+service_id+"&DISCNT_ID="+discnt_id+"&PACKAGE_ID="+package_id+"&PRODUCT_ID="+product_id+"&USER_ID="+user_id+"&USER_ID_A="+user_id_a;
		}
		$.ajax.submit("this","getElementParamAttr",param,null,function(data){
			putpagedata(data,service_id,discnt_id);
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
	else
	{
		var datasetsize=svcparamvalue.getCount();
		if(datasetsize<=1)
		{
			if(user_id == undefined || user_id == "")
			{
				param = "&SERVICE_ID="+service_id+"&DISCNT_ID="+discnt_id+"&PACKAGE_ID="+package_id+"&PRODUCT_ID="+product_id;
			}
			else
			{
				param = "&SERVICE_ID="+service_id+"&DISCNT_ID="+discnt_id+"&PACKAGE_ID="+package_id+"&PRODUCT_ID="+product_id+"&USER_ID="+user_id+"&USER_ID_A="+user_id_a;
			}
			$.ajax.submit("this","getElementParamAttr",param,null,function(data){
				putpagedata(data,service_id,discnt_id);
				$.endPageLoading();
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
		else
		{
			putpagedata(svcparamvalue,service_id,discnt_id);
		}
	};
	
}

function getPower()
{
	var name = $("#pam_LOWPOWERMODE").val();
	if(name == "PSM"){
		$("#TimerClock").attr('disabled','');
		$("#TimerSure").attr('disabled','');
		$("#TimerClock").css('display','inline-block');
		$("#TimerSure").css('display','inline-block');	
	}else{
		$("#TimerClock").attr('disabled','disabled');
		$("#TimerSure").attr('disabled','disabled');
		$("#TimerClock").css('display','none');
		$("#TimerSure").css('display','none');	
	}
}

/**
 * 作用：如果URL中没有带参数，AJAX刷新后的处理
 */
function putpagedataByajax(serviceId,discnt_id)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId,discnt_id);
}

/**
 * 作用：从url取关键值,并根据关键值从父页面取值   (如果URL中没有带参数，AJAX刷新后的处理)
 */
function putpagedata(dataset,service_id,discnt_id)
{
	var paramVerifySucc=dataset.get(0);  
	  $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面

	  var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态״̬
	  var platsvcdata=paramMap.get('PLATSVC');

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

}


function getDiscount(){
	var dis = 0;
	var inNetMonths = $("#pam_PromiseUseMonths").val();
	var inNetNums = $("#pam_BatchAccounts").val();
	var discount = $("#pam_Discount").val();
	if(inNetMonths!=""&&inNetNums!=""&&discount!=""){
		if(inNetMonths <= 36 && inNetNums <=10000){
			dis = 90;
		}else if(inNetMonths <= 36 && inNetNums >10000){
			dis = 70;
		}else if(inNetMonths > 36 && inNetNums <=10000){
			dis = 70;
		}else {
			dis = 50;
		}	
		if(discount<dis&&dis!=0){
			$("#pam_ApprovalNum").attr("disabled","");
		}else{
			$("#pam_ApprovalNum").attr("disabled","true");
		}
	}
	
}

function NUMBER(num){
	var reg = /^((?!0)\d{1,2}|100)$/;  
	 if(!num.match(reg)){  
	   return false;  
	 }else{  
	   return true;  
	 } 
}

function setCancleData(obj) {
	var serviceId="";	
	var discntId = "";
	var element_id = "";
	var element_type_code = "";
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
		if(pairName=="ELEMENT_ID")
		{
			element_id=pairValue;
		}
		if(pairName=="ELEMENT_TYPE_CODE")
		{
			element_type_code=pairValue;
		}
	}
	if(element_type_code == "S"){
		serviceId = element_id;
	}else{
		discntId = element_id;
	}
	
	if(svcparamvalue==""||svcparamvalue=="[]")  
	{
		var dataset = new Wade.DatasetList();
		var paramVerifySuccMap=new Wade.DataMap();
		paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
		dataset.add(paramVerifySuccMap);
		svcparamvalue=dataset.toString();
	}
	
	/*var hiddenName=$("#HIDDEN_NAME").val();
	var buttenName=$("#POPUP_BTN_NAME").val();
	setReturnValue(buttenName,buttenName,[hiddenName],[svcparamvalue]);*/
	$.setReturnValue();
}

function setData(obj) {

	if (!$.validate.verifyAll("iotParam")) {
		return;
	}
	commSubmit(); //设置值到IData
}

function getTime(){
	var time = $("#TimeClock").val();
	if(time == "360"){
		$("#pam_RAUTAUTIMER").empty();
		$("#pam_RAUTAUTIMER").append("<option value='3240'>54分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='3600'>60分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='3960'>66分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='4320'>72分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='4680'>78分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='5040'>84分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='5400'>90分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='5760'>96分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='6120'>102分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='6480'>108分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='6840'>114分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='7200'>120分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='7560'>126分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='7920'>132分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='8280'>138分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='8640'>144分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='9000'>150分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='9360'>156分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='9720'>162分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='10080'>168分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='10440'>174分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='10800'>180分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='11160'>186分钟</option>");
	}else if(time == "600"){
		$("#pam_RAUTAUTIMER").empty();
		$("#pam_RAUTAUTIMER").append("<option value='11400'>190分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='12000'>200分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='12600'>210分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='13200'>220分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='13800'>230分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='14400'>240分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='15000'>250分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='15600'>260分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='16200'>270分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='16800'>280分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='17400'>290分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='18000'>300分钟</option>");
		$("#pam_RAUTAUTIMER").append("<option value='18600'>310分钟</option>");
	}else if(time == "3600"){
		$("#pam_RAUTAUTIMER").empty();
		$("#pam_RAUTAUTIMER").append("<option value='21600'>6小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='25200'>7小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='28800'>8小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='32400'>9小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='36000'>10小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='39600'>11小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='43200'>12小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='46800'>13小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='50400'>14小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='54000'>15小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='57600'>16小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='61200'>17小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='64800'>18小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='68400'>19小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='72000'>20小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='75600'>21小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='79200'>22小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='82800'>23小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='86400'>24小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='90000'>25小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='93600'>26小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='97200'>27小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='100800'>28小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='104400'>29小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='108000'>30小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='111600'>31小时</option>");
	}else if(time == "36000" ){
		$("#pam_RAUTAUTIMER").empty();
		$("#pam_RAUTAUTIMER").append("<option value='144000'>40小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='180000'>50小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='216000'>60小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='252000'>70小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='288000'>80小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='324000'>90小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='360000'>100小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='396000'>110小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='432000'>120小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='468000'>130小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='504000'>140小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='540000'>150小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='576000'>160小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='612000'>170小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='648000'>180小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='684000'>190小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='720000'>200小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='756000'>210小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='792000'>220小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='828000'>230小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='864000'>240小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='900000'>250小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='936000'>260小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='972000'>270小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='1008000'>280小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='1044000'>290小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='1080000'>300小时</option>");
		$("#pam_RAUTAUTIMER").append("<option value='1116000'>310小时</option>");
	}
}
//function getApro(){
//	var pamAPPROvalueNUM = $("#pam_APPROVALNUM").val();
//	
//	var user_id_a = $.getSrcWindow().$("#GRP_USER_ID").val();
//	var paramm = "&GROUP_ID="+user_id_a+"&APRO="+pamAPPROVALNUM;
//	
//	if(pamAPPROVALNUM != undefined || pamAPPROVALNUM != ""){
//		$.ajax.submit("this","getApro",paramm,null,function(data){
//			$("#bt_ok").attr('disabled','');
//			$.endPageLoading();
//		},function(error_code,error_info,derror){
//			$("#bt_ok").attr('disabled','disabled');
//			$.endPageLoading();
//			showDetailErrorInfo(error_code,error_info,derror);
//		});
//	}
//}
function commSubmit() {
	var itemIndex= "";
	var service_id = "";
	var discnt_id = "";
	var element_id = "";
	var element_type_code = "";
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
		if(pairName=="ELEMENT_ID")
		{
			element_id=pairValue;
		}
		if(pairName=="ELEMENT_TYPE_CODE")
		{
			element_type_code=pairValue;
		}
	}
	if(element_type_code == "S"){
		service_id = element_id;
	}else{
		discnt_id = element_id;
	}
	
	
	
	var pamAPNAME = $("#pam_APNNAME").val();
	var pamLOWPOWERMODE = $("#pam_LOWPOWERMODE").val();
	var pamRAUTAUTIMER = $("#pam_RAUTAUTIMER").val();
	var pamDISCOUNT = $("#pam_Discount").val();
	var pamAPPROVALNUM = $("#pam_ApprovalNum").val();
	var pamPROMISEUSEMONTHS = $("#pam_PromiseUseMonths").val();
	var pamBATCHACCOUNTS = $("#pam_BatchAccounts").val();
	var pamCANSHARE = $("#pam_CanShare").val();
	
	
	
	var isEditFlag = false;
	if((pamAPNAME != ""||pamLOWPOWERMODE!="" || pamRAUTAUTIMER != "" )||(pamDISCOUNT!="" && pamAPPROVALNUM!="" && pamPROMISEUSEMONTHS!="" &&pamBATCHACCOUNTS!=""&&pamCANSHARE!="")){
		isEditFlag = true;
	}
	
	if(service_id!=""){
		if(pamAPNAME==""){
			alert("APNNAME不能为空");
			return;
		}else if(pamLOWPOWERMODE==""){
			alert("低功耗模式不能为空");
			return;
		}else if(pamLOWPOWERMODE=="PSM"&&pamRAUTAUTIMER==""){
			alert("定时器不能为空");
			return;
		}
	}else{
		
		var dis = 0;
		var inNetMonths = $("#pam_PromiseUseMonths").val();
		var inNetNums = $("#pam_BatchAccounts").val();
		var discount = $("#pam_Discount").val();
		if(inNetMonths!=""&&inNetNums!=""&&discount!=""){
			if(inNetMonths <= 36 && inNetNums <=10000){
				dis = 90;
			}else if(inNetMonths <= 36 && inNetNums >10000){
				dis = 70;
			}else if(inNetMonths > 36 && inNetNums <=10000){
				dis = 70;
			}else {
				dis = 50;
			}	
			if(discount<dis&&dis!=0&&pamAPPROVALNUM==""){
				alert("审批文号不能为空");
				return;
			}
		}
		
		if(pamDISCOUNT==""){
			alert("折扣率不能为空");
			return;
		}else if(pamPROMISEUSEMONTHS==""){
			alert("承诺在网时间不能为空");
			return;
		}else if(pamBATCHACCOUNTS==""){
			alert("一次性入网人数不能为空");
			return;
		}else if(pamCANSHARE==""||pamCANSHARE!="0"){
			alert("不可共享,CANSHARE字段填0");
			return;
		}
	}

	var paramset = $.DatasetList();
	
	var paramVerifySucc = $.DataMap(); //加上一个表示点过确认按钮的 DataMap
	if(isEditFlag){
		paramVerifySucc.put("PARAM_VERIFY_SUCC","true");
	}else{
		paramVerifySucc.put("PARAM_VERIFY_SUCC","false");
	}
	paramset.add(paramVerifySucc);
	
	var svcparam=$.DataMap();
	var platsvcparam=$.ajax.buildJsonData("NBIOTParam","pam");//获取表单以pam开头的数据
	var platsvcdata=$.DataMap(platsvcparam);
	svcparam.put("PLATSVC",platsvcdata);               //PLATSVC存放页面以pam开头的数据,重新打开子页面的时候保持原来的值用 
	paramset.add(svcparam);
	
	var svcparamdata=$.DataMap();
	if(pamAPNAME!=""){
		svcparamdata.put("APNNAME", pamAPNAME);
		svcparamdata.put("LOWPOWERMODE",pamLOWPOWERMODE);
		svcparamdata.put("RAUTAUTIMER", pamRAUTAUTIMER);
	}else{
		svcparamdata.put("Discount", pamDISCOUNT);
		svcparamdata.put("ApprovalNum", pamAPPROVALNUM);
		svcparamdata.put("PromiseUseMonths", pamPROMISEUSEMONTHS);
		svcparamdata.put("BatchAccounts",pamBATCHACCOUNTS);
		svcparamdata.put("CanShare",pamCANSHARE);
	}
	
	svcparamdata.eachKey(function(key) {
		try {
			var tmp = new Wade.DataMap();
			tmp.put("ATTR_CODE", key);
			tmp.put("ATTR_VALUE", svcparamdata.get(key, ""));
			paramset.add(tmp);
		} catch (e) {
			alert("参数不能为空");
		}
	});
	if(isEditFlag){//有修改才设置到产品组件
		$.getSrcWindow().$('#innervalue').text(paramset.toString());
//		$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	}
	$.setReturnValue();
}
