<?php
	include 'util/sessions.php';
	
	initConnections();
?>

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
	<?php
		if (!$isConnected)
		{
			if ($formSubmitted) {
	?>
		<p>
			L'identifiant ou le mot de passe est incorrect.<br />
			<a href = "../patternViewer.html">
				Retour à la page de connexion.
			</a>
		</p>
	<?php
			} else {
	?>
		<p>
			Pour accéder à cette page, vous devez vous connecter en cliquant
			<a href = "../patternViewer.html">
				ici.
			</a>
		</p>
	<?php
			}
		} else {
	?>
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
							<option value = "http://swip.univ-tlse2.fr/musicbrainz/patterns_copy">
								http://swip.univ-tlse2.fr/musicbrainz/patterns_copy
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
					<img src = "../img/patterns-logo.svg" />
				</a>
				
				<a href = "./cacheManagement.html" title = "Cache management">
					<img src = "../img/patterns-logo.svg" />
				</a>
				
				<a href = "./typeProperties.html" title = "*TypeProperties management">
					<img src = "../img/patterns-logo.svg" />
				</a>
			</div>
		
			<div class = "item" id = "connection">
				<img class = "showHideButton" src = "../img/unlock_padlock.png" />
				<form class = "panel" action = "../patternViewer.html" method = "post">
					<p>
						<?php echo $_SESSION['id']; ?>
					</p>
					<input type = "submit" id = "connectButton" value = "Log out" />
				</form>
			</div>
		</div>
		
		<div id = "content">
			<button id = "toggleMoving">Stop moving</button>
			<div id = "layout">
			</div>
			<h1>Update Patterns</h1>
			<div id = "updatePatterns">
				<form>
					<input type = "file" id = "inputFile" name = "inputFile" />
					<input type = "button" id = "selectFileButton" value = "Load file" />
					<br />
					<textarea id = "patternText"></textarea>
					<br />
					
					<h2>Translating Options</h2>
					<p id = "translatingOptionsPart">
						<label for = "patternName">Pattern set name:</label>
						<input type = "text" id = "patternName" value = "musicbrainz"/>
						<br />
						<label for = "ontologyUri">Targeted ontology URI:</label>
						<input type = "text" id = "ontologyUri" value = "http://purl.org/ontology/mo/"/>
						<br />
						<label for = "authorUri">Author URI:</label>
						<input type = "text" id = "authorUri" value = "http://camillepradel.com/uris#me"/>
					</p>
					
					<input type = "button" id = "upload" value = "Upload patterns" />
				</form>
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
	<?php
		}
	?>
	</body>
</html>