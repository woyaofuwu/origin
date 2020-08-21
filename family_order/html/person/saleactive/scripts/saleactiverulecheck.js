function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'initCampnTypes', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'QueryRecordPart,QueryListPart', function(rtnData) { 
			$.endPageLoading();
			 $('#SERIAL_NUMBER').val(userInfo.get("SERIAL_NUMBER"));
			 $('#USER_ID').val(userInfo.get("USER_ID"));
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			}); 
}
 

function queryProductByType()
{   
	var campnType = $('#SALE_CAMPN_TYPE').val(); 
	var param = "&CAMPN_TYPE="+campnType;  
	$.beginPageLoading("查询营销方案。。。");
	$.ajax.submit(null,'queryProdByLabel',param,'',function(dataset){
		$("#SALE_PRODUCT_ID").empty();
		$("#SALE_PRODUCT_ID").append("<option value=''>--请选择--</option>");
		$.each(dataset,function(key,values){
			$("#SALE_PRODUCT_ID").append("<option value='"+dataset.get(key).get("PRODUCT_ID")+"'>"+dataset.get(key).get("PRODUCT_ID")+"|"+dataset.get(key).get("PRODUCT_NAME")+"</option>");
		}); 
		$.endPageLoading(); 
	},
	function(errorcode, errorinfo){
			$.endPageLoading();
			$.showErrorMessage('营销方案列表查询失败',errorinfo);
			//showDetailErrorInfo(error_code,error_info,derror);
	}); 
} 
function checkByProduct(){
	var prodId = $('#SALE_PRODUCT_ID').val(); 
	var param = "&PRODUCT_ID="+prodId;   
	$.beginPageLoading("查询营销包。。。");
	$.ajax.submit(null,'queryPackageList',param,'',function(dataset){
		$("#SALE_PACKAGE_ID").empty();
		$.each(dataset,function(key,values){  
			$("#SALE_PACKAGE_ID").append("<option value='"+dataset.get(key).get("PACKAGE_ID")+"'>"+dataset.get(key).get("PACKAGE_NAME")+"</option>");
		}); 
		$.endPageLoading(); 
	},
	function(error_code,error_info){
			$.endPageLoading();
			$.showErrorMessage('营销包查询失败',error_info);
	}); 
}


function checkRule(){
	if(!$.validate.verifyAll("QueryRecordPart")){
		return false;
	}
	var campnType = $('#SALE_CAMPN_TYPE').val(); 
	var prodId = $('#SALE_PRODUCT_ID').val(); 
	var packId = $("#SALE_PACKAGE_ID").val();
	var serialNum=$('#SERIAL_NUMBER').val();
	var userId= $('#USER_ID').val();
	var param="&CAMPN_TYPE="+campnType+"&PRODUCT_ID="+prodId+"&PACKAGE_ID="+packId+"&SERIAL_NUMBER="+serialNum;
	$.beginPageLoading("开始校验。。。");
	$.ajax.submit(null,'checkSaleActiveRule',param,'QueryListPart',function(){
		$.endPageLoading(); 
	},
	function(error_code,error_info){
			$.endPageLoading();
			$.showErrorMessage('校验失败',error_info);
	}); 
}

function displaySwitch(btn, o){
    var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
		  div.css('display', 'none');
		  button.children("i").attr('className', 'e_ico-unfold'); 
		  button.children("span:first").text("展示客户信息");
    }else {
       div.css('display', '');
       button.children("i").attr('className', 'e_ico-fold'); 
       button.children("span:first").text("不展示客户信息");
    }
 }