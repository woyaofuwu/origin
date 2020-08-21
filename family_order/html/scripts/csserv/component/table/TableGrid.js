(function($){
	$.extend({tableManager:{
		List:new Array(),
		push:function(obj){
			this.List[obj.DBGridPK] = obj;
		    return obj.DBGridPK;
		},
		get:function(pk){
			var result = this.List[pk];
		    if(!result){
		      var obj = document.all("TableGrid_" + pk);
		      if(obj == null) {
		    	  return null;
		      }
		      result = new $.tableGrid(pk);
		    }
		    return result;
		},
		remove:function(pk){
			this.List[pk] = null;
		}
	}});
	
	$.tableGrid=function(aName,aPkName){
		this.DBGridPK = aName;
		this.pkName = aPkName; // 数据集的主键字段名称 
		$.tableManager.push(this); 
		this.initial(); 
	};
	
	$.virtualCol=function(){
		this.I = null;
		this.OldI = null;
		this.OldValue = null;
		this.Value = "";
		this.innerText = "";
		this.isModify = false;
		this.isFirstModify = true;
		this.toString  = function(){
		return "this.I = " + this.I + "\n"
		       + "this.OldI = " + this.OldI + "\n"
		       + "this.OldValue = " + this.OldValue + "\n"
		       + "this.Value = " + this.Value + "\n"
		       + "this.innerText = " + this.innerText + "\n"
		       + "this.isFirstModify = " + this.isFirstModify + "\n"
		       + "this.isModify = " + this.isModify + "\n";
		}
	};
	
	$.extend($.tableGrid.prototype,{
		initial:function(){
			this.DBHTML = "DBHtml";
			this.DBTree = "DBTree";
			this.DBListBox = "DBListBox";
			this.CurRow = -1;     //当前行索引
			this.CurCol = -1;     //当前列索引
			this.NewRowDataId = 0; //新增行的行号
			this.visioColCount =0; //显示列的数量
			this.colIndexs = new Array();//键为列名，值为索引
			this.colNames = new Array();//键为索引，值为列名
			this.colIsHide = new  Array();   // 数据列是否藏隐，键为索引，值为布尔值
			this.rowHideCols  = new  Array();// 在需要的时候存放行的隐藏列，键为行索引，值为隐藏列的数组
			this.canModifyCols = new Array(); //所有表格列的初始编辑状态
			this.TableGridDiv = document.all("TableGrid_" + this.DBGridPK);
			this.FieldTypeSetDiv = document.all("TableGrid_FieldTypeSet_" + this.DBGridPK);
			this.ListDataSourceDiv = document.all("TableGrid_DataSource_" + this.DBGridPK);
			this.Table = document.all(this.DBGridPK);
			this.DBTreeColName = $.attr(this.TableGridDiv,"DBTreeColName"); 
			this.deleteRows = new Array();  //记录删除的行
			this.canModfiyCells = new Array(); //记录能编辑的列，键为“行号-列号”，值为布尔值
			this.isMutilSelect = false;   //是否多选的
			if(!$.attr(this.TableGridDiv,"isMutilSelect")){
				this.isMutilSelect = false;
			}else{
				this.isMutilSelect = true;
			}
			this.isRowSequence = false;   //是否展示序号
			if(!$.attr(this.TableGridDiv,"isRowSequence")){
				this.isRowSequence = false;
			}else{
				this.isRowSequence = true;
			}
			this.IsEditable = false;   //表格是否可编辑
			if($.attr(this.TableGridDiv,"Edit") == "true"){
			    this.IsEditable = true;
			}
			this.onlyQuery = false; //判断当前是否是只读模式
			if($.attr(this.TableGridDiv,"onlyQuery") && $.attr(this.TableGridDiv,"onlyQuery")=='true'){
			    this.onlyQuery = true;
			}
			if($.attr(this.TableGridDiv,"RowHeight") && $.attr(this.TableGridDiv,"RowHeight")>0){
				this.DefaultRowHeight =this.TableGridDiv.RowHeight;
			}
			if($.attr(this.TableGridDiv,"S_OnGridDbClick")){
			    eval("this.OnGridDbClick = " + $.attr(this.TableGridDiv,"S_OnGridDbClick"));
			}
			if($.attr(this.TableGridDiv,"S_OnTitleDbClick")){
			    eval("this.S_OnTitleDbClick = " + $.attr(this.TableGridDiv,"S_OnTitleDbClick"));
			}
			if($.attr(this.TableGridDiv,"S_OnValueChange")){
				eval("this.OnValueChange = " + $.attr(this.TableGridDiv,"S_OnValueChange"));
			}
			if($.attr(this.TableGridDiv,"S_OnRowFocusChange")){
				eval("this.OnRowFocusChange = " + $.attr(this.TableGridDiv,"S_OnRowFocusChange"));
			}
			if($.attr(this.TableGridDiv,"S_OnCellFocusChange")){
				eval("this.OnCellFocusChange = " + $.attr(this.TableGridDiv,"S_OnCellFocusChange"));
			}
			if($.attr(this.TableRowSetDiv,"S_OnFocusOut")){
				eval("this.OnFocusOut = " +$.attr(this.TableRowSetDiv,"S_OnFocusOut"));
			}
            if($.attr(this.TableGridDiv,"S_OnContextMenu")){
			    eval("this.OnContextMenu = " + $.attr(this.TableGridDiv,"S_OnContextMenu"));
			}
			if($.attr(this.TableGridDiv,"S_OnBeforeTurnPage")){
			    eval("this.OnBeforeTurnPage = " + $.attr(this.TableGridDiv,"S_OnBeforeTurnPage"));
			}
			if($.attr(this.TableGridDiv,"S_OnAfterTurnPage")){
			    eval("this.OnAfterTurnPage = " + $.attr(this.TableGridDiv,"S_OnAfterTurnPage"));
			}
			if($.attr(this.TableGridDiv,"S_OnRowSelected")){
			    eval("this.OnRowSelected = " + $.attr(this.TableGridDiv,"S_OnRowSelected"));
			}
			if($.attr(this.TableGridDiv,"S_OnRowClick")){
			    eval("this.OnRowClick = " + $.attr(this.TableGridDiv,"S_OnRowClick"));
			}
			if($.attr(this.TableGridDiv,"S_OnDBLink")){
			    eval("this.OnDBLink = " + $.attr(this.TableGridDiv,"S_OnDBLink"));
			}
			if($.attr(this.TableGridDiv,"S_OnResize")){
			    eval("this.OnResize = " + $.attr(this.TableGridDiv,"S_OnResize"));
			}
			var headCount = this.getRowHeadColCount();
			var cells = this.Table.rows(0).cells;
			//处理显示的列
			for(var i=0;i< cells.length - headCount ;i++){
			    var tmpCell = cells(i + headCount);
			    this.colIndexs[$.attr(tmpCell,"FieldID").toUpperCase()] = i;
			    this.colNames[i] = $.attr(tmpCell,"FieldID");
			    this.colIsHide[i] = false;
			    this.canModifyCols[i] = $.attr(tmpCell,"CanModify");
			}
			//处理隐藏的列
			this.visioColCount =  cells.length - headCount;
			var tmpHideColNames = $.attr(this.TableGridDiv,"FieldIDs");
			if(tmpHideColNames){
			    var list = tmpHideColNames.split(",");
			    for(var i=0;i< list.length;i++){
			      this.colIndexs[list[i].toUpperCase()] = i + this.visioColCount;
			      this.colNames[ i + this.visioColCount] = list[i];
			      this.colIsHide[i + this.visioColCount] = true;
			    }
			}
			
			var fieldTypeSetStr = $.attr(this.FieldTypeSetDiv,"fieldTypeSet");
			this.FieldTypeSet = new $.FieldTypeSetClass(fieldTypeSetStr);
			
			var ListDataSourceStr = $.attr(this.ListDataSourceDiv,"dataSource");
			this.ListDataSource = new $.ListDataSourceFactory(this,ListDataSourceStr);
			
			if($.attr(this.TableGridDiv,"S_OnInit")){
				var onInit = $.attr(this.TableGridDiv,"S_OnInit");
				$(eval(onInit));
			}
			//this.getTableObject().onmouseover=this.showColTips;
	    },
	    getValue:function(index,name){
	    	name = name.toUpperCase();
	        var cell = this.getCellByName(index,name);
	        var editType = this.FieldTypeSet.getFieldByName(name).DefaultEditType;
	        return this.getValueFromCell(cell,editType);
	    },
	    getOldValue:function(index,name){
	    	var cell = this.getCellByName(index,name);
	        return this.getOldValueFromCell(cell);
	    },
	    getDisplayText:function(index,name){
	    	var cell = this.getCellByName(index,name);
	        var editType = this.FieldTypeSet.getFieldByName(name).DefaultEditType;
	        return this.getDisplayTextFromCell(cell,editType);
	    },
	    getOldDisplayText:function(){
	    	var cell = this.getCellByName(index,name);
	        return this.getOldDisplayTextFromCell(cell);
	    },
	    setValue:function(index,name,aId,aDisplayText,isTrigerEvent){
	    	if(isTrigerEvent == null){
	    		isTrigerEvent = true;
	    	}
	    	var tmpColIndex = this.getColIndex(name);
	        var cell = this.getCellByName(index,name); 
	        if(!cell){
	        	return false;
	        }
	        var editType = this.FieldTypeSet.getFieldByName(name).DefaultEditType;
	        if($(cell).data("editer")){
	        	if (aId == $(cell).data("editer").getID()){
	        		return false;
	        	}else{
	               $(cell).data("editer").setValue(aId,aDisplayText);
	               $(cell).attr("isModify",true);
	        	}
	        }else{
	        	 var fieldType = this.getFieldType(this.getColIndex(name));
	             if(this.ListDataSource && fieldType.ListDataSourceName) {
	            	 // 有下拉数据选择
	            	 aDisplayText = fieldType.getListDataTextById(this.ListDataSource,aId);
	             }
	             if($.attr(cell,"isFirstModify") == null || $.attr(cell,"isFirstModify") == true){
	                 if($.attr(cell,"I")){
	                     $.attr(cell,"OldI",$.attr(cell,"I"));
	                     $.attr(cell,"OldValue",cell.innerText);
	                 }else{
	                	 $.attr(cell,"OldI",cell.innerText);
	                	 $.attr(cell,"OldValue","");
	                 }
	                 $.attr(cell,"isFirstModify",false);
	             }
	             if (aDisplayText){
	                cell.innerText = aDisplayText;
	                $.attr(cell,"I",aId);
	             }else{
	                cell.innerText = aId;
	                $.attr(cell,"I","");
	             }
	             if(editType == this.DBHTML){
	                cell.innerHTML = cell.innerText;
	             }
                 if(editType == this.DBTree){
                	 $(cell).empty();//移除所有子节点
                	 $(cell).append("<a class='ico' href='#nogo'></a>");
	    	         $(cell).append("<a class='text' href='#nogo'>"+aId+"</a>");
                 }
	             $.attr(cell,"isModify",true);
	        }
	        var sts = this.getRowSts(index);
	        if (sts =='O'){
	        	this.setRowSts(index,'U');
	        }else if (sts == "NN"){
	            this.setRowSts(index,'N');
	        }
	        if (isTrigerEvent == true && this.OnValueChange){
	        	this.OnValueChange(index,name,this.getOldValueFromCell(cell),this.getValueFromCell(cell));
	        }
	        return true;
	    },
	    getCellByName:function(index,name){
	    	var colIndex = this.getColIndex(name);
	    	var cell = this.getCell(index,colIndex);
	    	return cell;
	    },
	    getColIndex:function(name){
	    	name = name.toUpperCase();  
	    	var index = this.colIndexs[name]; 
	    	if(index == null){
	    		index = -1;
	    	}
	    	return index;
	    },
	    getNameByIndex:function(colIndex){
	    	return this.colNames[colIndex];
	    },
	    getCell:function(rowIndex,colIndex){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	if (tmpRow == null){
	    		return null;
	    	}
	    	if((colIndex == null)||(colIndex<0)||(colIndex >= this.getColCount())){
	    		return null;     
	    	}
	    	if (this.colIsHide[colIndex] ==false ){ //非隐藏列
	            return tmpRow.cells(colIndex + this.getRowHeadColCount());
	        }else{  //隐藏列
	        	if(this.rowHideCols[rowIndex] ==  null){   // 从数据Row的属性中提取隐藏列数据
	        		this.rowHideCols[rowIndex] =  new Array();
	                for(var i= this.visioColCount;i< this.getColCount();i++){
	                   this.rowHideCols[rowIndex][i - this.visioColCount]  = new $.virtualCol();
	                   var tmpID = $(tmpRow).attr(this.colNames[i]);

	                   // 如果是属性中包含&nbsp;的话，则IE会将其转换成字节码为160的字符,16进制为A0
	                   var str_160 = String.fromCharCode(160);
	                   if(tmpID!=null && tmpID.indexOf(str_160)>-1){
	                  	  tmpID = tmpID.replace(/\xA0/g,' ');
	                  	  $(tmpRow).attr(this.colNames[i],tmpID);
	                   }

	                   var tmpText = $(tmpRow).attr(this.colNames[i]+"_DISPLAY");
	                   if(!tmpText){
	                      this.rowHideCols[rowIndex][i - this.visioColCount ].innerText = tmpID;
	                   }else{
	                      this.rowHideCols[rowIndex][i - this.visioColCount ].innerText = tmpText;
	                      this.rowHideCols[rowIndex][i - this.visioColCount ].I = tmpID;
	                   }
	                }
	        	}
	        	return this.rowHideCols[rowIndex][ colIndex - this.visioColCount];
	        }      
	    },
	    getRowObj:function(rowIndex){
	    	if(rowIndex == null){
	    	    return null;
	    	}
	    	rowIndex = parseInt(rowIndex);
	    	if ((rowIndex >=0)&&(rowIndex < this.count())){
	    		return this.getTableObject().rows(rowIndex);
	    	}else{
	    		return null;
	    	}
	    },
	    getValueFromCell:function(cell,editType){
	    	if(!cell){
	    		return "";
	    	}
	    	if ($(cell).data("editer")){
	    		return $(cell).data("editer").getID();
	    	}
	    	if ($(cell).attr("I")){
	    		return $(cell).attr("I");
	    	}else{
	    	    if(editType == this.DBHTML){
	    	    	return cell.innerHTML;
	    	    }else if(editType == this.DBTree){
	    	        return cell.childNodes(cell.childNodes.length -1).innerText;
	    	    }else if(editType == this.DBListBox){
	    	    	var selectObj = cell.childNodes(cell.childNodes.length -1);
	    	    	return selectObj.options[selectObj.selectedIndex].value;
	    	    }else{
	    	        return cell.innerText;
	    	    }
	    	}
	    },
	    getOldValueFromCell:function(cell){
	    	if(!cell)   return "";
	        if(cell.OldI) return cell.OldI;
	        if(cell.OldValue) return cell.OldValue;
	        return "";
	    },
	    getDisplayTextFromCell:function(cell,editType){
	    	if(!cell) return "";
	    	if ($(cell).data("editer")) return $(cell).data("editer").getValue();
	    	if(editType == this.DBHTML){
	    		return cell.innerHTML;
	    	}else if(editType == this.DBTree){
	    	    return cell.childNodes(cell.childNodes.length -1).innerText;
	    	}else{
	    		return cell.innerText;
	    	}
	    },
	    getOldDisplayTextFromCell:function(cell){
	    	if(!cell) return "";
	        if($(cell).attr("OldValue")) return $(cell).attr("OldValue");
	        if($(cell).attr("OldI")) return $(cell).attr("OldI");
	        return "";
	    },
	    getTableObject:function(){
	    	return this.Table;
	    },
	    count:function(){
	    	return this.getTableObject().rows.length;
	    },
	    getColCount:function(){
	    	return this.colNames.length;
	    },
	    getVisiColCount:function(){
	    	return this.visioColCount;
	    },
	    addContextMenu : function (name, e) {
			var trElem = this.findParent(e.target, "TR");
			if (trElem) {
				this.CurRow=trElem.rowIndex; //设置当前行
			}
		},
		findParent : function (elem, tag) {
			var e = null;
			if (elem) {
				e = elem;
				var index=0;//最多向上找10次
				while (e != null && e.nodeName != "TABLE" && index < 10) {
					if (e.nodeName == tag) {
						return e;
					} else {
						e = e.parentNode;
					}
					index++;
				}
			}
			return null;
		},
	    tdOnFocus:function(newRowId ,newColId){
	    	var oldRowIndex = this.CurRow;
	    	var oldColIndex = this.CurCol;
	    	newRowId =parseInt(newRowId);
	    	newColId =parseInt(newColId);
	    	this.setCellEditable(this.CurRow,this.CurCol,false);  // 清除原有控件
	    	this.currentRowChange(-1,oldRowIndex); 
	    	this.setCellEditable(newRowId,newColId,true);  //加载控件
	    	this.CurRow = newRowId;
	    	this.CurCol = newColId; 
	    	this.currentRowChange(this.CurRow,-1); 
	    	if (this.CurRow != oldRowIndex){
	    	    if (this.OnRowFocusChange){
	    	    	this.OnRowFocusChange(oldRowIndex,newRowId);
	    	    }
	    	} 
	    	if (this.OnCellFocusChange){
	    		this.OnCellFocusChange(oldRowIndex,oldColIndex,newRowId,newColId); 
	    	}
	    	return true;
	    },
	    setFocus:function(rowIndex,colIndex){
	    	rowIndex = parseInt(rowIndex);
	    	colIndex = parseInt(colIndex);
	    	this.tdOnFocus(rowIndex,colIndex);
	    },
	    setFocusByName:function(rowIndex,colName){
	    	var index = this.getColIndex(colName);
	    	this.setFocus(rowIndex,index);
	    },
	    currentRowChange:function(newRowIndex,oldRowIndex){ //设置行样式
	    	
	    },
	    setCellEditable:function(rowIndex,columnIndex,sts){
	    	if(!sts){
	    		sts = false;
	    	} 
	    	if(rowIndex < 0 || rowIndex >= this.count() 
	    			|| columnIndex < 0 || columnIndex >= this.getColCount()){
	    		return;
	    	}
	    	var cell = this.getCell(rowIndex,columnIndex);
	    	if(!cell){
	    		return;
	    	}
	    	if (cell && sts == false){ 
	    	    if($(cell).data("editer")){
	    	       var tmpId = $(cell).data("editer").getID();
	    	       var tmpText = $(cell).data("editer").getValue();
	    	       $(cell).data("editer",null);
	    	       cell.innerText = "";
	    	       this.setValue(rowIndex,this.getNameByIndex(columnIndex),tmpId,tmpText,true);
	    	    }
	        }else if(cell && sts == true && this.isCellEditable(rowIndex,columnIndex)){
	        	if (!$(cell).data("editer")){
	        		var tmpId = cell.innerText;
	                var tmpText = "";
	                if($(cell).attr("I")){
	                   var tmpId = $(cell).attr("I");
	                   var tmpText = cell.innerText;
	                }
	                var editer = this.getEditer(rowIndex,columnIndex,tmpId,tmpText);
	                if(editer){
	                    cell.innerText = "";
	       	            $(cell).attr("I",tmpId);
	       	            $(cell).attr("Value",tmpText);
	       	            $(cell).data("editer",editer);
	       	            $(cell).attr("class","edit"); //设置样式
	     		        cell.appendChild(editer.getUIObject());
	     		        editer.setValue(tmpId,tmpText);
	                }
	        	}
	        }else{
	        	$(cell).data("editer",null);
	        }
	    },
	    isCellEditable:function(rowIndex,colIndex){
	    	if((rowIndex <0) ||(rowIndex >= this.count())||(colIndex <0)||(colIndex >=this.getColCount())){
	    		return false;
	    	}
	    	var rowEditable = this.getRowEditSts(rowIndex);
	    	// 获得单元格的编辑状态
	    	var tmpCellEditSts = this.canModfiyCells[rowIndex +"-" + colIndex];
	    	if(!tmpCellEditSts){
	    		tmpCellEditSts = true;
	    	}
	    	var colIsEditable = false;
	    	if(this.canModifyCols[colIndex] && this.canModifyCols[colIndex] == "true"){
	    		colIsEditable = true;
	    	}
	    	return this.IsEditable && rowEditable && tmpCellEditSts && colIsEditable;
	    },
	    getEditer:function(rowIndex,colIndex,aId,aDisplayText){
	    	var fieldType = this.getFieldType(colIndex);
	    	return fieldType.getEditer(this,rowIndex,this.ListDataSource,aId,aDisplayText);
	    },
	    getColWidth:function(colIndex){
	    	var cell = this.Table.rows(0).cells(colIndex + this.getRowHeadColCount());
	    	if(cell){
	    		return cell.width;
	    	}else{
	    		return -1;
	    	}
	    },
	    setColWidth:function(colName,newWidth){
	    	var colIndex = this.getColIndex(colName);
	    	var incWidth  = newWidth - this.getColWidth(colIndex);
	    	for(var i=0;i< this.Table.rows.length; i++){
	    	    this.Table.rows(i).cells(colIndex).width = newWidth;
	    	}
	    	this.Table.width = parseInt(this.Table.width) + incWidth;

	    	if (this.Table && this.Table.rows.length >0){
	    	    this.Table.rows(0).cells(colIndex).width = newWidth;
	    	    this.Table.width = parseInt(this.Table.width) + incWidth;
	    	}
	    },
	    getRowHeadColCount:function(){
	    	var result =0;
	    	if (this.isMutilSelect == true){
	    		result = result+ 1;
	    	}
	    	if (this.isRowSequence == true){
	    		result = result+ 1;
	    	}
	    	return result;
	    },
	    getFieldType:function(colIndex){
	    	return this.FieldTypeSet.getFieldByName(this.colNames[colIndex]);
	    },
	    newRow:function(parentIndex,pkvalue){
	    	var rowIndex =this.count();
	    	return this.insertRow(rowIndex,parentIndex,pkvalue);
	    },
	    insertRow:function(rowIndex,parentIndex,pkvalue){
	    	var tmpRow = this.getTableObject().insertRow(rowIndex);
	    	if(pkvalue!= null ){
	    		tmpRow.I = pkvalue;
	    	}else{
	    		tmpRow.I = this.getNewRowDataId(); 
	    	}
	    	for(var i = 0;i <  this.Table.rows(0).cells.length ;i++){
	            var cell = tmpRow.insertCell(-1);
	            cell.className ="e_center";
	            cell.width = this.Table.rows(0).cells(i).width;
	       }
	       for(var i = 0;i < this.getColCount();i++){
	    	    var fieldType = this.getFieldType(i);
	    	    if(fieldType.getDefaultEditType() =="DBTree"){
	    	    	//var parentIndex = this.getParentOfRow(tmpRow.rowIndex);
	    	    	var finishRowIndex = this.getTableObject().rows.length - 1;
	    	    	var startRowIndex = tmpRow.rowIndex;
	    	    	var endRowIndex = this.getTableObject().rows.length - 1;
	    	    	this.recomputerChildRowIndex(finishRowIndex,startRowIndex,endRowIndex,1);
	    	    	if(parentIndex >=0){
	    	            var list = this.getChildList(parentIndex);
	    	            list[list.length] = tmpRow.rowIndex;
	    	            this.setChildList(parentIndex,list.sort());
	    	            var level = this.getLevel(parentIndex) + 1;
	    	            $(tmpRow).attr("level",level);
	    	            var treeCell =  this.getCell(rowIndex,i);
	    	            $(treeCell).attr("level",level);
	    	            $(treeCell).attr("class","level level-"+level+" file");
	    	            $(treeCell).append("<a class='ico' href='#nogo'></a>");
	    	            $(treeCell).append("<a class='text' href='#nogo'></a>");
	    	        }
	    	    }
	    	    this.setValue(rowIndex,this.getNameByIndex(i),"","");
	       }
	       var mutilSelectIndex = 0;
	       if(this.isRowSequence == true){
	          tmpRow.cells(0).innerHTML = rowIndex;
	          tmpRow.cells(0).className ="e_center";
	          mutilSelectIndex = 1;
	       }
	       if(this.isMutilSelect == true){
	          tmpRow.cells(mutilSelectIndex).innerHTML = " <input  type='checkbox' class='e_center-C' AG='true'/>";
	          tmpRow.cells(mutilSelectIndex).className ="e_center";
	       }
	       $.attr(tmpRow,"Sts","NN");
	       if(this.CurRow == $.attr(tmpRow,"rowIndex")){
	          this.CurRow = this.CurRow + 1;
	       }
	       this.setRow(rowIndex);
	       this.modifyRowSequence($.attr(tmpRow,"rowIndex"));
	       return rowIndex;
	    },
	    getNewRowDataId:function(){
	    	this.NewRowDataId = this.NewRowDataId + 1;
	        return this.FieldTypeSet.getName() + '_' + this.NewRowDataId;
	    },
	    setRow:function(rowIndex){
	    	this.tdOnFocus(rowIndex,this.CurCol);
	    },
	    modifyRowSequence:function(rowIndex){
	    	var Table = this.getTableObject();
	    	for(var i=rowIndex;i < Table.rows.length;i++){
	    		if(this.isRowSequence == true){
	    	        Table.rows(i).cells(0).innerText = i;
	    	    }
	    	}
	    },
	    deleteRow:function(rowIndex){
	    	if(rowIndex == null) rowIndex = this.CurRow;
	    	if ((rowIndex >=0) && (rowIndex < this.count())){
	    		var sts = this.getRowSts(rowIndex);
	    	    if((sts!='NN')&& (sts!='N')){
		    	    var tmpRow = new Array();
		    	    tmpRow.I = this.getRowId(rowIndex);
		    	    var tmpCell;
		    	    for(var i=0;i<this.getColCount();i++){
		    	           tmpCell = new $.virtualCol();
		    	           this.copyCell(this.getCell(rowIndex,i),tmpCell);
		    	           tmpRow[tmpRow.length] = (tmpCell);
		    	    }
		    	    this.deleteRows[this.deleteRows.length] = (tmpRow);
	    	    }
	    	    for(var i = rowIndex ;i < this.count() - 1; i++){
	    	        this.rowHideCols[i] = this.rowHideCols[i + 1];
	    	    }
	    	    this.rowHideCols[this.count() - 1] = null; 
	    		this.getTableObject().deleteRow(rowIndex); //删除行
	    	    if(this.CurRow > rowIndex){
	    	    	this.CurRow  = this.CurRow - 1;
	    	    }else if( this.CurRow == rowIndex){
	    	        if(this.CurRow == this.count()){
	    	           this.setRow(this.CurRow - 1);
	    	        }
	    	    }
	    	}
	    	this.currentRowChange(this.CurRow,-1);
	    	this.modifyRowSequence(rowIndex);
	    },
	    removeDeleteRow:function(pk){
	    	var i = 0;
	    	var pkColIndex = this.getColIndex(this.pkName);
	    	for( i = 0; i < this.deleteRows.length;i++){
	    	    if(this.getValueFromCell(this.deleteRows[i][pkColIndex]) == pk){
	    	    	break;
	    	    }
	    	}
	    	for(var j = i; j < this.deleteRows.length - 1 ;j++){
	    		this.deleteRows[j] = this.deleteRows[j - 1];
	    	}
	    	var result = false;
	    	if(i < this.deleteRows.length){
	    	    this.deleteRows.length = this.deleteRows.length - 1;
	    	    result = true;
	    	}
	    	return result;
	    },
	    getMutilSelectColIndex:function(){
	    	var colIndex = 0;
	    	if(this.isRowSequence == true){
	    		colIndex = 1;
	    	}
	    	return colIndex;
	    },
	    copyCell:function(sourceCell,destCell){
	    	if($(sourceCell).attr("I"))  $(destCell).attr("I",$(sourceCell).attr("I"));
	    	if($(sourceCell).attr("OldI"))  $(destCell).attr("OldI",$(sourceCell).attr("OldI"));
	    	if($(sourceCell).attr("OldValue"))  $(destCell).attr("OldValue",$(sourceCell).attr("OldValue"));
	    	if($(sourceCell).attr("Value"))  $(destCell).attr("Value",$(sourceCell).attr("Value"));
	    	if(sourceCell.innerText)  destCell.innerText = sourceCell.innerText;
	    	if($(sourceCell).attr("isModify"))  $(destCell).attr("isModify",$(sourceCell).attr("isModify"));
	    	if($(sourceCell).attr("isFirstModify"))  $(destCell).attr("isFirstModify",$(sourceCell).attr("isFirstModify"));
	    },
	    getCellSts:function(index,name){
	    	var cell = this.getCellByName(index,name);
	        return this.getCellStsFromCell(cell);
	    },
	    getCellStsFromCell:function(cell){
	    	if(!cell)   return "O";
	        var OldId = this.getOldValueFromCell(cell);
	        var NewId = this.getValueFromCell(cell);
	        if (!cell.isModify){// 没有修改
	           if ((!OldId)&&(!NewId)) return "NN";
	           else if ((!OldId)&&(NewId)) return "O";
	        }else{
	           if ((OldId)&&(!NewId)) return "D";
	           else if ((OldId)&&(NewId)) return "U";
	           else if ((!OldId)&&(NewId)) return "N";
	           else if ((!OldId)&&(!NewId)) return "O";
	        }
	    },
	    getCurRowIndex:function(){
	    	return this.CurRow;
	    },
	    clear:function(){
	    	for(var rowIndex = this.count()- 1;rowIndex>=0;rowIndex--){
	    		this.getTableObject().deleteRow(rowIndex);
	    	}
	    	this.setStsToOld();
	    	this.currentRowChange(-1,-1);   
	    },
	    setStsToOld:function(){
	    	this.deleteRows.length=0;
    	    for(var i=0;i<this.count();i++){
    	        var sts = this.getRowSts(i);
    	        if ((sts == "N")||(sts =="U") ){
    	           var row = this.getRowObj(i);
    	           $(row).attr("Sts","O");
	    	       for(var j=0;j< this.getColCount();j++){
	    	          var cell = this.getCell(i,j);
	    	          cell.OldI = null;
	    	          cell.OldValue = null;
	    	          cell.isModify = false;
	    	          cell.isFirstModify = true;
	    	       }
    	       }
    	    }
	    },
	    getRowSts:function(rowIndex){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	if (tmpRow && $(tmpRow).attr("Sts")){
	    		return $.attr(tmpRow,"Sts");
	    	}else{
	    		return "O";
	    	}
	    },
	    setRowSts:function(rowIndex,sts){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	$.attr(tmpRow,"Sts",sts);
	    },
	    setEditSts:function(value){
	    	if(!value) value = false;
	    	if(value && this.onlyQuery){
	    		alert("table can't edit!");
	    	    return ;
	    	}
	    	this.IsEditable = value;
	    },
	    setColEditSts:function(colName,value){
	    	if(!value) value = false;
	    	if(value && this.onlyQuery){
	    	    alert("table can't edit!");
	    	    return ;
	    	}
	    	var index = this.getColIndex(colName);
	    	if (index >= 0){
	    	    this.getFieldType(index).setAuthority(value);
	    	}
	    },
	    setCellEditSts:function(rowIndex,colName,value){
	    	if(!value) value = false;
	    	var colIndex = this.getColIndex(colName);
	    	this.canModfiyCells[rowIndex +"-" + colIndex] = value;
	    },
	    setRowEditSts:function(rowIndex,value){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	if(tmpRow){
	    		$.attr(tmpRow,"roweditable",value);
	    	}
	    },
	    getRowEditSts:function(rowIndex){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	if (!tmpRow){
	    		return false;
	    	}
	    	if($.attr(tmpRow,"roweditable") == false || $.attr(tmpRow,"roweditable") == "false"){
	    		return false;
	    	}
	    	return true;
	    },
	    getStartRowIndex:function(){
	    	return 0;
	    },
	    getEndRowIndex:function(){
	    	return this.count() - 1;
	    },
	    getRowId:function(rowIndex){
	    	var tmpRow = this.getRowObj(rowIndex);
	    	return $(tmpRow).attr("I");
	    },
	    getColNames:function(){
	    	return this.colNames;
	    },
	    getClassName:function(){
	    	return "TableGrid";
	    },
	    verify:function(rowIndex,colName){
	    	return this.getFieldType(colName).verify(this.getValue(rowIndex,colName)) ;
	    },
	    getSelectedRows:function(){
	    	var result = new Array();
	    	if (this.isMutilSelect== true){
		    	var colIndex =this.getMutilSelectColIndex();
		    	for(var i=0; i < this.count();i++){
		    	    var tmpRow = this.getRowObj(i);
		    	    if(tmpRow.cells(colIndex).children(0).checked == true)
		    	        result[result.length] = (i);
		    	}
	    	}else if (this.CurRow >=0){
	    		result[result.length] = (this.CurRow);
	    	}
	    	return result;
	    },
	    rowSelected:function(rowIndex,isSelected,isTriggerEvent){
	    	if(this.isMutilSelect== true){
	    	   var tmpRow = this.getRowObj(rowIndex);
	    	   if(tmpRow){
	    		  var colIndex =this.getMutilSelectColIndex();
	    	      tmpRow.cells(colIndex).children(0).checked = isSelected;
	    	      tmpRow.isSelected = isSelected;
	    	      this.currentRowChange(rowIndex,this.CurRow);
	    	      this.currentRowChange(this.CurRow,rowIndex);
	    	      if(isTriggerEvent != false){
	    		     isTriggerEvent =true;
	    	      }
	    		  if(isTriggerEvent && this.OnRowSelected){
	    	       	 this.OnRowSelected(rowIndex,isSelected);
	    	      }
	    	   }
	    	}else{
	    	    this.setRow(rowIndex);
	    	}
	    },
	    isSelected:function(rowIndex){
	    	var result = false;
	        if(this.isMutilSelect== true){
	          var tmpRow = this.getRowObj(rowIndex);
	          var colIndex =this.getMutilSelectColIndex();
	          if((tmpRow) &&(tmpRow.cells(colIndex).children(0).checked == true)){
	        	  result = true;
	          }
	        }else if(this.CurRow  == rowIndex){
	        	result = true;
	        }
	        return result;
	    },
	    setAllSelectCheckBoxSts:function(flag){
	    	this.disabledSelect(-100,!flag);
	    },
	    disabledSelect:function(rowIndex,flag){
	    	if(this.isMutilSelect== true){
	    	    var tmpRow;
	    	    if(rowIndex == -100){
	    	        tmpRow = this.Table.rows(0);
	    	    }else{
	    	        tmpRow = this.getRowObj(rowIndex);
	    	    }
	    	    if(tmpRow){
	    		    var colIndex =this.getMutilSelectColIndex();
	    	        var obj = tmpRow.cells(colIndex).children(0);
	    	        obj.disabled = flag;
	    	    }
	    	}
	    },
	    visibleSelect:function(rowIndex,flag){
	    	var str = "none";
	        if(flag == true){
	        	str = "block";
	        }
	        if(this.isMutilSelect== true){
	           var tmpRow;
	           if(rowIndex == -100){
	              tmpRow = this.Table.rows(0);
	           }else{
	              tmpRow = this.getRowObj(rowIndex);
	           }
	           if(tmpRow){
	    	      var colIndex =this.getMutilSelectColIndex();
	              var obj = tmpRow.cells(colIndex).children(0);
	              obj.style.display =str;
	           }
	        }
	    },
	    selectAll:function(isSelected,isTriggerEvent){
	    	var colIndex =this.getMutilSelectColIndex();
	    	this.Table.rows(0).cells(colIndex).children(0).checked = isSelected;
	    	for(var i=0; i < this.count();i++){
	    	     var tmpRow = this.getRowObj(i);
	    	     if(tmpRow.cells(colIndex).children(0) && tmpRow.cells(colIndex).children(0).style.display !="none"){
	    	         tmpRow.isSelected = isSelected;
	    	         tmpRow.cells(colIndex).children(0).checked = isSelected;
	    	     }
	    	}
	    	if(isTriggerEvent != false){
	    	   isTriggerEvent =true;
	    	}
	    	if(isTriggerEvent &&  this.OnRowSelected){
	    	   this.OnRowSelected(-100,isSelected);
	    	}
	    },
	    setTitle:function(colName,aTitle){
	    	var index = this.getColIndex(colName);
	    	this.setTitleByIndex(index,aTitle);
	    },
	    setTitleByIndex:function(index,aTitle){
	    	if(index <0)return;
	    	// 将改变的title存放起来
	    	if(this.changeTitleArr == null){
	    	    this.changeTitleArr = new Array();
	    	}
	    	this.changeTitleArr["" + index] = aTitle;
	    	var cell = this.Table.rows(0).cells(index + this.getRowHeadColCount());
	    	if(cell.childNodes.length<2){
	    	    cell.innerText=aTitle;
	    	}else{
	    	    cell.childNodes[1].innerText=aTitle;
	    	}
	    },
	    getTitle:function(colName){
	    	var index = this.getColIndex(colName);
	    	if(index >=0){
	    		return this.getTitleByIndex(index);
	    	}else{
	    		alert("illegal_field_name："+colName);
	    	}
	    },
	    getTitleByIndex:function(index){
	    	if(index >=0){
	    	    var cell = this.Table.rows(0).cells(index + this.getRowHeadColCount());
	    	    if(cell.childNodes.length<2){
	    	       return cell.innerText;
	    	    }else{
	    	       return cell.childNodes[1].innerText;
	    	    }
	    	}else{
	    		alert("illegal_field_index："+index);
	    	}
	    },
	    isRowHidden:function(rowIndex){
	    	var rowObj = this.getRowObj(rowIndex);
	    	if($(rowObj).attr("isHidden")){
	    		return $(rowObj).attr("isHidden");
	    	}
	    	return false;
	    },
	    setRowHidden:function(rowIndex,isHidden){
	    	var rowObj = this.getRowObj(rowIndex);
	    	if(isHidden==true){
	    		$(rowObj).attr("isHidden", true);
	    		//rowObj.style.display ="none";
	    		$(rowObj).css("display","none");
	        }else{
	        	$(rowObj).attr("isHidden", false);
	        }
	    },
	    showColTips:function(){
	    	var obj = window.event.srcElement;
	    	if(event.srcElement.tagName !='TD') return;
	    	obj = obj.parentNode;
		    obj.className="td_hover";
	    	
		    // 判断显示的title是否是tag显示，如果是，则不修改，否则实时设置单元格的title
	    	if(event.srcElement.tagPrompt == null || event.srcElement.tagPrompt==""){
	    		if(event.srcElement.title != null && event.srcElement.title != "" ){
	    			event.srcElement.tagPrompt = "true";
	    		}else{
	    			event.srcElement.tagPrompt = "false";
	    		}
	    	}
	    	if(event.srcElement.tagPrompt == "false" ){
	    		event.srcElement.title = event.srcElement.innerText;	
	    	}
	    },
	    toJsonArray:function(isOnlySendModifyData,colnames){
	    	this.tableFocusout();//
	    	var result = new $.DatasetList();
	        var colIndexs = new Array();
	        if(colnames == null || colnames.length == 0){
	            for(var i = 0; i < this.getColCount(); i++){
	                colIndexs[i] = i;
	            }
	        }else{
	            var tmpNames = colnames.split(",");
	            for(var i = 0; i < tmpNames.length; i++){
	                colIndexs[i] = this.getColIndex(tmpNames[i]);
	            }
	        }
	        for(var i = 1; i < this.count(); i++){
	            var s= this.toJsonDataRow(i,isOnlySendModifyData,colIndexs);
	            if(typeof(s) == "object" && s instanceof $.DataMap){
	            	result.add(s);	
	            }
	        }
	        return result;
	    },
	    toJsonDataRow:function(rowIndex,isOnlySendModifyData,colIndexs){
	    	if(isOnlySendModifyData == null){
	    		isOnlySendModifyData = true;
	    	}
	    	var isSelect = this.isSelected(rowIndex);
	    	if(!isSelect && this.isMutilSelect== true){
	    		return "";
	    	}
	    	var sts = this.getRowSts(rowIndex);
	    	if (isOnlySendModifyData == true && (sts == "O" || sts =="NN")){
	    		return "";
	    	}
	    	var result = new $.DataMap();
	    	for(var i=0;i< colIndexs.length;i++){
    		    var colName = this.getNameByIndex(colIndexs[i]);
    		    var cell = this.getCell(rowIndex,colIndexs[i]);
    		    var cellData = this.toJsonDataCell(cell,this.getNameByIndex(colIndexs[i]));
    		    if(typeof(cellData) == "object" && cellData instanceof $.DataMap){
    			    result.put(colName, cellData.get("ID"));
    		    }
	    	}
	    	return result;
	    },
	    toJsonDataCell:function(cell,name){
	    	var sts = this.getCellStsFromCell(cell);
	  	    var cellData = new Wade.DataMap();
	  	    cellData.put("NAME",name);
	  	    var OldId = this.getOldValueFromCell(cell);
	  	    var OldText = this.getOldDisplayTextFromCell(cell);
	  	    var NewId = this.getValueFromCell(cell);
	  	    var NewText = this.getDisplayTextFromCell(cell);

	  	    if(OldId == OldText){
	  	    	OldText = "";
	  	    }
	  	    if(NewId == NewText){
	  	    	NewText = "";
	  	    }
	  	    if(sts =="O"){
	  	        if(NewId){
	  	    	   cellData.put("OLD_ID",NewId); 
	  	    	   cellData.put("ID",NewId); 
	  	        }	  
	  	        if(NewText){
	  	    	   cellData.put("OLD_TEXT",NewText); 
	  	        }
	  	    }else{
	  	        if(OldId){
	  	    	   cellData.put("OLD_ID",OldId);
	  	        }
	  	        if(OldText){
	  	    	   cellData.put("OLD_TEXT",OldText);
	  	        }
	  	        if(NewId){
	  	       	   //var NewId = TableRowSet_checkAndTrans(NewId);
	  			   if('DBTextArea'==this.FieldTypeSet.getFieldByName(name).DefaultEditType){
	  				   NewId = NewId.replace(/\x0d\x0a/g,'@~');
	  		       }
	  			   cellData.put("ID",NewId);
	  		    }
	  	        if(NewText){
	  	           //NewText = TableRowSet_checkAndTrans(NewText);
	  	           if(NewText.replace(" ","").length ==0){
	  	        	   cellData.put("NEW_TEXT",NewText);
	  	           }else{
	  	        	   cellData.put("VALUE",NewText);
	  	           }
	  	        }
	  	    }
	  	    return cellData;
	    },
	    tableFocusout:function(){
	    	if(this.CurRow >=0 && this.CurCol >=0){
	    	     var cell = this.getCell(this.CurRow,this.CurCol);
	    	     if (cell && $(cell).data("editer") && $(cell).data("editer").getID() != $(cell).attr("I")){
	    	       if(!$(cell).attr("isFirstModify") || $(cell).attr("isFirstModify") == true){
	    	            $(cell).attr("OldI",$(cell).attr("I"));
	    	      	    if($(cell).attr("Value")){
	    	      	    	$(cell).attr("OldValue",$(cell).attr("Value"));
	    	      	    }
	    	      	    $(cell).attr("isFirstModify",false);
	    	       }
	    	       $(cell).attr("isModify",true);
	    	       var sts = this.getRowSts(this.CurRow);
	    	       if (sts =='O'){
	    	    	   this.setRowSts(this.CurRow,'U');
	    	       }else if (sts == "NN"){
	    	    	   this.setRowSts(this.CurRow,'N');
	    	       }
	    	   }
	       }
	    },
	    open:function(rowIndex,colIndex){
	    	var list = this.getChildList(rowIndex);
	    	for(var i = 0; i < list.length; i++){
    	       this.getRowObj(list[i]).style.display ="block";
    	       if(this.getIsOpen(list[i]) == true){
    	          this.open(list[i]);
    	       }
	        }
	    	var rowObj = this.getRowObj(rowIndex);
	    	var icoNode = $("a[class=ico]",rowObj);
	    	var parentCss = icoNode.parent().attr("class");
	    	icoNode.parent().attr("class", parentCss.replace(" fold", " unfold"));
	    	$.resizeHeight();//自适应高度
			parendCss=null;
	    },
	    close:function(rowIndex,colIndex){
	    	var list = this.getChildList(rowIndex); 
	        for(var i=0;i<list.length;i++){
	           this.close(list[i]);
	           this.getRowObj(list[i]).style.display ="none";
	        }
	        var rowObj = this.getRowObj(rowIndex);
	    	var icoNode = $("a[class=ico]",rowObj);
	    	var parentCss = icoNode.parent().attr("class");
	    	icoNode.parent().attr("class", parentCss.replace(" unfold", " fold"));
	    	$.resizeHeight();//自适应高度
			parendCss=null;
	    },
	    getChildList:function(rowIndex){
	    	var rowObj =  this.getRowObj(rowIndex);
	        if(!rowObj){
	        	return new Array();
	        }
	        var list = $(rowObj).data("childArray");
	        if(list == null){
	           list = new Array();
	           if($(rowObj).attr("child_list")){
	              var strList = $(rowObj).attr("child_list").split(',');
	              for(var i = 0; i < strList.length; i++)
	               list[i] = parseInt(strList[i]);
	           }
	           $(rowObj).data("childArray",list);
	        }
	        return list;
	    },
	    setChildList:function(rowIndex,childList){
	    	 var rowObj =  this.getRowObj(rowIndex);
	    	 $(rowObj).data("childArray",childList);
	    },
	    getIsOpen:function(rowIndex){
	    	var rowObj = this.getRowObj(rowIndex);
	    	var icoNode = $("a[class=ico]",rowObj);
	    	var parentCss = icoNode.parent().attr("class");
	        return parentCss.indexOf(' unfold') != -1;
	    },
	    getParentOfRow:function(rowIndex){
	    	var tmpRowIndex = rowIndex;
	    	while(tmpRowIndex >0){
	    	    tmpRowIndex = tmpRowIndex - 1;
	    	    var list = this.getChildList(tmpRowIndex);
	    	    for(var i = 0; i < list.length; i++){
	    	      if(list[i] == rowIndex)
	    	        return tmpRowIndex;
	    	    }
	    	}
	    	return -1;
	    },
	    getLevel:function(rowIndex){
	    	var rowObj = this.getRowObj(rowIndex);
	    	return parseInt($(rowObj).attr("level"));
	    },
	    recomputerChildRowIndex:function(finishRowIndex,startRowIndex,endRowIndex,increase){
	    	var dataTable = this.getTableObject();
	        for(var i = 0; i <= finishRowIndex; i++){
	           var list = this.getChildList(i);
	           if(list.length > 0){
	               for(var j = 0; j < list.length; j++){
	                  if(list[j] >= startRowIndex && list[j] <= endRowIndex)
	                    list[j] = list[j] + increase ;
	               }

	              this.setChildList(i,this.getChildList(i).sort(sortDigit));
	           }
	        }
	    },
	    getLastChildIndexOfRow:function(index){
	    	var list = this.getChildList(index);
	    	if(list.length == 0){
	    		return -1;
	    	}
	        var childCount = list.length;
	        var lastChild = list[0];
	        for(var i=0;i<childCount;i++){
	        	if(lastChild<list[i]){
	        		lastChild = list[i];
	        	}
	        }
	        return lastChild;
	    },
	    getLastRowIndexOfChild:function(rowIndex){
	    	var list = this.getChildList(rowIndex);
	        if(list.length == 0){
	        	return rowIndex;
	        }else{
	        	return this.getLastRowIndexOfChild(list[list.length -1]);
	        }
	    },
	    refresh:function(params){
	    	var tableId = this.DBGridPK;
	    	var scrollId = 'Scroll_'+this.DBGridPK;
	    	var partId = $('#'+scrollId).parent().attr("id");
	    	var navId = 'navbar_'+this.DBGridPK;
	    	$.beginPageLoading('Loading.....');
	    	ajaxSubmit(null,null,params,partId,function(){
	    		$.endPageLoading();
	    		$.tableManager.get(tableId).initial(); //重新初始化表格实例
	    		$('#'+navId).attr('condparams',params); //缓存请求参数供NavBar使用
	    	},function(code, info, detail){
	    		$.endPageLoading();
	    		MessageBox.error("错误提示","表格刷新失败！",null, null, info, detail);	
	    	},{async:false});
	    }
	});
})(Wade);

function TableGrid_OnFocusOut(aGridPK){
	var obj = window.event.srcElement;
    var parentObj = $.tableManager.get(aGridPK);
    if(parentObj.CurRow < 0 || parentObj.CurCol < 0){
       return;
    }
    var cell = parentObj.getCell(parentObj.CurRow,parentObj.CurCol);
    if (cell && $(cell).data("editer")){
    	var colName =parentObj.getNameByIndex(parentObj.CurCol);
        var tmpId = parentObj.getValue(parentObj.CurRow,colName);
        parentObj.setValue(parentObj.CurRow,colName,tmpId,"",true,true);
    }
    // 判断是否有失去焦点这个用户事件
  	var dbgrid = $.tableManager.get(aGridPK);
  	if(dbgrid.OnFocusOut==null){
  		return;
  	}
  	var tableDiv = document.getElementById("TableGrid_" + aGridPK);
  	var toElement = event.toElement;
  	while(true){
  		if(toElement== tableDiv){
  			return; //说明在区域内部,没有失去焦点,不用触发OnFocusOut
  		};
  		if(toElement==null || toElement.tagName=='BODY'){
  			break;	 //说明失去焦点
  		}
  		toElement = toElement.parentNode;
  	}
    if(cell && $(cell).data("editer")){
    	dbgrid.OnFocusOut(cell);
    }else{
    	dbgrid.OnFocusOut();
    }
}

function TableGrid_OnClick(aGridPK){
  var obj = window.event.srcElement;
  var parentObj = $.tableManager.get(aGridPK);
  if (obj.tagName == "TD"){   //单元格
     var rowid = parseInt(obj.parentNode.rowIndex);
     var colid = parseInt(obj.cellIndex) - parentObj.getRowHeadColCount();
     parentObj.tdOnFocus(rowid,colid);
     if(parentObj.OnRowClick && obj.type!="checkbox"){
    	 parentObj.OnRowClick(rowid,obj.checked);
     }
  }else if(obj.tagName == "SPAN"){ //单元格内容
     var rowid = parseInt(obj.parentNode.parentNode.rowIndex);
     var colid = parseInt(obj.parentNode.cellIndex) - parentObj.getRowHeadColCount();
     parentObj.tdOnFocus(rowid,colid);
  }else if(obj.tagName == "INPUT" && obj.type=="checkbox" && $(obj).attr("AG")){  //多选框
     var rowid = parseInt(obj.parentNode.parentNode.rowIndex);
     var colid = parseInt(obj.parentNode.cellIndex) - parentObj.getRowHeadColCount();
     var tmpRow = parentObj.getRowObj(rowid);
     if (tmpRow){
         tmpRow.isSelected = obj.checked;
         parentObj.currentRowChange(rowid,parentObj.CurRow);
         parentObj.currentRowChange(parentObj.CurRow,rowid);
     }
     if(colid < 0 && parentObj.OnRowSelected){
         parentObj.OnRowSelected(rowid,obj.checked);
     }
  }else if(obj.tagName =="A" && $(obj).attr("class") == "ico"){// 树节点的扩展
     var rowid = parseInt(obj.parentNode.parentNode.rowIndex);
     var colid = parseInt(obj.parentNode.cellIndex) - parentObj.getRowHeadColCount();
     var parentCss = $(obj.parentNode).attr("class");
     if(parentCss && parentCss.indexOf("file") != -1) {
    	 return true;  //如果是叶子节点则不处理
     }
     if(parentCss.indexOf(' unfold') != -1) {
    	 parentObj.close(rowid,colid); //关闭
	 }else if(parentCss.indexOf(' fold') != -1){
		 parentObj.open(rowid,colid);//打开
	 }
  }
}

function TableGrid_OnDbClick(aGridPK){
	var obj = window.event.srcElement;
	var parentObj = $.tableManager.get(aGridPK);
	if ((obj.tagName == "TD")){
	    var rowid = parseInt(obj.parentNode.rowIndex);
	    var colid = parseInt(obj.cellIndex) - parentObj.getRowHeadColCount();
	    parentObj.tdOnFocus(rowid,colid);
	    if (parentObj.OnGridDbClick){
	    	parentObj.OnGridDbClick(rowid,colid);
	    }
	}
}

function TableGrid_allSelectChange(aPK){
	var isSelected = window.event.srcElement.checked;
	var parentObj = $.tableManager.get(aPK);
	parentObj.selectAll(isSelected,true);
}

function TableGrid_OnDBLink(aGridPK,aColName,aId){
	var parentObj = $.tableManager.get(aGridPK);
	if(parentObj.OnDBLink){
		TableGrid_OnClick(aGridPK);
	    parentObj.OnDBLink(aColName,aId);
	}
}

function sortDigit(a,b){
  var tmpA = parseFloat(a);
  var tmpB = parseFloat(b);
  if(isNaN(tmpA) == false && isNaN(tmpB)== false){
     if(tmpA == tmpB){
    	 return 0;
     }else if (tmpA > tmpB){
       return 1;
     }else{
       return -1;
     }
  }else{
     if(a == b){
       return 0;
     }else if (a > b){
       return 1;
     }else{
       return -1;
     }
  }
}