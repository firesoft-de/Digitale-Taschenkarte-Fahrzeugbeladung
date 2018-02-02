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
	loadfullgroup = Wenn alle verfügbaren Datenbankeinträge zu einer Gruppe heruntergeladen werden sollen, wird dieser Parameter mit 1 gefüttert
	fullgroups = Die Gruppen welche vollständig heruntergeladen werden sollen
	*/
	
	//Basierend auf
	//https://stackoverflow.com/questions/2770273/pdostatement-to-json

	//=====================================================================
	//============================Variablen================================
	//=====================================================================	
	
		//Header setzen
		header('Content-Type: application/json');
		include 'util.php';
	
	//=====================================================================
	//========================Paramter abfragen============================
	//=====================================================================		
	
	
		//Datenbankversion abfragen
		$dbversion = getDBVersion();
		
		//Mitgegebene Paramter abrufen
		$table_input = $_GET['dbtable'];
		log_db("Abfrage für Tabelle " . $table_input);
		
		//Clientversion abfragen
		if (isset($_GET['dbversion'])) {
			$clientdbVersion = $_GET['dbversion'];}
		else {
			$clientdbVersion = "-1";
		}
		
		//Gruppe abfragen
		if (isset($_GET['groups'])) {
			$groups = $_GET['groups'];}
		else {
			$groups = "";
		}
		
		//Vollständig herunterzuladende Gruppen abfragen
		if (isset($_GET['fullgroups'])) {
			$fullgroups = $_GET['fullgroups'];
			log_db("Vollständige Gruppenabfrage");}
		else {
			$fullgroups = "";
		}
		
		$fullgroup = "";
		
		//Downloadmodus abfragen
		if (isset($_GET['loadfullgroup'])) {
			$fullgroup = $_GET['loadfullgroup'];}
		
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
				echo("ERROR_NO_TABLE");
				log_db("Keine passende Tabelle gefunden!");
				die;
			
		}
		
	//=====================================================================
	//============================Hauptteil================================
	//=====================================================================			
		
		//Datenbankzugangsdaten generieren
		//getDBAccess();
		
		$pdo = createDatabaseHandler();
				
		$results = array();
		$resultsX = basicDelivery($pdo, $dbtable, $groups, $dbversion, $clientdbVersion);
		
		$queryString = "";		
	
		$resultsY = fullDelivery($pdo, $dbtable, $fullgroup, $fullgroups, $dbversion);
		
		if (count($resultsX) > 0) {
			$results["OUTPUT"] = $resultsX;
		}
				
		if (count($resultsY) > 0) {
			$results["OUTPUT"] = $resultsY;
		}
		
		//Ausgabe als JSON codieren	
		$json = json_encode($results,JSON_PRETTY_PRINT);
			
		//DEBUG
		// var_dump($results);
		// var_dump($resultsX);
		// var_dump($resultsY);
		// var_dump($json);
		// debugJSON();
		
		print($json);	
	
			
	//=====================================================================
	//===========================Funktionen================================
	//=====================================================================	
	//=====================================================================	
	
		function basicDelivery($pdo, $dbtable, $groups, $dbversion, $clientdbVersion) {	
		
			//SQL Query zum Abfragen der Daten konstruieren
			$queryString = "";
			
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
			// print($queryString);
			
			//Datenbankabfrage vorbereiten
			$stmt=$pdo->prepare($queryString);
				
			$stmt->bindParam(':dbversion', $dbversion, PDO::PARAM_INT);
					
			if ($dbtable != "groupx") {
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
				
				$results[] = $row;				
		 
			}
			
			return $results;
		}
		
		function fullDelivery($pdo, $dbtable, $fullgroup, $fullgroups, $dbversion) {
			
			$queryString = "";
			
			if ($fullgroup == 1) {
				//Es sollen alle Datenbankeinträge heruntergeladen werden
				$queryString = "SELECT * FROM `" . $dbtable . "` WHERE version <= :dbversion";
			}	
			
			if ($fullgroups != null && $fullgroups != ""){				
				$queryString = $queryString . " AND (" . builtGroupQueryByName($fullgroups);
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
				$results[] = $row;
		 
			}
			
			return $results;
		}	
?>