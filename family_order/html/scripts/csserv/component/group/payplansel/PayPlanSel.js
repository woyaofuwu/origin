function selectPayPlan() {
	
	var planType = $('#PAY_PLAN_SEL_PLAN_TYPE').val();
	
    var afterFunction = $("#PAYPLAN_SEL_AFTER_ACTION").val();
    if (afterFunction != null && afterFunction != "") {
        eval(""+afterFunction);
    }
	
	var mode = $('#PAYPLAN_SEL_STYLE_MODE').val();
	if (planType == "G" && mode != 'simple') {
		$('#PAYPLANSEL_PAYITEM_PART').css("display", "");
		
	} else {
	    $('#PAYPLANSEL_PAYITEM_PART').css("display", "none");
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

function checkPayPlan() {
	var planType = $('#PAY_PLAN_SEL_PLAN_TYPE').val();;
	if(planType==''){
		alert('\u8BF7\u8F93\u9009\u62E9\u4ED8\u8D39\u7C7B\u578B\uFF01');
		$('#PAY_PLAN_SEL_PLAN_TYPE').focus();
        return false;
	}
	return true;
}

//初始信息状态
function cleanPayPlanSelPart(){
	var mode = $('#PAYPLAN_SEL_STYLE_MODE').val();
	if(mode != 'simple'){
		$("#payplansel_payitem_body").html('');
	}
	var roleObj = $('#PAY_PLAN_SEL_PLAN_TYPE');
	roleObj.html('');
	var roleSelInnerHTML = [];
	roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}


function renderPayPlanSel(productId, grpUserId){
	cleanPayPlanSelPart();
	if (grpUserId == ""){ 
		$.showWarnMessage('获取集团用户ID出错！','集团用户ID不能为空');
		return false;
	}
	
	$.beginPageLoading();
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplansel.PayPlanSelHttpHandler","qryPayPlanInfosByUserIdAndProductIdForGrp","&USER_ID="+grpUserId+"&PRODUCT_ID="+productId,
    	function(data){
    		if(data != null){
    			var payTypeSet = data.get("PAYPLANSEL_PAY_TYPE_SET")
    			if(payTypeSet != undefined && payTypeSet != null){
					insertPlanTypesHtml(payTypeSet);
				}
				var mode = $('#PAYPLAN_SEL_STYLE_MODE').val();
				if(mode != 'simple'){
					var payItems = data.get("PAYPLANSEL_PAY_ITEMS");
					if(payItems != undefined && payItems != null){
						insertPayItemsHtml(payItems);
					}
				}
				
				selectPayPlan();
			}
			$.endPageLoading();
	    	
	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
	    	
	    },{async:false});
	    
}

function renderGrpMemPayPlanSel(productId, grpUserId,mebUserId,mebEparchyCode){ 
	cleanPayPlanSelPart();
	if (grpUserId == ""){ 
		$.showWarnMessage('获取集团用户ID出错！','集团用户ID不能为空');
		return false;
	}
	
	$.beginPageLoading();
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplansel.PayPlanSelHttpHandler","getGrpMemPayPlanByUserId","&USER_ID="+grpUserId+"&PRODUCT_ID="+productId+"&MEB_USER_ID="+mebUserId+"&MEB_EPARCHY_CODE="+mebEparchyCode,
    	function(data){
    		if(data != null){
    			
    			var payTypeSet = data.get("PAYPLANSEL_PAY_TYPE_SET");
    			if(payTypeSet != undefined && payTypeSet != null){ 
					insertPlanTypesHtml(payTypeSet);
					$('#oldPayPlanSet').val(payTypeSet); 
				}
				var mode = $('#PAYPLAN_SEL_STYLE_MODE').val();
				var mebPayPlan = data.get("PAYPLANSEL_MEB");
				if(mebPayPlan != undefined && mebPayPlan != null){ 
					$('#PAY_PLAN_SEL_PLAN_TYPE').val(mebPayPlan.get("PLAN_TYPE_CODE"));
					$('#oldMemPay').val(mebPayPlan.get("PLAN_TYPE_CODE"));  
				}
				if(mode != 'simple'){
					var payItems = data.get("PAYPLANSEL_PAY_ITEMS");
					if(payItems != undefined && payItems != null){
						insertPayItemsHtml(payItems);
					}
				}
				
				selectPayPlan();
			}
			$.endPageLoading();
	    	
	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
	    	
	    },{async:false});
	    
}
function insertPlanTypesHtml(data){
	var leng = data.length;
	var roleObj = $('#PAY_PLAN_SEL_PLAN_TYPE');
	roleObj.html('');
	var roleSelInnerHTML = [];
	if(leng > 1 || leng==0){
		roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	}
	
	for(var i =0;i<leng;i++){
		var info = data.get(i);
		var payTypeCode = info.get("PAY_TYPE_CODE");
		var payType = info.get("PAY_TYPE");
		roleSelInnerHTML.push( '<option value="'+payTypeCode+'">'+payType+'</option>');
	}
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}

function insertPayItemsHtml(payItems){
	payItems.each(function(item,idx){
		
		$("#payplansel_payitem_body").prepend(makePayItemRowHtml(item));
			
	});
}

function makePayItemRowHtml(item){
	var paraCode1 = item.get('PARA_CODE1');
	var paraCode1Name = item.get('NOTE_ITEM');
	var html="";
	html += '<tr>';
	html += '<td >' + paraCode1+ '</td>';
	html += '<td >' + paraCode1Name+ '</td>';
	html += '</tr>';
	return html;
}

function getPayPlanValue(){
	return $('#PAY_PLAN_SEL_PLAN_TYPE').val();
}

