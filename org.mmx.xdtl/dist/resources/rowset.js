/*
 * Executed at engine startup. Any variables defined here will be globals. 
 */
 
"use strict";

/**
* Java 1.7-1.8 compatibility support functions
*/
function xdtlStringArray(s) {
    if (String.prototype.trimLeft === undefined)
        return java.lang.reflect.Array.newInstance(java.lang.String, s);                 		//Java1.7
    return java.lang.reflect.Array.newInstance(Java.type('java.lang.String[]'), s);  			//Java1.8
}

function xdtlJoin(arg, separator) {
	if (String.prototype.trimLeft === undefined) return arg.join(separator);			//Java1.7
	// return Java.from(arg.toString());
	return Java.from(arg).toString().join(separator);		//Java1.8
	// return java.util.Arrays.asList(Java.to(arg.join(separator), Java.type('java.lang.String[]')));	
}

function xdtlSplit(arg, separator) {
	if (String.prototype.trimLeft === undefined) return arg.split(separator);							//Java1.7
	return java.util.Arrays.asList(Java.to(arg.split(separator), Java.type('java.lang.String[]')));		//Java1.8
}


/**
 * Function converts input Array to ArrayList.
 */
function xdtlArrayToArray(arr) {
	var a = new java.util.ArrayList(arr.length);
	for (var i = 0; i < arr.length; i++) {
		a.add(arr[i]);
	}
	return a;
}

/**
 * Function returns file list as ArrayList (Rowset)???
 */
function xdtlFileList(fname, filterstring) {
	var dir = new java.io.File(fname);
	if (filterstring) {
		var generic_obj = { 
			filter: filterstring, 
			accept: function (dir, name) { return name.indexOf(filterstring) > -1; } 
		}
		var filter = new java.io.FilenameFilter(generic_obj);
	}
	else
		filter = null;

	return xdtlArrayToArray(dir.list(filter));
}


/**
 * Function creates new Java Arraylist (Rowset) using function 'arguments'.
 */
function xdtlArray() {
	var a = new java.util.ArrayList(arguments.length);
	for (var i = 0; i < arguments.length; i++) {
		a.add(arguments[i]);
	}

	return a;
}

/**
 * Function adds 'arguments' to existing ArrayList.
 * @a {ArrayList}.
 */
function xdtlArray2(a) {
	for (var i = 0; i < arguments.length; i++) {
		a.add(arguments[i]);
	}

	return a;
}

/**
 * Function converts input Array to comma separated string.
 * @a {Array}.
 */
function xdtlArrayToString (a) {
	var s='';
	s = a.toString();
	return s;
}

/**
 * Function converts input Array to comma separated string.
 * @a {Array}.
 */
function xdtlArrayToString2 (a) {
	var s='';
	for (var i = 0; i < a.length; i++) {
		if (a[i] != null)
			var e = a[i].toString().trim();
		else
			var e = '';
		if (i == 0)
			s = e;
		else
			s = s + ',' + e;
	}
	return s;
}

/**
 * Function converts comma separated list to new JavaSript Array.
 * @s {String} Comma separeated list that converted to array.
 */
function xdtlStringToArray(s) {

	var arr = s.split(',');
	return arr;
}

/**
 * Function converts comma separated list to new single column rowset. (NOT xdtl:Read command friendly).
 * @s {String} Comma separeated list that converted to rowset.
 */
function xdtlStringToRowset(s) {

	var arr = s.split(',');
	var a = new java.util.ArrayList(arr.length);
	for (var i = 0; i < arr.length; i++) {
		a.add(arr[i].trim());
	}
	return a;
}

/**
 * Function converts comma separated list to array and adds to existing or new rowset.
 * Rowset is Java ArrayList and row is Java Array (xdtl:Read command friendly).
 * @rs {ArrayList} Existing or new Rowset.
 * @s {String} Comma separeated list that converted to Array and added to rowset.
 */
function xdtlStringToRowset2(rs,s) {

	if (rs == null)
		var rs = new java.util.ArrayList(1);

	var arr = s.split(',');
	// var newrow = java.lang.reflect.Array.newInstance(java.lang.String, arr.length);
	var newrow = xdtlStringArray(arr.length);				//Java 1.7-1.8 Compatibility

	for (var i = 0; i < arr.length; i++) {
		newrow[i] = arr[i].toString();
	}
	rs.add(newrow);
	return rs;
}

/**
 * Function converts comma separated list to array and adds to existing or new rowset.
 * Rowset is Java ArrayList and row is Java Array (xdtl:Read command friendly).
 * @rs {ArrayList} Existing or new Rowset.
 * @s {String} Comma separeated list that converted to Array and added to rowset.
 */
function xdtlStringArrayToRowset(rs,arr) {

	if (rs == null)
		var rs = new java.util.ArrayList(1);

	// var newrow = java.lang.reflect.Array.newInstance(java.lang.String, arr.length)
	var newrow = xdtlStringArray(arr.length);				//Java 1.7-1.8 Compatibility

	for (var i = 0; i < arr.length; i++) {
		newrow[i] = arr[i].toString();
	}
	rs.add(newrow);
	return rs;
}

/**
 * Function converts rowset to string.
 * @rs {ArrayList} Input rowset.
 */
function xdtlRowsetToString(rs) {
	if (rs.size() == 0) return '';
	var result = '';

	for (var i = 0; i < rs.size(); i++) {
		// result += rs.get(i).join(',') + '|\n'
		// result += xdtlJoin(rs.get(i),',') + '|\n';
		result += Java.from(rs.get(i)).join(',') + '|\n';
	}

	return result;
}

/**
 * Function converts comma separated list to array and appends that row to rowset.
 * @rs {ArrayList} Existing rowset.
 * @s {String} Comma separeated list that converted to Array.
 */
function xdtlRowsetAppend(rs, s) {
	var row = s.split(',');
	rs.add(row);
	return rs;
}

/**
 * Function converts comma separated list to array and appends that row to rowset.
 * New Java ArrayList created when rowset parametes is not present.
 * @rs {ArrayList} Existing rowset.
 * @s {String} Comma separeated list that converted to Array.
 */
function xdtlRowsetAppend2(rs,s) {

	var arr = s.split(',');

	if (rs == null || rs == 'undefined')
		var rs = new java.util.ArrayList(1);

	if (arr.length != null && arr.length > 0)
		// var newrow = java.lang.reflect.Array.newInstance(java.lang.String, arr.length);
		var newrow = xdtlStringArray(arr.length);				//Java 1.7-1.8 Compatibility
	else
		// var newrow = java.lang.reflect.Array.newInstance(java.lang.String, 1);
		var newrow = xdtlStringArray(arr.length);				//Java 1.7-1.8 Compatibility

	for (var i = 0; i < arr.length; i++) {
	 		newrow[i] = arr[i];
	 	}

	//if (!rs.contains(newrow))
	rs.add(newrow);

	return rs;
}


/**
 * Function adds HasMap row to existing ArrayList rowset.
 * @rowset {ArrayList} Existing rowset.
 * @rowid {Int} row number where to add new column value. If null then all rows will 
 * @column {String?} Comma separeated list that converted to Array.

 */
function xdtlRowsetAppend3(rowset, map) {

	if (rowset == null)
		// ArrayList <HashMap>
		var rowset = new java.util.ArrayList(1);

	//var row = new java.util.HashMap();

	rowset.add(map);

	// if (map != null && map.size() > 0) {
	// 	for (var i = 0; i < map.size(); i++) {
	// 		var row = map;	//map.get(i);
	// 		rowset.add(row);
	// 	}
	// }
	return rowset;
}


/**
 * Function adds new column each rowset row, new column value added to each rows or specified row.
 * @rowset {ArrayList} Existing rowset.
 * @rowid {Int} row number where to add new column value. If null then all rows will 
 * @column {String?} Comma separeated list that converted to Array.
 */
function xdtlRowsetAddColumn(rowset, rowid, column) {
	// if (rowset == null) {
	// 	var rowset = new java.util.ArrayList(1);
	// 	var row = new java.util.Arrays.copyOf([""],1);
	// }
	// else {
	for (var i = 0; i < rowset.size(); i++) {

		var row = rowset.get(i);
		if (row.length <= 0) {
			//row = java.util.Arrays.asList("");
			// var newrow = java.lang.reflect.Array.newInstance(java.lang.String, 1);
			var newrow = getNewJavaStringArray(s); 			//Java1.7-1.8 Compatibility Function
		}
		else
     		var newrow = java.util.Arrays.copyOf(row, row.length + 1);

     	if (rowid >=0 && rowid == i) {
     		newrow[row.length] = column.toString();
     	}
     	if (rowid == null) {
     		newrow[row.length] = column.toString();
     	}
		rowset.remove(i);
		rowset.add(i,newrow);
	}

	return rowset;
}

/**
 * Function Creates New rowset (ArrayList).
 */
// function xdtlNewJArray(i) {

// 	if (i != null && i > 0)
// 		var newrow = java.lang.reflect.Array.newInstance(java.lang.String, i)
// 	else 
// 		var newrow = java.lang.reflect.Array.newInstance(java.lang.String, 1)

// 	return newrow;
// }

/**
 * Function Creates New rowset (ArrayList).
 */
// function xdtlNewJRowset(i) {

// 	var rowset = new java.util.ArrayList();
// 	if (i != null && i > 0)
// 		var newrow = java.lang.reflect.Array.newInstance(java.lang.String, i)
// 	else 
// 		var newrow = java.lang.reflect.Array.newInstance(java.lang.String, 1)

// 	rowset.add(0,newrow);

// 	return rowset;
// }

/**
 * Function adds rowset to existing rowset.
 * @rowset {ArrayList} Existing rowset.
 * @rowid {Int} row number where to add new column value. If null then all rows will 
 * @column {String?} Comma separeated list that converted to Array.
 */
function xdtlRowsetToRowset(rowset, rs) {

	if (rowset == null)
		var rowset = new java.util.ArrayList(1);

	if (rs != null && rs.size() > 0) {
		for (var i = 0; i < rs.size(); i++) {
			var row = rs.get(i);
			rowset.add(row);
		}
	}
	return rowset;
}

function xdtlRowsetToCSV(rowset, c, d) {
	if (rowset.size() == 0) return '';
	c = c || ';';
	d = d || '"'
	var result = '';
	for (var i = 0; i < rowset.size(); i++) {
		// result += d + rowset.get(i).join(d + c + d) + d + BR;
		// result += d + xdtlJoin(rowset.get(i),d + c + d) + d + BR;  	//Java 1.7-1.8 Compatibility
		result += d + Java.from(rowset.get(i)).join(d + c + d) + d + BR;
	}
	return result;
}

function xdtlRowsetInsertColumn(rowset, colno, column) {

    rs = rowset.get(0);
    var a = new java.util.ArrayList(rs.length + 1);
    var row = []
	for (var i = 0; i < rs.length; i++) {
        if (i != colno) {
            row.push(rs[i]);
        }
        else {
            row.push(column.toString());
            row.push(rs[i]);
        }
	}
    a.add(row);
	return a;
}


