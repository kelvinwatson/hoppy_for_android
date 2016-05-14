<?php

define('DEBUG',true);

class Debug{
    public static function consoleLog($data) {
        if(DEBUG) echo $data;
    }
}

?>