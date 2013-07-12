<?php
function verifAcces($id, $mdp)
{ // TODO : vérifications avec base de données.
	if ($id == "swipAdmin" && $mdp == "swipAdministrator")
	{
		$acces = true;
	} else {
		$acces = false;
	}
	
	return $acces;
}
?>