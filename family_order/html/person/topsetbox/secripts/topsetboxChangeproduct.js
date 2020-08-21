//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,sellTopSetBoxInfoPart,topSetBoxInfoPart',
			function(data) {
				$("#Artificial_services").val("0");
				$("#Artificial_services").attr("disabled","true");
		
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


function showOperSelectionContent(){
	var operSelect=$("#cond_OPER_SELECTIONS").val();
	if(operSelect=="1"){	//变更产品
		$("#productInfoArea").css("display","block");
		$("#topsetboxInfoArea").css("display","none");
		
		$.ajax.submit('AuthPart', 'queryProductInfo', null, 'topSetBoxProductInfoPart',
		function(data) {
			$.endPageLoading();
		}, function(error_code, error_info, derror) {
			$.endPageLoading();
			showDetailErrorInfo(error_code, error_info, derror);
		});
		
	}else if(operSelect=="2"){		//更换机顶盒
		$("#productInfoArea").css("display","none");
		$("#topsetboxInfoArea").css("display","block");
		
		$("#PRODUCT_ID").val("");
		$("#BASE_PACKAGES").val("");
		$("#OPTION_PACKAGES").val("");
		
	}else if(operSelect=="3"){		//变更产品和机顶盒
		$("#productInfoArea").css("display","block");
		$("#topsetboxInfoArea").css("display","block");
	}else{
		$("#productInfoArea").css("display","none");
		$("#topsetboxInfoArea").css("display","none");
		
	}

}


/**
 * 查询基础优惠包和可选优惠包
 */
function queryPackages() {
	var productId = $("#PRODUCT_ID").val();
	$.beginPageLoading("基础优惠包和可选优惠包查询中......");
	$.ajax.submit(null, 'queryDiscntPackagesByPID', "&PRODUCT_ID=" + productId, 'bPackagePart,oPackagePart',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
//	if($("#RES_ID").val()!=$("#RES_NO").val()){
//		alert("终端串不一致，请重新校验！");
//		return false;
//	}
//	if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
//		return false;
//	}
	
	var operSelect=$("#cond_OPER_SELECTIONS").val();
	if(operSelect==""){
		MessageBox.alert("提示","操作类型不能为空！");
		return false;
	}
	
	
	if(operSelect=="1"){	//变更产品
		if(!$.validate.verifyAll("topSetBoxProductInfoPart")) {
			return false;
		}
		
		
		
	}else if(operSelect=="2"){		//更换机顶盒
		if(!$.validate.verifyAll("topSetBoxInfoPart")) {
			return false;
		}

	}else if(operSelect=="3"){		//变更产品和机顶盒
		if(!$.validate.verifyAll("topSetBoxInfoPart")||!$.validate.verifyAll("topSetBoxProductInfoPart")) {
			return false;
		}
	}
	
	if(operSelect=="1"||operSelect=="3"){
		//核对变更的套餐是否一样
		var oldBasePackagesAll=$("#OLD_BASE_PACKAGES").val();
		var oldOptionPackagesAll=$("#OLD_OPTION_PACKAGES").val();
		
		var basePackages=$("#BASE_PACKAGES").val();
		var optionPackages=$("#OPTION_PACKAGES").val();
		if(!optionPackages||optionPackages==null){
			optionPackages="";
		}
		
		var basePackageSame=false;
		if(oldBasePackagesAll&&oldBasePackagesAll!=null&&oldBasePackagesAll!=""){
			var oldBasePackageArr=oldBasePackagesAll.split(",");
			if(oldBasePackageArr!=null&&oldBasePackageArr.length>0){
				var oldBasePackage=oldBasePackageArr[0];
				if(basePackages==oldBasePackage){
					basePackageSame=true;
				}
			}
			
		}
		
		var optionPackageSame=false;
		if(oldOptionPackagesAll&&oldOptionPackagesAll!=null&&oldOptionPackagesAll!=""){
			var oldOptionPackageArr=oldOptionPackagesAll.split(",");
			if(oldOptionPackageArr!=null&&oldOptionPackageArr.length>0){
				var oldOptionPackage=oldOptionPackageArr[0];
				if(oldOptionPackage&&oldOptionPackage!=""&&oldOptionPackage=="-1"){
					oldOptionPackage="";
				}
				if(oldOptionPackage==optionPackages){
					optionPackageSame=true;
				}
			}
			
		}
		
		if(basePackageSame&&optionPackageSame){
			showDetailErrorInfo("", "必选基础包和可选优惠包不能和原来完全一样！", "");
			return false;
		}
	}
	
	if(operSelect=="2"||operSelect=="3"){
		if($("#RES_ID").val()!=$("#RES_NO").val()){
			alert("终端串不一致，请重新校验！");
			return false;
		}
	}

	return true;
	
}

/**
 * 校验终端
 */
function checkTerminal(){
	var resID = $("#RES_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(resID == ""){
		alert("请输入终端串码");
		return false;
	}
	

	/**
	 * 老串号
	 * 宽带项目
	 * @author zhuoyingzhi
	 * 20160928
	 */
   var old_res_id=$("#OLD_RES_ID").val();
   if(old_res_id == ""){
	   alert("请先加载用户信息");
	   return false;
   }
   
	
	var operSelect=$("#cond_OPER_SELECTIONS").val();
	var refreshArea='topSetBoxInfoPart';
	
	if(operSelect=="3"){		//变更产品和机顶盒
		refreshArea=refreshArea+",topSetBoxProductInfoPart";
	}
	
	
	
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('sellTopSetBoxInfoPart', 'checkTerminal', "&RES_ID="+resID + "&SERIAL_NUMBER=" + serialNumber+"&cond_OPER_SELECTIONS="+operSelect, refreshArea,
			function(data) {
				
				$("#Artificial_services").val("0");
				$("#Artificial_services").attr("disabled","true");
		
				$.endPageLoading();
				debugger;
				//页面终端价格(元)展示去掉
				$("#RES_FEE").val("0.0");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


