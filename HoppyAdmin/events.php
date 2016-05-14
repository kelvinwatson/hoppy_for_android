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
  <script src="js/event.js"></script>
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
    <h3>View/Edit Events</h3>
    <div>
      <table class=" table table-responsive" style="font-size:0.8em; table-layout: fixed; word-wrap: break-word;">
        <thead>
          <tr>
            <th style="">Edit</th>
            <th style="">ID</th>
            <th style="">Name</th>
            <th style="">Date (YYYY-MM-DD)</th>
            <th style="">Logo URL</th>
            <th style="">Logo</th>
            <td style=""><strong>End Date (YYYY-MM-DD)</strong><br><em>Set to distant future if you want the event to persist in client apps.</em></td>
          </tr>
        </thead>
        <tbody>
          <tr><form action="events.php" method="post">
            <input type="hidden" name="user-action" value="add-event">
            <td style="width:5%"><input class="btn btn-success" type="submit" value="add" onclick="manageEvent('add');return false;"></th>
            <td style="">ID</td>
            <td style=""><textarea id="event-name" style="width:100%" placeholder="event name"></textarea></td>
            <td style=""><input type="date" id="event-date" style="width:100%"></td>
            <td style=""><textarea id="event-logoURL" style="width:100%" placeholder="event logo url"></textarea></td>
            <td style="">NA</td>
            <td style=""><input type="date" id="event-end-date" style="width:100%"></td>
          </tr>
        
          <?php
          if (!($stmt = $mySQLi->prepare(
            "SELECT e.id, e.name, e.event_date, e.logo_url, e.end_date FROM events e ORDER BY e.event_date DESC"))) {
            echo "Prepare failed: (" . $mySQLi->errno . ") " . $mySQLi->error;
          }

          if (!$stmt->execute()) {
            echo "Execute failed: (" . $stmt->errno . ") " . $stmt->error;
          }

          if(!$stmt->bind_result($eventId,$eventName,$eventDate,$eventLogoURL,$eventEndDate)){
            echo "Bind failed: "  . $mySQLi->connect_errno . " " . $mySQLi->connect_error;
          }
          
          while($row = $stmt->fetch()){ ?>
            <tr><form id="edit-event-form<?php echo $eventId ?>" name="edit-event-form">
            <input type="hidden" name="user-action" value="edit-event"></form></tr>
            <td style=""><input onclick="manageEvent('edit',<?php echo $eventId ?>);return false;" class="btn btn-warning" type="submit" value="edit"></td>
            <td style=""><?php echo $eventId ?><input type="hidden" name="event-id" value=<?php echo $eventId ?></td>
            <td style=""><textarea id="event-name<?php echo $eventId ?>" style="width:100%"><?php echo $eventName?></textarea></td>
            <td style=""><?php echo $eventDate?><input type="date" id="event-date<?php echo $eventId ?>" style="width:100%"></td>
            <td style=""><textarea id="event-logoURL<?php echo $eventId ?>" type="number" style="width:100%"><?php echo $eventLogoURL?></textarea></td>
            <td style=""><img class="img-responsive" src="<?php echo $eventLogoURL?>"></td>
            <td style=""><?php echo $eventEndDate?><input type="date" id="event-end-date<?php echo $eventId ?>" style="width:100%"></td>
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