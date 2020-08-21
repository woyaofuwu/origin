function submit()
{
	if(!$.validate.verifyAll("infoPart"))
	{
		return;
	}
    var serialNo = $("#SERIALNO").val(); 
    var productNo = $("#PRODUCT_NO").val(); 
    var nodeType = $("#NODE_TYPE").val(); 
    
    var info = "&SERIALNO=" + serialNo + "&PRODUCT_NO=" +productNo +"&NODE_TYPE=" +nodeType ;
    
    $.ajax.submit('', 'submit',info,'',
		function(data){
		   $.endPageLoading();
		},
		function(error_code,error_info,derror){
		   $.endPageLoading();
		   showDetailErrorInfo(error_code,error_info,derror);
		}
	);	
}