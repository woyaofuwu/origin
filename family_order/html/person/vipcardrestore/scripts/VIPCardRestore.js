(function($){
	$.extend({vipCardRestore:{
	changeValue:function(){
		var routeType = $("#ROUTE_TYPE").val();
		var userIdType = $("#USER_IDTYPE").val();
		var mobileNum = $("#cond_MOBILENUM").val();
		
		if("01"==routeType && "01"==userIdType) { 
			$("#cond_IDVALUE").val(mobileNum);  
		}
	},
	changeIdValue:function(){
		var routeType = $("#ROUTE_TYPE").val();
		var userIdType = $("#USER_IDTYPE").val();
		
		if("01"==routeType && "01"==userIdType) {
		var condIdValue = $("#cond_IDVALUE").val();
			$("#cond_MOBILENUM").val(condIdValue)
		}
	},
	checkVerify:function(){
		var idCardType = $("#cond_VERIFY_TYPE").val();
		if(idCardType==0){
			$("#checkIdCardNumPart1").css('display','');
			$("#checkIdCardNumPart").css('display','');
			$("#checkUserPWDPart").css('display','none');
			$("#IDCARD_TYPE").val($("#idcardtypehidden").val());
		}else{
			$("#idcardtypehidden").val( $("#IDCARD_TYPE").val());
			$("#checkIdCardNumPart1").css('display','none');
			$("#checkIdCardNumPart").css('display','none');
			$("#checkUserPWDPart").css('display','');
		}
	},
	queryCustInfo:function(){
		
		var userIdType = $("#USER_IDTYPE").val();
		if(userIdType == ""){
			alert("请选择客户类型！");
			return false;
		}
		
		var condIdValue = $("#cond_IDVALUE").val();
		if(condIdValue == ""){
			alert("请输入客户号码！");
			return false;
		}
		
		var verifyType = $("#cond_VERIFY_TYPE").val();
		if(verifyType == ""){
			alert("请选择校验方式！");
			return false;
		}
		//校验方式为证件校验
		var idcardType = $("#IDCARD_TYPE").val();
		if(verifyType == "0"){
			
			if(idcardType == ""){
				alert("请选择证件类型！");
				return false;
			}
			var idcardNum = $("#cond_IDCARDNUM").val(); 
			if(idcardNum == ""){
				alert("请输入证件号码！");
				return false;
			}
		}
		
		//密码校验
		if(verifyType == "1"){
			var userPasswd =$("#cond_USER_PASSWD").val(); 
			if(userPasswd == ""){
				alert("请输入密码！");
				return false;
			}
			idcardType="01";
		}
	
		if ($("#ROUTE_TYPE").val() == "00" && ( $("#PROV_CODE").val() == null ||  $("#PROV_CODE").val() == "")){
			alert('按省代码路由省代码不能为空！');
			return false;
		}
		if ($("#ROUTE_TYPE").val() == "01" && ( $("#cond_MOBILENUM").val() == null ||  $("#cond_MOBILENUM").val() == "")){
			alert('按电话号码路由电话号码不能为空！');
			return false;
		}
		
		
			var param = "&ROUTETYPE="+$("#ROUTE_TYPE").val()+"&MOBILENUM="+$("#cond_MOBILENUM").val()+
			"&PROVCODE="+$("#PROV_CODE").val()+
			
			"&VERIFYTYPE="+verifyType+
			"&IDCARDTYPE="+idcardType+
			"&IDCARDNUM="+$("#cond_IDCARDNUM").val()+
			"&USERPASSWD="+$("#cond_USER_PASSWD").val()+
			
			"&USERIDTYPE="+$("#USER_IDTYPE").val()+
			"&IDVALUE="+$("#cond_IDVALUE").val();
			
			$.beginPageLoading("用户资料加载中.....");
			$.ajax.submit(this, 'getCustInfo', param, 'custInfosViewPart', function() {
				$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
				$.endPageLoading();
			}, function(error_code, error_info) {
				$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
				$.endPageLoading();
				
				$.MessageBox.error(error_code, error_info);
			});
	},
	onTradeSubmitApp:function(){
		var operFee = $("#OPER_FEE").val();
		if(operFee == ""){
			alert("请输入手续费(元)");
			return false;
		}
		if(!verifyAll("submitInfosPart")) {
			return false;
		}
		
		if(!verifyAll("checkInfosPart")) {
			return false;
		}
		
		if(!verifyAll("freeInfosPart")) {
			return false;
		}
		
		var param = "&ROUTETYPE="+$("#ROUTE_TYPE").val()+
			"&MOBILENUM="+$("#cond_MOBILENUM").val()+
			"&PROVCODE="+$("#PROV_CODE").val()+
			"&VERIFYTYPE="+$("#cond_VERIFY_TYPE").val();+
			"&IDCARDTYPE="+$("#IDCARD_TYPE").val()+
			"&IDCARDNUM="+$("#cond_IDCARDNUM").val()+
			"&USERPASSWD="+$("#cond_USER_PASSWD").val()+
			"&USERIDTYPE="+$("#USER_IDTYPE").val()+
			"&IDVALUE="+$("#cond_IDVALUE").val();
			
			$.cssubmit.addParam(param);
		
		return true;
	}
	
	
		}});
})(Wade);