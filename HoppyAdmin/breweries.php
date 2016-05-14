<?php 
session_start();
error_reporting(E_ALL);
ini_set('display_errors',1);
ini_set('session.save_path', '/hoppyadmin/saved_sessions');
header('Content-Type: text/html; charset=utf-8');
include 'php/db.php';

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

?>

<!DOCTYPE html>
<html lang="en">

<head> 
  <script src="js/toast.js"></script>
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
      <table class="table table-responsive" style="font-size:0.8em; table-layout: fixed; word-wrap: break-word;">
        <thead>
          <tr>
            <th>Edit</th>
            <th>ID</th>
            <th>Brewery Name</th>
            <th>Description</th>
            <th>Logo URL</th>
            <th>Preview</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <form id="add-brewery-form" name="add-brewery-form">
              <input type="hidden" name="user-action" value="add-brewery">
              <td style="width:5%"><input class="btn btn-success" type="submit" value="add" onclick="manageBrewery('add');return false;"></th>
              <td style="width:5%">ID</td>
              <td style=""><textarea  id="brewery-name" name="brewery-name" style="width:100%" placeholder="brewery name"></textarea></td>
              <td style=""><textarea id="brewery-description" name="brewery-description" style="width:100%" placeholder="brewery description"></textarea></td>
              <td style="width:30%"><textarea id="brewery-logoURL" name="brewery-logoURL" style="width:100%" placeholder="brewery logo url"></textarea></td>
              <td style="width:10%"></td>
            </form>
          </tr>

        
          <?php
          if (!($stmt = $mySQLi->prepare(
            "SELECT brs.id, brs.name, brs.description, brs.logo_url FROM breweries brs"))) {
            echo "Prepare failed: (" . $mySQLi->errno . ") " . $mySQLi->error;
          }

          if (!$stmt->execute()) {
            echo "Execute failed: (" . $stmt->errno . ") " . $stmt->error;
          }

          if(!$stmt->bind_result($id,$name,$description,$logoURL)){
            echo "Bind failed: "  . $mySQLi->connect_errno . " " . $mySQLi->connect_error;
          }

          $id = $name = $description = $logoURL = NULL;
          $i=0;
          
          while($row = $stmt->fetch()){
            echo
              "<tr>
                <form id=\"edit-brewery-form$id\" name=\"edit-brewery-form\">
                  <input type=\"hidden\" name=\"user-action\" value=\"edit-brewery\">
                  <td style=\"width:10%\"><input class=\"btn btn-warning\" type=\"submit\" value=\"edit\" onclick=\"manageBrewery('edit',$id);return false;\"></td>
                  <td style=\"width:5%\">".$id."<input type=\"hidden\" name=\"brewery-id\" value=\"".$id."\"></td>
                  <td style=\"width:10%\"><textarea id=\"brewery-name$id\" name=\"brewery-name\" style=\"width:100%\">$name</textarea></td>
                  <td style=\"width:20%\"><textarea  id=\"brewery-description$id\" name=\"brewery-description\" style=\"width:100%\">$description</textarea></td>
                  <td style=\"width:5%\"><textarea id=\"brewery-logoURL$id\" name=\"brewery-logoURL\" style=\"width:100%\">$logoURL</textarea></td>
                  <td style=\"width:10%\"><img class=\"img-responsive\" src=\"$logoURL\"></td>
                </form>
              </tr>";
          }
          $stmt->free_result();
          $stmt->close();
          ?>
        </tbody>
      </table>
    </div>
  </div> <!--END VIEW TABLE ROW-->

</div> <!-- END CONTAINER -->

<!--SCRIPTS-->

<script type="text/javascript" src="js/brewery.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="http://getbootstrap.com/dist/js/bootstrap.min.js"/>
<script type="text/javascript" src="js/jQuery.js"></script>

</body>
</html>