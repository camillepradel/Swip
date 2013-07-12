<?php
include "codesBD.php";

// connexion au serveur mysql
mysql_connect(SERVER, LOGIN, PASSWORD) or die ('Error in connection to mysql : '.mysql_error);

// sélection de la base de données utilisée
mysql_select_db(BASE) or die('Unable to select data base ' . BASE);

// requête SQL listant les préfixes
$sqlQuery = "select * from prefixes order by prefix";

// exécution de la requête SQL
$req = mysql_query($sqlQuery) or die('SQL error !<br />'.$sqlQuery.'<br />'.mysql_error());


$list = array();

while ($data = mysql_fetch_assoc($req)) // pour chaque entrée trouvée
{
	$list[$data['URI']] = $data['prefix'];
}

mysql_close();// fermeture de la connexion

echo json_encode($list);// traduction en JSON de la liste des préfixes
?>