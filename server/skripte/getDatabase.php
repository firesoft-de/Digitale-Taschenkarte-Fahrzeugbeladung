<?php	
	//Basierend auf
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json

	//Datenbankversion abfragen
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	//Mitgegebene Paramter abrufen
	$clientdbVersion = $_GET['dbVersion'];;
	
	$table_input = $_GET['db_table'];;

	//Datenbanktabelle festlegen (zum verhindern von SQL Injcetions findet hier eine Entkopplung der Eingabe und des in die SQL Query gegebenen Wertes statt
	switch ($table_input) {
		case "equipment":
			$db_table = "equipment";
			break;
			
		case "tray":
			$db_table = "tray";
			break;
			
		case "positionimage":
			$db_table = "positionimage";
			break;
			
		default:
			$db_table = "";
		
	}
	
	//Datenbankzugangsdaten
	$dbFile = fopen(__DIR__ .  "/config/access.txt",'r');
	
	$db_server = fgets($dbFile);	
	$db_name = fgets($dbFile);	
	$db_user = fgets($dbFile);
	$db_password = fgets($dbFile);
	
	fclose($dbFile);
	
	//$db_server = "rdbms.strato.de"; 
	$db_server = trim(preg_replace('/\s+/', ' ', $db_server));
	$db_name = trim(preg_replace('/\s+/', ' ', $db_name));
	$db_user = trim(preg_replace('/\s+/', ' ', $db_user));
	$db_password = trim(preg_replace('/\s+/', ' ', $db_password));
	
	$pdo = new PDO('mysql:host=' . $db_server.';dbname=' . $db_name, $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM " . $db_table . " WHERE version > :clientdbversion";
	
	//Ausgabe per JSON
	$stmt=$pdo->prepare($queryString);
		
	//Die Benutzereingaben sicher in den Querystring einfügen
	$stmt->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
	
	//Statment schließen
	$stmt->closeCursor();
	
	//SQL Abfrage ausführen
	$stmt->execute();	
	
	//DEBUG Ausgabe SQL Query
	//$stmt->debugDumpParams();
	
	$results = array();
	
	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		
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