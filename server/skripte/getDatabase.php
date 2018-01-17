<?php	

	/* 	Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0. */

	
	/* 
	MÖGLICHE PARAMETER
	dbversion = Die Datenbankversion des Clients (nicht erforderlich wenn loadFullGroup geliefert wird)
	dbtable = Die abzufragende Tabelle
	groups = Die Gruppen zu welchen die Daten abgefragt werden sollen. Als Muster wird "Gruppe1_Gruppe2_Gruppe3" verwendet
	loadfullfroup = Wenn alle verfügbaren Datenbankeinträge zu einer Gruppe heruntergeladen werden sollen, wird dieser Parameter mit 1 gefüttert
	fullgroups = Die Gruppen welche vollständig heruntergeladen werden sollen
	*/
	
	//Basierend auf
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json

	//Header setzen
	header('Content-Type: application/json');
	
	//Datenbankversion abfragen
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbversion = fgets($dbFile);
	
	fclose($dbFile);
	
	//Mitgegebene Paramter abrufen
	$table_input = $_GET['dbtable'];
	
	//Clientversion abfragen
	if (isset($_GET['dbversion'])) {
		$clientdbVersion = $_GET['dbversion'];}
	else {
		$clientdbVersion = "";
	}
	
	//Gruppe abfragen
	if (isset($_GET['groups'])) {
		$groups = $_GET['groups'];}
	else {
		$groups = "";
	}
	
	//Vollständig herunterzuladende Gruppen abfragen
	if (isset($_GET['fullgroups'])) {
		$fullgroups = $_GET['fullgroups'];}
	else {
		$fullgroups = "";
	}
	
	$fullgroup = "";
	
	//Downloadmodus abfragen
	if (isset($_GET['loadFullGroup'])) {
		$fullgroup = $_GET['loadFullGroup'];}
	
	//Datenbanktabelle festlegen (zum verhindern von SQL Injcetions findet hier eine Entkopplung der Eingabe und des in die SQL Query gegebenen Wertes statt
	switch ($table_input) {
		case "equipment":
			$dbtable = "equipment";
			break;
			
		case "tray":
			$dbtable = "tray";
			break;
			
		case "positionimage":
			$dbtable = "positionimage";
			break;

		case "group":
			$dbtable = "groupx";
			break;
			
		default:
			$dbtable = "";
		
	}
	
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
	
	//Zugangsobjekt erzeugen
	$pdo = new PDO('mysql:host=' . $db_server.';dbname=' . $db_name, $db_user , $db_password);
	
	//SQL Query zum Abfragen der Daten konstruieren
	$queryString;
	
	//Abfrage welche Tabelle abgefragt werden soll. Fall die Gruppen abgefragt werden, müssen auch alle nicht geänderte Gruppen angezeigt werden.
	//Ansonsten würde in der App zu wenig im Gruppendialog angezeigt werden.
	if ($dbtable == "groupx") {
		$queryString = "SELECT * FROM `" . $dbtable . "` WHERE version <= :dbversion";
	}
	else {
		//Es sollen nur die geänderten Einträge heruntergeladen werden
		$queryString = "SELECT * FROM `" . $dbtable . "` WHERE version > :clientdbversion AND version <= :dbversion";			
	}
	
	//Falls Gruppen vorhanden sind diese anhängen
	if ($groups != null && $groups != ""){	
		$queryString = $queryString . " AND (" . builtGroupQueryByName($groups);
	}
	
	//Debug Ausgabe		
	//print($queryString);
	
	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);
		
	$stmt->bindParam(':dbversion', $dbversion, PDO::PARAM_INT);
			
	if ($dbtable != "groupx" && $fullgroup != 1) {
		$stmt->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
	}
	
	//Statment schließen
	$stmt->closeCursor();
	
	//SQL Abfrage ausführen
	$stmt->execute();	
	
	//DEBUG Ausgabe SQL Query
	//$stmt->debugDumpParams();
	
	$results = array(); 
	
	$group_array = getGroupArray();
	
	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		
		//Numerische Gruppenbezeichnung gegen alphabetische Gruppenbezeichnung austauschen
		if ($dbtable != "groupx"){			
			$row["groupId"] = translateGroupIdToName($group_array,$row["groupId"]);
		}
		//var_dump($row);
		
		//print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#");
		$results["OUTPUT"][] = $row;
 
	}
	
	
	
	
	//Falls ein vollständiger Download von Gruppen angefragt wurde, wird dieser nun bearbeitet
	
	if ($fullgroup == 1) {
		//Es sollen alle Datenbankeinträge heruntergeladen werden
		$queryString = "SELECT * FROM `" . $dbtable . "` WHERE version <= :dbversion";
	}	
	
	if ($fullgroup != null && $fullgroup != ""){	
		$queryString = $queryString . " AND (" . builtGroupQueryByName($fullgroup);
	}
	
	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);
		
	$stmt->bindParam(':dbversion', $dbversion, PDO::PARAM_INT);
			
	if ($dbtable != "groupx" && $fullgroup != 1) {
		$stmt->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
	}
	
	//Statment schließen
	$stmt->closeCursor();
	
	//SQL Abfrage ausführen
	$stmt->execute();	
	
	//DEBUG Ausgabe SQL Query
	//$stmt->debugDumpParams();
	
	$results = array(); 
	
	$group_array = getGroupArray();
	
	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		
		//Numerische Gruppenbezeichnung gegen alphabetische Gruppenbezeichnung austauschen
		if ($dbtable != "groupx"){			
			$row["groupId"] = translateGroupIdToName($group_array,$row["groupId"]);
		}
		//var_dump($row);
		
		//print($row['id'].";".$row['name'].";".$row['description'].";".$row['categoryId'].";".$row['setName'].";".$row['position'].";".$row['keywords']."#-#");
		$results["OUTPUT"][] = $row;
 
	}
	
	//Ausgabe als JSON codieren	
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
	
	//============================================================================
	//============================Funktionen======================================
	//============================================================================
	
	//Gruppenname in ID übersetzen
	function translateGroupNameToId($array, $name) {
		
		foreach ($array as $element) {
			if ($element['name'] == $name) {
				return $element['id'];
			}
		}
		
		//Gruppe ist nicht bekannt
		return -1;
	}
	
	//Gruppenid in Name übersetzen
	function translateGroupIdToName($array, $id) {
		
		foreach ($array as $element) {
			if ($element['id'] == $id) {
				return $element['name'];
			}
		}
		
		//Gruppe ist nicht bekannt
		return "-1";
	}
	
	//Erzeugt einen Anhang für die SQL Query mit der nach Gruppenitems gesucht werden kann
	function builtGroupQueryByName($names) {
		
		$name_array = explode("_", $names);
		$group_array = getGroupArray();
		$query = "";
		
		$id = translateGroupNameToId($group_array,$name_array[0]);
		
		if ($id != -1) {
			
			$query = "groupId = " . $id . " ";
			
			for ($x = 1; $x < count($name_array); $x++) {
				
				$id = translateGroupNameToId($group_array,$name_array[$x]);
		
				if ($id != -1) {
					
					$query = $query . " OR groupId = " . $id;
					
				}
				else {
					error_log("Es wurde eine fehlerhafte GruppenID übergeben!");
				}				
			}

			$query = $query . ")";
		}
		else {
			error_log("Es wurde eine fehlerhafte GruppenID übergeben!");
		}		
		return $query;
	}
	
	function getGroupArray() {
		
		global $dbtable;
		global $clientdbVersion;
		global $pdo;
	
		//SQL Query zum Abfragen der Daten konstruieren
		$queryString = "SELECT * FROM `groupx`";
		
		//Ausgabe per JSON
		$statment=$pdo->prepare($queryString);
			
		//Die Benutzereingaben sicher in den Querystring einfügen
		//$statment->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
		
		//Statment schließen
		$statment->closeCursor();
		
		//SQL Abfrage ausführen
		$statment->execute();	
		
		//DEBUG Ausgabe SQL Query
		//$statment->debugDumpParams();
		
		$results = array();
	
		while($res=$statment->fetch(PDO::FETCH_ASSOC)){
			
			$results[] = $res;
			//print($results[0]["name"] . "-");
			//print($res["name"] . "-");
	 
		}
		
		return $results;
	}
	
?>