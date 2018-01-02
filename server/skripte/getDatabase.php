<?php	
	//Datenbankversion abfragen
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	//.../getDatabase.php?dbVersion=1
	//Mitgegebene Paramter abrufen
	$clientdbVersion = $_GET['dbVersion'];;
	
	$db_table = $_GET['db_table'];;
	
	//Zugangsdaten abrufen
	$dbFile = fopen("access.txt",'r');
	
	$db_user = fgets($dbFile);
	$db_password = fgets($dbFile);
	//$db_server = "rdbms.strato.de";
	$db_server = "localhost";
	$db_name = "taka";
		
	fclose($dbFile);
	
	$db_user = trim(preg_replace('/\s+/', ' ', $db_user));
	$db_password = trim(preg_replace('/\s+/', ' ', $db_password));
	
	
	//TODO: Anpassen!
	$pdo = new PDO("mysql:host=".$db_server.";dbname=" . $db_name, $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM " . $db_table . " WHERE version > ".$clientdbVersion;
	
	//Ausgabe per print
	// foreach ($pdo->query($queryString as $row) {
		// //Ausgabe vorerst per print
		// print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#";
	// }
	
	//Ausgabe per JSON
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json
	$statement=$pdo->prepare($queryString);
	$statement->execute();	
	$results = array();
	
	while($row=$statement->fetch(PDO::FETCH_ASSOC)){
	  
			$results["OUTPUT"][] = $row;
			// $json = json_encode($row,JSON_PRETTY_PRINT);
			// echo json_last_error();
			// echo $json;
	 
	}
	
	$json = json_encode($results,JSON_PRETTY_PRINT);
	
	print($json);
?>