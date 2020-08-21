
/******************************************报停 js 开始*************************************************/

//业务提交
function submitBeforeCheck()
{	
	var param = "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	$.cssubmit.addParam(param);
    return true;
}
