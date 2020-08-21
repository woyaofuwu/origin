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
	function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
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
	function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	}); 
}