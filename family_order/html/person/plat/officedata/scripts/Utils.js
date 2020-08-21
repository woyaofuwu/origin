/**
 * 判断json对象是否为空。没有属性也认为空。
 * 
 * @param obj
 * @returns {Boolean}
 */
function isJSONObjNull(obj) {
	if (obj == null || obj == undefined) {
		return true;
	}
	if (JSONAttrLength(obj) == 0) {
		return true;
	}
	// alert(displayData(objData));
	return false;
};

/**
 * 取得一个json对象里的属性数量
 */
JSONAttrLength = function(obj)// 获得对象上的属性个数，不包含对象原形上的属性
{
	var count = 0;
	if (obj && typeof obj === "object") {
		for ( var ooo in obj) {
			//if (obj.hasOwnProperty(ooo)) {
				count++;
			//}
		}
		return count;
	} else {
		throw new Error("argunment can not be null;");
	}
};

function JSON2String(jsonObj) {
	// return $.DataMap(jsonObj).toString();
	return JSON.stringify(jsonObj);
}

function String2JSON(str) {
	//alert(str);
	return JSON.parse(str);
}

function CopyJSON(jsonObj) {
	var jsonstr = JSON.stringify(jsonObj);
	return JSON.parse(jsonstr);
}

// 判断jsonB的每个属性的值是否与jsonA的相等，相等返回true，否则返回false
function JSONContain(jsonA, jsonB) {
	for ( var item in jsonB) {
		var itemValue = jsonB[item];
		if (itemValue.length > 0) {
			if (jsonA[item] == undefined) {
				return false;
			} else if (jsonA[item] != itemValue) {
				return false;
			}
		}

	}
	return true;
}
