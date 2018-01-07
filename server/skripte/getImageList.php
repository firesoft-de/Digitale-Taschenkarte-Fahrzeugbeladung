<?php
	
	//Mitgegebene Paramter abrufen
	$clientdbVersion = $_GET['dbVersion'];;
	
	//Datenbankzugangsdaten
	$dbFile = fopen(__DIR__ .  "/config/access.txt",'r');
	
	$db_server = fgets($dbFile);
	$db_name = fgets($dbFile);	
	$db_user = fgets($dbFile);
	$db_password = fgets($dbFile);
	
	fclose($dbFile);
	
	$db_server = trim(preg_replace('/\s+/', ' ', $db_server));
	$db_name = trim(preg_replace('/\s+/', ' ', $db_name));
	$db_user = trim(preg_replace('/\s+/', ' ', $db_user));
	$db_password = trim(preg_replace('/\s+/', ' ', $db_password));
	
	$pdo = new PDO("mysql:host=".$db_server.";dbname=" . $db_name, $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM positionimage WHERE version > :clientdbversion";

	$stmt=$pdo->prepare($queryString);
	
	//Die Benutzereingaben sicher in den Querystring einfügen
	$stmt->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
	
	$stmt->execute();	
	$results = array();
	
	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		
		$results["OUTPUT"][] = $row;
 
	}
	$json = json_encode($results,JSON_PRETTY_PRINT);
	print($json);
	
?>