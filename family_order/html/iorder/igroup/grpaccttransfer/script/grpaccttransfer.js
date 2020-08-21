
// 获取集团客户标识
function getGrpCustInfo(){

    var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
    var custId = custInfo.get("CUST_ID"); 
    $("#cond_CUST_ID").val(custId);
    $.ajax.submit('', 'initial','&CUST_ID='+custId,'productInfoPart',
    		function(data){
    		   //alert("111");
    		   $.endPageLoading();
    		},
    		function(error_code,error_info,derror){
    		   $.endPageLoading();
    		   showDetailErrorInfo(error_code,error_info,derror);
    		}
    	);	
}

function queryProductInfo(userId){
	$.ajax.submit('', 'queryAcctInfo','&USER_ID='+userId,'acctPart',
		function(data){
		   //alert("111");
		   $.endPageLoading();
		},
		function(error_code,error_info,derror){
		   $.endPageLoading();
		   showDetailErrorInfo(error_code,error_info,derror);
		}
	);	
}

// 选择产品回调方法
function chooseOfferAfterAction(el)
{	
	$.beginPageLoading();
	
	$(el).siblings().removeClass('checked');
    $(el).addClass("checked");
	
	var productId = $(el).attr("PRODUCT_ID");
	var userId = $(el).attr("USER_ID");
	// 查询集团客户信息
	queryProductInfo(userId);
	
	// 查询集团用户的账户信息 
//    $.ajax.submit('', 'queryAcctInfo', '&USER_ID='+userId, 'acctPart',
//		function(data){
//    	  $.endPageLoading();
//	      var x_resultcode = data.get("x_resultcode","0");
//	      if(x_resultcode=='-1'){ 
//	    	  MessageBox.alert("",data.get("x_resultinfo"));
//      		  return;
//	      }else{
//	    	  $('#accountInfo').css("display","block");
//	      }
//		},	
//		function(error_code,error_info,derror){ 
//			showDetailErrorInfo(error_code,error_info,derror);
//	    }
//	);
    
    // 查询集团的付费变更信息
    $.ajax.submit('', 'getProductInfoByAjax', '&PRODUCT_ID='+productId+'&USER_ID='+userId, 'paymodePart,infoListPart',
    	function(data){ 
	    	$.endPageLoading();
	    	//alert("333");
	        var x_resultcode = data.get("x_resultcode","0");
	        if(x_resultcode == '-1'){ 
	        	MessageBox.alert("",data.get("x_resultinfo"));
	        	return;
	        }else{
	        	//alert("222");
	    	    $("#paymodePart").css("display","block"); 
	    	    $("#SubmitPart").css("display","block");  
	        }
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    }
	);
	
}

function submit(flag){	
	
	if(!comparaPayPlans()){return false;}	
	   
	$.beginPageLoading();
	$.ajax.submit("paymodePart,acctPart", "submitChange","", "", 
		function(data){	    
			$.endPageLoading();
			var obj = JSON.parse(data);
	    	MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
	    		if("ok" == btn){
					window.location.reload();
				}
	    	});
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);	
	    }
	);
}

function comparaPayPlans(){ 
	var payPlanResult =  $.DatasetList();
	 
	var payPlanList = $("input[name='USERPAYPLAN_PLANCODE_CHECK']:checked") ;
	 
	if(payPlanList.length==0){ 
		MessageBox.alert("提示信息", "请至少选择一个付费计划", function(btn){

		});
		return false;
	} 
	var oldpayPlanList = $('#OLD_PAYPLANEDIT_PLANINFOS').val();  
	if(oldpayPlanList==''){
		oldpayPlanList='[]';
	}
	var oldselectlist= $.DatasetList(oldpayPlanList); 
	payPlanList.each(function(){
		var checkvalue = $(this).val();
		var existSelectEle = false;
		oldselectlist.each(function(item, index, totalcount){
			if(checkvalue==item.get("PLAN_TYPE_CODE")){
				existSelectEle=true;
				return false;
			}
		});
		if(!existSelectEle){
			var elem = $.DataMap();
			elem.put("PLAN_TYPE_CODE",checkvalue);
			elem.put("MODIFY_TAG","0");
			payPlanResult.add(elem);
		}
		
	});
	
	oldselectlist.each(function(item, index, totalcount){
		var existSelectEle = false;
		payPlanList.each(function(){
			var checkvalue = $(this).val();
			if(checkvalue==item.get("PLAN_TYPE_CODE")){
				existSelectEle=true;
				return false;
			}
		});
		if(!existSelectEle){
			var elem = $.DataMap();
			elem.put("PLAN_TYPE_CODE",item.get("PLAN_TYPE_CODE"));
			elem.put("MODIFY_TAG","1");
			payPlanResult.add(elem);
		}
		
	}); 
	$("#PAYPLAN_INFOS").val(payPlanResult);
 
	return true;
}

function toggleshow(){
	$("#UI_show").css("display","none"); 
	$("#UI_hide").css("display","block"); 
	$("#acctPart").css("display","block");
}

function togglehide(){
	$("#UI_hide").css("display","none"); 
	$("#UI_show").css("display","block"); 
	$("#acctPart").css("display","none");
}
