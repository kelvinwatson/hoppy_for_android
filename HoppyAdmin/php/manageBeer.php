<?php 
session_start();
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
error_reporting(E_ALL);
ini_set('display_errors',1);
header('Content-Type: application/json');
include 'db.php';
//require 'password_compat/lib/password.php'; //for PHP<5.7

/* CONNECT TO DATABASE */
$mySQLi = connectToDatabase();
setUTF8($mySQLi);

$obj = new stdClass(); //for JSON output

/* RETRIEVE POST PARAMS */
$action = $_POST['user-action'];
$beerId = null;
$beerName = htmlspecialchars($_POST['beer-name']);
$beerType = htmlspecialchars($_POST['beer-type']);
$beerABV = (float)htmlspecialchars($_POST['beer-ABV']);
$beerIBU = (float)htmlspecialchars($_POST['beer-IBU']);
$beerDescription = htmlspecialchars($_POST['beer-description']);
$breweryId = htmlspecialchars($_POST['brewery-id']);

/*Debug only
$obj->httpResponseCode = 200;
$obj->response = "FAKEaddSuccesss";
$obj->errorMessage = "recvd: $action, $beerId, $beerName, $beerType, $beerABV, $beerIBU, $beerDescription, $breweryId";
echo json_encode($obj);
return;*/

if ($action == 'edit-beer'){
  $beerId = htmlspecialchars($_POST['beer-id']);
  if (!($stmt = $mySQLi->prepare("UPDATE beers SET name=?, type=?, ABV=?, IBU=?, description=?, brewery_id=? WHERE id=?"))) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  
  if (!$stmt->bind_param("ssddsii", $beerName, $beerType, $beerABV, $beerIBU, $beerDescription, $breweryId, $beerId)) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Bind failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Execute failed.';
    echo json_encode($obj);
    return;
  }
  $stmt->close();
  $obj->httpResponseCode = 200;
  $obj->response = "editSuccess";
  $obj->errorMessage = 'Edit beer successful.';
  echo json_encode($obj);
} else if ($action == "add-beer"){
    
  if (!($stmt = $mySQLi->prepare("INSERT INTO beers(name, type, ABV, IBU, description, brewery_id) VALUES (?,?,?,?,?,?)"))){
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->bind_param("ssddsi", $beerName, $beerType, $beerABV, $beerIBU, $beerDescription, $breweryId)) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Bind failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->execute()) {
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Execute failed.';
    echo json_encode($obj);
    return;
  }
  $stmt->close();
  $obj->httpResponseCode = 200;
  $obj->response = "addSuccesss";
  $obj->errorMessage = 'Add beer successful.';
  echo json_encode($obj);
}
return;
?>