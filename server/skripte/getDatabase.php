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
	 // foreach ($pdo->query($queryString) as $row) {
		 // //Ausgabe vorerst per print
		 // print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#");
	 // }
	
	//Ausgabe per JSON
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json
	$statement=$pdo->prepare($queryString);
	$statement->execute();	
	$results = array();
	
	while($row=$statement->fetch(PDO::FETCH_ASSOC)){
		
		//print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#");
		$results["OUTPUT"][] = $row;
 
	}
	
	$json = json_encode($results,JSON_PRETTY_PRINT);
	
	//DEBUG
	//print_r($results);
		
	// switch (json_last_error()) {
        // case JSON_ERROR_NONE:
            // echo ' - No errors';
        // break;
        // case JSON_ERROR_DEPTH:
            // echo ' - Maximum stack depth exceeded';
        // break;
        // case JSON_ERROR_STATE_MISMATCH:
            // echo ' - Underflow or the modes mismatch';
        // break;
        // case JSON_ERROR_CTRL_CHAR:
            // echo ' - Unexpected control character found';
        // break;
        // case JSON_ERROR_SYNTAX:
            // echo ' - Syntax error, malformed JSON';
        // break;
        // case JSON_ERROR_UTF8:
            // echo ' - Malformed UTF-8 characters, possibly incorrectly encoded';
        // break;
        // default:
            // echo ' - Unknown error';
        // break;
    // }
	
	
	print($json);
?>