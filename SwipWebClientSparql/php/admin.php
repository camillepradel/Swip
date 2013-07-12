<?php
session_start();
include "fonctions.php";

$erreur = false;

// entête html commune
$page = <<<EOF
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>SWIP - Administration</title>
		<link rel="stylesheet" href="../css/global.css" type="text/css">
		<link rel="stylesheet" href="../css/patternViewer.css" type="text/css">
		<link rel="stylesheet" href="../css/styleAdmin.css" type="text/css">
	</head>
	
	<body>

EOF;

// si on vient du formulaire de connexion
if (isset($_POST['id']) && isset($_POST['mdp'])) {
	if (verifAcces($_POST['id'], $_POST['mdp'])) {
		/* si l'identifiant et le mot de passe sont corrects, on défini
		les variables de session permettant d'accéder aux différentes pages d'administration. */
		$_SESSION['id'] = $_POST['id'];
		$_SESSION['mdp'] = $_POST['mdp'];
	} else {
		// sinon on affiche une erreur
		$erreur = true;
		$page .= <<<EOF
		<div id = "connexion">
			<p>
				L'identifiant ou le mot de passe est incorrect.<br />
				<a href = "../patternViewer.html">
					Retour à la page de connexion.
				</a>
			</p>
		</div>

EOF;
	}
}

if (isset($_SESSION['id']) && isset($_SESSION['mdp']) && verifAcces($_SESSION['id'], $_SESSION['mdp']))
{
	$id = $_SESSION['id'];
	// si on a les droits d'administration, alors on affiche la page suivante
	$page .= <<<EOF
		<div id = "leftTab">
			<div id = "controlPanel">
				<div class = "item" id = "configuration">
					<img class = "showHideButton" src="../img/configurations.png" />
					<form class = "panel">
						<label for = "sparqlendpoint">SPARQL endpoint: </label>
						<input type = "text" id="sparqlendpoint" value="http://swip.univ-tlse2.fr:8080/musicbrainz/" />
						<br />
						<label for = "namedgraph">Named Graph: </label>
						<select id = "namedgraph" name = "namedgraph">
							<option value = "http://swip.univ-tlse2.fr/musicbrainz/patterns">
								http://swip.univ-tlse2.fr/musicbrainz/patterns
							</option>
						</select>
						<br />
						<label for = "graphWidth">Graph width: </label>
						<input type = "text" id = "graphWidth" />
						<br />
						<label for = "graphHeight">Graph height: </label>
						<input type = "text" id = "graphHeight" />
						<br />
						<input type="button" id="load" value = "See patterns">
					</form>
				</div>
				<div class = "item" id = "prefixes">
					<img class = "showHideButton" src = "../img/prefixes.png" />
					<div class = "panel">
						<form>
							<fieldset>
								<legend>New prefix</legend>
								<label for = "newPrefix">Prefix name:</label>
								<input type = "text" id = "newPrefix" />
								<br />
								<label for = "newURI">Prefix URI:</label>
								<input type = "text" id = "newURI" />
								<img id = "addButton" src = "../img/addButton.png" />
							</fieldset>
						</form>
						<span class = 'listLabel'>No prefixes founded.</span>
					</div>
				</div>
			</div>
			
			<div id = "navigation">
				<a href = "../welcome.html" title = "Welcome">
					<img src = "../img/Home_Icon.svg" />
				</a>
				<a href = "../index.html">
					<img src="../img/muffin-favicon.ico" title = "SWIP querying interface" />
				</a>
				<img class = "active" src = "../img/patterns-logo.svg" />
				
				<a href = "./usefulMatchings.html" title = "Useful matchings">
					<img src = "./img/patterns-logo.svg" />
				</a>
				
				<a href = "./cacheManagement.html" title = "Cache management">
					<img src = "./img/patterns-logo.svg" />
				</a>
				
				<a href = "./typeProperties.html" title = "*TypeProperties management">
					<img src = "./img/patterns-logo.svg" />
				</a>
			</div>
		
			<div class = "item" id = "connection">
				<img class = "showHideButton" src = "../img/unlock_padlock.png" />
				<form class = "panel" action = "../patternViewer.html" method = "post">
					<p>
						$id
					</p>
					<input type = "submit" id = "connectButton" value = "Log out" />
				</form>
			</div>
		</div>
		
		<div id = "content">
			<button id = "toggleMoving">Stop moving</button>
			<div id = "layout">
			</div>
		</div>
		
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.touch-punch.min.js"></script>
		<script type="text/javascript" src="../js/jquery.jsPlumb-1.3.16-all-min.js "></script>
		<script type="text/javascript" src="../js/arbor.js"></script>
		<script type = "text/javascript" src = "../js/jquery-mousewheel.js"></script>
		<script type = "text/javascript" src = "../js/matchRecursive.js"></script>
		
		<script type = "text/javascript" src = "../js/semanticWebTools.js"></script>
		<script type = "text/javascript" src = "../js/global.js"></script>
		<script type = "text/javascript" src = "../js/patternViewer.js"></script>
		<script type = "text/javascript" src = "../js/admin.js"></script>
EOF;
} else if (!$erreur) {
	// sinon on demande de se connecter (à condition qu'on n'ait pas fait de demande de connexion)
	$page .= <<<EOF
		<div id = "connexion">
			<p>
				Pour accéder à cette page, vous devez vous connecter en cliquant
				<a href = "../patternViewer.html">
					ici.
				</a>
			</p>
		</div>

EOF;
}

$page .= <<<EOF
	</body>
</html>
EOF;

echo $page;
/*
extract css + js
lien elem - txt
liste ss pattern
inmport

icone patrons
placement des icones, */
?>