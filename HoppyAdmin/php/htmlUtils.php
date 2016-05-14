<?php

function defineModal($beerId, $beerName, $beerDescription){
 echo "
  <div class=\"modal fade\" id=\"itemModal".$beerId."\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\">
    <div class=\"modal-dialog\" role=\"document\">
      <div class=\"modal-content\">
        <div class=\"modal-header\">
          <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>
          <h4 class=\"modal-title\" id=\"myModalLabel\">Description of ".$beerName."</h4>
        </div>
        <div class=\"modal-body\">
          <div class=\"panel panel-default\">
            <div class=\"panel-heading\"><h3 class=\"panel-title\">".$beerName."</h3></div><div class=\"panel-body\">".$beerDescription."</div>
          </div></td>
        </div>
        <div class=\"modal-footer\">
          <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>
        </div>
      </div>
    </div>
  </div>";
}

?>