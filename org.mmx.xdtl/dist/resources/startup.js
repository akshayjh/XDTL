/*
 * Executed at engine startup. Any variables defined here will be globals. 
 */
 
"use strict";

/**
 * General variables to use in xdtl runtime.
 */
var xdtlDateCode = java.lang.String.format("%1$tY%1$tm%1$td", new java.util.Date());
var xdtlDateTimeCode = java.lang.String.format("%1$tY%1$tm%1$td.%1$tH%1$tM%1$tS", new java.util.Date());
var BR = java.lang.System.getProperty("line.separator");
var FILESEP = java.lang.System.getProperty("file.separator");
var PATHSEP = java.lang.System.getProperty("path.separator");
var USER = java.lang.System.getProperty("user.name");

/* Function removed in 2.0 release */

var removeThrower = function(fname) { throw "Function " + fname + " has removed in XDTL RT 2.0"; };
var removedFunctions = [
	'xdtlArrayToArray', 
	'xdtlStringToArray', 
	'xdtlStringToRowset', 
	'xdtlValuesToRow', 
	'xdtlFileList',
	'xdtlRowsetToString',
	'xdtlRowsetToList',
	'xdtlRowsetToCSV',
	'xdtlArrayToString',
	'xdtlArrayToString2',
	'xdtlListToRowset'];
for (var i = 0; i < removedFunctions.length; i++) {
	var nm = removedFunctions[i]; 
	this[nm] = function() { removeThrower(nm); }; 
}


function xdtlIIf(cond, yes, no) {
	return (cond) ? yes : no;
}

/**
 * Function checks if directiory name exists in filesystem.
 */
function xdtlDirectoryExists(dir) {
	var f = new java.io.File(dir);
	return f.exists() && f.isDirectory();
}

function xdtlMakeDirectory(dir) {
	var f = new java.io.File(dir);
	return f.mkdir();
}

/**
 * Function checks if file exists in filesystem.
 */
function xdtlFileExists(fname) {
	var f = new java.io.File(fname);
	return f.exists() && f.isFile();
}

/**
 Check if a string is part of a list.
 */
function xdtlInList(list, s) {
	if (list.indexOf(s) >= 0) {
		return true;
	}
	return false;
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
 Convert list to trimmed and quoted list with optional quote character (' is default), suitable for SQL statements.
 */
function xdtlListToQuotedList(s, c) {
	c = c || "'"; 
	var result = c + s.split(' ').join('').split(',').join(c + ',' + c) + c;
	return result;
}	

/**
 Return Value (second element) of key-value pair with optional separator character (: is default).
 */
function xdtlGetValue(s, c) {
	c = c || ":"; 
	var result = '';

	//Java 1.8 compatible (Javascript way)
	var arr = s.split(c);					              
	if (arr.length > 0) {
		result = arr[1];
	}
	return result;
}

/**
 Return Key (first element) of key-value pair with optional separator character (: is default).
 */
function xdtlGetKey(s, c) {
	c = c || ":"; 
	var result = '';

	//Java 1.8 compatible (Javascript way)
	var arr = s.split(c);					              
	if (arr.length > 0) {
		result = arr[0];
	}
	return result;
}


/**
 * Convert comma separated list to new single column rowset. (NOT xdtl:Read command friendly).
 */
function xdtlListToRowset(s) {
	var arr = s.split(',');
	var a = new java.util.ArrayList(arr.length);
	for (var i = 0; i < arr.length; i++) {
		a.add(arr[i].toString().trim());
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
 * Function converts comma separated list to array and adds to existing or new rowset.
 * Rowset is Java ArrayList and row is Java Array (xdtl:Read command friendly).
 * @rs {ArrayList} Existing or new Rowset.
 * @s {String} Comma separeated list that converted to Array and added to rowset.
 */
function xdtlStringToRowset2(rs,s) {

	if (rs == null)
		var rs = new java.util.ArrayList(1);

	var arr = s.split(',');
	var newrow = java.lang.reflect.Array.newInstance(java.lang.String, arr.length)

	for (var i = 0; i < arr.length; i++) {
		newrow[i] = arr[i].toString();
	}
	rs.add(newrow);
	return rs;
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

	rs.add(arr);
	return rs;
}

/**
 * Function adds new column each rowset row, new column value added to each rows or specified row.
 * @rowset {ArrayList} Existing rowset.
 * @rowid {Int} row number where to add new column value. If null then all rows will 
 * @column {String?} Comma separeated list that converted to Array.
 */
function xdtlRowsetAddColumn(rowset, rowid, column) {
	for (var i = 0; i < rowset.size(); i++) {
		var row = rowset.get(i);
     	var newrow = new java.util.Arrays.copyOf(row, row.length + 1);
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

/**
 * Function adds quote qualifier to string
 * @s {String} input string
 * @c {String} optional quote character
 */

function xdtlValueToQuotedValue(s, c) {
	c = c || "'"; 
	var result = c + s + c;
	return result;
}


