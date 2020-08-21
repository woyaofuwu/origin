function initPageParam_110000002222()
{
	if($.os.phone){
		var publicInfos = $("[name='openOfferChaId']");
		for(var i = 0, size = publicInfos.length; i < size; i++){
			var publicInfo = publicInfos[i];
			publicInfo.style.cssText="width:13.5em";
		}
	}
}

//提交
function checkSubOpte(obj)
{
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}