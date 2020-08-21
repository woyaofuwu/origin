function setData(){	
	setReturnValue(document.getElementById('mainProductSelected').value,document.getElementById('personProductTreeProductName').value);
}

function selectProductA()
{
	var staffEparChycode = $("#STAFF_EPARCHY_CODE").val();;
	document.getElementById("PROUDCT_A_OR_B").value=1;
	ProductSelect.popupProductSelect('0000',staffEparChycode,'');
}

function selectProductB()
{
	
	var staffEparChycode = $("#STAFF_EPARCHY_CODE").val();;
	document.getElementById("PROUDCT_A_OR_B").value=2;
	ProductSelect.popupProductSelect('0000',staffEparChycode,'');
}

function showproductID(productId,productName){
	var productAorB = $("#PROUDCT_A_OR_B").val();
	if (productAorB == 1){
		document.getElementById("cond_PRODUCT_NAME_A").value="[" + productId + "]" + productName;
	}else{
		document.getElementById("cond_PRODUCT_NAME_B").value="[" + productId + "]" + productName;
	}
	
}

/**
 * 提交查询前校验
 */
function queryProductTransInfo(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.ajax.submit('QueryCondPart', 'queryProductTransInfo', null, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}





