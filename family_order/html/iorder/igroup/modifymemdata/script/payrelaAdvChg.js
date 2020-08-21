// 查询号码订购的集团产品信息
function querygroupinfoBysn(){
	var sn = $("#cond_SERIAL_NUMBER").val();
	if(sn.length <= 0){
		MessageBox.alert("","请输入服务号码！");
		return;
	}
	$.beginPageLoading();
	$.ajax.submit("", "queryMemberInfo", "&cond_SERIAL_NUMBER="+sn, "UserInfoPart,ProducListPart,payInfoPart", 
		function (data) { 
			$.endPageLoading();
			showproduct();
			$("#UserInfoPart").css("display", "");
			$("#GroupInfoPart").css("display", "none");

    	},    	
    	function (errCode, errDesc,errStack) { 
    		MessageBox.error("错误提示", errDesc, "", null, errStack, errDesc);
    		$.endPageLoading();
    		return;
    	}
    );
}

// 展示成员订购的所有集团产品列表
function showproduct(){
	var sn = $("#MEB_SERIAL_NUMBER").val();
	if(sn.length <= 0){
		MessageBox.alert("","请先输入服务号码！");
		return;
	}
	
	// 付费账目区域隐藏
	hidePopup("mypop", "noteItemList");

	// 展示集团产品列表区域
	showPopup("mypop", "offerCatalogPopupItem"); 
}


function chooseOffer(el){     
	
	var offerCode = $(el).attr("PRODUCT_ID");
	var serialnumber = $(el).attr("SERIAL_NUMBER");
	var mebSn = $("#cond_SERIAL_NUMBER").val();
	
	var tag = true;
	
	$.beginPageLoading();
	
	var paramUrl = "&cond_SERIAL_NUMBER=" + mebSn + "&cond_GROUP_SERIAL_NUMBER=" + serialnumber +"&cond_PRODUCT_ID=" + offerCode;
	$.ajax.submit("", "queryMemberAcct", paramUrl, "GroupInfoPart", 
		function (data) { 
			$.endPageLoading();
			
    	}, 
    	function (errCode, errDesc,errStack) { 
    		tag = false;
    		MessageBox.error("错误提示", errDesc, "", null, errStack, errDesc);
    		$.endPageLoading();
    		return;
    	},
    	{async:false}
    );	
	
	if(tag == true){
		
		$("#GroupInfoPart").css("display", "");

	}
	
	// 查询定制付费信息
	var productid = $("#PRODUCT_ID").val();   
	var grpuserid = $("#GRP_USER_ID").val();  
	
	var custid = $("#CUST_ID").val();         
	var mebuserid = $("#MEB_USER_ID").val();  
	var epaychcode = $("#MEB_EPARCHY_CODE").val();  
	var groupid = $("#GROUP_ID").val();            
	// 规则校验
	var checkParam = "&CUST_ID=" + custid;
	checkParam += "&PRODUCT_ID=" + productid;
	checkParam += "&USER_ID=" + grpuserid;
	checkParam += "&USER_ID_B=" + mebuserid;
	checkParam += "&BRAND_CODE_B=" + $("#MEB_BRAND_CODE").val();
	checkParam += "&EPARCHY_CODE_B=" + epaychcode;
	// 规则校验需要返回值(注意:暂时还没有加上去判断)
	pageFlowRuleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ModMemPayRelaRule","checkBaseInfoRule",checkParam); 

	// 初始化付费账目、付费账目列表、
	var param = "&PRODUCT_ID=" + productid;
	param += "&GRP_USER_ID=" + grpuserid;
	param += "&CUST_ID=" + custid;
	param += "&MEB_USER_ID=" + mebuserid;
	param += "&MEB_EPARCHY_CODE=" + epaychcode;
	param += "&GROUP_ID=" + groupid;
	$.beginPageLoading();
	$.ajax.submit("", "initial", param, "payInfoPart,PayPart", 
		function (data) { 
			
			
			var payTypeSet = data.get("PAYPLANSEL_PAY_TYPE_SET");
			if(payTypeSet != undefined && payTypeSet != null){ 
				$('#oldPayPlanSet').val(payTypeSet); 
			}
			var mebPayPlan = data.get("PAYPLANSEL_MEB");
			
			if(mebPayPlan != undefined && mebPayPlan != null){ 
				//$('#PAY_PLAN_SEL_PLAN_TYPE').val(mebPayPlan.get("PLAN_TYPE_CODE"));
				$('#oldMemPay').val(mebPayPlan.get("PLAN_TYPE_CODE"));  
			}
			$('input[type*=radio]').each(function(){
				if($(this).val() == mebPayPlan.get("PLAN_TYPE_CODE"))
					$(this).attr('checked','checked');
				});

			
			selectPayPlan();

			$.endPageLoading();
		}, 
		function (errCode, errDesc,errStack) { 
			$.endPageLoading();
	        MessageBox.error("错误提示", errDesc, "", null, errStack, errDesc);
	        return;
	    },
    	{async:false}
	);
	
	
}


///////////////////////////////////////////////////////////////////////////


//选择付费方式
function selectPayPlan() {
	
	var planType = $("input[name='PAY_PLAN_SEL_PLAN_TYPE']:checked").val() ;
	if(planType == undefined || planType == ""){
		$("#grpPayRels").val('');
		return ;
	}
	
	if (planType == "G") {
		hidePopup('mypop');
		showPopup("mypop", "noteItemList");
		
	} else {
		hidePopup("mypop", "noteItemList");
	}
	
	var oldPlanTypeCode = $('#oldMemPay').val();
	if(planType == oldPlanTypeCode ){
		$("#grpPayRels").val('');
		return ;
	}
	 
	if($('#oldPayPlanSet').val()!="" ){
		var oldPlanSet = $.DatasetList($('#oldPayPlanSet').val());
	    var payPlanList = $.DatasetList();
	    for(var i=0;i<oldPlanSet.length;i++)
		{
			var plan = oldPlanSet.get(i);
			var isFind = "false";
			if(plan.get("PAY_TYPE_CODE")==planType )
			{
				plan.put("PLAN_TYPE",plan.get("PAY_TYPE_CODE")); 
				plan.put("MODIFY_TAG","0");
				isFind = "true";
			}
			if(oldPlanTypeCode && plan.get("PAY_TYPE_CODE")==oldPlanTypeCode)
			{
				plan.put("PLAN_TYPE",plan.get("PAY_TYPE_CODE"));
				plan.put("MODIFY_TAG","1");
				isFind = "true";
			} 
			if(isFind == "true")
			{
				payPlanList.add(plan);
				
			}
		} 
	    
		$("#grpPayRels").val(payPlanList);
	}
}






///////////////////////////////////////////BaseInfo




//重置方法
function clean(){
	
	window.location.reload();

}

//最终的提交
function submit(){
		
		var payPlanList = $("input[name='PAY_PLAN_SEL_PLAN_TYPE']:checked") ;
		if(payPlanList.length==0){ 
			MessageBox.alert("提示信息", "请至少选择一个付费计划", function(btn){

			});
			return false;
		} 
		
		var grpuserid= $("#GRP_USER_ID").val();  
		var serialNumber=$("#MEB_SERIAL_NUMBER").val();
		var productId=$("#PRODUCT_ID").val(); 
		
		if (serialNumber=="") {
		   MessageBox.alert("",'尚未查询成员资料，请输入成员手机号码查询！');
		   return ;
		}
		
		if (grpuserid=="") {
		   MessageBox.alert("",'尚未选择需要变更的集团产品！请在成员订购的集团列表中选择需要变更的集团产品！');
		   return ;
		}
		
		var remark = $('#param_REMARK').val();
		var	payInfo = $("#grpPayRels").val();		
		
		var param = "&MEB_SERIAL_NUMBER=" + serialNumber;
		param += "&GRP_USER_ID=" + grpuserid;
		param += "&PRODUCT_ID=" + productId;
		param += "&PAY_INFO=" + payInfo;
		param += "&REMARK_INFO=" + remark;
		
		//校验并设置提交参数
		$.beginPageLoading();
		
		$.ajax.submit('', "submitPayinfo", param, "", function (data) { 
			var obj = JSON.parse(data);
			MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
				if("ok" == btn){
					window.location.reload();
				}
			});
			$.endPageLoading();
		}, function (errCode, errDesc,errStack) { 
		    MessageBox.error("错误提示", errDesc, "", null, errStack, errDesc);
		    $.endPageLoading();
		});
		
	}	

