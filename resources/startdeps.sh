if [[ ! -z "$DEBUG" ]]; then
    set -ex
fi

DBRIDGE_ROOT=$( cd "$( dirname "BASH_SOURCE[0]" )" && pwd)

echo "pwd: ================> $DBRIDGE_ROOT"

echo "preparing images"

echo "===>"

#sudo docker pull mysql:5.6
#sudo docker pull mongo:3.2
#sudo docker pull redis:3.2

sudo docker rm -f mysql

echo "starting deps mysql"

echo "===>"

sudo docker run -p 3306:3306 --name mysql -v $DBRIDGE_ROOT/mysqldata:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.6


echo "now, you can init the mysql database"
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo GRANT ALL PRIVILEGES ON *.* TO molmc@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
echo CREATE DATABASE mydb;
echo FLUSH PRIVILEGES;
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"



