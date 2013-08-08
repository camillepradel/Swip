<?php
// Initialisation des variables de sessions.
session_start();

// Import des fonctions permettant de gerer la base de donnée relationnelle
include "dataBases.php";

/******************************************
 * Connexions aux pages d'administration. *
 ******************************************/
function initConnections() {
	/*
	 * Définition de la variable globale $isConnected permettant de vérifier si
	 * l'utilisateur est connecté ou non, et la variable globale $formSubmitted
	 * permettant de savoir si l'utilisateur est arrivé sur cette page après
	 * avoir soumit un formulaire de connexion ou non
	 */
	global $isConnected, $formSubmitted;
	
	$formSubmitted = false;
	
	// Si l'utilisateur arrive sur la page en ayant rempli un formulaire de connexion.
	if (isset($_POST['id']) && isset($_POST['mdp'])) {
		$formSubmitted = true;
		
		// Vérification de la validité de l'identifiant et du mot de passe.
		if (verifAcces($_POST['id'], $_POST['mdp'])) {
			$isConnected = true;
			$_SESSION['id'] = $_POST['id'];
			$_SESSION['mdp'] = $_POST['mdp'];
		} else {
			$isConnected = false;
		}
	} elseif (isset($_SESSION['id']) && isset($_SESSION['mdp'])) { // sinon s'il s'est déjà connecté
		if (verifAcces($_SESSION['id'], $_SESSION['mdp'])) { // et que l'identifiant et le mot de passe restent bons
			$isConnected = true;
		} else {
			$isConnected = false;
		}
	} else {
		$isConnected = false;
	}
}

/*
 * vérifie si $id et $mdp correspondent bien à l'identifiant et le mot de passe
 * d'un administrateur (enregistré dans la base de données de swip).
 */
function verifAcces($id, $mdp) {
	/*
	 * Définition des variables globales $idIsValid et $mdpIsValid
	 * permettant de vérifier que l'identifiant et le mot de passe entrés
	 * sont corrects.
	 */
	global $idIsValid, $mdpIsValid;
	$idIsValid = $mdpIsValid = false;
	
	global $currentID, $currentMdp;
	$currentID = $id;
	$currentMdp = $mdp;
	
	// connexion à la base de données de swip
	dbConnect();
	
	// liste de tous les administrateurs et comparaisons avec l'identifiant et le mot de passe donnés
	$sqlQuery = "select * from administrators;";
	
	processSqlQuery($sqlQuery, function ($resp) {
		global $idIsValid, $mdpIsValid, $currentID, $currentMdp;
		
		// pour chaque entrée trouvée
		while ($data = mysql_fetch_assoc($resp)) {
			if ($currentID == $data['id']) {
				$idIsValid = true;
				
				if ($currentMdp == $data['mdp'])
					$mdpIsValid = true;
			}
		}
	});
	
	return $idIsValid && $mdpIsValid;
}
?>