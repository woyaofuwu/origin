//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	$("#cond_SERIAL_NUMBER_INPUT").attr("disabled",false)
	backPopup(obj);
}