function getCardSN4USIM(){
	try{
		var cardInfo = downocx2.Get_CardSN();
		return splitResInfo(cardInfo);
	}catch(e){
		alert("调用ocx错误GetCardSN,请检查控件是否安装！");
		return false;
	}
}
function getOPSVersion4USIM(){
	try{
		var cardInfo = downocx2.Get_OPSVersion();
		return splitResInfo(cardInfo);
	}catch(e){
		alert("调用ocx错误TyCqRps_GetOcxInfo");
		return false;
	}
}
function getCardInfo4USIM(){
	try{
		var cardInfo = downocx2.Get_CardInfo();
		return splitResInfo(cardInfo);//需要改造
	}catch(e){
		alert("调用ocx错误ReadICCID");
		return false;
	}
}
function writeCardInfo4USIM(issueData){
	try{
		var cardInfo = downocx2.Write_Card(issueData);
		return splitResInfo(cardInfo);
	}catch(e){
		alert("调用ocx错误Rps_ReadIMSI");
		return false;
	}
}
function getCardErrorInfo4USIM(errCode){
	try{
		var cardInfo = downocx2.Get_OPSErrorMsg(errCode);
		return cardInfo;
	}catch(e){
		alert("调用ocx错误TyCqRps_GetErrMsg");
		return false;
	}
}
function splitResInfo(resultInfo){
	
	var cardInfoArray = new Array();
	cardInfoArray = resultInfo.split('|');
	var resCode = cardInfoArray[1];
	var resInfo = '';
	if(resCode != '0000'){
		var resalert = getCardErrorInfo4USIM(resCode);
		alert(resalert);
		return false;
	}else{
		resInfo = cardInfoArray[0];//此为返回错误信息
	}
	return resInfo;
}