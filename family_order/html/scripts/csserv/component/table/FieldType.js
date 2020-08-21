(function($){
	$.FieldTypeSetClass=function(fieldTypeSetStr){
		debugger;
		var fieldTypeSetMap = new $.DataMap(fieldTypeSetStr);
		this.Name = "";     //数据块的名称
		this.MainFieldName = null; //名称域的名称
		this.FieldList = new Array();//数据域类型列表
		this.FieldIndex = new Array();
		this.Name = fieldTypeSetMap.get("Name");
	    this.MainFieldName = fieldTypeSetMap.get("MainField");
	    var fieldTypeSetList = fieldTypeSetMap.get("FieldList");
	    for(var i = 0; i < fieldTypeSetList.length; i++){
	    	this.FieldList[i] = new $.FieldType(this,fieldTypeSetList.get(i));
			this.FieldIndex[this.FieldList[i].getName()] = i;
	    }
	};
	$.extend($.FieldTypeSetClass.prototype,{
		getClassName:function(){
		    return "FieldTypeSet";
	    },
	    getName:function(){
	    	return this.Name;
	    },
	    getMainFieldName:function(){
	    	return this.MainFieldName;
	    },
	    getSameAttrNameFieldList:function(attrName){
	    	var result = new Array();
	        if (!attrName || attrName == ""){
	        	return result;
	        }
	        for(var i=0;i<this.FieldList.length;i++){
	        	if(this.FieldList[i].getAttrName() == attrName){
		             result[result.length] = this.FieldList[i];
		        }
	        }
	        return result;
	    },
	    getIndexByName:function(name){
	    	return this.FieldIndex[name];
	    },
	    getNameById:function(id){
	    	if (this.FieldList){
	    	    for(var i =0;i < this.FieldList.length;i++){
	    	        if(this.FieldList[i].ID == id){
	    		        return this.FieldList[i].getName();
	    	        }
	    	    }
	    	}
	    	return null;
	    },
	    free:function(){
	    	alert("FieldTypeSet.free() not finished !");
	    },
	    count:function(){
	    	return this.FieldList.length;
	    },
	    getFieldByName:function(name){
	    	name = name.toUpperCase();
		    return this.FieldList[this.FieldIndex[name]];
	    }
	});
	$.FieldType=function(parent,fieldTypeMap){
		this.ID = null;
	    this.Name = fieldTypeMap.get("N");
	    this.AttrName = null;
	    this.Parent = null;
	    this.Title = fieldTypeMap.get("T");
	    this.DefaultEditType = fieldTypeMap.get("Ed") ;//缺省为单行编辑框
	    this.DataType = "String";
	    this.DisDataType = "String";
        this.MaxLength = null;
        this.Decimal   =null;
	    this.Authority = true; //缺省为不可以修改
	    this.IsNull =true;
	    this.IsPk = false;
	    this.DefaultValueId = null;
	    this.DefaultValueText = null;
	    this.ListDataSourceName = fieldTypeMap.get("DN");
	    this.IsVisibled = true;
     	this.IsGridVisibled = true;
	    this.Parent = parent; 
	};
	$.extend($.FieldType.prototype,{
		isVisibled:function(){
		    return this.IsVisibled; 
	    },
	    isGridVisibled:function(){
	    	return this.IsGridVisibled;
	    },
	    getClassName:function(){
	    	return "FieldType";
	    },
	    getId:function(){
	    	return this.ID;
	    },
	    getName:function(){
	    	return this.Name; 
	    },
	    getAttrName:function(){
	    	return this.AttrName; 
	    },
	    getTitle:function(){
	    	return this.Title;
	    },
	    getAuthority:function(){
	    	return this.Authority;
	    },
	    setAuthority:function(authority){
	    	this.Authority = authority;
	    },
	    getIsNull:function(){
	    	return this.IsNull;
	    },
	    getIsPk:function(){
	    	return this.IsPk;
	    },
	    getDataType:function(){
	    	return this.DataType;
	    },
	    getMaxLength:function(){
	    	return this.MaxLength;
	    },
	    getDecimal:function(){
	    	return this.Decimal;
	    },
	    getDisDataType:function(){
	    	return this.DisDataType;
	    },
	    getDefaultValueId:function(){
	    	return this.DefaultValueId;
	    },
	    setDefaultValueId:function(id){
	    	this.DefaultValueId = id;
	    },
	    getDefaultValueText:function(text){
	    	return this.DefaultValueText;
	    },
	    setDefaultValueText:function(){
	    	this.DefaultValueText = text;
	    },
	    getDefaultEditType:function(){
	    	return this.DefaultEditType;
	    },
	    setDefaultEditType:function(editType){
	    	this.DefaultEditType = editType;
	    },
	    getListDataTextById:function(aListDataSource,aId){
	    	var reText =null;
	    	if(aListDataSource && this.ListDataSourceName){
	  		    var listsource = aListDataSource.find(this.ListDataSourceName);
	  		    if(listsource){
	  			    reText = listsource.findTextById(aId);
	  		    }
	  		}
	    	return reText;
	    },
	    getEditer:function(aRowSet,rowIndex,aListDataSource,aId,displayText){
	    	var obj = null;
	  	    var tmp_edit_type = (this.DefaultEditType).toLowerCase();
	  	    switch(tmp_edit_type)
	  	    {
	  	       case "dblable" :
	  	       { 
	  	    	   obj = new $.DBLable(aId,displayText);
	  	    	   break;
	  	       }
	  	       case "dbtree": {
	  	    	   obj = new $.DBEditer(aId,displayText);
	  	    	   break;
	  	       }
	  	       case "dbedit": { 
	  	    	   obj = new $.DBEditer(aId,displayText);
	  	    	   break;
	  	       }
	  	       case "dbeditdialog": { 
	  	    	   obj = new $.DBEditDialog(aId,displayText);
	  	    	   break;
	  	       }
	  		   case "dbtextarea" :{
	  			   obj =  new $.DBTextArea(aId,displayText);
	  			   break;
	  		   }
	  	       case "dblistbox":
	  	       {
	  		 	   if(aListDataSource){
	  					var listsource = aListDataSource.find(this.ListDataSourceName);
	  					if(listsource){
	  						obj =  new $.DBListBox(aRowSet,rowIndex,listsource,aId);
	  					}
	  			   }else{
	  					alert("没有配置下拉数据源，请检查配置！");
	  			   }
	  		  	   break;
	  	       }
	  	       case "dbcheckbox" :
	  	       {
	  	    	   obj = new $.DBCheckBox(aId,this.CV,this.UCV); 
	  	    	   break;
	  	       }
	  	       case "dbdate":
	  	       {
	  			   obj = new $.DBDate(aId,displayText);
	  			   break;
	  		   }
	  	       case "dbdatetime" :
	  	       {
  				   obj = new $.DBDateTime(aId,displayText);
  				   break;
	  		   }
	  		   case "dbpassword":
	  		   {
	  			   obj =new $.DBPassword(aId,displayText);  
	  			   break;
	  		   }
	  	   }
	  		return obj;
	    },
	    verify:function(value){
	    	return true;
	    }
	});
	$.DBCheckBox=function(aValue,aCheckValue,aUnCheckValue){
		this.UIObject = document.createElement("input");
		this.UIObject.type = "checkbox";
	    $(this.UIObject).attr("UIType","DBCheckBox");
	    $(this.UIObject).attr("CheckValue",aCheckValue);
	    $(this.UIObject).attr("UnCheckValue",aUnCheckValue);
	};
	$.extend($.DBCheckBox.prototype,{
		getUIObject:function(){
		   return this.UIObject;
	    },
		getID:function(){
		   return this.getValue();
	    },
	    getValue:function(){
	    	if(this.UIObject.checked == true){
	    		$(this.UIObject).attr("CheckValue");
	    		return this.UIObject.CheckValue;
	    	}else{
	    		$(this.UIObject).attr("UnCheckValue");
	    	}
	    },
	    setValue:function(aId,displayText){
	    	if (aId == $(this.UIObject).attr("CheckValue")){
	    		this.UIObject.checked = true;
	    	}else{
	    		this.UIObject.checked = false;
	    	}
	    },
	    setEditable:function(pFlag){
	    	if(pFlag){
	    		this.UIObject.disabled = false;
	    	}else{
	    		this.UIObject.disabled = true;
	    	}
	    }
	});
	$.DBEditer=function(id,displayText){
		this.UIObject = document.createElement("input");
		this.UIObject.type ="text";
		this.UIObject.value = id;
		$(this.UIObject).attr("UIType","DBEdit");
	};
	$.extend($.DBEditer.prototype,{
		getUIObject:function(){
		   return this.UIObject;
	    },
		getID:function(){
		    return this.UIObject.value;
	    },
	    getValue:function(){
	    	return this.UIObject.value;
	    },
	    setValue:function(id,displayText){
	    	this.UIObject.value = id;
	    }
	});
	$.DBPassword=function(id,displayText){
		this.UIObject = document.createElement("input");
		this.UIObject.type ="password";
		this.UIObject.UIType ="DBPassword";
		this.UIObject.value = id;
	};
	$.extend($.DBPassword.prototype,{
		getUIObject:function(){
		   return this.UIObject;
	    },
		getID:function(){
		    return this.UIObject.value;
	    },
	    getValue:function(){
	    	return this.UIObject.value;
	    },
	    setValue:function(id,displayText){
	    	this.UIObject.value = id;
	    }
	});
	$.DBTextArea=function(id,displayText){
		this.UIObject = document.createElement("textArea");
		this.UIObject.UIType ="DBTextArea";
		this.UIObject.style.overflowX="auto";
		this.UIObject.style.overflowY="auto";
		this.UIObject.value = id;
	};
	$.extend($.DBTextArea.prototype,{
		getUIObject:function(){
		   return this.UIObject;
	    },
		getID:function(){
		    return this.UIObject.value;
	    },
	    getValue:function(){
	    	return this.UIObject.value;
	    },
	    setValue:function(id,displayText){
	    	this.UIObject.value = id;
	    }
	});
	$.DBLable=function(id,displayText){
		this.UIObject = document.createElement("input");
		this.UIObject.type ="text";
		this.UIObject.UIType ="DBLable";
		this.UIObject.readOnly = true;
		if(displayText==null || displayText==""){
			displayText = id;
		}
		this.UIObject.value = displayText;
	};
	$.extend($.DBLable.prototype,{
		getUIObject:function(){
		   return this.UIObject;
	    },
		getID:function(){
		    return this.UIObject.value;
	    },
	    getValue:function(){
	    	return this.UIObject.value;
	    },
	    setValue:function(id,displayText){
	    	if(displayText==null || displayText==""){
	    		 displayText = id;
	    	}
	    	this.UIObject.value = displayText;
	    }
	});
	$.DBListBox=function(aRowSet,rowIndex,aListSource,id,displayText){
		this.UIObject = document.createElement("select");
	    this.UIType = "DBListBox";
    	this.init(aListSource,id);
	};
	$.extend($.DBListBox.prototype,{
		init:function(aListSource,aValue){
			this.UIObject.options.length =0;
	    	var isFind = false;
	    	var selIndex = 0;
		    valueAttr = aListSource.getValueAttr();
	    	textAttr = aListSource.getTextAttr();
	    	var listBoxData = aListSource.getListBoxData();//获取下拉列表数据
	    	for(var i = 0; i < listBoxData.length; i++){
	    		var tmpOption = new Option();
	            tmpOption.value = listBoxData.get(i,valueAttr);
	            tmpOption.text = listBoxData.get(i,textAttr);
	            if(isFind == false && aValue == tmpOption.value ){
	                selIndex = i;
	                isFind = true;
	            }else{
	            	tmpOption.selected = false;
	            }
	            this.UIObject.options.add(tmpOption);
	    	}
	    	this.UIObject.selectedIndex = selIndex;
		},
		getUIObject:function(){
		    var span = document.createElement("span");
		    span.appendChild(this.UIObject);
		    var span1 = document.createElement("span");
		    span1.appendChild(span);
		    var span2 = document.createElement("span");
		    span2.appendChild(span1);
		    $(span2).attr("class","e_select");
		    return span2;
	    },
		getID:function(){
		    var selectedIndex = this.UIObject.selectedIndex;
		    if(selectedIndex < 0){
			    return "";
		    } 
		    var selectOption = this.UIObject.options(selectedIndex);
		    if(selectOption && selectOption.value){
			    return selectOption.value;
		    }
		    return "";
	    },
	    getValue:function(){
	    	var selectedIndex = this.UIObject.selectedIndex;
			if(selectedIndex < 0){
				return "";
			} 
			var selectOption = this.UIObject.options(selectedIndex);
			if(selectOption){
				return selectOption.text;
			}
			return "";
	    },
	    setValue:function(id,displayText){
	    	if (id || id ==0 ){
	    	    for(var i=0;i<this.UIObject.options.length;i++){
	    	        if(this.UIObject.options[i].value == id){
			    		 this.UIObject.options[i].selected = true;
			    		 return;
	    	        }
	    	    }
	    	}else if(this.UIObject.options.length>=1){
	    		this.UIObject.selectedIndex = 0;
	    	}else{
	    		this.UIObject.selectedIndex = -1;
	        }
	    }
	});
})(Wade);