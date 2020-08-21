function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'hiddenPart,QueryListPart', function(data1){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function discntInfo(){
	 var area = $("#OPEN_AREA").val();
	 var discnt = $("#DISCNT_NAME").val();
	 if(area != ''&& discnt!= ''){
		 $.beginPageLoading("正在获取优惠信息...");
		$.ajax.submit('ChooseDiscntPart', 'queryDiscntInfo',  null, 'DiscntInfoPart', function(data){
				$.endPageLoading();
			},
		    function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			   });
		  
	 }	
}

function changeArea(date){
	var area = $("#OPEN_AREA").val();
	var deputyCount = $("#DEPUTY_COUNT").val();
	if(area == 'CTRTC'){
		var html = $("tr[name=userDiscntTr]").html();
		if(html!=undefined&&html!=''){
			alert("该用户已经办理畅听日套餐功能性产品【99991128】不能重复办理！");
			$("#EuropeAera").css("display", "none");
			$("#DISCNT_NAME").attr("disabled", false);
			$("#DISCNT_NAME").val("");
			$("#DISCNT_NAME option[value!='']").remove();
			$("#DISCNT_CODE").html("");
			$("#ELEMENT_NAME").html("");
			return false;
		}
	}
	if(deputyCount == '1'&&area == 'CTRTC'){
		alert("该用户已经办理国漫日租套餐，请先通过“一级BOSS业务受理->一级BOSS管理->国漫一卡多号->国漫及一卡多号业务办理“界面进行退订！");
		$("#EuropeAera").css("display", "none");
		$("#DISCNT_NAME").attr("disabled", false);
		$("#DISCNT_NAME").val("");
		$("#DISCNT_NAME option[value!='']").remove();
		$("#DISCNT_CODE").html("");
		$("#ELEMENT_NAME").html("");
		return false;
	}
	$("#EuropeAera").css("display", "");
	//$("#DISCNT_NAME").val("7");
	//$("#DISCNT_NAME").attr("disabled", true);
	$("#DISCNT_NAME").attr("disabled", false);
	$("#DISCNT_NAME").val("");
	$("#DISCNT_CODE").html("");
	$("#ELEMENT_NAME").html("");

	var snb = $("#AUTH_SERIAL_NUMBER").val();
	$.ajax.submit('ChooseDiscntPart', 'queryAreaInfo',  '&snb=' + snb, 'SelectDiscntPart,EuropeAera', function(data){
	},
	function(error_code,error_info)
	{
		alert(error_info);
	});
	$.ajax.submit('ChooseDiscntPart', 'queryDiscntInfo',  null, 'DiscntInfoPart', function(data){
	},
	function(error_code,error_info){
		alert(error_info);
	});
	
//	else{
//		$("#EuropeAera").css("display", "none");
//		$("#DISCNT_NAME").attr("disabled", false);
//		$("#DISCNT_NAME").val("");
//		$("#DISCNT_CODE").html("");
//		$("#ELEMENT_NAME").html("");
//		
//		$.ajax.submit('ChooseDiscntPart', 'queryAreaInfo',  null, 'SelectDiscntPart', function(data){
//		 },
//	    function(error_code,error_info)
//	    {
//				alert(error_info);
//		});
//	 }
	
}

function submitBeforeAction(){

		if(!$.validate.verifyAll("ChooseDiscntPart")) {
			return false;
		}	
		if(!$.validate.verifyAll("DiscntInfoPart")) {
			return false;
		}	
		var discntCode = $("#DISCNT_CODE").html();
		if(!discntCode||discntCode=="null"||discntCode==""){
			alert("优惠编码不能为空！");
		}
		
		var openArea = $("#OPEN_AREA").val();
		var param = "&DISCNT_CODE="+discntCode+"&OP_TAG=0";
		$.cssubmit.addParam(param);       
	       return true;

}

function selectChange(obj)
{
	var discntCode = obj.getAttribute("discntCode");
	var discntName = obj.getAttribute("discntName");
//	alert(discntCode+"/"+discntName);
	MessageBox.confirm("确认提示", "退订信息提示！", 
				function(btn){
					if(btn == "ok"){
						$.ajax.submit('AuthPart', 'onTradeSubmit',  '&DISCNT_CODE='+discntCode+"&OP_TAG=1", "", function(data){
							 MessageBox.alert("提示","退订成功！",function(btn){
								 if(btn="ok")
								 {
								    window.location.reload();
								 }
							 });
							},
						    function(error_code,error_info){
									alert(error_info);
							   });
					}else{
						
					}
			}, null, "是否退订："+discntName+"?");	
}
