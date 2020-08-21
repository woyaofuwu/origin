function validate()
{
	if(!$.validate.verifyAll("previewInfoPart")) return false;
	
	var mebCount = $("#MEB_COUNT").val();
	
	if(!confirm("还有[" + mebCount + "]位成员使用了集团产品,是否确认一起状态变更!"))
	{
		return false;
	}
	
	return true;
}