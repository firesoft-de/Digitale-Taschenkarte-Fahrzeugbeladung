<?php
	
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
	
		
	function translateGroup($group) {
		
		global $pdo;
		
		$group = explode("_",$group);
		$group_array = getGroupArray();
		$query = "";
		
		$idarray = array();
		
		foreach($group as $element) {			
			$idarray[] = translateGroupNameToId($group_array,$element);
		}
				
		return $idarray;		
	}		

?>