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
$mySQLiBeers = connectToDatabase();
$mySQLiEvents = connectToDatabase();
$mySQLiFeatures = connectToDatabase();
?>

<!DOCTYPE html>
<html lang="en">

<head> 
  <script src="js/toast.js"></script>
  <script src="js/associate.js"></script>
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
  
  <?php
  if (!($stmtEvents = $mySQLiEvents->prepare(
    "SELECT e.id, e.name, e.event_date, e.logo_url, e.end_date FROM events e ORDER BY e.event_date DESC"))) {
    echo "Prepare failed: (" . $mySQLiEvents->errno . ") " . $mySQLiEvents->error;
  }

  if (!$stmtEvents->execute()) {
    echo "Execute failed: (" . $stmtEvents->errno . ") " . $stmtEvents->error;
  }

  if(!$stmtEvents->bind_result($eventId,$eventName,$eventDate,$eventLogoURL,$eventEndDate)){
    echo "Bind failed: "  . $mySQLiEvents->connect_errno . " " . $mySQLiEvents->connect_error;
  }
  
  while($row = $stmtEvents->fetch()){ ?>
    <div class="row"> <!--START VIEW TABLE ROW -->
      <h3>Associate Beers to <?php echo "$eventName $eventDate (event id=$eventId)" ?></h3>
      <div>
        <table class=" table table-responsive" style="font-size:0.8em; table-layout: fixed; word-wrap: break-word;">
          <thead>
            <tr>
              <th>Associate</th>
              <th>Beer ID</th>
              <th>Beer Name</th>
              <th>Brewery ID</th>
              <th>Brewery Name</th>
            </tr>
          </thead>
          <tbody>
            <form id="associate-beer-form<?php echo $eventId?>" name="associate-beer-form">
            <?php
            /*Get this event's featured beers in array*/
            $beersFeaturedInThisEvent = getFeaturedBeers($mySQLiFeatures, $eventId);
            //$allBeerIds = getAllBeerIds($mySQLiForBeerIds);
            
            if (!($stmtBeers = $mySQLiBeers->prepare(
              "SELECT beers.id, beers.name, beers.brewery_id, breweries.name
              FROM beers INNER JOIN breweries ON beers.brewery_id = breweries.id 
              ORDER BY beers.name ASC"))){
              echo "Prepare failed: (" . $mySQLiBeers->errno . ") " . $mySQLiBeers->error;
            }

            if (!$stmtBeers->execute()) {
              echo "Execute failed: (" . $stmtBeers->errno . ") " . $stmtBeers->error;
            }
            if(!$stmtBeers->bind_result($beerId,$beerName,$breweryId,$breweryName)){
              echo "Bind failed: "  . $mySQLiBeers->connect_errno . " " . $mySQLiBeers->connect_error;
            }
            while($row = $stmtBeers->fetch()){ ?>
              <tr>
                <td>
                  <?php 
                  if(in_array($beerId,$beersFeaturedInThisEvent)){ ?>
                    <input class="beer-checkbox-for-event-<?php echo $eventId ?>" type="checkbox" name="beer-id" value="<?php echo $beerId ?>" checked></td>
                  <?php } else { ?>
                    <input class="beer-checkbox-for-event-<?php echo $eventId ?>" type="checkbox" name="beer-id" value="<?php echo $beerId ?>"></td>                        
                  <?php } ?>
                  <td><?php echo $beerId ?></td>
                  <td><?php echo $beerName?></td>
                  <td><?php echo $breweryId ?></td>
                  <td><?php echo $breweryName; ?></select></td>
                </tr>
            <?php  
            } //END WHILE LOOP 
            $stmtBeers->close();
            ?>
            <tr>
              <td style="width:10%"><input onclick="associateBeers(<?php echo $eventId ?>);return false;" class="btn btn-success" type="submit" value="associate selected beers"></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
            </form>
          </tbody>
        </table>
      </div>
    </div> <!--END VIEW TABLE ROW-->
    <hr>
  <?php
  }
  ?>
  
</div> <!-- END CONTAINER -->

<!--SCRIPTS-->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script type="text/javascript" src="js/jQuery.js"></script>

</body>
</html>