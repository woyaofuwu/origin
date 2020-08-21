function refreshPartAfterAuth(data) 
{
	var userInfo = data.get("USER_INFO");
	var userId = userInfo.get("USER_ID");
	var param = "&USER_ID=" + userId;
	//$.beginPageLoading("用户资料查询中...");
	$.ajax.submit('AuthPart', 'afterAuthLoadChildInfo', param, 'memberListPart', function(ajaxData) {
		//加载家庭产品
		var productName = ajaxData.get('PRODUCT_NAME');
		var familySn = ajaxData.get('FAMILY_SN');
		var eparhyCode = ajaxData.get('EPARCHY_CODE');
		$("#FAMILY_PRODUCT_NAME").html(productName);
		$("#FAMILY_SN").val(familySn);
		$("#EPARCHY_CODE").val(eparhyCode);
		$.endPageLoading();
	}, function(error_code, error_info) {
		$.endPageLoading();	
		//$.MessageBox.alert(error_code, error_info, detail);
		$.MessageBox.alert(error_code, error_info, function(){
			$("#CSRESET_BUTTON").click();	
		});
		
		//触发重置按钮点击事件
		//$("#CSRESET_BUTTON").click();		
		//将提交按钮置灰
		$.cssubmit.disabledButton(true,$("#CSSUBMIT_BUTTON"))
	});
}




/**
 * 提交之前校验
 */
function submitBeforeCheck(){
	//debugger;
	var gpi = $('#GRP_PRODUCT_ID').val();
	var UNFamilyStyle = $("#UNFamily").css("display");
	
	if(""==gpi && UNFamilyStyle=="block"){
		alert('请选择家庭注销恢复产品！');
		return false;
	}
	
	$.cssubmit.dynamicParamData.put("SERIAL_NUMBER", $("#FAMILY_SN").val());
	$.cssubmit.dynamicParamData.put("EPARCHY_CODE", $("#EPARCHY_CODE").val());
	return true;
}



/*function destroyRecoveProduct() {
	debugger;
	//恢复产品串
	$("#RECOVEPRODUCTSTR").val("");
  

	
	//0:恢复办理前产品   1：变更到默认产品   2：不变更产品
	var grp_product_id = $("#GRP_PRODUCT_ID").val();
	var user_id =$.auth.getAuthData().get("USER_INFO").get("USER_ID");
	
	var relationTypeCode = $("#RELATION_TYPE_CODE").val();

	
	var param = '&GRP_PRODUCT_ID='+grp_product_id+'&USER_ID='+user_id+'&RELATION_TYPE_CODE='+relationTypeCode;
	//随心用退订恢复产品查询  
	if(grp_product_id!=''){
		$.ajax.submit('', 'destroyRecoveProduct', param, '', 
				function(ajaxData){
			          if(ajaxData!=null){
			        	  //alert(ajaxData.toString());
			        	  debugger;
			        	  $("#RECOVEPRODUCTSTR").val(ajaxData.toString());
			          }
					$.endPageLoading();
				
				},
				function(code, info){
					$.endPageLoading();
					alert(info);
				});
	}
}*/




/**
 * 
 */
/*function isBackTV(){
	debugger;
    var cells = $("input[name=checkBoxIndex]");
	for ( var i = 0; i < cells.length; i++) {
		if (cells[i].checked) {
			var data = hitvInfoTable.getRowData(i);
			if("否"==data.get('CANCLE_TAG')){
				var jsonData = $.DataMap();
				jsonData["CANCLE_TAG"] = "是"; 
				hitvInfoTable.updateRow(jsonData, i);
			}
		}else
			{
			var data = hitvInfoTable.getRowData(i);
			if("是"==data.get('CANCLE_TAG')){
				var jsonData = $.DataMap();
				jsonData["CANCLE_TAG"] = "否"; 
				hitvInfoTable.updateRow(jsonData, i);
			}
			}
	}	
	
	 
	var rHitvTable = hitvInfoTable.getCheckedRowsData("checkBoxIndex", "MY_HITV_SN");
	if(null==rHitvTable){
		$("#backHitv").css("display","none");
	}else
		{
			$("#backHitv").css("display","block");
		}


}*/


/**
 * 是否退订TV+
 */
/*function isCancleTV(){
	debugger;
	var isCancle = $("#switchTV").val();
	var isCancleTVtag = "";
	if("on"==isCancle){
		
		 $("#isCancleTVtag").val("0");
		 $("#discntInfo").css("display","block");
		 $("#backHitv").css("display","block");
		
	}else if("off"==isCancle){
		
		 $("#isCancleTVtag").val("1");
		 $("#discntInfo").css("display","none");
		 $("#backHitv").css("display","none");
		 
	}
	
}*/

