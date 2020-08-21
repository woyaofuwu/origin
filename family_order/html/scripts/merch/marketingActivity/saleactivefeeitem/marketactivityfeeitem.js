
var errDataList = $.DatasetList();
if(typeof(SaleFeeItem)=="undefined"){
	window["MarketActivityFeeItem"]=function(){};
	var marketActivityFeeItem = new MarketActivityFeeItem();
}

(function(){
	$.extend(MarketActivityFeeItem.prototype,{
		readerComponent: function(param){
		
		},
		hideAttrByInitValue:function(){
			$("#saleFeeItemPanel").css("display","none");
		},
		/*取到当前元素的属性信息*/
		saveElemAttr:function(){
			var attrs = $.DatasetList();
			var itemIndex = $('#ITEM_INDEX').val();
			$("#saleFeeItemPanel select").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attr.put('ATTR_DESC', $.attr(this, 'desc'));
				attr.put('RSRV_STR1', $.attr(this, 'rsrvStr1'));
				attr.put('IS_CAN_NULL', $.attr(this, 'nullable'));
				attr.put('ELEMENT_INDEX',itemIndex);
				attrs.add(attr);
			});
			$("#saleFeeItemPanel input").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attr.put('ATTR_DESC', $.attr(this, 'desc'));
				attr.put('RSRV_STR1', $.attr(this, 'rsrvStr1'));
				attr.put('IS_CAN_NULL', $.attr(this, 'nullable'));
				attr.put('ELEMENT_INDEX',itemIndex);
				attrs.add(attr);
			});
			return attrs;
		},
		spAddErrMsg:function(errCode,errMsg,elemIndex){
			var errData = $.DataMap();
			var itemIndex = $('#ITEM_INDEX').val();
			if(typeof(elemIndex) != "undefined") itemIndex = elemIndex;
			errData.put('ELEMENT_ERRCODE',errCode);
			errData.put('ELEMENT_ERRINFO',errMsg);
			errData.put('ELEMENT_ERRINDEX',itemIndex+'_'+errCode);
			
			errDataList.each(function(item, index, totalcount) {
				if(typeof(item) != "undefined"){
					if (item.get("ELEMENT_ERRINDEX") == errData.get("ELEMENT_ERRINDEX")) {
						errDataList.removeAt(index);
					}
				}
			});
			errDataList.add(errData);
		},
		spDelErrMsg:function(errCode,elemIndex){
			var itemIndex = $('#ITEM_INDEX').val();
			if(typeof(elemIndex) != "undefined") itemIndex = elemIndex;
			var elemErrIndex = itemIndex+'_'+errCode;
			errDataList.each(function(item, index, totalcount) {
				if(typeof(item) != "undefined"){
					if (item.get("ELEMENT_ERRINDEX") == elemErrIndex) {
						errDataList.removeAt(index);
					}
				}
			});
		},
		
		saleAttrFeeDeal:function(attrParam)
		{
			/*处理属性的费用信息*/
			for(var i = 0; i < attrParam.length; i++)
			{
				var attrData = attrParam.get(i);
				var attrCode = attrData.get('ATTR_CODE');
				var attrValue = attrData.get('ATTR_VALUE');
				var elemAttr = $('#'+attrCode);
				var elementId =  elemAttr.attr('elementId');
				var feeTypeCode = elemAttr.attr('feeTypeCode');
				var depositType = elemAttr.attr('depositType');
				var elementTypeCode = elemAttr.attr('elementTypeCode');
				if(attrCode == 'ITEM_FEE' && depositType == '0')
				{
					marketActivityModule.addFeeByAttr(attrValue*100, '2',feeTypeCode, attrValue*100, elementId,'A','0');
				}
				else if(attrCode == 'ITEM_OPER_FEE') //购机款 G
				{
					if(typeof(feeTypeCode) == "undefined" || feeTypeCode == '')
					{
						feeTypeCode = "7000";
					}
					marketActivityModule.addFeeByAttr(attrValue*100, '0', feeTypeCode, attrValue*100,elementId,elementTypeCode,'0');
				} 
				else if(attrCode == 'ITEM_FOREGIFT_FEE')//终端任意机型 G
				{
					if(typeof(feeTypeCode) == "undefined" || feeTypeCode == '') {
						feeTypeCode = "1";
					}
					marketActivityModule.addFeeByAttr(attrValue*100, '1', feeTypeCode, attrValue*100,elementId,elementTypeCode,'0');
				}
				else if(attrCode == '2102')
				{
					marketActivityFeeItem.setMinExpand(attrValue,attrParam);
					marketActivityFeeItem.setMaxExpand(attrValue,attrParam);
					marketActivityFeeItem.getExpand(attrValue,attrParam);
				}
			}
		},
		
		checkItemFee:function(attrs){
			
			var depositFee = "";
			var returnMonth = "";
			var returnFeePerMonth = "";
			var giftFee = "";
			var giftReturnMonth = "";
			var giftMonthFee = "";
			
			for(var i=0; i < attrs.length; i++)
			{
				var attrData = attrs.get(i);
				var attrCode = attrData.get("ATTR_CODE");
				var attrValue = attrData.get("ATTR_VALUE");
				
				if(attrCode == 'ITEM_MRTN_FEE')//预存月返还金额
				{
					returnFeePerMonth = attrValue;
				}
				else if(attrCode == 'ITEM_FEE')//预存金额
				{
					depositFee = attrValue;
				}
				else if(attrCode == 'ITEM_END_OFFSET')//预存返还月份
				{
					returnMonth = attrValue;
				}
				else if(attrCode == 'ITEM_GIFT_FEE')//赠送金额
				{
					giftFee = attrValue;
				}
				else if(attrCode == 'ITEM_GIFT_MONTHS')//赠送返还月份
				{
					giftReturnMonth = attrValue;
				}
				else if(attrCode == 'ITEM_MGFT_FEE')//赠送月返还金额
				{
					giftMonthFee = attrValue;
				}
				
				if((depositFee != "" && depositFee != "0") 
						&& (returnFeePerMonth != "" && returnFeePerMonth != "0") 
						&& (returnMonth != "" ))
				{
					if(depositFee != parseInt(returnMonth) * parseInt(returnFeePerMonth))
					{
						var errMsg = '当前总预存金额不等于月平均返回金额乘以返回月份的总金额，请重新填写';
						marketActivityFeeItem.spAddErrMsg('1003',errMsg);
						alert(errMsg);
						return false;
					}
					else
					{
						marketActivityFeeItem.spDelErrMsg('1003');
					}
					
				}
				if((giftFee != "" && giftFee != "0") 
						&& (giftReturnMonth != "" && giftReturnMonth != "0") 
						&& (giftMonthFee != ""))
				{
					if(giftFee != parseInt(giftReturnMonth) * parseInt(giftMonthFee))
					{
						var errMsg = '当前赠送金额不等于月平均返回金额乘以返回月份的总金额，请重新填写';
						marketActivityFeeItem.spAddErrMsg('1004',errMsg);
						alert(errMsg);
						return false;
					}
					else
					{
						marketActivityFeeItem.spDelErrMsg('1004');
					}
				}
				
				if((depositFee != "" && depositFee != "0") 
						&& (returnFeePerMonth != "" && returnFeePerMonth != "0"))
				{
					if(parseInt(returnFeePerMonth) > parseInt(depositFee) )
					{
						var errMsg = '当前月返还金额不能大于预存总金额，请重新填写';
						marketActivityFeeItem.spAddErrMsg('1005',errMsg);
						alert(errMsg);
						return false;
					}
					else
					{
						marketActivityFeeItem.spDelErrMsg('1005');
					}
				}
				
				if((giftFee != "" && giftFee != "0") 
						&& (giftReturnMonth != "" && giftReturnMonth != "0"))
				{
					if(parseInt(giftReturnMonth) > parseInt(giftFee) )
					{
						var errMsg = "当前月返还金额不能大于赠送总金额，请重新填写";
						marketActivityFeeItem.spAddErrMsg('1006',errMsg);
						alert(errMsg);
						return false;
					}
					else
					{
						marketActivityFeeItem.spDelErrMsg('1006');
					}
				}
			}
			
		},
		
		calReturnFee:function(attrs){
			
			var depositFee = "";
			var returnMonth = "";
			var returnFeePerMonth = "";
			for(var i=0; i < attrs.length; i++)
			{
				var attrData = attrs.get(i);
				var attrCode = attrData.get("ATTR_CODE");
				var attrValue = attrData.get("ATTR_VALUE");
				
				if(attrCode == 'ITEM_MRTN_FEE')
				{
					returnFeePerMonth = attrValue;
				}
				else if(attrCode == 'ITEM_FEE')
				{
					depositFee = attrValue;
				}
				else if(attrCode == 'ITEM_END_OFFSET')
				{
					returnMonth = attrValue;
				}
				
				if(depositFee == "" && returnMonth != "" && returnFeePerMonth != "") {
					depositFee = returnFeePerMonth * returnMonth;
				}
				if(depositFee != "" && returnMonth == "" && returnFeePerMonth != "") {
					returnMonth = parseInt(parseInt(depositFee) / parseInt(returnFeePerMonth));
				}
				if(depositFee != "" && returnMonth != "" && returnFeePerMonth == "") {
					returnFeePerMonth = parseInt(parseInt(depositFee) / parseInt(returnMonth));
				}
				
				if(attrCode == 'ITEM_MRTN_FEE')
				{
					attrData.put("ATTR_VALUE",marketActivityFeeItem.keep2Decimal(returnFeePerMonth));
				}
				else if(attrCode == 'ITEM_CREDIT')
				{
					attrData.put("ATTR_VALUE",marketActivityFeeItem.keep2Decimal(returnFeePerMonth));
				}
				else if(attrCode == 'ITEM_FEE')
				{
					$('#'+attrCode).val(depositFee);
					attrData.put("ATTR_VALUE",depositFee);
				}
				else if(attrCode == 'ITEM_FEE')
				{
					$('#'+attrCode).val(returnMonth);
					if(attr_value != "") {
						if(parseInt(attr_value) != attr_value){
							attrData.put("ATTR_VALUE",parseInt(attr_value) + 1);
						}
					}
					
				}
			}
			
		},
		onchangeCheck:function(obj)
		{
			obj = $(obj);
			marketActivityFeeItem.attrValueTypeCheck(obj);
			marketActivityFeeItem.saleAttrValueCheck(obj);
			//marketActivityFeeItem.checkItemFee(obj);
			//marketActivityFeeItem.calReturnFee(obj);
			marketActivityFeeItem.saleAttrDateDeal(obj);
		},
		/*校验营销活动的属性值信息*/
		saleAttrValueCheck:function(obj)
		{
			var attrCode = obj.attr('id');
			var attrValue = obj.attr('value');
			if(attrCode == '2102')
			{
				marketActivityFeeItem.checkMaxExpand(obj)
				marketActivityFeeItem.getMinExpand(obj)
				
			}
		},
		getExpand:function(attrFee,attrs){
			var quo = "";
			var flag = false;
			for(var i=0; i < attrs.length; i++) {
				var attrData = attrs.get(i);
				var arrtCode = attrData.get("ATTR_CODE");
				var attrValue = attrData.get("ATTR_VALUE");
				if(arrtCode=='1001') 
				{
					quo = attrValue;
				} 
			}
			
			for(var index=0; index < attrs.length; index++)
			{
				var attr = attrs.get(index);
				var attrCodei = attr.get("ATTR_CODE");
				
				if(attrCodei == '1001')
				{
					attr.put('ATTR_VALUE',quo);
				}
				else if(attrCodei == '2101')
				{
					attr.put('ATTR_VALUE',attrFee * quo);
				}
				else if(attrCodei == '2102')
				{
					attr.put('ATTR_VALUE',attrFee);
				}
			}
			
		},
		/*防止出现营业员输个0100的。。。0100*1后会变成100*/
		attrValueTypeCheck:function(obj)
		{	
			var attrValue = obj.val();
			obj.val(attrValue*1);
		},
		checkMaxExpand:function(obj){
			var quo = "";
			var flag = false;
			var attrCode = obj.attr('id');
			var attrValue = obj.attr('value');
			var avrgFee = $("#USER_AVGFEE").val();
			var avrgFeeTag = $("#AVGFEE_TAG").val();
			var maxExpData = $.DatasetList($("#MAX_EXPEND").val());
			if(avrgFeeTag == 'true' && parseFloat (attrValue) > parseFloat (avrgFee)) {
				
				var errMsg = "您的前3个月平均话费不能小于话费基数";
				marketActivityFeeItem.spAddErrMsg('1007',errMsg);
				alert(errMsg);
				return false;
			}
			else
			{
				marketActivityFeeItem.spDelErrMsg('1007');
			}
			
			for(var i = 0; i < maxExpData.length; i++) {
				if(parseFloat(attrValue) >= parseFloat(maxExpData.get(i).get('PARA_CODE1')) && parseFloat(attrValue) < parseFloat(maxExpData.get(i).get('PARA_CODE2'))) {
					quo = maxExpData.get(i).get('PARAM_CODE');
					flag = true;
					break;
				}
			}
			
			if(!flag) {
				
				var errMsg = "根据话费基数找不到对应的系数";
				marketActivityFeeItem.spAddErrMsg('1008',errMsg);
				alert(errMsg);
				return false;
			}
			else
			{
				marketActivityFeeItem.spDelErrMsg('1008');
			}
		},
		setMaxExpand:function(attrFee,attrs)
		{
			var quo = "0";
			var maxExpData = $.DatasetList($("#MAX_EXPEND").val());
			for(var i = 0; i < maxExpData.length; i++) {
				if(parseFloat(attrFee) >= parseFloat(maxExpData.get(i).get('PARA_CODE1')) && parseFloat(attrFee) < parseFloat(maxExpData.get(i).get('PARA_CODE2'))) {
					quo = maxExpData.get(i).get('PARAM_CODE');
					break;
				}
			}
			
			var maxExpand = attrFee * quo;
			for(var index = 0; index < attrs.length; index++)
			{
				var attr = attrs.get(index);
				var attrCodei = attr.get('ATTR_CODE');
				if(attrCodei == '1001')
				{
					attr.put('ATTR_VALUE',quo);
				}
				else if(attrCodei == '2101')
				{
					attr.put('ATTR_VALUE',maxExpand);
				}
				else if(attrCodei == '2102')
				{
					attr.put('ATTR_VALUE',attrFee);
				}
			}
		
		},
		getMinExpand:function(obj){
			var quo = "";
			var flag = false;
			var attrCode = obj.attr('id');
			var attrValue = obj.attr('value');
			var avrgFee = $("#USER_AVGFEE").val();
			var avrgFeeTag = $("#AVGFEE_TAG").val();
			var minExpData = $.DatasetList($("#MIN_EXPEND").val());
			
			if(avrgFeeTag == 'true' && parseFloat (attrValue) > parseFloat (avrgFee)) {
				var errMsg = "您的前3个月平均话费不能小于话费基数";
				marketActivityFeeItem.spAddErrMsg('1009',errMsg);
				alert(errMsg);
				return false;
			}
			else
			{
				marketActivityFeeItem.spDelErrMsg('1009');
			}
			
			for(var i = 0; i < minExpData.length; i++) {
				if(parseFloat(attrValue) >= parseFloat(minExpData.get(i).get('PARA_CODE1')) && parseFloat(attrValue) < parseFloat(minExpData.get(i).get('PARA_CODE2'))) {
					quo = minExpData.get(i).get('PARAM_CODE');
					flag = true;
					break;
				}
			}
			
			if(!flag) {
				var errMsg = "根据话费基数找不到对应的系数";
				marketActivityFeeItem.spAddErrMsg('1008',errMsg);
				alert(errMsg);
				return false;
			}
			else
			{
				marketActivityFeeItem.spDelErrMsg('1008');
			}
		},
		setMinExpand:function(attrFee,attrs){
			var quo = "";
			var minExpData = $.DatasetList($("#MIN_EXPEND").val());
			for(var i = 0; i < minExpData.length; i++) {
				if(parseFloat(attrFee) >= parseFloat(minExpData.get(i).get('PARA_CODE1')) && parseFloat(attrFee) < parseFloat(minExpData.get(i).get('PARA_CODE2'))) {
					quo = minExpData.get(i).get('PARAM_CODE');
					break;
				}
			}
			
			var maxExpand = attrFee * quo;
			
			for(var index = 0; index < attrs.length; index++)
			{
				var attr = attrs.get(index);
				var attrCodei = attr.get('ATTR_CODE');
				if(attrCodei == '1001')
				{
					attr.put('ATTR_VALUE',quo);
				}
				else if(attrCodei == '2101')
				{
					attr.put('ATTR_VALUE',maxExpand);
				}
				else if(attrCodei == '2102')
				{
					attr.put('ATTR_VALUE',attrFee);
				}
			}
			
		},
		keep2Decimal:function(value){
			value = Math.round(value * 100) / 100;
	
			return value;
		},
		
		/*根据元素属性修改活动开始时间和结束时间*/
		saleAttrDateDeal:function(obj){
			var flag = true;
			var retFlag = false;
			var startDate = "";
			
			var attrCode = obj.attr('id');
			var attrValue = obj.attr('value');
			
			var attrParam = $.DataMap();
			attrParam.put('ATTR_CODE',attrCode);
			attrParam.put('ATTR_VALUE',attrValue);
			var attrList = $.DatasetList();
			attrList.add(attrParam);
			
			if(attrCode == 'ITEM_GIFT_MONTHS' || attrCode == 'ITEM_END_OFFSET')
			{
				if(attrValue != '')
				{
					if(attrValue < 0 || attrValue > 100)
					{
						var errMsg = "当前返还月份输入不合法，请重新输入";
						marketActivityFeeItem.spAddErrMsg('1010',errMsg);
						alert(errMsg);
						return false;
					}
					else
					{
						marketActivityFeeItem.spDelErrMsg('1010');
					}
				}
			}
			if(attrCode == 'ITEM_START_DATE') {
				//根据选择的开始时间重新计算活动开始时间
				var againTag = $("#AGAIN_ACTIVE_TAG").val();
				var activeType = $('#ACTIVE_TYPE').val();
				var saleStartDate = $("#SALEACTIVE_START_DATE").val();
				if((againTag == 'true' || activeType == 'JX') && (saleStartDate.substr(0,10)>attrValue)){
					var errMsg = "预存话费,返还起始月份,不能小于活动起始时间!";
					marketActivityFeeItem.spAddErrMsg('1011',errMsg);
					alert(errMsg);
					return false;
				}
				else
				{
					marketActivityFeeItem.spDelErrMsg('1011');
				}
			} 
			
		},
		writeAttrDesc:function(attrs){
			var itemIndex = $('#ITEM_INDEX').val();
			var  elemAttrList = $.DatasetList($("#ATTR_DESC_LIST").val());
			if(typeof(elemAttrList) != 'undefined' && elemAttrList.length>0)
			{
				var elemAttrSize = elemAttrList.length;
				for(var i=0; i < elemAttrSize; i++)
				{
					var elemAttrData = elemAttrList.get(i);
					var currAttrCode = elemAttrData.get('ATTR_CODE');
					var currAttrIndex = elemAttrData.get('ELEMENT_INDEX');
					for(var k=0; k<attrs.length; k++)
					{
						var attr = attrs.get(k);
						var attrCode = attr.get('ATTR_CODE');
						var attrValue = attr.get('ATTR_VALUE');
						var attrIndex = attr.get('ELEMENT_INDEX');
						var attrDesc = attr.get('ATTR_DESC');
						if(typeof(attrCode) != 'undefined' && typeof(currAttrCode) != 'undefined' 
								&& currAttrCode == attrCode && attrIndex == currAttrIndex)
						{
							elemAttrData.put('ATTR_VALUE',attrValue);
							attrs.remove(attr);
						}
					}
				}
				//变态处理，慢慢研究吧
				for(var n=0; n < attrs.length; n++)
				{
					var elemAttrData = $.DataMap();
					elemAttrData.put('ATTR_CODE',attrs.get(n).get("ATTR_CODE"));
					elemAttrData.put('ATTR_VALUE',attrs.get(n).get("ATTR_VALUE"));
					elemAttrData.put('ATTR_DESC',attrs.get(n).get("ATTR_DESC"));
					elemAttrData.put('ELEMENT_INDEX',attrs.get(n).get("ELEMENT_INDEX"));
					elemAttrList.add(elemAttrData);
				}
				
			}
			else
			{
				for(var i=0; i < attrs.length; i++)
				{
					var elemAttrData = $.DataMap();
					elemAttrData.put('ATTR_CODE',attrs.get(i).get("ATTR_CODE"));
					elemAttrData.put('ATTR_VALUE',attrs.get(i).get("ATTR_VALUE"));
					elemAttrData.put('ATTR_DESC',attrs.get(i).get("ATTR_DESC"));
					elemAttrData.put('ELEMENT_INDEX',attrs.get(i).get("ELEMENT_INDEX"));
					elemAttrList.add(elemAttrData);
				}
			}
			marketActivityFeeItem.updateAttrInfo(elemAttrList);
			$("#elementPanel").css("display","none");
			$("#ATTR_DESC_LIST").val(elemAttrList);
			$("#attrDesc").text(marketActivityFeeItem.inputAttrDesc(elemAttrList));
			$('#attrDesc').css('display', '');
		},
		setErrMsg:function(){
			//先将默认错误删除掉
			marketActivityFeeItem.spDelErrMsg('1022');
			if(errDataList.length > 0)
			{
				return false;
			}
		},
		updateAttrInfo:function(elemAttrList){
			var itemIndex = $('#ITEM_INDEX').val();
			var elemAttrs = $.DatasetList();
			for(var i=0; i<elemAttrList.length; i++)
			{
				var elemAttrData = elemAttrList.get(i);
				var elemIndex = elemAttrData.get("ELEMENT_INDEX");
				if(typeof(itemIndex) != 'undefined' 
					&& itemIndex != '' && itemIndex == elemIndex)
				{
					elemAttrs.add(elemAttrData);
				}
			}
			
			var element = marketActivityModule.spGetElem(itemIndex);
			element.put("ATTR_PARAM", elemAttrs);
		},
		inputAttrDesc:function(elemAttrList){
			var attrDescStr='属性选择信息:';
			for(var i=0; i<elemAttrList.length; i++)
			{
				var elemAttrData = elemAttrList.get(i);
				var attrValue = elemAttrData.get("ATTR_VALUE");
				var attrDesc = elemAttrData.get("ATTR_DESC");
				attrDescStr += attrDesc+"："+attrValue+"；";
			}
			return attrDescStr;
		},
		getAttrInfoByAttrCode:function(attrCodeStr){
			var attrValue;
			var attrDataset = $.DatasetList($("#ATTR_DESC_LIST").val());
			for(var i =0; i<attrDataset.length; i++ )
			{
				var attr = attrs.get(i);
				var attrCode = attr.get('ATTR_CODE');
				if(attrCode && attrCode == attrCodeStr)
				{
					attrValue = attr.get('ATTR_VALUE');
					break;
				}
			}
			return attrValue;
		},
		confirmAttr: function() {
			var flag = true;
			var itemIndex = $('#ITEM_INDEX').val();
			if (!$.validate.verifyAll('saleFeeItemPanel')) 
			{
				return ;
			}
			var changeEndOffset =$("#CHANGED_END_OFFSET").val()
			var changeEndOffsetflag = false;
			var max=0;
			//当有ITEM_END_OFFSET属性时，按ITEM_END_OFFSET偏移，否则按ITEM_GIFT_MONTHS偏移
			var attrs = marketActivityFeeItem.saveElemAttr();
			if(typeof(attrs) != 'undefined' )
			{
				for(var index=0; index<attrs.length; index++)
				{
					var attrData = attrs.get(index);
					var attrCode = attrData.get('ATTR_CODE');
					var attrValue = attrData.get('ATTR_VALUE');
					if(attrCode == 'ITEM_START_DATE')
					{
						$("#CHANGED_STATE_DATE").val(attrValue);
						marketActivityModule.modifySaleDate(3,attrs.toString());
					}
					else if(attrCode == 'ITEM_END_OFFSET') {
						flag = false;
						changeEndOffsetflag=true;
						if(parseInt(attrValue) > parseInt(max))
						{
							max=attrValue
						}
					}
					else if(attrCode == 'ITEM_GIFT_MONTHS' && flag)
					{
						$("#CHANGED_END_OFFSET").val(attrValue);
						marketActivityModule.modifySaleDate(3,attrs.toString());
					}
				}
				//处理有两个"ITEM_END_OFFSET"(例如：最低消费月份，赠送返还月份)属性值的时候取最大的作为偏移量 
				if(changeEndOffsetflag&&max>0){
					$("#CHANGED_END_OFFSET").val(max);
					marketActivityModule.modifySaleDate(3,attrs.toString());
				}
			}
			marketActivityFeeItem.saleAttrFeeDeal(attrs);
			marketActivityFeeItem.checkItemFee(attrs);
			marketActivityFeeItem.calReturnFee(attrs);
			marketActivityFeeItem.writeAttrDesc(attrs);
			marketActivityFeeItem.setErrMsg();
			marketActivityFeeItem.hideAttrByInitValue();
		}
	});
}
)();