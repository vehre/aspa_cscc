<?
class ADOCommand {
  var $connection;
  var $commandText;
  var $name;
  var $parameters;
  

  function setConnection(&$con) {
	$this->connection = $con;
  }


  function getConnection() {
	return $this->connection;
  }


  function setCommandText($txt) {
	$this->commandText = $txt;
  }


  function getCommandText() {
	return $this->commandText;
  }


  function setName($cmdName) {
	$this->name = $cmdName;
  }


  function getName() {
	return $this->name;
  }


  function createParameter($pName, $pType, $pVal) {
	return array("name"=>$pName, "type"=>$pType, "value"=>$pVal);
  }


  function Execute() {
	//Build the sql from the CommandText and Parameters
  }
}
?>