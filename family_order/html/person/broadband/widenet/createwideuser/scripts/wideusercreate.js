PAGE_FEE_LIST = $.DataMap();
/* auth查询后自定义查询 */
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();

    $.beginPageLoading("业务资料查询。。。");
	$.ajax.submit(this, 'loadChildInfo', param, 'productType', function(data)
	{
	    initProduct();
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
    
	$("#PHONE").val($("#AUTH_SERIAL_NUMBER").val());
    $("#MODEM_STYLE").val("");
	$("#MODEM_NUMERIC_CODE").val("");
	$("#MODEM_STYLE").attr("disabled", false);
}

/*初始化产品*/
function initProduct(){
       offerList.renderComponent("",$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"));
            
//		selectedElements.renderComponent("&NEW_PRODUCT_ID=",$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"));
}

function checkSerialNumber(){
   var preWideType = $("#PREWIDE_TYPE").val();
   var serialNumberA = $("#SERIAL_NUMBER_A").val();

   if(preWideType==null||preWideType==""){
     alert("请先选择账号类型");
     $("#SERIAL_NUMBER_A").val("");
     return false;
   }
   if(serialNumberA==null||serialNumberA==""){
   	  alert("请输入主号码");
      return false;
   }
   if( $("#SERIAL_NUMBER").val() == serialNumberA){
      alert("开户号码不能与主号码一致!");
      return false;
   }
   var param = "&SERIAL_NUMBER_A="+serialNumberA +"&PREWIDE_TYPE="+preWideType+"&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
   $.beginPageLoading("主号码校验开始。。。");
   $.ajax.submit('', 'checkSerialNumber', param, 'allAcct', function(data){
            $("#GPON_USER_ID").val(data.get(0).get("GPON_USER_ID"));
            $("#GPON_SERIAL_NUMBER").val(data.get(0).get("GPON_SERIAL_NUMBER"));
            $("#USER_ID_A").val(data.get(0).get("USER_ID_A"));
		    $.endPageLoading();
	},
	function(error_code,error_info){
	         $("#SERIAL_NUMBER_A").val("");
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
    });

}




/*密码组件前校验*/
function PasswdbeforeAction() {
     if($("#WIDE_PSPT_ID").val()==""){
      alert("证件号码不能为空！");
      return false;
    }
    //将值赋给组件处理
    var psptId =$("#WIDE_PSPT_ID").val();
    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) {

$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}


function changeProduct()
{	 
    $.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("RES_FEE"));
	var productId=$("#WIDE_PRODUCT_ID").val();
	var eparchyCode=$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
    var param = "&NEW_PRODUCT_ID="+productId;
    offerList.renderComponent(productId,eparchyCode);
    selectedElements.renderComponent(param,eparchyCode);
    //$("#MODEM_STYLE").attr("disabled", true);
    //TODO 获取产品费用
    
    var feeData = $.DataMap();
    feeData.clear();
	feeData.put("MODE", "2");
	feeData.put("CODE", "0");
	feeData.put("FEE",  "0");
	feeData.put("PAY",  "0");		
	feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
	$.feeMgr.insertFee(feeData);
  
}

function afterRenderSelectedElements(data){
	var temp = data.get(0);
	$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
}

 

function onSubmit(){ 
	//REQ201510260003 光猫申领提示优化【2015下岛优化】
	//chenxy3 20151027 提交时候提示领取光猫
	if("613" == $("#TRADE_TYPE_CODE").val()){
		var serialNum=$("#AUTH_SERIAL_NUMBER").val();
		var tips="";
		if(serialNum.substring(0,1)=="0"){
			tips="FTTH宽带办理:开户完成后，请在“FTTH商务光猫申请”界面申领光猫。你确认要继续吗？";
		}else{
			tips="FTTH宽带办理:开户完成后或者参加宽带1+活动后，请在“FTTH光猫申请”界面申领光猫。你确认要继续吗？";
		}
		 
		if(window.confirm(tips)){
			    if(!verifyAll('widePart'))
			   {
			    	return false;
			   	} 
			    if(!verifyAll('modemPart'))
			    {
			 	   return false;
			    }
			    var data = selectedElements.getSubmitData();
			  if(data&&data.length>0){
			  	var param = "&SELECTED_ELEMENTS="+data.toString();	
			    }else{
			       alert('您未选择开户产品，不能提交！');
			       return false;
			    }  
			    if("611" == $("#TRADE_TYPE_CODE").val()){
				 param+="&GPON_USER_ID="+$("#GPON_USER_ID").val()+"&GPON_SERIAL_NUMBER="+$("#GPON_SERIAL_NUMBER").val()
				 +"&USER_ID_A="+$("#USER_ID_A").val();
				}
			    	$.cssubmit.addParam(param);
			     return true; 
 
		}else{
			return false;
		}
	}else{
		 if(!verifyAll('widePart'))
		   {
		    	return false;
		   	} 
		    if(!verifyAll('modemPart'))
		    {
		 	   return false;
		    }
		    var data = selectedElements.getSubmitData();
		  if(data&&data.length>0){
		  	var param = "&SELECTED_ELEMENTS="+data.toString();	
		    }else{
		       alert('您未选择开户产品，不能提交！');
		       return false;
		    }  
		    if("611" == $("#TRADE_TYPE_CODE").val()){
			 param+="&GPON_USER_ID="+$("#GPON_USER_ID").val()+"&GPON_SERIAL_NUMBER="+$("#GPON_SERIAL_NUMBER").val()
			 +"&USER_ID_A="+$("#USER_ID_A").val();
			}
		    	$.cssubmit.addParam(param);
		     return true; 
		}

}


function setModemNumeric(){
    if($("#MODEM_STYLE").val() == ""){
      $("#MODEM_NUMERIC_CODE").val("");
      alert('MODEM方式不能为空！');
      return false;
    }
	if($("#MODEM_STYLE").val()!="1"){//1:自备
	        $("#MODEM_NUMERIC").attr("disabled", false);
		    $("#MODEM_NUMERIC").attr("nullable", "no");
		    $("#MODEM_NUMERIC_CODE").val('ADSL-S6307HV（销售）');
				    
		     if($("#MODEM_STYLE").val() == "2"){//铁通MODEM租用费用 属于预存
		            PAGE_FEE_LIST.clear();
			        var feeData = $.DataMap();
		          	feeData.clear();
					feeData.put("MODE", "2");
					feeData.put("CODE", "201");
					feeData.put("FEE",  "8800");
					feeData.put("PAY",  "8800");		
					feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
					
					$.feeMgr.insertFee(feeData);	
					PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));	
			 }else if($("#MODEM_STYLE").val() == "3"){//铁通MODEM购买费用 属于营业费用
			        PAGE_FEE_LIST.clear();
			        var feeData = $.DataMap();
		          	feeData.clear();
					feeData.put("MODE", "0");
					feeData.put("CODE", "9205");
					feeData.put("FEE",  "8800");
					feeData.put("PAY",  "8800");		
					feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
						
					$.feeMgr.insertFee(feeData);
					PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));
			 }
		    
	}else{
	        $("#MODEM_NUMERIC").attr("disabled", true);
		    $("#MODEM_NUMERIC").attr("nullable", "yes");
		    $("#MODEM_NUMERIC_CODE").val("");
		    
		    PAGE_FEE_LIST.clear();
		    var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "200");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
			$.feeMgr.insertFee(feeData);
			PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));
	}	

      $("#MODEM_STYLE").attr("disabled", true);
}

function checkFTTH()
{
	//先判断是不是FTTH宽带开户，613FTTH宽带开户
	if("613" == $("#TRADE_TYPE_CODE").val()){
		
		var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
		
		if(window.confirm("是否申领光猫？")){
			
			//再判断是个人宽带还是商务宽带
			if(serialNumber.substring(0,1)=="1"){//个人手机用户宽带
				openNav('FTTH光猫申领','broadband.widenet.ftthmodermapply.FTTHModermApply', '','');
			}else{//商务宽带
				openNav('FTTH商务宽带光猫申领','ftthbusimodemapply.FTTHBusiModemApply', '','');
			}
			
		}else{
			
			var href = window.location.href;
			if(href){
				if(href.lastIndexOf("#nogo") == href.length-5){
					href = href.substring(0, href.length-5);
				}
				var url = href.substring(0, href.indexOf("?"));
				var reqParam = href.substr(href.indexOf("?")+1);
					
				var paramObj = $.params.load(reqParam);
				paramObj.remove("SERIAL_NUMBER");
				paramObj.remove("DISABLED_AUTH");
				paramObj.remove("AUTO_AUTH");
				var param = paramObj.toString();
				param += "&AUTO_AUTH=false";   ////受理成功以后，返回页面界面，禁用之前逻辑中的自动刷新
				window.location.href = url+"?"+param;
			}
			
		}
	}else{
		var href = window.location.href;
		if(href){
			if(href.lastIndexOf("#nogo") == href.length-5){
				href = href.substring(0, href.length-5);
			}
			var url = href.substring(0, href.indexOf("?"));
			var reqParam = href.substr(href.indexOf("?")+1);
					
			var paramObj = $.params.load(reqParam);
			paramObj.remove("SERIAL_NUMBER");
			paramObj.remove("DISABLED_AUTH");
			paramObj.remove("AUTO_AUTH");
			var param = paramObj.toString();
			param += "&AUTO_AUTH=false";   ////受理成功以后，返回页面界面，禁用之前逻辑中的自动刷新
			window.location.href = url+"?"+param;
		}
	}
}

function changeType(){
	
//	alert($("#ADSL_TYPE").val()+$.auth.getAuthData());
//	if($("#ADSL_TYPE").val() == "1")
//	{
//		$("#FtthAddress").css("display", "none");
//		$("#AdslAddress").css("display", "");
//		$("#fttb-modem").css("display", "none");
//		$("#adsl-modem").css("display", "");
//	}
//	else if ($("#ADSL_TYPE").val() == "2")
//	{
//		$("#FtthAddress").css("display", "");
//		$("#AdslAddress").css("display", "none");
//		$("#fttb-modem").css("display", "");
//		$("#adsl-modem").css("display", "none");
//	}
	
	if($("#ADSL_TYPE").val() == '' || $.auth.getAuthData() == 'undefined')
		return false; 
	
	var eparchyCode=$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	
	var inparam = "&ADSL_TYPE="+$("#ADSL_TYPE").val() + "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&ROUTE_EPARCHY_CODE="+eparchyCode;
	$.ajax.submit(null, 'getProductInfo', inparam, 'productType,widePart,modemPart', function(data) {
	   if(data.getCount() == 0)
	   {
		  alert("未获取到FTTB/FTTH宽带产品信息，请检查产品权限！");
	      return false;
	   }
//	   	$("#STAND_ADDRESS").val("");
//	   	$("#STAND_ADDRESS_CODE").val("");
      },
	    function(error_code,error_info){
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
   });
	
}
