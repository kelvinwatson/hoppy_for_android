#!/usr/bin/env bash

### ONLY PERFORM THESE STEPS IF INSTALLING LAMP FOR THE FIRST TIME ###
#sudo apt-get install lamp-server^
#cd /etc/apache2
#vim ports.conf
#change to 8081 and save
#cd sites-enabled
#vim 000-default.conf
#go to 45.58.38.34:8081, you should see the apache page
#cd vim /var/www/html/
#sudo vim info.php
#add this code: <?php phpinfo(); ?>
#/etc/init.d/apache2 restart;
#go to 45.58.38.34:8081/info.php, you should see php information

#/etc/init.d/apache2 stop;
#/etc/init.d/apache2 start;
/etc/init.d/apache2 restart;


/etc/init.d/apache2 status -l;
