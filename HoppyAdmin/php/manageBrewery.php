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
$breweryId=null;
$breweryName = htmlspecialchars($_POST['brewery-name']);
$breweryDescription = htmlspecialchars($_POST['brewery-description']);
$breweryLogoURL = htmlspecialchars($_POST['brewery-logoURL']);

if ($action == 'edit-brewery'){
  $breweryId = htmlspecialchars($_POST['brewery-id']);
  if (!($stmt = $mySQLi->prepare("UPDATE breweries SET name=?, description=?, logo_url=? WHERE id=?"))) {
    $obj->httpResponseCode = 400;
    $obj->response = "editFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->bind_param("sssi", $breweryName, $breweryDescription, $breweryLogoURL, $breweryId)) {
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
  $obj->errorMessage = 'Edit brewery successful.';
  echo json_encode($obj);
} else if ($action == "add-brewery"){
    
  if (!($stmt = $mySQLi->prepare("INSERT INTO breweries(name, description, logo_url) VALUES (?,?,?)"))){
    $obj->httpResponseCode = 400;
    $obj->response = "addFailure";
    $obj->errorMessage = 'Prepare failed.';
    echo json_encode($obj);
    return;
  }
  if (!$stmt->bind_param("sss", $breweryName, $breweryDescription, $breweryLogoURL)) {
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
  $obj->errorMessage = 'Add brewery successful.';
  echo json_encode($obj);
}
return;
?>