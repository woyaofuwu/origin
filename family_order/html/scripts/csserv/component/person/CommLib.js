(function($){
	$.extend({toollib:{
		//判断是否为空方法
		isBlank : function(val){
			if(!val || val == null || $.trim(val).length == 0) return true;
		},
		//判断是否中文
		isChinese : function(val){
			var model = /^[\u4E00-\u9FA5]+$/;
			if(model.exec(val)) return true;
			return false;
		},
		//判断是否数字
		isNumber : function(val){
			var model = /^[0-9]+$/;
    		if(model.exec(val)) return true;
    		return false;
		},
		//判断是否递增或递减的序列号
		isSerialCode:function(code){
			if(!this.isNumber(code)){
				return false;
			}
			var flag = true;
			var step = null;
			for(var i=0; i<code.length-1; i++){
				step = parseInt(code.charAt(i+1))-parseInt(code.charAt(i));
				if(step != 1 && step !=-1){
					flag = false;
					break;
				}
			}step=null;
			return flag;
		},
		//判断是否重复连号
		isRepeatCode:function(code){
			var flag = true;
			for(var i=0; i<code.length-1; i++){
				if(code.charAt(i+1) != code.charAt(i)){
					flag = false;
					break;
				}
			}
			return flag;
		},
		//判断是否为前后N位字符组合
		isSubRingCode:function(target, code, len){
			if(target.length<len*2 || code.length!=len*2 || len<1) {
				return false;
			}
			var prefix = target.substr(0, len);			
			var suffix = target.substr(target.length-len);
			if((prefix+suffix) == code || (suffix+prefix) == code){
				return true;
			}prefix = suffix =null;
			return false;
		},
		//统计号码里重复数字
		getRepeatCount:function(code){
			var count = 0;
			var target = "_";
			var sub = null;
			for(var i=0; i<code.length; i++){
				sub = code.charAt(i);
				if(target.indexOf(sub) == -1){
					count++;
					target += sub+"_";
				}
			}target = sub = null;
			return count;
		},
		//判断号码前后两半是否相同
		isHalfSame:function(code){
			var flag = false;
			var len = Math.floor(code.length/2);
			var mod = code.length%2?0:1;
			var prefix = code.substr(0, len);
			var suffix = code.substr(mod+len);
			if(prefix == suffix){
				flag = true;
			}prefix = suffix = null;
			return flag;
		},
		//校验是否全部为奇数或偶数
		isAllParity:function(code){
			if(!$.verifylib.checkPInteger(code)){
				return false;
			}
			var count = 0;
			var number = null;
			for(var i=0; i<code.length; i++){
				number = parseInt(code.charAt(i));
				count += (number%2==0)?1:0;
			}number = null;
			return (count==code.length || !count);
		},
		//校验前后前后两半是否为等差数列 add by fufn REQ201710120004
		isArithmetic:function(code){
			var prefix_flag = true;
			var suffix_flag = true;
			if(code.length<6){//(单数中间一位不校验，且6位及以上,6位以下不检验）
				return false;
			}
			var len = Math.floor(code.length/2);
			var mod = code.length%2?1:0;
			var prefix = code.substr(0, len);
			var suffix = code.substr(mod+len);
			for(var i=0; i<(prefix.length-2); i++){
				if((parseInt(prefix.charAt(i))-parseInt(prefix.charAt(i+1)))
						!=(parseInt(prefix.charAt(i+1))-parseInt(prefix.charAt(i+2))))
				{
					prefix_flag=false;
					break;
				}
			}//前半段校验
			for(var i=0; i<(suffix.length-2); i++){
				if((parseInt(suffix.charAt(i))-parseInt(suffix.charAt(i+1)))
						!=(parseInt(suffix.charAt(i+1))-parseInt(suffix.charAt(i+2))))
				{
					suffix_flag=false;
					break;
				}
			}//后半段校验
			return (prefix_flag&&suffix_flag);
		}
	}});
})(Wade);