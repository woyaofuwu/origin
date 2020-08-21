function initProdChaSpec_120000001160(obj) {
	if(obj){
        for(var i=0;i<obj.length;i++){
            var data=obj.get(i);
            $("#"+data.get("ATTR_CODE")).val(data.get("ATTR_VALUE"));
        }
	}
}


//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}
