function checkUser(data){
	var userInfo = data.get("USER_INFO");
    var param = "&USER_ID="+userInfo.get("USER_ID")+"&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('', 'loadChildInfo', param, '', 
	function(data){
		var brandName = data.get(0).get("USER_BRAND_NAME");
		var productName = data.get(0).get("USER_PRODUCT_NAME");
		
		$("#USER_BRAND_NAME").html(brandName);
		$("#USER_PRODUCT_NAME").html(productName);
		
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);
	if (div.css('display') != "none") {
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("显示客户信息");
	} else {
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户信息");
	}
}


function checkSubmitBefore(){
	   debugger;
	   
	   if(!$.validate.verifyAll("terminalInfoArea")) {
   			return false;
   	   }
	   
	   var terminalType=$("#TERMINAL_TYPE").val();
	   var terminalCode=$("#TERMINAL_CODE").val();
	   var terminalPrice=$("#TERMINAL_PRICE").val();
	   var terminalNumber=$("#TERMINAL_NUMBER").val();
	   var terminalTotalPrice=$("#TERMINAL_TOTAL_PRICE").val();
	   
	   if(!terminalType||terminalType==""){
		   alert("终端类型不能为空！");
		   return false;
	   }
	   if(!terminalCode||terminalCode==""){
		   alert("终端型号不能为空！");
		   return false;
	   }
	   if(!terminalPrice||terminalPrice==""){
		   alert("终端单价不能为空！");
		   return false;
	   }
	   if(!terminalNumber||terminalNumber==""){
		   alert("数量不能为空！");
		   return false;
	   }
	   if(!terminalTotalPrice||terminalTotalPrice==""){
		   alert("总价不能为空！");
		   return false;
	   }
	   
	   var terminalTotalPriceD=parseFloat(terminalTotalPrice);
	   if(terminalTotalPriceD<=0){
		   alert("总价必须大于0！");
		   return false;
	   }
	   
	   return true;
	}


function qryTerminal(){
	var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
	if(!serialNumber||serialNumber==""){
		alert("用户号码不能为空！");
		return false;
	}
	popupPage("ctt.cttTerminalSale.queryTerminal", 'init', "", '查询铁通终端', '700', '700');
}

function calculatotalPrice(){
	var price=$("#TERMINAL_PRICE").val();
	if(!price){
		return false;
	}
	
	var number=$("#TERMINAL_NUMBER").val();
	if(!number){
		alert("数量不能为空！");
		return false;
	}
	
	var numberInt=parseInt(number);
	var priceInt=parseFloat(price);
	
	var totalPrice=numberInt*priceInt;
	
	$("#TERMINAL_TOTAL_PRICE").val(totalPrice);
}