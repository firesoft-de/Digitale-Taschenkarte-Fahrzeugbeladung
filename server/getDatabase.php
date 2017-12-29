<?php	
	//Datenbankversion abfragen
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	//.../getDatabase.php?dbVersion=1
	//Mitgegebene Paramter abrufen
	$clienDbVersion = $_GET['dbVersion'];;
	
	//Zugangsdaten abrufen
	$dbFile = fopen("access.txt",'r');
	
	$db_user = fgets($dbFile);
	$db_password = fgets($dbFile);
	$db_server = "rdbms.strato.de";
	
	fclose($dbFile);
	
	//TODO: Anpassen!
	$pdo = new PDO('mysql:host=".db_server.";dbname=ditaka', $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM equitment WHERE version > ".$clientdbVersion;
	
	//Ausgabe per print
	// foreach ($pdo->query($queryString as $row) {
		// //Ausgabe vorerst per print
		// print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#";
	// }
	
	//Ausgabe per JSON
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json
	$statement=$pdo->prepare($queryString);
	$statement->execute();
	$results=$statement->fetchAll(PDO::FETCH_ASSOC);
	$json=json_encode($results);
	
	print($json);
?>