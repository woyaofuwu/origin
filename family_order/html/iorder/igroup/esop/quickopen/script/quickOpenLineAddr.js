function changeAreaByCity()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("cond_CITY");
	if (cityField.value == '海口')	{
		cond_AREA.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			cond_AREA.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		cond_AREA.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			cond_AREA.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		cond_AREA.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				cond_AREA.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};

//快速开通标准地址查询
function queryLineAddr(){
	debugger;
	var addrStandard = $("#cond_STANDARD_ADDR").val(); 
	var city = $("#cond_CITY").val(); 
	var area = $("#cond_AREA").val(); 
	var param = "&cond_STANDARD_ADDR="+addrStandard+"&cond_CITY="+city+"&cond_AREA="+area;
	ajaxSubmit(this,'queryLineAddr',param,"AddrResultPart",null,null);
}

//快速开通覆盖地址查询
function queryLineCoverAddr(){
	debugger;
	var addrStandard = $("#cond_STANDARD_ADDR").val(); 
	var coverTag = $("#cond_COVERTAG").val(); 
	var param = "&cond_STANDARD_ADDR="+addrStandard+"&cond_COVERTAG="+coverTag;
	ajaxSubmit(this,'queryLineCoverAddr',param,"AddrResultPart",null,null);
}