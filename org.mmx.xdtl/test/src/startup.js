/*
 * Executed at engine startup. Any variables defined here will be globals. 
 */
 
var xdtlDateCode = java.lang.String.format("%1$tY%1$tm%1$td", new Array(new java.util.Date()));

function xdtlArray() {
  var a = new java.util.ArrayList(arguments.length);
  
  for (var i = 0; i < arguments.length; i++) {
    a.add(arguments[i]);
  }
  
  return a;
}
