function closeThisPopup(){
	var popMode = $.params.get("POP_MODE");
	if("S" == popMode){
		setPopupReturnValue(this,'a','a',true);
	}else{
		backPopup(this);
	}	
}

function clearThisPopupValue(){
	setValueToFiled('','');
}

function setCheckedToValue(){
	var vals = '';
	var texts = '';
	var num = 0;
	$("#PRODUCT_TREE input[name]").each(function(){
		if(this.checked){
			var val  = this.getAttribute("value");
			var text = $(this).parent().siblings("div.text").attr("title");
			if(num > 0){
				vals = vals + ",";
				texts = texts + ',';
			}
			num++;
			vals = vals + val;
			texts = texts + text;
		}

	});
	setValueToFiled(vals, texts);
}

function setValueToFiled(values, texts){
	var valueCode = $.params.get("VALUECODE");
	var valueName = $.params.get("VALUENAME");
	var popMode = $.params.get("POP_MODE");
	var afterAction= $.params.get("AFTER_ACTION");
	var obj = {};
	obj[valueCode] = values;
	obj[valueName] = texts;
	if("S" == popMode){
		setPopupReturnValue(this, obj);
	}else{
		backPopup(this, obj);
	}
	if(afterAction){
		afterAction = 'parent.' +afterAction+'();'
		eval(afterAction);
	}
}