
//Auth组件查询后调用方法
function checkInfo()
{
	//查询条件校验
	if(!$.validate.verifyAll("ContInfoPart")) {
		return false;
	}
	return true;
}
