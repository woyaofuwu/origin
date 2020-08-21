function getDateByKey(arr,key){
	for(var i=0; i<arr.length;i++){
		if(arr[i].indexOf(key + ":")!=-1){
			return arr[i].substr(arr[i].indexOf(':')+1,arr[i].length);
		}
	}
	return "";
}

function getMsgByEForm(cardnoID,nameID,sexID,nationID,bornID,addressID,policeID,acdtivityID){
	var arg = new Object();
	var parameter = "";
	///eb/ebill/jsp/commonbill/idcardscan.jsp
	var str = window.showModalDialog("/eb/ebill/jsp/commonbill/idcardscan.jsp", arg, parameter);
	if(!str){
		alert("身份证扫描返回数据错误!");
		return;
	}
	var data = str.split("|"); 
	var name = getDateByKey(data,'name');
	var sex = getDateByKey(data,'sex');
	var nation = getDateByKey(data,'nation');
	var born = getDateByKey(data,'born');
	var cardno = getDateByKey(data,'cardno');
	var address = getDateByKey(data,'address');
	var newaddress = getDateByKey(data,'newaddress');
	var police = getDateByKey(data,'police');
	var acdtivity = getDateByKey(data,'acdtivity');
	var backbase64 = getDateByKey(data,'backbase64');
	var frontbase64 = getDateByKey(data,'frontbase64');
	if(acdtivity) {
		//当前日期格式化
		var toDay=new Date();
		var toMonth=(toDay.getMonth()+1).toString();
		if(toMonth.length==1){
			toMonth="0"+toMonth;
		}
		var thisDay=toDay.getDate().toString();
		if(thisDay.length==1){
			thisDay="0"+thisDay;
		} 
		var toDay=toDay.getFullYear().toString()+toMonth+thisDay;
		//有效期格式化
		var acdtivity2=acdtivity.substring(acdtivity.indexOf("-")+1);
		acdtivity2=parseInt(acdtivity2.replace(".", "").replace(".", ""));  
		if(acdtivity2<toDay){
			alert("证件有效期已过!有效期为："+acdtivity);
			return;
		}else{
			setEFormElementValue(acdtivityID,formatDate(acdtivity.substr(acdtivity.indexOf('-')+1,acdtivity.length).replace('.','-').replace('.','-')));
		}
	}
	var tag = (sexID == "PIC_INFO") ? false:true;
	var t = ("custInfo" == cardnoID.split("_")[0]) ? "custInfo_":"";
	if(name) setEFormElementValue(nameID,name);
	if(sex && tag) setEFormElementValue(sexID,sex);
	if(nation) setEFormElementValue(nationID,nation);
	var back = "";
	var front = "";
	if(!tag){
		if(cardnoID == "FORM_PSPT_ID"){//原客户证件扫描
			back = "FORM_BACKBASE64";
			front = "FORM_FRONTBASE64";
			setEFormElementValue("FORM_SCAN_TAG","0");
		}else{//经办人证件扫描
			back = t+"AGENT_BACKBASE64";
			front = t+"AGENT_FRONTBASE64";
			setEFormElementValue(t+"AGENT_SCAN_TAG","0");
		}
	}else{//客户证件扫描
		back = t+"BACKBASE64";
		front = t+"FRONTBASE64";
		setEFormElementValue(t+"SCAN_TAG","0");
	}
	if(backbase64) setEFormElementValue(back,backbase64);
	if(frontbase64) setEFormElementValue(front,frontbase64);
	if(born.length==8){ 
		setEFormElementValue(bornID,born.substring(0,4)+"-"+born.substring(4,6)+"-"+born.substring(6));
	}else{ 
		if(born) setEFormElementValue(bornID,formatDate(born.replace('年','-').replace('月','-').replace('日','')));
	}
	if(cardno) setEFormElementValue(cardnoID,cardno);
	if(address) setEFormElementValue(addressID,address);
	if(police) setEFormElementValue(policeID,police);
	
}
//跨区补卡专用
function getMsgByEFormKQBK(cardnoID,nameID,sexID,nationID,bornID,addressID,policeID,acdtivityID){
	var arg = new Object();
	var parameter = "";	 
	var str = window.showModalDialog("/eb/ebill/jsp/commonbill/idcardscan.jsp", arg, parameter);
	if(!str){
		alert("身份证扫描返回数据错误!");
		return;
	}
	var data = str.split("|"); 
	var name = getDateByKey(data,'name');
	var sex = getDateByKey(data,'sex');
	//var nation = getDateByKey(data,'nation');
	var born = getDateByKey(data,'born');
	var cardno = getDateByKey(data,'cardno');
	var address = getDateByKey(data,'address');
	//var newaddress = getDateByKey(data,'newaddress');
	var police = getDateByKey(data,'police');
	var acdtivity = getDateByKey(data,'acdtivity');
	var backbase64 = getDateByKey(data,'backbase64');
	var frontbase64 = getDateByKey(data,'frontbase64');
	var headbase64 = getDateByKey(data,'headbase64');
	
	if(name) setEFormElementValue(nameID,name);
	if(cardno) setEFormElementValue(cardnoID,cardno);
	
	if(acdtivity) {
		// 当前日期格式化
		var toDay=new Date();
		var toMonth=(toDay.getMonth()+1).toString();
		if(toMonth.length==1){
			toMonth="0"+toMonth;
		}
		var thisDay=toDay.getDate().toString();
		if(thisDay.length==1){
			thisDay="0"+thisDay;
		} 
		var toDay=toDay.getFullYear().toString()+toMonth+thisDay;
		// 有效期格式化
		var acdtivity2=acdtivity.substring(acdtivity.indexOf("-")+1);
		acdtivity2=parseInt(acdtivity2.replace(".", "").replace(".", ""));  
		if(acdtivity2<toDay){
			alert("证件有效期已过!有效期为："+acdtivity);
			return;
		}else{
			//setEFormElementValue(acdtivityID,formatDate(acdtivity.substr(acdtivity.indexOf('-')+1,acdtivity.length).replace('.','-').replace('.','-')));
		}
	}
	 
	var	back = "BACKBASE64";
	var	front = "FRONTBASE64";	 
	var	head = "HEADBASE64";	 
	var	cardSex = "CARD_SEX";	 
	var	cardAddress = "CARD_ADDRESS";	 
	var	cardBorn = "CARD_BORN";	 
	var	effDate = "EFF_DATE";	 
	var	cardIssued = "CARD_ISSUED";	 
	if(headbase64) setEFormElementValue(head,headbase64);
	if(sex) setEFormElementValue(cardSex,sex);
	if(address) setEFormElementValue(cardAddress,address);
	if(born) setEFormElementValue(cardBorn,born);
	if(acdtivity) setEFormElementValue(effDate,acdtivity);
	if(police) setEFormElementValue(cardIssued,police);
	if(backbase64) setEFormElementValue(back,backbase64);
	if(frontbase64) setEFormElementValue(front,frontbase64);
    
	setEFormElementValue("SCAN_TAG","0");
}
function setEFormElementValue(objName,value){
	if(!objName){
		return;
	}	
	var arr = objName.split(",");
	for(var i=0; i<arr.length;i++){
		if($("#"+arr[i]) && $("#"+arr[i]).length){
			setEFormValue($("#"+arr[i]),value);
		}
	}	

}
function setEFormValue(obj,value){
	var tagName=obj[0].tagName.toUpperCase();
	if(tagName == "INPUT" || tagName == "TEXTAREA"){
		obj.val(value);
	}else if(tagName == "SELECT" ){
		obj.find("option").each(function(){
			if(($(this).html()).indexOf(value)==-1){
				return true;
			}
			$(this).attr("selected", true);
			return false;
	     });
	}
}
function formatDate(DateIn){   
    var year = 0;   
    var month = 0;   
    var day = 0;       
    var curDate = "";
    var arr = DateIn.split("-");
    if(arr.length == 3){
    	year  = arr[0];
    	month  = Math.abs(arr[1]);
    	day  = Math.abs(arr[2]);
    }
    else{ 
    	DateIn = new Date();       
    	year  = DateIn.getFullYear();
    	month  = DateIn.getMonth()+1;
    	day  = DateIn.getDate();
    }
    curDate = year + "-";   
    if (month >= 10 ){   
        curDate = curDate + month + "-";   
    } else{   
        curDate = curDate + "0" + month + "-";   
    }   
    if (day >= 10 ){   
        curDate = curDate + day ;   
    } else{   
        curDate = curDate + "0" + day ;   
    }   
    return curDate;   
} 