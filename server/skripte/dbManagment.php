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
	// - newversion
	// - overrideversion
	
	
	//=====================================================================
	//============================Variablen================================
	//=====================================================================	
	
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
		loginteral("Nutzer " . $userid . " hat sich eingeloggt");
		
		//Befehl abrufen
		receiveCommand();
		//$basequery = buildQuery();
		loginteral("Auszuführender Befehl: " . $command);
		
		//Daten abrufen
		$dataarray = receiveData();
		
		//Daten verarbeiten
		work($dataarray);
				
		echo("CONFIRM_WORK_DONE");
		loginteral("CONFIRM_WORK_DONE");	
		
	
	//=====================================================================
	//===========================Funktionen================================
	//=====================================================================
		
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
				echo 'ERROR_NO_USER';
				die;
			}
			
			if (isSet($_POST['pass'])) {
				$pass = $_POST['pass'];
			}
			else {
				echo 'ERROR_NO_PASS';
				die;
			}		

			// if (isSet($_POST['group'])) {
				// $group = $_POST['group'];
			// }
			// else {
				// echo 'ERROR_NO_GROUP';
				// die;
			// }
			
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
				echo('ERROR_INVALID_AUTH');
				loginteral("Gescheiterter Loginversuch für Account: " . $user);
				die;
			}	
			$id = $res["id"];			

			if ($res == false) {
				echo('ERROR_INVALID_AUTH');
				loginteral("Gescheiterter Loginversuch für Account: " . $user);
				die;
			}
						
			// Überprüfen ob der Nutzer die Gruppe bearbeiten darf
			if ($res["groups"] == "all") {
				//Nutzer darf alle Gruppen bearbeiten
				$group = "all";
				return $id;
			}
			
			$group_array = explode("_",$res["groups"]);
			// $groupInputArray = explode("_",$group);
			
			// foreach($groupInputArray as $element) {
				
				// if (in_array($element, $group_array)) {
					// //Alles OK
				// }
				// else {
					// echo("ERROR_MISSING_GROUP_PERMISSION");
					// loginteral("Fehlende Berechtigung Account: " . $user);
					// die;
				// }	
			// }	
						
			$group = translateGroup($group_array);
						
			return $id;
		}
		
		function work($data) {
			global $group;
			global $table;
			global $pdo;
			global $tablecols;	
			global $command;
			
			$res = 0;
			
			for ($x = 0; $x < count($data); $x++) {
				
				$current_id = $data[$x]['id'];
				
				//Prüfen ob nach der Änderung der Eintrag noch in einer authorisierten Gruppe liegt
				if (!checkGroupPermissionOnItem($data[$x]['groupId'])) {
					$res = -1;
				}
				else {
					if ($command == "insert") {
						
						if (!checkIfEntryExists($current_id, $pdo, $table)) {
							//Wenn der Eintrag noch nicht exisitert, muss er mittels insert eingespielt werden
							
							//TODO: Überprüfen, ob der Nutzer neue Einträge anlegen darf.
													
							$querystring = "INSERT INTO " . '`' . $table . '`' . " (";
							$valuestring = "";
							
							for ($i= 0; $i < count($tablecols); $i++) {
								$element = $data[$x];
								$currentcol = $tablecols[$i];

								if ($currentcol == "groupId" && !is_numeric($element[$currentcol])) {
									//Vorliegende alphabetische Gruppenid in eine numerische Gruppenid umwandeln
									$group_array = getGroupArray();
									$element[$currentcol] = translateGroupNameToId($group_array, $element[$currentcol]);
								}
								
								$querystring = $querystring . "" . $currentcol . ", ";
								$valuestring = $valuestring . "'" . $element[$currentcol] . "',";
							}
																		
							$querystring = rtrim($querystring,", ");
							$valuestring = rtrim($valuestring,",");
							
							$querystring = $querystring . ") VALUES (" . $valuestring . ")";										
							
						}
						else {
							checkGroupPermission($current_id);
							
							//Wenn der Eintrag bereits exisitert, muss er mittels update eingespielt werden
							$querystring = "UPDATE " . '`' . $table . '`' . " SET ";
								
							for ($i= 1; $i < count($tablecols); $i++) {
								$element = $data[$x];
								$currentcol = $tablecols[$i];

								if ($currentcol == "groupId" && !is_numeric($element[$currentcol])) {
									//Vorliegende alphabetische Gruppenid in eine numerische Gruppenid umwandeln
									$group_array = getGroupArray();
									$element[$currentcol] = translateGroupNameToId($group_array, $element[$currentcol]);
								}
								
								$querystring = $querystring . "" . $currentcol . " = '" . $element[$currentcol] . "', ";
							}
							
							$querystring = rtrim($querystring,", ");	
							$querystring = $querystring . " WHERE " . $tablecols[0] . " = '" . $data[$x][$tablecols[0]] . "'";							
						}					
					}
				}
				
				// var_dump($querystring);
				
				if ($res >= 0) {
					// Datenbankabfrage durchführen
					$stmt=$pdo->prepare($querystring);				
					$stmt->closeCursor();	
					$stmt->execute();	
					
					$res = $stmt->rowCount();
					// var_dump($res);
				}
								
				if ($res == 0) {
					echo("ID " . $data[$x][$tablecols[0]] . " - SQL_ERROR \r\n");
				}
				else if ($res == -1) {
					echo("ID " . $data[$x][$tablecols[0]] . " - MISSING_GROUP_PERMISSION \r\n");
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
				echo 'ERROR_NO_DATA';
				die;
			}
			
			$rawdata = json_decode($data,true);
						
			if (!array_key_exists("INPUT",$rawdata)) {
				echo "ERROR_DATA_FORMAT_INVALID";
				die;
			}
			
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
				echo 'ERROR_NO_COMMAND';
				die;
			}
			
			if (isSet($_POST['table'])) {
				$table = $_POST['table'];
			}
			else {
				echo 'ERROR_NO_TABLE';
				die;
			}							
						
			$table = translateTable($table);
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
			// echo "query:";
			// var_dump($query);
			// echo "\r\n";
			
			// echo "res:";
			// var_dump($res);
			// echo "\r\n";
			
			// echo "group:";
			// var_dump($group);
			// echo "\r\n";
			
			if ($group == "all") {
				return true;
			}
			
			foreach($group as $element) {
				if ($element == $res["groupId"]) {
					return true;
				}
			}
			
			echo("ERROR_MISSING_GROUP_PERMISSION");
			die;
		}
		
		//Überprüft anhand der groupId eines einzutragenden Datensatzes ob die Zielgruppe durch den Benutzer geändert werden darf.
		function checkGroupPermissionOnItem($groupId) {		
			global $group;
			
			if ($group == "all") {
				return true;
			}
			
			if (!is_numeric($groupId)) {
				//Vorliegende alphabetische Gruppenid in eine numerische Gruppenid umwandeln
				$group_array = getGroupArray();
				$groupId = translateGroupNameToId($group_array, $groupId);
			}
			if (in_array($groupId,$group)) {
				return true;
			}
			else {
				return false;
			}
		}
		
		function checkTablePermission($table) {
			global $pdo;
			global $userid;
			
			//Query bauen um berechtigte Tabellen abzufragen
			$query = "SELECT `tables` FROM `userx` WHERE `id` LIKE " . $userid ;					
						
			$stmt=$pdo->prepare($query);			
			$stmt->closeCursor();	
			$stmt->execute();	
			
			$res = $stmt->fetch(PDO::FETCH_ASSOC);
			
			if ($res == null || $res == "" || count($res) == 0 || $res["tables"] == "") {
				return false;
			}
			
			if ($res["tables"] == "all") {
				return true;
			}
			
			//Berechtigte Tabellen splitten und auswerten
			$allowed_tables = array();
			$allowed_tables = explode("_", $res["tables"]);
						
			if (in_array($table, $allowed_tables)) {
				return true;
			}
			else {
				return false;
			}			
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
					$tablecols[0] = "id";
					$tablecols[1] = "groupId";
					$tablecols[2] = "name";
					$tablecols[3] = "description";
					$tablecols[4] = "descriptionTwo";
					$tablecols[5] = "positions";
					$tablecols[6] = "version";
					//TODO: Andere Spalten einbauen
					break;
					
				case "positionimage":
					$dbtable = "positionimage";
					$tablecols[0] = "id";
					$tablecols[1] = "path";
					$tablecols[2] = "categoryId";
					$tablecols[3] = "groupId";
					$tablecols[4] = "version";
					//TODO: Andere Spalten einbauen
					break;

				case "group":
					$dbtable = "groupx";
					$tablecols[0] = "id";
					$tablecols[1] = "name";
					$tablecols[2] = "trayname";
					//TODO: Andere Spalten einbauen
					break;
					
				case "user":
					$dbtable = "userx";
					$tablecols[0] = "id";
					$tablecols[1] = "name";
					$tablecols[2] = "groups";
					$tablecols[3] = "pass";
					//TODO: Überprüfen ob Nutzer Berechtigung zum Ändern der Benutzer besitzt
					break;				
					
				default:
					echo("ERROR_INVALID_TABLE");
					die;
				
			}
			
			if (!checkTablePermission($dbtable)) {
				echo("ERROR_MISSING_TABLE_PERMISSION");
				die;
			}
			
			return $dbtable;
		}	

		function setversion() {
			
			if (isSet($_POST['newversion'])) {
				$newversion = $_POST['newversion'];
			}
			else {
				echo 'ERROR_NO_VERSION';
				die;
			}
			
			$dbFile = fopen("db_Version.txt",'r');
			$dbVersion = fgets($dbFile);
			fclose($dbFile);
			
			// echo($dbVersion);
			// echo("-");
			// echo($newversion);
				
			loginteral("Eingegebene Version: " . $newversion . ", aktuelle Serverversion: " . $dbVersion);
			
			if ($dbVersion < $newversion) {
				$dbFile = fopen("db_Version.txt",'w');
				fwrite($dbFile, $newversion);
				fclose($dbFile);		
				echo("CONFIRM_VERSION_UPDATE");
				loginteral("Neue Version eingetragen: " . $newversion);	
			}	
			else if ($dbVersion == $newversion) {
				echo("WARNING_SAME_VERSION");
				loginteral("Keine Versionsänderung möglich.");
			}
			else {
				if (isSet($_POST['overrideversion'])) {
					$overrideversion = $_POST['overrideversion'];
					if ($overrideversion == 1) {
						$dbFile = fopen("db_Version.txt",'w');
						fwrite($dbFile, $newversion);
						fclose($dbFile);		
						echo("CONFIRM_VERSION_UPDATE_WITH_OVERRIDE");
						loginteral("Version zwangsweise überschrieben. Neue Version: " . $newversion);
					}
				}
				else {
					echo("ERROR_OUTDATED_VERSION");
					loginteral("Versionsupdate abgewiesen");
					die;
				}			
			}
					
		}
	
		function loginteral($message) {
			$dbFile = fopen("config/log_managment.txt",'a');
			
			fwrite($dbFile,"Timestamp: ");
			fwrite($dbFile,date("Y-m-d H:i:s"));
			fwrite($dbFile,"; Message: ");
			fwrite($dbFile,$message);
			fwrite($dbFile,"\r\n");
			
			fclose($dbFile);
		}
?>