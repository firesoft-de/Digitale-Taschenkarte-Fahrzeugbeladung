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


	//=====================================================================
	//============================PARAMETER================================
	//=====================================================================	
	// - user
	// - pass
	// - command
	// - group
	// - table
	// - data
	
	
	//=====================================================================
	//============================Variablen================================
	//=====================================================================	
	
		// $db_server;	
		// $db_name;	
		// $db_user;
		// $db_password;
		$dbtable = '';
		
		$pdo;
		
		$command;
		$group;
		$table;
		$tablecols;
		
		include 'util.php';
	
	//=====================================================================
	//============================Hauptteil================================
	//=====================================================================			
			
		//PDO Objekt erstellen
		$pdo = createDatabaseHandler();
		
		//Mitgelieferte Autorisierungsdaten überprüfen
		$userid = checkUser();
		
		//Befehl abrufen
		receiveCommand();
		$basequery = buildQuery();
		
		//Daten abrufen
		$dataarray = receiveData();
		
		//Daten verarbeiten
		work($basequery,$dataarray);
		
	
	//=====================================================================
	//===========================Funktionen================================
	//=====================================================================
	
		function createDatabaseHandler() {
			//Datenbankzugangsdaten
			$dbFile = fopen(__DIR__ .  "/config/access.txt",'r');
			
			// global $db_server;
			// global $db_name;
			// global $db_user;
			// global $db_password;
			
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
			return $pdo;
		}
		
		//Nutzer Autorisierung in der Datenbank checken
		function  checkUser() {
			
			global $group;
			
			$user = "";
			$pass = "";
			
			global $pdo;
			
			if (isSet($_POST['user'])) {
				$user = $_POST['user'];
			}
			else {
				echo 'NO_USER';
				die;
			}
			
			if (isSet($_POST['pass'])) {
				$pass = $_POST['pass'];
			}
			else {
				echo 'NO_PASS';
				die;
			}		

			if (isSet($_POST['group'])) {
				$group = $_POST['group'];
			}
			else {
				echo 'NO_GROUP';
				die;
			}
			
			// Query erstellen
			$queryString = "SELECT id,groups FROM `userx` WHERE name LIKE :user AND pass LIKE :pass";	
			
			// Datenbankabfrage vorbereiten
			$stmt=$pdo->prepare($queryString);				
			$stmt->bindParam(':user', $user , PDO::PARAM_STR);
			$stmt->bindParam(':pass', $pass , PDO::PARAM_STR);				
			$stmt->closeCursor();	
			$stmt->execute();	
			
			$res = -1;
			$res = $stmt->fetch(PDO::FETCH_ASSOC);
								
			// Überprüfen ob der Nutzer gültig ist
			if ($res["id"] == -1) {
				echo('INVALID_AUTH');
				die;
			}	
			$id = $res["id"];			

			if ($res == false) {
				echo('INVALID_AUTH');
				die;
			}
						
			// Überprüfen ob der Nutzer die Gruppe bearbeiten darf
			$group_array = explode("_",$res["groups"]);
			
			$group = translateGroup($group);
			
			foreach($group as $element) {
				
				if (in_array($element, $group_array)) {
					//Alles OK
				}
				else {
					echo("MISSING_GROUP_PERMISSION");
					die;
				}	
			}	
			return $id;
		}
		
		function work($query, $data) {
			global $group;
			global $table;
			global $pdo;
			global $tablecols;	
			global $command;
			
			for ($x = 0; $x < count($data); $x++) {
				
				//Überprüfen ob der angefragte Datenbankeintrag geändert werden darf
				checkGroupPermission($data[$x]['id']);
				
				if ($command == "insert") {
					
					$querystring = $query . "(";
					foreach ($tablecols as $element) {
						//var_dump($element);
						$querystring = $querystring . "`" . $element . "`,";
					}
					
					$querystring = rtrim($querystring,",");
					
					$querystring = $querystring . ") VALUES (";
					
					foreach($data[$x] as $subelement) {
						$querystring = $querystring . "`" . $subelement . "`,";
					}
							
					$querystring = rtrim($querystring,",");		
					$querystring = $querystring . ")";	
					
					// Query erstellen
					//var_dump($querystring);
					
				}
				else if ($command == "update") {
					
					$querystring = $query . "SET ";
					
					for ($i= 1; $i < count($tablecols); $i++) {
						$element = $data[$x];
						$querystring = $querystring . "" . $tablecols[$i] . " = '" . $element[$tablecols[$i]] . "', ";
					}
					
					$querystring = rtrim($querystring,", ");	
					$querystring = $querystring . " WHERE " . $tablecols[0] . " = '" . $data[$x][$tablecols[0]] . "'";	
					
					//DEBUG
					// var_dump($querystring);
					
				}
				
				// Datenbankabfrage durchführen
				$stmt=$pdo->prepare($querystring);				
				$stmt->closeCursor();	
				$stmt->execute();	
				
				$res = $stmt->rowCount();
				// var_dump($res);
				
				if ($res == 0) {
					echo("ID " . $data[$x][$tablecols[0]] . " - SQL_ERROR \r\n");
				}
				else {
					echo("ID " . $data[$x][$tablecols[0]] . " - SUCCESS \r\n");
				}				
			}			
		}
		
		// Daten via POST beziehen
		function receiveData() {
			
			if (isSet($_POST['data'])) {
				$data = $_POST['data'];
			}
			else {
				echo 'NO_DATA';
				die;
			}
			
			$rawdata = json_decode($data,true);
			$rawdata = $rawdata["INPUT"];
						
			return $rawdata;
			
		}
		
		//Befehl identifizieren und bereitstellen
		function receiveCommand() {		
			global $command;
			global $table;
			
			if (isSet($_POST['command'])) {
				$command = $_POST['command'];
			}
			else {
				echo 'NO_COMMAND';
				die;
			}
			
			if (isSet($_POST['table'])) {
				$table = $_POST['table'];
			}
			else {
				echo 'NO_TABLE';
				die;
			}							
		}
		
		//Befehl in Query umwandeln
		function buildQuery() {
			global $command;
			global $group;
			global $table;
			
			switch ($command) {				
				case 'insert':
					$query = "INSERT INTO ";
					break;
					
				case 'delete':
				
					break;
					
				case 'update':
					$query = "UPDATE ";
					break;	
				
				default:
					echo('UNKNOWN_COMMAND');
					die;
			}
				
			// Tabelle in Query einbauen
			$table = translateTable($table);
				
			$query = $query . '`' . $table . '` ';

			return $query;
		}
		
		//Berechtigung zum Ändern des Eintrags prüfen
		function checkGroupPermission($entryid) {			
			global $group;
			global $table;
			global $pdo;
			
			//Query bauen
			$query = "SELECT `groupId` FROM `" . $table . "` WHERE `id` LIKE " . $entryid ;					
						
			$stmt=$pdo->prepare($query);			
			$stmt->closeCursor();	
			$stmt->execute();	
			
			$res = $stmt->fetch(PDO::FETCH_ASSOC);
			
			//DEBUG
			// var_dump($res);
			// var_dump($group);
			
			foreach($group as $element) {
				if ($element == $res["groupId"]) {
					return true;
				}
			}
			
			echo("MISSING_GROUP_PERMISSION");
			die;
		}
		
				
		function translateTable($input) {
			global $tablecols;
			$tablecols = array();			
			
			switch ($input) {
				case "equipment":
					$dbtable = "equipment";
					$tablecols[0] = "id";
					$tablecols[1] = "categoryId";
					$tablecols[2] = "positionID";
					$tablecols[3] = "groupId";
					$tablecols[4] = "name";
					$tablecols[5] = "setName";
					$tablecols[6] = "description";
					$tablecols[7] = "position";
					$tablecols[8] = "notes";
					$tablecols[9] = "keywords";
					$tablecols[10] = "version";
					break;
					
				case "tray":
					$dbtable = "tray";
					//TODO: Andere Spalten einbauen
					break;
					
				case "positionimage":
					$dbtable = "positionimage";
					break;

				case "group":
					$dbtable = "groupx";
					break;
					
				case "user":
					$dbtable = "userx";
					break;				
					
				default:
					echo("INVALID_TABLE");
					die;
				
			}
			return $dbtable;
		}		
?>