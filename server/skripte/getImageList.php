<?php
	
	//Mitgegebene Paramter abrufen
	$clientdbVersion = $_GET['dbVersion'];;

	$db_server = "localhost";
	$db_name = "taka";
	
	$db_user = "sqluser";
	$db_password = "hhdEqOACkFQbGEcs";
	
	$pdo = new PDO("mysql:host=".$db_server.";dbname=" . $db_name, $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM positionimage WHERE version > ".$clientdbVersion;

	$statement=$pdo->prepare($queryString);
	$statement->execute();	
	$results = array();
	
	while($row=$statement->fetch(PDO::FETCH_ASSOC)){
		
		$results["OUTPUT"][] = $row;
 
	}
	$json = json_encode($results,JSON_PRETTY_PRINT);
	print($json);
	
?>