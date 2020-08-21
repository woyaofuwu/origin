function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'hiddenPart,QueryListPart', function(data1){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
	MessageBox.alert(error_info);
    });
}


function discntInfo(){
	 var area = $("#OPEN_AREA").val();
	 var discnt = $("#DISCNT_NAME").val();
	 var SERIAL_NUMBER = $("#AUTH_SERIAL_NUMBER").val();
	 if(area != ''&& discnt!= ''){
		$.beginPageLoading("正在获取优惠信息...");
		$.ajax.submit('ChooseDiscntPart', 'queryDiscntInfo',  "&SERIAL_NUMBER="+SERIAL_NUMBER, 'DiscntInfoPart', function(data){
				$.endPageLoading();
				var resultCode = data.get("RESULT_CODE");
				var resultInfo = data.get("RESULT_INFO");
		
				if(resultCode == "-1"){
					$("#DISCNT_NAME").val("");
					MessageBox.alert('提示',"话费余额不足,不能办理该套餐");
				}
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.alert(error_info);
		});
		  
	 }	
}

function changeArea(date){
	var area = $("#OPEN_AREA").val();
	var deputyCount = $("#DEPUTY_COUNT").val();
	if(area == 'CTRTC'){
		var html = $("tr[name=userDiscntTr]").html();
		if(html!=undefined&&html!=''){
		MessageBox.alert("该用户已经办理畅听日套餐功能性产品【99991128】不能重复办理！");
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
	MessageBox.alert("该用户已经办理国漫日租套餐，请先通过“一级BOSS业务受理->一级BOSS管理->国漫一卡多号->国漫及一卡多号业务办理“界面进行退订！");
		$("#EuropeAera").css("display", "none");
		$("#DISCNT_NAME").attr("disabled", false);
		$("#DISCNT_NAME").val("");
		$("#DISCNT_NAME option[value!='']").remove();
		$("#DISCNT_CODE").html("");
		$("#ELEMENT_NAME").html("");
		return false;
	}
    if(area == 'EU'||area == 'NA'||area == 'OA'||area == 'DL'||area == 'CTRTC'||area == 'HWSXK'){
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
		MessageBox.alert(error_info);
		});
		var SERIAL_NUMBER = $("#AUTH_SERIAL_NUMBER").val();
		$.ajax.submit('ChooseDiscntPart', 'queryDiscntInfo',  "&SERIAL_NUMBER="+SERIAL_NUMBER, 'DiscntInfoPart', function(data){
		},
	    function(error_code,error_info){
		MessageBox.alert(error_info);
		});
	}else{
		$("#EuropeAera").css("display", "none");
		$("#DISCNT_NAME").attr("disabled", false);
		$("#DISCNT_NAME").val("");
		$("#DISCNT_CODE").html("");
		$("#ELEMENT_NAME").html("");
        var snb = $("#AUTH_SERIAL_NUMBER").val();
		$.ajax.submit('ChooseDiscntPart', 'queryAreaInfo',  '&snb=' + snb, 'SelectDiscntPart', function(data){
		 },
	    function(error_code,error_info)
	    {
			MessageBox.alert(error_info);
		});
	 }
	
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
		MessageBox.alert("优惠编码不能为空！");
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
	// add by huangyq 
	var PROD_INST_ID = obj.getAttribute("prodInstId");
	// 添加 参数    +"&PROD_INST_ID="+PROD_INST_ID
//MessageBox.alert(discntCode+"/"+discntName);
	MessageBox.confirm("确认提示", "退订信息提示！", 
				function(btn){
					if(btn == "ok"){
						$.ajax.submit('AuthPart', 'onTradeSubmit',  '&DISCNT_CODE='+discntCode+"&OP_TAG=1"+"&PROD_INST_ID="+PROD_INST_ID, "", function(data){
							 MessageBox.alert("提示","退订成功！",function(btn){
								 if(btn="ok")
								 {
								    window.location.reload();
								 }
							 });
							},
						    function(error_code,error_info){
								MessageBox.alert(error_info);
							   });
					}else{
						
					}
			}, null, "是否退订："+discntName+"?");	
}
