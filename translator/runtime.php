<?php

function getSubsetRows($recordset, $fields) {
}

function toString($recordsetObject, $numRows, $columnDelimeter,
				  $RowDelimeter, $nullExpr) {
  $count = 0;
  $result = "";
  while(!$recordsetObject.EOF && ($count < $numRows || $numRows == -1)) {
	for ($i = 0; $i < $recordsetObject.FieldCount(); $i++) {
	  $val = $recordsetObject.fields[i];
	  if ($val == NULL) {
		$val = $nullExpr;
	  }
	  $result .= $i == 0 ? "" : $columnDelimeter;
	  $result .= $val;
	}
	$result .= $RowDelimeter;
	$recordsetObject.MoveNext();
	$count++;
  }
  return $result;
}

/*
 *Remove an entry from the stored values based on index
 *Remove an entry from the stored values based on it's key
*/
function removeApplicationContent($index) {
}


/*
 *Remove all variables
 */
function clearApplicationContents() {
}


/*
 *Fetch a value based on it's index or key
 */
function getApplicationItem($index) {
}


function setApplicationItem($key, $value) {
}

/*
Functions needed for emulation of vb built-in functions
*/
function dateAdd($part, $add, $value) {
}

function dateDiff($part, $date1, $date2) {
}

/*
 *Like DatePart in VB
 */
function datePart($part, $date) {
}

/*
 *like DateValue
 */
function dateValue($intDate) {
}

function Day($time) {
}

function filter($str1, $str2, $include=true, $binCompare=1) {
}

function formatDateTime() {
}

function getTime() {
}


/*
functions needed for the Request ActiveX
*/
function getSubCookie($key, $subKey) {
}

function getSubForm($key, $subKey) {
}

function getSubQueryString($key, $subKey) {
}


/*
Needed to emulate the redim statement
*/
function vb_redim($array, $dimensions, $preserve) {
}
?>
