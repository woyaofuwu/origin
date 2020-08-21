$(window).load(function(data) {

   if ($("#Send_GROUP_ID").val())
     {     
      $("#GROUP_ID_NAME").val($("#Send_GROUP_ID").val());
      groupSearchEnterAction();
     }
});
function selectGroupAfterAction(data){	
	insertGroupCustInfo(data);
	var eparchyCode=data.get("EPARCHY_CODE");
    $("#EPARCHY_CODE").val(eparchyCode);
	var userId = $("#USER_ID").val();	
	if(userId!=null){
	$("#UpByCust").attr('disabled',false);
	}
}

function selectGroupErrorAfterAction() 
{ 
    clearGroupCustInfo(); 
    $("#UpByCust").attr('disabled',true);
}

function UpdateByCustId(obj){
	var custId = $("#CUST_ID").val();
	var eparchyCode=$("#EPARCHY_CODE").val();
	MessageBox.confirm("提示信息",'此操作会更改客户['+custId+']下所有用户的电子发票设置信息，是否继续？',
    function(btn){
		if(btn=='cancel'){
			return;
		}else{
			popupPage('group.creategroupacct.ElecInvoiceInfo','initByCustId','&CUST_ID=' + custId+'&EPARCHY_CODE=' + eparchyCode, '集团客户设置', 650, null, null);	
		}		
	});	
}
function UpdateByUserId(obj){
	var custId = $("#CUST_ID").val();
	var eparchyCode=$("#EPARCHY_CODE").val();
	var userId = obj?$.attr(obj,"USER_ID"):""; 
	//var modifyTag=$("#MODIFY_TAG").val();
   var modifyTag= obj?$.attr(obj,"MODIFY_TAG"):"";
	
	var NewFlag="1";
	if(modifyTag=='0'){
		 NewFlag="0";
	}
	MessageBox.confirm('提示信息','此操作为设置用户['+userId+']的电子发票信息,是否继续？',
		    function(btn){
				if(btn=='cancel'){
					return;
				}else{					
					popupPage('group.creategroupacct.ElecInvoiceInfo','initByUserId','&USER_ID=' + userId+'&CUST_ID=' + custId+'&EPARCHY_CODE=' + eparchyCode+'&NEW_FLAG='+NewFlag , '集团用户设置', 950, null, $.attr(obj, 'id'));		
				}		
			});
	
}





