function comparaPayPlans(){ 
	var payPlanResult =  $.DatasetList();
	 
	var payPlanList = $("input[name='USERPAYPLAN_PLANCODE_CHECK']:checked") ;
	 
	if(payPlanList.length==0){ 
		alert('请至少选择一个付费计划！');
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

function renderPayPlanEditPart(productId, userId){

	var param = '&PRODUCT_ID='+productId+'&USER_ID='+userId;
	cleanPayPlanEditPart();
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit.PayPlanEditHttpHandler','rendPayPlanEditInfo',param,
    	function(data){
    		if(data == null){
    			return ;	
    		}
    		var payPlanSrc = data.get("PAYPLAN_SRC");
    		var payPlanList = data.get("PAYPLAN_LIST");
    		if(payPlanSrc){
    			insertPayPlanList(payPlanSrc);
    		}
    		
    		if(payPlanList){
    			$('#OLD_PAYPLANEDIT_PLANINFOS').val(payPlanList);
    		}
    		
    	},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		});
	
}


function insertPayPlanList(payPlans){
	payPlans.each(function(item,idx){
		
		$("#payplanedit_userplanlist").prepend(makePayPlanHtml(item));
			
	});
}
function makePayPlanHtml(item){
	
	var planType = item.get('PLAN_TYPE');
	var planChecked = item.get('CHECKED','');
	var planName = item.get('PLAN_NAME');
	var planTypeName = item.get('PLAN_TYPE_NAME');
	var payItemsDesc = item.get('PAY_ITEMS_DESC','');
	var planDesc = item.get('PLAN_DESC');
	if(planChecked != 'true')
		planChecked ='false';
	
	var html="";
	html += '<tr>';
	
	html += '<td class="e_center" ><input id="USERPAYPLAN_PLANCODE_CHECK" name="USERPAYPLAN_PLANCODE_CHECK" type="checkbox" value="'+planType+'" checked="'+planChecked+'" playCode="'+planType+'" /></td>';
	html += '<td class="e_center">' + planName+ '</td>';
	html += '<td class="e_center">' + planTypeName+ '</td>';
	html += '<td class="e_center">' + payItemsDesc+ '</td>';
	html += '<td class="e_center">' + planDesc+ '</td>';
	html += '</tr>';
	
	return html;
}

function cleanPayPlanEditPart(){
	$("#payplanedit_userplanlist").html('');
	$('#OLD_PAYPLANEDIT_PLANINFOS').val('');
}
