<?php 
session_start();
error_reporting(E_ALL);
ini_set('display_errors',1);
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
header('Content-Type: text/html; charset=utf-8');
include 'php/db.php';
include 'php/htmlUtils.php';


/* Always redirect user to HTTPS 
if(empty($_SERVER["HTTPS"]) || $_SERVER["HTTPS"] !== "on"){
  header("Location: https://" . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"]);
  exit();
}*/

if(!isset($_SESSION['loggedIn'])){
  header('Location: index.php');
  return;
} else if (!$_SESSION['loggedIn']){
  header('Location: index.php');  
  return;
}

/* CONNECT TO DATABASE */
$mySQLi = connectToDatabase();
$mySQLi2 = connectToDatabase();

?>

<!DOCTYPE html>
<html lang="en">

<head> 
  <script src="js/toast.js"></script>
  <script src="js/beer.js"></script>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Hoppy Administrator Portal</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
  <link rel="stylesheet" href="css/style.css">
  <link rel="stylesheet" href="css/toast.css">
</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#"><img class="top-left-logo" src="images/bunny.png"> Hoppy Portal</a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle line-height-custom" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Manage <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="beers.php">Beers</a></li>
            <li><a href="breweries.php">Breweries</a></li>
            <li><a href="events.php">Events</a></li>
            <li><a href="users.php">Users</a></li>
            <li role="separator" class="divider"></li>
            <li class="dropdown-header">Associate</li>
            <li><a href="eventBeers.php">Beers at Events</a></li>
          </ul>
        </li>
      </ul>
      <ul class="nav navbar-form navbar-right">
        <li><span class="glyphicon glyphicon-asterisk" style="color:#7FFF00"></span><span style="color:white"> Logged In (<?php echo $_SESSION['username']; ?>)
        <a type="submit" class="btn btn-info" onclick="logout(); return false;" href="logout.php">Log out</a></li>
      </ul>
    </div> 
  </div>
</nav>

<div class="container">
  <div class="row">
    <h1>Administrator Portal</h1>
  </div>
  <div class="row"> <!--START VIEW TABLE ROW -->
    <h3>View/Edit Beers</h3>
    <div>
      <table class=" table table-responsive" style="font-size:0.8em; table-layout: fixed; word-wrap: break-word;">
        <thead>
          <tr>
            <th style="width:5%">Edit</th>
            <th style="width:5%">ID</th>
            <th style="width:10%">Beer Name</th>
            <th style="width:20%">Type</th>
            <th style="width:5%">ABV</th>
            <th style="width:5%">IBU</th>
            <th style="width:20%">Description</th>
            <th style="width:5%">Brewery ID</th>
            <th style="width:25%">Brewery Name</th>
          </tr>
        </thead>
        <tbody>
          <tr><form action="beers.php#add" method="post">
            <input type="hidden" name="user-action" value="add-beer">
            <td style="width:5%"><input class="btn btn-success" type="submit" value="add" onclick="manageBeer('add');return false;"></th>
            <td style="width:5%">ID</td>
            <td style="width:10%"><textarea id="beer-name" style="width:100%" placeholder="beer name"></textarea></td>
            <td style="width:20%"><textarea id="beer-type" style="width:100%" placeholder="beer type"></textarea></td>
            <td style="width:5%"><textarea id="beer-ABV" style="width:100%" placeholder="ABV"></textarea></td>
            <td style="width:5%"><textarea id="beer-IBU" style="width:100%" placeholder="IBU"></textarea></td>
            <td style="width:20%"><textarea id="beer-description" style="width:100%" placeholder="beer description"></textarea></td>
            <td style="width:5%">NA</td>
            <td style="width:25%"><select id="brewery-name" style="width:100%"><?php getBreweryNamesAsSelectOptions($mySQLi,null); ?></select></td>
          </tr>

        
          <?php
          if (!($stmt = $mySQLi->prepare(
            "SELECT beers.id, beers.name, beers.type, beers.ABV, beers.IBU, beers.description, beers.brewery_id, breweries.name
            FROM beers INNER JOIN breweries ON beers.brewery_id = breweries.id"))) {
            echo "Prepare failed: (" . $mySQLi->errno . ") " . $mySQLi->error;
          }

          if (!$stmt->execute()) {
            echo "Execute failed: (" . $stmt->errno . ") " . $stmt->error;
          }

          if(!$stmt->bind_result($beerId,$beerName,$beerType,$beerABV,$beerIBU,$beerDescription,$breweryId,$breweryName)){
            echo "Bind failed: "  . $mySQLi->connect_errno . " " . $mySQLi->connect_error;
          }

          $beerId = $beerName = $beerType = $beerABV = $beerIBU = $beerDescription = $breweryId = $breweryName = NULL;
          $i=0;
          
          while($row = $stmt->fetch()){ ?>
            <tr><form id="edit-beer-form<?php echo $beerId ?>" name="edit-beer-form">
            <td style="width:10%"><input onclick="manageBeer('edit',<?php echo$beerId ?>);return false;" class="btn btn-warning" type="submit" value="edit"></td>
            <td style="width:5%"><?php echo $beerId ?><input type="hidden" name="beer-id" value=<?php echo $beerId ?></td>
            <td style="width:10%"><textarea id="beer-name<?php echo $beerId ?>" style="width:100%"><?php echo $beerName?></textarea></td>
            <td style="width:20%"><textarea id="beer-type<?php echo $beerId ?>" style="width:100%"><?php echo $beerType?></textarea></td>
            <td style="width:5%"><textarea id="beer-ABV<?php echo $beerId ?>" type="number" style="width:100%"><?php $precision=2; echo number_format((float) $beerABV, $precision, '.', '');?></textarea></td>
            <td style="width:5%"><textarea id="beer-IBU<?php echo $beerId ?>" style="width:100%"><?php echo $beerIBU?></textarea></td>
            <td style="width:10%"><textarea id="beer-description<?php echo $beerId ?>"><?php echo $beerDescription?></textarea></td>
            <td style="width:5%"><?php echo $breweryId ?></td>
            <td style="width:30%"><select id="brewery-name-for<?php echo $beerId ?>" name="brewery-name<?php echo $beerId ?>" style="width:100%"><?php getBreweryNamesAsSelectOptions($mySQLi2, $breweryId); ?></select></td>
            <input type="hidden" name="user-action" value="edit-beer"></form></tr>
          <?php  
          } //END WHILE LOOP 
          $stmt->free_result();
          $stmt->close();
          ?>
        </tbody>
      </table>
    </div>
  </div> <!--END VIEW TABLE ROW-->
  
</div> <!-- END CONTAINER -->

<!--SCRIPTS-->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script type="text/javascript" src="js/jQuery.js"></script>

</body>
</html>