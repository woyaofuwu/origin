function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'getCustInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'custInfoPart,assurePart', 
	function(){
	
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("´íÎó",error_info);
    });
}

/* check submit */
function checksubmit(){
	if(!$.validate.verifyAll("assurePart")) {
		return false;
	}
	return true;
}