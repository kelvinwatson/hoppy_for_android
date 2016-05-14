<?php
include 'config.php';
include 'debug.php';

function connectToDatabase(){
  global $host, $dbUser, $dbPass, $dbName, $dbPort;
  
  //Debug::consoleLog('Connecting to database...');
  
  /* CONNECT TO DATABASE */
  $mySQLi = new mysqli($host, $dbUser, $dbPass, $dbName, $dbPort);
  if ($mySQLi->connect_errno) {
    echo "Failed to connect to MySQL: (" . $mySQLi->connect_errno . ") " . $mySQLi->connect_error;
    return null;
  } 
  //Debug::consoleLog('Woot, connected!');
  return $mySQLi;
}

function getBreweryNamesAsSelectOptions($mySQLi, $selectedId){
  if (!($stmt = $mySQLi->prepare("SELECT brs.id, brs.name FROM breweries brs ORDER BY brs.name ASC"))) {
  echo "Prepare failed: (" . $mySQLi->errno . ") " . $mySQLi->error;
}

  if (!$stmt->execute()) {
    echo "Execute failed: (" . $stmt->errno . ") " . $stmt->error;
  }

  if(!$stmt->bind_result($breweryId,$breweryName)){
    echo "Bind failed: "  . $mySQLi->connect_errno . " " . $mySQLi->connect_error;
  }

  while($row = $stmt->fetch()){
    if($selectedId != null && $selectedId == $breweryId){
      echo "<option value=\"$breweryId\" selected>$breweryName (id=$breweryId)</option>";
    } else {
      echo "<option value=\"$breweryId\">$breweryName (id=$breweryId)</option>";
    }
  }
}

function setUTF8($mySQLi){
  if (!$mySQLi->set_charset("utf8")) {
    $obj = new stdClass(); //for JSON output
    $obj->http_response_code = 400;
    $obj->error_description = 'Error loading character set utf8.';
    echo json_encode($obj);		
    return;
  } else {
    return $mySQLi;
  }
}

function getFeaturedBeers($mySQLiFeatures, $desiredEvtId){
  if(!($stmtFeatures = $mySQLiFeatures->prepare(
    "SELECT f.beer_id, f.event_id FROM features f 
    WHERE f.event_id = ? 
    ORDER BY f.beer_id ASC"))){
    echo "Prepare failed: (" . $mySQLiFeatures->errno . ") " . $mySQLiFeatures->error;
  }
  if(!$stmtFeatures->bind_param("i", $desiredEvtId)) {
    echo "Binding parameters failed: (" . $stmt->errno . ") " . $stmt->error;
  }
  if(!$stmtFeatures->execute()) {
    echo "Execute failed: (" . $stmtFeatures->errno . ") " . $stmtFeatures->error;
  }
  if(!$stmtFeatures->bind_result($beerId,$eventId)){
    echo "Bind failed: "  . $mySQLiFeatures->connect_errno . " " . $mySQLiFeatures->connect_error;
  }
  $arr = array();
  $i=0;
  while ($stmtFeatures->fetch()){
    $arr[$i++] = $beerId; 
  }
  return $arr;
}


function getAllBeerIds($mySQLiForBeerIds){
  if (!($stmtBeers = $mySQLiForBeerIds->prepare(
    "SELECT beers.id FROM beers ORDER BY beers.name ASC"))){
    echo "Prepare failed: (" . $mySQLiForBeerIds->errno . ") " . $mySQLiForBeerIds->error;
  }

  if (!$stmtBeers->execute()) {
    echo "Execute failed: (" . $stmtBeers->errno . ") " . $stmtBeers->error;
  }
  if(!$stmtBeers->bind_result($beerId)){
    echo "Bind failed: "  . $mySQLiForBeerIds->connect_errno . " " . $mySQLiForBeerIds->connect_error;
  }
  $arr = array();
  $i=0;
  while ($stmtBeers->fetch()){
    $arr[$i++] = $beerId; 
  }
  return $arr;
}

?>